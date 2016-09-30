package org.apache.pdfbox.pdmodel.graphics.predictor;

/**
 * The none algorithm.
 *
 * <code>None(i,j) = Raw(i,j)</code>
 *
 * <code>Raw(i,j) = None(i,j)</code>
 *
 * @author xylifyx@yahoo.co.uk
 * @version $Revision: 1.3 $
 */
public class None extends PredictorAlgorithm
{
    /**
     * encode a byte array full of image data using the filter that this object
     * implements.
     *
     * @param src
     *            buffer
     * @param dest
     *            buffer
     */
    public void encode(byte[] src, byte[] dest)
    {
        checkBufsiz(dest, src);
        System.arraycopy(src,0,dest,0,src.length);
    }

    /**
     * decode a byte array full of image data using the filter that this object
     * implements.
     *
     * @param src
     *            buffer
     * @param dest
     *            buffer
     */
    public void decode(byte[] src, byte[] dest)
    {
        System.arraycopy(src,0,dest,0,src.length);
    }



    /**
     * {@inheritDoc}
     */
    public void encodeLine(byte[] src, byte[] dest, int srcDy, int srcOffset,
            int destDy, int destOffset)
    {
        int bpl = getWidth() * getBpp();
        for (int x = 0; x < bpl; x++)
        {
            dest[destOffset + x] = src[srcOffset + x];
        }
    }

    /**
     * {@inheritDoc}
     */
    public void decodeLine(byte[] src, byte[] dest, int srcDy, int srcOffset,
            int destDy, int destOffset)
    {
        int bpl = getWidth() * getBpp();
        for (int x = 0; x < bpl; x++)
        {
            dest[destOffset + x] = src[srcOffset + x];
        }
    }

}
