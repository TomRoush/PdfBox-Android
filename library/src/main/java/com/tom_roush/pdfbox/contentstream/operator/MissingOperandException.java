package com.tom_roush.pdfbox.contentstream.operator;

import java.io.IOException;
import java.util.List;

import com.tom_roush.pdfbox.cos.COSBase;

/**
 * Throw when a PDF operator is missing required operands.
 */
public final class MissingOperandException extends IOException
{
    public MissingOperandException(Operator operator, List<COSBase> operands)
    {
        super("Operator " + operator.getName() + " has too few operands: " + operands);
    }
}