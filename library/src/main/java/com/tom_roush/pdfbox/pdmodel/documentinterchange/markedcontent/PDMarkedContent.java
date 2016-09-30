package com.tom_roush.pdfbox.pdmodel.documentinterchange.markedcontent;

import java.util.ArrayList;
import java.util.List;

import com.tom_roush.pdfbox.cos.COSDictionary;
import com.tom_roush.pdfbox.cos.COSName;
import com.tom_roush.pdfbox.pdmodel.documentinterchange.taggedpdf.PDArtifactMarkedContent;
import com.tom_roush.pdfbox.pdmodel.graphics.PDXObject;
import com.tom_roush.pdfbox.text.TextPosition;

/**
 * A marked content.
 * 
 * @author Johannes Koch
 */
public class PDMarkedContent
{

    /**
     * Creates a marked-content sequence.
     * 
     * @param tag the tag
     * @param properties the properties
     * @return the marked-content sequence
     */
    public static PDMarkedContent create(COSName tag, COSDictionary properties)
    {
        if (COSName.ARTIFACT.equals(tag))
        {
            return new PDArtifactMarkedContent(properties);
        }
        return new PDMarkedContent(tag, properties);
    }


    private final String tag;
    private final COSDictionary properties;
    private final List<Object> contents;


    /**
     * Creates a new marked content object.
     * 
     * @param tag the tag
     * @param properties the properties
     */
    public PDMarkedContent(COSName tag, COSDictionary properties)
    {
        this.tag = tag == null ? null : tag.getName();
        this.properties = properties;
        this.contents = new ArrayList<Object>();
    }


    /**
     * Gets the tag.
     * 
     * @return the tag
     */
    public String getTag()
    {
        return this.tag;
    }

    /**
     * Gets the properties.
     * 
     * @return the properties
     */
    public COSDictionary getProperties()
    {
        return this.properties;
    }

    /**
     * Gets the marked-content identifier.
     * 
     * @return the marked-content identifier
     */
    public int getMCID()
    {
        return this.getProperties() == null ? null :
            this.getProperties().getInt(COSName.MCID);
    }

    /**
     * Gets the language (Lang).
     * 
     * @return the language
     */
    public String getLanguage()
    {
        return this.getProperties() == null ? null :
            this.getProperties().getNameAsString(COSName.LANG);
    }

    /**
     * Gets the actual text (ActualText).
     * 
     * @return the actual text
     */
    public String getActualText()
    {
        return this.getProperties() == null ? null :
            this.getProperties().getString(COSName.ACTUAL_TEXT);
    }

    /**
     * Gets the alternate description (Alt).
     * 
     * @return the alternate description
     */
    public String getAlternateDescription()
    {
        return this.getProperties() == null ? null :
            this.getProperties().getString(COSName.ALT);
    }

    /**
     * Gets the expanded form (E).
     * 
     * @return the expanded form
     */
    public String getExpandedForm()
    {
        return this.getProperties() == null ? null :
            this.getProperties().getString(COSName.E);
    }

    /**
     * Gets the contents of the marked content sequence. Can be
     * <ul>
     *   <li>{@link TextPosition},</li>
     *   <li>{@link PDMarkedContent}, or</li>
     *   <li>{@link PDXObject}.</li>
     * </ul>
     * 
     * @return the contents of the marked content sequence
     */
    public List<Object> getContents()
    {
        return this.contents;
    }

    /**
     * Adds a text position to the contents.
     * 
     * @param text the text position
     */
    public void addText(TextPosition text)
    {
        this.getContents().add(text);
    }

    /**
     * Adds a marked content to the contents.
     * 
     * @param markedContent the marked content
     */
    public void addMarkedContent(PDMarkedContent markedContent)
    {
        this.getContents().add(markedContent);
    }

    /**
     * Adds an XObject to the contents.
     * 
     * @param xobject the XObject
     */
    public void addXObject(PDXObject xobject)
    {
        this.getContents().add(xobject);
    }


    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder("tag=").append(this.tag)
            .append(", properties=").append(this.properties);
        sb.append(", contents=").append(this.contents);
        return sb.toString();
    }

}
