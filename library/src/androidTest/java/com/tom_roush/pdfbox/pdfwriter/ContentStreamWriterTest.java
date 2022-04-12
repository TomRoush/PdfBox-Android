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
package com.tom_roush.pdfbox.pdfwriter;

import android.content.Context;
import android.graphics.Bitmap;

import androidx.test.platform.app.InstrumentationRegistry;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import com.tom_roush.pdfbox.android.PDFBoxResourceLoader;
import com.tom_roush.pdfbox.android.TestResourceGenerator;
import com.tom_roush.pdfbox.cos.COSName;
import com.tom_roush.pdfbox.pdfparser.PDFStreamParser;
import com.tom_roush.pdfbox.pdmodel.PDDocument;
import com.tom_roush.pdfbox.pdmodel.PDPage;
import com.tom_roush.pdfbox.pdmodel.common.PDStream;
import com.tom_roush.pdfbox.rendering.PDFRenderer;
import com.tom_roush.pdfbox.rendering.TestRendering;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assume.assumeTrue;

/**
 *
 * @author Tilman Hausherr
 */
public class ContentStreamWriterTest
{

   private File testDirIn;
   private File testDirOut;

   private Context testContext;

   public ContentStreamWriterTest()
   {
   }

   @BeforeClass
   public static void setUpClass()
   {
   }

   @AfterClass
   public static void tearDownClass()
   {
   }

   @Before
   public void setUp()
   {
      testContext = InstrumentationRegistry.getInstrumentation().getContext();
      PDFBoxResourceLoader.init(testContext);

      testDirIn = new File(testContext.getCacheDir(), "pdfs");
      testDirIn.mkdirs();
      testDirOut = new File(testContext.getCacheDir(), "pdfbox-test-output/contentstream");
      testDirOut.mkdirs();
   }

   @After
   public void tearDown()
   {
   }

   /**
    * Test parse content stream, write back tokens and compare rendering.
    *
    * @throws java.io.IOException
    */
   @Test
   public void testPDFBox4750() throws IOException
   {
      String filename = "PDFBOX-4750.pdf";
      File file = TestResourceGenerator.downloadTestResource(testDirIn, filename, "https://issues.apache.org/jira/secure/attachment/12991833/PDFBOX-4750-test.pdf");
      assumeTrue(file.exists());
      PDDocument doc = PDDocument.load(file);

      PDFRenderer r = new PDFRenderer(doc);
      for (int i = 0; i < doc.getNumberOfPages(); ++i)
      {
         Bitmap bim1 = r.renderImageWithDPI(i, 96);
         FileOutputStream fileOut = new FileOutputStream(new File(testDirOut, filename + "-" + (i + 1) + ".png"));
         bim1.compress(Bitmap.CompressFormat.PNG, 100, fileOut);
         fileOut.close();

         PDPage page = doc.getPage(i);
         PDStream newContent = new PDStream(doc);

         PDFStreamParser parser = new PDFStreamParser(page);
         parser.parse();
         OutputStream os = newContent.createOutputStream(COSName.FLATE_DECODE);
         ContentStreamWriter tokenWriter = new ContentStreamWriter(os);
         tokenWriter.writeTokens(parser.getTokens());
         os.close();

         page.setContents(newContent);
      }
      doc.save(new File(testDirOut, filename));
      doc.close();

      File renderFile = new File(testDirOut, filename);
      TestRendering testRendering = new TestRendering();
      testRendering.setUp();
      testRendering.render(renderFile);
   }
}
