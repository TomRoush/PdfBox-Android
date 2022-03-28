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
import android.graphics.Color;
import android.util.Log;

import java.io.IOException;

import com.tom_roush.pdfbox.cos.COSName;

/**
 * A color space with black, white, and intermediate shades of gray.
 *
 * @author Ben Litchfield
 * @author John Hewson
 */
public final class PDDeviceGray extends PDDeviceColorSpace
{
    /** The single instance of this class. */
    public static final PDDeviceGray INSTANCE = new PDDeviceGray();

    private final PDColor initialColor = new PDColor(new float[] { 0 }, this);

    private PDDeviceGray()
    {
    }

    @Override
    public String getName()
    {
        return COSName.DEVICEGRAY.getName();
    }

    @Override
    public int getNumberOfComponents()
    {
        return 1;
    }

    @Override
    public float[] getDefaultDecode(int bitsPerComponent)
    {
        return new float[] { 0, 1 };
    }

    @Override
    public PDColor getInitialColor()
    {
        return initialColor;
    }

    @Override
    public float[] toRGB(float[] value)
    {
        return new float[] { value[0], value[0], value[0] };
    }

    @Override
    public Bitmap toRGBImage(Bitmap raster) throws IOException
    {
        if (raster.getConfig() != Bitmap.Config.ALPHA_8)
        {
            Log.e("PdfBox-Android", "Raster in PDDevicGrey was not ALPHA_8");
        }

        int width = raster.getWidth();
        int height = raster.getHeight();
        int[] imgPixels = new int[width];

        Bitmap image = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        int[] outPixels = new int[width];

        int gray;
        int rgb;
        for (int y = 0; y < height; y++)
        {
            raster.getPixels(imgPixels, 0, width, 0, y, width, 1);
            for (int x = 0; x < width; x++)
            {
                gray = Color.alpha(imgPixels[x]);
                rgb = Color.argb(255, gray, gray, gray);
                outPixels[x] = rgb;
            }
            image.setPixels(outPixels, 0, width, 0, y, width, 1);
        }

        return image;
    }
}
