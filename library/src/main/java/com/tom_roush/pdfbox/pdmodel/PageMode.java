package com.tom_roush.pdfbox.pdmodel;

/**
 * A name object specifying how the document shall be displayed when opened.
 *
 * @author John Hewson
 */
public enum PageMode
{
    /** Neither the outline nor the thumbnails are displayed. */
    USE_NONE("UseNone"),

    /** Show bookmarks when pdf is opened. */
    USE_OUTLINES("UseOutlines"),

    /** Show thumbnails when pdf is opened. */
    USE_THUMBS("UseThumbs"),

    /** Full screen mode with no menu bar, window controls. */
    FULL_SCREEN("FullScreen"),

    /** Optional content group panel is visible when opened. */
    USE_OPTIONAL_CONTENT("UseOC"),

    /** Attachments panel is visible. */
    USE_ATTACHMENTS("UseAttachments");

    public static PageMode fromString(String value)
    {
        if (value.equals("UseNone"))
        {
            return USE_NONE;
        }
        else if (value.equals("UseOutlines"))
        {
            return USE_OUTLINES;
        }
        else if (value.equals("UseThumbs"))
        {
            return USE_THUMBS;
        }
        else if (value.equals("FullScreen"))
        {
            return FULL_SCREEN;
        }
        else if (value.equals("UseOC"))
        {
            return USE_OPTIONAL_CONTENT;
        }
        else if (value.equals("UseAttachments"))
        {
            return USE_ATTACHMENTS;
        }
        throw new IllegalArgumentException(value);
    }

    private final String value;

    PageMode(String value)
    {
        this.value = value;
    }

    /**
     * Returns the string value, as used in a PDF file.
     */
    public String stringValue()
    {
        return value;
    }
}