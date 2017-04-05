package com.tom_roush.pdfbox.pdmodel.interactive.action;

import com.tom_roush.pdfbox.cos.COSArray;
import com.tom_roush.pdfbox.cos.COSBase;
import com.tom_roush.pdfbox.cos.COSDictionary;
import com.tom_roush.pdfbox.cos.COSName;
import com.tom_roush.pdfbox.pdmodel.common.COSArrayList;
import com.tom_roush.pdfbox.pdmodel.common.PDDestinationOrAction;

import java.util.ArrayList;
import java.util.List;

/**
 * This represents an action that can be executed in a PDF document.
 *
 * @author Ben Litchfield
 * @author Panagiotis Toumasis
 */
public abstract class PDAction implements PDDestinationOrAction
{
    /**
     * The type of PDF object.
     */
    public static final String TYPE = "Action";

    /**
     * The action dictionary.
     */
    protected COSDictionary action;

    /**
     * Default constructor.
     */
    public PDAction()
    {
        action = new COSDictionary();
        setType( TYPE );
    }

    /**
     * Constructor.
     *
     * @param a The action dictionary.
     */
    public PDAction( COSDictionary a )
    {
        action = a;
    }

    /**
     * Convert this standard java object to a COS object.
     *
     * @return The cos object that matches this Java object.
     */
    @Override
    public COSDictionary getCOSObject()
    {
        return action;
    }

    /**
     * This will get the type of PDF object that the actions dictionary describes.
     * If present must be Action for an action dictionary.
     *
     * @return The Type of PDF object.
     */
    public String getType()
    {
       return action.getNameAsString( COSName.TYPE );
    }

    /**
     * This will set the type of PDF object that the actions dictionary describes.
     * If present must be Action for an action dictionary.
     *
     * @param type The new Type for the PDF object.
     */
    public final void setType( String type )
    {
       action.setName(COSName.TYPE, type );
    }

    /**
     * This will get the type of action that the actions dictionary describes.
     * If present, must be Action for an action dictionary.
     *
     * @return The S entry of actions dictionary.
     */
    public String getSubType()
    {
        return action.getNameAsString(COSName.S);
    }

    /**
     * This will set the type of action that the actions dictionary describes.
     * If present, must be Action for an action dictionary.
     *
     * @param s The new type of action.
     */
    public void setSubType( String s )
    {
        action.setName(COSName.S, s);
    }

    /**
     * This will get the next action, or sequence of actions, to be performed after this one.
     * The value is either a single action dictionary or an array of action dictionaries
     * to be performed in order.
     *
     * @return The Next action or sequence of actions.
     */
    public List<PDAction> getNext()
    {
        List<PDAction> retval = null;
        COSBase next = action.getDictionaryObject(COSName.NEXT);
        if( next instanceof COSDictionary )
        {
            PDAction pdAction = PDActionFactory.createAction( (COSDictionary) next );
            retval = new COSArrayList<PDAction>(pdAction, next, action, COSName.NEXT);
        }
        else if( next instanceof COSArray )
        {
            COSArray array = (COSArray)next;
            List<PDAction> actions = new ArrayList<PDAction>();
            for( int i=0; i<array.size(); i++ )
            {
                actions.add( PDActionFactory.createAction( (COSDictionary) array.getObject( i )));
            }
            retval = new COSArrayList<PDAction>( actions, array );
        }

        return retval;
    }

    /**
     * This will set the next action, or sequence of actions, to be performed after this one.
     * The value is either a single action dictionary or an array of action dictionaries
     * to be performed in order.
     *
     * @param next The Next action or sequence of actions.
     */
    public void setNext( List<?> next )
    {
        action.setItem(COSName.NEXT, COSArrayList.converterToCOSArray( next ) );
    }
}
