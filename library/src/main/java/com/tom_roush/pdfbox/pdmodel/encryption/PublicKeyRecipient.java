package org.apache.pdfbox.pdmodel.encryption;

import java.security.cert.X509Certificate;

/**
 * Represents a recipient in the public key protection policy.
 *
 * @see PublicKeyProtectionPolicy
 *
 * @author Benoit Guillon
 */
public class PublicKeyRecipient
{
    private X509Certificate x509;

    private AccessPermission permission;

    /**
     * Returns the X509 certificate of the recipient.
     *
     * @return The X509 certificate
     */
    public X509Certificate getX509()
    {
        return x509;
    }

    /**
     * Set the X509 certificate of the recipient.
     *
     * @param aX509 The X509 certificate
     */
    public void setX509(X509Certificate aX509)
    {
        this.x509 = aX509;
    }

    /**
     * Returns the access permission granted to the recipient.
     *
     * @return The access permission object.
     */
    public AccessPermission getPermission()
    {
        return permission;
    }

    /**
     * Set the access permission granted to the recipient.
     *
     * @param permissions The permission to set.
     */
    public void setPermission(AccessPermission permissions)
    {
        this.permission = permissions;
    }
}
