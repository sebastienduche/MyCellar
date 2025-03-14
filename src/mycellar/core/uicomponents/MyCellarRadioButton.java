package mycellar.core.uicomponents;

import mycellar.core.IMyCellarComponent;
import mycellar.core.text.LabelKey;
import mycellar.core.text.LabelType;
import mycellar.core.text.MyCellarLabelManagement;
import mycellar.general.IResource;

import javax.swing.JRadioButton;

import static mycellar.ProgramConstants.FONT_PANEL;

/**
 * Titre : Cave &agrave; vin
 * Description : Votre description
 * Copyright : Copyright (c) 2011
 * Soci&eacute;t&eacute; : Seb Informatique
 *
 * @author S&eacute;bastien Duch&eacute;
 * @version 1.1
 * @since 14/03/25
 */
public final class MyCellarRadioButton extends JRadioButton implements IMyCellarComponent {

  private final LabelKey labelKey;

  public MyCellarRadioButton(LabelKey labelKey, boolean selected) {
    super("", selected);
    this.labelKey = labelKey;
    updateText();
    MyCellarLabelManagement.add(this);
    setFont(FONT_PANEL);
  }

  public MyCellarRadioButton(IResource key, boolean selected) {
    super("", selected);
    LabelType labelType = LabelType.LABEL;
    labelKey = new LabelKey(labelType, key);
    updateText();
    MyCellarLabelManagement.add(this);
    setFont(FONT_PANEL);
  }

  @Override
  public void updateText() {
    MyCellarLabelManagement.updateText(this, labelKey);
  }
}
