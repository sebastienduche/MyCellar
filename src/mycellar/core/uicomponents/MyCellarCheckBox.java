package mycellar.core.uicomponents;

import mycellar.core.IMyCellarComponent;
import mycellar.core.LabelProperty;
import mycellar.core.LabelType;
import mycellar.core.MyCellarLabelManagement;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JCheckBox;

import static mycellar.ProgramConstants.FONT_PANEL;

/**
 * Titre : Cave &agrave; vin
 * Description : Votre description
 * Copyright : Copyright (c) 2011
 * Soci&eacute;t&eacute; : Seb Informatique
 *
 * @author S&eacute;bastien Duch&eacute;
 * @version 0.6
 * @since 18/10/20
 */

public final class MyCellarCheckBox extends JCheckBox implements IMyCellarComponent {

  private static final long serialVersionUID = 2584507081563652083L;

  private LabelType type;
  private String code;
  private String value;
  private LabelProperty labelProperty;

  public MyCellarCheckBox(Icon icon) {
    super(icon);
    setFont(FONT_PANEL);
  }

  public MyCellarCheckBox(String text) {
    super(text);
    setFont(FONT_PANEL);
  }

  public MyCellarCheckBox(LabelType type, String code) {
    this.type = type;
    this.code = code;
    updateText();
    MyCellarLabelManagement.add(this);
    setFont(FONT_PANEL);
  }

  public MyCellarCheckBox(LabelType type, String code, LabelProperty labelProperty) {
    this.type = type;
    this.code = code;
    this.labelProperty = labelProperty;
    updateText();
    MyCellarLabelManagement.add(this);
    setFont(FONT_PANEL);
  }

  public MyCellarCheckBox(LabelType type, String code, String value) {
    this.type = type;
    this.code = code;
    this.value = value;
    updateText();
    MyCellarLabelManagement.add(this);
    setFont(FONT_PANEL);
  }

  public MyCellarCheckBox(Action a) {
    super(a);
    setFont(FONT_PANEL);
  }

  public MyCellarCheckBox(Icon icon, boolean selected) {
    super(icon, selected);
    setFont(FONT_PANEL);
  }

  public MyCellarCheckBox(String text, boolean selected) {
    super(text, selected);
    setFont(FONT_PANEL);
  }

  public MyCellarCheckBox(String text, Icon icon) {
    super(text, icon);
    setFont(FONT_PANEL);
  }

  public MyCellarCheckBox(String text, Icon icon, boolean selected) {
    super(text, icon, selected);
    setFont(FONT_PANEL);
  }

  @Override
  public void updateText() {
    MyCellarLabelManagement.updateText(this, type, code, value, labelProperty);
  }

}
