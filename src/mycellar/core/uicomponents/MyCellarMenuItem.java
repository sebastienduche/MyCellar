package mycellar.core.uicomponents;

import mycellar.core.IMyCellarComponent;
import mycellar.core.text.LabelKey;
import mycellar.core.text.LabelProperty;
import mycellar.core.text.MyCellarLabelManagement;

import javax.swing.JMenuItem;

/**
 * Titre : Cave &agrave; vin
 * Description : Votre description
 * Copyright : Copyright (c) 2020
 * Soci&eacute;t&eacute; : Seb Informatique
 *
 * @author S&eacute;bastien Duch&eacute;
 * @version 0.3
 * @since 05/05/22
 */
public final class MyCellarMenuItem extends JMenuItem implements IMyCellarComponent {

  private static final long serialVersionUID = -5930082155200496901L;
  private final LabelKey labelKey;

  public MyCellarMenuItem(String code, LabelProperty labelProperty) {
    labelKey = new LabelKey(code, labelProperty);
    updateText();
    MyCellarLabelManagement.add(this);
  }

  public MyCellarMenuItem(MyCellarAction action) {
    super(action);
    labelKey = action.getLabelKey();
    updateText();
    MyCellarLabelManagement.add(this);
  }

  @Override
  public void updateText() {
    MyCellarLabelManagement.updateText(this, labelKey);
  }
}
