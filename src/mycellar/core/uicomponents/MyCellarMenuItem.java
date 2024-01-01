package mycellar.core.uicomponents;

import mycellar.core.IMyCellarComponent;
import mycellar.core.text.LabelKey;
import mycellar.core.text.MyCellarLabelManagement;

import javax.swing.JMenuItem;

/**
 * Titre : Cave &agrave; vin
 * Description : Votre description
 * Copyright : Copyright (c) 2020
 * Soci&eacute;t&eacute; : Seb Informatique
 *
 * @author S&eacute;bastien Duch&eacute;
 * @version 0.4
 * @since 06/05/22
 */
public final class MyCellarMenuItem extends JMenuItem implements IMyCellarComponent {

  private final LabelKey labelKey;

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
