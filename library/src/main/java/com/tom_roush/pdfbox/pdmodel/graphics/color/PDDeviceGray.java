package org.apache.pdfbox.pdmodel.graphics.color;

import org.apache.pdfbox.cos.COSName;

/**
 * A color space with black, white, and intermediate shades of gray.
 *
 * @author Ben Litchfield
 * @author John Hewson
 */
public final class PDDeviceGray extends PDDeviceColorSpace
{
    /** The single instance of this class. */
    public static final PDDeviceGray INSTANCE = new PDDeviceGray();
    
    private final PDColor initialColor = new PDColor(new float[] { 0 }, this);

    private PDDeviceGray()
    {
    }

    @Override
    public String getName()
    {
        return COSName.DEVICEGRAY.getName();
    }

    @Override
    public int getNumberOfComponents()
    {
        return 1;
    }

    @Override
    public float[] getDefaultDecode(int bitsPerComponent)
    {
        return new float[] { 0, 1 };
    }

    @Override
    public PDColor getInitialColor()
    {
        return initialColor;
    }

//    @Override TODO
    public float[] toRGB(float[] value)
    {
        return new float[] { value[0], value[0], value[0] };
    }

//    @Override
//    public Bitmap toRGBImage(WritableRaster raster) throws IOException
//    {
//        int width = raster.getWidth();
//        int height = raster.getHeight();
//
//        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
//
//        int[] gray = new int[1];
//        int[] rgb = new int[3];
//        for (int y = 0; y < height; y++)
//        {
//            for (int x = 0; x < width; x++)
//            {
//                raster.getPixel(x, y, gray);
//                rgb[0] = gray[0];
//                rgb[1] = gray[0];
//                rgb[2] = gray[0];
//                image.getRaster().setPixel(x, y, rgb);
//            }
//        }
//
//        return image;
//    } TODO
}
