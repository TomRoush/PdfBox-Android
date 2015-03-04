package org.apache.pdfbox.cos;


/**
 * Object representing the physical reference to an indirect pdf object.
 *
 * @author Michael Traut
 * @version $Revision: 1.5 $
 */
public class COSObjectKey implements Comparable<COSObjectKey>
{
    private long number;
    private long generation;

    /**
     * PDFObjectKey constructor comment.
     *
     * @param object The object that this key will represent.
     */
    public COSObjectKey(COSObject object)
    {
        this( object.getObjectNumber().longValue(), object.getGenerationNumber().longValue() );
    }

    /**
     * PDFObjectKey constructor comment.
     *
     * @param num The object number.
     * @param gen The object generation number.
     */
    public COSObjectKey(long num, long gen)
    {
        number = num;
        generation = gen;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj)
    {
        return (obj instanceof COSObjectKey) &&
               ((COSObjectKey)obj).getNumber() == getNumber() &&
               ((COSObjectKey)obj).getGeneration() == getGeneration();
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
     * {@inheritDoc}
     */
    @Override
    public int hashCode()
    {
        return (int)(number + generation);
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
     * {@inheritDoc}
     */
    @Override
    public String toString()
    {
        return "" + getNumber() + " " + getGeneration() + " R";
    }

    /** {@inheritDoc} */
    @Override
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