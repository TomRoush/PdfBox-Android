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
package com.tom_roush.pdfbox.android;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;

public class PDFBoxResourceLoader
{

    /**
     * The AssetManager used to load the resources
     */
    private static AssetManager ASSET_MANAGER = null;

    /**
     * Initializes the loader
     *
     * @param context the context of the calling app
     */
    public static void init(Context context)
    {
        ASSET_MANAGER = context.getApplicationContext().getAssets();
    }

    /**
     * Checks whether the loader has been initialized
     *
     * @return whether the loader has been initialized or not
     */
    public static boolean isReady()
    {
        return ASSET_MANAGER != null;
    }

    /**
     * Loads a resource file located in the assets folder
     *
     * @param path the path to the resource
     *
     * @return the resource as an InputStream
     * @throws IOException if the resource cannot be found
     */
    public static InputStream getStream(String path) throws IOException
    {
        if (ASSET_MANAGER == null)
        {
            Log.e("PdfBox-Android",
                "PDFBoxResourceLoader is not initialized, call PDFBoxResourceLoader.init() before use");
        }
        return ASSET_MANAGER.open(path);
    }
}
