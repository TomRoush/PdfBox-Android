package org.apache.pdfbox.util;

import java.nio.charset.Charset;

/**
 * Utility class providing common Charsets used in PDF.
 *
 * @author John Hewson
 */
public final class Charsets
{
	private Charsets() {}

	/*** ASCII charset */
	public static final Charset US_ASCII = Charset.forName("US-ASCII");

	/*** UTF-16BE charset */
	public static final Charset UTF_16BE = Charset.forName("UTF-16BE");

	/*** UTF-16LE charset */
	public static final Charset UTF_16LE = Charset.forName("UTF-16LE");
}