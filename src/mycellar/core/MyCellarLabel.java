package mycellar.core;

import mycellar.Program;

import javax.swing.Icon;
import javax.swing.JLabel;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;

/**
 * Titre : Cave à vin
 * Description : Votre description
 * Copyright : Copyright (c) 2011
 * Société : Seb Informatique
 * 
 * @author Sébastien Duché
 * @version 0.4
 * @since 01/11/19
 */

public class MyCellarLabel extends JLabel {

	private static final long serialVersionUID = 4972622436840497820L;
	private static final Font FONT = new Font("Arial", Font.PLAIN, 12);

	private static final List<MyCellarLabel> LABEL_LIST = new ArrayList<>();

	private LabelType type;
	private String code;

	@Deprecated
	public MyCellarLabel() {
		setFont(FONT);
	}

	public MyCellarLabel(String text) {
		super(text);
		setFont(FONT);
	}

	public MyCellarLabel(LabelType type, String code) {
		this.type = type;
		this.code = code;
		updateText();
		LABEL_LIST.add(this);
		setFont(FONT);
	}

	public MyCellarLabel(Icon image) {
		super(image);
		setFont(FONT);
	}

	@Deprecated
	public MyCellarLabel(String text, int horizontalAlignment) {
		super(text, horizontalAlignment);
		setFont(FONT);
	}

	public MyCellarLabel(Icon image, int horizontalAlignment) {
		super(image, horizontalAlignment);
		setFont(FONT);
	}

	@Deprecated
	public MyCellarLabel(String text, Icon icon, int horizontalAlignment) {
		super(text, icon, horizontalAlignment);
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
		LABEL_LIST.forEach(MyCellarLabel::updateText);
	}
}
