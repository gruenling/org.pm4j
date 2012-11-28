package org.pm4j.jsf.connector;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;

import org.apache.commons.lang.StringUtils;
import org.pm4j.core.pm.PmAttr;

/**
 * A JSF view adapter that allows to use value change listeners for attribues.
 * <p>
 * Example usage: <pre>
 *   h:inputText
 *     value="#{pm.pmViewAdapter.valueAsString}"
 *     valueChangeListener="#{pm.pmViewAdapter.valueAsStringChangeListener}"
 * </pre>
 *
 * @author olaf boede
 */
public class AttrToJsfViewConnectorWithValueChangeListener {

  private final PmAttr<?> pmAttr;

  /**
   * The component binding is done only for a single request.<br>
   * This prevents live time/memory leak issues between the PM- and the JSF component trees.
   */
  private ThreadLocal<UIComponent> uiComponent = new ThreadLocal<UIComponent>();

  public AttrToJsfViewConnectorWithValueChangeListener(PmAttr<?> pmAttr) {
    this.pmAttr = pmAttr;
  }

  /**
   * A value change listener according to the JSF standard.
   *
   * @param event A value change event that contains the new value to apply.
   */
  public void valueAsStringChangeListener(ValueChangeEvent event) {
    String oldValue = StringUtils.defaultString((String)event.getOldValue(), "");
    String newValue = StringUtils.defaultString((String)event.getNewValue(), "");
    if (! oldValue.equals(newValue)) {
      pmAttr.setValueAsString(newValue);
    }
  }

  /**
   * Provides the attribute value string to render.
   *
   * @return The current value string.
   */
  public String getValueAsString() {
    return pmAttr.getValueAsString();
  }

  /**
   * A call to this setter should be ignored.
   * Unfortunately JSF calls the setter before the value change listener.
   * We have to wait for the value change listener call, because this gets done within the intended
   * JSF phase.
   *
   * @param value
   *            The value to ignore.
   */
  public void setValueAsString(String value) {
    // ignore
  }

  /**
   * A value change listener that uses the attribute value object without converting it to a string.
   * <p>
   * Useful for some JSF components that support non-string values.<br>
   * Example:<pre>
   *   h:selectBooleanCheckbox
   *     value="#{pm.pmViewAdapter.value}"
   *     valueChangeListener="#{pm.pmViewAdapter.valueChangeListener}"
   * </pre>
   *
   * @param event A value change event that contains the new value to apply.
   */
  @SuppressWarnings({ "unchecked", "rawtypes" })
  public void valueChangeListener(ValueChangeEvent event) {
    ((PmAttr)pmAttr).setValue(event.getNewValue());
  }


  /**
   * A getter that uses the attribute value object without converting it to a string.
   *
   * @return The attribute value.
   */
  public Object getValue() {
    return pmAttr.getValue();
  }

  /**
   * A call to this setter should be ignored.
   * Unfortunately JSF calls the setter before the value change listener.
   * We have to wait for the value change listener call, because this gets done within the intended
   * JSF phase.
   *
   * @param value
   *            The value to ignore.
   */
  public void setValue(Object value) {
    // ignore
  }

  /**
   * @return the (optionally) bound JSF component.
   */
  public UIComponent getUiComponent() {
    return uiComponent.get();
  }

  /**
   * Binds a component reference for the time of this request.
   *
   * @param uiComponent the component to bind.
   */
  public void setUiComponent(UIComponent uiComponent) {
    this.uiComponent.set(uiComponent);
  }

  /**
   * Gets the client ID of the bound {@link UIComponent}.
   * <p>
   * Provides only a result if the component was bound. See {@link #setUiComponent(UIComponent)}.
   *
   * @return the complete component client id.
   */
  public String getClientId() {
      UIComponent c = getUiComponent();
      return (c != null)
          ? c.getClientId(FacesContext.getCurrentInstance())
          : "";
  }

  /**
   * Gets the client ID of the bound {@link UIComponent}. Escaped for javascript usage. XXX olaf: still needed?
   * <p>
   * Provides only a result if the component was bound. See {@link #setUiComponent(UIComponent)}.
   *
   * @return the complete component id.
   */
  public String getEscapedClientId() {
    UIComponent c = getUiComponent();
    FacesContext ctxt = FacesContext.getCurrentInstance();

    return c != null
        ? StringUtils.replace(c.getClientId(ctxt), ":", "\\\\:")
        : null;
  }
}
