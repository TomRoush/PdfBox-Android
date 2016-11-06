package com.tom_roush.pdfbox.cos;

import junit.framework.TestCase;

import java.io.IOException;

/**
 * Test class for {@link COSBase}.
 */
public abstract class TestCOSBase extends TestCase
{
	/** The COSBase abstraction of the object being tested. */
	protected COSBase testCOSBase;

	/**
	 * Tests getCOSObject() - tests that the underlying object is returned.
	 */
	public void testGetCOSObject()
	{
		assertEquals(testCOSBase, testCOSBase.getCOSObject());
	}

	/**
	 * Test accept() - tests the interface for visiting a document at the COS level.
	 */
	public abstract void testAccept() throws IOException;

	/**
	 * Tests isDirect() and setDirect() - tests the getter/setter methods.
	 */
	public void testIsSetDirect()
	{
		testCOSBase.setDirect(true);
		assertTrue(testCOSBase.isDirect());
		testCOSBase.setDirect(false);
		assertFalse(testCOSBase.isDirect());
	}

	/**
	 * A simple utility function to compare two byte arrays.
	 * @param byteArr1 the expected byte array
	 * @param byteArr2 the byte array being compared
	 */
	protected void testByteArrays(byte[] byteArr1, byte[] byteArr2)
	{
		assertEquals(byteArr1.length, byteArr1.length);
		for (int i = 0; i < byteArr1.length; i++)
		{
			assertEquals(byteArr1[i], byteArr2[i]);
		}
	}
}