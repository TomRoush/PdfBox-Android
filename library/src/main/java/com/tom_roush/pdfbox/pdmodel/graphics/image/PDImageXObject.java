package com.tom_roush.pdfbox.pdmodel.graphics.image;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Log;

import com.tom_roush.pdfbox.cos.COSArray;
import com.tom_roush.pdfbox.cos.COSBase;
import com.tom_roush.pdfbox.cos.COSInputStream;
import com.tom_roush.pdfbox.cos.COSName;
import com.tom_roush.pdfbox.cos.COSStream;
import com.tom_roush.pdfbox.io.IOUtils;
import com.tom_roush.pdfbox.pdmodel.PDDocument;
import com.tom_roush.pdfbox.pdmodel.PDResources;
import com.tom_roush.pdfbox.pdmodel.common.PDMetadata;
import com.tom_roush.pdfbox.pdmodel.common.PDStream;
import com.tom_roush.pdfbox.pdmodel.graphics.PDXObject;
import com.tom_roush.pdfbox.pdmodel.graphics.color.PDColorSpace;
import com.tom_roush.pdfbox.pdmodel.graphics.color.PDDeviceGray;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.SoftReference;
import java.util.List;

/**
 * An Image XObject.
 *
 * @author John Hewson
 * @author Ben Litchfield
 */
public final class PDImageXObject extends PDXObject implements PDImage
{
    private SoftReference<Bitmap> cachedImage;
    private PDColorSpace colorSpace;
    private final PDResources resources; // current resource dictionary (has color spaces)

    /**
     * Creates a thumbnail Image XObject from the given COSBase and name.
     * @param cosStream the COS stream
     * @return an XObject
     * @throws IOException if there is an error creating the XObject.
     */
    public static PDImageXObject createThumbnail(COSStream cosStream) throws IOException
    {
        // thumbnails are special, any non-null subtype is treated as being "Image"
        PDStream pdStream = new PDStream(cosStream);
        return new PDImageXObject(pdStream, null);
    }

    /**
     * Creates an Image XObject in the given document.
     * @param document the current document
     * @throws java.io.IOException if there is an error creating the XObject.
     */
    public PDImageXObject(PDDocument document) throws IOException
    {
        this(new PDStream(document), null);
    }

    /**
     * Creates an Image XObject in the given document using the given filtered stream.
     * @param document the current document
     * @param encodedStream an encoded stream of image data
     * @param cosFilter the filter or a COSArray of filters
     * @param width the image width
     * @param height the image height
     * @param bitsPerComponent the bits per component
     * @param initColorSpace the color space
     * @throws IOException if there is an error creating the XObject.
     */
    public PDImageXObject(PDDocument document, InputStream encodedStream, COSBase cosFilter,
        int width, int height, int bitsPerComponent, PDColorSpace initColorSpace) throws IOException
    {
        super(createRawStream(document, encodedStream), COSName.IMAGE);
        getCOSStream().setItem(COSName.FILTER, cosFilter);
        resources = null;
        colorSpace = null;
        setBitsPerComponent(bitsPerComponent);
        setWidth(width);
        setHeight(height);
        setColorSpace(initColorSpace);
    }

    /**
     * Creates a COS stream from raw (encoded) data.
     */
    private static COSStream createRawStream(PDDocument document, InputStream rawInput)
        throws IOException
    {
        COSStream stream = document.getDocument().createCOSStream();
        OutputStream output = null;
        try
        {
            output = stream.createRawOutputStream();
            IOUtils.copy(rawInput, output);
        }
        finally
        {
            if (output != null)
            {
                output.close();
            }
        }
        return stream;
    }

    /**
     * Creates an Image XObject with the given stream as its contents and current color spaces.
     * @param stream the XObject stream to read
     * @param resources the current resources
     * @throws java.io.IOException if there is an error creating the XObject.
     */
    public PDImageXObject(PDStream stream, PDResources resources) throws IOException
    {
        this(stream, resources, stream.createInputStream());
    }

    /**
     * Create a PDImageXObject from an image file, see {@link #createFromFile(File, PDDocument)} for
     * more details.
     *
     * @param imagePath the image file path.
     * @param doc the document that shall use this PDImageXObject.
     * @return a PDImageXObject.
     * @throws IOException if there is an error when reading the file or creating the
     * PDImageXObject, or if the image type is not supported.
     */
    public static PDImageXObject createFromFile(String imagePath, PDDocument doc) throws IOException
    {
        return createFromFile(new File(imagePath), doc);
    }

    /**
     * Create a PDImageXObject from an image file. The file format is determined by the file name
     * suffix. The following suffixes are supported: jpg, jpeg, tif, tiff, gif, bmp and png. This is
     * a convenience method that calls {@link JPEGFactory#createFromStream},
     * {@link CCITTFactory#createFromFile} or {@link ImageIO#read} combined with
     * {@link LosslessFactory#createFromImage}. (The later can also be used to create a
     * PDImageXObject from a BufferedImage).
     *
     * @param file the image file.
     * @param doc the document that shall use this PDImageXObject.
     * @return a PDImageXObject.
     * @throws IOException if there is an error when reading the file or creating the
     * PDImageXObject.
     * @throws IllegalArgumentException if the image type is not supported.
     */
    public static PDImageXObject createFromFile(File file, PDDocument doc) throws IOException
    {
        String name = file.getName();
        int dot = file.getName().lastIndexOf('.');
        if (dot == -1)
        {
            throw new IOException("Image type not supported: " + name);
        }
        String ext = name.substring(dot + 1).toLowerCase();
        if ("jpg".equals(ext) || "jpeg".equals(ext))
        {
            return JPEGFactory.createFromStream(doc, new FileInputStream(file));
        }
        if ("tif".equals(ext) || "tiff".equals(ext))
        {
            return CCITTFactory.createFromFile(doc, file);
        }
        if ("gif".equals(ext) || "bmp".equals(ext) || "png".equals(ext))
        {
            Bitmap bim = BitmapFactory.decodeFile(file.getPath());
            return LosslessFactory.createFromImage(doc, bim);
        }
        throw new IOException("Image type not supported: " + name);
    }

    // repairs parameters using decode result
    private PDImageXObject(PDStream stream, PDResources resources, COSInputStream input)
    {
        super(repair(stream, input), COSName.IMAGE);
        this.resources = resources;
//        this.colorSpace = input.getDecodeResult().getJPXColorSpace();TODO: PdfBox-Android
    }

    // repairs parameters using decode result
    private static PDStream repair(PDStream stream, COSInputStream input)
    {
        stream.getStream().addAll(input.getDecodeResult().getParameters());
        return stream;
    }

    /**
     * Returns the metadata associated with this XObject, or null if there is none.
     * @return the metadata associated with this object.
     */
    public PDMetadata getMetadata()
    {
        COSStream cosStream = (COSStream) getCOSStream().getDictionaryObject(COSName.METADATA);
        if (cosStream != null)
        {
            return new PDMetadata(cosStream);
        }
        return null;
    }

    /**
     * Sets the metadata associated with this XObject, or null if there is none.
     * @param meta the metadata associated with this object
     */
    public void setMetadata(PDMetadata meta)
    {
        getCOSStream().setItem(COSName.METADATA, meta);
    }

    /**
     * Returns the key of this XObject in the structural parent tree.
     * @return this object's key the structural parent tree
     */
    public int getStructParent()
    {
        return getCOSStream().getInt(COSName.STRUCT_PARENT, 0);
    }

    /**
     * Sets the key of this XObject in the structural parent tree.
     * @param key the new key for this XObject
     */
    public void setStructParent(int key)
    {
        getCOSStream().setInt(COSName.STRUCT_PARENT, key);
    }

    /**
     * {@inheritDoc}
     * The returned images are cached via a SoftReference.
     */
    @Override
    public Bitmap getImage() throws IOException
    {
        if (cachedImage != null)
        {
            Bitmap cached = cachedImage.get();
            if (cached != null)
            {
                return cached;
            }
        }

        // get image as RGB
        Bitmap image = SampledImageReader.getRGBImage(this, getColorKeyMask());

        // soft mask (overrides explicit mask)
        PDImageXObject softMask = getSoftMask();
        if (softMask != null)
        {
            image = applyMask(image, softMask.getOpaqueImage(), true);
        }
        else
        {
            // explicit mask
            PDImageXObject mask = getMask();
            if (mask != null)
            {
                image = applyMask(image, mask.getOpaqueImage(), false);
            }
        }

        cachedImage = new SoftReference<Bitmap>(image);
        return image;
    }

    /**
     * {@inheritDoc}
     * The returned images are not cached.
     */
//    @Override
//    public BufferedImage getStencilImage(Paint paint) throws IOException
//    {
//        if (!isStencil())
//        {
//            throw new IllegalStateException("Image is not a stencil");
//        }
//        return SampledImageReader.getStencilImage(this, paint);
//    }TODO: PdfBox-Android

    /**
     * Returns an RGB buffered image containing the opaque image stream without any masks applied.
     * If this Image XObject is a mask then the buffered image will contain the raw mask.
     * @return the image without any masks applied
     * @throws IOException if the image cannot be read
     */
    public Bitmap getOpaqueImage() throws IOException
    {
        return SampledImageReader.getRGBImage(this, null);
    }

    // explicit mask: RGB + Binary -> ARGB
    // soft mask: RGB + Gray -> ARGB
    private Bitmap applyMask(Bitmap image, Bitmap mask, boolean isSoft)
            throws IOException
    {
        if (mask == null)
        {
            return image;
        }

        int width = image.getWidth();
        int height = image.getHeight();

        if (mask.getWidth() < width || mask.getHeight() < height)
        {
            mask = Bitmap.createScaledBitmap(mask, width, height, true);
        }
        else if (mask.getWidth() > width || mask.getHeight() > height)
        {
            width = mask.getWidth();
            height = mask.getHeight();
            image = Bitmap.createScaledBitmap(image, width, height, true);
        }

        // compose to ARGB
        Bitmap masked = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        // scale mask to fit image
        if (mask.getWidth() != width || mask.getHeight() != height)
        {
        	mask = Bitmap.createScaledBitmap(mask, width, height, true);
        }

        int[] imagePixels = new int[width * height];
        image.getPixels(imagePixels, 0, width, 0, 0, width, height);

        int[] maskPixels = new int[width * height];
        mask.getPixels(maskPixels, 0, width, 0, 0, width, height);

        int alphaPixel;
        for (int y = 0; y < height; y++)
        {
            for (int x = 0; x < width; x++)
            {
                int color = imagePixels[x + width * y];
                
                // Greyscale, any rgb component should do
                alphaPixel = Color.red(maskPixels[x + width * y]);
                if (!isSoft)
                {
                    alphaPixel = 255 - alphaPixel;
                }

                maskPixels[x + width * y] = Color.argb(alphaPixel, Color.red(color),
                    Color.green(color), Color.blue(color));
            }
        }
        masked.setPixels(maskPixels, 0, width, 0, 0, width, height);

        return masked;
    }

    /**
     * Returns the Mask Image XObject associated with this image, or null if there is none.
     * @return Mask Image XObject
     */
    public PDImageXObject getMask() throws IOException
    {
        COSBase mask = getCOSStream().getDictionaryObject(COSName.MASK);
        if (mask instanceof COSArray)
        {
            // color key mask, no explicit mask to return
            return null;
        }
        else
        {
            COSStream cosStream = (COSStream)getCOSStream().getDictionaryObject(COSName.MASK);
            if (cosStream != null)
            {
                // always DeviceGray
                return new PDImageXObject(new PDStream(cosStream), null);
            }
            return null;
        }
    }

    /**
     * Returns the color key mask array associated with this image, or null if there is none.
     * @return Mask Image XObject
     */
    public COSArray getColorKeyMask()
    {
        COSBase mask = getCOSStream().getDictionaryObject(COSName.MASK);
        if (mask instanceof COSArray)
        {
            return (COSArray)mask;
        }
        return null;
    }

    /**
     * Returns the Soft Mask Image XObject associated with this image, or null if there is none.
     * @return the SMask Image XObject, or null.
     */
    public PDImageXObject getSoftMask() throws IOException
    {
        COSStream cosStream = (COSStream)getCOSStream().getDictionaryObject(COSName.SMASK);
        if (cosStream != null)
        {
            // always DeviceGray
            return new PDImageXObject(new PDStream(cosStream), null);
        }
        return null;
    }

    @Override
    public int getBitsPerComponent()
    {
        if (isStencil())
        {
            return 1;
        }
        else
        {
            return getCOSStream().getInt(COSName.BITS_PER_COMPONENT, COSName.BPC);
        }
    }

    @Override
    public void setBitsPerComponent(int bpc)
    {
        getCOSStream().setInt(COSName.BITS_PER_COMPONENT, bpc);
    }

    @Override
    public PDColorSpace getColorSpace() throws IOException
    {
        if (colorSpace == null)
        {
            COSBase cosBase = getCOSStream().getDictionaryObject(COSName.COLORSPACE, COSName.CS);
            if (cosBase != null)
            {
                colorSpace = PDColorSpace.create(cosBase, resources);
            }
            else if (isStencil())
            {
                // stencil mask color space must be gray, it is often missing
                return PDDeviceGray.INSTANCE;
            }
            else
            {
                // an image without a color space is always broken
                throw new IOException("could not determine color space");
            }
        }
        return colorSpace;
    }

    @Override
    public InputStream createInputStream() throws IOException
    {
        return getStream().createInputStream();
    }

    @Override
    public InputStream createInputStream(List<String> stopFilters) throws IOException
    {
        return createInputStream();
    }

    @Override
    public boolean isEmpty()
    {
        return getStream().getStream().getLength() == 0;
    }

    @Override
    public void setColorSpace(PDColorSpace cs)
    {
        getCOSStream().setItem(COSName.COLORSPACE, cs != null ? cs.getCOSObject() : null);
    }

    @Override
    public int getHeight()
    {
        return getCOSStream().getInt(COSName.HEIGHT);
    }

    @Override
    public void setHeight(int h)
    {
        getCOSStream().setInt(COSName.HEIGHT, h);
    }

    @Override
    public int getWidth()
    {
        return getCOSStream().getInt(COSName.WIDTH);
    }

    @Override
    public void setWidth(int w)
    {
        getCOSStream().setInt(COSName.WIDTH, w);
    }

    @Override
    public boolean getInterpolate()
    {
        return getCOSStream().getBoolean(COSName.INTERPOLATE, false);
    }

    @Override
    public void setInterpolate(boolean value)
    {
        getCOSStream().setBoolean(COSName.INTERPOLATE, value);
    }

    @Override
    public void setDecode(COSArray decode)
    {
        getCOSStream().setItem(COSName.DECODE, decode);
    }

    @Override
    public COSArray getDecode()
    {
        COSBase decode = getCOSStream().getDictionaryObject(COSName.DECODE);
        if (decode instanceof COSArray)
        {
            return (COSArray) decode;
        }
        return null;
    }

    @Override
    public boolean isStencil()
    {
        return getCOSStream().getBoolean(COSName.IMAGE_MASK, false);
    }

    @Override
    public void setStencil(boolean isStencil)
    {
        getCOSStream().setBoolean(COSName.IMAGE_MASK, isStencil);
    }

    /**
     * This will get the suffix for this image type, e.g. jpg/png.
     * @return The image suffix or null if not available.
     */
    @Override
    public String getSuffix()
    {
        List<COSName> filters = getStream().getFilters();

        if (filters == null)
        {
            return "png";
        }
        else if (filters.contains(COSName.DCT_DECODE))
        {
            return "jpg";
        }
        else if (filters.contains(COSName.JPX_DECODE))
        {
            return "jpx";
        }
        else if (filters.contains(COSName.CCITTFAX_DECODE))
        {
            return "tiff";
        }
        else if (filters.contains(COSName.FLATE_DECODE)
                || filters.contains(COSName.LZW_DECODE)
                || filters.contains(COSName.RUN_LENGTH_DECODE))
        {
            return "png";
        }
        else
        {
        	Log.w("PdfBox-Android", "getSuffix() returns null, filters: " + filters);
            // TODO more...
            return null;
        }
    }
}
