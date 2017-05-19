/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.tom_roush.harmony.javax.imageio.stream;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.nio.ByteOrder;

public abstract class ImageInputStreamImpl implements ImageInputStream
{
    protected ByteOrder byteOrder = ByteOrder.BIG_ENDIAN;

    protected long streamPos = 0;
    protected long flushedPos = 0;
    protected int bitOffset = 0;

    private boolean closed = false;

    private final PositionStack posStack = new PositionStack();
    private final PositionStack offsetStack = new PositionStack();
    private final byte[] buff = new byte[8];

    public ImageInputStreamImpl()
    {
    }

    protected final void checkClosed() throws IOException
    {
        if (closed)
        {
            throw new IOException("stream is closed");
        }
    }

    public void setByteOrder(ByteOrder byteOrder)
    {
        this.byteOrder = byteOrder;
    }

    public ByteOrder getByteOrder()
    {
        return byteOrder;
    }

    public abstract int read() throws IOException;

    public int read(byte[] b) throws IOException
    {
        return read(b, 0, b.length);
    }

    public abstract int read(byte[] b, int off, int len) throws IOException;

    public void readBytes(IIOByteBuffer buf, int len) throws IOException
    {
        if (buf == null)
        {
            throw new NullPointerException("buffer is NULL");
        }

        byte[] b = new byte[len];
        len = read(b, 0, b.length);

        buf.setData(b);
        buf.setOffset(0);
        buf.setLength(len);
    }

    public boolean readBoolean() throws IOException
    {
        int b = read();
        if (b < 0)
        {
            throw new EOFException("EOF reached");
        }
        return b != 0;
    }

    public byte readByte() throws IOException
    {
        int b = read();
        if (b < 0)
        {
            throw new EOFException("EOF reached");
        }
        return (byte) b;
    }

    public int readUnsignedByte() throws IOException
    {
        int b = read();
        if (b < 0)
        {
            throw new EOFException("EOF reached");
        }
        return b;
    }

    public short readShort() throws IOException
    {
        if (read(buff, 0, 2) < 0)
        {
            throw new EOFException();
        }

        return byteOrder == ByteOrder.BIG_ENDIAN ?
            (short) ((buff[0] << 8) | (buff[1] & 0xff)) :
            (short) ((buff[1] << 8) | (buff[0] & 0xff));
    }

    public int readUnsignedShort() throws IOException
    {
        return ((int) readShort()) & 0xffff;
    }

    public char readChar() throws IOException
    {
        return (char) readShort();
    }

    public int readInt() throws IOException
    {
        if (read(buff, 0, 4) < 0)
        {
            throw new EOFException();
        }

        return byteOrder == ByteOrder.BIG_ENDIAN ? ((buff[0] & 0xff) << 24)
            | ((buff[1] & 0xff) << 16) | ((buff[2] & 0xff) << 8)
            | (buff[3] & 0xff) : ((buff[3] & 0xff) << 24)
            | ((buff[2] & 0xff) << 16) | ((buff[1] & 0xff) << 8)
            | (buff[0] & 0xff);
    }

    public long readUnsignedInt() throws IOException
    {
        return ((long) readInt()) & 0xffffffffL;
    }

    public long readLong() throws IOException
    {
        if (read(buff, 0, 8) < 0)
        {
            throw new EOFException();
        }

        if (byteOrder == ByteOrder.BIG_ENDIAN)
        {
            int i1 = ((buff[0] & 0xff) << 24) | ((buff[1] & 0xff) << 16)
                | ((buff[2] & 0xff) << 8) | (buff[3] & 0xff);
            int i2 = ((buff[4] & 0xff) << 24) | ((buff[5] & 0xff) << 16)
                | ((buff[6] & 0xff) << 8) | (buff[7] & 0xff);

            return ((i1 & 0xffffffffL) << 32) | (i2 & 0xffffffffL);
        }
        else
        {
            int i1 = ((buff[3] & 0xff) << 24) | ((buff[2] & 0xff) << 16)
                | ((buff[1] & 0xff) << 8) | (buff[0] & 0xff);
            int i2 = ((buff[7] & 0xff) << 24) | ((buff[6] & 0xff) << 16)
                | ((buff[5] & 0xff) << 8) | (buff[4] & 0xff);

            return ((i2 & 0xffffffffL) << 32) | (i1 & 0xffffffffL);
        }
    }

    public float readFloat() throws IOException
    {
        return Float.intBitsToFloat(readInt());
    }

    public double readDouble() throws IOException
    {
        return Double.longBitsToDouble(readLong());
    }

    public String readLine() throws IOException
    {
        final StringBuilder line = new StringBuilder(80);
        boolean isEmpty = true;
        int c = -1;

        while ((c = read()) != -1)
        {
            isEmpty = false;
            if (c == '\n')
            {
                break;
            }
            else if (c == '\r')
            {
                c = read();
                if ((c != '\n') && (c != -1))
                {
                    seek(getStreamPosition() - 1);
                }
                break;
            }
            line.append((char) c);
        }

        return isEmpty ? null : line.toString();
    }

    public String readUTF() throws IOException
    {
        ByteOrder byteOrder = getByteOrder();
        setByteOrder(ByteOrder.BIG_ENDIAN);
        final int size = readUnsignedShort();
        final byte[] buf = new byte[size];
        final char[] out = new char[size];

        readFully(buf, 0, size);
        setByteOrder(byteOrder);
        return new DataInputStream(new ByteArrayInputStream(buff)).readUTF();
//        return Util.convertUTF8WithBuf(buf, out, 0, size);
    }

    public void readFully(byte[] b, int off, int len) throws IOException
    {
        if ((off < 0) || (len < 0) || (off + len > b.length))
        {
            throw new IndexOutOfBoundsException();
        }

        while (len > 0)
        {
            int i = read(b, off, len);

            if (i == -1)
            {
                throw new EOFException();
            }

            off += i;
            len -= i;
        }
    }

    public void readFully(byte[] b) throws IOException
    {
        readFully(b, 0, b.length);
    }

    public void readFully(short[] s, int off, int len) throws IOException
    {
        if ((off < 0) || (len < 0) || (off + len > s.length))
        {
            throw new IndexOutOfBoundsException();
        }

        for (int i = 0; i < len; i++)
        {
            s[off + i] = readShort();
        }
    }

    public void readFully(char[] c, int off, int len) throws IOException
    {
        if ((off < 0) || (len < 0) || (off + len > c.length))
        {
            throw new IndexOutOfBoundsException();
        }

        for (int i = 0; i < len; i++)
        {
            c[off + i] = readChar();
        }
    }

    public void readFully(int[] i, int off, int len) throws IOException
    {
        if ((off < 0) || (len < 0) || (off + len > i.length))
        {
            throw new IndexOutOfBoundsException();
        }

        for (int k = 0; k < len; k++)
        {
            i[off + k] = readInt();
        }
    }

    public void readFully(long[] l, int off, int len) throws IOException
    {
        if ((off < 0) || (len < 0) || (off + len > l.length))
        {
            throw new IndexOutOfBoundsException();
        }

        for (int i = 0; i < len; i++)
        {
            l[off + i] = readLong();
        }
    }

    public void readFully(float[] f, int off, int len) throws IOException
    {
        if ((off < 0) || (len < 0) || (off + len > f.length))
        {
            throw new IndexOutOfBoundsException();
        }

        for (int i = 0; i < len; i++)
        {
            f[off + i] = readFloat();
        }
    }

    public void readFully(double[] d, int off, int len) throws IOException
    {
        if ((off < 0) || (len < 0) || (off + len > d.length))
        {
            throw new IndexOutOfBoundsException();
        }

        for (int i = 0; i < len; i++)
        {
            d[off + i] = readFloat();
        }
    }

    public long getStreamPosition() throws IOException
    {
        checkClosed();
        return streamPos;
    }

    public int getBitOffset() throws IOException
    {
        checkClosed();
        return bitOffset;
    }

    public void setBitOffset(int bitOffset) throws IOException
    {
        checkClosed();
        if ((bitOffset < 0) || (bitOffset > 7))
        {
            throw new IllegalArgumentException();
        }
        this.bitOffset = bitOffset;
    }

    int currentByte;

    public int readBit() throws IOException
    {
        checkClosed();

        int offset = bitOffset;
        int currentByte = read();

        if (currentByte == -1)
        {
            throw new EOFException();
        }

        offset = (offset + 1) & 7;

        if (offset != 0)
        {
            currentByte >>= 8 - offset;
            seek(getStreamPosition() - 1);
        }

        bitOffset = offset;
        return currentByte & 1;
    }

    public long readBits(int numBits) throws IOException
    {
        checkClosed();

        if ((numBits < 0) || (numBits > 64))
        {
            throw new IllegalArgumentException();
        }

        long res = 0;

        for (int i = 0; i < numBits; i++)
        {
            res <<= 1;
            res |= readBit();
        }

        return res;
    }

    public long length()
    {
        return -1L;
    }

    public int skipBytes(int n) throws IOException
    {
        return (int) skipBytes((long) n);
    }

    public long skipBytes(long n) throws IOException
    {
        seek(getStreamPosition() + n);
        return n;
    }

    public void seek(long pos) throws IOException
    {
        checkClosed();
        if (pos < getFlushedPosition())
        {
            throw new IllegalArgumentException("trying to seek before flushed pos");
        }
        bitOffset = 0;
        streamPos = pos;
    }

    public void mark()
    {
        try
        {
            posStack.push(getStreamPosition());
            offsetStack.push(getBitOffset());
        }
        catch (IOException e)
        {
            e.printStackTrace();
            throw new RuntimeException("Stream marking error");
        }
    }

    public void reset() throws IOException
    {
        if (!posStack.isEmpty() && !offsetStack.isEmpty())
        {
            long p = posStack.pop();
            if (p < flushedPos)
            {
                throw new IOException("marked position lies in the flushed portion of the stream");
            }
            seek(p);
            setBitOffset((int) offsetStack.pop());
        }
    }

    public void flushBefore(long pos) throws IOException
    {
        long sPos = getStreamPosition();
        if (pos > sPos)
        {
            throw new IndexOutOfBoundsException("Trying to flush outside of current position");
        }
        if (pos < flushedPos)
        {
            throw new IndexOutOfBoundsException("Trying to flush within already flushed portion");
        }
        flushedPos = pos;
        // -- TODO implement
    }

    public void flush() throws IOException
    {
        flushBefore(getStreamPosition());
    }

    public long getFlushedPosition()
    {
        return flushedPos;
    }

    public boolean isCached()
    {
        return false; // def
    }

    public boolean isCachedMemory()
    {
        return false; // def
    }

    public boolean isCachedFile()
    {
        return false; // def
    }

    public void close() throws IOException
    {
        checkClosed();
        closed = true;

    }

    @Override
    protected void finalize() throws Throwable
    {
        if (!closed)
        {
            try
            {
                close();
            }
            finally
            {
                super.finalize();
            }
        }
    }

    private static class PositionStack
    {
        private static final int SIZE = 10;

        private long[] values = new long[SIZE];
        private int pos = 0;

        void push(long v)
        {
            if (pos >= values.length)
            {
                ensure(pos + 1);
            }
            values[pos++] = v;
        }

        long pop()
        {
            return values[--pos];
        }

        boolean isEmpty()
        {
            return pos == 0;
        }

        private void ensure(int size)
        {
            long[] arr = new long[Math.max(2 * values.length, size)];
            System.arraycopy(values, 0, arr, 0, values.length);
            values = arr;
        }
    }
}
