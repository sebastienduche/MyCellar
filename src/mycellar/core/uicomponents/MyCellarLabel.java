package mycellar.core.uicomponents;

import mycellar.core.IMyCellarComponent;
import mycellar.core.text.LabelKey;
import mycellar.core.text.LabelProperty;
import mycellar.core.text.MyCellarLabelManagement;

import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import java.util.Timer;
import java.util.TimerTask;

import static mycellar.ProgramConstants.FONT_PANEL;

/**
 * Titre : Cave &agrave; vin
 * Description : Votre description
 * Copyright : Copyright (c) 2011
 * Soci&eacute;t&eacute; : Seb Informatique
 *
 * @author S&eacute;bastien Duch&eacute;
 * @version 1.3
 * @since 24/05/22
 */

public class MyCellarLabel extends JLabel implements IMyCellarComponent {

  private final LabelKey labelKey;

  public MyCellarLabel(String code) {
    labelKey = new LabelKey(code);
    updateText();
    MyCellarLabelManagement.add(this);
    setFont(FONT_PANEL);
  }

  public MyCellarLabel(String code, LabelProperty labelProperty) {
    labelKey = new LabelKey(code, labelProperty);
    updateText();
    MyCellarLabelManagement.add(this);
    setFont(FONT_PANEL);
  }

  public MyCellarLabel(String code, LabelProperty labelProperty, String value) {
    labelKey = new LabelKey(code, labelProperty, value);
    updateText();
    MyCellarLabelManagement.add(this);
    setFont(FONT_PANEL);
  }

  @Override
  public void updateText() {
    MyCellarLabelManagement.updateText(this, labelKey);
  }

  private void hide(boolean visible) {
    if (visible) {
      setText("");
    } else {
      setVisible(false);
    }
  }

  public void setText(String text, boolean autoHide) {
    setText(text, autoHide, 5000, true);
  }

  public void setText(String text, boolean autoHide, int delay, boolean visible) {
    super.setText(text);
    if (autoHide) {
      new Timer().schedule(
          new TimerTask() {
            @Override
            public void run() {
              SwingUtilities.invokeLater(() -> hide(visible));
            }
          },
          delay
      );
    }
  }

  public void setValue(String value) {
    if (labelKey != null) {
      labelKey.setValue(value);
      updateText();
    }
  }

}
