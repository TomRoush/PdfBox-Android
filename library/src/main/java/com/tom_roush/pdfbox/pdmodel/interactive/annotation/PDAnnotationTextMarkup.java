package com.tom_roush.pdfbox.pdmodel.interactive.annotation;

import com.tom_roush.pdfbox.cos.COSArray;
import com.tom_roush.pdfbox.cos.COSDictionary;
import com.tom_roush.pdfbox.cos.COSName;

/**
 * This is the abstract class that represents a text markup annotation
 * Introduced in PDF 1.3 specification, except Squiggly lines in 1.4.
 *
 * @author Paul King
 */
public class PDAnnotationTextMarkup extends PDAnnotationMarkup
{

    /**
     * The types of annotation.
     */
    public static final String SUB_TYPE_HIGHLIGHT = "Highlight";
    /**
     * The types of annotation.
     */
    public static final String SUB_TYPE_UNDERLINE = "Underline";
    /**
     * The types of annotation.
     */
    public static final String SUB_TYPE_SQUIGGLY = "Squiggly";
    /**
     * The types of annotation.
     */
    public static final String SUB_TYPE_STRIKEOUT = "StrikeOut";


    private PDAnnotationTextMarkup()
    {
        // Must be constructed with a subType or dictionary parameter
    }

    /**
     * Creates a TextMarkup annotation of the specified sub type.
     *
     * @param subType the subtype the annotation represents
     */
    public PDAnnotationTextMarkup(String subType)
    {
        super();
        setSubtype( subType );

        // Quad points are required, set and empty array
        setQuadPoints( new float[0] );
    }

    /**
     * Creates a TextMarkup annotation from a COSDictionary, expected to be a
     * correct object definition.
     *
     * @param field the PDF objet to represent as a field.
     */
    public PDAnnotationTextMarkup( COSDictionary field )
    {
        super( field );
    }

    /**
     * This will set the set of quadpoints which encompass the areas of this
     * annotation.
     *
     * @param quadPoints
     *            an array representing the set of area covered
     */
    public void setQuadPoints( float[] quadPoints )
    {
        COSArray newQuadPoints = new COSArray();
        newQuadPoints.setFloatArray( quadPoints );
        getCOSObject().setItem(COSName.QUADPOINTS, newQuadPoints);
    }

    /**
     * This will retrieve the set of quadpoints which encompass the areas of
     * this annotation.
     *
     * @return An array of floats representing the quad points.
     */
    public float[] getQuadPoints()
    {
        COSArray quadPoints = (COSArray) getCOSObject().getDictionaryObject(COSName.QUADPOINTS);
        if (quadPoints != null)
        {
            return quadPoints.toFloatArray();
        }
        else
        {
            return null; // Should never happen as this is a required item
        }
    }

    /**
     * This will set the sub type (and hence appearance, AP taking precedence) For
     * this annotation. See the SUB_TYPE_XXX constants for valid values.
     *
     * @param subType The subtype of the annotation
     */
    public void setSubtype( String subType )
    {
        getCOSObject().setName(COSName.SUBTYPE, subType);
    }

    /**
     * This will retrieve the sub type (and hence appearance, AP taking precedence)
     * For this annotation.
     *
     * @return The subtype of this annotation, see the SUB_TYPE_XXX constants.
     */
    public String getSubtype()
    {
        return getCOSObject().getNameAsString(COSName.SUBTYPE);
    }
}
