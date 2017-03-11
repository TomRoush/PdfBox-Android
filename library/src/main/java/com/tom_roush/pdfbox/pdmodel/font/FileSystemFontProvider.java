package com.tom_roush.pdfbox.pdmodel.font;

import android.util.Log;

import com.tom_roush.fontbox.cff.CFFFont;
import com.tom_roush.fontbox.cff.CFFParser;
import com.tom_roush.fontbox.ttf.NamingTable;
import com.tom_roush.fontbox.ttf.TTFParser;
import com.tom_roush.fontbox.ttf.TrueTypeCollection;
import com.tom_roush.fontbox.ttf.TrueTypeFont;
import com.tom_roush.fontbox.type1.Type1Font;
import com.tom_roush.fontbox.util.autodetect.FontFileFinder;
import com.tom_roush.pdfbox.io.IOUtils;
import com.tom_roush.pdfbox.util.PDFBoxResourceLoader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * External font provider which searches for fonts on the local filesystem.
 *
 * @author John Hewson
 */
final class FileSystemFontProvider extends FontProvider
{
    // cache of font files on the system (populated in constructor)
    private final Map<String, File> ttfFontFiles = new HashMap<String, File>();
    private final Map<String, File> cffFontFiles = new HashMap<String, File>();
    private final Map<String, File> type1FontFiles =  new HashMap<String, File>();

    // cache of loaded fonts which are in use (populated on-the-fly)
    private final Map<String, TrueTypeFont> ttfFonts = new HashMap<String, TrueTypeFont>();
    private final Map<String, CFFFont> cffFonts = new HashMap<String, CFFFont>();
    private final Map<String, Type1Font> type1Fonts = new HashMap<String, Type1Font>();

    /**
     * Constructor.
     */
    FileSystemFontProvider()
    {
        // XXX: load in background?
        if(PDFBoxResourceLoader.LOAD_FONTS == PDFBoxResourceLoader.FontLoadLevel.NONE) return;
        if(PDFBoxResourceLoader.LOAD_FONTS == PDFBoxResourceLoader.FontLoadLevel.MINIMUM) {
            // If MINIMUM, load only Droid fonts
            try
            {
                addTrueTypeFont(new File("/system/fonts/DroidSans.ttf"));
                addTrueTypeFont(new File("/system/fonts/DroidSans-Bold.ttf"));
                addTrueTypeFont(new File("/system/fonts/DroidSansMono.ttf"));
//                addTrueTypeFont(new File("/system/fonts/DroidSansFallback.ttf"));
                // XXX: list may need to be expanded for other character sets
                return;
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }

    	Log.v("PdfBoxAndroid", "Will search the local system for fonts");

        int count = 0;
        FontFileFinder fontFileFinder = new FontFileFinder();
        List<URI> fonts = fontFileFinder.find();
        for (URI font : fonts)
        {
            count++;
            File fontFile = new File(font);
            try
            {
                if (fontFile.getPath().toLowerCase().endsWith(".ttf") ||
                    fontFile.getPath().toLowerCase().endsWith(".otf"))
                {
                    addTrueTypeFont(fontFile);
                }
                else if (fontFile.getPath().toLowerCase().endsWith(".ttc") ||
                        fontFile.getPath().toLowerCase().endsWith(".otc"))
                {
                    addTrueTypeCollection(fontFile);
                }
                else if (fontFile.getPath().toLowerCase().endsWith(".pfb"))
                {
                    addType1Font(fontFile);
                }
            }
            catch (IOException e)
            {
            	Log.e("PdfBoxAndroid", "Error parsing font " + fontFile.getPath(), e);
            }
        }

        Log.v("PdfBoxAndroid", "Found " + count + " fonts on the local system");
    }

    /**
     * Adds a TTC or OTC to the file cache. To reduce memory, the parsed font is not cached.
     */
    private void addTrueTypeCollection(File ttcFile) throws IOException
    {
        TrueTypeCollection ttc = null;
        try
        {
            ttc = new TrueTypeCollection(ttcFile);
            for (TrueTypeFont ttf : ttc.getFonts())
            {
                addTrueTypeFontImpl(ttf, ttcFile);
            }
        }
        catch (NullPointerException e) // TTF parser is buggy
        {
            Log.e("PdfBox-Android", "Could not load font file: " + ttcFile, e);
        }
        catch (IOException e)
        {
            Log.e("PdfBox-Android", "Could not load font file: " + ttcFile, e);
        }
        finally
        {
            if (ttc != null)
            {
                ttc.close();
            }
        }
    }

    /**
     * Adds an OTF or TTF font to the file cache. To reduce memory, the parsed font is not cached.
     */
    private void addTrueTypeFont(File ttfFile) throws IOException
    {
        TTFParser ttfParser = new TTFParser(false, true);
        try
        {
            TrueTypeFont ttf = ttfParser.parse(ttfFile);
            addTrueTypeFontImpl(ttf, ttfFile);
        }
        catch (NullPointerException e) // TTF parser is buggy
        {
            Log.e("PdfBoxAndroid", "Could not load font file: " + ttfFile, e);
        }
        catch (IOException e)
        {
            Log.e("PdfBoxAndroid", "Could not load font file: " + ttfFile, e);
        }
    }

    /**
     * Adds an OTF or TTf font to the file cache. To reduce memory, the parsed font is not cached.
     */
    private void addTrueTypeFontImpl(TrueTypeFont ttf, File file) throws IOException
    {
        try
        {
        	// check for 'name' table
        	NamingTable nameTable = null;

        	// ttf could still be null
        	if (ttf != null)
        	{
        		nameTable = ttf.getNaming();
        	}
            if (nameTable == null)
            {
            	Log.w("PdfBoxAndroid", "Missing 'name' table in font " + file);
            }
            else
            {
                // read PostScript name, if any
                if (nameTable.getPostScriptName() != null)
                {
                    String psName = nameTable.getPostScriptName();

                    String format;
                    if (ttf.getTableMap().get("CFF ") != null)
                    {
                        format = "OTF";
                        cffFontFiles.putAll(toMap(getNames(ttf), file));
                    }
                    else
                    {
                        format = "TTF";
                        ttfFontFiles.putAll(toMap(getNames(ttf), file));
                    }

                    Log.v("PdfBoxAndroid", format +": '" + psName + "' / '" + nameTable.getFontFamily() +
                    		"' / '" + nameTable.getFontSubFamily() + "'");
                }
                else
                {
                	Log.w("PdfBoxAndroid", "Missing 'name' entry for PostScript name in font " + file);
                }
            }
        }
        finally
        {
            if (ttf != null)
            {
                ttf.close();
            }
        }
    }

    /**
     * Adds a Type 1 font to the file cache. To reduce memory, the parsed font is not cached.
     */
    private void addType1Font(File pfbFile) throws IOException
    {
        InputStream input = new FileInputStream(pfbFile);
        try
        {
            Type1Font type1 = Type1Font.createWithPFB(input);

            String psName = type1.getFontName();
            type1FontFiles.putAll(toMap(getNames(type1), pfbFile));

            Log.v("PdfBoxAndroid", "PFB: '" + psName + "' / '" + type1.getFamilyName() + "' / '" +
            		type1.getWeight() + "'");
        }
        finally
        {
            input.close();
        }
    }

    @Override
    public synchronized TrueTypeFont getTrueTypeFont(String postScriptName)
    {
        TrueTypeFont ttf = ttfFonts.get(postScriptName);
        if (ttf != null)
        {
            return ttf;
        }

        File file = ttfFontFiles.get(postScriptName);
        if (file != null)
        {
            try
            {
                ttf = readTrueTypeFont(postScriptName, file);

                for (String name : getNames(ttf))
                {
                    ttfFonts.put(name, ttf);
                }
                Log.d("PdfBoxAndroid", "Loaded " + postScriptName + " from " + file);
                return ttf;
            }
            catch (NullPointerException e) // TTF parser is buggy
            {
            	Log.e("PdfBoxAndroid", "Could not load font file: " + file, e);
            }
            catch (IOException e)
            {
            	Log.e("PdfBoxAndroid", "Could not load font file: " + file, e);
            }
        }
        return null;
    }

    private TrueTypeFont readTrueTypeFont(String postScriptName, File file) throws IOException
    {
        if (file.getName().toLowerCase().endsWith(".ttc"))
        {
            TrueTypeCollection ttc = new TrueTypeCollection(file);
            for (TrueTypeFont ttf : ttc.getFonts())
            {
                if (ttf.getName().equals(postScriptName))
                {
                    return ttf;
                }
            }
            throw new IOException("Font " + postScriptName + " not found in " + file);
        }
        else
        {
            TTFParser ttfParser = new TTFParser(false, true);
            return ttfParser.parse(file);
        }
    }

    @Override
    public synchronized CFFFont getCFFFont(String postScriptName)
    {
        CFFFont cff = cffFonts.get(postScriptName);
        if (cff != null)
        {
            return cff;
        }

        File file = cffFontFiles.get(postScriptName);
        if (file != null)
        {
            InputStream input = null;
            try
            {
                // todo JH: we don't yet support loading CFF fonts from OTC collections 
                input = new FileInputStream(file);
                byte[] bytes = IOUtils.toByteArray(input);
                CFFParser cffParser = new CFFParser();
                cff = cffParser.parse(bytes).get(0);
                for (String name : getNames(cff))
                {
                    cffFonts.put(name, cff);
                }
                Log.d("PdfBoxAndroid", "Loaded " + postScriptName + " from " + file);
                return cff;
            }
            catch (IOException e)
            {
            	Log.e("PdfBoxAndroid", "Could not load font file: " + file, e);
            }
            finally
            {
                IOUtils.closeQuietly(input);
            }
        }
        return null;
    }

    @Override
    public synchronized Type1Font getType1Font(String postScriptName)
    {
        Type1Font type1 = type1Fonts.get(postScriptName);
        if (type1 != null)
        {
            return type1;
        }

        File file = type1FontFiles.get(postScriptName);
        if (file != null)
        {
            InputStream input = null;
            try
            {
                input = new FileInputStream(file);
                type1 = Type1Font.createWithPFB(input);
                for (String name : getNames(type1))
                {
                    type1Fonts.put(name, type1);
                }
                Log.d("PdfBoxAndroid", "Loaded " + postScriptName + " from " + file);
                return type1;
            }
            catch (IOException e)
            {
            	Log.e("PdfBoxAndroid", "Could not load font file: " + file, e);
            }
            finally
            {
                IOUtils.closeQuietly(input);
            }
        }
        return null;
    }

    /**
     * Returns a map containing the given file for each string key.
     */
    private Map<String, File> toMap(Set<String> names, File file)
    {
        Map<String, File> map = new HashMap<String, File>();
        for (String name : names)
        {
            map.put(name, file);
        }
        return map;
    }

    @Override
    public String toDebugString()
    {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, File> entry : ttfFontFiles.entrySet())
        {
            sb.append("TTF: ");
            sb.append(entry.getKey());
            sb.append(": ");
            sb.append(entry.getValue().getPath());
            sb.append('\n');
        }
        for (Map.Entry<String, File> entry : cffFontFiles.entrySet())
        {
            sb.append("OTF: ");
            sb.append(entry.getKey());
            sb.append(": ");
            sb.append(entry.getValue().getPath());
            sb.append('\n');
        }
        for (Map.Entry<String, File> entry : type1FontFiles.entrySet())
        {
            sb.append("PFB: ");
            sb.append(entry.getKey());
            sb.append(": ");
            sb.append(entry.getValue().getPath());
            sb.append('\n');
        }
        return sb.toString();
    }
}
