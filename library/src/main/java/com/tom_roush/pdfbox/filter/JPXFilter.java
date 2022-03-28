package com.tom_roush.pdfbox.filter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;

import com.gemalto.jp2.JP2Decoder;
import com.gemalto.jp2.JP2Encoder;
import com.tom_roush.pdfbox.cos.COSDictionary;
import com.tom_roush.pdfbox.cos.COSName;
import com.tom_roush.pdfbox.io.IOUtils;
import com.tom_roush.pdfbox.pdmodel.graphics.color.PDJPXColorSpace;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * jpx decode support is provided by JP2ForAndroid.
 * subsampling and source region is nor support in current version(1.0.3).
 * (source region support may be coming soon.)
 */
public final class JPXFilter extends Filter {

    /**
     * {@inheritDoc}
     */
    @Override
    public DecodeResult decode(InputStream encoded, OutputStream decoded, COSDictionary
            parameters, int index, DecodeOptions options) throws IOException {
        DecodeResult result = new DecodeResult(new COSDictionary());
        result.getParameters().addAll(parameters);
        Bitmap image = readJPX(encoded, options, result);

        int arrLen = image.getWidth() * image.getHeight();
        int[] pixels = new int[arrLen];
        image.getPixels(pixels, 0, image.getWidth(), 0, 0, image.getWidth(), image.getHeight());
        for (int i = 0; i < arrLen; i++) {
            int color = pixels[i];
            decoded.write(Color.red(color));
            decoded.write(Color.green(color));
            decoded.write(Color.blue(color));
        }
        return result;
    }

    @Override
    public DecodeResult decode(InputStream encoded, OutputStream decoded,
                               COSDictionary parameters, int index) throws IOException {
        return decode(encoded, decoded, parameters, index, DecodeOptions.DEFAULT);
    }

    // try to read using JP2ForAndroid
    private Bitmap readJPX(InputStream input, DecodeOptions options, DecodeResult result) throws IOException {
        JP2Decoder decoder = new JP2Decoder(input);
        // TODO: uncomment after upgrading JP2ForAndroid
        // decoder.setSourceRegion(options.getSourceRegion());
        Bitmap image = decoder.decode();

        COSDictionary parameters = result.getParameters();

        // "Decode shall be ignored, except in the case where the image is treated as a mask"
        if (!parameters.getBoolean(COSName.IMAGE_MASK, false)) {
            parameters.setItem(COSName.DECODE, null);
        }

        // override dimensions, see PDFBOX-1735
        parameters.setInt(COSName.WIDTH, image.getWidth());
        parameters.setInt(COSName.HEIGHT, image.getHeight());

        // extract embedded color space
        if (!parameters.containsKey(COSName.COLORSPACE) && Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
            // COLORSPACE exists in parameters usually, so here would not be reached in most cases
            result.setColorSpace(new PDJPXColorSpace(image.getColorSpace()));
        }

        return image;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void encode(InputStream input, OutputStream encoded, COSDictionary parameters)
            throws IOException {
        Bitmap bitmap = BitmapFactory.decodeStream(input);
        byte[] jpeBytes = new JP2Encoder(bitmap).encode();
        IOUtils.copy(new ByteArrayInputStream(jpeBytes), encoded);
        encoded.flush();
    }
}
