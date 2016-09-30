package com.tom_roush.pdfbox.pdmodel.common.function.type4;

/**
 * Interface for PostScript operators.
 */
public interface Operator
{

    /**
     * Executes the operator. The method can inspect and manipulate the stack.
     * @param context the execution context
     */
    void execute(ExecutionContext context);

}
