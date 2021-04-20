package mycellar.core;

import mycellar.Start;

import javax.swing.JFormattedTextField;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.text.Format;

/**
 * <p>Titre : Cave à vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2005</p>
 * <p>Société : Seb Informatique</p>
 * @author Sébastien Duché
 * @version 0.5
 * @since 20/04/21
 */

public class JModifyFormattedTextField extends JFormattedTextField {

	private static final long serialVersionUID = -7364848812779720027L;
	
	private boolean modified;
	private boolean modifyActive;

	public JModifyFormattedTextField(Format format) {
		super(format);
		init();
	}
	
	private void init() {
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

	public void setModifyActive(boolean modifyActive) {
		this.modifyActive = modifyActive;
	}

}
