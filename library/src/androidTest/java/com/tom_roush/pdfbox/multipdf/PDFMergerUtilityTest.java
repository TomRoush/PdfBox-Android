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
package com.tom_roush.pdfbox.multipdf;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.test.InstrumentationRegistry;

import com.tom_roush.pdfbox.pdmodel.PDDocument;
import com.tom_roush.pdfbox.rendering.PDFRenderer;
import com.tom_roush.pdfbox.util.PDFBoxResourceLoader;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertEquals;

/**
 * Test suite for PDFMergerUtility.
 *
 * @author Maruan Sahyoun (PDF files)
 * @author Tilman Hausherr (code)
 */
public class PDFMergerUtilityTest
{
    final String SRCDIR = "pdfbox/input/merge";
    String TARGETTESTDIR;
    final int DPI = 96;
    Context testContext;

    @Before
    public void setUp() throws Exception
    {
        testContext = InstrumentationRegistry.getInstrumentation().getContext();
        PDFBoxResourceLoader.init(testContext);
        TARGETTESTDIR = android.os.Environment.getExternalStorageDirectory() + "/Download/pdfbox-test-output/merge/";
        new File(TARGETTESTDIR).mkdirs();
        if (!new File(TARGETTESTDIR).exists())
        {
            throw new IOException("could not create output directory");
        }
    }

    /**
     * Tests whether the merge of two PDF files with identically named but
     * different global resources works. The two PDF files have two fonts each
     * named /TT1 and /TT0 that are Arial and Courier and vice versa in the
     * second file. Revisions before 1613017 fail this test because global
     * resources were merged which made trouble when resources of the same kind
     * had the same name.
     *
     * @throws IOException if something goes wrong.
     */
    @Test
    public void testPDFMergerUtility() throws IOException
    {
        checkMergeIdentical("PDFBox.GlobalResourceMergeTest.Doc01.decoded.pdf",
            "PDFBox.GlobalResourceMergeTest.Doc02.decoded.pdf",
            "GlobalResourceMergeTestResult.pdf",
            false);

        // once again, with scratch file
        checkMergeIdentical("PDFBox.GlobalResourceMergeTest.Doc01.decoded.pdf",
            "PDFBox.GlobalResourceMergeTest.Doc02.decoded.pdf",
            "GlobalResourceMergeTestResult2.pdf",
            true);
    }

    // see PDFBOX-2893
    @Test
    public void testPDFMergerUtility2() throws IOException
    {
        checkMergeIdentical("PDFBox.GlobalResourceMergeTest.Doc01.pdf",
            "PDFBox.GlobalResourceMergeTest.Doc02.pdf",
            "GlobalResourceMergeTestResult.pdf",
            false);

        // once again, with scratch file
        checkMergeIdentical("PDFBox.GlobalResourceMergeTest.Doc01.pdf",
            "PDFBox.GlobalResourceMergeTest.Doc02.pdf",
            "GlobalResourceMergeTestResult2.pdf",
            true);
    }

    // checks that the result file of a merge has the same rendering as the two
    // source files
    private void checkMergeIdentical(String filename1, String filename2, String mergeFilename,
        boolean useScratchFiles)
        throws IOException
    {
        PDDocument srcDoc1 = PDDocument.load(testContext.getAssets().open(SRCDIR + "/" + filename1), (String) null);
        int src1PageCount = srcDoc1.getNumberOfPages();
        PDFRenderer src1PdfRenderer = new PDFRenderer(srcDoc1);
        Bitmap[] src1ImageTab = new Bitmap[src1PageCount];
        for (int page = 0; page < src1PageCount; ++page)
        {
            src1ImageTab[page] = src1PdfRenderer.renderImageWithDPI(page, DPI);
        }
        srcDoc1.close();

        PDDocument srcDoc2 = PDDocument.load(testContext.getAssets().open(SRCDIR + "/" + filename2), (String) null);
        int src2PageCount = srcDoc2.getNumberOfPages();
        PDFRenderer src2PdfRenderer = new PDFRenderer(srcDoc2);
        Bitmap[] src2ImageTab = new Bitmap[src2PageCount];
        for (int page = 0; page < src2PageCount; ++page)
        {
            src2ImageTab[page] = src2PdfRenderer.renderImageWithDPI(page, DPI);
        }
        srcDoc2.close();

        PDFMergerUtility pdfMergerUtility = new PDFMergerUtility();
        pdfMergerUtility.addSource(testContext.getAssets().open(SRCDIR + "/" + filename1));
        pdfMergerUtility.addSource(testContext.getAssets().open(SRCDIR + "/" + filename2));
        pdfMergerUtility.setDestinationFileName(TARGETTESTDIR + mergeFilename);
        pdfMergerUtility.mergeDocuments(useScratchFiles);

        PDDocument mergedDoc
            = PDDocument.load(new File(TARGETTESTDIR, mergeFilename), (String) null);
        PDFRenderer mergePdfRenderer = new PDFRenderer(mergedDoc);
        int mergePageCount = mergedDoc.getNumberOfPages();
        assertEquals(src1PageCount + src2PageCount, mergePageCount);
        for (int page = 0; page < src1PageCount; ++page)
        {
            Bitmap bim = mergePdfRenderer.renderImageWithDPI(page, DPI);
            checkImagesIdentical(bim, src1ImageTab[page]);
        }
        for (int page = 0; page < src2PageCount; ++page)
        {
            int mergePage = page + src1PageCount;
            Bitmap bim = mergePdfRenderer.renderImageWithDPI(mergePage, DPI);
            checkImagesIdentical(bim, src2ImageTab[page]);
        }
        mergedDoc.close();
    }

    private void checkImagesIdentical(Bitmap bim1, Bitmap bim2)
    {
        assertEquals(bim1.getHeight(), bim2.getHeight());
        assertEquals(bim1.getWidth(), bim2.getWidth());
        int w = bim1.getWidth();
        int h = bim1.getHeight();

        int[] bim1Pixels = new int[w * h];
        bim1.getPixels(bim1Pixels, 0, w, 0, 0, w, h);
        int[] bim2Pixels = new int[w * h];
        bim2.getPixels(bim2Pixels, 0, w, 0, 0, w, h);
        for (int i = 0; i < w; ++i)
        {
            for (int j = 0; j < h; ++j)
            {
                assertEquals(bim1Pixels[i + w * j], bim2Pixels[i + w * j]);
            }
        }
    }
}
