package mycellar;

import mycellar.actions.OpenWorkSheetAction;
import mycellar.core.ICutCopyPastable;
import mycellar.core.IMyCellar;
import mycellar.core.IUpdatable;
import mycellar.core.MyCellarButton;
import mycellar.core.MyCellarCheckBox;
import mycellar.core.MyCellarComboBox;
import mycellar.core.MyCellarLabel;
import mycellar.core.MyCellarSettings;
import mycellar.core.PopupListener;
import mycellar.requester.CollectionFilter;
import mycellar.requester.ui.PanelRequest;
import mycellar.vignobles.CountryVignobles;
import net.miginfocom.swing.MigLayout;

import javax.swing.JDialog;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseListener;
import java.text.Collator;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>Titre : Cave &agrave; vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2003</p>
 * <p>Soci&eacute;t&eacute; : Seb Informatique</p>
 * @author S&eacute;bastien Duch&eacute;
 * @version 20.1
 * @since 08/08/19
 */
public final class Search extends JPanel implements Runnable, ITabListener, ICutCopyPastable, IMyCellar, IUpdatable {

	private static final long serialVersionUID = 8497660112193602839L;
	private final JTable table;
	private static final TableValues MODEL = new TableValues();
	private static final MyCellarLabel TXT_NBRESUL = new MyCellarLabel();
	private static final MyCellarLabel TXT_NB = new MyCellarLabel();
	private final MyCellarButton suppr = new MyCellarButton(MyCellarImage.DELETE);
	private final MyCellarButton export = new MyCellarButton(MyCellarImage.EXPORT);
	private final MyCellarButton modif = new MyCellarButton(MyCellarImage.WINE);
	private final MyCellarComboBox<Rangement> lieu = new MyCellarComboBox<>();
	private final MyCellarComboBox<String> num_lieu = new MyCellarComboBox<>();
	private final MyCellarComboBox<String> column = new MyCellarComboBox<>();
	private final MyCellarComboBox<String> line = new MyCellarComboBox<>();
	private final MyCellarComboBox<String> year = new MyCellarComboBox<>();
	private final MyCellarLabel label3 = new MyCellarLabel();
	private final MyCellarLabel label4 = new MyCellarLabel();
	private final MyCellarLabel label5 = new MyCellarLabel();
	private final MyCellarLabel label6 = new MyCellarLabel();
	private final TextFieldPopup name;
	private final MyCellarButton cherche = new MyCellarButton(MyCellarImage.SEARCH);
	private final MyCellarButton vider = new MyCellarButton();
	private final char RECHERCHE = Program.getLabel("RECHERCHE").charAt(0);
	private final char MODIF = Program.getLabel("MODIF").charAt(0);
	private char SUPPR = Program.getLabel("SUPPR").charAt(0);
	private char EXPORT = Program.getLabel("EXPORT").charAt(0);
	private final MyCellarLabel resul_txt = new MyCellarLabel();
	private final MyCellarCheckBox multi = new MyCellarCheckBox();
	private final String label_empl = Program.getLabel("Infos101"); //"Tous les vins de l'emplacement");
	private final String label_num_empl = Program.getLabel("Infos102"); //"Tous les vins du lieu");
	private final String label_ligne = Program.getLabel("Infos103"); //"Tous les vins de la ligne");
	private final MyCellarCheckBox selectall = new MyCellarCheckBox();
	private final MyCellarButton addToWorksheet = new MyCellarButton(MyCellarImage.WORK);
	private final MyCellarCheckBox empty_search = new MyCellarCheckBox();
	private final MouseListener popup_l = new PopupListener();
	private final JTabbedPane tabbedPane = new JTabbedPane();
	private PanelYear panelYear;
	private final PanelRequest panelRequest;
	private boolean updateView = false;
	private final JMenuItem moveLine = new JMenuItem(Program.getLabel("Infos365"));

	/**
	 * Search: Constructeur via Start.
	 */
	public Search() {

		Debug("Constructor");
		TXT_NB.setText("-");
		TXT_NBRESUL.setText(Program.getLabel("Infos222")); //"Bouteille(s) trouvee(s): ");

		if (Program.getCaveConfigBool(MyCellarSettings.EMPTY_SEARCH, false)) {
			empty_search.setSelected(true);
		}

		name = new TextFieldPopup(Program.getStorage().getBottleNames(), 150) {
			@Override
			public void doAfterValidate() {
				new Thread(Search.this).start();
			}
		};
		//name.setCaseSensitive(false);

		//Ajout des lieux
		lieu.removeAllItems();
		lieu.addItem(Program.EMPTY_PLACE);
		Program.getCave().forEach(lieu::addItem);

		export.setText(Program.getLabel("Infos120")); //"Exporter le resultat");
		export.setMnemonic(EXPORT);
		selectall.setText(Program.getLabel("Infos126")); //"Tout selectionner");
		empty_search.setText(Program.getLabel("Infos275")); //Vider automatiquement
		selectall.setHorizontalAlignment(SwingConstants.RIGHT);
		selectall.setHorizontalTextPosition(SwingConstants.LEFT);

		selectall.addActionListener(this::selectall_actionPerformed);
		addToWorksheet.setText(Program.getLabel("Search.AddWorksheet"));
		addToWorksheet.addActionListener(this::addToWorksheet_actionPerformed);
		empty_search.addActionListener(this::empty_search_actionPerformed);
		export.addActionListener(this::export_actionPerformed);
		suppr.setText(Program.getLabel("Infos051")); //"Supprimer");
		suppr.setMnemonic(SUPPR);
		MyCellarLabel textControl2 = new MyCellarLabel(Program.getLabel("Infos080")); //"Selectionner un(des) vin(s) dans la liste. Cliquer sur \"Modifier\" ou \"Supprimer\"");
		modif.setText(Program.getLabel("Infos079")); //"Modifier");
		modif.setMnemonic(MODIF);
		if (MODEL.getRowCount() == 0) {
			modif.setEnabled(false);
			suppr.setEnabled(false);
			export.setEnabled(false);
		}

		suppr.addActionListener(this::suppr_actionPerformed);

		table = new JTable(MODEL);
		table.setAutoCreateRowSorter(true);
		label3.setText(Program.getLabel("Infos081")); //"Emplacement du vin");
		label4.setText(Program.getLabel("Infos082")); //"Numero du lieu");
		label5.setText(Program.getLabel("Infos028")); //"Ligne");
		label6.setText(Program.getLabel("Infos083")); //"Colonne");
		cherche.addActionListener(this::cherche_actionPerformed);
		cherche.setText(Program.getLabel("Infos084")); //"Chercher");
		cherche.setMnemonic(RECHERCHE);
		vider.addActionListener(this::vider_actionPerformed);
		vider.setText(Program.getLabel("Infos220")); //"Effacer les resultats");
		line.addItemListener(this::line_itemStateChanged);
		lieu.addItemListener(this::lieu_itemStateChanged);

		addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				keylistener_actionPerformed(e);
			}
		});

		num_lieu.addItemListener(this::num_lieu_itemStateChanged);
		TableColumnModel tcm = table.getColumnModel();
		TableColumn[] tc1 = new TableColumn[5];
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
		JScrollPane scrollpane = new JScrollPane(table);
		modif.addActionListener(this::modif_actionPerformed);
		resul_txt.setForeground(Color.red);
		resul_txt.setHorizontalAlignment(SwingConstants.CENTER);
		resul_txt.setFont(Program.FONT_DIALOG_SMALL);
		multi.addItemListener(this::multi_itemStateChanged);

		multi.setText(label_empl);
		TXT_NB.setForeground(Color.red);
		TXT_NB.setFont(Program.FONT_DIALOG_SMALL);
		TXT_NBRESUL.setHorizontalAlignment(SwingConstants.RIGHT);
		multi.setEnabled(false);

		tabbedPane.addChangeListener((e) -> {
			JTabbedPane pane = (JTabbedPane) e.getSource();
			if(pane.getSelectedComponent().equals(panelYear)){
				panelYear.fillYear();
			}
		});

		moveLine.addActionListener((e) -> new MoveLine());

		num_lieu.setEnabled(false);
		column.setEnabled(false);
		line.setEnabled(false);

		setLayout(new MigLayout("","[grow][]","[]10px[][grow][]"));

		JPanel panelName = new PanelName();
		panelYear = new PanelYear();
		JPanel panelPlace = new PanelPlace();
		panelRequest = new PanelRequest();
		tabbedPane.add(Program.getLabel("Infos077"), panelName);
		tabbedPane.add(Program.getLabel("Infos078"), panelPlace);
		tabbedPane.add(Program.getLabel("Infos219"), panelYear);
		tabbedPane.add(Program.getLabel("Infos318"), panelRequest);
		add(tabbedPane,"growx");
		add(new PanelOption(),"wrap");
		add(scrollpane, "grow, wrap, span 2");
		add(addToWorksheet,"alignx left, aligny top");
		add(selectall,"wrap, alignx right, aligny top");

		add(textControl2, "wrap, span 2, alignx center");
		add(resul_txt,"wrap, span 2, alignx center");
		add(modif, "split, span 2, align center");
		add(suppr, "wrap");

		setVisible(true);
		if (name.isVisible()) {
			name.requestFocusInWindow();
		}

		Start.getInstance().menuTools.add(moveLine);
	}

	/**
	 * export_actionPerformed: Fonction pour l'export du resultat de la recherche.
	 *
	 * @param e ActionEvent
	 */
	private void export_actionPerformed(ActionEvent e) {
		try {
			Debug("Exporting...");
			List<Bouteille> v = MODEL.getDatas();
			Export expor = new Export(v);
			JDialog dialog = new JDialog();
			dialog.add(expor);
			dialog.pack();
			dialog.setTitle(Program.getLabel("Infos151"));
			dialog.setLocationRelativeTo(Start.getInstance());
			dialog.setModal(true);
			dialog.setVisible(true);
			Debug("Export Done");
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
	private void suppr_actionPerformed(ActionEvent e) {
		try {
			Debug("Deleting...");
			final LinkedList<Bouteille> listToSupp = getSelectedBouteilles();

			if (listToSupp.isEmpty()) {
				//"Aucun vin a supprimer! / Veuillez selectionner les vins a supprimer.");
				Erreur.showSimpleErreur(Program.getError("Error064"), Program.getError("Error065"), true);
			}
			else {
				String erreur_txt1;
				String erreur_txt2;
				if (listToSupp.size() == 1) {
					erreur_txt1 = Program.getError("Error067"); //"1 vin selectionne.");
					erreur_txt2 = Program.getError("Error068"); //"Voulez-vous le supprimer?");
				}
				else {
					erreur_txt1 = MessageFormat.format(Program.getError("Error130"), listToSupp.size()); //vins selectionnes.");
					erreur_txt2 = Program.getError("Error131"); //"Voulez-vous les supprimer?");
				}
				int resul = JOptionPane.showConfirmDialog(this, erreur_txt1 + " " + erreur_txt2, Program.getLabel("Infos049"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
				if(resul == JOptionPane.YES_OPTION) {
					for(Bouteille bottle:listToSupp) {
						MODEL.removeBouteille(bottle);
						Program.getStorage().addHistory(History.DEL, bottle);
						Program.getStorage().deleteWine(bottle);
						Program.setToTrash(bottle);
						Start.removeBottleTab(bottle);
					}

					RangementUtils.putTabStock();
					Program.updateManagePlacePanel();

					if(listToSupp.size() == 1) {
						resul_txt.setText(Program.getLabel("Infos397"));
					} else {
						resul_txt.setText(MessageFormat.format(Program.getLabel("Infos398"), listToSupp.size()));
					}
				}
			}
			Debug("Deleting Done");
		}
		catch (Exception exc) {
			Program.showException(exc);
		}
	}

	/**
	 * num_lieu_itemStateChanged: Fonction appellee lors d'un changement dans la
	 * liste des numeros de lieu.
	 *
	 * @param e ItemEvent
	 */
	private void num_lieu_itemStateChanged(ItemEvent e) {
		try {
			Debug("Num_lieu_itemStateChanging...");
			int num_select = num_lieu.getSelectedIndex();
			int lieu_select = lieu.getSelectedIndex();

			multi.setSelected(false);
			num_lieu.setEnabled(true);
			line.setEnabled(num_select > 0);
			column.setEnabled(false); //true

			resul_txt.setText("");
			int nb_ligne = 0;
			Rangement rangement = lieu.getItemAt(lieu_select);
			if (num_select > 0) {
				nb_ligne = rangement.getNbLignes(num_select - 1);
			}	else {
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
			}	else {
				multi.setText(label_empl);
			}
		}
		catch (Exception exc) {
			Program.showException(exc);
		}
	}

	/**
	 * lieu_itemStateChanged: Fonction appellee lors d'un changement dans la liste
	 * des emplacements.
	 *
	 * @param e ItemEvent
	 */
	private void lieu_itemStateChanged(ItemEvent e) {
		try {
			Debug("Lieu_itemStateChanging...");
			int lieu_select = lieu.getSelectedIndex();
			Rangement rangement = lieu.getItemAt(lieu_select);

			multi.setEnabled(false);
			num_lieu.setEnabled(lieu_select > 0);

			line.setEnabled(false);
			column.setEnabled(false);

			multi.setSelected(false);
			resul_txt.setText("");
			if (lieu_select > 0) {
				multi.setEnabled(true);

				num_lieu.removeAllItems();
				final boolean caisse = rangement.isCaisse();
				if (caisse) {
					multi.setEnabled(false);
					num_lieu.addItem(Program.getLabel("Infos223")); //"Toutes");
					for (int i = 0; i < rangement.getNbEmplacements(); i++) {
						num_lieu.addItem(Integer.toString(i + rangement.getStartCaisse()));
					}
					label4.setText(Program.getLabel("Infos158")); //"Numero de caisse");
				}	else {
					line.removeAllItems();
					column.removeAllItems();
					num_lieu.addItem("");
					for (int i = 1; i <= rangement.getNbEmplacements(); i++) {
						num_lieu.addItem(Integer.toString(i));
					}
					label4.setText(Program.getLabel("Infos082")); //"Numero du lieu");
				}
				line.setVisible(!caisse);
				column.setVisible(!caisse);
				label6.setVisible(!caisse);
				label5.setVisible(!caisse);
				multi.setText(label_empl);
			}
		}
		catch (Exception exc) {
			Program.showException(exc);
		}
	}

	/**
	 * line_itemStateChanged: Fonction appellee lors d'un changement dans la liste
	 * des numeros de ligne.
	 *
	 * @param e ItemEvent
	 */
	private void line_itemStateChanged(ItemEvent e) {
		try {
			Debug("Line_itemStateChanging...");
			int nb_col = 0;
			int num_select = line.getSelectedIndex();
			int emplacement = num_lieu.getSelectedIndex();
			int lieu_select = lieu.getSelectedIndex();
			Rangement rangement = lieu.getItemAt(lieu_select);

			multi.setSelected(false);
			column.setEnabled(num_select > 0);

			resul_txt.setText("");
			if (num_select > 0) {
				nb_col = rangement.getNbColonnes(emplacement - 1, num_select - 1);
				multi.setText(label_ligne);
			}	else {
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
	private void cherche_actionPerformed(ActionEvent e) {
		//Fonction de recherche
		try {
			Debug("Cherche_actionPerforming...");
			name.removeMenu();
			TXT_NB.setText("-");
			TXT_NBRESUL.setText(Program.getLabel("Infos222"));
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
	private void vider_actionPerformed(ActionEvent e) {
		try {
			//Efface la recherche
			Debug("vider_actionPerforming...");
			SwingUtilities.invokeLater(this::emptyRows);
		}
		catch (Exception ex) {
			Program.showException(ex);
		}
	}

	private void emptyRows() {
		Debug("emptyRows...");
		TXT_NB.setText("-");
		TXT_NBRESUL.setText(Program.getLabel("Infos222"));
		modif.setEnabled(false);
		suppr.setEnabled(false);
		export.setEnabled(false);
		selectall.setSelected(false);
		addToWorksheet.setEnabled(false);
		resul_txt.setText("");
		MODEL.removeAll();
		Debug("emptyRows End");
	}

	//	Fonction de recherche full text avec carateres speciaux (*, ?)
	/**
	 * full_search: Fonction interne de recherche par nom.
	 *
	 */
	private boolean full_search() {
		Debug("Searching by text with pattern");
		//Recherche saisie
		String search = name.getText();
		MODEL.removeAll();
		Debug("Preparing statement...");
		int index = search.indexOf("*");
		int lastIndex = 0;
		StringBuilder regex = new StringBuilder();
		while (index != -1) {
			regex.append(search.substring(lastIndex, index));
			regex.append(".{0,}");
			lastIndex = index + 1;
			index = search.indexOf("*", index + 1);
		}
		regex.append(search.substring(lastIndex));

		search = regex.toString();
		regex = new StringBuilder();
		index = search.indexOf("?");
		lastIndex = 0;
		while (index != -1) {
			regex.append(search.substring(lastIndex, index));
			regex.append(".{1}");
			lastIndex = index + 1;
			index = search.indexOf("*", index + 1);
		}
		regex.append(search.substring(lastIndex));

		final String regexToSearch = regex.toString();
		Debug("Searching with regexp: " + regexToSearch);
		final Pattern p = Pattern.compile(regexToSearch, Pattern.CASE_INSENSITIVE);
		boolean already_found = false;
		for (Bouteille bottle : Program.getStorage().getAllList()) {
			Matcher m = p.matcher(bottle.getNom());
			if (m.matches()) {
				if(!MODEL.hasBottle(bottle)) {
					MODEL.addBouteille(bottle);
				} else {
					already_found = true;
				}
			}
		}
		int nRows = MODEL.getRowCount();
		updateLabelBottleNumber();
		if(nRows > 0) {
			modif.setEnabled(true);
			suppr.setEnabled(true);
		}
		resul_txt.setText(Program.getLabel("Infos088")); //"Recherche terminee.");
		return already_found;
	}


	/**
	 * modif_actionPerformed: Fonction appellee lors d'une modification de
	 * bouteilles.
	 *
	 * @param e ActionEvent
	 */
	private void modif_actionPerformed(ActionEvent e) {
		try {
			Debug("modif_actionPerforming...");
			int max_row = MODEL.getRowCount();
			int row = 0;
			final LinkedList<Bouteille> listToModify = new LinkedList<>();
			do {
				if ((boolean) MODEL.getValueAt(row, TableValues.ETAT)) {
					listToModify.add(MODEL.getBouteille(row));
				}
				row++;
			}	while (row < max_row);

			if (listToModify.isEmpty()) {
				//"Aucun vin a modifier! / Veuillez selectionner les vins a modifier.");
				Erreur.showSimpleErreur(Program.getError("Error071"), Program.getError("Error072"), true);
			} else {
				Debug("Modifying " + listToModify.size() + " bottle(s)...");
				Program.modifyBottles(listToModify);
			}
		}
		catch (Exception exc) {
			Program.showException(exc);
		}
	}
	/**
	 * multi_itemStateChanged: Fonction pour activer la recheche sur plusieurs
	 * lieu / numero de lieu / ligne.
	 *
	 * @param e ItemEvent
	 */
	private void multi_itemStateChanged(ItemEvent e) {
		if (multi.isSelected()) {
			if (line.getSelectedIndex() > 0) {
				column.setEnabled(false);
			} else if (num_lieu.getSelectedIndex() > 0) {
				column.setEnabled(false);
				line.setEnabled(false);
			} else if (lieu.getSelectedIndex() > 0) {
				column.setEnabled(false);
				line.setEnabled(false);
				num_lieu.setEnabled(false);
			}
		} else {
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
	 * run: Realise la recherche et l'affiche dans la JTable
	 */
	@Override
	public void run() {
		try {
			Debug("Running...");
			cherche.setEnabled(false);
			vider.setEnabled(false);
			export.setEnabled(false);
			selectall.setSelected(false);
			selectall.setEnabled(false);
			addToWorksheet.setEnabled(false);
			if (empty_search.isSelected()) {
				emptyRows();
			}
			boolean already_found = false;
			if(tabbedPane.getSelectedIndex() == 3) {
				already_found = searchByRequest();
			} else if (tabbedPane.getSelectedIndex() == 0) {
				already_found = full_search();
			} else if (tabbedPane.getSelectedIndex() == 1) {
				already_found = searchByPlace();
			} else if (tabbedPane.getSelectedIndex() == 2) {
				already_found = searchByYear();
			}
			if (already_found) {
				if (!Program.getCaveConfigBool(MyCellarSettings.DONT_SHOW_INFO, false)) {
					//"Lorsqu'une bouteille recherchee est deja presente dans la liste");
					//"des vins trouves, elle n'est pas ajoutee en double.");
					Erreur.showKeyErreur(Program.getError("Error133") , Program.getError("Error134"), MyCellarSettings.DONT_SHOW_INFO);
				}
			}
			resul_txt.setText(Program.getLabel("Infos088")); //"Recherche terminee.");
			if (MODEL.getRowCount() > 0) {
				SwingUtilities.invokeLater(MODEL::fireTableDataChanged);
				export.setEnabled(true);
				modif.setEnabled(true);
				suppr.setEnabled(true);
			}
			cherche.setEnabled(true);
			vider.setEnabled(true);
			selectall.setEnabled(true);
			addToWorksheet.setEnabled(true);
		}
		catch (Exception exc) {
			Program.showException(exc);
		}
	}

	private boolean searchByRequest() {
		Debug("Search by request");
		CountryVignobles.addVignobleFromBottles();
		Collection<Bouteille> bouteilles = CollectionFilter.select(Program.getStorage().getAllList() , panelRequest.getPredicates()).getResults();
		boolean already_found = false;
		if(bouteilles != null) {
			for(Bouteille b : bouteilles) {
				if(!MODEL.hasBottle(b)) {
					MODEL.addBouteille(b);
				} else {
					already_found = true;
				}
			}
		}
		Debug(MODEL.getRowCount() + " bottle(s) found");
		updateLabelBottleNumber();
		Debug("Search by request Done");
		return already_found;
	}

	/**
	 * 
	 */
	private static void updateLabelBottleNumber() {
		TXT_NB.setText(Integer.toString(MODEL.getRowCount()));
		if (MODEL.getRowCount() == 0) {
			TXT_NBRESUL.setText(Program.getLabel("Infos222"));
		} else {
			TXT_NBRESUL.setText(Program.getLabel("Infos239"));
		}
	}

	private boolean searchByPlace() {
		Debug("Searching by place");
		int lieu_select = lieu.getSelectedIndex();
		boolean already_found = false;

		if (lieu_select == 0) {
			Debug("ERROR: No place selected");
			Erreur.showSimpleErreur(Program.getError("Error055")); //Select emplacement
			resul_txt.setText("");
			return false;
		}
		
		Rangement rangement = lieu.getItemAt(lieu_select);
		if (rangement.isCaisse()) {
			//Pour la caisse
			int lieu_num = num_lieu.getSelectedIndex();
			resul_txt.setText(Program.getLabel("Infos087")); //"Recherche en cours...");
			int nb_empl_cave = rangement.getNbEmplacements();
			int boucle_toutes;
			int start_boucle;
			if (lieu_num == 0) { //New
				start_boucle = 1;
				boucle_toutes = nb_empl_cave + 1;
			} else {
				start_boucle = lieu_num;
				boucle_toutes = lieu_num + 1;
			}
			for (int x = start_boucle; x < boucle_toutes; x++) {
				int nb_bottles = rangement.getNbCaseUse(x - 1);
				for (int l = 0; l < nb_bottles; l++) {
					Bouteille b = rangement.getBouteilleCaisseAt(x - 1, l); //lieu_num
					if(b != null) {
						if(!MODEL.hasBottle(b)) {
							MODEL.addBouteille(b);
						} else {
							already_found = true;
						}
					} else {
						Debug("No bottle found in lieuselect-1="+(lieu_select - 1)+" x-1="+(x-1)+" l+1="+(l+1));
					}
				} //Fin for
			} //Fin for
			Debug(MODEL.getRowCount() + " bottle(s) found");
			updateLabelBottleNumber();

			if (MODEL.getRowCount() > 0) {
				modif.setEnabled(true);
				suppr.setEnabled(true);
			}
			resul_txt.setText(Program.getLabel("Infos088")); //"Recherche terminee.");
		} else {
			//Type armoire
			if (!multi.isSelected()) {
				int lieu_num = num_lieu.getSelectedIndex();
				int ligne = line.getSelectedIndex();
				int colonne = column.getSelectedIndex();
				if (lieu_num == 0) {
					Debug("ERROR: No Num place selected");
					resul_txt.setText("");
					Erreur.showSimpleErreur(Program.getError("Error056")); //"Veuillez selectionner un numero d'emplacement!";
					return false;
				}
				if (ligne == 0) {
					Debug("ERROR: No Line selected");
					resul_txt.setText("");
					Erreur.showSimpleErreur(Program.getError("Error057")); //"Veuillez selectionner un numero de ligne!";
					return false;
				}
				if (colonne == 0) {
					Debug("ERROR: No column selected");
					resul_txt.setText("");
					Erreur.showSimpleErreur(Program.getError("Error058")); //"Veuillez selectionner un numero de colonne!";
					return false;
				}
				Bouteille b = rangement.getBouteille(lieu_num - 1, ligne - 1, colonne - 1);
				resul_txt.setText(Program.getLabel("Infos087")); //"Recherche en cours...");

				if (b == null) {
					resul_txt.setText(Program.getLabel("Infos224")); //"Echec de la recherche.");
					Erreur.showSimpleErreur(Program.getError("Error066")); //Aucune bouteille trouve
					TXT_NB.setText("0");
					TXT_NBRESUL.setText(Program.getLabel("Infos222"));
					modif.setEnabled(false);
					suppr.setEnabled(false);
				}	else {
					if(!MODEL.hasBottle(b)) {
						MODEL.addBouteille(b);
					} else {
						already_found = true;
					}
					updateLabelBottleNumber();
					if (MODEL.getRowCount() > 0) {
						modif.setEnabled(true);
						suppr.setEnabled(true);
					}
					resul_txt.setText(Program.getLabel("Infos088")); //"Recherche terminee.");
				}
			}	else { //multi.getState == true
				//Cas recherche toutes bouteille (lieu, num_lieu, ligne)
				int lieu_num = num_lieu.getSelectedIndex();
				int ligne = line.getSelectedIndex();
				int colonne = column.getSelectedIndex();
				if (multi.getText().compareTo(label_empl) != 0) {
					if (lieu_num == 0) {
						Debug("ERROR: No Num place selected");
						Erreur.showSimpleErreur(Program.getError("Error056")); //"Veuillez selectionner un numero d'emplacement!";
						resul_txt.setText("");
						return false;
					}
					
					if (multi.getText().compareTo(label_num_empl) != 0) {
						if (ligne == 0) {
							Debug("ERROR: No line selected");
							Erreur.showSimpleErreur(Program.getError("Error057")); //"Veuillez selectionner un numero de ligne!";
							resul_txt.setText("");
							return false;
						}

						if (multi.getText().compareTo(label_ligne) != 0) {
							if (colonne == 0) {
								Debug("ERROR: No column selected");
								Erreur.showSimpleErreur(Program.getError("Error058")); //"Veuillez selectionner un numero de colonne!";
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
							if (b != null) {
								//Ajout de la bouteille dans la liste si elle n'y ait pas deja
								if (!MODEL.hasBottle(b)) {
									MODEL.addBouteille(b);
								} else {
									already_found = true;
								}
							}
						}
					}
				}
				int nbBottle = MODEL.getRowCount();
				Debug(nbBottle + " bottle(s) selected");
				updateLabelBottleNumber();
				if (nbBottle > 0) {
					modif.setEnabled(true);
					suppr.setEnabled(true);
				}
				resul_txt.setText(Program.getLabel("Infos088")); //"Recherche terminee.");
			} //Fin else multi
		} //fin else
		
		return already_found;
	}

	private boolean searchByYear() {
		Debug("Searching by year");
		int annee;
		int item_select = year.getSelectedIndex();
		int nb_year = year.getItemCount();
		String sYear = "";
		if (year.getSelectedItem() != null) {
			sYear = year.getSelectedItem().toString();
		}
		if(Bouteille.isNonVintageYear(sYear)) {
			annee = Bouteille.NON_VINTAGE_INT;
		} else {
			try {
				annee = Integer.parseInt(sYear);
			}	catch (Exception e) {
				annee = 0;
			}
		}

		resul_txt.setText(Program.getLabel("Infos087")); //"Recherche en cours...");
		boolean already_found = false;
		for (Bouteille b : Program.getStorage().getAllList()) {
			if(b == null) {
				continue;
			}
			//Recuperation du numero du lieu
			Rangement r = b.getRangement();

			if (annee == b.getAnneeInt() && nb_year != item_select && r != null) {
				if(!MODEL.hasBottle(b)) {
					MODEL.addBouteille(b);
				} else {
					already_found = true;
				}
			} else {
				if (b.getAnneeInt() < 1000 && (nb_year - 1) == item_select) { // Cas Autre
					if(!MODEL.hasBottle(b)) {
						MODEL.addBouteille(b);
					} else {
						already_found = true;
					}
				}
			}
		}
		Debug(MODEL.getRowCount() + " bottle(s) found");
		updateLabelBottleNumber();
		return already_found;
	}

	/**
	 * keylistener_actionPerformed: Ecoute clavier.
	 *
	 * @param e KeyEvent
	 */
	private void keylistener_actionPerformed(KeyEvent e) {
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
			cut();
		}
		if (e.getKeyCode() == KeyEvent.VK_X) {
			copy();
		}
		if (e.getKeyCode() == KeyEvent.VK_V) {
			paste();
		}
	}

	/**
	 * selectall_actionPerformed: Permet de selectionner toutes les lignes de la
	 * JTable
	 *
	 * @param e ActionEvent
	 */
	private void selectall_actionPerformed(ActionEvent e) {
		Debug("selectall_actionPerforming...");
		modif.setEnabled(false);
		suppr.setEnabled(false);
		for (int i = 0; i < MODEL.getRowCount(); i++) {
			MODEL.setValueAt(selectall.isSelected(), i, TableValues.ETAT);
		}
		if(MODEL.getRowCount() > 0) {
			modif.setEnabled(true);
			suppr.setEnabled(true);
		}
		table.updateUI();
		Debug("selectall_actionPerforming... Done");
	}

	/**
	 * addToWorksheet_actionPerformed: Permet d'ajouter des bouteilles a la feuille de travail
	 *
	 * @param e ActionEvent
	 */
	private void addToWorksheet_actionPerformed(ActionEvent e) {
		Debug("addToWorksheet_actionPerforming...");
		final LinkedList<Bouteille> list = getSelectedBouteilles();

		if (list.isEmpty()) {
			Erreur.showSimpleErreur(Program.getError("Error.NoWineSelected"), true);
			return;
		}
		new OpenWorkSheetAction(list).actionPerformed(null);
		Debug("addToWorksheet_actionPerforming... Done");
	}

	private LinkedList<Bouteille> getSelectedBouteilles() {
		int max_row = MODEL.getRowCount();
		final LinkedList<Bouteille> list = new LinkedList<>();
		// Recuperation du nombre de lignes selectionnees
		for (int i = 0; i < max_row; i++) {
			if ((boolean) MODEL.getValueAt(i, TableValues.ETAT)) {
				list.add(MODEL.getBouteille(i));
			}
		}
		return list;
	}

	/**
	 * empty_search_actionPerformed: Permet de vider automatiquement la recherche
	 *
	 * @param e ActionEvent
	 */
	private void empty_search_actionPerformed(ActionEvent e) {
		Program.putCaveConfigBool(MyCellarSettings.EMPTY_SEARCH, empty_search.isSelected());
	}

	/**
	 * Debug
	 *
	 * @param sText String
	 */
	private static void Debug(String sText) {
		Program.Debug("Search: " + sText);
	}

	private class PanelName extends JPanel{
		private static final long serialVersionUID = -2125241372841734287L;

		private PanelName(){
			name.setEditable(true);
			name.addMouseListener(popup_l);
			name.setFont(Program.FONT_PANEL);
			setLayout(new MigLayout("","[grow]","[]"));
			add(new MyCellarLabel(Program.getLabel("Infos085")),"wrap");
			add(name,"grow");
		}
	}

	private class PanelPlace extends JPanel{
		private static final long serialVersionUID = -2601861017578176513L;

		private PanelPlace(){
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

	private class PanelYear extends JPanel{
		private static final long serialVersionUID = 8579611890313378015L;
		private PanelYear(){
			setLayout(new MigLayout("","",""));
			MyCellarLabel labelYear = new MyCellarLabel(Program.getLabel("Infos133"));
			add(labelYear,"wrap");
			add(year,"");
		}
		private void fillYear(){
			year.removeAllItems();
			int[] an_array = Program.getAnnees();
			String[] mes_string = new String[an_array.length];
			for (int y = 0; y < an_array.length; y++) {
				mes_string[y] = Integer.toString(an_array[y]);
			}
			Arrays.sort(mes_string, Collator.getInstance());
			for (String s : mes_string) {
				if (Integer.parseInt(s) > 1000 && Integer.parseInt(s) < 9000) {
					year.addItem(s);
				}
			}
			year.addItem(Program.getLabel("Infos390")); //NV
			year.addItem(Program.getLabel("Infos225")); //"Autre");
		}
	}

	private class PanelOption extends JPanel {
		private static final long serialVersionUID = 6761656985728428915L;

		private PanelOption() {
			setLayout(new MigLayout("","","[][]"));
			add(cherche,"wrap");
			add(vider,"");
			add(export,"wrap");
			add(empty_search,"wrap, span 2");
			add(TXT_NBRESUL,"split");
			add(TXT_NB,"wrap");
		}
	}

	@Override
	public boolean tabWillClose(TabEvent event) {
		return true;
	}

	@Override
	public void tabClosed() {
		Start.getInstance().menuTools.remove(moveLine);
		Start.getInstance().updateMainPanel();
	}

	@Override
	public void setUpdateView(){
		updateView = true;
	}

	@Override
	public void updateView() {
		if(!updateView) {
			return;
		}
		updateView = false;
		lieu.removeAllItems();
		lieu.addItem(Program.EMPTY_PLACE);
		for (Rangement r : Program.getCave()) {
			lieu.addItem(r);
		}
	}

	static void removeBottle(Bouteille bottleToDelete) {
		MODEL.deleteBottle(bottleToDelete);
		MODEL.fireTableDataChanged();
		updateLabelBottleNumber();
	}

	static void updateTable() {
		SwingUtilities.invokeLater(MODEL::fireTableDataChanged);
	}

	static void clearResults() {
		SwingUtilities.invokeLater(MODEL::removeAll);
	}

  @Override
  public void cut() {
		if( tabbedPane.getSelectedIndex() == 0 ) {
			String text = name.getSelectedText();
			String fullText = name.getText();
			if(text != null) {
				name.setText(fullText.substring(0, name.getSelectionStart()) + fullText.substring(name.getSelectionEnd()));
				Program.CLIPBOARD.copier(text);
			}
		}
	}

  @Override
  public void copy() {
		if(tabbedPane.getSelectedIndex() == 0) {
			//if(name.hasFocus())
			{
				String text = name.getSelectedText();
				if(text != null) {
					Program.CLIPBOARD.copier(text);
				}
			}
		}
	}

	@Override
	public void paste() {
		if(tabbedPane.getSelectedIndex() == 0) {
			//if(name.hasFocus())
			{
				String fullText = name.getText();
				name.setText(fullText.substring(0,  name.getSelectionStart()) + Program.CLIPBOARD.coller() + fullText.substring(name.getSelectionEnd()));
			}
		}
	}

}
