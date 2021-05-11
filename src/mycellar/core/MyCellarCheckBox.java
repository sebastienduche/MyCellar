package mycellar.core;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JCheckBox;
import java.awt.Font;

/**
 * Titre : Cave à vin
 * Description : Votre description
 * Copyright : Copyright (c) 2011
 * Société : Seb Informatique
 *
 * @author Sébastien Duché
 * @version 0.6
 * @since 18/10/20
 */

public final class MyCellarCheckBox extends JCheckBox implements IMyCellarComponent {

	private static final long serialVersionUID = 2584507081563652083L;
	private static final Font FONT = new Font("Arial", Font.PLAIN, 12);

	private LabelType type;
	private String code;
	private String value;
	private LabelProperty labelProperty;

	public MyCellarCheckBox(Icon icon) {
		super(icon);
		setFont(FONT);
	}

	public MyCellarCheckBox(String text) {
		super(text);
		setFont(FONT);
	}

	public MyCellarCheckBox(LabelType type, String code) {
		this.type = type;
		this.code = code;
		updateText();
		MyCellarLabelManagement.add(this);
		setFont(FONT);
	}

	public MyCellarCheckBox(LabelType type, String code, LabelProperty labelProperty) {
		this.type = type;
		this.code = code;
		this.labelProperty = labelProperty;
		updateText();
		MyCellarLabelManagement.add(this);
		setFont(FONT);
	}

	public MyCellarCheckBox(LabelType type, String code, String value) {
		this.type = type;
		this.code = code;
		this.value = value;
		updateText();
		MyCellarLabelManagement.add(this);
		setFont(FONT);
	}

	public MyCellarCheckBox(Action a) {
		super(a);
		setFont(FONT);
	}

	public MyCellarCheckBox(Icon icon, boolean selected) {
		super(icon, selected);
		setFont(FONT);
	}

	public MyCellarCheckBox(String text, boolean selected) {
		super(text, selected);
		setFont(FONT);
	}

	public MyCellarCheckBox(String text, Icon icon) {
		super(text, icon);
		setFont(FONT);
	}

	public MyCellarCheckBox(String text, Icon icon, boolean selected) {
		super(text, icon, selected);
		setFont(FONT);
	}

	@Override
	public void updateText() {
		MyCellarLabelManagement.updateText(this, type, code, value, labelProperty);
	}

}
