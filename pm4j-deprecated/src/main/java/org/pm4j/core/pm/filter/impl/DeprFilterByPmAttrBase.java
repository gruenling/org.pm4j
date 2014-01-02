package org.pm4j.core.pm.filter.impl;

import org.pm4j.core.exception.PmRuntimeException;
import org.pm4j.core.pm.PmAttr;
import org.pm4j.core.pm.PmObject;
import org.pm4j.core.pm.filter.DeprCompOp;
import org.pm4j.core.pm.impl.PmUtil;

/**
 * Base implementation for classes that filter by the values of a item-PM attributes.
 * <p>
 * If you want to filter by the beans behind some item-PMs, consider to use {@link DeprFilterByDefinitionBase}
 * as your base class.
 *
 * @author olaf boede.
 *
 * @param <T_FILTER_VALUE>
 */
@Deprecated
public abstract class DeprFilterByPmAttrBase<T_FILTER_VALUE> extends DeprFilterByDefinitionBase<PmObject, T_FILTER_VALUE> {

  public DeprFilterByPmAttrBase(PmObject pmCtxt, Class<?>... compOpClasses) {
    super(pmCtxt, compOpClasses);
  }

  /**
   * The type save match implementation version to be implemented.
   *
   * @param item
   * @param compOp
   * @param filterByValue
   * @return
   */
  protected abstract boolean doesItemMatchImpl(PmAttr<?> pmAttr, DeprCompOp compOp, T_FILTER_VALUE filterByValue);

  @SuppressWarnings("unchecked")
  @Override
  public boolean doesItemMatch(Object item, DeprCompOp compOp, Object filterValue) {
    // if there is no comp, the filter usually does not have any effect.
    if (compOp == null) {
      return true;
    }

    PmObject itemPm = (PmObject)item;
    PmAttr<?> a = findFilterValueAttr(itemPm);

    if (a == null) {
      throw new PmRuntimeException(itemPm, "Missing filter attribute '" + getName() + "'."
          + "\n If you want to use the default convention: Please check if the correponding PMs (column and row) have the same name."
          + "\n Alternatively you may override 'FilterByDefintionBase.findFilterValueAttr()' or 'FilterByDefintionBase.doesItemMatch()'.");
    }

    return doesItemMatchImpl(a, (DeprCompOp)compOp, (T_FILTER_VALUE)filterValue);
  }

  /**
   * Finds the corresponding value attribute within the given item PM.<br>
   * In a usual table column scenario this filter would search for a row-cell that has
   * the same name as the column this filter is defined for.
   * <p>
   * The default implementation looks here for an attribute that has the same name as
   * this filter.
   *
   * @param item The PM to find the value attribute in.
   * @return The found attribute or <code>null</code>.
   */
  protected PmAttr<?> findFilterValueAttr(PmObject item) {
    return PmUtil.findPmChildOfType(item, getName(), PmAttr.class);
  }

  protected final boolean doesItemMatchImpl(PmObject item, DeprCompOp compOp, T_FILTER_VALUE filterValue) {
    throw new RuntimeException("Only the attribute parameter version of this method should be called. Please consider another filter-by base class if you want to use this method.");
  }


}
