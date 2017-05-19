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

import java.io.DataInput;
import java.io.IOException;
import java.nio.ByteOrder;

public interface ImageInputStream extends DataInput
{
    void setByteOrder(ByteOrder byteOrder);

    ByteOrder getByteOrder();

    int read() throws IOException;

    int read(byte[] b) throws IOException;

    int read(byte[] b, int off, int len) throws IOException;

    void readBytes(IIOByteBuffer buf, int len) throws IOException;

    boolean readBoolean() throws IOException;

    byte readByte() throws IOException;

    int readUnsignedByte() throws IOException;

    short readShort() throws IOException;

    int readUnsignedShort() throws IOException;

    char readChar() throws IOException;

    int readInt() throws IOException;

    long readUnsignedInt() throws IOException;

    long readLong() throws IOException;

    float readFloat() throws IOException;

    double readDouble() throws IOException;

    String readLine() throws IOException;

    String readUTF() throws IOException;

    void readFully(byte[] b, int off, int len) throws IOException;

    void readFully(byte[] b) throws IOException;

    void readFully(short[] s, int off, int len) throws IOException;

    void readFully(char[] c, int off, int len) throws IOException;

    void readFully(int[] i, int off, int len) throws IOException;

    void readFully(long[] l, int off, int len) throws IOException;

    void readFully(float[] f, int off, int len) throws IOException;

    void readFully(double[] d, int off, int len) throws IOException;

    long getStreamPosition() throws IOException;

    int getBitOffset() throws IOException;

    void setBitOffset(int bitOffset) throws IOException;

    int readBit() throws IOException;

    long readBits(int numBits) throws IOException;

    long length() throws IOException;

    int skipBytes(int n) throws IOException;

    long skipBytes(long n) throws IOException;

    void seek(long pos) throws IOException;

    void mark();

    void reset() throws IOException;

    void flushBefore(long pos) throws IOException;

    void flush() throws IOException;

    long getFlushedPosition();

    boolean isCached();

    boolean isCachedMemory();

    boolean isCachedFile();

    void close() throws IOException;
}
