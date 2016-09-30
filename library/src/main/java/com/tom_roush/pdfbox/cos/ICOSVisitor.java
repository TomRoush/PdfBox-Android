package com.tom_roush.pdfbox.cos;

import java.io.IOException;

/**
 * An interface for visiting a PDF document at the type (COS) level.
 *
 * @author Michael Traut
 */
public interface ICOSVisitor
{
    /**
     * Notification of visit to Array object.
     *
     * @param obj The Object that is being visited.
     * @return any Object depending on the visitor implementation, or null
     * @throws IOException If there is an error while visiting this object.
     */
    Object visitFromArray( COSArray obj ) throws IOException;

    /**
     * Notification of visit to boolean object.
     *
     * @param obj The Object that is being visited.
     * @return any Object depending on the visitor implementation, or null
     * @throws IOException If there is an error while visiting this object.
     */
    Object visitFromBoolean( COSBoolean obj ) throws IOException;

    /**
     * Notification of visit to dictionary object.
     *
     * @param obj The Object that is being visited.
     * @return any Object depending on the visitor implementation, or null
     * @throws IOException If there is an error while visiting this object.
     */
    Object visitFromDictionary( COSDictionary obj ) throws IOException;

    /**
     * Notification of visit to document object.
     *
     * @param obj The Object that is being visited.
     * @return any Object depending on the visitor implementation, or null
     * @throws IOException If there is an error while visiting this object.
     */
    Object visitFromDocument( COSDocument obj ) throws IOException;

    /**
     * Notification of visit to float object.
     *
     * @param obj The Object that is being visited.
     * @return any Object depending on the visitor implementation, or null
     * @throws IOException If there is an error while visiting this object.
     */
    Object visitFromFloat( COSFloat obj ) throws IOException;

    /**
     * Notification of visit to integer object.
     *
     * @param obj The Object that is being visited.
     * @return any Object depending on the visitor implementation, or null
     * @throws IOException If there is an error while visiting this object.
     */
    Object visitFromInt( COSInteger obj ) throws IOException;

    /**
     * Notification of visit to name object.
     *
     * @param obj The Object that is being visited.
     * @return any Object depending on the visitor implementation, or null
     * @throws IOException If there is an error while visiting this object.
     */
    Object visitFromName( COSName obj ) throws IOException;

    /**
     * Notification of visit to null object.
     *
     * @param obj The Object that is being visited.
     * @return any Object depending on the visitor implementation, or null
     * @throws IOException If there is an error while visiting this object.
     */
    Object visitFromNull( COSNull obj ) throws IOException;

    /**
     * Notification of visit to stream object.
     *
     * @param obj The Object that is being visited.
     * @return any Object depending on the visitor implementation, or null
     * @throws IOException If there is an error while visiting this object.
     */
    Object visitFromStream( COSStream obj ) throws IOException;

    /**
     * Notification of visit to string object.
     *
     * @param obj The Object that is being visited.
     * @return any Object depending on the visitor implementation, or null
     * @throws IOException If there is an error while visiting this object.
     */
    Object visitFromString( COSString obj ) throws IOException;
}
