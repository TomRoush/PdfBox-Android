/*
 * Copyright (c) 2000, 2006, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

package org.apache.javax.imageio.stream;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

/**
 * An implementation of <code>ImageInputStream</code> that gets its
 * input from a regular <code>InputStream</code>.  A memory buffer is
 * used to cache at least the data between the discard position and
 * the current read position.
 *
 * <p> In general, it is preferable to use a
 * <code>FileCacheImageInputStream</code> when reading from a regular
 * <code>InputStream</code>.  This class is provided for cases where
 * it is not possible to create a writable temporary file.
 *
 */
public class MemoryCacheImageInputStream
{
	/**
     * The current read position within the stream.  Subclasses are
     * responsible for keeping this value current from any method they
     * override that alters the read position.
     */
    protected long streamPos;
	
	/**
     * The current bit offset within the stream.  Subclasses are
     * responsible for keeping this value current from any method they
     * override that alters the bit offset.
     */
    protected int bitOffset;
    
    /**
     * The position prior to which data may be discarded.  Seeking
     * to a smaller position is not allowed.  <code>flushedPos</code>
     * will always be >= 0.
     */
    protected long flushedPos = 0;
    
    private InputStream stream;
    
    private MemoryCache cache = new MemoryCache();
    
    /**
     * Constructs a <code>MemoryCacheImageInputStream</code> that will read
     * from a given <code>InputStream</code>.
     *
     * @param stream an <code>InputStream</code> to read from.
     *
     * @exception IllegalArgumentException if <code>stream</code> is
     * <code>null</code>.
     */
    public MemoryCacheImageInputStream(InputStream stream) {
        if (stream == null) {
            throw new IllegalArgumentException("stream == null!");
        }
        this.stream = stream;
    }
	
	public long readBits(int numBits) throws IOException {
        if (numBits < 0 || numBits > 64) {
            throw new IllegalArgumentException();
        }
        if (numBits == 0) {
            return 0L;
        }

        // Have to read additional bits on the left equal to the bit offset
        int bitsToRead = numBits + bitOffset;

        // Compute final bit offset before we call read() and seek()
        int newBitOffset = (this.bitOffset + numBits) & 0x7;

        // Read a byte at a time, accumulate
        long accum = 0L;
        while (bitsToRead > 0) {
            int val = read();
            if (val == -1) {
                throw new EOFException();
            }

            accum <<= 8;
            accum |= val;
            bitsToRead -= 8;
        }

        // Move byte position back if in the middle of a byte
        if (newBitOffset != 0) {
            seek(streamPos - 1);
        }
        this.bitOffset = newBitOffset;

        // Shift away unwanted bits on the right.
        accum >>>= (-bitsToRead); // Negative of bitsToRead == extra bits read

        // Mask out unwanted bits on the left
        accum &= (-1L >>> (64 - numBits));

        return accum;
    }
	
	public void seek(long pos) throws IOException {
        // This test also covers pos < 0
        if (pos < flushedPos) {
            throw new IndexOutOfBoundsException("pos < flushedPos!");
        }

        this.streamPos = pos;
        this.bitOffset = 0;
    }
	
	public int read() throws IOException {
//		checkClosed();
        bitOffset = 0;
        long pos = cache.loadFromStream(stream, streamPos+1);
        if (pos  >= streamPos+1) {
            return cache.read(streamPos++);
        } else {
            return -1;
        }
	}

}
