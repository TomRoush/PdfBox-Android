package org.apache.pdfbox.pdmodel.graphics.predictor;

/**
 * The up algorithm.
 *
 * <code>Up(i,j) = Raw(i,j) - Raw(i,j-1)</code>
 *
 * <code>Raw(i,j) = Up(i,j) + Raw(i,j-1)</code>
 *
 * @author xylifyx@yahoo.co.uk
 * @version $Revision: 1.3 $
 */
public class Up extends PredictorAlgorithm
{
    /**
     * {@inheritDoc}
     */
    public void encodeLine(byte[] src, byte[] dest, int srcDy, int srcOffset,
            int destDy, int destOffset)
    {
        int bpl = getWidth()*getBpp();
        // case: y = 0;
        if (srcOffset - srcDy < 0)
        {
            if (0 < getHeight())
            {
                for (int x = 0; x < bpl; x++)
                {
                    dest[destOffset + x] = src[srcOffset + x];
                }
            }
        }
        else
        {
            for (int x = 0; x < bpl; x++)
            {
                dest[destOffset + x] = (byte) (src[srcOffset + x] - src[srcOffset
                        + x - srcDy]);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void decodeLine(byte[] src, byte[] dest, int srcDy, int srcOffset,
            int destDy, int destOffset)
    {
        // case: y = 0;
        int bpl = getWidth()*getBpp();
        if (destOffset - destDy < 0)
        {
            if (0 < getHeight())
            {
                for (int x = 0; x < bpl; x++)
                {
                    dest[destOffset + x] = src[srcOffset + x];
                }
            }
        }
        else
        {
            for (int x = 0; x < bpl; x++)
            {
                dest[destOffset + x] = (byte) (src[srcOffset + x] + dest[destOffset
                        + x - destDy]);
            }
        }
    }
}
