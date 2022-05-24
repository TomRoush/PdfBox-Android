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
package com.tom_roush.pdfbox.pdmodel.interactive.form;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.tom_roush.pdfbox.android.PDFBoxResourceLoader;
import com.tom_roush.pdfbox.cos.COSName;
import com.tom_roush.pdfbox.io.IOUtils;
import com.tom_roush.pdfbox.pdmodel.PDDocument;
import com.tom_roush.pdfbox.pdmodel.PDPage;
import com.tom_roush.pdfbox.pdmodel.interactive.annotation.PDAnnotation;
import com.tom_roush.pdfbox.pdmodel.interactive.annotation.PDAnnotationWidget;
import com.tom_roush.pdfbox.rendering.TestRendering;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Test for the PDButton class.
 *
 */
public class PDAcroFormInstrumentationTest
{

    private static File OUT_DIR;
    private static final File IN_DIR = new File("pdfbox/com/tom_roush/pdfbox/pdmodel/interactive/form");

    private Context testContext;

    @Before
    public void setUp()
    {
        testContext = InstrumentationRegistry.getInstrumentation().getContext();
        PDFBoxResourceLoader.init(testContext);

        OUT_DIR = new File(testContext.getCacheDir(), "pdfbox-test-output");
        OUT_DIR.mkdirs();
    }

    @Test
    public void testFlatten() throws IOException
    {
        PDDocument testPdf = PDDocument.load(testContext.getAssets().open(IN_DIR + "/" + "AlignmentTests.pdf"));
        testPdf.getDocumentCatalog().getAcroForm().flatten();
        assertTrue(testPdf.getDocumentCatalog().getAcroForm().getFields().isEmpty());
        File file = new File(OUT_DIR, "AlignmentTests-flattened.pdf");
        testPdf.save(file);
        // compare rendering
        TestRendering testRendering = new TestRendering();
        testRendering.setUp();
        testRendering.render(file);
    }

    /*
     * Same as above but remove the page reference from the widget annotation
     * before doing the flatten() to ensure that the widgets page reference is properly looked up
     * (PDFBOX-3301)
     */
    @Test
    public void testFlattenWidgetNoRef() throws IOException
    {
        PDDocument testPdf = PDDocument.load(testContext.getAssets().open(IN_DIR + "/" + "AlignmentTests.pdf"));
        PDAcroForm acroForm = testPdf.getDocumentCatalog().getAcroForm();
        for (PDField field : acroForm.getFieldTree()) {
            for (PDAnnotationWidget widget : field.getWidgets()) {
                widget.getCOSObject().removeItem(COSName.P);
            }
        }
        testPdf.getDocumentCatalog().getAcroForm().flatten();

        // 36 non widget annotations shall not be flattened
        assertEquals(36, testPdf.getPage(0).getAnnotations().size());

        assertTrue(testPdf.getDocumentCatalog().getAcroForm().getFields().isEmpty());
        File file = new File(OUT_DIR, "AlignmentTests-flattened-noRef.pdf");
        testPdf.save(file);
        // compare rendering
        TestRendering testRendering = new TestRendering();
        testRendering.setUp();
        testRendering.render(file);
    }

    @Test
    public void testFlattenSpecificFieldsOnly() throws IOException
    {
        File file = new File(OUT_DIR, "AlignmentTests-flattened-specificFields.pdf");

        List<PDField> fieldsToFlatten = new ArrayList<PDField>();

        PDDocument testPdf = null;
        try
        {
            testPdf = PDDocument.load(testContext.getAssets().open(IN_DIR + "/" + "AlignmentTests.pdf"));
            PDAcroForm acroFormToFlatten = testPdf.getDocumentCatalog().getAcroForm();
            int numFieldsBeforeFlatten = acroFormToFlatten.getFields().size();
            int numWidgetsBeforeFlatten = countWidgets(testPdf);

            fieldsToFlatten.add(acroFormToFlatten.getField("AlignLeft-Border_Small-Filled"));
            fieldsToFlatten.add(acroFormToFlatten.getField("AlignLeft-Border_Medium-Filled"));
            fieldsToFlatten.add(acroFormToFlatten.getField("AlignLeft-Border_Wide-Filled"));
            fieldsToFlatten.add(acroFormToFlatten.getField("AlignLeft-Border_Wide_Clipped-Filled"));

            acroFormToFlatten.flatten(fieldsToFlatten, true);
            int numFieldsAfterFlatten = acroFormToFlatten.getFields().size();
            int numWidgetsAfterFlatten = countWidgets(testPdf);

            assertEquals(numFieldsBeforeFlatten, numFieldsAfterFlatten + fieldsToFlatten.size());
            assertEquals(numWidgetsBeforeFlatten, numWidgetsAfterFlatten + fieldsToFlatten.size());

            testPdf.save(file);
        }
        finally
        {
            IOUtils.closeQuietly(testPdf);
        }
    }

    private int countWidgets(PDDocument documentToTest)
    {
        int count = 0;
        for (PDPage page : documentToTest.getPages())
        {
            try
            {
                for (PDAnnotation annotation : page.getAnnotations())
                {
                    if (annotation instanceof PDAnnotationWidget)
                    {
                        count ++;
                    }
                }
            }
            catch (IOException e)
            {
                // ignoring
            }
        }
        return count;
    }
}

