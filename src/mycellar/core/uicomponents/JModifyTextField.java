package mycellar.core.uicomponents;

import mycellar.general.ProgramPanels;

import javax.swing.JTextField;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Objects;

/**
 * <p>Titre : Cave &agrave; vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2005</p>
 * <p>Soci&eacute;t&eacute; : Seb Informatique</p>
 *
 * @author S&eacute;bastien Duch&eacute;
 * @version 0.8
 * @since 30/12/21
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
    addKeyListener(new KeyAdapter() {

      @Override
      public void keyTyped(KeyEvent arg0) {
        if (active && listenerEnable) {
          modified = true;
          doAfterModify();
        }
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
