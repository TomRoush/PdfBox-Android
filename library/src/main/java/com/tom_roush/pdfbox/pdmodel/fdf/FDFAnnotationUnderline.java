package com.tom_roush.pdfbox.pdmodel.fdf;

import com.tom_roush.pdfbox.cos.COSDictionary;
import com.tom_roush.pdfbox.cos.COSName;

import org.w3c.dom.Element;

import java.io.IOException;

/**
 * This represents a Underline FDF annotation.
 *
 * @author Ben Litchfield
 */
public class FDFAnnotationUnderline extends FDFAnnotationTextMarkup
{
    /**
     * COS Model value for SubType entry.
     */
    public static final String SUBTYPE ="Underline";

    /**
     * Default constructor.
     */
    public FDFAnnotationUnderline()
    {
        super();
        annot.setName( COSName.SUBTYPE, SUBTYPE );
    }

    /**
     * Constructor.
     *
     * @param a An existing FDF Annotation.
     */
    public FDFAnnotationUnderline( COSDictionary a )
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
    public FDFAnnotationUnderline( Element element ) throws IOException
    {
        super( element );
        annot.setName( COSName.SUBTYPE, SUBTYPE );
    }
}
