package org.apache.pdfboxandroid.pdmodel.interactive.form;

import java.io.IOException;
import java.util.Map;

import org.apache.pdfboxandroid.PDFBox;
import org.apache.pdfboxandroid.cos.COSArray;
import org.apache.pdfboxandroid.cos.COSBase;
import org.apache.pdfboxandroid.cos.COSDictionary;
import org.apache.pdfboxandroid.cos.COSName;
import org.apache.pdfboxandroid.cos.COSString;
import org.apache.pdfboxandroid.pdmodel.PDDocument;
import org.apache.pdfboxandroid.pdmodel.PDResources;
import org.apache.pdfboxandroid.pdmodel.common.COSObjectable;

import android.util.Log;

/**
 * This class represents the acroform of a PDF document.
 *
 * @author <a href="mailto:ben@benlitchfield.com">Ben Litchfield</a>
 * @version $Revision: 1.14 $
 */
public class PDAcroForm implements COSObjectable {
	private COSDictionary acroForm;
    private PDDocument document;

    private Map fieldCache;
    
    /**
     * Constructor.
     *
     * @param doc The document that this form is part of.
     * @param form The existing acroForm.
     */
    public PDAcroForm( PDDocument doc, COSDictionary form )
    {
    	Log.e(PDFBox.LOG_TAG, "new acro");
        document = doc;
        Log.e(PDFBox.LOG_TAG, "set doc");
        acroForm = form;
        Log.e(PDFBox.LOG_TAG, "set dic");
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
                (COSArray) acroForm.getDictionaryObject(COSName.getPDFName("Fields"));

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
     * {@inheritDoc}
     */
    public COSBase getCOSObject()
    {
        return acroForm;
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
     * This will get the document associated with this form.
     *
     * @return The PDF document.
     */
    public PDDocument getDocument()
    {
        return document;
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
}
