package Cave.core;

import java.awt.Font;

import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
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
public class MyCellarSpinner extends JSpinner {

	private static final long serialVersionUID = -6429351001334594600L;
	private static final Font font = new Font("Arial", 0, 12);


	public MyCellarSpinner() {
		setFont(font);
	}

	public MyCellarSpinner(SpinnerModel model) {
		super(model);
		setFont(font);
	}

}
