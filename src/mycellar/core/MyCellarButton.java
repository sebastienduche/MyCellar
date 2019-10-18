package mycellar.core;

import mycellar.Program;

import java.awt.Font;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;

import static mycellar.core.LabelType.INFO;

/**
 * Titre : Cave à vin
 * Description : Votre description
 * Copyright : Copyright (c) 2011
 * Société : Seb Informatique
 * 
 * @author Sébastien Duché
 * @version 0.2
 * @since 18/10/19
 */

public class MyCellarButton extends JButton {

	private static final long serialVersionUID = 8395284022737446765L;
	private static final Font FONT = new Font("Arial", Font.PLAIN, 12);

	public MyCellarButton() {
		setFont(FONT);
	}

	public MyCellarButton(Icon icon) {
		super(icon);
		setFont(FONT);
	}

	public MyCellarButton(String text) {
		super(text);
		setFont(FONT);
	}

	public MyCellarButton(LabelType type, String code) {
		super(type == INFO ? Program.getLabel("Infos" + code) : Program.getLabel("Errors" + code));
		setFont(FONT);
	}

	public MyCellarButton(Action a) {
		super(a);
		setFont(FONT);
	}

	public MyCellarButton(String text, Icon icon) {
		super(text, icon);
		setFont(FONT);
	}

	public MyCellarButton(LabelType type, String code, Icon icon) {
		super(type == INFO ? Program.getLabel("Infos" + code) : Program.getLabel("Errors" + code), icon);
		setFont(FONT);
	}

}
