package com.tom_roush.pdfbox.pdfparser;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;

import java.io.File;
import java.io.IOException;

import com.tom_roush.pdfbox.android.PDFBoxResourceLoader;
import com.tom_roush.pdfbox.android.TestResourceGenerator;
import com.tom_roush.pdfbox.pdmodel.PDDocument;
import com.tom_roush.pdfbox.rendering.PDFRenderer;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assume.assumeTrue;

public class TestPDFParserInstrumentation
{
    private Context testContext;

    @Before
    public void setUp() throws IOException
    {
        testContext = InstrumentationRegistry.getInstrumentation().getContext();
        PDFBoxResourceLoader.init(testContext);
    }

    /**
     * PDFBOX-3950: test parsing and rendering of truncated file with missing pages.
     *
     * @throws IOException
     */
    @Test
    public void testPDFBox3950() throws IOException
    {
        File TARGETPDFDIR = new File(testContext.getCacheDir(), "pdfs");
        TARGETPDFDIR.mkdirs();
        File pdfFile = TestResourceGenerator.downloadTestResource(TARGETPDFDIR, "PDFBOX-3950-23EGDHXSBBYQLKYOKGZUOVYVNE675PRD.pdf", "https://issues.apache.org/jira/secure/attachment/12890042/23EGDHXSBBYQLKYOKGZUOVYVNE675PRD.pdf");
        assumeTrue(pdfFile.exists());

        PDDocument doc = PDDocument.load(pdfFile);
        assertEquals(4, doc.getNumberOfPages());
        PDFRenderer renderer = new PDFRenderer(doc);
        for (int i = 0; i < doc.getNumberOfPages(); ++i)
        {
            try
            {
                renderer.renderImage(i);
            }
            catch (IOException ex)
            {
                if (i == 3 && ex.getMessage().equals("Missing descendant font array"))
                {
                    continue;
                }
                throw ex;
            }
        }
        doc.close();
    }
}
