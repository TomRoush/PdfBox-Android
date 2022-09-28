package com.tom_roush.pdfbox.pdmodel.graphics.shading;

import android.graphics.Rect;

import com.tom_roush.pdfbox.cos.COSArray;
import com.tom_roush.pdfbox.cos.COSBoolean;

import java.io.IOException;

public class AxialShadingContext extends ShadingContext {

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

    public AxialShadingContext(PDShadingType2 shading, Rect deviceBounds) throws IOException {
        super(shading);
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

//        try
//        {
            // get inverse transform to be independent of current user / device space
            // when handling actual pixels in getRaster()
//            rat = matrix.createAffineTransform().createInverse();
//            rat.concatenate(xform.createInverse());
//        }
//        catch (AffineTransform.NoninvertibleTransformException ex)
//        {
//            LOG.error(ex.getMessage() + ", matrix: " + matrix, ex);
//            rat = new AffineTransform();
//        }

        // shading space -> device space
//        AffineTransform shadingToDevice = (AffineTransform)xform.clone();
//        shadingToDevice.concatenate(matrix.createAffineTransform());

        // worst case for the number of steps is opposite diagonal corners, so use that
        double dist = Math.sqrt(Math.pow(deviceBounds.right - deviceBounds.left, 2) +
                Math.pow(deviceBounds.bottom - deviceBounds.top, 2));
        factor = (int) Math.ceil(dist);

        // build the color table for the given number of steps
        colorTable = calcColorTable();
    }

    private int[] calcColorTable() throws IOException
    {

        int[] map = new int[factor + 1];
        if (factor == 0 || Float.compare(d1d0, 0) == 0)
        {
            float[] values = axialShadingType.evalFunction(domain[0]);

            map[0] = convertToRGB(values);
        }
        else
        {
//            IccUtils iccUtils = new IccUtils();
//            iccUtils.loadProfile("/storage/emulated/0/Android/data/com.example.test/files/HFA_Eps15000_MK_Agave-Sisal.icc");
//            iccUtils.loadProfile2("/storage/emulated/0/Android/data/com.example.test/files/ISOcoated_v2_300_bas.icc","/storage/emulated/0/Android/data/com.example.test/files/HFA_Eps15000_MK_Agave-Sisal.icc");

            StringBuilder builder = new StringBuilder();
            for (int i = 0; i <= factor; i++)
            {
                float t = domain[0] + d1d0 * i / factor;
                float[] values = axialShadingType.evalFunction(t);
//                builder.delete(0,builder.length());
//                for (float jj:values)
//                    builder.append(jj+",");
//                Log.w("ceshi","calcColorTable:"+builder.toString());
//                int normRGBValues;
//                normRGBValues = (int) (values[0] * 65535);
//                normRGBValues |= (int) (values[1] * 65535) << 8;
//                normRGBValues |= (int) (values[2] * 65535) << 16;
//                normRGBValues |= (int) (values[3] * 65535) << 24;
//                iccUtils.applyCmyk(values);
//                builder.delete(0,builder.length());
//                for (float jj:values)
//                    builder.append(jj+",");
//                Log.w("ceshi","转换结果:"+builder.toString());
//                float[] test = new float[] {(map[i]>>24&0xff)/255.f, (map[i]>>16&0xff)/255.f,(map[0]>>8&0xff)/255.f,(map[0]&0xff)/255.f};
                map[i] = convertToRGB(values);

//                Log.w("ceshi",String.format("r:%d,g:%d,b:%d",map[i]>>16&0xff,map[0]>>8&0xff,map[0]&0xff));
            }
        }
        return map;
    }


    public int[] getRaster(int x, int y, int w, int h)
    {
        // create writable raster
//        Log.w("ceshi",String.format("x:%d,y:%d,w:%d,h:%h",x,y,w,h));
//        System.out.println(String.format("x:%d,y:%d,w:%d,h:%h",x,y,w,h));
        boolean useBackground;
//        int[] data = new int[w * h * 4];
        int[] data = new int[w * h];
        float[] values = new float[2];
        for (int j = 0; j < h; j++)
        {
            for (int i = 0; i < w; i++)
            {
                useBackground = false;
                values[0] = x + i;
                values[1] = y + j;
//                rat.transform(values, 0, values, 0, 1);
                double inputValue = x1x0 * (values[0] - coords[0]) + y1y0 * (values[1] - coords[1]);
                // TODO this happens if start == end, see PDFBOX-1442
                if (Double.compare(denom, 0) == 0)
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
                int index = (j * w + i);
                data[index] = value;
//                int index = (j * w + i) * 4;
//                data[index] = value & 255;
//                value >>= 8;
//                data[index + 1] = value & 255;
//                value >>= 8;
//                data[index + 2] = value & 255;
//                data[index + 3] = 255;
//                System.out.println(String.format("r:%d,g:%d,b:%d,a:%h",data[index],data[index+1],data[index+2],data[index+3]));
            }
        }
//        raster.setPixels(0, 0, w, h, data);
        return data;
    }


}
