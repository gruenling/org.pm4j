package org.pm4j.deprecated.core.pm.filter.impl;

import org.pm4j.common.util.CompareUtil;
import org.pm4j.core.pm.PmObject;
import org.pm4j.core.pm.api.PmLocalizeApi;

@Deprecated
public class DeprCompOpStringNotEquals extends DeprCompOpStringBase {

  public static final String NAME = "compOpStringNotEquals";

  public DeprCompOpStringNotEquals(PmObject pmCtxt) {
    super(NAME, PmLocalizeApi.localize(pmCtxt, NAME));
  }

  @Override
  protected boolean doesValueMatchImpl(String itemValue, String filterValue) {
    return ! CompareUtil.equalStrings(itemValue, filterValue, isIgnoreCase(), isIgnoreSpaces());
  }

  @Override
  protected boolean isEffectiveFilterValueImpl(String filterValue) {
    return true;
  }

}
