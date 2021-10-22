package mycellar.core;

import mycellar.general.ProgramPanels;

import javax.swing.JTextArea;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * <p>Titre : Cave &agrave; vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2005</p>
 * <p>Soci&eacute;t&eacute; : Seb Informatique</p>
 *
 * @author S&eacute;bastien Duch&eacute;
 * @version 0.5
 * @since 22/10/21
 */

public final class JModifyTextArea extends JTextArea {

  private static final long serialVersionUID = 7858711227949516336L;

  private boolean modified;
  private boolean active;

  JModifyTextArea() {
    modified = false;
    active = true;
    addKeyListener(new KeyListener() {

      @Override
      public void keyTyped(KeyEvent arg0) {
        if (active) {
          modified = true;
          doAfterModify();
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


  private void doAfterModify() {
    ProgramPanels.setSelectedPaneModified(true);
  }

  public boolean isModified() {
    return modified;
  }

  public void setModified(boolean modified) {
    this.modified = modified;
  }

  public boolean isModifyActive() {
    return active;
  }

  public void setModifyActive(boolean active) {
    this.active = active;
  }
}
