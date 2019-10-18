package mycellar.core;

import mycellar.Program;

import javax.swing.Icon;
import javax.swing.JLabel;
import java.awt.Font;

import static mycellar.core.LabelType.INFO;

/**
 * Titre : Cave à vin
 * Description : Votre description
 * Copyright : Copyright (c) 2011
 * Société : Seb Informatique
 * 
 * @author Sébastien Duché
 * @version 0.3
 * @since 18/10/19
 */

public class MyCellarLabel extends JLabel {

	private static final long serialVersionUID = 4972622436840497820L;
	private static final Font FONT = new Font("Arial", Font.PLAIN, 12);

	public MyCellarLabel() {
		setFont(FONT);
	}

	public MyCellarLabel(String text) {
		super(text);
		setFont(FONT);
	}

	public MyCellarLabel(LabelType type, String code) {
		super(type == INFO ? Program.getLabel("Infos" + code) : Program.getLabel("Errors" + code));
		setFont(FONT);
	}

	public MyCellarLabel(Icon image) {
		super(image);
		setFont(FONT);
	}

	public MyCellarLabel(String text, int horizontalAlignment) {
		super(text, horizontalAlignment);
		setFont(FONT);
	}

	public MyCellarLabel(Icon image, int horizontalAlignment) {
		super(image, horizontalAlignment);
		setFont(FONT);
	}

	public MyCellarLabel(String text, Icon icon, int horizontalAlignment) {
		super(text, icon, horizontalAlignment);
		setFont(FONT);
	}

}
