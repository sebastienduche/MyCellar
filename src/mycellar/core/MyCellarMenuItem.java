package mycellar.core;

import javax.swing.JMenuItem;

/**
 * Titre : Cave à vin
 * Description : Votre description
 * Copyright : Copyright (c) 2020
 * Société : Seb Informatique
 *
 * @author Sébastien Duché
 * @version 0.1
 * @since 04/12/20
 */
public class MyCellarMenuItem extends JMenuItem implements IMyCellarComponent {

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
    type = action.getType();
    code = action.getCode();
    labelProperty = action.getLabelProperty();
    updateText();
    MyCellarLabelManagement.add(this);
  }

  @Override
  public void updateText() {
    MyCellarLabelManagement.updateText(this, type, code, null, labelProperty);
  }
}
