package org.apache.pdfbox.cos;

import org.apache.pdfbox.pdmodel.common.COSObjectable;

import java.io.IOException;

/**
 * The base object that all objects in the PDF document will extend.
 *
 * @author Ben Litchfield
 */
public abstract class COSBase implements COSObjectable
{
    private boolean direct;

    /**
     * Constructor.
     */
    public COSBase()
    {
    }

    /**
     * Convert this standard java object to a COS object.
     *
     * @return The cos object that matches this Java object.
     */
    @Override
    public COSBase getCOSObject()
    {
        return this;
    }

    /**
     * visitor pattern double dispatch method.
     *
     * @param visitor The object to notify when visiting this object.
     * @return any object, depending on the visitor implementation, or null
     * @throws IOException If an error occurs while visiting this object.
     */
    public abstract Object accept(ICOSVisitor visitor) throws IOException;
    
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
    
    /**
     * Set the state true, if the dictionary should be written as a direct object and not indirect.
     * 
     * @param direct set it true, for writting direct object
     */
    public void setDirect(boolean direct)
    {
      this.direct = direct;
    }
}
