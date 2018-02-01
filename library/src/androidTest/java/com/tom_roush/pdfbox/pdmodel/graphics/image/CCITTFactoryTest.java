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
import android.support.test.InstrumentationRegistry;

import com.tom_roush.pdfbox.io.RandomAccessBuffer;
import com.tom_roush.pdfbox.pdmodel.PDDocument;
import com.tom_roush.pdfbox.pdmodel.PDPage;
import com.tom_roush.pdfbox.pdmodel.PDPageContentStream;
import com.tom_roush.pdfbox.pdmodel.common.PDRectangle;
import com.tom_roush.pdfbox.pdmodel.graphics.color.PDDeviceGray;
import com.tom_roush.pdfbox.util.PDFBoxResourceLoader;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static com.tom_roush.pdfbox.pdmodel.graphics.image.ValidateXImage.validate;

/**
 * Unit tests for CCITTFactory
 *
 * @author Tilman Hausherr
 */
public class CCITTFactoryTest extends TestCase
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
     * Tests CCITTFactory#createFromRandomAccess(PDDocument document,
     * RandomAccess reader) with a single page TIFF
     */
    @Test
    public void testCreateFromRandomAccessSingle() throws IOException
    {
        String tiffG3Path = "pdfbox/com/tom_roush/pdfbox/pdmodel/graphics/image/ccittg3.tif";
        String tiffG4Path = "pdfbox/com/tom_roush/pdfbox/pdmodel/graphics/image/ccittg4.tif";

        PDDocument document = new PDDocument();
        PDImageXObject ximage3 = CCITTFactory.createFromRandomAccess(document,
            new RandomAccessBuffer(testContext.getAssets().open(tiffG3Path)));
        validate(ximage3, 1, 344, 287, "tiff", PDDeviceGray.INSTANCE.getName());
//        Bitmap bim3 = BitmapFactory.decodeFile(tiffG3Path);
//        checkIdent(bim3, ximage3.getOpaqueImage());
        PDPage page = new PDPage(PDRectangle.A4);
        document.addPage(page);
        PDPageContentStream contentStream = new PDPageContentStream(document, page, true, false);
        contentStream.drawImage(ximage3, 0, 0, ximage3.getWidth(), ximage3.getHeight());
        contentStream.close();

        PDImageXObject ximage4 = CCITTFactory.createFromRandomAccess(document,
            new RandomAccessBuffer(testContext.getAssets().open(tiffG4Path)));
        validate(ximage4, 1, 344, 287, "tiff", PDDeviceGray.INSTANCE.getName());
//        Bitmap bim4 = BitmapFactory.decodeFile(tiffG4Path);
//        checkIdent(bim4, ximage4.getOpaqueImage());
        page = new PDPage(PDRectangle.A4);
        document.addPage(page);
        contentStream = new PDPageContentStream(document, page, true, false);
        contentStream.drawImage(ximage4, 0, 0);
        contentStream.close();

        document.save(testResultsDir + "/singletiff.pdf");
        document.close();

        document = PDDocument.load(new File(testResultsDir, "singletiff.pdf"), (String)null);
        assertEquals(2, document.getNumberOfPages());

        document.close();
    }

    /**
     * Tests CCITTFactory#createFromRandomAccess(PDDocument document,
     * RandomAccess reader) with a multi page TIFF
     */
    public void testCreateFromRandomAccessMulti() throws IOException
    {
        String tiffPath = "pdfbox/com/tom_roush/pdfbox/pdmodel/graphics/image/ccittg4multi.tif";

//        ImageInputStream is = ImageIO.createImageInputStream(new File(tiffPath));
//        ImageReader imageReader = ImageIO.getImageReaders(is).next();
//        imageReader.setInput(is);
//        int countTiffImages = imageReader.getNumImages(true);
//        assertTrue(countTiffImages > 1); TODO: PdfBox-Android

        PDDocument document = new PDDocument();

        int pdfPageNum = 0;
        while (true)
        {
            PDImageXObject ximage = CCITTFactory.createFromRandomAccess(document,
                new RandomAccessBuffer(testContext.getAssets().open(tiffPath)), pdfPageNum);
            if (ximage == null)
            {
                break;
            }
//            Bitmap bim = imageReader.read(pdfPageNum);
//            validate(ximage, 1, bim.getWidth(), bim.getHeight(), "tiff", PDDeviceGray.INSTANCE.getName());
//            checkIdent(bim, ximage.getOpaqueImage());
            PDPage page = new PDPage(PDRectangle.A4);
            float fX = ximage.getWidth() / page.getMediaBox().getWidth();
            float fY = ximage.getHeight() / page.getMediaBox().getHeight();
            float factor = Math.max(fX, fY);
            document.addPage(page);
            PDPageContentStream contentStream = new PDPageContentStream(document, page, true, false);
            contentStream.drawImage(ximage, 0, 0, ximage.getWidth() / factor, ximage.getHeight() / factor);
            contentStream.close();
            ++pdfPageNum;
        }

//        assertEquals(countTiffImages, pdfPageNum);

        document.save(testResultsDir + "/multitiff.pdf");
        document.close();

        document = PDDocument.load(new File(testResultsDir, "multitiff.pdf"), (String)null);
//        assertEquals(countTiffImages, document.getNumberOfPages());

        document.close();
//        imageReader.dispose();
    }
}
