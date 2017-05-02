package com.tom_roush.pdfbox.pdmodel.font;

import android.graphics.Path;
import android.util.Log;

import com.tom_roush.fontbox.FontBoxFont;
import com.tom_roush.fontbox.util.BoundingBox;
import com.tom_roush.pdfbox.cos.COSArray;
import com.tom_roush.pdfbox.cos.COSDictionary;
import com.tom_roush.pdfbox.cos.COSName;
import com.tom_roush.pdfbox.cos.COSStream;
import com.tom_roush.pdfbox.pdmodel.PDResources;
import com.tom_roush.pdfbox.pdmodel.common.PDRectangle;
import com.tom_roush.pdfbox.pdmodel.font.encoding.DictionaryEncoding;
import com.tom_roush.pdfbox.pdmodel.font.encoding.Encoding;
import com.tom_roush.pdfbox.pdmodel.font.encoding.GlyphList;
import com.tom_roush.pdfbox.util.Matrix;
import com.tom_roush.pdfbox.util.Vector;

import java.io.IOException;
import java.io.InputStream;

/**
 * A PostScript Type 3 Font.
 *
 * @author Ben Litchfield
 */
public class PDType3Font extends PDSimpleFont
{
	private PDResources resources;
	private COSDictionary charProcs;
	private Matrix fontMatrix;

	/**
	 * Constructor.
	 *
	 * @param fontDictionary The font dictionary according to the PDF specification.
	 */
	public PDType3Font(COSDictionary fontDictionary) throws IOException
	{
		super(fontDictionary);
		readEncoding();
	}

	@Override
	public String getName()
	{
		return dict.getNameAsString(COSName.NAME);
	}

	@Override
	protected final void readEncoding() throws IOException
	{
		COSDictionary encodingDict = (COSDictionary)dict.getDictionaryObject(COSName.ENCODING);
		encoding = new DictionaryEncoding(encodingDict);
		glyphList = GlyphList.getZapfDingbats();
	}

	@Override
	protected Encoding readEncodingFromFont() throws IOException
	{
		// Type 3 fonts do not have a built-in encoding
		throw new UnsupportedOperationException("not supported for Type 3 fonts");
	}

	@Override
	protected Boolean isFontSymbolic()
	{
		return false;
	}

    @Override
    public Path getPath(String name) throws IOException
    {
        // Type 3 fonts do not use vector paths
        throw new UnsupportedOperationException("not supported for Type 3 fonts");
    }

    @Override
    public boolean hasGlyph(String name) throws IOException
    {
        COSStream stream = (COSStream) getCharProcs().getDictionaryObject(COSName.getPDFName(name));
        return stream != null;
    }

    @Override
    public FontBoxFont getFontBoxFont()
    {
        // Type 3 fonts do not use FontBox fonts
        throw new UnsupportedOperationException("not supported for Type 3 fonts");
    }


    @Override
    public Vector getDisplacement(int code) throws IOException
	{
		return getFontMatrix().transform(new Vector(getWidth(code), 0));
	}

	@Override
	public float getWidth(int code) throws IOException
	{
		int firstChar = dict.getInt(COSName.FIRST_CHAR, -1);
		int lastChar = dict.getInt(COSName.LAST_CHAR, -1);
		if (getWidths().size() > 0 && code >= firstChar && code <= lastChar)
		{
			return getWidths().get(code - firstChar).floatValue();
		}
		else
		{
			PDFontDescriptor fd = getFontDescriptor();
			if (fd != null)
			{
				return fd.getMissingWidth();
			}
			else
			{
				// todo: call getWidthFromFont?
				Log.e("PdfBox-Android", "No width for glyph " + code + " in font " + getName());
				return 0;
			}
		}
	}

	@Override
	public float getWidthFromFont(int code)
	{
		// todo: could these be extracted from the font's stream?
		throw new UnsupportedOperationException("not suppported");
	}

	@Override
	public boolean isEmbedded()
	{
		return true;
	}

	@Override
	public float getHeight(int code) throws IOException
	{
		PDFontDescriptor desc = getFontDescriptor();
		if (desc != null)
		{
			// the following values are all more or less accurate at least all are average
			// values. Maybe we'll find another way to get those value for every single glyph
			// in the future if needed
			PDRectangle fontBBox = desc.getFontBoundingBox();
			float retval = 0;
			if (fontBBox != null)
			{
				retval = fontBBox.getHeight() / 2;
			}
			if (retval == 0)
			{
				retval = desc.getCapHeight();
			}
			if (retval == 0)
			{
				retval = desc.getAscent();
			}
			if (retval == 0)
			{
				retval = desc.getXHeight();
				if (retval > 0)
				{
					retval -= desc.getDescent();
				}
			}
			return retval;
		}
		return 0;
	}

	@Override
	protected byte[] encode(int unicode) throws IOException
	{
		throw new UnsupportedOperationException("Not implemented: Type3");
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
			COSArray array = (COSArray) dict.getDictionaryObject(COSName.FONT_MATRIX);
			if (array != null)
			{
				fontMatrix = new Matrix(array);
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
		// there's no font file to load
		return false;
	}

	/**
	 * Returns the optional resources of the type3 stream.
	 *
	 * @return the resources bound to be used when parsing the type3 stream
	 */
	public PDResources getResources()
	{
		if (resources == null)
		{
			COSDictionary resources = (COSDictionary) dict.getDictionaryObject(COSName.RESOURCES);
			if (resources != null)
			{
				this.resources = new PDResources(resources);
			}
		}
		return resources;
	}

	/**
	 * This will get the fonts bounding box.
	 *
	 * @return The fonts bounding box.
	 */
	public PDRectangle getFontBBox()
	{
		COSArray rect = (COSArray) dict.getDictionaryObject(COSName.FONT_BBOX);
		PDRectangle retval = null;
		if(rect != null)
		{
			retval = new PDRectangle(rect);
		}
		return retval;
	}

	@Override
	public BoundingBox getBoundingBox()
	{
		PDRectangle rect = getFontBBox();
		return new BoundingBox(rect.getLowerLeftX(), rect.getLowerLeftY(),
				rect.getWidth(), rect.getHeight());
	}

	/**
	 * Returns the dictionary containing all streams to be used to render the glyphs.
	 * 
	 * @return the dictionary containing all glyph streams.
	 */
	public COSDictionary getCharProcs()
	{
		if (charProcs == null)
		{
			charProcs = (COSDictionary) dict.getDictionaryObject(COSName.CHAR_PROCS);
		}
		return charProcs;
	}

	/**
	 * Returns the stream of the glyph for the given character code
	 *
	 * @param code character code
	 * @return the stream to be used to render the glyph
	 */
	public PDType3CharProc getCharProc(int code)
	{
		String name = getEncoding().getName(code);
		if (name != null)
		{
			COSStream stream;
			stream = (COSStream)getCharProcs().getDictionaryObject(COSName.getPDFName(name));
			return new PDType3CharProc(this, stream);
		}
		return null;
	}
}
