package org.apache.pdfbox.cos;

import java.io.File;
import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.exceptions.COSVisitorException;
import org.apache.pdfbox.io.RandomAccess;
import org.apache.pdfbox.io.RandomAccessBuffer;

/**
 * This is the in-memory representation of the PDF document.  You need to call
 * close() on this object when you are done using it!!
 *
 * @author <a href="ben@benlitchfield.com">Ben Litchfield</a>
 * @version $Revision: 1.28 $
 */
public class COSDocument extends COSBase
{
	
	/**
     * Log instance.
     */
    private static final Log LOG = LogFactory.getLog(COSDocument.class);

    private float version = 1.4f;

    /**
     * Maps ObjectKeys to a COSObject. Note that references to these objects
     * are also stored in COSDictionary objects that map a name to a specific object.
     */
//    private final Map<COSObjectKey, COSObject> objectPool =
//        new HashMap<COSObjectKey, COSObject>();

    /**
     * Maps object and generation id to object byte offsets.
     */
//    private final Map<COSObjectKey, Long> xrefTable =
//        new HashMap<COSObjectKey, Long>();

    /**
     * Document trailer dictionary.
     */
    private COSDictionary trailer;
    
    /**
     * Signature interface.
     */
//    private SignatureInterface signatureInterface;

    /**
     * This file will store the streams in order to conserve memory.
     */
    private final RandomAccess scratchFile;

    private final File tmpFile;

    private String headerString = "%PDF-" + version;

    private boolean warnMissingClose = true;
    
    /** signal that document is already decrypted, e.g. with {@link NonSequentialPDFParser} */
    private boolean isDecrypted = false;
    
    private long startXref;
    
    private boolean closed = false;

    /**
     * Flag to skip malformed or otherwise unparseable input where possible.
     */
    private final boolean forceParsing;
	
	/**
     * Constructor that will use the given random access file for storage
     * of the PDF streams. The client of this method is responsible for
     * deleting the storage if necessary that this file will write to. The
     * close method will close the file though.
     *
     * @param scratchFileValue the random access file to use for storage
     * @param forceParsingValue flag to skip malformed or otherwise unparseable
     *                     document content where possible
     */
    public COSDocument(RandomAccess scratchFileValue, boolean forceParsingValue) 
    {
        scratchFile = scratchFileValue;
        tmpFile = null;
        forceParsing = forceParsingValue;
    }

	/**
     * Constructor.  Uses memory to store stream.
     *
     *  @throws IOException If there is an error creating the tmp file.
     */
    public COSDocument() throws IOException 
    {
        this(new RandomAccessBuffer(), false);
    }

    /**
     * visitor pattern double dispatch method.
     *
     * @param visitor The object to notify when visiting this object.
     * @return any object, depending on the visitor implementation, or null
     * @throws COSVisitorException If an error occurs while visiting this object.
     */
    @Override
    public Object accept(ICOSVisitor visitor) throws COSVisitorException
    {
        return visitor.visitFromDocument( this );
    }

}
