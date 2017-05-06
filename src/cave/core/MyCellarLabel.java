package Cave.core;

import java.awt.Font;

import javax.swing.Icon;
import javax.swing.JLabel;

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

public class MyCellarLabel extends JLabel {

	private static final long serialVersionUID = 4972622436840497820L;
	private static final Font font = new Font("Arial", 0, 12);

	public MyCellarLabel() {
		setFont(font);
	}

	public MyCellarLabel(String text) {
		super(text);
		setFont(font);
	}

	public MyCellarLabel(Icon image) {
		super(image);
		setFont(font);
	}

	public MyCellarLabel(String text, int horizontalAlignment) {
		super(text, horizontalAlignment);
		setFont(font);
	}

	public MyCellarLabel(Icon image, int horizontalAlignment) {
		super(image, horizontalAlignment);
		setFont(font);
	}

	public MyCellarLabel(String text, Icon icon, int horizontalAlignment) {
		super(text, icon, horizontalAlignment);
		setFont(font);
	}

}
