package org.apache.pdfbox.pdmodel.font;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.fontbox.cff.CFFParser;
import org.apache.fontbox.cff.CFFType1Font;
import org.apache.fontbox.ttf.Type1Equivalent;
import org.apache.fontbox.util.BoundingBox;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.io.IOUtils;
import org.apache.pdfbox.pdmodel.common.PDStream;
import org.apache.pdfbox.pdmodel.font.encoding.Encoding;
import org.apache.pdfbox.pdmodel.font.encoding.Type1Encoding;
import org.apache.pdfbox.util.Matrix;
import org.apache.pdfbox.util.awt.AffineTransform;

import android.graphics.Path;
import android.graphics.PointF;
import android.util.Log;

/**
 * Type 1-equivalent CFF font.
 *
 * @author Villu Ruusmann
 * @author John Hewson
 */
public class PDType1CFont extends PDSimpleFont implements PDType1Equivalent
{
	private final Map<String, Float> glyphHeights = new HashMap<String, Float>();
	private Float avgWidth = null;
	private Matrix fontMatrix;
	private final AffineTransform fontMatrixTransform;

	private final CFFType1Font cffFont; // embedded font
	private final Type1Equivalent type1Equivalent; // embedded or system font for rendering
	private final boolean isEmbedded;
	private final boolean isDamaged;

	/**
	 * Constructor.
	 * 
	 * @param fontDictionary the corresponding dictionary
	 * @throws IOException it something went wrong
	 */
	public PDType1CFont(COSDictionary fontDictionary) throws IOException
	{
		super(fontDictionary);

		PDFontDescriptor fd = getFontDescriptor();
		byte[] bytes = null;
		if (fd != null)
		{
			PDStream ff3Stream = fd.getFontFile3();
			if (ff3Stream != null)
			{
				bytes = IOUtils.toByteArray(ff3Stream.createInputStream());
				if (bytes.length == 0)
				{
					Log.e("PdfBoxAndroid", "Invalid data for embedded Type1C font " + getName());
					bytes = null;
				}
			}
		}

		boolean fontIsDamaged = false;
		CFFType1Font cffEmbedded = null;
		try
		{
			if (bytes != null)
			{
				// note: this could be an OpenType file, fortunately CFFParser can handle that
				CFFParser cffParser = new CFFParser();
				cffEmbedded = (CFFType1Font)cffParser.parse(bytes).get(0);
			}
		}
		catch (IOException e)
		{
			Log.e("PdfBoxAndroid", "Can't read the embedded Type1C font " + getName(), e);
			fontIsDamaged = true;
		}
		isDamaged = fontIsDamaged;
		cffFont = cffEmbedded;

		if (cffFont != null)
		{
			type1Equivalent = cffFont;
			isEmbedded = true;
		}
		else
		{
			Type1Equivalent t1Equiv = ExternalFonts.getType1EquivalentFont(getBaseFont());
			if (t1Equiv != null)
			{
				type1Equivalent = t1Equiv;
			}
			else
			{
				type1Equivalent = ExternalFonts.getType1FallbackFont(getFontDescriptor());
				Log.w("PdfBoxAndroid", "Using fallback font " + type1Equivalent.getName() + " for " + getBaseFont());
			}
			isEmbedded = false;
		}
		readEncoding();
		fontMatrixTransform = getFontMatrix().createAffineTransform();
		fontMatrixTransform.scale(1000, 1000);
	}

	@Override
	public Type1Equivalent getType1Equivalent()
	{
		return type1Equivalent;
	}

	/**
	 * Returns the PostScript name of the font.
	 */
	public String getBaseFont()
	{
		return dict.getNameAsString(COSName.BASE_FONT);
	}

	@Override
	public Path getPath(String name) throws IOException
	{
		// Acrobat only draws .notdef for embedded or "Standard 14" fonts, see PDFBOX-2372
		if (isEmbedded() && name.equals(".notdef") && !isEmbedded() && !isStandard14())
		{
			return new Path();
		}
		else
		{
			return type1Equivalent.getPath(name);
		}
	}

	@Override
	public String getName()
	{
		return getBaseFont();
	}

	@Override
	public BoundingBox getBoundingBox() throws IOException
	{
		return type1Equivalent.getFontBBox();
	}

	@Override
	public String codeToName(int code)
	{
		return getEncoding().getName(code);
	}

	@Override
	protected Encoding readEncodingFromFont() throws IOException
	{
		return Type1Encoding.fromFontBox(type1Equivalent.getEncoding());
	}

	@Override
	public int readCode(InputStream in) throws IOException
	{
		return in.read();
	}

	@Override
	public Matrix getFontMatrix()
	{
		if (fontMatrix == null)
		{
			if (cffFont != null)
			{
				List<Number> numbers = cffFont.getFontMatrix();
				if (numbers != null && numbers.size() == 6)
				{
					fontMatrix = new Matrix(numbers.get(0).floatValue(), numbers.get(1).floatValue(),
							numbers.get(2).floatValue(), numbers.get(3).floatValue(),
							numbers.get(4).floatValue(), numbers.get(5).floatValue());
					return fontMatrix;
				}
			}
		}
		return fontMatrix;
	}

	@Override
	public boolean isDamaged()
	{
		return isDamaged;
	}

	@Override
	public float getWidthFromFont(int code) throws IOException
	{
		if (getStandard14AFM() != null)
		{
			return getStandard14Width(code);
		}

		String name = codeToName(code);
		float width = type1Equivalent.getWidth(name);

		PointF p = new PointF(width, 0f);
		fontMatrixTransform.transform(p, p);
		return p.x;
	}

	@Override
	public boolean isEmbedded()
	{
		return isEmbedded;
	}

	@Override
	public float getHeight(int code) throws IOException
	{
		String name = codeToName(code);
		float height = 0;
		if (!glyphHeights.containsKey(name))
		{
			height = (float)cffFont.getType1CharString(name).getBounds().height(); // todo: cffFont could be null
			glyphHeights.put(name, height);
		}
		return height;
	}
	
	@Override
	protected byte[] encode(int unicode) throws IOException
	{
		throw new UnsupportedOperationException("Not implemented: Type1C");
	}

	@Override
	public float getStringWidth(String string) throws IOException
	{
		float width = 0;
		for (int i = 0; i < string.length(); i++)
		{
			int codePoint = string.codePointAt(i);
			String name = getGlyphList().codePointToName(codePoint);
			width += cffFont.getType1CharString(name).getWidth();
		}
		return width;
	}

	@Override
	public float getAverageFontWidth()
	{
		if (avgWidth == null)
		{
			avgWidth = getAverageCharacterWidth();
		}
		return avgWidth;
	}

	/**
	 * Returns the embedded Type 1-equivalent CFF font.
	 * 
	 * @return the cffFont
	 */
	public CFFType1Font getCFFType1Font()
	{
		return cffFont;
	}

	// todo: this is a replacement for FontMetrics method
	private float getAverageCharacterWidth()
	{
		// todo: not implemented, highly suspect
		return 500;
	}
}
