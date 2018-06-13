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
 * @version 0.3
 * @since 21/01/17
 */

public class JModifyFormattedTextField extends JFormattedTextField {

	private static final long serialVersionUID = -7364848812779720027L;
	
	private boolean modified;
	private boolean active;

	private JModifyFormattedTextField() {
		init();
	}

	public JModifyFormattedTextField(Object value) {
		super(value);
		init();
	}

	public JModifyFormattedTextField(Format format) {
		super(format);
		init();
	}

	public JModifyFormattedTextField(AbstractFormatter formatter) {
		super(formatter);
		init();
	}

	public JModifyFormattedTextField(AbstractFormatterFactory factory) {
		super(factory);
		init();
	}

	public JModifyFormattedTextField(AbstractFormatterFactory factory,
			Object currentValue) {
		super(factory, currentValue);
		init();
	}
	
	public JModifyFormattedTextField(boolean active) {
		this();
		this.active = active;
	}
	
	public JModifyFormattedTextField(Object value, boolean active) {
		super(value);
		init();
		this.active = active;
	}

	public JModifyFormattedTextField(Format format, boolean active) {
		super(format);
		init();
		this.active = active;
	}

	public JModifyFormattedTextField(AbstractFormatter formatter, boolean active) {
		super(formatter);
		init();
		this.active = active;
	}

	public JModifyFormattedTextField(AbstractFormatterFactory factory, boolean active) {
		super(factory);
		init();
		this.active = active;
	}

	public JModifyFormattedTextField(AbstractFormatterFactory factory,
			Object currentValue, boolean active) {
		super(factory, currentValue);
		init();
		this.active = active;
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
	
	public boolean isModifyActive() {
		return active;
	}

	public void setModifyActive(boolean active) {
		this.active = active;
	}

}
