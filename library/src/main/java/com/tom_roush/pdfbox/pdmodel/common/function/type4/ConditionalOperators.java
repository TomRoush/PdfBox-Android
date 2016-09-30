package org.apache.pdfbox.pdmodel.common.function.type4;

import java.util.Stack;

/**
 * Provides the conditional operators such as "if" and "ifelse".
 */
class ConditionalOperators
{

    /** Implements the "if" operator. */
    static class If implements Operator
    {

        public void execute(ExecutionContext context)
        {
            Stack<Object> stack = context.getStack();
            InstructionSequence proc = (InstructionSequence)stack.pop();
            Boolean condition = (Boolean)stack.pop();
            if (condition)
            {
                proc.execute(context);
            }
        }

    }

    /** Implements the "ifelse" operator. */
    static class IfElse implements Operator
    {

        public void execute(ExecutionContext context)
        {
            Stack<Object> stack = context.getStack();
            InstructionSequence proc2 = (InstructionSequence)stack.pop();
            InstructionSequence proc1 = (InstructionSequence)stack.pop();
            Boolean condition = (Boolean)stack.pop();
            if (condition)
            {
                proc1.execute(context);
            }
            else
            {
                proc2.execute(context);
            }
        }

    }

}
