package org.apache.pdfbox.pdmodel.fdf;

import java.util.ArrayList;
import java.util.List;

import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.pdmodel.common.COSArrayList;
import org.apache.pdfbox.pdmodel.common.COSObjectable;

/**
 * This represents an FDF page that is part of the FDF document.
 *
 * @author <a href="mailto:ben@benlitchfield.com">Ben Litchfield</a>
 * @version $Revision: 1.3 $
 */
public class FDFPage implements COSObjectable
{
    private COSDictionary page;

    /**
     * Default constructor.
     */
    public FDFPage()
    {
        page = new COSDictionary();
    }

    /**
     * Constructor.
     *
     * @param p The FDF page.
     */
    public FDFPage( COSDictionary p )
    {
        page = p;
    }

    /**
     * Convert this standard java object to a COS object.
     *
     * @return The cos object that matches this Java object.
     */
    public COSBase getCOSObject()
    {
        return page;
    }

    /**
     * Convert this standard java object to a COS object.
     *
     * @return The cos object that matches this Java object.
     */
    public COSDictionary getCOSDictionary()
    {
        return page;
    }

    /**
     * This will get a list of FDFTemplage objects that describe the named pages
     * that serve as templates.
     *
     * @return A list of templates.
     */
    public List getTemplates()
    {
        List retval = null;
        COSArray array = (COSArray)page.getDictionaryObject( "Templates" );
        if( array != null )
        {
            List objects = new ArrayList();
            for( int i=0; i<array.size(); i++ )
            {
                objects.add( new FDFTemplate( (COSDictionary)array.getObject( i ) ) );
            }
            retval = new COSArrayList( objects, array );
        }
        return retval;
    }

    /**
     * A list of FDFTemplate objects.
     *
     * @param templates A list of templates for this Page.
     */
    public void setTemplates( List templates )
    {
        page.setItem( "Templates", COSArrayList.converterToCOSArray( templates ) );
    }

    /**
     * This will get the FDF page info object.
     *
     * @return The Page info.
     */
    public FDFPageInfo getPageInfo()
    {
        FDFPageInfo retval = null;
        COSDictionary dict = (COSDictionary)page.getDictionaryObject( "Info" );
        if( dict != null )
        {
            retval = new FDFPageInfo( dict );
        }
        return retval;
    }

    /**
     * This will set the page info.
     *
     * @param info The new page info dictionary.
     */
    public void setPageInfo( FDFPageInfo info )
    {
        page.setItem( "Info", info );
    }
}
