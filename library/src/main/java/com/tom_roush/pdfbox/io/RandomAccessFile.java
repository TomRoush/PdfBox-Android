package org.apache.pdfbox.io;

import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * An interface to allow temp PDF data to be stored in a scratch
 * file on the disk to reduce memory consumption.
 *
 * @author Ben Litchfield
 */
public class RandomAccessFile implements RandomAccess, Closeable
{
    private final java.io.RandomAccessFile ras;
    private boolean isClosed;

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
    @Override
    public void close() throws IOException
    {
        ras.close();
        isClosed = true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void seek(long position) throws IOException
    {
        ras.seek(position);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getPosition() throws IOException
    {
        return ras.getFilePointer();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public int read() throws IOException
    {
        return ras.read();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int read(byte[] b, int offset, int length) throws IOException
    {
        return ras.read(b, offset, length);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long length() throws IOException
    {
        return ras.length();
    }

    @Override
    public boolean isClosed()
    {
    	return isClosed;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void write(byte[] b, int offset, int length) throws IOException
    {
        ras.write(b, offset, length);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void write(int b) throws IOException
    {
        ras.write(b);
    }
}
