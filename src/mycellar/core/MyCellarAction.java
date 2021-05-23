package mycellar.core;

import mycellar.Program;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;

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
public abstract class MyCellarAction extends AbstractAction implements IMyCellarComponent {

  private final LabelType type;
  private final String code;
  private final LabelProperty labelProperty;
  private String descriptionLabelCode;

  public MyCellarAction(LabelType type, String code, LabelProperty labelProperty) {
    this.type = type;
    this.code = code;
    this.labelProperty = labelProperty;
    updateText();
    MyCellarLabelManagement.add(this);
  }

  public MyCellarAction(LabelType type, String code, LabelProperty labelProperty, Icon icon) {
    super("", icon);
    this.type = type;
    this.code = code;
    this.labelProperty = labelProperty;
    updateText();
    MyCellarLabelManagement.add(this);
  }

  public LabelType getType() {
    return type;
  }

  public String getCode() {
    return code;
  }

  public LabelProperty getLabelProperty() {
    return labelProperty;
  }

  @Override
  public void setText(String text) {
    putValue(Action.NAME, text);
    putValue(Action.SHORT_DESCRIPTION, descriptionLabelCode != null ? Program.getLabel(descriptionLabelCode) : text);
  }

  public void setDescriptionLabelCode(String code) {
    descriptionLabelCode = code;
  }

  @Override
  public final void updateText() {
    MyCellarLabelManagement.updateText(this, type, code, null, labelProperty);
  }
}
