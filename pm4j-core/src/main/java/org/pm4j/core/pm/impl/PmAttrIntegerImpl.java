package org.pm4j.core.pm.impl;

import org.pm4j.core.exception.PmRuntimeException;
import org.pm4j.core.exception.PmValidationException;
import org.pm4j.core.pm.PmAttrInteger;
import org.pm4j.core.pm.PmConstants;
import org.pm4j.core.pm.PmObject;
import org.pm4j.core.pm.annotation.PmAttrIntegerCfg;
import org.pm4j.core.pm.impl.converter.PmConverterInteger;

/**
 * Implements a PM attribute for {@link Integer} values.
 *
 * @author olaf boede
 */
public class PmAttrIntegerImpl extends PmAttrNumBase<Integer> implements PmAttrInteger {

  public PmAttrIntegerImpl(PmObject pmParent) {
    super(pmParent);
  }

  // ======== Interface implementation ======== //

  public Integer getMaxValue() {
    return getOwnMetaData().maxValue;
  }

  public Integer getMinValue() {
    return getOwnMetaData().minValue;
  }

  // ======== Value handling ======== //

  @Override
  protected void validate(Integer value) throws PmValidationException {
    super.validate(value);

    if (value != null) {
      int v = value.intValue();
      if (v < getMinValue().longValue()) {
        throw new PmValidationException(this, PmConstants.MSGKEY_VALIDATION_VALUE_TOO_LOW, getMinValue());
      }
      if (v > getMaxValue().longValue()) {
        throw new PmValidationException(this, PmConstants.MSGKEY_VALIDATION_VALUE_TOO_HIGH, getMaxValue());
      }
    }
  }

  // ======== meta data ======== //

  @Override
  protected PmObjectBase.MetaData makeMetaData() {
	MetaData md = new MetaData();
	md.setStringConverter(PmConverterInteger.INSTANCE);
	return md;
  }

  @Override
  protected void initMetaData(PmObjectBase.MetaData metaData) {
    super.initMetaData(metaData);
    MetaData myMetaData = (MetaData) metaData;

    PmAttrIntegerCfg annotation = AnnotationUtil.findAnnotation(this, PmAttrIntegerCfg.class);
    if (annotation != null) {
      int maxValue = myMetaData.maxValue = annotation.maxValue();
      int minValue = myMetaData.minValue = annotation.minValue();

      if (minValue > maxValue) {
        throw new PmRuntimeException(this, "minValue(" + minValue + ") > maxValue(" + maxValue + ")");
      }
    }
  }

  protected static class MetaData extends PmAttrNumBase.MetaData {
    private int maxValue = Integer.MAX_VALUE;
    private int minValue = Integer.MIN_VALUE;

    @Override
    protected double getMaxValue() {
      return maxValue;
    }
  }

  private final MetaData getOwnMetaData() {
    return (MetaData) getPmMetaData();
  }

}
