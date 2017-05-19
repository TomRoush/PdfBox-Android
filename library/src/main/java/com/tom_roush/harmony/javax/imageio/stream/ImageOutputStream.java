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

import java.io.DataOutput;
import java.io.IOException;

public interface ImageOutputStream extends DataOutput, ImageInputStream
{
    /**
     * DataOutput methods redeclaration
     */
    void write(int b) throws IOException;

    void write(byte[] b) throws IOException;

    void write(byte[] b, int off, int len) throws IOException;

    void writeBoolean(boolean b) throws IOException;

    void writeByte(int b) throws IOException;

    void writeShort(int v) throws IOException;

    void writeChar(int v) throws IOException;

    void writeInt(int v) throws IOException;

    void writeLong(long v) throws IOException;

    void writeFloat(float v) throws IOException;

    void writeDouble(double v) throws IOException;

    void writeBytes(String s) throws IOException;

    void writeChars(String s) throws IOException;

    void writeUTF(String s) throws IOException;

    /**
     * ImageInputStream method
     */
    void flushBefore(long pos) throws IOException;


    /**
     * ImageOutputStream specific methods
     */
    void writeShorts(short[] s, int off, int len) throws IOException;

    void writeChars(char[] c, int off, int len) throws IOException;

    void writeInts(int[] i, int off, int len) throws IOException;

    void writeLongs(long[] l, int off, int len) throws IOException;

    void writeFloats(float[] f, int off, int len) throws IOException;

    void writeDoubles(double[] d, int off, int len) throws IOException;

    void writeBit(int bit) throws IOException;

    void writeBits(long bits, int numBits) throws IOException;

}
