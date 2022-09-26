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

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;

import androidx.test.platform.app.InstrumentationRegistry;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import com.tom_roush.pdfbox.android.PDFBoxResourceLoader;
import com.tom_roush.pdfbox.cos.COSArray;
import com.tom_roush.pdfbox.cos.COSDictionary;
import com.tom_roush.pdfbox.cos.COSInteger;
import com.tom_roush.pdfbox.cos.COSName;
import com.tom_roush.pdfbox.pdmodel.PDDocument;
import com.tom_roush.pdfbox.pdmodel.PDPage;
import com.tom_roush.pdfbox.pdmodel.PDPageContentStream;
import com.tom_roush.pdfbox.pdmodel.graphics.color.PDDeviceGray;
import com.tom_roush.pdfbox.rendering.PDFRenderer;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Unit tests for PDInlineImage
 *
 * @author Tilman Hausherr
 */
public class PDInlineImageTest
{
    private File testResultsDir;
    private Context testContext;

    @Before
    public void setUp()
    {
        testContext = InstrumentationRegistry.getInstrumentation().getContext();
        PDFBoxResourceLoader.init(testContext);
        testResultsDir = new File(testContext.getCacheDir(), "pdfbox-test-output/graphics/");
        testResultsDir.mkdirs();
    }

    /**
     * Tests PDInlineImage#PDInlineImage(COSDictionary parameters, byte[] data,
     * Map<String, PDColorSpace> colorSpaces)
     */
    @Test
    public void testInlineImage() throws IOException
    {
        COSDictionary dict = new COSDictionary();
        dict.setBoolean(COSName.IM, true);
        int width = 31;
        int height = 27;
        dict.setInt(COSName.W, width);
        dict.setInt(COSName.H, height);
        dict.setInt(COSName.BPC, 1);
        int rowbytes = width / 8;
        if (rowbytes * 8 < width)
        {
            // PDF spec:
            // If the number of data bits per row is not a multiple of 8,
            // the end of the row is padded with extra bits to fill out the last byte.
            ++rowbytes;
        }

        // draw a grid
        int datalen = rowbytes * height;
        byte[] data = new byte[datalen];
        for (int i = 0; i < datalen; ++i)
        {
            data[i] = (i / 4 % 2 == 0) ? (byte) Integer.parseInt("10101010", 2) : 0;
        }

        PDInlineImage inlineImage1 = new PDInlineImage(dict, data, null);
        assertTrue(inlineImage1.isStencil());
        assertEquals(width, inlineImage1.getWidth());
        assertEquals(height, inlineImage1.getHeight());
        assertEquals(1, inlineImage1.getBitsPerComponent());

        COSDictionary dict2 = new COSDictionary();
        dict2.addAll(dict);
        // use decode array to revert in image2
        COSArray decodeArray = new COSArray();
        decodeArray.add(COSInteger.ONE);
        decodeArray.add(COSInteger.ZERO);
        dict2.setItem(COSName.DECODE, decodeArray);

        PDInlineImage inlineImage2 = new PDInlineImage(dict2, data, null);

        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        Bitmap stencilImage = inlineImage1.getStencilImage(paint);
        assertEquals(width, stencilImage.getWidth());
        assertEquals(height, stencilImage.getHeight());

        Bitmap stencilImage2 = inlineImage2.getStencilImage(paint);
        assertEquals(width, stencilImage2.getWidth());
        assertEquals(height, stencilImage2.getHeight());

        Bitmap image1 = inlineImage1.getImage();
        assertEquals(width, image1.getWidth());
        assertEquals(height, image1.getHeight());

        Bitmap image2 = inlineImage2.getImage();
        assertEquals(width, image2.getWidth());
        assertEquals(height, image2.getHeight());

        // write and read
        boolean writeOk = image1.compress(Bitmap.CompressFormat.PNG, 100,
            new FileOutputStream(testResultsDir.getPath() + "/inline-grid1.png"));
        assertTrue(writeOk);
        Bitmap bim1 = BitmapFactory.decodeFile(
            new File(testResultsDir + "/inline-grid1.png").getPath());
        assertNotNull(bim1);
        assertEquals(width, bim1.getWidth());
        assertEquals(height, bim1.getHeight());

        writeOk = image2.compress(Bitmap.CompressFormat.PNG, 100,
            new FileOutputStream(testResultsDir.getPath() + "/inline-grid2.png"));
        assertTrue(writeOk);
        Bitmap bim2 = BitmapFactory.decodeFile(
            new File(testResultsDir + "/inline-grid2.png").getPath());
        assertNotNull(bim2);
        assertEquals(width, bim2.getWidth());
        assertEquals(height, bim2.getHeight());


        // compare: pixels with even coordinates are white (FF), all others are black (0)
        int[] bimPixels = new int[width * height];
        bim1.getPixels(bimPixels, 0, width, 0, 0, width, height);
        for (int x = 0; x < width; ++x)
        {
            for (int y = 0; y < height; ++y)
            {
                if (x % 2 == 0 && y % 2 == 0)
                {
                    assertEquals(0xFFFFFF, bimPixels[x + width * y] & 0xFFFFFF);
                }
                else
                {
                    assertEquals(0, bimPixels[x + width * y] & 0xFFFFFF);
                }
            }
        }

        // compare: pixels with odd coordinates are white (FF), all others are black (0)
        bim2.getPixels(bimPixels, 0, width, 0, 0, width, height);
        for (int x = 0; x < width; ++x)
        {
            for (int y = 0; y < height; ++y)
            {
                if (x % 2 == 0 && y % 2 == 0)
                {
                    assertEquals(0, bimPixels[x + width * y] & 0xFFFFFF);
                }
                else
                {
                    assertEquals(0xFFFFFF, bimPixels[x + width * y] & 0xFFFFFF);
                }
            }
        }

        PDDocument document = new PDDocument();
        PDPage page = new PDPage();
        document.addPage(page);
        PDPageContentStream contentStream = new PDPageContentStream(document, page,
            PDPageContentStream.AppendMode.APPEND, false);
        contentStream.drawImage(inlineImage1, 150, 400);
        contentStream.drawImage(inlineImage1, 150, 500, inlineImage1.getWidth() * 2,
            inlineImage1.getHeight() * 2);
        contentStream.drawImage(inlineImage1, 150, 600, inlineImage1.getWidth() * 4,
            inlineImage1.getHeight() * 4);
        contentStream.drawImage(inlineImage2, 350, 400);
        contentStream.drawImage(inlineImage2, 350, 500, inlineImage2.getWidth() * 2,
            inlineImage2.getHeight() * 2);
        contentStream.drawImage(inlineImage2, 350, 600, inlineImage2.getWidth() * 4,
            inlineImage2.getHeight() * 4);
        contentStream.close();

        File pdfFile = new File(testResultsDir, "inline.pdf");
        document.save(pdfFile);
        document.close();

        document = PDDocument.load(pdfFile, (String) null);
        new PDFRenderer(document).renderImage(0);
        document.close();
    }

    // 3 Tests for PDFBOX-5360 with very small images (the last one based on a comment
    // by Oliver Schmidtmer at the end of PDFBOX-5340). All images are fully black bars.

    @Test
    public void testShortCCITT1() throws IOException
    {
        byte ba[] = new byte [] { 8, 0x10, 0x20, 0x40, (byte) 0x81, 2, 4, 8, 0x10, 0, 0x40, 4, 0, 0x40, 4, 0, 0x40, 4 };
        doInlineCcittImage(23, 10, ba);
    }

    @Test
    public void testShortCCITT2() throws IOException
    {
        byte ba[] = new byte [] { 8, 0x10, 0x20, 0x40, (byte) 0x81, 2, 0, 8, 0, (byte) 0x80, 8, 8, (byte) 0x80, 8, 0, (byte) 0x80};
        doInlineCcittImage(23, 7, ba);
    }

    @Test
    public void testShortCCITT3() throws IOException
    {
        byte ba[] = new byte [] { 103, 44, 103, 44, 103, 44, 103, 44, 0, 16, 1, 0, 16, 1, 0, 16, 1, 10};
        doInlineCcittImage(683, 4, ba);
    }

    private void doInlineCcittImage(int width, int height, byte[] ba) throws IOException
    {
        COSDictionary dict = new COSDictionary();
        dict.setInt(COSName.W, width);
        dict.setInt(COSName.H, height);
        dict.setInt(COSName.BPC, 1);
        COSArray array = new COSArray();
        array.add(COSInteger.ONE);
        array.add(COSInteger.ZERO);
        dict.setItem(COSName.D, array);
        dict.setBoolean(COSName.IM, true);
        dict.setItem(COSName.F, COSName.CCITTFAX_DECODE_ABBREVIATION);
        COSDictionary dict2 = new COSDictionary();
        dict2.setInt(COSName.COLUMNS, dict.getInt(COSName.W));
        dict.setItem(COSName.DP, dict2);
        PDInlineImage inlineImage = new PDInlineImage(dict, ba, null);
        assertEquals(true, inlineImage.isStencil());
        assertEquals(false, inlineImage.isEmpty());
        assertEquals(false, inlineImage.getInterpolate());
        assertEquals(dict, inlineImage.getCOSObject());
        assertEquals(PDDeviceGray.INSTANCE, inlineImage.getColorSpace());
        assertEquals(1, inlineImage.getBitsPerComponent());
        assertEquals("tiff", inlineImage.getSuffix());
        Bitmap bim = inlineImage.getImage();
        assertEquals(width, bim.getWidth());
        assertEquals(height, bim.getHeight());
        assertEquals(inlineImage.getWidth(), bim.getWidth());
        assertEquals(inlineImage.getHeight(), bim.getHeight());
        assertEquals(Bitmap.Config.ARGB_8888, bim.getConfig());
        ByteBuffer dbb = ByteBuffer.allocate(bim.getRowBytes() * height);
        bim.copyPixelsToBuffer(dbb);
        byte[] data = dbb.array();
        assertEquals(bim.getWidth() * bim.getHeight(), data.length / 4);
        for (int i = 0; i < data.length; ++i)
        {
            // Should be all 0, but this is represented by 4 bytes, not 1
            assertEquals((i - 3) % 4 == 0 ? -1 : 0, data[i]);
        }
    }
}

