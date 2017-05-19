package com.tom_roush.pdfbox.pdmodel.fdf;

import com.tom_roush.pdfbox.cos.COSArray;
import com.tom_roush.pdfbox.cos.COSDictionary;
import com.tom_roush.pdfbox.cos.COSName;
import com.tom_roush.pdfbox.pdmodel.common.PDRectangle;
import com.tom_roush.harmony.awt.AWTColor;

import org.w3c.dom.Element;

import java.io.IOException;

/**
 * This represents a Square FDF annotation.
 *
 * @author Ben Litchfield
 * @author Johanneke Lamberink
 */
public class FDFAnnotationSquare extends FDFAnnotation
{
    /**
     * COS Model value for SubType entry.
     */
    public static final String SUBTYPE ="Square";

    /**
     * Default constructor.
     */
    public FDFAnnotationSquare()
    {
        super();
        annot.setName( COSName.SUBTYPE, SUBTYPE );
    }

    /**
     * Constructor.
     *
     * @param a An existing FDF Annotation.
     */
    public FDFAnnotationSquare( COSDictionary a )
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
    public FDFAnnotationSquare( Element element ) throws IOException
    {
        super(element);
        annot.setName(COSName.SUBTYPE, SUBTYPE);

        String color = element.getAttribute("interior-color");
        if (color != null && color.length() == 7 && color.charAt(0) == '#')
        {
            int colorValue = Integer.parseInt(color.substring(1, 7), 16);
            setInteriorColor(new AWTColor(colorValue));
        }

        String fringe = element.getAttribute("fringe");
        if (fringe != null && !fringe.isEmpty())
        {
            String[] fringeValues = fringe.split(",");
            if (fringeValues.length != 4)
            {
                throw new IOException("Error: wrong amount of numbers in attribute 'fringe'");
            }
            float[] values = new float[4];
            for (int i = 0; i < 4; i++)
            {
                values[i] = Float.parseFloat(fringeValues[i]);
            }
            COSArray array = new COSArray();
            array.setFloatArray(values);
            setFringe(new PDRectangle(array));
        }
    }

    /**
     * This will set interior color of the drawn area.
     *
     * @param color The interior color of the circle.
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
     * This will retrieve the interior color of the drawn area.
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

    /**
     * This will set the fringe rectangle. Giving the difference between the annotations rectangle and where the drawing
     * occurs. (To take account of any effects applied through the BE entry for example)
     *
     * @param fringe the fringe
     */
    public void setFringe(PDRectangle fringe)
    {
        annot.setItem(COSName.RD, fringe);
    }

    /**
     * This will get the fringe. Giving the difference between the annotations rectangle and where the drawing occurs.
     * (To take account of any effects applied through the BE entry for example)
     *
     * @return the rectangle difference
     */
    public PDRectangle getFringe()
    {
        COSArray rd = (COSArray) annot.getDictionaryObject(COSName.RD);
        if (rd != null)
        {
            return new PDRectangle(rd);
        }
        else
        {
            return null;
        }
    }
}
