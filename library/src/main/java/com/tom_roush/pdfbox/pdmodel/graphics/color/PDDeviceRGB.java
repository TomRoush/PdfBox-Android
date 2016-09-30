package org.apache.pdfbox.pdmodel.graphics.color;

import org.apache.pdfbox.cos.COSName;

/**
 * Colours in the DeviceRGB colour space are specified according to the additive
 * RGB (red-green-blue) colour model.
 *
 * @author Ben Litchfield
 * @author John Hewson
 */
public final class PDDeviceRGB extends PDDeviceColorSpace {
    /**
     * This is the single instance of this class.
     */
    public static final PDDeviceRGB INSTANCE = new PDDeviceRGB();

    //    private final ColorSpace colorSpaceRGB = ColorSpace.getInstance(ColorSpace.CS_sRGB)
    private final PDColor initialColor = new PDColor(new float[]{0, 0, 0}, this);

    private PDDeviceRGB() {
        // there is a JVM bug which results in a CMMException which appears to be a race
        // condition caused by lazy initialization of the color transform, so we perform
        // an initial color conversion while we're still in a static context, see PDFBOX-2184
//        colorSpaceRGB.toRGB(new float[]{0, 0, 0});TODO
    }

    @Override
    public String getName() {
        return COSName.DEVICERGB.getName();
    }

    /**
     * @inheritDoc
     */
    public int getNumberOfComponents() {
        return 3;
    }

    @Override
    public float[] getDefaultDecode(int bitsPerComponent) {
        return new float[]{0, 1, 0, 1, 0, 1};
    }

    @Override
    public PDColor getInitialColor() {
        return initialColor;
    }

    @Override
    public float[] toRGB(float[] value) {
        // This is just assuming that the values being sent to it are already in RGB color space.
        if (value.length == 3) {
            return value;
        } else {
            return initialColor.getComponents();
        }
    }

//    @Override
//    public BufferedImage toRGBImage(WritableRaster raster) throws IOException
//    {
//        ColorModel colorModel = new ComponentColorModel(colorSpaceRGB,
//                false, false, Transparency.OPAQUE, raster.getDataBuffer().getDataType());
//
//        return new BufferedImage(colorModel, raster, false, null);
//    }TODO
}
