package org.apache.pdfboxandroid.pdmodel.interactive.annotation;

import org.apache.pdfboxandroid.cos.COSArray;
import org.apache.pdfboxandroid.cos.COSBase;
import org.apache.pdfboxandroid.cos.COSDictionary;
import org.apache.pdfboxandroid.cos.COSName;
import org.apache.pdfboxandroid.cos.COSStream;
import org.apache.pdfboxandroid.pdmodel.PDResources;
import org.apache.pdfboxandroid.pdmodel.common.COSObjectable;
import org.apache.pdfboxandroid.pdmodel.common.PDRectangle;

/**
 * This class represents an appearance for an annotation.
 *
 * @author <a href="mailto:ben@benlitchfield.com">Ben Litchfield</a>
 * @version $Revision: 1.4 $
 */
public class PDAppearanceStream implements COSObjectable {
	private COSStream stream = null;
	
	/**
     * Constructor.
     *
     * @param s The cos stream for this appearance.
     */
    public PDAppearanceStream( COSStream s )
    {
        stream = s;
    }
	
	/**
     * {@inheritDoc}
     */
    public COSBase getCOSObject()
    {
        return stream;
    }
    
    /**
     * This will set the bounding box for this appearance stream.
     *
     * @param rectangle The new bounding box.
     */
    public void setBoundingBox( PDRectangle rectangle )
    {
        COSArray array = null;
        if( rectangle != null )
        {
            array = rectangle.getCOSArray();
        }
        stream.setItem( COSName.BBOX, array );
    }
    
    /**
     * This will return the underlying stream.
     *
     * @return The wrapped stream.
     */
    public COSStream getStream()
    {
        return stream;
    }
    
    /**
     * This will get the resources for this appearance stream.
     *
     * @return The appearance stream resources.
     */
    public PDResources getResources()
    {
        PDResources retval = null;
        COSDictionary dict = (COSDictionary)stream.getDictionaryObject( COSName.RESOURCES );
        if( dict != null )
        {
            retval = new PDResources( dict );
        }
        return retval;
    }
    
    /**
     * This will set the new resources.
     *
     * @param resources The new resources.
     */
    public void setResources( PDResources resources )
    {
        COSDictionary dict = null;
        if( resources != null )
        {
            dict = resources.getCOSDictionary();
        }
        stream.setItem( COSName.RESOURCES, dict );
    }
    
    /**
     * Get the bounding box for this appearance.  This may return null in which
     * case the Rectangle from the annotation should be used.
     *
     * @return The bounding box for this appearance.
     */
    public PDRectangle getBoundingBox()
    {
        PDRectangle box = null;
        COSArray bbox = (COSArray)stream.getDictionaryObject( COSName.BBOX );
        if( bbox != null )
        {
            box = new PDRectangle( bbox );
        }
        return box;
    }
}
