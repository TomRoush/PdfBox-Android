package com.tom_roush.pdfbox.util;

/**
 * A 2D vector.
 *
 * @author John Hewson
 */
public final class Vector
{
    private final float x, y;

    public Vector(float x, float y)
    {
        this.x = x;
        this.y = y;
    }

    /**
     * Returns the x magnitude.
     */
    public float getX()
    {
        return x;
    }

    /**
     * Returns the y magnitude.
     */
    public float getY()
    {
        return y;
    }

    /**
     * Returns a new vector scaled by both x and y.
     *
     * @param sxy x and y scale
     */
    public Vector scale(float sxy)
    {
        return new Vector(x * sxy, y * sxy);
    }

    @Override
    public String toString()
    {
        return "(" + x + ", " + y + ")";
    }
}
