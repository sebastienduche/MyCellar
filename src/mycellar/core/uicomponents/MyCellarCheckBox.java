package mycellar.core.uicomponents;

import mycellar.core.IMyCellarComponent;
import mycellar.core.text.LabelKey;
import mycellar.core.text.LabelProperty;
import mycellar.core.text.MyCellarLabelManagement;
import mycellar.general.IResource;
import mycellar.general.ResourceKey;

import javax.swing.JCheckBox;

import static mycellar.ProgramConstants.FONT_PANEL;

/**
 * Titre : Cave &agrave; vin
 * Description : Votre description
 * Copyright : Copyright (c) 2011
 * Soci&eacute;t&eacute; : Seb Informatique
 *
 * @author S&eacute;bastien Duch&eacute;
 * @version 1.3
 * @since 14/03/25
 */

public final class MyCellarCheckBox extends JCheckBox implements IMyCellarComponent {

  private LabelKey labelKey;

  public MyCellarCheckBox() {
    super("");
    setFont(FONT_PANEL);
  }

  public MyCellarCheckBox(ResourceKey key) {
    labelKey = new LabelKey(key);
    updateText();
    MyCellarLabelManagement.add(this);
    setFont(FONT_PANEL);
  }

  public MyCellarCheckBox(IResource key, boolean selected) {
    super("", selected);
    labelKey = new LabelKey(key);
    updateText();
    MyCellarLabelManagement.add(this);
    setFont(FONT_PANEL);
  }

  public MyCellarCheckBox(IResource key, LabelProperty labelProperty) {
    labelKey = new LabelKey(key, labelProperty);
    updateText();
    MyCellarLabelManagement.add(this);
    setFont(FONT_PANEL);
  }

  @Override
  public void updateText() {
    MyCellarLabelManagement.updateText(this, labelKey);
  }

}
