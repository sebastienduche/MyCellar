package mycellar;

import mycellar.core.MyCellarLabel;
import net.miginfocom.swing.MigLayout;

import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import java.awt.Component;


/**
 * <p>Titre : Cave à vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 1998</p>
 * <p>Société : Seb Informatique</p>
 * @author Sébastien Duché
 * @version 2.4
 * @since 29/08/19
 */
public class Erreur {
	private final JCheckBox checkNotShow = new JCheckBox(Program.getLabel("Infos213"));
	static final long serialVersionUID = 230405;
	private static final Erreur INSTANCE = new Erreur();

	private Erreur() {

	}

	private static Erreur getInstance() {
		return INSTANCE;
	}

	/**
	 * Erreur: Constructeur d'un message d'erreur simple.
	 *
	 * @param texte
	 */
	public static void showSimpleErreur(String texte) {
			JOptionPane.showMessageDialog(null, texte, Program.getError("Error015"), JOptionPane.ERROR_MESSAGE);
	}

	/**
	 * Erreur: Constructeur d'un message d'erreur simple.
	 *
	 * @param target
	 * @param texte
	 */
	public static void showSimpleErreur(Component target, String texte) {
		JOptionPane.showMessageDialog(target, texte, Program.getError("Error015"), JOptionPane.ERROR_MESSAGE);
	}

	/**
	 * Erreur: Constructeur d'un message d'erreur simple.
	 *
	 * @param texte
	 */
	public static void showSimpleErreur(String texte, boolean information) {
		JOptionPane.showMessageDialog(null, texte,  information ? Program.getError("Error032"): Program.getError("Error015"),information ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);
	}

	/**
	 * Erreur: Constructeur d'un message d'erreur simple.
	 *
	 * @param texte1 String: Message 1
	 * @param texte2 String: Message 2
	 */
	public static void showSimpleErreur(String texte1, String texte2) {
			getInstance().initialize(texte1, texte2, false, null);
	}

	/**
	 * Erreur: Constructeur d'un message d'erreur avec type Information.
	 *
	 * @param texte1 String: Message 1
	 * @param texte2 String: Message 2
	 *               @param information String: Titre de la fenêtre
	 */
	public static void showSimpleErreur(String texte1, String texte2, boolean information) {
		getInstance().initialize(texte1, texte2, information, null);
	}

	/**
	 * Erreur: Constructeur d'un message d'erreur avec option d'affichage ou non la prochaine fois
	 *
	 * @param texte1 String: Message 1
	 * @param texte2 String: Message 2
	 * @param key String: Nom de la Clé
	 */
	public static void showKeyErreur(String texte1, String texte2, String key) {
		getInstance().initialize(texte1, texte2, true, key);
	}

	private void initialize(String texte1, String texte2, boolean information, String keyword) {
		JPanel panel = new JPanel();
		panel.setLayout(new MigLayout("","grow","[]"));
		MyCellarLabel label1 = new MyCellarLabel(texte1);
    MyCellarLabel label2 = new MyCellarLabel(texte2);
		checkNotShow.setFont(Program.FONT_BOUTTON_SMALL);
		panel.add(label1);
		panel.add(label2,"newline, hidemode 3");
		panel.add(checkNotShow,"newline, hidemode 3, gaptop 15px");
		checkNotShow.setVisible(keyword != null && !keyword.isEmpty());
		label2.setVisible(!texte2.isEmpty());
		JOptionPane.showMessageDialog(null,panel,information ? Program.getError("Error032"): Program.getError("Error015"),information ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);
		if (checkNotShow.isSelected()) {
			//Ecriture dans un fichier pour ne plus afficher le message
			Program.putCaveConfigBool(keyword, true);
		}
	}

}
