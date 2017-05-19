package com.tom_roush.pdfbox.pdmodel.font;

import android.graphics.Path;
import android.graphics.PointF;
import android.util.Log;

import com.tom_roush.fontbox.EncodedFont;
import com.tom_roush.fontbox.FontBoxFont;
import com.tom_roush.fontbox.cff.CFFParser;
import com.tom_roush.fontbox.cff.CFFType1Font;
import com.tom_roush.fontbox.util.BoundingBox;
import com.tom_roush.pdfbox.cos.COSDictionary;
import com.tom_roush.pdfbox.cos.COSName;
import com.tom_roush.pdfbox.io.IOUtils;
import com.tom_roush.pdfbox.pdmodel.common.PDStream;
import com.tom_roush.pdfbox.pdmodel.font.encoding.Encoding;
import com.tom_roush.pdfbox.pdmodel.font.encoding.StandardEncoding;
import com.tom_roush.pdfbox.pdmodel.font.encoding.Type1Encoding;
import com.tom_roush.pdfbox.util.Matrix;
import com.tom_roush.harmony.awt.geom.AffineTransform;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Type 1-equivalent CFF font.
 *
 * @author Villu Ruusmann
 * @author John Hewson
 */
public class PDType1CFont extends PDSimpleFont
{
	private final Map<String, Float> glyphHeights = new HashMap<String, Float>();
	private Float avgWidth = null;
	private Matrix fontMatrix;
	private final AffineTransform fontMatrixTransform;

	private final CFFType1Font cffFont; // embedded font
    private final FontBoxFont genericFont; // embedded or system font for rendering
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
					Log.e("PdfBox-Android", "Invalid data for embedded Type1C font " + getName());
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
			Log.e("PdfBox-Android", "Can't read the embedded Type1C font " + getName(), e);
			fontIsDamaged = true;
		}
		isDamaged = fontIsDamaged;
		cffFont = cffEmbedded;

		if (cffFont != null)
		{
            genericFont = cffFont;
            isEmbedded = true;
        }
		else
		{
            FontMapping<FontBoxFont> mapping = FontMapper.getFontBoxFont(getBaseFont(), fd);
            genericFont = mapping.getFont();

            if (mapping.isFallback())
            {
                Log.w("PdfBox-Android",
                    "Using fallback font " + genericFont.getName() + " for " + getBaseFont());
            }
            isEmbedded = false;
		}
		readEncoding();
		fontMatrixTransform = getFontMatrix().createAffineTransform();
		fontMatrixTransform.scale(1000, 1000);
	}

	@Override
    public FontBoxFont getFontBoxFont()
    {
        return genericFont;
    }

	/**
	 * Returns the PostScript name of the font.
	 */
    public final String getBaseFont()
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
            return genericFont.getPath(name);
        }
    }

    @Override
    public boolean hasGlyph(String name) throws IOException
    {
        return genericFont.hasGlyph(name);
    }

	@Override
    public final String getName()
    {
		return getBaseFont();
	}

	@Override
	public BoundingBox getBoundingBox() throws IOException
	{
        return genericFont.getFontBBox();
    }

    //@Override
    public String codeToName(int code)
    {
		return getEncoding().getName(code);
	}

	@Override
	protected Encoding readEncodingFromFont() throws IOException
	{
		if (getStandard14AFM() != null)
		{
			// read from AFM
			return new Type1Encoding(getStandard14AFM());
		}
		else
		{
            // extract from Type1 font/substitute
            if (genericFont instanceof EncodedFont)
            {
                //FIXME dead instanceof
                return Type1Encoding.fromFontBox(((EncodedFont) genericFont).getEncoding());
            }
            else
            {
                // default (only happens with TTFs)
                return StandardEncoding.INSTANCE;
            }
        }
    }

	@Override
	public int readCode(InputStream in) throws IOException
	{
		return in.read();
	}

	@Override
    public final Matrix getFontMatrix()
    {
		if (fontMatrix == null)
		{
            List<Number> numbers = null;
            try
            {
                numbers = genericFont.getFontMatrix();
            }
            catch (IOException e)
            {
                fontMatrix = DEFAULT_FONT_MATRIX;
            }

            if (numbers != null && numbers.size() == 6)
            {
                fontMatrix = new Matrix(
                    numbers.get(0).floatValue(), numbers.get(1).floatValue(),
                    numbers.get(2).floatValue(), numbers.get(3).floatValue(),
                    numbers.get(4).floatValue(), numbers.get(5).floatValue());
            }
            else
            {
                return super.getFontMatrix();
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
		String name = codeToName(code);
        float width = genericFont.getWidth(name);

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
			height = cffFont.getType1CharString(name).getBounds().height(); // todo: cffFont could be null
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
