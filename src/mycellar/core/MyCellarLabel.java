package mycellar.core;

import javax.swing.Icon;
import javax.swing.JLabel;
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
