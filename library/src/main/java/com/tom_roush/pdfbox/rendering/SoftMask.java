package com.tom_roush.pdfbox.rendering;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.RectF;

import com.tom_roush.harmony.awt.geom.AffineTransform;
import com.tom_roush.pdfbox.pdmodel.common.function.PDFunction;
import com.tom_roush.pdfbox.pdmodel.common.function.PDFunctionTypeIdentity;
import com.tom_roush.pdfbox.pdmodel.graphics.color.PDColor;

import java.io.IOException;

public class SoftMask implements WrapPaint {

    private static final Bitmap.Config ARGB_COLOR_MODEL = Bitmap.Config.ARGB_8888;

    private final WrapPaint paint;
    private Bitmap mask;
    private final RectF bboxDevice;
    private int bc = 0;
    private final PDFunction transferFunction;

    /**
     * Creates a new soft mask paint.
     *
     * @param paint            underlying paint.
     * @param mask             soft mask
     * @param bboxDevice       bbox of the soft mask in the underlying Graphics2D device space
     * @param backdropColor    the color to be used outside the transparency group’s bounding box; if
     *                         null, black will be used.
     * @param transferFunction the transfer function, may be null.
     */
    SoftMask(WrapPaint paint, Bitmap mask, RectF bboxDevice, PDColor backdropColor, PDFunction transferFunction) {
        this.paint = paint;
        this.mask = mask;
        this.bboxDevice = bboxDevice;
        if (transferFunction instanceof PDFunctionTypeIdentity) {
            this.transferFunction = null;
        } else {
            this.transferFunction = transferFunction;
        }
        if (backdropColor != null) {
            try {
                int bgColor = backdropColor.toRGB();
                float red = Color.red(bgColor) / 255F;
                float green = Color.green(bgColor) / 255F;
                float blue = Color.blue(bgColor) / 255F;
                // http://stackoverflow.com/a/25463098/535646
                bc = Math.round(299 * red + 587 * green + 114 * blue) / 1000;
            } catch (IOException ex) {
                // keep default
            }
        }
    }

//    @Override
//    public void drawShader(Bitmap origin, Canvas canvas) {
//        Rect clipBounds = canvas.getClipBounds();
//        Bitmap masked = new SoftPaintContext(origin).getRaster(clipBounds.left, clipBounds.top, clipBounds.width(), clipBounds.height());
//        Rect src = new Rect(0, 0, masked.getWidth(), masked.getHeight());
//        //paint.
//        canvas.drawBitmap(masked, src, new Rect(0, 0, clipBounds.width(), clipBounds.height()), null);
//        masked.recycle();
//    }

    @Override
    public PaintContext createContext(Rect deviceBounds, AffineTransform xform) {
        PaintContext ctx = this.paint.createContext(deviceBounds, xform);
        return new SoftPaintContext(ctx);
    }

    private class SoftPaintContext implements PaintContext {
        private final PaintContext context;
        public SoftPaintContext(PaintContext context) {
            this.context = context;
        }

        @Override
        public Bitmap.Config getColorModel() {
            return ARGB_COLOR_MODEL;
        }

        @Override
        public Bitmap getRaster(int x1, int y1, int w, int h) {
            int[] pixels = new int[w];
            // getRaster would return a w*h bitmap
            Bitmap origin = context.getRaster(x1, y1, w, h);
            // mask size should be greater than w*h
            int maskWidth = mask.getWidth();
            int maskHeight = mask.getHeight();
            int[] pixelsMask = new int[maskWidth];
            float[] input = null;
            Float[] map = null;

            if (transferFunction != null) {
                map = new Float[256];
                input = new float[1];
            }
            Bitmap output = Bitmap.createBitmap(w, h, getColorModel());

            // the soft mask has its own bbox
            x1 = x1 - (int) bboxDevice.left;
            y1 = y1 - (int) bboxDevice.top;

            int pixelInput = 0;
            int[] pixelOutput = new int[4];
            for (int y = 0; y < h; y++) {
                origin.getPixels(pixels, 0, w, 0, y, w, 1);
                mask.getPixels(pixelsMask, 0, maskWidth, 0, y1 + y, maskWidth, 1);
                for (int x = 0; x < w; x++) {
                    pixelInput = pixels[y * w + x];

                    pixelOutput[0] = Color.red(pixelInput);
                    pixelOutput[1] = Color.green(pixelInput);
                    pixelOutput[2] = Color.blue(pixelInput);
                    pixelOutput[3] = Color.alpha(pixelInput);

                    // get the alpha value from the gray mask, if within mask bounds
                    if (x1 + x >= 0 && y1 + y >= 0 && x1 + x < maskWidth && y1 + y < maskHeight) {
                        int maskColor = pixelsMask[(y1 + y) * maskWidth + x1 + x];
                        int g = Color.red(maskColor);
                        if (transferFunction != null) {
                            // apply transfer function
                            try {
                                if (map[g] != null) {
                                    // was calculated before
                                    pixelOutput[3] = Math.round(pixelOutput[3] * map[g]);
                                } else {
                                    // calculate and store in map
                                    input[0] = g / 255f;
                                    float f = transferFunction.eval(input)[0];
                                    map[g] = f;
                                    pixelOutput[3] = Math.round(pixelOutput[3] * f);
                                }
                            } catch (IOException ex) {
                                // ignore exception, treat as outside
                                pixelOutput[3] = Math.round(pixelOutput[3] * (bc / 255f));
                            }
                        } else {
                            pixelOutput[3] = Math.round(pixelOutput[3] * (g / 255f));
                        }
                    } else {
                        pixelOutput[3] = Math.round(pixelOutput[3] * (bc / 255f));
                    }
                    pixels[y * w + x] = Color.argb(pixelOutput[3], pixelOutput[0], pixelOutput[1], pixelOutput[2]);
                }
                output.setPixels(pixels, 0, w, 0, y, w, 1);
            }

            // buffer
            origin.recycle();
            return output;
        }

        @Override
        public void dispose() {
            mask.recycle();
        }
    }
}
