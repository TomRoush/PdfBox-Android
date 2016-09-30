package com.tom_roush.pdfbox.pdmodel.encryption;

/**
*
* Represents the necessary information to decrypt a document protected by
* the standard security handler (password protection).
*
* This is only composed of a password.
*
* The following example shows how to decrypt a document protected with
* the standard security handler:
*
*  <pre>
*  PDDocument doc = PDDocument.load(in);
*  StandardDecryptionMaterial dm = new StandardDecryptionMaterial("password");
*  doc.openProtection(dm);
*  </pre>
*
* @author Benoit Guillon
*/

public class StandardDecryptionMaterial extends DecryptionMaterial
{

   private String password = null;

   /**
    * Create a new standard decryption material with the given password.
    *
    * @param pwd The password.
    */
   public StandardDecryptionMaterial(String pwd)
   {
       password = pwd;
   }

   /**
    * Returns the password.
    *
    * @return The password used to decrypt the document.
    */
   public String getPassword()
   {
       return password;
   }

}
