package com.tom_roush.pdfbox.filter;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;

import com.tom_roush.pdfbox.cos.COSDictionary;
import com.tom_roush.pdfbox.cos.COSName;
import com.tom_roush.pdfbox.io.IOUtils;
import com.xsooy.jbig2.Jbig2Utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class JBIG2Filter extends Filter{

    private static final int CACHE_SIZE = 1024;

    @Override
    public DecodeResult decode(InputStream encoded, OutputStream decoded, COSDictionary parameters, int index) throws IOException {

//        int bits = parameters.getInt(COSName.BITS_PER_COMPONENT, 1);
//        COSDictionary params = getDecodeParams(parameters, index);

        DecodeResult result = new DecodeResult(parameters);
        result.getParameters().addAll(parameters);
        Jbig2Utils jbig2Utils = new Jbig2Utils();
        byte[] data = new byte[encoded.available()];
        IOUtils.populateBuffer(encoded,data);
        byte[] image = jbig2Utils.converData(data);

        int arrLen = image.length;
        byte[] buffer = new byte[CACHE_SIZE];
        int pos = 0;

        for (int i = 0; i < arrLen; i++)
        {
            if (pos >= buffer.length)
            {
                decoded.write(buffer, 0, pos);
                pos = 0;
            }
            buffer[pos] = image[i];
            pos++;
        }
        decoded.write(buffer, 0, pos);
        return result;
    }

    @Override
    protected void encode(InputStream input, OutputStream encoded, COSDictionary parameters) throws IOException {

    }
}
