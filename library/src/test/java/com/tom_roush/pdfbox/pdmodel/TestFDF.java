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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.util.List;

import com.tom_roush.pdfbox.pdmodel.fdf.FDFDocument;
import com.tom_roush.pdfbox.pdmodel.fdf.FDFField;
import com.tom_roush.pdfbox.pdmodel.interactive.form.PDAcroForm;

import junit.framework.TestCase;

/**
 * This will test the FDF algorithms in PDFBox.
 *
 * @author Ben Litchfield
 * @author Tilman Hausherr
 *
 */
public class TestFDF extends TestCase
{
    /**
     * Test load two simple fdf files with two fields. One of the files does not have a
     * /Type/Catalog entry, which isn't required anyway (PDFBOX-3639).
     *
     * @throws URISyntaxException
     * @throws IOException
     */
    public void testLoad2() throws URISyntaxException, IOException
    {
        checkFields("/pdfbox/com/tom_roush/pdfbox/pdfparser/withcatalog.fdf");
        checkFields("/pdfbox/com/tom_roush/pdfbox/pdfparser/nocatalog.fdf");
    }

    private void checkFields(String name) throws IOException, URISyntaxException
    {
        FDFDocument fdf = FDFDocument.load(new File(TestFDF.class.getResource(name).toURI()));
        fdf.saveXFDF(new PrintWriter(new ByteArrayOutputStream()));

        List<FDFField> fields = fdf.getCatalog().getFDF().getFields();

        assertEquals(2, fields.size());
        assertEquals("Field1", fields.get(0).getPartialFieldName());
        assertEquals("Field2", fields.get(1).getPartialFieldName());
        assertEquals("Test1", fields.get(0).getValue());
        assertEquals("Test2", fields.get(1).getValue());

        PDDocument pdf = PDDocument.load(new File(TestFDF.class.getResource("/pdfbox/com/tom_roush/pdfbox/pdfparser/SimpleForm2Fields.pdf").toURI()));
        PDAcroForm acroForm = pdf.getDocumentCatalog().getAcroForm();
        acroForm.importFDF(fdf);
        assertEquals("Test1", acroForm.getField("Field1").getValueAsString());
        assertEquals("Test2", acroForm.getField("Field2").getValueAsString());

        pdf.close();

        fdf.close();
    }
}
