package org.apache.pdfbox.pdmodel.fdf;

import java.io.IOException;

import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.w3c.dom.Element;

/**
 * This represents a Line FDF annotation.
 *
 * @author Ben Litchfield
 */
public class FDFAnnotationLine extends FDFAnnotation
{
    /**
     * COS Model value for SubType entry.
     */
    public static final String SUBTYPE ="Line";

    /**
     * Default constructor.
     */
    public FDFAnnotationLine()
    {
        super();
        annot.setName( COSName.SUBTYPE, SUBTYPE );
    }

    /**
     * Constructor.
     *
     * @param a An existing FDF Annotation.
     */
    public FDFAnnotationLine( COSDictionary a )
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
    public FDFAnnotationLine( Element element ) throws IOException
    {
        super( element );
        annot.setName( COSName.SUBTYPE, SUBTYPE );
    }
}
