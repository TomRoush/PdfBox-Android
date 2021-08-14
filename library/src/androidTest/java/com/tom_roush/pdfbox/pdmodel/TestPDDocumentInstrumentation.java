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
package com.tom_roush.pdfbox.pdmodel;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

import androidx.test.platform.app.InstrumentationRegistry;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import com.tom_roush.harmony.awt.AWTColor;
import com.tom_roush.pdfbox.pdmodel.font.PDType1Font;
import com.tom_roush.pdfbox.pdmodel.graphics.image.LosslessFactory;
import com.tom_roush.pdfbox.pdmodel.graphics.image.PDImageXObject;
import com.tom_roush.pdfbox.rendering.PDFRenderer;
import com.tom_roush.pdfbox.util.PDFBoxResourceLoader;

import org.junit.Assert;
import org.junit.Before;

import junit.framework.TestCase;

/**
 * Testcase introduced with PDFBOX-1581.
 *
 */
public class TestPDDocumentInstrumentation extends TestCase
{
    @Before
    public void setUp()
    {
        Context testContext = InstrumentationRegistry.getInstrumentation().getContext();
        PDFBoxResourceLoader.init(testContext);
    }

    /**
     * Test whether importPage does a deep copy (if not, the save would fail, see PDFBOX-3328)
     *
     * @throws java.io.IOException
     */
    public void testImportPage() throws IOException
    {
        PDDocument doc1 = new PDDocument();
        PDPage page = new PDPage();
        PDPageContentStream pageContentStream = new PDPageContentStream(doc1, page);
        Bitmap bim = Bitmap.createBitmap(100, 50, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bim);
//        Font font = new Font("Dialog", Font.PLAIN, 20);
        canvas.drawText("PDFBox", 10, 30, new Paint());
        PDImageXObject img = LosslessFactory.createFromImage(doc1, bim);
        pageContentStream.drawImage(img, 200, 500);
        pageContentStream.setFont(PDType1Font.HELVETICA, 20);
        pageContentStream.beginText();
        pageContentStream.setNonStrokingColor(AWTColor.blue);
        pageContentStream.newLineAtOffset(200, 600);
        pageContentStream.showText("PDFBox");
        pageContentStream.endText();
        pageContentStream.close();
        doc1.addPage(page);
        Bitmap bim1 = new PDFRenderer(doc1).renderImage(0);

        PDDocument doc2 = new PDDocument();
        doc2.importPage(doc1.getPage(0));
        doc1.close();
        Bitmap bim2 = new PDFRenderer(doc2).renderImage(0);
        doc2.save(new ByteArrayOutputStream());
        doc2.close();

        assertEquals(bim1.getWidth(), bim2.getWidth());
        assertEquals(bim1.getHeight(), bim2.getHeight());
        int w = bim1.getWidth();
        int h = bim1.getHeight();
        int[] pixels1 = new int[w * h];
        bim1.getPixels(pixels1, 0, w, 0, 0, w, h);
        int[] pixels2 = new int[w * h];
        bim2.getPixels(pixels2, 0, w, 0, 0, w, h);
        assertEquals(w * h, pixels1.length);
        Assert.assertArrayEquals(pixels1, pixels2);
    }
}
