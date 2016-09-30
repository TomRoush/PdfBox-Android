package com.tom_roush.pdfbox.filter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.tom_roush.pdfbox.cos.COSDictionary;
import com.tom_roush.pdfbox.cos.COSName;

/**
 * Decrypts data encrypted by a security handler, reproducing the data as it was before encryption.
 * @author Adam Nichols
 */
final class CryptFilter extends Filter
{
    @Override
    public DecodeResult decode(InputStream encoded, OutputStream decoded,
                                         COSDictionary parameters, int index) throws IOException
    {
        COSName encryptionName = (COSName) parameters.getDictionaryObject(COSName.NAME);
        if(encryptionName == null || encryptionName.equals(COSName.IDENTITY)) 
        {
            // currently the only supported implementation is the Identity crypt filter
            Filter identityFilter = new IdentityFilter();
            identityFilter.decode(encoded, decoded, parameters, index);
            return new DecodeResult(parameters);
        }
        throw new IOException("Unsupported crypt filter " + encryptionName.getName());
    }

    @Override
    protected void encode(InputStream input, OutputStream encoded, COSDictionary parameters)
            throws IOException
    {
        COSName encryptionName = (COSName) parameters.getDictionaryObject(COSName.NAME);
        if(encryptionName == null || encryptionName.equals(COSName.IDENTITY))
        {
            // currently the only supported implementation is the Identity crypt filter
            Filter identityFilter = new IdentityFilter();
            identityFilter.encode(input, encoded, parameters);
        }
        else
        {
            throw new IOException("Unsupported crypt filter " + encryptionName.getName());
        }
    }
}
