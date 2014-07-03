package org.apache.pdfboxandroid.pdmodel;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.pdfboxandroid.cos.COSBase;
import org.apache.pdfboxandroid.cos.COSDictionary;
import org.apache.pdfboxandroid.cos.COSDocument;
import org.apache.pdfboxandroid.cos.COSName;
import org.apache.pdfboxandroid.exceptions.COSVisitorException;
import org.apache.pdfboxandroid.io.RandomAccess;
import org.apache.pdfboxandroid.pdfparser.PDFParser;
import org.apache.pdfboxandroid.pdfwriter.COSWriter;

import android.util.Log;

import com.birdbran2.awt.print.Pagable;

public class PDDocument implements Pagable {
	private COSDocument document;
	
	//cached values
//    private PDDocumentInformation documentInformation;
    private PDDocumentCatalog documentCatalog;
    
    /**
     * Keep tracking customized documentId for the trailer. If null, a new 
     * id will be generated for the document. This ID doesn't represent the
     * actual documentId from the trailer.
     */
    private Long documentId;
	
    /**
     * This will load a document from a file.
     *
     * @param filename The name of the file to load.
     *
     * @return The document that was loaded.
     *
     * @throws IOException If there is an error reading from the stream.
     */
    public static PDDocument load( String filename ) throws IOException
    {
        return load( new FileInputStream( filename ) );
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
     * Constructor that uses an existing document.  The COSDocument that
     * is passed in must be valid.
     *
     * @param doc The COSDocument that this document wraps.
     */
    public PDDocument( COSDocument doc )
    {
        document = doc;
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
     * This will get the low level document.
     *
     * @return The document that this layer sits on top of.
     */
    public COSDocument getDocument()
    {
        return document;
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
//    	Assume we haven't got more pages @Birdbrain
//    	TODO We shouldn't at this stage
//        getDocumentCatalog().getPages().updateCount();
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
    
    public Long getDocumentId() 
    {
      return documentId;
    }
    
    /**
     * This will close the underlying COSDocument object.
     *
     * @throws IOException If there is an error releasing resources.
     */
    public void close() throws IOException
    {
        document.close();
    }
}