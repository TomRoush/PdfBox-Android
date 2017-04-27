package com.tom_roush.pdfbox.multipdf;

import com.tom_roush.pdfbox.pdmodel.PDDocument;
import com.tom_roush.pdfbox.pdmodel.PDPage;

import java.io.IOException;

/**
 * This class will extract one or more sequential pages and create a new document.
 * @author Adam Nichols (adam@apache.org)
 */
public class PageExtractor {
    private PDDocument sourceDocument;

    // first page to extract is page 1 (by default)
    private int startPage = 1;
    private int endPage = 0;

	/** 
	 * Creates a new instance of PageExtractor
	 * @param sourceDocument The document to split.
	 */
	public PageExtractor(PDDocument sourceDocument) {
		this.sourceDocument = sourceDocument;
		endPage = sourceDocument.getNumberOfPages();
	}

	/** 
	 * Creates a new instance of PageExtractor
	 * @param sourceDocument The document to split.
	 * @param startPage The first page you want extracted (inclusive)
	 * @param endPage The last page you want extracted (inclusive)
	 */
	public PageExtractor(PDDocument sourceDocument, int startPage, int endPage) {
		this(sourceDocument);
		this.startPage = startPage;
		this.endPage = endPage;
	}

	/**
	 * This will take a document and extract the desired pages into a new 
	 * document.  Both startPage and endPage are included in the extracted 
	 * document.  If the endPage is greater than the number of pages in the 
	 * source document, it will go to the end of the document.  If startPage is
	 * less than 1, it'll start with page 1.  If startPage is greater than 
	 * endPage or greater than the number of pages in the source document, a 
	 * blank document will be returned.
	 * 
	 * @return The extracted document
	 * @throws IOException If there is an IOError
	 */
	public PDDocument extract() throws IOException {
		PDDocument extractedDocument = new PDDocument();
		extractedDocument.setDocumentInformation(sourceDocument.getDocumentInformation());
		extractedDocument.getDocumentCatalog().setViewerPreferences(
				sourceDocument.getDocumentCatalog().getViewerPreferences());

		for (int i = startPage; i <= endPage; i++) {
			PDPage page = sourceDocument.getPage(i - 1);
			PDPage imported = extractedDocument.importPage(page);
			imported.setCropBox(page.getCropBox());
			imported.setMediaBox(page.getMediaBox());
			imported.setResources(page.getResources());
			imported.setRotation(page.getRotation());
		}

		return extractedDocument;
	}

	/**
	 * Gets the first page number to be extracted.
	 * @return the first page number which should be extracted
	 */
	public int getStartPage() {
		return startPage;
	}

	/**
	 * Sets the first page number to be extracted.
	 * @param startPage the first page number which should be extracted
	 */
	public void setStartPage(int startPage) {
		this.startPage = startPage;
	}

	/**
	 * Gets the last page number (inclusive) to be extracted.
	 * @return the last page number which should be extracted
	 */
	public int getEndPage() {
		return endPage;
	}

	/**
	 * Sets the last page number to be extracted.
	 * @param endPage the last page number which should be extracted
	 */
	public void setEndPage(int endPage) {
		this.endPage = endPage;
	}
}
