package mycellar.core.uicomponents;

import mycellar.core.IMyCellarComponent;
import mycellar.core.LabelProperty;
import mycellar.core.LabelType;
import mycellar.core.MyCellarLabelManagement;

import javax.swing.JMenuItem;

/**
 * Titre : Cave &agrave; vin
 * Description : Votre description
 * Copyright : Copyright (c) 2020
 * Soci&eacute;t&eacute; : Seb Informatique
 *
 * @author S&eacute;bastien Duch&eacute;
 * @version 0.1
 * @since 04/12/20
 */
public final class MyCellarMenuItem extends JMenuItem implements IMyCellarComponent {

  private final LabelType type;
  private final String code;
  private final LabelProperty labelProperty;

  public MyCellarMenuItem(LabelType type, String code, LabelProperty labelProperty) {
    this.type = type;
    this.code = code;
    this.labelProperty = labelProperty;
    updateText();
    MyCellarLabelManagement.add(this);
  }

  public MyCellarMenuItem(MyCellarAction action) {
    super(action);
    type = action.getTextLabelType();
    code = action.getTextLabelCode();
    labelProperty = action.getTextLabelProperty();
    updateText();
    MyCellarLabelManagement.add(this);
  }

  @Override
  public void updateText() {
    MyCellarLabelManagement.updateText(this, type, code, null, labelProperty);
  }
}
