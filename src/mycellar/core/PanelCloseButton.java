package mycellar.core;

import mycellar.core.text.MyCellarLabelManagement;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * Titre : Cave &agrave; vin
 * Description : Votre description
 * Copyright : Copyright (c) 2014
 * Soci&eacute;t&eacute; : Seb Informatique
 *
 * @author S&eacute;bastien Duch&eacute;
 * @version 0.3
 * @since 27/05/21
 */
public abstract class PanelCloseButton extends JButton implements ActionListener {

  private static final long serialVersionUID = 76516458718107537L;
  private static final MouseListener MOUSE_LISTENER = new MouseAdapter() {
    @Override
    public void mouseEntered(MouseEvent e) {
      Component component = e.getComponent();
      if (component instanceof AbstractButton) {
        AbstractButton button = (AbstractButton) component;
        button.setBorderPainted(true);
      }
    }

    @Override
    public void mouseExited(MouseEvent e) {
      Component component = e.getComponent();
      if (component instanceof AbstractButton) {
        AbstractButton button = (AbstractButton) component;
        button.setBorderPainted(false);
      }
    }
  };

  protected PanelCloseButton() {
    int size = 17;
    setPreferredSize(new Dimension(size, size));
    setToolTipText(MyCellarLabelManagement.getLabel("Main.Delete"));
    // Make the button looks the same for all Laf's
    setUI(new BasicButtonUI());
    // Make it transparent
    setContentAreaFilled(false);
    // No need to be focusable
    setFocusable(false);
    setBorder(BorderFactory.createEtchedBorder());
    setBorderPainted(false);
    // Making nice rollover effect
    // we use the same listener for all buttons
    addMouseListener(MOUSE_LISTENER);
    setRolloverEnabled(true);
    // Close the proper tab by clicking the button
    addActionListener(this);
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    perform();
  }

  public abstract void perform();

  // we don't want to update UI for this button
  @Override
  public void updateUI() {
  }

  // paint the cross
  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    Graphics2D g2 = (Graphics2D) g.create();
    // shift the image for pressed buttons
    if (getModel().isPressed()) {
      g2.translate(1, 1);
    }
    g2.setStroke(new BasicStroke(2));
    g2.setColor(Color.BLACK);
    if (getModel().isRollover()) {
      g2.setColor(Color.MAGENTA);
    }
    int delta = 6;
    g2.drawLine(delta, delta, getWidth() - delta - 1, getHeight()
        - delta - 1);
    g2.drawLine(getWidth() - delta - 1, delta, delta, getHeight()
        - delta - 1);
    g2.dispose();
  }
}
