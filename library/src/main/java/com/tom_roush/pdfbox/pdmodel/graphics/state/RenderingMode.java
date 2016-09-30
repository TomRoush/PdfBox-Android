package com.tom_roush.pdfbox.pdmodel.graphics.state;

/**
 * Text Rendering Mode.
 *
 * @author John Hewson
 */
public enum RenderingMode
{
    /**
     * Fill text.
     */
    FILL(0),

    /**
     * Stroke text.
     */
    STROKE(1),

    /**
     * Fill, then stroke text.
     */
    FILL_STROKE(2),

    /**
     * Neither fill nor stroke text (invisible)
     */
    NEITHER(3),

    /**
     * Fill text and add to path for clipping.
     */
    FILL_CLIP(4),

    /**
     * Stroke text and add to path for clipping.
     */
    STROKE_CLIP(5),

    /**
     * Fill, then stroke text and add to path for clipping.
     */
    FILL_STROKE_CLIP(6),

    /**
     * Add text to path for clipping.
     */
    NEITHER_CLIP(7);

    private static final RenderingMode[] VALUES = RenderingMode.values();

    public static RenderingMode fromInt(int value)
    {
        return VALUES[value];
    }

    private final int value;

    RenderingMode(int value)
    {
        this.value = value;
    }

    /**
     * Returns the integer value of this mode, as used in a PDF file.
     */
    public int intValue()
    {
        return value;
    }

    /**
     * Returns true is this mode fills text.
     */
    public boolean isFill()
    {
        return this == FILL ||
               this == FILL_STROKE ||
               this == FILL_CLIP ||
               this == FILL_STROKE_CLIP;
    }

    /**
     * Returns true is this mode strokes text.
     */
    public boolean isStroke()
    {
        return this == STROKE ||
               this == FILL_STROKE ||
               this == STROKE ||
               this == FILL_STROKE_CLIP;
    }

    /**
     * Returns true is this mode clips text.
     */
    public boolean isClip()
    {
        return this == FILL_CLIP ||
               this == STROKE_CLIP ||
               this == FILL_STROKE_CLIP ||
               this == NEITHER_CLIP;
    }
}
