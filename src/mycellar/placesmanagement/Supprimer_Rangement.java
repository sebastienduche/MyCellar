package mycellar.placesmanagement;

import mycellar.Bouteille;
import mycellar.Erreur;
import mycellar.HistoryState;
import mycellar.ITabListener;
import mycellar.MyXmlDom;
import mycellar.Program;
import mycellar.Rangement;
import mycellar.RangementUtils;
import mycellar.Start;
import mycellar.TabEvent;
import mycellar.core.IMyCellar;
import mycellar.core.IUpdatable;
import mycellar.core.LabelProperty;
import mycellar.core.LabelType;
import mycellar.core.MyCellarButton;
import mycellar.core.MyCellarComboBox;
import mycellar.core.MyCellarLabel;
import net.miginfocom.swing.MigLayout;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.text.MessageFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import static mycellar.Program.EMPTY_PLACE;
import static mycellar.Program.FONT_DIALOG_SMALL;
import static mycellar.Program.deleteSupprimerRangement;
import static mycellar.Program.getAide;
import static mycellar.Program.getCave;
import static mycellar.Program.getCaveLength;
import static mycellar.Program.getError;
import static mycellar.Program.getLabel;
import static mycellar.Program.getPreviewXMLFileName;
import static mycellar.Program.getStorage;
import static mycellar.Program.open;
import static mycellar.Program.removeCave;
import static mycellar.Program.setToTrash;
import static mycellar.Program.showException;
import static mycellar.Program.updateAllPanels;


/**
 * <p>Titre : Cave &agrave; vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2005</p>
 * <p>Soci&eacute;t&eacute; : Seb Informatique</p>
 * @author S&eacute;bastien Duch&eacute;
 * @version 8.5
 * @since 10/12/20
 */

public final class Supprimer_Rangement extends JPanel implements ITabListener, IMyCellar, IUpdatable {

	private static final long serialVersionUID = 6959053537854600207L;
	private final MyCellarComboBox<Rangement> choix = new MyCellarComboBox<>();
	private final MyCellarLabel label_final = new MyCellarLabel();
	private int nb_case_use_total = 0;
	private final MyCellarButton preview = new MyCellarButton(LabelType.INFO, "138");
	private final char supprimerChar = getLabel("SUPPR").charAt(0);
	private final char previewChar = getLabel("VISUAL").charAt(0);
	private final JTable table;
	private final LinkedList<SupprimerLine> listSupprimer = new LinkedList<>();
	private boolean updateView = false;
	private final SupprimerModel model;
	
	/**
	 * Supprimer_Rangement: Constructeur pour Supprimer un rangement
	 *
	 */
	public Supprimer_Rangement() {

		Debug("Initializing...");
		setLayout(new MigLayout("","[grow]","20px[]15px[]15px[]"));
		MyCellarLabel textControl2 = new MyCellarLabel(getLabel("Infos054")); //"Veuillez selectionner le rangement a supprimer:");
		MyCellarButton supprimer = new MyCellarButton(getLabel("Main.Delete")); //"Supprimer");
		supprimer.setMnemonic(supprimerChar);
		preview.setMnemonic(previewChar);
		
		supprimer.addActionListener(this::supprimer_actionPerformed);
		addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				keylistener_actionPerformed(e);
			}
		});

		model = new SupprimerModel(listSupprimer);
		table = new JTable(model);
		JScrollPane scroll = new JScrollPane(table);

		add(textControl2, "split 2, gap");
		add(choix, "wrap");
		add(scroll, "grow, wrap");
		add(label_final, "grow, center, wrap");
		add(preview, "split 2, center");
		add(supprimer, "");
	
		preview.addActionListener(this::preview_actionPerformed);

		choix.addItemListener(this::choix_itemStateChanged);

		choix.addItem(EMPTY_PLACE);
		getCave().forEach(choix::addItem);
		RangementUtils.putTabStock();
		setVisible(true);
	}

	/**
	 * choix_itemStateChanged: Methode pour la premiere liste
	 *
	 * @param e ItemEvent
	 */
	private void choix_itemStateChanged(ItemEvent e) {
		try {
			Debug("choix_itemStateChanging...");
			listSupprimer.clear();
			nb_case_use_total = 0;

			int num_select = choix.getSelectedIndex();
			if (num_select != 0) {
				preview.setEnabled(true);
				Rangement rangement = (Rangement) choix.getSelectedItem();
				// Nombre d'emplacement
				if (rangement != null) {
					int num_emplacement = rangement.getNbEmplacements();

					if (!rangement.isCaisse()) {
						model.setCaisse(false);
						Debug("Selecting standard place...");
						// Description du nombre de ligne par partie
						nb_case_use_total = 0;
						for (int i = 0; i < num_emplacement; i++) {
							SupprimerLine line = new SupprimerLine("", i + 1, rangement.getNbLignes(i), rangement.getNbCaseUse(i));
							listSupprimer.add(line);
							nb_case_use_total += rangement.getNbCaseUse(i);
						}
					} else { //Pour caisse
						int start_caisse = rangement.getStartCaisse();
						model.setCaisse(true);
						Debug("Selecting Box place...");
						nb_case_use_total = 0;
						for (int i = 0; i < num_emplacement; i++) {
							SupprimerLine line = new SupprimerLine("", i + start_caisse, 0, rangement.getNbCaseUse(i));
							listSupprimer.add(line);
							nb_case_use_total += rangement.getNbCaseUse(i);
						}
					}
				}

				label_final.setForeground(Color.red);
				label_final.setFont(FONT_DIALOG_SMALL);
				label_final.setHorizontalAlignment(SwingConstants.CENTER);
				Debug("There is(are0 " + nb_case_use_total + " bottle(s) in this place!");
				if (nb_case_use_total == 0) {
					label_final.setText(getLabel("Infos065")); //"Le rangement est vide");
				}	else {
					if (nb_case_use_total == 1) {
						label_final.setText(getLabel("DeletePlace.still1Item", LabelProperty.SINGLE)); //"Il reste 1 vin dans le rangement!!!");
					}	else {
						label_final.setText(MessageFormat.format(getLabel("DeletePlace.stillNItems", LabelProperty.PLURAL), nb_case_use_total)); //Il reste n vins dans le rangement
					}
				}
				table.updateUI();
			}	else {
				label_final.setText("");
				preview.setEnabled(false);
			}
		}	catch (Exception exc) {
			showException(exc);
		}
	}

	/**
	 * supprimer_actionPerformed: methode pour la suppression d'un rangement.
	 *
	 * @param e ActionEvent
	 */
	private void supprimer_actionPerformed(ActionEvent e) {
		try {
			Debug("supprimer_actionPerforming...");
			final int num_select = choix.getSelectedIndex();

			// Verifier l'etat du rangement avant de le supprimer et demander confirmation
			if (num_select > 0) {
				if (getCaveLength() == 1) {
					Erreur.showSimpleErreur(getError("SupprimerRangement.ForbiddenToDelete"));
					return;
				}
				final Rangement cave = (Rangement) choix.getSelectedItem();
				if (cave != null) {
					String erreur_txt1;
					if (nb_case_use_total == 0) {
						String tmp = cave.getNom();
						Debug("MESSAGE: Delete this place: " + tmp + "?");
						erreur_txt1 = MessageFormat.format(getError("Error139"), tmp); //Voulez vous supprimer le rangement
						if(JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(this, erreur_txt1, getLabel("Infos049"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE)) {
							removeCave(cave);
							choix.removeItemAt(num_select);
							choix.setSelectedIndex(0);
							updateAllPanels();
						}
					}	else {
						String nom = cave.getNom();
						if (nb_case_use_total == 1) {
							erreur_txt1 = MessageFormat.format(getLabel("DeletePlace.still1ItemIn", LabelProperty.SINGLE), nom); //il reste 1 bouteille dans
						} else {
							erreur_txt1 = MessageFormat.format(getLabel("DeletePlace.stillNItemsIn", LabelProperty.PLURAL), nb_case_use_total, nom); //Il reste n bouteilles dans
						}
						//"Voulez vous supprimer le rangement et les BOUTEILLES restantes?");
						String erreur_txt2 = getError("Error039", LabelProperty.THE_PLURAL);
						Debug("MESSAGE: Delete this place " + nom + " and all bottle(s) (" + nb_case_use_total + ")?");
						if (JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(this, erreur_txt1 + " " + erreur_txt2, getLabel("Infos049"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE)) {
							new Thread(() -> {
								//Suppression des bouteilles presentes dans le rangement
								String tmp_nom = cave.getNom();

								List<Bouteille> bottleList = getStorage().getAllList().stream().filter((bottle) -> bottle.getEmplacement().equals(tmp_nom)).collect(Collectors.toList());
									for (Bouteille b : bottleList) {
									getStorage().addHistory(HistoryState.DEL, b);
									getStorage().deleteWine(b);
									setToTrash(b);
								}
									removeCave(cave);
									updateAllPanels();
							}).start();
							choix.removeItemAt(num_select);
							choix.setSelectedIndex(0);
						}
					}
				}
			}
		} catch (Exception exc) {
			showException(exc);
		}
	}

	/**
	 * preview_actionPerformed: Methode pour visualiser un rangement
	 *
	 * @param e ActionEvent
	 */
	private void preview_actionPerformed(ActionEvent e) {
		try {
			Debug("preview_actionPerforming...");
			int num_select = choix.getSelectedIndex();
			if (num_select == 0) {
				preview.setEnabled(false);
				return;
			}
			Rangement rangement = (Rangement) choix.getSelectedItem();
			LinkedList<Rangement> rangements = new LinkedList<>();
			rangements.add(rangement);
			MyXmlDom.writeRangements("", rangements, false);
			open(new File(getPreviewXMLFileName()));
		}	catch (Exception exc) {
			showException(exc);
		}
	}

	/**
	 * keylistener_actionPerformed: Methode d'ecoute clavier.
	 *
	 * @param e KeyEvent
	 */
	private void keylistener_actionPerformed(KeyEvent e) {
		if (e.getKeyCode() == supprimerChar) {
			supprimer_actionPerformed(null);
		} else if (e.getKeyCode() == previewChar && preview.isEnabled()) {
			preview_actionPerformed(null);
		} else if (e.getKeyCode() == KeyEvent.VK_F1) {
			getAide();
		}
	}

	/**
	 * Debug
	 *
	 * @param sText String
	 */
	private static void Debug(String sText) {
		Program.Debug("Supprimer_Rangement: " + sText);
	}

	@Override
	public boolean tabWillClose(TabEvent event) {
		return true;
	}

	@Override
	public void tabClosed() {
		Start.getInstance().updateMainPanel();
		deleteSupprimerRangement();
	}

	@Override
	public void setUpdateView(){
		updateView  = true;
	}
	/**
	 * Mise a jour de la liste des rangements
	 */
	@Override
	public void updateView() {
		if (!updateView) {
			return;
		}
		updateView = false;
		RangementUtils.putTabStock();
		choix.removeAllItems();
		choix.addItem(EMPTY_PLACE);
		getCave().forEach(choix::addItem);
	}

	static class SupprimerModel extends DefaultTableModel{

		private static final long serialVersionUID = -3295046126691124148L;
		private final List<SupprimerLine> list;
		private final List<Column> columns;
		private final Column colLine = new Column(Column.LINE, getLabel("Infos027"));
		private boolean isCaisse = false;

		private SupprimerModel(List<SupprimerLine> list) {
			this.list = list;
			columns = new LinkedList<>();
			columns.add(new Column(Column.PART, getLabel("Infos081")));
			columns.add(colLine);
			columns.add(new Column(Column.WINE, getLabel("Infos057")));

		}
		public void setCaisse(boolean caisse) {
			if (isCaisse != caisse) {
				isCaisse = caisse;
				if (isCaisse) {
					columns.remove(colLine);
				} else {
					columns.add(1, colLine);
				}
			}
			fireTableStructureChanged();
		}
		@Override
		public int getColumnCount() {
			return columns.size();
		}
		@Override
		public String getColumnName(int column) {
			return columns.get(column).getLabel();
		}
		@Override
		public int getRowCount() {
			if(list == null) {
				return 0;
			}
			return list.size();
		}
		@Override
		public Object getValueAt(int row, int column) {
			SupprimerLine line = list.get(row);
			Column col = columns.get(column);
			switch(col.getCol()) {
			case 0:
				return line.getNumPartLabel();
			case 1:
				return line.getNbLineLabel();
			case 2:
				return line.getNbWineLabel();
			default:
				return "";
			}
		}
		@Override
		public boolean isCellEditable(int row, int column) {
			return false;
		}

		static class Column {
			private static final int PART = 0;
			private static final int LINE = 1;
			private static final int WINE = 2;

			private final int col;
			private final String label;

			private Column(int col, String label) {
				this.col = col;
				this.label = label;
			}
			private int getCol() {
				return col;
			}
			public String getLabel() {
				return label;
			}
		}
	}

	static class SupprimerLine {
		private String place;
		private int numPart;
		private int nbLine;
		private int nbWine;

		private SupprimerLine(String place, int numPart, int nbLine, int nbWine) {
			this.place = place;
			this.numPart = numPart;
			this.nbLine = nbLine;
			this.nbWine = nbWine;
		}
		public String getPlace() {
			return place;
		}
		public void setPlace(String place) {
			this.place = place;
		}
		public int getNumPart() {
			return numPart;
		}
		String getNumPartLabel() {
			return getLabel("Infos029") + " " + numPart;
		}
		public void setNumPart(int numPart) {
			this.numPart = numPart;
		}
		public int getNbLine() {
			return nbLine;
		}
		String getNbLineLabel() {
			if(nbLine <= 1) {
				return MessageFormat.format(getLabel("Infos060"), nbLine);
			}
			return MessageFormat.format(getLabel("Infos061"), nbLine);
		}
		public void setNbLine(int nbLine) {
			this.nbLine = nbLine;
		}
		public int getNbWine() {
			return nbWine;
		}
		String getNbWineLabel() {
			return MessageFormat.format(getLabel("Main.severalItems", new LabelProperty(nbWine > 1)), nbWine);
		}
		public void setNbWine(int nbWine) {
			this.nbWine = nbWine;
		}
	}

}
