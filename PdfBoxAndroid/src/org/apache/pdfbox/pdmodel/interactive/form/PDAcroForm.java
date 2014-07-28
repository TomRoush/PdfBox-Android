package org.apache.pdfbox.pdmodel.interactive.form;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSString;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.common.COSArrayList;
import org.apache.pdfbox.pdmodel.common.COSObjectable;

/**
 * This class represents the acroform of a PDF document.
 *
 * @author <a href="mailto:ben@benlitchfield.com">Ben Litchfield</a>
 * @version $Revision: 1.14 $
 */
public class PDAcroForm implements COSObjectable
{

	private COSDictionary acroForm;
    private PDDocument document;

    private Map fieldCache;

    /**
     * Constructor.
     *
     * @param doc The document that this form is part of.
     */
    public PDAcroForm( PDDocument doc )
    {
        document = doc;
        acroForm = new COSDictionary();
        COSArray fields = new COSArray();
        acroForm.setItem( COSName.getPDFName( "Fields" ), fields );
    }

    /**
     * Constructor.
     *
     * @param doc The document that this form is part of.
     * @param form The existing acroForm.
     */
    public PDAcroForm( PDDocument doc, COSDictionary form )
    {
        document = doc;
        acroForm = form;
    }

    /**
     * This will get the document associated with this form.
     *
     * @return The PDF document.
     */
    public PDDocument getDocument()
    {
        return document;
    }

    /**
     * This will get the dictionary that this form wraps.
     *
     * @return The dictionary for this form.
     */
    public COSDictionary getDictionary()
    {
        return acroForm;
    }
    
    /**
     * This will return all of the fields in the document.  The type
     * will be a org.apache.pdfbox.pdmodel.field.PDField.
     *
     * @return A list of all the fields.
     * @throws IOException If there is an error while getting the list of fields.
     */
    public List getFields() throws IOException
    {
        List retval = null;
        COSArray fields =
            (COSArray) acroForm.getDictionaryObject(
                COSName.getPDFName("Fields"));

        if( fields != null )
        {
            List actuals = new ArrayList();
            for (int i = 0; i < fields.size(); i++)
            {
                COSDictionary element = (COSDictionary) fields.getObject(i);
                if (element != null)
                {
                    PDField field = PDFieldFactory.createField( this, element );
                    if( field != null )
                    {
                        actuals.add(field);
                    }
                }
            }
            retval = new COSArrayList( actuals, fields );
        }
        return retval;
    }
    
    /**
     * This will get a field by name, possibly using the cache if setCache is true.
     *
     * @param name The name of the field to get.
     *
     * @return The field with that name of null if one was not found.
     *
     * @throws IOException If there is an error getting the field type.
     */
    public PDField getField( String name ) throws IOException
    {
        PDField retval = null;
        if( fieldCache != null )
        {
            retval = (PDField)fieldCache.get( name );
        }
        else
        {
            String[] nameSubSection = name.split( "\\." );
            COSArray fields =
                (COSArray) acroForm.getDictionaryObject(
                    COSName.getPDFName("Fields"));

            for (int i = 0; i < fields.size() && retval == null; i++)
            {
                COSDictionary element = (COSDictionary) fields.getObject(i);
                if( element != null )
                {
                    COSString fieldName =
                        (COSString)element.getDictionaryObject( COSName.getPDFName( "T" ) );
                    if( fieldName.getString().equals( name ) ||
                        fieldName.getString().equals( nameSubSection[0] ) )
                    {
                        PDField root = PDFieldFactory.createField( this, element );

                        if( nameSubSection.length > 1 )
                        {
                            PDField kid = root.findKid( nameSubSection, 1 );
                            if( kid != null )
                            {
                                retval = kid;
                            }
                            else
                            {
                                retval = root;
                            }
                        }
                        else
                        {
                            retval = root;
                        }
                    }
                }
            }
        }
        return retval;
    }
    
    /**
     * This will get the default resources for the acro form.
     *
     * @return The default resources.
     */
    public PDResources getDefaultResources()
    {
        PDResources retval = null;
        COSDictionary dr = (COSDictionary)acroForm.getDictionaryObject( COSName.getPDFName( "DR" ) );
        if( dr != null )
        {
            retval = new PDResources( dr );
        }
        return retval;
    }

    /**
     * This will set the default resources for the acroform.
     *
     * @param dr The new default resources.
     */
    public void setDefaultResources( PDResources dr )
    {
        COSDictionary drDict = null;
        if( dr != null )
        {
            drDict = dr.getCOSDictionary();
        }
        acroForm.setItem( COSName.getPDFName( "DR" ), drDict );
    }
    
    /**
     * {@inheritDoc}
     */
    public COSBase getCOSObject()
    {
        return acroForm;
    }

}
