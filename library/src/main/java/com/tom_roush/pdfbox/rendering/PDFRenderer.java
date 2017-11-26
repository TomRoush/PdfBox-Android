package com.tom_roush.pdfbox.rendering;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.tom_roush.pdfbox.pdmodel.PDDocument;
import com.tom_roush.pdfbox.pdmodel.PDPage;
import com.tom_roush.pdfbox.pdmodel.common.PDRectangle;

import java.io.IOException;

/**
 * Renders a PDF document to an AWT BufferedImage.
 * This class may be overridden in order to perform custom rendering.
 * @author John Hewson
 * @author Andreas Lehmkühler
 *
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
		return renderImage(pageIndex, 1, Bitmap.Config.ARGB_8888);
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
        return renderImage(pageIndex, dpi / 72f, Bitmap.Config.ARGB_8888);
    }

    /**
     * Returns the given page as an RGB image at the given DPI.
     * @param pageIndex the zero-based index of the page to be converted
     * @param dpi the DPI (dots per inch) to render at
     * @param imageType the type of image to return
     * @return the rendered page image
     * @throws IOException if the PDF cannot be read
     */
    public Bitmap renderImageWithDPI(int pageIndex, float dpi, Bitmap.Config imageType)
        throws IOException
    {
        return renderImage(pageIndex, dpi / 72f, imageType);
    }
	
	/**
	 * Returns the given page as an RGB image at the given scale.
	 * @param pageIndex the zero-based index of the page to be converted
	 * @param scale the scaling factor, where 1 = 72 DPI
     * @param config the bitmap config to create
	 * @return the rendered page image
	 * @throws IOException if the PDF cannot be read
	 */
	public Bitmap renderImage(int pageIndex, float scale, Bitmap.Config config) throws IOException
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
            image = Bitmap.createBitmap(heightPx, widthPx, config);
        }
        else
        {
            image = Bitmap.createBitmap(widthPx, heightPx, config);
        }

        // use a transparent background if the imageType supports alpha
        Paint paint = new Paint();
        Canvas canvas = new Canvas(image);
        if (config != Bitmap.Config.ARGB_8888)
        {
            paint.setColor(Color.WHITE);
            paint.setStyle(Paint.Style.FILL);
            canvas.drawRect(0, 0, image.getWidth(), image.getHeight(), paint);
            paint.reset();
        }

        renderPage(page, paint, canvas, image.getWidth(), image.getHeight(), scale, scale);

        return image;
	}
	
	// renders a page to the given graphics
    public void renderPage(PDPage page, Paint paint, Canvas canvas, int width, int height, float scaleX,
                            float scaleY) throws IOException
    {
        canvas.scale(scaleX, scaleY);

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
