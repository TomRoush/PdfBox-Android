package org.apache.pdfbox.pdmodel.interactive.action;

import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.pdmodel.common.PDTextStream;

/**
 * This represents a JavaScript action.
 *
 * @author Michael Schwarzenberger
 */
public class PDActionJavaScript extends PDAction
{
    /**
     * This type of action this object represents.
     */
    public static final String SUB_TYPE = "JavaScript";

    /**
     * Constructor #1.
     */
    public PDActionJavaScript()
    {
        super();
        setSubType( SUB_TYPE );
    }

    /**
     * Constructor.
     *
     * @param js Some javascript code.
     */
    public PDActionJavaScript( String js )
    {
        this();
        setAction( js );
    }

    /**
     * Constructor #2.
     *
     *  @param a The action dictionary.
     */
    public PDActionJavaScript(COSDictionary a)
    {
        super(a);
    }

    /**
     * @param sAction The JavaScript.
     */
    public void setAction(PDTextStream sAction)
    {
        action.setItem("JS", sAction);
    }

    /**
     * @param sAction The JavaScript.
     */
    public void setAction(String sAction)
    {
        action.setString("JS", sAction);
    }

    /**
     * @return The Javascript Code.
     */
    public PDTextStream getAction()
    {
        return PDTextStream.createTextStream( action.getDictionaryObject("JS") );
    }
}
