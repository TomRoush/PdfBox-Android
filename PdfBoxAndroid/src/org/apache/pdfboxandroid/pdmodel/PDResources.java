package org.apache.pdfboxandroid.pdmodel;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.pdfboxandroid.PDFBox;
import org.apache.pdfboxandroid.cos.COSBase;
import org.apache.pdfboxandroid.cos.COSDictionary;
import org.apache.pdfboxandroid.cos.COSName;
import org.apache.pdfboxandroid.pdmodel.common.COSDictionaryMap;
import org.apache.pdfboxandroid.pdmodel.common.COSObjectable;
import org.apache.pdfboxandroid.pdmodel.font.PDFont;
import org.apache.pdfboxandroid.pdmodel.font.PDFontFactory;

import android.util.Log;

/**
 * This represents a set of resources available at the page/pages/stream level.
 * 
 * @author <a href="mailto:ben@benlitchfield.com">Ben Litchfield</a>
 * 
 */
public class PDResources implements COSObjectable {
	private COSDictionary resources;
    private Map<String, PDFont> fonts = null;
    private Map<PDFont, String> fontMappings = new HashMap<PDFont, String>();
//    private Map<String, PDColorSpace> colorspaces = null;
//    private Map<String, PDXObject> xobjects = null;
//    private Map<PDXObject, String> xobjectMappings = null;
//    private HashMap<String, PDXObjectImage> images = null;
//    private Map<String, PDExtendedGraphicsState> graphicsStates = null;
//    private Map<String, PDPatternResources> patterns = null;
//    private Map<String, PDShadingResources> shadings = null;
	
	/**
     * Default constructor.
     */
    public PDResources()
    {
        resources = new COSDictionary();
    }
	
	/**
     * Prepopulated resources.
     * 
     * @param resourceDictionary The cos dictionary for this resource.
     */
    public PDResources(COSDictionary resourceDictionary)
    {
        resources = resourceDictionary;
    }

	/**
     * Convert this standard java object to a COS object.
     * 
     * @return The cos object that matches this Java object.
     */
    public COSBase getCOSObject()
    {
        return resources;
    }
    
    /**
     * This will get the underlying dictionary.
     * 
     * @return The dictionary for these resources.
     */
    public COSDictionary getCOSDictionary()
    {
        return resources;
    }
    
    /**
     * This will get the map of fonts. This will never return null.
     * 
     * @return The map of fonts.
     */
    public Map<String, PDFont> getFonts()
    {
        if (fonts == null)
        {
            // at least an empty map will be returned
            // TODO we should return null instead of an empty map
            fonts = new HashMap<String, PDFont>();
            COSDictionary fontsDictionary = (COSDictionary) resources.getDictionaryObject(COSName.FONT);
            if (fontsDictionary == null)
            {
                fontsDictionary = new COSDictionary();
                resources.setItem(COSName.FONT, fontsDictionary);
            }
            else
            {
                for (COSName fontName : fontsDictionary.keySet())
                {
                    COSBase font = fontsDictionary.getDictionaryObject(fontName);
                    // data-000174.pdf contains a font that is a COSArray, looks to be an error in the
                    // PDF, we will just ignore entries that are not dictionaries.
                    if (font instanceof COSDictionary)
                    {
                        PDFont newFont = null;
                        try
                        {
                            newFont = PDFontFactory.createFont((COSDictionary) font);
                        }
                        catch (IOException exception)
                        {
                            Log.e("error while creating a font" + exception, PDFBox.LOG_TAG);
                        }
                        if (newFont != null)
                        {
                            fonts.put(fontName.getName(), newFont);
                        }
                    }
                }
            }
            setFonts(fonts);
        }
        return fonts;
    }
    
    /**
     * This will set the map of fonts.
     * 
     * @param fontsValue The new map of fonts.
     */
    public void setFonts(Map<String, PDFont> fontsValue)
    {
        fonts = fontsValue;
        if (fontsValue != null)
        {
            resources.setItem(COSName.FONT, COSDictionaryMap.convert(fontsValue));
            fontMappings = reverseMap(fontsValue, PDFont.class);
        }
        else
        {
            resources.removeItem(COSName.FONT);
            fontMappings = null;
        }
    }
    
    private <T> Map<T, String> reverseMap(Map<String, T> map, Class<T> keyClass)
    {
        Map<T, String> reversed = new java.util.HashMap<T, String>();
        for (Map.Entry<String, T> entry : map.entrySet())
        {
            reversed.put(keyClass.cast(entry.getValue()), (String) entry.getKey());
        }
        return reversed;
    }
    
    /**
     * Adds the given font to the resources of the current page using the given font key.
     * 
     * @param font the font to be added
     * @param fontKey key to used to map to the given font
     * @return the font name to be used within the content stream.
     */
    public String addFont(PDFont font, String fontKey)
    {
        if (fonts == null)
        {
            // initialize fonts map
            getFonts();
        }

        String fontMapping = fontMappings.get(font);
        if (fontMapping == null)
        {
            fontMapping = fontKey;
            fontMappings.put(font, fontMapping);
            fonts.put(fontMapping, font);
            addFontToDictionary(font, fontMapping);
        }
        return fontMapping;
    }
    
    private void addFontToDictionary(PDFont font, String fontName)
    {
        COSDictionary fontsDictionary = (COSDictionary) resources.getDictionaryObject(COSName.FONT);
        fontsDictionary.setItem(fontName, font);
    }
}
