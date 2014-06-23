package org.apache.pdfboxandroid.io;

import java.io.IOException;
import java.util.ArrayList;

/**
 * An implementation of the RandomAccess interface to store a pdf in memory.
 * The data will be stored in 16kb chunks organized in an ArrayList.  
 *
 */
public class RandomAccessBuffer implements RandomAccess {
	// chunk size is 16kb
    private static final int BUFFER_SIZE = 16384;
    // list containing all chunks
    private ArrayList<byte[]> bufferList = null;
    // current chunk
    private byte[] currentBuffer;
    // current pointer to the whole buffer
    private long pointer;
    // current pointer for the current chunk
    private long currentBufferPointer;
    // size of the whole buffer
    private long size;
    // current chunk list index
    private int bufferListIndex;
    // maximum chunk list index
    private int bufferListMaxIndex;

    /**
     * Default constructor.
     */
    public RandomAccessBuffer()
    {
        // starting with one chunk
        bufferList = new ArrayList<byte[]>();
        currentBuffer = new byte[BUFFER_SIZE];
        bufferList.add(currentBuffer);
        pointer = 0;
        currentBufferPointer = 0;
        size = 0;
        bufferListIndex = 0;
        bufferListMaxIndex = 0;
    }
    
    /**
     * {@inheritDoc}
     */
    public void write(int b) throws IOException
    {
        // end of buffer reached?
        if (currentBufferPointer >= BUFFER_SIZE) 
        {
            if (pointer + BUFFER_SIZE >= Integer.MAX_VALUE) 
            {
                throw new IOException("RandomAccessBuffer overflow");
            }
            expandBuffer();
        }
        currentBuffer[(int)currentBufferPointer++] = (byte)b;
        pointer++;
        if (pointer > this.size)
        {
            this.size = pointer;
        }
        // end of buffer reached now?
        if (currentBufferPointer >= BUFFER_SIZE) 
        {
            if (pointer + BUFFER_SIZE >= Integer.MAX_VALUE) 
            {
                throw new IOException("RandomAccessBuffer overflow");
            }
            expandBuffer();
        }
    }
    
    /**
     * create a new buffer chunk and adjust all pointers and indices.
     */
    private void expandBuffer() 
    {
        if (bufferListMaxIndex > bufferListIndex)
        {
            // there is already an existing chunk
            nextBuffer();
        }
        else
        {
            // create a new chunk and add it to the buffer
            currentBuffer = new byte[BUFFER_SIZE];
            bufferList.add(currentBuffer);
            currentBufferPointer = 0;
            bufferListMaxIndex++;
            bufferListIndex++;
        }
    }
    
    /**
     * switch to the next buffer chunk and reset the buffer pointer.
     */
    private void nextBuffer() 
    {
        currentBufferPointer = 0;
        currentBuffer = bufferList.get(++bufferListIndex);
    }
    
    /**
     * {@inheritDoc}
     */
    public void write(byte[] b, int offset, int length) throws IOException
    {
        long newSize = pointer + length;
        long remainingBytes = BUFFER_SIZE - currentBufferPointer;
        if (length >= remainingBytes)
        {
            if (newSize > Integer.MAX_VALUE) 
            {
                throw new IOException("RandomAccessBuffer overflow");
            }
            // copy the first bytes to the current buffer
            System.arraycopy(b, offset, currentBuffer, (int)currentBufferPointer, (int)remainingBytes);
            int newOffset = offset + (int)remainingBytes;
            long remainingBytes2Write = length - remainingBytes;
            // determine how many buffers are needed for the remaining bytes
            int numberOfNewArrays = (int)remainingBytes2Write / BUFFER_SIZE;
            for (int i=0;i<numberOfNewArrays;i++) 
            {
                expandBuffer();
                System.arraycopy(b, newOffset, currentBuffer, (int)currentBufferPointer, BUFFER_SIZE);
                newOffset += BUFFER_SIZE;
            }
            // are there still some bytes to be written?
            remainingBytes2Write -= numberOfNewArrays * BUFFER_SIZE;
            if (remainingBytes2Write >= 0)
            {
                expandBuffer();
                if (remainingBytes2Write > 0)
                {
                    System.arraycopy(b, newOffset, currentBuffer, (int)currentBufferPointer, (int)remainingBytes2Write);
                }
                currentBufferPointer = remainingBytes2Write;
            }
        }
        else
        {
            System.arraycopy(b, offset, currentBuffer, (int)currentBufferPointer, length);
            currentBufferPointer += length;
        }
        pointer += length;
        if (pointer > this.size)
        {
            this.size = pointer;
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public long length() throws IOException
    {
        return size;
    }

    /**
     * {@inheritDoc}
     */
    public void seek(long position) throws IOException
    {
        pointer = position;
        // calculate the chunk list index
        bufferListIndex = (int)(position / BUFFER_SIZE);
        currentBufferPointer = position % BUFFER_SIZE;
        currentBuffer = bufferList.get(bufferListIndex);
    }

    /**
     * {@inheritDoc}
     */
    public void close() throws IOException
    {
        currentBuffer = null;
        bufferList.clear();
        pointer = 0;
        currentBufferPointer = 0;
        size = 0;
        bufferListIndex = 0;
    }

    /**
     * {@inheritDoc}
     */
    public int read() throws IOException
    {
        if (pointer >= this.size)
        {
            return -1;
        }
        if (currentBufferPointer >= BUFFER_SIZE)
        {
            if (bufferListIndex >= bufferListMaxIndex)
            {
                return -1;
            }
            else
            {
                currentBuffer = bufferList.get(++bufferListIndex);
                currentBufferPointer = 0;
            }
        }
        pointer++;
        return currentBuffer[(int)currentBufferPointer++] & 0xff;
    }
}
