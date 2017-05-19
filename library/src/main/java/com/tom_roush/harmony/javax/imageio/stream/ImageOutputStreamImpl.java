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
/**
 * @author Rustem V. Rafikov
 */
package com.tom_roush.harmony.javax.imageio.stream;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteOrder;

public abstract class ImageOutputStreamImpl extends ImageInputStreamImpl
    implements ImageOutputStream
{
    private final byte[] buff = new byte[8];

    public ImageOutputStreamImpl()
    {
    }

    public abstract void write(int b) throws IOException;

    public void write(byte[] b) throws IOException
    {
        write(b, 0, b.length);
    }

    public abstract void write(byte[] b, int off, int len) throws IOException;

    public void writeBoolean(boolean v) throws IOException
    {
        write(v ? 1 : 0);
    }

    public void writeByte(int v) throws IOException
    {
        write(v);
    }

    public void writeShort(int v) throws IOException
    {
        if (byteOrder == ByteOrder.BIG_ENDIAN)
        {
            buff[0] = (byte) (v >> 8);
            buff[1] = (byte) v;
        }
        else
        {
            buff[1] = (byte) (v >> 8);
            buff[0] = (byte) v;
        }

        write(buff, 0, 2);
    }

    public void writeChar(int v) throws IOException
    {
        writeShort(v);
    }

    public void writeInt(int v) throws IOException
    {
        if (byteOrder == ByteOrder.BIG_ENDIAN)
        {
            buff[0] = (byte) (v >> 24);
            buff[1] = (byte) (v >> 16);
            buff[2] = (byte) (v >> 8);
            buff[3] = (byte) v;
        }
        else
        {
            buff[3] = (byte) (v >> 24);
            buff[2] = (byte) (v >> 16);
            buff[1] = (byte) (v >> 8);
            buff[0] = (byte) v;
        }

        write(buff, 0, 4);
    }

    public void writeLong(long v) throws IOException
    {
        if (byteOrder == ByteOrder.BIG_ENDIAN)
        {
            buff[0] = (byte) (v >> 56);
            buff[1] = (byte) (v >> 48);
            buff[2] = (byte) (v >> 40);
            buff[3] = (byte) (v >> 32);
            buff[4] = (byte) (v >> 24);
            buff[5] = (byte) (v >> 16);
            buff[6] = (byte) (v >> 8);
            buff[7] = (byte) (v);
        }
        else
        {
            buff[7] = (byte) (v >> 56);
            buff[6] = (byte) (v >> 48);
            buff[5] = (byte) (v >> 40);
            buff[4] = (byte) (v >> 32);
            buff[3] = (byte) (v >> 24);
            buff[2] = (byte) (v >> 16);
            buff[1] = (byte) (v >> 8);
            buff[0] = (byte) (v);
        }

        write(buff, 0, 8);
    }

    public void writeFloat(float v) throws IOException
    {
        writeInt(Float.floatToIntBits(v));
    }

    public void writeDouble(double v) throws IOException
    {
        writeLong(Double.doubleToLongBits(v));
    }

    public void writeBytes(String s) throws IOException
    {
        write(s.getBytes());
    }

    public void writeChars(String s) throws IOException
    {
        char[] chs = s.toCharArray();
        writeChars(chs, 0, chs.length);
    }

    public void writeUTF(String s) throws IOException
    {
        ByteOrder byteOrder = getByteOrder();
        setByteOrder(ByteOrder.BIG_ENDIAN);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        new DataOutputStream(baos).writeUTF(s);
        write(baos.toByteArray(), 0, baos.size());
        setByteOrder(byteOrder);
    }

    public void writeShorts(short[] s, int off, int len) throws IOException
    {
        if ((off < 0) || (len < 0) || (off + len > s.length))
        {
            throw new IndexOutOfBoundsException();
        }

        for (int i = 0; i < len; i++)
        {
            writeShort(s[off + i]);
        }
    }

    public void writeChars(char[] c, int off, int len) throws IOException
    {
        if ((off < 0) || (len < 0) || (off + len > c.length))
        {
            throw new IndexOutOfBoundsException();
        }

        for (int i = 0; i < len; i++)
        {
            writeShort(c[off + i]);
        }
    }

    public void writeInts(int[] i, int off, int len) throws IOException
    {
        if ((off < 0) || (len < 0) || (off + len > i.length))
        {
            throw new IndexOutOfBoundsException();
        }

        for (int n = 0; n < len; n++)
        {
            writeInt(i[off + n]);
        }
    }

    public void writeLongs(long[] l, int off, int len) throws IOException
    {
        if ((off < 0) || (len < 0) || (off + len > l.length))
        {
            throw new IndexOutOfBoundsException();
        }

        for (int i = 0; i < len; i++)
        {
            writeLong(l[off + i]);
        }
    }

    public void writeFloats(float[] f, int off, int len) throws IOException
    {
        if ((off < 0) || (len < 0) || (off + len > f.length))
        {
            throw new IndexOutOfBoundsException();
        }

        for (int i = 0; i < len; i++)
        {
            writeFloat(f[off + i]);
        }
    }

    public void writeDoubles(double[] d, int off, int len) throws IOException
    {
        if ((off < 0) || (len < 0) || (off + len > d.length))
        {
            throw new IndexOutOfBoundsException();
        }

        for (int i = 0; i < len; i++)
        {
            writeDouble(d[off + i]);
        }
    }

    public void writeBit(int bit) throws IOException
    {
        writeBits((long) bit, 1);
    }

    public void writeBits(long bits, int numBits) throws IOException
    {
        checkClosed();

        if (bitOffset > 0)
        {
            int oldBitOffset = bitOffset;
            int currentByte = read();
            if (currentByte == -1)
            {
                currentByte = 0;
            }
            else
            {
                seek(getStreamPosition() - 1);
            }

            int num = 8 - oldBitOffset;
            if (numBits >= num)
            {
                int mask = -1 >>> (32 - num);
                currentByte &= ~mask;
                numBits -= num;
                currentByte |= ((bits >> numBits) & mask);
                write(currentByte);
            }
            else
            {
                int offset = oldBitOffset + numBits;
                int mask = -1 >>> numBits;
                currentByte &= ~(mask << (8 - offset));
                currentByte |= ((bits & mask) << (8 - offset));
                write(currentByte);
                seek(getStreamPosition() - 1);
                bitOffset = offset;
                numBits = 0;
            }
        }

        while (numBits > 7)
        {
            int mask = -1 >>> 24;
            int currentByte = (int) ((bits >> (numBits - 8)) & mask);
            write(currentByte);
            numBits -= 8;
        }

        if (numBits > 0)
        {
            int mask = -1 >>> 24;
            int currentByte = (int) ((bits << (8 - numBits)) & mask);
            write(currentByte);
            seek(getStreamPosition() - 1);
            bitOffset = numBits;
        }
    }

    protected final void flushBits() throws IOException
    {
        checkClosed();
        if (bitOffset == 0)
        {
            return;
        }
        int offset = bitOffset;
        int currentByte = read();
        if (currentByte == -1)
        {
            currentByte = 0;
            bitOffset = 0;
        }
        else
        {
            seek(getStreamPosition() - 1);
            currentByte &= -1 << (8 - offset);
        }
        write(currentByte);
    }
}
