package com.tom_roush.pdfbox.pdmodel.font;

import android.util.Log;

import com.tom_roush.pdfbox.cos.COSBase;
import com.tom_roush.pdfbox.cos.COSDictionary;
import com.tom_roush.pdfbox.cos.COSName;

import java.io.IOException;

/**
 * Creates the appropriate font subtype based on information in the dictionary.
 * @author Ben Litchfield
 */
public final class PDFontFactory
{
	private PDFontFactory()
	{
	}

	/**
	 * Creates a new PDFont instance with the appropriate subclass.
	 *
	 * @param dictionary a font dictionary
	 * @return a PDFont instance, based on the SubType entry of the dictionary
	 * @throws IOException if something goes wrong
	 */
	public static PDFont createFont(COSDictionary dictionary) throws IOException
	{
		COSName type = dictionary.getCOSName(COSName.TYPE, COSName.FONT);
		if (!COSName.FONT.equals(type))
		{
			Log.e("PdfBox-Android", "Expected 'Font' dictionary but found '" + type.getName() + "'");
		}

		COSName subType = dictionary.getCOSName(COSName.SUBTYPE);
		if (COSName.TYPE1.equals(subType))
		{
			COSBase fd = dictionary.getDictionaryObject(COSName.FONT_DESC);
			if (fd instanceof COSDictionary && ((COSDictionary) fd).containsKey(COSName.FONT_FILE3))
			{
				return new PDType1CFont(dictionary);
			}
			return new PDType1Font(dictionary);
		}
		else if (COSName.MM_TYPE1.equals(subType))
		{
			COSBase fd = dictionary.getDictionaryObject(COSName.FONT_DESC);
			if (fd instanceof COSDictionary && ((COSDictionary)fd).containsKey(COSName.FONT_FILE3))
			{
				return new PDType1CFont(dictionary);
			}
			return new PDMMType1Font(dictionary);
		}
		else if (COSName.TRUE_TYPE.equals(subType))
		{
			return new PDTrueTypeFont(dictionary);
		}
		else if (COSName.TYPE3.equals(subType))
		{
			return new PDType3Font(dictionary);
		}
		else if (COSName.TYPE0.equals(subType))
		{
			return new PDType0Font(dictionary);
		}
		else if (COSName.CID_FONT_TYPE0.equals(subType))
		{
			throw new IllegalArgumentException("Type 0 descendant font not allowed");
		}
		else if (COSName.CID_FONT_TYPE2.equals(subType))
		{
			throw new IllegalArgumentException("Type 2 descendant font not allowed");
		}
		else
		{
			// assuming Type 1 font (see PDFBOX-1988) because it seems that Adobe Reader does this
			// however, we may need more sophisticated logic perhaps looking at the FontFile
			Log.w("PdfBox-Android", "Invalid font subtype '" + subType + "'");
			return new PDType1Font(dictionary);
		}
	}

	/**
	 * Creates a new PDCIDFont instance with the appropriate subclass.
	 *
	 * @param dictionary descendant font dictionary
	 * @return a PDCIDFont instance, based on the SubType entry of the dictionary
	 * @throws IOException if something goes wrong
	 */
	static PDCIDFont createDescendantFont(COSDictionary dictionary, PDType0Font parent)
			throws IOException
	{
		COSName type = dictionary.getCOSName(COSName.TYPE, COSName.FONT);
		if (!COSName.FONT.equals(type))
		{
			throw new IllegalArgumentException("Expected 'Font' dictionary but found '" + type.getName() + "'");
		}

		COSName subType = dictionary.getCOSName(COSName.SUBTYPE);
		if (COSName.CID_FONT_TYPE0.equals(subType))
		{
			return new PDCIDFontType0(dictionary, parent);
		}
		else if (COSName.CID_FONT_TYPE2.equals(subType))
		{
			return new PDCIDFontType2(dictionary, parent);
		}
		else
		{
			throw new IOException("Invalid font type: " + type);
		}
	}

	/**
	 * Create a default font.
	 * 
	 * @return a default font
	 * @throws IOException if something goes wrong
	 */
	public static PDFont createDefaultFont() throws IOException
	{
		COSDictionary dict = new COSDictionary();
		dict.setItem(COSName.TYPE, COSName.FONT);
		dict.setItem(COSName.SUBTYPE, COSName.TRUE_TYPE);
		dict.setString(COSName.BASE_FONT, "Arial");
		return createFont(dict);
	}
}