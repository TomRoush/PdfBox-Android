package com.tom_roush.pdfbox.pdmodel.font.encoding;

import com.tom_roush.pdfbox.cos.COSBase;

/**
 * This is the Mac OS Roman encoding, which is similar to the
 * MacRomanEncoding with the addition of 15 entries
 */
public class MacOSRomanEncoding extends MacRomanEncoding
{

    /**
     * Singleton instance of this class.
     *
     * @since Apache PDFBox 2.0.0
     */
    public static final MacOSRomanEncoding INSTANCE = new MacOSRomanEncoding();

    /**
     * Constructor.
     */
    public MacOSRomanEncoding()
    {
        super();
        add(255, "notequal");
        add(260, "infinity");
        add(262, "lessequal");
        add(263, "greaterequal");
        add(266, "partialdiff");
        add(267, "summation");
        add(270, "product");
        add(271, "pi");
        add(272, "integral");
        add(275, "Omega");
        add(303, "radical");
        add(305, "approxequal");
        add(306, "Delta");
        add(327, "lozenge");
        add(333, "Euro");
        add(360, "apple");
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
