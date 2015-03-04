package org.apache.pdfbox.pdmodel.fdf;

import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSString;
import org.apache.pdfbox.pdmodel.common.COSObjectable;

/**
 * This represents an object that can be used in a Field's Opt entry to represent
 * an available option and a default appearance string.
 *
 * @author Ben Litchfield
 */
public class FDFOptionElement implements COSObjectable
{
    private COSArray option;

    /**
     * Default constructor.
     */
    public FDFOptionElement()
    {
        option = new COSArray();
        option.add( new COSString( "" ) );
        option.add( new COSString( "" ) );
    }

    /**
     * Constructor.
     *
     * @param o The option element.
     */
    public FDFOptionElement( COSArray o )
    {
        option = o;
    }

    /**
     * Convert this standard java object to a COS object.
     *
     * @return The cos object that matches this Java object.
     */
    public COSBase getCOSObject()
    {
        return option;
    }

    /**
     * Convert this standard java object to a COS object.
     *
     * @return The cos object that matches this Java object.
     */
    public COSArray getCOSArray()
    {
        return option;
    }

    /**
     * This will get the string of one of the available options.  A required element.
     *
     * @return An available option.
     */
    public String getOption()
    {
        return ((COSString)option.getObject( 0 ) ).getString();
    }

    /**
     * This will set the string for an available option.
     *
     * @param opt One of the available options.
     */
    public void setOption( String opt )
    {
        option.set( 0, new COSString( opt ) );
    }

    /**
     * This will get the string of default appearance string.  A required element.
     *
     * @return A default appearance string.
     */
    public String getDefaultAppearanceString()
    {
        return ((COSString)option.getObject( 1 ) ).getString();
    }

    /**
     * This will set the default appearance string.
     *
     * @param da The default appearance string.
     */
    public void setDefaultAppearanceString( String da )
    {
        option.set( 1, new COSString( da ) );
    }
}
