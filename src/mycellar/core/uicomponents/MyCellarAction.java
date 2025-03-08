package mycellar.core.uicomponents;

import mycellar.core.IMyCellarComponent;
import mycellar.core.text.LabelKey;
import mycellar.core.text.LabelProperty;
import mycellar.core.text.LabelType;
import mycellar.core.text.MyCellarLabelManagement;
import mycellar.general.ResourceKey;

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
 * @version 0.8
 * @since 08/03/25
 */
public abstract class MyCellarAction extends AbstractAction implements IMyCellarComponent {

  private final LabelKey labelKey;

  private String descriptionLabelCode;
  private LabelProperty descriptionLabelProperty;

  private boolean withText = true;

  public MyCellarAction(String code, LabelProperty textLabelProperty) {
    labelKey = new LabelKey(code, textLabelProperty);
    updateText();
    MyCellarLabelManagement.add(this);
  }

  @Deprecated(since = "version90")
  public MyCellarAction(String code, LabelProperty textLabelProperty, Icon icon) {
    super("", icon);
    labelKey = new LabelKey(code, textLabelProperty);
    updateText();
    MyCellarLabelManagement.add(this);
  }

  public MyCellarAction(ResourceKey key, LabelProperty textLabelProperty, Icon icon) {
    super("", icon);
    labelKey = new LabelKey(key.getKey(), textLabelProperty);
    updateText();
    MyCellarLabelManagement.add(this);
  }

  public MyCellarAction(String code, Icon icon) {
    this(code, LabelProperty.SINGLE, icon);
  }


  public LabelKey getLabelKey() {
    return labelKey;
  }

  @Override
  public void setText(String text) {
    putValue(Action.NAME, withText ? text : "");
    putValue(Action.SHORT_DESCRIPTION, getLabel(LabelType.LABEL, descriptionLabelCode, descriptionLabelProperty, null));
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
      return (MyCellarAction) super.clone();
    } catch (CloneNotSupportedException e) {
      throw new AssertionError();
    }
  }
}
