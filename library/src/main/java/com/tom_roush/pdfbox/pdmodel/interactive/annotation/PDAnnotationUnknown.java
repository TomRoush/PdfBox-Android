package com.tom_roush.pdfbox.pdmodel.interactive.annotation;

import com.tom_roush.pdfbox.cos.COSDictionary;

/**
 * This is the class that represents an arbitary Unknown Annotation type.
 *
 * @author Paul King
 */
public class PDAnnotationUnknown extends PDAnnotation
{

   /**
     * Creates an arbitary annotation from a COSDictionary, expected to be
     * a correct object definition for some sort of annotation.
     *
     * @param dic The dictionary which represents this Annotation.
     */
    public PDAnnotationUnknown(COSDictionary dic)
    {
        super( dic );
    }
}
