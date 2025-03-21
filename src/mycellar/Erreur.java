package mycellar;

import mycellar.core.uicomponents.MyCellarSimpleLabel;
import mycellar.frame.MainFrame;
import mycellar.general.IResource;
import mycellar.general.ResourceErrorKey;
import net.miginfocom.swing.MigLayout;

import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import java.awt.Component;

import static mycellar.ProgramConstants.FONT_BUTTON_SMALL;
import static mycellar.core.text.MyCellarLabelManagement.getError;
import static mycellar.core.text.MyCellarLabelManagement.getLabel;
import static mycellar.general.ResourceErrorKey.ERROR032;
import static mycellar.general.ResourceErrorKey.ERROR_ERROR;
import static mycellar.general.ResourceKey.MAIN_ASKCONFIRMATION;
import static mycellar.general.ResourceKey.MAIN_DONTSHOWNEXTTIME;


/**
 * Titre : Cave &agrave; vin
 * Description : Votre description
 * Copyright : Copyright (c) 1998
 * Soci&eacute;t&eacute; : Seb Informatique
 *
 * @author S&eacute;bastien Duch&eacute;
 * @version 3.4
 * @since 21/03/25
 */
public class Erreur {

  private Erreur() {
  }

  /**
   * Error message with one label
   */
  public static void showSimpleErreur(String text) {
    showSimpleErreur(MainFrame.getInstance(), text);
  }

  /**
   * Error message with one label
   */
  public static void showSimpleErreur(Component target, String text) {
    JOptionPane.showMessageDialog(target, text, getError(ERROR_ERROR), JOptionPane.ERROR_MESSAGE);
  }

  /**
   * Error message with 2 labels
   */
  public static void showSimpleErreur(String text1, String text2) {
    JOptionPane.showMessageDialog(MainFrame.getInstance(), String.format("%s\n%s", text1, text2), getError(ERROR_ERROR), JOptionPane.ERROR_MESSAGE);
  }

  /**
   * Error message with 2 labels
   */
  public static void showSimpleErreur(ResourceErrorKey text1, ResourceErrorKey text2) {
    JOptionPane.showMessageDialog(MainFrame.getInstance(), String.format("%s\n%s", getError(text1), getError(text2)), getError(ERROR_ERROR), JOptionPane.ERROR_MESSAGE);
  }

  /**
   * Information message with one label.
   */
  public static void showInformationMessage(String text) {
    JOptionPane.showMessageDialog(MainFrame.getInstance(), text, getError(ERROR032), JOptionPane.INFORMATION_MESSAGE);
  }

  public static void showInformationMessage(IResource resource1, IResource resource2) {
    String message = String.format("%s\n%s",
        resource1 instanceof ResourceErrorKey ? getError(resource1) : getLabel(resource1),
        resource2 instanceof ResourceErrorKey ? getError(resource2) : getLabel(resource2));
    JOptionPane.showMessageDialog(MainFrame.getInstance(), message, getError(ERROR032), JOptionPane.INFORMATION_MESSAGE);
  }

  public static void showInformationMessage(String text1, String text2) {
    JOptionPane.showMessageDialog(MainFrame.getInstance(), String.format("%s\n%s", text1, text2), getError(ERROR032), JOptionPane.INFORMATION_MESSAGE);
  }

  public static int showAskConfirmationMessage(String message) {
    return JOptionPane.showConfirmDialog(MainFrame.getInstance(), message, getLabel(MAIN_ASKCONFIRMATION), JOptionPane.YES_NO_OPTION);
  }

  /**
   * Information message with option to not show this message the next time
   */
  public static void showInformationMessageWithKey(String text1, String text2, String key) {
   initialise(MainFrame.getInstance(), text1, text2, key);
  }

  public static void showInformationMessageWithKey(String text, String key) {
   initialise(MainFrame.getInstance(), text, "", key);
  }

  private static void initialise(Component target, String text1, String text2, String keyword) {
    JPanel panel = new JPanel();
    panel.setLayout(new MigLayout("", "grow", "[]"));
    MyCellarSimpleLabel label2 = new MyCellarSimpleLabel(text2);
    JCheckBox checkNotShow = new JCheckBox(getLabel(MAIN_DONTSHOWNEXTTIME));
    checkNotShow.setFont(FONT_BUTTON_SMALL);
    panel.add(new MyCellarSimpleLabel(text1));
    panel.add(label2, "newline, hidemode 3");
    panel.add(checkNotShow, "newline, hidemode 3, gaptop 15px");
    checkNotShow.setVisible(true);
    label2.setVisible(!text2.isEmpty());
    JOptionPane.showMessageDialog(target, panel, getError(ERROR032), JOptionPane.INFORMATION_MESSAGE);
    if (checkNotShow.isSelected()) {
      Program.putCaveConfigBool(keyword, true);
    }
  }

}
