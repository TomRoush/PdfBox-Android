/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.tom_roush.pdfbox.pdmodel.graphics.image;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.tom_roush.pdfbox.cos.COSDictionary;
import com.tom_roush.pdfbox.cos.COSName;
import com.tom_roush.pdfbox.filter.Filter;
import com.tom_roush.pdfbox.filter.FilterFactory;
import com.tom_roush.pdfbox.io.IOUtils;
import com.tom_roush.pdfbox.pdmodel.PDDocument;
import com.tom_roush.pdfbox.pdmodel.graphics.color.PDColorSpace;
import com.tom_roush.pdfbox.pdmodel.graphics.color.PDDeviceGray;
import com.tom_roush.pdfbox.pdmodel.graphics.color.PDDeviceRGB;

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
        return createFromByteArray(document, IOUtils.toByteArray(stream));
    }

    /**
     * Creates a new JPEG Image XObject from a byte array containing JPEG data.
     *
     * @param document the document where the image will be created
     * @param byteArray bytes of JPEG image
     * @return a new Image XObject
     *
     * @throws IOException if the input stream cannot be read
     */
    public static PDImageXObject createFromByteArray(PDDocument document, byte[] byteArray)
        throws IOException
    {
        // copy stream
        ByteArrayInputStream byteStream = new ByteArrayInputStream(byteArray);

        Dimensions meta = retrieveDimensions(byteStream);

        PDColorSpace colorSpace = PDDeviceRGB.INSTANCE; // All images are RGB after being loaded by Bitmaps

        // create PDImageXObject from stream
        PDImageXObject pdImage = new PDImageXObject(document, byteStream,
            COSName.DCT_DECODE, meta.width, meta.height, 8, colorSpace);

        return pdImage;
    }

    private static class Dimensions
    {
        private int width;
        private int height;
        private int numComponents;
    }

    private static Dimensions retrieveDimensions(ByteArrayInputStream stream) throws IOException
    {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(stream, null, options);
        stream.reset();

        Dimensions meta = new Dimensions();
        meta.width = options.outWidth;
        meta.height = options.outHeight;

        return meta;
    }

    /**
     * Creates a new JPEG PDImageXObject from a Bitmap.
     * <p>
     * Do not read a JPEG image from a stream/file and call this method; you'll get more speed and
     * quality by calling {@link #createFromStream(com.tom_roush.pdfbox.pdmodel.PDDocument,
     * java.io.InputStream) createFromStream()} instead.
     *
     * @param document the document where the image will be created
     * @param image the Bitmap to embed
     * @return a new Image XObject
     * @throws IOException if the JPEG data cannot be written
     */
    public static PDImageXObject createFromImage(PDDocument document, Bitmap image)
        throws IOException
    {
        return createFromImage(document, image, 0.75f);
    }

    /**
     * Creates a new JPEG PDImageXObject from a Bitmap and a given quality.
     * <p>
     * Do not read a JPEG image from a stream/file and call this method; you'll get more speed and
     * quality by calling {@link #createFromStream(com.tom_roush.pdfbox.pdmodel.PDDocument,
     * java.io.InputStream) createFromStream()} instead.
     *
     * The image will be created with a dpi value of 72 to be stored in metadata.
     * @param document the document where the image will be created
     * @param image the Bitmap to embed
     * @param quality The desired JPEG compression quality; between 0 (best
     * compression) and 1 (best image quality). See
     * {@link Bitmap#compress(Bitmap.CompressFormat, int, OutputStream)} for more details.
     * @return a new Image XObject
     * @throws IOException if the JPEG data cannot be written
     */
    public static PDImageXObject createFromImage(PDDocument document, Bitmap image,
        float quality) throws IOException
    {
        return createFromImage(document, image, quality, 72);
    }

    /**
     * Creates a new JPEG Image XObject from a Bitmap, a given quality and dpi metadata.
     * <p>
     * Do not read a JPEG image from a stream/file and call this method; you'll get more speed and
     * quality by calling {@link #createFromStream(com.tom_roush.pdfbox.pdmodel.PDDocument,
     * java.io.InputStream) createFromStream()} instead.
     *
     * @param document the document where the image will be created
     * @param image the Bitmap to embed
     * @param quality The desired JPEG compression quality; between 0 (best
     * compression) and 1 (best image quality). See
     * {@link Bitmap#compress(Bitmap.CompressFormat, int, OutputStream)} for more details.
     * @param dpi the desired dpi (resolution) value of the JPEG to be stored in metadata. This
     * value has no influence on image content or size.
     * @return a new Image XObject
     * @throws IOException if the JPEG data cannot be written
     */
    public static PDImageXObject createFromImage(PDDocument document, Bitmap image,
        float quality, int dpi) throws IOException
    {
        return createJPEG(document, image, quality, dpi);
    }

    // returns the alpha channel of an image
    private static Bitmap getAlphaImage(Bitmap image)
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
        // create XObject
        byte[] encoded = encodeImageToJPEGStream(image, quality, dpi);
        ByteArrayInputStream encodedByteStream = new ByteArrayInputStream(encoded);

        PDImageXObject pdImage = new PDImageXObject(document, encodedByteStream, COSName.DCT_DECODE,
            image.getWidth(), image.getHeight(), 8,
            PDDeviceRGB.INSTANCE
        );

        // extract alpha channel (if any)
        if (image.hasAlpha())
        {
            // alpha -> soft mask
            PDImage xAlpha = JPEGFactory.createAlphaFromARGBImage(document, image);
            pdImage.getCOSObject().setItem(COSName.SMASK, xAlpha);
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
        bpc = 8;
        for (int pixel : pixels)
        {
            bos.write(Color.alpha(pixel));
        }
        //        }

        return prepareImageXObject(document, bos.toByteArray(),
            image.getWidth(), image.getHeight(), bpc, PDDeviceGray.INSTANCE);
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

    private static byte[] encodeImageToJPEGStream(Bitmap image, float quality, int dpi) throws IOException
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, (int)(quality * 100), baos);
        return baos.toByteArray();
    }

    // returns a PDColorSpace for a given BufferedImage
    //	private static PDColorSpace getColorSpaceFromAWT(Bitmap awtImage) TODO: PdfBox-Android

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
