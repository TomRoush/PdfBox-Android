package com.tom_roush.pdfbox.pdmodel.fdf;

import android.util.Log;

import com.tom_roush.pdfbox.cos.COSArray;
import com.tom_roush.pdfbox.cos.COSDictionary;
import com.tom_roush.pdfbox.cos.COSName;
import com.tom_roush.harmony.awt.AWTColor;

import org.w3c.dom.Element;

import java.io.IOException;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

/**
 * This represents a Polygon FDF annotation.
 *
 * @author Ben Litchfield
 */
public class FDFAnnotationPolygon extends FDFAnnotation
{
    /**
     * COS Model value for SubType entry.
     */
    public static final String SUBTYPE ="Polygon";

    /**
     * Default constructor.
     */
    public FDFAnnotationPolygon()
    {
        super();
        annot.setName( COSName.SUBTYPE, SUBTYPE );
    }

    /**
     * Constructor.
     *
     * @param a An existing FDF Annotation.
     */
    public FDFAnnotationPolygon( COSDictionary a )
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
    public FDFAnnotationPolygon( Element element ) throws IOException
    {
        super(element);
        annot.setName(COSName.SUBTYPE, SUBTYPE);

        initVertices(element);
        String color = element.getAttribute("interior-color");
        if (color != null && color.length() == 7 && color.charAt(0) == '#')
        {
            int colorValue = Integer.parseInt(color.substring(1, 7), 16);
            setInteriorColor(new AWTColor(colorValue));
        }
    }

    private void initVertices(Element element) throws IOException, NumberFormatException
    {
        XPath xpath = XPathFactory.newInstance().newXPath();
        try
        {
            String vertices = xpath.evaluate("vertices", element);
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
            Log.d("PdfBox-Android", "Error while evaluating XPath expression for polygon vertices");
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
     * This will set interior color of the drawn area.
     *
     * @param color The interior color of the drawn area.
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
     * This will get interior color of the drawn area.
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
