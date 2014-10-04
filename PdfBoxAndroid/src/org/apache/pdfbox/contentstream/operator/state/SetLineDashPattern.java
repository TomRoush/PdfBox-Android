package org.apache.pdfbox.contentstream.operator.state;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.contentstream.operator.Operator;
import org.apache.pdfbox.contentstream.operator.OperatorProcessor;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSNumber;
import org.apache.pdfbox.pdmodel.graphics.PDLineDashPattern;

/**
 * d: Set the line dash pattern.
 *
 * @author Ben Litchfield
 */
public class SetLineDashPattern extends OperatorProcessor
{
    /**
     * log instance
     */
    private static final Log LOG = LogFactory.getLog(SetLineDashPattern.class);

    @Override
    public void process(Operator operator, List<COSBase> arguments)
    {
        COSArray dashArray = (COSArray) arguments.get(0);
        int dashPhase = ((COSNumber) arguments.get(1)).intValue();
        if (dashPhase < 0)
        {
            LOG.warn("dash phaseStart has negative value " + dashPhase + ", set to 0");
            dashPhase = 0;
        }
        PDLineDashPattern lineDash = new PDLineDashPattern(dashArray, dashPhase);
        context.getGraphicsState().setLineDashPattern(lineDash);
    }

    @Override
    public String getName()
    {
        return "d";
    }
}
