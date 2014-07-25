package org.apache.pdfbox.pdmodel;

import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.common.COSObjectable;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;

/**
 * This class represents the acroform of a PDF document.
 *
 * @author <a href="mailto:ben@benlitchfield.com">Ben Litchfield</a>
 * @version $Revision: 1.21 $
 */
public class PDDocumentCatalog implements COSObjectable
{
	
	private COSDictionary root;
    private PDDocument document;

    private PDAcroForm acroForm = null;

    /**
     * Page mode where neither the outline nor the thumbnails
     * are displayed.
     */
    public static final String PAGE_MODE_USE_NONE = "UseNone";
    /**
     * Show bookmarks when pdf is opened.
     */
    public static final String PAGE_MODE_USE_OUTLINES = "UseOutlines";
    /**
     * Show thumbnails when pdf is opened.
     */
    public static final String PAGE_MODE_USE_THUMBS = "UseThumbs";
    /**
     * Full screen mode with no menu bar, window controls.
     */
    public static final String PAGE_MODE_FULL_SCREEN = "FullScreen";
    /**
     * Optional content group panel is visible when opened.
     */
    public static final String PAGE_MODE_USE_OPTIONAL_CONTENT = "UseOC";
    /**
     * Attachments panel is visible.
     */
    public static final String PAGE_MODE_USE_ATTACHMENTS = "UseAttachments";

    /**
     * Display one page at a time.
     */
    public static final String PAGE_LAYOUT_SINGLE_PAGE = "SinglePage";
    /**
     * Display the pages in one column.
     */
    public static final String PAGE_LAYOUT_ONE_COLUMN = "OneColumn";
    /**
     * Display the pages in two columns, with odd numbered pagse on the left.
     */
    public static final String PAGE_LAYOUT_TWO_COLUMN_LEFT = "TwoColumnLeft";
    /**
     * Display the pages in two columns, with odd numbered pagse on the right.
     */
    public static final String PAGE_LAYOUT_TWO_COLUMN_RIGHT ="TwoColumnRight";
    /**
     * Display the pages two at a time, with odd-numbered pages on the left.
     * @since PDF Version 1.5
     */
    public static final String PAGE_LAYOUT_TWO_PAGE_LEFT = "TwoPageLeft";
    /**
     * Display the pages two at a time, with odd-numbered pages on the right.
     * @since PDF Version 1.5
     */
    public static final String PAGE_LAYOUT_TWO_PAGE_RIGHT = "TwoPageRight";

    /**
     * Constructor.
     *
     * @param doc The document that this catalog is part of.
     */
    public PDDocumentCatalog( PDDocument doc )
    {
        document = doc;
        root = new COSDictionary();
        root.setItem( COSName.TYPE, COSName.CATALOG );
        document.getDocument().getTrailer().setItem( COSName.ROOT, root );
    }

    /**
     * Constructor.
     *
     * @param doc The document that this catalog is part of.
     * @param rootDictionary The root dictionary that this object wraps.
     */
    public PDDocumentCatalog( PDDocument doc, COSDictionary rootDictionary )
    {
        document = doc;
        root = rootDictionary;
    }

    /**
     * Convert this standard java object to a COS object.
     *
     * @return The cos object that matches this Java object.
     */
    public COSBase getCOSObject()
    {
        return root;
    }

    /**
     * Convert this standard java object to a COS object.
     *
     * @return The cos object that matches this Java object.
     */
    public COSDictionary getCOSDictionary()
    {
        return root;
    }
    
    /**
     * This will get the documents acroform.  This will return null if
     * no acroform is part of the document.
     *
     * @return The documents acroform.
     */
    public PDAcroForm getAcroForm()
    {
        if( acroForm == null )
        {
            COSDictionary acroFormDic =
                (COSDictionary)root.getDictionaryObject( COSName.ACRO_FORM );
            if( acroFormDic != null )
            {
                acroForm = new PDAcroForm( document, acroFormDic );
            }
        }
        return acroForm;
    }

}
