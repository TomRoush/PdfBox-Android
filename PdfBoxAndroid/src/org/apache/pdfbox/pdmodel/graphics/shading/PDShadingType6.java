package org.apache.pdfbox.pdmodel.graphics.shading;

import org.apache.pdfbox.cos.COSDictionary;

/**
 * This represents resources for a shading type 6 (Coons Patch Meshes).
 *
 * @version $Revision: 1.0 $
 */
public class PDShadingType6 extends PDShadingType4
{
    /**
     * Constructor using the given shading dictionary.
     *
     * @param shadingDictionary The dictionary for this shading.
     */
    public PDShadingType6(COSDictionary shadingDictionary)
    {
        super(shadingDictionary);
    }

    /**
     * {@inheritDoc}
     */
    public int getShadingType()
    {
        return PDShadingResources.SHADING_TYPE6;
    }
}
