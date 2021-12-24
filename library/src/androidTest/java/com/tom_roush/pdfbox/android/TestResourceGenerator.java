package com.tom_roush.pdfbox.android;

import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;

import com.tom_roush.pdfbox.io.IOUtils;

public class TestResourceGenerator
{
    public static File downloadTestResource(File targetDir, String targetName, String url) {
        File resourceFile = new File(targetDir, targetName);
        if (!resourceFile.exists())
        {
            try
            {
                Log.i("PdfBox-Android", "Resource file not cached, Downloading file " + targetName);
                InputStream urlStream = new URL(url).openStream();
                IOUtils.copy(urlStream, new FileOutputStream(resourceFile));
            }
            catch (Exception e)
            {
                Log.w("PdfBox-Android", "Unable to download test file. Test will be skipped");
                return null;
            }
        }
        return resourceFile;
    }
}
