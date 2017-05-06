package Cave;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import Cave.core.MyCellarButton;
import Cave.core.MyCellarCheckBox;
import Cave.core.MyCellarComboBox;
import Cave.core.MyCellarLabel;
import Cave.core.MyCellarFields;
import net.miginfocom.swing.MigLayout;

/**
 * <p>Titre : Cave à vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2004</p>
 * <p>Société : Seb Informatique</p>
 * @author Sébastien Duché
 * @version 1.2
 * @since 13/11/16
 */
public class CSVOptions extends JDialog {
	private JPanel jPanel1 = new JPanel();
	private MyCellarLabel info_separator = new MyCellarLabel();
	private MyCellarCheckBox export[] = new MyCellarCheckBox[1];
	private JScrollPane jScrollPane1 = new JScrollPane();
	private JPanel jPanel2 = new JPanel();
	private MyCellarLabel colonnes[] = new MyCellarLabel[1];
	private MyCellarButton valider = new MyCellarButton();
	private MyCellarButton annuler = new MyCellarButton();
	private MyCellarComboBox<String> separator = new MyCellarComboBox<String>();
	private int nb_colonnes = 0;
	static final long serialVersionUID = 230705;

	/**
	 * CSVOptions: Constructeur pour la fenêtre d'options.
	 */
	public CSVOptions() {
		try {
			jbInit();
		}
		catch (Exception e) {
			Program.showException(e);
		}
	}

	/**
	 * jbInit: Fonction d'initialisation.
	 *
	 * @throws Exception
	 */
	private void jbInit() throws Exception {
		Debug("JbInit");
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		this.setTitle(Program.getLabel("Infos269"));

		this.addKeyListener(new java.awt.event.KeyListener() {
			public void keyReleased(java.awt.event.KeyEvent e) {}

			public void keyPressed(java.awt.event.KeyEvent e) {
				keylistener_actionPerformed(e);
			}

			public void keyTyped(java.awt.event.KeyEvent e) {}
		});


		this.setLayout(new MigLayout("","grow",""));
		this.setResizable(false);
		jPanel1.setBorder(BorderFactory.createEtchedBorder());
		jPanel1.setLayout(new MigLayout("","grow",""));
		jPanel1.setFont(Program.font_panel);
		info_separator.setText(Program.getLabel("Infos034") + ":"); //Séparateur
		java.util.LinkedList<MyCellarFields> listColumns = MyCellarFields.getFieldsList();
		nb_colonnes = listColumns.size();
		colonnes = new MyCellarLabel[nb_colonnes];
		export = new MyCellarCheckBox[nb_colonnes];
		for (int i = 0; i < nb_colonnes; i++) {
			export[i] = new MyCellarCheckBox(Program.getLabel("Infos261"));
			try {
				int I = Program.getCaveConfigInt("SIZE_COL" + i + "EXPORT_CSV", 0);
				if (I == 1) {
					export[i].setSelected(true);
				}
				else {
					export[i].setSelected(false);
				}
			}
			catch (NumberFormatException nfe) {
				export[i].setSelected(false);
				Program.putCaveConfigString("SIZE_COL" + i + "EXPORT_CSV", "0");
			}
			colonnes[i] = new MyCellarLabel(listColumns.get(i).toString());
		}
		jPanel2.setLayout(new MigLayout("","[grow][grow]",""));
		jPanel2.setFont(Program.font_panel);
		valider.setText("OK");
		separator.addItem(Program.getLabel("Infos002"));
		separator.addItem(Program.getLabel("Infos042"));
		separator.addItem(Program.getLabel("Infos043"));
		separator.addItem(Program.getLabel("Infos044"));
		String key = null;
		key = Program.getCaveConfigString("SEPARATOR_DEFAULT", ",");
		if (key.equals(";")) {
			separator.setSelectedIndex(1);
		}
		else {
			if (key.equals(":")) {
				separator.setSelectedIndex(2);
			}
			else {
				if (key.equals("/")) {
					separator.setSelectedIndex(3);
				}
			}
		}
		valider.addActionListener((e) -> valider_actionPerformed(e));
		annuler.setText(Program.getLabel("Infos055"));
		annuler.addActionListener((e) -> dispose());

		this.add(info_separator, "split 2");
		this.add(separator, "wrap");

		jScrollPane1 = new JScrollPane(jPanel2);
		jScrollPane1.setBorder(BorderFactory.createTitledBorder(Program.getLabel("Infos258")));
		this.add(jScrollPane1, "grow, gaptop 15px, wrap");
		for (int i = 0; i < nb_colonnes; i++) {
			jPanel2.add(colonnes[i],"grow");
			jPanel2.add(export[i],"wrap");
		}

		this.add(valider, "gaptop 15px, split 2, center");
		this.add(annuler);
		setSize(400, 500);
	    setLocationRelativeTo(null);
		Debug("JbInit OK");
	}

	/**
	 * valider_actionPerformed: Valider les modifications et quitter.
	 *
	 * @param e ActionEvent
	 */
	void valider_actionPerformed(ActionEvent e) {
		Debug("valider_actionPerforming...");
		for (int i = 0; i < nb_colonnes; i++) {
			if (export[i].isSelected()) {
				Program.putCaveConfigString("SIZE_COL" + i + "EXPORT_CSV", "1");
			}
			else {
				Program.putCaveConfigString("SIZE_COL" + i + "EXPORT_CSV", "0");
			}
		}
		int separ_select = separator.getSelectedIndex();
		switch (separ_select) {
		case 0:
			Program.putCaveConfigString("SEPARATOR_DEFAULT", ",");
			break;
		case 1:
			Program.putCaveConfigString("SEPARATOR_DEFAULT", ";");
			break;
		case 2:
			Program.putCaveConfigString("SEPARATOR_DEFAULT", ":");
			break;
		case 3:
			Program.putCaveConfigString("SEPARATOR_DEFAULT", "/");
			break;
		}
		this.dispose();
	}

	/**
	 * keylistener_actionPerformed: Fonction d'écoute clavier.
	 *
	 * @param e KeyEvent
	 */
	void keylistener_actionPerformed(KeyEvent e) {
		if (e.getKeyCode() == 'o' || e.getKeyCode() == 'O' || e.getKeyCode() == KeyEvent.VK_ENTER) {
			valider_actionPerformed(null);
		}
	}

	/**
	 * Debug
	 *
	 * @param sText String
	 */
	public static void Debug(String sText) {
		Program.Debug("CSVOptions: " + sText);
	}

}
