package com.tom_roush.pdfbox.pdmodel.graphics.color;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.ColorSpace;
import android.util.Log;

import com.tom_roush.pdfbox.cos.COSArray;
import com.tom_roush.pdfbox.cos.COSBase;
import com.tom_roush.pdfbox.cos.COSFloat;
import com.tom_roush.pdfbox.cos.COSName;
import com.tom_roush.pdfbox.cos.COSObject;
import com.tom_roush.pdfbox.cos.COSStream;
import com.tom_roush.pdfbox.pdmodel.PDDocument;
import com.tom_roush.pdfbox.pdmodel.PDResources;
import com.tom_roush.pdfbox.pdmodel.common.PDRange;
import com.tom_roush.pdfbox.pdmodel.common.PDStream;
import com.xsooy.icc.IccUtils;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * ICCBased color spaces are based on a cross-platform color profile as defined by the
 * International Color Consortium (ICC).
 *
 * @author Ben Litchfield
 * @author John Hewson
 */
public final class PDICCBased extends PDCIEBasedColorSpace
{

    private final PDStream stream;
    private int numberOfComponents = -1;
//    private ICC_Profile iccProfile;
    private PDColorSpace alternateColorSpace;
//    private ICC_ColorSpace awtColorSpace;
    private PDColor initialColor;
    private boolean isRGB = false;
    // allows to force using alternate color space instead of ICC color space for performance
    // reasons with LittleCMS (LCMS), see PDFBOX-4309
    // WARNING: do not activate this in a conforming reader
    private boolean useOnlyAlternateColorSpace = false;
//    private static final boolean IS_KCMS;
    private IccUtils iccUtils;
    private int colorType = TYPE_RGB;

//    static
//    {
//        String cmmProperty = System.getProperty("sun.java2d.cmm");
//        boolean result = false;
//        if ("sun.java2d.cmm.kcms.KcmsServiceProvider".equals(cmmProperty))
//        {
//            try
//            {
//                Class.forName("sun.java2d.cmm.kcms.KcmsServiceProvider");
//                result = true;
//            }
//            catch (ClassNotFoundException e)
//            {
//                // KCMS not available
//            }
//        }
//        // else maybe KCMS was available, but not wished
//        IS_KCMS = result;
//    }

    /**
     * Creates a new ICC color space with an empty stream.
     * @param doc the document to store the ICC data
     */
    public PDICCBased(PDDocument doc)
    {
        array = new COSArray();
        array.add(COSName.ICCBASED);
        stream = new PDStream(doc);
        array.add(stream);
    }

    /**
     * Creates a new ICC color space using the PDF array.
     *
     * @param iccArray the ICC stream object.
     * @throws IOException if there is an error reading the ICC profile or if the parameter is
     * invalid.
     */
    private PDICCBased(COSArray iccArray) throws IOException
    {
        useOnlyAlternateColorSpace = System
                .getProperty("org.apache.pdfbox.rendering.UseAlternateInsteadOfICCColorSpace") != null;
        array = iccArray;
        stream = new PDStream((COSStream) iccArray.getObject(1));
        loadICCProfile();
    }

    /**
     * Creates a new ICC color space using the PDF array, optionally using a resource cache.
     *
     * @param iccArray the ICC stream object.
     * @param resources resources to use as cache, or null for no caching.
     * @return an ICC color space.
     * @throws IOException if there is an error reading the ICC profile or if the parameter is
     * invalid.
     */
    public static PDICCBased create(COSArray iccArray, PDResources resources) throws IOException
    {
        checkArray(iccArray);
        COSBase base = iccArray.get(1);
        COSObject indirect = null;
        if (base instanceof COSObject)
        {
            indirect = (COSObject) base;
        }
        if (indirect != null && resources != null && resources.getResourceCache() != null)
        {
            PDColorSpace space = resources.getResourceCache().getColorSpace(indirect);
            if (space instanceof PDICCBased)
            {
                return (PDICCBased) space;
            }
        }
        PDICCBased space = new PDICCBased(iccArray);
        if (indirect != null && resources != null && resources.getResourceCache() != null)
        {
            resources.getResourceCache().put(indirect, space);
        }
        return space;
    }

    private static void checkArray(COSArray iccArray) throws IOException
    {
        if (iccArray.size() < 2)
        {
            throw new IOException("ICCBased colorspace array must have two elements");
        }
        if (!(iccArray.getObject(1) instanceof COSStream))
        {
            throw new IOException("ICCBased colorspace array must have a stream as second element");
        }
    }

    @Override
    public String getName()
    {
        return COSName.ICCBASED.getName();
    }

    /**
     * Get the underlying ICC profile stream.
     * @return the underlying ICC profile stream
     */
    public PDStream getPDStream()
    {
        return stream;
    }

    private static int intFromBigEndian(byte[] array, int index) {
        return (((array[index]   & 0xff) << 24) |
                ((array[index+1] & 0xff) << 16) |
                ((array[index+2] & 0xff) <<  8) |
                (array[index+3] & 0xff));
    }

    private byte[] getProfileDataFromStream(InputStream s) throws IOException {
        BufferedInputStream bis = new BufferedInputStream(s);
        bis.mark(128); // 128 is the length of the ICC profile header

        int result = 0;
        byte[] header = new byte[128];
        result = bis.read(header);
//        byte[] header = bis.readNBytes(128);
        if (result<128 || header[36] != 0x61 || header[37] != 0x63 ||
                header[38] != 0x73 || header[39] != 0x70) {
            return null;   /* not a valid profile */
        }
        int profileSize = intFromBigEndian(header, 0);
        bis.reset();
        byte[] profile = new byte[profileSize];
        try {
            if (bis.read(profile) == profileSize)
                return profile;
            else
                throw new IOException("profile load error");
        } catch (OutOfMemoryError e) {
            throw new IOException("Color profile is too big");
        }
    }

    /**
     * Load the ICC profile, or init alternateColorSpace color space.
     */
    private void loadICCProfile() throws IOException
    {
        if (useOnlyAlternateColorSpace)
        {
            try
            {
                fallbackToAlternateColorSpace(null);
                return;
            }
            catch (IOException e)
            {
                Log.w("PdfBox-Android","Error initializing alternate color space: " + e.getLocalizedMessage());
            }
        }
        try
        {
            InputStream input = this.stream.createInputStream();
            // if the embedded profile is sRGB then we can use Java's built-in profile, which
            // results in a large performance gain as it's our native color space, see PDFBOX-2587
//            ICC_Profile profile;
            iccUtils = new IccUtils();

            colorType = IccUtils.getIccColorType(iccUtils.loadProfileByData(getProfileDataFromStream(input)));
            switch (colorType) {
                case TYPE_GRAY:
                    numberOfComponents = 1;
                    break;
                case TYPE_RGB:
                    numberOfComponents = 3;
                    isRGB = true;
                    break;
                case TYPE_CMYK:
                    numberOfComponents = 4;
                    break;
            }

            // set initial colour
            float[] initial = new float[getNumberOfComponents()];
            for (int c = 0; c < initial.length; c++)
            {
                initial[c] = Math.max(0, getRangeForComponent(c).getMin());
            }
            initialColor = new PDColor(initial, this);

        }
        catch (IllegalArgumentException |
                ArrayIndexOutOfBoundsException | IOException e)
        {
            fallbackToAlternateColorSpace(e);
        }
    }

    private void fallbackToAlternateColorSpace(Exception e) throws IOException
    {
        iccUtils = null;
//        awtColorSpace = null;
        alternateColorSpace = getAlternateColorSpace();
        if (alternateColorSpace.equals(PDDeviceRGB.INSTANCE))
        {
            isRGB = true;
        }
        if (e != null)
        {
            Log.w("PdfBox-Android","Can't read embedded ICC profile (" + e.getLocalizedMessage() +
                    "), using alternate color space: " + alternateColorSpace.getName());
        }
        initialColor = alternateColorSpace.getInitialColor();
    }

    /**
     * Returns true if the given profile represents sRGB.
     * (unreliable on the data of ColorSpace.CS_sRGB in openjdk)
     */
//    private boolean is_sRGB(ICC_Profile profile)
//    {
//        byte[] bytes = Arrays.copyOfRange(profile.getData(ICC_Profile.icSigHead),
//                ICC_Profile.icHdrModel, ICC_Profile.icHdrModel + 7);
//        String deviceModel = new String(bytes, StandardCharsets.US_ASCII).trim();
//        return deviceModel.equals("sRGB");
//    }

    // PDFBOX-4114: fix profile that has the wrong display class,
    // as done by Harald Kuhr in twelvemonkeys JPEGImageReader.ensureDisplayProfile()
//    private static ICC_Profile ensureDisplayProfile(ICC_Profile profile)
//    {
//        if (profile.getProfileClass() != ICC_Profile.CLASS_DISPLAY)
//        {
//            byte[] profileData = profile.getData(); // Need to clone entire profile, due to a OpenJDK bug
//
//            if (profileData[ICC_Profile.icHdrRenderingIntent] == ICC_Profile.icPerceptual)
//            {
//                LOG.warn("ICC profile is Perceptual, ignoring, treating as Display class");
//                intToBigEndian(ICC_Profile.icSigDisplayClass, profileData, ICC_Profile.icHdrDeviceClass);
//                return ICC_Profile.getInstance(profileData);
//            }
//        }
//        return profile;
//    }

    private static void intToBigEndian(int value, byte[] array, int index)
    {
        array[index] = (byte) (value >> 24);
        array[index + 1] = (byte) (value >> 16);
        array[index + 2] = (byte) (value >> 8);
        array[index + 3] = (byte) (value);
    }

    @Override
    public float[] toRGB(float[] value) throws IOException
    {
        if (isRGB)
        {
            return value;
        }
        if (iccUtils!=null) {
            float[] xyz = new float[3];
            iccUtils.applyGray(value,xyz);
            return xyzToRgb(xyz);
        }
//        if (awtColorSpace != null)
//        {
            // PDFBOX-2142: clamp bad values
            // WARNING: toRGB is very slow when used with LUT-based ICC profiles
//            return awtColorSpace.toRGB(clampColors(awtColorSpace, value));
//        }
//        else
//        {
            return alternateColorSpace.toRGB(value);
//        }
    }

    @Override
    public Bitmap toRGBImage(Bitmap raster) throws IOException {
        int width = raster.getWidth();
        int height = raster.getHeight();
        Bitmap rgbImage = Bitmap.createBitmap(width,height, Bitmap.Config.ARGB_8888);
        switch (colorType) {
            case TYPE_GRAY:
                int[] src = new int[width];
                for (int y = 0; y < height; y++)
                {
                    raster.getPixels(src,0,width,0,y,width,1);
                    for (int x = 0; x < width; x++)
                    {
                        src[x] = Color.argb(255,src[x]>>24&0xff,src[x]>>24&0xff,src[x]>>24&0xff);
                    }
                    rgbImage.setPixels(src,0,width,0,y,width,1);
                }
                return rgbImage;
            default:
                //TODO:PdfBox-Android
                return raster;
        }
    }

//    @Override
//    public Bitmap toRGBImage(int[] raster) throws IOException {
//        return null;
//    }

//    private float[] clampColors(ICC_ColorSpace cs, float[] value)
//    {
//        float[] result = new float[value.length];
//        for (int i = 0; i < value.length; ++i)
//        {
//            float minValue = cs.getMinValue(i);
//            float maxValue = cs.getMaxValue(i);
//            result[i] = value[i] < minValue ? minValue : (value[i] > maxValue ? maxValue : value[i]);
//        }
//        return result;
//    }

//    @Override
//    public BufferedImage toRGBImage(WritableRaster raster) throws IOException
//    {
//        if (awtColorSpace != null)
//        {
//            return toRGBImageAWT(raster, awtColorSpace);
//        }
//        else
//        {
//            return alternateColorSpace.toRGBImage(raster);
//        }
//    }
//
//    @Override
//    public BufferedImage toRawImage(WritableRaster raster) throws IOException
//    {
//        if(awtColorSpace == null)
//        {
//            return alternateColorSpace.toRawImage(raster);
//        }
//        return toRawImage(raster, awtColorSpace);
//    }

    @Override
    public int getNumberOfComponents()
    {
        if (numberOfComponents < 0)
        {
            numberOfComponents = stream.getCOSObject().getInt(COSName.N);

            // PDFBOX-4801 correct wrong /N values
//            if (iccProfile != null)
//            {
//                int numIccComponents = iccProfile.getNumComponents();
//                if (numIccComponents != numberOfComponents)
//                {
//                    LOG.warn("Using " + numIccComponents + " components from ICC profile info instead of " +
//                            numberOfComponents + " components from /N entry");
//                    numberOfComponents = numIccComponents;
//                }
//            }
        }
        return numberOfComponents;
    }

    @Override
    public float[] getDefaultDecode(int bitsPerComponent)
    {
        if (iccUtils != null)
        {
            int n = getNumberOfComponents();
            float[] decode = new float[n * 2];
            for (int i = 0; i < n; i++)
            {
                decode[i * 2] = getMinValue(colorType,i);
                decode[i * 2 + 1] = getMaxValue(colorType,i);
            }
            return decode;
        }
        else
        {
            return alternateColorSpace.getDefaultDecode(bitsPerComponent);
        }
    }

    @Override
    public PDColor getInitialColor()
    {
        return initialColor;
    }

    /**
     * Returns a list of alternate color spaces for non-conforming readers.
     * WARNING: Do not use the information in a conforming reader.
     * @return A list of alternateColorSpace color spaces.
     * @throws IOException If there is an error getting the alternateColorSpace color spaces.
     */
    public PDColorSpace getAlternateColorSpace() throws IOException
    {
        COSBase alternate = stream.getCOSObject().getDictionaryObject(COSName.ALTERNATE);
        COSArray alternateArray;
        if(alternate == null)
        {
            alternateArray = new COSArray();
            int numComponents = getNumberOfComponents();
            COSName csName;
            switch (numComponents)
            {
                case 1:
                    csName = COSName.DEVICEGRAY;
                    break;
                case 3:
                    csName = COSName.DEVICERGB;
                    break;
                case 4:
                    csName = COSName.DEVICECMYK;
                    break;
                default:
                    throw new IOException("Unknown color space number of components:" + numComponents);
            }
            alternateArray.add(csName);
        }
        else
        {
            if(alternate instanceof COSArray)
            {
                alternateArray = (COSArray)alternate;
            }
            else if(alternate instanceof COSName)
            {
                alternateArray = new COSArray();
                alternateArray.add(alternate);
            }
            else
            {
                throw new IOException("Error: expected COSArray or COSName and not " +
                        alternate.getClass().getName());
            }
        }
        return PDColorSpace.create(alternateArray);
    }

    /**
     * Returns the range for a certain component number.
     * This will never return null.
     * If it is not present then the range 0..1 will be returned.
     * @param n the component number to get the range for
     * @return the range for this component
     */
    public PDRange getRangeForComponent(int n)
    {
        COSArray rangeArray = stream.getCOSObject().getCOSArray(COSName.RANGE);
        if (rangeArray == null || rangeArray.size() < getNumberOfComponents() * 2)
        {
            return new PDRange(); // 0..1
        }
        return new PDRange(rangeArray, n);
    }

    /**
     * Returns the metadata stream for this object, or null if there is no metadata stream.
     * @return the metadata stream, or null if there is none
     */
    public COSStream getMetadata()
    {
        return stream.getCOSObject().getCOSStream(COSName.METADATA);
    }

    /**
     * Returns the type of the color space in the ICC profile. If the ICC profile is invalid, the
     * type of the alternate colorspace is returned, which will be one of
     * {@link #TYPE_GRAY TYPE_GRAY}, {@link #TYPE_RGB TYPE_RGB},
     * {@link #TYPE_CMYK TYPE_CMYK}, or -1 if that one is invalid.
     *
     * @return an ICC color space type. and the static values of
     * {@link ColorSpace} for more details.
     */
    public int getColorSpaceType()
    {
//        if (iccProfile != null)
//        {
//            return iccProfile.getColorSpaceType();
//        }

        // if the ICC Profile could not be read
        switch (alternateColorSpace.getNumberOfComponents())
        {
            case 1:
                return TYPE_GRAY;
            case 3:
                return TYPE_RGB;
            case 4:
                return TYPE_CMYK;
            default:
                // should not happen as all ICC color spaces in PDF must have 1,3, or 4 components
                return -1;
        }
    }

    /**
     * Sets the list of alternateColorSpace color spaces.
     *
     * @param list the list of color space objects
     */
    public void setAlternateColorSpaces(List<PDColorSpace> list)
    {
        COSArray altArray = null;
        if(list != null)
        {
            altArray = new COSArray(list);
        }
        stream.getCOSObject().setItem(COSName.ALTERNATE, altArray);
    }

    /**
     * Sets the range for this color space.
     * @param range the new range for the a component
     * @param n the component to set the range for
     */
    public void setRangeForComponent(PDRange range, int n)
    {
        COSArray rangeArray = stream.getCOSObject().getCOSArray(COSName.RANGE);
        if (rangeArray == null)
        {
            rangeArray = new COSArray();
            stream.getCOSObject().setItem(COSName.RANGE, rangeArray);
        }
        // extend range array with default values if needed
        while (rangeArray.size() < (n + 1) * 2)
        {
            rangeArray.add(new COSFloat(0));
            rangeArray.add(new COSFloat(1));
        }
        rangeArray.set(n*2, new COSFloat(range.getMin()));
        rangeArray.set(n*2+1, new COSFloat(range.getMax()));
    }

    /**
     * Sets the metadata stream that is associated with this color space.
     * @param metadata the new metadata stream
     */
    public void setMetadata(COSStream metadata)
    {
        stream.getCOSObject().setItem(COSName.METADATA, metadata);
    }

    /**
     * Internal accessor to support indexed raw images.
     * @return true if this colorspace is sRGB.
     */
    boolean isSRGB()
    {
        return isRGB;
    }

    @Override
    public String toString()
    {
        return getName() + "{numberOfComponents: " + getNumberOfComponents() + "}";
    }
}

