package com.tom_roush.pdfbox.pdmodel.graphics.shading;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.util.Log;

import com.tom_roush.harmony.awt.geom.AffineTransform;
import com.tom_roush.pdfbox.cos.COSArray;
import com.tom_roush.pdfbox.cos.COSBoolean;
import com.tom_roush.pdfbox.rendering.PaintContext;
import com.tom_roush.pdfbox.util.Matrix;

import java.io.IOException;

public class AxialShadingContext extends ShadingContext implements PaintContext {

    private PDShadingType2 axialShadingType;

    private final float[] coords;
    private final float[] domain;
    private final boolean[] extend;
    private final double x1x0;
    private final double y1y0;
    private final float d1d0;
    private final double denom;

    private final int factor;
    private final int[] colorTable;

    private AffineTransform rat;

    /**
     * Constructor creates an instance to be used for fill operations.
     *
     * @param shading the shading type to be used
     * @param xform transformation for user to device space
     * @param matrix the pattern matrix concatenated with that of the parent content stream
     * @param deviceBounds the bounds of the area to paint, in device units
     * @throws IOException if there is an error getting the color space or doing color conversion.
     */
    public AxialShadingContext(PDShadingType2 shading, AffineTransform xform,
                               Matrix matrix, Rect deviceBounds) throws IOException
    {
        super(shading, xform, matrix);
        this.axialShadingType = shading;
        coords = shading.getCoords().toFloatArray();

        // domain values
        if (shading.getDomain() != null)
        {
            domain = shading.getDomain().toFloatArray();
        }
        else
        {
            // set default values
            domain = new float[] { 0, 1 };
        }
        // extend values
        COSArray extendValues = shading.getExtend();
        if (extendValues != null)
        {
            extend = new boolean[2];
            extend[0] = ((COSBoolean) extendValues.getObject(0)).getValue();
            extend[1] = ((COSBoolean) extendValues.getObject(1)).getValue();
        }
        else
        {
            // set default values
            extend = new boolean[] { false, false };
        }
        // calculate some constants to be used in getRaster
        x1x0 = coords[2] - coords[0];
        y1y0 = coords[3] - coords[1];
        d1d0 = domain[1] - domain[0];
        denom = Math.pow(x1x0, 2) + Math.pow(y1y0, 2);

        try
        {
            // get inverse transform to be independent of current user / device space
            // when handling actual pixels in getRaster()
            rat = matrix.createAffineTransform().createInverse();
            rat.concatenate(xform.createInverse());
        }
        catch (AffineTransform.NoninvertibleTransformException ex)
        {
            Log.e("Pdfbox-Android", ex.getMessage() + ", matrix: " + matrix, ex);
            rat = new AffineTransform();
        }

        // shading space -> device space
        AffineTransform shadingToDevice = (AffineTransform)xform.clone();
        shadingToDevice.concatenate(matrix.createAffineTransform());

        // worst case for the number of steps is opposite diagonal corners, so use that
        double dist = Math.sqrt(Math.pow(deviceBounds.right - deviceBounds.left, 2) +
                Math.pow(deviceBounds.bottom - deviceBounds.top, 2));
        factor = (int) Math.ceil(dist);

        // build the color table for the given number of steps
        colorTable = calcColorTable();
    }

    /**
     * Calculate the color on the axial line and store them in an array.
     *
     * @return an array, index denotes the relative position, the corresponding
     * value is the color on the axial line
     * @throws IOException if the color conversion fails.
     */
    private int[] calcColorTable() throws IOException
    {
        int[] map = new int[factor + 1];
        if (factor == 0 || d1d0 == 0)
        {
            float[] values = axialShadingType.evalFunction(domain[0]);
            map[0] = convertToRGB(values);
        }
        else
        {
            for (int i = 0; i <= factor; i++)
            {
                float t = domain[0] + d1d0 * i / factor;
                float[] values = axialShadingType.evalFunction(t);
                map[i] = convertToRGB(values);
            }
        }
        return map;
    }

    @Override
    public void dispose() {
        axialShadingType = null;
    }

    @Override
    public Bitmap.Config getColorModel() {
        return super.getColorModel();
    }

    @Override
    public Bitmap getRaster(int x, int y, int w, int h) {
        // create writable raster
        Bitmap raster = Bitmap.createBitmap(w, h, getColorModel());
        int[] data = new int[w * h];
        boolean useBackground;
        float[] values = new float[2];
        for (int j = 0; j < h; j++)
        {
            for (int i = 0; i < w; i++)
            {
                useBackground = false;
                values[0] = x + i;
                values[1] = y + j;
                rat.transform(values, 0, values, 0, 1);
                double inputValue = x1x0 * (values[0] - coords[0]) + y1y0 * (values[1] - coords[1]);
                // TODO this happens if start == end, see PDFBOX-1442
                if (denom == 0)
                {
                    if (getBackground() == null)
                    {
                        continue;
                    }
                    useBackground = true;
                }
                else
                {
                    inputValue /= denom;
                }
                // input value is out of range
                if (inputValue < 0)
                {
                    // the shading has to be extended if extend[0] == true
                    if (extend[0])
                    {
                        inputValue = domain[0];
                    }
                    else
                    {
                        if (getBackground() == null)
                        {
                            continue;
                        }
                        useBackground = true;
                    }
                }
                // input value is out of range
                else if (inputValue > 1)
                {
                    // the shading has to be extended if extend[1] == true
                    if (extend[1])
                    {
                        inputValue = domain[1];
                    }
                    else
                    {
                        if (getBackground() == null)
                        {
                            continue;
                        }
                        useBackground = true;
                    }
                }
                int value;
                if (useBackground)
                {
                    // use the given background color values
                    value = getRgbBackground();
                }
                else
                {
                    int key = (int) (inputValue * factor);
                    value = colorTable[key];
                }
                int index = j * w + i;
                int r = value & 255;
                value >>= 8;
                int g = value & 255;
                value >>= 8;
                int b = value & 255;
                data[index] = Color.argb(255, r, g, b);
            }
        }
        raster.setPixels(data, 0, w, 0, 0, w, h);
        return raster;
    }
}
