package org.apache.pdfbox.rendering;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.pdfbox.pdmodel.font.PDType1Equivalent;

import android.graphics.Path;
import android.util.Log;

/**
 * Glyph to GeneralPath conversion for Type 1 PFB and CFF, and TrueType fonts with a 'post' table.
 */
final class Type1Glyph2D implements Glyph2D
{
	private final Map<Integer, Path> cache = new HashMap<Integer, Path>();
	private final PDType1Equivalent font;
	/**
	 * Constructor.
	 *
	 * @param font PDF Type1 font.
	 */
	public Type1Glyph2D(PDType1Equivalent font)
	{
		this.font = font;
	}
	@Override
	public Path getPathForCharacterCode(int code)
	{
		// cache
		if (cache.containsKey(code))
		{
			return cache.get(code);
		}
		// fetch
		try
		{
			String name = font.codeToName(code);
			if (name.equals(".notdef"))
			{
				Log.w("PdfBoxAndroid", "No glyph for " + code + " (" + name + ") in font " + font.getName());
			}
			// todo: can this happen? should it be encapsulated?
			Path path = font.getPath(name);
			if (path == null)
			{
				path = font.getPath(".notdef");
			}
			cache.put(code, path);
			return path;
		}
		catch (IOException e)
		{
			Log.e("PdfBoxAndroid", "Glyph rendering failed", e); // todo: escalate this error?
			return new Path();
		}
	}
	@Override
	public void dispose()
	{
		cache.clear();
	}
}