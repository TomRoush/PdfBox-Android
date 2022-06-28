/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.tom_roush.harmony.awt;

import android.graphics.Rect;

import com.tom_roush.harmony.awt.geom.AffineTransform;

public interface Paint
{

    /**
     * Creates and returns a {@link PaintContext} used to
     * generate the color pattern.
     * The arguments to this method convey additional information
     * about the rendering operation that may be
     * used or ignored on various implementations of the {@code Paint} interface.
     * A caller must pass non-{@code null} values for all of the arguments.
     * Implementations of the {@code Paint} interface are allowed to use or ignore
     * any of the arguments as makes sense for their function.
     * Implementations are allowed to throw {@code NullPointerException} for
     * any {@code null} argument, but are not required to do so.
     *
     * @param deviceBounds the device space bounding box
     * of the graphics primitive being rendered.
     * Implementations of the {@code Paint} interface
     * are allowed to throw {@code NullPointerException}
     * for a {@code null deviceBounds}.
     * @param xform the {@link AffineTransform} from user
     * space into device space.
     * Implementations of the {@code Paint} interface
     * are allowed to throw {@code NullPointerException}
     * for a {@code null xform}.
     *
     * @return the {@code PaintContext} for
     * generating color patterns.
     * @see PaintContext
     * @see Rect
     * @see AffineTransform
     */
    PaintContext createContext(Rect deviceBounds, AffineTransform xform);
}
