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
import android.support.test.InstrumentationRegistry;

import com.tom_roush.pdfbox.pdmodel.PDDocument;
import com.tom_roush.pdfbox.rendering.PDFRenderer;
import com.tom_roush.pdfbox.util.PDFBoxResourceLoader;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

/**
 * @author adam
 */
public class PDFontTest
{
    Context testContext;

    @Before
    public void setUp() throws IOException
    {
        testContext = InstrumentationRegistry.getInstrumentation().getContext();
        PDFBoxResourceLoader.init(testContext);
    }

    /**
     * Test of the error reported in PDFBox-988
     */
    @Test
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
}
