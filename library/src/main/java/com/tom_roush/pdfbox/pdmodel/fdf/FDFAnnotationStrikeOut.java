package com.tom_roush.pdfbox.pdmodel.fdf;

import com.tom_roush.pdfbox.cos.COSDictionary;
import com.tom_roush.pdfbox.cos.COSName;

import org.w3c.dom.Element;

import java.io.IOException;

/**
 * This represents a StrikeOut FDF annotation.
 *
 * @author Ben Litchfield
 */
public class FDFAnnotationStrikeOut extends FDFAnnotationTextMarkup
{
    /**
     * COS Model value for SubType entry.
     */
    public static final String SUBTYPE ="StrikeOut";

    /**
     * Default constructor.
     */
    public FDFAnnotationStrikeOut()
    {
        super();
        annot.setName( COSName.SUBTYPE, SUBTYPE );
    }

    /**
     * Constructor.
     *
     * @param a An existing FDF Annotation.
     */
    public FDFAnnotationStrikeOut( COSDictionary a )
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
    public FDFAnnotationStrikeOut( Element element ) throws IOException
    {
        super( element );
        annot.setName( COSName.SUBTYPE, SUBTYPE );
    }
}
