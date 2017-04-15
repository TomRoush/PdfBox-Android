package com.tom_roush.pdfbox.pdfwriter;

import com.tom_roush.pdfbox.contentstream.operator.Operator;
import com.tom_roush.pdfbox.cos.COSArray;
import com.tom_roush.pdfbox.cos.COSBase;
import com.tom_roush.pdfbox.cos.COSBoolean;
import com.tom_roush.pdfbox.cos.COSDictionary;
import com.tom_roush.pdfbox.cos.COSFloat;
import com.tom_roush.pdfbox.cos.COSInteger;
import com.tom_roush.pdfbox.cos.COSName;
import com.tom_roush.pdfbox.cos.COSString;
import com.tom_roush.pdfbox.util.Charsets;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

/**
 * A class that will take a list of tokens and write out a stream with them.
 *
 * @author Ben Litchfield
 */
public class ContentStreamWriter
{
    private final OutputStream output;
    /**
     * space character.
     */
    public static final byte[] SPACE = new byte[] { 32 };

    /**
     * standard line separator
     */
    public static final byte[] EOL = new byte[] { 0x0A };

    /**
     * This will create a new content stream writer.
     *
     * @param out The stream to write the data to.
     */
    public ContentStreamWriter( OutputStream out )
    {
        output = out;
    }

    /**
     * Writes a single operand token.
     *
     * @param base The operand to write to the stream.
     * @throws IOException If there is an error writing to the stream.
     */
    public void writeToken(COSBase base) throws IOException
    {
        writeObject(base);
    }

    /**
     * Writes a single operator token.
     *
     * @param op The operator to write to the stream.
     * @throws IOException If there is an error writing to the stream.
     */
    public void writeToken(Operator op) throws IOException
    {
        writeObject(op);
    }

    /**
     * Writes a series of tokens followed by a new line.
     *
     * @param tokens The tokens to write to the stream.
     * @throws IOException If there is an error writing to the stream.
     */
    public void writeTokens(Object... tokens) throws IOException
    {
        for (Object token : tokens)
        {
            writeObject(token);
        }
        output.write("\n".getBytes(Charsets.US_ASCII));
    }

    /**
     * This will write out the list of tokens to the stream.
     *
     * @param tokens The tokens to write to the stream.
     * @throws IOException If there is an error writing to the stream.
     */
    public void writeTokens(List tokens) throws IOException
    {
        for (Object token : tokens)
        {
            writeObject(token);
        }
    }

    private void writeObject( Object o ) throws IOException
    {
        if( o instanceof COSString )
        {
        	COSWriter.writeString((COSString)o, output);
            output.write(SPACE);
        }
        else if( o instanceof COSFloat )
        {
            ((COSFloat)o).writePDF( output );
            output.write(SPACE);
        }
        else if( o instanceof COSInteger )
        {
            ((COSInteger)o).writePDF( output );
            output.write(SPACE);
        }
        else if( o instanceof COSBoolean )
        {
            ((COSBoolean)o).writePDF( output );
            output.write(SPACE);
        }
        else if( o instanceof COSName )
        {
            ((COSName)o).writePDF( output );
            output.write(SPACE);
        }
        else if( o instanceof COSArray )
        {
            COSArray array = (COSArray)o;
            output.write(COSWriter.ARRAY_OPEN);
            for( int i=0; i<array.size(); i++ )
            {
                writeObject( array.get( i ) );
                output.write( SPACE );
            }

            output.write( COSWriter.ARRAY_CLOSE );
        }
        else if( o instanceof COSDictionary )
        {
            COSDictionary obj = (COSDictionary)o;
            output.write( COSWriter.DICT_OPEN );
            for (Map.Entry<COSName, COSBase> entry : obj.entrySet())
            {
                if (entry.getValue() != null)
                {
                    writeObject( entry.getKey() );
                    output.write( SPACE );
                    writeObject( entry.getValue() );
                    output.write( SPACE );
                }
            }
            output.write( COSWriter.DICT_CLOSE );
            output.write( SPACE );
        }
        else if( o instanceof Operator)
        {
            Operator op = (Operator)o;
            if( op.getName().equals( "BI" ) )
            {
                output.write( "BI".getBytes(Charsets.ISO_8859_1) );
                COSDictionary dic = op.getImageParameters();
                for( COSName key : dic.keySet() )
                {
                    Object value = dic.getDictionaryObject( key );
                    key.writePDF( output );
                    output.write( SPACE );
                    writeObject( value );
                    output.write( EOL );
                }
                output.write( "ID".getBytes(Charsets.ISO_8859_1) );
                output.write( EOL );
                output.write( op.getImageData() );
                output.write(EOL);
                output.write("EI".getBytes(Charsets.ISO_8859_1));
                output.write(EOL);
            }
            else
            {
                output.write( op.getName().getBytes(Charsets.ISO_8859_1) );
                output.write( EOL );
            }
        }
        else
        {
            throw new IOException( "Error:Unknown type in content stream:" + o );
        }
    }
}
