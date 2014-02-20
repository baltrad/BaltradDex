package eu.baltrad.dex.net.controller.util;

import org.apache.log4j.Logger;
import org.springframework.ui.Model;

import eu.baltrad.dex.util.MessageResourceUtil;

public class ModelMessageHelper implements MessageSetter {
  private MessageResourceUtil util = null;
  private boolean logDex = true;
  private Logger log = null;
  
  public ModelMessageHelper(MessageResourceUtil util) {
    this(util, true);
  }
  
  public ModelMessageHelper(MessageResourceUtil util, boolean logDex) {
    this.util = util;
    this.logDex = logDex;
    this.log = Logger.getLogger("DEX");
  }

  protected ModelMessageHelper() {
    this.log = Logger.getLogger("DEX");
  }
  
  protected void setMessageResources(MessageResourceUtil util) {
    this.util = util;
  }

  protected MessageResourceUtil getMessageResources() {
    return this.util;
  }

  protected void setLogDex(boolean logDex) {
    this.logDex = logDex;
  }
  
  protected boolean getLogDex() {
    return this.logDex;
  }
  
  public void setSuccessMessage(Model model, String msgkey, Object...msgValues) {
    String successMsg = null;
    if (msgValues.length > 0) {
      successMsg = util.getMessage(msgkey, msgValues);
    } else {
      successMsg = util.getMessage(msgkey);
    }
    setMessage(model, SUCCESS_MSG_KEY, successMsg);
    if (logDex) {
      log.warn(successMsg);
    }
  }
 
  public void setSuccessDetailsMessage(Model model, String msgkey, String details, Object...msgValues) {
    String successMsg = null;
    if (msgValues.length > 0) {
      successMsg = util.getMessage(msgkey, msgValues);
    } else {
      successMsg = util.getMessage(msgkey);
    }
    setMessage(model, SUCCESS_MSG_KEY, SUCCESS_DETAILS_KEY, successMsg, details);
    if (logDex) {
      log.warn(successMsg);
    }
  }
  
  public void setErrorMessage(Model model, String msgkey, Object...msgValues) {
    String errorMsg = null;
    if (msgValues.length > 0) {
      errorMsg = util.getMessage(msgkey, msgValues);
    } else {
      errorMsg = util.getMessage(msgkey);
    }
    setTextErrorMessage(model, errorMsg);
  }

  public void setTextErrorMessage(Model model, String txt) {
    setMessage(model, ERROR_MSG_KEY, txt);
    if (logDex) {
      log.error(txt);
    }
  }
  
  public void setErrorDetailsMessage(Model model, String msgkey, String details, Object...msgValues) {
    String errorMsg = null;
    if (msgValues.length > 0) {
      errorMsg = util.getMessage(msgkey, msgValues);
    } else {
      errorMsg = util.getMessage(msgkey);
    }
    setMessage(model, ERROR_MSG_KEY, ERROR_DETAILS_KEY, errorMsg, details);
    if (logDex) {
      log.error(errorMsg + ": " + details);
    }
  }

  public void setKeyMessage(Model model, String key, String msgkey, Object...msgValues) {
    String keyMsg = null;
    if (msgValues.length > 0) {
      keyMsg = util.getMessage(msgkey, msgValues);
    } else {
      keyMsg = util.getMessage(msgkey);
    }
    setMessage(model, key, keyMsg);
    if (logDex) {
      log.warn(keyMsg);
    }
  }
  
  public String getMessage(String key, Object...msgValues) {
    if (msgValues.length > 0) {
      return util.getMessage(key, msgValues);
    } else {
      return util.getMessage(key);
    }
  }
  
  @Override
  public void setMessage(Model model, String messageKey, String message) {
    model.addAttribute(messageKey, message);
  }

  @Override
  public void setMessage(Model model, String messageKey, String detailsKey, String message, String details) {
    model.addAttribute(messageKey, message);
    model.addAttribute(detailsKey, details);    
  }
}
