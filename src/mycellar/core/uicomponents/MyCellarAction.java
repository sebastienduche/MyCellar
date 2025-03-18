package mycellar.core.uicomponents;

import mycellar.core.IMyCellarComponent;
import mycellar.core.text.LabelKey;
import mycellar.core.text.MyCellarLabelManagement;
import mycellar.general.IResource;
import mycellar.general.ResourceKey;

import javax.swing.AbstractAction;
import javax.swing.Icon;

import static mycellar.core.text.MyCellarLabelManagement.getLabel;

/**
 * Titre : Cave &agrave; vin
 * Description : Votre description
 * Copyright : Copyright (c) 2020
 * Soci&eacute;t&eacute; : Seb Informatique
 *
 * @author S&eacute;bastien Duch&eacute;
 * @version 1.0
 * @since 18/03/25
 */
public abstract class MyCellarAction extends AbstractAction implements IMyCellarComponent {

  private final LabelKey labelKey;

  private IResource resource;

  private boolean withText = true;

  public MyCellarAction(ResourceKey key) {
    labelKey = new LabelKey(key);
    updateText();
    MyCellarLabelManagement.add(this);
  }

  public MyCellarAction(ResourceKey key, Icon icon) {
    super("", icon);
    labelKey = new LabelKey(key);
    updateText();
    MyCellarLabelManagement.add(this);
  }

  LabelKey getLabelKey() {
    return labelKey;
  }

  @Override
  public void setText(String text) {
    putValue(NAME, withText ? text : "");
    putValue(SHORT_DESCRIPTION, getLabel(resource));
  }

  protected void setDescriptionLabel(ResourceKey key) {
    resource = key;
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
