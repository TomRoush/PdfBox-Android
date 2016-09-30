package org.apache.pdfbox.pdmodel.common.function.type4;

import java.util.Stack;

/**
 * Makes up the execution context, holding the available operators and the execution stack.
 */
public class ExecutionContext
{

    private Operators operators;
    private Stack<Object> stack = new Stack<Object>();

    /**
     * Creates a new execution context.
     * @param operatorSet the operator set
     */
    public ExecutionContext(Operators operatorSet)
    {
        this.operators = operatorSet;
    }

    /**
     * Returns the stack used by this execution context.
     * @return the stack
     */
    public Stack<Object> getStack()
    {
        return this.stack;
    }

    /**
     * Returns the operator set used by this execution context.
     * @return the operator set
     */
    public Operators getOperators()
    {
        return this.operators;
    }

    /**
     * Pops a number (int or real) from the stack. If it's neither data type, a
     * ClassCastException is thrown.
     * @return the number
     */
    public Number popNumber()
    {
        return (Number)stack.pop();
    }

    /**
     * Pops a value of type int from the stack. If the value is not of type int, a
     * ClassCastException is thrown.
     * @return the int value
     */
    public int popInt()
    {
        return ((Integer)stack.pop());
    }

    /**
     * Pops a number from the stack and returns it as a real value. If the value is not of a
     * numeric type, a ClassCastException is thrown.
     * @return the real value
     */
    public float popReal()
    {
        return ((Number)stack.pop()).floatValue();
    }

}
