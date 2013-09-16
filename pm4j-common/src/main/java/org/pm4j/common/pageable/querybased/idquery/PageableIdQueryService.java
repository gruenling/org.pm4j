package org.pm4j.common.pageable.querybased.idquery;

import java.util.List;

import org.pm4j.common.pageable.ItemIdDao;
import org.pm4j.common.query.QueryOptions;
import org.pm4j.common.query.QueryParams;

/**
 * Interface for services that provide data for a {@link PageableIdQueryCollectionImpl}.
 *
 * @author OBOEDE
 *
 * @param <T_BEAN> Supported bean type.
 * @param <T_ID> The corresponding bean identifier type.
 */
public interface PageableIdQueryService<T_BEAN, T_ID> extends ItemIdDao<T_BEAN, T_ID> {

  List<T_ID> findIds(QueryParams query, long startIdx, int pageSize);

  List<T_BEAN> getItems(List<T_ID> ids);

  /**
   * Provides the number of items that match the given query filter criteria.
   *
   * @param query
   * @return
   */
  long getItemCount(QueryParams query);

  /**
   * Provides the set of filter definitions and attribute sort orders that can be
   * processed by this service.
   *
   * @return
   */
  QueryOptions getQueryOptions();

}
