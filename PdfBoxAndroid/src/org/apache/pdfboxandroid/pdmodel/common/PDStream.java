package org.apache.pdfboxandroid.pdmodel.common;

import java.io.IOException;
import java.io.InputStream;

import org.apache.pdfboxandroid.cos.COSBase;
import org.apache.pdfboxandroid.cos.COSStream;

/**
 * A PDStream represents a stream in a PDF document. Streams are tied to a
 * single PDF document.
 * 
 * @author <a href="mailto:ben@benlitchfield.com">Ben Litchfield</a>
 * @version $Revision: 1.17 $
 */
public class PDStream implements COSObjectable {
	private COSStream stream;
	
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
     * Convert this standard java object to a COS object.
     * 
     * @return The cos object that matches this Java object.
     */
    public COSBase getCOSObject()
    {
        return stream;
    }
}
