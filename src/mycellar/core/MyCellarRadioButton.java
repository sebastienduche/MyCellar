package mycellar.core;

import javax.swing.JRadioButton;
import java.awt.Font;

/**
 * Titre : Cave &agrave; vin
 * Description : Votre description
 * Copyright : Copyright (c) 2011
 * Soci&eacute;t&eacute; : Seb Informatique
 *
 * @author S&eacute;bastien Duch&eacute;
 * @version 0.5
 * @since 16/10/20
 */
public class MyCellarRadioButton extends JRadioButton implements IMyCellarComponent {

	private static final long serialVersionUID = 5420315767498997450L;
	private static final Font FONT = new Font("Arial", Font.PLAIN, 12);

	private LabelType type;
	private String code;
	private String value;
	private LabelProperty labelProperty;

	@Deprecated
	public MyCellarRadioButton() {
		setFont(FONT);
	}

	public MyCellarRadioButton(LabelType type, String code, boolean selected) {
		super("", selected);
		this.type = type;
		this.code = code;
		updateText();
		MyCellarLabelManagement.add(this);
		setFont(FONT);
	}

	@Deprecated
	public MyCellarRadioButton(String text, boolean selected) {
		super(text, selected);
		setFont(FONT);
	}

	@Override
	public void updateText() {
	  MyCellarLabelManagement.updateText(this, type, code, value, labelProperty);
	}
}
