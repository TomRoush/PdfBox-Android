/*
 *  Copyright 2011 adam.
 *
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

package com.tom_roush.pdfbox.pdmodel.font;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import com.tom_roush.fontbox.ttf.TTFParser;
import com.tom_roush.fontbox.ttf.TrueTypeFont;
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader;
import com.tom_roush.pdfbox.pdmodel.PDDocument;
import com.tom_roush.pdfbox.pdmodel.PDPage;
import com.tom_roush.pdfbox.pdmodel.PDPageContentStream;
import com.tom_roush.pdfbox.pdmodel.common.PDRectangle;
import com.tom_roush.pdfbox.rendering.PDFRenderer;

import org.junit.Before;

import junit.framework.TestCase;

/**
 *
 * @author adam
 */
public class PDFontTest extends TestCase
{
    private Context testContext;

    @Before
    public void setUp() throws IOException
    {
        testContext = InstrumentationRegistry.getInstrumentation().getContext();
        PDFBoxResourceLoader.init(testContext);
    }

    /**
     * Test of the error reported in PDFBox-988
     */
    public void testPDFBox988() throws Exception
    {
        PDDocument doc = null;
        try
        {
            doc = PDDocument.load(testContext.getAssets()
                .open("pdfbox/com/tom_roush/pdfbox/pdmodel/font/F001u_3_7j.pdf"));
            PDFRenderer renderer = new PDFRenderer(doc);
            renderer.renderImage(0);
            // the allegation is that renderImage() will crash the JVM or hang
        }
        finally
        {
            if (doc != null)
            {
                doc.close();
            }
        }
    }

    /**
     * PDFBOX-3337: Test ability to reuse a TrueTypeFont for several PDFs to avoid parsing it over
     * and over again.
     *
     * @throws java.io.IOException
     */
    public void testPDFBox3337() throws IOException
    {
        InputStream ttfStream = testContext.getAssets()
            .open("com/tom_roush/pdfbox/resources/ttf/LiberationSans-Regular.ttf");
        final TrueTypeFont ttf = new TTFParser ().parse (ttfStream);

        for (int i = 0; i < 2; ++i)
        {
            PDDocument doc = new PDDocument();

            final PDPage page = new PDPage(PDRectangle.A4);
            doc.addPage(page);

            PDPageContentStream cs = new PDPageContentStream(doc, page);
            PDFont font = PDType0Font.load(doc, ttf, true);
            cs.setFont(font, 10);
            cs.beginText();
            cs.showText("PDFBOX");
            cs.endText();
            cs.close();
            doc.save(new ByteArrayOutputStream());
            doc.close();
        }
    }
}
