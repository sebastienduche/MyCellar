package mycellar.core;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JRadioButton;
import java.awt.Font;
/**
 * Titre : Cave à vin
 * Description : Votre description
 * Copyright : Copyright (c) 2011
 * Société : Seb Informatique
 * 
 * @author Sébastien Duché
 * @version 0.2
 * @since 08/06/18
 */
public class MyCellarRadioButton extends JRadioButton {

	private static final long serialVersionUID = 5420315767498997450L;
	private static final Font FONT = new Font("Arial", Font.PLAIN, 12);


	public MyCellarRadioButton() {
		setFont(FONT);
	}

	public MyCellarRadioButton(Icon icon) {
		super(icon);
		setFont(FONT);
	}

	public MyCellarRadioButton(Action a) {
		super(a);
		setFont(FONT);
	}

	public MyCellarRadioButton(String text) {
		super(text);
		setFont(FONT);
	}

	public MyCellarRadioButton(Icon icon, boolean selected) {
		super(icon, selected);
		setFont(FONT);
	}

	public MyCellarRadioButton(String text, boolean selected) {
		super(text, selected);
		setFont(FONT);
	}

	public MyCellarRadioButton(String text, Icon icon) {
		super(text, icon);
		setFont(FONT);
	}

	public MyCellarRadioButton(String text, Icon icon, boolean selected) {
		super(text, icon, selected);
		setFont(FONT);
	}

}
