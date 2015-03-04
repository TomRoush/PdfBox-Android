package org.apache.pdfbox.pdmodel.graphics.pattern;

import java.io.IOException;

import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.graphics.shading.PDShading;
import org.apache.pdfbox.pdmodel.graphics.state.PDExtendedGraphicsState;

/**
 * A shading pattern dictionary.
 */
public class PDShadingPattern extends PDAbstractPattern
{
    private PDExtendedGraphicsState extendedGraphicsState;
    private PDShading shading;

    /**
     * Creates a new shading pattern.
     */
    public PDShadingPattern()
    {
        super();
        getCOSDictionary().setInt(COSName.PATTERN_TYPE, PDAbstractPattern.TYPE_SHADING_PATTERN);
    }

    /**
     * Creates a new shading pattern from the given COS dictionary.
     * @param resourceDictionary The COSDictionary for this pattern resource.
     */
    public PDShadingPattern(COSDictionary resourceDictionary)
    {
        super(resourceDictionary);
    }

    @Override
    public int getPatternType()
    {
        return PDAbstractPattern.TYPE_SHADING_PATTERN;
    }

    /**
     * This will get the external graphics state for this pattern.
     * @return The extended graphics state for this pattern.
     */
    public PDExtendedGraphicsState getExtendedGraphicsState()
    {
        if (extendedGraphicsState == null) 
        {
            COSDictionary dictionary = (COSDictionary)getCOSDictionary().getDictionaryObject( COSName.EXT_G_STATE );
            if( dictionary != null )
            {
                extendedGraphicsState = new PDExtendedGraphicsState( dictionary );
            }
        }
        return extendedGraphicsState;
    }

    /**
     * This will set the external graphics state for this pattern.
     * @param extendedGraphicsState The new external graphics state for this pattern.
     */
    public void setExternalGraphicsState( PDExtendedGraphicsState extendedGraphicsState )
    {
        this.extendedGraphicsState = extendedGraphicsState;
        if (extendedGraphicsState != null)
        {
            getCOSDictionary().setItem( COSName.EXT_G_STATE, extendedGraphicsState );
        }
        else
        {
            getCOSDictionary().removeItem(COSName.EXT_G_STATE);
        }
    }

    /**
     * This will get the shading resources for this pattern.
     * @return The shading resources for this pattern.
     * @throws IOException if something went wrong
     */
    public PDShading getShading() throws IOException
    {
        if (shading == null) 
        {
            COSDictionary dictionary = (COSDictionary)getCOSDictionary()
            		.getDictionaryObject( COSName.SHADING );
            if( dictionary != null )
            {
                shading = PDShading.create(dictionary);
            }
        }
        return shading;
    }

    /**
     * This will set the shading resources for this pattern.
     * @param shadingResources The new shading resources for this pattern.
     */
    public void setShading( PDShading shadingResources )
    {
        shading = shadingResources;
        if (shadingResources != null)
        {
            getCOSDictionary().setItem( COSName.SHADING, shadingResources );
        }
        else
        {
            getCOSDictionary().removeItem(COSName.SHADING);
        }
    }
}
