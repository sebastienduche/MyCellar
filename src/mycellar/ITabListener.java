/* *******************************************************
 * (c) 1996-2009 HR Access Solutions. All rights reserved
 * ******************************************************/

package mycellar;

import mycellar.core.uicomponents.TabEvent;
import mycellar.frame.MainFrame;

import java.util.EventListener;

/**
 * A listener notified when a tab is about to close.
 *
 * @author Francois RITALY / HR Access Solutions
 */
public interface ITabListener extends EventListener {

  /**
   * Notified the listener that the containing tab is about to close. The
   * listener can veto the tab closing.
   *
   * @param event the notified event
   * @return whether the tab closing is to be done.
   */
  default boolean tabWillClose(TabEvent event) {
    return true;
  }

  default void tabClosed() {
    MainFrame.updateMainPanel();
  }
}
