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
package com.tom_roush.fontbox.cmap;

import java.io.File;
import java.io.IOException;

import com.tom_roush.fontbox.ttf.CmapLookup;
import com.tom_roush.fontbox.ttf.TTFParser;
import com.tom_roush.fontbox.ttf.TrueTypeFont;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assume.assumeTrue;

/**
 * This will test the CMap implementation.
 *
 */
public class TestCMap
{

    /**
     * Check whether the mapping is working correct.
     * @throws IOException If something went wrong during adding a mapping
     */
    @Test
    public void testLookup() throws IOException
    {
        byte[] bs = new byte[1];
        bs[0] = (byte)200;

        CMap cMap = new CMap();
        cMap.addCharMapping(bs, "a");
        Assert.assertTrue("a".equals(cMap.toUnicode(200)));
    }

    /**
     * PDFBOX-3997: test unicode that is above the basic multilingual plane, here: helicopter
     * symbol, or D83D DE81 in the Noto Emoji font.
     *
     * @throws IOException
     */
    @Test
//    TODO: PdfBox-Android - provide test file
    public void testPDFBox3997() throws IOException
    {
        String fontPath = "target/pdfs/NotoEmoji-Regular.ttf";
        assumeTrue(new File(fontPath).exists());
        TrueTypeFont ttf = new TTFParser().parse(fontPath);
        CmapLookup cmap = ttf.getUnicodeCmapLookup(false);
        Assert.assertEquals(886, cmap.getGlyphId(0x1F681));
        ttf.close();
    }
}
