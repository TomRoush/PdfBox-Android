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
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;

import com.tom_roush.harmony.javax.imageio.stream.MemoryCacheImageOutputStream;
import com.tom_roush.pdfbox.cos.COSDictionary;
import com.tom_roush.pdfbox.cos.COSInteger;
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
    /**
     * Internal, only for benchmark purpose
     */
    static boolean usePredictorEncoder = false;

    private LosslessFactory()
    {
    }

    /**
     * Creates a new lossless encoded image XObject from a Bitmap.
     * <p>
     * <u>New for advanced users from 2.0.12 on:</u><br>
     * If you created your image with a non standard ICC colorspace, it will be
     * preserved. (If you load images in java using ImageIO then no need to read
     * this segment) However a new colorspace will be created for each image. So
     * if you create a PDF with several such images, consider replacing the
     * colorspace with a common object to save space. This is done with
     * {@link PDImageXObject#getColorSpace()} and
     * {@link PDImageXObject#setColorSpace(com.tom_roush.pdfbox.pdmodel.graphics.color.PDColorSpace) PDImageXObject.setColorSpace()}
     *
     * @param document the document where the image will be created
     * @param image the Bitmap to embed
     * @return a new image XObject
     * @throws IOException if something goes wrong
     */
    public static PDImageXObject createFromImage(PDDocument document, Bitmap image)
        throws IOException
    {
        if (isGrayImage(image))
        {
            return createFromGrayImage(image, document);
        }

        // We try to encode the image with predictor
        if (usePredictorEncoder)
        {
            PDImageXObject pdImageXObject = new PredictorEncoder(document, image).encode();
            if (pdImageXObject != null)
            {
                if (pdImageXObject.getColorSpace() == PDDeviceRGB.INSTANCE &&
                    pdImageXObject.getBitsPerComponent() < 16 &&
                    image.getWidth() * image.getHeight() <= 50 * 50)
                {
                    // also create classic compressed image, compare sizes
                    PDImageXObject pdImageXObjectClassic = createFromRGBImage(image, document);
                    if (pdImageXObjectClassic.getCOSObject().getLength() <
                        pdImageXObject.getCOSObject().getLength())
                    {
                        Log.e("PdfBox-Android", "Return classic");
                        pdImageXObject.getCOSObject().close();
                        return pdImageXObjectClassic;
                    }
                    else
                    {
                        Log.e("PdfBox-Android", "Return predictor");
                        pdImageXObjectClassic.getCOSObject().close();
                    }
                }
                return pdImageXObject;
            }
        }

        // Fallback: We export the image as 8-bit sRGB and might loose color information
        return createFromRGBImage(image, document);
    }

    private static boolean isGrayImage(Bitmap image)
    {
        return image.getConfig() == Bitmap.Config.ALPHA_8;
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
     * Create a PDImageXObject using the Flate filter.
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
    static PDImageXObject prepareImageXObject(PDDocument document,
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

    private static class PredictorEncoder
    {
        private final PDDocument document;
        private final Bitmap image;
        private final int bytesPerComponent;
        private final int bytesPerPixel;

        private final int height;
        private final int width;

        private final byte[] dataRawRowNone;
        private final byte[] dataRawRowSub;
        private final byte[] dataRawRowUp;
        private final byte[] dataRawRowAverage;
        private final byte[] dataRawRowPaeth;

        final Bitmap.Config imageType;
        final boolean hasAlpha;
        final byte[] alphaImageData;

        final byte[] aValues;
        final byte[] cValues;
        final byte[] bValues;
        final byte[] xValues;
        final byte[] tmpResultValues;

        /**
         * Initialize the encoder and set all final fields
         */
        PredictorEncoder(PDDocument document, Bitmap image)
        {
            this.document = document;
            this.image = image;

            // The raw count of components per pixel including optional alpha
            this.bytesPerComponent = 1;

            // Only the bytes we need in the output (excluding alpha)
            this.bytesPerPixel = 3 * bytesPerComponent;

            this.height = image.getHeight();
            this.width = image.getWidth();
            this.imageType = image.getConfig();
            this.hasAlpha = image.hasAlpha();
            this.alphaImageData = hasAlpha ? new byte[width * height * bytesPerComponent] : null;

            // The rows have 1-byte encoding marker and width * BYTES_PER_PIXEL pixel-bytes
            int dataRowByteCount = width * bytesPerPixel + 1;
            this.dataRawRowNone = new byte[dataRowByteCount];
            this.dataRawRowSub = new byte[dataRowByteCount];
            this.dataRawRowUp = new byte[dataRowByteCount];
            this.dataRawRowAverage = new byte[dataRowByteCount];
            this.dataRawRowPaeth = new byte[dataRowByteCount];

            // Write the encoding markers
            dataRawRowNone[0] = 0;
            dataRawRowSub[0] = 1;
            dataRawRowUp[0] = 2;
            dataRawRowAverage[0] = 3;
            dataRawRowPaeth[0] = 4;

            // c | b
            // -----
            // a | x
            //
            // x => current pixel
            this.aValues = new byte[bytesPerPixel];
            this.cValues = new byte[bytesPerPixel];
            this.bValues = new byte[bytesPerPixel];
            this.xValues = new byte[bytesPerPixel];
            this.tmpResultValues = new byte[bytesPerPixel];
        }

        /**
         * Tries to compress the image using a predictor.
         *
         * @return the image or null if it is not possible to encoded the image (e.g. not supported
         * raster format etc.)
         */
        PDImageXObject encode() throws IOException
        {
            final int elementsInRowPerPixel;

            // These variables store a row of the image each, the exact type depends
            // on the image encoding. Can be a int[], short[] or byte[]
            int[] prevRow;
            int[] transferRow;

            switch (imageType)
            {
                case ARGB_8888:
                case RGB_565:
                {
                    elementsInRowPerPixel = 1;
                    prevRow = new int[width * elementsInRowPerPixel];
                    transferRow = new int[width * elementsInRowPerPixel];
                    break;
                }

                default:
                    // We can not handle this unknown format
                    return null;
            }

            final int elementsInTransferRow = width * elementsInRowPerPixel;

            // pre-size the output stream to half of the maximum size
            ByteArrayOutputStream stream = new ByteArrayOutputStream(
                height * width * bytesPerPixel / 2);
            Deflater deflater = new Deflater(Filter.getCompressionLevel());
            DeflaterOutputStream zip = new DeflaterOutputStream(stream, deflater);

            int alphaPtr = 0;

            for (int rowNum = 0; rowNum < height; rowNum++)
            {
                image.getPixels(transferRow, 0, width, 0, rowNum, width, 1);

                // We start to write at index one, as the predictor marker is in index zero
                int writerPtr = 1;
                Arrays.fill(aValues, (byte) 0);
                Arrays.fill(cValues, (byte) 0);

                final int[] transferRowInt;
                final int[] prevRowInt;

                {
                    transferRowInt = (int[]) transferRow;
                    prevRowInt = (int[]) prevRow;
                }

                for (int indexInTransferRow = 0; indexInTransferRow < elementsInTransferRow;
                     indexInTransferRow += elementsInRowPerPixel, alphaPtr += bytesPerComponent)
                {
                    // Copy the pixel values into the byte array
                    {
                        copyIntToBytes(transferRowInt, indexInTransferRow, xValues, alphaImageData,
                            alphaPtr);
                        copyIntToBytes(prevRowInt, indexInTransferRow, bValues, null, 0);
                    }

                    // Encode the pixel values in the different encodings
                    int length = xValues.length;
                    for (int bytePtr = 0; bytePtr < length; bytePtr++)
                    {
                        int x = xValues[bytePtr] & 0xFF;
                        int a = aValues[bytePtr] & 0xFF;
                        int b = bValues[bytePtr] & 0xFF;
                        int c = cValues[bytePtr] & 0xFF;
                        dataRawRowNone[writerPtr] = (byte) x;
                        dataRawRowSub[writerPtr] = pngFilterSub(x, a);
                        dataRawRowUp[writerPtr] = pngFilterUp(x, b);
                        dataRawRowAverage[writerPtr] = pngFilterAverage(x, a, b);
                        dataRawRowPaeth[writerPtr] = pngFilterPaeth(x, a, b, c);
                        writerPtr++;
                    }

                    //  We shift the values into the prev / upper left values for the next pixel
                    System.arraycopy(xValues, 0, aValues, 0, bytesPerPixel);
                    System.arraycopy(bValues, 0, cValues, 0, bytesPerPixel);
                }

                byte[] rowToWrite = chooseDataRowToWrite();

                // Write and compress the row as long it is hot (CPU cache wise)
                zip.write(rowToWrite, 0, rowToWrite.length);

                // We swap prev and transfer row, so that we have the prev row for the next row.
                int[] temp = prevRow;
                prevRow = transferRow;
                transferRow = temp;
            }
            zip.close();
            deflater.end();

            return preparePredictorPDImage(stream, bytesPerComponent * 8);
        }

        private void copyIntToBytes(int[] transferRow, int indexInTranferRow, byte[] targetValues,
            byte[] alphaImageData, int alphaPtr)
        {
            int val = transferRow[indexInTranferRow];
            byte b0 = (byte) Color.blue(val);
            byte b1 = (byte) Color.green(val);
            byte b2 = (byte) Color.red(val);

            switch (imageType)
            {
                case ARGB_8888:
                    targetValues[0] = b2;
                    targetValues[1] = b1;
                    targetValues[2] = b0;
                    if (alphaImageData != null)
                    {
                        byte b3 = (byte) Color.alpha(val);
                        alphaImageData[alphaPtr] = b3;
                    }
                    break;
                case RGB_565:
                    targetValues[0] = b2;
                    targetValues[1] = b1;
                    targetValues[2] = b0;
                    break;
                default:
                    break;
            }
        }

//        private void copyImageBytes(byte[] transferRow, int indexInTranferRow, byte[] targetValues,
//            byte[] alphaImageData, int alphaPtr)

//        private static void copyShortsToBytes(short[] transferRow, int indexInTranferRow,
//            byte[] targetValues, byte[] alphaImageData, int alphaPtr)

        private PDImageXObject preparePredictorPDImage(ByteArrayOutputStream stream,
            int bitsPerComponent) throws IOException
        {
            int h = image.getHeight();
            int w = image.getWidth();

//            ColorSpace srcCspace = image.getColorModel().getColorSpace();
//            int srcCspaceType = srcCspace.getType();
//            PDColorSpace pdColorSpace = srcCspaceType == ColorSpace.TYPE_CMYK
//                ? PDDeviceCMYK.INSTANCE
//                : (srcCspaceType == ColorSpace.TYPE_GRAY
//                ? PDDeviceGray.INSTANCE : PDDeviceRGB.INSTANCE);
            PDColorSpace pdColorSpace = PDDeviceRGB.INSTANCE;

            // Encode the image profile if the image has one
//            if (srcCspace instanceof ICC_ColorSpace) TODO: PdfBox-Android

            PDImageXObject imageXObject = new PDImageXObject(document,
                new ByteArrayInputStream(stream.toByteArray()), COSName.FLATE_DECODE, w,
                h, bitsPerComponent, pdColorSpace);

            COSDictionary decodeParms = new COSDictionary();
            decodeParms.setItem(COSName.BITS_PER_COMPONENT, COSInteger.get(bitsPerComponent));
            decodeParms.setItem(COSName.PREDICTOR, COSInteger.get(15));
            decodeParms.setItem(COSName.COLUMNS, COSInteger.get(w));
            decodeParms.setItem(COSName.COLORS, COSInteger.get(3 /*srcCspace.getNumComponents()*/));
            imageXObject.getCOSObject().setItem(COSName.DECODE_PARMS, decodeParms);

            if (hasAlpha) {
                PDImageXObject pdMask = prepareImageXObject(document, alphaImageData,
                    image.getWidth(), image.getHeight(), 8 * bytesPerComponent, PDDeviceGray.INSTANCE);
                imageXObject.getCOSObject().setItem(COSName.SMASK, pdMask);
            }
            return imageXObject;
        }

        /**
         * We look which row encoding is the "best" one, ie. has the lowest sum. We don't implement
         * anything fancier to choose the right row encoding. This is just the recommend algorithm
         * in the spec. The get the perfect encoding you would need to do a brute force check how
         * all the different encoded rows compress in the zip stream together. You have would have
         * to check 5*image-height permutations...
         *
         * @return the "best" row encoding of the row encodings
         */
        private byte[] chooseDataRowToWrite()
        {
            byte[] rowToWrite = dataRawRowNone;
            long estCompressSum = estCompressSum(dataRawRowNone);
            long estCompressSumSub = estCompressSum(dataRawRowSub);
            long estCompressSumUp = estCompressSum(dataRawRowUp);
            long estCompressSumAvg = estCompressSum(dataRawRowAverage);
            long estCompressSumPaeth = estCompressSum(dataRawRowPaeth);
            if (estCompressSum > estCompressSumSub)
            {
                rowToWrite = dataRawRowSub;
                estCompressSum = estCompressSumSub;
            }
            if (estCompressSum > estCompressSumUp)
            {
                rowToWrite = dataRawRowUp;
                estCompressSum = estCompressSumUp;
            }
            if (estCompressSum > estCompressSumAvg)
            {
                rowToWrite = dataRawRowAverage;
                estCompressSum = estCompressSumAvg;
            }
            if (estCompressSum > estCompressSumPaeth)
            {
                rowToWrite = dataRawRowPaeth;
            }
            return rowToWrite;
        }

        /*
         * PNG Filters, see https://www.w3.org/TR/PNG-Filters.html
         */
        private static byte pngFilterSub(int x, int a)
        {
            return (byte) ((x & 0xFF) - (a & 0xFF));
        }

        private static byte pngFilterUp(int x, int b)
        {
            // Same as pngFilterSub, just called with the prior row
            return pngFilterSub(x, b);
        }

        private static byte pngFilterAverage(int x, int a, int b)
        {
            return (byte) (x - ((b + a) / 2));
        }

        private static byte pngFilterPaeth(int x, int a, int b, int c)
        {
            int p = a + b - c;
            int pa = Math.abs(p - a);
            int pb = Math.abs(p - b);
            int pc = Math.abs(p - c);
            final int pr;
            if (pa <= pb && pa <= pc)
            {
                pr = a;
            }
            else if (pb <= pc)
            {
                pr = b;
            }
            else
            {
                pr = c;
            }

            int r = x - pr;
            return (byte) (r);
        }

        private static long estCompressSum(byte[] dataRawRowSub)
        {
            long sum = 0;
            for (byte aDataRawRowSub : dataRawRowSub)
            {
                // https://www.w3.org/TR/PNG-Encoders.html#E.Filter-selection
                sum += Math.abs(aDataRawRowSub);
            }
            return sum;
        }
    }
}
