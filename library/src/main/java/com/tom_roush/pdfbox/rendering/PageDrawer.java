/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.tom_roush.pdfbox.rendering;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.Region;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.tom_roush.harmony.awt.geom.AffineTransform;
import com.tom_roush.pdfbox.contentstream.PDFGraphicsStreamEngine;
import com.tom_roush.pdfbox.cos.COSArray;
import com.tom_roush.pdfbox.cos.COSBase;
import com.tom_roush.pdfbox.cos.COSDictionary;
import com.tom_roush.pdfbox.cos.COSName;
import com.tom_roush.pdfbox.pdmodel.PDResources;
import com.tom_roush.pdfbox.pdmodel.common.PDRectangle;
import com.tom_roush.pdfbox.pdmodel.common.function.PDFunction;
import com.tom_roush.pdfbox.pdmodel.documentinterchange.markedcontent.PDPropertyList;
import com.tom_roush.pdfbox.pdmodel.font.PDCIDFontType0;
import com.tom_roush.pdfbox.pdmodel.font.PDCIDFontType2;
import com.tom_roush.pdfbox.pdmodel.font.PDFont;
import com.tom_roush.pdfbox.pdmodel.font.PDTrueTypeFont;
import com.tom_roush.pdfbox.pdmodel.font.PDType0Font;
import com.tom_roush.pdfbox.pdmodel.font.PDType1CFont;
import com.tom_roush.pdfbox.pdmodel.font.PDType1Font;
import com.tom_roush.pdfbox.pdmodel.font.PDType3Font;
import com.tom_roush.pdfbox.pdmodel.graphics.PDLineDashPattern;
import com.tom_roush.pdfbox.pdmodel.graphics.PDXObject;
import com.tom_roush.pdfbox.pdmodel.graphics.blend.BlendMode;
import com.tom_roush.pdfbox.pdmodel.graphics.color.PDColor;
import com.tom_roush.pdfbox.pdmodel.graphics.color.PDColorSpace;
import com.tom_roush.pdfbox.pdmodel.graphics.color.PDDeviceGray;
import com.tom_roush.pdfbox.pdmodel.graphics.color.PDICCBased;
import com.tom_roush.pdfbox.pdmodel.graphics.form.PDFormXObject;
import com.tom_roush.pdfbox.pdmodel.graphics.form.PDTransparencyGroup;
import com.tom_roush.pdfbox.pdmodel.graphics.image.PDImage;
import com.tom_roush.pdfbox.pdmodel.graphics.image.PDImageXObject;
import com.tom_roush.pdfbox.pdmodel.graphics.optionalcontent.PDOptionalContentGroup;
import com.tom_roush.pdfbox.pdmodel.graphics.optionalcontent.PDOptionalContentGroup.RenderState;
import com.tom_roush.pdfbox.pdmodel.graphics.optionalcontent.PDOptionalContentMembershipDictionary;
import com.tom_roush.pdfbox.pdmodel.graphics.shading.PDShading;
import com.tom_roush.pdfbox.pdmodel.graphics.state.PDExtendedGraphicsState;
import com.tom_roush.pdfbox.pdmodel.graphics.state.PDGraphicsState;
import com.tom_roush.pdfbox.pdmodel.graphics.state.PDSoftMask;
import com.tom_roush.pdfbox.pdmodel.graphics.state.RenderingMode;
import com.tom_roush.pdfbox.pdmodel.interactive.annotation.AnnotationFilter;
import com.tom_roush.pdfbox.pdmodel.interactive.annotation.PDAnnotation;
import com.tom_roush.pdfbox.pdmodel.interactive.annotation.PDAnnotationUnknown;
import com.tom_roush.pdfbox.pdmodel.interactive.annotation.PDAppearanceDictionary;
import com.tom_roush.pdfbox.util.Matrix;
import com.tom_roush.pdfbox.util.Vector;

/**
 * Paints a page in a PDF document to a Canvas context. May be subclassed to provide custom
 * rendering.
 *
 * <p>
 * If you want to do custom graphics processing rather than Canvas rendering, then you should
 * subclass {@link PDFGraphicsStreamEngine} instead. Subclassing PageDrawer is only suitable for
 * cases where the goal is to render onto a {@link Canvas} surface. In that case you'll also
 * have to subclass {@link PDFRenderer} and override
 * {@link PDFRenderer#createPageDrawer(PageDrawerParameters)}. See the <i>OpaquePDFRenderer.java</i>
 * example in the source code download on how to do this.
 *
 * @author Ben Litchfield
 */
public class PageDrawer extends PDFGraphicsStreamEngine
{
    // parent document renderer - note: this is needed for not-yet-implemented resource caching
    private final PDFRenderer renderer;

    private final boolean subsamplingAllowed;

    // the graphics device to draw to, xform is the initial transform of the device (i.e. DPI)
    private Paint paint;
    private Canvas canvas;
    private AffineTransform xform;
    private float xformScalingFactorX;
    private float xformScalingFactorY;

    // the page box to draw (usually the crop box but may be another)
    private PDRectangle pageSize;

    // whether image of a transparency group must be flipped
    // needed when in a tiling pattern
    private boolean flipTG = false;

    // clipping winding rule used for the clipping path
    private Path.FillType clipWindingRule = null;
    private Path linePath = new Path();

    // last clipping path
    private Region lastClip;
    private int lastStackSize = 0;

    // clip when drawPage() is called, can be null, must be intersected when clipping
    private Path initialClip;

    // shapes of glyphs being drawn to be used for clipping
    private List<Path> textClippings;

    // glyph cache
    private final Map<PDFont, Glyph2D> fontGlyph2D = new HashMap<PDFont, Glyph2D>();

    private PointF currentPoint = new PointF();

    private final Deque<TransparencyGroup> transparencyGroupStack = new ArrayDeque<>();

    // if greater zero the content is hidden and will not be rendered
    private int nestedHiddenOCGCount;

    private final RenderDestination destination;
    private final float imageDownscalingOptimizationThreshold;

    /**
     * Default annotations filter, returns all annotations
     */
    private AnnotationFilter annotationFilter = new AnnotationFilter()
    {
        @Override
        public boolean accept(PDAnnotation annotation)
        {
            return true;
        }
    };

    /**
     * Constructor.
     *
     * @param parameters Parameters for page drawing.
     * @throws IOException If there is an error loading properties from the file.
     */
    public PageDrawer(PageDrawerParameters parameters) throws IOException
    {
        super(parameters.getPage());
        this.renderer = parameters.getRenderer();
        this.subsamplingAllowed = parameters.isSubsamplingAllowed();
        this.destination = parameters.getDestination();
        this.imageDownscalingOptimizationThreshold =
            parameters.getImageDownscalingOptimizationThreshold();
    }

    /**
     * Return the AnnotationFilter.
     *
     * @return the AnnotationFilter
     */
    public AnnotationFilter getAnnotationFilter()
    {
        return annotationFilter;
    }

    /**
     * Set the AnnotationFilter.
     *
     * <p>Allows to only render annotation accepted by the filter.
     *
     * @param annotationFilter the AnnotationFilter
     */
    public void setAnnotationFilter(AnnotationFilter annotationFilter)
    {
        this.annotationFilter = annotationFilter;
    }

    /**
     * Returns the parent renderer.
     */
    public final PDFRenderer getRenderer()
    {
        return renderer;
    }

    /**
     * Returns the underlying Canvas. May be null if drawPage has not yet been called.
     */
    protected final Canvas getCanvas()
    {
        return canvas;
    }

    /**
     * Returns the current line path. This is reset to empty after each fill/stroke.
     */
    protected final Path getLinePath()
    {
        return linePath;
    }

    /**
     * Sets high-quality rendering hints on the current Canvas.
     */
    private void setRenderingHints()
    {
//        graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
//            RenderingHints.VALUE_INTERPOLATION_BICUBIC);
//        graphics.setRenderingHint(RenderingHints.KEY_RENDERING,
//            RenderingHints.VALUE_RENDER_QUALITY);
        paint.setAntiAlias(true);
    }

    /**
     * Draws the page to the requested Canvas.
     *
     * @param p The paint.
     * @param c The canvas to draw onto.
     * @param pageSize The size of the page to draw.
     * @throws IOException If there is an IO error while drawing the page.
     */
    public void drawPage(Paint p, Canvas c, PDRectangle pageSize) throws IOException
    {
        paint = p;
        canvas = c;
        xform = new AffineTransform(canvas.getMatrix());
        Matrix m = new Matrix(xform);
        xformScalingFactorX = Math.abs(m.getScalingFactorX());
        xformScalingFactorY = Math.abs(m.getScalingFactorY());
        // backup init status
        canvas.save();
        this.pageSize = pageSize;

        setRenderingHints();

        canvas.translate(0, pageSize.getHeight());
        canvas.scale(1, -1);

        // adjust for non-(0,0) crop box
        canvas.translate(-pageSize.getLowerLeftX(), -pageSize.getLowerLeftY());

        processPage(getPage());

        for (PDAnnotation annotation : getPage().getAnnotations(annotationFilter))
        {
            showAnnotation(annotation);
        }
        canvas.restore();
    }

//    void drawTilingPattern(Graphics2D g, PDTilingPattern pattern, PDColorSpace colorSpace,
//        PDColor color, Matrix patternMatrix) throws IOException TODO: PdfBox-Android

    private float clampColor(float color)
    {
        return color < 0 ? 0 : (color > 1 ? 1 : color);
    }

//    protected Paint getPaint(PDColor color) throws IOException TODO: PdfBox-Android

    // returns an integer for color that Android understands from the PDColor
    // TODO: alpha?
    private int getColor(PDColor color) throws IOException {
        double alphaConstant = this.getGraphicsState().getAlphaConstant();
        PDColorSpace colorSpace = color.getColorSpace();
        float[] floats = colorSpace.toRGB(color.getComponents());
        int alpha = Long.valueOf(Math.round(alphaConstant * 255.0)).intValue();
        int r = Math.round(floats[0] * 255);
        int g = Math.round(floats[1] * 255);
        int b = Math.round(floats[2] * 255);
        return Color.argb(alpha, r, g, b);
    }

    /**
     * Sets the clipping path using caching for performance. We track lastClip manually because
     * {@link Graphics2D#getClip()} returns a new object instead of the same one passed to
     * {@link Graphics2D#setClip(java.awt.Shape) setClip()}. You may need to call this if you
     * override {@link #showGlyph(Matrix, PDFont, int, Vector) showGlyph()}. See
     * <a href="https://issues.apache.org/jira/browse/PDFBOX-5093">PDFBOX-5093</a> for more.
     */
    protected final void setClip()
    {
        Region clippingPath = getGraphicsState().getCurrentClippingPath();
        if (clippingPath != lastClip)
        {
            // android canvas manage clips with save/restore in a private stack, we can not
            // modify clip casually, so we store current stack size in `lastStackSize` after setting clip,
            // and restore it before next setting
            if (lastStackSize >= 1)
            {
                canvas.restoreToCount(lastStackSize);
            }
            lastStackSize = canvas.save();
            if (!clippingPath.isEmpty())
            {
                canvas.clipPath(clippingPath.getBoundaryPath());
            }
            if (initialClip != null)
            {
                // apply the remembered initial clip, but transform it first
                //TODO see PDFBOX-4583
            }
            lastClip = clippingPath;
        }
    }

    @Override
    public void beginText() throws IOException
    {
        setClip();
        beginTextClip();
    }

    @Override
    public void endText() throws IOException
    {
        endTextClip();
    }

    /**
     * Begin buffering the text clipping path, if any.
     */
    private void beginTextClip()
    {
        // buffer the text clippings because they represents a single clipping area
        textClippings = new ArrayList<Path>();
    }

    /**
     * End buffering the text clipping path, if any.
     */
    private void endTextClip()
    {
        PDGraphicsState state = getGraphicsState();
        RenderingMode renderingMode = state.getTextState().getRenderingMode();

        // apply the buffered clip as one area
        if (renderingMode.isClip() && !textClippings.isEmpty())
        {
            // PDFBOX-4150: this is much faster than using textClippingArea.add(new Area(glyph))
            // https://stackoverflow.com/questions/21519007/fast-union-of-shapes-in-java
            Path path = new Path();
            path.setFillType(Path.FillType.WINDING);
            for (Path shape : textClippings)
            {
                path.addPath(shape);
            }
            state.intersectClippingPath(path);
            textClippings = new ArrayList<Path>();

            // PDFBOX-3681: lastClip needs to be reset, because after intersection it is still the same
            // object, thus setClip() would believe that it is cached.
            lastClip = null;
        }
    }

    @Override
    protected void showFontGlyph(Matrix textRenderingMatrix, PDFont font, int code,
        Vector displacement) throws IOException
    {
        AffineTransform at = textRenderingMatrix.createAffineTransform();
        at.concatenate(font.getFontMatrix().createAffineTransform());

        Glyph2D glyph2D = createGlyph2D(font);
        try
        {
            drawGlyph2D(glyph2D, font, code, displacement, at);
        }
        catch (IOException ex)
        {
            Log.e("PdfBox-Android", "Could not draw glyph for code " + code + " at position (" +
                at.getTranslateX() + "," + at.getTranslateY() + ")", ex);
        }
    }

    /**
     * Render the font using the Glyph2D interface.
     *
     * @param glyph2D the Glyph2D implementation provided a Path for each glyph
     * @param font the font
     * @param code character code
     * @param displacement the glyph's displacement (advance)
     * @param at the transformation
     * @throws IOException if something went wrong
     */
    private void drawGlyph2D(Glyph2D glyph2D, PDFont font, int code, Vector displacement,
        AffineTransform at) throws IOException
    {
        PDGraphicsState state = getGraphicsState();
        RenderingMode renderingMode = state.getTextState().getRenderingMode();

        Path path = glyph2D.getPathForCharacterCode(code);
        if (path != null)
        {
            // Stretch non-embedded glyph if it does not match the height/width contained in the PDF.
            // Vertical fonts have zero X displacement, so the following code scales to 0 if we don't skip it.
            // TODO: How should vertical fonts be handled?
            if (!font.isEmbedded() && !font.isVertical() && !font.isStandard14() && font.hasExplicitWidth(code))
            {
                float fontWidth = font.getWidthFromFont(code);
                if (fontWidth > 0 && // ignore spaces
                    Math.abs(fontWidth - displacement.getX() * 1000) > 0.0001)
                {
                    float pdfWidth = displacement.getX() * 1000;
                    at.scale(pdfWidth / fontWidth, 1);
                }
            }

            // render glyph
//            Shape glyph = at.createTransformedShape(path);
            path.transform(at.toMatrix());

            if (isContentRendered())
            {
                if (renderingMode.isFill())
                {
                    paint.setColor(getNonStrokingColor());
                    setClip();
                    paint.setStyle(Paint.Style.FILL);
                    canvas.drawPath(path, paint);
                }

                if (renderingMode.isStroke())
                {
                    paint.setColor(getStrokingColor());
                    setStroke();
                    setClip();
                    paint.setStyle(Paint.Style.STROKE);
                    canvas.drawPath(path, paint);
                }
            }

            if (renderingMode.isClip())
            {
//                textClippings.add(glyph); TODO: PdfBox-Android
            }
        }
    }

    @Override
    protected void showType3Glyph(Matrix textRenderingMatrix, PDType3Font font, int code,
        Vector displacement) throws IOException
    {
        PDGraphicsState state = getGraphicsState();
        RenderingMode renderingMode = state.getTextState().getRenderingMode();
        if (!RenderingMode.NEITHER.equals(renderingMode))
        {
            super.showType3Glyph(textRenderingMatrix, font, code, displacement);
        }
    }

    /**
     * Provide a Glyph2D for the given font.
     *
     * @param font the font
     * @return the implementation of the Glyph2D interface for the given font
     * @throws IOException if something went wrong
     */
    private Glyph2D createGlyph2D(PDFont font) throws IOException
    {
        Glyph2D glyph2D = fontGlyph2D.get(font);
        // Is there already a Glyph2D for the given font?
        if (glyph2D != null)
        {
            return glyph2D;
        }

        if (font instanceof PDTrueTypeFont)
        {
            PDTrueTypeFont ttfFont = (PDTrueTypeFont)font;
            glyph2D = new TTFGlyph2D(ttfFont);  // TTF is never null
        }
        else if (font instanceof PDType1Font)
        {
            PDType1Font pdType1Font = (PDType1Font)font;
            glyph2D = new Type1Glyph2D(pdType1Font); // T1 is never null
        }
        else if (font instanceof PDType1CFont)
        {
            PDType1CFont type1CFont = (PDType1CFont)font;
            glyph2D = new Type1Glyph2D(type1CFont);
        }
        else if (font instanceof PDType0Font)
        {
            PDType0Font type0Font = (PDType0Font) font;
            if (type0Font.getDescendantFont() instanceof PDCIDFontType2)
            {
                glyph2D = new TTFGlyph2D(type0Font); // TTF is never null
            }
            else if (type0Font.getDescendantFont() instanceof PDCIDFontType0)
            {
                // a Type0 CIDFont contains CFF font
                PDCIDFontType0 cidType0Font = (PDCIDFontType0)type0Font.getDescendantFont();
                glyph2D = new CIDType0Glyph2D(cidType0Font); // todo: could be null (need incorporate fallback)
            }
        }
        else
        {
            throw new IllegalStateException("Bad font type: " + font.getClass().getSimpleName());
        }

        // cache the Glyph2D instance
        if (glyph2D != null)
        {
            fontGlyph2D.put(font, glyph2D);
        }

        if (glyph2D == null)
        {
            // todo: make sure this never happens
            throw new UnsupportedOperationException("No font for " + font.getName());
        }

        return glyph2D;
    }

    @Override
    public void appendRectangle(PointF p0, PointF p1, PointF p2, PointF p3)
    {
        // to ensure that the path is created in the right direction, we have to create
        // it by combining single lines instead of creating a simple rectangle
        linePath.moveTo((float) p0.x, (float) p0.y);
        linePath.lineTo((float) p1.x, (float) p1.y);
        linePath.lineTo((float) p2.x, (float) p2.y);
        linePath.lineTo((float) p3.x, (float) p3.y);

        // close the subpath instead of adding the last line so that a possible set line
        // cap style isn't taken into account at the "beginning" of the rectangle
        linePath.close();
    }

//    private Paint applySoftMaskToPaint(Paint parentPaint, PDSoftMask softMask) throws IOException TODO: Pdfbox-Android

//    private void adjustRectangle(RectF r) TODO: PdfBox-Android

//    private Bitmap adjustImage(Bitmap gray) TODO: PdfBox-Android

//    private Paint getStrokingPaint() throws IOException TODO: PdfBox-Android

    private int getStrokingColor() throws IOException
    {
        return getColor(getGraphicsState().getStrokingColor());
    }

//    protected final Paint getNonStrokingPaint() throws IOException TODO: PdfBox-Android

    protected final int getNonStrokingColor() throws IOException
    {
        return getColor(getGraphicsState().getNonStrokingColor());
    }

    // set stroke based on the current CTM and the current stroke
    private void setStroke()
    {
        PDGraphicsState state = getGraphicsState();

        // apply the CTM
        float lineWidth = transformWidth(state.getLineWidth());

        // minimum line width as used by Adobe Reader
        if (lineWidth < 0.25)
        {
            lineWidth = 0.25f;
        }

        PDLineDashPattern dashPattern = state.getLineDashPattern();
        // PDFBOX-5168: show an all-zero dash array line invisible like Adobe does
        // must do it here because getDashArray() sets minimum width because of JVM bugs
        float[] dashArray = dashPattern.getDashArray();
        if (isAllZeroDash(dashArray))
        {
            return;
        }
        float phaseStart = dashPattern.getPhase();
        dashArray = getDashArray(dashPattern);
        phaseStart = transformWidth(phaseStart);

        paint.setStrokeWidth(lineWidth);
        paint.setStrokeCap(state.getLineCap());
        paint.setStrokeJoin(state.getLineJoin());
        float miterLimit = state.getMiterLimit();
        if (miterLimit < 1)
        {
            Log.w("PdfBox-Android", "Miter limit must be >= 1, value " + miterLimit + " is ignored");
            miterLimit = 10;
        }
        paint.setStrokeMiter(miterLimit);
        if (dashArray != null)
        {
            paint.setPathEffect(new DashPathEffect(dashArray, phaseStart));
        }
    }

    private boolean isAllZeroDash(float[] dashArray)
    {
        if (dashArray.length > 0)
        {
            for (int i = 0; i < dashArray.length; ++i)
            {
                if (dashArray[i] != 0)
                {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    private float[] getDashArray(PDLineDashPattern dashPattern)
    {
        float[] dashArray = dashPattern.getDashArray();
        int phase = dashPattern.getPhase();
        // avoid empty, infinite and NaN values (PDFBOX-3360)
        if (dashArray.length == 0 || Float.isInfinite(phase) || Float.isNaN(phase))
        {
            return null;
        }
        for (int i = 0; i < dashArray.length; ++i)
        {
            if (Float.isInfinite(dashArray[i]) || Float.isNaN(dashArray[i]))
            {
                return null;
            }
        }
        for (int i = 0; i < dashArray.length; ++i)
        {
            // apply the CTM
            float w = transformWidth(dashArray[i]);
            // minimum line dash width avoids JVM crash,
            // see PDFBOX-2373, PDFBOX-2929, PDFBOX-3204, PDFBOX-3813
            // also avoid 0 in array like "[ 0 1000 ] 0 d", see PDFBOX-3724
            if (xformScalingFactorX < 0.5f)
            {
                // PDFBOX-4492
                dashArray[i] = Math.max(w, 0.2f);
            }
            else
            {
                dashArray[i] = Math.max(w, 0.062f);
            }
        }
        return dashArray;
    }

    @Override
    public void strokePath() throws IOException
    {
        if (isContentRendered())
        {
            setStroke();
            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(getStrokingColor());
            setClip();
            canvas.drawPath(linePath, paint);
        }
        linePath.reset();
    }

    @Override
    public void fillPath(Path.FillType windingRule) throws IOException
    {
        PDGraphicsState graphicsState = getGraphicsState();
        paint.setColor(getNonStrokingColor());
        setClip();
        linePath.setFillType(windingRule);

        // disable anti-aliasing for rectangular paths, this is a workaround to avoid small stripes
        // which occur when solid fills are used to simulate piecewise gradients, see PDFBOX-2302
        // note that we ignore paths with a width/height under 1 as these are fills used as strokes,
        // see PDFBOX-1658 for an example
        RectF bounds = new RectF();
        linePath.computeBounds(bounds, true);
        boolean noAntiAlias = isRectangular(linePath) && bounds.width() > 1 &&
            bounds.height() > 1;
        if (noAntiAlias)
        {
            paint.setAntiAlias(false);
        }

        if (isContentRendered())
        {
            paint.setStyle(Paint.Style.FILL);
            canvas.drawPath(linePath, paint);
        }

        linePath.reset();

        if (noAntiAlias)
        {
            // JDK 1.7 has a bug where rendering hints are reset by the above call to
            // the setRenderingHint method, so we re-set all hints, see PDFBOX-2302
            setRenderingHints();
        }
    }

//    private void intersectShadingBBox(PDColor color, Area area) throws IOException TODO: PdfBox-Android

    /**
     * Returns true if the given path is rectangular.
     */
    private boolean isRectangular(Path path)
    {
        RectF rect = new RectF();
        return path.isRect(rect);
    }

    /**
     * Fills and then strokes the path.
     *
     * @param windingRule The winding rule this path will use.
     * @throws IOException If there is an IO error while filling the path.
     */
    @Override
    public void fillAndStrokePath(Path.FillType windingRule) throws IOException
    {
        // Cloning needed because fillPath() resets linePath
        Path path = new Path(linePath);
        fillPath(windingRule);
        linePath = path;
        strokePath();
    }

    @Override
    public void clip(Path.FillType windingRule)
    {
        // the clipping path will not be updated until the succeeding painting operator is called
        clipWindingRule = windingRule;
    }

    @Override
    public void moveTo(float x, float y)
    {
        currentPoint.set(x, y);
        linePath.moveTo(x, y);
    }

    @Override
    public void lineTo(float x, float y)
    {
        currentPoint.set(x, y);
        linePath.lineTo(x, y);
    }

    @Override
    public void curveTo(float x1, float y1, float x2, float y2, float x3, float y3)
    {
        currentPoint.set(x3, y3);
        linePath.cubicTo(x1, y1, x2, y2, x3, y3); // TODO: PdfBox-Android check if this should be relative
    }

    @Override
    public PointF getCurrentPoint()
    {
        return currentPoint;
    }

    @Override
    public void closePath()
    {
        linePath.close();
    }

    @Override
    public void endPath()
    {
//        TODO: PdfBox-Android adding clipping causes rendering issues
        linePath.reset();
    }

    @Override
    public void drawImage(PDImage pdImage) throws IOException
    {
        if (pdImage instanceof PDImageXObject &&
            isHiddenOCG(((PDImageXObject) pdImage).getOptionalContent()))
        {
            return;
        }
        if (!isContentRendered())
        {
            return;
        }
        Matrix ctm = getGraphicsState().getCurrentTransformationMatrix();
        AffineTransform at = ctm.createAffineTransform();

        if (!pdImage.getInterpolate())
        {
            // if the image is scaled down, we use smooth interpolation, eg PDFBOX-2364
            // only when scaled up do we use nearest neighbour, eg PDFBOX-2302 / mori-cvpr01.pdf
            // PDFBOX-4930: we use the sizes of the ARGB image. These can be different
            // than the original sizes of the base image, when the mask is bigger.
            // PDFBOX-5091: also consider subsampling, the sizes are different too.
            Bitmap bim;
            if (subsamplingAllowed)
            {
                bim = pdImage.getImage(null, getSubsampling(pdImage, at));
            }
            else
            {
                bim = pdImage.getImage();
            }
            Matrix m = new Matrix(at);
            boolean isScaledUp = bim.getWidth() < Math.abs(Math.round(m.getScalingFactorX())) ||
                bim.getHeight() < Math.abs(Math.round(m.getScalingFactorY()));

            if (isScaledUp)
            {
//                graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
//                    RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
            }
        }

        setClip();

        if (pdImage.isStencil())
        {
//            if (getGraphicsState().getNonStrokingColor().getColorSpace() instanceof PDPattern) TODO: PdfBox-Android
//            else
//            TODO: PdfBox-Android draw stenciled Bitmap
        }
        else
        {
            if (subsamplingAllowed)
            {
                int subsampling = getSubsampling(pdImage, at);
                // draw the subsampled image
                drawBitmap(pdImage.getImage(null, subsampling), at);
            }
            else
            {
                // subsampling not allowed, draw the image
                drawBitmap(pdImage.getImage(), at);
            }
        }

        if (!pdImage.getInterpolate())
        {
            // JDK 1.7 has a bug where rendering hints are reset by the above call to
            // the setRenderingHint method, so we re-set all hints, see PDFBOX-2302
            setRenderingHints();
        }
    }

    /**
     * Calculated the subsampling frequency for a given PDImage based on the current transformation
     * and its calculated transform
     *
     * @param pdImage PDImage to be drawn
     * @param at Transform that will be applied to the image when drawing
     * @return The rounded-down ratio of image pixels to drawn pixels. Returned value will always be
     * >=1.
     */
    private int getSubsampling(PDImage pdImage, AffineTransform at)
    {
        // calculate subsampling according to the resulting image size
        double scale = Math.abs(at.getDeterminant() * xform.getDeterminant());

        int subsampling = (int) Math.floor(Math.sqrt(pdImage.getWidth() * pdImage.getHeight() / scale));
        if (subsampling > 8)
        {
            subsampling = 8;
        }
        if (subsampling < 1)
        {
            subsampling = 1;
        }
        if (subsampling > pdImage.getWidth() || subsampling > pdImage.getHeight())
        {
            // For very small images it is possible that the subsampling would imply 0 size.
            // To avoid problems, the subsampling is set to no less than the smallest dimension.
            subsampling = Math.min(pdImage.getWidth(), pdImage.getHeight());
        }
        return subsampling;
    }

    private void drawBitmap(Bitmap image, AffineTransform at) throws IOException
    {
        setClip();
        AffineTransform imageTransform = new AffineTransform(at);
        int width = image.getWidth();
        int height = image.getHeight();
        imageTransform.scale(1.0 / width, -1.0 / height);
        imageTransform.translate(0, -height);

        PDSoftMask softMask = getGraphicsState().getSoftMask();
        if( softMask != null )
        {
            RectF rectangle = new RectF(0, 0, width, height);
//            Paint awtPaint; TODO: PdfBox-Android
        }
        else
        {
            COSBase transfer = getGraphicsState().getTransfer();
            if (transfer instanceof COSArray || transfer instanceof COSDictionary)
            {
                image = applyTransferFunction(image, transfer);
            }

            canvas.drawBitmap(image, imageTransform.toMatrix(), paint);
        }
    }

    private Bitmap applyTransferFunction(Bitmap image, COSBase transfer) throws IOException
    {
        Bitmap bim = Bitmap.createBitmap(image.getWidth(), image.getHeight(), Bitmap.Config.ARGB_8888);
        // TODO: Pdfbox-Android - does this always need to be ARGB_8888?

        // prepare transfer functions (either one per color or one for all) 
        // and maps (actually arrays[256] to be faster) to avoid calculating values several times
        Integer[] rMap;
        Integer[] gMap;
        Integer[] bMap;
        PDFunction rf;
        PDFunction gf;
        PDFunction bf;
        if (transfer instanceof COSArray)
        {
            COSArray ar = (COSArray) transfer;
            rf = PDFunction.create(ar.getObject(0));
            gf = PDFunction.create(ar.getObject(1));
            bf = PDFunction.create(ar.getObject(2));
            rMap = new Integer[256];
            gMap = new Integer[256];
            bMap = new Integer[256];
        }
        else
        {
            rf = PDFunction.create(transfer);
            gf = rf;
            bf = rf;
            rMap = new Integer[256];
            gMap = rMap;
            bMap = rMap;
        }

        // apply the transfer function to each color, but keep alpha
        float[] input = new float[1];
        for (int x = 0; x < image.getWidth(); ++x)
        {
            for (int y = 0; y < image.getHeight(); ++y)
            {
                int rgb = image.getPixel(x, y);
                int ri = (rgb >> 16) & 0xFF;
                int gi = (rgb >> 8) & 0xFF;
                int bi = rgb & 0xFF;
                int ro;
                int go;
                int bo;
                if (rMap[ri] != null)
                {
                    ro = rMap[ri];
                }
                else
                {
                    input[0] = (ri & 0xFF) / 255f;
                    ro = (int) (rf.eval(input)[0] * 255);
                    rMap[ri] = ro;
                }
                if (gMap[gi] != null)
                {
                    go = gMap[gi];
                }
                else
                {
                    input[0] = (gi & 0xFF) / 255f;
                    go = (int) (gf.eval(input)[0] * 255);
                    gMap[gi] = go;
                }
                if (bMap[bi] != null)
                {
                    bo = bMap[bi];
                }
                else
                {
                    input[0] = (bi & 0xFF) / 255f;
                    bo = (int) (bf.eval(input)[0] * 255);
                    bMap[bi] = bo;
                }
                bim.setPixel(x, y, (rgb & 0xFF000000) | (ro << 16) | (go << 8) | bo);
            }
        }
        return bim;
    }

    @Override
    public void shadingFill(COSName shadingName) throws IOException
    {
        if (!isContentRendered())
        {
            return;
        }
        PDShading shading = getResources().getShading(shadingName);
        if (shading == null)
        {
            Log.e("PdfBox-Android", "shading " + shadingName + " does not exist in resources dictionary");
            return;
        }
        Matrix ctm = getGraphicsState().getCurrentTransformationMatrix();

//        graphics.setComposite(getGraphicsState().getNonStrokingJavaComposite());
//        Shape savedClip = graphics.getClip();
//        graphics.setClip(null);
//        lastClips = null;

        // get the transformed BBox and intersect with current clipping path
        // need to do it here and not in shading getRaster() because it may have been rotated
        PDRectangle bbox = shading.getBBox();
//        Area area;
        if (bbox != null)
        {
//            area = new Area(bbox.transform(ctm));
//            area.intersect(getGraphicsState().getCurrentClippingPath());
        }
        else
        {
            RectF bounds = shading.getBounds(new AffineTransform(), ctm);
            if (bounds != null)
            {
                bounds.union((float)Math.floor(bounds.left - 1),
                    (float)Math.floor(bounds.top - 1));
                bounds.union((float)Math.ceil(bounds.right + 1),
                    (float)Math.ceil(bounds.bottom + 1));
//                area = new Area(bounds);
//                area.intersect(getGraphicsState().getCurrentClippingPath());
            }
            else
            {
//                area = getGraphicsState().getCurrentClippingPath();
            }
        }
//        if (!area.isEmpty()) TODO: PdfBox-Android
//        graphics.setClip(savedClip);
    }

    @Override
    public void showAnnotation(PDAnnotation annotation) throws IOException
    {
        lastClip = null;
        // Device checks shouldn't be needed
        if (annotation.isNoView())
        {
            return;
        }
        if (annotation.isHidden())
        {
            return;
        }
        if (annotation.isInvisible() && annotation instanceof PDAnnotationUnknown)
        {
            // "If set, do not display the annotation if it does not belong to one
            // of the standard annotation types and no annotation handler is available."
            return;
        }
        //TODO support NoZoom, example can be found in p5 of PDFBOX-2348

        if (isHiddenOCG(annotation.getOptionalContent()))
        {
            return;
        }

        PDAppearanceDictionary appearance = annotation.getAppearance();
        if (appearance == null || appearance.getNormalAppearance() == null)
        {
            annotation.constructAppearances(renderer.document);
        }

        if (annotation.isNoRotate() && getCurrentPage().getRotation() != 0)
        {
            PDRectangle rect = annotation.getRectangle();
            android.graphics.Matrix savedTransform = canvas.getMatrix();
            // "The upper-left corner of the annotation remains at the same point in
            //  default user space; the annotation pivots around that point."
            canvas.rotate(getCurrentPage().getRotation(),
                rect.getLowerLeftX(), rect.getUpperRightY());
            super.showAnnotation(annotation);
            canvas.setMatrix(savedTransform);
        }
        else
        {
            super.showAnnotation(annotation);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void showForm(PDFormXObject form) throws IOException
    {
        if (isHiddenOCG(form.getOptionalContent()))
        {
            return;
        }
        if (isContentRendered())
        {
            Path savedLinePath = new Path(linePath);
            linePath = new Path();
            super.showForm(form);
            linePath = savedLinePath;
        }
    }

    public void setStroke(Paint p, float width, Paint.Cap cap, Paint.Join join, float miterLimit, float[] dash, float dash_phase)
    {
        p.setStrokeWidth(width);
        p.setStrokeCap(cap);
        p.setStrokeJoin(join);
        p.setStrokeMiter(miterLimit);
        if(dash != null)
        {
            p.setPathEffect(new DashPathEffect(dash, dash_phase));
        }
    }

    @Override
    public void showTransparencyGroup(PDTransparencyGroup form) throws IOException
    {
        showTransparencyGroupOnCanvas(form, canvas);
    }

    /**
     * For advanced users, to extract the transparency group into a separate graphics device.
     *
     * @param form
     * @param canvas
     * @throws IOException
     */
    protected void showTransparencyGroupOnCanvas(PDTransparencyGroup form, Canvas canvas)
        throws IOException
    {
        if (isHiddenOCG(form.getOptionalContent()))
        {
            return;
        }
        if (!isContentRendered())
        {
            return;
        }
        TransparencyGroup group =
            new TransparencyGroup(form, false, getGraphicsState().getCurrentTransformationMatrix(), null);
//        Bitmap image = group.getImage();
//        if (image == null)
//        {
            // image is empty, don't bother
//            return;
//        }

//        graphics.setComposite(getGraphicsState().getNonStrokingJavaComposite());
        setClip();

        // both the DPI xform and the CTM were already applied to the group, so all we do
        // here is draw it directly onto the Graphics2D device at the appropriate position
//        AffineTransform savedTransform = graphics.getTransform();

        AffineTransform transform = new AffineTransform(xform);
        transform.scale(1.0 / xformScalingFactorX, 1.0 / xformScalingFactorY);
//        graphics.setTransform(transform);

        // adjust bbox (x,y) position at the initial scale + cropbox
//        PDRectangle bbox = group.getBBox();
//        float x = bbox.getLowerLeftX() - pageSize.getLowerLeftX();
//        float y = pageSize.getUpperRightY() - bbox.getUpperRightY();

        if (flipTG)
        {
//            graphics.translate(0, image.getHeight());
//            graphics.scale(1, -1);
        }
        else
        {
//            graphics.translate(x * xformScalingFactorX, y * xformScalingFactorY);
        }

        PDSoftMask softMask = getGraphicsState().getSoftMask();
        if (softMask != null)
        {
//            Paint awtPaint = new TexturePaint(image,
//                new Rectangle2D.Float(0, 0, image.getWidth(), image.getHeight()));
//            awtPaint = applySoftMaskToPaint(awtPaint, softMask);
//            graphics.setPaint(awtPaint);
//            graphics.fill(
//                new Rectangle2D.Float(0, 0, bbox.getWidth() * xformScalingFactorX, bbox.getHeight() * xformScalingFactorY));
        }
        else
        {
//            try
//            {
//                graphics.drawImage(image, null, null);
//            }
//            catch (InternalError ie)
//            {
//                Log.e("PdfBox-Android", "Exception drawing image, see JDK-6689349, " +
//                    "try rendering into a BufferedImage instead", ie);
//            }
        }

//        graphics.setTransform(savedTransform);
    }

    /**
     * Transparency group.
     **/
    private final class TransparencyGroup
    {
//        private final Bitmap image;
//        private final PDRectangle bbox;

//        private final int minX;
//        private final int minY;
//        private final int maxX;
//        private final int maxY;
//        private final int width;
//        private final int height;

        /**
         * Creates a buffered image for a transparency group result.
         *
         * @param form the transparency group of the form or soft mask.
         * @param isSoftMask true if this is a soft mask.
         * @param ctm the relevant current transformation matrix. For soft masks, this is the CTM at
         * the time the soft mask is set (not at the time the soft mask is used for fill/stroke!),
         * for forms, this is the CTM at the time the form is invoked.
         * @param backdropColor the color according to the /bc entry to be used for luminosity soft
         * masks.
         * @throws IOException
         */
        private TransparencyGroup(PDTransparencyGroup form, boolean isSoftMask, Matrix ctm,
            PDColor backdropColor) throws IOException
        {
//            Graphics2D savedGraphics = graphics;
//            Area savedLastClip = lastClip;
//            Shape savedInitialClip = initialClip;

            // get the CTM x Form Matrix transform
            Matrix transform = Matrix.concatenate(ctm, form.getMatrix());

            // transform the bbox
            PDRectangle formBBox = form.getBBox();
            if (formBBox == null)
            {
                // PDFBOX-5471
                // check done here and not in caller to avoid getBBox() creating rectangle twice
                Log.w("PdfBox-Android", "transparency group ignored because BBox is null");
                formBBox = new PDRectangle();
            }
            Path transformedBox = formBBox.transform(transform);

            // clip the bbox to prevent giant bboxes from consuming all memory
//            Area transformed = new Area(transformedBox);
//            transformed.intersect(getGraphicsState().getCurrentClippingPath());
//            Rectangle2D clipRect = transformed.getBounds2D();
//            if (clipRect.isEmpty())
//            {
//                image = null;
//                bbox = null;
//                minX = 0;
//                minY = 0;
//                maxX = 0;
//                maxY = 0;
//                width = 0;
//                height = 0;
//                return;
//            }
//            this.bbox = new PDRectangle((float)clipRect.getX(), (float)clipRect.getY(),
//                (float)clipRect.getWidth(), (float)clipRect.getHeight());

            // apply the underlying Graphics2D device's DPI transform
            AffineTransform xformOriginal = xform;
            xform = AffineTransform.getScaleInstance(xformScalingFactorX, xformScalingFactorY);
//            Rectangle2D bounds = xform.createTransformedShape(clipRect).getBounds2D();

//            minX = (int) Math.floor(bounds.getMinX());
//            minY = (int) Math.floor(bounds.getMinY());
//            maxX = (int) Math.floor(bounds.getMaxX()) + 1;
//            maxY = (int) Math.floor(bounds.getMaxY()) + 1;

//            width = maxX - minX;
//            height = maxY - minY;

            // FIXME - color space
            if (isGray(form.getGroup().getColorSpace(form.getResources())))
            {
//                image = create2ByteGrayAlphaImage(width, height);
            }
            else
            {
//                image = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            }
//            Graphics2D g = image.createGraphics();

            boolean needsBackdrop = !isSoftMask && !form.getGroup().isIsolated() &&
                hasBlendMode(form, new HashSet<COSBase>());
            Bitmap backdropImage = null;
            // Position of this group in parent group's coordinates
            int backdropX = 0;
            int backdropY = 0;
            if (needsBackdrop)
            {
                if (transparencyGroupStack.isEmpty())
                {
                    // Use the current page as the parent group.
                    backdropImage = renderer.getPageImage();
                    if (backdropImage == null)
                    {
                        needsBackdrop = false;
                    }
                    else
                    {
//                        backdropX = minX;
//                        backdropY = backdropImage.getHeight() - maxY;
                    }
                }
                else
                {
                    TransparencyGroup parentGroup = transparencyGroupStack.peek();
//                    backdropImage = parentGroup.image;
//                    backdropX = minX - parentGroup.minX;
//                    backdropY = parentGroup.maxY - maxY;
                }
            }

//            Graphics2D g = image.createGraphics();
            if (needsBackdrop)
            {
                // backdropImage must be included in group image but not in group alpha.
//                g.drawImage(backdropImage, 0, 0, width, height,
//                    backdropX, backdropY, backdropX + width, backdropY + height, null);
//                g = new GroupGraphics(image, g);
            }
            if (isSoftMask && backdropColor != null)
            {
                // "If the subtype is Luminosity, the transparency group XObject G shall be
                // composited with a fully opaque backdrop whose colour is everywhere defined
                // by the soft-mask dictionary's BC entry."
//                g.setBackground(new Color(backdropColor.toRGB()));
//                g.clearRect(0, 0, width, height);
            }

            // flip y-axis
//            g.translate(0, image.getHeight());
//            g.scale(1, -1);

            boolean savedFlipTG = flipTG;
            flipTG = false;

            // apply device transform (DPI)
            // the initial translation is ignored, because we're not writing into the initial graphics device
//            g.transform(xform);

            PDRectangle pageSizeOriginal = pageSize;
//            pageSize = new PDRectangle(minX / xformScalingFactorX,
//                minY / xformScalingFactorY,
//                (float) (bounds.getWidth() / xformScalingFactorX),
//                (float) (bounds.getHeight() / xformScalingFactorY));
            Path.FillType clipWindingRuleOriginal = clipWindingRule;
            clipWindingRule = null;
            Path linePathOriginal = linePath;
            linePath = new Path();

            // adjust the origin
//            g.translate(-clipRect.getX(), -clipRect.getY());

//            graphics = g;
            setRenderingHints();
            try
            {
                if (isSoftMask)
                {
                    processSoftMask(form);
                }
                else
                {
                    transparencyGroupStack.push(this);
                    processTransparencyGroup(form);
                    if (!transparencyGroupStack.isEmpty())
                    {
                        transparencyGroupStack.pop();
                    }
                }

                if (needsBackdrop)
                {
//                    ((GroupGraphics) graphics).removeBackdrop(backdropImage, backdropX, backdropY);
                }
            }
            finally
            {
                flipTG = savedFlipTG;
//                lastClip = savedLastClip;
//                graphics.dispose();
//                graphics = savedGraphics;
//                initialClip = savedInitialClip;
                clipWindingRule = clipWindingRuleOriginal;
                linePath = linePathOriginal;
                pageSize = pageSizeOriginal;
                xform = xformOriginal;
            }
        }

        // http://stackoverflow.com/a/21181943/535646
//        private BufferedImage create2ByteGrayAlphaImage(int width, int height) TODO: PdfBox-Android

        private boolean isGray(PDColorSpace colorSpace)
        {
            if (colorSpace instanceof PDDeviceGray)
            {
                return true;
            }
            if (colorSpace instanceof PDICCBased)
            {
                try
                {
                    return ((PDICCBased) colorSpace).getAlternateColorSpace() instanceof PDDeviceGray;
                }
                catch (IOException ex)
                {
                    return false;
                }
            }
            return false;
        }

//        Bitmap getImage()

//        PDRectangle getBBox()

//        RectF getBounds()
    }

    private boolean hasBlendMode(PDTransparencyGroup group, Set<COSBase> groupsDone)
    {
        if (groupsDone.contains(group.getCOSObject()))
        {
            // The group was already processed. Avoid endless recursion.
            return false;
        }
        groupsDone.add(group.getCOSObject());

        PDResources resources = group.getResources();
        if (resources == null)
        {
            return false;
        }
        for (COSName name : resources.getExtGStateNames())
        {
            PDExtendedGraphicsState extGState = resources.getExtGState(name);
            if (extGState == null)
            {
                continue;
            }
            BlendMode blendMode = extGState.getBlendMode();
            if (blendMode != BlendMode.NORMAL)
            {
                return true;
            }
        }

        // Recursively process nested transparency groups
        for (COSName name : resources.getXObjectNames())
        {
            PDXObject xObject;
            try
            {
                xObject = resources.getXObject(name);
            }
            catch (IOException ex)
            {
                continue;
            }
            if (xObject instanceof PDTransparencyGroup &&
                hasBlendMode((PDTransparencyGroup)xObject, groupsDone))
            {
                return true;
            }
        }

        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void beginMarkedContentSequence(COSName tag, COSDictionary properties)
    {
        if (nestedHiddenOCGCount > 0)
        {
            nestedHiddenOCGCount++;
            return;
        }
        if (tag == null || getPage().getResources() == null)
        {
            return;
        }
        if (isHiddenOCG(getPage().getResources().getProperties(tag)))
        {
            nestedHiddenOCGCount = 1;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void endMarkedContentSequence()
    {
        if (nestedHiddenOCGCount > 0)
        {
            nestedHiddenOCGCount--;
        }
    }

    private boolean isContentRendered()
    {
        return nestedHiddenOCGCount <= 0;
    }

    private boolean isHiddenOCG(PDPropertyList propertyList)
    {
        if (propertyList instanceof PDOptionalContentGroup)
        {
            PDOptionalContentGroup group = (PDOptionalContentGroup) propertyList;
            RenderState printState = group.getRenderState(destination);
            if (printState == null)
            {
                if (!getRenderer().isGroupEnabled(group))
                {
                    return true;
                }
            }
            else if (RenderState.OFF.equals(printState))
            {
                return true;
            }
        }
        else if (propertyList instanceof PDOptionalContentMembershipDictionary)
        {
            return isHiddenOCMD((PDOptionalContentMembershipDictionary) propertyList);
        }
        return false;
    }

    private boolean isHiddenOCMD(PDOptionalContentMembershipDictionary ocmd)
    {
        if (ocmd.getCOSObject().getCOSArray(COSName.VE) != null)
        {
            // support seems to be optional, and is approximated by /P and /OCGS
            Log.i("PdfBox-Android", "/VE entry ignored in Optional Content Membership Dictionary");
        }
        List<PDPropertyList> oCGs = ocmd.getOCGs();
        if (oCGs.isEmpty())
        {
            return false;
        }
        List<Boolean> visibles = new ArrayList<Boolean>();
        for (PDPropertyList prop : oCGs)
        {
            visibles.add(!isHiddenOCG(prop));
        }
        COSName visibilityPolicy = ocmd.getVisibilityPolicy();
        // visible if any of the entries in OCGs are OFF
        if (COSName.ANY_OFF.equals(visibilityPolicy))
        {
            for (boolean visible : visibles)
            {
                if (!visible)
                {
                    return false;
                }
            }
            return true;
        }
        // visible only if all of the entries in OCGs are ON
        if (COSName.ALL_ON.equals(visibilityPolicy))
        {
            for (boolean visible : visibles)
            {
                if (!visible)
                {
                    return true;
                }
            }
            return false;
        }
        // visible only if all of the entries in OCGs are OFF
        if (COSName.ALL_OFF.equals(visibilityPolicy))
        {
            for (boolean visible : visibles)
            {
                if (visible)
                {
                    return true;
                }
            }
            return false;
        }
        // visible if any of the entries in OCGs are ON
        // AnyOn is default
        for (boolean visible : visibles)
        {
            if (visible)
            {
                return false;
            }
        }
        return true;
    }
}
