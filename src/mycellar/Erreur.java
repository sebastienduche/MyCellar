package mycellar;

import mycellar.core.uicomponents.MyCellarLabel;
import net.miginfocom.swing.MigLayout;

import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import java.awt.Component;

import static mycellar.ProgramConstants.FONT_BOUTTON_SMALL;


/**
 * <p>Titre : Cave &agrave; vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 1998</p>
 * <p>Soci&eacute;t&eacute; : Seb Informatique</p>
 *
 * @author S&eacute;bastien Duch&eacute;
 * @version 2.7
 * @since 06/01/22
 */
public class Erreur {

  private Erreur() {
  }

  /**
   * Error message with one label
   *
   * @param text
   */
  public static void showSimpleErreur(String text) {
    JOptionPane.showMessageDialog(Start.getInstance(), text, Program.getError("Error015"), JOptionPane.ERROR_MESSAGE);
  }

  /**
   * Error message with one label
   *
   * @param target
   * @param text
   */
  public static void showSimpleErreur(Component target, String text) {
    JOptionPane.showMessageDialog(target, text, Program.getError("Error015"), JOptionPane.ERROR_MESSAGE);
  }

  /**
   * Information message with one label.
   *
   * @param text
   */
  public static void showInformationMessage(String text) {
    JOptionPane.showMessageDialog(Start.getInstance(), text, Program.getError("Error032"), JOptionPane.INFORMATION_MESSAGE);
  }

  /**
   * Error message with 2 labels
   *
   * @param text1
   * @param text2
   */
  public static void showSimpleErreur(String text1, String text2) {
    new Erreur().initialize(Start.getInstance(), text1, text2, false, null);
  }

  public static void showSimpleErreur(Component target, String texte1, String texte2) {
    new Erreur().initialize(target, texte1, texte2, false, null);
  }

  /**
   * Information message with 2 labels
   *
   * @param text1
   * @param text2
   */
  public static void showInformationMessage(String text1, String text2) {
    new Erreur().initialize(Start.getInstance(), text1, text2, true, null);
  }

  /**
   * Information message with option to not show this message the next time
   *
   * @param text1
   * @param text2
   * @param key
   */
  public static void showInformationMessageWithKey(String text1, String text2, String key) {
    new Erreur().initialize(Start.getInstance(), text1, text2, true, key);
  }

  private void initialize(Component target, String text1, String text2, boolean information, String keyword) {
    JPanel panel = new JPanel();
    panel.setLayout(new MigLayout("", "grow", "[]"));
    MyCellarLabel label2 = new MyCellarLabel(text2);
    JCheckBox checkNotShow = new JCheckBox(Program.getLabel("Infos213"));
    checkNotShow.setFont(FONT_BOUTTON_SMALL);
    panel.add(new MyCellarLabel(text1));
    panel.add(label2, "newline, hidemode 3");
    panel.add(checkNotShow, "newline, hidemode 3, gaptop 15px");
    checkNotShow.setVisible(MyCellarUtils.isDefined(keyword));
    label2.setVisible(!text2.isEmpty());
    JOptionPane.showMessageDialog(target, panel, information ? Program.getError("Error032") : Program.getError("Error015"), information ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);
    if (checkNotShow.isSelected()) {
      Program.putCaveConfigBool(keyword, true);
    }
  }

}
