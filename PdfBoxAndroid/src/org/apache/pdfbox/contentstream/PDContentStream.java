package org.apache.pdfbox.contentstream;

import org.apache.pdfbox.cos.COSStream;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.common.PDRectangle;

/**
 * A content stream.
 *
 * @author John Hewson
 */
public interface PDContentStream
{
    /**
     * Returns the underlying COS stream.
     */
    public COSStream getContentStream();

    /**
     * Returns this stream's resources, if any.
     */
    public PDResources getResources();

    /**
     * Returns the bounding box of the contents, if any.
     */
    public PDRectangle getBBox();

    // todo: Matrix
}