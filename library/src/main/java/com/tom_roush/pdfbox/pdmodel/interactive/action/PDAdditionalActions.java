package org.apache.pdfbox.pdmodel.interactive.action;

import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;

import org.apache.pdfbox.pdmodel.common.COSObjectable;

/**
 * This represents a dictionary of actions that occur due to events.
 *
 * @author Ben Litchfield
 */
public class PDAdditionalActions implements COSObjectable
{
    private COSDictionary actions;

    /**
     * Default constructor.
     */
    public PDAdditionalActions()
    {
        actions = new COSDictionary();
    }

    /**
     * Constructor.
     *
     * @param a The action dictionary.
     */
    public PDAdditionalActions( COSDictionary a )
    {
        actions = a;
    }

    /**
     * Convert this standard java object to a COS object.
     *
     * @return The cos object that matches this Java object.
     */
    @Override
    public COSBase getCOSObject()
    {
        return actions;
    }

    /**
     * Convert this standard java object to a COS object.
     *
     * @return The cos object that matches this Java object.
     */
    public COSDictionary getCOSDictionary()
    {
        return actions;
    }

    /**
     * Get the F action.
     *
     * @return The F action.
     */
    public PDAction getF()
    {
        return PDActionFactory.createAction( (COSDictionary)actions.getDictionaryObject("F" ) );
    }

    /**
     * Set the F action.
     *
     * @param action Get the F action.
     */
    public void setF( PDAction action )
    {
        actions.setItem( "F", action );
    }
}
