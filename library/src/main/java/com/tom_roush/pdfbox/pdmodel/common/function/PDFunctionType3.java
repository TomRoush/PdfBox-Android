package com.tom_roush.pdfbox.pdmodel.common.function;

import com.tom_roush.pdfbox.cos.COSArray;
import com.tom_roush.pdfbox.cos.COSBase;
import com.tom_roush.pdfbox.cos.COSName;
import com.tom_roush.pdfbox.pdmodel.common.PDRange;

import java.io.IOException;

/**
 * This class represents a Type 3 (stitching) function in a PDF document.
 *
 * @author Ben Litchfield
 */
public class PDFunctionType3 extends PDFunction
{

    private COSArray functions = null;
    private COSArray encode = null;
    private COSArray bounds = null;
    
    /**
     * Constructor.
     *
     * @param functionStream The function .
     */
    public PDFunctionType3(COSBase functionStream)
    {
        super( functionStream );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getFunctionType()
    {
        return 3;
    }
    
    /**
    * {@inheritDoc}
    */
    @Override
    public float[] eval(float[] input) throws IOException
    {
        //This function is known as a "stitching" function. Based on the input, it decides which child function to call.
        // All functions in the array are 1-value-input functions
        //See PDF Reference section 3.9.3.
        PDFunction function = null;
        float x = input[0];
        PDRange domain = getDomainForInput(0);
        // clip input value to domain
        x = clipToRange(x, domain.getMin(), domain.getMax());

        COSArray functionsArray = getFunctions();
        int numberOfFunctions = functionsArray.size();
        // This doesn't make sense but it may happen ...
        if (numberOfFunctions == 1) 
        {
            function = PDFunction.create(functionsArray.get(0));
            PDRange encRange = getEncodeForParameter(0);
            x = interpolate(x, domain.getMin(), domain.getMax(), encRange.getMin(), encRange.getMax());
        }
        else 
        {
            float[] boundsValues = getBounds().toFloatArray();
            int boundsSize = boundsValues.length;
            // create a combined array containing the domain and the bounds values
            // domain.min, bounds[0], bounds[1], ...., bounds[boundsSize-1], domain.max
            float[] partitionValues = new float[boundsSize+2];
            int partitionValuesSize = partitionValues.length;
            partitionValues[0] = domain.getMin();
            partitionValues[partitionValuesSize-1] = domain.getMax();
            System.arraycopy(boundsValues, 0, partitionValues, 1, boundsSize);
            // find the partition 
            for (int i=0; i < partitionValuesSize-1; i++)
            {
                if ( x >= partitionValues[i] && 
                        (x < partitionValues[i+1] || (i == partitionValuesSize - 2 && x == partitionValues[i+1])))
                {
                    function = PDFunction.create(functionsArray.get(i));
                    PDRange encRange = getEncodeForParameter(i);
                    x = interpolate(x, partitionValues[i], partitionValues[i+1], encRange.getMin(), encRange.getMax());
                    break;
                }
            }
        }
        if (function == null)
        {
        	throw new IOException("partition not found in type 3 function");
        }
        float[] functionValues = new float[]{x};
        // calculate the output values using the chosen function
        float[] functionResult = function.eval(functionValues);
        // clip to range if available
        return clipToRange(functionResult);
    }
    
    /**
     * Returns all functions values as COSArray.
     * 
     * @return the functions array. 
     */
    public COSArray getFunctions()
    {
        if (functions == null)
        {
            functions = (COSArray) (getCOSObject().getDictionaryObject(COSName.FUNCTIONS));
        }
        return functions;
    }
    
    /**
     * Returns all bounds values as COSArray.
     * 
     * @return the bounds array. 
     */
    public COSArray getBounds()
    {
        if (bounds == null) 
        {
            bounds = (COSArray) (getCOSObject().getDictionaryObject(COSName.BOUNDS));
        }
        return bounds;
    }
    
    /**
     * Returns all encode values as COSArray.
     * 
     * @return the encode array. 
     */
    public COSArray getEncode()
    {
        if (encode == null)
        {
            encode = (COSArray) (getCOSObject().getDictionaryObject(COSName.ENCODE));
        }
        return encode;
    }
    
    /**
     * Get the encode for the input parameter.
     *
     * @param n The function parameter number.
     *
     * @return The encode parameter range or null if none is set.
     */
    private PDRange getEncodeForParameter(int n) 
    {
        COSArray encodeValues = getEncode();
        return new PDRange( encodeValues, n );
    }

}
