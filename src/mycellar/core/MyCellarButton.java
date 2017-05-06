package mycellar.core;

import java.awt.Font;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;

/**
 * Titre : Cave à vin
 * Description : Votre description
 * Copyright : Copyright (c) 2011
 * Société : Seb Informatique
 * 
 * @author Sébastien Duché
 * @version 0.1
 * @since 16/04/15
 */

public class MyCellarButton extends JButton {

	private static final long serialVersionUID = 8395284022737446765L;
	private static final Font font = new Font("Arial", 0, 12);

	public MyCellarButton() {
		setFont(font);
	}

	public MyCellarButton(Icon icon) {
		super(icon);
		setFont(font);
	}

	public MyCellarButton(String text) {
		super(text);
		setFont(font);
	}

	public MyCellarButton(Action a) {
		super(a);
		setFont(font);
	}

	public MyCellarButton(String text, Icon icon) {
		super(text, icon);
		setFont(font);
	}

}
