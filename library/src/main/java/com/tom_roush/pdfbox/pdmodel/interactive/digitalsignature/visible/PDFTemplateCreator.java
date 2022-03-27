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
package com.tom_roush.pdfbox.pdmodel.interactive.digitalsignature.visible;

import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import com.tom_roush.harmony.awt.geom.AffineTransform;
import com.tom_roush.pdfbox.cos.COSDocument;
import com.tom_roush.pdfbox.cos.COSName;
import com.tom_roush.pdfbox.pdfwriter.COSWriter;
import com.tom_roush.pdfbox.pdmodel.PDDocument;
import com.tom_roush.pdfbox.pdmodel.PDPage;
import com.tom_roush.pdfbox.pdmodel.PDResources;
import com.tom_roush.pdfbox.pdmodel.common.PDRectangle;
import com.tom_roush.pdfbox.pdmodel.common.PDStream;
import com.tom_roush.pdfbox.pdmodel.graphics.form.PDFormXObject;
import com.tom_roush.pdfbox.pdmodel.interactive.form.PDAcroForm;
import com.tom_roush.pdfbox.pdmodel.interactive.form.PDSignatureField;

/**
 * Class to build PDF template.
 *
 * @author Vakhtang Koroghlishvili
 */
public class PDFTemplateCreator
{
    private final PDFTemplateBuilder pdfBuilder;

    /**
     * Constructor.
     *
     * @param templateBuilder
     */
    public PDFTemplateCreator(PDFTemplateBuilder templateBuilder)
    {
        pdfBuilder = templateBuilder;
    }

    /**
     * Returns the PDFTemplateStructure object.
     *
     * @return the PDFTemplateStructure object.
     */
    public PDFTemplateStructure getPdfStructure()
    {
        return pdfBuilder.getStructure();
    }

    /**
     * Build a PDF with a visible signature step by step, and return it as a stream.
     *
     * @param properties
     * @return InputStream
     * @throws IOException
     */
    public InputStream buildPDF(PDVisibleSignDesigner properties) throws IOException
    {
        Log.i("PdfBox-Android", "pdf building has been started");
        PDFTemplateStructure pdfStructure = pdfBuilder.getStructure();

        // we create array of [Text, ImageB, ImageC, ImageI]
        pdfBuilder.createProcSetArray();

        //create page
        pdfBuilder.createPage(properties);
        PDPage page = pdfStructure.getPage();

        //create template
        pdfBuilder.createTemplate(page);
        PDDocument template = pdfStructure.getTemplate();

        //create /AcroForm
        pdfBuilder.createAcroForm(template);
        PDAcroForm acroForm = pdfStructure.getAcroForm();

        // AcroForm contains signature fields
        pdfBuilder.createSignatureField(acroForm);
        PDSignatureField pdSignatureField = pdfStructure.getSignatureField();

        // create signature
        //TODO 
        // The line below has no effect with the CreateVisibleSignature example. 
        // The signature field is needed as a "holder" for the /AP tree, 
        // but the /P and /V PDSignatureField entries are ignored by PDDocument.addSignature
        pdfBuilder.createSignature(pdSignatureField, page, "");

        // that is /AcroForm/DR entry
        pdfBuilder.createAcroFormDictionary(acroForm, pdSignatureField);

        // create AffineTransform
        pdfBuilder.createAffineTransform(properties.getTransform());
        AffineTransform transform = pdfStructure.getAffineTransform();

        // rectangle, formatter, image. /AcroForm/DR/XObject contains that form
        pdfBuilder.createSignatureRectangle(pdSignatureField, properties);
        pdfBuilder.createFormatterRectangle(properties.getFormatterRectangleParameters());
        PDRectangle bbox = pdfStructure.getFormatterRectangle();
        pdfBuilder.createSignatureImage(template, properties.getImage());

        // create form stream, form and  resource. 
        pdfBuilder.createHolderFormStream(template);
        PDStream holderFormStream = pdfStructure.getHolderFormStream();
        pdfBuilder.createHolderFormResources();
        PDResources holderFormResources = pdfStructure.getHolderFormResources();
        pdfBuilder.createHolderForm(holderFormResources, holderFormStream, bbox);

        // that is /AP entry the appearance dictionary.
        pdfBuilder.createAppearanceDictionary(pdfStructure.getHolderForm(), pdSignatureField);

        // inner form stream, form and resource (holder form contains inner form)
        pdfBuilder.createInnerFormStream(template);
        pdfBuilder.createInnerFormResource();
        PDResources innerFormResource = pdfStructure.getInnerFormResources();
        pdfBuilder.createInnerForm(innerFormResource, pdfStructure.getInnerFormStream(), bbox);
        PDFormXObject innerForm = pdfStructure.getInnerForm();

        // inner form must be in the holder form as we wrote
        pdfBuilder.insertInnerFormToHolderResources(innerForm, holderFormResources);

        //  Image form is in this structure: /AcroForm/DR/FRM/Resources/XObject/n2
        pdfBuilder.createImageFormStream(template);
        PDStream imageFormStream = pdfStructure.getImageFormStream();
        pdfBuilder.createImageFormResources();
        PDResources imageFormResources = pdfStructure.getImageFormResources();
        pdfBuilder.createImageForm(imageFormResources, innerFormResource, imageFormStream, bbox,
            transform, pdfStructure.getImage());

        pdfBuilder.createBackgroundLayerForm(innerFormResource, bbox);

        // now inject procSetArray
        pdfBuilder.injectProcSetArray(innerForm, page, innerFormResource, imageFormResources,
            holderFormResources, pdfStructure.getProcSet());

        COSName imageFormName = pdfStructure.getImageFormName();
        COSName imageName = pdfStructure.getImageName();
        COSName innerFormName = pdfStructure.getInnerFormName();

        // now create Streams of AP
        pdfBuilder.injectAppearanceStreams(holderFormStream, imageFormStream, imageFormStream,
            imageFormName, imageName, innerFormName, properties);
        pdfBuilder.createVisualSignature(template);
        pdfBuilder.createWidgetDictionary(pdSignatureField, holderFormResources);

        InputStream in = getVisualSignatureAsStream(pdfStructure.getVisualSignature());
        Log.i("PdfBox-Android", "stream returning started, size= " + in.available());

        // we must close the document
        template.close();

        // return result of the stream 
        return in;
    }

    private InputStream getVisualSignatureAsStream(COSDocument visualSignature) throws IOException
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        COSWriter writer = new COSWriter(baos);
        writer.write(visualSignature);
        writer.close();
        return new ByteArrayInputStream(baos.toByteArray());
    }
}
