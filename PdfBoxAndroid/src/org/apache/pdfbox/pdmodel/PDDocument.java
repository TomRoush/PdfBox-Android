package org.apache.pdfbox.pdmodel;

import java.io.BufferedInputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSDocument;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.exceptions.COSVisitorException;
import org.apache.pdfbox.io.RandomAccess;
import org.apache.pdfbox.pdfparser.BaseParser;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdfwriter.COSWriter;

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
    private PDDocumentCatalog documentCatalog;
    
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
     * Constructor that uses an existing document.  The COSDocument that
     * is passed in must be valid.
     *
     * @param doc The COSDocument that this document wraps.
     */
    public PDDocument( COSDocument doc )
    {
        this(doc, null);
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
     * This will get the low level document.
     *
     * @return The document that this layer sits on top of.
     */
    public COSDocument getDocument()
    {
        return document;
    }
    
    /**
     * This will get the document CATALOG.  This is guaranteed to not return null.
     *
     * @return The documents /Root dictionary
     */
    public PDDocumentCatalog getDocumentCatalog()
    {
        if( documentCatalog == null )
        {
            COSDictionary trailer = document.getTrailer();
            COSBase dictionary = trailer.getDictionaryObject( COSName.ROOT );
            if (dictionary instanceof COSDictionary) 
            {
                documentCatalog = new PDDocumentCatalog(this, (COSDictionary) dictionary);
            } 
            else 
            {
                documentCatalog = new PDDocumentCatalog(this);
            }
        }
        return documentCatalog;
    }
	
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
     * Save the document to a file.
     *
     * @param fileName The file to save as.
     *
     * @throws IOException If there is an error saving the document.
     * @throws COSVisitorException If an error occurs while generating the data.
     */
    public void save( String fileName ) throws IOException, COSVisitorException
    {
        save( new File( fileName ) );
    }

    /**
     * Save the document to a file.
     *
     * @param file The file to save as.
     *
     * @throws IOException If there is an error saving the document.
     * @throws COSVisitorException If an error occurs while generating the data.
     */
    public void save( File file ) throws IOException, COSVisitorException
    {
        save( new FileOutputStream( file ) );
    }

    /**
     * This will save the document to an output stream.
     *
     * @param output The stream to write to.
     *
     * @throws IOException If there is an error writing the document.
     * @throws COSVisitorException If an error occurs while generating the data.
     */
    public void save( OutputStream output ) throws IOException, COSVisitorException
    {
        //update the count in case any pages have been added behind the scenes.
        getDocumentCatalog().getPages().updateCount();
        COSWriter writer = null;
        try
        {
            writer = new COSWriter( output );
            writer.write( this );
            writer.close();
        }
        finally
        {
            if( writer != null )
            {
                writer.close();
            }
        }
    }
    
    /**
     * This will close the underlying COSDocument object.
     *
     * @throws IOException If there is an error releasing resources.
     */
    public void close() throws IOException
    {
    	documentCatalog = null;
//    	documentInformation = null;
//    	encParameters = null;
    	if (pageMap != null)
    	{
    		pageMap.clear();
    		pageMap = null;
    	}
//    	securityHandler = null;
    	if (document != null)
    	{
	        document.close();
	        document = null;
    	}
        if (parser != null)
        {
        	parser.clearResources();
        	parser = null;
        }
    }
    
    /**
     * Indicates if all security is removed or not when writing the pdf.
     * @return returns true if all security shall be removed otherwise false
     */
    public boolean isAllSecurityToBeRemoved()
    {
        return allSecurityToBeRemoved;
    }

    /**
     * Activates/Deactivates the removal of all security when writing the pdf.
     *  
     * @param removeAllSecurity remove all security if set to true
     */
    public void setAllSecurityToBeRemoved(boolean removeAllSecurity)
    {
        allSecurityToBeRemoved = removeAllSecurity;
    }
    
    public Long getDocumentId() 
    {
      return documentId;
    }
    
    public void setDocumentId(Long docId)
    {
      documentId = docId;
    }
    
}
