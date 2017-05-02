package com.tom_roush.pdfbox.pdmodel.interactive.action;

import com.tom_roush.pdfbox.cos.COSBase;
import com.tom_roush.pdfbox.cos.COSDictionary;
import com.tom_roush.pdfbox.cos.COSName;
import com.tom_roush.pdfbox.cos.COSStream;
import com.tom_roush.pdfbox.cos.COSString;

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
    public final void setAction(String sAction)
    {
        action.setString(COSName.JS, sAction);
    }

    /**
     * @return The Javascript Code.
     */
    public String getAction()
    {
        COSBase base = action.getDictionaryObject( COSName.JS );
        if (base instanceof COSString)
        {
            return ((COSString)base).getString();
        }
        else if (base instanceof COSStream)
        {
            return ((COSStream)base).getString();
        }
        else
        {
            return null;
        }
    }
}
