package mycellar.core;

import java.awt.Font;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JCheckBox;

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

public class MyCellarCheckBox extends JCheckBox {

	private static final long serialVersionUID = 2584507081563652083L;
	private static final Font font = new Font("Arial", 0, 12);


	public MyCellarCheckBox() {
		setFont(font);
	}

	public MyCellarCheckBox(Icon icon) {
		super(icon);
		setFont(font);
	}

	public MyCellarCheckBox(String text) {
		super(text);
		setFont(font);
	}

	public MyCellarCheckBox(Action a) {
		super(a);
		setFont(font);
	}

	public MyCellarCheckBox(Icon icon, boolean selected) {
		super(icon, selected);
		setFont(font);
	}

	public MyCellarCheckBox(String text, boolean selected) {
		super(text, selected);
		setFont(font);
	}

	public MyCellarCheckBox(String text, Icon icon) {
		super(text, icon);
		setFont(font);
	}

	public MyCellarCheckBox(String text, Icon icon, boolean selected) {
		super(text, icon, selected);
		setFont(font);
	}

}
