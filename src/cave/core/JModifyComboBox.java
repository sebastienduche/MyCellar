package Cave.core;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import Cave.core.MyCellarComboBox;
import Cave.Start;

/**
 * <p>Titre : Cave à vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2005</p>
 * <p>Société : Seb Informatique</p>
 * @author Sébastien Duché
 * @version 0.3
 * @since 21/01/17
 */

public class JModifyComboBox<T> extends MyCellarComboBox<T> {

	private static final long serialVersionUID = 833606680694326736L;

	private boolean modified;
	private boolean active;

	public JModifyComboBox() {
		modified = false;
		active = true;
		addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent arg0) {
				if(arg0.getStateChange() == ItemEvent.SELECTED) {
					if(active) {
						modified = true;
						doAfterModify();
					}
				}
			}
		});
	}
	
	public JModifyComboBox(boolean active) {
		this();
		this.active = active;
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
