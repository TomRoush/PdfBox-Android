package org.apache.pdfbox.pdmodel.common;

import org.apache.pdfbox.cos.COSBase;

/**
 * This is an interface used to get/create the underlying COSObject.
 *
 * @author <a href="mailto:ben@benlitchfield.com">Ben Litchfield</a>
 * @version $Revision: 1.3 $
 */
public interface COSObjectable
{
    /**
     * Convert this standard java object to a COS object.
     *
     * @return The cos object that matches this Java object.
     */
    COSBase getCOSObject();
}
