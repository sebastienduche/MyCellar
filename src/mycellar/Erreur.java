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
 * @version 2.6
 * @since 14/10/21
 */
public class Erreur {
  private final JCheckBox checkNotShow = new JCheckBox(Program.getLabel("Infos213"));

  private Erreur() {}

  /**
   * Error message with one label
   *
   * @param texte
   */
  public static void showSimpleErreur(String texte) {
    JOptionPane.showMessageDialog(Start.getInstance(), texte, Program.getError("Error015"), JOptionPane.ERROR_MESSAGE);
  }

  /**
   * Error message with one label
   *
   * @param target
   * @param texte
   */
  public static void showSimpleErreur(Component target, String texte) {
    JOptionPane.showMessageDialog(target, texte, Program.getError("Error015"), JOptionPane.ERROR_MESSAGE);
  }

  /**
   * Error or information message with one label.
   *
   * @param texte
   * @param information
   */
  public static void showSimpleErreur(String texte, boolean information) {
    JOptionPane.showMessageDialog(Start.getInstance(), texte, information ? Program.getError("Error032") : Program.getError("Error015"), information ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);
  }

  /**
   * Error message with 2 labels
   *
   * @param texte1
   * @param texte2
   */
  public static void showSimpleErreur(String texte1, String texte2) {
    new Erreur().initialize(texte1, texte2, false, null);
  }

  /**
   * Error or information message with 2 labels
   *
   * @param texte1
   * @param texte2
   * @param information
   */
  public static void showSimpleErreur(String texte1, String texte2, boolean information) {
    new Erreur().initialize(texte1, texte2, information, null);
  }

  /**
   * Information message with option to not show this message the next time
   *
   * @param texte1
   * @param texte2
   * @param key
   */
  public static void showInformationMessageWithKey(String texte1, String texte2, String key) {
    new Erreur().initialize(texte1, texte2, true, key);
  }

  private void initialize(String texte1, String texte2, boolean information, String keyword) {
    JPanel panel = new JPanel();
    panel.setLayout(new MigLayout("", "grow", "[]"));
    MyCellarLabel label2 = new MyCellarLabel(texte2);
    checkNotShow.setFont(FONT_BOUTTON_SMALL);
    panel.add(new MyCellarLabel(texte1));
    panel.add(label2, "newline, hidemode 3");
    panel.add(checkNotShow, "newline, hidemode 3, gaptop 15px");
    checkNotShow.setVisible(MyCellarUtils.isDefined(keyword));
    label2.setVisible(!texte2.isEmpty());
    JOptionPane.showMessageDialog(Start.getInstance(), panel, information ? Program.getError("Error032") : Program.getError("Error015"), information ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);
    if (checkNotShow.isSelected()) {
      Program.putCaveConfigBool(keyword, true);
    }
  }

}
