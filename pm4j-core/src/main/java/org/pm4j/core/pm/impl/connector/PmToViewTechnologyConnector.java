package org.pm4j.core.pm.impl.connector;

import org.pm4j.core.pm.PmObject;
import org.pm4j.core.pm.impl.inject.NamedObjectResolver;
import org.pm4j.navi.NaviHistory;
import org.pm4j.navi.impl.NaviLinkImpl;

/**
 * Interface for UI platform specific navigation tasks.
 *
 * @author olaf boede
 */
// TODO: Check the interface! Most methods are not clear.
public interface PmToViewTechnologyConnector {

  /**
   * Navigates the UI to the specified location.
   *
   * @param naviLink
   *          The navigation target. The application stays on the current page
   *          when this parameter is <code>null</code>.
   */
  void redirect(NaviLinkImpl naviLink);

  /**
   * Navigates the UI to the specified location.<p>
   *
   * The parameter set of the current request will be added to the new request
   * to be generated.
   *
   * @param toPageId UI platform id of the page to navigate to.
   */
  void redirectWithRequestParams(String toPageId);

  /**
   * @return <code>true</code> when the current location was started with pm4j request parameters.
   */
  boolean hasRequestParams();

  /**
   * Provides a PM-system specific value.
   * <p>
   * In case of web applications it provides a parameter that is encoded within
   * the 'pm4j' request attribute.
   *
   * @param attrName
   *          Name of the attribute.
   * @return The attribute value. Is <code>null</code> when there is no
   *         attribute for the given name.
   */
  String readRequestValue(String attrName);

  /**
   * @return The history of visited pages.
   */
  NaviHistory getNaviHistory();

  /**
   * Reads a (view technology specific) value from the current request context.
   * <p>
   * In case of a web application it provides the value of an http request
   * attribute.
   *
   * @param attrName
   *          Name (key) of the attribute.
   * @return The found value. May be <code>null</code>.
   */
  Object readRequestAttribute(String attrName);

  void setRequestAttribute(String attrName, Object value);

  /**
   * Provides an optional view technology specific named object resolver.
   * <p>
   * It may be configured to provide objects defined in a view technology
   * specific naming context. E.g. JSF.
   *
   * @return the configued resolver or <code>null</code>.
   */
  NamedObjectResolver getNamedObjectResolver();

  /**
   * Provides a view technology specific connector.
   * <p>
   * An example use case:<br>
   * For some JSF use cases it makes sense to set attribute values using a
   * valueChangeListener. For such scenarios a JSF connector may provide
   * a connector instance having a corresponding value change listener method.
   *
   * @param pm The PM to create a connector for.
   * @return The view technology specific component connector.
   */
  Object createPmToViewConnector(PmObject pm);

}
