package mycellar.core.uicomponents;

import mycellar.core.IMyCellarComponent;
import mycellar.core.text.LabelKey;
import mycellar.core.text.LabelType;
import mycellar.core.text.MyCellarLabelManagement;

import javax.swing.JRadioButton;

import static mycellar.ProgramConstants.FONT_PANEL;

/**
 * Titre : Cave &agrave; vin
 * Description : Votre description
 * Copyright : Copyright (c) 2011
 * Soci&eacute;t&eacute; : Seb Informatique
 *
 * @author S&eacute;bastien Duch&eacute;
 * @version 0.6
 * @since 22/02/22
 */
public final class MyCellarRadioButton extends JRadioButton implements IMyCellarComponent {

  private static final long serialVersionUID = 5420315767498997450L;

  private LabelKey labelKey;

  @Deprecated
  public MyCellarRadioButton() {
    setFont(FONT_PANEL);
  }

  public MyCellarRadioButton(LabelType type, String code, boolean selected) {
    super("", selected);
    labelKey = new LabelKey(type, code);
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
    MyCellarLabelManagement.updateText(this, labelKey);
  }
}
