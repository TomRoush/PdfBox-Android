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
package com.tom_roush.pdfbox.pdmodel.graphics.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.test.platform.app.InstrumentationRegistry;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;

import com.tom_roush.pdfbox.android.PDFBoxResourceLoader;
import com.tom_roush.pdfbox.android.TestResourceGenerator;
import com.tom_roush.pdfbox.io.IOUtils;
import com.tom_roush.pdfbox.pdmodel.PDDocument;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Test that the convenience methods are really doing what's expected, and having the same as
 * the more focused factory methods.
 *
 * @author Tilman Hausherr
 */
public class PDImageXObjectTest
{
   private Context testContext;
   private String inPath = "pdfbox/com/tom_roush/pdfbox/pdmodel/graphics/image/";
   private File CACHE_DIR;

   @Before
   public void setUp() throws Exception
   {
      testContext = InstrumentationRegistry.getInstrumentation().getContext();
      PDFBoxResourceLoader.init(testContext);
      CACHE_DIR = new File(testContext.getCacheDir(), "assets");
      CACHE_DIR.mkdirs();
   }

   public PDImageXObjectTest()
   {
   }

   /**
    * Test of createFromFileByExtension method, of class PDImageXObject.
    */
   @Test
   public void testCreateFromFileByExtension() throws Exception
   {
      testCompareCreatedFileByExtensionWithCreatedByCCITTFactory("ccittg4.tif");

      testCompareCreatedFileByExtensionWithCreatedByJPEGFactory("jpeg.jpg");
      testCompareCreatedFileByExtensionWithCreatedByJPEGFactory("jpegcmyk.jpg");

      testCompareCreatedFileByExtensionWithCreatedByLosslessFactory("gif.gif");
//      testCompareCreatedFileByExtensionWithCreatedByLosslessFactory("gif-1bit-transparent.gif");
//      testCompareCreatedFileByExtensionWithCreatedByLosslessFactory("png_indexed_8bit_alpha.png");
      testCompareCreatedFileByExtensionWithCreatedByLosslessFactory("png.png");
//      testCompareCreatedFileByExtensionWithCreatedByLosslessFactory("lzw.tif");
   }

   /**
    * Test of createFromFile method, of class PDImageXObject.
    */
   @Test
   public void testCreateFromFile() throws Exception
   {
      testCompareCreatedFileWithCreatedByCCITTFactory("ccittg4.tif");

      testCompareCreatedFileWithCreatedByJPEGFactory("jpeg.jpg");
      testCompareCreatedFileWithCreatedByJPEGFactory("jpegcmyk.jpg");

      testCompareCreatedFileWithCreatedByLosslessFactory("gif.gif");
//      testCompareCreatedFileWithCreatedByLosslessFactory("gif-1bit-transparent.gif");
//      testCompareCreatedFileWithCreatedByLosslessFactory("png_indexed_8bit_alpha.png");
      testCompareCreatedFileWithCreatedByLosslessFactory("png.png");
//      testCompareCreatedFileWithCreatedByLosslessFactory("lzw.tif");
   }


   /**
    * Test of createFromFileByContent method, of class PDImageXObject.
    */
   @Test
   public void testCreateFromFileByContent() throws Exception
   {
      testCompareCreateByContentWithCreatedByCCITTFactory("ccittg4.tif");

      testCompareCreatedByContentWithCreatedByJPEGFactory("jpeg.jpg");
      testCompareCreatedByContentWithCreatedByJPEGFactory("jpegcmyk.jpg");

      testCompareCreatedByContentWithCreatedByLosslessFactory("gif.gif");
//      testCompareCreatedByContentWithCreatedByLosslessFactory("gif-1bit-transparent.gif");
//      testCompareCreatedByContentWithCreatedByLosslessFactory("png_indexed_8bit_alpha.png");
      testCompareCreatedByContentWithCreatedByLosslessFactory("png.png");
//      testCompareCreatedByContentWithCreatedByLosslessFactory("lzw.tif");
   }


   /**
    * Test of createFromByteArray method, of class PDImageXObject.
    */
   @Test
   public void testCreateFromByteArray() throws Exception
   {
      testCompareCreatedFromByteArrayWithCreatedByCCITTFactory("ccittg4.tif");

      testCompareCreatedFromByteArrayWithCreatedByJPEGFactory("jpeg.jpg");
      testCompareCreatedFromByteArrayWithCreatedByJPEGFactory("jpegcmyk.jpg");

      testCompareCreatedFromByteArrayWithCreatedByLosslessFactory("gif.gif");
//      testCompareCreatedFromByteArrayWithCreatedByLosslessFactory("gif-1bit-transparent.gif");
//      testCompareCreatedFromByteArrayWithCreatedByLosslessFactory("png_indexed_8bit_alpha.png");
      testCompareCreatedFromByteArrayWithCreatedByLosslessFactory("png.png");
//      testCompareCreatedFromByteArrayWithCreatedByLosslessFactory("lzw.tif");
   }

   private void testCompareCreatedFileByExtensionWithCreatedByLosslessFactory(String filename)
       throws IOException, URISyntaxException
   {
      PDDocument doc = new PDDocument();
      File file = TestResourceGenerator.copyStreamToFile(CACHE_DIR, filename, testContext.getAssets().open(inPath + filename));
      PDImageXObject image = PDImageXObject.createFromFileByExtension(file, doc);

      Bitmap bim = BitmapFactory.decodeStream(testContext.getAssets().open(inPath + filename));
      PDImageXObject expectedImage = LosslessFactory.createFromImage(doc, bim);

      Assert.assertEquals(expectedImage.getSuffix(), image.getSuffix());
      checkIdentARGB(image.getImage(), expectedImage.getImage());

      doc.close();
   }

   private void testCompareCreatedFileByExtensionWithCreatedByCCITTFactory(String filename)
       throws IOException, URISyntaxException
   {
      PDDocument doc = new PDDocument();
      File file = TestResourceGenerator.copyStreamToFile(CACHE_DIR, filename, testContext.getAssets().open(inPath + filename));
      PDImageXObject image = PDImageXObject.createFromFileByExtension(file, doc);

      PDImageXObject expectedImage = CCITTFactory.createFromFile(doc, file);

      Assert.assertEquals(expectedImage.getSuffix(), image.getSuffix());
      checkIdentARGB(image.getImage(), expectedImage.getImage());

      doc.close();
   }

   private void testCompareCreatedFileByExtensionWithCreatedByJPEGFactory(String filename)
       throws IOException, URISyntaxException
   {
      PDDocument doc = new PDDocument();
      File file = TestResourceGenerator.copyStreamToFile(CACHE_DIR, filename, testContext.getAssets().open(inPath + filename));
      PDImageXObject image = PDImageXObject.createFromFileByExtension(file, doc);

      PDImageXObject expectedImage = JPEGFactory.createFromStream(doc, new FileInputStream(file));

      Assert.assertEquals(expectedImage.getSuffix(), image.getSuffix());
      checkIdentARGB(image.getImage(), expectedImage.getImage());

      doc.close();
   }

   private void testCompareCreatedFileWithCreatedByLosslessFactory(String filename)
       throws IOException, URISyntaxException
   {
      PDDocument doc = new PDDocument();
      File file = TestResourceGenerator.copyStreamToFile(CACHE_DIR, filename, testContext.getAssets().open(inPath + filename));
      PDImageXObject image = PDImageXObject.createFromFile(file.getAbsolutePath(), doc);

      Bitmap bim = BitmapFactory.decodeStream(testContext.getAssets().open(inPath + filename));
      PDImageXObject expectedImage = LosslessFactory.createFromImage(doc, bim);

      Assert.assertEquals(expectedImage.getSuffix(), image.getSuffix());
      checkIdentARGB(image.getImage(), expectedImage.getImage());

      doc.close();
   }

   private void testCompareCreatedFileWithCreatedByCCITTFactory(String filename)
       throws IOException, URISyntaxException
   {
      PDDocument doc = new PDDocument();
      File file = TestResourceGenerator.copyStreamToFile(CACHE_DIR, filename, testContext.getAssets().open(inPath + filename));
      PDImageXObject image = PDImageXObject.createFromFile(file.getAbsolutePath(), doc);

      PDImageXObject expectedImage = CCITTFactory.createFromFile(doc, file);

      Assert.assertEquals(expectedImage.getSuffix(), image.getSuffix());
      checkIdentARGB(image.getImage(), expectedImage.getImage());

      doc.close();
   }

   private void testCompareCreatedFileWithCreatedByJPEGFactory(String filename)
       throws IOException, URISyntaxException
   {
      PDDocument doc = new PDDocument();
      File file = TestResourceGenerator.copyStreamToFile(CACHE_DIR, filename, testContext.getAssets().open(inPath + filename));
      PDImageXObject image = PDImageXObject.createFromFile(file.getAbsolutePath(), doc);

      PDImageXObject expectedImage = JPEGFactory.createFromStream(doc, new FileInputStream(file));

      Assert.assertEquals(expectedImage.getSuffix(), image.getSuffix());
      checkIdentARGB(image.getImage(), expectedImage.getImage());

      doc.close();
   }

   private void testCompareCreatedByContentWithCreatedByLosslessFactory(String filename)
       throws IOException, URISyntaxException
   {
      PDDocument doc = new PDDocument();
      File file = TestResourceGenerator.copyStreamToFile(CACHE_DIR, filename, testContext.getAssets().open(inPath + filename));
      PDImageXObject image = PDImageXObject.createFromFileByContent(file, doc);

      Bitmap bim = BitmapFactory.decodeStream(testContext.getAssets().open(inPath + filename));
      PDImageXObject expectedImage = LosslessFactory.createFromImage(doc, bim);

      Assert.assertEquals(expectedImage.getSuffix(), image.getSuffix());
      checkIdentARGB(image.getImage(), expectedImage.getImage());

      doc.close();
   }

   private void testCompareCreateByContentWithCreatedByCCITTFactory(String filename)
       throws IOException, URISyntaxException
   {
      PDDocument doc = new PDDocument();
      File file = TestResourceGenerator.copyStreamToFile(CACHE_DIR, filename, testContext.getAssets().open(inPath + filename));
      PDImageXObject image = PDImageXObject.createFromFileByContent(file, doc);

      PDImageXObject expectedImage = CCITTFactory.createFromFile(doc, file);

      Assert.assertEquals(expectedImage.getSuffix(), image.getSuffix());
      checkIdentARGB(image.getImage(), expectedImage.getImage());

      doc.close();
   }

   private void testCompareCreatedByContentWithCreatedByJPEGFactory(String filename)
       throws IOException, URISyntaxException
   {
      PDDocument doc = new PDDocument();
      File file = TestResourceGenerator.copyStreamToFile(CACHE_DIR, filename, testContext.getAssets().open(inPath + filename));
      PDImageXObject image = PDImageXObject.createFromFileByContent(file, doc);

      PDImageXObject expectedImage = JPEGFactory.createFromStream(doc, new FileInputStream(file));

      Assert.assertEquals(expectedImage.getSuffix(), image.getSuffix());
      checkIdentARGB(image.getImage(), expectedImage.getImage());

      doc.close();
   }




   private void testCompareCreatedFromByteArrayWithCreatedByLosslessFactory(String filename)
       throws IOException, URISyntaxException
   {
      PDDocument doc = new PDDocument();
      File file = TestResourceGenerator.copyStreamToFile(CACHE_DIR, filename, testContext.getAssets().open(inPath + filename));
      byte[] byteArray = IOUtils.toByteArray(new FileInputStream(file));
      PDImageXObject image = PDImageXObject.createFromByteArray(doc, byteArray, null);

      Bitmap bim = BitmapFactory.decodeStream(testContext.getAssets().open(inPath + filename));
      PDImageXObject expectedImage = LosslessFactory.createFromImage(doc, bim);

      Assert.assertEquals(expectedImage.getSuffix(), image.getSuffix());
      checkIdentARGB(image.getImage(), expectedImage.getImage());

      doc.close();
   }

   private void testCompareCreatedFromByteArrayWithCreatedByCCITTFactory(String filename)
       throws IOException, URISyntaxException
   {
      PDDocument doc = new PDDocument();
      File file = TestResourceGenerator.copyStreamToFile(CACHE_DIR, filename, testContext.getAssets().open(inPath + filename));
      byte[] byteArray = IOUtils.toByteArray(new FileInputStream(file));
      PDImageXObject image = PDImageXObject.createFromByteArray(doc, byteArray, null);

      PDImageXObject expectedImage = CCITTFactory.createFromFile(doc, file);

      Assert.assertEquals(expectedImage.getSuffix(), image.getSuffix());
      checkIdentARGB(image.getImage(), expectedImage.getImage());

      doc.close();
   }

   private void testCompareCreatedFromByteArrayWithCreatedByJPEGFactory(String filename)
       throws IOException, URISyntaxException
   {
      PDDocument doc = new PDDocument();
      File file = TestResourceGenerator.copyStreamToFile(CACHE_DIR, filename, testContext.getAssets().open(inPath + filename));
      byte[] byteArray = IOUtils.toByteArray(new FileInputStream(file));
      PDImageXObject image = PDImageXObject.createFromByteArray(doc, byteArray, null);

      PDImageXObject expectedImage = JPEGFactory.createFromStream(doc, new FileInputStream(file));

      Assert.assertEquals(expectedImage.getSuffix(), image.getSuffix());
      checkIdentARGB(image.getImage(), expectedImage.getImage());

      doc.close();
   }

   private void checkIdentARGB(Bitmap expectedImage, Bitmap actualImage)
   {
      String errMsg = "";

      int w = expectedImage.getWidth();
      int h = expectedImage.getHeight();
      Assert.assertEquals(w, actualImage.getWidth());
      Assert.assertEquals(h, actualImage.getHeight());
      for (int y = 0; y < h; ++y)
      {
         for (int x = 0; x < w; ++x)
         {
            if (expectedImage.getPixel(x, y) != actualImage.getPixel(x, y))
            {
               errMsg = String.format("(%d,%d) %06X != %06X", x, y, expectedImage.getPixel(x, y), actualImage.getPixel(x, y));
            }
            Assert.assertEquals(errMsg, expectedImage.getPixel(x, y), actualImage.getPixel(x, y));
         }
      }
   }
}
