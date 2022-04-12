/*****************************************************************************
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 ****************************************************************************/

package com.tom_roush.pdfbox.pdfparser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URISyntaxException;

import com.tom_roush.pdfbox.cos.COSDocument;
import com.tom_roush.pdfbox.io.MemoryUsageSetting;
import com.tom_roush.pdfbox.io.RandomAccessBufferedFileInputStream;
import com.tom_roush.pdfbox.io.RandomAccessRead;
import com.tom_roush.pdfbox.io.ScratchFile;
import com.tom_roush.pdfbox.pdmodel.PDDocument;
import com.tom_roush.pdfbox.pdmodel.PDDocumentInformation;
import com.tom_roush.pdfbox.pdmodel.interactive.documentnavigation.outline.PDDocumentOutline;
import com.tom_roush.pdfbox.pdmodel.interactive.documentnavigation.outline.PDOutlineItem;
import com.tom_roush.pdfbox.util.DateConverter;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assume.assumeTrue;

public class TestPDFParser
{
    private static final String PATH_OF_PDF = "src/test/resources/pdfbox/input/yaddatest.pdf";
    private static final File tmpDirectory = new File(System.getProperty("java.io.tmpdir"));
    private static final File TARGETPDFDIR = new File("target/pdfs");

    private int numberOfTmpFiles = 0;

    /**
     * Initialize the number of tmp file before the test
     *
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception
    {
        numberOfTmpFiles = getNumberOfTempFile();
    }

    /**
     * Count the number of temporary files
     *
     * @return
     */
    private int getNumberOfTempFile()
    {
        int result = 0;
        File[] tmpPdfs = tmpDirectory.listFiles(new FilenameFilter()
        {
            @Override
            public boolean accept(File dir, String name)
            {
                return name.startsWith(COSParser.TMP_FILE_PREFIX)
                    && name.endsWith("pdf");
            }
        });

        if (tmpPdfs != null)
        {
            result = tmpPdfs.length;
        }

        return result;
    }

    @Test
    public void testPDFParserFile() throws IOException
    {
        executeParserTest(new RandomAccessBufferedFileInputStream(new File(PATH_OF_PDF)), MemoryUsageSetting.setupMainMemoryOnly());
    }

    @Test
    public void testPDFParserInputStream() throws IOException
    {
        executeParserTest(new RandomAccessBufferedFileInputStream(new FileInputStream(PATH_OF_PDF)), MemoryUsageSetting.setupMainMemoryOnly());
    }

    @Test
    public void testPDFParserFileScratchFile() throws IOException
    {
        executeParserTest(new RandomAccessBufferedFileInputStream(new File(PATH_OF_PDF)), MemoryUsageSetting.setupTempFileOnly());
    }

    @Test
    public void testPDFParserInputStreamScratchFile() throws IOException
    {
        executeParserTest(new RandomAccessBufferedFileInputStream(new FileInputStream(PATH_OF_PDF)), MemoryUsageSetting.setupTempFileOnly());
    }

    @Test
    public void testPDFParserMissingCatalog() throws IOException, URISyntaxException
    {
        // PDFBOX-3060
        PDDocument.load(new File(TestPDFParser.class.getResource("/pdfbox/com/tom_roush/pdfbox/pdfparser/MissingCatalog.pdf").toURI())).close();
    }

    /**
     * Test whether /Info dictionary is retrieved correctly when rebuilding the trailer of a corrupt
     * file. An incorrect algorithm would result in an outline dictionary being mistaken for an
     * /Info.
     *
     * @throws IOException
     */
//    TODO: PdfBox-Android - provide test file
    @Test
    public void testPDFBox3208() throws IOException
    {
        File testPdf = new File(TARGETPDFDIR,"PDFBOX-3208-L33MUTT2SVCWGCS6UIYL5TH3PNPXHIS6.pdf");
        assumeTrue(testPdf.exists());
        PDDocument doc = PDDocument.load(testPdf);

        PDDocumentInformation di = doc.getDocumentInformation();
        assertEquals("Liquent Enterprise Services", di.getAuthor());
        assertEquals("Liquent services server", di.getCreator());
        assertEquals("Amyuni PDF Converter version 4.0.0.9", di.getProducer());
        assertEquals("", di.getKeywords());
        assertEquals("", di.getSubject());
        assertEquals("892B77DE781B4E71A1BEFB81A51A5ABC_20140326022424.docx", di.getTitle());
        assertEquals(DateConverter.toCalendar("D:20140326142505-02'00'"), di.getCreationDate());
        assertEquals(DateConverter.toCalendar("20140326172513Z"), di.getModificationDate());

        doc.close();
    }

    /**
     * Test whether the /Info is retrieved correctly when rebuilding the trailer of a corrupt file,
     * despite the /Info dictionary not having a modification date.
     *
     * @throws IOException
     */
//    TODO: PdfBox-Android - provide test file
    @Test
    public void testPDFBox3940() throws IOException
    {
        File testPdf = new File(TARGETPDFDIR,"PDFBOX-3940-079977.pdf");
        assumeTrue(testPdf.exists());
        PDDocument doc = PDDocument.load(testPdf);
        PDDocumentInformation di = doc.getDocumentInformation();
        assertEquals("Unknown", di.getAuthor());
        assertEquals("C:REGULA~1IREGSFR_EQ_EM.WP", di.getCreator());
        assertEquals("Acrobat PDFWriter 3.02 for Windows", di.getProducer());
        assertEquals("", di.getKeywords());
        assertEquals("", di.getSubject());
        assertEquals("C:REGULA~1IREGSFR_EQ_EM.PDF", di.getTitle());
        assertEquals(DateConverter.toCalendar("Tuesday, July 28, 1998 4:00:09 PM"), di.getCreationDate());

        doc.close();
    }

    /**
     * PDFBOX-3783: test parsing of file with trash after %%EOF.
     *
     * @throws IOException
     */
//    TODO: PdfBox-Android - provide test file
    @Test
    public void testPDFBox3783() throws IOException
    {
        File testPdf = new File(TARGETPDFDIR,"PDFBOX-3783-72GLBIGUC6LB46ELZFBARRJTLN4RBSQM.pdf");
        assumeTrue(testPdf.exists());
        PDDocument.load(testPdf).close();
    }

    /**
     * PDFBOX-3785, PDFBOX-3957:
     * Test whether truncated file with several revisions has correct page count.
     *
     * @throws IOException
     */
//    TODO: PdfBox-Android - provide test file
    @Test
    public void testPDFBox3785() throws IOException
    {
        File testPdf = new File(TARGETPDFDIR,"PDFBOX-3785-202097.pdf");
        assumeTrue(testPdf.exists());
        PDDocument doc = PDDocument.load(testPdf);
        assertEquals(11, doc.getNumberOfPages());
        doc.close();
    }

    /**
     * PDFBOX-3947: test parsing of file with broken object stream.
     *
     * @throws IOException
     */
//    TODO: PdfBox-Android - provide test file
    @Test
    public void testPDFBox3947() throws IOException
    {
        File testPdf = new File(TARGETPDFDIR, "PDFBOX-3947-670064.pdf");
        assumeTrue(testPdf.exists());
        PDDocument.load(testPdf).close();
    }

    /**
     * PDFBOX-3948: test parsing of file with object stream containing some unexpected newlines.
     *
     * @throws IOException
     */
//    TODO: PdfBox-Android - provide test file
    @Test
    public void testPDFBox3948() throws IOException
    {
        File testPdf = new File(TARGETPDFDIR, "PDFBOX-3948-EUWO6SQS5TM4VGOMRD3FLXZHU35V2CP2.pdf");
        assumeTrue(testPdf.exists());
        PDDocument.load(testPdf).close();
    }

    /**
     * PDFBOX-3949: test parsing of file with incomplete object stream.
     *
     * @throws IOException
     */
//    TODO: PdfBox-Android - provide test file
    @Test
    public void testPDFBox3949() throws IOException
    {
        File testPdf = new File(TARGETPDFDIR, "PDFBOX-3949-MKFYUGZWS3OPXLLVU2Z4LWCTVA5WNOGF.pdf");
        assumeTrue(testPdf.exists());
        PDDocument.load(testPdf).close();
    }

    // testPDFBox3950 is an instrumentation test

    /**
     * PDFBOX-3951: test parsing of truncated file.
     *
     * @throws IOException
     */
//    TODO: PdfBox-Android - provide test file
    @Test
    public void testPDFBox3951() throws IOException
    {
        File testPdf = new File(TARGETPDFDIR, "PDFBOX-3951-FIHUZWDDL2VGPOE34N6YHWSIGSH5LVGZ.pdf");
        assumeTrue(testPdf.exists());
        PDDocument doc = PDDocument.load(testPdf);
        assertEquals(143, doc.getNumberOfPages());
        doc.close();
    }

    /**
     * PDFBOX-3964: test parsing of broken file.
     *
     * @throws IOException
     */
//    TODO: PdfBox-Android - provide test file
    @Test
    public void testPDFBox3964() throws IOException
    {
        File testPdf = new File(TARGETPDFDIR, "PDFBOX-3964-c687766d68ac766be3f02aaec5e0d713_2.pdf");
        assumeTrue(testPdf.exists());
        PDDocument doc = PDDocument.load(testPdf);
        assertEquals(10, doc.getNumberOfPages());
        doc.close();
    }

    /**
     * Test whether /Info dictionary is retrieved correctly in brute force search for the
     * Info/Catalog dictionaries.
     *
     * @throws IOException
     */
//    TODO: PdfBox-Android - provide test file
    @Test
    public void testPDFBox3977() throws IOException
    {
        File testPdf = new File(TARGETPDFDIR,"PDFBOX-3977-63NGFQRI44HQNPIPEJH5W2TBM6DJZWMI.pdf");
        assumeTrue(testPdf.exists());
        PDDocument doc = PDDocument.load(testPdf);
        PDDocumentInformation di = doc.getDocumentInformation();
        assertEquals("QuarkXPress(tm) 6.52", di.getCreator());
        assertEquals("Acrobat Distiller 7.0 pour Macintosh", di.getProducer());
        assertEquals("Fich sal Fabr corr1 (Page 6)", di.getTitle());
        assertEquals(DateConverter.toCalendar("D:20070608151915+02'00'"), di.getCreationDate());
        assertEquals(DateConverter.toCalendar("D:20080604152122+02'00'"), di.getModificationDate());
        doc.close();
    }

    /**
     * Test parsing the "genko_oc_shiryo1.pdf" file, which is susceptible to regression.
     *
     * @throws IOException
     */
//    TODO: PdfBox-Android - provide test file
    @Test
    public void testParseGenko() throws IOException
    {
        File testPdf = new File(TARGETPDFDIR, "genko_oc_shiryo1.pdf");
        assumeTrue(testPdf.exists());
        PDDocument.load(testPdf).close();
    }

    /**
     * Test parsing the file from PDFBOX-4338, which brought an
     * ArrayIndexOutOfBoundsException before the bug was fixed.
     *
     * @throws IOException
     */
//    TODO: PdfBox-Android - provide test file
    @Test
    public void testPDFBox4338() throws IOException
    {
        File testPdf = new File(TARGETPDFDIR, "PDFBOX-4338.pdf");
        assumeTrue(testPdf.exists());
        PDDocument.load(testPdf).close();
    }

    /**
     * Test parsing the file from PDFBOX-4339, which brought a
     * NullPointerException before the bug was fixed.
     *
     * @throws IOException
     */
//    TODO: PdfBox-Android - provide test file
    @Test
    public void testPDFBox4339() throws IOException
    {
        File testPdf = new File(TARGETPDFDIR, "PDFBOX-4339.pdf");
        assumeTrue(testPdf.exists());
        PDDocument.load(testPdf).close();
    }

    /**
     * Test parsing the "WXMDXCYRWFDCMOSFQJ5OAJIAFXYRZ5OA.pdf" file, which is susceptible to
     * regression.
     *
     * @throws IOException
     */
//    TODO: PdfBox-Android - provide test file
    @Test
    public void testPDFBox4153() throws IOException
    {
        File testPdf = new File(TARGETPDFDIR, "PDFBOX-4153-WXMDXCYRWFDCMOSFQJ5OAJIAFXYRZ5OA.pdf");
        assumeTrue(testPdf.exists());
        PDDocument doc = PDDocument.load(testPdf);
        PDDocumentOutline documentOutline = doc.getDocumentCatalog().getDocumentOutline();
        PDOutlineItem firstChild = documentOutline.getFirstChild();
        assertEquals("Main Menu", firstChild.getTitle());
        doc.close();
    }

    /**
     * Test that PDFBOX-4490 has 3 pages.
     *
     * @throws IOException
     */
//    TODO: PdfBox-Android - provide test file
    @Test
    public void testPDFBox4490() throws IOException
    {
        File testPdf = new File(TARGETPDFDIR, "PDFBOX-4490.pdf");
        assumeTrue(testPdf.exists());
        PDDocument doc = PDDocument.load(testPdf);
        assertEquals(3, doc.getNumberOfPages());
        doc.close();
    }

    private void executeParserTest(RandomAccessRead source, MemoryUsageSetting memUsageSetting) throws IOException
    {
        ScratchFile scratchFile = new ScratchFile(memUsageSetting);
        PDFParser pdfParser = new PDFParser(source, scratchFile);
        pdfParser.parse();
        COSDocument doc = pdfParser.getDocument();
        assertNotNull(doc);
        doc.close();
        source.close();
        // number tmp file must be the same
        assertEquals(numberOfTmpFiles, getNumberOfTempFile());
    }

}
