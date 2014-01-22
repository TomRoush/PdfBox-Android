PdfBox-Android
==============

Basic PDF form filling on Android with a modified version of Apache's PdfBox project


This is a stripped down version of Apache's PdfBox Java library for manipulating PDF documents. This version:
-Contains only the methods required to open a PDF document with forms, fill them, and save and close the document
-Works on Android
-All method and variable names remain the same, but the base package is now org.apache.pdfboxandroid

Example code:
```
File root = android.os.Environment.getExternalStorageDirectory(); 
	    File dir = new File (root.getAbsolutePath() + "/download");
	    dir.mkdirs();
	    File out = new File(dir, "test.pdf");

	    try {
	    	PDDocument pdfDoc = PDDocument.load(root.getAbsolutePath() + "/download" + "/input.pdf");
	    	PDDocumentCatalog docCatalog = pdfDoc.getDocumentCatalog();
	    	PDAcroForm acroForm = docCatalog.getAcroForm();
	    	PDField field = acroForm.getField("Date");
	    	if (field != null) {
	    	    field.setValue("Today");
	    	}
	    	try {
				pdfDoc.save(root.getAbsolutePath() + "/download" + "/output.pdf");
			} catch (COSVisitorException e) {
				e.printStackTrace();
			}
	    	pdfDoc.close();
```
