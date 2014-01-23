package org.apache.pdfboxandroid.persistence.util;

public class COSObjectKey implements Comparable<COSObjectKey> {
	private long number;
    private long generation;
    
    /**
     * PDFObjectKey constructor comment.
     *
     * @param num The object number.
     * @param gen The object generation number.
     */
    public COSObjectKey(long num, long gen)
    {
        setNumber(num);
        setGeneration(gen);
    }
    
    /**
     * This will set the objects generation number.
     *
     * @param newGeneration The objects generation number.
     */
    public void setGeneration(long newGeneration)
    {
        generation = newGeneration;
    }
    
    /**
     * This will set the objects id.
     *
     * @param newNumber The objects number.
     */
    public void setNumber(long newNumber)
    {
        number = newNumber;
    }
	
	/**
     * This will get the objects id.
     *
     * @return The object's id.
     */
    public long getNumber()
    {
        return number;
    }
    
    /**
     * This will get the generation number.
     *
     * @return The objects generation number.
     */
    public long getGeneration()
    {
        return generation;
    }
	
	/** {@inheritDoc} */
    public int compareTo(COSObjectKey other)
    {
        if (getNumber() < other.getNumber())
        {
            return -1;
        }
        else if (getNumber() > other.getNumber())
        {
            return 1;
        }
        else
        {
            if (getGeneration() < other.getGeneration())
            {
                return -1;
            }
            else if (getGeneration() > other.getGeneration())
            {
                return 1;
            }
            else
            {
                return 0;
            }
        }
    }
}
