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

import com.tom_roush.pdfbox.cos.COSName;
import com.tom_roush.pdfbox.pdmodel.PDDocument;
import com.tom_roush.pdfbox.pdmodel.interactive.annotation.PDAnnotationWidget;
import com.tom_roush.pdfbox.rendering.TestRendering;
import com.tom_roush.pdfbox.util.PDFBoxResourceLoader;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Test for the PDButton class.
 */
public class PDAcroFormTest
{

    private PDDocument document;
    private PDAcroForm acroForm;

    private static File OUT_DIR;
    private static final String IN_DIR = "pdfbox/com/tom_roush/pdfbox/pdmodel/interactive/form";

    Context testContext;

    @Before
    public void setUp()
    {
        testContext = InstrumentationRegistry.getInstrumentation().getContext();
        PDFBoxResourceLoader.init(testContext);

        document = new PDDocument();
        acroForm = new PDAcroForm(document);
        document.getDocumentCatalog().setAcroForm(acroForm);

        OUT_DIR = new File(testContext.getCacheDir(), "pdfbox-test-output");
        OUT_DIR.mkdirs();
    }

    @Test
    public void testFieldsEntry()
    {
        // the /Fields entry has been created with the AcroForm
        // as this is a required entry
        assertNotNull(acroForm.getFields());
        assertEquals(acroForm.getFields().size(), 0);

        // there shouldn't be an exception if there is no such field
        assertNull(acroForm.getField("foo"));

        // remove the required entry which is the case for some
        // PDFs (see PDFBOX-2965)
        acroForm.getCOSObject().removeItem(COSName.FIELDS);

        // ensure there is always an empty collection returned
        assertNotNull(acroForm.getFields());
        assertEquals(acroForm.getFields().size(), 0);

        // there shouldn't be an exception if there is no such field
        assertNull(acroForm.getField("foo"));
    }

    @Test
    public void testAcroFormProperties()
    {
        assertTrue(acroForm.getDefaultAppearance().isEmpty());
        acroForm.setDefaultAppearance("/Helv 0 Tf 0 g");
        assertEquals(acroForm.getDefaultAppearance(), "/Helv 0 Tf 0 g");
    }

    @Test
    public void testFlatten() throws IOException
    {
        PDDocument testPdf = PDDocument.load(
            testContext.getAssets().open(IN_DIR + "/" + "AlignmentTests.pdf"));
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
        PDDocument testPdf = PDDocument.load(
            testContext.getAssets().open(IN_DIR + "/" + "AlignmentTests.pdf"));
        PDAcroForm acroForm = testPdf.getDocumentCatalog().getAcroForm();
        for (PDField field : acroForm.getFieldTree()) {
            for (PDAnnotationWidget widget : field.getWidgets()) {
                widget.getCOSObject().removeItem(COSName.P);
            }
        }
        testPdf.getDocumentCatalog().getAcroForm().flatten();
        assertTrue(testPdf.getDocumentCatalog().getAcroForm().getFields().isEmpty());
        File file = new File(OUT_DIR, "AlignmentTests-flattened-noRef.pdf");
        testPdf.save(file);
        // compare rendering
        TestRendering testRendering = new TestRendering();
        testRendering.setUp();
        testRendering.render(file);
    }

    @After
    public void tearDown() throws IOException
    {
        document.close();
    }

}

