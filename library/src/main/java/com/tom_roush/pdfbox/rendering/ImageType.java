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
package com.tom_roush.pdfbox.rendering;

import android.graphics.Bitmap;

/**
 * Image type for rendering.
 */
public enum ImageType
{
    /** Black or white. */
    BINARY
        {
            @Override
            Bitmap.Config toBitmapConfig()
            {
                return Bitmap.Config.ALPHA_8; // TODO: PdfBox-Android Need to take care with this
            }
        },

    /** Shades of gray */
    GRAY
        {
            @Override
            Bitmap.Config toBitmapConfig()
            {
                return Bitmap.Config.ALPHA_8;
            }
        },

    /** Red, Green, Blue */
    RGB
        {
            @Override // TODO: PdfBox-Android 565?
            Bitmap.Config toBitmapConfig()
            {
                return Bitmap.Config.ARGB_8888;
            }
        },

    /** Alpha, Red, Green, Blue */
    ARGB
        {
            @Override
            Bitmap.Config toBitmapConfig()
            {
                return Bitmap.Config.ARGB_8888;
            }
        },

    /** Blue, Green, Red */
    BGR
        {
            @Override
            Bitmap.Config toBitmapConfig()
            {
                return Bitmap.Config.ARGB_8888;
            }
        };

    abstract Bitmap.Config toBitmapConfig();
}
