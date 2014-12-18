package org.apache.pdfbox.rendering.font;

import java.io.IOException;
import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.fontbox.cff.Type2CharString;
import org.apache.pdfbox.pdmodel.font.PDCIDFontType0;

import android.graphics.Path;

/**
 * GeneralPath conversion for CFF CIDFont.
 *
 * @author John Hewson
 */
public class CIDType0Glyph2D implements Glyph2D
{
	private static final Log LOG = LogFactory.getLog(CIDType0Glyph2D.class);
	private final HashMap<Integer, Path> cache = new HashMap<Integer, Path>();
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
				LOG.warn("No glyph for " + code + " (CID " + cidHex + ") in font " + fontName);
			}
			Path path = charString.getPath();
			cache.put(code, path);
			return path;
		}
		catch (IOException e)
		{
			LOG.error("Glyph rendering failed", e); // todo: escalate this error?
			return new Path();
		}
	}
	@Override
	public void dispose()
	{
		cache.clear();
	}
}