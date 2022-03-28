package com.tom_roush.pdfbox.pdmodel.graphics.shading;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;

import androidx.test.platform.app.InstrumentationRegistry;

import com.tom_roush.harmony.javax.imageio.stream.MemoryCacheImageOutputStream;
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader;
import com.tom_roush.pdfbox.cos.COSArray;
import com.tom_roush.pdfbox.cos.COSDictionary;
import com.tom_roush.pdfbox.cos.COSFloat;
import com.tom_roush.pdfbox.cos.COSInteger;
import com.tom_roush.pdfbox.cos.COSName;
import com.tom_roush.pdfbox.cos.COSStream;
import com.tom_roush.pdfbox.pdmodel.PDDocument;
import com.tom_roush.pdfbox.pdmodel.PDPage;
import com.tom_roush.pdfbox.pdmodel.PDPageContentStream;
import com.tom_roush.pdfbox.pdmodel.common.function.PDFunctionType2;
import com.tom_roush.pdfbox.pdmodel.graphics.color.PDDeviceRGB;
import com.tom_roush.pdfbox.rendering.ImageType;
import com.tom_roush.pdfbox.rendering.PDFRenderer;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

public class ShadingEnd2EndTest {

    private Context testContext;

    @Before
    public void setUp() {
        testContext = InstrumentationRegistry.getInstrumentation().getContext();
        PDFBoxResourceLoader.init(testContext);
    }

    @Test
    public void TestShading2() {
        try {
            Bitmap bitmap = getShadingBitmap(2);
            int height = bitmap.getHeight();
            Assert.assertEquals("(5, -5) should be white", Color.WHITE, bitmap.getPixel(5, height - 5));
            int cdiff = calcColorDiff(Color.RED, bitmap.getPixel(13, height - 13));
            Assert.assertTrue("(13, -13) should be red, diff:" + cdiff, cdiff < 15);
            cdiff = calcColorDiff(Color.GREEN, bitmap.getPixel(95, height - 95));
            Assert.assertTrue("(95, -95) should be green, diff:" + cdiff, cdiff < 35);
            Assert.assertEquals("(110, -110) should be white", Color.WHITE, bitmap.getPixel(110, height - 110));
        }
        catch (IOException e){
            Assert.fail("Exception: " + e.getMessage());
        }
    }

    @Test
    public void TestShading3(){
        try {
            Bitmap bitmap = getShadingBitmap(3);
            int cdiff = calcColorDiff(Color.rgb(10, 10, 10), bitmap.getPixel(100, 394));
            Assert.assertTrue("(100, 394) should be black, diff: " + cdiff, cdiff < 20);

            cdiff = calcColorDiff(Color.rgb(128, 128, 128), bitmap.getPixel(102, 367));
            Assert.assertTrue("(102, 367) should be gray, diff: " + cdiff, cdiff < 20);

            cdiff = calcColorDiff(Color.rgb(255, 255, 255), bitmap.getPixel(102, 340));
            Assert.assertTrue("(102, 340) should be white, diff: " + cdiff, cdiff < 20);

            cdiff = calcColorDiff(Color.rgb(153, 153, 153), bitmap.getPixel(130, 390));
            Assert.assertTrue("(130, 390) should be gray, diff: " + cdiff, cdiff < 20);
        }
        catch (IOException e){
            Assert.fail("Exception: " + e.getMessage());
        }
    }

    @Test
    public void TestShading4(){
        try {
            Bitmap bitmap = getShadingBitmap(4);
            int height = bitmap.getHeight();
            int cdiff = calcColorDiff(Color.RED, bitmap.getPixel(5, height - 2));
            Assert.assertTrue("(5, -2) should be red, diff: " + cdiff, cdiff < 20);

            cdiff = calcColorDiff(Color.GREEN, bitmap.getPixel(100, height - 97));
            Assert.assertTrue("(100, -97) should be green, diff: " + cdiff, cdiff < 25);

            cdiff = calcColorDiff(Color.BLUE, bitmap.getPixel(195, height - 2));
            Assert.assertTrue("(195, -2) should be green, diff: " + cdiff, cdiff < 25);

            cdiff = calcColorDiff(Color.WHITE, bitmap.getPixel(5, height - 8));
            Assert.assertTrue("(5, -8) should be white, diff: " + cdiff, cdiff < 20);

            cdiff = calcColorDiff(Color.WHITE, bitmap.getPixel(195, height - 10));
            Assert.assertTrue("(195, -10) should be white, diff: " + cdiff, cdiff < 20);
        }
        catch (IOException e){
            Assert.fail("Exception: " + e.getMessage());
        }
    }

    private static int calcColorDiff(int a, int cb) {
        int r = Color.red(cb);
        int g = Color.green(cb);
        int b = Color.blue(cb);
        Log.i("Test", String.format("(%d, %d, %d)", r, g, b));
        return Math.abs(Color.red(a) - r) +
                Math.abs(Color.green(a) - g) +
                Math.abs(Color.blue(a) - b);
    }

    public static Bitmap getShadingBitmap(int stype) throws IOException {
        PDShading shading;
        if(stype == 2){
            shading = createShading2();
        }
        else if(stype == 3){
            shading = createShading3();
        }
        else if(stype == 4){
            shading = createShading4();
        }
        else{
            throw new UnsupportedOperationException("shading type not support:" + stype);
        }
        PDDocument document = new PDDocument();
        PDPage page = new PDPage();
        document.addPage(page);
        try (PDPageContentStream contentStream = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, false))
        {
            contentStream.shadingFill(shading);
        }
        PDFRenderer renderer = new PDFRenderer(document);
        // Render the image to an RGB Bitmap
        return renderer.renderImage(0, 1, ImageType.RGB);
    }


    private static PDShadingType4 createShading4() throws IOException {
        // See PDF 32000 specification,
// 8.7.4.5.5 Type 4 Shadings (Free-Form Gouraud-Shaded Triangle Meshes)
        PDShadingType4 gouraudShading = new PDShadingType4(new COSStream());
        gouraudShading.setShadingType(PDShading.SHADING_TYPE4);
// we use multiple of 8, so that no padding is needed
        gouraudShading.setBitsPerFlag(8);
        gouraudShading.setBitsPerCoordinate(16);
        gouraudShading.setBitsPerComponent(8);
        COSArray decodeArray = new COSArray();
// coordinates x y map 16 bits 0..FFFF to 0..FFFF to make your life easy
// so no calculation is needed, but you can only use integer coordinates
// for real numbers, you'll need smaller bounds, e.g. 0xFFFF / 0xA = 0x1999
// would allow 1 point decimal result coordinate.
// See in PDF specification: 8.9.5.2 Decode Arrays
        decodeArray.add(COSInteger.ZERO);
        decodeArray.add(COSInteger.get(0xFFFF));
        decodeArray.add(COSInteger.ZERO);
        decodeArray.add(COSInteger.get(0xFFFF));
// colors r g b map 8 bits from 0..FF to 0..1
        decodeArray.add(COSInteger.ZERO);
        decodeArray.add(COSInteger.ONE);
        decodeArray.add(COSInteger.ZERO);
        decodeArray.add(COSInteger.ONE);
        decodeArray.add(COSInteger.ZERO);
        decodeArray.add(COSInteger.ONE);
        gouraudShading.setDecodeValues(decodeArray);
        gouraudShading.setColorSpace(PDDeviceRGB.INSTANCE);

// Function is not required for type 4 shadings and not really useful,
// because if a function would be used, each edge "color" of a triangle would be one value,
// which would then transformed into n color components by the function so it is
// difficult to get 3 "extremes".

        OutputStream os = ((COSStream) gouraudShading.getCOSObject()).createOutputStream();
        MemoryCacheImageOutputStream mcos = new MemoryCacheImageOutputStream(os);

// Vertex 1, starts with flag1
// (flags always 0 for vertices of start triangle)
        mcos.writeByte(0);
// x1 y1 (left corner)
        mcos.writeShort(0);
        mcos.writeShort(0);
// r1 g1 b1 (red)
        mcos.writeByte(0xFF);
        mcos.writeByte(0);
        mcos.writeByte(0);

// Vertex 2, starts with flag2
        mcos.writeByte(0);
// x2 y2 (top corner)
        mcos.writeShort(100);
        mcos.writeShort(100);
// r2 g2 b2 (green)
        mcos.writeByte(0);
        mcos.writeByte(0xFF);
        mcos.writeByte(0);

// Vertex 3, starts with flag3
        mcos.writeByte(0);
// x3 y3 (right corner)
        mcos.writeShort(200);
        mcos.writeShort(0);
// r3 g3 b3 (blue)
        mcos.writeByte(0);
        mcos.writeByte(0);
        mcos.writeByte(0xFF);

        mcos.close();
// outside stream MUST be closed as well, see javadoc of MemoryCacheImageOutputStream
        os.close();
        return gouraudShading;
    }

    private static PDShadingType3 createShading3() throws IOException {
// type 2 (exponential) function with attributes
        COSDictionary fdict = new COSDictionary();
        fdict.setInt(COSName.FUNCTION_TYPE, 2);
        COSArray domain = new COSArray();
        domain.add(COSInteger.get(0));
        domain.add(COSInteger.get(1));
        COSArray c0 = new COSArray();
        c0.add(COSFloat.get("1"));
        c0.add(COSFloat.get("1"));
        c0.add(COSFloat.get("1"));
        COSArray c1 = new COSArray();
        c1.add(COSFloat.get("0"));
        c1.add(COSFloat.get("0"));
        c1.add(COSFloat.get("0"));
        fdict.setItem(COSName.DOMAIN, domain);
        fdict.setItem(COSName.C0, c0);
        fdict.setItem(COSName.C1, c1);
        fdict.setInt(COSName.N, 1);
        PDFunctionType2 func = new PDFunctionType2(fdict);

// radial shading with attributes
        PDShadingType3 radialShading = new PDShadingType3(new COSDictionary());
        radialShading.setColorSpace(PDDeviceRGB.INSTANCE);
        radialShading.setShadingType(PDShading.SHADING_TYPE3);
        COSArray coords2 = new COSArray();
        coords2.add(COSInteger.get(100));
        coords2.add(COSInteger.get(400));
        coords2.add(COSInteger.get(50)); // radius1
        coords2.add(COSInteger.get(100));
        coords2.add(COSInteger.get(400));
        coords2.add(COSInteger.get(0)); // radius2
        radialShading.setCoords(coords2);
        radialShading.setFunction(func);

        return radialShading;
    }

    private static PDShadingType2 createShading2() throws IOException {
        int startColor = Color.RED;
        int endColor = Color.GREEN;

        COSDictionary fdict = new COSDictionary();

        fdict.setInt(COSName.FUNCTION_TYPE, 2);

        COSArray domain = new COSArray();
        domain.add(COSInteger.get(0));
        domain.add(COSInteger.get(1));

        COSArray c0 = new COSArray();
        c0.add(new COSFloat(Color.red(startColor) / 255f));
        c0.add(new COSFloat(Color.green(startColor) / 255f));
        c0.add(new COSFloat(Color.blue(startColor) / 255f));

        COSArray c1 = new COSArray();
        c1.add(new COSFloat(Color.red(endColor) / 255f));
        c1.add(new COSFloat(Color.green(endColor) / 255f));
        c1.add(new COSFloat(Color.blue(endColor) / 255f));

        fdict.setItem(COSName.DOMAIN, domain);
        fdict.setItem(COSName.C0, c0);
        fdict.setItem(COSName.C1, c1);
        fdict.setInt(COSName.N, 1);

        PDFunctionType2 func = new PDFunctionType2(fdict);

        PDShadingType2 axialShading = new PDShadingType2(new COSDictionary());

        axialShading.setColorSpace(PDDeviceRGB.INSTANCE);
        axialShading.setShadingType(PDShading.SHADING_TYPE2);

        COSArray coords1 = new COSArray();
        coords1.add(new COSFloat(10));
        coords1.add(new COSFloat(10));
        coords1.add(new COSFloat(100));
        coords1.add(new COSFloat(100));

        axialShading.setCoords(coords1);
        axialShading.setFunction(func);

        return axialShading;
    }
}
