package org.apache.pdfbox.util;

import java.io.IOException;
import java.net.URL;
import java.util.Properties;

/**
 * Exposes PDFBox version.
 */
public final class Version
{
    private static final String PDFBOX_VERSION_PROPERTIES =
            "org/apache/pdfbox/resources/pdfbox.properties";

    private Version()
    {
        // static helper
    }

    /**
     * Returns the version of PDFBox.
     */
    public static String getVersion()
    {
        try
        {
            URL url = Version.class.getClassLoader().getResource(PDFBOX_VERSION_PROPERTIES);
            if (url == null)
            {
                return null;
            }
            Properties properties = new Properties();
            properties.load(url.openStream());
            return properties.getProperty("pdfbox.version", null);
        }
        catch (IOException io)
        {
            return null;
        }
    }
}
