package org.apache.pdfbox.pdmodel.graphics.image;


/**
 * An image factory.
 *
 * @author John Hewson
 * @author Brigitte Mathiak
 */
class ImageFactory
{
    protected ImageFactory()
    {
    }

    // returns a PDColorSpace for a given BufferedImage
//    protected static PDColorSpace getColorSpaceFromAWT(BufferedImage awtImage)
//    {
//        if (awtImage.getColorModel().getNumComponents() == 1)
//        {
//            // 256 color (gray) JPEG
//            return PDDeviceGray.INSTANCE;
//        }
//        else
//        {
//            return toPDColorSpace(awtImage.getColorModel().getColorSpace());
//        }
//    }TODO

    // returns a PDColorSpace for a given AWT ColorSpace
//    protected static PDColorSpace toPDColorSpace(ColorSpace awtColorSpace)
//    {
//        if (awtColorSpace instanceof ICC_ColorSpace && !awtColorSpace.isCS_sRGB())
//        {
//            throw new UnsupportedOperationException("ICC color spaces not implemented");
//        }
//        else
//        {
//            switch (awtColorSpace.getType())
//            {
//                case ColorSpace.TYPE_RGB:  return PDDeviceRGB.INSTANCE;
//                case ColorSpace.TYPE_GRAY: return PDDeviceGray.INSTANCE;
//                case ColorSpace.TYPE_CMYK: return PDDeviceCMYK.INSTANCE;
//                default: throw new UnsupportedOperationException("color space not implemented: " +
//                        awtColorSpace.getType());
//            }
//        }
//    }TODO

    // returns the color channels of an image
//    protected static BufferedImage getColorImage(BufferedImage image)
//    {
//        if (!image.getColorModel().hasAlpha())
//        {
//            return image;
//        }
//
//        if (image.getColorModel().getColorSpace().getType() != ColorSpace.TYPE_RGB)
//        {
//            throw new UnsupportedOperationException("only RGB color spaces are implemented");
//        }
//
//        // create an RGB image without alpha
//        //BEWARE: the previous solution in the history 
//        // g.setComposite(AlphaComposite.Src) and g.drawImage()
//        // didn't work properly for TYPE_4BYTE_ABGR.
//        // alpha values of 0 result in a black dest pixel!!!
//        BufferedImage rgbImage = new BufferedImage(
//                image.getWidth(),
//                image.getHeight(),
//                BufferedImage.TYPE_3BYTE_BGR);
//        return new ColorConvertOp(null).filter(image, rgbImage);
//    }TODO
}
