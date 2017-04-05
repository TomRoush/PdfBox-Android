package com.tom_roush.pdfbox.pdmodel.interactive.annotation;

import com.tom_roush.pdfbox.cos.COSDictionary;
import com.tom_roush.pdfbox.cos.COSName;
import com.tom_roush.pdfbox.pdmodel.common.filespecification.PDFileSpecification;

import java.io.IOException;

/**
 * This is the class that represents a file attachement.
 *
 * @author Ben Litchfield
 */
public class PDAnnotationFileAttachment extends PDAnnotationMarkup
{
    /**
     * See get/setAttachmentName.
     */
    public static final String ATTACHMENT_NAME_PUSH_PIN = "PushPin";
    /**
     * See get/setAttachmentName.
     */
    public static final String ATTACHMENT_NAME_GRAPH = "Graph";
    /**
     * See get/setAttachmentName.
     */
    public static final String ATTACHMENT_NAME_PAPERCLIP = "Paperclip";
    /**
     * See get/setAttachmentName.
     */
    public static final String ATTACHMENT_NAME_TAG = "Tag";

    /**
     * The type of annotation.
     */
    public static final String SUB_TYPE = "FileAttachment";

    /**
     * Constructor.
     */
    public PDAnnotationFileAttachment()
    {
        super();
        getCOSObject().setItem(COSName.SUBTYPE, COSName.getPDFName(SUB_TYPE));
    }

    /**
     * Creates a Link annotation from a COSDictionary, expected to be
     * a correct object definition.
     *
     * @param field the PDF objet to represent as a field.
     */
    public PDAnnotationFileAttachment(COSDictionary field)
    {
        super( field );
    }

    /**
     * Return the attached file.
     *
     * @return The attached file.
     *
     * @throws IOException If there is an error creating the file spec.
     */
    public PDFileSpecification getFile() throws IOException
    {
        return PDFileSpecification.createFS(getCOSObject().getDictionaryObject("FS"));
    }

    /**
     * Set the attached file.
     *
     * @param file The file that is attached.
     */
    public void setFile( PDFileSpecification file )
    {
        getCOSObject().setItem("FS", file);
    }

    /**
     * This is the name used to draw the type of attachment.
     * See the ATTACHMENT_NAME_XXX constants.
     *
     * @return The name that describes the visual cue for the attachment.
     */
    public String getAttachmentName()
    {
        return getCOSObject().getNameAsString("Name", ATTACHMENT_NAME_PUSH_PIN);
    }

    /**
     * Set the name used to draw the attachement icon.
     * See the ATTACHMENT_NAME_XXX constants.
     *
     * @param name The name of the visual icon to draw.
     */
    public void setAttachementName( String name )
    {
        getCOSObject().setName("Name", name);
    }
}
