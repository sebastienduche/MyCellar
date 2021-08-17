package mycellar.core;

import mycellar.Start;

import java.awt.event.ItemEvent;

/**
 * <p>Titre : Cave à vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2005</p>
 * <p>Société : Seb Informatique</p>
 *
 * @author Sébastien Duché
 * @version 0.4
 * @since 12/03/21
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
    Start.setPaneModified(true);
  }
}
