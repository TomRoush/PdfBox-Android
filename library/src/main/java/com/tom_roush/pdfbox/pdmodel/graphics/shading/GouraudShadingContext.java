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

import android.graphics.Point;
import android.graphics.Rect;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.tom_roush.harmony.awt.geom.AffineTransform;
import com.tom_roush.pdfbox.util.Matrix;

/**
 * Shades Gouraud triangles for Type4ShadingContext and Type5ShadingContext.
 *
 * @author Tilman Hausherr
 * @author Shaola Ren
 */
abstract class GouraudShadingContext extends TriangleBasedShadingContext
{
    /**
     * triangle list.
     */
    private List<ShadedTriangle> triangleList = new ArrayList<ShadedTriangle>();

    /**
     * Constructor creates an instance to be used for fill operations.
     *
     * @param shading the shading type to be used
     * @param xform transformation for user to device space
     * @param matrix the pattern matrix concatenated with that of the parent content stream
     * @throws IOException if something went wrong
     */
    protected GouraudShadingContext(PDShading shading, AffineTransform xform,
        Matrix matrix) throws IOException
    {
        super(shading, xform, matrix);
    }

    final void setTriangleList(List<ShadedTriangle> triangleList)
    {
        this.triangleList = triangleList;
    }

    @Override
    protected Map<Point, Integer> calcPixelTable(Rect deviceBounds) throws IOException
    {
        Map<Point, Integer> map = new HashMap<Point, Integer>();
        super.calcPixelTable(triangleList, map, deviceBounds);
        return map;
    }

    @Override
    public void dispose()
    {
        triangleList = null;
        super.dispose();
    }

    @Override
    protected boolean isDataEmpty()
    {
        return triangleList.isEmpty();
    }
}
