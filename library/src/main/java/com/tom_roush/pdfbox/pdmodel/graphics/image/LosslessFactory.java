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
        if ((image.getConfig() == Bitmap.Config.ALPHA_8))
        {
            return createFromGrayImage(image, document);
        }
        else
        {
            return createFromRGBImage(image, document);
        }
    }

    // grayscale images need one color per sample
    private static PDImageXObject createFromGrayImage(Bitmap image, PDDocument document)
        throws IOException
    {
        int height = image.getHeight();
        int width = image.getWidth();
        int[] rgbLineBuffer = new int[width];
        int bpc = 8;
        ByteArrayOutputStream baos = new ByteArrayOutputStream(((width*bpc/8)+(width*bpc%8 != 0 ? 1:0))*height);
        MemoryCacheImageOutputStream mcios = new MemoryCacheImageOutputStream(baos);
        for (int y = 0; y < height; ++y)
        {
            image.getPixels(rgbLineBuffer, 0, width, 0, y, width, 1);
            for (int pixel : rgbLineBuffer)
            {
                mcios.writeBits(pixel & 0xFF, bpc);
            }

            int bitOffset = mcios.getBitOffset();
            if (bitOffset != 0)
            {
                mcios.writeBits(0, 8 - bitOffset);
            }
        }
        mcios.flush();
        mcios.close();
        return prepareImageXObject(document, baos.toByteArray(),
            image.getWidth(), image.getHeight(), bpc, PDDeviceGray.INSTANCE);
    }

    private static PDImageXObject createFromRGBImage(Bitmap image, PDDocument document) throws IOException
    {
        int height = image.getHeight();
        int width = image.getWidth();
        int[] rgbLineBuffer = new int[width];
        int bpc = 8;
        PDDeviceColorSpace deviceColorSpace = PDDeviceRGB.INSTANCE;
        byte[] imageData = new byte[width * height * 3];
        int byteIdx = 0;
        int alphaByteIdx = 0;
        int alphaBitPos = 7;
        int apbc = 8;
        byte[] alphaImageData;
        if (image.hasAlpha())
        {
            alphaImageData = new byte[((width * apbc / 8) + (width * apbc % 8 != 0 ? 1 : 0)) * height];
        }
        else
        {
            alphaImageData = new byte[0];
        }
        for (int y = 0; y < height; ++y)
        {
            image.getPixels(rgbLineBuffer, 0, width, 0, y, width, 1);
            for (int pixel : rgbLineBuffer)
            {
                imageData[byteIdx++] = (byte) ((pixel >> 16) & 0xFF);
                imageData[byteIdx++] = (byte) ((pixel >> 8) & 0xFF);
                imageData[byteIdx++] = (byte) (pixel & 0xFF);
                if (image.hasAlpha())
                {
                    // we have the alpha right here, so no need to do it separately
                    // as done prior April 2018
                    {
                        // write a byte
                        alphaImageData[alphaByteIdx++] = (byte) ((pixel >> 24) & 0xFF);
                    }
                }
            }
        }
        PDImageXObject pdImage = prepareImageXObject(document, imageData,
            image.getWidth(), image.getHeight(), bpc, deviceColorSpace);
        if (image.hasAlpha())
        {
            PDImageXObject pdMask = prepareImageXObject(document, alphaImageData,
                image.getWidth(), image.getHeight(), apbc, PDDeviceGray.INSTANCE);
            pdImage.getCOSObject().setItem(COSName.SMASK, pdMask);
        }
        return pdImage;
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
        //pre-size the output stream to half of the input
        ByteArrayOutputStream baos = new ByteArrayOutputStream(byteArray.length/2);

        Filter filter = FilterFactory.INSTANCE.getFilter(COSName.FLATE_DECODE);
        filter.encode(new ByteArrayInputStream(byteArray), baos, new COSDictionary(), 0);

        ByteArrayInputStream encodedByteStream = new ByteArrayInputStream(baos.toByteArray());
        return new PDImageXObject(document, encodedByteStream, COSName.FLATE_DECODE,
            width, height, bitsPerComponent, initColorSpace);
    }

}
