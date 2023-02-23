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
package com.tom_roush.pdfbox.pdmodel.graphics.color;

import android.graphics.Bitmap;

import java.io.IOException;

import com.tom_roush.pdfbox.cos.COSName;

/**
 * Allows colors to be specified according to the subtractive CMYK (cyan, magenta, yellow, black)
 * model typical of printers and other paper-based output devices.
 *
 * @author John Hewson
 * @author Ben Litchfield
 */
public class PDDeviceCMYK extends PDDeviceColorSpace
{
   /**  The single instance of this class. */
   public static PDDeviceCMYK INSTANCE;
   static
   {
      INSTANCE = new PDDeviceCMYK();
   }

   private final PDColor initialColor = new PDColor(new float[] { 0, 0, 0, 1 }, this);

   protected PDDeviceCMYK()
   {
   }

   @Override
   public String getName()
   {
      return COSName.DEVICECMYK.getName();
   }

   @Override
   public int getNumberOfComponents()
   {
      return 4;
   }

   @Override
   public float[] getDefaultDecode(int bitsPerComponent)
   {
      return new float[] { 0, 1, 0, 1, 0, 1, 0, 1 };
   }

   @Override
   public PDColor getInitialColor()
   {
      return initialColor;
   }

   @Override
   public float[] toRGB(float[] value) throws IOException
   {
      float c = value[0];
      float m = value[1];
      float y = value[2];
      float k = value[3];

      float r = 255 * (1 - c) * (1 - k);
      float g = 255 * (1 - m) * (1 - k);
      float b = 255 * (1 - y) * (1 - k);
      return new float[] { r, g, b };
   }

   @Override
   public Bitmap toRGBImage(Bitmap raster) throws IOException
   {
      return raster;
   }
}
