package org.apache.pdfboxandroid.cos;

import org.apache.pdfboxandroid.exceptions.COSVisitorException;
import org.apache.pdfboxandroid.filter.FilterManager;
import org.apache.pdfboxandroid.pdmodel.common.COSObjectable;

public abstract class COSBase implements COSObjectable {
	private boolean needToBeUpdate;
	
	private boolean direct;
	
	/**
     * Convert this standard java object to a COS object.
     *
     * @return The cos object that matches this Java object.
     */
    public COSBase getCOSObject()
    {
        return this;
    }
    
    /**
     * Set the state true, if the dictionary should be written as a direct object and not indirect.
     * 
     * @param direct set it true, for writting direct object
     */
    public void setDirect(boolean direct)
    {
      this.direct = direct;
    }
    
    /**
     * This will get the filter manager to use to filter streams.
     *
     * @return The filter manager.
     */
    public FilterManager getFilterManager()
    {
        /**
         * @todo move this to PDFdocument or something better
         */
        return new FilterManager();
    }
    
    /**
     * visitor pattern double dispatch method.
     *
     * @param visitor The object to notify when visiting this object.
     * @return any object, depending on the visitor implementation, or null
     * @throws COSVisitorException If an error occurs while visiting this object.
     */
    public abstract Object accept(ICOSVisitor visitor) throws COSVisitorException;
    
    public boolean isNeedToBeUpdate() 
    {
      return needToBeUpdate;
    }
    
    /**
     * If the state is set true, the dictionary will be written direct into the called object. 
     * This means, no indirect object will be created.
     * 
     * @return the state
     */
    public boolean isDirect() 
    {
        return direct;
    }
}