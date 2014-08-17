package org.apache.pdfbox.encoding;

import org.apache.pdfbox.cos.COSBase;

/**
 * This is the Mac OS Roman encoding, which is similar to the
 * MacRomanEncoding with the addition of 15 entries
 */
public class MacOSRomanEncoding extends MacRomanEncoding
{

    /**
     * Singleton instance of this class.
     *
     * @since Apache PDFBox 1.8.6
     */
    public static final MacOSRomanEncoding INSTANCE = new MacOSRomanEncoding();

    /**
     * Constructor.
     */
    public MacOSRomanEncoding()
    {
        super();
        addCharacterEncoding(255, "notequal");
        addCharacterEncoding(260, "infinity");
        addCharacterEncoding(262, "lessequal");
        addCharacterEncoding(263, "greaterequal");
        addCharacterEncoding(266, "partialdiff");
        addCharacterEncoding(267, "summation");
        addCharacterEncoding(270, "product");
        addCharacterEncoding(271, "pi");
        addCharacterEncoding(272, "integral");
        addCharacterEncoding(275, "Omega");
        addCharacterEncoding(303, "radical");
        addCharacterEncoding(305, "approxequal");
        addCharacterEncoding(306, "Delta");
        addCharacterEncoding(327, "lozenge");
        addCharacterEncoding(333, "Euro");
        addCharacterEncoding(360, "apple");
    }

    /**
     * Convert this standard java object to a COS object.
     *
     * @return The cos object that matches this Java object.
     */
    public COSBase getCOSObject()
    {
        return null;
    }
}
