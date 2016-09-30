package org.apache.pdfbox.rendering;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.fontbox.cff.Type2CharString;
import org.apache.pdfbox.pdmodel.font.PDCIDFontType0;

import android.graphics.Path;
import android.util.Log;

/**
 * GeneralPath conversion for CFF CIDFont.
 *
 * @author John Hewson
 */
final class CIDType0Glyph2D implements Glyph2D
{
	private final Map<Integer, Path> cache = new HashMap<Integer, Path>();
	private final PDCIDFontType0 font;
	private final String fontName;
	/**
	 * Constructor.
	 *
	 * @param font Type 0 CIDFont
	 */
	public CIDType0Glyph2D(PDCIDFontType0 font) // todo: what about PDCIDFontType2?
	{
		this.font = font;
		fontName = font.getBaseFont();
	}
	@Override
	public Path getPathForCharacterCode(int code)
	{
		int cid = font.getParent().codeToCID(code);
		if (cache.containsKey(code))
		{
			return cache.get(code);
		}
		try
		{
			Type2CharString charString = font.getType2CharString(cid);
			if (charString.getGID() == 0)
			{
				String cidHex = String.format("%04x", cid);
				Log.w("PdfBoxAndroid", "No glyph for " + code + " (CID " + cidHex + ") in font " + fontName);
			}
			Path path = charString.getPath();
			cache.put(code, path);
			return path;
		}
		catch (IOException e)
		{
			// TODO: escalate this error?
			Log.w("PdfBoxAndroid", "Glyph rendering failed", e);
			return new Path();
		}
	}
	@Override
	public void dispose()
	{
		cache.clear();
	}
}