package org.pm4j.common.pageable.inmem;

import java.util.Collection;

import org.pm4j.common.pageable.PageableCollection;

/**
 * In memory specific pageable collection extension.<br>
 * Allows to use a compare operator.
 *
 * @param <T_ITEM> type of handled collection items.
 *
 * @author olaf boede
 */
public interface InMemCollection<T_ITEM> extends PageableCollection<T_ITEM> {

  /**
   * Provides the backing collection this pageable instance works on.
   *
   * @return the collection. An immutable empty collection if there is no backing collection.
   */
  Collection<T_ITEM> getBackingCollection();

}
