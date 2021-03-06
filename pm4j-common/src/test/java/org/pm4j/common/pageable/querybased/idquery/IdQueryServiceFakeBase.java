package org.pm4j.common.pageable.querybased.idquery;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.pm4j.common.pageable.querybased.QueryServiceFakeBase;
import org.pm4j.common.query.QueryParams;

/**
 * A pageable ID service fake that works in memory.
 *
 * @param <T_ITEM>
 * @param <T_ID>
 *
 * @author olaf boede
 */
public abstract class IdQueryServiceFakeBase<T_ITEM, T_ID> extends QueryServiceFakeBase<T_ITEM, T_ID> implements IdQueryService<T_ITEM, T_ID> {

  public static final String METHOD_FIND_IDS = "findIds";
  public static final String METHOD_GET_ITEMS = "getItems";
  public static final String METHOD_GET_ITEM_COUNT = "getItemCount";


  @Override
  public List<T_ID> findIds(QueryParams query, long startIdx, int pageSize) {
    callCounter.incCallCount(METHOD_FIND_IDS);
    if (startIdx >= idToBeanMap.size()) {
      return Collections.emptyList();
    }

    List<T_ITEM> allQueryResultItems = getQueryResult(query);
    int endIdx = Math.min((int) startIdx + pageSize, allQueryResultItems.size());

    List<T_ITEM> beanList = allQueryResultItems.subList((int) startIdx, endIdx);
    List<T_ID> idList = new ArrayList<T_ID>();
    for (T_ITEM b : beanList) {
      idList.add(getIdForItem(b));
    }
    return idList;
  }

  @Override
  public List<T_ITEM> getItems(List<T_ID> ids) {
    callCounter.incCallCount(METHOD_GET_ITEMS);
    List<T_ITEM> beans = new ArrayList<T_ITEM>(ids.size());
    for (T_ID id : ids) {
      beans.add(idToBeanMap.get(id));
    }
    return beans;
  }

  @Override
  public long getItemCount(QueryParams query) {
    callCounter.incCallCount(METHOD_GET_ITEM_COUNT);
    return getQueryResult(query).size();
  }

}
