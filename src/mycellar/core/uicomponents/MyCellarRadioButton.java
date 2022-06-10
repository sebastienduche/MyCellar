package mycellar.core.uicomponents;

import mycellar.core.IMyCellarComponent;
import mycellar.core.text.LabelKey;
import mycellar.core.text.LabelType;
import mycellar.core.text.MyCellarLabelManagement;

import javax.swing.JRadioButton;

import static mycellar.ProgramConstants.FONT_PANEL;
import static mycellar.ProgramConstants.KEY_TYPE;

/**
 * Titre : Cave &agrave; vin
 * Description : Votre description
 * Copyright : Copyright (c) 2011
 * Soci&eacute;t&eacute; : Seb Informatique
 *
 * @author S&eacute;bastien Duch&eacute;
 * @version 0.8
 * @since 05/05/22
 */
public final class MyCellarRadioButton extends JRadioButton implements IMyCellarComponent {

  private static final long serialVersionUID = 5420315767498997450L;

  private final LabelKey labelKey;

  public MyCellarRadioButton(String code, boolean selected) {
    super("", selected);
    LabelType labelType = LabelType.LABEL;
    if (code.startsWith(KEY_TYPE)) {
      labelType = LabelType.NONE;
      code = code.substring(KEY_TYPE.length());
    }
    labelKey = new LabelKey(labelType, code);
    updateText();
    MyCellarLabelManagement.add(this);
    setFont(FONT_PANEL);
  }

  @Override
  public void updateText() {
    MyCellarLabelManagement.updateText(this, labelKey);
  }
}
