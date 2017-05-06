package Cave.core;

import java.awt.Font;
import java.util.Vector;

import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;

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
	private static final Font font = new Font("Arial", 0, 12);

	public MyCellarComboBox() {
		setFont(font);
	}

	public MyCellarComboBox(ComboBoxModel<T> aModel) {
		super(aModel);
		setFont(font);
	}

	@SuppressWarnings("unused")
	private MyCellarComboBox(Object[] items) {
	}

	public MyCellarComboBox(Vector<T> items) {
		super(items);
		setFont(font);
	}

}
