package mycellar.core;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JTextArea;

import mycellar.Start;

/**
 * <p>Titre : Cave à vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2005</p>
 * <p>Société : Seb Informatique</p>
 * @author Sébastien Duché
 * @version 0.4
 * @since 28/08/20
 */

public class JModifyTextArea extends JTextArea {

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
		Start.setPaneModified(true);
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
