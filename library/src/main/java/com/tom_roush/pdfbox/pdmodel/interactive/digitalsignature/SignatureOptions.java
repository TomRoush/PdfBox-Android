package com.tom_roush.pdfbox.pdmodel.interactive.digitalsignature;

import com.tom_roush.pdfbox.cos.COSDocument;
import com.tom_roush.pdfbox.io.RandomAccessBufferedFileInputStream;
import com.tom_roush.pdfbox.pdfparser.PDFParser;
import com.tom_roush.pdfbox.pdmodel.interactive.digitalsignature.visible.PDVisibleSigProperties;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * TODO description needed
 */
public class SignatureOptions implements Closeable
{
    private COSDocument visualSignature;
    private int preferedSignatureSize;
    private int pageNo;

    /**
     * Creates the default signature options.
     */
    public SignatureOptions()
    {
        pageNo = 0;
    }

    /**
     * Set the page number.
     * 
     * @param pageNo the page number
     */
    public void setPage(int pageNo)
    {
        this.pageNo = pageNo;
    }
  
    /**
     * Get the page number.
     * 
     * @return the page number
     */
    public int getPage() 
    {
        return pageNo;
    }

    /**
     * Reads the visual signature from the given file.
     *
     * @param file the file containing the visual signature
     * @throws IOException when something went wrong during parsing
     */
    public void setVisualSignature(File file) throws IOException
    {
        PDFParser parser = new PDFParser(new RandomAccessBufferedFileInputStream(file));
        parser.parse();
        visualSignature = parser.getDocument();
    }

    /**
     * Reads the visual signature from the given input stream.
     *
     * @param is the input stream containing the visual signature
     * @throws IOException when something went wrong during parsing
     */
    public void setVisualSignature(InputStream is) throws IOException
    {
        PDFParser parser = new PDFParser(new RandomAccessBufferedFileInputStream(is));
        parser.parse();
        visualSignature = parser.getDocument();
    }
    
    /**
     * Reads the visual signature from the given visual signature properties
     *  
     * @param visSignatureProperties the <code>PDVisibleSigProperties</code> object containing the visual signature
     * 
     * @throws IOException when something went wrong during parsing
     */
    public void setVisualSignature(PDVisibleSigProperties visSignatureProperties) throws IOException
    { 
        setVisualSignature(visSignatureProperties.getVisibleSignature());
    }

    /**
     * Get the visual signature.
     * 
     * @return the visual signature
     */
    public COSDocument getVisualSignature()
    {
        return visualSignature;
    }
  
    /**
     * Get the preferred size of the signature.
     * 
     * @return the preferred size
     */
    public int getPreferedSignatureSize()
    {
      return preferedSignatureSize;
    }
  
    /**
     * Set the preferred size of the signature.
     * 
     * @param size the size of the signature
     */
    public void setPreferedSignatureSize(int size)
    {
        if (size > 0)
        {
            preferedSignatureSize = size;
        }
    }

    /**
     * Closes the visual signature COSDocument, if any.
     *
     * @throws IOException if the document could not be closed
     */
    @Override
    public void close() throws IOException
    {
        if (visualSignature != null)
        {
            visualSignature.close();
        }
    }
}
