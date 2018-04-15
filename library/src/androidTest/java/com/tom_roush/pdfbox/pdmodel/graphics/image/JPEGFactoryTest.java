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
import android.support.test.InstrumentationRegistry;
import android.util.Log;

import com.tom_roush.pdfbox.pdmodel.PDDocument;
import com.tom_roush.pdfbox.pdmodel.graphics.color.PDDeviceGray;
import com.tom_roush.pdfbox.pdmodel.graphics.color.PDDeviceRGB;
import com.tom_roush.pdfbox.util.PDFBoxResourceLoader;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import static com.tom_roush.pdfbox.pdmodel.graphics.image.ValidateXImage.colorCount;
import static com.tom_roush.pdfbox.pdmodel.graphics.image.ValidateXImage.doWritePDF;
import static com.tom_roush.pdfbox.pdmodel.graphics.image.ValidateXImage.validate;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Unit tests for JPEGFactory
 *
 * @author Tilman Hausherr
 */
public class JPEGFactoryTest
{
    private File testResultsDir;

    Context testContext;

    @Before
    public void setUp() throws Exception
    {
        testContext = InstrumentationRegistry.getInstrumentation().getContext();
        PDFBoxResourceLoader.init(testContext);
        testResultsDir = new File(android.os.Environment.getExternalStorageDirectory() +
            "/Download/pdfbox-test-output/graphics/");
        testResultsDir.mkdirs();
    }

    /**
     * Tests JPEGFactory#createFromStream(PDDocument document, InputStream
     * stream) with color JPEG file
     */
    @Test
    public void testCreateFromStream() throws IOException
    {
        PDDocument document = new PDDocument();
        InputStream stream = testContext.getAssets().open(
            "pdfbox/com/tom_roush/pdfbox/pdmodel/graphics/image/jpeg.jpg");
        PDImageXObject ximage = JPEGFactory.createFromStream(document, stream);
        validate(ximage, 8, 344, 287, "jpg", PDDeviceRGB.INSTANCE.getName());

        doWritePDF(document, ximage, testResultsDir, "jpegrgbstream.pdf");
    }

    /**
     * Tests JPEGFactory#createFromStream(PDDocument document, InputStream
     * stream) with gray JPEG file
     */
    @Test
    public void testCreateFromStream256() throws IOException
    {
        PDDocument document = new PDDocument();
        InputStream stream = testContext.getAssets().open(
            "pdfbox/com/tom_roush/pdfbox/pdmodel/graphics/image/jpeg256.jpg");
        PDImageXObject ximage = JPEGFactory.createFromStream(document, stream);
        validate(ximage, 8, 344, 287, "jpg", PDDeviceGray.INSTANCE.getName());

        doWritePDF(document, ximage, testResultsDir, "jpeg256stream.pdf");
    }

    /**
     * Tests RGB JPEGFactory#createFromImage(PDDocument document, BufferedImage
     * image) with color JPEG image
     */
    @Test
    public void testCreateFromImageRGB() throws IOException
    {
        PDDocument document = new PDDocument();
        Bitmap image = BitmapFactory.decodeStream(testContext.getAssets().open(
            "pdfbox/com/tom_roush/pdfbox/pdmodel/graphics/image/jpeg.jpg"));
//        assertEquals(3, image.getColorModel().getNumComponents()); TODO: PdfBox-Android
        PDImageXObject ximage = JPEGFactory.createFromImage(document, image);
        validate(ximage, 8, 344, 287, "jpg", PDDeviceRGB.INSTANCE.getName());

        doWritePDF(document, ximage, testResultsDir, "jpegrgb.pdf");
    }

    /**
     * Tests RGB JPEGFactory#createFromImage(PDDocument document, BufferedImage
     * image) with gray JPEG image
     */
    @Test
    public void testCreateFromImage256() throws IOException
    {
        PDDocument document = new PDDocument();
        Bitmap image = BitmapFactory.decodeStream(testContext.getAssets().open(
            "pdfbox/com/tom_roush/pdfbox/pdmodel/graphics/image/jpeg256.jpg"));
//        assertEquals(1, image.getColorModel().getNumComponents()); TODO: PdfBox-Android
        Log.e("PdfBox-Android", image.getConfig().toString());
        PDImageXObject ximage = JPEGFactory.createFromImage(document, image);
        validate(ximage, 8, 344, 287, "jpg", PDDeviceGray.INSTANCE.getName());

        doWritePDF(document, ximage, testResultsDir, "jpeg256.pdf");
    }

    /**
     * Tests ARGB JPEGFactory#createFromImage(PDDocument document, BufferedImage
     * image)
     */
    @Test
    public void testCreateFromImageARGB_8888() throws IOException
    {
        // workaround Open JDK bug
        // http://bugs.java.com/bugdatabase/view_bug.do?bug_id=7044758
        if (System.getProperty("java.runtime.name").equals("OpenJDK Runtime Environment")
            && (System.getProperty("java.specification.version").equals("1.6")
            || System.getProperty("java.specification.version").equals("1.7")
            || System.getProperty("java.specification.version").equals("1.8")))
        {
            return;
        }

        PDDocument document = new PDDocument();
        Bitmap image = BitmapFactory.decodeStream(testContext.getAssets().open(
            "pdfbox/com/tom_roush/pdfbox/pdmodel/graphics/image/jpeg.jpg"));

        // create an ARGB image
        int width = image.getWidth();
        int height = image.getHeight();
        Bitmap argbImage = image.copy(Bitmap.Config.ARGB_8888, true);
        argbImage.setHasAlpha(true);

        int[] argbPixels = new int[width * height];
        argbImage.getPixels(argbPixels, 0, width, 0, 0, width, height);
        for (int x = 0; x < argbImage.getWidth(); ++x)
        {
            for (int y = 0; y < argbImage.getHeight(); ++y)
            {
                argbPixels[x + width * y] =
                    (argbPixels[x + width * y] & 0xFFFFFF) | ((y / 10 * 10) << 24);
            }
        }
        argbImage.setPixels(argbPixels, 0, width, 0, 0, width, height);

        PDImageXObject ximage = JPEGFactory.createFromImage(document, argbImage);
        validate(ximage, 8, width, height, "jpg",
            PDDeviceRGB.INSTANCE.getName());
        assertNotNull(ximage.getSoftMask());
        // TODO: PdfBox-Android should be "jpg", but we're using FLATE for the alpha
        validate(ximage.getSoftMask(), 8, width, height, "png",
            PDDeviceGray.INSTANCE.getName());
        assertTrue(colorCount(ximage.getSoftMask().getImage()) > image.getHeight() / 10);

        doWritePDF(document, ximage, testResultsDir, "jpeg-intargb.pdf");
    }

    /**
     * Tests ARGB JPEGFactory#createFromImage(PDDocument document, BufferedImage
     * image)
     */
    @Test
    public void testCreateFromImageARGB_4444() throws IOException
    {
        // workaround Open JDK bug
        // http://bugs.java.com/bugdatabase/view_bug.do?bug_id=7044758
        if (System.getProperty("java.runtime.name").equals("OpenJDK Runtime Environment")
            && (System.getProperty("java.specification.version").equals("1.6")
            || System.getProperty("java.specification.version").equals("1.7")
            || System.getProperty("java.specification.version").equals("1.8")))
        {
            return;
        }

        PDDocument document = new PDDocument();
        Bitmap image = BitmapFactory.decodeStream(testContext.getAssets().open(
            "pdfbox/com/tom_roush/pdfbox/pdmodel/graphics/image/jpeg.jpg"));

        // create an ARGB image
        int width = image.getWidth();
        int height = image.getHeight();
        Bitmap argbImage = image.copy(Bitmap.Config.ARGB_4444, true);
        argbImage.setHasAlpha(true);

        int[] argbPixels = new int[width * height];
        argbImage.getPixels(argbPixels, 0, width, 0, 0, width, height);
        for (int x = 0; x < argbImage.getWidth(); ++x)
        {
            for (int y = 0; y < argbImage.getHeight(); ++y)
            {
                argbPixels[x + width * y] =
                    (argbPixels[x + width * y] & 0xFFFFFF) | ((y / 10 * 10) << 24);
            }
        }
        argbImage.setPixels(argbPixels, 0, width, 0, 0, width, height);

        PDImageXObject ximage = JPEGFactory.createFromImage(document, argbImage);
        validate(ximage, 8, width, height, "jpg",
            PDDeviceRGB.INSTANCE.getName());
        assertNotNull(ximage.getSoftMask());
        // TODO: PdfBox-Android should be "jpg", but we're using FLATE for the alpha
        validate(ximage.getSoftMask(), 8, width, height, "png",
            PDDeviceGray.INSTANCE.getName());
        assertTrue(colorCount(ximage.getSoftMask().getImage()) >= 16);

        doWritePDF(document, ximage, testResultsDir, "jpeg-4bargb.pdf");
    }
}
