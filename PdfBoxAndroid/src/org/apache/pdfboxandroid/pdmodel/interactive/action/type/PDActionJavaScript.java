package org.apache.pdfboxandroid.pdmodel.interactive.action.type;

import org.apache.pdfboxandroid.cos.COSDictionary;

/**
 * This represents a JavaScript action.
 *
 * @author Michael Schwarzenberger (mi2kee@gmail.com)
 * @version $Revision: 1.1 $
 */
public class PDActionJavaScript extends PDAction {
	/**
     * This type of action this object represents.
     */
    public static final String SUB_TYPE = "JavaScript";
    
    /**
     * Constructor #2.
     *
     *  @param a The action dictionary.
     */
    public PDActionJavaScript(COSDictionary a)
    {
        super(a);
    }
}
