package org.pm4j.core.pm.filter;

import org.pm4j.common.query.QueryParams;
import org.pm4j.core.pm.PmTable2;

/**
 * Interface for classes that may use dynamically defined filters.<br>
 * E.g. for tables or lists with filters that may be specified by the user.
 *
 * @author olaf boede.
 *
 * @deprecated see {@link PmTable2} and {@link QueryParams}.
 */
@Deprecated
public interface DeprFilterable {

  /**
   * Activates the given filter.<br>
   * Replaces the filter that was formerly defined for the given id.<br>
   * Removes the filter if <code>null</code> will be passed as
   * <code>filter</code> parameter.
   *
   * @param filterId
   *          The filter identifier.
   * @param filter
   *          The filter to apply.<br>
   *          <code>null</code> removes the filter definition.
   * @return <code>true</code> indicates that the filter was really changed to
   *         the provided definition.<br>
   *         <code>false</code> may occur if a filter change decorator prevents
   *         the change.
   */
  boolean setFilter(String filterId, DeprFilter filter);

  /**
   * Retrieves the current filter is registered for the given identifier.
   *
   * @param filterId The filter identifier.
   * @return The found filter. Is <code>null</code> if no filter is defined for the given identifier.
   */
  DeprFilter getFilter(String filterId);

}
