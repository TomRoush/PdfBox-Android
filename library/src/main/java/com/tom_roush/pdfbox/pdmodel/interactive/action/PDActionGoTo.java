package com.tom_roush.pdfbox.pdmodel.interactive.action;

import com.tom_roush.pdfbox.cos.COSArray;
import com.tom_roush.pdfbox.cos.COSBase;
import com.tom_roush.pdfbox.cos.COSDictionary;
import com.tom_roush.pdfbox.cos.COSName;
import com.tom_roush.pdfbox.pdmodel.interactive.documentnavigation.destination.PDDestination;
import com.tom_roush.pdfbox.pdmodel.interactive.documentnavigation.destination.PDPageDestination;

import java.io.IOException;

/**
 * This represents a go-to action that can be executed in a PDF document.
 *
 * @author Ben Litchfield
 * @author Panagiotis Toumasis
 */
public class PDActionGoTo extends PDAction
{
    /**
     * This type of action this object represents.
     */
    public static final String SUB_TYPE = "GoTo";

    /**
     * Default constructor.
     */
    public PDActionGoTo()
    {
        super();
        setSubType( SUB_TYPE );
    }

    /**
     * Constructor.
     *
     * @param a The action dictionary.
     */
    public PDActionGoTo( COSDictionary a )
    {
        super( a );
    }

    /**
     * This will get the destination to jump to.
     *
     * @return The D entry of the specific go-to action dictionary.
     *
     * @throws IOException If there is an error creating the destination.
     */
    public PDDestination getDestination() throws IOException
    {
        return PDDestination.create(getCOSObject().getDictionaryObject(COSName.D));
    }

    /**
     * This will set the destination to jump to.
     *
     * @param d The destination.
     *
     * @throws IllegalArgumentException if the destination is not a page dictionary object.
     */
    public void setDestination( PDDestination d )
    {
        if (d instanceof PDPageDestination)
        {
            PDPageDestination pageDest = (PDPageDestination) d;
            COSArray destArray = pageDest.getCOSObject();
            if (destArray.size() >= 1)
            {
                COSBase page = destArray.getObject(0);
                if (!(page instanceof COSDictionary))
                {
                    throw new IllegalArgumentException("Destination of a GoTo action must be " +
                        "a page dictionary object");
                }
            }
        }
        getCOSObject().setItem(COSName.D, d);
    }
}
