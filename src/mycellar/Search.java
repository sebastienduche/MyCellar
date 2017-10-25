package mycellar;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.LinkedList;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import mycellar.actions.OpenAddVinAction;
import mycellar.core.MyCellarButton;
import mycellar.core.MyCellarCheckBox;
import mycellar.core.MyCellarComboBox;
import mycellar.core.MyCellarLabel;
import mycellar.requester.CollectionFilter;
import mycellar.requester.ui.PanelRequest;
import mycellar.showfile.ShowFile;
import net.miginfocom.swing.MigLayout;

/**
 * <p>Titre : Cave à vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2003</p>
 * <p>Société : Seb Informatique</p>
 * @author Sébastien Duché
 * @version 18.1
 * @since 25/10/17
 */
public class Search extends JPanel implements Runnable, ITabListener {
	private static JTable table;
	private final static TableValues model = new TableValues();
	public JScrollPane scrollpane;
	private final MyCellarButton suppr = new MyCellarButton(MyCellarImage.DELETE);
	private final MyCellarButton export = new MyCellarButton(MyCellarImage.EXPORT);
	private final MyCellarLabel textControl2 = new MyCellarLabel();
	private final MyCellarButton modif = new MyCellarButton(MyCellarImage.WINE);
	private final MyCellarComboBox<String> num_lieu = new MyCellarComboBox<String>();
	private final MyCellarComboBox<String> column = new MyCellarComboBox<String>();
	private final MyCellarComboBox<String> lieu = new MyCellarComboBox<String>();
	private final MyCellarComboBox<String> line = new MyCellarComboBox<String>();
	private final MyCellarComboBox<String> year = new MyCellarComboBox<String>();
	private final MyCellarLabel label3 = new MyCellarLabel();
	private final MyCellarLabel label4 = new MyCellarLabel();
	private final MyCellarLabel label5 = new MyCellarLabel();
	private final MyCellarLabel label6 = new MyCellarLabel();
	private TextFieldPopup name = null;
	private final MyCellarButton cherche = new MyCellarButton(MyCellarImage.SEARCH);
	private final MyCellarButton vider = new MyCellarButton();
	private char RECHERCHE = Program.getLabel("RECHERCHE").charAt(0);
	private char MODIF = Program.getLabel("MODIF").charAt(0);
	private char SUPPR = Program.getLabel("SUPPR").charAt(0);
	private char EXPORT = Program.getLabel("EXPORT").charAt(0);
	private final MyCellarCheckBox casse = new MyCellarCheckBox(Program.getLabel("Infos086")); //"Respecter la casse");;
	private final MyCellarLabel resul_txt = new MyCellarLabel();
	private final MyCellarCheckBox multi = new MyCellarCheckBox();
	private String label_empl = Program.getLabel("Infos101"); //"Tous les vins de l'emplacement");
	private String label_num_empl = Program.getLabel("Infos102"); //"Tous les vins du lieu");
	private String label_ligne = Program.getLabel("Infos103"); //"Tous les vins de la ligne");
	private final static MyCellarLabel txt_nbresul = new MyCellarLabel();
	private final static MyCellarLabel txt_nb = new MyCellarLabel();
	private final MyCellarCheckBox selectall = new MyCellarCheckBox();
	private final MyCellarCheckBox empty_search = new MyCellarCheckBox();
	private final JPopupMenu popup = new JPopupMenu();
	private final JMenuItem couper = new JMenuItem(Program.getLabel("Infos241"), new ImageIcon("./resources/Cut16.gif"));
	private final JMenuItem copier = new JMenuItem(Program.getLabel("Infos242"), new ImageIcon("./resources/Copy16.gif"));
	private final JMenuItem coller = new JMenuItem(Program.getLabel("Infos243"), new ImageIcon("./resources/Paste16.gif"));
	private final JMenuItem cut = new JMenuItem(Program.getLabel("Infos241"), new ImageIcon("./resources/Cut16.gif"));
	private final JMenuItem copy = new JMenuItem(Program.getLabel("Infos242"), new ImageIcon("./resources/Copy16.gif"));
	private final JMenuItem paste = new JMenuItem(Program.getLabel("Infos243"), new ImageIcon("./resources/Paste16.gif"));
	private final MyClipBoard clipboard = new MyClipBoard();
	private final JMenuItem moveLine = new JMenuItem(Program.getLabel("Infos365"));
	private Component objet1 = null;
	private final MouseListener popup_l = new PopupListener();
	private final JTabbedPane tabbedPane = new JTabbedPane();
	private PanelName panelName = null;
	private PanelPlace panelPlace = null;
	private PanelYear panelYear = null;
	private PanelRequest panelRequest = null;
	private boolean updateView = false;
	static final long serialVersionUID = 030107;

	/**
	 * Search: Constructeur pour l'appel de la fenêtre Search via Start.
	 */
	public Search() {

		Debug("Constructor");
		txt_nb.setText("-");
		txt_nbresul.setText(Program.getLabel("Infos222")); //"Bouteille(s) trouvée(s): ");

		String key = Program.getCaveConfigString("EMPTY_SEARCH", "OFF");
		if (key.equals("ON")) {
			empty_search.setSelected(true);
		}

		name = new TextFieldPopup(Program.getStorage().getBottleNames(), 150);
		//name.setCaseSensitive(false);

		//Ajout des lieux
		lieu.removeAllItems();
		lieu.addItem("");
		for (int i = 0; i < Program.GetCaveLength(); i++) {
			lieu.addItem(Program.getCave(i).getNom());
		}
		export.setText(Program.getLabel("Infos120")); //"Exporter le résultat");
		export.setMnemonic(EXPORT);
		selectall.setText(Program.getLabel("Infos126")); //"Tout sélectionner");
		empty_search.setText(Program.getLabel("Infos275")); //Vider automatiquement
		selectall.setHorizontalAlignment(4);
		selectall.setHorizontalTextPosition(2);

		selectall.addActionListener((e) -> selectall_actionPerformed(e));
		empty_search.addActionListener((e) -> empty_search_actionPerformed(e));
		export.addActionListener((e) -> export_actionPerformed(e));
		suppr.setText(Program.getLabel("Infos051")); //"Supprimer");
		suppr.setMnemonic(SUPPR);
		textControl2.setText(Program.getLabel("Infos080")); //"Sélectionner un(des) vin(s) dans la liste. Cliquer sur \"Modifier\" ou \"Supprimer\"");
		modif.setText(Program.getLabel("Infos079")); //"Modifier");
		modif.setMnemonic(MODIF);
		if (model.getRowCount() == 0) {
			modif.setEnabled(false);
			suppr.setEnabled(false);
			export.setEnabled(false);
		}

		suppr.addActionListener((e) -> suppr_actionPerformed(e));

		table = new JTable(model);
		table.setAutoCreateRowSorter(true);
		label3.setText(Program.getLabel("Infos081")); //"Emplacement du vin");
		label4.setText(Program.getLabel("Infos082")); //"Numéro du lieu");
		label5.setText(Program.getLabel("Infos028")); //"Ligne");
		label6.setText(Program.getLabel("Infos083")); //"Colonne");
		cherche.addActionListener((e) -> cherche_actionPerformed(e));
		cherche.setText(Program.getLabel("Infos084")); //"Chercher");
		cherche.setMnemonic(RECHERCHE);
		vider.addActionListener((e) -> vider_actionPerformed(e));
		vider.setText(Program.getLabel("Infos220")); //"Effacer les résultats");
		line.addItemListener((e) -> line_itemStateChanged(e));
		lieu.addItemListener((e) -> lieu_itemStateChanged(e));

		this.addKeyListener(new java.awt.event.KeyListener() {
			public void keyReleased(java.awt.event.KeyEvent e) {}

			public void keyPressed(java.awt.event.KeyEvent e) {
				keylistener_actionPerformed(e);
			}

			public void keyTyped(java.awt.event.KeyEvent e) {}
		});

		num_lieu.addItemListener((e) -> num_lieu_itemStateChanged(e));
		TableColumnModel tcm = table.getColumnModel();
		TableColumn tc1[] = new TableColumn[5];
		for (int w = 0; w < 5; w++) {
			tc1[w] = tcm.getColumn(w);
			tc1[w].setCellRenderer(new ToolTipRenderer());
			switch (w) {
			case 1:
				tc1[w].setMinWidth(150);
				break;
			case 2:
				tc1[w].setMinWidth(50);
				break;
			case 3:
				tc1[w].setMinWidth(100);
				break;
			default:
				tc1[w].setMinWidth(30);
				break;
			}
		}
		TableColumn tc = tcm.getColumn(TableValues.ETAT);
		tc.setCellRenderer(new StateRenderer());
		tc.setCellEditor(new StateEditor());
		tc.setMinWidth(25);
		tc.setMaxWidth(25);
		tc = tcm.getColumn(TableValues.SHOW);
		tc.setCellRenderer(new StateButtonRenderer());
		tc.setCellEditor(new StateButtonEditor());
		scrollpane = new JScrollPane(table);
		modif.addActionListener((e) -> modif_actionPerformed(e));
		resul_txt.setForeground(Color.red);
		resul_txt.setHorizontalAlignment(0);
		resul_txt.setFont(Program.font_dialog_small);
		multi.addItemListener((e) -> multi_itemStateChanged(e));

		multi.setText(label_empl);
		txt_nb.setForeground(Color.red);
		txt_nb.setFont(Program.font_dialog_small);
		txt_nbresul.setHorizontalAlignment(4);
		multi.setEnabled(false);

		tabbedPane.addChangeListener((e) -> {
			JTabbedPane pane = (JTabbedPane) e.getSource();
			if(pane.getSelectedComponent().equals(panelYear)){
				panelYear.fillYear();
			}
		});

		//Menu Contextuel
		couper.addActionListener((e) -> couper_actionPerformed(e));
		cut.addActionListener((e) -> couper_actionPerformed(e));
		copier.addActionListener((e) -> copier_actionPerformed(e));
		copy.addActionListener((e) -> copier_actionPerformed(e));
		coller.addActionListener((e) -> coller_actionPerformed(e));
		paste.addActionListener((e) -> coller_actionPerformed(e));
		moveLine.addActionListener((e) -> moveLine_actionPerformed(e));

		couper.setEnabled(false);
		copier.setEnabled(false);
		paste.setEnabled(false);
		popup.add(couper);
		popup.add(copier);
		popup.add(coller);
		cut.setEnabled(false);
		copy.setEnabled(false);
		cut.setAccelerator(KeyStroke.getKeyStroke('X', ActionEvent.CTRL_MASK));
		copy.setAccelerator(KeyStroke.getKeyStroke('C', ActionEvent.CTRL_MASK));
		paste.setAccelerator(KeyStroke.getKeyStroke('V', ActionEvent.CTRL_MASK));
		num_lieu.setEnabled(false);
		column.setEnabled(false);
		line.setEnabled(false);

		setLayout(new MigLayout("","[grow][]","[]10px[][grow][]"));

		panelName = new PanelName();
		panelYear = new PanelYear();
		panelPlace = new PanelPlace();
		panelRequest = new PanelRequest();
		tabbedPane.add(Program.getLabel("Infos077"), panelName);
		tabbedPane.add(Program.getLabel("Infos078"), panelPlace);
		tabbedPane.add(Program.getLabel("Infos219"), panelYear);
		tabbedPane.add(Program.getLabel("Infos318"), panelRequest);
		add(tabbedPane,"growx");
		add(new PanelOption(),"wrap");
		this.add(scrollpane, "grow, wrap, span 2");
		add(selectall,"wrap, span 2, alignx right");

		add(textControl2, "wrap, span 2, alignx center");
		add(resul_txt,"wrap, span 2, alignx center");
		add(modif, "split, span 2, align center");
		add(suppr, "wrap");

		int val = Program.getCaveConfigInt("SEARCH_DEFAULT", 0);
		tabbedPane.setSelectedIndex(val);
		this.setVisible(true);
		if( name != null && name.isVisible() )
			name.requestFocusInWindow();

		Start.menuTools.add(moveLine);
	}

	/**
	 * export_actionPerformed: Fonction pour l'export du résultat de la recherche.
	 *
	 * @param e ActionEvent
	 */
	void export_actionPerformed(ActionEvent e) {
		try {
			Debug("Exporting...");
			LinkedList<Bouteille> v = model.getDatas();
			Export expor = new Export(v);
			JDialog dialog = new JDialog();
			dialog.add(expor);
			dialog.pack();
			dialog.setTitle(Program.getLabel("Infos151"));
			dialog.setLocationRelativeTo(null);
			dialog.setModal(true);
			dialog.setVisible(true);
			Debug("Export Completed");
		}
		catch (Exception exc) {
			Program.showException(exc);
		}
	}

	/**
	 * suppr_actionPerformed: Fonction pour la suppression de bouteilles.
	 *
	 * @param e ActionEvent
	 */
	void suppr_actionPerformed(ActionEvent e) {
		try {
			Debug("Deleting...");
			int max_row = model.getRowCount();
			LinkedList<Bouteille> listToSupp = new LinkedList<Bouteille>();
			// Récupération du nombre de lignes sélectionnées
			for( int i=0; i<max_row; i++ ) {
				if (model.getValueAt(i, TableValues.ETAT).toString() == "true") {
					listToSupp.add(model.getBouteille(i));
				}
			}

			if (listToSupp.size() == 0) {
				//"Aucun vin à supprimer! / Veuillez sélectionner les vins à supprimer.");
				new Erreur(Program.getError("Error064"), Program.getError("Error065"), true);
			}
			else {
				String erreur_txt1;
				String erreur_txt2;
				if (listToSupp.size() == 1) {
					erreur_txt1 = Program.getError("Error067"); //"1 vin sélectionné.");
					erreur_txt2 = Program.getError("Error068"); //"Voulez-vous le supprimer?");
				}
				else {
					erreur_txt1 = MessageFormat.format(Program.getError("Error130"), listToSupp.size()); //vins sélectionnés.");
					erreur_txt2 = Program.getError("Error131"); //"Voulez-vous les supprimer?");
				}
				int resul = JOptionPane.showConfirmDialog(this, erreur_txt1 + " " + erreur_txt2, Program.getLabel("Infos049"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
				if(resul == JOptionPane.YES_OPTION) {
					for(Bouteille bottle:listToSupp) {
						model.removeBouteille(bottle);
						Program.getStorage().addHistory(History.DEL, bottle);
						Program.getStorage().deleteWine(bottle);
						Program.setToTrash(bottle);
						Start.removeBottleTab(bottle);
					}

					RangementUtils.putTabStock();
					Program.updateManagePlacePanel();

					if(listToSupp.size() == 1)
						resul_txt.setText(Program.getLabel("Infos397"));
					else
						resul_txt.setText(MessageFormat.format(Program.getLabel("Infos398"), listToSupp.size()));
				}
			}
		}
		catch (Exception exc) {
			Program.showException(exc);
		}
	}

	/**
	 * num_lieu_itemStateChanged: Fonction appellée lors d'un changement dans la
	 * liste des numéros de lieu.
	 *
	 * @param e ItemEvent
	 */
	void num_lieu_itemStateChanged(ItemEvent e) {
		try {
			Debug("Num_lieu_itemStateChanging...");
			int nb_ligne = 0;
			int num_select = num_lieu.getSelectedIndex();
			int lieu_select = lieu.getSelectedIndex();

			multi.setSelected(false);
			num_lieu.setEnabled(true);
			line.setEnabled(num_select > 0);
			column.setEnabled(false); //true

			resul_txt.setText("");
			if (num_select > 0) {
				nb_ligne = Program.getCave(lieu_select - 1).getNbLignes(num_select - 1);
			}
			else {
				multi.setText(label_empl);
			}
			line.removeAllItems();
			column.removeAllItems();
			line.addItem("");
			for (int i = 1; i <= nb_ligne; i++) {
				line.addItem(Integer.toString(i));

			}
			if (num_select > 0) {
				multi.setText(label_num_empl);
			}
			else {
				multi.setText(label_empl);
			}
		}
		catch (Exception exc) {
			Program.showException(exc);
		}
	}

	/**
	 * lieu_itemStateChanged: Fonction appellée lors d'un changement dans la liste
	 * des emplacements.
	 *
	 * @param e ItemEvent
	 */
	void lieu_itemStateChanged(ItemEvent e) {
		try {
			Debug("Lieu_itemStateChanging...");
			int nb_emplacement = 0;
			int lieu_select = lieu.getSelectedIndex();
			boolean isCaisse = false;
			int start_caisse = 0;

			multi.setEnabled(false);
			num_lieu.setEnabled(lieu_select > 0);

			line.setEnabled(false);
			column.setEnabled(false);

			multi.setSelected(false);
			resul_txt.setText("");
			if (lieu_select > 0) {
				multi.setEnabled(true);
				nb_emplacement = Program.getCave(lieu_select - 1).getNbEmplacements();
				isCaisse = Program.getCave(lieu_select - 1).isCaisse();
				start_caisse = Program.getCave(lieu_select - 1).getStartCaisse();

				if (isCaisse) { //Type caisse
					multi.setEnabled(false);

					num_lieu.removeAllItems();
					num_lieu.addItem(Program.getLabel("Infos223")); //"Toutes");
					for (int i = 0; i < nb_emplacement; i++) {
						num_lieu.addItem(Integer.toString(i + start_caisse));

					}
					line.setVisible(false);
					column.setVisible(false);
					label4.setText(Program.getLabel("Infos158")); //"Numéro de caisse");
					label6.setVisible(false);
					label5.setVisible(false);
				}
				else {
					num_lieu.removeAllItems();
					line.removeAllItems();
					column.removeAllItems();
					num_lieu.addItem("");
					for (int i = 1; i <= nb_emplacement; i++) {
						num_lieu.addItem(Integer.toString(i));
					}
					line.setVisible(true);
					column.setVisible(true);
					label4.setText(Program.getLabel("Infos082")); //"Numéro du lieu");
					label5.setVisible(true);
					label6.setVisible(true);
				}
				multi.setText(label_empl);
			}
		}
		catch (Exception exc) {
			Program.showException(exc);
		}
	}

	/**
	 * line_itemStateChanged: Fonction appellée lors d'un changement dans la liste
	 * des numéros de ligne.
	 *
	 * @param e ItemEvent
	 */
	void line_itemStateChanged(ItemEvent e) {
		try {
			Debug("Line_itemStateChanging...");
			int nb_col = 0;
			int num_select = line.getSelectedIndex();
			int emplacement = num_lieu.getSelectedIndex();
			int lieu_select = lieu.getSelectedIndex();

			multi.setSelected(false);
			column.setEnabled(num_select > 0);

			resul_txt.setText("");
			if (num_select > 0) {
				nb_col = Program.getCave(lieu_select - 1).getNbColonnes(emplacement - 1, num_select - 1);
				multi.setText(label_ligne);
			}
			else {
				multi.setText(label_num_empl);
			}
			column.removeAllItems();
			column.addItem("");
			for (int i = 1; i <= nb_col; i++) {
				column.addItem(Integer.toString(i));
			}
		}
		catch (Exception exc) {
			Program.showException(exc);
		}
	}


	/**
	 * cherche_actionPerformed: Fonction de recherche.
	 *
	 * @param e ActionEvent
	 */
	void cherche_actionPerformed(ActionEvent e) {
		//Fonction de recherche
		try {
			Debug("Cherche_actionPerforming...");
			name.removeMenu();
			txt_nb.setText("-");
			txt_nbresul.setText(Program.getLabel("Infos222"));
			resul_txt.setText(Program.getLabel("Infos087")); //"Recherche en cours...");
			new Thread(this).start();
		}
		catch (Exception exc) {
			Program.showException(exc);
		}
	}

	/**
	 * vider_actionPerformed: Fonction pour vider la liste de recherche.
	 *
	 * @param e ActionEvent
	 */
	void vider_actionPerformed(ActionEvent e) {
		try {
			//Efface la recherche
			Debug("vider_actionPerforming...");
			SwingUtilities.invokeLater(() -> emptyRows());
		}
		catch (Exception exc) {
			Program.showException(exc);
		}
	}

	private void emptyRows() {
		Debug("emptyRows...");
		txt_nb.setText("-");
		txt_nbresul.setText(Program.getLabel("Infos222"));
		modif.setEnabled(false);
		suppr.setEnabled(false);
		export.setEnabled(false);
		selectall.setSelected(false);
		resul_txt.setText("");
		model.removeAll();
	}

	//	Fonction de recherche full text avec carat!res spéciaux (*, ?)
	/**
	 * full_search: Fonction interne de recherche par nom.
	 *
	 */
	private boolean full_search() {
		Debug("Searching by text");
		//Recherche saisie
		String txt = name.getText();
		boolean already_found = false;
		Debug("Starting Full_search...");
		model.removeAll();
		Debug("Preparing statement...");
		for (Bouteille bottle : Program.getStorage().getAllList()) {
			int index = txt.indexOf("*");
			while (index != -1) {
				if (index != 0) {
					if (index < txt.length()) {
						txt = txt.substring(0, index) + ".{0,}" + txt.substring(index + 1);
					}
					else {
						txt = txt.substring(0, index) + ".{0,}";
					}
				}
				else {
					if (index < txt.length()) {
						txt = ".{0,}" + txt.substring(index + 1);
					}
					else {
						txt = ".{0,}";
					}
				}
				index = txt.indexOf("*");
			}
			index = txt.indexOf("?");
			while (index != -1) {
				if (index != 0) {
					if (index < txt.length()) {
						txt = txt.substring(0, index) + ".{1}" + txt.substring(index + 1);
					}
					else {
						txt = txt.substring(0, index) + ".{1}";
					}
				}
				else {
					if (index < txt.length()) {
						txt = ".{1}" + txt.substring(index + 1);
					}
					else {
						txt = ".{1}";
					}
				}
				index = txt.indexOf("?");
			}

			java.util.regex.Pattern p = null;
			if (casse.isSelected()) {
				p = java.util.regex.Pattern.compile(txt);
			}
			else {
				p = java.util.regex.Pattern.compile(txt, java.util.regex.Pattern.CASE_INSENSITIVE);
			}
			java.util.regex.Matcher m = p.matcher(bottle.getNom());
			if (m.matches()) {
				if(!model.hasBottle(bottle))
					model.addBouteille(bottle);
				else {
					already_found = true;
				}
			}
		}
		int nRows = model.getRowCount();
		updateLabelBottleNumber();
		if(nRows > 0) {
			modif.setEnabled(true);
			suppr.setEnabled(true);
		}
		resul_txt.setText(Program.getLabel("Infos088")); //"Recherche terminée.");
		return already_found;
	}


	/**
	 * modif_actionPerformed: Fonction appellée lors d'une modification de
	 * bouteilles.
	 *
	 * @param e ActionEvent
	 */
	void modif_actionPerformed(ActionEvent e) {
		try {
			Debug("modif_actionPerforming...");
			int max_row = model.getRowCount();
			int row = 0;
			LinkedList<Bouteille> listToModify = new LinkedList<Bouteille>();
			do {
				if ((boolean) model.getValueAt(row, TableValues.ETAT)) {
					listToModify.add(model.getBouteille(row));
				}
				row++;
			}
			while (row < max_row);

			if (listToModify.size() == 0) {
				//"Aucun vin à modifier! / Veuillez sélectionner les vins à modifier.");
				new Erreur(Program.getError("Error071"), Program.getError("Error072"), true);
			}
			else {
				Debug("Modifying "+listToModify.size()+" bottles...");
				modifyBottles(listToModify);
			}
		}
		catch (Exception exc) {
			Program.showException(exc);
		}
	}

	private void modifyBottles(LinkedList<Bouteille> listToModify) {
		new OpenAddVinAction(listToModify).actionPerformed(null);
	}

	/**
	 * multi_itemStateChanged: Fonction pour activer la recheche sur plusieurs
	 * lieu / numéro de lieu / ligne.
	 *
	 * @param e ItemEvent
	 */
	void multi_itemStateChanged(ItemEvent e) {
		if (multi.isSelected()) {
			if (line.getSelectedIndex() > 0) {
				column.setEnabled(false);
			}
			else if (num_lieu.getSelectedIndex() > 0) {
				column.setEnabled(false);
				line.setEnabled(false);
			}
			else if (lieu.getSelectedIndex() > 0) {
				column.setEnabled(false);
				line.setEnabled(false);
				num_lieu.setEnabled(false);
			}
		}
		else {
			if (lieu.getSelectedIndex() != 0) {
				num_lieu.setEnabled(true);
			}
			if (num_lieu.getSelectedIndex() != 0) {
				line.setEnabled(true);
			}
			if (line.getSelectedIndex() != 0) {
				column.setEnabled(true);
			}
		}
	}

	/**
	 * run: Réalise la recherche et l'affiche dans la JTable
	 */
	public void run() {
		try {
			Debug("Running...");
			boolean already_found = false;
			cherche.setEnabled(false);
			vider.setEnabled(false);
			export.setEnabled(false);
			selectall.setSelected(false);
			selectall.setEnabled(false);
			if (empty_search.isSelected()) {
				emptyRows();
			}
			if(tabbedPane.getSelectedIndex() == 3) {
				already_found = searchByRequest();
			}
			else if (tabbedPane.getSelectedIndex() == 0) {
				already_found = full_search();
			}
			else if (tabbedPane.getSelectedIndex() == 1) {
				already_found = searchByPlace();
			}
			else if (tabbedPane.getSelectedIndex() == 2) {
				already_found = searchByYear();
			}
			if (already_found) {
				int param = Program.getCaveConfigInt("DONT_SHOW_INFO", 0);
				if (param == 0) {
					//"Lorsqu'une bouteille recherchée est déjà présente dans la liste");
					//"des vins trouvés, elle n'est pas ajoutée en double.");
					new Erreur(Program.getError("Error133") , Program.getError("Error134"), true, "DONT_SHOW_INFO");
				}
			}
			resul_txt.setText(Program.getLabel("Infos088")); //"Recherche terminée.");
			if (model.getRowCount() > 0) {
				SwingUtilities.invokeLater(() -> {
					model.fireTableDataChanged();
				});
				export.setEnabled(true);
				modif.setEnabled(true);
				suppr.setEnabled(true);
			}
			cherche.setEnabled(true);
			vider.setEnabled(true);
			selectall.setEnabled(true);
		}
		catch (Exception exc) {
			Program.showException(exc);
		}
	}

	private boolean searchByRequest() {
		boolean already_found = false;
		Debug("Search by request");
		Collection<Bouteille> bouteilles = CollectionFilter.select(Program.getStorage().getAllList() , panelRequest.getPredicates()).getResults();
		if(bouteilles != null) {
			for(Bouteille b : bouteilles) {
				if(!model.hasBottle(b))
					model.addBouteille(b);
				else
					already_found = true;
			}
		}
		Debug(model.getRowCount()+" bottle(s) found");
		updateLabelBottleNumber();
		Debug("Search by request completed");
		return already_found;
	}

	/**
	 * 
	 */
	private static void updateLabelBottleNumber() {
		txt_nb.setText(Integer.toString(model.getRowCount()));
		if (model.getRowCount() == 0) {
			txt_nbresul.setText(Program.getLabel("Infos222"));
		}
		else {
			txt_nbresul.setText(Program.getLabel("Infos239"));
		}
	}

	private boolean searchByPlace() {
		Debug("Searching by place");
		int lieu_select = lieu.getSelectedIndex();
		boolean already_found = false;

		if (lieu_select == 0) {
			Debug("ERROR: No place selected");
			new Erreur(Program.getError("Error055")); //Select emplacement
			resul_txt.setText("");
			return false;
		}
		
		Rangement rangement = Program.getCave(lieu_select - 1); 
		if (rangement.isCaisse()) {
			//Pour la caisse
			int lieu_num = num_lieu.getSelectedIndex();
			resul_txt.setText(Program.getLabel("Infos087")); //"Recherche en cours...");
			int nb_bottles = 0;
			int nb_empl_cave = rangement.getNbEmplacements();
			int boucle_toutes = 1;
			int start_boucle = 0;
			Bouteille b = null;
			if (lieu_num == 0) { //New
				start_boucle = 1;
				boucle_toutes = nb_empl_cave + 1;
			}
			else {
				start_boucle = lieu_num;
				boucle_toutes = lieu_num + 1;
				nb_bottles = rangement.getNbCaseUse(lieu_num - 1); //lieu_num
			}
			for (int x = start_boucle; x < boucle_toutes; x++) {
				nb_bottles = rangement.getNbCaseUse(x - 1);
				for (int l = 0; l < nb_bottles; l++) {
					b = rangement.getBouteilleCaisseAt(x - 1, l); //lieu_num
					if(b != null) {
						lieu_num = b.getNumLieu();
						if(!model.hasBottle(b)) {
							model.addBouteille(b);
						}
						else
							already_found = true;
					}
					else
						Debug("No bottle found in lieuselect-1="+(lieu_select - 1)+" x-1="+(x-1)+" l+1="+(l+1));

				} //Fin for
			} //Fin for
			Debug(model.getRowCount()+" bottle(s) found");
			updateLabelBottleNumber();

			if (model.getRowCount() > 0) {
				modif.setEnabled(true);
				suppr.setEnabled(true);
			}
			resul_txt.setText(Program.getLabel("Infos088")); //"Recherche terminée.");
		}
		else {
			//Type armoire
			if (!multi.isSelected()) {
				int lieu_num = num_lieu.getSelectedIndex();
				int ligne = line.getSelectedIndex();
				int colonne = column.getSelectedIndex();
				if (lieu_num == 0) {
					Debug("ERROR: No Num place selected");
					resul_txt.setText("");
					new Erreur(Program.getError("Error056")); //"Veuillez sélectionner un numéro d'emplacement!";
					return false;
				}
				if (ligne == 0) {
					Debug("ERROR: No Line selected");
					resul_txt.setText("");
					new Erreur(Program.getError("Error057")); //"Veuillez sélectionner un numéro de ligne!";
					return false;
				}
				if (colonne == 0) {
					Debug("ERROR: No column selected");
					resul_txt.setText("");
					new Erreur(Program.getError("Error058")); //"Veuillez sélectionner un numéro de colonne!";
					return false;
				}
				Bouteille b = rangement.getBouteille(lieu_num - 1, ligne - 1, colonne -1);
				resul_txt.setText(Program.getLabel("Infos087")); //"Recherche en cours...");

				if (b == null) {
					resul_txt.setText(Program.getLabel("Infos224")); //"Echec de la recherche.");
					new Erreur(Program.getError("Error066")); //Aucune bouteille trouve
					txt_nb.setText("0");
					txt_nbresul.setText(Program.getLabel("Infos222"));
					modif.setEnabled(false);
					suppr.setEnabled(false);
				}
				else {
					if(!model.hasBottle(b)) {
						model.addBouteille(b);
					}
					else
						already_found = true;
					updateLabelBottleNumber();
					if (model.getRowCount() > 0) {
						modif.setEnabled(true);
						suppr.setEnabled(true);
					}
					resul_txt.setText(Program.getLabel("Infos088")); //"Recherche terminée.");
				}
			}
			else { //multi.getState == true
				//Cas recherche toutes bouteille (lieu, num_lieu, ligne)
				int lieu_num = num_lieu.getSelectedIndex();
				int ligne = line.getSelectedIndex();
				int colonne = column.getSelectedIndex();
				if (multi.getText().compareTo(label_empl) != 0) {
					if (lieu_num == 0) {
						Debug("ERROR: No Num place selected");
						new Erreur(Program.getError("Error056")); //"Veuillez sélectionner un numéro d'emplacement!";
						resul_txt.setText("");
						return false;
					}
					
					if (multi.getText().compareTo(label_num_empl) != 0) {
						if (ligne == 0) {
							Debug("ERROR: No line selected");
							new Erreur(Program.getError("Error057")); //"Veuillez sélectionner un numéro de ligne!";
							resul_txt.setText("");
							return false;
						}

						if (multi.getText().compareTo(label_ligne) != 0) {
							if (colonne == 0) {
								Debug("ERROR: No column selected");
								new Erreur(Program.getError("Error058")); //"Veuillez sélectionner un numéro de colonne!";
								resul_txt.setText("");
								return false;
							}
						}
					}
				}
				resul_txt.setText(Program.getLabel("Infos087")); //"Recherche en cours...");
				//Recherche toutes les bouteilles d'un emplacement
				int nb_empl = rangement.getNbEmplacements();
				int i_deb = 1;
				int j_deb = 1;
				int i_fin = nb_empl;
				int j_fin = 0;
				if (multi.getText().compareTo(label_num_empl) == 0) {
					i_deb = lieu_num;
					i_fin = lieu_num;
				}
				if (multi.getText().compareTo(label_ligne) == 0) {
					i_deb = lieu_num;
					i_fin = lieu_num;
					j_deb = ligne;
					j_fin = ligne;
				}
				for (int i = i_deb; i <= i_fin; i++) {
					int nb_lignes = rangement.getNbLignes(i - 1);
					if (multi.getText().compareTo(label_ligne) != 0) {
						j_fin = nb_lignes;
					}
					for (int j = j_deb; j <= j_fin; j++) {
						int nb_colonnes = rangement.getNbColonnes(i - 1, j - 1);
						for (int k = 1; k <= nb_colonnes; k++) {
							Bouteille b = rangement.getBouteille(i - 1, j - 1, k - 1);

							lieu_num = i;
							ligne = j;
							colonne = k;
							if (b != null) {
								//Ajout de la bouteille dans la liste si elle n'y ait pas déjà
								if(!model.hasBottle(b)) {
									model.addBouteille(b);
								}
								else
									already_found = true;
							}
						}
					}
				}
				int nbBottle = model.getRowCount();
				Debug(nbBottle+" bottle(s) selected");
				updateLabelBottleNumber();
				if (nbBottle > 0) {
					modif.setEnabled(true);
					suppr.setEnabled(true);
				}
				resul_txt.setText(Program.getLabel("Infos088")); //"Recherche terminée.");
			} //Fin else multi
		} //fin else
		
		return already_found;
	}

	private boolean searchByYear() {
		Debug("Searching by year");
		int annee;
		String lelieu = "";
		int item_select = year.getSelectedIndex();
		int nb_year = year.getItemCount();
		String sYear = year.getSelectedItem().toString();

		boolean already_found = false;
		if(Bouteille.isNonVintageYear(sYear))
			annee = Bouteille.NON_VINTAGE_INT;
		else {
			try {
				annee = Integer.parseInt(sYear);
			}
			catch (Exception e) {annee = 0;
			}
		}

		resul_txt.setText(Program.getLabel("Infos087")); //"Recherche en cours...");
		for (Bouteille b : Program.getStorage().getAllList()) {
			if(b == null)
				continue;
			lelieu = b.getEmplacement();

			//Récupération du numéro du lieu
			Rangement r = Program.getCave(lelieu);

			if (annee == b.getAnneeInt() && nb_year != item_select && r != null) {
				if(!model.hasBottle(b)) {
					model.addBouteille(b);
				}
				else {
					already_found = true;
				}
			}
			else {
				if (b.getAnneeInt() < 1000 && (nb_year - 1) == item_select) { // Cas Autre
					if(!model.hasBottle(b)) {
						model.addBouteille(b);
					}
					else {
						already_found = true;
					}
				}
			}
		}
		Debug(model.getRowCount()+" bottle(s) found");
		updateLabelBottleNumber();
		return already_found;
	}

	/**
	 * keylistener_actionPerformed: Ecoute clavier.
	 *
	 * @param e KeyEvent
	 */
	void keylistener_actionPerformed(KeyEvent e) {
		if ( (e.getKeyCode() == RECHERCHE && e.isControlDown()) || e.getKeyCode() == KeyEvent.VK_ENTER) {
			cherche_actionPerformed(null);
		}
		if (e.getKeyCode() == MODIF && modif.isEnabled() && e.isControlDown()) {
			modif_actionPerformed(null);
		}
		if (e.getKeyCode() == SUPPR && suppr.isEnabled() && e.isControlDown()) {
			suppr_actionPerformed(null);
		}
		if (e.getKeyCode() == EXPORT && export.isEnabled() && e.isControlDown()) {
			export_actionPerformed(null);
		}
		if (e.getKeyCode() == KeyEvent.VK_F1) {
			Program.getAide();
		}
		if (e.getKeyCode() == KeyEvent.VK_C) {
			copier_actionPerformed(null);
		}
		if (e.getKeyCode() == KeyEvent.VK_X) {
			couper_actionPerformed(null);
		}
		if (e.getKeyCode() == KeyEvent.VK_V) {
			coller_actionPerformed(null);
		}
	}

	/**
	 * selectall_actionPerformed: Permet de sélectionner toutes les lignes de la
	 * JTable
	 *
	 * @param e ActionEvent
	 */
	void selectall_actionPerformed(ActionEvent e) {
		Debug("selectall_actionPerforming...");
		modif.setEnabled(false);
		suppr.setEnabled(false);
		for (int i = 0; i < model.getRowCount(); i++) {
			model.setValueAt(new Boolean(selectall.isSelected()), i, TableValues.ETAT);
		}
		if(model.getRowCount() > 0) {
			modif.setEnabled(true);
			suppr.setEnabled(true);
		}
		table.updateUI();
		Debug("selectall_actionPerforming... Done");
	}

	/**
	 * empty_search_actionPerformed: Permet de vider automatiquement la recherche
	 *
	 * @param e ActionEvent
	 */
	void empty_search_actionPerformed(ActionEvent e) {
		if (empty_search.isSelected())
			Program.putCaveConfigString("EMPTY_SEARCH", "ON");
		else
			Program.putCaveConfigString("EMPTY_SEARCH", "OFF");
	}

	/**
	 * param_actionPerformed: Appelle la fenêtre de paramètres.
	 *
	 * @param e ActionEvent
	 */
	void param_actionPerformed(ActionEvent e) {
		Debug("param_actionPerforming...");
		String titre = Program.getLabel("Infos304");
		String message2 = Program.getLabel("Infos305");
		String titre_properties[] = new String[4];
		titre_properties[0] = Program.getLabel("Infos077");
		titre_properties[1] = Program.getLabel("Infos078");
		titre_properties[2] = Program.getLabel("Infos219");
		titre_properties[3] = Program.getLabel("Infos340");
		String default_value[] = new String[4];
		String key_properties[] = new String[4];
		key_properties[0] = "SEARCH_DEFAULT";
		key_properties[1] = "SEARCH_DEFAULT";
		key_properties[2] = "SEARCH_DEFAULT";
		key_properties[3] = "LAST_SEARCH_COUNT";
		String val = Program.getCaveConfigString(key_properties[0], "0");
		int nVal = Program.getCaveConfigInt(key_properties[3], 20);

		default_value[0] = "false";
		default_value[1] = "false";
		default_value[2] = "false";
		if (val.equals("0")) {
			default_value[0] = "true";
		}
		if (val.equals("1")) {
			default_value[1] = "true";
		}
		if (val.equals("2")) {
			default_value[2] = "true";
		}
		default_value[3] = Integer.toString(nVal);
		String type_objet[] = {"MyCellarRadioButton", "MyCellarRadioButton", "MyCellarRadioButton", "MyCellarSpinner"};
		MyOptions myoptions = new MyOptions(titre, "", message2, titre_properties, default_value, key_properties, type_objet, Program.getCaveConfig(), false);
		myoptions.setVisible(true);
	}

	/**
	 * couper_actionPerformed: Couper
	 *
	 * @param e ActionEvent
	 */
	void couper_actionPerformed(ActionEvent e) {
		String txt = "";
		JTextField jtf = (JTextField) objet1;
		txt = jtf.getSelectedText();
		jtf.setText(jtf.getText().substring(0, jtf.getSelectionStart()) + jtf.getText().substring(jtf.getSelectionEnd()));
		clipboard.copier(txt);
	}

	/**
	 * copier_actionPerformed: Copier
	 *
	 * @param e ActionEvent
	 */
	void copier_actionPerformed(ActionEvent e) {
		String txt = "";
		JTextField jtf = (JTextField) objet1;
		txt = jtf.getSelectedText();
		clipboard.copier(txt);
	}

	/**
	 * coller_actionPerformed: Couper
	 *
	 * @param e ActionEvent
	 */
	void coller_actionPerformed(ActionEvent e) {

		try {
			JTextField jtf = (JTextField) objet1;
			jtf.setText(jtf.getText().substring(0, jtf.getSelectionStart()) + clipboard.coller() + jtf.getText().substring(jtf.getSelectionEnd()));
		}
		catch (NullPointerException npe) {}
		;
	}

	/**
	 * aide_actionPerformed: Aide
	 *
	 * @param e ActionEvent
	 */
	void aide_actionPerformed(ActionEvent e) {
		Program.getAide();
	}

	/**
	 * <p>Titre : Cave à vin</p>
	 * <p>Description : Votre description</p>
	 * <p>Copyright : Copyright (c) 1998</p>
	 * <p>Société : Seb Informatique</p>
	 * @author Sébastien Duché
	 * @version 0.1
	 * @since 17/04/05
	 */
	class PopupListener extends MouseAdapter {

		public void mousePressed(MouseEvent e) {
			maybeShowPopup(e);
		}

		public void mouseClicked(MouseEvent e) {
			maybeShowPopup(e);
		}

		public void mouseEntered(MouseEvent e) {
		}

		public void mouseExited(MouseEvent e) {
		}

		public void mouseReleased(MouseEvent e) {
		}

		private void maybeShowPopup(MouseEvent e) {
			try {
				try {
					JTextField jtf = (JTextField) e.getComponent();
					if (jtf.isEnabled() && jtf.isVisible()) {
						objet1 = e.getComponent();
					}
				}
				catch (Exception ee) {}
				if (e.getButton() == MouseEvent.BUTTON3) {
					if (e.getComponent().isFocusable() && e.getComponent().isEnabled()) {
						e.getComponent().requestFocus();
						JTextField jtf = (JTextField) objet1;
						if (jtf.getSelectedText() == null) {
							couper.setEnabled(false);
							copier.setEnabled(false);
						}
						else {
							couper.setEnabled(true);
							copier.setEnabled(true);
						}
						if (jtf.isEnabled() && jtf.isVisible()) {
							popup.show(e.getComponent(), e.getX(), e.getY());
						}
					}
				}
				if (e.getButton() == MouseEvent.BUTTON1) {
					if (e.getComponent().isFocusable() && e.getComponent().isEnabled()) {
						e.getComponent().requestFocus();
						JTextField jtf = (JTextField) objet1;
						if (jtf.getSelectedText() == null) {
							cut.setEnabled(false);
							copy.setEnabled(false);
						}
						else {
							cut.setEnabled(true);
							copy.setEnabled(true);
						}
					}
				}
			}
			catch (NullPointerException npe) {}
		}
	}

	/**
	 * showfile_actionPerformed: Appelle la fenêtre de visualisation du fichier.
	 *
	 * @param a ActionEvent
	 */
	void showfile_actionPerformed(ActionEvent a) {
		try {
			if (Program.showfile == null) {
				Program.showfile = new ShowFile();
			}
		}
		catch (Exception e) {
			Program.showException(e);
		}
	}

	/**
	 * moveLine_actionPerformed: Appelle la fenêtre de déplacement des vins d'une ligne
	 *
	 * @param a ActionEvent
	 */
	void moveLine_actionPerformed(ActionEvent a) {
		new MoveLine();
	}

	/**
	 * Debug
	 *
	 * @param sText String
	 */
	public static void Debug(String sText) {
		Program.Debug("Search: " + sText);
	}

	class PanelName extends JPanel{
		private static final long serialVersionUID = -2125241372841734287L;

		public PanelName(){
			name.setEditable(true);
			name.addMouseListener(popup_l);
			name.setFont(Program.font_panel);
			setLayout(new MigLayout("","[grow]","[]"));
			add(new MyCellarLabel(Program.getLabel("Infos085")),"wrap");
			add(name,"grow");
		}
	}

	class PanelPlace extends JPanel{
		private static final long serialVersionUID = -2601861017578176513L;

		public PanelPlace(){
			setLayout(new MigLayout("","[grow]","[][][][]"));
			add(label3,"");
			add(label4,"");
			add(label5,"");
			add(label6,"wrap");
			add(lieu,"");
			add(num_lieu,"");
			add(line,"");
			add(column,"wrap");
			add(multi,"span 4");
		}
	}

	class PanelYear extends JPanel{
		private static final long serialVersionUID = 8579611890313378015L;
		public PanelYear(){
			setLayout(new MigLayout("","",""));
			MyCellarLabel labelYear = new MyCellarLabel(Program.getLabel("Infos133"));
			add(labelYear,"wrap");
			add(year,"");
		}
		public void fillYear(){
			year.removeAllItems();
			int an_array[] = Program.getAnnees();
			String mes_string[] = new String[an_array.length];
			for (int y = 0; y < an_array.length; y++) {
				mes_string[y] = Integer.toString(an_array[y]);
			}
			java.util.Arrays.sort(mes_string, java.text.Collator.getInstance());
			for (int y = 0; y < mes_string.length; y++) {
				if (Integer.parseInt(mes_string[y]) > 1000 && Integer.parseInt(mes_string[y]) < 9000) {
					year.addItem(mes_string[y]);
				}
			}
			year.addItem(Program.getLabel("Infos390")); //NV
			year.addItem(Program.getLabel("Infos225")); //"Autre");
		}
	}

	class PanelOption extends JPanel{
		private static final long serialVersionUID = 6761656985728428915L;

		public PanelOption(){
			setLayout(new MigLayout("","","[][]"));
			add(cherche,"wrap");
			add(vider,"");
			add(export,"wrap");
			add(empty_search,"wrap, span 2");
			add(txt_nbresul,"split");
			add(txt_nb,"wrap");
		}
	}

	@Override
	public boolean tabWillClose(TabEvent event) {
		return true;
	}

	@Override
	public void tabClosed() {
		Start.updateMainPanel();
	}

	public void setUpdateView(){
		updateView = true;
	}

	public void updateView() {
		if(!updateView)
			return;
		updateView = false;
		lieu.removeAllItems();
		lieu.addItem("");
		for (Rangement r : Program.getCave()) {
			lieu.addItem(r.getNom());
		}
	}

	public static void removeBottle(Bouteille bottleToDelete) {
		model.deleteBottle(bottleToDelete);
		model.fireTableDataChanged();
		updateLabelBottleNumber();
	}

	public static void updateTable() {
		SwingUtilities.invokeLater(() -> model.fireTableDataChanged());
		
	}

	public static void clearResults() {
		SwingUtilities.invokeLater(() -> model.removeAll());
	}

	public void cut() {
		if( tabbedPane.getSelectedIndex() == 0 ) {
			String text = name.getSelectedText();
			String fullText = name.getText();
			if(text != null) {
				name.setText(fullText.substring(0, name.getSelectionStart()) + fullText.substring(name.getSelectionEnd()));
				clipboard.copier(text);
			}
		}
	}

	public void copy() {
		if( tabbedPane.getSelectedIndex() == 0 )
		{
			//if(name.hasFocus())
			{
				String text = name.getSelectedText();
				if(text != null)
				{
					clipboard.copier(text);
				}
			}
		}
	}

	public void paste() {
		if( tabbedPane.getSelectedIndex() == 0 )
		{
			//if(name.hasFocus())
			{
				String fullText = name.getText();
				name.setText(fullText.substring(0,  name.getSelectionStart()) + clipboard.coller() + fullText.substring(name.getSelectionEnd()));
			}
		}
	}

}
