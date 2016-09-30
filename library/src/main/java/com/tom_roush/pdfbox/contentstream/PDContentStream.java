package com.tom_roush.pdfbox.contentstream;

import com.tom_roush.pdfbox.cos.COSStream;
import com.tom_roush.pdfbox.pdmodel.PDResources;
import com.tom_roush.pdfbox.pdmodel.common.PDRectangle;
import com.tom_roush.pdfbox.util.Matrix;

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
    COSStream getContentStream();

    /**
     * Returns this stream's resources
     */
    PDResources getResources();

    /**
     * Returns the bounding box of the contents, if any.
     */
    PDRectangle getBBox();

    /**
     * Returns the matrix which transforms from the stream's space to user space.
     */
    Matrix getMatrix();
}