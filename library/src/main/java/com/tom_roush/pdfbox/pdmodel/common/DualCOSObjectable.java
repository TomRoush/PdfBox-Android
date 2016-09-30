package org.apache.pdfbox.pdmodel.common;

import org.apache.pdfbox.cos.COSBase;

/**
 * This is an interface to represent a PDModel object that holds two COS objects.
 *
 * @author Ben Litchfield
 */
public interface DualCOSObjectable
{
    /**
     * Convert this standard java object to a COS object.
     *
     * @return The cos object that matches this Java object.
     */
    COSBase getFirstCOSObject();

    /**
     * Convert this standard java object to a COS object.
     *
     * @return The cos object that matches this Java object.
     */
    COSBase getSecondCOSObject();
}
