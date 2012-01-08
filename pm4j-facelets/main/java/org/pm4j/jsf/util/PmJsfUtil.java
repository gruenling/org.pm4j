package org.pm4j.jsf.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.pm4j.core.exception.PmRuntimeException;
import org.pm4j.core.pm.PmAttr;
import org.pm4j.core.pm.PmConversation;
import org.pm4j.core.pm.PmMessage;
import org.pm4j.core.pm.PmObject;
import org.pm4j.core.pm.PmOption;
import org.pm4j.core.pm.PmOptionSet;
import org.pm4j.core.pm.api.PmExpressionApi;
import org.pm4j.core.pm.impl.PmAttrBase;
import org.pm4j.core.pm.impl.PmConversationImpl;
import org.pm4j.jsf.Pm4jJsfConstants;
import org.pm4j.jsf.PmConnectorForJsf;
import org.pm4j.jsf.PmMessageCleanupListener;
import org.pm4j.jsf.UrlParamCoder;
import org.pm4j.jsf.impl.UrlParamCoderJson;
import org.pm4j.navi.NaviHistory;
import org.pm4j.navi.NaviLink;
import org.pm4j.navi.impl.NaviRuntimeException;

/**
 * Some convenience functions for JSF Api used in combination with presentation
 * models.
 *
 * @author olaf boede
 */
public class PmJsfUtil {

  /** The PM request parameter name. */
  public static final String PM4J_REQUEST_PARAM = "pm4j";
  /** Name of the request parameter map cache attribute. */
  public static final String REQUEST_ATTR_MAP_CACHE = "pm4j.req_param_cache";
  /** The algorithm that encodes a parameter map to a 'pm4j' request parameter value. */
  public static UrlParamCoder URL_PARAM_CODER = new UrlParamCoderJson();



  /** A re-useable empty array. */
  private static final SelectItem[] EMPTY_SELECTITEMS = {};

  /**
   * Provides JSF {@link SelectItem}s for the options of the given
   * {@link PmAttr}.
   *
   * @param pmAttr
   *          the attribute to get the options for.
   * @return the matching {@link SelectItem}s. An empty array when there are no
   *         options.
   */
  public static SelectItem[] makeSelectItems(PmAttr<?> pmAttr) {
    try {
      PmOptionSet pmOptionSet = pmAttr.getOptionSet();
      if (pmOptionSet != null) {
        List<PmOption> pmOptions = pmOptionSet.getOptions();
        List<SelectItem> items = new ArrayList<SelectItem>(pmOptions.size());
        // XXX olaf: is that still required? I suppose that all id's are handled finally as strings only.
        boolean useStringIds = ((PmAttrBase<?,?>)pmAttr).isSupportingAsStringValues();
        for (PmOption o : pmOptions) {
          Serializable id = useStringIds ? o.getIdAsString() : o.getId();

          // for JSF 1.1:
          if (id == null)
            id = "";

          SelectItem si = new SelectItem(id, o.getPmTitle());
          si.setDisabled(! o.isEnabled());
          items.add(si);
        }
        return items.toArray(new SelectItem[items.size()]);
      } else {
        return EMPTY_SELECTITEMS;
      }
    } catch (RuntimeException e) {
      throw new PmRuntimeException(pmAttr, e);
    }
  }

  /**
   * Provides a PM property value.
   *
   * @param pm
   *          The PM context object to get the property from.
   * @param key
   *          The property key.
   * @return The found value or <code>null</code>.
   */
  public static Object findPmProperty(PmObject pm, String key) {
    return PmExpressionApi.findByExpression(pm, key);
  }

  /**
   * Provides a PM property value.
   *
   * @param pm
   *          The PM context object to get the property from.
   * @param key
   *          The property key.
   * @return The found value.
   * @throws PmRuntimeException
   *           when no value exists for the given key.
   */
  public static Object getPmProperty(PmObject pm, String key) {
    return PmExpressionApi.getByExpression(pm, key);
  }

  /**
   * Sets a session context property value.
   *
   * @param pmConversation
   *          The session context that stores the parameter value.
   * @param key
   *          Name of the property to set.
   * @param value
   *          The new property value.
   * @return The value of the session context property.
   */
  public static Object setPmProperty(PmConversation pmConversation, String key, Object value) {
    if (pmConversation == null) {
      throw new NaviRuntimeException("Parameter 'pmConversation' should not be null.");
    }
    if (StringUtils.isBlank(key)) {
      throw new NaviRuntimeException("Parameter 'key' should not be empty or null.");
    }

    pmConversation.setPmNamedObject(key, value);

    return value;
  }

  /**
   * Registers the session messages for cleanup after the next render phase.
   *
   * @param pmConversation
   *          The session to register for message cleanup
   * @return The message list of the given presentation model.
   */
  public static List<PmMessage> registerPmMessagesForCleanup(PmConversation pmConversation) {
    if (pmConversation != null) {
      List<PmMessage> messages = pmConversation.getPmMessages();
      if ( (messages.size() > 0) ||
    	   ! pmConversation.isPmValid() ) {
        PmMessageCleanupListener.getInstance().addSession(pmConversation);
      }
      return messages;
    } else {
      return Collections.emptyList();
    }
  }

  /**
   * Generates a relative URL for a given navigation link that includes the
   * mapped servlet name.<br>
   * The uri includes the navigation version information (if available).
   * <p>
   * TODO: Supports only application local relative links for now.
   *
   * @param naviLink
   *          The link.
   * @return A relative URL for the link that includes the mapped servlet name
   *         and navigation version information.
   */
  // TODO: The PM is not really needed here. A signature without PM could be moved to NaviJsfUtil.
  public static String uriForNaviLink(PmObject pm, NaviLink naviLink) {
    if (naviLink != null) {
      PmConversationImpl pmConversation = (PmConversationImpl) pm.getPmConversation();
      PmConnectorForJsf naviHandler = (PmConnectorForJsf) pmConversation.getViewConnector();

      boolean withVersion = ! naviLink.isExternalLink();
      return JsfUtil.relUrlWithServletName(naviHandler.relUrlForNaviLink(naviLink, withVersion));
    }
    else {
      return null;
    }
  }

  /**
   * Generates a relative URL for a given navigation link that includes the
   * mapped servlet name.<br>
   * The URL includes the popup specific navigation version information (if available).
   *
   * @param naviLink
   *          The link.
   * @return A relative URL for the link that includes the mapped servlet name
   *         and navigation version information.
   */
  // TODO: The PM is not really needed here. A signature without PM could be moved to NaviJsfUtil.
  public static String urlForPopupNaviLink(PmObject pm, NaviLink naviLink) {
    if (naviLink != null) {
      String baseLink = uriForNaviLinkWithoutVersion(pm, naviLink);
      return baseLink +
             (baseLink.indexOf('?') == -1 ? '?' : '&') +
             NaviJsfUtil.getPopupNaviParam();
    }
    else {
      return null;
    }
  }

  /**
   * Generates a relative URL for a given navigation link that includes the
   * mapped servlet name.<br>
   * <p>
   * TODO: Supports only application local relative links for now.
   *
   * @param naviLink
   *          The link.
   * @return A relative URL for the link that includes the mapped servlet name
   *         and navigation version information.
   */
  // TODO: The PM is not really needed here. A signature without PM could be moved to NaviJsfUtil.
  public static String uriForNaviLinkWithoutVersion(PmObject pm, NaviLink naviLink) {
    if (naviLink != null) {
      PmConversationImpl pmConversation = (PmConversationImpl) pm.getPmConversation();
      PmConnectorForJsf viewConnector = (PmConnectorForJsf) pmConversation.getViewConnector();
      return JsfUtil.relUrlWithServletName(viewConnector.relUrlForNaviLink(naviLink, false));
    }
    else {
      return null;
    }
  }

  /**
   * An html helper that provides the PM parameters of the current request as hidden
   * fields.
   *
   * @return A set of hidden input fields.
   */
  // XXX:  check if these are really required in case of an active redirect handling
  //       of the NaviHistoryPhaseListener.
  //       In this case, only a pm4j-parameter change based on an ajax request seems to
  //       require a hidden field support. - But: Is there a single case in the current
  //       application use cases?
  //       So, this construct seems to be useful only for cases
  //       without navigation version triggered redirects.
  public static String hiddenFieldsForRequestParams() {
    StringBuilder sb = new StringBuilder();
    String existingReqParam = JsfUtil.readReqParam(PM4J_REQUEST_PARAM);

    if (existingReqParam != null) {
      sb.append("<input type='hidden' name='").append(PM4J_REQUEST_PARAM)
        .append("' value='").append(existingReqParam).append("'/>");
    }

    // Repeat the current navigation version.
    NaviHistory history = NaviJsfUtil.getNaviHistory();
    if (history != null) {
      sb.append("<input type='hidden' name='").append(history.getNaviCfg().getVersionParamName())
        .append("' value='").append(history.getVersionString()).append("'/>");
    }

    return sb.toString();
  }

  public static String getPmReqestParamKey() {
    return PM4J_REQUEST_PARAM;
  }

  /**
   * @return The value of the current pm4j request parameter.<br>
   *         Provides an empty string when there is currently no pm4j request
   *         parameter.
   */
  public static String getPmReqestParamValue() {
    return StringUtils.defaultString(JsfUtil.readReqParam(PM4J_REQUEST_PARAM));
  }

  /**
   * Reads a value from the (optionally existing) 'pm4j' request parameter set.
   *
   * @param paramName Name of the parameter.
   * @return The found value or <code>null</code>.
   */
  public static String readPm4jParamValue(String paramName) {
    return ObjectUtils.toString(getPmRequestParamMap().get(paramName), null);
  }

  /**
   * Reads the request parameter 'pm4j' and provides its key-value pairs.
   * <p>
   * Caches the converted map within the request to allow faster request
   * processing.
   *
   * @param request The current http request.
   * @return The pm4j parameter set.
   */
  public static Map<String, Object> getPmRequestParamMap() {
    return getPmRequestParamMap(JsfUtil.getHttpRequest());
  }

  /**
   * Reads the request parameter 'pm4j' and provides its key-value pairs.
   * <p>
   * Caches the converted map within the request to allow faster request
   * processing.
   *
   * @param request The current http request.
   * @return The pm4j parameter set.
   */
  public static Map<String, Object> getPmRequestParamMap(HttpServletRequest request) {
    @SuppressWarnings("unchecked")
    Map<String, Object> map = (Map<String, Object>)request.getAttribute(REQUEST_ATTR_MAP_CACHE);
    if (map == null) {
      String paramValueString = request.getParameter(PmJsfUtil.PM4J_REQUEST_PARAM);
      map = URL_PARAM_CODER.paramValueToMap(paramValueString);
      request.setAttribute(REQUEST_ATTR_MAP_CACHE, map);
    }
    return map;
  }

  /**
   * Reads the key parameter (see {@link Pm4jJsfConstants#CMD_PARAM_ELEM_KEY}) that get
   * send by some a4j links (such as 'ma4j:keyLink') that don't have reliable UI positions...
   *
   * @return The read parameter value or <code>null</code> it none was sent.
   */
  public static String readKeyLinkParam() {
    return JsfUtil.readReqParam(Pm4jJsfConstants.CMD_PARAM_ELEM_KEY);
  }

  /**
   * Reads the key parameter (see {@link Pm4jJsfConstants#CMD_PARAM_ELEM_KEY}) that get
   * send by some a4j links (such as 'ma4j:keyLink') that don't have reliable UI positions...
   *
   * @return The read parameter value or <code>null</code> it none was sent.
   */
  public static Long readKeyLinkParamAsLong() {
    String value = readKeyLinkParam();
    return value != null
              ? Long.parseLong(value)
              : null;
  }

}
