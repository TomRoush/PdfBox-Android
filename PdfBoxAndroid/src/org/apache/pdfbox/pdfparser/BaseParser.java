package org.apache.pdfbox.pdfparser;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.io.PushBackInputStream;

/**
 * This class is used to contain parsing logic that will be used by both the
 * PDFParser and the COSStreamParser.
 *
 * @author <a href="mailto:ben@benlitchfield.com">Ben Litchfield</a>
 * 
 */
public abstract class BaseParser
{

	private static final long OBJECT_NUMBER_THRESHOLD = 10000000000L;

    private static final long GENERATION_NUMBER_THRESHOLD = 65535;
    
    /**
     * system property allowing to define size of push back buffer.
     */
    public static final String PROP_PUSHBACK_SIZE = "org.apache.pdfbox.baseParser.pushBackSize";

    /**
     * Log instance.
     */
    private static final Log LOG = LogFactory.getLog(BaseParser.class);

    private static final int E = 'e';
    private static final int N = 'n';
    private static final int D = 'd';

    private static final int S = 's';
    private static final int T = 't';
    private static final int R = 'r';
    private static final int A = 'a';
    private static final int M = 'm';

    private static final int O = 'o';
    private static final int B = 'b';
    private static final int J = 'j';

    private final int    strmBufLen = 2048;
    private final byte[] strmBuf    = new byte[ strmBufLen ];

    /**
     * This is a byte array that will be used for comparisons.
     */
    public static final byte[] ENDSTREAM =
        new byte[] { E, N, D, S, T, R, E, A, M };

    /**
     * This is a byte array that will be used for comparisons.
     */
    public static final byte[] ENDOBJ =
        new byte[] { E, N, D, O, B, J };

    /**
     * This is a string constant that will be used for comparisons.
     */
    public static final String DEF = "def";
    /**
     * This is a string constant that will be used for comparisons.
     */
    private static final String ENDOBJ_STRING = "endobj";
    /**
     * This is a string constant that will be used for comparisons.
     */
    private static final String ENDSTREAM_STRING = "endstream";
    /**
     * This is a string constant that will be used for comparisons.
     */
    private static final String STREAM_STRING = "stream";
    /**
     * This is a string constant that will be used for comparisons.
     */
    private static final String TRUE = "true";
    /**
     * This is a string constant that will be used for comparisons.
     */
    private static final String FALSE = "false";
    /**
     * This is a string constant that will be used for comparisons.
     */
    private static final String NULL = "null";

    /**
     * Default value of the {@link #forceParsing} flag.
     */
    static boolean FORCE_PARSING = true;

    static
    {
        // get preferences value for force parsing
        try
        {
            FORCE_PARSING = Boolean.getBoolean("org.apache.pdfbox.forceParsing");
        }
        catch (SecurityException e)
        {
            // PDFBOX-1946 since Boolean.getBoolean calls System.getProperty, this can occur
            /* ignore and use default */
        }
    }

    /**
     * This is the stream that will be read from.
     */
    protected PushBackInputStream pdfSource;

    /**
     * This is the document that will be parsed.
     */
    protected COSDocument document;

    /**
     * Flag to skip malformed or otherwise unparseable input where possible.
     */
    protected final boolean forceParsing;
    
    /**
     * Constructor.
     *
     * @since Apache PDFBox 1.3.0
     * @param input The input stream to read the data from.
     * @param forceParsingValue flag to skip malformed or otherwise unparseable
     *                     input where possible
     * @throws IOException If there is an error reading the input stream.
     */
    public BaseParser(InputStream input, boolean forceParsingValue)
            throws IOException
    {
        int pushbacksize = 65536;
        try
        {
            pushbacksize = Integer.getInteger( PROP_PUSHBACK_SIZE, 65536 );
        }
        catch (SecurityException e)  // getInteger calls System.getProperties, which can get exception
        {
            // ignore and use default
        }
        pdfSource = new PushBackInputStream(
                new BufferedInputStream(input, 16384), pushbacksize );
        forceParsing = forceParsingValue;
    }
    
}
