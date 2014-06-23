package org.apache.pdfboxandroid.util;

import org.apache.pdfboxandroid.cos.COSDictionary;

/**
 * This contains all of the image parameters for in inlined image.
 *
 * @author <a href="mailto:ben@benlitchfield.com">Ben Litchfield</a>
 * @version $Revision: 1.4 $
 */
public class ImageParameters {
	private COSDictionary dictionary;
	
	/**
     * Constructor.
     *
     * @param params The image parameters.
     */
    public ImageParameters( COSDictionary params )
    {
        dictionary = params;
    }
    
    /**
     * This will get the dictionary that stores the image parameters.
     *
     * @return The COS dictionary that stores the image parameters.
     */
    public COSDictionary getDictionary()
    {
        return dictionary;
    }
}
