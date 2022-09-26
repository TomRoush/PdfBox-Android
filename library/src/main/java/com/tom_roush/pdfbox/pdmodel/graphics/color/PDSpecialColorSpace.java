package com.tom_roush.pdfbox.pdmodel.graphics.color;

import com.tom_roush.pdfbox.cos.COSBase;

public abstract class PDSpecialColorSpace extends PDColorSpace
{
    @Override
    public COSBase getCOSObject()
    {
        return array;
    }
}
