package org.apache.pdfbox.pdmodel.interactive.annotation;

import org.apache.pdfbox.cos.COSStream;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.common.PDStream;
import org.apache.pdfbox.pdmodel.graphics.form.PDFormXObject;

/**
 * An appearance stream is a form XObject, a self-contained content stream that shall be rendered
 * inside the annotation rectangle.
 *
 * @author Ben Litchfield
 * @author John Hewson
 */
public class PDAppearanceStream extends PDFormXObject
{
    /**
     * Creates a Form XObject for reading.
     * @param stream The XObject stream
     */
    public PDAppearanceStream(COSStream stream)
    {
        super(new PDStream(stream));
    }

    /**
     * Creates a Form Image XObject for writing, in the given document.
     * @param document The current document
     */
    public PDAppearanceStream(PDDocument document)
    {
        super(document);
    }
}