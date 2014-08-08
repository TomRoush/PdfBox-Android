package org.apache.pdfbox.filter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;

/**
*
* @author adam.nichols
*/
public class CryptFilter implements Filter
{
   /**
    * {@inheritDoc}
    */
   public void decode( InputStream compressedData, OutputStream result, COSDictionary options, int filterIndex ) 
       throws IOException
   {
       COSName encryptionName = (COSName)options.getDictionaryObject(COSName.NAME);
       if(encryptionName == null || encryptionName.equals(COSName.IDENTITY)) 
       {
           // currently the only supported implementation is the Identity crypt filter
           Filter identityFilter = new IdentityFilter();
           identityFilter.decode(compressedData, result, options, filterIndex);
       }
       else 
       {
           throw new IOException("Unsupported crypt filter "+encryptionName.getName());
       }
   }
   
   /**
    * {@inheritDoc}
    */
   public void encode( InputStream rawData, OutputStream result, COSDictionary options, int filterIndex ) 
       throws IOException
   {
       COSName encryptionName = (COSName)options.getDictionaryObject(COSName.NAME);
       if(encryptionName == null || encryptionName.equals(COSName.IDENTITY))
       {
           // currently the only supported implementation is the Identity crypt filter
           Filter identityFilter = new IdentityFilter();
           identityFilter.encode(rawData, result, options, filterIndex);
       }
       else
       {
           throw new IOException("Unsupported crypt filter "+encryptionName.getName());
       }
   }
}
