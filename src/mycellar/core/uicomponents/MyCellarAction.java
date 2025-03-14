package mycellar.core.uicomponents;

import mycellar.core.IMyCellarComponent;
import mycellar.core.text.LabelKey;
import mycellar.core.text.LabelProperty;
import mycellar.core.text.LabelType;
import mycellar.core.text.MyCellarLabelManagement;
import mycellar.general.IResource;
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
 * @version 0.9
 * @since 14/03/25
 */
public abstract class MyCellarAction extends AbstractAction implements IMyCellarComponent {

  private final LabelKey labelKey;

  private IResource descriptionResource;
  private LabelProperty descriptionLabelProperty;

  private boolean withText = true;

  public MyCellarAction(ResourceKey key, LabelProperty textLabelProperty) {
    labelKey = new LabelKey(key, textLabelProperty);
    updateText();
    MyCellarLabelManagement.add(this);
  }

  public MyCellarAction(ResourceKey key, LabelProperty textLabelProperty, Icon icon) {
    super("", icon);
    labelKey = new LabelKey(key, textLabelProperty);
    updateText();
    MyCellarLabelManagement.add(this);
  }

  public MyCellarAction(ResourceKey key, Icon icon) {
    this(key, LabelProperty.SINGLE, icon);
  }

  LabelKey getLabelKey() {
    return labelKey;
  }

  @Override
  public void setText(String text) {
    putValue(NAME, withText ? text : "");
    putValue(SHORT_DESCRIPTION, getLabel(LabelType.LABEL, descriptionResource, descriptionLabelProperty, null));
  }

  protected void setDescriptionLabel(ResourceKey key) {
    setDescriptionLabel(key, null);
  }

  protected void setDescriptionLabel(ResourceKey key, LabelProperty labelProperty) {
    descriptionResource = key;
    descriptionLabelProperty = labelProperty;
  }

  protected void setWithText(boolean withText) {
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
