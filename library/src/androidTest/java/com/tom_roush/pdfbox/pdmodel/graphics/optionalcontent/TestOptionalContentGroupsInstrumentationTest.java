package com.tom_roush.pdfbox.pdmodel.graphics.optionalcontent;

import android.content.Context;
import android.graphics.Bitmap;

import androidx.test.platform.app.InstrumentationRegistry;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import com.tom_roush.harmony.awt.AWTColor;
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader;
import com.tom_roush.pdfbox.cos.COSName;
import com.tom_roush.pdfbox.pdmodel.PDDocument;
import com.tom_roush.pdfbox.pdmodel.PDPage;
import com.tom_roush.pdfbox.pdmodel.PDPageContentStream;
import com.tom_roush.pdfbox.pdmodel.PDPageContentStream.AppendMode;
import com.tom_roush.pdfbox.pdmodel.PDResources;
import com.tom_roush.pdfbox.pdmodel.PageMode;
import com.tom_roush.pdfbox.pdmodel.font.PDFont;
import com.tom_roush.pdfbox.pdmodel.font.PDType1Font;
import com.tom_roush.pdfbox.rendering.PDFRenderer;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TestOptionalContentGroupsInstrumentationTest
{
   private File testResultsDir;
   private Context testContext;

   @Before
   public void setUp()
   {
      testContext = InstrumentationRegistry.getInstrumentation().getContext();
      PDFBoxResourceLoader.init(testContext);
      testResultsDir = new File(testContext.getCacheDir(), "pdfbox-test-output/optionalcontent/");
      testResultsDir.mkdirs();
   }

   /**
    * PDFBOX-4496: setGroupEnabled(String, boolean) must catch all OCGs of a name even when several
    * names are identical.
    *
    * @throws IOException
    */
   @Test
   public void testOCGGenerationSameNameCanHaveSameVisibilityOff() throws IOException
   {
      Bitmap expectedImage;
      Bitmap actualImage;

      PDDocument doc = new PDDocument();
      try
      {
         //Create new page
         PDPage page = new PDPage();
         doc.addPage(page);
         PDResources resources = page.getResources();
         if (resources == null)
         {
            resources = new PDResources();
            page.setResources(resources);
         }

         //Prepare OCG functionality
         PDOptionalContentProperties ocprops = new PDOptionalContentProperties();
         doc.getDocumentCatalog().setOCProperties(ocprops);
         //ocprops.setBaseState(BaseState.ON); //ON=default

         //Create OCG for background
         PDOptionalContentGroup background = new PDOptionalContentGroup("background");
         ocprops.addGroup(background);
         assertTrue(ocprops.isGroupEnabled("background"));

         //Create OCG for enabled
         PDOptionalContentGroup enabled = new PDOptionalContentGroup("science");
         ocprops.addGroup(enabled);
         assertFalse(ocprops.setGroupEnabled("science", true));
         assertTrue(ocprops.isGroupEnabled("science"));

         //Create OCG for disabled1
         PDOptionalContentGroup disabled1 = new PDOptionalContentGroup("alternative");
         ocprops.addGroup(disabled1);

         //Create OCG for disabled2 with same name as disabled1
         PDOptionalContentGroup disabled2 = new PDOptionalContentGroup("alternative");
         ocprops.addGroup(disabled2);

         assertFalse(ocprops.setGroupEnabled("alternative", false));
         assertFalse(ocprops.isGroupEnabled("alternative"));

         //Setup page content stream and paint background/title
         PDPageContentStream contentStream = new PDPageContentStream(doc, page, AppendMode.OVERWRITE, false);
         PDFont font = PDType1Font.HELVETICA_BOLD;
         contentStream.beginMarkedContent(COSName.OC, background);
         contentStream.beginText();
         contentStream.setFont(font, 14);
         contentStream.newLineAtOffset(80, 700);
         contentStream.showText("PDF 1.5: Optional Content Groups");
         contentStream.endText();
         contentStream.endMarkedContent();

         font = PDType1Font.HELVETICA;

         //Paint enabled layer
         contentStream.beginMarkedContent(COSName.OC, enabled);
         contentStream.setNonStrokingColor(AWTColor.GREEN);
         contentStream.beginText();
         contentStream.setFont(font, 12);
         contentStream.newLineAtOffset(80, 600);
         contentStream.showText("The earth is a sphere");
         contentStream.endText();
         contentStream.endMarkedContent();

         //Paint disabled layer1
         contentStream.beginMarkedContent(COSName.OC, disabled1);
         contentStream.setNonStrokingColor(AWTColor.RED);
         contentStream.beginText();
         contentStream.setFont(font, 12);
         contentStream.newLineAtOffset(80, 500);
         contentStream.showText("Alternative 1: The earth is a flat circle");
         contentStream.endText();
         contentStream.endMarkedContent();

         //Paint disabled layer2
         contentStream.beginMarkedContent(COSName.OC, disabled2);
         contentStream.setNonStrokingColor(AWTColor.BLUE);
         contentStream.beginText();
         contentStream.setFont(font, 12);
         contentStream.newLineAtOffset(80, 450);
         contentStream.showText("Alternative 2: The earth is a flat parallelogram");
         contentStream.endText();
         contentStream.endMarkedContent();

         contentStream.close();

         doc.getDocumentCatalog().setPageMode(PageMode.USE_OPTIONAL_CONTENT);

         File targetFile = new File(testResultsDir, "ocg-generation-same-name-off.pdf");
         doc.save(targetFile.getAbsolutePath());
         doc.close();

         // render PDF with science disabled and alternatives with same name enabled
         doc = PDDocument.load(new File(testResultsDir, "ocg-generation-same-name-off.pdf"));
         doc.getDocumentCatalog().getOCProperties().setGroupEnabled("background", false);
         doc.getDocumentCatalog().getOCProperties().setGroupEnabled("science", false);
         doc.getDocumentCatalog().getOCProperties().setGroupEnabled("alternative", true);
         actualImage = new PDFRenderer(doc).renderImage(0, 2);
         actualImage.compress(Bitmap.CompressFormat.PNG, 100,
             new FileOutputStream(new File(testResultsDir, "ocg-generation-same-name-off-actual.png")));
      }
      finally
      {
         doc.close();
      }

      // create PDF without OCGs to created expected rendering
      PDDocument doc2 = new PDDocument();
      try
      {
         //Create new page
         PDPage page = new PDPage();
         doc2.addPage(page);
         PDResources resources = page.getResources();
         if (resources == null)
         {
            resources = new PDResources();
            page.setResources(resources);
         }

         PDPageContentStream contentStream = new PDPageContentStream(doc2, page, AppendMode.OVERWRITE, false);
         PDFont font = PDType1Font.HELVETICA;

         contentStream.setNonStrokingColor(AWTColor.RED);
         contentStream.beginText();
         contentStream.setFont(font, 12);
         contentStream.newLineAtOffset(80, 500);
         contentStream.showText("Alternative 1: The earth is a flat circle");
         contentStream.endText();

         contentStream.setNonStrokingColor(AWTColor.BLUE);
         contentStream.beginText();
         contentStream.setFont(font, 12);
         contentStream.newLineAtOffset(80, 450);
         contentStream.showText("Alternative 2: The earth is a flat parallelogram");
         contentStream.endText();

         contentStream.close();

         expectedImage = new PDFRenderer(doc2).renderImage(0, 2);
         actualImage.compress(Bitmap.CompressFormat.PNG, 100,
             new FileOutputStream(new File(testResultsDir, "ocg-generation-same-name-off-expected.png")));
      }
      finally
      {
         doc2.close();
      }

      // compare images
      int height = expectedImage.getHeight();
      int width = expectedImage.getWidth();

      int[] expectedImagePixels = new int[width * height];
      expectedImage.getPixels(expectedImagePixels, 0, width, 0, 0, width, height);
      int[] actualImagePixels = new int[width * height];
      actualImage.getPixels(actualImagePixels, 0, width, 0, 0, width, height);
      Assert.assertArrayEquals(expectedImagePixels, actualImagePixels);
   }
}
