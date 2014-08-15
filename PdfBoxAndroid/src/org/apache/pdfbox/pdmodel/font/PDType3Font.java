package org.apache.pdfbox.pdmodel.font;

import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.common.PDMatrix;

/**
 * This is implementation of the Type3 Font.
 *
 * @author <a href="mailto:ben@benlitchfield.com">Ben Litchfield</a>
 * @version $Revision: 1.8 $
 */
public class PDType3Font extends PDSimpleFont
{
    //A map of character code to java.awt.Image for the glyph
//    private Map<Character, Image> images = new HashMap<Character, Image>();TODO

    /**
     * Constructor.
     */
    public PDType3Font()
    {
        super();
        font.setItem( COSName.SUBTYPE, COSName.TYPE3 );
    }

    /**
     * Constructor.
     *
     * @param fontDictionary The font dictionary according to the PDF specification.
     */
    public PDType3Font( COSDictionary fontDictionary )
    {
        super( fontDictionary );
    }

    /**
     * Type3 fonts have their glyphs defined as a content stream.  This
     * will create the image that represents that character
     *
     * @throws IOException If there is an error creating the image.
     */
//    private Image createImageIfNecessary( char character ) throws IOException
//    {
//        Character c = new Character( character );
//        Image retval = (Image)images.get( c );
//        if( retval == null )
//        {
//            COSDictionary charProcs = (COSDictionary)font.getDictionaryObject( COSName.CHAR_PROCS );
//            COSStream stream = (COSStream)charProcs.getDictionaryObject( COSName.getPDFName( "" + character ) );
//            if( stream != null )
//            {
//                Type3StreamParser parser = new Type3StreamParser();
//                retval = parser.createImage( stream );
//                images.put( c, retval );
//            }
//            else
//            {
//                //stream should not be null!!
//            }
//        }
//        return retval;
//
//    }TODO

    /**
     * {@inheritDoc}
     */
//    public void drawString( String string, int[] codePoints, Graphics g, float fontSize, AffineTransform at, float x, float y ) 
//        throws IOException
//    {
//        for(int i=0; i<string.length(); i++)
//        {
//            //todo need to use image observers and such
//            char c = string.charAt( i );
//            Image image = createImageIfNecessary( c );
//            if( image != null )
//            {
//                int newWidth = (int)(.12*image.getWidth(null));
//                int newHeight = (int)(.12*image.getHeight(null));
//                if( newWidth > 0 && newHeight > 0 )
//                {
//                    image = image.getScaledInstance( newWidth, newHeight, Image.SCALE_SMOOTH );
//                    g.drawImage( image, (int)x, (int)y, null );
//                    x+=newWidth;
//                }
//            }
//        }
//    }TODO

    /**
     * Set the font matrix for this type3 font.
     *
     * @param matrix The font matrix for this type3 font.
     */
    public void setFontMatrix( PDMatrix matrix )
    {
        font.setItem( COSName.FONT_MATRIX, matrix );
    }
}
