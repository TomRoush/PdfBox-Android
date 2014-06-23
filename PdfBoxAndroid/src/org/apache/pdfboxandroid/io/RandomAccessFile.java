package org.apache.pdfboxandroid.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class RandomAccessFile implements RandomAccess {
	private java.io.RandomAccessFile ras;

    /**
     * Constructor.
     *
     * @param file The file to write the data to.
     * @param mode The writing mode.
     * @throws FileNotFoundException If the file cannot be created.
     */
    public RandomAccessFile(File file, String mode) throws FileNotFoundException
    {
        ras = new java.io.RandomAccessFile(file, mode);
    }

    /**
     * {@inheritDoc}
     */
    public void close() throws IOException
    {
        ras.close();
    }

    /**
     * {@inheritDoc}
     */
    public void seek(long position) throws IOException
    {
        ras.seek(position);
    }

//    /**
//     * {@inheritDoc}
//     */
//    public long getPosition() throws IOException {
//        return ras.getFilePointer();
//    }
    
    /**
     * {@inheritDoc}
     */
    public int read() throws IOException
    {
        return ras.read();
    }

//    /**
//     * {@inheritDoc}
//     */
//    public int read(byte[] b, int offset, int length) throws IOException
//    {
//        return ras.read(b, offset, length);
//    }

    /**
     * {@inheritDoc}
     */
    public long length() throws IOException
    {
        return ras.length();
    }

    /**
     * {@inheritDoc}
     */
    public void write(byte[] b, int offset, int length) throws IOException
    {
        ras.write(b, offset, length);
    }

    /**
     * {@inheritDoc}
     */
    public void write(int b) throws IOException
    {
        ras.write(b);
    }
}
