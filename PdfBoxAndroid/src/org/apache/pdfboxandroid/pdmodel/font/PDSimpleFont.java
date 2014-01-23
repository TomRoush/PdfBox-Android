package org.apache.pdfboxandroid.pdmodel.font;

import java.io.IOException;
import java.util.HashMap;

import org.apache.fontbox.util.ResourceLoader;
import org.apache.pdfboxandroid.PDFBox;
import org.apache.pdfboxandroid.cos.COSArray;
import org.apache.pdfboxandroid.cos.COSBase;
import org.apache.pdfboxandroid.cos.COSDictionary;
import org.apache.pdfboxandroid.cos.COSName;
import org.apache.pdfboxandroid.cos.COSNumber;
import org.apache.pdfboxandroid.cos.COSStream;
import org.apache.pdfboxandroid.encoding.DictionaryEncoding;
import org.apache.pdfboxandroid.encoding.Encoding;
import org.apache.pdfboxandroid.encoding.EncodingManager;

import android.util.Log;

/**
 * This class contains implementation details of the simple pdf fonts.
 *
 * @author <a href="mailto:ben@benlitchfield.com">Ben Litchfield</a>
 * @version $Revision: 1.18 $
 */
public class PDSimpleFont extends PDFont {
	private final HashMap<Integer, Float> mFontSizes =
			new HashMap<Integer, Float>(128);
	
	private float avgFontWidth = 0.0f;

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
						Log.d("Debug: Could not find encoding for " + encodingName , PDFBox.LOG_TAG);
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
						cmap = parseCmap( null, encodingStream.getUnfilteredStream() );
					}
					catch(IOException exception) 
					{
						Log.e("Error: Could not parse the embedded CMAP" , PDFBox.LOG_TAG);
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
					Log.e("Error: Could not create the DictionaryEncoding" , PDFBox.LOG_TAG);
				}
			}
		}
		setFontEncoding(fontEncoding);
		extractToUnicodeEncoding();

		if (cmap == null && cmapName != null) 
		{
			String resourceName = resourceRootCMAP + cmapName;
			try 
			{
				cmap = parseCmap( resourceRootCMAP, ResourceLoader.loadResource( resourceName ) );
				if( cmap == null && encodingName == null)
				{
					Log.e("Error: Could not parse predefined CMAP file for '" + cmapName + "'" , PDFBox.LOG_TAG);
				}
			}
			catch(IOException exception) 
			{
				Log.e("Error: Could not find predefined CMAP file for '" + cmapName + "'" , PDFBox.LOG_TAG);
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
					toUnicodeCmap = parseCmap( resourceRootCMAP, ((COSStream)toUnicode).getUnfilteredStream());
				}
				catch(IOException exception) 
				{
					Log.e("Error: Could not load embedded ToUnicode CMap" , PDFBox.LOG_TAG);
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
						Log.e("Error: Could not find predefined ToUnicode CMap file for '" + cmapName + "'" , PDFBox.LOG_TAG);
					}
					if( toUnicodeCmap == null)
					{
						Log.e("Error: Could not parse predefined ToUnicode CMap file for '" + cmapName + "'" , PDFBox.LOG_TAG);
					}
				}
			}
		}
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
}
