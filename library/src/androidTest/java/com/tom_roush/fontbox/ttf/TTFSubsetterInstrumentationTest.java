/*
 * Copyright 2015 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.tom_roush.fontbox.ttf;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import com.tom_roush.pdfbox.android.PDFBoxResourceLoader;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 *
 * @author Tilman Hausherr
 */
public class TTFSubsetterInstrumentationTest
{

    private Context testContext;

    @Before
    public void setUp() throws Exception
    {
        testContext = InstrumentationRegistry.getInstrumentation().getContext();
        PDFBoxResourceLoader.init(testContext);
    }

    /**
     * Test of PDFBOX-3757: check that PostScript names that are not part of WGL4Names don't get
     * shuffled in buildPostTable().
     *
     * @throws java.io.IOException
     */
    @Test
    public void testPDFBox3757() throws IOException
    {
        InputStream testFile = testContext.getAssets().open("fontbox/ttf/LiberationSans-Regular.ttf");

        TrueTypeFont ttf = new TTFParser().parse(testFile);
        TTFSubsetter ttfSubsetter = new TTFSubsetter(ttf);
        ttfSubsetter.add('\u00D6'); // 'Ö' doesn't work with jdk6 (PDFBOX-3757)?
        ttfSubsetter.add('\u200A');
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ttfSubsetter.writeToStream(baos);
        TrueTypeFont subset = new TTFParser(true).parse(new ByteArrayInputStream(baos.toByteArray()));

        assertEquals(5, subset.getNumberOfGlyphs());

        assertEquals(0, subset.nameToGID(".notdef"));
        assertEquals(1, subset.nameToGID("O"));
        assertEquals(2, subset.nameToGID("Odieresis"));
        assertEquals(3, subset.nameToGID("uni200A"));
        assertEquals(4, subset.nameToGID("dieresis.uc"));

        PostScriptTable pst = subset.getPostScript();
        assertEquals(pst.getName(0), ".notdef");
        assertEquals(pst.getName(1), "O");
        assertEquals(pst.getName(2), "Odieresis");
        assertEquals(pst.getName(3), "uni200A");
        assertEquals(pst.getName(4), "dieresis.uc");

        assertTrue("Hair space path should be empty", subset.getPath("uni200A").isEmpty());
        assertFalse("UC dieresis path should not be empty", subset.getPath("dieresis.uc").isEmpty());

        subset.close();
    }
}
