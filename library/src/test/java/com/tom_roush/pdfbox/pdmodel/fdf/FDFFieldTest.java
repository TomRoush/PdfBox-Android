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

package com.tom_roush.pdfbox.pdmodel.fdf;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import com.tom_roush.pdfbox.cos.COSArray;
import com.tom_roush.pdfbox.cos.COSBase;
import com.tom_roush.pdfbox.cos.COSName;
import com.tom_roush.pdfbox.cos.COSStream;
import com.tom_roush.pdfbox.cos.COSString;
import com.tom_roush.pdfbox.pdmodel.common.COSArrayList;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/*
 * Test some characteristics of FDFFields
 */
public class FDFFieldTest
{
    @Test
    public void testCOSStringValue() throws IOException
    {
        String testString = "Test value";
        COSString testCOSString = new COSString(testString);

        FDFField field = new FDFField();
        field.setValue(testCOSString);

        assertEquals(testCOSString, (COSString) field.getCOSValue());
        assertEquals(testString, field.getValue());
    }


    @Test
    public void testTextAsCOSStreamValue() throws IOException
    {
        String testString = "Test value";
        byte[] testBytes = testString.getBytes("ASCII");
        COSStream stream = createStream(testBytes, null);

        FDFField field = new FDFField();
        field.setValue(stream);

        assertEquals(testString, field.getValue());
    }

    @Test
    public void testCOSNameValue() throws IOException
    {
        String testString = "Yes";
        COSName testCOSSName = COSName.getPDFName(testString);

        FDFField field = new FDFField();
        field.setValue(testCOSSName);

        assertEquals(testCOSSName, (COSName) field.getCOSValue());
        assertEquals(testString, field.getValue());
    }

    @Test
    public void testCOSArrayValue() throws IOException
    {
        List<String> testList = new ArrayList<String>();
        testList.add("A");
        testList.add("B");

        COSArray testCOSArray = COSArrayList.convertStringListToCOSStringCOSArray(testList);

        FDFField field = new FDFField();
        field.setValue(testCOSArray);

        assertEquals(testCOSArray, (COSArray) field.getCOSValue());
        assertEquals(testList, field.getValue());
    }


    private COSStream createStream(byte[] testString, COSBase filters) throws IOException
    {
        COSStream stream = new COSStream();
        OutputStream output = stream.createOutputStream(filters);
        output.write(testString);
        output.close();
        return stream;
    }
}
