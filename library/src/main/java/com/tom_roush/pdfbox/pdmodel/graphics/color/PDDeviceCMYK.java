package com.tom_roush.pdfbox.pdmodel.graphics.color;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.opengl.Matrix;
import android.util.Log;

import com.tom_roush.pdfbox.android.PDFBoxResourceLoader;
import com.tom_roush.pdfbox.cos.COSName;
import com.tom_roush.pdfbox.io.IOUtils;
import com.xsooy.icc.IccUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class PDDeviceCMYK extends PDDeviceColorSpace
{

    private IccUtils iccUtils;
    /**  The single instance of this class. */
    public static PDDeviceCMYK INSTANCE;
    static
    {
        INSTANCE = new PDDeviceCMYK();
    }

    private final PDColor initialColor = new PDColor(new float[] { 0, 0, 0, 1 }, this);
    //    private ICC_ColorSpace awtColorSpace;
    public volatile boolean initDone = false;

    protected PDDeviceCMYK()
    {
    }

    /**
     * Lazy load the ICC profile, because it's slow.
     */
    protected void init() throws IOException
    {
        // no need to synchronize this check as it is atomic
        if (initDone)
        {
            return;
        }
        synchronized (this)
        {
            // we might have been waiting for another thread, so check again
            if (initDone)
            {
                return;
            }

            InputStream inputStream = PDFBoxResourceLoader.getStream("com/tom_roush/pdfbox/resources/icc/ISOcoated_v2_300_bas.icc");
            byte[] buff = new byte[inputStream.available()];
            IOUtils.populateBuffer(inputStream,buff);
            iccUtils = new IccUtils();
            iccUtils.loadProfileByData(buff);
            initDone = true;
//            if (new File(IccUtils.iccProfileDir+"/ISOcoated_v2_300_bas.icc").exists()) {
//                iccUtils = new IccUtils();
//                iccUtils.loadProfile(IccUtils.iccProfileDir+"/ISOcoated_v2_300_bas.icc");
//            }
//            initDone = true;
        }
    }

//    protected ICC_Profile getICCProfile() throws IOException
//    {
//        // Adobe Acrobat uses "U.S. Web Coated (SWOP) v2" as the default
//        // CMYK profile, however it is not available under an open license.
//        // Instead, the "ISO Coated v2 300% (basICColor)" is used, which
//        // is an open alternative to the "ISO Coated v2 300% (ECI)" profile.
//
//        String resourceName = "/org/apache/pdfbox/resources/icc/ISOcoated_v2_300_bas.icc";
//        InputStream resourceAsStream = PDDeviceCMYK.class.getResourceAsStream(resourceName);
//        if (resourceAsStream == null)
//        {
//            throw new IOException("resource '" + resourceName + "' not found");
//        }
//        try (InputStream is = new BufferedInputStream(resourceAsStream))
//        {
//            return ICC_Profile.getInstance(is);
//        }
//    }

    @Override
    public String getName()
    {
        return COSName.DEVICECMYK.getName();
    }

    @Override
    public int getNumberOfComponents()
    {
        return 4;
    }

    @Override
    public float[] getDefaultDecode(int bitsPerComponent)
    {
        return new float[] { 0, 1, 0, 1, 0, 1, 0, 1 };
    }

    @Override
    public PDColor getInitialColor()
    {
        return initialColor;
    }

    @Override
    public float[] toRGB(float[] value) throws IOException
    {
        init();
//        Log.w("ceshi",String.format("value:%f,%f,%f,%f",value[0],value[1],value[2],value[3]));
        //cmyk to lab
        if (iccUtils!=null) {
            float[] data = new float[3];
            iccUtils.applyCmyk(value,data);
//            float[] lab = toLab(data);
//            float[] xyz = labToXyz(lab);
//            return NormalColorSpace.xyzToRgb(xyz);
            return data;
        }
        return new float[]{(1-value[0])*(1-value[3]),(1-value[1])*(1-value[3]),(1-value[2])*(1-value[3])};
    }

    @Override
    public Bitmap toRGBImage(Bitmap raster) throws IOException {
        init();
        int width = raster.getWidth();
        int height = raster.getHeight();

        ByteBuffer buffer = ByteBuffer.allocate(raster.getRowBytes() * height);
        raster.copyPixelsToBuffer(buffer);
        final byte[] output = buffer.array();

        int[] src = new int[width];
        int[] out = new int[width];
        int numberOfComponents = getNumberOfComponents();
        float[] value = new float[numberOfComponents];
        for (int y = 0; y < height; y++)
        {
            raster.getPixels(src,0,width,0,y,width,1);
            for (int x = 0; x < width; x++)
            {
                for (int i=0;i<numberOfComponents;i++) {
                    value[i] =(output[(x+y*width)*numberOfComponents+i] & 0xff) / 255.f;
                }
                float[] rgb = toRGB(value);
                int color = Color.argb(255,(int)(rgb[0]*255),(int)(rgb[1]*255),(int)(rgb[2]*255));
                out[x] = color;
            }
            raster.setPixels(out,0,width,0,y,width,1);
        }
        return raster;
    }

//    public Bitmap toRGBImage(byte[] raster,int width,int height) throws IOException {
//        init();
//        Bitmap rgbImage = Bitmap.createBitmap(width,height, Bitmap.Config.ARGB_8888);
//        int location = 0;
//        for (int y = 0; y < height; y++)
//        {
//            for (int x = 0; x < width; x++)
//            {
//                float[] value = new float[4];
//                for (int i=0;i<4;i++) {
//                    value[i] = ((raster[location]&0xff)/255.f);
//                    location++;
//                }
//                Log.w("ceshi",String.format("c:%d,m:%d,y:%d,k:%d",(int)(value[0]*100),(int)(value[1]*100),(int)(value[2]*100),(int)(value[3]*100)));
//                float[] rgb = toRGB(value);
////                if (x==0 && y==0) {
//                    Log.w("ceshi",String.format("r:%d,g:%d,b:%d",(int)(rgb[0]*255),(int)(rgb[1]*255),(int)(rgb[2]*255)));
////                }
//                int color = Color.argb(255,(int)(rgb[0]*255),(int)(rgb[1]*255),(int)(rgb[2]*255));
//                rgbImage.setPixel(x,y,color);
//            }
//        }
//        return rgbImage;
//    }

}
