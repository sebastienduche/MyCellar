package mycellar.core;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.text.Format;

import javax.swing.JFormattedTextField;

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

public class JModifyFormattedTextField extends JFormattedTextField {

	private static final long serialVersionUID = -7364848812779720027L;
	
	private boolean modified;
	private boolean active;

	JModifyFormattedTextField(Format format) {
		super(format);
		init();
	}
	
	private void init() {
		modified = false;
		active = true;
		addKeyListener(new KeyListener() {
			
			@Override
			public void keyTyped(KeyEvent arg0) {
				if(active){
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
	
	private void doAfterModify(){
		Start.setPaneModified(true);
	}
	
	public boolean isModified() {
		return modified;
	}

	public void setModified(boolean modified) {
		this.modified = modified;
	}

	public void setModifyActive(boolean active) {
		this.active = active;
	}

}
