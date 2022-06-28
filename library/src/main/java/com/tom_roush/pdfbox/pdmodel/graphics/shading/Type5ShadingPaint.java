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
package com.tom_roush.pdfbox.pdmodel.graphics.shading;

import android.graphics.Rect;
import android.util.Log;

import java.io.IOException;

import com.tom_roush.harmony.awt.PaintContext;
import com.tom_roush.harmony.awt.geom.AffineTransform;
import com.tom_roush.pdfbox.util.Matrix;

/**
 * Paint for Gouraud Triangle Lattice (Type 5) shading.
 */
class Type5ShadingPaint extends ShadingPaint<PDShadingType5>
{
    /**
     * Constructor.
     *
     * @param shading the shading resources
     * @param matrix the pattern matrix concatenated with that of the parent content stream
     */
    Type5ShadingPaint(PDShadingType5 shading, Matrix matrix)
    {
        super(shading, matrix);
    }

    @Override
    public PaintContext createContext(Rect deviceBounds, AffineTransform xform)
    {
        try
        {
            return new Type5ShadingContext(shading, xform, matrix, deviceBounds);
        }
        catch (IOException e)
        {
            Log.e("PdfBox-Android", "An error occurred while painting", e);
            return null;
        }
    }
}
