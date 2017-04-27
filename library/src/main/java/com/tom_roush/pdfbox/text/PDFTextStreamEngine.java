package com.tom_roush.pdfbox.text;

import android.util.Log;

import com.tom_roush.pdfbox.contentstream.PDFStreamEngine;
import com.tom_roush.pdfbox.contentstream.operator.DrawObject;
import com.tom_roush.pdfbox.contentstream.operator.state.Concatenate;
import com.tom_roush.pdfbox.contentstream.operator.state.Restore;
import com.tom_roush.pdfbox.contentstream.operator.state.Save;
import com.tom_roush.pdfbox.contentstream.operator.state.SetGraphicsStateParameters;
import com.tom_roush.pdfbox.contentstream.operator.state.SetMatrix;
import com.tom_roush.pdfbox.contentstream.operator.text.BeginText;
import com.tom_roush.pdfbox.contentstream.operator.text.EndText;
import com.tom_roush.pdfbox.contentstream.operator.text.MoveText;
import com.tom_roush.pdfbox.contentstream.operator.text.MoveTextSetLeading;
import com.tom_roush.pdfbox.contentstream.operator.text.NextLine;
import com.tom_roush.pdfbox.contentstream.operator.text.SetCharSpacing;
import com.tom_roush.pdfbox.contentstream.operator.text.SetFontAndSize;
import com.tom_roush.pdfbox.contentstream.operator.text.SetTextHorizontalScaling;
import com.tom_roush.pdfbox.contentstream.operator.text.SetTextLeading;
import com.tom_roush.pdfbox.contentstream.operator.text.SetTextRenderingMode;
import com.tom_roush.pdfbox.contentstream.operator.text.SetTextRise;
import com.tom_roush.pdfbox.contentstream.operator.text.SetWordSpacing;
import com.tom_roush.pdfbox.contentstream.operator.text.ShowText;
import com.tom_roush.pdfbox.contentstream.operator.text.ShowTextAdjusted;
import com.tom_roush.pdfbox.contentstream.operator.text.ShowTextLine;
import com.tom_roush.pdfbox.contentstream.operator.text.ShowTextLineAndSpace;
import com.tom_roush.pdfbox.pdmodel.PDPage;
import com.tom_roush.pdfbox.pdmodel.common.PDRectangle;
import com.tom_roush.pdfbox.pdmodel.font.PDFont;
import com.tom_roush.pdfbox.pdmodel.font.PDSimpleFont;
import com.tom_roush.pdfbox.pdmodel.font.PDType3Font;
import com.tom_roush.pdfbox.pdmodel.font.encoding.GlyphList;
import com.tom_roush.pdfbox.pdmodel.graphics.state.PDGraphicsState;
import com.tom_roush.pdfbox.util.Matrix;
import com.tom_roush.pdfbox.util.PDFBoxResourceLoader;
import com.tom_roush.pdfbox.util.Vector;

import java.io.IOException;
import java.io.InputStream;

/**
 * PDFStreamEngine subclass for advanced processing of text via TextPosition.
 *
 * @see com.tom_roush.pdfbox.text.TextPosition
 * @author Ben Litchfield
 * @author John Hewson
 */
class PDFTextStreamEngine extends PDFStreamEngine
{
	private int pageRotation;
	private PDRectangle pageSize;
	private final GlyphList glyphList;
	private Matrix legacyCTM;

	/**
	 * Constructor.
	 */
    PDFTextStreamEngine() throws IOException
    {
		addOperator(new BeginText());
		addOperator(new Concatenate());
		addOperator(new DrawObject()); // special text version
		addOperator(new EndText());
		addOperator(new SetGraphicsStateParameters());
		addOperator(new Save());
		addOperator(new Restore());
		addOperator(new NextLine());
		addOperator(new SetCharSpacing());
		addOperator(new MoveText());
		addOperator(new MoveTextSetLeading());
		addOperator(new SetFontAndSize());
		addOperator(new ShowText());
		addOperator(new ShowTextAdjusted());
		addOperator(new SetTextLeading());
		addOperator(new SetMatrix());
		addOperator(new SetTextRenderingMode());
		addOperator(new SetTextRise());
		addOperator(new SetWordSpacing());
		addOperator(new SetTextHorizontalScaling());
		addOperator(new ShowTextLine());
		addOperator(new ShowTextLineAndSpace());
		
		// load additional glyph list for Unicode mapping
		String path = "com/tom_roush/pdfbox/resources/glyphlist/additional.txt";
		InputStream input;
		if(PDFBoxResourceLoader.isReady()) {
			input = PDFBoxResourceLoader.getStream(path);
		} else {
			// Fallback
			input = GlyphList.class.getClassLoader().getResourceAsStream(path);
		}
		glyphList = new GlyphList(GlyphList.getAdobeGlyphList(), input);
	}

	/**
	 * This will initialise and process the contents of the stream.
	 *
	 * @param page the page to process
	 * @throws java.io.IOException if there is an error accessing the stream.
	 */
	@Override
	public void processPage(PDPage page) throws IOException
	{
		this.pageRotation = page.getRotation();
		this.pageSize = page.getCropBox();
		super.processPage(page);
	}
	
	@Override
	protected void showText(byte[] string) throws IOException
	{
		legacyCTM = getGraphicsState().getCurrentTransformationMatrix().clone();
		super.showText(string);
	}

	/**
	 * This method was originally written by Ben Litchfield for PDFStreamEngine.
	 */
	@Override
	protected void showGlyph(Matrix textRenderingMatrix, PDFont font, int code, String unicode,
			Vector displacement) throws IOException
	{
		//
		// legacy calculations which were previously in PDFStreamEngine
		//

		PDGraphicsState state = getGraphicsState();
		Matrix ctm = legacyCTM;
		float fontSize = state.getTextState().getFontSize();
		float horizontalScaling = state.getTextState().getHorizontalScaling() / 100f;
		Matrix textMatrix = getTextMatrix();

		// 1/2 the bbox is used as the height todo: why?
		float glyphHeight = font.getBoundingBox().getHeight() / 2;

		// transformPoint from glyph space -> text space
		float height = font.getFontMatrix().transformPoint(0, glyphHeight).y;

		// (modified) combined displacement, this is calculated *without* taking the character
		// spacing and word spacing into account, due to legacy code in TextStripper
		float tx = displacement.getX() * fontSize * horizontalScaling;
		float ty = 0; // todo: support vertical writing mode

		// (modified) combined displacement matrix
		Matrix td = Matrix.getTranslateInstance(tx, ty);

		// (modified) text rendering matrix
		Matrix nextTextRenderingMatrix = td.multiply(textMatrix).multiply(ctm); // text space -> device space
		float nextX = nextTextRenderingMatrix.getTranslateX();
		float nextY = nextTextRenderingMatrix.getTranslateY();

		// (modified) width and height calculations
		float dxDisplay = nextX - textRenderingMatrix.getTranslateX();
		float dyDisplay = height * textRenderingMatrix.getScalingFactorY();

		//
		// start of the original method
		//

		// Note on variable names. There are three different units being used in this code.
		// Character sizes are given in glyph units, text locations are initially given in text
		// units, and we want to save the data in display units. The variable names should end with
		// Text or Disp to represent if the values are in text or disp units (no glyph units are
		// saved).

		float fontSizeText = getGraphicsState().getTextState().getFontSize();
		float horizontalScalingText = getGraphicsState().getTextState().getHorizontalScaling()/100f;
		//Matrix ctm = getGraphicsState().getCurrentTransformationMatrix();

		float glyphSpaceToTextSpaceFactor = 1 / 1000f;
		if (font instanceof PDType3Font)
		{
			// This will typically be 1000 but in the case of a type3 font
			// this might be a different number
			glyphSpaceToTextSpaceFactor = 1f / font.getFontMatrix().getScaleX();
		}

		float spaceWidthText = 0;
		try
		{
			// to avoid crash as described in PDFBOX-614, see what the space displacement should be
			spaceWidthText = font.getSpaceWidth() * glyphSpaceToTextSpaceFactor;
		}
		catch (Throwable exception)
		{
			Log.w("PdfBox-Android", exception.getMessage(), exception);
		}

		if (spaceWidthText == 0)
		{
			spaceWidthText = font.getAverageFontWidth() * glyphSpaceToTextSpaceFactor;
			// the average space width appears to be higher than necessary so make it smaller
			spaceWidthText *= .80f;
		}
		if (spaceWidthText == 0)
		{
			spaceWidthText = 1.0f; // if could not find font, use a generic value
		}

		// the space width has to be transformed into display units
		float spaceWidthDisplay = spaceWidthText * fontSizeText * horizontalScalingText *
				textRenderingMatrix.getScalingFactorX()  * ctm.getScalingFactorX();
		
		// use our additional glyph list for Unicode mapping
		unicode = font.toUnicode(code, glyphList);

		// when there is no Unicode mapping available, Acrobat simply coerces the character code
		// into Unicode, so we do the same. Subclasses of PDFStreamEngine don't necessarily want
		// this, which is why we leave it until this point in PDFTextStreamEngine.
		if (unicode == null)
		{
			if (font instanceof PDSimpleFont)
			{
				char c = (char) code;
				unicode = new String(new char[] { c });
			}
			else
			{
				// Acrobat doesn't seem to coerce composite font's character codes, instead it
				// skips them. See the "allah2.pdf" TestTextStripper file.
				return;
			}
		}

		processTextPosition(new TextPosition(pageRotation, pageSize.getWidth(),
				pageSize.getHeight(), textRenderingMatrix, nextX, nextY,
				dyDisplay, dxDisplay,
				spaceWidthDisplay, unicode, new int[] { code } , font, fontSize,
				(int)(fontSize * textRenderingMatrix.getScalingFactorX())));
	}

	/**
	 * A method provided as an event interface to allow a subclass to perform some specific
	 * functionality when text needs to be processed.
	 *
	 * @param text The text to be processed.
	 */
	protected void processTextPosition(TextPosition text)
	{
		// subclasses can override to provide specific functionality
	}
}
