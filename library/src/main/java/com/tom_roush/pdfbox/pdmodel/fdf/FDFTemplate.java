package com.tom_roush.pdfbox.pdmodel.fdf;

import com.tom_roush.pdfbox.cos.COSArray;
import com.tom_roush.pdfbox.cos.COSDictionary;
import com.tom_roush.pdfbox.cos.COSName;
import com.tom_roush.pdfbox.pdmodel.common.COSArrayList;
import com.tom_roush.pdfbox.pdmodel.common.COSObjectable;

import java.util.ArrayList;
import java.util.List;

/**
 * This represents an FDF template that is part of the FDF page.
 *
 * @author Ben Litchfield
 */
public class FDFTemplate implements COSObjectable
{
    private final COSDictionary template;

    /**
     * Default constructor.
     */
    public FDFTemplate()
    {
        template = new COSDictionary();
    }

    /**
     * Constructor.
     *
     * @param t The FDF page template.
     */
    public FDFTemplate( COSDictionary t )
    {
        template = t;
    }

    /**
     * Convert this standard java object to a COS object.
     *
     * @return The cos object that matches this Java object.
     */
    @Override
    public COSDictionary getCOSObject()
    {
        return template;
    }

    /**
     * This is the template reference.
     *
     * @return The template reference.
     */
    public FDFNamedPageReference getTemplateReference()
    {
        FDFNamedPageReference retval = null;
        COSDictionary dict = (COSDictionary)template.getDictionaryObject( COSName.TREF );
        if( dict != null )
        {
            retval = new FDFNamedPageReference( dict );
        }
        return retval;
    }

    /**
     * This will set the template reference.
     *
     * @param tRef The template reference.
     */
    public void setTemplateReference( FDFNamedPageReference tRef )
    {
        template.setItem( COSName.TREF, tRef );
    }

    /**
     * This will get a list of fields that are part of this template.
     *
     * @return A list of fields.
     */
    public List<FDFField> getFields()
    {
        List<FDFField> retval = null;
        COSArray array = (COSArray)template.getDictionaryObject( COSName.FIELDS );
        if( array != null )
        {
            List<FDFField> fields = new ArrayList<FDFField>();
            for( int i=0; i<array.size(); i++ )
            {
                fields.add( new FDFField( (COSDictionary)array.getObject( i ) ) );
            }
            retval = new COSArrayList<FDFField>( fields, array );
        }
        return retval;
    }

    /**
     * This will set a list of fields for this template.
     *
     * @param fields The list of fields to set for this template.
     */
    public void setFields( List<FDFField> fields )
    {
        template.setItem( COSName.FIELDS, COSArrayList.converterToCOSArray( fields ) );
    }

    /**
     * A flag telling if the fields imported from the template may be renamed if there are conflicts.
     *
     * @return A flag telling if the fields can be renamed.
     */
    public boolean shouldRename()
    {
        return template.getBoolean( COSName.RENAME, false );
    }

    /**
     * This will set if the fields can be renamed.
     *
     * @param value The flag value.
     */
    public void setRename( boolean value )
    {
        template.setBoolean( COSName.RENAME, value );
    }
}
