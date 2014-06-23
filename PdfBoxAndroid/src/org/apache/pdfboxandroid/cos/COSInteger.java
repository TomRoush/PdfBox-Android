package org.apache.pdfboxandroid.cos;

import java.io.IOException;
import java.io.OutputStream;

import org.apache.pdfboxandroid.exceptions.COSVisitorException;

public class COSInteger extends COSNumber {
	/**
     * The lowest integer to be kept in the {@link #STATIC} array.
     */
    private static int LOW = -100;

    /**
     * The highest integer to be kept in the {@link #STATIC} array.
     */
    private static int HIGH = 256;
    
    /**
     * Static instances of all COSIntegers in the range from {@link #LOW}
     * to {@link #HIGH}.
     */
    private static final COSInteger[] STATIC = new COSInteger[HIGH - LOW + 1];
    
    /**
     * Constant for the number zero.
     * @since Apache PDFBox 1.1.0
     */
    public static final COSInteger ZERO = get(0); 
	
	private long value;
	
	/**
     * Polymorphic access to value as int
     * This will get the integer value of this object.
     *
     * @return The int value of this object,
     */
    public long longValue()
    {
        return value;
    }
    
    /**
     * Returns a COSInteger instance with the given value.
     *
     * @param val integer value
     * @return COSInteger instance
     */
    public static COSInteger get(long val) {
        if (LOW <= val && val <= HIGH) {
            int index = (int) val - LOW;
            // no synchronization needed
            if (STATIC[index] == null) {
                STATIC[index] = new COSInteger(val);
            }
            return STATIC[index];
        } else {
            return new COSInteger(val);
        }
    }
    
    /**
     * constructor.
     *
     * @deprecated use the static {@link #get(long)} method instead
     * @param val The integer value of this object.
     */
    public COSInteger( long val )
    {
        value = val;
    }
    
    /**
     * Polymorphic access to value as int
     * This will get the integer value of this object.
     *
     * @return The int value of this object,
     */
    public int intValue()
    {
        return (int)value;
    }
    
    /**
     * polymorphic access to value as float.
     *
     * @return The float value of this object.
     */
    public float floatValue()
    {
        return value;
    }
    
    /**
     * This will output this string as a PDF object.
     *
     * @param output The stream to write to.
     * @throws IOException If there is an error writing to the stream.
     */
    public void writePDF( OutputStream output ) throws IOException
    {
        output.write(String.valueOf(value).getBytes("ISO-8859-1"));
    }

    /**
     * visitor pattern double dispatch method.
     *
     * @param visitor The object to notify when visiting this object.
     * @return any object, depending on the visitor implementation, or null
     * @throws COSVisitorException If an error occurs while visiting this object.
     */
    public Object accept(ICOSVisitor visitor) throws COSVisitorException
    {
        return visitor.visitFromInt(this);
    }
}
