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

import com.tom_roush.pdfbox.contentstream.PDFGraphicsStreamEngine;
import com.tom_roush.pdfbox.cos.COSName;
import com.tom_roush.pdfbox.pdmodel.common.PDRectangle;
import com.tom_roush.pdfbox.pdmodel.font.PDCIDFontType0;
import com.tom_roush.pdfbox.pdmodel.font.PDCIDFontType2;
import com.tom_roush.pdfbox.pdmodel.font.PDFont;
import com.tom_roush.pdfbox.pdmodel.font.PDTrueTypeFont;
import com.tom_roush.pdfbox.pdmodel.font.PDType0Font;
import com.tom_roush.pdfbox.pdmodel.font.PDType1CFont;
import com.tom_roush.pdfbox.pdmodel.font.PDType1Font;
import com.tom_roush.pdfbox.pdmodel.graphics.PDLineDashPattern;
import com.tom_roush.pdfbox.pdmodel.graphics.color.PDColor;
import com.tom_roush.pdfbox.pdmodel.graphics.color.PDColorSpace;
import com.tom_roush.pdfbox.pdmodel.graphics.form.PDFormXObject;
import com.tom_roush.pdfbox.pdmodel.graphics.image.PDImage;
import com.tom_roush.pdfbox.pdmodel.graphics.shading.PDShading;
import com.tom_roush.pdfbox.pdmodel.graphics.state.PDGraphicsState;
import com.tom_roush.pdfbox.pdmodel.graphics.state.PDSoftMask;
import com.tom_roush.pdfbox.pdmodel.graphics.state.RenderingMode;
import com.tom_roush.pdfbox.pdmodel.interactive.annotation.PDAnnotation;
import com.tom_roush.pdfbox.util.Matrix;
import com.tom_roush.pdfbox.util.Vector;
import com.tom_roush.harmony.awt.geom.AffineTransform;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Paints a page in a PDF document to a Canvas context. May be subclassed to provide custom
 * rendering.
 *
 * <p>If you want to do custom graphics processing rather than Canvas rendering, then you should
 * subclass PDFGraphicsStreamEngine instead. Subclassing PageDrawer is only suitable for cases
 * where the goal is to render onto a Canvas surface.
 *
 * @author Ben Litchfield
 */
public class PageDrawer extends PDFGraphicsStreamEngine
{
    // parent document renderer - note: this is needed for not-yet-implemented resource caching
    private final PDFRenderer renderer;

	// the graphics device to draw to, xform is the initial transform of the device (i.e. DPI)
	Paint paint;
	Canvas canvas;
	private AffineTransform xform;

	// the page box to draw (usually the crop box but may be another)
	private PDRectangle pageSize;

	// clipping winding rule used for the clipping path
    private Path.FillType clipWindingRule = null;
    private Path linePath = new Path();

    // last clipping path
    private Region lastClip;

    // buffered clipping area for text being drawn
    private Region textClippingArea;

    // glyph cache
    private final Map<PDFont, Glyph2D> fontGlyph2D = new HashMap<PDFont, Glyph2D>();

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
     * Sets high-quality rendering hints on the current Graphics2D.
     */
    private void setRenderingHints()
    {
//        graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
//                                  RenderingHints.VALUE_INTERPOLATION_BICUBIC);
//        graphics.setRenderingHint(RenderingHints.KEY_RENDERING,
//                                  RenderingHints.VALUE_RENDER_QUALITY);
        paint.setAntiAlias(true);
    }

	/**
	 * Draws the page to the requested canvas.
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
		this.pageSize = pageSize;

		setRenderingHints();

		canvas.translate(0, pageSize.getHeight());
		canvas.scale(1, -1);

		paint.setStrokeCap(Paint.Cap.BUTT);
		paint.setStrokeJoin(Paint.Join.MITER);
        paint.setStrokeWidth(1.0f);

		// adjust for non-(0,0) crop box
		canvas.translate(-pageSize.getLowerLeftX(), -pageSize.getLowerLeftY());

		processPage(getPage());

		for (PDAnnotation annotation : getPage().getAnnotations())
		{
			showAnnotation(annotation);
		}

//		graphics = null;
	}

	/**
     * Draws the pattern stream to the requested context.
     *
     * @param g The graphics context to draw onto.
     * @param pattern The tiling pattern to be used.
     * @param colorSpace color space for this tiling.
     * @param color color for this tiling.
     * @param patternMatrix the pattern matrix
     * @throws IOException If there is an IO error while drawing the page.
     */
//    void drawTilingPattern(Graphics2D g, PDTilingPattern pattern, PDColorSpace colorSpace,
//                                  PDColor color, Matrix patternMatrix) throws IOException
//    {
//        Graphics2D oldGraphics = graphics;
//        graphics = g;
//
//        Path oldLinePath = linePath;
//        linePath = new GeneralPath();
//
//        Area oldLastClip = lastClip;
//        lastClip = null;
//
//        setRenderingHints();
//        processTilingPattern(pattern, color, colorSpace, patternMatrix);
//
//        graphics = oldGraphics;
//        linePath = oldLinePath;
//        lastClip = oldLastClip;
//    } TODO: PdfBox-Android

    /**
     * Returns an AWT paint for the given PDColor.
     */
//    protected Paint getPaint(PDColor color) throws IOException
//    {
//        PDColorSpace colorSpace = color.getColorSpace();
//        if (!(colorSpace instanceof PDPattern))
//        {
//            float[] rgb = colorSpace.toRGB(color.getComponents());
//            return new Color(rgb[0], rgb[1], rgb[2]);
//        }
//        else
//        {
//            PDPattern patternSpace = (PDPattern)colorSpace;
//            PDAbstractPattern pattern = patternSpace.getPattern(color);
//            if (pattern instanceof PDTilingPattern)
//            {
//                PDTilingPattern tilingPattern = (PDTilingPattern) pattern;
//
//                if (tilingPattern.getPaintType() == PDTilingPattern.PAINT_COLORED)
//                {
//                    // colored tiling pattern
//                    return new TilingPaint(this, tilingPattern, xform);
//                }
//                else
//                {
//                    // uncolored tiling pattern
//                    return new TilingPaint(this, tilingPattern,
//                            patternSpace.getUnderlyingColorSpace(), color, xform);
//                }
//            }
//            else
//            {
//                PDShadingPattern shadingPattern = (PDShadingPattern)pattern;
//                PDShading shading = shadingPattern.getShading();
//                if (shading == null)
//                {
//                    LOG.error("shadingPattern is null, will be filled with transparency");
//                    return new Color(0,0,0,0);
//                }
//                return shading.toPaint(Matrix.concatenate(getInitialMatrix(),
//					shadingPattern.getMatrix()));
//            }
//        }
//    } TODO: PdfBox-Android

    // returns an integer for color that Android understands from the PDColor
	// TODO: alpha?
    private int getColor(PDColor color) throws IOException {
        PDColorSpace colorSpace = color.getColorSpace();
        float[] floats = colorSpace.toRGB(color.getComponents());
        int r = Math.round(floats[0] * 255);
        int g = Math.round(floats[1] * 255);
        int b = Math.round(floats[2] * 255);
        return Color.rgb(r, g, b);
    }

    // sets the clipping path using caching for performance, we track lastClip manually because
    // Graphics2D#getClip() returns a new object instead of the same one passed to setClip
    private void setClip()
    {
        Region clippingPath = getGraphicsState().getCurrentClippingPath();
        if (clippingPath != lastClip)
        {
            canvas.clipPath(clippingPath.getBoundaryPath());
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
        // buffer the text clip because it represents a single clipping area
        textClippingArea = new Region();
    }

    /**
     * End buffering the text clipping path, if any.
     */
    private void endTextClip()
    {
        PDGraphicsState state = getGraphicsState();
        RenderingMode renderingMode = state.getTextState().getRenderingMode();

        // apply the buffered clip as one area
        if (renderingMode.isClip() && !textClippingArea.isEmpty())
        {
            state.intersectClippingPath(textClippingArea);
            textClippingArea = null;
        }
    }

    @Override
    protected void showFontGlyph(Matrix textRenderingMatrix, PDFont font, int code, String unicode,
                                 Vector displacement) throws IOException
    {
        AffineTransform at = textRenderingMatrix.createAffineTransform();
        at.concatenate(font.getFontMatrix().createAffineTransform());

        Glyph2D glyph2D = createGlyph2D(font);
        drawGlyph2D(glyph2D, font, code, displacement, at);
    }

    /**
     * Render the font using the Glyph2D interface.
     *
     * @param glyph2D the Glyph2D implementation provided a GeneralPath for each glyph
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
            // stretch non-embedded glyph if it does not match the width contained in the PDF
            if (!font.isEmbedded())
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

            if (renderingMode.isFill())
            {
//                graphics.setComposite(state.getNonStrokingJavaComposite());
//                graphics.setPaint(getNonStrokingPaint());
            	paint.setColor(getNonStrokingColor());
                setClip();
//                graphics.fill(glyph);
                paint.setStyle(Paint.Style.FILL);
                canvas.drawPath(path, paint);
//                canvas.clipPath(path);
//                canvas.drawRect(new Rect(0, 0, canvas.getWidth(), canvas.getHeight()), paint);
            }

            if (renderingMode.isStroke())
            {
//                graphics.setComposite(state.getStrokingJavaComposite());
//                graphics.setPaint(getStrokingPaint());
            	paint.setColor(getStrokingColor());
//                graphics.setStroke(getStroke());
                setClip();
//                graphics.draw(glyph);
                paint.setStyle(Paint.Style.STROKE);
                canvas.drawPath(path, paint);
//                canvas.clipPath(path);
//                canvas.drawRect(new Rect(0, 0, canvas.getWidth(), canvas.getHeight()), paint);
            }

            if (renderingMode.isClip())
            {
//                textClippingArea.add(new Area(glyph));
            }
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
        // Is there already a Glyph2D for the given font?
        if (fontGlyph2D.containsKey(font))
        {
            return fontGlyph2D.get(font);
        }

        Glyph2D glyph2D = null;
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
//            fontGlyph2D.put(font, glyph2D); TODO: use caching
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
        linePath.moveTo(p0.x, p0.y);
        linePath.lineTo(p1.x, p1.y);
        linePath.lineTo(p2.x, p2.y);
        linePath.lineTo(p3.x, p3.y);

        // close the subpath instead of adding the last line so that a possible set line
        // cap style isn't taken into account at the "beginning" of the rectangle
        linePath.close();
    }

    /**
     * Generates AWT raster for a soft mask
     *
     * @param softMask soft mask
     * @return AWT raster for soft mask
     * @throws IOException
     */
//    private Raster createSoftMaskRaster(PDSoftMask softMask) throws IOException
//    {
//        TransparencyGroup transparencyGroup = new TransparencyGroup(softMask.getGroup(), true);
//        COSName subtype = softMask.getSubType();
//        if (COSName.ALPHA.equals(subtype))
//        {
//            return transparencyGroup.getAlphaRaster();
//        }
//        else if (COSName.LUMINOSITY.equals(subtype))
//        {
//            return transparencyGroup.getLuminosityRaster();
//        }
//        else
//        {
//            throw new IOException("Invalid soft mask subtype.");
//        }
//    } TODO: PdfBox-Android

//    private Paint applySoftMaskToPaint(Paint parentPaint, PDSoftMask softMask) throws IOException
//    {
//        if (softMask != null)
//        {
//            //TODO PDFBOX-2934
//            if (COSName.ALPHA.equals(softMask.getSubType()))
//            {
//                Log.i("PdfBox-Android", "alpha smask not implemented yet, is ignored");
//                return parentPaint;
//            }
//            return new SoftMaskPaint(parentPaint, createSoftMaskRaster(softMask));
//        }
//        else
//        {
//            return parentPaint;
//        }
//    } TODO: PdfBox-Android

    // returns the stroking AWT Paint
//    private Paint getStrokingPaint() throws IOException
//    {
//        return applySoftMaskToPaint(
//                getPaint(getGraphicsState().getStrokingColor()),
//                getGraphicsState().getSoftMask());
//    } TODO: PdfBox-Android

    private int getStrokingColor() throws IOException {
        return getColor(getGraphicsState().getStrokingColor());
    }

    // returns the non-stroking AWT Paint
//    private Paint getNonStrokingPaint() throws IOException
//    {
//        return getPaint(getGraphicsState().getNonStrokingColor());
//    } TODO: PdfBox-Android
    
    private int getNonStrokingColor() throws IOException {
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
        int phaseStart = dashPattern.getPhase();
        float[] dashArray = dashPattern.getDashArray();
        if (dashArray != null)
        {
            // apply the CTM
            for (int i = 0; i < dashArray.length; ++i)
            {
                // minimum line dash width avoids JVM crash, see PDFBOX-2373
                float w = transformWidth(dashArray[i]);
                if (w != 0)
                {
                    dashArray[i] = Math.max(w, 0.016f);
                }
            }
            phaseStart = (int) transformWidth(phaseStart);

            // empty dash array is illegal
            if (dashArray.length == 0)
            {
                dashArray = null;
            }
        }

        paint.setStrokeWidth(lineWidth);
        paint.setStrokeCap(state.getLineCap());
        paint.setStrokeJoin(state.getLineJoin());
        if (dashArray != null)
        {
            paint.setPathEffect(new DashPathEffect(dashArray, phaseStart));
        }
    }

    @Override
    public void strokePath() throws IOException
    {

//        graphics.setComposite(getGraphicsState().getStrokingJavaComposite());

        setStroke();
        setClip();
        paint.setARGB(255, 0, 0, 0); // TODO set the correct color from graphics state.
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(getStrokingColor());
        setClip();
        canvas.drawPath(linePath, paint);
        linePath.reset();
    }

    @Override
    public void fillPath(Path.FillType windingRule) throws IOException
    {
//        graphics.setComposite(getGraphicsState().getNonStrokingJavaComposite());
    	paint.setColor(getNonStrokingColor());
    	setClip();
    	linePath.setFillType(windingRule);

        // disable anti-aliasing for rectangular paths, this is a workaround to avoid small stripes
        // which occur when solid fills are used to simulate piecewise gradients, see PDFBOX-2302
        // note that we ignore paths with a width/height under 1 as these are fills used as strokes,
        // see PDFBOX-1658 for an example
//        RectF bounds = new RectF(();
//        linePath.computeBounds(bounds, true);
//        boolean noAntiAlias = isRectangular(linePath) && bounds.width() > 1 && bounds.height() > 1;
//        if (noAntiAlias)
        {
//            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
//                                      RenderingHints.VALUE_ANTIALIAS_OFF);
        }

        // TODO: PdfBox-Android: Commit 7f5861f9559e30ad68f0bcbce5b9dd2e7c2621b5 ?

        paint.setStyle(Paint.Style.FILL);
        canvas.drawPath(linePath, paint);     
        linePath.reset();

//        if (noAntiAlias)
        {
            // JDK 1.7 has a bug where rendering hints are reset by the above call to
            // the setRenderingHint method, so we re-set all hints, see PDFBOX-2302
        	setRenderingHints();
        }
    }

    /**
     * Returns true if the given path is rectangular.
     */
//    private boolean isRectangular(GeneralPath path)
//    {
//        PathIterator iter = path.getPathIterator(null);
//        double[] coords = new double[6];
//        int count = 0;
//        int[] xs = new int[4];
//        int[] ys = new int[4];
//        while (!iter.isDone())
//        {
//            switch(iter.currentSegment(coords))
//            {
//                case PathIterator.SEG_MOVETO:
//                    if (count == 0)
//                    {
//                        xs[count] = (int)Math.floor(coords[0]);
//                        ys[count] = (int)Math.floor(coords[1]);
//                    }
//                    else
//                    {
//                        return false;
//                    }
//                    count++;
//                    break;
//
//                case PathIterator.SEG_LINETO:
//                    if (count < 4)
//                    {
//                        xs[count] = (int)Math.floor(coords[0]);
//                        ys[count] = (int)Math.floor(coords[1]);
//                    }
//                    else
//                    {
//                        return false;
//                    }
//                    count++;
//                    break;
//
//                case PathIterator.SEG_CUBICTO:
//                    return false;
//
//                case PathIterator.SEG_CLOSE:
//                    break;
//            }
//            iter.next();
//        }
//
//        if (count == 4)
//        {
//            return xs[0] == xs[1] || xs[0] == xs[2] ||
//                   ys[0] == ys[1] || ys[0] == ys[3];
//        }
//        return false;
//    } TODO: PdfBox-Android

    /**
     * Fills and then strokes the path.
     *
     * @param windingRule The winding rule this path will use.
     * @throws IOException If there is an IO error while filling the path.
     */
    @Override
    public void fillAndStrokePath(Path.FillType windingRule) throws IOException
    {
        // TODO can we avoid cloning the path?
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
        linePath.moveTo(x, y);
    }

    @Override
    public void lineTo(float x, float y)
    {
        linePath.lineTo(x, y);
    }

    @Override
    public void curveTo(float x1, float y1, float x2, float y2, float x3, float y3)
    {
        linePath.cubicTo(x1, y1, x2, y2, x3, y3); // TODO: check if this should be relative
    }

    @Override
    public PointF getCurrentPoint()
    {
    	Log.d("PdfBox-Android", "PageDrawer.getCurrentPoint does not return the right value");
    	return new PointF();
//        return linePath.getCurrentPoint();
    }

    @Override
    public void closePath()
    {
        linePath.close();
    }

    @Override
    public void endPath()
    {
//        if (clipWindingRule != -1)
//        {
//            linePath.setWindingRule(clipWindingRule);
//            getGraphicsState().intersectClippingPath(linePath);
//            clipWindingRule = -1;
//        }
        linePath.reset();
    }

    @Override
    public void drawImage(PDImage pdImage) throws IOException
    {
        com.tom_roush.pdfbox.util.Matrix ctm = getGraphicsState().getCurrentTransformationMatrix();
        AffineTransform at = ctm.createAffineTransform();

        if (!pdImage.getInterpolate())
        {
        	boolean isScaledUp = pdImage.getWidth() < Math.round(at.getScaleX()) ||
        			pdImage.getHeight() < Math.round(at.getScaleY());

        	// if the image is scaled down, we use smooth interpolation, eg PDFBOX-2364
        	// only when scaled up do we use nearest neighbour, eg PDFBOX-2302 / mori-cvpr01.pdf
        	// stencils are excluded from this rule (see survey.pdf)
        	if (isScaledUp || pdImage.isStencil())
        	{
//        		graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
//        				RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        	}
        }

        if (pdImage.isStencil())
        {
            // fill the image with paint
//            Bitmap image = pdImage.getStencilImage(getNonStrokingPaint());

            // draw the image
//            drawBufferedImage(image, at);
        }
        else
        {
            // draw the image
            drawBufferedImage(pdImage.getImage(), at);
        }

        if (!pdImage.getInterpolate())
        {
        	// JDK 1.7 has a bug where rendering hints are reset by the above call to
        	// the setRenderingHint method, so we re-set all hints, see PDFBOX-2302
        	setRenderingHints();
        }
    }

    private void drawBufferedImage(Bitmap image, AffineTransform at) throws IOException
    {
//        graphics.setComposite(getGraphicsState().getNonStrokingJavaComposite());
        setClip();
        PDSoftMask softMask = getGraphicsState().getSoftMask();
        if( softMask != null )
        {
            AffineTransform imageTransform = new AffineTransform(at);
            imageTransform.scale(1, -1);
            imageTransform.translate(0, -1);
//            Paint awtPaint = new TexturePaint(image,
//                    new Rectangle2D.Double(imageTransform.getTranslateX(), imageTransform.getTranslateY(),
//                            imageTransform.getScaleX(), imageTransform.getScaleY()));
//            awtPaint = applySoftMaskToPaint(awtPaint, softMask);
//            graphics.setPaint(awtPaint);
            RectF unitRect = new RectF(0, 0, 1, 1);
//            graphics.fill(at.createTransformedShape(unitRect));
        }
        else
        {
            int width = image.getWidth();
            int height = image.getHeight();
            AffineTransform imageTransform = new AffineTransform(at);
            imageTransform.scale((1.0f / width), (-1.0f / height));
            imageTransform.translate(0, -height);
            canvas.drawBitmap(image, imageTransform.toMatrix(), paint);
        }
    }

    @Override
    public void shadingFill(COSName shadingName) throws IOException
    {
        PDShading shading = getResources().getShading(shadingName);
        com.tom_roush.pdfbox.util.Matrix ctm = getGraphicsState().getCurrentTransformationMatrix();
//        Paint paint = shading.toPaint(ctm);

//        graphics.setComposite(getGraphicsState().getNonStrokingJavaComposite());
//        graphics.setPaint(paint);
//        graphics.setClip(null);
//        lastClip = null;
//        graphics.fill(getGraphicsState().getCurrentClippingPath());
    }

    @Override
    public void showAnnotation(PDAnnotation annotation) throws IOException
    {
//        lastClip = null;
    	//TODO support more annotation flags (Invisible, NoZoom, NoRotate)
//    	int deviceType = graphics.getDeviceConfiguration().getDevice().getType();
//    	if (deviceType == GraphicsDevice.TYPE_PRINTER && !annotation.isPrinted())
//    	{
//    		return;
//    	} Shouldn't be needed
    	if (/*deviceType == GraphicsDevice.TYPE_RASTER_SCREEN && */annotation.isNoView())
    	{
    		return;
    	}
    	if (annotation.isHidden())
    	{
    		return;
    	}
    	super.showAnnotation(annotation);
    }

    @Override
    public void showTransparencyGroup(PDFormXObject form) throws IOException
    {
        TransparencyGroup group = new TransparencyGroup(form, false);

//        graphics.setComposite(getGraphicsState().getNonStrokingJavaComposite());
        setClip();

        // both the DPI xform and the CTM were already applied to the group, so all we do
        // here is draw it directly onto the Graphics2D device at the appropriate position
//        PDRectangle bbox = group.getBBox();
//        AffineTransform prev = graphics.getTransform();
//        float x = bbox.getLowerLeftX();
//        float y = pageSize.getHeight() - bbox.getLowerLeftY() - bbox.getHeight();
//        graphics.setTransform(AffineTransform.getTranslateInstance(x * xform.getScaleX(),
//                                                                   y * xform.getScaleY()));

        PDSoftMask softMask = getGraphicsState().getSoftMask();
        if (softMask != null)
        {
//            Bitmap image = group.getImage();
//            Paint awtPaint = new TexturePaint(image,
//                    new Rectangle2D.Float(0, 0, image.getWidth(), image.getHeight()));
//            awtPaint = applySoftMaskToPaint(awtPaint, softMask); // todo: PDFBOX-994 problem here?
//            graphics.setPaint(awtPaint);
//            graphics.fill(new Rectangle2D.Float(0, 0, bbox.getWidth() * (float)xform.getScaleX(),
//                                                bbox.getHeight() * (float)xform.getScaleY()));
        }
        else
        {
//            graphics.drawImage(group.getImage(), null, null);
        }

//        graphics.setTransform(prev);
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
//        private final int width;
//        private final int height; TODO: PdfBox-Android

        /**
         * Creates a buffered image for a transparency group result.
         */
        private TransparencyGroup(PDFormXObject form, boolean isSoftMask) throws IOException
        {
//            Graphics2D g2dOriginal = graphics;
//            Area lastClipOriginal = lastClip;

            // get the CTM x Form Matrix transform
//            Matrix ctm = getGraphicsState().getCurrentTransformationMatrix();
//            Matrix transform = Matrix.concatenate(ctm, form.getMatrix());

            // transform the bbox
//            Path transformedBox = form.getBBox().transform(transform);

            // clip the bbox to prevent giant bboxes from consuming all memory
//            Area clip = (Area)getGraphicsState().getCurrentClippingPath().clone();
//            clip.intersect(new Area(transformedBox));
//            Rectangle2D clipRect = clip.getBounds2D();
//            this.bbox = new PDRectangle((float)clipRect.getX(), (float)clipRect.getY(),
//                                        (float)clipRect.getWidth(), (float)clipRect.getHeight());

            // apply the underlying Graphics2D device's DPI transform
//            Shape deviceClip = xform.createTransformedShape(clip);
//            Rectangle2D bounds = deviceClip.getBounds2D();

//            minX = (int) Math.floor(bounds.getMinX());
//            minY = (int) Math.floor(bounds.getMinY());
//            int maxX = (int) Math.floor(bounds.getMaxX()) + 1;
//            int maxY = (int) Math.floor(bounds.getMaxY()) + 1;

//            width = maxX - minX;
//            height = maxY - minY;

//            image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB); // FIXME - color space
//            Graphics2D g = image.createGraphics();

            // flip y-axis
//            g.translate(0, height);
//            g.scale(1, -1);

            // apply device transform (DPI)
//            g.transform(xform);

            // adjust the origin
//            g.translate(-clipRect.getX(), -clipRect.getY());

//            graphics = g;
            try
            {
                if (isSoftMask)
                {
//                    processSoftMask(form);
                }
                else
                {
                    processTransparencyGroup(form);
                }
            }
            finally
            {
//                lastClip = lastClipOriginal;                
//                graphics.dispose();
//                graphics = g2dOriginal;
            }
        }

//        public Bitmap getImage()
//        {
//            return image;
//        }

//        public PDRectangle getBBox()
//        {
//            return bbox;
//        }

//        public Raster getAlphaRaster()
//        {
//            return image.getAlphaRaster();
//        }

//        public Raster getLuminosityRaster()
//        {
//            BufferedImage gray = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
//            Graphics g = gray.getGraphics();
//            g.drawImage(image, 0, 0, null);
//            g.dispose();
//
//            return gray.getRaster();
//        }
    }
}