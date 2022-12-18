package com.xsooy.jpeg;

public class JpegUtils {

    static {
        System.loadLibrary("jpegUse");
    }

    public native byte[] converData(byte[] data);

    public native void converDataToArray(byte[] data,byte[] ouput);

}
