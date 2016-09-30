package org.apache.pdfbox.pdmodel.graphics.predictor;

/**
 * We can use raw on the right hand side of
 * the decoding formula because it is already decoded.
 *
 * <code>average(i,j) = raw(i,j) + (raw(i-1,j)+raw(i,j-1)/2</code>
 *
 * decoding
 *
 * <code>raw(i,j) = avarage(i,j) - (raw(i-1,j)+raw(i,j-1)/2</code>
 *
 * @author xylifyx@yahoo.co.uk
 * @version $Revision: 1.3 $
 */
public class Average extends PredictorAlgorithm
{
    /**
     * Not an optimal version, but close to the def.
     *
     * {@inheritDoc}
     */
    public void encodeLine(byte[] src, byte[] dest, int srcDy, int srcOffset,
            int destDy, int destOffset)
    {
        int bpl = getWidth() * getBpp();
        for (int x = 0; x < bpl; x++)
        {
            dest[x + destOffset] = (byte) (src[x + srcOffset] - ((leftPixel(
                    src, srcOffset, srcDy, x) + abovePixel(src, srcOffset,
                    srcDy, x)) >>> 2));
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
            dest[x + destOffset] = (byte) (src[x + srcOffset] + ((leftPixel(
                    dest, destOffset, destDy, x) + abovePixel(dest,
                    destOffset, destDy, x)) >>> 2));
        }
    }
}
