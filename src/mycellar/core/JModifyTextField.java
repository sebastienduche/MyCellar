package mycellar.core;

import mycellar.Start;

import javax.swing.JTextField;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Objects;

/**
 * <p>Titre : Cave à vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2005</p>
 * <p>Société : Seb Informatique</p>
 * @author Sébastien Duché
 * @version 0.6
 * @since 20/04/21
 */

public class JModifyTextField extends JTextField {

	private static final long serialVersionUID = 7663077125632345441L;

	private boolean modified;
	private boolean modifyActive;
	
	public JModifyTextField() {
		modified = false;
		modifyActive = true;
		addKeyListener(new KeyListener() {
			
			@Override
			public void keyTyped(KeyEvent arg0) {
				if(modifyActive) {
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
		Start.setPaneModified(true);
	}

	public boolean isModified() {
		return modified;
	}

	public void setModified(boolean modified) {
		this.modified = modified;
	}
	
	public boolean isModifyActive() {
		return modifyActive;
	}

	public void setModifyActive(boolean modifyActive) {
		this.modifyActive = modifyActive;
	}

	@Override
	public String getText() {
		return Objects.requireNonNull(super.getText()).strip();
	}
}
