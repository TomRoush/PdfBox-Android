package com.tom_roush.pdfbox.pdmodel.interactive.action;

import com.tom_roush.pdfbox.cos.COSDictionary;
import com.tom_roush.pdfbox.pdmodel.common.COSObjectable;

/**
 * This represents a dictionary of actions that occur due to events.
 *
 * @author Ben Litchfield
 */
public class PDAdditionalActions implements COSObjectable
{
    private final COSDictionary actions;

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
    public COSDictionary getCOSObject()
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
