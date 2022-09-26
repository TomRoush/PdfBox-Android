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

import com.tom_roush.pdfbox.android.PDFBoxResourceLoader;
import com.tom_roush.pdfbox.pdmodel.PDDocument;
import com.tom_roush.pdfbox.rendering.TestRendering;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class MultilineFieldsInstrumentationTest
{
    private static File OUT_DIR;
    private static final String IN_DIR = "pdfbox/com/tom_roush/pdfbox/pdmodel/interactive/form";
    private static final String NAME_OF_PDF = "MultilineFields.pdf";
    private static final String TEST_VALUE = "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, " +
        "sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam";

    private Context testContext;

    private PDDocument document;
    private PDAcroForm acroForm;

    @Before
    public void setUp() throws IOException
    {
        testContext = InstrumentationRegistry.getInstrumentation().getContext();
        PDFBoxResourceLoader.init(testContext);
        document = PDDocument.load(testContext.getAssets().open(IN_DIR + "/" + NAME_OF_PDF));
        acroForm = document.getDocumentCatalog().getAcroForm();
        OUT_DIR = new File(testContext.getCacheDir(), "pdfbox-test-output");
        OUT_DIR.mkdirs();
    }

    @Test
    public void fillFields() throws IOException
    {
        PDTextField field = (PDTextField) acroForm.getField("AlignLeft");
        field.setValue(TEST_VALUE);

        field = (PDTextField) acroForm.getField("AlignMiddle");
        field.setValue(TEST_VALUE);

        field = (PDTextField) acroForm.getField("AlignRight");
        field.setValue(TEST_VALUE);

        field = (PDTextField) acroForm.getField("AlignLeft-Border_Small");
        field.setValue(TEST_VALUE);

        field = (PDTextField) acroForm.getField("AlignMiddle-Border_Small");
        field.setValue(TEST_VALUE);

        field = (PDTextField) acroForm.getField("AlignRight-Border_Small");
        field.setValue(TEST_VALUE);

        field = (PDTextField) acroForm.getField("AlignLeft-Border_Medium");
        field.setValue(TEST_VALUE);

        field = (PDTextField) acroForm.getField("AlignMiddle-Border_Medium");
        field.setValue(TEST_VALUE);

        field = (PDTextField) acroForm.getField("AlignRight-Border_Medium");
        field.setValue(TEST_VALUE);

        field = (PDTextField) acroForm.getField("AlignLeft-Border_Wide");
        field.setValue(TEST_VALUE);

        field = (PDTextField) acroForm.getField("AlignMiddle-Border_Wide");
        field.setValue(TEST_VALUE);

        field = (PDTextField) acroForm.getField("AlignRight-Border_Wide");
        field.setValue(TEST_VALUE);

        // compare rendering
        File file = new File(OUT_DIR, NAME_OF_PDF);
        document.save(file);
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
