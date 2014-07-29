package org.apache.pdfbox.pdmodel.font;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSNumber;
import org.apache.pdfbox.cos.COSStream;
import org.apache.pdfbox.encoding.DictionaryEncoding;
import org.apache.pdfbox.encoding.Encoding;
import org.apache.pdfbox.encoding.EncodingManager;
import org.apache.pdfbox.io.IOUtils;
import org.apache.pdfbox.util.ResourceLoader;

/**
 * This class contains implementation details of the simple pdf fonts.
 *
 * @author <a href="mailto:ben@benlitchfield.com">Ben Litchfield</a>
 * @version $Revision: 1.18 $
 */
public abstract class PDSimpleFont extends PDFont
{

	private final HashMap<Integer, Float> mFontSizes =
			new HashMap<Integer, Float>(128);

	private float avgFontWidth = 0.0f;
	private float avgFontHeight = 0.0f;
	private float fontWidthOfSpace = -1f; 

	private static final byte[] SPACE_BYTES = { (byte)32 };


	/**
	 * Log instance.
	 */
	private static final Log LOG = LogFactory.getLog(PDSimpleFont.class);

	/**
	 * Constructor.
	 */
	public PDSimpleFont()
	{
		super();
	}

	/**
	 * Constructor.
	 *
	 * @param fontDictionary The font dictionary according to the PDF specification.
	 */
	public PDSimpleFont( COSDictionary fontDictionary )
	{
		super( fontDictionary );
	}

	/**
	 * This will get the font width for a character.
	 *
	 * @param c The character code to get the width for.
	 * @param offset The offset into the array.
	 * @param length The length of the data.
	 *
	 * @return The width is in 1000 unit of text space, ie 333 or 777
	 *
	 * @throws IOException If an error occurs while parsing.
	 */
	public float getFontWidth( byte[] c, int offset, int length ) throws IOException
	{
		int code = getCodeFromArray( c, offset, length );
		Float fontWidth = mFontSizes.get(code);
		if (fontWidth == null)
		{
			fontWidth = getFontWidth(code);
			if (fontWidth <= 0)
			{
				//hmm should this be in PDType1Font??
				fontWidth = getFontWidthFromAFMFile( code );
			}
			mFontSizes.put(code, fontWidth);
		}
		return fontWidth;
	}

	/**
	 * This will get the average font width for all characters.
	 *
	 * @return The width is in 1000 unit of text space, ie 333 or 777
	 *
	 * @throws IOException If an error occurs while parsing.
	 */
	public float getAverageFontWidth() throws IOException
	{
		float average = 0.0f;

		//AJW
		if (avgFontWidth != 0.0f)
		{
			average = avgFontWidth;
		}
		else
		{
			float totalWidth = 0.0f;
			float characterCount = 0.0f;
			COSArray widths = (COSArray)font.getDictionaryObject( COSName.WIDTHS );
			if( widths != null )
			{
				for( int i=0; i<widths.size(); i++ )
				{
					COSNumber fontWidth = (COSNumber)widths.getObject( i );
					if( fontWidth.floatValue() > 0 )
					{
						totalWidth += fontWidth.floatValue();
						characterCount += 1;
					}
				}
			}

			if( totalWidth > 0 )
			{
				average = totalWidth / characterCount;
			}
			else
			{
				average = getAverageFontWidthFromAFMFile();
			}
			avgFontWidth = average;
		}
		return average;
	}
	
	/**
     * This will get the ToUnicode object.
     *
     * @return The ToUnicode object.
     */
    public COSBase getToUnicode()
    {
        return font.getDictionaryObject( COSName.TO_UNICODE );
    }

	/**
	 * {@inheritDoc}
	 */
	protected void determineEncoding()
	{
		String cmapName = null;
		COSName encodingName = null;
		COSBase encoding = getEncoding(); 
		Encoding fontEncoding = null;
		if (encoding != null) 
		{
			if (encoding instanceof COSName) 
			{
				if (cmap == null)
				{
					encodingName = (COSName)encoding;
					cmap = cmapObjects.get( encodingName.getName() );
					if (cmap == null) 
					{
						cmapName = encodingName.getName();
					}
				}
				if (cmap == null && cmapName != null)
				{
					try 
					{
						fontEncoding =
								EncodingManager.INSTANCE.getEncoding(encodingName);
					}
					catch(IOException exception) 
					{
						LOG.debug("Debug: Could not find encoding for " + encodingName );
					}
				}
			}
			else if(encoding instanceof COSStream )
			{
				if (cmap == null)
				{
					COSStream encodingStream = (COSStream)encoding;
					try 
					{
						InputStream is = encodingStream.getUnfilteredStream();
						cmap = parseCmap(null, is);
						IOUtils.closeQuietly(is);
					}
					catch(IOException exception) 
					{
						LOG.error("Error: Could not parse the embedded CMAP" );
					}
				}
			}
			else if (encoding instanceof COSDictionary) 
			{
				try 
				{
					fontEncoding = new DictionaryEncoding((COSDictionary)encoding);
				}
				catch(IOException exception) 
				{
					LOG.error("Error: Could not create the DictionaryEncoding" );
				}
			}
		}
		setFontEncoding(fontEncoding);
		extractToUnicodeEncoding();

		if (cmap == null && cmapName != null) 
		{
			InputStream cmapStream = null;
			try 
			{
				// look for a predefined CMap with the given name
				cmapStream = ResourceLoader.loadResource(resourceRootCMAP + cmapName);
				if (cmapStream != null)
				{
					cmap = parseCmap(resourceRootCMAP, cmapStream);
					if (cmap == null && encodingName == null)
					{
						LOG.error("Error: Could not parse predefined CMAP file for '" + cmapName + "'");
					}
				}
				else
				{
					LOG.debug("Debug: '" + cmapName + "' isn't a predefined map, most likely it's embedded in the pdf itself.");
				}
			}
			catch(IOException exception) 
			{
				LOG.error("Error: Could not find predefined CMAP file for '" + cmapName + "'" );
			}
			finally
			{
				IOUtils.closeQuietly(cmapStream);
			}
		}
	}

	private void extractToUnicodeEncoding()
	{
		COSName encodingName = null;
		String cmapName = null;
		COSBase toUnicode = getToUnicode();
		if( toUnicode != null )
		{
			setHasToUnicode(true);
			if ( toUnicode instanceof COSStream )
			{
				try 
				{
					InputStream is = ((COSStream) toUnicode).getUnfilteredStream();
					toUnicodeCmap = parseCmap(resourceRootCMAP, is);
					IOUtils.closeQuietly(is);
				}
				catch(IOException exception) 
				{
					LOG.error("Error: Could not load embedded ToUnicode CMap" );
				}
			}
			else if ( toUnicode instanceof COSName)
			{
				encodingName = (COSName)toUnicode;
				toUnicodeCmap = cmapObjects.get( encodingName.getName() );
				if (toUnicodeCmap == null) 
				{
					cmapName = encodingName.getName();
					String resourceName = resourceRootCMAP + cmapName;
					try 
					{
						toUnicodeCmap = parseCmap( resourceRootCMAP, ResourceLoader.loadResource( resourceName ));
					}
					catch(IOException exception) 
					{
						LOG.error("Error: Could not find predefined ToUnicode CMap file for '" + cmapName + "'" );
					}
					if( toUnicodeCmap == null)
					{
						LOG.error("Error: Could not parse predefined ToUnicode CMap file for '" + cmapName + "'" );
					}
				}
			}
		}
	}

}
