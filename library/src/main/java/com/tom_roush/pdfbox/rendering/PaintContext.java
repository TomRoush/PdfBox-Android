package com.tom_roush.pdfbox.rendering;

import android.graphics.Bitmap;
import android.graphics.ColorSpace;

public interface PaintContext {

    /**
     * Releases the resources allocated for the operation.
     */
    public void dispose();

    /**
     * Returns the {@code ColorModel} of the output.  Note that
     * this {@code ColorModel} might be different from the hint
     * specified in the createContext method of
     * {@code Paint}.  Not all {@code PaintContext} objects are
     * capable of generating color patterns in an arbitrary
     * {@code ColorModel}.
     * @return the {@code ColorModel} of the output.
     */
    Bitmap.Config getColorModel();

    /**
     * Returns a {@code Raster} containing the colors generated for
     * the graphics operation.
     * @param x the x coordinate of the area in device space
     * for which colors are generated.
     * @param y the y coordinate of the area in device space
     * for which colors are generated.
     * @param w the width of the area in device space
     * @param h the height of the area in device space
     * @return a {@code Raster} representing the specified
     * rectangular area and containing the colors generated for
     * the graphics operation.
     */
    Bitmap getRaster(int x, int y, int w, int h);
}
