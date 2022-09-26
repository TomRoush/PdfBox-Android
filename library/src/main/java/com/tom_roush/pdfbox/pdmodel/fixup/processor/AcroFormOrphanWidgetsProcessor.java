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
package com.tom_roush.pdfbox.pdmodel.fixup.processor;

import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.tom_roush.fontbox.ttf.TrueTypeFont;
import com.tom_roush.pdfbox.cos.COSDictionary;
import com.tom_roush.pdfbox.cos.COSName;
import com.tom_roush.pdfbox.pdmodel.PDDocument;
import com.tom_roush.pdfbox.pdmodel.PDPage;
import com.tom_roush.pdfbox.pdmodel.PDResources;
import com.tom_roush.pdfbox.pdmodel.font.FontMapper;
import com.tom_roush.pdfbox.pdmodel.font.FontMappers;
import com.tom_roush.pdfbox.pdmodel.font.FontMapping;
import com.tom_roush.pdfbox.pdmodel.font.PDType0Font;
import com.tom_roush.pdfbox.pdmodel.interactive.annotation.PDAnnotation;
import com.tom_roush.pdfbox.pdmodel.interactive.annotation.PDAnnotationWidget;
import com.tom_roush.pdfbox.pdmodel.interactive.annotation.PDAppearanceStream;
import com.tom_roush.pdfbox.pdmodel.interactive.form.PDAcroForm;
import com.tom_roush.pdfbox.pdmodel.interactive.form.PDField;
import com.tom_roush.pdfbox.pdmodel.interactive.form.PDFieldFactory;
import com.tom_roush.pdfbox.pdmodel.interactive.form.PDVariableText;

/**
 *  Generate field entries from page level widget annotations
 *  if there AcroForm /Fields entry is empty.
 *
 */
public class AcroFormOrphanWidgetsProcessor extends AbstractProcessor
{

   public AcroFormOrphanWidgetsProcessor(PDDocument document)
   {
      super(document);
   }

   @Override
   public void process()
   {
      /*
       * Get the AcroForm in it's current state.
       *
       * Also note: getAcroForm() applies a default fixup which this processor
       * is part of. So keep the null parameter otherwise this will end
       * in an endless recursive call
       */
      PDAcroForm acroForm = document.getDocumentCatalog().getAcroForm(null);

      if (acroForm != null)
      {
         resolveFieldsFromWidgets(acroForm);
      }
   }

   private void resolveFieldsFromWidgets(PDAcroForm acroForm)
   {
      Log.d("PdfBox-Android", "rebuilding fields from widgets");

      PDResources resources = acroForm.getDefaultResources();
      if (resources == null)
      {
         // failsafe. Currently resources is never null because defaultfixup is called first.
         Log.d("PdfBox-Android", "AcroForm default resources is null");
         return;
      }

      List<PDField> fields = new ArrayList<PDField>();
      Map<String, PDField> nonTerminalFieldsMap = new HashMap<String, PDField>();
      for (PDPage page : document.getPages())
      {
         try
         {
            handleAnnotations(acroForm, resources, fields, page.getAnnotations(), nonTerminalFieldsMap);
         }
         catch (IOException ioe)
         {
            Log.d("PdfBox-Android", "couldn't read annotations for page " + ioe.getMessage());
         }
      }

      acroForm.setFields(fields);

      for (PDField field : acroForm.getFieldTree())
      {
         if (field instanceof PDVariableText)
         {
            ensureFontResources(resources, (PDVariableText) field);
         }
      }
   }

   private void handleAnnotations(PDAcroForm acroForm, PDResources acroFormResources,
       List<PDField> fields, List<PDAnnotation> annotations,
       Map<String, PDField> nonTerminalFieldsMap)
   {
      for (PDAnnotation annot : annotations)
      {
         if (annot instanceof PDAnnotationWidget)
         {
            addFontFromWidget(acroFormResources, annot);

            COSDictionary parent = annot.getCOSObject().getCOSDictionary(COSName.PARENT);
            if (parent != null)
            {
               PDField resolvedField = resolveNonRootField(acroForm, parent, nonTerminalFieldsMap);
               if (resolvedField != null)
               {
                  fields.add(resolvedField);
               }
            }
            else
            {
               fields.add(PDFieldFactory.createField(acroForm, annot.getCOSObject(), null));
            }
         }
      }
   }

   /**
    * Add font resources from the widget to the AcroForm to make sure embedded fonts are being used
    * and not added by ensureFontResources potentially using a fallback font.
    *
    * @param acroFormResources AcroForm default resources, should not be null.
    * @param annotation annotation, should not be null.
    */
   private void addFontFromWidget(PDResources acroFormResources, PDAnnotation annotation)
   {
      PDAppearanceStream normalAppearanceStream = annotation.getNormalAppearanceStream();
      if (normalAppearanceStream == null)
      {
         return;
      }
      PDResources widgetResources = normalAppearanceStream.getResources();
      if (widgetResources == null)
      {
         return;
      }
      for (COSName fontName : widgetResources.getFontNames())
      {
         if (!fontName.getName().startsWith("+"))
         {
            try
            {
               if (acroFormResources.getFont(fontName) == null)
               {
                  acroFormResources.put(fontName, widgetResources.getFont(fontName));
                  Log.d("PdfBox-Android", "added font resource to AcroForm from widget for font name " + fontName.getName());
               }
            }
            catch (IOException ioe)
            {
               Log.d("PdfBox-Android", "unable to add font to AcroForm for font name " + fontName.getName());
            }
         }
         else
         {
            Log.d("PdfBox-Android", "font resource for widget was a subsetted font - ignored: " + fontName.getName());
         }
      }
   }

   /*
    *  Widgets having a /Parent entry are non root fields. Go up until the root node is found
    *  and handle from there.
    */
   private PDField resolveNonRootField(PDAcroForm acroForm, COSDictionary parent, Map<String, PDField> nonTerminalFieldsMap)
   {
      while (parent.containsKey(COSName.PARENT))
      {
         parent = parent.getCOSDictionary(COSName.PARENT);
         if (parent == null)
         {
            return null;
         }
      }

      if (nonTerminalFieldsMap.get(parent.getString(COSName.T)) == null)
      {
         PDField field = PDFieldFactory.createField(acroForm, parent, null);
         if (field != null)
         {
            nonTerminalFieldsMap.put(field.getFullyQualifiedName(), field);
         }
         return field;
      }

      // this should not happen, likely broken PDF
      return null;
   }

   /*
    *  Lookup the font used in the default appearance and if this is
    *  not available try to find a suitable font and use that.
    *  This may not be the original font but a similar font replacement
    *
    *  TODO: implement a font lookup similar as discussed in PDFBOX-2661 so that already existing
    *        font resources might be accepatble.
    *        In such case this must be implemented in PDDefaultAppearanceString too!
    */
   private void ensureFontResources(PDResources defaultResources, PDVariableText field)
   {
      String daString = field.getDefaultAppearance();
      if (daString.startsWith("/") && daString.length() > 1)
      {
         COSName fontName = COSName.getPDFName(daString.substring(1, daString.indexOf(" ")));
         try
         {
            if (defaultResources.getFont(fontName) == null)
            {
               Log.d("PdfBox-Android", "trying to add missing font resource for field " + field.getFullyQualifiedName());
               FontMapper mapper = FontMappers.instance();
               FontMapping<TrueTypeFont> fontMapping = mapper.getTrueTypeFont(fontName.getName() , null);
               if (fontMapping != null)
               {
                  PDType0Font pdFont = PDType0Font.load(document, fontMapping.getFont(), false);
                  Log.d("PdfBox-Android", "looked up font for " + fontName.getName() + " - found " + fontMapping.getFont().getName());
                  defaultResources.put(fontName, pdFont);
               }
               else
               {
                  Log.d("PdfBox-Android", "no suitable font found for field " + field.getFullyQualifiedName() + " for font name " + fontName.getName());
               }
            }
         }
         catch (IOException ioe)
         {
            Log.d("PdfBox-Android", "Unable to handle font resources for field " + field.getFullyQualifiedName() + ": " + ioe.getMessage());
         }
      }
   }
}