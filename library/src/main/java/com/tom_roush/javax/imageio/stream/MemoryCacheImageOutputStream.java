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

import java.io.IOException;
import java.io.OutputStream;

/**
 * An implementation of <code>ImageOutputStream</code> that writes its
 * output to a regular <code>OutputStream</code>.  A memory buffer is
 * used to cache at least the data between the discard position and
 * the current write position.  The only constructor takes an
 * <code>OutputStream</code>, so this class may not be used for
 * read/modify/write operations.  Reading can occur only on parts of
 * the stream that have already been written to the cache and not
 * yet flushed.
 *
 */
public class MemoryCacheImageOutputStream
{
	private OutputStream stream;

	private MemoryCache cache = new MemoryCache();

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

	/**
	 * Constructs a <code>MemoryCacheImageOutputStream</code> that will write
	 * to a given <code>OutputStream</code>.
	 *
	 * @param stream an <code>OutputStream</code> to write to.
	 *
	 * @exception IllegalArgumentException if <code>stream</code> is
	 * <code>null</code>.
	 */
	public MemoryCacheImageOutputStream(OutputStream stream) {
		if (stream == null) {
			throw new IllegalArgumentException("stream == null!");
		}
		this.stream = stream;
	}

	public void writeBits(long bits, int numBits) throws IOException {
		if (numBits < 0 || numBits > 64) {
			throw new IllegalArgumentException("Bad value for numBits!");
		}
		if (numBits == 0) {
			return;
		}

		// Prologue: deal with pre-existing bits

		// Bug 4499158, 4507868 - if we're at the beginning of the stream
		// and the bit offset is 0, there can't be any pre-existing bits
		if ((streamPos > 0) || (bitOffset > 0)) {
			int offset = bitOffset;  // read() will reset bitOffset
			int partialByte = read();
			if (partialByte != -1) {
				seek(streamPos - 1);
			} else {
				partialByte = 0;
			}

			if (numBits + offset < 8) {
				// Notch out the partial byte and drop in the new bits
				int shift = 8 - (offset+numBits);
				int mask = -1 >>> (32 - numBits);
				partialByte &= ~(mask << shift);  // Clear out old bits
				partialByte |= ((bits & mask) << shift); // Or in new ones
				write(partialByte);
				seek(streamPos - 1);
				bitOffset = offset + numBits;
				numBits = 0;  // Signal that we are done
			} else {
				// Fill out the partial byte and reduce numBits
				int num = 8 - offset;
				int mask = -1 >>> (32 - num);
				partialByte &= ~mask;  // Clear out bits
				partialByte |= ((bits >> (numBits - num)) & mask);
				// Note that bitOffset is already 0, so there is no risk
				// of this advancing to the next byte
				write(partialByte);
				numBits -= num;
			}
		}
	}

	public int read() throws IOException {
		bitOffset = 0;

		int val = cache.read(streamPos);
		if (val != -1) {
			++streamPos;
		}
		return val;
	}
	
	public void seek(long pos) throws IOException {
        // This test also covers pos < 0
        if (pos < flushedPos) {
            throw new IndexOutOfBoundsException("pos < flushedPos!");
        }

        this.streamPos = pos;
        this.bitOffset = 0;
    }
	
	public void write(int b) throws IOException {
        flushBits(); // this will call checkClosed() for us
        cache.write(b, streamPos);
        ++streamPos;
    }
	
	/**
     * If the bit offset is non-zero, forces the remaining bits
     * in the current byte to 0 and advances the stream position
     * by one.  This method should be called by subclasses at the
     * beginning of the <code>write(int)</code> and
     * <code>write(byte[], int, int)</code> methods.
     *
     * @exception IOException if an I/O error occurs.
     */
    protected final void flushBits() throws IOException {
        if (bitOffset != 0) {
            int offset = bitOffset;
            int partialByte = read(); // Sets bitOffset to 0
            if (partialByte < 0) {
                // Fix 4465683: When bitOffset is set
                // to something non-zero beyond EOF,
                // we should set that whole byte to
                // zero and write it to stream.
                partialByte = 0;
                bitOffset = 0;
            }
            else {
                seek(streamPos - 1);
                partialByte &= -1 << (8 - offset);
            }
            write(partialByte);
        }
    }
    
    public void flush() throws IOException {
        flushBefore(streamPos);
    }
    
    public void flushBefore(long pos) throws IOException {
        if (pos < flushedPos) {
            throw new IndexOutOfBoundsException("pos < flushedPos!");
        }
        if (pos > streamPos) {
            throw new IndexOutOfBoundsException("pos > getStreamPosition()!");
        }
        // Invariant: flushedPos >= 0
        flushedPos = pos;
    }
}
