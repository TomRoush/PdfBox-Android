package org.apache.pdfbox.pdmodel.graphics.blend;

/**
 * Non-separable blend mode (supports blend function).
 *
 * @author Kühn & Weyh Software, GmbH
 */
public abstract class NonSeparableBlendMode extends BlendMode
{
    NonSeparableBlendMode()
    {
    }

    public abstract void blend(float[] srcValues, float[] dstValues, float[] result);
}
