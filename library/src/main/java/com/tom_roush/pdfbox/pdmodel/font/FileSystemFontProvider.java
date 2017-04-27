package com.tom_roush.pdfbox.pdmodel.font;

import android.util.Log;

import com.tom_roush.fontbox.FontBoxFont;
import com.tom_roush.fontbox.cff.CFFCIDFont;
import com.tom_roush.fontbox.cff.CFFFont;
import com.tom_roush.fontbox.ttf.NamingTable;
import com.tom_roush.fontbox.ttf.OTFParser;
import com.tom_roush.fontbox.ttf.OpenTypeFont;
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
import java.util.ArrayList;
import java.util.List;

/**
 * A FontProvider which searches for fonts on the local filesystem.
 *
 * @author John Hewson
 */
final class FileSystemFontProvider extends FontProvider
{
    private final List<FSFontInfo> fontInfoList = new ArrayList<FSFontInfo>();
    private final FontCache cache;

    private class FSFontInfo extends FontInfo
    {
        private final String postScriptName;
        private final FontFormat format;
        private final PDCIDSystemInfo cidSystemInfo;
        private final File file;

        private FSFontInfo(File file, FontFormat format, String postScriptName,
            PDCIDSystemInfo cidSystemInfo)
        {
            this.file = file;
            this.format = format;
            this.postScriptName = postScriptName;
            this.cidSystemInfo = cidSystemInfo;
        }

        @Override
        public String getPostScriptName()
        {
            return postScriptName;
        }

        @Override
        public FontFormat getFormat()
        {
            return format;
        }

        @Override
        public PDCIDSystemInfo getCIDSystemInfo()
        {
            return cidSystemInfo;
        }

        @Override
        public FontBoxFont getFont()
        {
            FontBoxFont cached = cache.getFont(this);
            if (cached != null)
            {
                return cached;
            }
            else
            {
                FontBoxFont font;
                switch (format)
                {
                    case PFB:
                        font = getType1Font(postScriptName, file);
                        break;
                    case TTF:
                        font = getTrueTypeFont(postScriptName, file);
                        break;
                    case OTF:
                        font = getOTFFont(postScriptName, file);
                        break;
                    default:
                        throw new RuntimeException("can't happen");
                }
                cache.addFont(this, font);
                return font;
            }
        }

        @Override
        public String toString()
        {
            return super.toString() + " " + file;
        }
    }

    /**
     * Constructor.
     */
    FileSystemFontProvider(FontCache cache)
    {
        this.cache = cache;

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

    	Log.v("PdfBox-Android", "Will search the local system for fonts");

        List<File> files = new ArrayList<File>();
        FontFileFinder fontFileFinder = new FontFileFinder();
        List<URI> fonts = fontFileFinder.find();
        for (URI font : fonts)
        {
            files.add(new File(font));
        }
        Log.v("PdfBox-Android", "Found " + files.size() + " fonts on the local system");

        // todo: loading all of these fonts is slow, can we cache this?
        for (File file : files)
        {
            try
            {
                if (file.getPath().toLowerCase().endsWith(".ttf") ||
                    file.getPath().toLowerCase().endsWith(".otf"))
                {
                    addTrueTypeFont(file);
                }
                else if (file.getPath().toLowerCase().endsWith(".ttc") ||
                    file.getPath().toLowerCase().endsWith(".otc"))
                {
                    addTrueTypeCollection(file);
                }
                else if (file.getPath().toLowerCase().endsWith(".pfb"))
                {
                    addType1Font(file);
                }
            }
            catch (IOException e)
            {
                Log.e("PdfBox-Android", "Error parsing font " + file.getPath(), e);
            }
        }
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
        try
        {
            if (ttfFile.getPath().endsWith(".otf"))
            {
                OTFParser parser = new OTFParser(false, true);
                OpenTypeFont otf = parser.parse(ttfFile);
                addTrueTypeFontImpl(otf, ttfFile);
            }
            else
            {
                TTFParser parser = new TTFParser(false, true);
                TrueTypeFont ttf = parser.parse(ttfFile);
                addTrueTypeFontImpl(ttf, ttfFile);
            }
        }
        catch (NullPointerException e) // TTF parser is buggy
        {
            Log.e("PdfBox-Android", "Could not load font file: " + ttfFile, e);
        }
        catch (IOException e)
        {
            Log.e("PdfBox-Android", "Could not load font file: " + ttfFile, e);
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
                // todo: this is a performance bottleneck, we don't actually need to read this table
                nameTable = ttf.getNaming();
            }
            if (nameTable == null)
            {
            	Log.w("PdfBox-Android", "Missing 'name' table in font " + file);
            }
            else
            {
                // read PostScript name, if any
                if (ttf.getName() != null)
                {
                    String format;
                    if (ttf instanceof OpenTypeFont)
                    {
                        format = "OTF";
                        CFFFont cff = ((OpenTypeFont) ttf).getCFF().getFont();
                        PDCIDSystemInfo ros = null;
                        if (cff instanceof CFFCIDFont)
                        {
                            CFFCIDFont cidFont = (CFFCIDFont) cff;
                            String registry = cidFont.getRegistry();
                            String ordering = cidFont.getOrdering();
                            int supplement = cidFont.getSupplement();
                            ros = new PDCIDSystemInfo(registry, ordering, supplement);
                        }
                        fontInfoList.add(new FSFontInfo(file, FontFormat.OTF, ttf.getName(), ros));
                    }
                    else
                    {
                        format = "TTF";
                        fontInfoList.add(new FSFontInfo(file, FontFormat.TTF, ttf.getName(), null));
                    }

                    Log.v("PdfBox-Android", format + ": '" + ttf.getName() + "' / '" +
                        nameTable.getFontFamily() + "' / '" + nameTable.getFontSubFamily() + "'");
                }
                else
                {
                	Log.w("PdfBox-Android", "Missing 'name' entry for PostScript name in font " + file);
                }
            }
        }
        catch (IOException e)
        {
            Log.e("PdfBox-Android", "Could not load font file: " + file, e);
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

            fontInfoList.add(new FSFontInfo(pfbFile, FontFormat.PFB, type1.getName(), null));

            Log.v("PdfBox-Android", "PFB: '" + type1.getName() + "' / '" + type1.getFamilyName() +
                "' / '" + type1.getWeight() + "'");
        }
        catch (IOException e)
        {
            Log.e("PdfBox-Android", "Could not load font file: " + pfbFile, e);
        }
        finally
        {
            input.close();
        }
    }

    private TrueTypeFont getTrueTypeFont(String postScriptName, File file)
    {
        try
        {
            TrueTypeFont ttf = readTrueTypeFont(postScriptName, file);
            Log.d("PdfBox-Android", "Loaded " + postScriptName + " from " + file);
            return ttf;
        }
        catch (NullPointerException e) // TTF parser is buggy
        {
            Log.d("PdfBox-Android", "Could not load font file: " + file, e);
        }
        catch (IOException e)
        {
            Log.d("PdfBox-Android", "Could not load font file: " + file, e);
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

    private OpenTypeFont getOTFFont(String postScriptName, File file)
    {
        try
        {
            // todo JH: we don't yet support loading CFF fonts from OTC collections 
            OTFParser parser = new OTFParser(false, true);
            OpenTypeFont otf = parser.parse(file);
            Log.d("PdfBox-Android", "Loaded " + postScriptName + " from " + file);
            return otf;
        }
        catch (IOException e)
        {
            Log.e("PdfBox-Android", "Could not load font file: " + file, e);
        }
        return null;
    }

    private Type1Font getType1Font(String postScriptName, File file)
    {
        InputStream input = null;
        try
        {
            input = new FileInputStream(file);
            Type1Font type1 = Type1Font.createWithPFB(input);
            Log.d("PdfBox-Android", "Loaded " + postScriptName + " from " + file);
            return type1;
        }
        catch (IOException e)
        {
            Log.e("PdfBox-Android", "Could not load font file: " + file, e);
        }
        finally
        {
            IOUtils.closeQuietly(input);
        }
        return null;
    }

    @Override
    public String toDebugString()
    {
        StringBuilder sb = new StringBuilder();
        for (FSFontInfo info : fontInfoList)
        {
            sb.append(info.getFormat());
            sb.append(": ");
            sb.append(info.getPostScriptName());
            sb.append(": ");
            sb.append(info.file.getPath());
            sb.append('\n');
        }
        return sb.toString();
    }

    @Override
    public List<? extends FontInfo> getFontInfo()
    {
        return fontInfoList;
    }
}
