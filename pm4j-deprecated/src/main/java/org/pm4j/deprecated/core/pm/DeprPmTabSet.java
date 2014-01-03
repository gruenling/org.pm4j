package org.pm4j.deprecated.core.pm;

import java.util.List;

import org.pm4j.core.pm.PmElement;
import org.pm4j.core.pm.PmTabSet;
import org.pm4j.deprecated.core.pm.impl.DeprPmTabSetImpl;


/**
 * A PM for a multi tab set.
 * <p>
 * The child {@link PmElement}s are of a {@link DeprPmTabSet} can be used to represent the content of
 * tabs.
 * <p>
 * Child elements act as tab, simply if they are used in a tab view. See the documentation
 * of the related views for more view technology related usage hints.
 * <p>
 * ATTENTION: Current JSF limitation:<br>
 * The id (name) of the tab set should be unique within the PM tree.<br>
 * If there are other PMs with the same identifier within the faces tree, the corresponding
 * faces component can't be found.
 * <p>
 * This limitation should not be harmful if you use an expressive name for a named object or sub-element
 * that acts as a tab set.
 *
 * @author olaf boede
 *
 * @deprecated Please use {@link PmTabSet}.
 */
public interface DeprPmTabSet extends PmElement {

  /**
   * This method gets called whenever the user or internal UI logic attempts
   * to switch from one opened tab to another one.
   * <p>
   * This method may prevent the tab switch operation by returning
   * <code>false</code>.
   * <p>
   * <b>ATTENTION: This method is not intended for overriding!</b><br>
   * Please define specific tab switch logic by overriding
   * {@link DeprPmTabSetImpl#switchToTabPmImpl(PmElement, PmElement)}.
   *
   * @param fromTab
   *          The currently opened tab.
   * @param toTab
   *          The tab to switch to.
   * @return <code>true</code> if the PM logic implementation allows the tab switch. <br>
   *         <code>false</code> if the PM logic implementation prevents the tab switch.
   */
  boolean switchToTabPm(PmElement fromTab, PmElement toTab);

  boolean switchToTabPm(PmElement toTab);

  /**
   * @return The currently active tab.
   */
  PmElement getCurrentTabPm();

  /**
   * @return The set of tabs.
   */
  List<PmElement> getTabPms();


}
