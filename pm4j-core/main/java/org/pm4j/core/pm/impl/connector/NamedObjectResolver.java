package org.pm4j.core.pm.impl.connector;

/**
 * Interface for technology specific adapters which resolve objects by name.
 * <p>
 * There are implementations for specific environments like EJB, Spring etc.
 *
 * @author olaf boede
 */
public interface NamedObjectResolver {

  /**
   * Searches a named object.
   *
   * @param name Name of the object to finde.
   * @return The found instance or <code>null</code>.
   */
  Object findObject(String name);

}
