package com.tom_roush.pdfbox.contentstream.operator.graphics;

import android.util.Log;

import com.tom_roush.pdfbox.contentstream.operator.Operator;
import com.tom_roush.pdfbox.cos.COSBase;

import java.io.IOException;
import java.util.List;

/**
 * h Close the path.
 *
 * @author Ben Litchfield
 */
public final class ClosePath extends GraphicsOperatorProcessor
{
    @Override
    public void process(Operator operator, List<COSBase> operands) throws IOException
    {
        if (context.getCurrentPoint() == null)
        {
            Log.w("PdfBox-Android", "ClosePath wihtout initial MoveTo");
            return;
        }
        context.closePath();
    }

    @Override
    public String getName()
    {
        return "h";
    }
}
