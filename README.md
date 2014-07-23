PdfBox-Android
==============

The beginnings of a port of Apache's PdfBox library to be usable on Android


Important notes:

-Based on PdfBox v1.8.6

-This is still a work in progress; much of the functionality is still missing


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
