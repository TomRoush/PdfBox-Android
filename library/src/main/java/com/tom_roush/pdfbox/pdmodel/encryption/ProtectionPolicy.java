package com.tom_roush.pdfbox.pdmodel.encryption;

/**
 * This class represents the protection policy to apply to a document.
 *
 * Objects implementing this abstract class can be passed to the protect method of PDDocument
 * to protect a document.
 *
 * @see com.tom_roush.pdfbox.pdmodel.PDDocument#protect(ProtectionPolicy)
 *
 * @author Benoit Guillon
 */
public abstract class ProtectionPolicy
{

    private static final int DEFAULT_KEY_LENGTH = 40;

    private int encryptionKeyLength = DEFAULT_KEY_LENGTH;

    /**
     * set the length in (bits) of the secret key that will be
     * used to encrypt document data.
     * The default value is 40 bits, which provides a low security level
     * but is compatible with old versions of Acrobat Reader.
     *
     * @param l the length in bits (must be 40, 128 or 256)
     */
    public void setEncryptionKeyLength(int l)
    {
        if(l!=40 && l!=128 && l!=256)
        {
            throw new IllegalArgumentException("Invalid key length '" + l + "' value must be 40, 128 or 256!");
        }
        encryptionKeyLength = l;
    }

    /**
     * Get the length of the secrete key that will be used to encrypt
     * document data.
     *
     * @return The length (in bits) of the encryption key.
     */
    public int getEncryptionKeyLength()
    {
        return encryptionKeyLength;
    }
}
