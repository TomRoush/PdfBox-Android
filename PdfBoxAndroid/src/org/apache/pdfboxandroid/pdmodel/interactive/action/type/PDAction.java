package org.apache.pdfboxandroid.pdmodel.interactive.action.type;

import org.apache.pdfboxandroid.cos.COSBase;
import org.apache.pdfboxandroid.cos.COSDictionary;
import org.apache.pdfboxandroid.pdmodel.common.PDDestinationOrAction;

/**
 * This represents an action that can be executed in a PDF document.
 *
 * @author <a href="mailto:ben@benlitchfield.com">Ben Litchfield</a>
 * @author Panagiotis Toumasis (ptoumasis@mail.gr)
 * @version $Revision: 1.3 $
 */
public abstract class PDAction implements PDDestinationOrAction {
	/**
     * The action dictionary.
     */
    protected COSDictionary action;
    
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
    public COSBase getCOSObject()
    {
        return action;
    }
}
