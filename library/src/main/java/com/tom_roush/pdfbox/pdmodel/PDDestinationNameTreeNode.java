package com.tom_roush.pdfbox.pdmodel;

import java.io.IOException;

import com.tom_roush.pdfbox.cos.COSBase;
import com.tom_roush.pdfbox.cos.COSDictionary;
import com.tom_roush.pdfbox.cos.COSName;
import com.tom_roush.pdfbox.pdmodel.common.COSObjectable;
import com.tom_roush.pdfbox.pdmodel.common.PDNameTreeNode;
import com.tom_roush.pdfbox.pdmodel.interactive.documentnavigation.destination.PDDestination;
import com.tom_roush.pdfbox.pdmodel.interactive.documentnavigation.destination.PDPageDestination;

/**
 * This class holds all of the name trees that are available at the document level.
 *
 * @author Ben Litchfield
 */
public class PDDestinationNameTreeNode extends PDNameTreeNode
{

    /**
     * Constructor.
     */
    public PDDestinationNameTreeNode()
    {
        super( PDPageDestination.class );
    }

    /**
     * Constructor.
     *
     * @param dic The COS dictionary.
     */
    public PDDestinationNameTreeNode( COSDictionary dic )
    {
        super( dic, PDPageDestination.class );
    }

    /**
     * {@inheritDoc}
     */
    protected COSObjectable convertCOSToPD( COSBase base ) throws IOException
    {
        COSBase destination = base;
        if( base instanceof COSDictionary )
        {
            //the destination is sometimes stored in the D dictionary
            //entry instead of being directly an array, so just dereference
            //it for now
            destination = ((COSDictionary)base).getDictionaryObject( COSName.D );
        }
        return PDDestination.create( destination );
    }

    /**
     * {@inheritDoc}
     */
    protected PDNameTreeNode createChildNode( COSDictionary dic )
    {
        return new PDDestinationNameTreeNode(dic);
    }
}
