package org.apache.pdfboxandroid.pdmodel.interactive.action.type;

import org.apache.pdfboxandroid.cos.COSDictionary;

/**
 * This represents a URI action that can be executed in a PDF document.
 *
 * @author <a href="mailto:ben@benlitchfield.com">Ben Litchfield</a>
 * @author Panagiotis Toumasis (ptoumasis@mail.gr)
 * @version $Revision: 1.3 $
 */
public class PDActionURI extends PDAction {
	/**
     * This type of action this object represents.
     */
    public static final String SUB_TYPE = "URI";
    
    /**
     * Constructor.
     *
     * @param a The action dictionary.
     */
    public PDActionURI( COSDictionary a )
    {
        super( a );
    }
}
