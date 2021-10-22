package mycellar.core;

import mycellar.general.ProgramPanels;

import java.awt.event.ItemEvent;

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

public final class JModifyComboBox<T> extends MyCellarComboBox<T> {

  private static final long serialVersionUID = 833606680694326736L;

  private boolean modified;
  private boolean active;

  public JModifyComboBox() {
    modified = false;
    active = true;
    addItemListener(itemEvent -> {
      if (itemEvent.getStateChange() == ItemEvent.SELECTED) {
        if (active) {
          modified = true;
          doAfterModify();
        }
      }
    });
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

  private void doAfterModify() {
    ProgramPanels.setSelectedPaneModified(true);
  }
}
