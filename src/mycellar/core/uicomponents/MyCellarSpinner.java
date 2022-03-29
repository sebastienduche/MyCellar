package mycellar.core.uicomponents;

import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import static mycellar.ProgramConstants.FONT_PANEL;

/**
 * Titre : Cave &agrave; vin
 * Description : Votre description
 * Copyright : Copyright (c) 2011
 * Soci&eacute;t&eacute; : Seb Informatique
 *
 * @author S&eacute;bastien Duch&eacute;
 * @version 0.5
 * @since 30/08/20
 */
public final class MyCellarSpinner extends JSpinner {

  private static final long serialVersionUID = -6429351001334594600L;
  private final SpinnerNumberModel model;


  public MyCellarSpinner(int min, int max) {
    model = new SpinnerNumberModel(min, min, max, 1);
    setModel(model);
    setFont(FONT_PANEL);
  }

  public int getIntValue() {
    return model.getNumber().intValue();
  }
}
