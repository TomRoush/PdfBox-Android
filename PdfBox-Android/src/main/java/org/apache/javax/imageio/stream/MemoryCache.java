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
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Package-visible class consolidating common code for
 * <code>MemoryCacheImageInputStream</code> and
 * <code>MemoryCacheImageOutputStream</code>.
 * This class keeps an <code>ArrayList</code> of 8K blocks,
 * loaded sequentially.  Blocks may only be disposed of
 * from the index 0 forward.  As blocks are freed, the
 * corresponding entries in the array list are set to
 * <code>null</code>, but no compacting is performed.
 * This allows the index for each block to never change,
 * and the length of the cache is always the same as the
 * total amount of data ever cached.  Cached data is
 * therefore always contiguous from the point of last
 * disposal to the current length.
 *
 * <p> The total number of blocks resident in the cache must not
 * exceed <code>Integer.MAX_VALUE</code>.  In practice, the limit of
 * available memory will be exceeded long before this becomes an
 * issue, since a full cache would contain 8192*2^31 = 16 terabytes of
 * data.
 *
 * A <code>MemoryCache</code> may be reused after a call
 * to <code>reset()</code>.
 */
public class MemoryCache
{
	private static final int BUFFER_LENGTH = 8192;

    private ArrayList cache = new ArrayList();

    private long cacheStart = 0L;

    /**
     * The largest position ever written to the cache.
     */
    private long length = 0L;
    
    private byte[] getCacheBlock(long blockNum) throws IOException {
        long blockOffset = blockNum - cacheStart;
        if (blockOffset > Integer.MAX_VALUE) {
            // This can only happen when the cache hits 16 terabytes of
            // contiguous data...
            throw new IOException("Cache addressing limit exceeded!");
        }
        return (byte[])cache.get((int)blockOffset);
    }
    
    /**
     * Ensures that at least <code>pos</code> bytes are cached,
     * or the end of the source is reached.  The return value
     * is equal to the smaller of <code>pos</code> and the
     * length of the source.
     */
    public long loadFromStream(InputStream stream, long pos)
        throws IOException {
        // We've already got enough data cached
        if (pos < length) {
            return pos;
        }

        int offset = (int)(length % BUFFER_LENGTH);
        byte [] buf = null;

        long len = pos - length;
        if (offset != 0) {
            buf = getCacheBlock(length/BUFFER_LENGTH);
        }

        while (len > 0) {
            if (buf == null) {
                try {
                    buf = new byte[BUFFER_LENGTH];
                } catch (OutOfMemoryError e) {
                    throw new IOException("No memory left for cache!");
                }
                offset = 0;
            }

            int left = BUFFER_LENGTH - offset;
            int nbytes = (int)Math.min(len, (long)left);
            nbytes = stream.read(buf, offset, nbytes);
            if (nbytes == -1) {
                return length; // EOF
            }

            if (offset == 0) {
                cache.add(buf);
            }

            len -= nbytes;
            length += nbytes;
            offset += nbytes;

            if (offset >= BUFFER_LENGTH) {
                // we've filled the current buffer, so a new one will be
                // allocated next time around (and offset will be reset to 0)
                buf = null;
            }
        }

        return pos;
    }
    
    /**
     * Returns the single byte at the given position, as an
     * <code>int</code>.  Returns -1 if this position has
     * not been cached or has been disposed.
     */
    public int read(long pos) throws IOException {
        if (pos >= length) {
            return -1;
        }

        byte[] buf = getCacheBlock(pos/BUFFER_LENGTH);
        if (buf == null) {
            return -1;
        }

        return buf[(int)(pos % BUFFER_LENGTH)] & 0xff;
    }
    
    /**
     * Overwrites or appends a single byte to the cache.
     * The length of the cache will be extended as needed to hold
     * the incoming data.
     *
     * @param b an <code>int</code> whose 8 least significant bits
     * will be written.
     * @param pos the cache position at which to begin writing.
     *
     * @exception IndexOutOfBoundsException if <code>pos</code> is negative.
     */
    public void write(int b, long pos) throws IOException {
        if (pos < 0) {
            throw new ArrayIndexOutOfBoundsException("pos < 0");
        }

        // Ensure there is space for the incoming data
        if (pos >= length) {
            pad(pos);
            length = pos + 1;
        }

        // Insert the data.
        byte[] buf = getCacheBlock(pos/BUFFER_LENGTH);
        int offset = (int)(pos % BUFFER_LENGTH);
        buf[offset] = (byte)b;
    }
    
    /**
     * Ensure that there is space to write a byte at the given position.
     */
    private void pad(long pos) throws IOException {
        long currIndex = cacheStart + cache.size() - 1;
        long lastIndex = pos/BUFFER_LENGTH;
        long numNewBuffers = lastIndex - currIndex;
        for (long i = 0; i < numNewBuffers; i++) {
            try {
                cache.add(new byte[BUFFER_LENGTH]);
            } catch (OutOfMemoryError e) {
                throw new IOException("No memory left for cache!");
            }
        }
    }
}
