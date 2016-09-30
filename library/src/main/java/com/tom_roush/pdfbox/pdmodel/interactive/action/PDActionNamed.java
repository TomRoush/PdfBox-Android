package com.tom_roush.pdfbox.pdmodel.interactive.action;

import com.tom_roush.pdfbox.cos.COSDictionary;

/**
 * This represents a named action in a PDF document.
 */
public class PDActionNamed extends PDAction
{
	/**
	 * This type of action this object represents.
	 */
	public static final String SUB_TYPE = "Named";

	/**
	 * Default constructor.
	 */
	public PDActionNamed()
	{
		action = new COSDictionary();
		setSubType(SUB_TYPE);
	}

	/**
	 * Constructor.
	 *
	 * @param a The action dictionary.
	 */
	public PDActionNamed(COSDictionary a)
	{
		super(a);
	}

	/**
	 * This will get the name of the action to be performed.
	 *
	 * @return The name of the action to be performed.
	 */
	public String getN()
	{
		return action.getNameAsString("N");
	}

	/**
	 * This will set the name of the action to be performed.
	 *
	 * @param name The name of the action to be performed.
	 */
	public void setN(String name)
	{
		action.setName("N", name);
	}
}
