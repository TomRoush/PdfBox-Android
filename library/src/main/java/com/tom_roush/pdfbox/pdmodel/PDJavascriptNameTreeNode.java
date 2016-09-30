package org.apache.pdfbox.pdmodel;

import java.io.IOException;

import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSStream;
import org.apache.pdfbox.cos.COSString;
import org.apache.pdfbox.pdmodel.common.COSObjectable;
import org.apache.pdfbox.pdmodel.common.PDNameTreeNode;
import org.apache.pdfbox.pdmodel.common.PDTextStream;

/**
 * This class holds all of the name trees that are available at the document level.
 *
 * @author Ben Litchfield
 */
public class PDJavascriptNameTreeNode extends PDNameTreeNode
{
    /**
     * Constructor.
     */
    public PDJavascriptNameTreeNode()
    {
        super( PDTextStream.class );
    }

    /**
     * Constructor.
     *
     * @param dic The COS dictionary.
     */
    public PDJavascriptNameTreeNode( COSDictionary dic )
    {
        super( dic, PDTextStream.class );
    }

    /**
     * {@inheritDoc}
     */
    protected COSObjectable convertCOSToPD( COSBase base ) throws IOException
    {
        PDTextStream stream = null;
        if( base instanceof COSString )
        {
            stream = new PDTextStream((COSString)base);
        }
        else if( base instanceof COSStream )
        {
            stream = new PDTextStream((COSStream)base);
        }
        else
        {
            throw new IOException( "Error creating Javascript object, expected either COSString or COSStream and not " 
                    + base );
        }
        return stream;
    }

    /**
     * {@inheritDoc}
     */
    protected PDNameTreeNode createChildNode( COSDictionary dic )
    {
        return new PDJavascriptNameTreeNode(dic);
    }
}
