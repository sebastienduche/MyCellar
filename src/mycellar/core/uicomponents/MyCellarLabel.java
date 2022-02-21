package mycellar.core.uicomponents;

import mycellar.core.IMyCellarComponent;
import mycellar.core.LabelProperty;
import mycellar.core.LabelType;
import mycellar.core.MyCellarLabelManagement;

import javax.swing.Icon;
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
 * @version 0.8
 * @since 23/05/21
 */

public class MyCellarLabel extends JLabel implements IMyCellarComponent {

  private static final long serialVersionUID = 4972622436840497820L;

  private LabelType type;
  private String code;
  private String value;
  private LabelProperty labelProperty;

  @Deprecated
  public MyCellarLabel() {
    setFont(FONT_PANEL);
  }

  public MyCellarLabel(String text) {
    super(text);
    setFont(FONT_PANEL);
  }

  public MyCellarLabel(LabelType type, String code) {
    this.type = type;
    this.code = code;
    updateText();
    MyCellarLabelManagement.add(this);
    setFont(FONT_PANEL);
  }

  public MyCellarLabel(LabelType type, String code, LabelProperty labelProperty) {
    this.type = type;
    this.code = code;
    this.labelProperty = labelProperty;
    updateText();
    MyCellarLabelManagement.add(this);
    setFont(FONT_PANEL);
  }

  public MyCellarLabel(LabelType type, String code, String value) {
    this.type = type;
    this.code = code;
    this.value = value;
    updateText();
    MyCellarLabelManagement.add(this);
    setFont(FONT_PANEL);
  }

  public MyCellarLabel(Icon image) {
    super(image);
    setFont(FONT_PANEL);
  }

  @Deprecated
  public MyCellarLabel(String text, int horizontalAlignment) {
    super(text, horizontalAlignment);
    setFont(FONT_PANEL);
  }

  public MyCellarLabel(Icon image, int horizontalAlignment) {
    super(image, horizontalAlignment);
    setFont(FONT_PANEL);
  }

  @Deprecated
  public MyCellarLabel(String text, Icon icon, int horizontalAlignment) {
    super(text, icon, horizontalAlignment);
    setFont(FONT_PANEL);
  }

  @Override
  public void updateText() {
    MyCellarLabelManagement.updateText(this, type, code, value, labelProperty);
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
