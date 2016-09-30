package org.apache.pdfbox.pdmodel.font.encoding;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.apache.pdfbox.util.PDFBoxResourceLoader;

import android.util.Log;

/**
 * PostScript glyph list, maps glyph names to sequences of Unicode characters.
 * Instances of GlyphList are immutable.
 */
public final class GlyphList
{
	private static final GlyphList DEFAULT;
	private static final GlyphList ZAPF_DINGBATS;

	/**
	 * Returns the Adobe Glyph List (AGL).
	 */
	public static GlyphList getAdobeGlyphList()
	{
		return DEFAULT;
	}
	/**
	 * Returns the Zapf Dingbats glyph list.
	 */
	public static GlyphList getZapfDingbats()
	{
		return ZAPF_DINGBATS;
	}

	static
	{
		try
		{
			String path = "org/apache/pdfbox/resources/glyphlist/";
			// Adobe Glyph List (AGL)
			if(PDFBoxResourceLoader.isReady()) {
				DEFAULT = new GlyphList(PDFBoxResourceLoader.getStream(path + "glyphlist.txt"));
			} else {
				// Fallback
				ClassLoader loader = GlyphList.class.getClassLoader();
				DEFAULT = new GlyphList(loader.getResourceAsStream(path + "glyphlist.txt"));
			}
			
			// Zapf Dingbats has its own glyph list
			if(PDFBoxResourceLoader.isReady()) {
				ZAPF_DINGBATS = new GlyphList(PDFBoxResourceLoader.getStream(path + "zapfdingbats.txt"));
			} else {
				// Fallback
				ClassLoader loader = GlyphList.class.getClassLoader();
				ZAPF_DINGBATS = new GlyphList(loader.getResourceAsStream(path + "zapfdingbats.txt"));
			}
			
			// not supported in PDFBox 2.0, but we issue a warning, see PDFBOX-2379
			try
			{
				String location = System.getProperty("glyphlist_ext");
				if (location != null)
				{
					throw new UnsupportedOperationException("glyphlist_ext is no longer supported, "
							+ "use GlyphList.DEFAULT.addGlyphs(Properties) instead");
				}
			}
			catch (SecurityException e) // can occur on System.getProperty
			{
				// PDFBOX-1946 ignore and continue
			}
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
	}

	private final Map<String, String> nameToUnicode;
	private final Map<String, String> unicodeToName;

	/**
	 * Creates a new GlyphList from a glyph list file.
	 *
	 * @param input glyph list in Adobe format
	 * @throws IOException if the glyph list could not be read
	 */
	public GlyphList(InputStream input) throws IOException
	{
		nameToUnicode = new HashMap<String, String>();
		unicodeToName = new HashMap<String, String>();
		loadList(input);
	}
	/**
	 * Creates a new GlyphList from multiple glyph list files.
	 *
	 * @param glyphList an existing glyph list to be copied
	 * @param input glyph list in Adobe format
	 * @throws IOException if the glyph list could not be read
	 */
	public GlyphList(GlyphList glyphList, InputStream input) throws IOException
	{
		nameToUnicode = new HashMap<String, String>(glyphList.nameToUnicode);
		unicodeToName = new HashMap<String, String>(glyphList.unicodeToName);
		loadList(input);
	}

	private void loadList(InputStream input) throws IOException
	{
		BufferedReader in = new BufferedReader(new InputStreamReader(input));
		try
		{
			String line = null;
			while ((line = in.readLine()) != null)
			{
				if (!line.startsWith("#"))
				{
					String[] parts = line.split(";");
					if (parts.length < 2)
					{
						throw new IOException("Invalid glyph list entry: " + line);
					}
					String name = parts[0];
					String[] unicodeList = parts[1].split(" ");
					if (nameToUnicode.containsKey(name))
					{
						Log.w("PdfBoxAndroid", "duplicate value for " + name + " -> " + parts[1] + " " +
								nameToUnicode.get(name));
					}
					int[] codePoints = new int[unicodeList.length];
					int index = 0;
					for (String hex : unicodeList)
					{
						codePoints[index++] = Integer.parseInt(hex, 16);
					}
					String string = new String(codePoints, 0 , codePoints.length);
					// forward mapping
					nameToUnicode.put(name, string);
					// reverse mapping
					if (!unicodeToName.containsKey(string))
					{
						unicodeToName.put(string, name);
					}
				}
			}
		}
		finally
		{
			in.close();
		}
	}

	/**
	 * Returns the name for the given Unicode code point.
	 *
	 * @param codePoint Unicode code point
	 * @return PostScript glyph name, or ".notdef"
	 */
	public String codePointToName(int codePoint)
	{
		String name = unicodeToName.get(new String(new int[] { codePoint }, 0 , 1));
		if (name == null)
		{
			return ".notdef";
		}
		return name;
	}

	/**
	 * Returns the name for a given sequence of Unicode characters.
	 *
	 * @param unicodeSequence sequence of Unicode characters
	 * @return PostScript glyph name, or ".notdef"
	 */
	public String sequenceToName(String unicodeSequence)
	{
		String name = unicodeToName.get(unicodeSequence);
		if (name == null)
		{
			return ".notdef";
		}
		return name;
	}

	/**
	 * Returns the Unicode character sequence for the given glyph name, or null if there isn't any.
	 *
	 * @param name PostScript glyph name
	 * @return Unicode character(s), or null.
	 */
	public String toUnicode(String name)
	{
		if (name == null)
		{
			return null;
		}

		String unicode = nameToUnicode.get(name);
		if (unicode == null)
		{
			// test if we have a suffix and if so remove it
			if (name.indexOf('.') > 0)
			{
				unicode = toUnicode(name.substring(0, name.indexOf('.')));
			}
			else if (name.startsWith("uni") && name.length() == 7)
			{
				// test for Unicode name in the format uniXXXX where X is hex
				int nameLength = name.length();
				StringBuilder uniStr = new StringBuilder();
				try
				{
					for (int chPos = 3; chPos + 4 <= nameLength; chPos += 4)
					{
						int codePoint = Integer.parseInt(name.substring(chPos, chPos + 4), 16);
						if (codePoint > 0xD7FF && codePoint < 0xE000)
						{
							Log.w("PdfBoxAndroid", "Unicode character name with disallowed code area: " + name);
						}
						else
						{
							uniStr.append((char) codePoint);
						}
					}
					unicode = uniStr.toString();
				}
				catch (NumberFormatException nfe)
				{
					Log.w("PdfBoxAndroid", "Not a number in Unicode character name: " + name);
				}
			}
			else if (name.startsWith("u") && name.length() == 5)
			{
				// test for an alternate Unicode name representation uXXXX
				try
				{
					int codePoint = Integer.parseInt(name.substring(1), 16);
					if (codePoint > 0xD7FF && codePoint < 0xE000)
					{
						Log.w("PdfBoxAndroid", "Unicode character name with disallowed code area: " + name);
					}
					else
					{
						unicode = String.valueOf((char) codePoint);
					}
				}
				catch (NumberFormatException nfe)
				{
					Log.w("PdfBoxAndroid", "Not a number in Unicode character name: " + name);
				}
			}
			nameToUnicode.put(name, unicode);
		}
		return unicode;
	}
}
