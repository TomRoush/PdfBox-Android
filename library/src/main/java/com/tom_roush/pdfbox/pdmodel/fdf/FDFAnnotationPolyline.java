package com.tom_roush.pdfbox.pdmodel.fdf;

import android.util.Log;

import com.tom_roush.pdfbox.cos.COSArray;
import com.tom_roush.pdfbox.cos.COSDictionary;
import com.tom_roush.pdfbox.cos.COSName;
import com.tom_roush.pdfbox.pdmodel.interactive.annotation.PDAnnotationLine;
import com.tom_roush.harmony.awt.AWTColor;

import org.w3c.dom.Element;

import java.io.IOException;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

/**
 * This represents a Polyline FDF annotation.
 *
 * @author Ben Litchfield
 */
public class FDFAnnotationPolyline extends FDFAnnotation
{
    /**
     * COS Model value for SubType entry.
     */
    public static final String SUBTYPE ="Polyline";

    /**
     * Default constructor.
     */
    public FDFAnnotationPolyline()
    {
        super();
        annot.setName( COSName.SUBTYPE, SUBTYPE );
    }

    /**
     * Constructor.
     *
     * @param a An existing FDF Annotation.
     */
    public FDFAnnotationPolyline( COSDictionary a )
    {
        super( a );
    }

    /**
     * Constructor.
     *
     *  @param element An XFDF element.
     *
     *  @throws IOException If there is an error extracting information from the element.
     */
    public FDFAnnotationPolyline( Element element ) throws IOException
    {
        super(element);
        annot.setName(COSName.SUBTYPE, SUBTYPE);

        initVertices(element);
        initStyles(element);
    }

    private void initVertices(Element element) throws IOException, NumberFormatException
    {
        XPath xpath = XPathFactory.newInstance().newXPath();
        try
        {
            String vertices = xpath.evaluate("vertices[1]", element);
            if (vertices == null || vertices.isEmpty())
            {
                throw new IOException("Error: missing element 'vertices'");
            }
            String[] verticesValues = vertices.split(",");
            float[] values = new float[verticesValues.length];
            for (int i = 0; i < verticesValues.length; i++)
            {
                values[i] = Float.parseFloat(verticesValues[i]);
            }
            setVertices(values);
        }
        catch (XPathExpressionException e)
        {
            Log.d("PdfBox-Android",
                "Error while evaluating XPath expression for polyline vertices");
        }
    }

    private void initStyles(Element element) throws NumberFormatException
    {
        String startStyle = element.getAttribute("head");
        if (startStyle != null && !startStyle.isEmpty())
        {
            setStartPointEndingStyle(startStyle);
        }
        String endStyle = element.getAttribute("tail");
        if (endStyle != null && !endStyle.isEmpty())
        {
            setEndPointEndingStyle(endStyle);
        }

        String color = element.getAttribute("interior-color");
        if (color != null && color.length() == 7 && color.charAt(0) == '#')
        {
            int colorValue = Integer.parseInt(color.substring(1, 7), 16);
            setInteriorColor(new AWTColor(colorValue));
        }
    }

    /**
     * This will set the coordinates of the the vertices.
     *
     * @param vertices array of floats [x1, y1, x2, y2, ...] vertex coordinates in default user space.
     */
    public void setVertices(float[] vertices)
    {
        COSArray newVertices = new COSArray();
        newVertices.setFloatArray(vertices);
        annot.setItem(COSName.VERTICES, newVertices);
    }

    /**
     * This will get the coordinates of the the vertices.
     *
     * @return array of floats [x1, y1, x2, y2, ...] vertex coordinates in default user space.
     */
    public float[] getVertices()
    {
        COSArray array = (COSArray) annot.getDictionaryObject(COSName.VERTICES);
        if (array != null)
        {
            return array.toFloatArray();
        }
        else
        {
            return null; // Should never happen as this is a required item
        }
    }

    /**
     * This will set the line ending style for the start point, see the LE_ constants for the possible values.
     *
     * @param style The new style.
     */
    public void setStartPointEndingStyle(String style)
    {
        if (style == null)
        {
            style = PDAnnotationLine.LE_NONE;
        }
        COSArray array = (COSArray) annot.getDictionaryObject(COSName.LE);
        if (array == null)
        {
            array = new COSArray();
            array.add(COSName.getPDFName(style));
            array.add(COSName.getPDFName(PDAnnotationLine.LE_NONE));
            annot.setItem(COSName.LE, array);
        }
        else
        {
            array.setName(0, style);
        }
    }

    /**
     * This will retrieve the line ending style for the start point, possible values shown in the LE_ constants section.
     *
     * @return The ending style for the start point.
     */
    public String getStartPointEndingStyle()
    {
        String retval = PDAnnotationLine.LE_NONE;
        COSArray array = (COSArray) annot.getDictionaryObject(COSName.LE);
        if (array != null)
        {
            retval = array.getName(0);
        }

        return retval;
    }

    /**
     * This will set the line ending style for the end point, see the LE_ constants for the possible values.
     *
     * @param style The new style.
     */
    public void setEndPointEndingStyle(String style)
    {
        if (style == null)
        {
            style = PDAnnotationLine.LE_NONE;
        }
        COSArray array = (COSArray) annot.getDictionaryObject(COSName.LE);
        if (array == null)
        {
            array = new COSArray();
            array.add(COSName.getPDFName(PDAnnotationLine.LE_NONE));
            array.add(COSName.getPDFName(style));
            annot.setItem(COSName.LE, array);
        }
        else
        {
            array.setName(1, style);
        }
    }

    /**
     * This will retrieve the line ending style for the end point, possible values shown in the LE_ constants section.
     *
     * @return The ending style for the end point.
     */
    public String getEndPointEndingStyle()
    {
        String retval = PDAnnotationLine.LE_NONE;
        COSArray array = (COSArray) annot.getDictionaryObject(COSName.LE);
        if (array != null)
        {
            retval = array.getName(1);
        }

        return retval;
    }

    /**
     * This will set interior color of the line endings defined in the LE entry.
     *
     * @param color The interior color of the line endings.
     */
    public void setInteriorColor(AWTColor color)
    {
        COSArray array = null;
        if (color != null)
        {
            float[] colors = color.getRGBColorComponents(null);
            array = new COSArray();
            array.setFloatArray(colors);
        }
        annot.setItem(COSName.IC, array);
    }

    /**
     * This will retrieve the interior color of the line endings defined in the LE entry.
     *
     * @return object representing the color.
     */
    public AWTColor getInteriorColor()
    {
        AWTColor retval = null;
        COSArray array = (COSArray) annot.getDictionaryObject(COSName.IC);
        if (array != null)
        {
            float[] rgb = array.toFloatArray();
            if (rgb.length >= 3)
            {
                retval = new AWTColor(rgb[0], rgb[1], rgb[2]);
            }
        }
        return retval;
    }
}
