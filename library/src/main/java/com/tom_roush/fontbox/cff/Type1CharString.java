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
package com.tom_roush.fontbox.cff;

import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.tom_roush.fontbox.encoding.StandardEncoding;
import com.tom_roush.fontbox.type1.Type1CharStringReader;
import com.tom_roush.harmony.awt.geom.AffineTransform;

/**
 * This class represents and renders a Type 1 CharString.
 *
 * @author Villu Ruusmann
 * @author John Hewson
 */
public class Type1CharString
{
    private Type1CharStringReader font;
    private final String fontName;
    private final String glyphName;
    private Path path = null;
    private int width = 0;
    private PointF leftSideBearing = null;
    private PointF current = null;
    private boolean isFlex = false;
    private final List<PointF> flexPoints = new ArrayList<PointF>();
    protected List<Object> type1Sequence;
    protected int commandCount;

    /**
     * Constructs a new Type1CharString object.
     *
     * @param font Parent Type 1 CharString font.
     * @param fontName Name of the font.
     * @param glyphName Name of the glyph.
     * @param sequence Type 1 char string sequence
     */
    public Type1CharString(Type1CharStringReader font, String fontName, String glyphName,
        List<Object> sequence)
    {
        this(font, fontName, glyphName);
        type1Sequence = sequence;
    }

    /**
     * Constructor for use in subclasses.
     *
     * @param font Parent Type 1 CharString font.
     * @param fontName Name of the font.
     * @param glyphName Name of the glyph.
     */
    protected Type1CharString(Type1CharStringReader font, String fontName, String glyphName)
    {
        this.font = font;
        this.fontName = fontName;
        this.glyphName = glyphName;
        this.current = new PointF(0, 0);
    }

    // todo: NEW name (or CID as hex)
    public String getName()
    {
        return glyphName;
    }

    /**
     * Returns the bounds of the renderer path.
     * @return the bounds as RectF
     */
    public RectF getBounds()
    {
        if (path == null)
        {
            render();
        }
        RectF retval = new RectF();
        path.computeBounds(retval, true);
        return retval;
    }

    /**
     * Returns the advance width of the glyph.
     * @return the width
     */
    public int getWidth()
    {
        if (path == null)
        {
            render();
        }
        return width;
    }

    /**
     * Returns the path of the character.
     * @return the path
     */
    public Path getPath()
    {
        if (path == null)
        {
            render();
        }
        return path;
    }

    /**
     * Returns the Type 1 char string sequence.
     * @return the Type 1 sequence
     */
    public List<Object> getType1Sequence()
    {
        return type1Sequence;
    }

    /**
     * Renders the Type 1 char string sequence to a Path.
     */
    private void render()
    {
        path = new Path();
        leftSideBearing = new PointF(0, 0);
        width = 0;
        CharStringHandler handler = new CharStringHandler() {
            @Override
            public List<Number> handleCommand(List<Number> numbers, CharStringCommand command)
            {
                return Type1CharString.this.handleCommand(numbers, command);
            }
        };
        handler.handleSequence(type1Sequence);
    }

    private List<Number> handleCommand(List<Number> numbers, CharStringCommand command)
    {
        commandCount++;
        String name = CharStringCommand.TYPE1_VOCABULARY.get(command.getKey());

        if ("rmoveto".equals(name))
        {
            if (numbers.size() >= 2)
            {
                if (isFlex)
                {
                    flexPoints.add(new PointF(numbers.get(0).floatValue(), numbers.get(1).floatValue()));
                }
                else
                {
                    rmoveTo(numbers.get(0), numbers.get(1));
                }
            }
        }
        else if ("vmoveto".equals(name))
        {
            if (!numbers.isEmpty())
            {
                if (isFlex)
                {
                    // not in the Type 1 spec, but exists in some fonts
                    flexPoints.add(new PointF(0f, numbers.get(0).floatValue()));
                }
                else
                {
                    rmoveTo(0, numbers.get(0));
                }
            }
        }
        else if ("hmoveto".equals(name))
        {
            if (!numbers.isEmpty())
            {
                if (isFlex)
                {
                    // not in the Type 1 spec, but exists in some fonts
                    flexPoints.add(new PointF(numbers.get(0).floatValue(), 0f));
                }
                else
                {
                    rmoveTo(numbers.get(0), 0);
                }
            }
        }
        else if ("rlineto".equals(name))
        {
            if (numbers.size() >= 2)
            {
                rlineTo(numbers.get(0), numbers.get(1));
            }
        }
        else if ("hlineto".equals(name))
        {
            if (!numbers.isEmpty())
            {
                rlineTo(numbers.get(0), 0);
            }
        }
        else if ("vlineto".equals(name))
        {
            if (!numbers.isEmpty())
            {
                rlineTo(0, numbers.get(0));
            }
        }
        else if ("rrcurveto".equals(name))
        {
            if (numbers.size() >= 6)
            {
                rrcurveTo(numbers.get(0), numbers.get(1), numbers.get(2),
                    numbers.get(3), numbers.get(4), numbers.get(5));
            }
        }
        else if ("closepath".equals(name))
        {
            closeCharString1Path();
        }
        else if ("sbw".equals(name))
        {
            if (numbers.size() >= 3)
            {
                leftSideBearing = new PointF(numbers.get(0).floatValue(), numbers.get(1).floatValue());
                width = numbers.get(2).intValue();
                current.set(leftSideBearing);
            }
        }
        else if ("hsbw".equals(name))
        {
            if (numbers.size() >= 2)
            {
                leftSideBearing = new PointF(numbers.get(0).floatValue(), 0);
                width = numbers.get(1).intValue();
                current.set(leftSideBearing);
            }
        }
        else if ("vhcurveto".equals(name))
        {
            if (numbers.size() >= 4)
            {
                rrcurveTo(0, numbers.get(0), numbers.get(1),
                    numbers.get(2), numbers.get(3), 0);
            }
        }
        else if ("hvcurveto".equals(name))
        {
            if (numbers.size() >= 4)
            {
                rrcurveTo(numbers.get(0), 0, numbers.get(1),
                    numbers.get(2), 0, numbers.get(3));
            }
        }
        else if ("seac".equals(name))
        {
            if (numbers.size() >= 5)
            {
                seac(numbers.get(0), numbers.get(1), numbers.get(2), numbers.get(3), numbers.get(4));
            }
        }
        else if ("setcurrentpoint".equals(name))
        {
            if (numbers.size() >= 2)
            {
                setcurrentpoint(numbers.get(0), numbers.get(1));
            }
        }
        else if ("callothersubr".equals(name))
        {
            if (!numbers.isEmpty())
            {
                callothersubr(numbers.get(0).intValue());
            }
        }
        else if ("div".equals(name))
        {
            if (numbers.size() >= 2)
            {
                float b = numbers.get(numbers.size() - 1).floatValue();
                float a = numbers.get(numbers.size() - 2).floatValue();

                float result = a / b;

                List<Number> list = new ArrayList<Number>(numbers);
                list.remove(list.size() - 1);
                list.remove(list.size() - 1);
                list.add(result);
                return list;
            }
        }
        else if ("hstem".equals(name) || "vstem".equals(name) ||
            "hstem3".equals(name) || "vstem3".equals(name) || "dotsection".equals(name))
        {
            // ignore hints
        }
        else if ("endchar".equals(name))
        {
            // end
        }
        else if ("return".equals(name) || "callsubr".equals(name))
        {
            // indicates an invalid charstring
            Log.w("PdfBox-Android", "Unexpected charstring command: " + name + " in glyph " +
                glyphName + " of font " + fontName);
        }
        else if (name != null)
        {
            // indicates a PDFBox bug
            throw new IllegalArgumentException("Unhandled command: " + name);
        }
        else
        {
            // indicates an invalid charstring
            Log.w("PdfBox-Android", "Unknown charstring command: " + command.getKey() + " in glyph " + glyphName +
                " of font " + fontName);
        }
        return null;
    }

    /**
     * Sets the current absolute point without performing a moveto.
     * Used only with results from callothersubr
     */
    private void setcurrentpoint(Number x, Number y)
    {
        current.set(x.floatValue(), y.floatValue());
    }

    /**
     * Flex (via OtherSubrs)
     * @param num OtherSubrs entry number
     */
    private void callothersubr(int num)
    {
        if (num == 0)
        {
            // end flex
            isFlex = false;

            if (flexPoints.size() < 7)
            {
                Log.w("PdfBox-Android", "flex without moveTo in font " + fontName + ", glyph " + glyphName +
                    ", command " + commandCount);
                return;
            }

            // reference point is relative to start point
            PointF reference = flexPoints.get(0);
            reference.set(current.x + reference.x,
                current.y + reference.y);

            // first point is relative to reference point
            PointF first = flexPoints.get(1);
            first.set(reference.x + first.x, reference.y + first.y);

            // make the first point relative to the start point
            first.set(first.x - current.x, first.y - current.y);

            PointF p1 = flexPoints.get(1);
            PointF p2 = flexPoints.get(2);
            PointF p3 = flexPoints.get(3);
            rrcurveTo(p1.x, p1.y, p2.x, p2.y, p3.x, p3.y);

            PointF p4 = flexPoints.get(4);
            PointF p5 = flexPoints.get(5);
            PointF p6 = flexPoints.get(6);
            rrcurveTo(p4.x, p4.y, p5.x, p5.y, p6.x, p6.y);

            flexPoints.clear();
        }
        else if (num == 1)
        {
            // begin flex
            isFlex = true;
        }
        else
        {
            Log.w("PdfBox-Android", "Invalid callothersubr parameter: " + num);
        }
    }

    /**
     * Relative moveto.
     */
    private void rmoveTo(Number dx, Number dy)
    {
        float x = (float)current.x + dx.floatValue();
        float y = (float)current.y + dy.floatValue();
        path.moveTo(x, y);
        current.set(x, y);
    }

    /**
     * Relative lineto.
     */
    private void rlineTo(Number dx, Number dy)
    {
        float x = (float)current.x + dx.floatValue();
        float y = (float)current.y + dy.floatValue();
        if (path.isEmpty())
        {
            Log.w("PdfBox-Android", "rlineTo without initial moveTo in font " + fontName + ", glyph " + glyphName);
            path.moveTo(x, y);
        }
        else
        {
            path.lineTo(x, y);
        }
        current.set(x, y);
    }

    /**
     * Relative curveto.
     */
    private void rrcurveTo(Number dx1, Number dy1, Number dx2, Number dy2,
        Number dx3, Number dy3)
    {
        float x1 = (float) current.x + dx1.floatValue();
        float y1 = (float) current.y + dy1.floatValue();
        float x2 = x1 + dx2.floatValue();
        float y2 = y1 + dy2.floatValue();
        float x3 = x2 + dx3.floatValue();
        float y3 = y2 + dy3.floatValue();
        if (path.isEmpty())
        {
            Log.w("PdfBox-Android", "rrcurveTo without initial moveTo in font " + fontName + ", glyph " + glyphName);
            path.moveTo(x3, y3);
        }
        else
        {
            path.cubicTo(x1, y1, x2, y2, x3, y3);
        }
        current.set(x3, y3);
    }

    /**
     * Close path.
     */
    private void closeCharString1Path()
    {
        if (path.isEmpty())
        {
            Log.w("PdfBox-Android", "closepath without initial moveTo in font " + fontName + ", glyph " + glyphName);
        }
        else
        {
            path.close();
        }
        path.moveTo(current.x, current.y);
    }

    /**
     * Standard Encoding Accented Character
     *
     * Makes an accented character from two other characters.
     * @param asb
     */
    private void seac(Number asb, Number adx, Number ady, Number bchar, Number achar)
    {
        // base character
        String baseName = StandardEncoding.INSTANCE.getName(bchar.intValue());
        try
        {
            Type1CharString base = font.getType1CharString(baseName);
            path.op(base.getPath(), Path.Op.UNION); // TODO: PdfBox-Android
        }
        catch (IOException e)
        {
            Log.w("PdfBox-Android", "invalid seac character in glyph " + glyphName + " of font " + fontName);
        }
        // accent character
        String accentName = StandardEncoding.INSTANCE.getName(achar.intValue());
        try
        {
            Type1CharString accent = font.getType1CharString(accentName);
            if (path == accent.getPath())
            {
                // PDFBOX-5339: avoid ArrayIndexOutOfBoundsException 
                // reproducable with poc file crash-4698e0dc7833a3f959d06707e01d03cda52a83f4
                Log.w("PdfBox-Android", "Path for " + baseName + " and for accent " + accentName + " are same, ignored");
                return;
            }
            AffineTransform at = AffineTransform.getTranslateInstance(
                leftSideBearing.x + adx.floatValue() - asb.floatValue(),
                leftSideBearing.y + ady.floatValue());
            path.op(accent.getPath(), Path.Op.UNION); // TODO: PdfBox-Android
        }
        catch (IOException e)
        {
            Log.w("PdfBox-Android", "invalid seac character in glyph " + glyphName + " of font " + fontName);
        }
    }

    @Override
    public String toString()
    {
        return type1Sequence.toString().replace("|","\n").replace(",", " ");
    }
}
