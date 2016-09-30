package com.tom_roush.pdfbox.pdmodel.interactive.digitalsignature;

import java.io.IOException;
import java.io.InputStream;

/**
 * Providing an interface for accessing necessary functions for signing a PDF document.
 *
 * @author Thomas Chojecki
 */
public interface SignatureInterface
{
    /**
     * Creates a cms signature for the given content
     *
     * @param content is the content as a (Filter)InputStream
     * @return signature as a byte array
     */
    byte[] sign(InputStream content) throws IOException;
}
