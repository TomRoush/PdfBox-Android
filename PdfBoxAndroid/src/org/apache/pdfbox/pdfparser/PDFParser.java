package org.apache.pdfbox.pdfparser;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.cos.COSDocument;
import org.apache.pdfbox.exceptions.WrappedIOException;
import org.apache.pdfbox.io.RandomAccess;

/**
 * This class will handle the parsing of the PDF document.
 *
 * @author <a href="mailto:ben@benlitchfield.com">Ben Litchfield</a>
 * @version $Revision: 1.53 $
 */
public class PDFParser extends BaseParser
{
	
	/**
     * Log instance.
     */
    private static final Log LOG = LogFactory.getLog(PDFParser.class);
	
	private static final int SPACE_BYTE = 32;

    private static final String PDF_HEADER = "%PDF-";
    private static final String FDF_HEADER = "%FDF-";
    
    private static final String PDF_DEFAULT_VERSION = "1.4";
    private static final String FDF_DEFAULT_VERSION = "1.0";
    
    /**
     * A list of duplicate objects found when Parsing the PDF
     * File.
     */
//    private List<ConflictObj> conflictList = new ArrayList<ConflictObj>();

    /** Collects all Xref/trailer objects and resolves them into single
     *  object using startxref reference. 
     */
//    protected XrefTrailerResolver xrefTrailerResolver = new XrefTrailerResolver();

    /**
     * Temp file directory.
     */
    private File tempDirectory = null;

    private RandomAccess raf = null;
	
	/**
     * Constructor to allow control over RandomAccessFile.
     * @param input The input stream that contains the PDF document.
     * @param rafi The RandomAccessFile to be used in internal COSDocument
     *
     * @throws IOException If there is an error initializing the stream.
     */
    public PDFParser(InputStream input, RandomAccess rafi) throws IOException 
    {
        this(input, rafi, FORCE_PARSING);
    }
    
    /**
     * Constructor to allow control over RandomAccessFile.
     * Also enables parser to skip corrupt objects to try and force parsing
     * @param input The input stream that contains the PDF document.
     * @param rafi The RandomAccessFile to be used in internal COSDocument
     * @param force When true, the parser will skip corrupt pdf objects and
     * will continue parsing at the next object in the file
     *
     * @throws IOException If there is an error initializing the stream.
     */
    public PDFParser(InputStream input, RandomAccess rafi, boolean force) throws IOException 
    {
        super(input, force);
        this.raf = rafi;
    }
    
    /**
     * This will parse the stream and populate the COSDocument object.  This will close
     * the stream when it is done parsing.
     *
     * @throws IOException If there is an error reading from the stream or corrupt data
     * is found.
     */
    public void parse() throws IOException
    {
        try
        {
            if ( raf == null )
            {
                if( tempDirectory != null )
                {
                    document = new COSDocument( tempDirectory );
                }
                else
                {
                    document = new COSDocument();
                }
            }
            else
            {
                document = new COSDocument( raf );
            }
            setDocument( document );

            parseHeader();

            //Some PDF files have garbage between the header and the
            //first object
            skipToNextObj();

            boolean wasLastParsedObjectEOF = false;
            while(true)
            {
                if(pdfSource.isEOF())
                {
                    break;
                }
                                
                try
                {
                    // don't reset flag to false if it is already true
                    wasLastParsedObjectEOF |= parseObject();
                }
                catch(IOException e)
                {
                    /*
                     * PDF files may have random data after the EOF marker. Ignore errors if
                     * last object processed is EOF.
                     */
                    if( wasLastParsedObjectEOF )
                    {
                        break;
                    }
                    if(isContinueOnError(e))
                    {
                        /*
                         * Warning is sent to the PDFBox.log and to the Console that
                         * we skipped over an object
                         */
                        LOG.warn("Parsing Error, Skipping Object", e);
                        
                        skipSpaces();
                        long lastOffset = pdfSource.getOffset();
                        skipToNextObj();
                        
                        /* the nextObject is the one we want to skip 
                         * so read the 'Object Number' without interpret it
                         * in order to force the skipObject
                         */
                        if (lastOffset == pdfSource.getOffset()) {
                            readStringNumber();
                            skipToNextObj();
                        }
                    }
                    else
                    {
                        throw e;
                    }
                }
                skipSpaces();
            }

            // set xref to start with
            xrefTrailerResolver.setStartxref( document.getStartXref() );

            // get resolved xref table + trailer
            document.setTrailer( xrefTrailerResolver.getTrailer() );
            document.addXRefTable( xrefTrailerResolver.getXrefTable() );

            if( !document.isEncrypted() )
            {
                document.dereferenceObjectStreams();
            }
            else
            {
                LOG.info("Document is encrypted");
            }
            ConflictObj.resolveConflicts(document, conflictList);
        }
        catch( Throwable t )
        {
            //so if the PDF is corrupt then close the document and clear
            //all resources to it
            if( document != null )
            {
                document.close();
                document = null;
            }
            if( t instanceof IOException )
            {
                throw (IOException)t;
            }
            else
            {
                throw new WrappedIOException( t );
            }
        }
        finally
        {
            pdfSource.close();
        }
    }
	
}
