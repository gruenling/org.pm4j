package org.pm4j.core.pm.impl.changehandler;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;

import org.pm4j.common.modifications.ModificationHandler;
import org.pm4j.common.pageable.PageableCollection;
import org.pm4j.common.selection.Selection;
import org.pm4j.common.util.beanproperty.PropertyAndVetoableChangeListener;
import org.pm4j.core.pm.impl.PmTableImpl;

/**
 * A master-details handler that uses a {@link PmTableImpl} as master.
 *
 * @param <T_MASTER_BEAN>
 *          the type of master beans behind the master table rows.
 *
 * @author olaf boede
 */
public class MasterPmTable2HandlerImpl<T_MASTER_BEAN> extends MasterPmSelectionHandlerImpl<T_MASTER_BEAN> {

  /**
   * @param masterPm
   *          the table of master records.
   * @param detailsHandlers
   *          the dependent detail area handlers.
   */
  public MasterPmTable2HandlerImpl(PmTableImpl<?, T_MASTER_BEAN> masterPm, DetailsPmHandler[] detailsHandlers) {
    super(masterPm, masterPm.getPmPageableBeanCollection().getSelectionHandler(), detailsHandlers);
  }

  @Override
  protected ModificationHandler<T_MASTER_BEAN> getModificationHandler() {
    return getMasterTablePm().getPmPageableBeanCollection().getModificationHandler();
  }


  @Override
  public void startObservers() {
    super.startObservers();
    getMasterTablePm().getPmPageableBeanCollection().addPropertyAndVetoableListener(
        PageableCollection.EVENT_REMOVE_SELECTION, new MasterRecordRemoveListener());
  }

  /**
   * Provides the master row bean of the table.<br>
   * In some scenarios the master may be different from the 'selected' row.
   * E.g. in case of a multi selection with an active master row.
   */
  @Override
  protected T_MASTER_BEAN getSelectedMasterBean() {
    return getMasterTablePm().getMasterRowPmBean();
  }

  /**
   * Type safe access method.
   *
   * @return The master table.
   */
  @SuppressWarnings("unchecked")
  protected PmTableImpl<?, T_MASTER_BEAN> getMasterTablePm() {
    return (PmTableImpl<?, T_MASTER_BEAN>) getMasterPm();
  }

  /**
   * A listener that informs the details in the event of a master record delete operation.
   * <p>
   * It remembers the selected master record before the delete operation gets executed
   * and informs the detail handlers after successful delete operation execution.<br>
   * This will only be done if the master record was really part of the deleted record selection.
   */
  public class MasterRecordRemoveListener implements PropertyAndVetoableChangeListener {

    private T_MASTER_BEAN masterBeanBeforeDeleteOperation;

    @Override
    public void vetoableChange(PropertyChangeEvent evt) throws PropertyVetoException {
      masterBeanBeforeDeleteOperation = getSelectedMasterBean();
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
      PmTableImpl<?, T_MASTER_BEAN> masterTablePm = getMasterTablePm();
      masterTablePm.clearMasterRowPm();
      @SuppressWarnings("unchecked")
      Selection<T_MASTER_BEAN> deletedBeansSelection = (Selection<T_MASTER_BEAN>) evt.getOldValue();
      if ((masterBeanBeforeDeleteOperation != null) &&
          deletedBeansSelection.contains(masterBeanBeforeDeleteOperation)) {
        T_MASTER_BEAN newMasterBean = masterTablePm.getMasterRowPmBean();
        for (DetailsPmHandler dh : getDetailsPmHandlers()) {
          dh.afterMasterRecordChange(newMasterBean);
        }
      }
    }

  }

}
