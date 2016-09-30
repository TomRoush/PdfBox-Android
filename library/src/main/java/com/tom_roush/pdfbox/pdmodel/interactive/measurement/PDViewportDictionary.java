package org.apache.pdfbox.pdmodel.interactive.measurement;

import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.common.COSObjectable;
import org.apache.pdfbox.pdmodel.common.PDRectangle;

/**
 * This class represents a viewport dictionary.
 */
public class PDViewportDictionary implements COSObjectable
{

    /**
     * The type of this annotation.
     */
    public static final String TYPE = "Viewport";
    
    private COSDictionary viewportDictionary;

    /**
     * Constructor.
     */
    public PDViewportDictionary()
    {
        this.viewportDictionary = new COSDictionary();
    }

    /**
     * Constructor.
     * 
     * @param dictionary the dictionary
     */
    public PDViewportDictionary(COSDictionary dictionary)
    {
        this.viewportDictionary = dictionary;
    }

    /**
     * {@inheritDoc} 
     */
    public COSBase getCOSObject()
    {
        return this.viewportDictionary;
    }

    /**
     * This will return the corresponding dictionary.
     * 
     * @return the viewport dictionary
     */
    public COSDictionary getDictionary()
    {
        return this.viewportDictionary;
    }

    /**
     * Returns the type of the viewport dictionary.
     * It must be "Viewport"
     * @return the type of the external data dictionary
     */

    public String getType()
    {
        return TYPE;
    }

    /**
     * This will retrieve the rectangle specifying the location of the viewport.
     * 
     * @return the location
     */
    public PDRectangle getBBox()
    {
        COSArray bbox = (COSArray)this.getDictionary().getDictionaryObject("BBox");
        if (bbox != null)
        {
            return new PDRectangle(bbox);
        }
        return null;
    }

    /**
     * This will set the rectangle specifying the location of the viewport.
     * 
     * @param rectangle the rectangle specifying the location.
     */
    public void setBBox(PDRectangle rectangle)
    {
        this.getDictionary().setItem("BBox", rectangle);
    }

    /**
     * This will retrieve the name of the viewport.
     * 
     * @return the name of the viewport
     */
    public String getName()
    {
        return this.getDictionary().getNameAsString(COSName.NAME);
    }

   /**
    * This will set the name of the viewport.
    *  
    * @param name the name of the viewport
    */
    public void setName(String name)
    {
        this.getDictionary().setName(COSName.NAME, name);
    }

    /**
     * This will retrieve the measure dictionary.
     * 
     * @return the measure dictionary
     */
    public PDMeasureDictionary getMeasure()
    {
        COSDictionary measure = (COSDictionary)this.getDictionary().getDictionaryObject("Measure");
        if (measure != null)
        {
            return new PDMeasureDictionary(measure);
        }
        return null;
    }

    /**
     * This will set the measure dictionary.
     * 
     * @param measure the measure dictionary
     */
    public void setMeasure(PDMeasureDictionary measure)
    {
        this.getDictionary().setItem("Measure", measure);
    }

}
