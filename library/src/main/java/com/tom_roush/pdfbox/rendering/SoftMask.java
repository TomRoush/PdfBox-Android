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

package com.tom_roush.pdfbox.rendering;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.RectF;

import java.io.IOException;

import com.tom_roush.harmony.awt.AWTColor;
import com.tom_roush.harmony.awt.Paint;
import com.tom_roush.harmony.awt.PaintContext;
import com.tom_roush.harmony.awt.geom.AffineTransform;
import com.tom_roush.pdfbox.pdmodel.common.function.PDFunction;
import com.tom_roush.pdfbox.pdmodel.common.function.PDFunctionTypeIdentity;
import com.tom_roush.pdfbox.pdmodel.graphics.color.PDColor;

/**
 * A Paint which applies a soft mask to an underlying Paint.
 *
 * @author Petr Slaby
 * @author John Hewson
 * @author Matthias Bläsing
 * @author Tilman Hausherr
 */
class SoftMask implements Paint
{
    private static final Bitmap.Config ARGB_COLOR_MODEL = Bitmap.Config.ARGB_8888;

    private final Paint paint;
    private final Bitmap mask;
    private final RectF bboxDevice;
    private int bc = 0;
    private final PDFunction transferFunction;

    /**
     * Creates a new soft mask paint.
     *
     * @param paint underlying paint.
     * @param mask soft mask
     * @param bboxDevice bbox of the soft mask in the underlying Graphics2D device space
     * @param backdropColor the color to be used outside the transparency group’s bounding box; if
     * null, black will be used.
     * @param transferFunction the transfer function, may be null.
     */
    SoftMask(Paint paint, Bitmap mask, RectF bboxDevice, PDColor backdropColor, PDFunction transferFunction)
    {
        this.paint = paint;
        this.mask = mask;
        this.bboxDevice = bboxDevice;
        if (transferFunction instanceof PDFunctionTypeIdentity)
        {
            this.transferFunction = null;
        }
        else
        {
            this.transferFunction = transferFunction;
        }
        if (backdropColor != null)
        {
            try
            {
                AWTColor color = new AWTColor(backdropColor.toRGB());
                // http://stackoverflow.com/a/25463098/535646
                bc = (299 * color.getRed() + 587 * color.getGreen() + 114 * color.getBlue()) / 1000;
            }
            catch (IOException ex)
            {
                // keep default
            }
        }
    }

    @Override
    public PaintContext createContext(Rect deviceBounds, AffineTransform xform)
    {
        PaintContext ctx = paint.createContext(deviceBounds, xform);
        return new SoftPaintContext(ctx);
    }

    private class SoftPaintContext implements PaintContext
    {
        private final PaintContext context;

        SoftPaintContext(PaintContext context)
        {
            this.context = context;
        }

        @Override
        public Bitmap.Config getColorModel()
        {
            return ARGB_COLOR_MODEL;
        }

        @Override
        public Bitmap getRaster(int x1, int y1, int w, int h)
        {
            int[] pixels = new int[w];
            Bitmap origin = context.getRaster(x1, y1, w, h);
            int maskWidth = mask.getWidth();
            int maskHeight = mask.getHeight();
            int[] pixelsMask = new int[maskWidth];
            float[] input = null;
            Float[] map = null;

            if (transferFunction != null)
            {
                map = new Float[256];
                input = new float[1];
            }

            // buffer
            Bitmap output = Bitmap.createBitmap(w, h, getColorModel());

            // the soft mask has its own bbox
            x1 = x1 - (int)bboxDevice.left;
            y1 = y1 - (int)bboxDevice.top;

            int gray;
            int pixelInput;
            int[] pixelOutput = new int[4];
            for (int y = 0; y < h; y++)
            {
                origin.getPixels(pixels, 0, w, 0, y, w, 1);
                mask.getPixels(pixelsMask, 0, maskWidth, 0, y1 + y, maskWidth, 1);
                for (int x = 0; x < w; x++)
                {
                    pixelInput = pixels[x];

                    pixelOutput[0] = Color.red(pixelInput);
                    pixelOutput[1] = Color.green(pixelInput);
                    pixelOutput[2] = Color.blue(pixelInput);
                    pixelOutput[3] = Color.alpha(pixelInput);

                    // get the alpha value from the gray mask, if within mask bounds
                    gray = 0;
                    if (x1 + x >= 0 && y1 + y >= 0 && x1 + x < maskWidth && y1 + y < maskHeight)
                    {
                        gray = pixelsMask[x1 + x];
                        int g = Color.red(gray);
                        if (transferFunction != null)
                        {
                            // apply transfer function
                            try
                            {
                                if (map[g] != null)
                                {
                                    // was calculated before
                                    pixelOutput[3] = Math.round(pixelOutput[3] * map[g]);
                                }
                                else
                                {
                                    // calculate and store in map
                                    input[0] = g / 255f;
                                    float f = transferFunction.eval(input)[0];
                                    map[g] = f;
                                    pixelOutput[3] = Math.round(pixelOutput[3] * f);
                                }
                            }
                            catch (IOException ex)
                            {
                                // ignore exception, treat as outside
                                pixelOutput[3] = Math.round(pixelOutput[3] * (bc / 255f));
                            }
                        }
                        else
                        {
                            pixelOutput[3] = Math.round(pixelOutput[3] * (g / 255f));
                        }
                    }
                    else
                    {
                        pixelOutput[3] = Math.round(pixelOutput[3] * (bc / 255f));
                    }
                    pixels[x] = Color.argb(pixelOutput[3], pixelOutput[0], pixelOutput[1], pixelOutput[2]);
                }
                output.setPixels(pixels, 0, w, 0, y, w, 1);
            }

            origin.recycle();
            return output;
        }

        @Override
        public void dispose()
        {
            mask.recycle();
            context.dispose();
        }
    }
}
