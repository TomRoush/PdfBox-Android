package org.apache.pdfboxandroid.encoding;

import java.io.IOException;

import org.apache.pdfboxandroid.cos.COSName;

/**
 * This class will handle getting the appropriate encodings.
 *
 * @author <a href="mailto:ben@benlitchfield.com">Ben Litchfield</a>
 * @version $Revision: 1.9 $
 */
public class EncodingManager {
	/**
     * Default singleton instance of this class.
     *
     * @since Apache PDFBox 1.3.0
     */
    public static final EncodingManager INSTANCE = new EncodingManager();
    
    /**
     * This will get an encoding by name.
     *
     * @param name The name of the encoding to get.
     * @return The encoding that matches the name.
     * @throws IOException if there is no encoding with that name.
     */
    public Encoding getEncoding( COSName name ) throws IOException {
        if (COSName.STANDARD_ENCODING.equals(name)) {
            return StandardEncoding.INSTANCE;
        } else if (COSName.WIN_ANSI_ENCODING.equals(name)) {
            return WinAnsiEncoding.INSTANCE;
        } else if (COSName.MAC_ROMAN_ENCODING.equals(name)) {
            return MacRomanEncoding.INSTANCE;
        } else if (COSName.PDF_DOC_ENCODING.equals(name)) {
            return PdfDocEncoding.INSTANCE;
        } else {
            throw new IOException(
                    "Unknown encoding for '" + name.getName() + "'");
        }
    }
}
