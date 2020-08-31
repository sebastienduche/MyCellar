package mycellar.core;

import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import java.awt.Font;
/**
 * Titre : Cave à vin
 * Description : Votre description
 * Copyright : Copyright (c) 2011
 * Société : Seb Informatique
 * 
 * @author Sébastien Duché
 * @version 0.5
 * @since 30/08/20
 */
public class MyCellarSpinner extends JSpinner {

	private static final long serialVersionUID = -6429351001334594600L;
	private static final Font FONT = new Font("Arial", Font.PLAIN, 12);
	private final SpinnerNumberModel model;


	public MyCellarSpinner(int min, int max) {
	  model = new SpinnerNumberModel(min, min, max, 1);
	  setModel(model);
		setFont(FONT);
	}

	public int getIntValue() {
	  return model.getNumber().intValue();
	}
}
