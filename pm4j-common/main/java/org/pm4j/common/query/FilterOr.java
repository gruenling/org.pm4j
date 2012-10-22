package org.pm4j.common.query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;


public class FilterOr implements FilterExpression {

  private List<FilterExpression> expressions;

  public FilterOr(FilterExpression... expressions) {
    this(Arrays.asList(expressions));
  }

  public FilterOr(Collection<? extends FilterExpression> expressions) {
    this.expressions = new ArrayList<FilterExpression>(expressions);
  }

  public void add(FilterExpression expression) {
    this.expressions.add(expression);
  }

  public List<FilterExpression> getExpressions() {
    return expressions;
  }
}
