package org.pm4j.core.sample.admin.remote_sample.client;

import org.pm4j.core.pm.PmAttr;
import org.pm4j.core.pm.PmAttrString;
import org.pm4j.core.pm.PmCommand;
import org.pm4j.core.pm.PmEvent;
import org.pm4j.core.pm.annotation.PmAttrStringCfg;
import org.pm4j.core.pm.annotation.PmCommandCfg;
import org.pm4j.core.pm.impl.PmAttrImpl;
import org.pm4j.core.pm.impl.PmAttrStringImpl;
import org.pm4j.core.pm.impl.PmCommandImpl;
import org.pm4j.core.pm.impl.PmElementImpl;
import org.pm4j.core.sample.admin.remote_sample.annotations.PmClientCfg;
import org.pm4j.core.sample.admin.remote_sample.shared.LoginState;

@PmClientCfg(serverPm="#loginSrvPm")
public class LoginPm extends PmElementImpl {
  
  @PmAttrStringCfg(minLen=3)
  public final PmAttrString userName = new PmAttrStringImpl(this);
  
  @PmAttrStringCfg(minLen=6)
  public final PmAttrString pwd = new PmAttrStringImpl(this);
  
  public final PmCommand cmdLogin = new PmCommandImpl(this);
  
  @PmClientCfg(isServerProvided=false) 
  @PmCommandCfg(requiresValidValues=false) 
  public final PmCommand cmdCancel = new PmCommandImpl(this);
  
  /** Some server provided content for this client session. */ 
  public final PmAttr<LoginState> loginState = new PmAttrImpl<LoginState>(this) {
    protected void onPmValueChange(PmEvent event) {
      // store some relevant information within the client session.
    }
  };
}
