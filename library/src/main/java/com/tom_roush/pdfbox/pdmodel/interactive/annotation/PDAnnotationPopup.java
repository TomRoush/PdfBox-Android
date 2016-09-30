package org.apache.pdfbox.pdmodel.interactive.annotation;

import java.io.IOException;

import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;

/**
 * This is the class that represents a popup annotation.
 * Introduced in PDF 1.3 specification
 *
 * @author Paul King
 */
public class PDAnnotationPopup extends PDAnnotation
{

    /**
     * The type of annotation.
     */
    public static final String SUB_TYPE = "Popup";

    /**
     * Constructor.
     */
    public PDAnnotationPopup()
    {
        super();
        getDictionary()
                .setItem( COSName.SUBTYPE, COSName.getPDFName( SUB_TYPE ) );
    }

    /**
     * Creates a popup annotation from a COSDictionary, expected to be a correct
     * object definition.
     *
     * @param field
     *            the PDF objet to represent as a field.
     */
    public PDAnnotationPopup( COSDictionary field )
    {
        super( field );
    }

    /**
     * This will set inital state of the annotation, open or closed.
     *
     * @param open
     *            Boolean value, true = open false = closed.
     */
    public void setOpen( boolean open )
    {
        getDictionary().setBoolean( "Open" , open );
    }

    /**
     * This will retrieve the initial state of the annotation, open Or closed
     * (default closed).
     *
     * @return The initial state, true = open false = closed.
     */
    public boolean getOpen()
    {
        return getDictionary().getBoolean( "Open" , false );
    }

    /**
     * This will set the markup annotation which this popup relates to.
     *
     * @param annot
     *            the markup annotation.
     */
    public void setParent( PDAnnotationMarkup annot )
    {
        getDictionary().setItem( COSName.PARENT, annot.getDictionary() );
    }

    /**
     * This will retrieve the markup annotation which this popup relates to.
     *
     * @return The parent markup annotation.
     */
    public PDAnnotationMarkup getParent()
    {
        PDAnnotationMarkup am = null;
        try
        {
            am = (PDAnnotationMarkup) PDAnnotation.createAnnotation(
                    getDictionary().getDictionaryObject(COSName.PARENT, COSName.P));
        }
        catch (IOException ioe)
        {
            // Couldn't construct the annotation, so return null i.e. do nothing
        }
        return am;
    }

}
