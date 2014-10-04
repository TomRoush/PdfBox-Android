package org.apache.pdfbox.pdmodel.graphics.image;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.filter.MissingImageReaderException;
import org.apache.pdfbox.io.IOUtils;
import org.apache.pdfbox.pdmodel.PDDocument;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ImageReader;

/**
 * Factory for creating a PDImageXObject containing a JPEG compressed image.
 * @author John Hewson
 */
public final class JPEGFactory extends ImageFactory
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
        Bitmap awtImage = readJPEG(byteStream);
        byteStream.reset();

        // create Image XObject from stream
        PDImageXObject pdImage = new PDImageXObject(document, byteStream, 
                COSName.DCT_DECODE, awtImage.getWidth(), awtImage.getHeight(), 
                8); //awtImage.getColorModel().getComponentSize(0),
//                getColorSpaceFromAWT(awtImage));
//                null);

        // no alpha
//        if (awtImage.getColorModel().hasAlpha())
//        {
//            throw new UnsupportedOperationException("alpha channel not implemented");
//        } TODO

        return pdImage;
//        return null;
    }

    private static Bitmap readJPEG(InputStream stream) throws IOException
    {
    	return BitmapFactory.decodeStream(stream);
    	
//        // find suitable image reader
//        Iterator readers = ImageIO.getImageReadersByFormatName("JPEG");
//        ImageReader reader = null;
//        while (readers.hasNext())
//        {
//            reader = (ImageReader) readers.next();
//            if (reader.canReadRaster())
//            {
//                break;
//            }
//        }
//
//        if (reader == null)
//        {
//            throw new MissingImageReaderException("Cannot read JPEG image: " +
//                    "a suitable JAI I/O image filter is not installed");
//        }
//
//        ImageInputStream iis = null;
//        try
//        {
//            iis = ImageIO.createImageInputStream(stream);
//            reader.setInput(iis);
//
//            ImageIO.setUseCache(false);
//            return reader.read(0);
//        }
//        finally
//        {
//            if (iis != null)
//            {
//                iis.close();
//            }
//            reader.dispose();
//        }
    }

    /**
     * Creates a new JPEG Image XObject from a Buffered Image.
     * @param document the document where the image will be created
     * @param image the buffered image to embed
     * @return a new Image XObject
     * @throws IOException if the JPEG data cannot be written
     */
//    public static PDImageXObject createFromImage(PDDocument document, BufferedImage image)
//        throws IOException
//    {
//        return createFromImage(document, image, 0.75f);
//    }TODO

    /**
     * Creates a new JPEG Image XObject from a Buffered Image and a given quality.
     * The image will be created at 72 DPI.
     * @param document the document where the image will be created
     * @param image the buffered image to embed
     * @param quality the desired JPEG compression quality
     * @return a new Image XObject
     * @throws IOException if the JPEG data cannot be written
     */
//    public static PDImageXObject createFromImage(PDDocument document, BufferedImage image,
//                                                 float quality) throws IOException
//    {
//        return createFromImage(document, image, quality, 72);
//    }TODO

    /**
     * Creates a new JPEG Image XObject from a Buffered Image, a given quality and DPI.
     * @param document the document where the image will be created
     * @param image the buffered image to embed
     * @param quality the desired JPEG compression quality
     * @param dpi the desired DPI (resolution) of the JPEG
     * @return a new Image XObject
     * @throws IOException if the JPEG data cannot be written
     */
//    public static PDImageXObject createFromImage(PDDocument document, BufferedImage image,
//                                                 float quality, int dpi) throws IOException
//    {
//        return createJPEG(document, image, quality, dpi);
//    }TODO
    
    // returns the alpha channel of an image
//    private static BufferedImage getAlphaImage(BufferedImage image) throws IOException
//    {
//        if (!image.getColorModel().hasAlpha())
//        {
//            return null;
//        }
//        if (image.getTransparency() == Transparency.BITMASK)
//        {
//            throw new UnsupportedOperationException("BITMASK Transparency JPEG compression is not useful, use LosslessImageFactory instead");
//        }
//        WritableRaster alphaRaster = image.getAlphaRaster();
//        BufferedImage alphaImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
//        alphaImage.setData(alphaRaster);
//        return alphaImage;
//    }TODO
    
    // Creates an Image XObject from a Buffered Image using JAI Image I/O
//    private static PDImageXObject createJPEG(PDDocument document, BufferedImage image,
//                                             float quality, int dpi) throws IOException
//    {
//        // extract alpha channel (if any)
//        BufferedImage awtColorImage = getColorImage(image);
//        BufferedImage awtAlphaImage = getAlphaImage(image);
//
//        // create XObject
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        ImageIOUtil.writeImage(awtColorImage, "jpeg", baos, dpi, quality);
//        ByteArrayInputStream byteStream = new ByteArrayInputStream(baos.toByteArray());
//        
//        
//        PDImageXObject pdImage = new PDImageXObject(document, byteStream, 
//                COSName.DCT_DECODE, awtColorImage.getWidth(), awtColorImage.getHeight(), 
//                awtColorImage.getColorModel().getComponentSize(0),
//                getColorSpaceFromAWT(awtColorImage));
//
//        // alpha -> soft mask
//        if (awtAlphaImage != null)
//        {
//            PDImage xAlpha = JPEGFactory.createFromImage(document, awtAlphaImage, quality);
//            pdImage.getCOSStream().setItem(COSName.SMASK, xAlpha);
//        }
//
//        return pdImage;
//    }TODO
}
