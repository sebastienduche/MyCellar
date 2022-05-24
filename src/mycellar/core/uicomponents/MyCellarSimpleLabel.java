package mycellar.core.uicomponents;

import mycellar.core.IMyCellarComponent;

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
 * @version 0.1
 * @since 24/05/22
 */

public class MyCellarSimpleLabel extends JLabel implements IMyCellarComponent {

  private static final long serialVersionUID = 4972622436840497820L;

  public MyCellarSimpleLabel() {
    setFont(FONT_PANEL);
  }

  public MyCellarSimpleLabel(String text) {
    super(text);
    setFont(FONT_PANEL);
  }

  @Override
  public void updateText() {
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

}
