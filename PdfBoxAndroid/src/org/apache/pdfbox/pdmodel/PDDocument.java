package org.apache.pdfbox.pdmodel;

import java.io.BufferedInputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.apache.pdfbox.cos.COSDocument;
import org.apache.pdfbox.io.RandomAccess;
import org.apache.pdfbox.pdfparser.BaseParser;
import org.apache.pdfbox.pdfparser.PDFParser;

import awt.print.Pageable;

/**
 * This is the in-memory representation of the PDF document.  You need to call
 * close() on this object when you are done using it!!
 * <p>
 * This class implements the {@link Pageable} interface, but since PDFBox
 * version 1.3.0 you should be using the {@link PDPageable} adapter instead
 * (see <a href="https://issues.apache.org/jira/browse/PDFBOX-788">PDFBOX-788</a>).
 *
 * @author <a href="mailto:ben@benlitchfield.com">Ben Litchfield</a>
 * @version $Revision: 1.47 $
 */
public class PDDocument implements Pageable, Closeable
{
	
	private COSDocument document;

    //cached values
//    private PDDocumentInformation documentInformation;
//    private PDDocumentCatalog documentCatalog;
    
    //The encParameters will be cached here.  When the document is decrypted then
    //the COSDocument will not have an "Encrypt" dictionary anymore and this object
    //must be used.
//    private PDEncryptionDictionary encParameters = null;

    /**
     * The security handler used to decrypt / encrypt the document.
     */
//    private SecurityHandler securityHandler = null;


    /**
     * This assocates object ids with a page number.  It's used to determine
     * the page number for bookmarks (or page numbers for anything else for
     * which you have an object id for that matter). 
     */
    private Map<String, Integer> pageMap = null;
    
    /**
     * This will hold a flag which tells us if we should remove all security
     * from this documents.
     */
    private boolean allSecurityToBeRemoved = false;

    /**
     * Keep tracking customized documentId for the trailer. If null, a new 
     * id will be generated for the document. This ID doesn't represent the
     * actual documentId from the trailer.
     */
    private Long documentId;

    private BaseParser parser;
	
	/**
     * This will load a document from an input stream.
     *
     * @param input The stream that contains the document.
     *
     * @return The document that was loaded.
     *
     * @throws IOException If there is an error reading from the stream.
     */
    public static PDDocument load( InputStream input ) throws IOException
    {
        return load( input, null );
    }
    
    /**
     * This will load a document from an input stream.
     *
     * @param input The stream that contains the document.
     * @param scratchFile A location to store temp PDFBox data for this document.
     *
     * @return The document that was loaded.
     *
     * @throws IOException If there is an error reading from the stream.
     */
    public static PDDocument load( InputStream input, RandomAccess scratchFile ) throws IOException
    {
        PDFParser parser = new PDFParser( new BufferedInputStream( input ) , scratchFile );
        parser.parse();
        return parser.getPDDocument();
    }
    
    /**
     * Constructor that uses an existing document. The COSDocument that is passed in must be valid.
     * 
     * @param doc The COSDocument that this document wraps.
     * @param usedParser the parser which is used to read the pdf
     */
    public PDDocument(COSDocument doc, BaseParser usedParser)
    {
        document = doc;
        parser = usedParser;
    }
    
    /**
     * This will close the underlying COSDocument object.
     *
     * @throws IOException If there is an error releasing resources.
     */
    public void close() throws IOException
    {
//    	documentCatalog = null;
//    	documentInformation = null;
//    	encParameters = null;
//    	if (pageMap != null)
//    	{
//    		pageMap.clear();
//    		pageMap = null;
//    	}
//    	securityHandler = null;
//    	if (document != null)
//    	{
//	        document.close();
//	        document = null;
//    	}
//        if (parser != null)
//        {
//        	parser.clearResources();
//        	parser = null;
//        }
    }
    
}
