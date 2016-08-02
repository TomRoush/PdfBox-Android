package org.apache.pdfboxandroid.sample;

import android.app.Activity;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.JPEGFactory;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDCheckbox;
import org.apache.pdfbox.pdmodel.interactive.form.PDComboBox;
import org.apache.pdfbox.pdmodel.interactive.form.PDFieldTreeNode;
import org.apache.pdfbox.pdmodel.interactive.form.PDListBox;
import org.apache.pdfbox.pdmodel.interactive.form.PDRadioButton;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.util.PDFBoxResourceLoader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {
	File root;
	AssetManager assetManager;
	Bitmap pageImage;
	TextView tv;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	@Override
	protected void onStart() {
		super.onStart();
		setup();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}

	/**
	 * Initializes variables used for convenience
	 */
	private void setup() {
		// Enable Android-style asset loading (highly recommended)
		PDFBoxResourceLoader.init(getApplicationContext());
		// Find the root of the external storage.
		root = android.os.Environment.getExternalStorageDirectory();
		assetManager = getAssets();
		tv = (TextView) findViewById(R.id.statusTextView);
	}

	/**
	 * Creates a new PDF from scratch and saves it to a file
	 */
	protected void createPdf(View v) {
		PDDocument document = new PDDocument();
		PDPage page = new PDPage();
		document.addPage(page);

		// Create a new font object selecting one of the PDF base fonts
		PDFont font = PDType1Font.HELVETICA;
		// Or a custom font
//		try {
//			PDType0Font font = PDType0Font.load(document, assetManager.open("MyFontFile.TTF"));
//		} catch(IOException e) {
//			e.printStackTrace();
//		}

		PDPageContentStream contentStream;

		try {
			// Define a content stream for adding to the PDF
			contentStream = new PDPageContentStream(document, page);

			// Write Hello World in blue text
			contentStream.beginText();
			contentStream.setNonStrokingColor(15, 38, 192);
			contentStream.setFont(font, 12);
			contentStream.newLineAtOffset(100, 700);
			contentStream.showText("Hello World");
			contentStream.endText();

			// Load in the images
			InputStream in = assetManager.open("falcon.jpg");
			InputStream alpha = assetManager.open("trans.png");

			// Draw a green rectangle
			contentStream.addRect(5, 500, 100, 100);
			contentStream.setNonStrokingColor(0, 255, 125);
			contentStream.fill();

			// Draw the falcon base image
			PDImageXObject ximage = JPEGFactory.createFromStream(document, in);
			contentStream.drawImage(ximage, 20, 20);

			// Draw the red overlay image
			Bitmap alphaImage = BitmapFactory.decodeStream(alpha);
			PDImageXObject alphaXimage = LosslessFactory.createFromImage(document, alphaImage);
			contentStream.drawImage(alphaXimage, 20, 20 );

			// Make sure that the content stream is closed:
			contentStream.close();

			// Save the final pdf document to a file
			String path = root.getAbsolutePath() + "/Download/Created.pdf";
			document.save(path);
			document.close();
			tv.setText("Successfully wrote PDF to " + path);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Loads an existing PDF and renders it to a Bitmap
	 */
	protected void renderFile(View v) {
		// Render the page and save it to an image file
		try {
			// Load in an already created PDF
			PDDocument document = PDDocument.load(assetManager.open("Created.pdf"));
			// Create a renderer for the document
			PDFRenderer renderer = new PDFRenderer(document);
			// Render the image to an RGB Bitmap
			pageImage = renderer.renderImage(0, 1, Bitmap.Config.RGB_565);

			// Save the render result to an image
			String path = root.getAbsolutePath() + "/Download/render.jpg";
			File renderFile = new File(path);
			FileOutputStream fileOut = new FileOutputStream(renderFile);
			pageImage.compress(Bitmap.CompressFormat.JPEG, 100, fileOut);
			fileOut.close();
			tv.setText("Successfully rendered image to " + path);
			// Optional: display the render result on screen
			displayRenderedImage();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Fills in a PDF form and saves the result
	 */
	protected void fillForm(View v) {
		try {
			// Load the document and get the AcroForm
			PDDocument document = PDDocument.load(assetManager.open("FormTest.pdf"));
			PDDocumentCatalog docCatalog = document.getDocumentCatalog();
			PDAcroForm acroForm = docCatalog.getAcroForm();

			// Fill the text field
			PDFieldTreeNode field = acroForm.getField("TextField");
			field.setValue("Filled Text Field");
			// Optional: don't allow this field to be edited
			field.setReadonly(true);

			PDFieldTreeNode checkbox = acroForm.getField("Checkbox");
			((PDCheckbox) checkbox).check();

			PDFieldTreeNode radio = acroForm.getField("Radio");
			((PDRadioButton)radio).setValue("Second");

			PDFieldTreeNode listbox = acroForm.getField("ListBox");
			List<Integer> listValues = new ArrayList<>();
			listValues.add(1);
			listValues.add(2);
			((PDListBox) listbox).setSelectedOptionsIndex(listValues);

			PDFieldTreeNode dropdown = acroForm.getField("Dropdown");
			((PDComboBox) dropdown).setValue("Hello");

			String path = root.getAbsolutePath() + "/Download/FilledForm.pdf";
			tv.setText("Saved filled form to " + path);
			document.save(path);
			document.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void fillField(PDAcroForm acroForm, String fieldName, String value) throws IOException {

	}

	/**
	 * Strips the text from a PDF and displays the text on screen
	 */
	protected void stripText(View v) {
		String parsedText = null;
		PDDocument document = null;
		try {
			document = PDDocument.load(assetManager.open("Hello.pdf"));
		} catch(IOException e) {
			e.printStackTrace();
		}

		try {
			PDFTextStripper pdfStripper = new PDFTextStripper();
			pdfStripper.setStartPage(0);
			pdfStripper.setEndPage(1);
			parsedText = "Parsed text: " + pdfStripper.getText(document);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (document != null) document.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		tv.setText(parsedText);
	}

	/**
	 * Helper method for drawing the result of renderFile() on screen
	 */
	private void displayRenderedImage() {
		new Thread() {
			public void run() {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						ImageView imageView = (ImageView) findViewById(R.id.renderedImageView);
						imageView.setImageBitmap(pageImage);
					}
				});
			}
		}.start();
	}
}