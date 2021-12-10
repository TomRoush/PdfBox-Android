/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.tom_roush.pdfbox.filter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.tom_roush.pdfbox.cos.COSDictionary;
import com.tom_roush.pdfbox.io.IOUtils;

/**
 * Decompresses data encoded using a DCT (discrete cosine transform)
 * technique based on the JPEG standard.
 *
 * @author John Hewson
 */
final class DCTFilter extends Filter
{
    private static final int POS_TRANSFORM = 11;
    private static final String ADOBE = "Adobe";

    @Override
    public DecodeResult decode(InputStream encoded, OutputStream decoded, COSDictionary
        parameters, int index, DecodeOptions options) throws IOException
    {
        // Already ready, just read it back out
        IOUtils.copy(encoded, decoded);

        return new DecodeResult(parameters);
    }

    @Override
    public DecodeResult decode(InputStream encoded, OutputStream decoded,
        COSDictionary parameters, int index) throws IOException
    {
        return decode(encoded, decoded, parameters, index, DecodeOptions.DEFAULT);
    }

//    private Integer getAdobeTransform(IIOMetadata metadata) TODO: PdfBox-Android

//    private int getAdobeTransformByBruteForce(ImageInputStream iis) throws IOException TODO: PdfBox-Android

//    private WritableRaster fromYCCKtoCMYK(Raster raster) TODO: PdfBox-Android

//    private WritableRaster fromYCbCrtoCMYK(Raster raster) TODO: PdfBox-Android

//    private WritableRaster fromBGRtoRGB(Raster raster) TODO: PdfBox-Android

//    private String getNumChannels(ImageReader reader) TODO: PdfBox-Android

    // clamps value to 0-255 range
    private int clamp(float value)
    {
        return (int)((value < 0) ? 0 : ((value > 255) ? 255 : value));
    }

    @Override
    protected void encode(InputStream input, OutputStream encoded, COSDictionary parameters)
        throws IOException
    {
        throw new UnsupportedOperationException("DCTFilter encoding not implemented, use the JPEGFactory methods instead");
    }
}
