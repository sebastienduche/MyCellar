package mycellar.core.uicomponents;

import mycellar.core.IMyCellarComponent;
import mycellar.core.text.LabelKey;
import mycellar.core.text.LabelProperty;
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
 * @version 0.6
 * @since 05/05/22
 */
public abstract class MyCellarAction extends AbstractAction implements IMyCellarComponent {

  private static final long serialVersionUID = -6495907213999756931L;

  private final LabelKey labelKey;

  private String descriptionLabelCode;
  private LabelProperty descriptionLabelProperty;

  private boolean withText = true;

  public MyCellarAction(String textLabelCode, LabelProperty textLabelProperty) {
    labelKey = new LabelKey(textLabelCode, textLabelProperty);
    updateText();
    MyCellarLabelManagement.add(this);
  }

  public MyCellarAction(String textLabelCode, LabelProperty textLabelProperty, Icon icon) {
    super("", icon);
    labelKey = new LabelKey(textLabelCode, textLabelProperty);
    updateText();
    MyCellarLabelManagement.add(this);
  }

  public MyCellarAction(String textLabelCode, Icon icon) {
    this(textLabelCode, LabelProperty.SINGLE, icon);
  }


  public LabelKey getLabelKey() {
    return labelKey;
  }

  @Override
  public void setText(String text) {
    putValue(Action.NAME, withText ? text : "");
    putValue(Action.SHORT_DESCRIPTION, getLabel(new LabelKey(descriptionLabelCode, descriptionLabelProperty)));
  }

  public void setDescriptionLabel(String labelCode) {
    setDescriptionLabel(labelCode, null);
  }

  public void setDescriptionLabel(String labelCode, LabelProperty labelProperty) {
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
