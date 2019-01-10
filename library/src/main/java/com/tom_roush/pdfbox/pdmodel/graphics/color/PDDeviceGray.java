package com.tom_roush.pdfbox.pdmodel.graphics.color;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;

import com.tom_roush.pdfbox.cos.COSName;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * A color space with black, white, and intermediate shades of gray.
 *
 * @author Ben Litchfield
 * @author John Hewson
 */
public final class PDDeviceGray extends PDDeviceColorSpace
{
    /** The single instance of this class. */
    public static final PDDeviceGray INSTANCE = new PDDeviceGray();
    
    private final PDColor initialColor = new PDColor(new float[] { 0 }, this);

    private PDDeviceGray()
    {
    }

    @Override
    public String getName()
    {
        return COSName.DEVICEGRAY.getName();
    }

    @Override
    public int getNumberOfComponents()
    {
        return 1;
    }

    @Override
    public float[] getDefaultDecode(int bitsPerComponent)
    {
        return new float[] { 0, 1 };
    }

    @Override
    public PDColor getInitialColor()
    {
        return initialColor;
    }

    @Override
    public float[] toRGB(float[] value)
    {
        return new float[] { value[0], value[0], value[0] };
    }

    @Override
    public Bitmap toRGBImage(Bitmap raster) throws IOException
    {
        if (raster.getConfig() != Bitmap.Config.ALPHA_8)
        {
            Log.e("PdfBox-Android", "Raster in PDDevicGrey was not ALPHA_8");
        }

        int width = raster.getWidth();
        int height = raster.getHeight();

        Bitmap image = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        ByteBuffer buffer = ByteBuffer.allocate(raster.getRowBytes() * height);
        raster.copyPixelsToBuffer(buffer);
        byte[] gray = buffer.array();

        int[] rgb = new int[width * height];
        image.getPixels(rgb, 0, width, 0, 0, width, height);
        for (int y = 0; y < height; y++)
        {
            for (int x = 0; x < width; x++)
            {
                int idx = x + width * y;
                int value = gray[idx];
                rgb[idx] = Color.argb(255, value, value, value);
            }
        }
        image.setPixels(rgb, 0, width, 0, 0, width, height);
        return image;
    }
}
