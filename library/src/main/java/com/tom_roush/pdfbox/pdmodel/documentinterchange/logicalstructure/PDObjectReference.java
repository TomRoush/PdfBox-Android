package org.apache.pdfbox.pdmodel.documentinterchange.logicalstructure;

import java.io.IOException;

import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.common.COSObjectable;
import org.apache.pdfbox.pdmodel.graphics.PDXObject;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotation;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationUnknown;

/**
 * An object reference.
 * 
 * @author Ben Litchfield
 */
public class PDObjectReference implements COSObjectable
{

    /**
     * TYPE of this object.
     */
    public static final String TYPE = "OBJR";

    private COSDictionary dictionary;

    /**
     * Returns the underlying dictionary.
     * 
     * @return the dictionary
     */
    protected COSDictionary getCOSDictionary()
    {
        return this.dictionary;
    }

    /**
     * Default Constructor.
     *
     */
    public PDObjectReference()
    {
        this.dictionary = new COSDictionary();
        this.dictionary.setName(COSName.TYPE, TYPE);
    }

    /**
     * Constructor for an existing object reference.
     *
     * @param theDictionary The existing dictionary.
     */
    public PDObjectReference(COSDictionary theDictionary)
    {
        dictionary = theDictionary;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public COSBase getCOSObject()
    {
        return this.dictionary;
    }

    /**
     * Gets a higher-level object for the referenced object.
     * Currently this method may return a {@link PDAnnotation},
     * a {@link PDXObject} or <code>null</code>.
     * 
     * @return a higher-level object for the referenced object
     */
    public COSObjectable getReferencedObject()
    {
        COSBase obj = this.getCOSDictionary().getDictionaryObject(COSName.OBJ);
        if (!(obj instanceof COSDictionary))
        {
            return null;
        }
        try
        {
            PDXObject xobject = PDXObject.createXObject(obj, null, null); // <-- TODO: valid?
            if (xobject != null)
            {
                return xobject;
            }
            COSDictionary objDictionary  = (COSDictionary)obj;
            PDAnnotation annotation = PDAnnotation.createAnnotation(obj);
            /*
             * COSName.TYPE is optional, so if annotation is of type unknown and
             * COSName.TYPE is not COSName.ANNOT it still may be an annotation.
             * TODO shall we return the annotation object instead of null?
             * what else can be the target of the object reference?
             */
            if (!(annotation instanceof PDAnnotationUnknown) 
                    || COSName.ANNOT.equals(objDictionary.getDictionaryObject(COSName.TYPE))) 
            {
                return annotation;
            }
        }
        catch (IOException exception)
        {
            // this can only happen if the target is an XObject.
        }
        return null;
    }

    /**
     * Sets the referenced annotation.
     * 
     * @param annotation the referenced annotation
     */
    public void setReferencedObject(PDAnnotation annotation)
    {
        this.getCOSDictionary().setItem(COSName.OBJ, annotation);
    }

    /**
     * Sets the referenced XObject.
     * 
     * @param xobject the referenced XObject
     */
    public void setReferencedObject(PDXObject xobject)
    {
        this.getCOSDictionary().setItem(COSName.OBJ, xobject);
    }

}
