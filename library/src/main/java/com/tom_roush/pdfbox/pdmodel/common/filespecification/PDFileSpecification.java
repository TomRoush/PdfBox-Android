package com.tom_roush.pdfbox.pdmodel.common.filespecification;

import java.io.IOException;

import com.tom_roush.pdfbox.cos.COSBase;
import com.tom_roush.pdfbox.cos.COSDictionary;
import com.tom_roush.pdfbox.cos.COSString;
import com.tom_roush.pdfbox.pdmodel.common.COSObjectable;

/**
 * This represents a file specification.
 *
 * @author Ben Litchfield
 */
public abstract class PDFileSpecification implements COSObjectable
{

    /**
     * A file specfication can either be a COSString or a COSDictionary.  This
     * will create the file specification either way.
     *
     * @param base The cos object that describes the fs.
     *
     * @return The file specification for the COSBase object.
     *
     * @throws IOException If there is an error creating the file spec.
     */
    public static PDFileSpecification createFS( COSBase base ) throws IOException
    {
        PDFileSpecification retval = null;
        if( base == null )
        {
            //then simply return null
        }
        else if( base instanceof COSString )
        {
            retval = new PDSimpleFileSpecification( (COSString)base );
        }
        else if( base instanceof COSDictionary )
        {
            retval = new PDComplexFileSpecification( (COSDictionary)base );
        }
        else
        {
            throw new IOException( "Error: Unknown file specification " + base );
        }
        return retval;
    }

    /**
     * This will get the file name.
     *
     * @return The file name.
     */
    public abstract String getFile();

    /**
     * This will set the file name.
     *
     * @param file The name of the file.
     */
    public abstract void setFile( String file );
}
