package org.apache.pdfbox.pdfparser;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSBoolean;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSNull;
import org.apache.pdfbox.cos.COSNumber;
import org.apache.pdfbox.cos.COSObject;
import org.apache.pdfbox.cos.COSStream;
import org.apache.pdfbox.io.RandomAccess;
import org.apache.pdfbox.util.ImageParameters;
import org.apache.pdfbox.util.PDFOperator;

/**
 * This will parse a PDF byte stream and extract operands and such.
 *
 * @author <a href="mailto:ben@benlitchfield.com">Ben Litchfield</a>
 * @version $Revision: 1.32 $
 */
public class PDFStreamParser extends BaseParser
{
	
	private List<Object> streamObjects = new ArrayList<Object>( 100 );
    private final RandomAccess file;
    private final int    maxBinCharTestLength = 5;
    private final byte[] binCharTestArr = new byte[maxBinCharTestLength];

	/**
     * Constructor that takes a stream to parse.
     *
     * @since Apache PDFBox 1.3.0
     * @param stream The stream to read data from.
     * @param raf The random access file.
     * @param forceParsing flag to skip malformed or otherwise unparseable
     *                     input where possible
     * @throws IOException If there is an error reading from the stream.
     */
    public PDFStreamParser(
            InputStream stream, RandomAccess raf, boolean forceParsing)
            throws IOException 
    {
        super(stream, forceParsing);
        file = raf;
    }
    
    /**
     * Constructor that takes a stream to parse.
     *
     * @param stream The stream to read data from.
     * @param raf The random access file.
     *
     * @throws IOException If there is an error reading from the stream.
     */
    public PDFStreamParser(InputStream stream, RandomAccess raf)
            throws IOException 
    {
        this(stream, raf, FORCE_PARSING);
    }
    
    /**
     * Constructor.
     *
     * @param stream The stream to parse.
     *
     * @throws IOException If there is an error initializing the stream.
     */
    public PDFStreamParser( COSStream stream ) throws IOException
    {
       this( stream.getUnfilteredStream(), stream.getScratchFile() );
    }
    
    /**
     * This will parse the tokens in the stream.  This will close the
     * stream when it is finished parsing.
     *
     * @throws IOException If there is an error while parsing the stream.
     */
    public void parse() throws IOException
    {
        try
        {
            Object token = null;
            while( (token = parseNextToken()) != null )
            {
                streamObjects.add( token );
                //logger().fine( "parsed=" + token );
            }
        }
        finally
        {
            pdfSource.close();
        }
    }
    
    /**
     * This will parse the next token in the stream.
     *
     * @return The next token in the stream or null if there are no more tokens in the stream.
     *
     * @throws IOException If an io error occurs while parsing the stream.
     */
    private Object parseNextToken() throws IOException
    {
        Object retval = null;

        skipSpaces();
        int nextByte = pdfSource.peek();
        if( ((byte)nextByte) == -1 )
        {
            return null;
        }
        char c = (char)nextByte;
        switch(c)
        {
            case '<':
            {
                int leftBracket = pdfSource.read();//pull off first left bracket
                c = (char)pdfSource.peek(); //check for second left bracket
                pdfSource.unread( leftBracket ); //put back first bracket
                if(c == '<')
                {

                    COSDictionary pod = parseCOSDictionary();
                    skipSpaces();
                    if((char)pdfSource.peek() == 's')
                    {
                        retval = parseCOSStream( pod, file );
                    }
                    else
                    {
                        retval = pod;
                    }
                }
                else
                {
                    retval = parseCOSString();
                }
                break;
            }
            case '[': // array
            {
                retval = parseCOSArray();
                break;
            }
            case '(': // string
                retval = parseCOSString();
                break;
            case '/':   // name
                retval = parseCOSName();
                break;
            case 'n':   // null
            {
                String nullString = readString();
                if( nullString.equals( "null") )
                {
                    retval = COSNull.NULL;
                }
                else
                {
                    retval = PDFOperator.getOperator( nullString );
                }
                break;
            }
            case 't':
            case 'f':
            {
                String next = readString();
                if( next.equals( "true" ) )
                {
                    retval = COSBoolean.TRUE;
                    break;
                }
                else if( next.equals( "false" ) )
                {
                    retval = COSBoolean.FALSE;
                }
                else
                {
                    retval = PDFOperator.getOperator( next );
                }
                break;
            }
            case 'R':
            {
                String line = readString();
                if( line.equals( "R" ) )
                {
                    retval = new COSObject( null );
                }
                else
                {
                    retval = PDFOperator.getOperator( line );
                }
                break;
            }
            case '0':
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
            case '8':
            case '9':
            case '-':
            case '+':
            case '.':
            {
                /* We will be filling buf with the rest of the number.  Only
                 * allow 1 "." and "-" and "+" at start of number. */
                StringBuffer buf = new StringBuffer();
                buf.append( c );
                pdfSource.read();

                boolean dotNotRead = (c != '.');
                while( Character.isDigit(( c = (char)pdfSource.peek()) ) || (dotNotRead && (c == '.')) )
                {
                    buf.append( c );
                    pdfSource.read();

                    if (dotNotRead && (c == '.'))
                    {
                        dotNotRead = false;
                    }
                }
                retval = COSNumber.get( buf.toString() );
                break;
            }
            case 'B':
            {
                String next = readString();
                retval = PDFOperator.getOperator( next );
                if( next.equals( "BI" ) )
                {
                    PDFOperator beginImageOP = (PDFOperator)retval;
                    COSDictionary imageParams = new COSDictionary();
                    beginImageOP.setImageParameters( new ImageParameters( imageParams ) );
                    Object nextToken = null;
                    while( (nextToken = parseNextToken()) instanceof COSName )
                    {
                        Object value = parseNextToken();
                        imageParams.setItem( (COSName)nextToken, (COSBase)value );
                    }
                    //final token will be the image data, maybe??
                    PDFOperator imageData = (PDFOperator)nextToken;
                    beginImageOP.setImageData( imageData.getImageData() );
                }
                break;
            }
            case 'I':
            {
                //Special case for ID operator
                String id = "" + (char)pdfSource.read() + (char)pdfSource.read();
                if( !id.equals( "ID" ) )
                {
                    throw new IOException( "Error: Expected operator 'ID' actual='" + id + "'" );
                }
                ByteArrayOutputStream imageData = new ByteArrayOutputStream();
                if( isWhitespace() )
                {
                    //pull off the whitespace character
                    pdfSource.read();
                }
                int lastByte = pdfSource.read();
                int currentByte = pdfSource.read();
                // PDF spec is kinda unclear about this. Should a whitespace
                // always appear before EI? Not sure, so that we just read
                // until EI<whitespace>.
                // Be aware not all kind of whitespaces are allowed here. see PDFBOX1561
                while( !(lastByte == 'E' &&
                         currentByte == 'I' &&
                         isSpaceOrReturn() &&
                         hasNoFollowingBinData( pdfSource )) &&
                       !pdfSource.isEOF() )
                {
                    imageData.write( lastByte );
                    lastByte = currentByte;
                    currentByte = pdfSource.read();
                }
                // the EI operator isn't unread, as it won't be processed anyway
                retval = PDFOperator.getOperator( "ID" );
                // save the image data to the operator, so that it can be accessed it later
                ((PDFOperator)retval).setImageData( imageData.toByteArray() );
                break;
            }
            case ']':
            {
                // some ']' around without its previous '['
                // this means a PDF is somewhat corrupt but we will continue to parse.
                pdfSource.read();
                retval = COSNull.NULL;  // must be a better solution than null...
                break;
            }
            default:
            {
                //we must be an operator
                String operator = readOperator();
                if( operator.trim().length() == 0 )
                {
                    //we have a corrupt stream, stop reading here
                    retval = null;
                }
                else
                {
                    retval = PDFOperator.getOperator( operator );
                }
            }

        }

        return retval;
    }

}
