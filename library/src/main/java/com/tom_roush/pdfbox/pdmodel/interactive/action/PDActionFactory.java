package org.apache.pdfbox.pdmodel.interactive.action;

import org.apache.pdfbox.cos.COSDictionary;

/**
 * This class will take a dictionary and determine which type of action to create.
 *
 * @author Ben Litchfield
 */
public final class PDActionFactory
{
    /**
     * Utility Class.
     */
    private PDActionFactory()
    {
        //utility class
    }

    /**
     * This will create the correct type of action based on the type specified
     * in the dictionary.
     *
     * @param action An action dictionary.
     *
     * @return An action of the correct type.
     */
    public static PDAction createAction( COSDictionary action )
    {
        PDAction retval = null;
        if( action != null )
        {
            String type = action.getNameAsString( "S" );
            if( PDActionJavaScript.SUB_TYPE.equals( type ) )
            {
                retval = new PDActionJavaScript( action );
            }
            else if( PDActionGoTo.SUB_TYPE.equals( type ) )
            {
                retval = new PDActionGoTo( action );
            }
            else if( PDActionLaunch.SUB_TYPE.equals( type ) )
            {
                retval = new PDActionLaunch( action );
            }
            else if( PDActionRemoteGoTo.SUB_TYPE.equals( type ) )
            {
                retval = new PDActionRemoteGoTo( action );
            }
            else if( PDActionURI.SUB_TYPE.equals( type ) )
            {
                retval = new PDActionURI( action );
            }
            else if (PDActionNamed.SUB_TYPE.equals(type))
            {
            	retval = new PDActionNamed(action);
            }
        }
        return retval;
    }

}
