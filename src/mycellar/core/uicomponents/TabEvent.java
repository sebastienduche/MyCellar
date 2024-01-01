/* *******************************************************
 *  1996-2009 HR Access Solutions. All rights reserved
 * ******************************************************/

package mycellar.core.uicomponents;

import java.util.EventObject;

/**
 * An event triggered by a tab when it's about to close.
 *
 * @author Francois RITALY / HR Access Solutions
 */
public class TabEvent extends EventObject {

  /**
   * Creates a new instance of TabEvent.
   */
  public TabEvent(Object source) {
    super(source);
  }
}
