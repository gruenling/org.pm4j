package org.pm4j.common.util.beanproperty;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.beans.VetoableChangeSupport;

import org.pm4j.common.exception.CheckedExceptionWrapper;

/**
 * Provides some property change support.
 *
 * @author olaf boede
 */
public class PropertyChangeSupportedBase implements PropertyChangeSupported, Cloneable {

  private PropertyChangeSupport pcs;
  private VetoableChangeSupport vpcs;
  /** A switch that may be used the switch fireing of veto events off. */
  private boolean vetoEventEnabled = true;

  @Override
  public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
    getPcs().addPropertyChangeListener(propertyName, listener);
  }

  @Override
  public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
    getPcs().removePropertyChangeListener(propertyName, listener);
  }

  protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
    getPcs().firePropertyChange(propertyName, oldValue, newValue);
  }

  protected void firePropertyChange(PropertyChangeEvent event) {
    getPcs().firePropertyChange(event);
  }

  @Override
  public void addVetoableChangeListener(String propertyName, VetoableChangeListener listener) {
    getVpcs().addVetoableChangeListener(propertyName, listener);
  }

  @Override
  public void removeVetoableChangeListener(String propertyName, VetoableChangeListener listener) {
    getVpcs().removeVetoableChangeListener(propertyName, listener);
  }

  @Override
  public void addPropertyAndVetoableListener(String propertyName, PropertyAndVetoableChangeListener listener) {
    addPropertyChangeListener(propertyName, listener);
    addVetoableChangeListener(propertyName, listener);
  }

  @Override
  public void removePropertyAndVetoableListener(String propertyName, PropertyAndVetoableChangeListener listener) {
    removePropertyChangeListener(propertyName, listener);
    removeVetoableChangeListener(propertyName, listener);
  }

  @Override
  public void setVetoEventEnabled(boolean vetoEventEnabled) {
    this.vetoEventEnabled = vetoEventEnabled;
  }

  @Override
  public boolean isVetoEventEnabled() {
    return vetoEventEnabled;
  }

  protected void fireVetoableChange(String propertyName, Object oldValue, Object newValue) throws PropertyVetoException {
    if (vetoEventEnabled) {
      getVpcs().fireVetoableChange(propertyName, oldValue, newValue);
    }
  }

  protected void fireVetoableChange(PropertyChangeEvent event) throws PropertyVetoException {
    if (vetoEventEnabled) {
      getVpcs().fireVetoableChange(event);
    }
  }

  /**
   * Does not clone the observer references. These observers are only registered for the original.
   */
  @Override
  protected PropertyChangeSupportedBase clone() {
	try {
		PropertyChangeSupportedBase clone = (PropertyChangeSupportedBase) super.clone();
		clone.pcs = null;
		clone.vpcs = null;
		clone.vetoEventEnabled = true;
		return clone;
	} catch (CloneNotSupportedException e) {
		throw new CheckedExceptionWrapper(e);
	}
  }

  private PropertyChangeSupport getPcs() {
    if (pcs == null) {
      pcs = new PropertyChangeSupport(this);
    }
    return pcs;
  }

  private VetoableChangeSupport getVpcs() {
    if (vpcs == null) {
      vpcs = new VetoableChangeSupport(this);
    }
    return vpcs;
  }

}
