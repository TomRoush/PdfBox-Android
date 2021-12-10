/*

   Licensed to the Apache Software Foundation (ASF) under one or more
   contributor license agreements.  See the NOTICE file distributed with
   this work for additional information regarding copyright ownership.
   The ASF licenses this file to You under the Apache License, Version 2.0
   (the "License"); you may not use this file except in compliance with
   the License.  You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

 */
package com.tom_roush.pdfbox.rendering;

import android.graphics.Path;
import android.util.Log;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.tom_roush.fontbox.ttf.HeaderTable;
import com.tom_roush.fontbox.ttf.TrueTypeFont;
import com.tom_roush.harmony.awt.geom.AffineTransform;
import com.tom_roush.pdfbox.pdmodel.font.PDCIDFontType2;
import com.tom_roush.pdfbox.pdmodel.font.PDFont;
import com.tom_roush.pdfbox.pdmodel.font.PDTrueTypeFont;
import com.tom_roush.pdfbox.pdmodel.font.PDType0Font;
import com.tom_roush.pdfbox.pdmodel.font.PDVectorFont;

/**
 * This class provides a glyph to Path conversion for TrueType and OpenType fonts.
 */
final class TTFGlyph2D implements Glyph2D
{
    private final PDFont font;
    private final TrueTypeFont ttf;
    private PDVectorFont vectorFont;
    private float scale = 1.0f;
    private boolean hasScaling;
    private final Map<Integer, Path> glyphs = new HashMap<Integer, Path>();
    private final boolean isCIDFont;

    /**
     * Constructor.
     *
     * @param ttfFont TrueType font
     */
    TTFGlyph2D(PDTrueTypeFont ttfFont) throws IOException
    {
        this(ttfFont.getTrueTypeFont(), ttfFont, false);
        vectorFont = ttfFont;
    }

    /**
     * Constructor.
     *
     * @param type0Font Type0 font, with CIDFontType2 descendant
     */
    TTFGlyph2D(PDType0Font type0Font) throws IOException
    {
        this(((PDCIDFontType2)type0Font.getDescendantFont()).getTrueTypeFont(), type0Font, true);
        vectorFont = type0Font;
    }

    private TTFGlyph2D(TrueTypeFont ttf, PDFont font, boolean isCIDFont) throws IOException
    {
        this.font = font;
        this.ttf = ttf;
        this.isCIDFont = isCIDFont;
        // get units per em, which is used as scaling factor
        HeaderTable header = this.ttf.getHeader();
        if (header != null && header.getUnitsPerEm() != 1000)
        {
            // in most case the scaling factor is set to 1.0f
            // due to the fact that units per em is set to 1000
            scale = 1000f / header.getUnitsPerEm();
            hasScaling = true;
        }
    }

    @Override
    public Path getPathForCharacterCode(int code) throws IOException
    {
        int gid = getGIDForCharacterCode(code);
        return getPathForGID(gid, code);
    }

    // Try to map the given code to the corresponding glyph-ID
    private int getGIDForCharacterCode(int code) throws IOException
    {
        if (isCIDFont)
        {
            return ((PDType0Font)font).codeToGID(code);
        }
        else
        {
            return ((PDTrueTypeFont)font).codeToGID(code);
        }
    }

    /**
     * Returns the path describing the glyph for the given glyphId.
     *
     * @param gid the GID
     * @param code the character code
     *
     * @return the Path for the given glyphId
     */
    public Path getPathForGID(int gid, int code) throws IOException
    {
        if (gid == 0 && !isCIDFont && code == 10 && font.isStandard14())
        {
            // PDFBOX-4001 return empty path for line feed on std14
            // need to catch this early because all "bad" glyphs have gid 0
            Log.w("PdfBox-Android", "No glyph for code " + code + " in font " + font.getName());
            return new Path();
        }
        Path glyphPath = glyphs.get(gid);
        if (glyphPath == null)
        {
            if (gid == 0 || gid >= ttf.getMaximumProfile().getNumGlyphs())
            {
                if (isCIDFont)
                {
                    int cid = ((PDType0Font) font).codeToCID(code);
                    String cidHex = String.format("%04x", cid);
                    Log.w("PdfBox-Android", "No glyph for code " + code + " (CID " + cidHex + ") in font " +
                        font.getName());
                }
                else
                {
                    Log.w("PdfBox-Android", "No glyph for " + code + " in font " + font.getName());
                }
            }

            Path glyph = vectorFont.getPath(code);

            // Acrobat only draws GID 0 for embedded or "Standard 14" fonts, see PDFBOX-2372
            if (gid == 0 && !font.isEmbedded() && !font.isStandard14())
            {
                glyph = null;
            }

            if (glyph == null)
            {
                // empty glyph (e.g. space, newline)
                glyphPath = new Path();
//                glyphs.put(gid, glyphPath); TODO: PdfBox-Android
            }
            else
            {
                glyphPath = glyph;
                if (hasScaling)
                {
                    AffineTransform atScale = AffineTransform.getScaleInstance(scale, scale);
                    glyphPath.transform(atScale.toMatrix());
                }
//                glyphs.put(gid, glyphPath); TODO: PdfBox-Android
            }
        }
        // todo: expensive
        return new Path(glyphPath);
    }

    @Override
    public void dispose()
    {
        glyphs.clear();
    }
}
