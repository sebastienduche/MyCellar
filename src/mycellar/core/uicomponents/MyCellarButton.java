package mycellar.core.uicomponents;

import mycellar.core.IMyCellarComponent;
import mycellar.core.text.LabelKey;
import mycellar.core.text.MyCellarLabelManagement;
import mycellar.general.IResource;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;

import static mycellar.ProgramConstants.FONT_PANEL;

/**
 * Titre : Cave &agrave; vin
 * Description : Votre description
 * Copyright : Copyright (c) 2011
 * Soci&eacute;t&eacute; : Seb Informatique
 *
 * @author S&eacute;bastien Duch&eacute;
 * @version 1.5
 * @since 18/03/25
 */

public final class MyCellarButton extends JButton implements IMyCellarComponent {

  private LabelKey labelKey;

  public MyCellarButton(Icon icon) {
    super(icon);
    setFont(FONT_PANEL);
  }

  public MyCellarButton(IResource code) {
    labelKey = new LabelKey(code);
    updateText();
    MyCellarLabelManagement.add(this);
    setFont(FONT_PANEL);
  }

  public MyCellarButton(IResource key, Action a) {
    super(a);
    labelKey = new LabelKey(key);
    updateText();
    MyCellarLabelManagement.add(this);
    setFont(FONT_PANEL);
  }

  public MyCellarButton(IResource key, Icon icon) {
    super(icon);
    labelKey = new LabelKey(key);
    updateText();
    setFont(FONT_PANEL);
  }

  @Override
  public void updateText() {
    MyCellarLabelManagement.updateText(this, labelKey);
  }

}
