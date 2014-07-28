package org.apache.pdfbox.pdmodel.common;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSStream;
import org.apache.pdfbox.pdmodel.PDDocument;

/**
 * A PDStream represents a stream in a PDF document. Streams are tied to a
 * single PDF document.
 * 
 * @author <a href="mailto:ben@benlitchfield.com">Ben Litchfield</a>
 * @version $Revision: 1.17 $
 */
public class PDStream implements COSObjectable
{

	private COSStream stream;

    /**
     * This will create a new PDStream object.
     */
    protected PDStream()
    {
        // should only be called by PDMemoryStream
    }
    
    /**
     * This will create a new PDStream object.
     * 
     * @param document
     *            The document that the stream will be part of.
     */
    public PDStream(PDDocument document)
    {
        stream = document.getDocument().createCOSStream();
    }
    
    /**
     * Constructor.
     * 
     * @param str
     *            The stream parameter.
     */
    public PDStream(COSStream str)
    {
        stream = str;
    }
    
    /**
     * Constructor. Reads all data from the input stream and embeds it into the
     * document, this will close the InputStream.
     * 
     * @param doc
     *            The document that will hold the stream.
     * @param str
     *            The stream parameter.
     * @throws IOException
     *             If there is an error creating the stream in the document.
     */
    public PDStream(PDDocument doc, InputStream str) throws IOException
    {
        this(doc, str, false);
    }
    
    /**
     * Constructor. Reads all data from the input stream and embeds it into the
     * document, this will close the InputStream.
     * 
     * @param doc
     *            The document that will hold the stream.
     * @param str
     *            The stream parameter.
     * @param filtered
     *            True if the stream already has a filter applied.
     * @throws IOException
     *             If there is an error creating the stream in the document.
     */
    public PDStream(PDDocument doc, InputStream str, boolean filtered)
            throws IOException
    {
        OutputStream output = null;
        try
        {
            stream = doc.getDocument().createCOSStream();
            if (filtered)
            {
                output = stream.createFilteredStream();
            } 
            else
            {
                output = stream.createUnfilteredStream();
            }
            byte[] buffer = new byte[1024];
            int amountRead = -1;
            while ((amountRead = str.read(buffer)) != -1)
            {
                output.write(buffer, 0, amountRead);
            }
        } 
        finally
        {
            if (output != null)
            {
                output.close();
            }
            if (str != null)
            {
                str.close();
            }
        }
    }
    
    /**
     * Convert this standard java object to a COS object.
     * 
     * @return The cos object that matches this Java object.
     */
    public COSBase getCOSObject()
    {
        return stream;
    }
    
    /**
     * This will get a stream that can be read from.
     * 
     * @return An input stream that can be read from.
     * 
     * @throws IOException
     *             If an IO error occurs during reading.
     */
    public InputStream createInputStream() throws IOException
    {
        return stream.getUnfilteredStream();
    }
    
    /**
     * Get the cos stream associated with this object.
     * 
     * @return The cos object that matches this Java object.
     */
    public COSStream getStream()
    {
        return stream;
    }

}
