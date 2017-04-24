package com.tom_roush.pdfbox.pdmodel.graphics.color;

import android.util.Log;

import com.tom_roush.pdfbox.cos.COSArray;
import com.tom_roush.pdfbox.cos.COSBase;
import com.tom_roush.pdfbox.cos.COSName;
import com.tom_roush.pdfbox.cos.COSObject;
import com.tom_roush.pdfbox.pdmodel.MissingResourceException;
import com.tom_roush.pdfbox.pdmodel.PDResources;
import com.tom_roush.pdfbox.pdmodel.common.COSObjectable;

import java.io.IOException;

/**
 * A color space specifies how the colours of graphics objects will be painted on the page.
 *
 * @author John Hewson
 * @author Ben Litchfield
 */
public abstract class PDColorSpace implements COSObjectable {
    /**
     * Creates a color space space given a name or array.
     *
     * @param colorSpace the color space COS object
     * @return a new color space
     * @throws IOException if the color space is unknown or cannot be created
     */
    public static PDColorSpace create(COSBase colorSpace) throws IOException {
        return create(colorSpace, null);
    }

    /**
     * Creates a color space given a name or array.
     *
     * @param colorSpace the color space COS object
     * @param resources  the current resources.
     * @return a new color space
     * @throws MissingResourceException if the color space is missing in the resources dictionary
     * @throws IOException              if the color space is unknown or cannot be created
     */
    public static PDColorSpace create(COSBase colorSpace,
                                      PDResources resources)
            throws IOException {
        if (colorSpace instanceof COSObject) {
            return create(((COSObject) colorSpace).getObject(), resources);
        } else if (colorSpace instanceof COSName) {
            COSName name = (COSName) colorSpace;

            // default color spaces
            if (resources != null) {
                COSName defaultName = null;
                if (name.equals(COSName.DEVICECMYK) &&
                        resources.hasColorSpace(COSName.DEFAULT_CMYK)) {
                    defaultName = COSName.DEFAULT_CMYK;
                } else if (name.equals(COSName.DEVICERGB) &&
                        resources.hasColorSpace(COSName.DEFAULT_RGB)) {
                    defaultName = COSName.DEFAULT_RGB;
                } else if (name.equals(COSName.DEVICEGRAY) &&
                        resources.hasColorSpace(COSName.DEFAULT_GRAY)) {
                    defaultName = COSName.DEFAULT_GRAY;
                }

                if (resources.hasColorSpace(defaultName)) {
                    return resources.getColorSpace(defaultName);
                }
            }

            // built-in color spaces
            /*if (name == COSName.DEVICECMYK || name == COSName.CMYK) {
                return PDDeviceCMYK.INSTANCE;
            } else*/ if (name == COSName.DEVICERGB || name == COSName.RGB) {
                return PDDeviceRGB.INSTANCE;
            } else if (name == COSName.DEVICEGRAY || name == COSName.G) {
                return PDDeviceGray.INSTANCE;
            } /*else if (name == COSName.PATTERN) {
                return new PDPattern(resources);
            } */else if (resources != null) {
                if (!resources.hasColorSpace(name)) {
                    throw new MissingResourceException("Missing color space: " + name.getName());
                }
                return resources.getColorSpace(name);
            } else {
                throw new MissingResourceException("Unknown color space: " + name.getName());
            }
        } else if (colorSpace instanceof COSArray) {
            COSArray array = (COSArray) colorSpace;
            COSName name = (COSName) array.get(0);
//
//            // TODO cache these returned color spaces?
//
//            if (name == COSName.CALGRAY) {
//                return new PDCalGray(array);
//            } else if (name == COSName.CALRGB) {
//                return new PDCalRGB(array);
//            } else if (name == COSName.DEVICEN) {
//                return new PDDeviceN(array);
//            } else if (name == COSName.INDEXED || name == COSName.I) {
//                return new PDIndexed(array);
//            } else if (name == COSName.SEPARATION) {
//                return new PDSeparation(array);
//            } else if (name == COSName.ICCBASED) {
//                return new PDICCBased(array);
//            } else if (name == COSName.LAB) {
//                return new PDLab(array);
//            } else if (name == COSName.PATTERN) {
//                if (array.size() == 1) {
//                    return new PDPattern(resources);
//                } else {
//                    return new PDPattern(resources, PDColorSpace.create(array.get(1)));
//                }
//            } else if (name == COSName.DEVICECMYK || name == COSName.CMYK ||
//                    name == COSName.DEVICERGB || name == COSName.RGB ||
//                    name == COSName.DEVICEGRAY || name == COSName.PATTERN) {
//                // not allowed in an array, but we sometimes encounter these regardless
//                return create(name, resources);
//            } else {
//                throw new IOException("Invalid color space kind: " + name);
//            }

//            throw new IOException("Invalid color space kind: " + name);
            Log.e("PdfBox-Android", "Invalid color space kind: " + name + ". Will try DeviceRGB instead");
            return PDDeviceRGB.INSTANCE;
        } else {
            throw new IOException("Expected a name or array but got: " + colorSpace);
        }
    }

    // array for the given parameters
    protected COSArray array;

    /**
     * Returns the name of the color space.
     *
     * @return the name of the color space
     */
    public abstract String getName();

    /**
     * Returns the number of components in this color space
     *
     * @return the number of components in this color space
     */
    public abstract int getNumberOfComponents();

    /**
     * Returns the default decode array for this color space.
     *
     * @return the default decode array
     */
    public abstract float[] getDefaultDecode(int bitsPerComponent);

    /**
     * Returns the initial color value for this color space.
     *
     * @return the initial color value for this color space
     */
    public abstract PDColor getInitialColor();

    /**
     * Returns the RGB equivalent of the given color value.
     *
     * @param value a color value with component values between 0 and 1
     * @return an array of R,G,B value between 0 and 255
     * @throws IOException if the color conversion fails
     */
    public abstract float[] toRGB(float[] value) throws IOException;

//    /**
//     * Returns the (A)RGB equivalent of the given raster.
//     * @param raster the source raster
//     * @return an (A)RGB Bitmap
//     * @throws IOException if the color conversion fails
//     */
//    public abstract Bitmap toRGBImage(WritableRaster raster) throws IOException; TODO: PdfBox-Android

//    /**
//     * Returns the (A)RGB equivalent of the given raster, using the given AWT color space
//     * to perform the conversion.
//     *
//     * @param raster     the source raster
//     * @param colorSpace the AWT
//     * @return an (A)RGB buffered image
//     */
//    protected Bitmap toRGBImageAWT(WritableRaster raster, ColorSpace colorSpace)
//    {
//        //
//        // WARNING: this method is performance sensitive, modify with care!
//        //
//
//        // ICC Profile color transforms are only fast when performed using ColorConvertOp
//        ColorModel colorModel = new ComponentColorModel(colorSpace,
//            false, false, Transparency.OPAQUE, raster.getDataBuffer().getDataType());
//
//        BufferedImage src = new BufferedImage(colorModel, raster, false, null);
//        BufferedImage dest = new BufferedImage(raster.getWidth(), raster.getHeight(),
//                                               BufferedImage.TYPE_INT_RGB);
//        ColorConvertOp op = new ColorConvertOp(null);
//        op.filter(src, dest);
//        return dest;
//    } TODO: PdfBox-Android

    @Override
    public COSBase getCOSObject() {
        return array;
    }
}
