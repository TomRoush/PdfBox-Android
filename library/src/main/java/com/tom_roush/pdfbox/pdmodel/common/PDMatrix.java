package com.tom_roush.pdfbox.pdmodel.common;

import com.tom_roush.pdfbox.cos.COSArray;
import com.tom_roush.pdfbox.cos.COSBase;
import com.tom_roush.pdfbox.cos.COSFloat;
import com.tom_roush.pdfbox.cos.COSNumber;

/**
 * This class will be used for matrix manipulation.
 *
 * @author <a href="mailto:ben@benlitchfield.com">Ben Litchfield</a>
 * @version $Revision: 1.3 $
 */
public class PDMatrix implements COSObjectable
{
    private COSArray matrix;
    // the number of row elements depends on the number of elements
    // within the given matrix
    // 3x3 e.g. Matrix of a CalRGB colorspace dictionary
    // 3x2 e.g. FontMatrix of a type 3 font
    private int numberOfRowElements = 3;
    
    /**
     * Constructor.
     */
    public PDMatrix()
    {
        matrix = new COSArray();
        matrix.add( new COSFloat( 1.0f ) );
        matrix.add( new COSFloat( 0.0f ) );
        matrix.add( new COSFloat( 0.0f ) );
        matrix.add( new COSFloat( 0.0f ) );
        matrix.add( new COSFloat( 1.0f ) );
        matrix.add( new COSFloat( 0.0f ) );
        matrix.add( new COSFloat( 0.0f ) );
        matrix.add( new COSFloat( 0.0f ) );
        matrix.add( new COSFloat( 1.0f ) );
    }

    /**
     * Constructor.
     *
     * @param array The array that describes the matrix.
     */
    public PDMatrix( COSArray array )
    {
        if ( array.size() == 6) 
        {
            numberOfRowElements = 2;
        }
        matrix = array;
    }

    /**
     * This will get the underlying array value.
     *
     * @return The cos object that this object wraps.
     */
    public COSArray getCOSArray()
    {
        return matrix;
    }

    /**
     * Convert this standard java object to a COS object.
     *
     * @return The cos object that matches this Java object.
     */
    public COSBase getCOSObject()
    {
        return matrix;
    }


    /**
     * This will get a matrix value at some point.
     *
     * @param row The row to get the value from.
     * @param column The column to get the value from.
     *
     * @return The value at the row/column position.
     */
    public float getValue( int row, int column )
    {
        return ((COSNumber)matrix.get( row*numberOfRowElements + column )).floatValue();
    }

    /**
     * This will set a value at a position.
     *
     * @param row The row to set the value at.
     * @param column the column to set the value at.
     * @param value The value to set at the position.
     */
    public void setValue( int row, int column, float value )
    {
        matrix.set( row*numberOfRowElements+column, new COSFloat( value ) );
    }
}
