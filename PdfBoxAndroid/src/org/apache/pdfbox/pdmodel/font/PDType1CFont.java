package org.apache.pdfbox.pdmodel.font;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.fontbox.afm.FontMetric;
import org.apache.fontbox.cff.CFFFont;
import org.apache.fontbox.cff.CFFParser;
import org.apache.fontbox.cff.charset.CFFCharset;
import org.apache.fontbox.cff.encoding.CFFEncoding;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSNumber;
import org.apache.pdfbox.encoding.Encoding;
import org.apache.pdfbox.encoding.EncodingManager;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.common.PDStream;

/**
 * This class represents a CFF/Type2 Font (aka Type1C Font).
 * @author Villu Ruusmann
 */
public class PDType1CFont extends PDSimpleFont
{
	
	private CFFFont cffFont = null;

    private String fontname = null;

    private Map<Integer, String> sidToName = new HashMap<Integer, String>();

    private Map<Integer, Integer> codeToSID = new HashMap<Integer, Integer>();

    private Map<Integer, String> sidToCharacter = new HashMap<Integer, String>();

    private Map<String, Integer> characterToSID = new HashMap<String, Integer>();

    private FontMetric fontMetric = null;

//    private Font awtFont = null;

    private Map<String, Float> glyphWidths = new HashMap<String, Float>();

    private Map<String, Float> glyphHeights = new HashMap<String, Float>();

    private Float avgWidth = null;

    private PDRectangle fontBBox = null;

    private static final Log log = LogFactory.getLog(PDType1CFont.class);

    private static final byte[] SPACE_BYTES = {(byte)32};

	/**
     * Constructor.
     * @param fontDictionary the corresponding dictionary
     */
    public PDType1CFont( COSDictionary fontDictionary ) throws IOException
    {
        super( fontDictionary );
        load();
    }
    
    private void load() throws IOException
    {
        byte[] cffBytes = loadBytes();

        CFFParser cffParser = new CFFParser();
        List<CFFFont> fonts = cffParser.parse(cffBytes);

        String baseFontName = getBaseFont();
        if (fonts.size() > 1 && baseFontName != null)
        {
            for (CFFFont font: fonts) 
            {
                if (baseFontName.equals(font.getName())) 
                {
                    cffFont = font;
                    break;
                }
            }
        }
        if (cffFont == null) 
        {
            cffFont = (CFFFont)fonts.get(0);
        }

        // cache the font name
        fontname = cffFont.getName();

        Number defaultWidthX = (Number) cffFont.getProperty("defaultWidthX");
        this.glyphWidths.put(null, Float.valueOf(defaultWidthX.floatValue()));

        CFFEncoding encoding = cffFont.getEncoding();
        PDFEncoding pdfEncoding = new PDFEncoding(encoding);

        CFFCharset charset = cffFont.getCharset();
        PDFCharset pdfCharset = new PDFCharset(charset);

        Map<String,byte[]> charStringsDict = cffFont.getCharStringsDict();
        Map<String,byte[]> pdfCharStringsDict = new LinkedHashMap<String,byte[]>();
        pdfCharStringsDict.put(".notdef", charStringsDict.get(".notdef"));

        Map<Integer,String> codeToNameMap = new LinkedHashMap<Integer,String>();

        Collection<CFFFont.Mapping> mappings = cffFont.getMappings();
        for( Iterator<CFFFont.Mapping> it = mappings.iterator(); it.hasNext();)
        {
            CFFFont.Mapping mapping = it.next();
            Integer code = Integer.valueOf(mapping.getCode());
            String name = mapping.getName();
            codeToNameMap.put(code, name);
        }

        Set<String> knownNames = new HashSet<String>(codeToNameMap.values());

        Map<Integer,String> codeToNameOverride = loadOverride();
        for( Iterator<Map.Entry<Integer, String>> it = (codeToNameOverride.entrySet()).iterator(); it.hasNext();)
        {
            Map.Entry<Integer, String> entry = it.next();
            Integer code = (Integer)entry.getKey();
            String name = (String)entry.getValue();
            if(knownNames.contains(name))
            {
                codeToNameMap.put(code, name);
            }
        }

        Map<String,String> nameToCharacter;
        try
        {
            // TODO remove access by reflection
            Field nameToCharacterField = Encoding.class.getDeclaredField("NAME_TO_CHARACTER");
            nameToCharacterField.setAccessible(true);
            nameToCharacter = (Map<String,String>)nameToCharacterField.get(null);
        }
        catch( Exception e )
        {
            throw new RuntimeException(e);
        }

        for( Iterator<Map.Entry<Integer,String>> it = (codeToNameMap.entrySet()).iterator(); it.hasNext();)
        {
            Map.Entry<Integer,String> entry = it.next();
            Integer code = (Integer)entry.getKey();
            String name = (String)entry.getValue();
            String uniName = "uni";
            String character = (String)nameToCharacter.get(name);
            if( character != null )
            {
                for( int j = 0; j < character.length(); j++ )
                {
                    uniName += hexString(character.charAt(j), 4);
                }
            }
            else
            {
                uniName += hexString(code.intValue(), 4);
                character = String.valueOf((char)code.intValue());
            }
            pdfEncoding.register(code.intValue(), code.intValue());
            pdfCharset.register(code.intValue(), uniName);
            pdfCharStringsDict.put(uniName, charStringsDict.get(name));
        }

        cffFont.setEncoding(pdfEncoding);
        cffFont.setCharset(pdfCharset);
        charStringsDict.clear();
        charStringsDict.putAll(pdfCharStringsDict);

        
        Encoding fontEncoding = getFontEncoding();
        Map<String, Integer> nameToCode = fontEncoding != null ? fontEncoding.getNameToCodeMap() : null;
        for (CFFFont.Mapping mapping : mappings)
        {
            int sid = mapping.getSID();
            String name = mapping.getName();
            String character = null;
            if (nameToCode != null && nameToCode.containsKey(name))
            {
                sid = nameToCode.get(name);
                character = fontEncoding.getCharacter(name);
            }
            if (character == null)
            {
                character = Encoding.getCharacterForName(name);
            }
            sidToName.put(sid, name);
            codeToSID.put(mapping.getCode(), sid);
            if (character != null)
            {
                sidToCharacter.put(sid, character);
                characterToSID.put(character, sid);
            }
        }

    }
    
    private byte[] loadBytes() throws IOException
    {
        PDFontDescriptor fd = getFontDescriptor();
        if( fd != null && fd instanceof PDFontDescriptorDictionary)
        {
            PDStream ff3Stream = ((PDFontDescriptorDictionary)fd).getFontFile3();
            if( ff3Stream != null )
            {
                ByteArrayOutputStream os = new ByteArrayOutputStream();

                InputStream is = ff3Stream.createInputStream();
                try
                {
                    byte[] buf = new byte[512];
                    while(true)
                    {
                        int count = is.read(buf);
                        if( count < 0 )
                        {
                            break;
                        }
                        os.write(buf, 0, count);
                    }
                }
                finally
                {
                    is.close();
                }

                return os.toByteArray();
            }
        }

        throw new IOException();
    }
    
    private static String hexString( int code, int length )
    {
        String string = Integer.toHexString(code);
        while(string.length() < length)
        {
            string = ("0" + string);
        }

        return string;
    }
    
    private Map<Integer,String> loadOverride() throws IOException
    {
        Map<Integer,String> result = new LinkedHashMap<Integer,String>();
        COSBase encoding = getEncoding();
        if( encoding instanceof COSName )
        {
            COSName name = (COSName)encoding;
            result.putAll(loadEncoding(name));
        }
        else if( encoding instanceof COSDictionary )
        {
            COSDictionary encodingDic = (COSDictionary)encoding;
            COSName baseName = (COSName)encodingDic.getDictionaryObject(COSName.BASE_ENCODING);
            if( baseName != null )
            {
                result.putAll(loadEncoding(baseName));
            }
            COSArray differences = (COSArray)encodingDic.getDictionaryObject(COSName.DIFFERENCES);
            if( differences != null )
            {
                result.putAll(loadDifferences(differences));
            }
        }

        return result;
    }
    
    private Map<Integer,String> loadEncoding(COSName name) throws IOException
    {
        Map<Integer,String> result = new LinkedHashMap<Integer,String>();
        Encoding encoding = EncodingManager.INSTANCE.getEncoding(name);
        for( Iterator<Map.Entry<Integer,String>> it = (encoding.getCodeToNameMap().entrySet()).iterator();
                    it.hasNext();)
        {
            Map.Entry<Integer,String> entry = it.next();
            result.put(entry.getKey(), (entry.getValue()));
        }

        return result;
    }

    private Map<Integer,String> loadDifferences(COSArray differences)
    {
        Map<Integer,String> result = new LinkedHashMap<Integer,String>();
        Integer code = null;
        for( int i = 0; i < differences.size(); i++)
        {
            COSBase element = differences.get(i);
            if( element instanceof COSNumber )
            {
                COSNumber number = (COSNumber)element;
                code = Integer.valueOf(number.intValue());
            } 
            else 
            {
                if( element instanceof COSName )
                {
                    COSName name = (COSName)element;
                    result.put(code, name.getName());
                    code = Integer.valueOf(code.intValue() + 1);
                }
            }
        }
        return result;
    }
    
    /**
     * This class represents a PDFEncoding.
     *
     */
    private static class PDFEncoding extends CFFEncoding
    {

        private PDFEncoding( CFFEncoding parent )
        {
            Iterator<Entry> parentEntries = parent.getEntries().iterator();
            while(parentEntries.hasNext())
            {
                addEntry(parentEntries.next());
            }
        }

        public boolean isFontSpecific()
        {
            return true;
        }

    }

    /**
     * This class represents a PDFCharset.
     *
     */
    private static class PDFCharset extends CFFCharset
    {
        private PDFCharset( CFFCharset parent )
        {
            Iterator<Entry> parentEntries = parent.getEntries().iterator();
            while(parentEntries.hasNext())
            {
                addEntry(parentEntries.next());
            }
        }

        public boolean isFontSpecific()
        {
            return true;
        }

    }

}
