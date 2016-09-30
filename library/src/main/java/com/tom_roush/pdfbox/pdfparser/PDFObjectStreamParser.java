package org.apache.pdfbox.pdfparser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDocument;
import org.apache.pdfbox.cos.COSObject;
import org.apache.pdfbox.cos.COSStream;

import android.util.Log;

/**
 * This will parse a PDF 1.5 object stream and extract all of the objects from the stream.
 *
 * @author Ben Litchfield
 */
public class PDFObjectStreamParser extends BaseParser
{
    private List<COSObject> streamObjects = null;
    private final COSStream stream;

    /**
     * Constructor.
     *
     * @param strm The stream to parse.
     * @param doc The document for the current parsing.
     * @throws IOException If there is an error initializing the stream.
     */
    public PDFObjectStreamParser(COSStream strm, COSDocument doc) throws IOException
    {
        super(strm.getUnfilteredStream());
        document = doc;
        stream = strm;
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
            //need to first parse the header.
            int numberOfObjects = stream.getInt( "N" );
            List<Long> objectNumbers = new ArrayList<Long>( numberOfObjects );
            streamObjects = new ArrayList<COSObject>( numberOfObjects );
            for( int i=0; i<numberOfObjects; i++ )
            {
                long objectNumber = readObjectNumber();
             // skip offset
                readLong();
                objectNumbers.add( objectNumber);
            }
            COSObject object;
            COSBase cosObject;
            int objectCounter = 0;
            while( (cosObject = parseDirObject()) != null )
            {
                object = new COSObject(cosObject);
                object.setGenerationNumber(0);
                if (objectCounter >= objectNumbers.size())
                {
                	Log.e("PdfBoxAndroid", "/ObjStm (object stream) has more objects than /N " + numberOfObjects);
                    break;
                }
                object.setObjectNumber( objectNumbers.get( objectCounter) );
                streamObjects.add( object );
                Log.d("PdfBoxAndroid", "parsed=" + object );
                // According to the spec objects within an object stream shall not be enclosed 
                // by obj/endobj tags, but there are some pdfs in the wild using those tags
                // skip endobject marker if present
                if (!pdfSource.isEOF() && pdfSource.peek() == 'e')
                {
                	readLine();
                }
                objectCounter++;
            }
        }
        finally
        {
            pdfSource.close();
        }
    }

    /**
     * This will get the objects that were parsed from the stream.
     *
     * @return All of the objects in the stream.
     */
    public List<COSObject> getObjects()
    {
        return streamObjects;
    }
}
