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
import android.graphics.Rect;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;

import com.tom_roush.harmony.javax.imageio.stream.ImageInputStream;
import com.tom_roush.harmony.javax.imageio.stream.MemoryCacheImageInputStream;
import com.tom_roush.pdfbox.cos.COSArray;
import com.tom_roush.pdfbox.cos.COSNumber;
import com.tom_roush.pdfbox.filter.DecodeOptions;
import com.tom_roush.pdfbox.io.IOUtils;
import com.tom_roush.pdfbox.pdmodel.graphics.color.PDColorSpace;

/**
 * Reads a sampled image from a PDF file.
 * @author John Hewson
 */
final class SampledImageReader
{
    private SampledImageReader()
    {
    }

    /**
     * Returns an ARGB image filled with the given paint and using the given image as a mask.
     * @param paint the paint to fill the visible portions of the image with
     * @return a masked image filled with the given paint
     * @throws IOException if the image cannot be read
     * @throws IllegalStateException if the image is not a stencil.
     */
    public static Bitmap getStencilImage(PDImage pdImage, Paint paint) throws IOException
    {
        int width = pdImage.getWidth();
        int height = pdImage.getHeight();

        // compose to ARGB
        Bitmap masked = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas g = new Canvas(masked);

        // draw the mask
        //g.drawImage(mask, 0, 0, null);

        // fill with paint using src-in
        //g.setComposite(AlphaComposite.SrcIn);
        g.drawRect(0, 0, width, height, paint);

        // set the alpha

        // avoid getting a Bitmap for the mask to lessen memory footprint.
        // Such masks are always bpc=1 and have no colorspace, but have a decode.
        // (see 8.9.6.2 Stencil Masking)
        ImageInputStream iis = null;
        try
        {
            iis = new MemoryCacheImageInputStream(pdImage.createInputStream());
            final float[] decode = getDecodeArray(pdImage);
            int value = decode[0] < decode[1] ? 1 : 0;
            int rowLen = width / 8;
            if (width % 8 > 0)
            {
                rowLen++;
            }
            byte[] buff = new byte[rowLen];
            for (int y = 0; y < height; y++)
            {
                int x = 0;
                int readLen = iis.read(buff);
                for (int r = 0; r < rowLen && r < readLen; r++)
                {
                    int byteValue = buff[r];
                    int mask = 128;
                    int shift = 7;
                    for (int i = 0; i < 8; i++)
                    {
                        int bit = (byteValue & mask) >> shift;
                        mask >>= 1;
                        --shift;
                        if (bit == value)
                        {
                            masked.setPixel(x, y, Color.TRANSPARENT);
                        }
                        x++;
                        if (x == width)
                        {
                            break;
                        }
                    }
                }
                if (readLen != rowLen)
                {
                    Log.w("PdfBox-Android", "premature EOF, image will be incomplete");
                    break;
                }
            }
        }
        finally
        {
            if (iis != null)
            {
                iis.close();
            }
        }

        return masked;
    }

    /**
     * Returns the content of the given image as an AWT buffered image with an RGB color space.
     * If a color key mask is provided then an ARGB image is returned instead.
     * This method never returns null.
     * @param pdImage the image to read
     * @param colorKey an optional color key mask
     * @return content of this image as an RGB buffered image
     * @throws IOException if the image cannot be read
     */
    public static Bitmap getRGBImage(PDImage pdImage, COSArray colorKey) throws IOException
    {
        return getRGBImage(pdImage, null, 1, colorKey);
    }

    private static Rect clipRegion(PDImage pdImage, Rect region)
    {
        if (region == null)
        {
            return new Rect(0, 0, pdImage.getWidth(), pdImage.getHeight());
        }
        else
        {
            int x = Math.max(0, region.left);
            int y = Math.max(0, region.top);
            int width = Math.min(region.width(), pdImage.getWidth() - x);
            int height = Math.min(region.height(), pdImage.getHeight() - y);
            return new Rect(x, y, width, height);
        }
    }

    /**
     * Returns the content of the given image as an AWT buffered image with an RGB color space.
     * If a color key mask is provided then an ARGB image is returned instead.
     * This method never returns null.
     * @param pdImage the image to read
     * @param region The region of the source image to get, or null if the entire image is needed.
     *               The actual region will be clipped to the dimensions of the source image.
     * @param subsampling The amount of rows and columns to advance for every output pixel, a value
     * of 1 meaning every pixel will be read. It must not be larger than the image width or height.
     * @param colorKey an optional color key mask
     * @return content of this image as an (A)RGB buffered image
     * @throws IOException if the image cannot be read
     */
    public static Bitmap getRGBImage(PDImage pdImage, Rect region, int subsampling,
        COSArray colorKey) throws IOException
    {
        if (pdImage.isEmpty())
        {
            throw new IOException("Image stream is empty");
        }
        Rect clipped = clipRegion(pdImage, region);

        // get parameters, they must be valid or have been repaired
        final PDColorSpace colorSpace = pdImage.getColorSpace();
        final int numComponents = colorSpace.getNumberOfComponents();
        final int width = (int) Math.ceil(clipped.width() / subsampling);
        final int height = (int) Math.ceil(clipped.height() / subsampling);
        final int bitsPerComponent = pdImage.getBitsPerComponent();
        final float[] decode = getDecodeArray(pdImage);

        if (width <= 0 || height <= 0 || pdImage.getWidth() <= 0 || pdImage.getHeight() <= 0)
        {
            throw new IOException("image width and height must be positive");
        }

        try
        {
            if (bitsPerComponent == 1 && colorKey == null && numComponents == 1)
            {
                return from1Bit(pdImage, clipped, subsampling, width, height);
            }

            //
            // An AWT raster must use 8/16/32 bits per component. Images with < 8bpc
            // will be unpacked into a byte-backed raster. Images with 16bpc will be reduced
            // in depth to 8bpc as they will be drawn to TYPE_INT_RGB images anyway. All code
            // in PDColorSpace#toRGBImage expects an 8-bit range, i.e. 0-255.
            final float[] defaultDecode = pdImage.getColorSpace().getDefaultDecode(8);
            if (pdImage.getSuffix() != null && pdImage.getSuffix().equals("jpg") && subsampling == 1)
            {
                return BitmapFactory.decodeStream(pdImage.createInputStream());
            }
            else if (bitsPerComponent == 8 && Arrays.equals(decode, defaultDecode) &&
                colorKey == null)
            {
                // convert image, faster path for non-decoded, non-colormasked 8-bit images
                return from8bit(pdImage, clipped, subsampling, width, height);
            }
            Log.e("PdfBox-Android", "Trying to create other-bit image not supported");
//        return fromAny(pdImage, colorKey, clipped, subsampling, width, height);
            return from8bit(pdImage, clipped, subsampling, width, height);
        }
        catch (NegativeArraySizeException ex)
        {
            throw new IOException(ex);
        }
    }

    private static Bitmap from1Bit(PDImage pdImage, Rect clipped, final int subsampling,
        final int width, final int height) throws IOException
    {
        int currentSubsampling = subsampling;
        final PDColorSpace colorSpace = pdImage.getColorSpace();
        final float[] decode = getDecodeArray(pdImage);
        Bitmap raster = Bitmap.createBitmap(width, height, Bitmap.Config.ALPHA_8);
        ByteBuffer buffer = ByteBuffer.allocate(raster.getRowBytes() * height);
        raster.copyPixelsToBuffer(buffer);
        byte[] output;

        DecodeOptions options = new DecodeOptions(currentSubsampling);
        options.setSourceRegion(clipped);
        // read bit stream
        InputStream iis = null;
        try
        {
            // create stream
            iis = pdImage.createInputStream(options);

            final int inputWidth;
            final int startx;
            final int starty;
            final int scanWidth;
            final int scanHeight;
            if (options.isFilterSubsampled())
            {
                // Decode options were honored, and so there is no need for additional clipping or subsampling
                inputWidth = width;
                startx = 0;
                starty = 0;
                scanWidth = width;
                scanHeight = height;
                currentSubsampling = 1;
            }
            else
            {
                // Decode options not honored, so we need to clip and subsample ourselves.
                inputWidth = pdImage.getWidth();
                startx = clipped.left;
                starty = clipped.top;
                scanWidth = clipped.width();
                scanHeight = clipped.height();
            }
            output = buffer.array();
            final boolean isIndexed = false; // colorSpace instanceof PDIndexed; TODO: PdfBox-Android

            int rowLen = inputWidth / 8;
            if (inputWidth % 8 > 0)
            {
                rowLen++;
            }

            // read stream
            byte value0;
            byte value1;
            if (isIndexed || decode[0] < decode[1])
            {
                value0 = 0;
                value1 = (byte) 255;
            }
            else
            {
                value0 = (byte) 255;
                value1 = 0;
            }
            byte[] buff = new byte[rowLen];
            int idx = 0;
            for (int y = 0; y < starty + scanHeight; y++)
            {
                int x = 0;
                int readLen = iis.read(buff);
                if (y < starty || y % currentSubsampling > 0)
                {
                    continue;
                }
                for (int r = 0; r < rowLen && r < readLen; r++)
                {
                    int value = buff[r];
                    int mask = 128;
                    for (int i = 0; i < 8; i++)
                    {
                        if (x >= startx + scanWidth)
                        {
                            break;
                        }
                        int bit = value & mask;
                        mask >>= 1;
                        if (x >= startx && x % currentSubsampling == 0)
                        {
                            output[idx++] = bit == 0 ? value0 : value1;
                        }
                        x++;
                    }
                }
                if (readLen != rowLen)
                {
                    Log.w("PdfBox-Android", "premature EOF, image will be incomplete");
                    break;
                }
            }

            buffer.rewind();
            raster.copyPixelsFromBuffer(buffer);

            // use the color space to convert the image to RGB
            return colorSpace.toRGBImage(raster);
        }
        finally
        {
            if (iis != null)
            {
                iis.close();
            }
        }
    }

    // faster, 8-bit non-decoded, non-colormasked image conversion
    private static Bitmap from8bit(PDImage pdImage, Rect clipped, final int subsampling,
        final int width, final int height) throws IOException
    {
        int currentSubsampling = subsampling;
        DecodeOptions options = new DecodeOptions(currentSubsampling);
        options.setSourceRegion(clipped);
        InputStream input = pdImage.createInputStream(options);
        try
        {
            final int inputWidth;
            final int startx;
            final int starty;
            final int scanWidth;
            final int scanHeight;
            if (options.isFilterSubsampled())
            {
                // Decode options were honored, and so there is no need for additional clipping or subsampling
                inputWidth = width;
                startx = 0;
                starty = 0;
                scanWidth = width;
                scanHeight = height;
                currentSubsampling = 1;
            }
            else
            {
                // Decode options not honored, so we need to clip and subsample ourselves.
                inputWidth = pdImage.getWidth();
                startx = clipped.left;
                starty = clipped.top;
                scanWidth = clipped.width();
                scanHeight = clipped.height();
            }
            final int numComponents = pdImage.getColorSpace().getNumberOfComponents();
            // get the raster's underlying byte buffer
            int[] banks = new int[width * height];
//            byte[][] banks = ((DataBufferByte) raster.getDataBuffer()).getBankData();
            byte[] tempBytes = new byte[numComponents * inputWidth];
            // compromise between memory and time usage:
            // reading the whole image consumes too much memory
            // reading one pixel at a time makes it slow in our buffering infrastructure 
            int i = 0;
            for (int y = 0; y < starty + scanHeight; ++y)
            {
                input.read(tempBytes);
                if (y < starty || y % currentSubsampling > 0)
                {
                    continue;
                }

                for (int x = startx; x < startx + scanWidth; x += currentSubsampling)
                {
                    int tempBytesIdx = x * numComponents;
                    if (numComponents == 3)
                    {
                        banks[i] = Color.argb(255, tempBytes[tempBytesIdx] & 0xFF,
                            tempBytes[tempBytesIdx + 1] & 0xFF, tempBytes[tempBytesIdx + 2] & 0xFF);
                    }
                    else if (numComponents == 1)
                    {
                        int in = tempBytes[tempBytesIdx] & 0xFF;
                        banks[i] = Color.argb(in, in, in, in);
                    }
                    ++i;
                }
            }
            Bitmap raster = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            raster.setPixels(banks, 0, width, 0 ,0, width, height);

            // use the color space to convert the image to RGB
//            return pdImage.getColorSpace().toRGBImage(raster); TODO: PdfBox-Android
            return raster;
        }
        finally
        {
            IOUtils.closeQuietly(input);
        }
    }

    // slower, general-purpose image conversion from any image format
//    private static BufferedImage fromAny(PDImage pdImage, WritableRaster raster, COSArray colorKey, Rectangle clipped,
//        final int subsampling, final int width, final int height) TODO: Pdfbox-Android

    // color key mask: RGB + Binary -> ARGB
//    private static BufferedImage applyColorKeyMask(BufferedImage image, BufferedImage mask) TODO: PdfBox-Android

    // gets decode array from dictionary or returns default
    private static float[] getDecodeArray(PDImage pdImage) throws IOException
    {
        final COSArray cosDecode = pdImage.getDecode();
        float[] decode = null;

        if (cosDecode != null)
        {
            int numberOfComponents = pdImage.getColorSpace().getNumberOfComponents();
            if (cosDecode.size() != numberOfComponents * 2)
            {
                if (pdImage.isStencil() && cosDecode.size() >= 2
                    && cosDecode.get(0) instanceof COSNumber
                    && cosDecode.get(1) instanceof COSNumber)
                {
                    float decode0 = ((COSNumber) cosDecode.get(0)).floatValue();
                    float decode1 = ((COSNumber) cosDecode.get(1)).floatValue();
                    if (decode0 >= 0 && decode0 <= 1 && decode1 >= 0 && decode1 <= 1)
                    {
                        Log.w("PdfBox-Android", "decode array " + cosDecode
                            + " not compatible with color space, using the first two entries");
                        return new float[]
                            {
                                decode0, decode1
                            };
                    }
                }
                Log.e("PdfBox-Android", "decode array " + cosDecode
                    + " not compatible with color space, using default");
            }
            else
            {
                decode = cosDecode.toFloatArray();
            }
        }

        // use color space default
        if (decode == null)
        {
            return pdImage.getColorSpace().getDefaultDecode(pdImage.getBitsPerComponent());
        }

        return decode;
    }
}
