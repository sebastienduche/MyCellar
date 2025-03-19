package mycellar;

import mycellar.core.uicomponents.MyCellarSimpleLabel;
import mycellar.frame.MainFrame;
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
 * @version 3.3
 * @since 19/03/25
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
   * Information message with one label.
   */
  public static void showInformationMessage(String text) {
    JOptionPane.showMessageDialog(MainFrame.getInstance(), text, getError(ERROR032), JOptionPane.INFORMATION_MESSAGE);
  }

  public static int showAskConfirmationMessage(String message) {
    return JOptionPane.showConfirmDialog(MainFrame.getInstance(), message, getLabel(MAIN_ASKCONFIRMATION), JOptionPane.YES_NO_OPTION);
  }

  /**
   * Error message with 2 labels
   */
  public static void showSimpleErreur(String text1, String text2) {
    initialise(MainFrame.getInstance(), text1, text2, false, null);
  }

  public static void showSimpleErreur(Component target, String texte1, String texte2) {
   initialise(target, texte1, texte2, false, null);
  }

  /**
   * Information message with 2 labels
   */
  public static void showInformationMessage(String text1, String text2) {
   initialise(MainFrame.getInstance(), text1, text2, true, null);
  }

  /**
   * Information message with option to not show this message the next time
   */
  public static void showInformationMessageWithKey(String text1, String text2, String key) {
   initialise(MainFrame.getInstance(), text1, text2, true, key);
  }

  public static void showInformationMessageWithKey(String text, String key) {
   initialise(MainFrame.getInstance(), text, "", true, key);
  }

  private static void initialise(Component target, String text1, String text2, boolean information, String keyword) {
    JPanel panel = new JPanel();
    panel.setLayout(new MigLayout("", "grow", "[]"));
    MyCellarSimpleLabel label2 = new MyCellarSimpleLabel(text2);
    JCheckBox checkNotShow = new JCheckBox(getLabel(MAIN_DONTSHOWNEXTTIME));
    checkNotShow.setFont(FONT_BUTTON_SMALL);
    panel.add(new MyCellarSimpleLabel(text1));
    panel.add(label2, "newline, hidemode 3");
    panel.add(checkNotShow, "newline, hidemode 3, gaptop 15px");
    checkNotShow.setVisible(MyCellarUtils.isDefined(keyword));
    label2.setVisible(!text2.isEmpty());
    JOptionPane.showMessageDialog(target, panel, information ? getError(ERROR032) : getError(ERROR_ERROR), information ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);
    if (checkNotShow.isSelected()) {
      Program.putCaveConfigBool(keyword, true);
    }
  }

}
