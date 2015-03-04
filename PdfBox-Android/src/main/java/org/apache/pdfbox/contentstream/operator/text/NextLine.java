package org.apache.pdfbox.contentstream.operator.text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.pdfbox.contentstream.operator.Operator;
import org.apache.pdfbox.contentstream.operator.OperatorProcessor;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSFloat;

/**
 * T*: Move to start of next text line.
 *
 * @author Laurent Huault
 */
public class NextLine extends OperatorProcessor
{
    @Override
    public void process(Operator operator, List<COSBase> arguments) throws IOException
    {
        //move to start of next text line
        ArrayList<COSBase> args = new ArrayList<COSBase>();
        args.add(new COSFloat(0f));
        // this must be -leading instead of just leading as written in the
        // specification (p.369) the acrobat reader seems to implement it the same way
        args.add(new COSFloat(-1 * context.getGraphicsState().getTextState().getLeading()));
        // use Td instead of repeating code
        context.processOperator("Td", args);
    }

    @Override
    public String getName()
    {
        return "T*";
    }
}
