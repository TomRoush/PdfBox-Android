package com.tom_roush.pdfbox.pdmodel.interactive.action;

import com.tom_roush.pdfbox.cos.COSDictionary;
import com.tom_roush.pdfbox.pdmodel.common.COSObjectable;

/**
 * This class represents a page object's dictionary of actions
 * that occur due to events.
 *
 * @author Ben Litchfield
 * @author Panagiotis Toumasis
 */
public class PDPageAdditionalActions implements COSObjectable
{
    private final COSDictionary actions;

    /**
     * Default constructor.
     */
    public PDPageAdditionalActions()
    {
        actions = new COSDictionary();
    }

    /**
     * Constructor.
     *
     * @param a The action dictionary.
     */
    public PDPageAdditionalActions( COSDictionary a )
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
     * This will get an action to be performed when the page
     * is opened. This action is independent of any that may be
     * defined by the OpenAction entry in the document catalog,
     * and is executed after such an action.
     *
     * @return The O entry of page object's additional actions dictionary.
     */
    public PDAction getO()
    {
        COSDictionary o = (COSDictionary)actions.getDictionaryObject( "O" );
        PDAction retval = null;
        if( o != null )
        {
            retval = PDActionFactory.createAction( o );
        }
        return retval;
    }

    /**
     * This will set an action to be performed when the page
     * is opened. This action is independent of any that may be
     * defined by the OpenAction entry in the document catalog,
     * and is executed after such an action.
     *
     * @param o The action to be performed.
     */
    public void setO( PDAction o )
    {
        actions.setItem( "O", o );
    }

    /**
     * This will get an action to be performed when the page
     * is closed. This action applies to the page being closed,
     * and is executed before any other page opened.
     *
     * @return The C entry of page object's additional actions dictionary.
     */
    public PDAction getC()
    {
        COSDictionary c = (COSDictionary)actions.getDictionaryObject( "C" );
        PDAction retval = null;
        if( c != null )
        {
            retval = PDActionFactory.createAction( c );
        }
        return retval;
    }

    /**
     * This will set an action to be performed when the page
     * is closed. This action applies to the page being closed,
     * and is executed before any other page opened.
     *
     * @param c The action to be performed.
     */
    public void setC( PDAction c )
    {
        actions.setItem( "C", c );
    }
}
