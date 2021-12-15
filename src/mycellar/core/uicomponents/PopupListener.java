package mycellar.core.uicomponents;

import mycellar.MyCellarImage;
import mycellar.Program;

import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Titre : Cave &agrave; vin
 * Description : Votre description
 * Copyright : Copyright (c) 1998
 * Soci&eacute;t&eacute; : Seb Informatique
 *
 * @author S&eacute;bastien Duch&eacute;
 * @version 0.4
 * @since 04/07/18
 */
public class PopupListener extends MouseAdapter {

  private final JPopupMenu popup = new JPopupMenu();
  private final JMenuItem couper = new JMenuItem(Program.getLabel("Infos241"), MyCellarImage.CUT);
  private final JMenuItem copier = new JMenuItem(Program.getLabel("Infos242"), MyCellarImage.COPY);
  private final JMenuItem coller = new JMenuItem(Program.getLabel("Infos243"), MyCellarImage.PASTE);
  private JComponent textField;

  public PopupListener() {
    popup.add(couper);
    popup.add(copier);
    popup.add(coller);
    couper.addActionListener(this::couper_actionPerformed);
    copier.addActionListener(this::copier_actionPerformed);
    coller.addActionListener(this::coller_actionPerformed);
  }

  @Override
  public void mousePressed(MouseEvent e) {
    maybeShowPopup(e);
  }

  @Override
  public void mouseClicked(MouseEvent e) {
    maybeShowPopup(e);
  }

  private void maybeShowPopup(MouseEvent e) {
    try {
      JComponent jtf = (JComponent) e.getComponent();
      if (jtf.isEnabled() && jtf.isVisible()) {
        textField = (JComponent) e.getComponent();
      }
    } catch (RuntimeException ee) {
      return;
    }

    if (e.getButton() == MouseEvent.BUTTON3) {
      if (textField.isFocusable() && textField.isEnabled()) {
        textField.requestFocus();
        if (textField instanceof JTextField) {
          couper.setEnabled(((JTextField) textField).getSelectedText() != null);
          copier.setEnabled(((JTextField) textField).getSelectedText() != null);
        } else if (textField instanceof JTextArea) {
          couper.setEnabled(((JTextArea) textField).getSelectedText() != null);
          copier.setEnabled(((JTextArea) textField).getSelectedText() != null);
        }
        if (textField.isVisible()) {
          popup.show(e.getComponent(), e.getX(), e.getY());
        }
      }
    }
    /*if (e.getButton() == MouseEvent.BUTTON1) {
      if (jtf.isFocusable() && jtf.isEnabled()) {
        jtf.requestFocus();
        if (jtf.getSelectedText() == null) {
          cut.setEnabled(false);
          copy.setEnabled(false);
        }
        else {
          cut.setEnabled(true);
          copy.setEnabled(true);
        }
      }
    }*/
  }

  /**
   * coller_actionPerformed: Couper
   *
   * @param e ActionEvent
   */
  private void coller_actionPerformed(ActionEvent e) {

    if (textField instanceof JTextField) {
      JTextField jtf = (JTextField) textField;
      jtf.setText(jtf.getText().substring(0, jtf.getSelectionStart()) + Program.CLIPBOARD.coller() + jtf.getText().substring(jtf.getSelectionEnd()));
    } else if (textField instanceof JTextArea) {
      JTextArea jtf = (JTextArea) textField;
      jtf.setText(jtf.getText().substring(0, jtf.getSelectionStart()) + Program.CLIPBOARD.coller() + jtf.getText().substring(jtf.getSelectionEnd()));
    }
  }

  /**
   * couper_actionPerformed: Couper
   *
   * @param e ActionEvent
   */
  private void couper_actionPerformed(ActionEvent e) {
    String txt = "";
    if (textField instanceof JTextField) {
      JTextField jtf = (JTextField) textField;
      txt = jtf.getSelectedText();
      jtf.setText(jtf.getText().substring(0, jtf.getSelectionStart()) + jtf.getText().substring(jtf.getSelectionEnd()));
    } else if (textField instanceof JTextArea) {
      JTextArea jtf = (JTextArea) textField;
      txt = jtf.getSelectedText();
      jtf.setText(jtf.getText().substring(0, jtf.getSelectionStart()) + jtf.getText().substring(jtf.getSelectionEnd()));
    }

    Program.CLIPBOARD.copier(txt);
  }

  /**
   * copier_actionPerformed: Copier
   *
   * @param e ActionEvent
   */
  private void copier_actionPerformed(ActionEvent e) {
    String txt = "";
    if (textField instanceof JTextField) {
      JTextField jtf = (JTextField) textField;
      txt = jtf.getSelectedText();
    } else if (textField instanceof JTextArea) {
      JTextArea jtf = (JTextArea) textField;
      txt = jtf.getSelectedText();
    }

    Program.CLIPBOARD.copier(txt);
  }
}
