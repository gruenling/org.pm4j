package org.pm4j.common.expr;

import org.pm4j.common.expr.NameWithModifier.Modifier;
import org.pm4j.common.expr.parser.ParseCtxt;


/**
 * An expression that returns just the current value.
 *
 * @author olaf boede
 */
public class ThisExpr extends ExprBase<ExprExecCtxt> implements OptionalExpression {

  public static final String THIS_KEYWORD = "this";

  public ThisExpr(ParseCtxt ctxt) {
    super(ctxt);
  }

  @Override
  protected Object execImpl(ExprExecCtxt ctxt) {
    return ctxt.getCurrentValue();
  }

  @Override
  public boolean hasNameModifier(Modifier nameModifier) {
    return false;
  }

  /**
   * @param ctxt The information to parse.
   * @return The {@link ThisExpr} instance if the string 'this' was read.
   *         Otherwise <code>null</code>.
   */
  public static ThisExpr parse(ParseCtxt ctxt) {
    return ctxt.readOptionalString("this")
              ? new ThisExpr(ctxt)
              : null;
  }

}
