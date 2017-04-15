package com.tom_roush.pdfbox.pdmodel.fdf;

import com.tom_roush.pdfbox.cos.COSDictionary;
import com.tom_roush.pdfbox.cos.COSName;

import org.w3c.dom.Element;

import java.io.IOException;

/**
 * This represents a FreeText FDF annotation.
 *
 * @author Ben Litchfield
 */
public class FDFAnnotationFreeText extends FDFAnnotation
{
    /**
     * COS Model value for SubType entry.
     */
    public static final String SUBTYPE ="FreeText";

    /**
     * Default constructor.
     */
    public FDFAnnotationFreeText()
    {
        super();
        annot.setName( COSName.SUBTYPE, SUBTYPE );
    }

    /**
     * Constructor.
     *
     * @param a An existing FDF Annotation.
     */
    public FDFAnnotationFreeText( COSDictionary a )
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
    public FDFAnnotationFreeText( Element element ) throws IOException
    {
        super(element);
        annot.setName(COSName.SUBTYPE, SUBTYPE);

        setJustification(element.getAttribute("justification"));
        String rotation = element.getAttribute("rotation");
        if (rotation != null && !rotation.isEmpty())
        {
            setRotation(Integer.parseInt(rotation));
        }
    }

    /**
     * This will set the form of quadding (justification) of the annotation text.
     *
     * @param justification The quadding of the text.
     */
    public void setJustification(String justification)
    {
        int quadding = 0;
        if ("centered".equals(justification))
        {
            quadding = 1;
        }
        else if ("right".equals(justification))
        {
            quadding = 2;
        }
        annot.setInt(COSName.Q, quadding);
    }

    /**
     * This will get the form of quadding (justification) of the annotation text.
     *
     * @return The quadding of the text.
     */
    public String getJustification()
    {
        return "" + annot.getInt(COSName.Q, 0);
    }

    /**
     * This will set the clockwise rotation in degrees.
     *
     * @param rotation The number of degrees of clockwise rotation.
     */
    public void setRotation(int rotation)
    {
        annot.setInt(COSName.ROTATE, rotation);
    }

    /**
     * This will get the clockwise rotation in degrees.
     *
     * @return The number of degrees of clockwise rotation.
     */
    public String getRotation()
    {
        return annot.getString(COSName.ROTATE);
    }
}
