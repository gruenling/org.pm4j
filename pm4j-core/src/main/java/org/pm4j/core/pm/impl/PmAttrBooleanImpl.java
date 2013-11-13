package org.pm4j.core.pm.impl;

import org.pm4j.core.pm.PmAttrBoolean;
import org.pm4j.core.pm.PmObject;
import org.pm4j.core.pm.PmOptionSet;
import org.pm4j.core.pm.api.PmLocalizeApi;
import org.pm4j.core.pm.impl.converter.PmConverterBoolean;
import org.pm4j.core.pm.impl.options.PmOptionSetImpl;
import org.pm4j.core.pm.impl.options.PmOptionSetUtil;

/**
 * Implements a PM attribute for {@link Boolean} values.
 *
 * @author olaf boede
 */
public class PmAttrBooleanImpl extends PmAttrBase<Boolean, Boolean> implements PmAttrBoolean {

  public PmAttrBooleanImpl(PmObject pmParent) {
    super(pmParent);
  }

  /**
   * Provides a localized according to the current value.
   * <p>
   * Provides the localizations for the keys 'Boolean.TRUE' and 'Boolean.FALSE'.
   */
  @Override
  public String getValueLocalized() {
    Boolean value = getValue();
    return (value != null)
      ? PmLocalizeApi.localizeBooleanValue(this, value)
      : null;
  }

  @Override
  protected PmOptionSet getOptionSetImpl() {
    boolean withNullOption = !isRequired() ||
                             (getValue() == null);
    return new PmOptionSetImpl(PmOptionSetUtil.makeBooleanOptions(this, withNullOption));
  }

  // ======== meta data ======== //

  @Override
  protected PmObjectBase.MetaData makeMetaData() {
	  MetaData md = new PmAttrBase.MetaData(10);
	  md.setStringConverter(PmConverterBoolean.INSTANCE);
	  return md;
  }

}
