package mycellar.core;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import mycellar.Start;
import mycellar.core.MyCellarComboBox;

/**
 * <p>Titre : Cave à vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2005</p>
 * <p>Société : Seb Informatique</p>
 * @author Sébastien Duché
 * @version 0.3
 * @since 21/01/17
 */

public final class JModifyComboBox<T> extends MyCellarComboBox<T> {

	private static final long serialVersionUID = 833606680694326736L;

	private boolean modified;
	private boolean active;

	JModifyComboBox() {
		modified = false;
		active = true;
		addItemListener(itemEvent -> {
			if(itemEvent.getStateChange() == ItemEvent.SELECTED) {
				if(active) {
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

	private void doAfterModify(){
		Start.setPaneModified(true);
	}
}
