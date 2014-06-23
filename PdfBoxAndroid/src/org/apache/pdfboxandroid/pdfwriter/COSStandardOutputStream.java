/**
 * 
 */
package org.apache.pdfboxandroid.pdfwriter;

import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.channels.FileChannel;

import org.apache.pdfboxandroid.util.StringUtil;

/**
 * simple output stream with some minor features for generating "pretty"
 * pdf files.
 *
 * @author Michael Traut
 * @version $Revision: 1.5 $
 */
public class COSStandardOutputStream extends FilterOutputStream {
	/**
     * To be used when 2 byte sequence is enforced.
     */
    public static final byte[] CRLF = StringUtil.getBytes("\r\n");
	/**
	 * standard line separator.
	 */
	public static final byte[] EOL = StringUtil.getBytes("\n");

	// current byte pos in the output stream
	private long pos = 0;
	// flag to prevent generating two newlines in sequence
	private boolean onNewLine = false;
	// flag to prevent generating two newlines in sequence
	//    private boolean onNewLine = false;
	private FileChannel fileChannel = null;
	private FileDescriptor fileDescriptor = null;
	//    private long mark = -1;

	/**
	 * COSOutputStream constructor comment.
	 *
	 * @param out The underlying stream to write to.
	 */
	public COSStandardOutputStream(OutputStream out)
	{
		super(out);
		if(out instanceof FileOutputStream) {
			try {
				fileChannel = ((FileOutputStream)out).getChannel();
				fileDescriptor = ((FileOutputStream)out).getFD();
				pos = fileChannel.position();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * This will write an EOL to the stream.
	 *
	 * @throws IOException If there is an error writing to the stream
	 */
	public void writeEOL() throws IOException
	{
		if (!isOnNewLine())
		{
			write(EOL);
			setOnNewLine(true);
		}
	}

	/**
	 * This will tell if we are on a newline.
	 *
	 * @return true If we are on a newline.
	 */
	public boolean isOnNewLine()
	{
		return onNewLine;
	}

	/**
	 * This will set a flag telling if we are on a newline.
	 *
	 * @param newOnNewLine The new value for the onNewLine attribute.
	 */
	public void setOnNewLine(boolean newOnNewLine)
	{
		onNewLine = newOnNewLine;
	}
	
	/**
     * This will get the current position in the stream.
     *
     * @return The current position in the stream.
     */
    public long getPos()
    {
        return pos;
    }
    
    /**
     * This will get the current position in the stream.
     *
     * @return The current position in the stream.
     * @throws IOException 
     */
    public void setPos(long pos) throws IOException
    {
        if(fileChannel!=null) {
            checkPos();
            this.pos=pos;
            fileChannel.position(pos);
        }
    }
    
    private void checkPos() throws IOException 
    {
        if(fileChannel!=null && fileChannel.position() != getPos())
            throw new IOException("OutputStream has an invalid position");
    }
    
    /**
     * This will write a CRLF to the stream.
     *
     * @throws IOException If there is an error writing the data to the stream.
     */
    public void writeCRLF() throws IOException
    {
        write(CRLF);
    }
}
