package mycellar.core;

import mycellar.Program;

import java.awt.Font;
import java.util.ArrayList;
import java.util.List;

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
 * @version 0.3
 * @since 28/08/20
 */

public class MyCellarCheckBox extends JCheckBox {

	private static final long serialVersionUID = 2584507081563652083L;
	private static final Font FONT = new Font("Arial", Font.PLAIN, 12);

	private static final List<MyCellarCheckBox> LABEL_LIST = new ArrayList<>();

	private LabelType type;
	private String code;

	public MyCellarCheckBox(Icon icon) {
		super(icon);
		setFont(FONT);
	}

	public MyCellarCheckBox(String text) {
		super(text);
		setFont(FONT);
	}

	public MyCellarCheckBox(LabelType type, String code) {
		this.type = type;
		this.code = code;
		updateText();
		LABEL_LIST.add(this);
		setFont(FONT);
	}

	public MyCellarCheckBox(Action a) {
		super(a);
		setFont(FONT);
	}

	public MyCellarCheckBox(Icon icon, boolean selected) {
		super(icon, selected);
		setFont(FONT);
	}

	public MyCellarCheckBox(String text, boolean selected) {
		super(text, selected);
		setFont(FONT);
	}

	public MyCellarCheckBox(String text, Icon icon) {
		super(text, icon);
		setFont(FONT);
	}

	public MyCellarCheckBox(String text, Icon icon, boolean selected) {
		super(text, icon, selected);
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
		LABEL_LIST.forEach(MyCellarCheckBox::updateText);
	}

}
