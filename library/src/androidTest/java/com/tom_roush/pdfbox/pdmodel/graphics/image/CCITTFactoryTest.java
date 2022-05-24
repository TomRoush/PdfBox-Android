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

import androidx.test.platform.app.InstrumentationRegistry;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

import com.tom_roush.pdfbox.android.PDFBoxResourceLoader;
import com.tom_roush.pdfbox.io.IOUtils;
import com.tom_roush.pdfbox.io.RandomAccessBuffer;
import com.tom_roush.pdfbox.pdmodel.PDDocument;
import com.tom_roush.pdfbox.pdmodel.PDPage;
import com.tom_roush.pdfbox.pdmodel.PDPageContentStream;
import com.tom_roush.pdfbox.pdmodel.PDPageContentStream.AppendMode;
import com.tom_roush.pdfbox.pdmodel.common.PDRectangle;
import com.tom_roush.pdfbox.pdmodel.graphics.color.PDDeviceGray;

import org.junit.Assert;

import junit.framework.TestCase;

import static com.tom_roush.pdfbox.pdmodel.graphics.image.ValidateXImage.validate;

/**
 * Unit tests for CCITTFactory
 *
 * @author Tilman Hausherr
 */
public class CCITTFactoryTest extends TestCase
{
    private File testResultsDir;
    private Context testContext;

    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
        testContext = InstrumentationRegistry.getInstrumentation().getContext();
        PDFBoxResourceLoader.init(testContext);
        testResultsDir = new File(testContext.getCacheDir(), "pdfbox-test-output/graphics/");
        testResultsDir.mkdirs();
    }

    /**
     * Tests CCITTFactory#createFromRandomAccess(PDDocument document,
     * RandomAccess reader) with a single page TIFF
     */
    public void testCreateFromRandomAccessSingle() throws IOException
    {
        String tiffG3Path = "pdfbox/com/tom_roush/pdfbox/pdmodel/graphics/image/ccittg3.tif";
        String tiffG4Path = "pdfbox/com/tom_roush/pdfbox/pdmodel/graphics/image/ccittg4.tif";

        PDDocument document = new PDDocument();
        PDImageXObject ximage3 = CCITTFactory.createFromRandomAccess(document,
            new RandomAccessBuffer(testContext.getAssets().open(tiffG3Path)));
        validate(ximage3, 1, 344, 287, "tiff", PDDeviceGray.INSTANCE.getName());
//        Bitmap bim3 = ImageIO.read(new File(tiffG3Path));
//        checkIdent(bim3, ximage3.getOpaqueImage());
        PDPage page = new PDPage(PDRectangle.A4);
        document.addPage(page);
        PDPageContentStream contentStream = new PDPageContentStream(document, page, AppendMode.APPEND, false);
        contentStream.drawImage(ximage3, 0, 0, ximage3.getWidth(), ximage3.getHeight());
        contentStream.close();

        PDImageXObject ximage4 = CCITTFactory.createFromRandomAccess(document,
            new RandomAccessBuffer(testContext.getAssets().open(tiffG4Path)));
        validate(ximage4, 1, 344, 287, "tiff", PDDeviceGray.INSTANCE.getName());
//        Bitmap bim4 = ImageIO.read(new File(tiffG3Path));
//        checkIdent(bim4, ximage4.getOpaqueImage());
        page = new PDPage(PDRectangle.A4);
        document.addPage(page);
        contentStream = new PDPageContentStream(document, page, AppendMode.APPEND, false);
        contentStream.drawImage(ximage4, 0, 0);
        contentStream.close();

        document.save(testResultsDir + "/singletiff.pdf");
        document.close();

        document = PDDocument.load(new File(testResultsDir, "singletiff.pdf"));
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
            PDPageContentStream contentStream = new PDPageContentStream(document, page, AppendMode.APPEND, false);
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

    public void testCreateFromBitmap() throws IOException
    {
        String tiffG4Path = "pdfbox/com/tom_roush/pdfbox/pdmodel/graphics/image/ccittg4.tif";

        PDDocument document = new PDDocument();
//        Bitmap bim = ImageIO.read(new File(tiffG4Path));
//        PDImageXObject ximage3 = CCITTFactory.createFromImage(document, bim);
//        validate(ximage3, 1, 344, 287, "tiff", PDDeviceGray.INSTANCE.getName());
//        checkIdent(bim, ximage3.getOpaqueImage());

        PDPage page = new PDPage(PDRectangle.A4);
        document.addPage(page);
        PDPageContentStream contentStream = new PDPageContentStream(document, page, AppendMode.APPEND, false);
//        contentStream.drawImage(ximage3, 0, 0, ximage3.getWidth(), ximage3.getHeight());
        contentStream.close();

        document.save(testResultsDir + "/singletifffrombi.pdf");
        document.close();

        document = PDDocument.load(new File(testResultsDir, "singletifffrombi.pdf"));
        assertEquals(1, document.getNumberOfPages());

        document.close();
    }

    public void testCreateFromBufferedChessImage() throws IOException
    {
        PDDocument document = new PDDocument();
        Bitmap bim = Bitmap.createBitmap(343, 287, Bitmap.Config.ALPHA_8);
        Assert.assertNotEquals((bim.getWidth() / 8) * 8, bim.getWidth()); // not mult of 8
        int col = 0;
        for (int x = 0; x < bim.getWidth(); ++x)
        {
            for (int y = 0; y < bim.getHeight(); ++y)
            {
                bim.setPixel(x, y, col & 0xFFFFFF);
                col = ~col;
            }
        }

        PDImageXObject ximage3 = CCITTFactory.createFromImage(document, bim);
        validate(ximage3, 1, 343, 287, "tiff", PDDeviceGray.INSTANCE.getName());
//        checkIdent(bim, ximage3.getOpaqueImage()); TODO: PdfBox-Android

        PDPage page = new PDPage(PDRectangle.A4);
        document.addPage(page);
        PDPageContentStream contentStream = new PDPageContentStream(document, page, AppendMode.APPEND, false);
        contentStream.drawImage(ximage3, 0, 0, ximage3.getWidth(), ximage3.getHeight());
        contentStream.close();

        document.save(testResultsDir + "/singletifffromchessbi.pdf");
        document.close();

        document = PDDocument.load(new File(testResultsDir, "singletifffromchessbi.pdf"));
        assertEquals(1, document.getNumberOfPages());

        document.close();
    }

    /**
     * Tests that CCITTFactory#createFromFile(PDDocument document, File file) doesn't lock the
     * source file
     */
    public void testCreateFromFileLock() throws IOException
    {
        // copy the source file to a temp directory, as we will be deleting it
        String tiffG3Path = "pdfbox/com/tom_roush/pdfbox/pdmodel/graphics/image/ccittg3.tif";
        InputStream sourceTiff = testContext.getAssets().open(tiffG3Path);
        File copiedTiffFile = new File(testResultsDir, "ccittg3.tif");
        copyFile(sourceTiff, copiedTiffFile);
        PDDocument document = new PDDocument();
        CCITTFactory.createFromFile(document, copiedTiffFile);
        assertTrue(copiedTiffFile.delete());
    }

    /**
     * Tests that CCITTFactory#createFromFile(PDDocument document, File file, int number) doesn't
     * lock the source file
     */
    public void testCreateFromFileNumberLock() throws IOException
    {
        // copy the source file to a temp directory, as we will be deleting it
        String tiffG3Path = "pdfbox/com/tom_roush/pdfbox/pdmodel/graphics/image/ccittg3.tif";
        InputStream sourceTiff = testContext.getAssets().open(tiffG3Path);
        File copiedTiffFile = new File(testResultsDir, "ccittg3n.tif");
        copyFile(sourceTiff, copiedTiffFile);
        PDDocument document = new PDDocument();
        CCITTFactory.createFromFile(document, copiedTiffFile, 0);
        assertTrue(copiedTiffFile.delete());
    }

    private void copyFile(InputStream source, File dest) throws IOException
    {
        InputStream is = null;
        OutputStream os = null;
        try
        {
            is = source;
            os = new FileOutputStream(dest);
            IOUtils.copy(is, os);
        }
        finally
        {
            is.close();
            os.close();
        }
    }

    /**
     * Tests that byte/short tag values are read correctly (ignoring possible garbage in remaining
     * bytes).
     */
    public void testByteShortPaddedWithGarbage() throws IOException
    {
        PDDocument document = new PDDocument();
        String basePath = "pdfbox/com/tom_roush/pdfbox/pdmodel/graphics/image/ccittg3-garbage-padded-fields";
        for (String ext : Arrays.asList(".tif", "-bigendian.tif"))
        {
            String tiffPath = basePath + ext;
            InputStream sourceTiff = testContext.getAssets().open(tiffPath);
            File copiedTiffFile = new File(testContext.getCacheDir(), "ccittg3-ccittg3-garbage-padded-fields" + ext);
            copyFile(sourceTiff, copiedTiffFile);

            PDImageXObject ximage3 = CCITTFactory.createFromFile(document, copiedTiffFile);
            validate(ximage3, 1, 344, 287, "tiff", PDDeviceGray.INSTANCE.getName());
        }
        document.close();
    }
}
