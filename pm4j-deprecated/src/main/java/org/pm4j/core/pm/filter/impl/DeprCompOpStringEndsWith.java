package org.pm4j.core.pm.filter.impl;

import org.apache.commons.lang.StringUtils;
import org.pm4j.common.util.CompareUtil;
import org.pm4j.core.pm.PmObject;
import org.pm4j.core.pm.api.PmLocalizeApi;

@Deprecated
public class DeprCompOpStringEndsWith extends DeprCompOpStringBase {

  public static final String NAME = "compOpStringEndsWith";

  public DeprCompOpStringEndsWith(PmObject pmCtxt) {
    super(NAME, PmLocalizeApi.localize(pmCtxt, NAME));
  }

  @Override
  protected boolean doesValueMatchImpl(String itemValue, String filterValue) {
    return CompareUtil.endsWith(itemValue, filterValue, isIgnoreCase(), isIgnoreSpaces());
  }

  @Override
  protected boolean isEffectiveFilterValueImpl(String filterValue) {
    return ! StringUtils.isBlank(filterValue);
  }

}
