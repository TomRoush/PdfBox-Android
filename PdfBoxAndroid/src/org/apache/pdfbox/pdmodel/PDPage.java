package org.apache.pdfbox.pdmodel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.common.COSObjectable;
import org.apache.pdfbox.pdmodel.common.PDRectangle;

import awt.print.Printable;

/**
 * This represents a single page in a PDF document.
 * <p>
 * This class implements the {@link Printable} interface, but since PDFBox
 * version 1.3.0 you should be using the {@link PDPageable} adapter instead
 * (see <a href="https://issues.apache.org/jira/browse/PDFBOX-788">PDFBOX-788</a>).
 *
 * @author <a href="mailto:ben@benlitchfield.com">Ben Litchfield</a>
 * @version $Revision: 1.29 $
 */
public class PDPage implements COSObjectable, Printable
{
	
	/**
     * Log instance.
     */
    private static final Log LOG = LogFactory.getLog(PDPage.class);

    private static final int DEFAULT_USER_SPACE_UNIT_DPI = 72;

    private static final float MM_TO_UNITS = 1/(10*2.54f)*DEFAULT_USER_SPACE_UNIT_DPI;

    /**
     * Fully transparent that can fall back to white when image type has no alpha.
     */
//    private static final Color TRANSPARENT_WHITE = new Color( 255, 255, 255, 0 );

    private COSDictionary page;

//    private PDResources pageResources;
    
    /**
     * A page size of LETTER or 8.5x11.
     */
    public static final PDRectangle PAGE_SIZE_LETTER = 
        new PDRectangle( 8.5f*DEFAULT_USER_SPACE_UNIT_DPI, 11f*DEFAULT_USER_SPACE_UNIT_DPI );
    /**
     * A page size of A0 Paper.
     */
    public static final PDRectangle PAGE_SIZE_A0 = new PDRectangle( 841*MM_TO_UNITS, 1189*MM_TO_UNITS );
    /**
     * A page size of A1 Paper.
     */
    public static final PDRectangle PAGE_SIZE_A1 = new PDRectangle( 594*MM_TO_UNITS, 841*MM_TO_UNITS );
    /**
     * A page size of A2 Paper.
     */
    public static final PDRectangle PAGE_SIZE_A2 = new PDRectangle( 420*MM_TO_UNITS, 594*MM_TO_UNITS );
    /**
     * A page size of A3 Paper.
     */
    public static final PDRectangle PAGE_SIZE_A3 = new PDRectangle( 297*MM_TO_UNITS, 420*MM_TO_UNITS );
    /**
     * A page size of A4 Paper.
     */
    public static final PDRectangle PAGE_SIZE_A4 = new PDRectangle( 210*MM_TO_UNITS, 297*MM_TO_UNITS );
    /**
     * A page size of A5 Paper.
     */
    public static final PDRectangle PAGE_SIZE_A5 = new PDRectangle( 148*MM_TO_UNITS, 210*MM_TO_UNITS );
    /**
     * A page size of A6 Paper.
     */
    public static final PDRectangle PAGE_SIZE_A6 = new PDRectangle( 105*MM_TO_UNITS, 148*MM_TO_UNITS );

    /**
     * Creates a new instance of PDPage with a size of 8.5x11.
     */
    public PDPage()
    {
        page = new COSDictionary();
        page.setItem( COSName.TYPE, COSName.PAGE );
        setMediaBox( PAGE_SIZE_LETTER );
    }

    /**
     * Creates a new instance of PDPage.
     *
     * @param size The MediaBox or the page.
     */
    public PDPage(PDRectangle size)
    {
        page = new COSDictionary();
        page.setItem( COSName.TYPE, COSName.PAGE );
        setMediaBox( size );
    }



    /**
     * Creates a new instance of PDPage.
     *
     * @param pageDic The existing page dictionary.
     */
    public PDPage( COSDictionary pageDic )
    {
        page = pageDic;
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
     * This will get the underlying dictionary that this class acts on.
     *
     * @return The underlying dictionary for this class.
     */
    public COSDictionary getCOSDictionary()
    {
        return page;
    }


    /**
     * This is the parent page node.  The parent is a required element of the
     * page.  This will be null until this page is added to the document.
     *
     * @return The parent to this page.
     */
    public PDPageNode getParent()
    {
        if( parent == null)
        {
            COSDictionary parentDic = (COSDictionary)page.getDictionaryObject( COSName.PARENT, COSName.P );
            if( parentDic != null )
            {
                parent = new PDPageNode( parentDic );
            }
        }
        return parent;
    }

    private PDPageNode parent = null;
    
    private PDRectangle mediaBox = null;
    
    /**
     * This will set the mediaBox for this page.
     *
     * @param mediaBoxValue The new mediaBox for this page.
     */
    public void setMediaBox( PDRectangle mediaBoxValue )
    {
        this.mediaBox = mediaBoxValue;
        if( mediaBoxValue == null )
        {
            page.removeItem( COSName.MEDIA_BOX );
        }
        else
        {
            page.setItem( COSName.MEDIA_BOX, mediaBoxValue.getCOSArray() );
        }
    }

}
