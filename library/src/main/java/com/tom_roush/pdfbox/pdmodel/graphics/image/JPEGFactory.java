package com.tom_roush.pdfbox.pdmodel.graphics.image;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.tom_roush.pdfbox.cos.COSName;
import com.tom_roush.pdfbox.io.IOUtils;
import com.tom_roush.pdfbox.pdmodel.PDDocument;
import com.tom_roush.pdfbox.pdmodel.graphics.color.PDDeviceGray;
import com.tom_roush.pdfbox.pdmodel.graphics.color.PDDeviceRGB;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Factory for creating a PDImageXObject containing a JPEG compressed image.
 * @author John Hewson
 */
public final class JPEGFactory
{
	private JPEGFactory()
	{
	}

	/**
	 * Creates a new JPEG Image XObject from an input stream containing JPEG data.
	 * 
	 * The input stream data will be preserved and embedded in the PDF file without modification.
	 * @param document the document where the image will be created
	 * @param stream a stream of JPEG data
	 * @return a new Image XObject
	 * 
	 * @throws IOException if the input stream cannot be read
	 */
	public static PDImageXObject createFromStream(PDDocument document, InputStream stream)
			throws IOException
	{
		// copy stream
		ByteArrayInputStream byteStream = new ByteArrayInputStream(IOUtils.toByteArray(stream));

		// read image
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeStream(byteStream, null, options);
		byteStream.reset();

		// create Image XObject from stream
		PDImageXObject pdImage = new PDImageXObject(document, byteStream,
            COSName.DCT_DECODE, options.outWidth, options.outHeight,
            8, //awtImage.getColorModel().getComponentSize(0),
            PDDeviceRGB.INSTANCE //getColorSpaceFromAWT(awtImage)); // TODO: PdfBox-Android
        );

		return pdImage;
	}

	/**
	 * Creates a new JPEG Image XObject from a Buffered Image.
	 * @param document the document where the image will be created
	 * @param image the buffered image to embed
	 * @return a new Image XObject
	 * @throws IOException if the JPEG data cannot be written
	 */
    public static PDImageXObject createFromImage(PDDocument document, Bitmap image)
        throws IOException
    {
        return createFromImage(document, image, 0.75f);
    }

	/**
	 * Creates a new JPEG Image XObject from a Buffered Image and a given quality.
	 * The image will be created at 72 DPI.
	 * @param document the document where the image will be created
	 * @param image the buffered image to embed
	 * @param quality the desired JPEG compression quality
	 * @return a new Image XObject
	 * @throws IOException if the JPEG data cannot be written
	 */
    public static PDImageXObject createFromImage(PDDocument document, Bitmap image,
            float quality) throws IOException
    {
        return createFromImage(document, image, quality, 72);
    }

	/**
	 * Creates a new JPEG Image XObject from a Buffered Image, a given quality and DPI.
	 * @param document the document where the image will be created
	 * @param image the buffered image to embed
	 * @param quality the desired JPEG compression quality
	 * @param dpi the desired DPI (resolution) of the JPEG
	 * @return a new Image XObject
	 * @throws IOException if the JPEG data cannot be written
	 */
    public static PDImageXObject createFromImage(PDDocument document, Bitmap image,
                                                 float quality, int dpi) throws IOException
    {
        return createJPEG(document, image, quality, dpi);
    }

	// returns the alpha channel of an image
    private static Bitmap getAlphaImage(Bitmap image) throws IOException
    {
        if (!image.hasAlpha())
        {
            return null;
        }
        return image.extractAlpha(); 
    }

	// Creates an Image XObject from a Buffered Image using JAI Image I/O
    private static PDImageXObject createJPEG(PDDocument document, Bitmap image,
            float quality, int dpi) throws IOException
    {
        Bitmap whiteImage = Bitmap.createBitmap(image.getWidth(), image.getHeight(),image.getConfig()); 
        Bitmap alphaImage = getAlphaImage(image);

        whiteImage.eraseColor(Color.WHITE);
        Canvas canvas = new Canvas(whiteImage);
        canvas.drawBitmap(image, 0f, 0f, null);
        image.recycle();

        ByteArrayOutputStream bos = new ByteArrayOutputStream(); 
        whiteImage.compress(Bitmap.CompressFormat.JPEG, (int)(quality * 100), bos); 
        byte[] bitmapData = bos.toByteArray();
        ByteArrayInputStream byteStream = new ByteArrayInputStream(bitmapData);

        PDImageXObject pdImage = new PDImageXObject(document, byteStream, 
                COSName.DCT_DECODE, whiteImage.getWidth(), whiteImage.getHeight(), 
                8, 
                PDDeviceRGB.INSTANCE 
        );

        // alpha -> soft mask
        if (alphaImage != null)
        {
            ByteArrayOutputStream aBos = new ByteArrayOutputStream(); 
            // This is problematic at the moment as
            // compress does not seem to support ALPHA_8 as returned by getAlphaImage()
            boolean ok = alphaImage.compress(Bitmap.CompressFormat.JPEG, (int)(quality * 100), aBos); 
            System.err.println("Compressing alpha image: " + String.valueOf(ok) + " " + alphaImage.getConfig().toString());
            byte[] aBitmapData = aBos.toByteArray();
            ByteArrayInputStream aByteStream = new ByteArrayInputStream(aBitmapData);

            PDImageXObject xAlpha = new PDImageXObject(document, aByteStream, 
                    COSName.DCT_DECODE, alphaImage.getWidth(), alphaImage.getHeight(), 
                    8, 
                    PDDeviceGray.INSTANCE 
            );

            pdImage.getCOSStream().setItem(COSName.SMASK, xAlpha);
        }
	    return pdImage;
	}

//	private static void encodeImageToJPEGStream(BufferedImage image, float quality, int dpi,
//			OutputStream out) throws IOException
//	{
//		// encode to JPEG
//		ImageOutputStream ios = null;
//		ImageWriter imageWriter = null;
//		try
//		{
//			// find JAI writer
//			imageWriter = ImageIO.getImageWritersBySuffix("jpeg").next();
//			ios = ImageIO.createImageOutputStream(out);
//			imageWriter.setOutput(ios);
//			// add compression
//			JPEGImageWriteParam jpegParam = (JPEGImageWriteParam)imageWriter.getDefaultWriteParam();
//			jpegParam.setCompressionMode(JPEGImageWriteParam.MODE_EXPLICIT);
//			jpegParam.setCompressionQuality(quality);
//			// add metadata
//			ImageTypeSpecifier imageTypeSpecifier = new ImageTypeSpecifier(image);
//			IIOMetadata data = imageWriter.getDefaultImageMetadata(imageTypeSpecifier, jpegParam);
//			Element tree = (Element)data.getAsTree("javax_imageio_jpeg_image_1.0");
//			Element jfif = (Element)tree.getElementsByTagName("app0JFIF").item(0);
//			jfif.setAttribute("Xdensity", Integer.toString(dpi));
//			jfif.setAttribute("Ydensity", Integer.toString(dpi));
//			jfif.setAttribute("resUnits", "1"); // 1 = dots/inch
//			// write
//			imageWriter.write(data, new IIOImage(image, null, null), jpegParam);
//		}
//		finally
//		{
//			// clean up
//			IOUtils.closeQuietly(out);
//			if (ios != null)
//			{
//				ios.close();
//			}
//			if (imageWriter != null)
//			{
//				imageWriter.dispose();
//			}
//		}
//	} TODO: PdfBox-Android

	// returns a PDColorSpace for a given BufferedImage
//	private static PDColorSpace getColorSpaceFromAWT(Bitmap awtImage)
//	{
//		if (awtImage.getColorModel().getNumComponents() == 1)
//		{
//			// 256 color (gray) JPEG
//			return PDDeviceGray.INSTANCE;
//		}
//
//		ColorSpace awtColorSpace = awtImage.getColorModel().getColorSpace();
//		if (awtColorSpace instanceof ICC_ColorSpace && !awtColorSpace.isCS_sRGB())
//		{
//			throw new UnsupportedOperationException("ICC color spaces not implemented");
//		}
//
//		switch (awtColorSpace.getType())
//		{
//			case ColorSpace.TYPE_RGB:
//				return PDDeviceRGB.INSTANCE;
//			case ColorSpace.TYPE_GRAY:
//				return PDDeviceGray.INSTANCE;
//			case ColorSpace.TYPE_CMYK:
//				return PDDeviceCMYK.INSTANCE;
//			default:
//				throw new UnsupportedOperationException("color space not implemented: "
//						+ awtColorSpace.getType());
//		}
//	} TODO: PdfBox-Android

	// returns the color channels of an image
	private static Bitmap getColorImage(Bitmap image)
	{
		if (!image.hasAlpha())
		{
			return image;
		}

		if (!image.getConfig().name().contains("RGB"))
		{
			throw new UnsupportedOperationException("only RGB color spaces are implemented");
		}

		// create an RGB image without alpha
		//BEWARE: the previous solution in the history
		// g.setComposite(AlphaComposite.Src) and g.drawImage()
		// didn't work properly for TYPE_4BYTE_ABGR.
		// alpha values of 0 result in a black dest pixel!!!
		Bitmap rgbImage = Bitmap.createBitmap(image.getWidth(), image.getHeight(), Bitmap.Config.ARGB_8888);
//		return new ColorConvertOp(null).filter(image, rgbImage); TODO: PdfBox-Android
		Paint alphaPaint = new Paint();
		alphaPaint.setAlpha(0);
		Canvas canvas = new Canvas(rgbImage);
		canvas.drawBitmap(image, 0, 0, alphaPaint);
		return rgbImage;
	}
}
