PdfBox-Android
==============

The beginnings of a port of Apache's PdfBox library to be usable on Android. The latest jar for the latest build can be found inside the PdfBoxAndroid folder

The main code of this project is licensed under the Apache 2.0 License, found at http://www.apache.org/licenses/LICENSE-2.0.html

#### Important notes:

-Based on PdfBox v1.8.6

-This is still a work in progress; much of the functionality is still missing

#### Libraries:
Apache Commons Logging library: http://commons.apache.org/proper/commons-logging/
SpongyCastle core, prov, and pkiv: https://github.com/rtyley/spongycastle/

Example code: Load a pdf called in.pdf from the project's asset folder, fill a field called Date, and save the pdf as out.pdf in the device's Downloads folder
```
	AssetManager assetManager = getAssets();
	    try {
	    	InputStream template = assetManager.open("in.pdf");
	    	PDDocument pdfDoc = PDDocument.load(template);
	    	PDDocumentCatalog docCatalog = pdfDoc.getDocumentCatalog();
	    	PDAcroForm acroForm = docCatalog.getAcroForm();
	    	if(acroForm == null) {Log.e(PDFBox.LOG_TAG, "null acroform");}
	    	PDField field = acroForm.getField("Date");
	    	if (field != null) {
	    	    field.setValue("Today");
	    	} else {
	    	    Log.e(PDFBox.LOG_TAG, "No field found with name:" + "Date");
	    	}
	    	try {
				pdfDoc.save(root.getAbsolutePath() + "/download" + "/out.pdf");
			} catch (COSVisitorException e) {
				e.printStackTrace();
			}
	    	pdfDoc.close();
	    	
	    } catch (FileNotFoundException e) {
	        e.printStackTrace();
	        Log.i("debug", "Unable to find specified file");
	    } catch (IOException e) {
	        e.printStackTrace();
	    } catch (Exception e) {
	    	e.printStackTrace();
	    }
```
