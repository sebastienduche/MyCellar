package mycellar.core;

import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;
import java.awt.Font;

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
public class MyCellarComboBox<T> extends JComboBox<T> {

	private static final long serialVersionUID = -1622264730055596931L;
	private static final Font FONT = new Font("Arial", Font.PLAIN, 12);

	public MyCellarComboBox() {
		setFont(FONT);
	}

	public MyCellarComboBox(ComboBoxModel<T> aModel) {
		super(aModel);
		setFont(FONT);
	}
}
