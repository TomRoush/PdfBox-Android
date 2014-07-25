package org.apache.pdfbox.pdmodel.interactive.annotation;

import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;

/**
 * This is the class that represents a widget.
 *
 * @author <a href="mailto:ben@benlitchfield.com">Ben Litchfield</a>
 * @version $Revision: 1.3 $
 */
public class PDAnnotationWidget extends PDAnnotation
{

	/**
     * The type of annotation.
     */
    public static final String SUB_TYPE = "Widget";


    /**
     * Constructor.
     */
    public PDAnnotationWidget()
    {
        super();
        getDictionary().setName( COSName.SUBTYPE, SUB_TYPE);
    }
    
    /**
     * Creates a PDWidget from a COSDictionary, expected to be
     * a correct object definition for a field in PDF.
     *
     * @param field the PDF objet to represent as a field.
     */
    public PDAnnotationWidget(COSDictionary field)
    {
        super( field );
    }

    /**
     * Returns the highlighting mode. Default value: <code>I</code>
     * <dl>
     *   <dt><code>N</code></dt>
     *     <dd>(None) No highlighting.</dd>
     *   <dt><code>I</code></dt>
     *     <dd>(Invert) Invert the contents of the annotation rectangle.</dd>
     *   <dt><code>O</code></dt>
     *     <dd>(Outline) Invert the annotation's border.</dd>
     *   <dt><code>P</code></dt>
     *     <dd>(Push) Display the annotation's down appearance, if any. If no
     *      down appearance is defined, the contents of the annotation rectangle
     *      shall be offset to appear as if it were pushed below the surface of
     *      the page</dd>
     *   <dt><code>T</code></dt>
     *     <dd>(Toggle) Same as <code>P</code> (which is preferred).</dd>
     * </dl>
     * 
     * @return the highlighting mode
     */
    public String getHighlightingMode()
    {
        return this.getDictionary().getNameAsString(COSName.H, "I");
    }

    /**
     * Sets the highlighting mode.
     * <dl>
     *   <dt><code>N</code></dt>
     *     <dd>(None) No highlighting.</dd>
     *   <dt><code>I</code></dt>
     *     <dd>(Invert) Invert the contents of the annotation rectangle.</dd>
     *   <dt><code>O</code></dt>
     *     <dd>(Outline) Invert the annotation's border.</dd>
     *   <dt><code>P</code></dt>
     *     <dd>(Push) Display the annotation's down appearance, if any. If no
     *      down appearance is defined, the contents of the annotation rectangle
     *      shall be offset to appear as if it were pushed below the surface of
     *      the page</dd>
     *   <dt><code>T</code></dt>
     *     <dd>(Toggle) Same as <code>P</code> (which is preferred).</dd>
     * </dl>
     * 
     * @param highlightingMode the highlighting mode
     *  the defined values
     */
    public void setHighlightingMode(String highlightingMode)
    {
        if ((highlightingMode == null)
            || "N".equals(highlightingMode) || "I".equals(highlightingMode)
            || "O".equals(highlightingMode) || "P".equals(highlightingMode)
            || "T".equals(highlightingMode))
        {
            this.getDictionary().setName(COSName.H, highlightingMode);
        }
        else
        {
            throw new IllegalArgumentException( "Valid values for highlighting mode are " +
                "'N', 'N', 'O', 'P' or 'T'" );
        }
    }

}
