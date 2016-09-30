package com.tom_roush.pdfbox.pdmodel.documentinterchange.taggedpdf;

import com.tom_roush.pdfbox.cos.COSArray;
import com.tom_roush.pdfbox.cos.COSDictionary;
import com.tom_roush.pdfbox.cos.COSName;
import com.tom_roush.pdfbox.pdmodel.common.PDRectangle;
import com.tom_roush.pdfbox.pdmodel.documentinterchange.markedcontent.PDMarkedContent;

/**
 * An artifact marked content.
 *
 * @author Johannes Koch
 */
public class PDArtifactMarkedContent extends PDMarkedContent
{

    public PDArtifactMarkedContent(COSDictionary properties)
    {
        super(COSName.ARTIFACT, properties);
    }


    /**
     * Gets the type (Type).
     * 
     * @return the type
     */
    public String getType()
    {
        return this.getProperties().getNameAsString(COSName.TYPE);
    }

    /**
     * Gets the artifact's bounding box (BBox).
     * 
     * @return the artifact's bounding box
     */
    public PDRectangle getBBox()
    {
        PDRectangle retval = null;
        COSArray a = (COSArray) this.getProperties().getDictionaryObject(
            COSName.BBOX);
        if (a != null)
        {
            retval = new PDRectangle(a);
        }
        return retval;
    }

    /**
     * Is the artifact attached to the top edge?
     * 
     * @return <code>true</code> if the artifact is attached to the top edge,
     * <code>false</code> otherwise
     */
    public boolean isTopAttached()
    {
        return this.isAttached("Top");
    }

    /**
     * Is the artifact attached to the bottom edge?
     * 
     * @return <code>true</code> if the artifact is attached to the bottom edge,
     * <code>false</code> otherwise
     */
    public boolean isBottomAttached()
    {
        return this.isAttached("Bottom");
    }

    /**
     * Is the artifact attached to the left edge?
     * 
     * @return <code>true</code> if the artifact is attached to the left edge,
     * <code>false</code> otherwise
     */
    public boolean isLeftAttached()
    {
        return this.isAttached("Left");
    }

    /**
     * Is the artifact attached to the right edge?
     * 
     * @return <code>true</code> if the artifact is attached to the right edge,
     * <code>false</code> otherwise
     */
    public boolean isRightAttached()
    {
        return this.isAttached("Right");
    }

    /**
     * Gets the subtype (Subtype).
     * 
     * @return the subtype
     */
    public String getSubtype()
    {
        return this.getProperties().getNameAsString(COSName.SUBTYPE);
    }


    /**
     * Is the artifact attached to the given edge?
     * 
     * @param edge the edge
     * @return <code>true</code> if the artifact is attached to the given edge,
     * <code>false</code> otherwise
     */
    private boolean isAttached(String edge)
    {
        COSArray a = (COSArray) this.getProperties().getDictionaryObject(
            COSName.ATTACHED);
        if (a != null)
        {
            for (int i = 0; i < a.size(); i++)
            {
                if (edge.equals(a.getName(i)))
                {
                    return true;
                }
            }
        }
        return false;
    }

}
