package com.tom_roush.pdfbox.pdmodel.fdf;

import java.io.IOException;

import com.tom_roush.pdfbox.cos.COSDictionary;
import com.tom_roush.pdfbox.cos.COSName;
import org.w3c.dom.Element;

/**
 * This represents a Squiggly FDF annotation.
 *
 * @author Ben Litchfield
 */
public class FDFAnnotationSquiggly extends FDFAnnotation
{
    /**
     * COS Model value for SubType entry.
     */
    public static final String SUBTYPE ="Squiggly";

    /**
     * Default constructor.
     */
    public FDFAnnotationSquiggly()
    {
        super();
        annot.setName( COSName.SUBTYPE, SUBTYPE );
    }

    /**
     * Constructor.
     *
     * @param a An existing FDF Annotation.
     */
    public FDFAnnotationSquiggly( COSDictionary a )
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
    public FDFAnnotationSquiggly( Element element ) throws IOException
    {
        super( element );
        annot.setName( COSName.SUBTYPE, SUBTYPE );
    }
}
