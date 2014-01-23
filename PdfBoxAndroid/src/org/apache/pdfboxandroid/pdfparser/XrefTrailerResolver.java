package org.apache.pdfboxandroid.pdfparser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.pdfboxandroid.PDFBox;
import org.apache.pdfboxandroid.cos.COSDictionary;
import org.apache.pdfboxandroid.cos.COSName;
import org.apache.pdfboxandroid.persistence.util.COSObjectKey;

import android.util.Log;

/**
 * This class will collect all XRef/trailer objects and creates correct
 * xref/trailer information after all objects are read using startxref
 * and 'Prev' information (unused XRef/trailer objects are discarded).
 *
 * In case of missing startxref or wrong startxref pointer all
 * XRef/trailer objects are used to create xref table / trailer dictionary
 * in order they occur.
 *
 * For each new xref object/XRef stream method {@link #nextXrefObj(int)}
 * must be called with start byte position. All following calls to
 * {@link #setXRef(COSObjectKey, int)} or {@link #setTrailer(COSDictionary)}
 * will add the data for this byte position.
 *
 * After all objects are parsed the startxref position must be provided
 * using {@link #setStartxref(int)}. This is used to build the chain of
 * active xref/trailer objects used for creating document trailer and xref table.
 *
 * @author Timo Böhme (timo.boehme at ontochem.com)
 */
public class XrefTrailerResolver {
	/**
     * A class which represents a xref/trailer object.
     */
    private class XrefTrailerObj
    {
        protected COSDictionary trailer = null;
        private final Map<COSObjectKey, Long> xrefTable = new HashMap<COSObjectKey, Long>();
        
        /**
         *  Default cosntructor.
         */
        private XrefTrailerObj()
        {
        }
    }
	
    private final Map<Long, XrefTrailerObj> bytePosToXrefMap = new HashMap<Long, XrefTrailerObj>();
    private XrefTrailerObj curXrefTrailerObj   = null;
    private XrefTrailerObj resolvedXrefTrailer = null;
	
	/**
     * Sets the byte position of the first XRef
     * (has to be called after very last startxref was read).
     * This is used to resolve chain of active XRef/trailer.
     *
     * In case startxref position is not found we output a
     * warning and use all XRef/trailer objects combined
     * in byte position order.
     * Thus for incomplete PDF documents with missing
     * startxref one could call this method with parameter value -1.
     * 
     * @param startxrefBytePosValue starting position of the first XRef
     * 
     */
    public void setStartxref( long startxrefBytePosValue )
    {
        if ( resolvedXrefTrailer != null )
        {
            Log.w( "Method must be called only ones with last startxref value.", PDFBox.LOG_TAG);
            return;
        }

        resolvedXrefTrailer = new XrefTrailerObj();
        resolvedXrefTrailer.trailer = new COSDictionary();

        XrefTrailerObj curObj = bytePosToXrefMap.get( startxrefBytePosValue );
        List<Long>  xrefSeqBytePos = new ArrayList<Long>();

        if ( curObj == null )
        {
            // no XRef at given position
            Log.w( "Did not found XRef object at specified startxref position " + startxrefBytePosValue, PDFBox.LOG_TAG);

            // use all objects in byte position order (last entries overwrite previous ones)
            xrefSeqBytePos.addAll( bytePosToXrefMap.keySet() );
            Collections.sort( xrefSeqBytePos );
        }
        else
        {
            // found starting Xref object
            // add this and follow chain defined by 'Prev' keys
            xrefSeqBytePos.add( startxrefBytePosValue );
            while ( curObj.trailer != null )
            {
                long prevBytePos = curObj.trailer.getLong( COSName.PREV, -1L );
                if ( prevBytePos == -1 )
                {
                    break;
                }

                curObj = bytePosToXrefMap.get( prevBytePos );
                if ( curObj == null )
                {
                    Log.w( "Did not found XRef object pointed to by 'Prev' key at position " + prevBytePos, PDFBox.LOG_TAG);
                    break;
                }
                xrefSeqBytePos.add( prevBytePos );

                // sanity check to prevent infinite loops
                if ( xrefSeqBytePos.size() >= bytePosToXrefMap.size() )
                {
                    break;
                }
            }
            // have to reverse order so that later XRefs will overwrite previous ones
            Collections.reverse( xrefSeqBytePos );
        }

        // merge used and sorted XRef/trailer
        for ( Long bPos : xrefSeqBytePos )
        {
            curObj = bytePosToXrefMap.get( bPos );
            if ( curObj.trailer != null )
            {
                resolvedXrefTrailer.trailer.addAll( curObj.trailer );
            }
            resolvedXrefTrailer.xrefTable.putAll( curObj.xrefTable );
        }

    }
    
    /**
     * Gets the resolved trailer. Might return <code>null</code> in case
     * {@link #setStartxref(int)} was not called before.
     *
     * @return the trailer if available
     */
    public COSDictionary getTrailer()
    {
        return ( resolvedXrefTrailer == null ) ? null : resolvedXrefTrailer.trailer;
    }
    
    /**
     * Gets the resolved xref table. Might return <code>null</code> in case
     *  {@link #setStartxref(int)} was not called before.
     *
     * @return the xrefTable if available
     */
    public Map<COSObjectKey, Long> getXrefTable()
    {
        return ( resolvedXrefTrailer == null ) ? null : resolvedXrefTrailer.xrefTable;
    }
    
    /**
     * Signals that a new XRef object (table or stream) starts.
     * @param startBytePos the offset to start at
     *
     */
    public void nextXrefObj( final long startBytePos )
    {
        bytePosToXrefMap.put( startBytePos, curXrefTrailerObj = new XrefTrailerObj() );
    }
    
    /**
     * Adds trailer information for current XRef object.
     *
     * @param trailer the current document trailer dictionary
     */
    public void setTrailer( COSDictionary trailer )
    {
        if ( curXrefTrailerObj == null )
        {
            // should not happen...
            Log.w( "Cannot add trailer because XRef start was not signalled." , PDFBox.LOG_TAG);
            return;
        }
        curXrefTrailerObj.trailer = trailer;
    }
    
    /**
     * Populate XRef HashMap of current XRef object.
     * Will add an Xreftable entry that maps ObjectKeys to byte offsets in the file.
     * @param objKey The objkey, with id and gen numbers
     * @param offset The byte offset in this file
     */
    public void setXRef( COSObjectKey objKey, long offset )
    {
        if ( curXrefTrailerObj == null )
        {
            // should not happen...
            Log.w( "Cannot add XRef entry for '" + objKey.getNumber() + "' because XRef start was not signalled."  , PDFBox.LOG_TAG);
            return;
        }
        curXrefTrailerObj.xrefTable.put( objKey, offset );
    }
}
