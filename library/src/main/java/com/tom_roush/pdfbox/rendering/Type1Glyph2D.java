package com.tom_roush.pdfbox.rendering;

import android.graphics.Path;
import android.util.Log;

import com.tom_roush.pdfbox.pdmodel.font.PDSimpleFont;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Glyph to GeneralPath conversion for Type 1 PFB and CFF, and TrueType fonts with a 'post' table.
 */
final class Type1Glyph2D implements Glyph2D
{
	private final Map<Integer, Path> cache = new HashMap<Integer, Path>();
    private final PDSimpleFont font;

    /**
     * Constructor.
	 *
	 * @param font PDF Type1 font.
	 */
    Type1Glyph2D(PDSimpleFont font)
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
            String name = font.getEncoding().getName(code);
            if (!font.hasGlyph(name))
            {
				Log.w("PdfBox-Android", "No glyph for " + code + " (" + name + ") in font " + font.getName());
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
			Log.e("PdfBox-Android", "Glyph rendering failed", e); // todo: escalate this error?
			return new Path();
		}
	}
	@Override
	public void dispose()
	{
		cache.clear();
	}
}