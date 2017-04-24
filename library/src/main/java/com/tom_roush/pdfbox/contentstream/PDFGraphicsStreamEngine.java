package com.tom_roush.pdfbox.contentstream;

import android.graphics.Path;
import android.graphics.PointF;

import com.tom_roush.pdfbox.contentstream.operator.color.SetNonStrokingColor;
import com.tom_roush.pdfbox.contentstream.operator.color.SetNonStrokingColorN;
import com.tom_roush.pdfbox.contentstream.operator.color.SetNonStrokingColorSpace;
import com.tom_roush.pdfbox.contentstream.operator.color.SetNonStrokingDeviceGrayColor;
import com.tom_roush.pdfbox.contentstream.operator.color.SetNonStrokingDeviceRGBColor;
import com.tom_roush.pdfbox.contentstream.operator.color.SetStrokingColor;
import com.tom_roush.pdfbox.contentstream.operator.color.SetStrokingColorN;
import com.tom_roush.pdfbox.contentstream.operator.color.SetStrokingColorSpace;
import com.tom_roush.pdfbox.contentstream.operator.color.SetStrokingDeviceGrayColor;
import com.tom_roush.pdfbox.contentstream.operator.color.SetStrokingDeviceRGBColor;
import com.tom_roush.pdfbox.contentstream.operator.graphics.AppendRectangleToPath;
import com.tom_roush.pdfbox.contentstream.operator.graphics.ClipEvenOddRule;
import com.tom_roush.pdfbox.contentstream.operator.graphics.ClipNonZeroRule;
import com.tom_roush.pdfbox.contentstream.operator.graphics.CloseAndStrokePath;
import com.tom_roush.pdfbox.contentstream.operator.graphics.CloseFillEvenOddAndStrokePath;
import com.tom_roush.pdfbox.contentstream.operator.graphics.CloseFillNonZeroAndStrokePath;
import com.tom_roush.pdfbox.contentstream.operator.graphics.ClosePath;
import com.tom_roush.pdfbox.contentstream.operator.graphics.CurveTo;
import com.tom_roush.pdfbox.contentstream.operator.graphics.CurveToReplicateFinalPoint;
import com.tom_roush.pdfbox.contentstream.operator.graphics.CurveToReplicateInitialPoint;
import com.tom_roush.pdfbox.contentstream.operator.graphics.DrawObject;
import com.tom_roush.pdfbox.contentstream.operator.graphics.EndPath;
import com.tom_roush.pdfbox.contentstream.operator.graphics.FillEvenOddAndStrokePath;
import com.tom_roush.pdfbox.contentstream.operator.graphics.FillEvenOddRule;
import com.tom_roush.pdfbox.contentstream.operator.graphics.FillNonZeroAndStrokePath;
import com.tom_roush.pdfbox.contentstream.operator.graphics.FillNonZeroRule;
import com.tom_roush.pdfbox.contentstream.operator.graphics.LegacyFillNonZeroRule;
import com.tom_roush.pdfbox.contentstream.operator.graphics.LineTo;
import com.tom_roush.pdfbox.contentstream.operator.graphics.MoveTo;
import com.tom_roush.pdfbox.contentstream.operator.graphics.ShadingFill;
import com.tom_roush.pdfbox.contentstream.operator.graphics.StrokePath;
import com.tom_roush.pdfbox.contentstream.operator.state.Concatenate;
import com.tom_roush.pdfbox.contentstream.operator.state.Restore;
import com.tom_roush.pdfbox.contentstream.operator.state.Save;
import com.tom_roush.pdfbox.contentstream.operator.state.SetFlatness;
import com.tom_roush.pdfbox.contentstream.operator.state.SetGraphicsStateParameters;
import com.tom_roush.pdfbox.contentstream.operator.state.SetLineCapStyle;
import com.tom_roush.pdfbox.contentstream.operator.state.SetLineDashPattern;
import com.tom_roush.pdfbox.contentstream.operator.state.SetLineJoinStyle;
import com.tom_roush.pdfbox.contentstream.operator.state.SetLineMiterLimit;
import com.tom_roush.pdfbox.contentstream.operator.state.SetLineWidth;
import com.tom_roush.pdfbox.contentstream.operator.state.SetMatrix;
import com.tom_roush.pdfbox.contentstream.operator.state.SetRenderingIntent;
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
import com.tom_roush.pdfbox.cos.COSName;
import com.tom_roush.pdfbox.pdmodel.PDPage;
import com.tom_roush.pdfbox.pdmodel.graphics.image.PDImage;

import java.io.IOException;

/**
 * PDFStreamEngine subclass for advanced processing of graphics.
 * This class should be subclasses by end users looking to hook into graphics operations.
 *
 * @author John Hewson
 */
public abstract class PDFGraphicsStreamEngine extends PDFStreamEngine {
    // may be null, for example if the stream is a tiling pattern
    private final PDPage page;

    /**
     * Constructor.
     */
    protected PDFGraphicsStreamEngine(PDPage page) {
        this.page = page;

        addOperator(new CloseFillNonZeroAndStrokePath());
        addOperator(new FillNonZeroAndStrokePath());
        addOperator(new CloseFillEvenOddAndStrokePath());
        addOperator(new FillEvenOddAndStrokePath());
//        addOperator(new BeginInlineImage());TODO: PdfBox-Android
        addOperator(new BeginText());
        addOperator(new CurveTo());
        addOperator(new Concatenate());
        addOperator(new SetStrokingColorSpace());
        addOperator(new SetNonStrokingColorSpace());
        addOperator(new SetLineDashPattern());
        addOperator(new DrawObject()); // special graphics version
        addOperator(new EndText());
        addOperator(new FillNonZeroRule());
        addOperator(new LegacyFillNonZeroRule());
        addOperator(new FillEvenOddRule());
        addOperator(new SetStrokingDeviceGrayColor());
        addOperator(new SetNonStrokingDeviceGrayColor());
        addOperator(new SetGraphicsStateParameters());
        addOperator(new ClosePath());
        addOperator(new SetFlatness());
        addOperator(new SetLineJoinStyle());
        addOperator(new SetLineCapStyle());
//        addOperator(new SetStrokingDeviceCMYKColor());TODO: PdfBox-Android
//        addOperator(new SetNonStrokingDeviceCMYKColor());TODO: PdfBox-Android
        addOperator(new LineTo());
        addOperator(new MoveTo());
        addOperator(new SetLineMiterLimit());
        addOperator(new EndPath());
        addOperator(new Save());
        addOperator(new Restore());
        addOperator(new AppendRectangleToPath());
        addOperator(new SetStrokingDeviceRGBColor());
        addOperator(new SetNonStrokingDeviceRGBColor());
        addOperator(new SetRenderingIntent());
        addOperator(new CloseAndStrokePath());
        addOperator(new StrokePath());
        addOperator(new SetStrokingColor());
        addOperator(new SetNonStrokingColor());
        addOperator(new SetStrokingColorN());
        addOperator(new SetNonStrokingColorN());
        addOperator(new ShadingFill());
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
        addOperator(new CurveToReplicateInitialPoint());
        addOperator(new SetLineWidth());
        addOperator(new ClipNonZeroRule());
        addOperator(new ClipEvenOddRule());
        addOperator(new CurveToReplicateFinalPoint());
        addOperator(new ShowTextLine());
        addOperator(new ShowTextLineAndSpace());
    }

    /**
     * Returns the page.
     */
    protected final PDPage getPage() {
        return page;
    }

    /**
     * Append a rectangle to the current path.
     */
    public abstract void appendRectangle(PointF p0, PointF p1,
                                         PointF p2, PointF p3) throws IOException;

    /**
     * Draw the image.
     *
     * @param pdImage The image to draw.
     */
    public abstract void drawImage(PDImage pdImage) throws IOException;

    /**
     * Modify the current clipping path by intersecting it with the current path.
     * The clipping path will not be updated until the succeeding painting operator is called.
     *
     * @param windingRule The winding rule which will be used for clipping.
     */
    public abstract void clip(Path.FillType windingRule) throws IOException;

    /**
     * Starts a new path at (x,y).
     */
    public abstract void moveTo(float x, float y) throws IOException;

    /**
     * Draws a line from the current point to (x,y).
     */
    public abstract void lineTo(float x, float y) throws IOException;

    /**
     * Draws a curve from the current point to (x3,y3) using (x1,y1) and (x2,y2) as control points.
     */
    public abstract void curveTo(float x1, float y1,
                                 float x2, float y2,
                                 float x3, float y3) throws IOException;

    /**
     * Returns the current point of the current path.
     */
    public abstract PointF getCurrentPoint() throws IOException;

    /**
     * Closes the current path.
     */
    public abstract void closePath() throws IOException;

    /**
     * Ends the current path without filling or stroking it. The clipping path is updated here.
     */
    public abstract void endPath() throws IOException;

    /**
     * Stroke the path.
     *
     * @throws IOException If there is an IO error while stroking the path.
     */
    public abstract void strokePath() throws IOException;

    /**
     * Fill the path.
     *
     * @param windingRule The winding rule this path will use.
     */
    public abstract void fillPath(Path.FillType windingRule) throws IOException;

    /**
     * Fills and then strokes the path.
     *
     * @param windingRule The winding rule this path will use.
     */
    public abstract void fillAndStrokePath(Path.FillType windingRule) throws IOException;

    /**
     * Fill with Shading.
     *
     * @param shadingName The name of the Shading Dictionary to use for this fill instruction.
     */
    public abstract void shadingFill(COSName shadingName) throws IOException;
}
