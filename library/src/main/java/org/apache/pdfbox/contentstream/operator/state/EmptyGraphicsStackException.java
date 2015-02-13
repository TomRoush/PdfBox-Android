package org.apache.pdfbox.contentstream.operator.state;

import java.io.IOException;

/**
 * Throw when restore is executed when the graphics stack is empty.
 */
public final class EmptyGraphicsStackException extends IOException
{
    EmptyGraphicsStackException()
    {
        super("Cannot execute restore, the graphics stack is empty");
    }
}