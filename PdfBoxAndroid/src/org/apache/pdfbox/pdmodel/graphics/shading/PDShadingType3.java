package org.apache.pdfbox.pdmodel.graphics.shading;

import org.apache.pdfbox.cos.COSDictionary;

/**
 * This represents resources for a radial shading.
 *
 * @version $Revision: 1.0 $
 */
public class PDShadingType3 extends PDShadingType2
{
    /**
     * Constructor using the given shading dictionary.
     *
     * @param shadingDictionary The dictionary for this shading.
     */
    public PDShadingType3(COSDictionary shadingDictionary)
    {
        super(shadingDictionary);
    }

    /**
     * {@inheritDoc}
     */
    public int getShadingType()
    {
        return PDShadingResources.SHADING_TYPE3;
    }
}
