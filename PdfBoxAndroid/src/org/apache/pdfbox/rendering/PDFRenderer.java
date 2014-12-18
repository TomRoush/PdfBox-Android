package org.apache.pdfbox.rendering;

import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

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
		return renderImage(pageIndex, 1);
	}
	
	/**
	 * Returns the given page as an RGB image at the given scale.
	 * @param pageIndex the zero-based index of the page to be converted
	 * @param scale the scaling factor, where 1 = 72 DPI
	 * @return the rendered page image
	 * @throws IOException if the PDF cannot be read
	 */
	public Bitmap renderImage(int pageIndex, float scale) throws IOException
	{
		PDPage page = document.getPage(pageIndex);

        PDRectangle cropbBox = page.getCropBox();
        float widthPt = cropbBox.getWidth();
        float heightPt = cropbBox.getHeight();
        int widthPx = Math.round(widthPt * scale);
        int heightPx = Math.round(heightPt * scale);
        int rotationAngle = page.getRotation();

        // normalize the rotation angle
        if (rotationAngle < 0)
        {
            rotationAngle += 360;
        }
        else if (rotationAngle >= 360)
        {
            rotationAngle -= 360;
        }

        // swap width and height
        Bitmap image;
        if (rotationAngle == 90 || rotationAngle == 270)
        {
            image = Bitmap.createBitmap(heightPx, widthPx, Bitmap.Config.RGB_565);
        }
        else
        {
            image = Bitmap.createBitmap(widthPx, heightPx, Bitmap.Config.RGB_565);
        }

        // use a transparent background if the imageType supports alpha
//        Graphics2D g = image.createGraphics();
        Paint paint = new Paint();
        Canvas canvas = new Canvas(image);
//        if (imageType != Bitmap.Config.ARGB_8888)
//        {
            paint.setColor(Color.WHITE);
            paint.setStyle(Paint.Style.FILL);
            canvas.drawRect(0, 0, image.getWidth(), image.getHeight(), paint);
//        }

        renderPage(page, paint, canvas, image.getWidth(), image.getHeight(), scale, scale);
//        g.dispose();

        return image;
	}
	
	// renders a page to the given graphics
    private void renderPage(PDPage page, Paint paint, Canvas canvas, int width, int height, float scaleX,
                            float scaleY) throws IOException
    {
//        graphics.clearRect(0, 0, width, height);

        canvas.scale(scaleX, scaleY);
        // TODO should we be passing the scale to PageDrawer rather than messing with Graphics?

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

        PageDrawer drawer = new PageDrawer(this, page);
        drawer.drawPage(paint, canvas, cropBox);
    }
}
