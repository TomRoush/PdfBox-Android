package org.apache.pdfbox.pdmodel.common;

import java.io.IOException;
import java.io.InputStream;

import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSStream;

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
