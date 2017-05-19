package com.tom_roush.harmony.awt;

import android.graphics.Color;

/**
 * Allows for color manipulation similar to the default Java Color class
 *
 * @author Tom Roush
 */
public class AWTColor {
    /**
     * The color white.
     */
    public final static AWTColor white = new AWTColor(255, 255, 255);

    /**
     * The color white.
     */
    public final static AWTColor WHITE = white;

    /**
     * The color light gray.
     */
    public final static AWTColor lightGray = new AWTColor(192, 192, 192);

    /**
     * The color light gray.
     */
    public final static AWTColor LIGHT_GRAY = lightGray;

    /**
     * The color gray.
     */
    public final static AWTColor gray = new AWTColor(128, 128, 128);

    /**
     * The color gray.
     */
    public final static AWTColor GRAY = gray;

    /**
     * The color dark gray.
     */
    public final static AWTColor darkGray = new AWTColor(64, 64, 64);

    /**
     * The color dark gray.
     */
    public final static AWTColor DARK_GRAY = darkGray;

    /**
     * The color black.
     */
    public final static AWTColor black = new AWTColor(0, 0, 0);

    /**
     * The color black.
     */
    public final static AWTColor BLACK = black;

    /**
     * The color red.
     */
    public final static AWTColor red = new AWTColor(255, 0, 0);

    /**
     * The color red.
     */
    public final static AWTColor RED = red;

    /**
     * The color pink.
     */
    public final static AWTColor pink = new AWTColor(255, 175, 175);

    /**
     * The color pink.
     */
    public final static AWTColor PINK = pink;

    /**
     * The color orange.
     */
    public final static AWTColor orange = new AWTColor(255, 200, 0);

    /**
     * The color orange.
     */
    public final static AWTColor ORANGE = orange;

    /**
     * The color yellow.
     */
    public final static AWTColor yellow = new AWTColor(255, 255, 0);

    /**
     * The color yellow.
     */
    public final static AWTColor YELLOW = yellow;

    /**
     * The color green.
     */
    public final static AWTColor green = new AWTColor(0, 255, 0);

    /**
     * The color green.
     */
    public final static AWTColor GREEN = green;

    /**
     * The color magenta.
     */
    public final static AWTColor magenta = new AWTColor(255, 0, 255);

    /**
     * The color magenta.
     */
    public final static AWTColor MAGENTA = magenta;

    /**
     * The color cyan.
     */
    public final static AWTColor cyan = new AWTColor(0, 255, 255);

    /**
     * The color cyan.
     */
    public final static AWTColor CYAN = cyan;

    /**
     * The color blue.
     */
    public final static AWTColor blue = new AWTColor(0, 0, 255);

    /**
     * The color blue.
     */
    public final static AWTColor BLUE = blue;

    public int color;

    /**
     * Creates an AWTColor from a color int
     *
     * @param c The color integer
     */
    public AWTColor(int c) {
        color = c;
    }

    /**
     * Creates an AWTColor with the specified RGB values that is fully opaque
     *
     * @param r The red component
     * @param g The green component
     * @param b The blue component
     */
    public AWTColor(int r, int g, int b)
    {
        this(r, g, b, 255);
    }

    /**
     * Creates an AWTColor with the specified RGBA values
     *
     * @param r The red component
     * @param g The green component
     * @param b The blue component
     * @param a The alpha component
     */
    public AWTColor(int r, int g, int b, int a)
    {
        color = Color.argb(a, r, g, b);
    }

    /**
     * Creates an AWTColor with the specified RGB values
     *
     * @param r The red component as a float in the range (0.0f - 1.0f)
     * @param g The green component as a float in the range (0.0f - 1.0f)
     * @param b The blue component as a float in the range (0.0f - 1.0f)
     */
    public AWTColor(float r, float g, float b)
    {
        color = Color.rgb((int) (r * 255.0f), (int) (g * 255.0f), (int) (b * 255.0f));
    }

    /**
     * Returns the red component of this color
     *
     * @return the value of the red component
     */
    public int getRed()
    {
        return Color.red(color);
    }

    /**
     * Returns the green component of this color
     *
     * @return the value of the green component
     */
    public int getGreen()
    {
        return Color.green(color);
    }

    /**
     * Returns the blue component of this color
     *
     * @return the value of the blue component
     */
    public int getBlue()
    {
        return Color.blue(color);
    }

    /**
     * Returns the alpha component of this color
     *
     * @return the value of the alpha component
     */
    public int getAlpha()
    {
        return Color.alpha(color);
    }

    /**
     * Returns the RGB values range (0.0 - 1.0) as a float array
     *
     * @param compArray the array that will hold the values or null
     * @return the float array of the RGB values
     */
    public float[] getRGBColorComponents(float[] compArray)
    {
        float[] retval;
        if (compArray == null)
        {
            retval = new float[3];
        }
        else
        {
            retval = compArray;
        }
        retval[0] = (float) getRed() / 255.0f;
        retval[1] = (float) getGreen() / 255.0f;
        retval[2] = (float) getBlue() / 255.0f;
        return retval;
    }
}
