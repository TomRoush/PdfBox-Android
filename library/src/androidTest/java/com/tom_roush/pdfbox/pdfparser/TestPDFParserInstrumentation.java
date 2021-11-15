package com.tom_roush.pdfbox.pdfparser;

import android.content.Context;
import android.util.Log;

import androidx.test.platform.app.InstrumentationRegistry;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import com.tom_roush.pdfbox.android.PDFBoxResourceLoader;
import com.tom_roush.pdfbox.io.IOUtils;
import com.tom_roush.pdfbox.pdmodel.PDDocument;
import com.tom_roush.pdfbox.rendering.PDFRenderer;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

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
        File pdfFile = new File(testContext.getCacheDir(),
            "PDFBOX-3950-23EGDHXSBBYQLKYOKGZUOVYVNE675PRD.pdf");

        if (!pdfFile.exists())
        {
            try
            {
                Log.i("PdfBox-Android", "PDF not cached, Downloading PDF for TestPDFParser.testPDFBox3950");
                InputStream pdfUrlStream = new URL(
                    "https://issues.apache.org/jira/secure/attachment/12890042/23EGDHXSBBYQLKYOKGZUOVYVNE675PRD.pdf")
                    .openStream();
                IOUtils.copy(pdfUrlStream, new FileOutputStream(pdfFile));
            }
            catch (Exception e)
            {
                Log.w("PdfBox-Android", "Unable to download test PDF. Skipping test TestPDFParser.testPDFBox3950");
                return;
            }
        }

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
