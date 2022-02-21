package mycellar.core.uicomponents;

import mycellar.core.IMyCellarComponent;
import mycellar.core.LabelProperty;
import mycellar.core.LabelType;
import mycellar.core.MyCellarLabelManagement;

import javax.swing.JRadioButton;

import static mycellar.ProgramConstants.FONT_PANEL;

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
public final class MyCellarRadioButton extends JRadioButton implements IMyCellarComponent {

  private static final long serialVersionUID = 5420315767498997450L;

  private LabelType type;
  private String code;
  private String value;
  private LabelProperty labelProperty;

  @Deprecated
  public MyCellarRadioButton() {
    setFont(FONT_PANEL);
  }

  public MyCellarRadioButton(LabelType type, String code, boolean selected) {
    super("", selected);
    this.type = type;
    this.code = code;
    updateText();
    MyCellarLabelManagement.add(this);
    setFont(FONT_PANEL);
  }

  @Deprecated
  public MyCellarRadioButton(String text, boolean selected) {
    super(text, selected);
    setFont(FONT_PANEL);
  }

  @Override
  public void updateText() {
    MyCellarLabelManagement.updateText(this, type, code, value, labelProperty);
  }
}
