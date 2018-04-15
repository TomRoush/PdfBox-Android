package com.tom_roush.pdfbox.pdmodel.graphics.image;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.tom_roush.pdfbox.cos.COSDictionary;
import com.tom_roush.pdfbox.cos.COSName;
import com.tom_roush.pdfbox.filter.Filter;
import com.tom_roush.pdfbox.filter.FilterFactory;
import com.tom_roush.pdfbox.io.IOUtils;
import com.tom_roush.pdfbox.pdmodel.PDDocument;
import com.tom_roush.pdfbox.pdmodel.graphics.color.PDColorSpace;
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
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, (int)(quality * 100), bos);
        byte[] bitmapData = bos.toByteArray();
        ByteArrayInputStream byteStream = new ByteArrayInputStream(bitmapData);

        PDImageXObject pdImage = new PDImageXObject(document, byteStream, 
                COSName.DCT_DECODE, image.getWidth(), image.getHeight(),
                8, 
                PDDeviceRGB.INSTANCE 
        );

        // alpha -> soft mask
        if (image.hasAlpha())
        {
            PDImageXObject xAlpha = createAlphaFromARGBImage(document, image);

            pdImage.getCOSStream().setItem(COSName.SMASK, xAlpha);
        }
	    return pdImage;
	}

	// createAlphaFromARGBImage and prepareImageXObject taken from LosslessFactory
    /**
     * Creates a grayscale Flate encoded PDImageXObject from the alpha channel
     * of an image.
     *
     * @param document the document where the image will be created.
     * @param image an ARGB image.
     *
     * @return the alpha channel of an image as a grayscale image.
     *
     * @throws IOException if something goes wrong
     */
    private static PDImageXObject createAlphaFromARGBImage(PDDocument document, Bitmap image)
        throws IOException
    {
        // this implementation makes the assumption that the raster uses
        // SinglePixelPackedSampleModel, i.e. the values can be used 1:1 for
        // the stream.
        // Sadly the type of the databuffer is TYPE_INT and not TYPE_BYTE.
        if (!image.hasAlpha())
        {
            return null;
        }

        int[] pixels = new int[image.getHeight() * image.getWidth()];
        image.getPixels(pixels, 0, image.getWidth(), 0, 0, image.getWidth(), image.getHeight());

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        int bpc;
//        if (image.getTransparency() == Transparency.BITMASK)
//        {
//            bpc = 1;
//            MemoryCacheImageOutputStream mcios = new MemoryCacheImageOutputStream(bos);
//            int width = alphaRaster.getSampleModel().getWidth();
//            int p = 0;
//            for (int pixel : pixels)
//            {
//                mcios.writeBit(pixel);
//                ++p;
//                if (p % width == 0)
//                {
//                    while (mcios.getBitOffset() != 0)
//                    {
//                        mcios.writeBit(0);
//                    }
//                }
//            }
//            mcios.flush();
//            mcios.close();
//        }
//        else
//        {
        bpc = 8;
        for (int pixel : pixels)
        {
            bos.write(Color.alpha(pixel));
        }
//        }

        PDImageXObject pdImage = prepareImageXObject(document, bos.toByteArray(),
            image.getWidth(), image.getHeight(), bpc, PDDeviceGray.INSTANCE);
        return pdImage;
    }

    /**
     * Create a PDImageXObject while making a decision whether not to
     * compress, use Flate filter only, or Flate and LZW filters.
     *
     * @param document The document.
     * @param byteArray array with data.
     * @param width the image width
     * @param height the image height
     * @param bitsPerComponent the bits per component
     * @param initColorSpace the color space
     * @return the newly created PDImageXObject with the data compressed.
     * @throws IOException
     */
    private static PDImageXObject prepareImageXObject(PDDocument document,
        byte [] byteArray, int width, int height, int bitsPerComponent,
        PDColorSpace initColorSpace) throws IOException
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        Filter filter = FilterFactory.INSTANCE.getFilter(COSName.FLATE_DECODE);
        filter.encode(new ByteArrayInputStream(byteArray), baos, new COSDictionary(), 0);

        ByteArrayInputStream encodedByteStream = new ByteArrayInputStream(baos.toByteArray());
        return new PDImageXObject(document, encodedByteStream, COSName.FLATE_DECODE, // TODO: PdfBox-Android should be DCT
            width, height, bitsPerComponent, initColorSpace);
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
