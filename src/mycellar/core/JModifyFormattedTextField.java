package mycellar.core;

import mycellar.general.ProgramPanels;

import javax.swing.JFormattedTextField;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.text.Format;

/**
 * <p>Titre : Cave &agrave; vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2005</p>
 * <p>Soci&eacute;t&eacute; : Seb Informatique</p>
 *
 * @author S&eacute;bastien Duch&eacute;
 * @version 0.6
 * @since 22/10/21
 */

public final class JModifyFormattedTextField extends JFormattedTextField {

  private static final long serialVersionUID = -7364848812779720027L;

  private boolean modified;
  private boolean active;
  private boolean listenerEnable;

  public JModifyFormattedTextField(Format format) {
    super(format);
    modified = false;
    active = true;
    listenerEnable = true;
    addKeyListener(new KeyListener() {

      @Override
      public void keyTyped(KeyEvent arg0) {
        if (active && listenerEnable) {
          modified = true;
          ProgramPanels.setSelectedPaneModified(true);
        }
      }

      @Override
      public void keyReleased(KeyEvent arg0) {
      }

      @Override
      public void keyPressed(KeyEvent arg0) {
      }
    });
  }

  public boolean isModified() {
    return modified;
  }

  public void setModified(boolean modified) {
    this.modified = modified;
  }

  public void setActive(boolean active) {
    this.active = active;
  }

  public void setListenerEnable(boolean listenerEnable) {
    this.listenerEnable = listenerEnable;
  }
}
