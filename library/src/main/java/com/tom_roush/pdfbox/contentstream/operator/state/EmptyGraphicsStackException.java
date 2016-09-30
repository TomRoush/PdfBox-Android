package org.apache.pdfbox.contentstream.operator.state;

import java.io.IOException;

/**
 * Throw when restore is executed when the graphics stack is empty.
 */
public final class EmptyGraphicsStackException extends IOException
{
    /**
	 * See https://stackoverflow.com/questions/285793/
	 */
	private static final long serialVersionUID = 1L;

	EmptyGraphicsStackException()
    {
        super("Cannot execute restore, the graphics stack is empty");
    }
}