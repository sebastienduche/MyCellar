package mycellar.core;

import mycellar.Program;

import javax.swing.JRadioButton;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;

/**
 * Titre : Cave &agrave; vin
 * Description : Votre description
 * Copyright : Copyright (c) 2011
 * Soci&eacute;t&eacute; : Seb Informatique
 *
 * @author S&eacute;bastien Duch&eacute;
 * @version 0.3
 * @since 01/11/19
 */
public class MyCellarRadioButton extends JRadioButton {

	private static final long serialVersionUID = 5420315767498997450L;
	private static final Font FONT = new Font("Arial", Font.PLAIN, 12);

	private static final List<MyCellarRadioButton> LABEL_LIST = new ArrayList<>();

	private LabelType type;
	private String code;

	@Deprecated
	public MyCellarRadioButton() {
		setFont(FONT);
	}

	public MyCellarRadioButton(LabelType type, String code, boolean selected) {
		super("", selected);
		this.type = type;
		this.code = code;
		updateText();
		LABEL_LIST.add(this);
		setFont(FONT);
	}

	public MyCellarRadioButton(String text, boolean selected) {
		super(text, selected);
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
		LABEL_LIST.forEach(MyCellarRadioButton::updateText);
	}
}
