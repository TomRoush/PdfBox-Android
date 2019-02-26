/*
 * Copyright 2014 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
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
import android.graphics.Color;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import com.tom_roush.harmony.javax.imageio.stream.MemoryCacheImageOutputStream;
import com.tom_roush.pdfbox.cos.COSDictionary;
import com.tom_roush.pdfbox.cos.COSName;
import com.tom_roush.pdfbox.filter.Filter;
import com.tom_roush.pdfbox.filter.FilterFactory;
import com.tom_roush.pdfbox.pdmodel.PDDocument;
import com.tom_roush.pdfbox.pdmodel.graphics.color.PDColorSpace;
import com.tom_roush.pdfbox.pdmodel.graphics.color.PDDeviceColorSpace;
import com.tom_roush.pdfbox.pdmodel.graphics.color.PDDeviceGray;
import com.tom_roush.pdfbox.pdmodel.graphics.color.PDDeviceRGB;

/**
 * Factory for creating a PDImageXObject containing a lossless compressed image.
 *
 * @author Tilman Hausherr
 */
public final class LosslessFactory
{
    private LosslessFactory()
    {
    }
    
    /**
     * Creates a new lossless encoded Image XObject from a Buffered Image.
     *
     * @param document the document where the image will be created
     * @param image the buffered image to embed
     * @return a new Image XObject
     * @throws IOException if something goes wrong
     */
    public static PDImageXObject createFromImage(PDDocument document, Bitmap image)
            throws IOException
    {
        int bpc;
        PDDeviceColorSpace deviceColorSpace;

        int height = image.getHeight();
        int width = image.getWidth();
        byte[] imageData;

        if (image.getConfig() == Bitmap.Config.ALPHA_8)
//        if ((image.getType() == BufferedImage.TYPE_BYTE_GRAY && image.getColorModel().getPixelSize() <= 8)
//                || (image.getType() == BufferedImage.TYPE_BYTE_BINARY && image.getColorModel().getPixelSize() == 1))
        {
            // grayscale images need one color per sample
//            bpc = image.getColorModel().getPixelSize();
            bpc = 8;
            deviceColorSpace = PDDeviceGray.INSTANCE;

            ByteArrayOutputStream bos = new ByteArrayOutputStream(
                (width * bpc / 8) + (width * bpc % 8 != 0 ? 1 : 0) * height);
            MemoryCacheImageOutputStream mcios = new MemoryCacheImageOutputStream(bos);

            int[] imagePixels = new int[width * height];
            image.getPixels(imagePixels, 0, width, 0, 0, width, height);
            for (int y = 0; y < height; ++y)
            {
                for (int pixelIdx = width * y; pixelIdx < (y + 1) * width; ++pixelIdx)
                {
                    mcios.writeBits(imagePixels[pixelIdx] & 0xFF, bpc);
                }

                int bitOffset = mcios.getBitOffset();
                if (bitOffset != 0)
                {
                    mcios.writeBits(0, 8 - bitOffset);
                }
            }
            mcios.flush();
            mcios.close();

            imageData = bos.toByteArray();
        }
        else
        {
            // RGB
            bpc = 8;
            deviceColorSpace = PDDeviceRGB.INSTANCE;
            imageData = new byte[width * height * 3];
            int byteIdx = 0;

            int[] imagePixels = new int[width * height];
            image.getPixels(imagePixels, 0, width, 0, 0, width, height);
            for (int pixel : imagePixels)
            {
                imageData[byteIdx++] = (byte)((pixel >> 16) & 0xFF);
                imageData[byteIdx++] = (byte)((pixel >> 8) & 0xFF);
                imageData[byteIdx++] = (byte)(pixel & 0xFF);
            }
        }

        PDImageXObject pdImage = prepareImageXObject(document, imageData,
                image.getWidth(), image.getHeight(), bpc, deviceColorSpace);

        // alpha -> soft mask
        PDImage xAlpha = createAlphaFromARGBImage(document, image); // TODO: PdfBox-Android - simplify with extract alpha?
        if (xAlpha != null)
        {
            pdImage.getCOSStream().setItem(COSName.SMASK, xAlpha);
        }

        return pdImage;
    }

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
        
        // extract the alpha information
//        WritableRaster alphaRaster = image.getAlphaRaster();
//        if (alphaRaster == null)
//        {
//            // happens sometimes (PDFBOX-2654) despite colormodel claiming to have alpha
//            return createAlphaFromARGBImage2(document, image);
//        }

//        int[] pixels = alphaRaster.getPixels(0, 0,
//                alphaRaster.getSampleModel().getWidth(),
//                alphaRaster.getSampleModel().getHeight(),
//                (int[]) null);
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

    // create alpha image the hard way: get the alpha through getRGB()
//    private static PDImageXObject createAlphaFromARGBImage2(PDDocument document, Bitmap bi)
//            throws IOException
//    {
//        ByteArrayOutputStream bos = new ByteArrayOutputStream();
//        int bpc;
//        if (bi.getTransparency() == Transparency.BITMASK)
//        {
//            bpc = 1;
//            MemoryCacheImageOutputStream mcios = new MemoryCacheImageOutputStream(bos);
//            for (int y = 0, h = bi.getHeight(); y < h; ++y)
//            {
//                for (int x = 0, w = bi.getWidth(); x < w; ++x)
//                {
//                    int alpha = bi.getRGB(x, y) >>> 24;
//                    mcios.writeBit(alpha);
//                }
//                while (mcios.getBitOffset() != 0)
//                {
//                    mcios.writeBit(0);
//                }
//            }
//            mcios.flush();
//            mcios.close();
//        }
//        else
//        {
//            bpc = 8;
//            for (int y = 0, h = bi.getHeight(); y < h; ++y)
//            {
//                for (int x = 0, w = bi.getWidth(); x < w; ++x)
//                {
//                    int alpha = Color.alpha(bi.getPixel(x, y));
//                    bos.write(alpha);
//                }
//            }
//        }
//
//        PDImageXObject pdImage = prepareImageXObject(document, bos.toByteArray(), 
//                bi.getWidth(), bi.getHeight(), bpc, PDDeviceGray.INSTANCE);
//
//        return pdImage;
//    }            

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
        // Pre-size the output stream to half of the input
        ByteArrayOutputStream baos = new ByteArrayOutputStream(byteArray.length / 2);

        Filter filter = FilterFactory.INSTANCE.getFilter(COSName.FLATE_DECODE);
        filter.encode(new ByteArrayInputStream(byteArray), baos, new COSDictionary(), 0);

        ByteArrayInputStream encodedByteStream = new ByteArrayInputStream(baos.toByteArray());
        return new PDImageXObject(document, encodedByteStream, COSName.FLATE_DECODE,
                width, height, bitsPerComponent, initColorSpace);
    }

}
