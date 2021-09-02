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

import com.tom_roush.pdfbox.pdmodel.PDDocument;
import com.tom_roush.pdfbox.rendering.TestRendering;
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class AcroFormsRotationTest
{

    private static File OUT_DIR;
    private static final String IN_DIR = "pdfbox/com/tom_roush/pdfbox/pdmodel/interactive/form";
    private static final String NAME_OF_PDF = "AcroFormsRotation.pdf";
    private static final String TEST_VALUE = "Lorem ipsum dolor sit amet, consetetur sadipscing elitr,"
        + " sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua.";

    private PDDocument document;
    private PDAcroForm acroForm;

    Context testContext;

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

        // portrait page
        // single line fields
        PDField field = acroForm.getField("pdfbox.portrait.single.rotation0");
        field.setValue(field.getFullyQualifiedName());
        field = acroForm.getField("pdfbox.portrait.single.rotation90");
        field.setValue(field.getFullyQualifiedName());
        field = acroForm.getField("pdfbox.portrait.single.rotation180");
        field.setValue(field.getFullyQualifiedName());
        field = acroForm.getField("pdfbox.portrait.single.rotation270");
        field.setValue(field.getFullyQualifiedName());

        // multiline fields
        field = acroForm.getField("pdfbox.portrait.multi.rotation0");
        field.setValue(field.getFullyQualifiedName() + "\n" + TEST_VALUE);
        field = acroForm.getField("pdfbox.portrait.multi.rotation90");
        field.setValue(field.getFullyQualifiedName() + "\n" + TEST_VALUE);
        field = acroForm.getField("pdfbox.portrait.multi.rotation180");
        field.setValue(field.getFullyQualifiedName() + "\n" + TEST_VALUE);
        field = acroForm.getField("pdfbox.portrait.multi.rotation270");
        field.setValue(field.getFullyQualifiedName() + "\n" + TEST_VALUE);

        // 90 degrees rotated page
        // single line fields
        field = acroForm.getField("pdfbox.page90.single.rotation0");
        field.setValue("pdfbox.page90.single.rotation0");
        field = acroForm.getField("pdfbox.page90.single.rotation90");
        field.setValue("pdfbox.page90.single.rotation90");
        field = acroForm.getField("pdfbox.page90.single.rotation180");
        field.setValue("pdfbox.page90.single.rotation180");
        field = acroForm.getField("pdfbox.page90.single.rotation270");
        field.setValue("pdfbox.page90.single.rotation270");

        // multiline fields
        field = acroForm.getField("pdfbox.page90.multi.rotation0");
        field.setValue(field.getFullyQualifiedName() + "\n" + TEST_VALUE);
        field = acroForm.getField("pdfbox.page90.multi.rotation90");
        field.setValue(field.getFullyQualifiedName() + "\n" + TEST_VALUE);
        field = acroForm.getField("pdfbox.page90.multi.rotation180");
        field.setValue(field.getFullyQualifiedName() + "\n" + TEST_VALUE);
        field = acroForm.getField("pdfbox.page90.multi.rotation270");
        field.setValue(field.getFullyQualifiedName() + "\n" + TEST_VALUE);

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