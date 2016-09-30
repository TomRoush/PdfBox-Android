package org.apache.pdfbox.pdmodel.font;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.fontbox.afm.AFMParser;
import org.apache.fontbox.afm.FontMetrics;
import org.apache.pdfbox.util.PDFBoxResourceLoader;

/**
 * The "Standard 14" PDF fonts, also known as the "base 14" fonts.
 * There are 14 font files, but Acrobat uses additional names for compatibility, e.g. Arial.
 *
 * @author John Hewson
 */
class Standard14Fonts
{
	private Standard14Fonts()
	{
	}

	private static final Set<String> STANDARD_14_NAMES = new HashSet<String>();
	private static final Map<String, String> STANDARD_14_MAPPING = new HashMap<String, String>();
	private static final Map<String, FontMetrics> STANDARD14_AFM_MAP;
	static
	{
		try
		{
			STANDARD14_AFM_MAP = new HashMap<String, FontMetrics>();
			addAFM("Courier-Bold");
			addAFM("Courier-BoldOblique");
			addAFM("Courier");
			addAFM("Courier-Oblique");
			addAFM("Helvetica");
			addAFM("Helvetica-Bold");
			addAFM("Helvetica-BoldOblique");
			addAFM("Helvetica-Oblique");
			addAFM("Symbol");
			addAFM("Times-Bold");
			addAFM("Times-BoldItalic");
			addAFM("Times-Italic");
			addAFM("Times-Roman");
			addAFM("ZapfDingbats");

			// alternative names from Adobe Supplement to the ISO 32000
			addAFM("CourierCourierNew", "Courier");
			addAFM("CourierNew", "Courier");
			addAFM("CourierNew,Italic", "Courier-Oblique");
			addAFM("CourierNew,Bold", "Courier-Bold");
			addAFM("CourierNew,BoldItalic", "Courier-BoldOblique");
			addAFM("Arial", "Helvetica");
			addAFM("Arial,Italic", "Helvetica-Oblique");
			addAFM("Arial,Bold", "Helvetica-Bold");
			addAFM("Arial,BoldItalic", "Helvetica-BoldOblique");
			addAFM("TimesNewRoman", "Times-Roman");
			addAFM("TimesNewRoman,Italic", "Times-Italic");
			addAFM("TimesNewRoman,Bold", "Times-Bold");
			addAFM("TimesNewRoman,BoldItalic", "Times-BoldItalic");

			// Acrobat treats these fonts as "standard 14" too (at least Acrobat preflight says so)
			addAFM("Symbol,Italic", "Symbol");
			addAFM("Symbol,Bold", "Symbol");
			addAFM("Symbol,BoldItalic", "Symbol");
			addAFM("Times", "Times-Roman");
			addAFM("Times,Italic", "Times-Italic");
			addAFM("Times,Bold", "Times-Bold");
			addAFM("Times,BoldItalic", "Times-BoldItalic");
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
	}

	private static void addAFM(String fontName) throws IOException
	{
		addAFM(fontName, fontName);
	}

	private static void addAFM(String fontName, String afmName) throws IOException
	{
		STANDARD_14_NAMES.add(fontName);
		STANDARD_14_MAPPING.put(fontName, afmName);

		if (STANDARD14_AFM_MAP.containsKey(afmName))
		{
			STANDARD14_AFM_MAP.put(fontName, STANDARD14_AFM_MAP.get(afmName));
		}

		String resourceName = "org/apache/pdfbox/resources/afm/" + afmName + ".afm";
		InputStream afmStream;
		if(PDFBoxResourceLoader.isReady()) {
			afmStream = PDFBoxResourceLoader.getStream(resourceName);
		} else {
			// Fallback
			URL url = PDType1Font.class.getClassLoader().getResource(resourceName);
			if (url != null)
			{
				afmStream = url.openStream();
			}
			else
			{
				throw new IOException(resourceName + " not found");
			}
		}
		
		try
		{
			AFMParser parser = new AFMParser(afmStream);
			FontMetrics metric = parser.parse();
			STANDARD14_AFM_MAP.put(fontName, metric);
		}
		finally
		{
			afmStream.close();
		}
	}

	/**
	 * Returns the AFM for the given font.
	 * @param baseName base name of font
	 */
	public static FontMetrics getAFM(String baseName)
	{
		return STANDARD14_AFM_MAP.get(baseName);
	}

	/**
	 * Returns true if the given font name a Standard 14 font.
	 * @param baseName base name of font
	 */
	public static boolean containsName(String baseName)
	{
		return STANDARD_14_NAMES.contains(baseName);
	}

	/**
	 * Returns the set of Standard 14 font names, including additional names.
	 */
	public static Set<String> getNames()
	{
		return Collections.unmodifiableSet(STANDARD_14_NAMES);
	}

	/**
	 * Returns the name of the actual font which the given font name maps to.
	 * @param baseName base name of font
	 */
	public static String getMappedFontName(String baseName)
	{
		return STANDARD_14_MAPPING.get(baseName);
	}
}
