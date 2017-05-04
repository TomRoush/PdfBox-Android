package com.tom_roush.pdfbox.text;

import android.graphics.RectF;

import com.tom_roush.pdfbox.pdmodel.PDPage;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

/**
 * This will extract text from a specified region in the PDF.
 *
 * @author Ben Litchfield
 */
public class PDFTextStripperByArea extends PDFTextStripper
{
    private final List<String> regions = new ArrayList<String>();
    private final Map<String, RectF> regionArea = new HashMap<String, RectF>();
    private final Map<String, Vector<List<TextPosition>>> regionCharacterList =
        new HashMap<String, Vector<List<TextPosition>>>();
    private final Map<String, StringWriter> regionText = new HashMap<String, StringWriter>();

    /**
     * Constructor.
     * @throws IOException If there is an error loading properties.
     */
    public PDFTextStripperByArea() throws IOException
    {
        super();
        super.setShouldSeparateByBeads(false);
    }

    /**
     * This method does nothing in this derived class, because beads and regions are incompatible. Beads are
     * ignored when stripping by area.
     */
    @Override
    public void setShouldSeparateByBeads(boolean aShouldSeparateByBeads)
    {
    }

    /**
     * Add a new region to group text by.
     *
     * @param regionName The name of the region.
     * @param rect The rectangle area to retrieve the text from.
     */
    public void addRegion( String regionName, RectF rect )
    {
        regions.add( regionName );
        regionArea.put( regionName, rect );
    }

    /**
     * Get the list of regions that have been setup.
     *
     * @return A list of java.lang.String objects to identify the region names.
     */
    public List<String> getRegions()
    {
        return regions;
    }

    /**
     * Get the text for the region, this should be called after extractRegions().
     *
     * @param regionName The name of the region to get the text from.
     * @return The text that was identified in that region.
     */
    public String getTextForRegion( String regionName )
    {
        StringWriter text = regionText.get( regionName );
        return text.toString();
    }

    /**
     * Process the page to extract the region text.
     *
     * @param page The page to extract the regions from.
     * @throws IOException If there is an error while extracting text.
     */
    public void extractRegions( PDPage page ) throws IOException
    {
        Iterator<String> regionIter = regions.iterator();
        while( regionIter.hasNext() )
        {
            setStartPage(getCurrentPageNo());
            setEndPage(getCurrentPageNo());
            //reset the stored text for the region so this class
            //can be reused.
            String regionName = regionIter.next();
            Vector<List<TextPosition>> regionCharactersByArticle = new Vector<List<TextPosition>>();
            regionCharactersByArticle.add( new ArrayList<TextPosition>() );
            regionCharacterList.put( regionName, regionCharactersByArticle );
            regionText.put( regionName, new StringWriter() );
        }

        if (page.hasContents())
        {
        	processPage( page );
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void processTextPosition( TextPosition text )
    {
        Iterator<String> regionIter = regionArea.keySet().iterator();
        while( regionIter.hasNext() )
        {
            String region = regionIter.next();
            RectF rect = regionArea.get( region );
            if( rect.contains( text.getX(), text.getY() ) )
            {
                charactersByArticle = regionCharacterList.get( region );
                super.processTextPosition( text );
            }
        }
    }

    
    /**
     * This will print the processed page text to the output stream.
     *
     * @throws IOException If there is an error writing the text.
     */
    @Override
    protected void writePage() throws IOException
    {
        Iterator<String> regionIter = regionArea.keySet().iterator();
        while( regionIter.hasNext() )
        {
            String region = regionIter.next();
            charactersByArticle = regionCharacterList.get( region );
            output = regionText.get( region );
            super.writePage();
        }
    }
}
