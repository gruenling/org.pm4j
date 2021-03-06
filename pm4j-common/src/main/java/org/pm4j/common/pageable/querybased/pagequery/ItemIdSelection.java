package org.pm4j.common.pageable.querybased.pagequery;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import org.pm4j.common.pageable.querybased.QueryService;
import org.pm4j.common.util.collection.ListUtil;

/**
 * A selection that holds the ID's of selected items.
 * <p>
 * It uses a {@link PageQueryService} instance to retrieve the selected instances from the service.
 */
public class ItemIdSelection<T_ITEM, T_ID> extends PageQuerySelectionHandler.QuerySelectionWithClickedIds<T_ITEM, T_ID> {
  private static final long serialVersionUID = 1L;

  private final Collection<T_ID> ids;

  /**
   * Creates a selection based on a set of selected id's.
   *
   * @param service the service used to retrieve items for the selected id's.
   * @param ids the set of selected id's.
   */
  @SuppressWarnings("unchecked")
  public ItemIdSelection(QueryService<T_ITEM, T_ID> service, Collection<T_ID> ids) {
    super(service);
    this.ids = (ids != null) ? Collections.unmodifiableCollection(ids) : Collections.EMPTY_LIST;
  }

  /**
   * Creates a selection based on another selection and some additional items.
   *
   * @param srcSelection the base selection.
   * @param ids the set of additional items.
   */
  public ItemIdSelection(ItemIdSelection<T_ITEM, T_ID> srcSelection, Collection<T_ID> ids) {
    super(srcSelection.getService());
    this.ids = ListUtil.collectionsToList(srcSelection.getClickedIds().getIds(), ids);
  }

  @Override
  public long getSize() {
    return ids.size();
  }

  @Override
  public boolean contains(T_ITEM item) {
    return ids.contains(getService().getIdForItem(item));
  }

  @Override
  public Iterator<T_ITEM> iterator() {
    return new ItemIterator();
  }

  /** Block size has currently no effect on this iterator implementation. This may be changed in the future. */
  @Override
  public void setIteratorBlockSizeHint(int readBlockSize) {
  }

  protected Collection<T_ID> getSelectedOrDeselectedIds() {
    return ids;
  }

  @Override
  public ClickedIds<T_ID> getClickedIds() {
    return new ClickedIds<T_ID>(ids, false);
  }

  class ItemIterator implements Iterator<T_ITEM> {
    private final Iterator<T_ID> idIterator = ids.iterator();

    @Override
    public boolean hasNext() {
      return idIterator.hasNext();
    }

    @Override
    public T_ITEM next() {
      T_ID id = idIterator.next();
      // TODO olaf: not yet optimized to read in blocks
      return getService().getItemForId(id);
    }

    @Override
    public void remove() {
      throw new UnsupportedOperationException();
    }
  }
}