package com.tom_roush.pdfbox.pdmodel;

import com.tom_roush.pdfbox.cos.COSBase;
import com.tom_roush.pdfbox.cos.COSDictionary;
import com.tom_roush.pdfbox.pdmodel.common.PDNameTreeNode;
import com.tom_roush.pdfbox.pdmodel.common.filespecification.PDComplexFileSpecification;

import java.io.IOException;

/**
 * This class holds all of the name trees that are available at the document level.
 *
 * @author Ben Litchfield
 */
public class PDEmbeddedFilesNameTreeNode extends PDNameTreeNode<PDComplexFileSpecification>
{
    /**
     * Constructor.
     */
    public PDEmbeddedFilesNameTreeNode()
    {
        super();
    }

    /**
     * Constructor.
     *
     * @param dic The COS dictionary.
     */
    public PDEmbeddedFilesNameTreeNode( COSDictionary dic )
    {
        super(dic);
    }

    @Override
    protected PDComplexFileSpecification convertCOSToPD(COSBase base) throws IOException
    {
        return new PDComplexFileSpecification( (COSDictionary)base );
    }

    @Override
    protected PDNameTreeNode createChildNode( COSDictionary dic )
    {
        return new PDEmbeddedFilesNameTreeNode(dic);
    }
}
