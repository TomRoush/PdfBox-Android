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

import android.graphics.Path;
import android.util.Log;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.tom_roush.pdfbox.pdmodel.font.PDSimpleFont;

/**
 * Glyph to GeneralPath conversion for Type 1 PFB and CFF, and TrueType fonts with a 'post' table.
 */
final class Type1Glyph2D implements Glyph2D
{
    private final Map<Integer, Path> cache = new HashMap<Integer, Path>();
    private final PDSimpleFont font;

    /**
     * Constructor.
     *
     * @param font PDF Type1 font.
     */
    Type1Glyph2D(PDSimpleFont font)
    {
        this.font = font;
    }
    @Override
    public Path getPathForCharacterCode(int code)
    {
        // cache
        if (cache.containsKey(code))
        {
            return cache.get(code);
        }
        // fetch
        try
        {
            String name = font.getEncoding().getName(code);
            if (!font.hasGlyph(name))
            {
                Log.w("PdfBox-Android", "No glyph for " + code + " (" + name + ") in font " + font.getName());
            }
            // todo: can this happen? should it be encapsulated?
            Path path = font.getPath(name);
            if (path == null)
            {
                path = font.getPath(".notdef");
            }
            cache.put(code, path);
            return path;
        }
        catch (IOException e)
        {
            Log.e("PdfBox-Android", "Glyph rendering failed", e); // todo: escalate this error?
            return new Path();
        }
    }
    @Override
    public void dispose()
    {
        cache.clear();
    }
}
