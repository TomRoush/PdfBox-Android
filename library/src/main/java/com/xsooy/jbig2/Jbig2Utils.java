package com.xsooy.jbig2;

public class Jbig2Utils {

    static {
        System.loadLibrary("jbig2Use");
    }

//    public native void init();

    public native byte[] converData(byte[] data);

}
