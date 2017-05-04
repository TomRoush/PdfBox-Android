package com.tom_roush.pdfbox.contentstream;

import com.tom_roush.pdfbox.pdmodel.PDResources;
import com.tom_roush.pdfbox.pdmodel.common.PDRectangle;
import com.tom_roush.pdfbox.util.Matrix;

import java.io.IOException;
import java.io.InputStream;

/**
 * A content stream.
 *
 * @author John Hewson
 */
public interface PDContentStream
{
    /**
     * Returns this stream's content, if any.
     *
     * @return An InputStream or null.
     * @throws IOException If the stream could not be read
     */
    InputStream getContents() throws IOException;

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