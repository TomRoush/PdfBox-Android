package org.apache.pdfbox.pdmodel.fdf;

import java.io.IOException;

import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.w3c.dom.Element;

/**
 * This represents a Sound FDF annotation.
 *
 * @author Ben Litchfield
 */
public class FDFAnnotationSound extends FDFAnnotation
{
    /**
     * COS Model value for SubType entry.
     */
    public static final String SUBTYPE ="Sound";

    /**
     * Default constructor.
     */
    public FDFAnnotationSound()
    {
        super();
        annot.setName( COSName.SUBTYPE, SUBTYPE );
    }

    /**
     * Constructor.
     *
     * @param a An existing FDF Annotation.
     */
    public FDFAnnotationSound( COSDictionary a )
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
    public FDFAnnotationSound( Element element ) throws IOException
    {
        super( element );
        annot.setName( COSName.SUBTYPE, SUBTYPE );
    }
}
