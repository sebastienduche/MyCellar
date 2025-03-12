package mycellar.core.uicomponents;

import mycellar.core.IMyCellarComponent;
import mycellar.core.text.LabelKey;
import mycellar.core.text.LabelType;
import mycellar.core.text.MyCellarLabelManagement;
import mycellar.general.IResource;

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
 * @version 0.9
 * @since 12/03/25
 */
public final class MyCellarRadioButton extends JRadioButton implements IMyCellarComponent {

  private final LabelKey labelKey;

  @Deprecated(since = "version90")
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

  public MyCellarRadioButton(IResource key, boolean selected) {
    super("", selected);
    LabelType labelType = LabelType.LABEL;
    labelKey = new LabelKey(labelType, key.getKey());
    updateText();
    MyCellarLabelManagement.add(this);
    setFont(FONT_PANEL);
  }

  @Override
  public void updateText() {
    MyCellarLabelManagement.updateText(this, labelKey);
  }
}
