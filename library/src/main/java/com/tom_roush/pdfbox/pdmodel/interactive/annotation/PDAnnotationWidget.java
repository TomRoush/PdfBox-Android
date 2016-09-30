package org.apache.pdfbox.pdmodel.interactive.annotation;

import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.interactive.action.PDAction;
import org.apache.pdfbox.pdmodel.interactive.action.PDActionFactory;
import org.apache.pdfbox.pdmodel.interactive.action.PDAnnotationAdditionalActions;

/**
 * This is the class that represents a widget.
 *
 * @author Ben Litchfield
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
     * @param field the PDF object to represent as a field.
     */
    public PDAnnotationWidget(COSDictionary field)
    {
        super( field );
        getDictionary().setName( COSName.SUBTYPE, SUB_TYPE);
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

    /**
     * Returns the appearance characteristics dictionary.
     * 
     * @return the appearance characteristics dictionary
     */
    public PDAppearanceCharacteristicsDictionary getAppearanceCharacteristics()
    {
        COSBase mk = this.getDictionary().getDictionaryObject(COSName.MK);
        if (mk instanceof COSDictionary)
        {
            return new PDAppearanceCharacteristicsDictionary((COSDictionary) mk);
        }
        return null;
    }

    /**
     * Sets the appearance characteristics dictionary.
     * 
     * @param appearanceCharacteristics the appearance characteristics dictionary
     */
    public void setAppearanceCharacteristics(PDAppearanceCharacteristicsDictionary appearanceCharacteristics)
    {
        this.getDictionary().setItem(COSName.MK, appearanceCharacteristics);
    }

    /**
     * Get the action to be performed when this annotation is to be activated.
     *
     * @return The action to be performed when this annotation is activated.
     */
    public PDAction getAction()
    {
        COSDictionary action = (COSDictionary)
            this.getDictionary().getDictionaryObject( COSName.A );
        return PDActionFactory.createAction( action );
    }

    /**
     * Set the annotation action.
     * As of PDF 1.6 this is only used for Widget Annotations
     * @param action The annotation action.
     */
    public void setAction( PDAction action )
    {
        this.getDictionary().setItem( COSName.A, action );
    }

    /**
     * Get the additional actions for this field.  This will return null
     * if there are no additional actions for this field.
     * As of PDF 1.6 this is only used for Widget Annotations.
     *
     * @return The actions of the field.
     */
    public PDAnnotationAdditionalActions getActions()
    {
        COSDictionary aa = (COSDictionary)this.getDictionary().getDictionaryObject( "AA" );
        PDAnnotationAdditionalActions retval = null;
        if( aa != null )
        {
            retval = new PDAnnotationAdditionalActions( aa );
        }
        return retval;
    }

    /**
     * Set the actions of the field.
     *
     * @param actions The field actions.
     */
    public void setActions( PDAnnotationAdditionalActions actions )
    {
        this.getDictionary().setItem( "AA", actions );
    }

    /**
     * This will set the border style dictionary, specifying the width and dash
     * pattern used in drawing the line.
     *
     * @param bs the border style dictionary to set.
     *
     */
    public void setBorderStyle( PDBorderStyleDictionary bs )
    {
        this.getDictionary().setItem( "BS", bs);
    }

    /**
     * This will retrieve the border style dictionary, specifying the width and
     * dash pattern used in drawing the line.
     *
     * @return the border style dictionary.
     */
    public PDBorderStyleDictionary getBorderStyle()
    {
    	COSDictionary bs = (COSDictionary) this.getDictionary().getItem(COSName.BS);
        if (bs != null)
        {
            return new PDBorderStyleDictionary( bs );
        }
        else
        {
            return null;
        }
    }

    // TODO where to get acroForm from?
//    public PDField getParent() throws IOException
//    {
//        COSBase parent = this.getCOSObject().getDictionaryObject(COSName.PARENT);
//        if (parent instanceof COSDictionary)
//        {
//            PDAcroForm acroForm = null;
//            return PDFieldFactory.createField(acroForm, (COSDictionary) parent);
//        }
//        return null;
//    }
}
