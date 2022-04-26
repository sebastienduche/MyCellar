package mycellar.core.uicomponents;

import mycellar.core.IMyCellarComponent;
import mycellar.core.text.LabelKey;
import mycellar.core.text.LabelProperty;
import mycellar.core.text.MyCellarLabelManagement;

import javax.swing.Icon;
import javax.swing.JCheckBox;

import static mycellar.ProgramConstants.FONT_PANEL;
import static mycellar.core.text.LabelType.INFO_OTHER;

/**
 * Titre : Cave &agrave; vin
 * Description : Votre description
 * Copyright : Copyright (c) 2011
 * Soci&eacute;t&eacute; : Seb Informatique
 *
 * @author S&eacute;bastien Duch&eacute;
 * @version 0.8
 * @since 26/04/22
 */

public final class MyCellarCheckBox extends JCheckBox implements IMyCellarComponent {

  private static final long serialVersionUID = 2584507081563652083L;
  private LabelKey labelKey;

  public MyCellarCheckBox(Icon icon) {
    super(icon);
    setFont(FONT_PANEL);
  }

  public MyCellarCheckBox() {
    super("");
    setFont(FONT_PANEL);
  }

  public MyCellarCheckBox(String code) {
    labelKey = new LabelKey(INFO_OTHER, code);
    updateText();
    MyCellarLabelManagement.add(this);
    setFont(FONT_PANEL);
  }

  public MyCellarCheckBox(String code, LabelProperty labelProperty) {
    labelKey = new LabelKey(INFO_OTHER, code, labelProperty);
    updateText();
    MyCellarLabelManagement.add(this);
    setFont(FONT_PANEL);
  }

  public MyCellarCheckBox(String text, boolean selected) {
    super(text, selected);
    setFont(FONT_PANEL);
  }

  public MyCellarCheckBox(String text, Icon icon, boolean selected) {
    super(text, icon, selected);
    setFont(FONT_PANEL);
  }

  @Override
  public void updateText() {
    MyCellarLabelManagement.updateText(this, labelKey);
  }

}
