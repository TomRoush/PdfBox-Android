package org.apache.pdfboxandroid.cos;

import java.io.IOException;

public abstract class COSNumber extends COSBase {
	
	
	/**
     * This will get the long value of this number.
     *
     * @return The long value of this number.
     */
    public abstract long longValue();
    
    /**
     * This will get the integer value of this number.
     *
     * @return The integer value of this number.
     */
    public abstract int intValue();
    
    /**
     * This factory method will get the appropriate number object.
     *
     * @param number The string representation of the number.
     *
     * @return A number object, either float or int.
     *
     * @throws IOException If the string is not a number.
     */
    public static COSNumber get( String number ) throws IOException
    {
        if (number.length() == 1) 
        {
            char digit = number.charAt(0);
            if ('0' <= digit && digit <= '9') 
            {
                return COSInteger.get(digit - '0');
            } 
            else if (digit == '-' || digit == '.') 
            {
                // See https://issues.apache.org/jira/browse/PDFBOX-592
                return COSInteger.ZERO;
            } 
            else 
            {
                throw new IOException("Not a number: " + number);
            }
        } 
        else if (number.indexOf('.') == -1 && (number.toLowerCase().indexOf('e') == -1)) 
        {
            try
            {
                return COSInteger.get( Long.parseLong( number ) );
            }
            catch( NumberFormatException e )
            {
                throw new IOException( "Value is not an integer: " + number );
            }
        } 
        else 
        {
            return new COSFloat(number);
        }
    }
    
    /**
     * This will get the float value of this number.
     *
     * @return The float value of this object.
     */
    public abstract float floatValue();
}
