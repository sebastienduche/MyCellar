package mycellar.core.uicomponents;

import mycellar.core.IMyCellarComponent;
import mycellar.core.LabelProperty;
import mycellar.core.LabelType;
import mycellar.core.MyCellarLabelManagement;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;

import static mycellar.ProgramConstants.FONT_PANEL;

/**
 * Titre : Cave &agrave; vin
 * Description : Votre description
 * Copyright : Copyright (c) 2011
 * Soci&eacute;t&eacute; : Seb Informatique
 *
 * @author S&eacute;bastien Duch&eacute;
 * @version 0.8
 * @since 04/12/20
 */

public final class MyCellarButton extends JButton implements IMyCellarComponent {

  private static final long serialVersionUID = 8395284022737446765L;

  private LabelType type;
  private String code;
  private LabelProperty labelProperty;
  private String value;

  public MyCellarButton(Icon icon) {
    super(icon);
    setFont(FONT_PANEL);
  }

  public MyCellarButton(String text) {
    super(text);
    setFont(FONT_PANEL);
  }

  public MyCellarButton(LabelType type, String code) {
    this.type = type;
    this.code = code;
    updateText();
    MyCellarLabelManagement.add(this);
    setFont(FONT_PANEL);
  }

  public MyCellarButton(LabelType type, String code, String value) {
    this.type = type;
    this.code = code;
    this.value = value;
    updateText();
    MyCellarLabelManagement.add(this);
    setFont(FONT_PANEL);
  }

  public MyCellarButton(LabelType type, String code, LabelProperty labelProperty) {
    this.type = type;
    this.code = code;
    this.labelProperty = labelProperty;
    updateText();
    MyCellarLabelManagement.add(this);
    setFont(FONT_PANEL);
  }

  public MyCellarButton(LabelType type, String code, Action a) {
    super(a);
    this.type = type;
    this.code = code;
    updateText();
    MyCellarLabelManagement.add(this);
    setFont(FONT_PANEL);
  }

  public MyCellarButton(LabelType type, String code, LabelProperty labelProperty, Action a) {
    super(a);
    this.type = type;
    this.code = code;
    this.labelProperty = labelProperty;
    updateText();
    MyCellarLabelManagement.add(this);
    setFont(FONT_PANEL);
  }

  public MyCellarButton(LabelType type, String code, String value, Action a) {
    super(a);
    this.type = type;
    this.code = code;
    this.value = value;
    updateText();
    MyCellarLabelManagement.add(this);
    setFont(FONT_PANEL);
  }

  public MyCellarButton(String text, Icon icon) {
    super(text, icon);
    setFont(FONT_PANEL);
  }

  public MyCellarButton(LabelType type, String code, Icon icon) {
    super(icon);
    this.type = type;
    this.code = code;
    updateText();
    setFont(FONT_PANEL);
  }

  @Override
  public void updateText() {
    MyCellarLabelManagement.updateText(this, type, code, value, labelProperty);
  }

}
