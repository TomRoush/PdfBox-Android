package org.apache.pdfbox.pdmodel.fdf;

import java.util.ArrayList;
import java.util.List;

import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.common.COSArrayList;
import org.apache.pdfbox.pdmodel.common.COSObjectable;
import org.apache.pdfbox.pdmodel.common.PDNamedTextStream;
import org.apache.pdfbox.pdmodel.common.PDTextStream;

/**
 * This represents an FDF JavaScript dictionary that is part of the FDF document.
 *
 * @author Ben Litchfield
 */
public class FDFJavaScript implements COSObjectable
{
    private COSDictionary js;

    /**
     * Default constructor.
     */
    public FDFJavaScript()
    {
        js = new COSDictionary();
    }

    /**
     * Constructor.
     *
     * @param javaScript The FDF java script.
     */
    public FDFJavaScript( COSDictionary javaScript )
    {
        js = javaScript;
    }

    /**
     * Convert this standard java object to a COS object.
     *
     * @return The cos object that matches this Java object.
     */
    public COSBase getCOSObject()
    {
        return js;
    }

    /**
     * Convert this standard java object to a COS object.
     *
     * @return The cos object that matches this Java object.
     */
    public COSDictionary getCOSDictionary()
    {
        return js;
    }

    /**
     * This will get the javascript that is executed before the import.
     *
     * @return Some javascript code.
     */
    public PDTextStream getBefore()
    {
        return PDTextStream.createTextStream( js.getDictionaryObject( COSName.BEFORE ) );
    }

    /**
     * This will set the javascript code the will get execute before the import.
     *
     * @param before A reference to some javascript code.
     */
    public void setBefore( PDTextStream before )
    {
        js.setItem( COSName.BEFORE, before );
    }

    /**
     * This will get the javascript that is executed after the import.
     *
     * @return Some javascript code.
     */
    public PDTextStream getAfter()
    {
        return PDTextStream.createTextStream( js.getDictionaryObject( COSName.AFTER ) );
    }

    /**
     * This will set the javascript code the will get execute after the import.
     *
     * @param after A reference to some javascript code.
     */
    public void setAfter( PDTextStream after )
    {
        js.setItem( COSName.AFTER, after );
    }

    /**
     * This will return a list of PDNamedTextStream objects.  This is the "Doc"
     * entry of the pdf document.  These will be added to the PDF documents
     * javascript name tree.  This will not return null.
     *
     * @return A list of all named javascript entries.
     */
    public List<PDNamedTextStream> getNamedJavaScripts()
    {
        COSArray array = (COSArray)js.getDictionaryObject( COSName.DOC );
        List<PDNamedTextStream> namedStreams = new ArrayList<PDNamedTextStream>();
        if( array == null )
        {
            array = new COSArray();
            js.setItem( COSName.DOC, array );
        }
        for( int i=0; i<array.size(); i++ )
        {
            COSName name = (COSName)array.get( i );
            i++;
            COSBase stream = array.get( i );
            PDNamedTextStream namedStream = new PDNamedTextStream( name, stream );
            namedStreams.add( namedStream );
        }
        return new COSArrayList<PDNamedTextStream>( namedStreams, array );
    }

    /**
     * This should be a list of PDNamedTextStream objects.
     *
     * @param namedStreams The named streams.
     */
    public void setNamedJavaScripts( List<PDTextStream> namedStreams )
    {
        COSArray array = COSArrayList.converterToCOSArray( namedStreams );
        js.setItem( COSName.DOC, array );
    }
}