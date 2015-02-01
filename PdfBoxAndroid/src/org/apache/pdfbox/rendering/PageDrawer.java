package org.apache.pdfbox.rendering;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.contentstream.PDFGraphicsStreamEngine;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDCIDFontType0;
import org.apache.pdfbox.pdmodel.font.PDCIDFontType2;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDTrueTypeFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.font.PDType1CFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.form.PDFormXObject;
import org.apache.pdfbox.pdmodel.graphics.image.PDImage;
import org.apache.pdfbox.pdmodel.graphics.shading.PDShading;
import org.apache.pdfbox.pdmodel.graphics.state.PDGraphicsState;
import org.apache.pdfbox.pdmodel.graphics.state.PDSoftMask;
import org.apache.pdfbox.pdmodel.graphics.state.RenderingMode;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotation;
import org.apache.pdfbox.rendering.font.CIDType0Glyph2D;
import org.apache.pdfbox.rendering.font.Glyph2D;
import org.apache.pdfbox.rendering.font.TTFGlyph2D;
import org.apache.pdfbox.rendering.font.Type1Glyph2D;
import org.apache.pdfbox.util.Vector;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;

/**
 * Paints a page in a PDF document to a Graphics context.
 * 
 * @author Ben Litchfield
 */
public final class PageDrawer extends PDFGraphicsStreamEngine
{
	private static final Log LOG = LogFactory.getLog(PageDrawer.class);
	
	// parent document renderer
	private final PDFRenderer renderer;
	
	// the graphics device to draw to, xform is the initial transform of the device (i.e. DPI)
	Paint paint;
	Canvas canvas;
	private Matrix xform;
	
	// the page box to draw (usually the crop box but may be another)
	PDRectangle pageSize;
	
	// clipping winding rule used for the clipping path
    private int clipWindingRule = -1;
    private Path linePath = new Path();

    // last clipping path
//    private Area lastClip;TODO

    // buffered clipping area for text being drawn
//    private Area textClippingArea;TODO

    private final Map<PDFont, Glyph2D> fontGlyph2D = new HashMap<PDFont, Glyph2D>();
	
	/**
	 * Constructor.
	 * 
	 * @param renderer renderer to render the page.
	 * @param page the page that is to be rendered.
	 * @throws IOException If there is an error loading properties from the file.
	 */
	public PageDrawer(PDFRenderer renderer, PDPage page) throws IOException
	{
		super(page);
		this.renderer = renderer;
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
	 * 
	 * @param g
	 * @param pageSize
	 * @throws IOException
	 */
	public void drawPage(Paint p, Canvas c, PDRectangle pageSize) throws IOException
	{
		paint = p;
		canvas = c;
		xform = canvas.getMatrix();
		this.pageSize = pageSize;
		
		setRenderingHints();
		
		canvas.translate(0, (int) pageSize.getHeight());
		canvas.scale(1, -1);
		
		paint.setStrokeCap(Paint.Cap.BUTT);
		paint.setStrokeJoin(Paint.Join.MITER);
		
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
//    public void drawTilingPattern(Graphics2D g, PDTilingPattern pattern, PDColorSpace colorSpace,
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
//    }

    /**
     * Returns an AWT paint for the given PDColor.
     */
//    private Paint getPaint(PDColor color) throws IOException
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
//    }

    // sets the clipping path using caching for performance, we track lastClip manually because
    // Graphics2D#getClip() returns a new object instead of the same one passed to setClip
    private void setClip()
    {
//        Area clippingPath = getGraphicsState().getCurrentClippingPath();
//        if (clippingPath != lastClip)
//        {
//            graphics.setClip(clippingPath);
//            lastClip = clippingPath;
//        }
    }

    @Override
    public void beginText() throws IOException
    {
        setClip();
    }

    @Override
    protected void showText(byte[] string) throws IOException
    {
        PDGraphicsState state = getGraphicsState();
        RenderingMode renderingMode = state.getTextState().getRenderingMode();

        // buffer the text clip because it represents a single clipping area
        if (renderingMode.isClip())
        {
//            textClippingArea = new Area();
        }

        super.showText(string);

        // apply the buffered clip as one area
        if (renderingMode.isClip())
        {
//            state.intersectClippingPath(textClippingArea);
//            textClippingArea = null;
        }
    }

//    @Override
//    protected void showFontGlyph(org.apache.pdfbox.util.Matrix textRenderingMatrix, PDFont font, int code, String unicode,
//                                 Vector displacement) throws IOException
//    {
//        android.graphics.Matrix at = textRenderingMatrix.createAffineTransform();
//        at.concatenate(font.getFontMatrix().createAffineTransform());
//
//        Glyph2D glyph2D = createGlyph2D(font);
//        drawGlyph2D(glyph2D, font, code, displacement, at);
//    }

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
                             android.graphics.Matrix at) throws IOException
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
                    at.postScale(pdfWidth / fontWidth, 1);
                }
            }

            // render glyph
//            Shape glyph = at.createTransformedShape(path);

            if (renderingMode.isFill())
            {
//                graphics.setComposite(state.getNonStrokingJavaComposite());
//                graphics.setPaint(getNonStrokingPaint());
            	paint.setColor(Color.BLACK);
                setClip();
//                graphics.fill(glyph);
                paint.setStyle(Paint.Style.FILL);
                canvas.clipPath(path);
                canvas.drawRect(new Rect(0, 0, canvas.getWidth(), canvas.getHeight()), paint);
            }

            if (renderingMode.isStroke())
            {
//                graphics.setComposite(state.getStrokingJavaComposite());
//                graphics.setPaint(getStrokingPaint());
            	paint.setColor(Color.BLACK);
//                graphics.setStroke(getStroke());
                setClip();
//                graphics.draw(glyph);
                paint.setStyle(Paint.Style.STROKE);
                canvas.clipPath(path);
                canvas.drawRect(new Rect(0, 0, canvas.getWidth(), canvas.getHeight()), paint);
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
//    }

//    private Paint applySoftMaskToPaint(Paint parentPaint, PDSoftMask softMask) throws IOException
//    {
//        if (softMask != null) 
//        {
//            return new SoftMaskPaint(parentPaint, createSoftMaskRaster(softMask));
//        }
//        else 
//        {
//            return parentPaint;
//        }
//    }

    // returns the stroking AWT Paint
//    private Paint getStrokingPaint() throws IOException
//    {
//        return applySoftMaskToPaint(
//                getPaint(getGraphicsState().getStrokingColor()),
//                getGraphicsState().getSoftMask());
//    }

    // returns the non-stroking AWT Paint
//    private Paint getNonStrokingPaint() throws IOException
//    {
//        return getPaint(getGraphicsState().getNonStrokingColor());
//    }

    // create a new stroke based on the current CTM and the current stroke
//    private BasicStroke getStroke()
//    {
//        PDGraphicsState state = getGraphicsState();
//
//        // apply the CTM
//        float lineWidth = transformWidth(state.getLineWidth());
//
//        // minimum line width as used by Adobe Reader
//        if (lineWidth < 0.25)
//        {
//            lineWidth = 0.25f;
//        }
//
//        PDLineDashPattern dashPattern = state.getLineDashPattern();
//        int phaseStart = dashPattern.getPhase();
//        float[] dashArray = dashPattern.getDashArray();
//        if (dashArray != null)
//        {
//            // apply the CTM
//            for (int i = 0; i < dashArray.length; ++i)
//            {
//                dashArray[i] = transformWidth(dashArray[i]);
//            }
//            phaseStart = (int)transformWidth(phaseStart);
//
//            // empty dash array is illegal
//            if (dashArray.length == 0)
//            {
//                dashArray = null;
//            }
//        }
//        return new BasicStroke(lineWidth, state.getLineCap(), state.getLineJoin(),
//                               state.getMiterLimit(), dashArray, phaseStart);
//    }

    @Override
    public void strokePath() throws IOException
    {
//        graphics.setComposite(getGraphicsState().getStrokingJavaComposite());
//        graphics.setPaint(getStrokingPaint());
//        graphics.setStroke(getStroke());
        setClip();
//        graphics.draw(linePath);
        linePath.reset();
    }

    @Override
    public void fillPath(int windingRule) throws IOException
    {
//        graphics.setComposite(getGraphicsState().getNonStrokingJavaComposite());
//        graphics.setPaint(getNonStrokingPaint());
        setClip();
//        linePath.setWindingRule(windingRule);

        // disable anti-aliasing for rectangular paths, this is a workaround to avoid small stripes
        // which occur when solid fills are used to simulate piecewise gradients, see PDFBOX-2302
//        boolean isRectangular = isRectangular(linePath);
//        if (isRectangular)
        {
//            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
//                                      RenderingHints.VALUE_ANTIALIAS_OFF);
        }

//        graphics.fill(linePath);
//        linePath.reset();

//        if (isRectangular)
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
//    }

    /**
     * Fills and then strokes the path.
     *
     * @param windingRule The winding rule this path will use.
     * @throws IOException If there is an IO error while filling the path.
     */
    @Override
    public void fillAndStrokePath(int windingRule) throws IOException
    {
        // TODO can we avoid cloning the path?
        Path path = new Path(linePath);
        fillPath(windingRule);
        linePath = path;
        strokePath();
    }

    @Override
    public void clip(int windingRule)
    {
        // the clipping path will not be updated until the succeeding painting operator is called
//        clipWindingRule = windingRule;
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
    	LOG.debug("PageDrawer.getCurrentPoint does not return the right value");
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
        org.apache.pdfbox.util.Matrix ctm = getGraphicsState().getCurrentTransformationMatrix();
        android.graphics.Matrix at = ctm.createAffineTransform();
        
        if (!pdImage.getInterpolate())
        {
        	float[] values = new float[9];
        	at.getValues(values);
        	boolean isScaledUp = pdImage.getWidth() < Math.round(values[Matrix.MSCALE_X]) ||
        	pdImage.getHeight() < Math.round(values[Matrix.MSCALE_X]);

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
//            PDColor color = getGraphicsState().getNonStrokingColor();
//            Bitmap image = pdImage.getStencilImage(getPaint(color));

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

    public void drawBufferedImage(Bitmap image, android.graphics.Matrix at) throws IOException
    {
//        graphics.setComposite(getGraphicsState().getNonStrokingJavaComposite());
        setClip();
        PDSoftMask softMask = getGraphicsState().getSoftMask();
        if( softMask != null )
        {
            android.graphics.Matrix imageTransform = new android.graphics.Matrix(at);
            imageTransform.postScale(1, -1);
            imageTransform.postTranslate(0, -1);
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
            android.graphics.Matrix imageTransform = new android.graphics.Matrix(at);
            imageTransform.postScale((1.0f / width), (-1.0f / height));
            imageTransform.postTranslate(0, -height);
//            graphics.drawImage(image, imageTransform, null);
        }
    }

    @Override
    public void shadingFill(COSName shadingName) throws IOException
    {
        PDShading shading = getResources().getShading(shadingName);
        org.apache.pdfbox.util.Matrix ctm = getGraphicsState().getCurrentTransformationMatrix();
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
//        private final int height;

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