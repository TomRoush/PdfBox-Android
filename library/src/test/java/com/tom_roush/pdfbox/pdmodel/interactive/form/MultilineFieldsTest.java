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

import com.tom_roush.pdfbox.pdmodel.PDDocument;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

public class MultilineFieldsTest
{
    private static final File OUT_DIR = new File("target/test-output");
    private static final String PATH_OF_PDF =
        "/pdfbox/com/tom_roush/pdfbox/pdmodel/interactive/form/MultilineFields.pdf";
    private static final String TEST_VALUE =
        "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, " +
            "sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam";


    private PDDocument document;
    private PDAcroForm acroForm;

    @Before
    public void setUp() throws IOException
    {
        document = PDDocument.load(getClass().getResourceAsStream(PATH_OF_PDF)); // XXX: from File
        acroForm = document.getDocumentCatalog().getAcroForm();
        OUT_DIR.mkdirs();
    }

    @Test
    public void fillFields() throws IOException
    {
        PDField field = (PDField) acroForm.getField("AlignLeft");
        field.setValue(TEST_VALUE);

        field = (PDField) acroForm.getField("AlignMiddle");
        field.setValue(TEST_VALUE);

        field = (PDField) acroForm.getField("AlignRight");
        field.setValue(TEST_VALUE);

        field = (PDField) acroForm.getField("AlignLeft-Border_Small");
        field.setValue(TEST_VALUE);

        field = (PDField) acroForm.getField("AlignMiddle-Border_Small");
        field.setValue(TEST_VALUE);

        field = (PDField) acroForm.getField("AlignRight-Border_Small");
        field.setValue(TEST_VALUE);

        field = (PDField) acroForm.getField("AlignLeft-Border_Medium");
        field.setValue(TEST_VALUE);

        field = (PDField) acroForm.getField("AlignMiddle-Border_Medium");
        field.setValue(TEST_VALUE);

        field = (PDField) acroForm.getField("AlignRight-Border_Medium");
        field.setValue(TEST_VALUE);

        field = (PDField) acroForm.getField("AlignLeft-Border_Wide");
        field.setValue(TEST_VALUE);

        field = (PDField) acroForm.getField("AlignMiddle-Border_Wide");
        field.setValue(TEST_VALUE);

        field = (PDField) acroForm.getField("AlignRight-Border_Wide");
        field.setValue(TEST_VALUE);
    }

    @After
    public void tearDown() throws IOException
    {
        File file = new File(OUT_DIR, "MultilineTests.pdf");
        document.save(file);
        document.close();
    }
}
