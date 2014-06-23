package org.apache.pdfboxandroid.pdmodel.font;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.fontbox.ttf.CMAPEncodingEntry;
import org.apache.fontbox.ttf.CMAPTable;
import org.apache.fontbox.ttf.GlyphData;
import org.apache.fontbox.ttf.GlyphTable;
import org.apache.fontbox.ttf.HeaderTable;
import org.apache.fontbox.ttf.HorizontalHeaderTable;
import org.apache.fontbox.ttf.HorizontalMetricsTable;
import org.apache.fontbox.ttf.NameRecord;
import org.apache.fontbox.ttf.NamingTable;
import org.apache.fontbox.ttf.OS2WindowsMetricsTable;
import org.apache.fontbox.ttf.PostScriptTable;
import org.apache.fontbox.ttf.TTFParser;
import org.apache.fontbox.ttf.TrueTypeFont;
import org.apache.fontbox.util.ResourceLoader;
import org.apache.pdfboxandroid.cos.COSDictionary;
import org.apache.pdfboxandroid.encoding.Encoding;
import org.apache.pdfboxandroid.encoding.WinAnsiEncoding;
import org.apache.pdfboxandroid.pdmodel.common.PDRectangle;

/**
 * This is the TrueType implementation of fonts.
 *
 * @author <a href="mailto:ben@benlitchfield.com">Ben Litchfield</a>
 * 
 */
public class PDTrueTypeFont extends PDSimpleFont {
	/**
     * This is the key to a property in the PDFBox_External_Fonts.properties
     * file to load a Font when a mapping does not exist for the current font.
     */
    public static final String UNKNOWN_FONT = "UNKNOWN_FONT";
    
//    private Font awtFont = null;

    private static Properties externalFonts = new Properties();
//    private static Map<String,TrueTypeFont> loadedExternalFonts = new HashMap<String,TrueTypeFont>();

	/**
     * Constructor.
     *
     * @param fontDictionary The font dictionary according to the PDF specification.
     * 
     * @throws IOException exception if something went wrong when loading the font.
     */
    public PDTrueTypeFont( COSDictionary fontDictionary ) throws IOException
    {
        super( fontDictionary );
        ensureFontDescriptor();
    }
    
    private void ensureFontDescriptor() throws IOException
    {
        if( getFontDescriptor() == null )
        {
            PDFontDescriptorDictionary fdd = new PDFontDescriptorDictionary(); 
            setFontDescriptor(fdd);
            InputStream ttfData = getExternalTTFData();
            if( ttfData != null )
            {
                try
                {
                    loadDescriptorDictionary(fdd, ttfData);
                }
                finally
                {
                    ttfData.close();
                }
            }
        }
    }
    
    private InputStream getExternalTTFData() throws IOException
    {
        String ttfResource = externalFonts.getProperty( UNKNOWN_FONT );
        String baseFont = getBaseFont();
        if( baseFont != null && externalFonts.containsKey(baseFont) )
        {
            ttfResource = externalFonts.getProperty( baseFont );
        }
        return (ttfResource != null ? ResourceLoader.loadResource(ttfResource) : null);
    }
    
    private void loadDescriptorDictionary(PDFontDescriptorDictionary fd, InputStream ttfData) throws IOException
    {
        TrueTypeFont ttf = null;
        try
        {
            TTFParser parser = new TTFParser();
            ttf = parser.parseTTF( ttfData );
            NamingTable naming = ttf.getNaming();
            List<NameRecord> records = naming.getNameRecords();
            for( int i=0; i<records.size(); i++ )
            {
                NameRecord nr = records.get( i );
                if( nr.getNameId() == NameRecord.NAME_POSTSCRIPT_NAME )
                {
                    setBaseFont( nr.getString() );
                    fd.setFontName( nr.getString() );
                }
                else if( nr.getNameId() == NameRecord.NAME_FONT_FAMILY_NAME )
                {
                    fd.setFontFamily( nr.getString() );
                }
            }

            OS2WindowsMetricsTable os2 = ttf.getOS2Windows();
            boolean isSymbolic = false;
            switch( os2.getFamilyClass() )
            {
                case OS2WindowsMetricsTable.FAMILY_CLASS_SYMBOLIC:
                    isSymbolic = true;
                    break;
                case OS2WindowsMetricsTable.FAMILY_CLASS_SCRIPTS:
                    fd.setScript( true );
                    break;
                case OS2WindowsMetricsTable.FAMILY_CLASS_CLAREDON_SERIFS:
                case OS2WindowsMetricsTable.FAMILY_CLASS_FREEFORM_SERIFS:
                case OS2WindowsMetricsTable.FAMILY_CLASS_MODERN_SERIFS:
                case OS2WindowsMetricsTable.FAMILY_CLASS_OLDSTYLE_SERIFS:
                case OS2WindowsMetricsTable.FAMILY_CLASS_SLAB_SERIFS:
                    fd.setSerif( true );
                    break;
                default:
                    //do nothing
            }
            switch( os2.getWidthClass() )
            {
                case OS2WindowsMetricsTable.WIDTH_CLASS_ULTRA_CONDENSED:
                    fd.setFontStretch( "UltraCondensed" );
                    break;
                case OS2WindowsMetricsTable.WIDTH_CLASS_EXTRA_CONDENSED:
                    fd.setFontStretch( "ExtraCondensed" );
                    break;
                case OS2WindowsMetricsTable.WIDTH_CLASS_CONDENSED:
                    fd.setFontStretch( "Condensed" );
                    break;
                case OS2WindowsMetricsTable.WIDTH_CLASS_SEMI_CONDENSED:
                    fd.setFontStretch( "SemiCondensed" );
                    break;
                case OS2WindowsMetricsTable.WIDTH_CLASS_MEDIUM:
                    fd.setFontStretch( "Normal" );
                    break;
                case OS2WindowsMetricsTable.WIDTH_CLASS_SEMI_EXPANDED:
                    fd.setFontStretch( "SemiExpanded" );
                    break;
                case OS2WindowsMetricsTable.WIDTH_CLASS_EXPANDED:
                    fd.setFontStretch( "Expanded" );
                    break;
                case OS2WindowsMetricsTable.WIDTH_CLASS_EXTRA_EXPANDED:
                    fd.setFontStretch( "ExtraExpanded" );
                    break;
                case OS2WindowsMetricsTable.WIDTH_CLASS_ULTRA_EXPANDED:
                    fd.setFontStretch( "UltraExpanded" );
                    break;
                default:
                    //do nothing
            }
            fd.setFontWeight( os2.getWeightClass() );
            fd.setSymbolic( isSymbolic );
            fd.setNonSymbolic( !isSymbolic );

            //todo retval.setItalic
            //todo retval.setAllCap
            //todo retval.setSmallCap
            //todo retval.setForceBold

            HeaderTable header = ttf.getHeader();
            PDRectangle rect = new PDRectangle();
            float scaling = 1000f/header.getUnitsPerEm();
            rect.setLowerLeftX( header.getXMin() * scaling );
            rect.setLowerLeftY( header.getYMin() * scaling );
            rect.setUpperRightX( header.getXMax() * scaling );
            rect.setUpperRightY( header.getYMax() * scaling );
            fd.setFontBoundingBox( rect );

            HorizontalHeaderTable hHeader = ttf.getHorizontalHeader();
            fd.setAscent( hHeader.getAscender() * scaling );
            fd.setDescent( hHeader.getDescender() * scaling );

            GlyphTable glyphTable = ttf.getGlyph();
            GlyphData[] glyphs = glyphTable.getGlyphs();

            PostScriptTable ps = ttf.getPostScript();
            fd.setFixedPitch( ps.getIsFixedPitch() > 0 );
            fd.setItalicAngle( ps.getItalicAngle() );

            String[] names = ps.getGlyphNames();
            
            if( names != null )
            {
                for( int i=0; i<names.length; i++ )
                {
                     //if we have a capital H then use that, otherwise use the
                    //tallest letter
                    if( names[i].equals( "H" ) )
                    {
                        fd.setCapHeight( glyphs[i].getBoundingBox().getUpperRightY()/scaling );
                    }
                    if( names[i].equals( "x" ) )
                    {
                        fd.setXHeight( glyphs[i].getBoundingBox().getUpperRightY()/scaling );
                    }
                }
            }

            //hmm there does not seem to be a clear definition for StemV,
            //this is close enough and I am told it doesn't usually get used.
            fd.setStemV( (fd.getFontBoundingBox().getWidth() * .13f) );

            CMAPTable cmapTable = ttf.getCMAP();
            CMAPEncodingEntry[] cmaps = cmapTable.getCmaps();
            CMAPEncodingEntry uniMap = null;
            
            for( int i=0; i<cmaps.length; i++ )
            {
                if( cmaps[i].getPlatformId() == CMAPTable.PLATFORM_WINDOWS) 
                {
                    int platformEncoding = cmaps[i].getPlatformEncodingId();
                    if ( CMAPTable.ENCODING_UNICODE == platformEncoding )
                    {
                        uniMap = cmaps[i];
                        break;
                    }
                }
            }

            Map<Integer, String> codeToName = this.getFontEncoding().getCodeToNameMap();
             
            int firstChar = Collections.min(codeToName.keySet());
            int lastChar = Collections.max(codeToName.keySet());
            
            HorizontalMetricsTable hMet = ttf.getHorizontalMetrics();
            int[] widthValues = hMet.getAdvanceWidth();
            // some monospaced fonts provide only one value for the width 
            // instead of an array containing the same value for every glyphid 
            boolean isMonospaced = fd.isFixedPitch();
            int nWidths=lastChar-firstChar+1;
            List<Float> widths = new ArrayList<Float>(nWidths);
            // use the first width as default
            // proportional fonts -> width of the .notdef character
            // monospaced-fonts -> the first width
            int defaultWidth = Math.round(widthValues[0] * scaling);
            for (int i = 0; i < nWidths; i++)
            {
                widths.add((float)defaultWidth);
            }
            // Encoding singleton to have acces to the chglyph name to
            // unicode cpoint point mapping of Adobe's glyphlist.txt
            Encoding glyphlist = WinAnsiEncoding.INSTANCE;

            // A character code is mapped to a glyph name via the provided
            // font encoding. Afterwards, the glyph name is translated to a
            // glyph ID.
            // For details, see PDFReference16.pdf, Section 5.5.5, p.401
            //
            for (Entry<Integer, String> e : codeToName.entrySet()) 
            {
                String name = e.getValue();
                // pdf code to unicode by glyph list.
                String c = glyphlist.getCharacter(name);
                int charCode = c.codePointAt(0);
                int gid = uniMap.getGlyphId(charCode);
                if (gid != 0) 
                {
                    if (isMonospaced)
                    {
                        widths.set(e.getKey().intValue() - firstChar, (float)defaultWidth);
                    }
                    else
                    {
                        widths.set(e.getKey().intValue() - firstChar, (float)Math.round(widthValues[gid] * scaling));
                    }
                }
            }
            setWidths( widths );
            setFirstChar( firstChar );
            setLastChar( lastChar );
        }
        finally
        {
            if( ttf != null )
            {
                ttf.close();
            }
        }
    }
}
