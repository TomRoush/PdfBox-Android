package org.apache.pdfboxandroid.io;

import java.io.IOException;

public interface RandomAccessRead extends SequentialRead {
	/**
     * Seek to a position in the data.
     *
     * @param position The position to seek to.
     * @throws IOException If there is an error while seeking.
     */
    public void seek(long position) throws IOException;
}
