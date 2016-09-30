package org.apache.pdfbox.pdmodel.common;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSStream;
import org.apache.pdfbox.pdmodel.PDDocument;

/**
 * This class represents metadata for various objects in a PDF document.
 *
 * @author Ben Litchfield
 */
public class PDMetadata extends PDStream
{

    /**
     * This will create a new PDMetadata object.
     *
     * @param document The document that the stream will be part of.
     */
    public PDMetadata( PDDocument document )
    {
        super( document );
        getStream().setName( COSName.TYPE, "Metadata" );
        getStream().setName( COSName.SUBTYPE, "XML" );
    }

    /**
     * Constructor.  Reads all data from the input stream and embeds it into the
     * document, this will close the InputStream.
     *
     * @param doc The document that will hold the stream.
     * @param str The stream parameter.
     * @param filtered True if the stream already has a filter applied.
     * @throws IOException If there is an error creating the stream in the document.
     */
    public PDMetadata( PDDocument doc, InputStream str, boolean filtered ) throws IOException
    {
        super( doc, str, filtered );
        getStream().setName( COSName.TYPE, "Metadata" );
        getStream().setName( COSName.SUBTYPE, "XML" );
    }

    /**
     * Constructor.
     *
     * @param str The stream parameter.
     */
    public PDMetadata( COSStream str )
    {
        super( str );
    }

    /**
     * Extract the XMP metadata.
     * To persist changes back to the PDF you must call importXMPMetadata.
     *
     * @return A stream to get the xmp data from.
     *
     * @throws IOException If there is an error parsing the XMP data.
     */
    public InputStream exportXMPMetadata() throws IOException
    {
        return createInputStream();
    }

    /**
     * Import an XMP stream into the PDF document.
     *
     * @param xmp The XMP data.
     *
     * @throws IOException If there is an error generating the XML document.
     */
    public void importXMPMetadata( byte[] xmp )
        throws IOException
    {
    	OutputStream os = createOutputStream();
    	os.write(xmp);
    	os.close();
    }
}
