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
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.test.InstrumentationRegistry;

import com.tom_roush.pdfbox.pdmodel.PDDocument;
import com.tom_roush.pdfbox.pdmodel.PDPage;
import com.tom_roush.pdfbox.pdmodel.PDPageContentStream;
import com.tom_roush.pdfbox.pdmodel.graphics.color.PDDeviceGray;
import com.tom_roush.pdfbox.pdmodel.graphics.color.PDDeviceRGB;
import com.tom_roush.pdfbox.rendering.PDFRenderer;
import com.tom_roush.pdfbox.util.PDFBoxResourceLoader;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static com.tom_roush.pdfbox.pdmodel.graphics.image.ValidateXImage.checkIdent;
import static com.tom_roush.pdfbox.pdmodel.graphics.image.ValidateXImage.colorCount;
import static com.tom_roush.pdfbox.pdmodel.graphics.image.ValidateXImage.doWritePDF;
import static com.tom_roush.pdfbox.pdmodel.graphics.image.ValidateXImage.validate;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

/**
 * Unit tests for LosslessFactory
 *
 * @author Tilman Hausherr
 */
public class LosslessFactoryTest
{
    private File testResultsDir;
    private Context testContext;

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
     * Tests RGB LosslessFactoryTest#createFromImage(PDDocument document,
     * BufferedImage image)
     *
     * @throws java.io.IOException
     */
    @Test
    public void testCreateLosslessFromImageRGB() throws IOException
    {
        PDDocument document = new PDDocument();
        Bitmap image = BitmapFactory.decodeStream(testContext.getAssets().open(
            "pdfbox/com/tom_roush/pdfbox/pdmodel/graphics/image/png.png"));

        PDImageXObject ximage1 = LosslessFactory.createFromImage(document, image);
        validate(ximage1, 8, image.getWidth(), image.getHeight(), "png",
            PDDeviceRGB.INSTANCE.getName());
        checkIdent(image, ximage1.getImage());

        // Create a grayscale image
        Bitmap grayImage = Bitmap.createBitmap(image.getWidth(), image.getHeight(),
            Bitmap.Config.ALPHA_8);
        Canvas canvas = new Canvas();
        canvas.setBitmap(grayImage);
        Paint paint = new Paint();
        canvas.drawBitmap(image, 0, 0, paint);
        PDImageXObject ximage2 = LosslessFactory.createFromImage(document, grayImage);
        validate(ximage2, 8, grayImage.getWidth(), grayImage.getHeight(), "png",
            PDDeviceGray.INSTANCE.getName());
        checkIdent(grayImage, ximage2.getImage());

        // Create a bitonal image
//        BufferedImage bitonalImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_BYTE_BINARY); TODO: PdfBox-Android

        // avoid multiple of 8 to test padding
//        assertFalse(bitonalImage.getWidth() % 8 == 0);

//        g = bitonalImage.getGraphics();
//        g.drawImage(image, 0, 0, null);
//        g.dispose();
//        PDImageXObject ximage3 = LosslessFactory.createFromImage(document, bitonalImage);
//        validate(ximage3, 1, bitonalImage.getWidth(), bitonalImage.getHeight(), "png", PDDeviceGray.INSTANCE.getName());
//        checkIdent(bitonalImage, ximage3.getImage());

        // This part isn't really needed because this test doesn't break
        // if the mask has the wrong colorspace (PDFBOX-2057), but it is still useful
        // if something goes wrong in the future and we want to have a PDF to open.
        PDPage page = new PDPage();
        document.addPage(page);
        PDPageContentStream contentStream = new PDPageContentStream(document, page, true, false);
        contentStream.drawImage(ximage1, 200, 300, ximage1.getWidth() / 2, ximage1.getHeight() / 2);
        contentStream.drawImage(ximage2, 200, 450, ximage2.getWidth() / 2, ximage2.getHeight() / 2);
//        contentStream.drawImage(ximage3, 200, 600, ximage3.getWidth() / 2, ximage3.getHeight() / 2);
        contentStream.close();

        File pdfFile = new File(testResultsDir, "misc.pdf");
        document.save(pdfFile);
        document.close();

        document = PDDocument.load(pdfFile, (String) null);
        new PDFRenderer(document).renderImage(0);
        document.close();
    }

    /**
     * Tests INT_ARGB LosslessFactoryTest#createFromImage(PDDocument document,
     * BufferedImage image)
     *
     * @throws java.io.IOException
     */
    @Test
    public void testCreateLosslessFromImageINT_ARGB() throws IOException
    {
        PDDocument document = new PDDocument();
        Bitmap image = BitmapFactory.decodeStream(testContext.getAssets().open(
            "pdfbox/com/tom_roush/pdfbox/pdmodel/graphics/image/png.png"));

        // create an ARGB image
        int w = image.getWidth();
        int h = image.getHeight();
        Bitmap argbImage = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas();
        canvas.setBitmap(argbImage);
        Paint paint = new Paint();
        canvas.drawBitmap(image, 0, 0, paint);

        int[] argbPixels = new int[w * h];
        argbImage.getPixels(argbPixels, 0, w, 0, 0, w, h);
        for (int x = 0; x < argbImage.getWidth(); ++x)
        {
            for (int y = 0; y < argbImage.getHeight(); ++y)
            {
                argbPixels[x + w * y] = (argbPixels[x + w * y] & 0xFFFFFF) | ((y / 10 * 10) << 24);
            }
        }
        argbImage.setPixels(argbPixels, 0, w, 0, 0, w, h);

        PDImageXObject ximage = LosslessFactory.createFromImage(document, argbImage);
        validate(ximage, 8, argbImage.getWidth(), argbImage.getHeight(), "png",
            PDDeviceRGB.INSTANCE.getName());
        checkIdent(argbImage, ximage.getImage());
        checkIdentRGB(argbImage, ximage.getOpaqueImage());

        assertNotNull(ximage.getSoftMask());
        validate(ximage.getSoftMask(), 8, argbImage.getWidth(), argbImage.getHeight(), "png",
            PDDeviceGray.INSTANCE.getName());
        assertTrue(colorCount(ximage.getSoftMask().getImage()) > image.getHeight() / 10);

        doWritePDF(document, ximage, testResultsDir, "intargb.pdf");
    }

    // testCreateLosslessFromImageBITMASK_INT_ARGB: Android does not have bitmask transparency

    // testCreateLosslessFromImageBITMASK4BYTE_ABGR: Android does not have bitmask transparency

    /**
     * Tests 4BYTE_ABGR LosslessFactoryTest#createFromImage(PDDocument document,
     * BufferedImage image)
     *
     * @throws java.io.IOException
     */
    @Test
    public void testCreateLosslessFromImage4BYTE_ABGR() throws IOException
    {
        PDDocument document = new PDDocument();
        Bitmap image = BitmapFactory.decodeStream(testContext.getAssets().open(
            "pdfbox/com/tom_roush/pdfbox/pdmodel/graphics/image/png.png"));

        // create an ARGB image
        int w = image.getWidth();
        int h = image.getHeight();
        Bitmap argbImage = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_4444);
        Canvas canvas = new Canvas();
        canvas.setBitmap(argbImage);
        Paint paint = new Paint();
        canvas.drawBitmap(image, 0, 0, paint);

        int[] argbPixels = new int[w * h];
        argbImage.getPixels(argbPixels, 0, w, 0, 0, w, h);
        for (int x = 0; x < argbImage.getWidth(); ++x)
        {
            for (int y = 0; y < argbImage.getHeight(); ++y)
            {
                argbPixels[x + w * y] = (argbPixels[x + w * y] & 0xFFFFFF) | ((y / 10 * 10) << 24);
            }
        }

        argbImage.setPixels(argbPixels, 0, w, 0, 0, w, h);

        PDImageXObject ximage = LosslessFactory.createFromImage(document, argbImage);

        validate(ximage, 8, w, h, "png", PDDeviceRGB.INSTANCE.getName());
        checkIdent(argbImage, ximage.getImage());
        checkIdentRGB(argbImage, ximage.getOpaqueImage());

        assertNotNull(ximage.getSoftMask());
        validate(ximage.getSoftMask(), 8, w, h, "png", PDDeviceGray.INSTANCE.getName());
        assertTrue(colorCount(ximage.getSoftMask().getImage()) > image.getHeight() / 10);

        doWritePDF(document, ximage, testResultsDir, "4babgr.pdf");
    }

    // TODO: PdfBox-Android : testCreateLosslessFromTransparentGIF: GIF images not currently supported

    /**
     * Check whether the RGB part of images are identical.
     *
     * @param expectedImage
     * @param actualImage
     */
    private void checkIdentRGB(Bitmap expectedImage, Bitmap actualImage)
    {
        String errMsg = "";

        int w = expectedImage.getWidth();
        int h = expectedImage.getHeight();
        assertEquals(w, actualImage.getWidth());
        assertEquals(h, actualImage.getHeight());

        int[] expectedPixels = new int[w * h];
        expectedImage.getPixels(expectedPixels, 0, w, 0, 0, w, h);
        int[] actualPixels = new int[w * h];
        actualImage.getPixels(actualPixels, 0, w, 0, 0, w, h);

        for (int y = 0; y < h; ++y)
        {
            for (int x = 0; x < w; ++x)
            {
                int idx = x + w * y;
                if ((expectedPixels[idx] & 0xFFFFFF) != (actualPixels[idx] &
                    0xFFFFFF))
                {
                    errMsg = String.format("(%d,%d) %06X != %06X", x, y,
                        expectedPixels[idx] & 0xFFFFFF,
                        actualPixels[idx] & 0xFFFFFF);
                }
                assertEquals(errMsg, expectedPixels[idx] & 0xFFFFFF,
                    actualPixels[idx] & 0xFFFFFF);
            }
        }
    }

    // doBitmaskTransparencyTest: Android does not have bitmask transparency
}
