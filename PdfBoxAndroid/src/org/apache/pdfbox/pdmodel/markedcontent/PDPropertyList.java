package org.apache.pdfbox.pdmodel.markedcontent;

import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.common.COSObjectable;
import org.apache.pdfbox.pdmodel.graphics.optionalcontent.PDOptionalContentGroup;

/**
 * This class represents a property list used for the marked content feature to map a resource name
 * to a dictionary.
 *
 * @since PDF 1.2
 * @version $Revision$
 */
public class PDPropertyList implements COSObjectable
{

    private COSDictionary props;

    /**
     * Creates a new property list.
     */
    public PDPropertyList()
    {
        this.props = new COSDictionary();
    }

    /**
     * Creates a new instance based on a given {@link COSDictionary}.
     * @param dict the dictionary
     */
    public PDPropertyList(COSDictionary dict)
    {
        this.props = dict;
    }

    /** {@inheritDoc} */
    public COSBase getCOSObject()
    {
        return this.props;
    }

    /**
     * Returns the optional content group belonging to the given resource name.
     * @param name the resource name
     * @return the optional content group or null if the group was not found
     */
    public PDOptionalContentGroup getOptionalContentGroup(COSName name)
    {
        COSDictionary dict = (COSDictionary)props.getDictionaryObject(name);
        if (dict != null)
        {
            if (COSName.OCG.equals(dict.getItem(COSName.TYPE)))
            {
                return new PDOptionalContentGroup(dict);
            }
        }
        return null;
    }

    /**
     * Puts a mapping from a resource name to an optional content group.
     * @param name the resource name
     * @param ocg the optional content group
     */
    public void putMapping(COSName name, PDOptionalContentGroup ocg)
    {
        putMapping(name, (COSDictionary)ocg.getCOSObject());
    }

    private void putMapping(COSName name, COSDictionary dict)
    {
        props.setItem(name, dict);
    }

}
