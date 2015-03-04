package org.apache.pdfbox.pdmodel.font;

import org.apache.pdfbox.contentstream.PDContentStream;
import org.apache.pdfbox.cos.COSStream;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.common.COSObjectable;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.util.Matrix;

/**
 * A Type 3 character procedure. This is a standalone PDF content stream.
 *
 * @author John Hewson
 */
public final class PDType3CharProc implements COSObjectable, PDContentStream
{
	private final PDType3Font font;
	private final COSStream charStream;

	public PDType3CharProc(PDType3Font font, COSStream charStream)
	{
		this.font = font;
		this.charStream = charStream;
	}

	@Override
	public COSStream getCOSObject()
	{
		return charStream;
	}

	public PDType3Font getFont()
	{
		return font;
	}

	@Override
	public COSStream getContentStream()
	{
		return charStream;
	}

	@Override
	public PDResources getResources()
	{
		return font.getResources();
	}

	@Override
	public PDRectangle getBBox()
	{
		return font.getFontBBox();
	}
	
	@Override
	public Matrix getMatrix() {
		return font.getFontMatrix();
	}

	// todo: add methods for getting the character's width from the stream
}