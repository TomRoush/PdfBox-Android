package com.tom_roush.pdfbox.pdmodel.common;

import com.tom_roush.pdfbox.cos.COSBase;

/**
 * This is an interface used to get/create the underlying COSObject.
 *
 * @author Ben Litchfield
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
