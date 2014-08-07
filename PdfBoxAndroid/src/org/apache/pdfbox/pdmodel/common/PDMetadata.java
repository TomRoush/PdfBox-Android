package org.apache.pdfbox.pdmodel.common;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.pdfbox.cos.COSStream;
import org.apache.pdfbox.pdmodel.PDDocument;

/**
 * This class represents metadata for various objects in a PDF document.
 *
 * @author <a href="mailto:ben@benlitchfield.com">Ben Litchfield</a>
 * @version $Revision: 1.3 $
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
        getStream().setName( "Type", "Metadata" );
        getStream().setName( "Subtype", "XML" );
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
        getStream().setName( "Type", "Metadata" );
        getStream().setName( "Subtype", "XML" );
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
     * Extract the XMP metadata and create and build an in memory object.
     * To persist changes back to the PDF you must call importXMPMetadata.
     * 
     * @deprecated will return an InputStream in the future.
     *
     * @return A parsed XMP object.
     *
     * @throws IOException If there is an error parsing the XMP data.
     */
//    @Deprecated
//    public XMPMetadata exportXMPMetadata() throws IOException
//    {
//        return XMPMetadata.load( createInputStream() );
//    }

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
    
    
    /**
     * Import an XMP stream into the PDF document.
     *
     * @param xmp The XMP data.
     *
     * @throws IOException If there is an error generating the XML document.
     * @throws TransformerException If there is an error generating the XML document.
     */
//    @Deprecated
//    public void importXMPMetadata( XMPMetadata xmp )
//        throws IOException, TransformerException
//    {
//        xmp.save( createOutputStream() );
//    }
}
