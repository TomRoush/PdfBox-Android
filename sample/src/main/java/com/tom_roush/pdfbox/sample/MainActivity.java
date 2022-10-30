package com.tom_roush.pdfbox.sample;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.documentfile.provider.DocumentFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.Security;
import java.util.ArrayList;
import java.util.List;

import com.tom_roush.pdfbox.pdmodel.PDDocument;
import com.tom_roush.pdfbox.pdmodel.PDDocumentCatalog;
import com.tom_roush.pdfbox.pdmodel.PDPage;
import com.tom_roush.pdfbox.pdmodel.PDPageContentStream;
import com.tom_roush.pdfbox.pdmodel.encryption.AccessPermission;
import com.tom_roush.pdfbox.pdmodel.encryption.StandardProtectionPolicy;
import com.tom_roush.pdfbox.pdmodel.font.PDFont;
import com.tom_roush.pdfbox.pdmodel.font.PDType1Font;
import com.tom_roush.pdfbox.pdmodel.graphics.image.JPEGFactory;
import com.tom_roush.pdfbox.pdmodel.graphics.image.LosslessFactory;
import com.tom_roush.pdfbox.pdmodel.graphics.image.PDImageXObject;
import com.tom_roush.pdfbox.pdmodel.interactive.form.PDAcroForm;
import com.tom_roush.pdfbox.pdmodel.interactive.form.PDCheckBox;
import com.tom_roush.pdfbox.pdmodel.interactive.form.PDComboBox;
import com.tom_roush.pdfbox.pdmodel.interactive.form.PDField;
import com.tom_roush.pdfbox.pdmodel.interactive.form.PDListBox;
import com.tom_roush.pdfbox.pdmodel.interactive.form.PDRadioButton;
import com.tom_roush.pdfbox.pdmodel.interactive.form.PDTextField;
import com.tom_roush.pdfbox.rendering.ImageType;
import com.tom_roush.pdfbox.rendering.PDFRenderer;
import com.tom_roush.pdfbox.text.PDFTextStripper;
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

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
        // Enable Android asset loading
        PDFBoxResourceLoader.init(getApplicationContext());
        // Find the root of the external storage.

        root = getExternalFilesDir(null);
        assetManager = getAssets();
        tv = (TextView) findViewById(R.id.statusTextView);
    }

    /**
     * Creates a new PDF from scratch and saves it to a file
     */
    public void createPdf(View v) {
        PDDocument document = new PDDocument();
        PDPage page = new PDPage();
        document.addPage(page);

        // Create a new font object selecting one of the PDF base fonts
        PDFont font = PDType1Font.HELVETICA;
        // Or a custom font
//        try
//        {
//            // Replace MyFontFile with the path to the asset font you'd like to use.
//            // Or use LiberationSans "com/tom_roush/pdfbox/resources/ttf/LiberationSans-Regular.ttf"
//            font = PDType0Font.load(document, assetManager.open("MyFontFile.TTF"));
//        }
//        catch (IOException e)
//        {
//            Log.e("PdfBox-Android-Sample", "Could not load font", e);
//        }

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
            String path = root.getAbsolutePath() + "/Created.pdf";
            document.save(path);
            document.close();
            tv.setText("Successfully wrote PDF to " + path);

        } catch (IOException e) {
            Log.e("PdfBox-Android-Sample", "Exception thrown while creating PDF", e);
        }
    }

    private String getPreviewDir() {
        File storageDir = new File(getExternalFilesDir("").getAbsolutePath()+"/preview/");
        if (!storageDir.exists()) {
            storageDir.mkdirs();
        }
        return storageDir.getAbsolutePath()+"/";
    }

    public String getScanImageDir(){
        File storageDir = new File(getExternalFilesDir("").getAbsolutePath()+"/scanHistory/");
        if (!storageDir.exists()) {
            storageDir.mkdirs();
        }
        return storageDir.getAbsolutePath()+"/";
    }

    public void saveFileByPath(FileInputStream inputStream, String filePath) {
        Log.w("ceshi","保存路径："+filePath);
//        Log.w("ceshi","目录："+filePath.substring(0,filePath.lastIndexOf("/")+1));

        File dirFile = new File(getScanImageDir());
        if (!dirFile.exists()) {
            Log.w("ceshi","临时文件路径创建："+dirFile.mkdirs());
        }
        File target = new File(filePath);
        Log.w("ceshi","saveFileByPath="+filePath);
        try {
            FileOutputStream outputStream = new FileOutputStream(target);
            int temp = 0;
            byte[] data = new byte[1024];
            while((temp = inputStream.read(data))!=-1) {
                outputStream.write(data,0,temp);
//                Log.w("ceshi","文件读取中"+temp);
            }
            inputStream.close();
            outputStream.close();
        } catch (Exception e) {
            Log.w("ceshi","saveFileByPathException="+e.toString());
        }
    }

    public void openFile(View v) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        String [] mimeTypes = {
//                "image/*",
                "application/pdf"
//                ,"text/plain","application/vnd.ms-powerpoint",
//                "application/vnd.openxmlformats-officedocument.presentationml.presentation",
//                "application/msword",
//                "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
//                "application/vnd.ms-excel",
//                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
        };
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent,200);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Uri uri = data.getData();
        if (uri!= null) {
            Log.w("ceshi","返回的路径:"+uri.getEncodedPath());
            try {
                DocumentFile documentFile = DocumentFile.fromSingleUri(this,uri);
                saveFileByPath((FileInputStream) getContentResolver().openInputStream(documentFile.getUri()),getScanImageDir()+documentFile.getName());
                renderFile(documentFile);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Loads an existing PDF and renders it to a Bitmap
     */
    public void renderFile(DocumentFile documentFile) {
        // Render the page and save it to an image file
        try {
            // Load in an already created PDF
            PDDocument document = PDDocument.load(new File(getScanImageDir()+documentFile.getName()));
            // Create a renderer for the document
            PDFRenderer renderer = new PDFRenderer(document);
            // Render the image to an RGB Bitmap
            pageImage = renderer.renderImage(0, 1, ImageType.RGB);

            // Save the render result to an image
            String path = root.getAbsolutePath() + "/render.jpg";
            File renderFile = new File(path);
            FileOutputStream fileOut = new FileOutputStream(renderFile);
            pageImage.compress(Bitmap.CompressFormat.JPEG, 100, fileOut);
            fileOut.close();
            tv.setText("Successfully rendered image to " + path);
            // Optional: display the render result on screen
            displayRenderedImage();
        }
        catch (IOException e)
        {
            Log.e("PdfBox-Android-Sample", "Exception thrown while rendering file", e);
        }
    }

    /**
     * Fills in a PDF form and saves the result
     */
    public void fillForm(View v) {
        try {
            // Load the document and get the AcroForm
            PDDocument document = PDDocument.load(assetManager.open("FormTest.pdf"));
            PDDocumentCatalog docCatalog = document.getDocumentCatalog();
            PDAcroForm acroForm = docCatalog.getAcroForm();

            // Fill the text field
            PDTextField field = (PDTextField) acroForm.getField("TextField");
            field.setValue("Filled Text Field");
            // Optional: don't allow this field to be edited
            field.setReadOnly(true);

            PDField checkbox = acroForm.getField("Checkbox");
            ((PDCheckBox) checkbox).check();

            PDField radio = acroForm.getField("Radio");
            ((PDRadioButton)radio).setValue("Second");

            PDField listbox = acroForm.getField("ListBox");
            List<Integer> listValues = new ArrayList<>();
            listValues.add(1);
            listValues.add(2);
            ((PDListBox) listbox).setSelectedOptionsIndex(listValues);

            PDField dropdown = acroForm.getField("Dropdown");
            ((PDComboBox) dropdown).setValue("Hello");

            String path = root.getAbsolutePath() + "/FilledForm.pdf";
            tv.setText("Saved filled form to " + path);
            document.save(path);
            document.close();
        } catch (IOException e) {
            Log.e("PdfBox-Android-Sample", "Exception thrown while filling form fields", e);
        }
    }

    /**
     * Strips the text from a PDF and displays the text on screen
     */
    public void stripText(View v) {
        String parsedText = null;
        PDDocument document = null;
        try {
            document = PDDocument.load(assetManager.open("Hello.pdf"));
        } catch(IOException e) {
            Log.e("PdfBox-Android-Sample", "Exception thrown while loading document to strip", e);
        }

        try {
            PDFTextStripper pdfStripper = new PDFTextStripper();
            pdfStripper.setStartPage(0);
            pdfStripper.setEndPage(1);
            parsedText = "Parsed text: " + pdfStripper.getText(document);
        }
        catch (IOException e)
        {
            Log.e("PdfBox-Android-Sample", "Exception thrown while stripping text", e);
        } finally {
            try {
                if (document != null) document.close();
            }
            catch (IOException e)
            {
                Log.e("PdfBox-Android-Sample", "Exception thrown while closing document", e);
            }
        }
        tv.setText(parsedText);
    }

    /**
     * Creates a simple pdf and encrypts it
     */
    public void createEncryptedPdf(View v)
    {
        String path = root.getAbsolutePath() + "/crypt.pdf";

        int keyLength = 128; // 128 bit is the highest currently supported

        // Limit permissions of those without the password
        AccessPermission ap = new AccessPermission();
        ap.setCanPrint(false);

        // Sets the owner password and user password
        StandardProtectionPolicy spp = new StandardProtectionPolicy("12345", "hi", ap);

        // Setups up the encryption parameters
        spp.setEncryptionKeyLength(keyLength);
        spp.setPermissions(ap);
        BouncyCastleProvider provider = new BouncyCastleProvider();
        Security.addProvider(provider);

        PDFont font = PDType1Font.HELVETICA;
        PDDocument document = new PDDocument();
        PDPage page = new PDPage();

        document.addPage(page);

        try
        {
            PDPageContentStream contentStream = new PDPageContentStream(document, page);

            // Write Hello World in blue text
            contentStream.beginText();
            contentStream.setNonStrokingColor(15, 38, 192);
            contentStream.setFont(font, 12);
            contentStream.newLineAtOffset(100, 700);
            contentStream.showText("Hello World");
            contentStream.endText();
            contentStream.close();

            // Save the final pdf document to a file
            document.protect(spp); // Apply the protections to the PDF
            document.save(path);
            document.close();
            tv.setText("Successfully wrote PDF to " + path);

        }
        catch (IOException e)
        {
            Log.e("PdfBox-Android-Sample", "Exception thrown while creating PDF for encryption", e);
        }
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