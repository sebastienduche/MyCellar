package mycellar.core;

import java.awt.Font;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JRadioButton;
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
public class MyCellarRadioButton extends JRadioButton {

	private static final long serialVersionUID = 5420315767498997450L;
	private static final Font font = new Font("Arial", 0, 12);


	public MyCellarRadioButton() {
		setFont(font);
	}

	public MyCellarRadioButton(Icon icon) {
		super(icon);
		setFont(font);
	}

	public MyCellarRadioButton(Action a) {
		super(a);
		setFont(font);
	}

	public MyCellarRadioButton(String text) {
		super(text);
		setFont(font);
	}

	public MyCellarRadioButton(Icon icon, boolean selected) {
		super(icon, selected);
		setFont(font);
	}

	public MyCellarRadioButton(String text, boolean selected) {
		super(text, selected);
		setFont(font);
	}

	public MyCellarRadioButton(String text, Icon icon) {
		super(text, icon);
		setFont(font);
	}

	public MyCellarRadioButton(String text, Icon icon, boolean selected) {
		super(text, icon, selected);
		setFont(font);
	}

}
