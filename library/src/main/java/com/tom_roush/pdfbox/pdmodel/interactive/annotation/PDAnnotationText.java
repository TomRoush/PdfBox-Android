package org.apache.pdfbox.pdmodel.interactive.annotation;

import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;

/**
 * This is the class that represents a text annotation.
 *
 * @author Paul King
 */
public class PDAnnotationText extends PDAnnotationMarkup
{

    /*
     * The various values of the Text as defined in the PDF 1.7 reference Table
     * 172
     */

    /**
     * Constant for the name of a text annotation.
     */
    public static final String NAME_COMMENT = "Comment";

    /**
     * Constant for the name of a text annotation.
     */
    public static final String NAME_KEY = "Key";

    /**
     * Constant for the name of a text annotation.
     */
    public static final String NAME_NOTE = "Note";

    /**
     * Constant for the name of a text annotation.
     */
    public static final String NAME_HELP = "Help";

    /**
     * Constant for the name of a text annotation.
     */
    public static final String NAME_NEW_PARAGRAPH = "NewParagraph";

    /**
     * Constant for the name of a text annotation.
     */
    public static final String NAME_PARAGRAPH = "Paragraph";

    /**
     * Constant for the name of a text annotation.
     */
    public static final String NAME_INSERT = "Insert";

    /**
     * The type of annotation.
     */
    public static final String SUB_TYPE = "Text";

    /**
     * Constructor.
     */
    public PDAnnotationText()
    {
        super();
        getDictionary()
                .setItem( COSName.SUBTYPE, COSName.getPDFName( SUB_TYPE ) );
    }

    /**
     * Creates a Text annotation from a COSDictionary, expected to be a correct
     * object definition.
     *
     * @param field
     *            the PDF object to represent as a field.
     */
    public PDAnnotationText( COSDictionary field )
    {
        super( field );
    }

    /**
     * This will set initial state of the annotation, open or closed.
     *
     * @param open
     *            Boolean value, true = open false = closed
     */
    public void setOpen( boolean open )
    {
        getDictionary().setBoolean( COSName.getPDFName( "Open" ), open );
    }

    /**
     * This will retrieve the initial state of the annotation, open Or closed
     * (default closed).
     *
     * @return The initial state, true = open false = closed
     */
    public boolean getOpen()
    {
        return getDictionary().getBoolean( COSName.getPDFName( "Open" ), false );
    }

    /**
     * This will set the name (and hence appearance, AP taking precedence) For
     * this annotation. See the NAME_XXX constants for valid values.
     *
     * @param name
     *            The name of the annotation
     */
    public void setName( String name )
    {
        getDictionary().setName( COSName.NAME, name );
    }

    /**
     * This will retrieve the name (and hence appearance, AP taking precedence)
     * For this annotation. The default is NOTE.
     *
     * @return The name of this annotation, see the NAME_XXX constants.
     */
    public String getName()
    {
        return getDictionary().getNameAsString( COSName.NAME, NAME_NOTE );
    }

    /**
     * This will retrieve the annotation state.
     * 
     * @return the annotation state
     */
    public String getState()
    {
        return this.getDictionary().getString("State");
    }

    /**
     * This will set the annotation state.
     * 
     * @param state the annotation state 
     */
    public void setState(String state)
    {
        this.getDictionary().setString("State", state);
    }

    /**
     * This will retrieve the annotation state model.
     * 
     * @return the annotation state model
     */
    public String getStateModel()
    {
        return this.getDictionary().getString("StateModel");
    }

    /**
     * This will set the annotation state model.
     * Allowed values are "Marked" and "Review"
     * 
     * @param stateModel the annotation state model
     */
    public void setStateModel(String stateModel)
    {
        this.getDictionary().setString("StateModel", stateModel);
    }

}
