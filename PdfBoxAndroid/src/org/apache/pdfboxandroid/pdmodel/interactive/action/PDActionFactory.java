package org.apache.pdfboxandroid.pdmodel.interactive.action;

import org.apache.pdfboxandroid.cos.COSDictionary;
import org.apache.pdfboxandroid.pdmodel.interactive.action.type.PDAction;
import org.apache.pdfboxandroid.pdmodel.interactive.action.type.PDActionGoTo;
import org.apache.pdfboxandroid.pdmodel.interactive.action.type.PDActionJavaScript;
import org.apache.pdfboxandroid.pdmodel.interactive.action.type.PDActionLaunch;
import org.apache.pdfboxandroid.pdmodel.interactive.action.type.PDActionRemoteGoTo;
import org.apache.pdfboxandroid.pdmodel.interactive.action.type.PDActionURI;

/**
 * This class will take a dictionary and determine which type of action to create.
 *
 * @author <a href="mailto:ben@benlitchfield.com">Ben Litchfield</a>
 * @version $Revision: 1.5 $
 */
public class PDActionFactory {
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
        }
        return retval;
    }
}
