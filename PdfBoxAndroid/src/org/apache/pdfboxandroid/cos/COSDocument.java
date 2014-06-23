package org.apache.pdfboxandroid.cos;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.pdfboxandroid.PDFBox;
import org.apache.pdfboxandroid.exceptions.COSVisitorException;
import org.apache.pdfboxandroid.io.RandomAccess;
import org.apache.pdfboxandroid.io.RandomAccessBuffer;
import org.apache.pdfboxandroid.io.RandomAccessFile;
import org.apache.pdfboxandroid.pdmodel.interactive.digitalsignature.SignatureInterface;
import org.apache.pdfboxandroid.persistence.util.COSObjectKey;

import android.util.Log;

public class COSDocument extends COSBase {
	private float version = 1.4f;
	
	/**
     * Maps ObjectKeys to a COSObject. Note that references to these objects
     * are also stored in COSDictionary objects that map a name to a specific object.
     */
    private final Map<COSObjectKey, COSObject> objectPool = new HashMap<COSObjectKey, COSObject>();
	
	/**
     * This file will store the streams in order to conserve memory.
     */
    private final RandomAccess scratchFile;
    
	private final File tmpFile;
	
	private String headerString = "%PDF-" + version;
    
    private long startXref;
    
    private boolean closed = false;
    
    /**
     * Maps object and generation id to object byte offsets.
     */
    private final Map<COSObjectKey, Long> xrefTable = new HashMap<COSObjectKey, Long>();
    
    /**
     * Document trailer dictionary.
     */
    private COSDictionary trailer;
    
    /**
     * Signature interface.
     */
    private SignatureInterface signatureInterface;
    
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
    }
    
    /**
     * Constructor that will use the following random access file for storage
     * of the PDF streams.  The client of this method is responsible for deleting
     * the storage if necessary that this file will write to.  The close method
     * will close the file though.
     *
     * @param file The random access file to use for storage.
     */
    public COSDocument(RandomAccess file) 
    {
        this(file, false);
    }
	
	/**
     * Constructor that will create a create a scratch file in the
     * following directory.
     *
     * @param scratchDir The directory to store a scratch file.
     *
     * @throws IOException If there is an error creating the tmp file.
     */
    public COSDocument(File scratchDir) throws IOException 
    {
        this(scratchDir, false);
    }
    
    /**
     * Constructor that will use a temporary file in the given directory
     * for storage of the PDF streams. The temporary file is automatically
     * removed when this document gets closed.
     *
     * @param scratchDir directory for the temporary file,
     *                   or <code>null</code> to use the system default
     * @param forceParsingValue flag to skip malformed or otherwise unparseable
     *                     document content where possible
     * @throws IOException if something went wrong
     */
    public COSDocument(File scratchDir, boolean forceParsingValue) throws IOException 
    {
        tmpFile = File.createTempFile("pdfbox-", ".tmp", scratchDir);
        scratchFile = new RandomAccessFile(tmpFile, "rw");
    }
    
    /**
     * Return the startXref Position of the parsed document. This will only be needed for incremental updates.
     * 
     * @return a long with the old position of the startxref
     */
    public long getStartXref()
    {
      return startXref;
    }
    
    /**
     * // MIT added, maybe this should not be supported as trailer is a persistence construct.
     * This will set the document trailer.
     *
     * @param newTrailer the document trailer dictionary
     */
    public void setTrailer(COSDictionary newTrailer)
    {
        trailer = newTrailer;
    }
    
    /**
     * Populate XRef HashMap with given values.
     * Each entry maps ObjectKeys to byte offsets in the file.
     * @param xrefTableValues  xref table entries to be added
     */
    public void addXRefTable( Map<COSObjectKey, Long> xrefTableValues )
    {
        xrefTable.putAll( xrefTableValues );
    }
    
    /**
     * Returns the xrefTable which is a mapping of ObjectKeys
     * to byte offsets in the file.
     * @return mapping of ObjectsKeys to byte offsets
     */
    public Map<COSObjectKey, Long> getXrefTable()
    {
        return xrefTable;
    }
    
    /**
     * This will get an object from the pool.
     *
     * @param key The object key.
     *
     * @return The object in the pool or a new one if it has not been parsed yet.
     *
     * @throws IOException If there is an error getting the proxy object.
     */
    public COSObject getObjectFromPool(COSObjectKey key) throws IOException
    {
        COSObject obj = null;
        if( key != null )
        {
            obj = objectPool.get(key);
        }
        if (obj == null)
        {
            // this was a forward reference, make "proxy" object
            obj = new COSObject(null);
            if( key != null )
            {
                obj.setObjectNumber( COSInteger.get( key.getNumber() ) );
                obj.setGenerationNumber( COSInteger.get( key.getGeneration() ) );
                objectPool.put(key, obj);
            }
        }
        return obj;
    }
    
    /**
     * This will get the scratch file for this document.
     *
     * @return The scratch file.
     * 
     * 
     */
    public RandomAccess getScratchFile()
    {
        // TODO the direct access to the scratch file should be removed.
        if (!closed)
        {
            return scratchFile;
        }
        else
        {
            Log.e(PDFBox.LOG_TAG, "Can't access the scratch file as it is already closed!");
            return null;
        }
    }
    
    /**
     * This will close all storage and delete the tmp files.
     *
     *  @throws IOException If there is an error close resources.
     */
    public void close() throws IOException
    {
        if (!closed) 
        {
            scratchFile.close();
            if (tmpFile != null) 
            {
                tmpFile.delete();
            }
            closed = true;
        }
    }
    
    /**
     * This method set the startxref value of the document. This will only 
     * be needed for incremental updates.
     * 
     * @param startXrefValue the value for startXref
     */
    public void setStartXref(long startXrefValue)
    {
        startXref = startXrefValue;
    }
    
    /**
     * @param header The headerString to set.
     */
    public void setHeaderString(String header)
    {
        headerString = header;
    }
    
    /**
     * This will set the version of this PDF document.
     *
     * @param versionValue The version of the PDF document.
     */
    public void setVersion( float versionValue )
    {
        // update header string
        if (versionValue != version) 
        {
            headerString = headerString.replaceFirst(String.valueOf(version), String.valueOf(versionValue));
        }
        version = versionValue;
    }
    
    /**
     * This will get the version of this PDF document.
     *
     * @return This documents version.
     */
    public float getVersion()
    {
        return version;
    }
    
    /**
     * This will get the document trailer.
     *
     * @return the document trailer dict
     */
    public COSDictionary getTrailer()
    {
        return trailer;
    }
    
    /**
     * Create a new COSStream using the underlying scratch file.
     * 
     * @return the new COSStream
     */
    public COSStream createCOSStream()
    {
        return new COSStream( getScratchFile() );
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
    
    /**
     * Determines it the trailer is a XRef stream or not.
     * 
     * @return true if the trailer is a XRef stream
     */
    public boolean isXRefStream()
    {
        if (trailer != null)
        {
            return COSName.XREF.equals(trailer.getItem(COSName.TYPE));
        }
        return false;
    }
    
    /**
     * This will return the signature interface.
     * @return the signature interface 
     */
    public SignatureInterface getSignatureInterface() 
    {
        return signatureInterface;
    }
    
    /**
     * @return Returns the headerString.
     */
    public String getHeaderString()
    {
        return headerString;
    }
}