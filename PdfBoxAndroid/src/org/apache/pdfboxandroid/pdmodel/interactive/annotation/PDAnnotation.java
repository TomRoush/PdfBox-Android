package org.apache.pdfboxandroid.pdmodel.interactive.annotation;

import org.apache.pdfboxandroid.cos.COSArray;
import org.apache.pdfboxandroid.cos.COSBase;
import org.apache.pdfboxandroid.cos.COSDictionary;
import org.apache.pdfboxandroid.cos.COSName;
import org.apache.pdfboxandroid.pdmodel.common.COSObjectable;
import org.apache.pdfboxandroid.pdmodel.common.PDRectangle;

/**
 * This class represents a PDF annotation.
 * 
 * @author <a href="mailto:ben@benlitchfield.com">Ben Litchfield</a>
 * 
 */
public abstract class PDAnnotation implements COSObjectable {
	
	private COSDictionary dictionary;
	
	/**
     * Constructor.
     * 
     * @param dict The annotations dictionary.
     */
    public PDAnnotation(COSDictionary dict)
    {
        dictionary = dict;
    }
	
	/**
     * returns the dictionary.
     * 
     * @return the dictionary
     */
    public COSDictionary getDictionary()
    {
        return dictionary;
    }
    
	/**
     * Interface method for COSObjectable.
     * 
     * @return This object as a standard COS object.
     */
    public COSBase getCOSObject()
    {
        return getDictionary();
    }
    
    /**
     * This will get the appearance dictionary associated with this annotation. This may return null.
     * 
     * @return This annotations appearance.
     */
    public PDAppearanceDictionary getAppearance()
    {
        PDAppearanceDictionary ap = null;
        COSDictionary apDic = (COSDictionary) dictionary.getDictionaryObject(COSName.AP);
        if (apDic != null)
        {
            ap = new PDAppearanceDictionary(apDic);
        }
        return ap;
    }
    
    /**
     * This will set the appearance associated with this annotation.
     * 
     * @param appearance The appearance dictionary for this annotation.
     */
    public void setAppearance(PDAppearanceDictionary appearance)
    {
        COSDictionary ap = null;
        if (appearance != null)
        {
            ap = appearance.getDictionary();
        }
        dictionary.setItem(COSName.AP, ap);
    }
    
    /**
     * The annotation rectangle, defining the location of the annotation on the page in default user space units. This
     * is usually required and should not return null on valid PDF documents. But where this is a parent form field with
     * children, such as radio button collections then the rectangle will be null.
     * 
     * @return The Rect value of this annotation.
     */
    public PDRectangle getRectangle()
    {
        COSArray rectArray = (COSArray) dictionary.getDictionaryObject(COSName.RECT);
        PDRectangle rectangle = null;
        if (rectArray != null)
        {
            rectangle = new PDRectangle(rectArray);
        }
        return rectangle;
    }
}
