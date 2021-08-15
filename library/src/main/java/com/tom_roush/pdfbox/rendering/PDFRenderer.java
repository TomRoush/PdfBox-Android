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

import com.tom_roush.pdfbox.pdmodel.PDDocument;
import com.tom_roush.pdfbox.pdmodel.PDPage;
import com.tom_roush.pdfbox.pdmodel.common.PDRectangle;

/**
 * Renders a PDF document to an AWT BufferedImage.
 * This class may be overridden in order to perform custom rendering.
 *
 * @author John Hewson
 */
public class PDFRenderer
{
    protected final PDDocument document;
    // TODO keep rendering state such as caches here

    /**
     * Creates a new PDFRenderer.
     * @param document the document to render
     */
    public PDFRenderer(PDDocument document)
    {
        this.document = document;
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
        PDPage page = document.getPage(pageIndex);

        PDRectangle cropbBox = page.getCropBox();
        float widthPt = cropbBox.getWidth();
        float heightPt = cropbBox.getHeight();
        int widthPx = Math.round(widthPt * scale);
        int heightPx = Math.round(heightPt * scale);
        int rotationAngle = page.getRotation();

        // swap width and height
        Bitmap image;
        if (rotationAngle == 90 || rotationAngle == 270)
        {
            image = Bitmap.createBitmap(heightPx, widthPx, imageType.toBitmapConfig());
        }
        else
        {
            image = Bitmap.createBitmap(widthPx, heightPx, imageType.toBitmapConfig());
        }

        // use a transparent background if the imageType supports alpha
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

        renderPage(page, paint, canvas, image.getWidth(), image.getHeight(), scale, scale);

        return image;
    }

    /**
     * Renders a given page to an AWT Graphics2D instance.
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
     * Renders a given page to an AWT Graphics2D instance.
     * @param pageIndex the zero-based index of the page to be converted
     * @param paint the Paint that will be used to draw the page
     * @param canvas the Canvas on which to draw the page
     * @param scale the scale to draw the page at
     * @throws IOException if the PDF cannot be read
     */
    public void renderPageToGraphics(int pageIndex, Paint paint, Canvas canvas, float scale)
        throws IOException
    {
        PDPage page = document.getPage(pageIndex);
        // TODO need width/wight calculations? should these be in PageDrawer?
        PDRectangle adjustedCropBox = page.getCropBox();
        renderPage(page, paint, canvas, (int)adjustedCropBox.getWidth(), (int)adjustedCropBox.getHeight(), scale, scale);
    }

    // renders a page to the given graphics
    private void renderPage(PDPage page, Paint paint, Canvas canvas, int width, int height, float scaleX,
        float scaleY) throws IOException
    {
        canvas.scale(scaleX, scaleY);
        // TODO should we be passing the scale to PageDrawer rather than messing with Graphics?

//        graphics.clearRect(0, 0, width, height);

        PDRectangle cropBox = page.getCropBox();
        int rotationAngle = page.getRotation();

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
            }
            canvas.translate(translateX, translateY);
            canvas.rotate((float) Math.toRadians(rotationAngle));
        }

        // the end-user may provide a custom PageDrawer
        PageDrawerParameters parameters = new PageDrawerParameters(this, page);
        PageDrawer drawer = createPageDrawer(parameters);
        drawer.drawPage(paint, canvas, cropBox);
    }

    /**
     * Returns a new PageDrawer instance, using the given parameters. May be overridden.
     */
    protected PageDrawer createPageDrawer(PageDrawerParameters parameters) throws IOException
    {
        return new PageDrawer(parameters);
    }
}
