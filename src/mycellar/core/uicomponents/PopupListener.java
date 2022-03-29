package mycellar.core.uicomponents;

import mycellar.MyCellarImage;
import mycellar.Program;
import mycellar.core.text.LabelType;

import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import static mycellar.core.text.MyCellarLabelManagement.getLabel;

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
  private final JMenuItem cut = new JMenuItem(getLabel(LabelType.INFO, "241"), MyCellarImage.CUT);
  private final JMenuItem copy = new JMenuItem(getLabel(LabelType.INFO, "242"), MyCellarImage.COPY);
  private JComponent textField;

  public PopupListener() {
    popup.add(cut);
    popup.add(copy);
    JMenuItem paste = new JMenuItem(getLabel(LabelType.INFO, "243"), MyCellarImage.PASTE);
    popup.add(paste);
    cut.addActionListener(this::cut_actionPerformed);
    copy.addActionListener(this::copy_actionPerformed);
    paste.addActionListener(this::paste_actionPerformed);
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
          cut.setEnabled(((JTextField) textField).getSelectedText() != null);
          copy.setEnabled(((JTextField) textField).getSelectedText() != null);
        } else if (textField instanceof JTextArea) {
          cut.setEnabled(((JTextArea) textField).getSelectedText() != null);
          copy.setEnabled(((JTextArea) textField).getSelectedText() != null);
        }
        if (textField.isVisible()) {
          popup.show(e.getComponent(), e.getX(), e.getY());
        }
      }
    }
  }

  private void paste_actionPerformed(ActionEvent e) {
    if (textField instanceof JTextField) {
      JTextField jtf = (JTextField) textField;
      jtf.setText(jtf.getText().substring(0, jtf.getSelectionStart()) + Program.CLIPBOARD.paste() + jtf.getText().substring(jtf.getSelectionEnd()));
    } else if (textField instanceof JTextArea) {
      JTextArea jtf = (JTextArea) textField;
      jtf.setText(jtf.getText().substring(0, jtf.getSelectionStart()) + Program.CLIPBOARD.paste() + jtf.getText().substring(jtf.getSelectionEnd()));
    }
  }

  private void cut_actionPerformed(ActionEvent e) {
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

    Program.CLIPBOARD.copy(txt);
  }

  private void copy_actionPerformed(ActionEvent e) {
    String txt = "";
    if (textField instanceof JTextField) {
      JTextField jtf = (JTextField) textField;
      txt = jtf.getSelectedText();
    } else if (textField instanceof JTextArea) {
      JTextArea jtf = (JTextArea) textField;
      txt = jtf.getSelectedText();
    }

    Program.CLIPBOARD.copy(txt);
  }
}
