package com.xsooy.icc;

import static com.tom_roush.pdfbox.pdmodel.graphics.color.PDColorSpace.TYPE_CMYK;
import static com.tom_roush.pdfbox.pdmodel.graphics.color.PDColorSpace.TYPE_GRAY;
import static com.tom_roush.pdfbox.pdmodel.graphics.color.PDColorSpace.TYPE_RGB;

public class IccUtils {

    static {
        System.loadLibrary("icc");
    }

    public native int loadProfile(String path);

    public native int loadProfileByData(byte[] data);

//    public native int loadProfile2(String path,String path2);

    public native float apply(float color);

    //gray to xyz
    public native void applyGray(float[] in,float[] out);

    //cmyk to lab
    public native void applyCmyk(float[] in,float[] out);

    public static int getIccColorType(int code) {
        switch (code) {
            case 0x47524159:
                return TYPE_GRAY;
            case 0x434D594B:
                return TYPE_CMYK;
//            case 0x52474220:
//                return TYPE_RGB;
            default:
                return TYPE_RGB;
        }
    }

}
