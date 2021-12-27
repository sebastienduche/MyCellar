package mycellar.core.uicomponents;

import mycellar.general.ProgramPanels;

import javax.swing.JTextField;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Objects;

/**
 * <p>Titre : Cave &agrave; vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2005</p>
 * <p>Soci&eacute;t&eacute; : Seb Informatique</p>
 *
 * @author S&eacute;bastien Duch&eacute;
 * @version 0.7
 * @since 22/10/21
 */
public final class JModifyTextField extends JTextField {

  private static final long serialVersionUID = 7663077125632345441L;

  private boolean modified;
  private boolean active;
  private boolean listenerEnable;

  public JModifyTextField() {
    modified = false;
    active = true;
    listenerEnable = true;
    addKeyListener(new KeyListener() {

      @Override
      public void keyTyped(KeyEvent arg0) {
        if (active && listenerEnable) {
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

  public boolean isActive() {
    return active;
  }

  public void setActive(boolean active) {
    this.active = active;
  }

  @Override
  public String getText() {
    return Objects.requireNonNull(super.getText()).strip();
  }

  public void setListenerEnable(boolean listenerEnable) {
    this.listenerEnable = listenerEnable;
  }
}
