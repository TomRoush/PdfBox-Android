package com.tom_roush.pdfbox.pdmodel.fdf;

import android.util.Log;

import com.tom_roush.pdfbox.cos.COSArray;
import com.tom_roush.pdfbox.cos.COSBase;
import com.tom_roush.pdfbox.cos.COSDictionary;
import com.tom_roush.pdfbox.cos.COSName;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

/**
 * This represents a Ink FDF annotation.
 *
 * @author Ben Litchfield
 */
public class FDFAnnotationInk extends FDFAnnotation
{
    /**
     * COS Model value for SubType entry.
     */
    public static final String SUBTYPE ="Ink";

    /**
     * Default constructor.
     */
    public FDFAnnotationInk()
    {
        super();
        annot.setName( COSName.SUBTYPE, SUBTYPE );
    }

    /**
     * Constructor.
     *
     * @param a An existing FDF Annotation.
     */
    public FDFAnnotationInk( COSDictionary a )
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
    public FDFAnnotationInk( Element element ) throws IOException
    {
        super(element);
        annot.setName(COSName.SUBTYPE, SUBTYPE);

        XPath xpath = XPathFactory.newInstance().newXPath();
        try
        {
            NodeList gestures = (NodeList) xpath.evaluate("inklist/gesture", element,
                XPathConstants.NODESET);
            if (gestures.getLength() == 0)
            {
                throw new IOException("Error: missing element 'gesture'");
            }
            List<float[]> inklist = new ArrayList<float[]>();
            for (int i = 0; i < gestures.getLength(); i++)
            {
                Node node = gestures.item(i);
                if (node instanceof Element)
                {
                    String gesture = node.getFirstChild().getNodeValue();
                    String[] gestureValues = gesture.split(",|;");
                    float[] values = new float[gestureValues.length];
                    for (int j = 0; j < gestureValues.length; j++)
                    {
                        values[j] = Float.parseFloat(gestureValues[j]);
                    }
                    inklist.add(values);
                }
            }
        }
        catch (XPathExpressionException e)
        {
            Log.d("PdfBox-Android", "Error while evaluating XPath expression for inklist gestures");
        }
    }

    /**
     * Set the paths making up the freehand "scribble".
     *
     * The ink annotation is made up of one ore more disjoint paths. Each array entry is an array
     * representing a stroked path, being a series of alternating horizontal and vertical coordinates
     * in default user space.
     *
     * @param inklist the List of arrays representing the paths.
     */
    public void setInkList(List<float[]> inklist)
    {
        COSArray newInklist = new COSArray();
        for (float[] array : inklist)
        {
            COSArray newArray = new COSArray();
            newArray.setFloatArray(array);
            newInklist.add(newArray);
        }
        annot.setItem(COSName.INKLIST, newInklist);
    }

    /**
     * Get the paths making up the freehand "scribble".
     *
     * @return the List of arrays representing the paths.
     * @see #setInkList(List)
     */
    public List<float[]> getInkList()
    {
        COSArray array = (COSArray) annot.getDictionaryObject(COSName.INKLIST);
        if (array != null)
        {
            List<float[]> retval = new ArrayList<float[]>();
            for (COSBase entry : array)
            {
                retval.add(((COSArray) entry).toFloatArray());
            }
            return retval;
        }
        else
        {
            return null; // Should never happen as this is a required item
        }
    }
}
