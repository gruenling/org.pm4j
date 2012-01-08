package org.pm4j.core.pm.impl;

import java.util.Date;

import org.apache.commons.lang.time.FastDateFormat;
import org.pm4j.common.util.CompareUtil;
import org.pm4j.core.pm.PmAttrDate;
import org.pm4j.core.pm.PmObject;
import org.pm4j.core.pm.impl.converter.PmConverterDate;

public class PmAttrDateBase<T_BACKING_DATE> extends PmAttrBase<Date, T_BACKING_DATE> implements PmAttrDate {

  public PmAttrDateBase(PmObject pmParent) {
    super(pmParent);
  }

  /**
   * The default format key {@link #RESKEY_DEFAULT_FORMAT_PATTERN} applies when no
   * special format is defined (either by resource key with postfix or
   * annotation).
   *
   * @see PmAttrBase#getFormatString()
   */
  @Override
  protected String getFormatDefaultResKey() {
    return RESKEY_DEFAULT_FORMAT_PATTERN;
  }

  public String getOutputFormat() {
    // TODO: missing support for alternate converter configuration objects.
    return PmConverterDate.INSTANCE.getOutputFormat(this);
  }

  /**
   * A subclass with a tooltip that provides a hint about the format.
   */
  // XXX olaf: should this stay in the core?
  public static class WithFormatTooltip<T_BACKING_DATE> extends PmAttrDateBase<T_BACKING_DATE> {

    public static final String RESKEY_DATEATTR_FORMAT_TOOLTIP = "pmAttrDate.WithFormatTooltip_tooltip";

    public WithFormatTooltip(PmElementBase pmParentBean) {
      super(pmParentBean);
    }

    @Override
    protected String getPmTooltipImpl() {
      String fmt = getOutputFormat();
      return localize(RESKEY_DATEATTR_FORMAT_TOOLTIP,
                      fmt, FastDateFormat.getInstance(fmt).format(new Date()));
    }
  }

  @Override
  public int compareTo(PmObject otherPm) {
    return getPmAbsoluteName().equals(PmUtil.getPmAbsoluteName(otherPm))
              ? CompareUtil.compare(getValue(), ((PmAttrDate)otherPm).getValue())
              : super.compareTo(otherPm);
  }

  // ======== meta data ======== //

  @Override
  protected void initMetaData(PmObjectBase.MetaData metaData) {
    super.initMetaData(metaData);

    ((MetaData) metaData).setConverterDefault(PmConverterDate.INSTANCE);
  }

}
