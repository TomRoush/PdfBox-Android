package com.tom_roush.pdfbox.rendering;

import android.graphics.ColorSpace;
import android.graphics.Rect;

import com.tom_roush.harmony.awt.geom.AffineTransform;

public interface WrapPaint {

    /**
     * Creates and returns a {@link PaintContext} used to
     * generate the color pattern.
     * The arguments to this method convey additional information
     * about the rendering operation that may be
     * used or ignored on various implementations of the {@code Paint} interface.
     * A caller must pass non-{@code null} values for all of the arguments
     * except for the {@code ColorModel} argument which may be {@code null} to
     * indicate that no specific {@code ColorModel} type is preferred.
     * Implementations of the {@code Paint} interface are allowed to use or ignore
     * any of the arguments as makes sense for their function, and are
     * not constrained to use the specified {@code ColorModel} for the returned
     * {@code PaintContext}, even if it is not {@code null}.
     * Implementations are allowed to throw {@code NullPointerException} for
     * any {@code null} argument other than the {@code ColorModel} argument,
     * but are not required to do so.
     *
     * @param deviceBounds the device space bounding box
     *                     of the graphics primitive being rendered.
     *                     Implementations of the {@code Paint} interface
     *                     are allowed to throw {@code NullPointerException}
     *                     for a {@code null deviceBounds}.
     * @param xform the {@link AffineTransform} from user
     *              space into device space.
     *                     Implementations of the {@code Paint} interface
     *                     are allowed to throw {@code NullPointerException}
     *                     for a {@code null xform}.
     * @return the {@code PaintContext} for
     *         generating color patterns.
     * @see PaintContext
     * @see ColorSpace
     * @see Rect
     * @see Rect
     * @see AffineTransform
     */
    public PaintContext createContext(Rect deviceBounds, AffineTransform xform);
}
