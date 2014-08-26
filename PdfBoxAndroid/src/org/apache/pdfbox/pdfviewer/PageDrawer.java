package org.apache.pdfbox.pdfviewer;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.PDShading;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotation;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAppearanceDictionary;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAppearanceStream;
import org.apache.pdfbox.util.Matrix;
import org.apache.pdfbox.util.PDFStreamEngine;
import org.apache.pdfbox.util.ResourceLoader;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;

/**
 * This will paint a page in a PDF document to a graphics context.
 *
 * @author <a href="mailto:ben@benlitchfield.com">Ben Litchfield</a>
 * @version $Revision: 1.22 $
 */
public class PageDrawer extends PDFStreamEngine
{

    /**
     * Log instance.
     */
    private static final Log LOG = LogFactory.getLog(PageDrawer.class);

    private Paint paint;
    
    private Canvas canvas;
    
    /**
     * clipping winding rule used for the clipping path.
     */
    private int clippingWindingRule = -1;

    /**
     * Size of the page.
     */
    protected RectF pageSize;
    /**
     * Current page to be rendered.
     */
    protected PDPage page;

//    private GeneralPath linePath = new GeneralPath();TODO

//    private BasicStroke stroke = null;TODO
    
    /**
     * Default constructor, loads properties from file.
     *
     * @throws IOException If there is an error loading properties from the file.
     */
    public PageDrawer() throws IOException
    {
        super( ResourceLoader.loadProperties(
                "org/apache/pdfbox/resources/PageDrawer.properties", true ) );
    }

    /**
     * This will draw the page to the requested context.
     *
     * @param g The graphics context to draw onto.
     * @param p The page to draw.
     * @param pageDimension The size of the page to draw.
     *
     * @throws IOException If there is an IO error while drawing the page.
     */
    public void drawPage( Paint g, Canvas c, PDPage p, RectF pageDimension ) throws IOException
    {
        paint = (Paint)g;
        canvas = c;
        page = p;
        pageSize = pageDimension;
        
//        graphics.setRenderingHint( RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON );TODO
        paint.setFlags(Paint.ANTI_ALIAS_FLAG);
        // initialize the used stroke with CAP_BUTT instead of CAP_SQUARE
        paint.setStrokeWidth(1.0f);
        paint.setStrokeCap(Paint.Cap.BUTT);
        paint.setStrokeJoin(Paint.Join.MITER);
        // Only if there is some content, we have to process it. 
        // Otherwise we are done here and we will produce an empty page
        if ( page.getContents() != null) 
        {
            PDResources resources = page.findResources();
            processStream( page, resources, page.getContents().getStream() );
        }
        List<PDAnnotation> annotations = page.getAnnotations();
        for( int i=0; i<annotations.size(); i++ )
        {
            PDAnnotation annot = (PDAnnotation)annotations.get( i );
            PDRectangle rect = annot.getRectangle();
            String appearanceName = annot.getAppearanceStream();
            PDAppearanceDictionary appearDictionary = annot.getAppearance();
            if( appearDictionary != null )
            {
                if( appearanceName == null )
                {
                    appearanceName = "default";
                }
                Map<String, PDAppearanceStream> appearanceMap = appearDictionary.getNormalAppearance();
                if (appearanceMap != null) 
                { 
                    PDAppearanceStream appearance = 
                        (PDAppearanceStream)appearanceMap.get( appearanceName ); 
                    if( appearance != null ) 
                    { 
                        PointF point = new PointF(rect.getLowerLeftX(), rect.getLowerLeftY());
                        Matrix matrix = appearance.getMatrix();
                        if (matrix != null) 
                        {
                            // transform the rectangle using the given matrix 
                            android.graphics.Matrix at = matrix.createAffineTransform();
                            float[] points = {point.x, point.y};
                            at.mapPoints(points);
                            point.x = points[0];
                            point.y = points[1];
                        }
                        canvas.translate( (int)point.x, -(int)point.y );
                        processSubStream( page, appearance.getResources(), appearance.getStream() ); 
                        canvas.translate( -(int)point.x, (int)point.y); 
                    }
                }
            }
        }

    }

    /**
     * Remove all cached resources.
     */
    public void dispose()
    {
//        graphics = null;TODO
//        linePath = null;TODO
        page = null;
//        pageSize = null;TODO
    }

    /**
     * You should override this method if you want to perform an action when a
     * text is being processed.
     *
     * @param text The text to process
     */
//    protected void processTextPosition( TextPosition text )
//    {
//        try
//        {
//            PDGraphicsState graphicsState = getGraphicsState();
//            Composite composite;
//            Paint paint;
//            switch(graphicsState.getTextState().getRenderingMode()) 
//            {
//                case PDTextState.RENDERING_MODE_FILL_TEXT:
//                    composite = graphicsState.getNonStrokeJavaComposite();
//                    paint = graphicsState.getNonStrokingColor().getJavaColor();
//                    if (paint == null)
//                    {
//                        paint = graphicsState.getNonStrokingColor().getPaint(pageSize.height);
//                    }
//                    break;
//                case PDTextState.RENDERING_MODE_STROKE_TEXT:
//                    composite = graphicsState.getStrokeJavaComposite();
//                    paint = graphicsState.getStrokingColor().getJavaColor();
//                    if (paint == null)
//                    {
//                        paint = graphicsState.getStrokingColor().getPaint(pageSize.height);
//                    }
//                    break;
//                case PDTextState.RENDERING_MODE_NEITHER_FILL_NOR_STROKE_TEXT:
//                    //basic support for text rendering mode "invisible"
//                    Color nsc = graphicsState.getStrokingColor().getJavaColor();
//                    float[] components = {Color.black.getRed(),Color.black.getGreen(),Color.black.getBlue()};
//                    paint = new Color(nsc.getColorSpace(),components,0f);
//                    composite = graphicsState.getStrokeJavaComposite();
//                    break;
//                default:
//                    // TODO : need to implement....
//                    LOG.debug("Unsupported RenderingMode "
//                            + this.getGraphicsState().getTextState().getRenderingMode()
//                            + " in PageDrawer.processTextPosition()."
//                            + " Using RenderingMode "
//                            + PDTextState.RENDERING_MODE_FILL_TEXT
//                            + " instead");
//                    composite = graphicsState.getNonStrokeJavaComposite();
//                    paint = graphicsState.getNonStrokingColor().getJavaColor();
//            }
//            graphics.setComposite(composite);
//            graphics.setPaint(paint);
//            
//            PDFont font = text.getFont();
//            Matrix textPos = text.getTextPos().copy();
//            float x = textPos.getXPosition();
//            // the 0,0-reference has to be moved from the lower left (PDF) to the upper left (AWT-graphics)
//            float y = pageSize.height - textPos.getYPosition();
//            // Set translation to 0,0. We only need the scaling and shearing
//            textPos.setValue(2, 0, 0);
//            textPos.setValue(2, 1, 0);
//            // because of the moved 0,0-reference, we have to shear in the opposite direction
//            textPos.setValue(0, 1, (-1)*textPos.getValue(0, 1));
//            textPos.setValue(1, 0, (-1)*textPos.getValue(1, 0));
//            AffineTransform at = textPos.createAffineTransform();
//            PDMatrix fontMatrix = font.getFontMatrix();
//            at.scale(fontMatrix.getValue(0, 0) * 1000f, fontMatrix.getValue(1, 1) * 1000f);
//            //TODO setClip() is a massive performance hot spot. Investigate optimization possibilities
//            graphics.setClip(graphicsState.getCurrentClippingPath());
//            // the fontSize is no longer needed as it is already part of the transformation
//            // we should remove it from the parameter list in the long run
//            font.drawString( text.getCharacter(), text.getCodePoints(), graphics, 1, at, x, y );
//        }
//        catch( IOException io )
//        {
//            LOG.error (io, io);
//        }
//    }TODO

    /**
     * Get the graphics that we are currently drawing on.
     *
     * @return The graphics we are drawing on.
     */
//    public Graphics2D getGraphics()
//    {
//        return graphics;
//    }TODO

    /**
     * Get the page that is currently being drawn.
     *
     * @return The page that is being drawn.
     */
    public PDPage getPage()
    {
        return page;
    }

    /**
     * Get the size of the page that is currently being drawn.
     *
     * @return The size of the page that is being drawn.
     */
//    public Dimension getPageSize()
//    {
//        return pageSize;
//    }TODO

    /**
     * Fix the y coordinate.
     *
     * @param y The y coordinate.
     * @return The updated y coordinate.
     */
//    public double fixY( double y )
//    {
//        return pageSize.getHeight() - y;
//    }TODO

    /**
     * Get the current line path to be drawn.
     *
     * @return The current line path to be drawn.
     */
//    public GeneralPath getLinePath()
//    {
//        return linePath;
//    }TODO

    /**
     * Set the line path to draw.
     *
     * @param newLinePath Set the line path to draw.
     */
//    public void setLinePath(GeneralPath newLinePath)
//    {
//        if (linePath == null || linePath.getCurrentPoint() == null)
//        {
//            linePath = newLinePath;
//        }
//        else
//        {
//            linePath.append(newLinePath, false);
//        }
//    }TODO


    /**
     * Fill the path.
     *
     * @param windingRule The winding rule this path will use.
     * 
     * @throws IOException If there is an IO error while filling the path.
     */
//    public void fillPath(int windingRule) throws IOException
//    {
//        graphics.setComposite(getGraphicsState().getNonStrokeJavaComposite());
//        Paint nonStrokingPaint = getGraphicsState().getNonStrokingColor().getJavaColor();
//        if ( nonStrokingPaint == null )
//        {
//            nonStrokingPaint = getGraphicsState().getNonStrokingColor().getPaint(pageSize.height);
//        }
//        if ( nonStrokingPaint == null )
//        {
//            LOG.info("ColorSpace "+getGraphicsState().getNonStrokingColor().getColorSpace().getName()
//                    +" doesn't provide a non-stroking color, using white instead!");
//            nonStrokingPaint = Color.WHITE;
//        }
//        graphics.setPaint( nonStrokingPaint );
//        getLinePath().setWindingRule(windingRule);
//        graphics.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF );
//        graphics.setClip(getGraphicsState().getCurrentClippingPath());
//        graphics.fill( getLinePath() );
//        getLinePath().reset();
//    }TODO


    /**
     * This will set the current stroke.
     *
     * @param newStroke The current stroke.
     * 
     */
//    public void setStroke(BasicStroke newStroke)
//    {
//        stroke = newStroke;
//    }TODO

    /**
     * This will return the current stroke.
     *
     * @return The current stroke.
     * 
     */
//    public BasicStroke getStroke()
//    {
//        return stroke;
//    }TODO
    
    /**
     * Create a new stroke based on the current ctm and the current stroke.
     * 
     * @return the transformed stroke
     */
//    private BasicStroke calculateStroke()
//    {
//        float lineWidth = (float)getGraphicsState().getLineWidth();
//        Matrix ctm = getGraphicsState().getCurrentTransformationMatrix();
//        if (ctm != null && ctm.getXScale() > 0)
//        {
//            lineWidth = lineWidth * ctm.getXScale();
//        }
//        if (lineWidth < 0.25)
//        {
//            lineWidth = 0.25f;
//        }
//        BasicStroke currentStroke = null;
//        if (stroke == null)
//        {
//        	currentStroke = new BasicStroke(lineWidth);
//        }
//        else
//        {
//            float phaseStart = stroke.getDashPhase();
//            float[] dashArray = stroke.getDashArray();
//            if (dashArray != null)
//            {
//                if (ctm != null && ctm.getXScale() > 0)
//                {
//                    for (int i = 0; i < dashArray.length; ++i)
//                    {
//                        dashArray[i] *= ctm.getXScale();
//                    }
//                }
//                phaseStart *= ctm.getXScale();
//            }
//            currentStroke = new BasicStroke(lineWidth, stroke.getEndCap(), stroke.getLineJoin(),
//                    stroke.getMiterLimit(), dashArray, phaseStart);
//        }
//        return currentStroke;
//    }TODO
    
    /**
     * Stroke the path.
     *
     * @throws IOException If there is an IO error while stroking the path.
     */
//    public void strokePath() throws IOException
//    {
//        graphics.setComposite(getGraphicsState().getStrokeJavaComposite());
//        Paint strokingPaint = getGraphicsState().getStrokingColor().getJavaColor();
//        if ( strokingPaint == null )
//        {
//            strokingPaint = getGraphicsState().getStrokingColor().getPaint(pageSize.height);
//        }
//        if ( strokingPaint == null )
//        {
//            LOG.info("ColorSpace "+getGraphicsState().getStrokingColor().getColorSpace().getName()
//                    +" doesn't provide a stroking color, using white instead!");
//            strokingPaint = Color.WHITE;
//        }
//        graphics.setPaint(strokingPaint);
//        graphics.setStroke(calculateStroke());
//        graphics.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF );
//        graphics.setClip(getGraphicsState().getCurrentClippingPath());
//        GeneralPath path = getLinePath();
//        graphics.draw( path );
//        path.reset();
//    }TODO

    /**
     * Called when the color changed.
     * @param bStroking true for the stroking color, false for the non-stroking color
     * @throws IOException if an I/O error occurs
     */
    @Deprecated
    public void colorChanged(boolean bStroking) throws IOException
    {
        //logger().info("changing " + (bStroking ? "" : "non") + "stroking color");
    }

    //This code generalizes the code Jim Lynch wrote for AppendRectangleToPath
    /**
     * use the current transformation matrix to transform a single point.
     * @param x x-coordinate of the point to be transform
     * @param y y-coordinate of the point to be transform
     * @return the transformed coordinates as Point2D.Double
     */
//    public java.awt.geom.Point2D.Double transformedPoint(double x, double y)
//    {
//        double[] position = {x,y}; 
//        getGraphicsState().getCurrentTransformationMatrix().createAffineTransform().transform(
//                position, 0, position, 0, 1);
//        position[1] = fixY(position[1]);
//        return new Point2D.Double(position[0],position[1]);
//    }TODO

    /**
     * Set the clipping Path.
     *
     * @param windingRule The winding rule this path will use.
     * 
     * @deprecated use {@link #setClippingWindingRule(int)} instead
     * 
     */
    public void setClippingPath(int windingRule)
    {
        setClippingWindingRule(windingRule);
    }
    
    /**
     * Set the clipping winding rule.
     *
     * @param windingRule The winding rule which will be used for clipping.
     * 
     */
    public void setClippingWindingRule(int windingRule)
    {
        clippingWindingRule = windingRule;
    }

    /**
     * Set the clipping Path.
     *
     */
//    public void endPath()
//    {
//        if (clippingWindingRule > -1)
//        {
//            PDGraphicsState graphicsState = getGraphicsState();
//            GeneralPath clippingPath = (GeneralPath)getLinePath().clone();
//            clippingPath.setWindingRule(clippingWindingRule);
//            // If there is already set a clipping path, we have to intersect the new with the existing one
//            if (graphicsState.getCurrentClippingPath() != null) 
//            {
//                Area currentArea = new Area(getGraphicsState().getCurrentClippingPath());
//                Area newArea = new Area(clippingPath);
//                currentArea.intersect(newArea);
//                graphicsState.setCurrentClippingPath(currentArea);
//            }
//            else 
//            {
//                graphicsState.setCurrentClippingPath(clippingPath);
//            }
//            clippingWindingRule = -1;
//        }
//        getLinePath().reset();
//    }TODO
    /**
     * Draw the AWT image. Called by Invoke.
     * Moved into PageDrawer so that Invoke doesn't have to reach in here for Graphics as that breaks extensibility.
     *
     * @param awtImage The image to draw.
     * @param at The transformation to use when drawing.
     * 
     */
//    public void drawImage(Image awtImage, AffineTransform at)
//    {
//        graphics.setComposite(getGraphicsState().getStrokeJavaComposite());
//        graphics.setClip(getGraphicsState().getCurrentClippingPath());
//        graphics.drawImage( awtImage, at, null );
//    }TODO
    
    /**
     * Fill with Shading.  Called by SHFill operator.
     *
     * @param ShadingName  The name of the Shading Dictionary to use for this fill instruction.
     *
     * @throws IOException If there is an IO error while shade-filling the path/clipping area.
     * 
     * @deprecated use {@link #shFill(COSName)} instead.
     */
//    public void SHFill(COSName ShadingName) throws IOException
//    {
//        shFill(ShadingName);
//    }TODO
    
    /**
     * Fill with Shading.  Called by SHFill operator.
     *
     * @param shadingName  The name of the Shading Dictionary to use for this fill instruction.
     *
     * @throws IOException If there is an IO error while shade-filling the clipping area.
     */
//    public void shFill(COSName shadingName) throws IOException
//    {
//        PDShadingResources shading = getResources().getShadings().get(shadingName.getName());
//        LOG.debug("Shading = " + shading.toString());
//        int shadingType = shading.getShadingType();
//        Matrix ctm = getGraphicsState().getCurrentTransformationMatrix();
//        Paint paint = null;
//        switch (shadingType)
//        {
//            case 1:
//                paint = new Type1ShadingPaint((PDShadingType1)shading, ctm, pageSize.height);
//                break;
//            case 2:
//                paint = new AxialShadingPaint((PDShadingType2)shading, ctm, pageSize.height);
//                break;
//            case 3:
//                paint = new RadialShadingPaint((PDShadingType3)shading, ctm, pageSize.height);
//                break;
//            case 4:
//                paint = new Type4ShadingPaint((PDShadingType4)shading, ctm, pageSize.height);
//                break;
//            case 5:
//                paint = new Type5ShadingPaint((PDShadingType5)shading, ctm, pageSize.height);
//                break;
//            case 6:
//            case 7:
//                // TODO
//                LOG.debug("Shading type "+shadingType+" not yet supported");
//                paint = new Color(0, 0, 0, 0); // transparent
//                break;
//            default:
//                throw new IOException("Invalid ShadingType " + shadingType + " for Shading " + shadingName);
//        }
//        graphics.setComposite(getGraphicsState().getNonStrokeJavaComposite());
//        graphics.setPaint(paint);
//        graphics.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF );
//        graphics.fill( getGraphicsState().getCurrentClippingPath() );
//    }TODO
    /**
     * Fill with a Function-based gradient / shading.  
     * If extending the class, override this and its siblings, not the public SHFill method.
     *
     * @param Shading  The Shading Dictionary to use for this fill instruction.
     *
     * @throws IOException If there is an IO error while shade-filling the path/clipping area.
     */
    protected void SHFill_Function(PDShading Shading) throws IOException
    {
        throw new IOException("Not Implemented");
    }

    /**
     * Fill with an Axial Shading.  
     * If extending the class, override this and its siblings, not the public SHFill method.
     *
     * @param Shading  The Shading Dictionary to use for this fill instruction.
     *
     * @throws IOException If there is an IO error while shade-filling the path/clipping area.
     */
    protected void SHFill_Axial(PDShading Shading) throws IOException
    {
        throw new IOException("Not Implemented");
        
    }

    /**
     * Fill with a Radial gradient / shading.  
     * If extending the class, override this and its siblings, not the public SHFill method.
     *
     * @param Shading  The Shading Dictionary to use for this fill instruction.
     *
     * @throws IOException If there is an IO error while shade-filling the path/clipping area.
     */
    protected void SHFill_Radial(PDShading Shading) throws IOException
    {
        throw new IOException("Not Implemented");
    }
    
    /**
     * Fill with a Free-form Gourad-shaded triangle mesh.
     * If extending the class, override this and its siblings, not the public SHFill method.
     *
     * @param Shading  The Shading Dictionary to use for this fill instruction.
     *
     * @throws IOException If there is an IO error while shade-filling the path/clipping area.
     */
    protected void SHFill_FreeGourad(PDShading Shading) throws IOException
    {
        throw new IOException("Not Implemented");
    }
    
    /**
     * Fill with a Lattice-form Gourad-shaded triangle mesh.
     * If extending the class, override this and its siblings, not the public SHFill method.
     *
     * @param Shading  The Shading Dictionary to use for this fill instruction.
     *
     * @throws IOException If there is an IO error while shade-filling the path/clipping area.
     */
    protected void SHFill_LatticeGourad(PDShading Shading) throws IOException
    {
        throw new IOException("Not Implemented");
    }
    
    /**
     * Fill with a Coons patch mesh
     * If extending the class, override this and its siblings, not the public SHFill method.
     *
     * @param Shading  The Shading Dictionary to use for this fill instruction.
     *
     * @throws IOException If there is an IO error while shade-filling the path/clipping area.
     */
    protected void SHFill_CoonsPatch(PDShading Shading) throws IOException
    {
        throw new IOException("Not Implemented");
    }
    
    /**
     * Fill with a Tensor-product patch mesh.
     * If extending the class, override this and its siblings, not the public SHFill method.
     *
     * @param Shading  The Shading Dictionary to use for this fill instruction.
     *
     * @throws IOException If there is an IO error while shade-filling the path/clipping area.
     */
    protected void SHFill_TensorPatch(PDShading Shading) throws IOException
    {
        throw new IOException("Not Implemented");
    }
}
