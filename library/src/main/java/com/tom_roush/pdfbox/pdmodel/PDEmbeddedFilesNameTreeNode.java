package org.apache.pdfbox.pdmodel;

import java.io.IOException;

import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.pdmodel.common.COSObjectable;
import org.apache.pdfbox.pdmodel.common.PDNameTreeNode;
import org.apache.pdfbox.pdmodel.common.filespecification.PDComplexFileSpecification;

/**
 * This class holds all of the name trees that are available at the document level.
 *
 * @author Ben Litchfield
 */
public class PDEmbeddedFilesNameTreeNode extends PDNameTreeNode
{
    /**
     * Constructor.
     */
    public PDEmbeddedFilesNameTreeNode()
    {
        super( PDComplexFileSpecification.class );
    }

    /**
     * Constructor.
     *
     * @param dic The COS dictionary.
     */
    public PDEmbeddedFilesNameTreeNode( COSDictionary dic )
    {
        super( dic, PDComplexFileSpecification.class );
    }

    /**
     * {@inheritDoc}
     */
    protected COSObjectable convertCOSToPD( COSBase base ) throws IOException
    {
        return new PDComplexFileSpecification( (COSDictionary)base );
    }

    /**
     * {@inheritDoc}
     */
    protected PDNameTreeNode createChildNode( COSDictionary dic )
    {
        return new PDEmbeddedFilesNameTreeNode(dic);
    }
}
