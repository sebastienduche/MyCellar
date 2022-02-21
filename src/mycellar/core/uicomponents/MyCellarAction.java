package mycellar.core.uicomponents;

import mycellar.core.IMyCellarComponent;
import mycellar.core.LabelProperty;
import mycellar.core.LabelType;
import mycellar.core.MyCellarLabelManagement;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;

import static mycellar.core.MyCellarLabelManagement.getLabel;

/**
 * Titre : Cave &agrave; vin
 * Description : Votre description
 * Copyright : Copyright (c) 2020
 * Soci&eacute;t&eacute; : Seb Informatique
 *
 * @author S&eacute;bastien Duch&eacute;
 * @version 0.3
 * @since 21/02/22
 */
public abstract class MyCellarAction extends AbstractAction implements IMyCellarComponent {

  private final LabelType textLabelType;
  private final String textLabelCode;
  private final LabelProperty textLabelProperty;

  private LabelType descriptionLabelType;
  private String descriptionLabelCode;
  private LabelProperty descriptionLabelProperty;

  @Deprecated
  private String oldDescriptionLabelCode;
  private boolean withText = true;

  public MyCellarAction(LabelType textLabelType, String textLabelCode, LabelProperty textLabelProperty) {
    this.textLabelType = textLabelType;
    this.textLabelCode = textLabelCode;
    this.textLabelProperty = textLabelProperty;
    updateText();
    MyCellarLabelManagement.add(this);
  }

  public MyCellarAction(LabelType textLabelType, String textLabelCode, LabelProperty textLabelProperty, Icon icon) {
    super("", icon);
    this.textLabelType = textLabelType;
    this.textLabelCode = textLabelCode;
    this.textLabelProperty = textLabelProperty;
    updateText();
    MyCellarLabelManagement.add(this);
  }

  public MyCellarAction(LabelType textLabelType, String textLabelCode, Icon icon) {
    this(textLabelType, textLabelCode, LabelProperty.SINGLE, icon);
  }

  public LabelType getTextLabelType() {
    return textLabelType;
  }

  public String getTextLabelCode() {
    return textLabelCode;
  }

  public LabelProperty getTextLabelProperty() {
    return textLabelProperty;
  }

  @Override
  public void setText(String text) {
    putValue(Action.NAME, withText ? text : "");
    if (oldDescriptionLabelCode != null) {
      putValue(Action.SHORT_DESCRIPTION, getLabel(oldDescriptionLabelCode, textLabelProperty));
    } else if (descriptionLabelType != null) {
      putValue(Action.SHORT_DESCRIPTION, getLabel(descriptionLabelType, descriptionLabelCode, descriptionLabelProperty));
    } else {
      putValue(Action.SHORT_DESCRIPTION, text);
    }
  }

  public void setDescriptionLabel(LabelType labelType, String labelCode, LabelProperty labelProperty) {
    descriptionLabelType = labelType;
    descriptionLabelCode = labelCode;
    descriptionLabelProperty = labelProperty;
  }

  @Deprecated
  public void setDescriptionLabelCode(String code) {
    oldDescriptionLabelCode = code;
  }

  public void setWithText(boolean withText) {
    this.withText = withText;
    if (!withText) {
      setText("");
    }
  }

  @Override
  public final void updateText() {
    MyCellarLabelManagement.updateText(this, textLabelType, textLabelCode, null, textLabelProperty);
  }

  @Override
  public MyCellarAction clone() {
    try {
      MyCellarAction clone = (MyCellarAction) super.clone();
      return clone;
    } catch (CloneNotSupportedException e) {
      throw new AssertionError();
    }
  }
}
