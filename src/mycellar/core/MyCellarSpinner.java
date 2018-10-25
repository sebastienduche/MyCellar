package mycellar.core;

import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import java.awt.Font;
/**
 * Titre : Cave à vin
 * Description : Votre description
 * Copyright : Copyright (c) 2011
 * Société : Seb Informatique
 * 
 * @author Sébastien Duché
 * @version 0.3
 * @since 25/10/18
 */
public class MyCellarSpinner extends JSpinner {

	private static final long serialVersionUID = -6429351001334594600L;
	private static final Font FONT = new Font("Arial", Font.PLAIN, 12);


	public MyCellarSpinner() {
		setFont(FONT);
	}

	public MyCellarSpinner(SpinnerModel model) {
		super(model);
		setFont(FONT);
	}

	public Integer getIntValue() {
		return (Integer) getValue();
	}
}
