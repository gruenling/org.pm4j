package org.pm4j.deprecated.core.pm.pageable;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.pm4j.core.pm.PmBean;
import org.pm4j.core.pm.PmCommandDecorator;
import org.pm4j.core.pm.PmEvent;
import org.pm4j.core.pm.api.PmEventApi;
import org.pm4j.core.pm.api.PmFactoryApi;
import org.pm4j.core.pm.impl.pageable.PmBeanCollection;
import org.pm4j.deprecated.core.pm.DeprPmTable;
import org.pm4j.deprecated.core.pm.DeprPmTable.TableChange;
import org.pm4j.deprecated.core.pm.filter.DeprFilter;

/**
 * A {@link PageableItems} instance that provides {@link PmBean} instances in
 * front of a {@link PageableItems} container that handles the corresponding
 * bean instances.
 *
 * @author olaf boede
 *
 * @param <T_PM>
 *          The kind of {@link PmBean} provided by this class.
 * @param <T_BEAN>
 *          The kind of corresponding bean, handled by the backing
 *          {@link DeprPageableCollection} instance.
 *
 * @deprecated please use {@link PmBeanCollection}
 */
// TODO olaf: control/ensure that the PM factory releases the PMs for the beans that are no longer on the current page.
@Deprecated
public class DeprPageablePmsForBeans<T_PM extends PmBean<T_BEAN>, T_BEAN> implements DeprPageableCollection<T_PM> {

  private DeprPmTable<T_PM>             pmCtxt;
  private DeprPageableCollection<T_BEAN> beans;

  /**
   * Creates a collection backed by the given {@link DeprPageableCollection} of beans.
   *
   * @param pmCtxt
   *          The PM context used to create the PM's for the bean items.
   * @param beanItems
   *          The collection of beans to represent by this collection of bean-PM's.
   */
  public DeprPageablePmsForBeans(DeprPmTable<T_PM> pmCtxt, DeprPageableCollection<T_BEAN> beanItems) {
    this.pmCtxt = pmCtxt;
    this.beans = beanItems;
  }

  /**
   * Creates a collection backed by a {@link DeprPageableListImpl}.
   *
   * @param pmCtxt
   *          The PM context used to create the PM's for the bean items.
   * @param beans
   *          The set of beans to handle.
   * @param initialSortComparator
   *          A comparator that defines the initial sort order. May be <code>null</code>.
   */
  public DeprPageablePmsForBeans(DeprPmTable<T_PM> pmCtxt, Collection<T_BEAN> beans, Comparator<T_BEAN> initialSortComparator) {
      this(pmCtxt, new DeprPageableListImpl<T_BEAN>(beans, initialSortComparator));
  }

  /**
   * Creates a collection backed by a {@link DeprPageableListImpl} with no initial
   * sort order definition.
   *
   * @param pmCtxt
   *          The PM context used to create the PM's for the bean items.
   * @param beans
   *          The set of beans to handle.
   */
  public DeprPageablePmsForBeans(DeprPmTable<T_PM> pmCtxt, Collection<T_BEAN> beans) {
    this(pmCtxt, beans, null);
}

  @SuppressWarnings("unchecked")
  @Override
  public List<T_PM> getItemsOnPage() {
    return (List<T_PM>) PmFactoryApi.getPmListForBeans(pmCtxt, beans.getItemsOnPage(), false);
  }

  @Override
  public int getPageSize() {
    return beans.getPageSize();
  }

  @Override
  public void setPageSize(int newSize) {
    beans.setPageSize(newSize);
  }

  @Override
  public int getCurrentPageIdx() {
    return beans.getCurrentPageIdx();
  }

  @Override
  public void setCurrentPageIdx(int pageIdx) {
    beans.setCurrentPageIdx(pageIdx);
  }

  @SuppressWarnings("unchecked")
  @Override
  public void sortItems(Comparator<?> sortComparator) {
    beans.sortItems(sortComparator != null
        ? new BeanComparatorBasedOnPmComparator<T_PM, T_BEAN>(
              (Comparator<T_PM>)sortComparator)
        : null);
  }

  @Override
  public void setInitialBeanSortComparator(Comparator<?> comparator) {
    beans.setInitialBeanSortComparator(comparator);
  }

  @Override
  public void setItemFilter(DeprFilter filter) {
    beans.setItemFilter(filter != null
        ? new BeanFilterBasedOnPmFilter(filter)
        : null);
  }

  @Override
  public int getNumOfItems() {
    return beans.getNumOfItems();
  }

  @Override
  public int getNumOfUnfilteredItems() {
    return beans.getNumOfUnfilteredItems();
  }

  @Override
  public Iterator<T_PM> getAllItemsIterator() {
    final Iterator<T_BEAN> beanIter = beans.getAllItemsIterator();
    return new Iterator<T_PM>() {
      @Override
      public boolean hasNext() {
        return beanIter.hasNext();
      }
      @Override
      public T_PM next() {
        T_BEAN b = beanIter.next();
        return b != null
            ? (T_PM) PmFactoryApi.<T_BEAN, T_PM>getPmForBean(pmCtxt, b)
            : null;
      }
      @Override
      public void remove() {
        throw new UnsupportedOperationException();
      }
    };
  }

  @Override
  public boolean isSelected(T_PM item) {
    return item != null
            ? beans.isSelected(item.getPmBean())
            : null;
  }

  @Override
  public void select(T_PM item, boolean doSelect) {
    // do nothing if the selection status will not be changed.
    if (doSelect == isSelected(item)) {
      return;
    }

    // FIXME olaf: report the automatic de-selection in case of single-select.
    //  Idea: create a kind of selection event that contains info about the old and the new
    //        selection state.
    //        This could also be used to prevent a flood of selection change events for cases
    //        like 'selectAll'...

    T_BEAN b = item != null ? item.getPmBean() : null;
    Collection<PmCommandDecorator> decorators = pmCtxt.getDecorators(TableChange.SELECTION);
    boolean beforeSuccess = true;
    for (PmCommandDecorator pmCommandDecorator : decorators) {
      // ignore beforeDo outcome
      beforeSuccess = beforeSuccess && pmCommandDecorator.beforeDo(null);
    }
    if (beforeSuccess) {
      beans.select(b, doSelect);
      for (PmCommandDecorator pmCommandDecorator : decorators) {
        pmCommandDecorator.afterDo(null);
      }
      // fire the event after successful select
      PmEventApi.firePmEventIfInitialized(pmCtxt, PmEvent.SELECTION_CHANGE);
    }
  }

  @Override
  public boolean isMultiSelect() {
    return beans.isMultiSelect();
  }

  @Override
  public void setMultiSelect(boolean isMultiSelect) {
    beans.setMultiSelect(isMultiSelect);
  }

  @SuppressWarnings("unchecked")
  @Override
  public Collection<T_PM> getSelectedItems() {
    return (Collection<T_PM>) PmFactoryApi.getPmListForBeans(pmCtxt, beans.getSelectedItems(), false);
  }

  @Override
  public void onUpdateCollection() {
    beans.onUpdateCollection();
  }

  public DeprPageableCollection<T_BEAN> getBeans() {
    return beans;
  }

  public void setBeans(DeprPageableCollection<T_BEAN> pageableBeanCollection) {
    this.beans = pageableBeanCollection;
  }

  /**
   * A comparator that can compare backing beans based on another comparator
   * that compares the corresponding PMs.
   *
   * @param <T_ITEM_PM>
   * @param <T_ITEM>
   */
  class BeanComparatorBasedOnPmComparator<T_ITEM_PM extends PmBean<T_ITEM>, T_ITEM> implements Comparator<T_ITEM> {

    private final Comparator<T_ITEM_PM> pmComparator;

    public BeanComparatorBasedOnPmComparator(Comparator<T_ITEM_PM> pmComparator) {
      this.pmComparator = pmComparator;
    }

    @Override
    public int compare(T_ITEM o1, T_ITEM o2) {
      T_ITEM_PM pm1 = PmFactoryApi.<T_ITEM, T_ITEM_PM>getPmForBean(pmCtxt, o1);
      T_ITEM_PM pm2 = PmFactoryApi.<T_ITEM, T_ITEM_PM>getPmForBean(pmCtxt, o2);
      return pmComparator.compare(pm1, pm2);
    }
  }

  // TODO olaf: this reverse reference construction should skipped asap.
  /**
   * A filter that can compare backing beans based on another filter
   * that compares the corresponding PMs.
   */
  class BeanFilterBasedOnPmFilter implements DeprFilter {

    private final DeprFilter pmFilter;

    public BeanFilterBasedOnPmFilter(DeprFilter pmFilter) {
      this.pmFilter = pmFilter;
    }

    public boolean doesItemMatch(Object item) {
      PmBean<Object> itemPm = PmFactoryApi.<Object, PmBean<Object>>getPmForBean(pmCtxt, item);
      return pmFilter.doesItemMatch(itemPm);
    }

    @Override
    public boolean isBeanFilter() {
      return true;
    }

  }

}
