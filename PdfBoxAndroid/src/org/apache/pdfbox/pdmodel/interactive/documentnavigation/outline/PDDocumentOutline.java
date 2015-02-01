package org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline;

import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;

/**
 * This represents an outline in a pdf document.
 *
 * @author <a href="mailto:ben@benlitchfield.com">Ben Litchfield</a>
 * @version $Revision: 1.2 $
 */
public class PDDocumentOutline extends PDOutlineNode
{

    /**
     * Default Constructor.
     */
    public PDDocumentOutline()
    {
        super();
        node.setName( COSName.TYPE, "Outlines" );
    }

    /**
     * Constructor for an existing document outline.
     *
     * @param dic The storage dictionary.
     */
    public PDDocumentOutline( COSDictionary dic )
    {
        super( dic );
    }
}
