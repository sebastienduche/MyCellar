package mycellar.core.panel;

import mycellar.core.uicomponents.MyCellarButton;
import mycellar.core.uicomponents.MyCellarSimpleLabel;
import mycellar.general.ResourceKey;
import net.miginfocom.swing.MigLayout;

import javax.swing.AbstractAction;
import javax.swing.JPanel;
import java.awt.Color;

import static javax.swing.SwingConstants.CENTER;

/**
 * Titre : Cave &agrave; vin
 * Description : Votre description
 * Copyright : Copyright (c) 2025
 * Soci&eacute;t&eacute; : Seb Informatique
 *
 * @author S&eacute;bastien Duch&eacute;
 * @version 0.2
 * @since 12/09/25
 */
public class PanelSave extends JPanel {

  protected MyCellarButton firstButton;
  protected MyCellarButton secondButton;
  protected final MyCellarSimpleLabel end = new MyCellarSimpleLabel();

  public PanelSave() {
    setLayout(new MigLayout("", "grow", ""));
    end.setForeground(Color.red);
    end.setHorizontalAlignment(CENTER);
    add(end, "center, hidemode 3, wrap");
  }

  public void enableAll(boolean enable) {
    firstButton.setEnabled(enable);
    if (secondButton != null) {
      secondButton.setEnabled(enable);
    }
    end.setVisible(enable);
  }

  public void initializeFirstButton(ResourceKey resourceKey, AbstractAction action) {
    firstButton = new MyCellarButton(resourceKey, action);
    add(firstButton, "center, split 2");
  }

  public void setFirstButtonMnemonic(char c) {
    if (firstButton != null) {
      firstButton.setMnemonic(c);
    }
  }

  public void setSecondButtonMnemonic(char c) {
    if (secondButton != null) {
      secondButton.setMnemonic(c);
    }
  }

  public void initializeSecondButton(ResourceKey resourceKey, AbstractAction action) {
    secondButton = new MyCellarButton(resourceKey, action);
    add(secondButton);
  }

  public void setEndText(String label) {
    end.setText(label);
  }

  public void setEndText(String label, boolean autoHide) {
    end.setText(label, autoHide);
  }

  public void enableFirstButton(boolean b) {
    if (firstButton != null) {
      firstButton.setEnabled(b);
    }
  }

  public void enableSecondButton(boolean b) {
    if (secondButton != null) {
      secondButton.setEnabled(b);
    }
  }

  public void resetText() {
    end.setText("");
  }

  public void setFirstButtonText(String label) {
    if (firstButton != null) {
      firstButton.setText(label);
    }
  }

  public boolean isFirstButtonEnabled() {
    return firstButton != null && firstButton.isEnabled();
  }

  public boolean isSecondButtonEnabled() {
    return secondButton != null && secondButton.isEnabled();
  }
}
