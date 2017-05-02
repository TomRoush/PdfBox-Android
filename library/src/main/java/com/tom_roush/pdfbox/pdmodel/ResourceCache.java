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

package com.tom_roush.pdfbox.pdmodel;

import com.tom_roush.pdfbox.cos.COSObject;
import com.tom_roush.pdfbox.pdmodel.documentinterchange.markedcontent.PDPropertyList;
import com.tom_roush.pdfbox.pdmodel.font.PDFont;
import com.tom_roush.pdfbox.pdmodel.graphics.PDXObject;
import com.tom_roush.pdfbox.pdmodel.graphics.color.PDColorSpace;
import com.tom_roush.pdfbox.pdmodel.graphics.pattern.PDAbstractPattern;
import com.tom_roush.pdfbox.pdmodel.graphics.shading.PDShading;
import com.tom_roush.pdfbox.pdmodel.graphics.state.PDExtendedGraphicsState;

import java.io.IOException;

/**
 * A document-wide cache for page resources.
 *
 * @author John Hewson
 */
public interface ResourceCache
{
    /**
     * Returns the font resource for the given indirect object, if it is in the cache.
     */
    PDFont getFont(COSObject indirect) throws IOException;

    /**
     * Returns the color space resource for the given indirect object, if it is in the cache.
     */
    PDColorSpace getColorSpace(COSObject indirect) throws IOException;

    /**
     * Returns the external graphics state resource for the given indirect object, if it is in the
     * cache.
     */
    PDExtendedGraphicsState getExtGState(COSObject indirect);

    /**
     * Returns the shading resource for the given indirect object, if it is in the cache.
     */
    PDShading getShading(COSObject indirect) throws IOException;

    /**
     * Returns the pattern resource for the given indirect object, if it is in the cache.
     */
    PDAbstractPattern getPattern(COSObject indirect) throws IOException;

    /**
     * Returns the property list resource for the given indirect object, if it is in the cache.
     */
    PDPropertyList getProperties(COSObject indirect);

    /**
     * Returns the XObject resource for the given indirect object, if it is in the cache.
     */
    PDXObject getXObject(COSObject indirect) throws IOException;

    /**
     * Puts the given indirect font resource in the cache.
     */
    void put(COSObject indirect, PDFont font) throws IOException;

    /**
     * Puts the given indirect color space resource in the cache.
     */
    void put(COSObject indirect, PDColorSpace colorSpace) throws IOException;

    /**
     * Puts the given indirect extended graphics state resource in the cache.
     */
    void put(COSObject indirect, PDExtendedGraphicsState extGState);

    /**
     * Puts the given indirect shading resource in the cache.
     */
    void put(COSObject indirect, PDShading shading) throws IOException;

    /**
     * Puts the given indirect pattern resource in the cache.
     */
    void put(COSObject indirect, PDAbstractPattern pattern) throws IOException;

    /**
     * Puts the given indirect property list resource in the cache.
     */
    void put(COSObject indirect, PDPropertyList propertyList);

    /**
     * Puts the given indirect XObject resource in the cache.
     */
    void put(COSObject indirect, PDXObject xobject) throws IOException;
}
