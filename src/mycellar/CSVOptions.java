package mycellar;

import mycellar.core.MyCellarButton;
import mycellar.core.MyCellarCheckBox;
import mycellar.core.MyCellarComboBox;
import mycellar.core.MyCellarFields;
import mycellar.core.MyCellarLabel;
import net.miginfocom.swing.MigLayout;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

/**
 * <p>Titre : Cave à vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2004</p>
 * <p>Société : Seb Informatique</p>
 * @author Sébastien Duché
 * @version 2.0
 * @since 01/12/18
 */
class CSVOptions extends JDialog {
	private final MyCellarCheckBox export[];
	private final MyCellarComboBox<String> separator = new MyCellarComboBox<>();
	private final int nb_colonnes;
	static final long serialVersionUID = 230705;

	/**
	 * CSVOptions: Constructeur pour la fenêtre d'options.
	 */
	public CSVOptions() {

		Debug("Constructor");
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setModal(true);
		setTitle(Program.getLabel("Infos269"));

		addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == 'o' || e.getKeyCode() == 'O' || e.getKeyCode() == KeyEvent.VK_ENTER) {
					valider_actionPerformed(null);
				}
			}
		});

		setLayout(new MigLayout("","grow",""));
		setResizable(false);
		JPanel panel = new JPanel();
		panel.setBorder(BorderFactory.createEtchedBorder());
		panel.setLayout(new MigLayout("","grow",""));
		panel.setFont(Program.FONT_PANEL);
		MyCellarLabel info_separator = new MyCellarLabel(Program.getLabel("Infos034") + ":"); //Séparateur
		ArrayList<MyCellarFields> listColumns = MyCellarFields.getFieldsList();
		nb_colonnes = listColumns.size();
		final MyCellarLabel[] colonnes = new MyCellarLabel[nb_colonnes];
		export = new MyCellarCheckBox[nb_colonnes];
		for (int i = 0; i < nb_colonnes; i++) {
			export[i] = new MyCellarCheckBox(Program.getLabel("Infos261"));
			export[i].setSelected(Program.getCaveConfigInt("SIZE_COL" + i + "EXPORT_CSV", 0) == 1);
			colonnes[i] = new MyCellarLabel(listColumns.get(i).toString());
		}
		JPanel jPanel2 = new JPanel();
		jPanel2.setLayout(new MigLayout("","[grow][grow]",""));
		jPanel2.setFont(Program.FONT_PANEL);
		MyCellarButton valider = new MyCellarButton(Program.getLabel("Main.OK"));
		separator.addItem(Program.getLabel("Infos002"));
		separator.addItem(Program.getLabel("Infos042"));
		separator.addItem(Program.getLabel("Infos043"));
		separator.addItem(Program.getLabel("Infos044"));
		String key = Program.getCaveConfigString("SEPARATOR_DEFAULT", ",");
		if (key.equals(";")) {
			separator.setSelectedIndex(1);
		}	else if (key.equals(":")) {
			separator.setSelectedIndex(2);
		}	else if (key.equals("/")) {
			separator.setSelectedIndex(3);
		}
		valider.addActionListener(this::valider_actionPerformed);
		MyCellarButton annuler = new MyCellarButton(Program.getLabel("Infos055"));
		annuler.addActionListener((e) -> dispose());

		add(info_separator, "split 2");
		add(separator, "wrap");

		JScrollPane jScrollPane1 = new JScrollPane(jPanel2);
		jScrollPane1.setBorder(BorderFactory.createTitledBorder(Program.getLabel("Infos258")));
		add(jScrollPane1, "grow, gaptop 15px, wrap");
		for (int i = 0; i < nb_colonnes; i++) {
			jPanel2.add(colonnes[i],"grow");
			jPanel2.add(export[i],"wrap");
		}

		add(valider, "gaptop 15px, split 2, center");
		add(annuler);
		setSize(400, 500);
		setLocationRelativeTo(Start.getInstance());
		Debug("JbInit OK");
	}

	/**
	 * valider_actionPerformed: Valider les modifications et quitter.
	 *
	 * @param e ActionEvent
	 */
	private void valider_actionPerformed(ActionEvent e) {
		Debug("valider_actionPerforming...");
		for (int i = 0; i < nb_colonnes; i++) {
			Program.putCaveConfigInt("SIZE_COL" + i + "EXPORT_CSV", export[i].isSelected() ? 1 : 0);
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
		dispose();
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
