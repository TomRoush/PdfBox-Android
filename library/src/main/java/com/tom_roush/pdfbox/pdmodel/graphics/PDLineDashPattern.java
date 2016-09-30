package com.tom_roush.pdfbox.pdmodel.graphics;

import com.tom_roush.pdfbox.cos.COSArray;
import com.tom_roush.pdfbox.cos.COSBase;
import com.tom_roush.pdfbox.cos.COSInteger;

import com.tom_roush.pdfbox.pdmodel.common.COSArrayList;
import com.tom_roush.pdfbox.pdmodel.common.COSObjectable;

import java.util.Arrays;

/**
 * A line dash pattern for stroking paths.
 * Instances of PDLineDashPattern are immutable.
 * @author Ben Litchfield
 * @author John Hewson
 */
public final class PDLineDashPattern implements COSObjectable
{
    private final int phase;
    private final float[] array;

    /**
     * Creates a new line dash pattern, with no dashes and a phase of 0.
     */
    public PDLineDashPattern()
    {
        array = new float[] { };
        phase = 0;
    }

    /**
     * Creates a new line dash pattern from a dash array and phase.
     * @param array the dash array
     * @param phase the phase
     */
    public PDLineDashPattern(COSArray array, int phase)
    {
        this.array = array.toFloatArray();
        this.phase = phase;
    }

    @Override
    public COSBase getCOSObject()
    {
        COSArray cos = new COSArray();
        cos.add(COSArrayList.converterToCOSArray(Arrays.asList(array)));
        cos.add(COSInteger.get(phase));
        return cos;
    }

    /**
     * Returns the dash phase.
     * This specifies the distance into the dash pattern at which to start the dash.
     * @return the dash phase
     */
    public int getPhase()
    {
        return phase;
    }

    /**
     * Returns the dash array.
     * @return the dash array
     */
    public float[] getDashArray()
    {
        return array.clone();
    }
}
