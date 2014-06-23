package org.apache.pdfboxandroid.encoding;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.pdfboxandroid.PDFBox;
import org.apache.pdfboxandroid.pdmodel.common.COSObjectable;

import android.util.Log;

/**
 * This is an interface to a text encoder.
 *
 * @author <a href="mailto:ben@benlitchfield.com">Ben Litchfield</a>
 * @version $Revision: 1.15 $
 */
public abstract class Encoding implements COSObjectable {
	/**
     * This is a mapping from a character code to a character name.
     */
    protected final Map<Integer, String> codeToName =
        new HashMap<Integer, String>();
    
    /**
     * This is a mapping from a character name to a character code.
     */
    protected final Map<String, Integer> nameToCode =
        new HashMap<String, Integer>();
    
    private static final Map<String, String> NAME_TO_CHARACTER =
            new HashMap<String, String>();
	
	/**
     * This will get the character from the code.
     *
     * @param code The character code.
     *
     * @return The printable character for the code.
     *
     * @throws IOException If there is not name for the character.
     */
    public String getCharacter( int code ) throws IOException
    {
        String name = getName( code );
        if (name != null)
        {
            return getCharacter( getName( code ) );
        }
        return null;
    }
    
    /**
     * This will get the character from the name.
     *
     * @param name The name of the character.
     *
     * @return The printable character for the code.
     */
    public String getCharacter( String name )
    {
        String character = NAME_TO_CHARACTER.get( name );
        if( character == null )
        {
            // test if we have a suffix and if so remove it
            if ( name.indexOf('.') > 0 )
            {
                character = getCharacter(name.substring( 0, name.indexOf('.') ));
            }
            // test for Unicode name
            // (uniXXXX - XXXX must be a multiple of four;
            // each representing a hexadecimal Unicode code point)
            else if ( name.startsWith( "uni" ) )
            {
                int nameLength = name.length();
                StringBuilder uniStr = new StringBuilder();
                try
                {
                    for ( int chPos = 3; chPos + 4 <= nameLength; chPos += 4 )
                    {
                        int characterCode = Integer.parseInt( name.substring( chPos, chPos + 4), 16 );

                        if ( characterCode > 0xD7FF && characterCode < 0xE000 )
                        {
                            Log.w(PDFBox.LOG_TAG, "Unicode character name with not allowed code area: " + name);
                        }
                        else
                        {
                            uniStr.append( (char) characterCode );
                        }
                    }
                    character = uniStr.toString();
                    NAME_TO_CHARACTER.put(name, character);
                }
                catch (NumberFormatException nfe)
                {
                    Log.w( PDFBox.LOG_TAG, "Not a number in Unicode character name: " + name );
                    character = name;
                }
            }
            // test for an alternate Unicode name representation 
            else if ( name.startsWith( "u" ) )
            {
                try
                {
                    int characterCode = Integer.parseInt( name.substring( 1 ), 16 );
                    if ( characterCode > 0xD7FF && characterCode < 0xE000 )
                    {
                        Log.w( PDFBox.LOG_TAG, "Unicode character name with not allowed code area: " + name );
                    }
                    else
                    {
                        character = String.valueOf((char)characterCode);
                        NAME_TO_CHARACTER.put(name, character);
                    }
                }
                catch (NumberFormatException nfe)
                {
                    Log.w( PDFBox.LOG_TAG, "Not a number in Unicode character name: " + name );
                    character = name;
                }
            }
            else if (nameToCode.containsKey(name))
            {
                int code = nameToCode.get(name);
                character = Character.toString((char)code);
            }
            else
            {
                character = name;
            }
        }
        return character;
    }
    
    /**
     * This will take a character code and get the name from the code.
     *
     * @param code The character code.
     *
     * @return The name of the character.
     *
     * @throws IOException If there is no name for the code.
     */
    public String getName( int code ) throws IOException
    {
        return codeToName.get( code );
    }
    
    /**
     * This will add a character encoding.
     *
     * @param code The character code that matches the character.
     * @param name The name of the character.
     */
    public void addCharacterEncoding( int code, String name )
    {
        codeToName.put( code, name );
        nameToCode.put( name, code );
    }
    
    /**
     * Returns an unmodifiable view of the Code2Name mapping.
     * @return the Code2Name map
     */
    public Map<Integer, String> getCodeToNameMap()
    {
        return Collections.unmodifiableMap(codeToName);
    }
}
