package mycellar.core;

import java.awt.Font;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;

/**
 * Titre : Cave à vin
 * Description : Votre description
 * Copyright : Copyright (c) 2011
 * Société : Seb Informatique
 * 
 * @author Sébastien Duché
 * @version 0.7
 * @since 16/10/20
 */

public class MyCellarButton extends JButton implements IMyCellarComponent {

	private static final long serialVersionUID = 8395284022737446765L;
	private static final Font FONT = new Font("Arial", Font.PLAIN, 12);

	private LabelType type;
	private String code;
	private LabelProperty labelProperty;
	private String value;

	public MyCellarButton(Icon icon) {
		super(icon);
		setFont(FONT);
	}

	public MyCellarButton(String text) {
		super(text);
		setFont(FONT);
	}

	public MyCellarButton(LabelType type, String code) {
		this.type = type;
		this.code = code;
		updateText();
		MyCellarLabelManagement.add(this);
		setFont(FONT);
	}
	
	 public MyCellarButton(LabelType type, String code, String value) {
	    this.type = type;
	    this.code = code;
	    this.value = value;
	    updateText();
	    MyCellarLabelManagement.add(this);
	    setFont(FONT);
	  }

	public MyCellarButton(LabelType type, String code, Action a) {
		super(a);
		this.type = type;
    this.code = code;
		updateText();
		MyCellarLabelManagement.add(this);
		setFont(FONT);
	}

	public MyCellarButton(LabelType type, String code, LabelProperty labelProperty, Action a) {
		super(a);
		this.type = type;
		this.code = code;
		this.labelProperty = labelProperty;
		updateText();
		MyCellarLabelManagement.add(this);
		setFont(FONT);
	}
	
	public MyCellarButton(LabelType type, String code, String value, Action a) {
    super(a);
    this.type = type;
    this.code = code;
    this.value = value;
    updateText();
    MyCellarLabelManagement.add(this);
    setFont(FONT);
  }

	public MyCellarButton(String text, Icon icon) {
		super(text, icon);
		setFont(FONT);
	}

	public MyCellarButton(LabelType type, String code, Icon icon) {
		super(icon);
		this.type = type;
		this.code = code;
		updateText();
		setFont(FONT);
	}

	@Override
	public void updateText() {
     MyCellarLabelManagement.updateText(this, type, code, value, labelProperty);
	}

}
