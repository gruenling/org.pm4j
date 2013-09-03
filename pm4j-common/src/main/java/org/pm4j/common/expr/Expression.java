package org.pm4j.common.expr;


/**
 * Interface for an algorithm that evaluates an expression.
 *
 * @author olaf boede
 */
public interface Expression {

  public enum SyntaxVersion {
    /**
     * The (o) modifier has the same meaning than the (x) modifier.
     * Named objects can be accessed without '#' sign.
     */
    VERSION_1,

    /**
     * The (o) and (x) modifier have different meanings.
     * The # hash prefix is mandatory for named objects.
     */
    VERSION_2
  };

  /**
   * Executes the expression and returns the evaluated result value.
   *
   * @param ctxt the expression execution context.
   * @return the evaluation result.
   */
  Object exec(ExprExecCtxt ctxt);

  /**
   * Value assignment operation. Is supported by path expressions that reference
   * attributes that provide public modification access (setter or public
   * non-final declaration).
   *
   * @param ctxt
   *          Run time context data for the expression evaluation.
   * @param value
   *          The value to set to the field, addressed by the path.
   */
  void execAssign(ExprExecCtxt ctxt, Object value);

}
