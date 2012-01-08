package org.pm4j.core.pm.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pm4j.core.exception.PmRuntimeException;
import org.pm4j.core.pm.PmElement;
import org.pm4j.core.pm.PmObject;
import org.pm4j.core.pm.PmTable;
import org.pm4j.core.pm.PmTableCol;
import org.pm4j.core.pm.PmTableGenericRow;

/**
 * A table row implementation that is specific for {@link DeprecatedPmTableOfPmElementsImpl}.
 * <p>
 * The table cells are provided as follows:<br>
 * A row represents a {@link PmElement}.<br>
 * For each column cell, the element will be asked for a child PM that has the
 * same name as the table column.
 *
 * @author olaf boede
 */
public class PmTableRowCellsImpl<T_ROW_ELEMENT extends PmElement> implements PmTableGenericRow<T_ROW_ELEMENT> {

  @SuppressWarnings("unused")
  private static final Log LOG = LogFactory.getLog(PmTableRowCellsImpl.class);

  private final PmTable<T_ROW_ELEMENT> pmTable;
  private final T_ROW_ELEMENT pmTableRow;
  private List<PmObject> items;

  public PmTableRowCellsImpl(PmTable<T_ROW_ELEMENT> pmTable, T_ROW_ELEMENT pmTableRow) {
    assert pmTable != null;
    assert pmTableRow != null;

    this.pmTable = pmTable;
    this.pmTableRow = pmTableRow;

    // A table may present 'adopted' PMs of lists that already part of the PM hierarchy.
    if (pmTableRow.getPmParent() == null) {
      pmTableRow.setPmParent(pmTable);
    }
  }

  @Override
  public PmTable<T_ROW_ELEMENT> getPmTable() {
    return pmTable;
  }

  @Override
  public List<PmObject> getCells() {
    if (items == null) {
      List<PmObject> items = new ArrayList<PmObject>();
      for (PmTableCol c : pmTable.getColumns()) {
        String colName = c.getPmName();
        PmObject pm = PmUtil.findChildPm(pmTableRow, colName);
        if (pm != null) {
          items.add(pm);
        }
        else {
          // add a label that is only referenced by the list of items.
          // this way it is only bound to the life time of this row.
          items.add(new PmLabelImpl(pmTable));
        }
      }
      this.items = items;
    }

    return items;
  }

  @Override
  public PmObject getCell(int colIdx) {
    List<PmObject> cells = getCells();

    if (cells.size() <= colIdx) {
      throw new PmRuntimeException(pmTable, "Invalid column index '" + colIdx
          + "'. Only " + cells.size() + " columns are configured.");
    }

    return cells.get(colIdx);
  }

  @Override
  public T_ROW_ELEMENT getBackingBean() {
    return pmTableRow;
  }

}
