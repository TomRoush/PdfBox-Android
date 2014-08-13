package org.apache.pdfbox.pdmodel.font;

import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;

/**
 * This is implementation of the CIDFontType0 Font.
 *
 * @author <a href="mailto:ben@benlitchfield.com">Ben Litchfield</a>
 * @version $Revision: 1.6 $
 */
public class PDCIDFontType0Font extends PDCIDFont
{

    /**
     * Constructor.
     */
    public PDCIDFontType0Font()
    {
        super();
        font.setItem( COSName.SUBTYPE, COSName.CID_FONT_TYPE0 );
    }

    /**
     * Constructor.
     *
     * @param fontDictionary The font dictionary according to the PDF specification.
     */
    public PDCIDFontType0Font( COSDictionary fontDictionary )
    {
        super( fontDictionary );
    }

    /**
     * Returns the AWT font that corresponds with this CIDFontType0 font.
     * By default we try to look up a system font with the same name. If that
     * fails and the font file is embedded in the PDF document, we try to
     * generate the AWT font using the {@link PDType1CFont} class. Ideally
     * the embedded font would be used always if available, but since the
     * code doesn't work correctly for all fonts yet we opt to use the
     * system font by default.
     *
     * @return AWT font, or <code>null</code> if not available
     */
//    public Font getawtFont() throws IOException
//    {
//        PDFontDescriptor fd = getFontDescriptor();
//        Font awtFont = null;
//        if (fd.getFontName() != null)
//        {
//            awtFont = FontManager.getAwtFont(fd.getFontName());
//        }
//        if (awtFont == null && fd instanceof PDFontDescriptorDictionary) {
//            PDFontDescriptorDictionary fdd = (PDFontDescriptorDictionary) fd;
//            if (fdd.getFontFile3() != null) {
//                // Create a font with the embedded data
//                // TODO: This still doesn't work right for
//                // some embedded fonts
//                awtFont = new PDType1CFont(font).getawtFont();
//            }
//        }
//
//        return awtFont;
//    }TODO

}
