package org.apache.pdfboxandroid.io;

import java.io.IOException;

public interface RandomAccess extends RandomAccessRead {
	/**
     * Write a byte to the stream.
     *
     * @param b The byte to write.
     * @throws IOException If there is an IO error while writing.
     */
    public void write(int b) throws IOException;
	
	/**
     * Write a buffer of data to the stream.
     *
     * @param b The buffer to get the data from.
     * @param offset An offset into the buffer to get the data from.
     * @param length The length of data to write.
     * @throws IOException If there is an error while writing the data.
     */
    public void write(byte[] b, int offset, int length) throws IOException;
}