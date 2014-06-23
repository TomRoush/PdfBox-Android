package org.apache.pdfboxandroid.pdfwriter;

import java.io.FilterInputStream;
import java.io.InputStream;

public class COSFilterInputStream extends FilterInputStream {
	int[] byteRange;
	
	public COSFilterInputStream(InputStream in, int[] byteRange)
	  {
	    super(in);
	    this.byteRange = byteRange;
	  }
}
