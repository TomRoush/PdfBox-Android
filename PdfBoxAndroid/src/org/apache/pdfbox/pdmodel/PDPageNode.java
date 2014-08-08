package org.apache.pdfbox.pdmodel;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSInteger;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSNumber;
import org.apache.pdfbox.pdmodel.common.COSArrayList;
import org.apache.pdfbox.pdmodel.common.COSObjectable;

/**
 * This represents a page node in a pdf document.
 *
 * @author <a href="mailto:ben@benlitchfield.com">Ben Litchfield</a>
 * @version $Revision: 1.8 $
 */
public class PDPageNode implements COSObjectable
{

	private COSDictionary page;

    /**
     * Log instance.
     */
    private static final Log log = LogFactory.getLog(PDPageNode.class);

    /**
     * Creates a new instance of PDPage.
     */
    public PDPageNode()
    {
        page = new COSDictionary();
        page.setItem( COSName.TYPE, COSName.PAGES );
        page.setItem( COSName.KIDS, new COSArray() );
        page.setItem( COSName.COUNT, COSInteger.ZERO );
    }

    /**
     * Creates a new instance of PDPage.
     *
     * @param pages The dictionary pages.
     */
    public PDPageNode( COSDictionary pages )
    {
        page = pages;
    }
    
    /**
     * This will update the count attribute of the page node.  This only needs to
     * be called if you add or remove pages.  The PDDocument will call this for you
     * when you use the PDDocumnet persistence methods.  So, basically most clients
     * will never need to call this.
     *
     * @return The update count for this node.
     */
    public long updateCount()
    {
        long totalCount = 0;
        List kids = getKids();
        Iterator kidIter = kids.iterator();
        while( kidIter.hasNext() )
        {
            Object next = kidIter.next();
            if( next instanceof PDPage )
            {
                totalCount++;
            }
            else
            {
                PDPageNode node = (PDPageNode)next;
                totalCount += node.updateCount();
            }
        }
        page.setLong( COSName.COUNT, totalCount );
        return totalCount;
    }
    
    /**
     * This will get the count of descendent page objects.
     *
     * @return The total number of descendent page objects.
     */
    public long getCount()
    {
        if(page == null)
        {
            return 0L;
        }
        COSBase num = page.getDictionaryObject(COSName.COUNT);
        if(num == null)
        {
            return 0L;
        }
        return ((COSNumber) num).intValue();
    }
    
    /**
     * This will get the underlying dictionary that this class acts on.
     *
     * @return The underlying dictionary for this class.
     */
    public COSDictionary getDictionary()
    {
        return page;
    }

    /**
     * {@inheritDoc}
     */
    public COSBase getCOSObject()
    {
        return page;
    }
    
    /**
     * This will return all kids of this node, either PDPageNode or PDPage.
     *
     * @return All direct descendents of this node.
     */
    public List getKids()
    {
        List actuals = new ArrayList();
        COSArray kids = getAllKids(actuals, page, false);
        return new COSArrayList( actuals, kids );
    }
    
    /**
     * This will return all kids of this node as PDPage.
     *
     * @param result All direct and indirect descendents of this node are added to this list.
     */
    public void getAllKids(List result)
    {
        getAllKids(result, page, true);
    }

    /**
     * This will return all kids of the given page node as PDPage.
     *
     * @param result All direct and optionally indirect descendents of this node are added to this list.
     * @param page Page dictionary of a page node.
     * @param recurse if true indirect descendents are processed recursively
     */
    private static COSArray getAllKids(List result, COSDictionary page, boolean recurse)
    {
        if(page == null)
            return null;
        COSArray kids = (COSArray)page.getDictionaryObject( COSName.KIDS );
        if ( kids == null)
        {
            log.error("No Kids found in getAllKids(). Probably a malformed pdf.");
            return null;
        }
        for( int i=0; i<kids.size(); i++ )
        {
            COSBase obj = kids.getObject( i );
            if (obj instanceof COSDictionary)
            {
                COSDictionary kid = (COSDictionary)obj;
                if( COSName.PAGE.equals( kid.getDictionaryObject( COSName.TYPE ) ) )
                {
                    result.add( new PDPage( kid ) );
                }
                else
                {
                    if (recurse)
                    {
                        getAllKids(result, kid, recurse);
                    }
                    else
                    {
                        result.add( new PDPageNode( kid ) );
                    }
                }
            }
        }
        return kids;
    }

}
