package org.apache.pdfboxandroid.cos;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.pdfboxandroid.exceptions.COSVisitorException;
import org.apache.pdfboxandroid.persistence.util.COSHEXTable;

/**
 * This class represents a PDF named object.
 *
 * @author <a href="mailto:ben@benlitchfield.com">Ben Litchfield</a>
 * 
 */
public final class COSName extends COSBase implements Comparable<COSName> {
	/**
	 * Note: This is a ConcurrentHashMap because a HashMap must be synchronized if accessed by
	 * multiple threads.
	 */
	private static Map<String, COSName> nameMap = new ConcurrentHashMap<String, COSName>(8192);

	/**
	 * All common COSName values are stored in a simple HashMap. They are already defined as
	 * static constants and don't need to be synchronized for multithreaded environments.
	 */
	private static Map<String, COSName> commonNameMap =
			new HashMap<String, COSName>();

	/**
	 * A common COSName value.
	 */
	public static final COSName AA = new COSName( "AA" );
	/**
	 * A common COSName value.
	 */
	public static final COSName ACRO_FORM = new COSName( "AcroForm" );
	/**
	 * A common COSName value.
	 */
	public static final COSName ANNOT = new COSName( "Annot" );
	/**
	 * A common COSName value.
	 */
	public static final COSName AP = new COSName( "AP" );
	/**
	 * A common COSName value.
	 */
	public static final COSName ASCENT = new COSName( "Ascent" );
	/**
	 * A common COSName value.
	 */
	public static final COSName BASE_ENCODING = new COSName( "BaseEncoding" );
	/**
	 * A common COSName value.
	 */
	public static final COSName BASE_FONT = new COSName( "BaseFont" );
	/**
	 * A common COSName value.
	 */
	public static final COSName BBOX = new COSName( "BBox" );
	/**
	 * A common COSName value.
	 */
	public static final COSName BITS_PER_COMPONENT = new COSName("BitsPerComponent" );
	/**
	 * A common COSName value.
	 */
	public static final COSName BYTERANGE = new COSName("ByteRange");
	/**
	 * A common COSName value.
	 */
	public static final COSName CAP_HEIGHT = new COSName( "CapHeight" );
	/**
	 * A common COSName value.
	 */
	public static final COSName CATALOG = new COSName( "Catalog" );
	/**
	 * A common COSName value.
	 */
	public static final COSName CID_FONT_TYPE0 = new COSName( "CIDFontType0" );
	/**
	 * A common COSName value.
	 */
	public static final COSName CID_FONT_TYPE2 = new COSName( "CIDFontType2" );
	/**
	 * A common COSName value.
	 */
	public static final COSName COLORS = new COSName( "Colors" );
	/**
	 * A common COSName value.
	 */
	public static final COSName COLUMNS = new COSName( "Columns" );
	/**
	 * A common COSName value.
	 */
	public static final COSName CONTENTS = new COSName( "Contents" );
	/**
	 * A common COSName value.
	 */
	public static final COSName DA = new COSName( "DA" );
	/**
	 * A common COSName value.
	 */
	public static final COSName DECODE_PARMS = new COSName( "DecodeParms" );
	/**
	 * A common COSName value.
	 */
	public static final COSName DEFAULT = new COSName( "default" );
	/**
	 * A common COSName value.
	 */
	public static final COSName DESCENT = new COSName(  "Descent" );
	/**
	 * A common COSName value.
	 */
	public static final COSName DESCENDANT_FONTS = new COSName(  "DescendantFonts" );
	/**
	 * A common COSName value.
	 */
	public static final COSName DIFFERENCES = new COSName( "Differences" );
	/**
	 * A common COSName value.
	 */
	public static final COSName DOC_CHECKSUM = new COSName( "DocChecksum" );
	/**
	 * A common COSName value.
	 */
	public static final COSName DOC_TIME_STAMP = new COSName( "DocTimeStamp" );
	/**
	 * A common COSName value.
	 */
	public static final COSName DP = new COSName( "DP" );
	/**
	 * A common COSName value.
	 */
	public static final COSName ENCODING = new COSName( "Encoding" );
	/**
	 * A common COSName value.
	 */
	public static final COSName FF = new COSName( "Ff" );
	/**
	 * A common COSName value.
	 */
	public static final COSName FIELDS = new COSName( "Fields" );
	/**
	 * A common COSName value.
	 */
	public static final COSName FILTER = new COSName( "Filter" );
	/**
	 * A common COSName value.
	 */
	public static final COSName FIRST_CHAR = new COSName( "FirstChar" );
	/**
	 * A common COSName value.
	 */
	public static final COSName FLAGS = new COSName( "Flags" );
	/**
	 * A common COSName value.
	 */
	public static final COSName FLATE_DECODE = new COSName( "FlateDecode" );
	/**
	 * A common COSName value.
	 */
	public static final COSName FONT = new COSName( "Font" );
	/**
	 * A common COSName value.
	 */
	public static final COSName FONT_BBOX = new COSName( "FontBBox" );
	/**
	 * A common COSName value.
	 */
	public static final COSName FONT_DESC = new COSName("FontDescriptor");
	/**
	 * A common COSName value.
	 */
	public static final COSName FONT_FAMILY = new COSName("FontFamily");
	/**
	 * A common COSName value.
	 */
	public static final COSName FONT_FILE3 = new COSName("FontFile3");
	/**
	 * A common COSName value.
	 */
	public static final COSName FONT_NAME = new COSName("FontName" );
	/**
	 * A common COSName value.
	 */
	public static final COSName FONT_STRETCH = new COSName("FontStretch" );
	/**
	 * A common COSName value.
	 */
	public static final COSName FONT_WEIGHT = new COSName("FontWeight" );
	/**
	 * A common COSName value.
	 */
	public static final COSName FT = new COSName( "FT" );
	/**
	 * A common COSName value.
	 */
	public static final COSName I = new COSName("I");
	/**
	 * A common COSName value.
	 */
	public static final COSName ID = new COSName("ID");
	/**
	 * A common COSName value.
	 */
	public static final COSName INDEX = new COSName( "Index" );
	/**
	 * A common COSName value.
	 */
	public static final COSName INFO = new COSName( "Info" );
	/**
	 * A common COSName value.
	 */
	public static final COSName ITALIC_ANGLE = new COSName( "ItalicAngle" );
	/**
	 * A common COSName value.
	 */
	public static final COSName KIDS = new COSName( "Kids" );
	/**
	 * A common COSName value.
	 */
	public static final COSName LAST_CHAR = new COSName( "LastChar" );
	/**
	 * A common COSName value.
	 */
	public static final COSName LENGTH = new COSName( "Length" );
	/**
	 * A common COSName value.
	 */
	public static final COSName MAC_ROMAN_ENCODING = new COSName( "MacRomanEncoding" );
	/**
	 * A common COSName value.
	 */
	public static final COSName MISSING_WIDTH = new COSName( "MissingWidth" );
	/**
	 * A common COSName value.
	 */
	public static final COSName MM_TYPE1 = new COSName(  "MMType1" );
	/**
	 * A common COSName value.
	 */
	public static final COSName N = new COSName( "N" );
	/**
	 * A common COSName value.
	 */
	public static final COSName OPT = new COSName( "Opt" );
	/**
	 * A common COSName value.
	 */
	public static final COSName P = new COSName( "P" );
	/**
	 * A common COSName value.
	 */
	public static final COSName PARENT = new COSName( "Parent" );
	/**
	 * A common COSName value.
	 */
	public static final COSName PDF_DOC_ENCODING = new COSName( "PDFDocEncoding" );
	/**
	 * A common COSName value.
	 */
	public static final COSName PREDICTOR = new COSName( "Predictor" );
	/**
	 * A common COSName value.
	 */
	public static final COSName PREV = new COSName( "Prev" );
	/**
	 * A common COSName value.
	 */
	public static final COSName Q = new COSName( "Q" );
	/**
	 * A common COSName value.
	 */
	public static final COSName RECT = new COSName( "Rect" );
	/**
	 * A common COSName value.
	 */
	public static final COSName RESOURCES = new COSName( "Resources" );
	/**
	 * A common COSName value.
	 */
	public static final COSName ROOT = new COSName( "Root" );
	/**
	 * A common COSName value.
	 */
	public static final COSName SIG = new COSName("Sig");
	/**
	 * A common COSName value.
	 */
	public static final COSName SIZE = new COSName( "Size" );
	/**
	 * A common COSName value.
	 */
	public static final COSName STANDARD_ENCODING = new COSName( "StandardEncoding" );
	/**
	 * A common COSName value.
	 */
	public static final COSName STEM_V = new COSName( "StemV" );
	/**
	 * A common COSName value.
	 */
	public static final COSName SUBTYPE = new COSName( "Subtype" );
	/**
	 * A common COSName value.
	 */
	public static final COSName TO_UNICODE = new COSName( "ToUnicode" );
	/**
	 * A common COSName value.
	 */
	public static final COSName TRUE_TYPE = new COSName("TrueType" );
	/**
	 * A common COSName value.
	 */
	public static final COSName TYPE = new COSName( "Type" );
	/**
	 * A common COSName value.
	 */
	public static final COSName TYPE0 = new COSName(  "Type0" );
	/**
	 * A common COSName value.
	 */
	public static final COSName TYPE1 = new COSName(  "Type1" );
	/**
	 * A common COSName value.
	 */
	public static final COSName TYPE3 = new COSName(  "Type3" );
	/**
	 * A common COSName value.
	 */
	public static final COSName V = new COSName( "V" );
	/**
	 * A common COSName value.
	 */
	public static final COSName VERSION = new COSName( "Version" );
	/**
	 * A common COSName value.
	 */
	public static final COSName W = new COSName( "W" );
	/**
	 * A common COSName value.
	 */
	public static final COSName WIDTHS = new COSName( "Widths" );
	/**
	 * A common COSName value.
	 */
	public static final COSName WIN_ANSI_ENCODING = new COSName( "WinAnsiEncoding" );
	/**
	 * A common COSName value.
	 */
	public static final COSName XHEIGHT = new COSName( "XHeight" );
	/**
	 * A common COSName value.
	 */
	public static final COSName XOBJECT = new COSName( "XObject" );
	/**
	 * A common COSName value.
	 */
	public static final COSName XREF = new COSName( "XRef" );
	/**
	 * A common COSName value.
	 */
	public static final COSName XREF_STM = new COSName( "XRefStm" );

	/**
	 * The prefix to a PDF name.
	 */
	public static final byte[] NAME_PREFIX = new byte[] { 47  }; // The / character
	/**
	 * The escape character for a name.
	 */
	public static final byte[] NAME_ESCAPE = new byte[] { 35  };  //The # character

	private String name;
	private int hashCode;

	/**
	 * Private constructor.  This will limit the number of COSName objects.
	 * that are created.
	 *
	 * @param aName The name of the COSName object.
	 * @param staticValue Indicates if the COSName object is static so that it can
	 *        be stored in the HashMap without synchronizing.
	 */
	private COSName( String aName, boolean staticValue )
	{
		name = aName;
		if ( staticValue )
		{
			commonNameMap.put( aName, this);
		}
		else
		{
			nameMap.put( aName, this );
		}
		hashCode = name.hashCode();
	}

	/**
	 * Private constructor.  This will limit the number of COSName objects.
	 * that are created.
	 *
	 * @param aName The name of the COSName object.
	 */
	private COSName( String aName )
	{
		this( aName, true );
	}

	/**
	 * This will get the name of this COSName object.
	 *
	 * @return The name of the object.
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * This will get a COSName object with that name.
	 *
	 * @param aName The name of the object.
	 *
	 * @return A COSName with the specified name.
	 */
	public static final COSName getPDFName( String aName )
	{
		COSName name = null;
		if( aName != null )
		{
			// Is it a common COSName ??
			name = commonNameMap.get( aName );
			if( name == null )
			{
				// It seems to be a document specific COSName
				name = nameMap.get( aName );
				if( name == null )
				{
					//name is added to the synchronized map in the constructor
					name = new COSName( aName, false );
				}
			}
		}
		return name;
	}

	/**
	 * {@inheritDoc}
	 */
	public int compareTo(COSName other)
	{
		return this.name.compareTo( other.name );
	}

	/**
	 * This will output this string as a PDF object.
	 *
	 * @param output The stream to write to.
	 * @throws IOException If there is an error writing to the stream.
	 */
	public void writePDF( OutputStream output ) throws IOException
	{
		output.write(NAME_PREFIX);
		byte[] bytes = getName().getBytes("ISO-8859-1");
		for (int i = 0; i < bytes.length;i++)
		{
			int current = ((bytes[i]+256)%256);

			if(current <= 32 || current >= 127 ||
					current == '(' ||
					current == ')' ||
					current == '[' ||
					current == ']' ||
					current == '/' ||
					current == '%' ||
					current == '<' ||
					current == '>' ||
					current == NAME_ESCAPE[0] )
			{
				output.write(NAME_ESCAPE);
				output.write(COSHEXTable.TABLE[current]);
			}
			else
			{
				output.write(current);
			}
		}
	}

	/**
	 * visitor pattern double dispatch method.
	 *
	 * @param visitor The object to notify when visiting this object.
	 * @return any object, depending on the visitor implementation, or null
	 * @throws COSVisitorException If an error occurs while visiting this object.
	 */
	public Object accept(ICOSVisitor  visitor) throws COSVisitorException
	{
		return visitor.visitFromName(this);
	}
}
