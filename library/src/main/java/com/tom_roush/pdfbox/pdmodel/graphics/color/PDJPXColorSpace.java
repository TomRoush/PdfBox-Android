package com.tom_roush.pdfbox.pdmodel.graphics.color;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorSpace;
import android.os.Build;

import java.io.IOException;

public class PDJPXColorSpace  extends PDColorSpace{
    private final ColorSpace colorSpace;

    /**
     * Creates a new JPX color space from the given AWT color space.
     * @param colorSpace AWT color space from a JPX image
     */
    public PDJPXColorSpace(ColorSpace colorSpace)
    {
        this.colorSpace = colorSpace;
    }

    @Override
    public String getName() {
        return "JPX";
    }

    @Override
    public int getNumberOfComponents() {
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.O){
            return colorSpace.getComponentCount();
        }
        return 0;
    }

    @Override
    public float[] getDefaultDecode(int bitsPerComponent) {
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
            int n = getNumberOfComponents();
            float[] decode = new float[n * 2];
            for (int i = 0; i < n; i++) {
                decode[i * 2] = colorSpace.getMinValue(i);
                decode[i * 2 + 1] = colorSpace.getMaxValue(i);
            }
            return decode;
        }
        return new float[0];
    }

    @Override
    public PDColor getInitialColor() {
        throw new UnsupportedOperationException("JPX color spaces don't support drawing");
    }

    @Override
    public float[] toRGB(float[] value) throws IOException {
        throw new UnsupportedOperationException("JPX color spaces don't support drawing");
    }

    @Override
    public Bitmap toRGBImage(Bitmap raster) throws IOException {
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.O){
            Bitmap dest = Bitmap.createBitmap(raster.getWidth(), raster.getHeight(), Bitmap.Config.RGB_565, false, colorSpace);
            Canvas canvas = new Canvas(dest);
            canvas.drawBitmap(raster, 0, 0, null);
            return dest;
        }
        return null;
    }
}
