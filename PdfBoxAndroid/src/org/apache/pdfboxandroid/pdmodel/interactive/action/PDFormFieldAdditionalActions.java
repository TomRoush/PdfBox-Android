package org.apache.pdfboxandroid.pdmodel.interactive.action;

import org.apache.pdfboxandroid.cos.COSBase;
import org.apache.pdfboxandroid.cos.COSDictionary;
import org.apache.pdfboxandroid.pdmodel.common.COSObjectable;
import org.apache.pdfboxandroid.pdmodel.interactive.action.type.PDAction;

/**
 * This class represents a form field's dictionary of actions
 * that occur due to events.
 *
 * @author <a href="mailto:ben@benlitchfield.com">Ben Litchfield</a>
 * @author Panagiotis Toumasis (ptoumasis@mail.gr)
 * @version $Revision: 1.2 $
 */
public class PDFormFieldAdditionalActions implements COSObjectable {
	private COSDictionary actions;
	
	/**
     * Constructor.
     *
     * @param a The action dictionary.
     */
    public PDFormFieldAdditionalActions( COSDictionary a )
    {
        actions = a;
    }
    
    /**
     * This will get a JavaScript action to be performed before
     * the field is formatted to display its current value. This
     * allows the field's value to be modified before formatting.
     *
     * @return The F entry of form field's additional actions dictionary.
     */
    public PDAction getF()
    {
        COSDictionary f = (COSDictionary)actions.getDictionaryObject( "F" );
        PDAction retval = null;
        if( f != null )
        {
            retval = PDActionFactory.createAction( f );
        }
        return retval;
    }

	/**
     * Convert this standard java object to a COS object.
     *
     * @return The cos object that matches this Java object.
     */
    public COSBase getCOSObject()
    {
        return actions;
    }

}
