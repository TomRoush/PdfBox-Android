package org.apache.pdfboxandroid.pdmodel.interactive.annotation;

import org.apache.pdfboxandroid.cos.COSDictionary;

/**
 * This is the class that represents a widget.
 *
 * @author <a href="mailto:ben@benlitchfield.com">Ben Litchfield</a>
 * @version $Revision: 1.3 $
 */
public class PDAnnotationWidget extends PDAnnotation {
	/**
     * The type of annotation.
     */
    public static final String SUB_TYPE = "Widget";
    
    /**
     * Creates a PDWidget from a COSDictionary, expected to be
     * a correct object definition for a field in PDF.
     *
     * @param field the PDF objet to represent as a field.
     */
    public PDAnnotationWidget(COSDictionary field)
    {
        super( field );
    }
}
