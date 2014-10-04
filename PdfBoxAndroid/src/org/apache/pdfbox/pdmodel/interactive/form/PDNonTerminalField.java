package org.apache.pdfbox.pdmodel.interactive.form;

import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSInteger;
import org.apache.pdfbox.cos.COSName;

/**
 * A non terminal field in an interactive form.
 *
 * @author Andreas Lehmk�hler
 */
public class PDNonTerminalField extends PDFieldTreeNode
{
    /**
     * Constructor.
     * 
     * @param theAcroForm The form that this field is part of.
     */
    public PDNonTerminalField(PDAcroForm theAcroForm)
    {
        super(theAcroForm);
    }

    /**
     * Constructor.
     * 
     * @param theAcroForm The form that this field is part of.
     * @param field the PDF object to represent as a field.
     * @param parentNode the parent node of the node to be created
     */
    public PDNonTerminalField(PDAcroForm theAcroForm, COSDictionary field, PDFieldTreeNode parentNode)
    {
        super(theAcroForm, field, parentNode);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getFieldFlags()
    {
        int retval = 0;
        COSInteger ff = (COSInteger) getDictionary().getDictionaryObject(COSName.FF);
        if (ff != null)
        {
            retval = ff.intValue();
        }
        // There is no need to look up the parent hierarchy within a non terminal field
        return retval;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getFieldType()
    {
        // There is no need to look up the parent hierarchy within a non terminal field
        return getDictionary().getNameAsString(COSName.FT);
    }

    @Override
    public Object getValue()
    {
        // Nonterminal fields don't support the "V" entry.
        return null;
    }
    
    @Override
    public void setValue(Object value)
    {
        // Nonterminal fields don't support the "V" entry.
        throw new RuntimeException( "Nonterminal fields don't support the \"V\" entry." );
    }
    
    @Override
    public Object getDefaultValue()
    {
        // Nonterminal fields don't support the "DV" entry.
        return null;
    }
    
    @Override
    public void setDefaultValue(Object value)
    {
        // Nonterminal fields don't support the "DV" entry.
        throw new RuntimeException( "Nonterminal fields don't support the \"DV\" entry." );
    }
    
}
