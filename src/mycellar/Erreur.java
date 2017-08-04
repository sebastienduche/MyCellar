package mycellar;

import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import mycellar.core.MyCellarLabel;
import net.miginfocom.swing.MigLayout;


/**
 * <p>Titre : Cave à vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 1998</p>
 * <p>Société : Seb Informatique</p>
 * @author Sébastien Duché
 * @version 1.9
 * @since 03/08/17
 */
public class Erreur {
	private MyCellarLabel textControl1 = new MyCellarLabel();
	private MyCellarLabel text_message1 = new MyCellarLabel();
	private MyCellarLabel text_message2 = new MyCellarLabel();
	private boolean Coche = false; //Pour la case à cocher
	private String keyword; //Pour la case à cocher
	private JCheckBox coche = new JCheckBox(Program.getLabel("Infos213"));
	static final long serialVersionUID = 230405;

	/**
	 * Erreur: Constructeur d'un message d'erreur simple.
	 *
	 * @param texte1 String: Message
	 */
	public Erreur(String texte1) {
		JOptionPane.showMessageDialog(null, texte1, Program.getError("Error015"), JOptionPane.ERROR_MESSAGE);
	}

	/**
	 * Erreur: Constructeur d'un message d'erreur simple.
	 *
	 * @param texte1 String: Message
	 * @param message boolean
	 */
	public Erreur(String texte1, boolean information) {
		JOptionPane.showMessageDialog(null, texte1,  information ? Program.getError("Error032"): Program.getError("Error015"),information ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);
	}

	/**
	 * Erreur: Constructeur d'un message d'erreur simple.
	 *
	 * @param texte1 String: Message 1
	 * @param texte2 String: Message 2
	 */
	public Erreur(String texte1, String texte2) {
		initialize(texte1, texte2, false);
	}

	/**
	 * Erreur: Constructeur d'un message avec Titre particulier.
	 *
	 * @param texte1 String: Message 1
	 * @param texte2 String: Message 2
	 * @param title String: Titre de la fenêtre
	 * @param mess String: Message du titre
	 */
	public Erreur(String texte1, String texte2, boolean information) {
		initialize(texte1, texte2, information);
	}

	/**
	 * Erreur: Constructeur d'un message d'erreur avec option d'affichage ou non la prochaine fois
	 *
	 * @param texte1 String: Message 1
	 * @param texte2 String: Message 2
	 * @param information boolean: Message d'information ou d'erreur
	 * @param mess String: Message du titre
	 * @param acocher boolean: indique la présence d'une case à cocher
	 * @param key String: Nom de la Clé
	 */
	public Erreur(String texte1, String texte2, boolean information, String key) {
		keyword = key;
		Coche = true;
		initialize(texte1, texte2, information);
	}


	private void initialize(String texte1, String texte2, boolean information) {
		JPanel panel = new JPanel();
		panel.setLayout(new MigLayout("","grow","[]"));
		textControl1.setFont(Program.font_dialog_small);
		text_message1.setText(texte1);
		coche.setFont(Program.font_boutton_small);
		text_message2.setText(texte2);
		panel.add(text_message1,"");
		panel.add(text_message2,"newline, hidemode 3");
		panel.add(coche,"newline, hidemode 3, gaptop 15px");
		coche.setVisible(Coche);
		text_message2.setVisible(!texte2.isEmpty());
		JOptionPane.showMessageDialog(null,panel,information ? Program.getError("Error032"): Program.getError("Error015"),information ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);
		if (Coche && coche.isSelected()) {
			//Ecriture dans un fichier pour ne plus afficher le message
			Program.putCaveConfigString(keyword, "1");
		}
	}

}
