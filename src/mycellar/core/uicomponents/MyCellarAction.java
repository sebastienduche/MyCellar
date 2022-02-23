package mycellar.core.uicomponents;

import mycellar.core.IMyCellarComponent;
import mycellar.core.text.LabelKey;
import mycellar.core.text.LabelProperty;
import mycellar.core.text.LabelType;
import mycellar.core.text.MyCellarLabelManagement;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;

import static mycellar.core.text.MyCellarLabelManagement.getLabel;

/**
 * Titre : Cave &agrave; vin
 * Description : Votre description
 * Copyright : Copyright (c) 2020
 * Soci&eacute;t&eacute; : Seb Informatique
 *
 * @author S&eacute;bastien Duch&eacute;
 * @version 0.4
 * @since 22/02/22
 */
public abstract class MyCellarAction extends AbstractAction implements IMyCellarComponent {

  private final LabelKey labelKey;

  private LabelType descriptionLabelType;
  private String descriptionLabelCode;
  private LabelProperty descriptionLabelProperty;

  private boolean withText = true;

  public MyCellarAction(LabelType textLabelType, String textLabelCode, LabelProperty textLabelProperty) {
    labelKey = new LabelKey(textLabelType, textLabelCode, textLabelProperty);
    updateText();
    MyCellarLabelManagement.add(this);
  }

  public MyCellarAction(LabelType textLabelType, String textLabelCode, LabelProperty textLabelProperty, Icon icon) {
    super("", icon);
    labelKey = new LabelKey(textLabelType, textLabelCode, textLabelProperty);
    updateText();
    MyCellarLabelManagement.add(this);
  }

  public MyCellarAction(LabelType textLabelType, String textLabelCode, Icon icon) {
    this(textLabelType, textLabelCode, LabelProperty.SINGLE, icon);
  }


  public LabelKey getLabelKey() {
    return labelKey;
  }

  @Override
  public void setText(String text) {
    putValue(Action.NAME, withText ? text : "");
    if (descriptionLabelType != null) {
      putValue(Action.SHORT_DESCRIPTION, getLabel(new LabelKey(descriptionLabelType, descriptionLabelCode, descriptionLabelProperty)));
    } else {
      putValue(Action.SHORT_DESCRIPTION, text);
    }
  }

  public void setDescriptionLabel(LabelType labelType, String labelCode) {
    setDescriptionLabel(labelType, labelCode, null);
  }

  public void setDescriptionLabel(LabelType labelType, String labelCode, LabelProperty labelProperty) {
    descriptionLabelType = labelType;
    descriptionLabelCode = labelCode;
    descriptionLabelProperty = labelProperty;
  }

  public void setWithText(boolean withText) {
    this.withText = withText;
    if (!withText) {
      setText("");
    }
  }

  @Override
  public final void updateText() {
    MyCellarLabelManagement.updateText(this, labelKey);
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
