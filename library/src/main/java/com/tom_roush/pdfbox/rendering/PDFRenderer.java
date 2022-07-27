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
import android.graphics.Paint;

import java.io.IOException;

import com.tom_roush.pdfbox.cos.COSName;
import com.tom_roush.pdfbox.pdmodel.PDDocument;
import com.tom_roush.pdfbox.pdmodel.PDPage;
import com.tom_roush.pdfbox.pdmodel.PDPageTree;
import com.tom_roush.pdfbox.pdmodel.PDResources;
import com.tom_roush.pdfbox.pdmodel.common.PDRectangle;
import com.tom_roush.pdfbox.pdmodel.graphics.blend.BlendMode;
import com.tom_roush.pdfbox.pdmodel.graphics.optionalcontent.PDOptionalContentGroup;
import com.tom_roush.pdfbox.pdmodel.graphics.optionalcontent.PDOptionalContentProperties;
import com.tom_roush.pdfbox.pdmodel.graphics.state.PDExtendedGraphicsState;
import com.tom_roush.pdfbox.pdmodel.interactive.annotation.AnnotationFilter;
import com.tom_roush.pdfbox.pdmodel.interactive.annotation.PDAnnotation;

/**
 * Renders a PDF document to a Bitmap.
 * This class may be overridden in order to perform custom rendering.
 *
 * @author John Hewson
 */
public class PDFRenderer
{
    protected final PDDocument document;
    // TODO keep rendering state such as caches here

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

    private boolean subsamplingAllowed = false;

    private RenderDestination defaultDestination;

    private Bitmap pageImage;

    private float imageDownscalingOptimizationThreshold = 0.5f;

    private final PDPageTree pageTree;

    /**
     * Creates a new PDFRenderer.
     * @param document the document to render
     */
    public PDFRenderer(PDDocument document)
    {
        this.document = document;
        this.pageTree = document.getPages();
    }

    /**
     * Return the AnnotationFilter.
     *
     * @return the AnnotationFilter
     */
    public AnnotationFilter getAnnotationsFilter()
    {
        return annotationFilter;
    }

    /**
     * Set the AnnotationFilter.
     *
     * <p>Allows to only render annotation accepted by the filter.
     *
     * @param annotationsFilter the AnnotationFilter
     */
    public void setAnnotationsFilter(AnnotationFilter annotationsFilter)
    {
        this.annotationFilter = annotationsFilter;
    }

    /**
     * Value indicating if the renderer is allowed to subsample images before drawing, according to
     * image dimensions and requested scale.
     *
     * Subsampling may be faster and less memory-intensive in some cases, but it may also lead to
     * loss of quality, especially in images with high spatial frequency.
     *
     * @return true if subsampling of images is allowed, false otherwise.
     */
    public boolean isSubsamplingAllowed()
    {
        return subsamplingAllowed;
    }

    /**
     * Sets a value instructing the renderer whether it is allowed to subsample images before
     * drawing. The subsampling frequency is determined according to image size and requested scale.
     *
     * Subsampling may be faster and less memory-intensive in some cases, but it may also lead to
     * loss of quality, especially in images with high spatial frequency.
     *
     * @param subsamplingAllowed The new value indicating if subsampling is allowed.
     */
    public void setSubsamplingAllowed(boolean subsamplingAllowed)
    {
        this.subsamplingAllowed = subsamplingAllowed;
    }

    /**
     * @return the defaultDestination
     */
    public RenderDestination getDefaultDestination()
    {
        return defaultDestination;
    }

    /**
     * @param defaultDestination the defaultDestination to set
     */
    public void setDefaultDestination(RenderDestination defaultDestination)
    {
        this.defaultDestination = defaultDestination;
    }

    /**
     *
     * @return get the image downscaling optimization threshold. See
     * {@link #getImageDownscalingOptimizationThreshold()} for details.
     */
    public float getImageDownscalingOptimizationThreshold()
    {
        return imageDownscalingOptimizationThreshold;
    }

    /**
     * Set the image downscaling optimization threshold. This must be a value between 0 and 1. When
     * rendering downscaled images and rendering hints are set to bicubic+quality and the scaling is
     * smaller than the threshold, a more quality-optimized but slower method will be used. The
     * default is 0.5 which is a good compromise.
     *
     * @param imageDownscalingOptimizationThreshold
     */
    public void setImageDownscalingOptimizationThreshold(float imageDownscalingOptimizationThreshold)
    {
        this.imageDownscalingOptimizationThreshold = imageDownscalingOptimizationThreshold;
    }

    /**
     * Returns the given page as an RGB image at 72 DPI
     * @param pageIndex the zero-based index of the page to be converted.
     * @return the rendered page image
     * @throws IOException if the PDF cannot be read
     */
    public Bitmap renderImage(int pageIndex) throws IOException
    {
        return renderImage(pageIndex, 1);
    }

    /**
     * Returns the given page as an RGB image at the given scale.
     * A scale of 1 will render at 72 DPI.
     * @param pageIndex the zero-based index of the page to be converted
     * @param scale the scaling factor, where 1 = 72 DPI
     * @return the rendered page image
     * @throws IOException if the PDF cannot be read
     */
    public Bitmap renderImage(int pageIndex, float scale) throws IOException
    {
        return renderImage(pageIndex, scale, ImageType.RGB);
    }

    /**
     * Returns the given page as an RGB image at the given DPI.
     * @param pageIndex the zero-based index of the page to be converted
     * @param dpi the DPI (dots per inch) to render at
     * @return the rendered page image
     * @throws IOException if the PDF cannot be read
     */
    public Bitmap renderImageWithDPI(int pageIndex, float dpi) throws IOException
    {
        return renderImage(pageIndex, dpi / 72f, ImageType.RGB);
    }

    /**
     * Returns the given page as an RGB image at the given DPI.
     * @param pageIndex the zero-based index of the page to be converted
     * @param dpi the DPI (dots per inch) to render at
     * @param imageType the type of image to return
     * @return the rendered page image
     * @throws IOException if the PDF cannot be read
     */
    public Bitmap renderImageWithDPI(int pageIndex, float dpi, ImageType imageType)
        throws IOException
    {
        return renderImage(pageIndex, dpi / 72f, imageType);
    }

    /**
     * Returns the given page as an RGB or ARGB image at the given scale.
     * @param pageIndex the zero-based index of the page to be converted
     * @param scale the scaling factor, where 1 = 72 DPI
     * @param imageType the type of image to return
     * @return the rendered page image
     * @throws IOException if the PDF cannot be read
     */
    public Bitmap renderImage(int pageIndex, float scale, ImageType imageType)
        throws IOException
    {
        return renderImage(pageIndex, scale, imageType,
            defaultDestination == null ? RenderDestination.EXPORT : defaultDestination);
    }

    /**
     * Returns the given page as an RGB or ARGB image at the given scale.
     * @param pageIndex the zero-based index of the page to be converted
     * @param scale the scaling factor, where 1 = 72 DPI
     * @param imageType the type of image to return
     * @param destination controlling visibility of optional content groups
     * @return the rendered page image
     * @throws IOException if the PDF cannot be read
     */
    public Bitmap renderImage(int pageIndex, float scale, ImageType imageType, RenderDestination destination)
        throws IOException
    {
        PDPage page = pageTree.get(pageIndex);

        PDRectangle cropBox = page.getCropBox();
        float widthPt = cropBox.getWidth();
        float heightPt = cropBox.getHeight();

        // PDFBOX-4306 avoid single blank pixel line on the right or on the bottom
        int widthPx = (int) Math.max(Math.floor(widthPt * scale), 1);
        int heightPx = (int) Math.max(Math.floor(heightPt * scale), 1);

        // PDFBOX-4518 the maximum size (w*h) of a buffered image is limited to Integer.MAX_VALUE
        if ((long) widthPx * (long) heightPx > Integer.MAX_VALUE)
        {
            throw new IOException("Maximum size of image exceeded (w * h * scale ^ 2) = "//
                + widthPt + " * " + heightPt + " * " + scale + " ^ 2 > " + Integer.MAX_VALUE);
        }

        int rotationAngle = page.getRotation();

        Bitmap.Config bimType;
        if (imageType != ImageType.ARGB && hasBlendMode(page))
        {
            // PDFBOX-4095: if the PDF has blending on the top level, draw on transparent background
            // Inspired from PDF.js: if a PDF page uses any blend modes other than Normal,
            // PDF.js renders everything on a fully transparent RGBA canvas. 
            // Finally when the page has been rendered, PDF.js draws the RGBA canvas on a white canvas.
            bimType = Bitmap.Config.ARGB_8888;
        }
        else
        {
            bimType = imageType.toBitmapConfig();
        }

        // swap width and height
        Bitmap image;
        if (rotationAngle == 90 || rotationAngle == 270)
        {
            image = Bitmap.createBitmap(heightPx, widthPx, bimType);
        }
        else
        {
            image = Bitmap.createBitmap(widthPx, heightPx, bimType);
        }

        pageImage = image;

        // use a transparent background if the image type supports alpha
        Paint paint = new Paint();
        Canvas canvas = new Canvas(image);
        if (imageType == ImageType.ARGB)
        {
            paint.setColor(Color.TRANSPARENT);
        }
        else
        {
            paint.setColor(Color.WHITE);
        }
        paint.setStyle(Paint.Style.FILL);
        canvas.drawRect(0, 0, image.getWidth(), image.getHeight(), paint);
        paint.reset();

        transform(canvas, page.getRotation(), cropBox, scale, scale);

        // the end-user may provide a custom PageDrawer
        PageDrawerParameters parameters =
            new PageDrawerParameters(this, page, subsamplingAllowed, destination,
                imageDownscalingOptimizationThreshold);
        PageDrawer drawer = createPageDrawer(parameters);
        drawer.drawPage(paint, canvas, cropBox);

        if (image.getConfig() != imageType.toBitmapConfig())
        {
            // PDFBOX-4095: draw temporary transparent image on white background
            Bitmap newImage =
                Bitmap.createBitmap(image.getWidth(), image.getHeight(), imageType.toBitmapConfig());
            Canvas dstCanvas = new Canvas(newImage);
            paint.setColor(Color.WHITE);
            paint.setStyle(Paint.Style.FILL);
            dstCanvas.drawRect(0, 0, image.getWidth(), image.getHeight(), paint);
            dstCanvas.drawBitmap(image, 0.0f, 0.0f, paint);
            image = newImage;
        }

        return image;
    }

    /**
     * Renders a given page to a Canvas instance at 72 DPI.
     * <p>
     * Read {@link #renderPageToGraphics(int, Paint, Canvas, float, float, com.tom_roush.pdfbox.rendering.RenderDestination) renderPageToGraphics(int, Graphics2D, float, float, RenderDestination)}
     * before using this.
     *
     * @param pageIndex the zero-based index of the page to be converted
     * @param paint the Paint that will be used to draw the page
     * @param canvas the Canvas on which to draw the page
     * @throws IOException if the PDF cannot be read
     */
    public void renderPageToGraphics(int pageIndex, Paint paint, Canvas canvas) throws IOException
    {
        renderPageToGraphics(pageIndex, paint, canvas, 1);
    }

    /**
     * Renders a given page to a Canvas instance.
     * <p>
     * Read {@link #renderPageToGraphics(int, Paint, Canvas, float, float, com.tom_roush.pdfbox.rendering.RenderDestination) renderPageToGraphics(int, Graphics2D, float, float, RenderDestination)}
     * before using this.
     *
     * @param pageIndex the zero-based index of the page to be converted
     * @param paint the Paint that will be used to draw the page
     * @param canvas the Canvas on which to draw the page
     * @param scale the scaling factor, where 1 = 72 DPI
     * @throws IOException if the PDF cannot be read
     */
    public void renderPageToGraphics(int pageIndex, Paint paint, Canvas canvas, float scale)
        throws IOException
    {
        renderPageToGraphics(pageIndex, paint, canvas, scale, scale);
    }

    /**
     * Renders a given page to a Canvas instance.
     * <p>
     * Read {@link #renderPageToGraphics(int, Paint, Canvas, float, float, com.tom_roush.pdfbox.rendering.RenderDestination) renderPageToGraphics(int, Graphics2D, float, float, RenderDestination)}
     * before using this.
     *
     * @param pageIndex the zero-based index of the page to be converted
     * @param paint the Paint that will be used to draw the page
     * @param canvas the Canvas on which to draw the page
     * @param scaleX the scale to draw the page at for the x-axis, where 1 = 72 DPI
     * @param scaleY the scale to draw the page at for the y-axis, where 1 = 72 DPI
     * @throws IOException if the PDF cannot be read
     */
    public void renderPageToGraphics(int pageIndex, Paint paint, Canvas canvas, float scaleX, float scaleY)
        throws IOException
    {
        renderPageToGraphics(pageIndex, paint, canvas, scaleX, scaleY,
            defaultDestination == null ? RenderDestination.VIEW : defaultDestination);
    }

    /**
     * Renders a given page to a Canvas instance.
     *
     * @param pageIndex the zero-based index of the page to be converted
     * @param paint the Paint that will be used to draw the page
     * @param canvas the Canvas on which to draw the page
     * @param scaleX the scale to draw the page at for the x-axis, where 1 = 72 DPI
     * @param scaleY the scale to draw the page at for the y-axis, where 1 = 72 DPI
     * @param destination controlling visibility of optional content groups
     * @throws IOException if the PDF cannot be read
     */
    public void renderPageToGraphics(int pageIndex, Paint paint, Canvas canvas, float scaleX, float scaleY, RenderDestination destination)
        throws IOException
    {
        PDPage page = pageTree.get(pageIndex);
        // TODO need width/height calculations? should these be in PageDrawer?

        PDRectangle cropBox = page.getCropBox();
        transform(canvas, page.getRotation(), cropBox, scaleX, scaleY);
        canvas.drawRect(0, 0, cropBox.getWidth(), cropBox.getHeight(), paint);

        // the end-user may provide a custom PageDrawer
        PageDrawerParameters parameters =
            new PageDrawerParameters(this, page, subsamplingAllowed, destination,
                imageDownscalingOptimizationThreshold);
        PageDrawer drawer = createPageDrawer(parameters);
        drawer.drawPage(paint, canvas, cropBox);
    }

    /**
     * Indicates whether an optional content group is enabled.
     * @param group the group
     * @return true if the group is enabled
     */
    public boolean isGroupEnabled(PDOptionalContentGroup group)
    {
        PDOptionalContentProperties ocProperties = document.getDocumentCatalog().getOCProperties();
        return ocProperties == null || ocProperties.isGroupEnabled(group);
    }

    // scale rotate translate
    private void transform(Canvas canvas, int rotationAngle, PDRectangle cropBox, float scaleX, float scaleY)
    {
        canvas.scale(scaleX, scaleY);

        // TODO should we be passing the scale to PageDrawer rather than messing with Graphics?
        if (rotationAngle != 0)
        {
            float translateX = 0;
            float translateY = 0;
            switch (rotationAngle)
            {
                case 90:
                    translateX = cropBox.getHeight();
                    break;
                case 270:
                    translateY = cropBox.getWidth();
                    break;
                case 180:
                    translateX = cropBox.getWidth();
                    translateY = cropBox.getHeight();
                    break;
                default:
                    break;
            }
            canvas.translate(translateX, translateY);
            canvas.rotate(rotationAngle);
        }
    }

    /**
     * Returns a new PageDrawer instance, using the given parameters. May be overridden.
     */
    protected PageDrawer createPageDrawer(PageDrawerParameters parameters) throws IOException
    {
        PageDrawer pageDrawer = new PageDrawer(parameters);
        pageDrawer.setAnnotationFilter(annotationFilter);
        return pageDrawer;
    }

    private boolean hasBlendMode(PDPage page)
    {
        // check the current resources for blend modes
        PDResources resources = page.getResources();
        if (resources == null)
        {
            return false;
        }
        for (COSName name : resources.getExtGStateNames())
        {
            PDExtendedGraphicsState extGState = resources.getExtGState(name);
            if (extGState != null)
            {
                // extGState null can happen if key exists but no value
                // see PDFBOX-3950-23EGDHXSBBYQLKYOKGZUOVYVNE675PRD.pdf
                BlendMode blendMode = extGState.getBlendMode();
                if (blendMode != BlendMode.NORMAL)
                {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Returns the image to which the current page is being rendered.
     * May be null if the page is rendered to a Graphics2D object
     * instead of a Bitmap.
     */
    Bitmap getPageImage()
    {
        return pageImage;
    }
}
