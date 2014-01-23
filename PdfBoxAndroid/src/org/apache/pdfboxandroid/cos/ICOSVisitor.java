package org.apache.pdfboxandroid.cos;

import org.apache.pdfboxandroid.exceptions.COSVisitorException;

/**
 * An interface for visiting a PDF document at the type (COS) level.
 *
 * @author Michael Traut
 * @version $Revision: 1.6 $
 */
public interface ICOSVisitor {
	/**
     * Notification of visit to Array object.
     *
     * @param obj The Object that is being visited.
     * @return any Object depending on the visitor implementation, or null
     * @throws COSVisitorException If there is an error while visiting this object.
     */
    public Object visitFromArray( COSArray obj ) throws COSVisitorException;

    /**
     * Notification of visit to boolean object.
     *
     * @param obj The Object that is being visited.
     * @return any Object depending on the visitor implementation, or null
     * @throws COSVisitorException If there is an error while visiting this object.
     */
    public Object visitFromBoolean( COSBoolean obj ) throws COSVisitorException;

    /**
     * Notification of visit to dictionary object.
     *
     * @param obj The Object that is being visited.
     * @return any Object depending on the visitor implementation, or null
     * @throws COSVisitorException If there is an error while visiting this object.
     */
    public Object visitFromDictionary( COSDictionary obj ) throws COSVisitorException;

    /**
     * Notification of visit to document object.
     *
     * @param obj The Object that is being visited.
     * @return any Object depending on the visitor implementation, or null
     * @throws COSVisitorException If there is an error while visiting this object.
     */
    public Object visitFromDocument( COSDocument obj ) throws COSVisitorException;

    /**
     * Notification of visit to float object.
     *
     * @param obj The Object that is being visited.
     * @return any Object depending on the visitor implementation, or null
     * @throws COSVisitorException If there is an error while visiting this object.
     */
    public Object visitFromFloat( COSFloat obj ) throws COSVisitorException;

    /**
     * Notification of visit to integer object.
     *
     * @param obj The Object that is being visited.
     * @return any Object depending on the visitor implementation, or null
     * @throws COSVisitorException If there is an error while visiting this object.
     */
    public Object visitFromInt( COSInteger obj ) throws COSVisitorException;

    /**
     * Notification of visit to name object.
     *
     * @param obj The Object that is being visited.
     * @return any Object depending on the visitor implementation, or null
     * @throws COSVisitorException If there is an error while visiting this object.
     */
    public Object visitFromName( COSName obj ) throws COSVisitorException;

    /**
     * Notification of visit to null object.
     *
     * @param obj The Object that is being visited.
     * @return any Object depending on the visitor implementation, or null
     * @throws COSVisitorException If there is an error while visiting this object.
     */
    public Object visitFromNull( COSNull obj ) throws COSVisitorException;

    /**
     * Notification of visit to stream object.
     *
     * @param obj The Object that is being visited.
     * @return any Object depending on the visitor implementation, or null
     * @throws COSVisitorException If there is an error while visiting this object.
     */
    public Object visitFromStream( COSStream obj ) throws COSVisitorException;

    /**
     * Notification of visit to string object.
     *
     * @param obj The Object that is being visited.
     * @return any Object depending on the visitor implementation, or null
     * @throws COSVisitorException If there is an error while visiting this object.
     */
    public Object visitFromString( COSString obj ) throws COSVisitorException;
}
