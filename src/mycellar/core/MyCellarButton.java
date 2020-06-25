package mycellar.core;

import mycellar.Program;

import java.awt.Font;
import java.util.ArrayList;
import java.util.List;

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
 * @version 0.3
 * @since 01/11/19
 */

public class MyCellarButton extends JButton {

	private static final long serialVersionUID = 8395284022737446765L;
	private static final Font FONT = new Font("Arial", Font.PLAIN, 12);

	private static final List<MyCellarButton> LABEL_LIST = new ArrayList<>();

	private LabelType type;
	private String code;

	@Deprecated
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
		this.type = type;
		this.code = code;
		updateText();
		LABEL_LIST.add(this);
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
		super(icon);
		this.type = type;
		this.code = code;
		updateText();
		setFont(FONT);
	}

	private void updateText() {
		switch (type) {
			case INFO:
				setText(Program.getLabel("Infos" + code));
				break;
			case ERROR:
				setText(Program.getError("Errors" + code));
				break;
			case INFO_OTHER:
				setText(Program.getLabel(code));
				break;
			case ERROR_OTHER:
				setText(Program.getError(code));
				break;
		}
	}

	public static void updateLabels() {
		LABEL_LIST.forEach(MyCellarButton::updateText);
	}

}
