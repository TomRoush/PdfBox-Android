package com.tom_roush.pdfbox.pdmodel.fdf;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

/**
 * This class with handle some simple XML operations.
 *
 * @author Ben Litchfield
 */
final class XMLUtil
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
        catch (FactoryConfigurationError e )
        {
        	throw new IOException( e.getMessage(), e );
        }
        catch (ParserConfigurationException e)
        {
        	throw new IOException( e.getMessage(), e );
        }
        catch (SAXException e)
        {
        	throw new IOException( e.getMessage(), e );
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
    	StringBuilder sb = new StringBuilder();
    	NodeList children = node.getChildNodes();
    	int numNodes = children.getLength();
    	for( int i=0; i<numNodes; i++ )
    	{
            Node next = children.item( i );
            if( next instanceof Text )
            {
            	sb.append(next.getNodeValue());
            }
        }
    	return sb.toString();
    }
}
