package org.apache.pdfbox.util;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

/**
 * This class with handle some simple XML operations.
 *
 * @author <a href="mailto:ben@benlitchfield.com">Ben Litchfield</a>
 * @version $Revision: 1.3 $
 */
public class XMLUtil
{
    /**
     * Utility class, should not be instantiated.
     *
     */
    private XMLUtil()
    {
    }

    /**
     * This will parse an XML stream and create a DOM document.
     *
     * @param is The stream to get the XML from.
     * @return The DOM document.
     * @throws IOException It there is an error creating the dom.
     */
    public static Document parse( InputStream is ) throws IOException
    {
        try
        {
            DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = builderFactory.newDocumentBuilder();
            return builder.parse( is );
        }
        catch( Exception e )
        {
            IOException thrown = new IOException( e.getMessage() );
            throw thrown;
        }
    }

    /**
     * This will get the text value of an element.
     *
     * @param node The node to get the text value for.
     * @return The text of the node.
     */
    public static String getNodeValue( Element node )
    {
        String retval = "";
        NodeList children = node.getChildNodes();
        for( int i=0; i<children.getLength(); i++ )
        {
            Node next = children.item( i );
            if( next instanceof Text )
            {
                retval = next.getNodeValue();
            }
        }
        return retval;
    }
}
