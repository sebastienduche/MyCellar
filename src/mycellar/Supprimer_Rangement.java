package mycellar;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.io.File;
import java.text.MessageFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import mycellar.core.MyCellarButton;
import mycellar.core.MyCellarComboBox;
import mycellar.core.MyCellarLabel;
import net.miginfocom.swing.MigLayout;


/**
 * <p>Titre : Cave à vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2005</p>
 * <p>Société : Seb Informatique</p>
 * @author Sébastien Duché
 * @version 6.8
 * @since 27/07/17
 */

public class Supprimer_Rangement extends JPanel implements ITabListener {

	private static final long serialVersionUID = 6959053537854600207L;
	private MyCellarLabel textControl2 = new MyCellarLabel();
	private MyCellarComboBox<String> choix = new MyCellarComboBox<String>();
	private MyCellarLabel label_final = new MyCellarLabel();
	private MyCellarButton supprimer = new MyCellarButton();
	private JScrollPane scroll = new JScrollPane();
	private int nb_case_use_total = 0;
	private MyCellarButton preview = new MyCellarButton();
	private char SUPPRIMER = Program.getLabel("SUPPR").charAt(0);
	private char PREVIEW = Program.getLabel("VISUAL").charAt(0);
	private JTable table;
	private LinkedList<SupprimerLine> listSupprimer = new LinkedList<SupprimerLine>();
	private boolean updateView = false;
	private SupprimerModel model;
	
	/**
	 * Supprimer_Rangement: Constructeur pour Supprimer un rangement
	 *
	 * @param cave LinkedList<Rangement>: Tableau des rangements
	 */
	public Supprimer_Rangement() {

		Debug("Initializing...");
		setLayout(new MigLayout("","[grow]","20px[]15px[]15px[]"));
		textControl2.setText(Program.getLabel("Infos054")); //"Veuillez sélectionner le rangement � supprimer:");
		supprimer.setText(Program.getLabel("Infos051")); //"Supprimer");
		supprimer.setMnemonic(SUPPRIMER);
		preview.setMnemonic(PREVIEW);
		
		supprimer.addActionListener((e) -> supprimer_actionPerformed(e));
		this.addKeyListener(new java.awt.event.KeyListener() {
			public void keyReleased(java.awt.event.KeyEvent e) {}

			public void keyPressed(java.awt.event.KeyEvent e) {
				keylistener_actionPerformed(e);
			}

			public void keyTyped(java.awt.event.KeyEvent e) {}
		});

		model = new SupprimerModel( listSupprimer );
		table = new JTable(model);
		scroll = new JScrollPane(table);

		add(textControl2, "split 2, gap");
		add(choix, "wrap");
		add(scroll, "grow, wrap");
		add(label_final, "grow, center, wrap");
		add(preview, "split 2, center");
		add(supprimer, "");
	
		preview.setText(Program.getLabel("Infos138")); //"Visualiser le rangement");
		preview.addActionListener((e) -> preview_actionPerformed(e));

		choix.addItemListener((e) -> choix_itemStateChanged(e));

		choix.addItem("");
		for (int i = 0; i < Program.GetCaveLength(); i++) {
			if (Program.getCave(i) != null) {
				choix.addItem(Program.getCave(i).getNom());
			}
		}
		RangementUtils.putTabStock();
		this.setVisible(true);
	}

	/**
	 * choix_itemStateChanged: Méthode pour la première liste
	 *
	 * @param e ItemEvent
	 */
	void choix_itemStateChanged(ItemEvent e) {
		try {
			Debug("choix_itemStateChanging...");
			int i;
			int num_select;
			int num_emplacement;
			listSupprimer.clear();
			nb_case_use_total = 0;
			
			num_select = choix.getSelectedIndex();
			if (num_select != 0) {
				preview.setEnabled(true);
				Rangement rangement = Program.getCave(num_select - 1);
				// Nombre d'emplacement
				num_emplacement = rangement.getNbEmplacements();
				
				if (!rangement.isCaisse()) { 
					model.setCaisse(false);
					Debug("Selecting standard place...");
					// Description du nombre de ligne par partie
					nb_case_use_total = 0;
					for (i = 0; i < num_emplacement; i++) {
						SupprimerLine line = new SupprimerLine("", i + 1, rangement.getNbLignes(i), rangement.getNbCaseUse(i));
						listSupprimer.add(line);
						nb_case_use_total += rangement.getNbCaseUse(i);
					}
				}
				else { //Pour caisse
					int start_caisse = rangement.getStartCaisse();
					model.setCaisse(true);
					Debug("Selecting Box place...");
					nb_case_use_total = 0;
					for (i = 0; i < num_emplacement; i++) {
						SupprimerLine line = new SupprimerLine("", i + start_caisse, 0, rangement.getNbCaseUse(i));
						listSupprimer.add(line);
						nb_case_use_total += rangement.getNbCaseUse(i);
					}
				}

				label_final.setForeground(Color.red);
				label_final.setFont(Program.font_dialog_small);
				label_final.setHorizontalAlignment(0);
				Debug("There is "+nb_case_use_total+" bottle(s) in this place!");
				if (nb_case_use_total == 0) {
					label_final.setText(Program.getLabel("Infos065")); //"Le rangement est vide");
				}
				else {
					if (nb_case_use_total == 1) {
						label_final.setText(Program.getLabel("Infos066")); //"Il reste 1 vin dans le rangement!!!");
					}
					else {
						label_final.setText(MessageFormat.format(Program.getLabel("Infos072"), nb_case_use_total)); //Il reste n vins dans le rangement
					}
				}
				table.updateUI();
			}
			else {
				label_final.setText("");
				preview.setEnabled(false);
			}
		}
		catch (Exception exc) {
			Program.showException(exc);
		}
	}

	/**
	 * supprimer_actionPerformed: méthode pour la suppression d'un rangement.
	 *
	 * @param e ActionEvent
	 */
	void supprimer_actionPerformed(ActionEvent e) {
		try {
			Debug("supprimer_actionPerforming...");
			final int num_select = choix.getSelectedIndex();
			String erreur_txt1;
			String erreur_txt2;

			// Vérifier l'état du rangement avant de le supprimer et demander confirmation
			if (num_select > 0) {
				if(Program.GetCaveLength() == 1) {
					new Erreur(Program.getError("SupprimerRangement.ForbiddenToDelete"));
					return;
				}
				Rangement cave = Program.getCave(num_select - 1);
				if (nb_case_use_total == 0) {
					String tmp = cave.getNom();
					Debug("MESSAGE: Delete this place: "+tmp+"?");
					erreur_txt1 = new String(Program.getError("Error139") + " " + tmp + " ?"); //Voulez vous supprimer le rangement
					if( JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(this, erreur_txt1, Program.getLabel("Infos049"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE))        
					{
						Program.removeCave(cave);
						choix.removeItemAt(num_select);
						choix.setSelectedIndex(0);
						Program.updateAllPanels();
					}  
				}
				else {
					String sName = cave.getNom();
					if (nb_case_use_total == 1) {
						erreur_txt1 = MessageFormat.format(Program.getLabel("Infos073"), sName); //il reste 1 bouteille dans
					}
					else {
						erreur_txt1 = MessageFormat.format(Program.getLabel("Infos074"), nb_case_use_total, sName); //Il reste n bouteilles dans
					}
					erreur_txt2 = Program.getError("Error039"); //"Voulez vous supprimer le rangement et les BOUTEILLES restantes?");
					Debug("MESSAGE: Delete this place "+sName+" and all bottle(s) ("+nb_case_use_total+")?");
					if( JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(this, erreur_txt1 + " " + erreur_txt2, Program.getLabel("Infos049"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE))        
					{
						class Run implements Runnable {
							@Override
							public void run() {
								//Suppression des bouteilles présentes dans le rangement
								String tmp_nom = Program.getCave(num_select - 1).getNom();

								List<Bouteille> bottleList = Program.getStorage().getAllList().stream().filter((bottle) -> bottle.getEmplacement().equals(tmp_nom)).collect(Collectors.toList());
								for(Bouteille b : bottleList) {
									Program.getStorage().addHistory(History.DEL, b);
									Program.getStorage().deleteWine(b);
									Program.setToTrash(b);
								}
								Program.removeCave(cave);
								Program.updateAllPanels();
							} 
						}
						new Thread(new Run()).start();
						choix.removeItemAt(num_select);
						choix.setSelectedIndex(0);
					}
				}
			}
		}
		catch (Exception exc) {
			Program.showException(exc);
		}
	}

	/**
	 * preview_actionPerformed: Méthode pour visualiser un rangement
	 *
	 * @param e ActionEvent
	 */
	void preview_actionPerformed(ActionEvent e) {
		try {
			Debug("preview_actionPerforming...");
			int num_select = choix.getSelectedIndex();
			if (num_select == 0) {
				preview.setEnabled(false);
			}
			else {
				Rangement rangement = Program.getCave(num_select -1);
				LinkedList<Rangement> rangements = new LinkedList<Rangement>();
				rangements.add(rangement);
				MyXmlDom.writeRangements("", rangements, false);
				Program.open(new File(Program.getPreviewXMLFileName()));
			}
		}
		catch (Exception exc) {
			Program.showException(exc);
		}
	}

	/**
	 * keylistener_actionPerformed: Méthode d'écoute clavier.
	 *
	 * @param e KeyEvent
	 */
	void keylistener_actionPerformed(KeyEvent e) {
		if (e.getKeyCode() == SUPPRIMER) {
			supprimer_actionPerformed(null);
		}
		if (e.getKeyCode() == PREVIEW && preview.isEnabled()) {
			preview_actionPerformed(null);
		}
		if (e.getKeyCode() == KeyEvent.VK_F1) {
			aide_actionPerformed(null);
		}
	}

	/**
	 * this_windowActivated: Mis au premier plan de l'Program.erreur.
	 *
	 * @param e WindowEvent
	 */
	void this_windowActivated(WindowEvent e) {
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
	 * Debug
	 *
	 * @param sText String
	 */
	public static void Debug(String sText) {
		Program.Debug("Supprimer_Rangement: " + sText);
	}

	@Override
	public boolean tabWillClose(TabEvent event) {
		return true;
	}

	@Override
	public void tabClosed() {
		Start.updateMainPanel();
		Program.deletePlace = null;
	}

	public void setUpdateView(){
		updateView  = true;
	}
	/**
	 * Mise à jour de la liste des rangements
	 */
	public void updateView() {
		if(!updateView)
			return;
		updateView = false;
		RangementUtils.putTabStock();
		choix.removeAllItems();
		choix.addItem("");
		for (int i = 0; i < Program.GetCaveLength(); i++) {
			choix.addItem(Program.getCave(i).getNom());
		}
	}

	class SupprimerModel extends DefaultTableModel{

		private static final long serialVersionUID = -3295046126691124148L;
		private LinkedList<SupprimerLine> list = null;
		private LinkedList<Column> columns = null;
		private boolean isCaisse = false;
		private Column colLine = new Column( Column.LINE, Program.getLabel("Infos027"));
		public SupprimerModel(LinkedList<SupprimerLine> list)
		{
			this.list = list;
			columns = new LinkedList<Column>();
			columns.add(new Column( Column.PART, Program.getLabel("Infos059")));
			columns.add(colLine);
			columns.add(new Column( Column.WINE, Program.getLabel("Infos057")));

		}
		public void setCaisse(boolean caisse)
		{
			if(isCaisse != caisse)
			{
				isCaisse = caisse;
				if(isCaisse)
					columns.remove(colLine);
				else
					columns.add(1, colLine);
			}
			fireTableStructureChanged();
		}
		@Override
		public int getColumnCount() {
			return columns.size();
		}
		@Override
		public String getColumnName(int column) {
			return ((Column)columns.get(column)).getLabel();
		}
		@Override
		public int getRowCount() {
			if(list == null)
				return 0;
			return list.size();
		}
		@Override
		public Object getValueAt(int row, int column) {
			SupprimerLine line = list.get(row);
			Column col = columns.get(column);
			switch(col.getCol())
			{
			case 0:
				return line.getNumPartLabel();
			case 1:
				return line.getNbLineLabel();
			case 2:
				return line.getNbWineLabel();
			}
			return "";
		}
		@Override
		public boolean isCellEditable(int row, int column) {
			return false;
		}

		class Column{
			public static final int PART = 0;
			public static final int LINE = 1;
			public static final int WINE = 2;

			private int col;
			private String label;
			public Column(int col, String label) {
				this.col = col;
				this.label = label;
			}
			public int getCol() {
				return col;
			}
			public String getLabel() {
				return label;
			}
		}
	}

	class SupprimerLine{
		private String place;
		private int numPart;
		private int nbLine;
		private int nbWine;
		public SupprimerLine(String place, int numPart, int nbLine, int nbWine) {
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
		public String getNumPartLabel() {
			return Program.getLabel("Infos029") + " "+ numPart;
		}
		public void setNumPart(int numPart) {
			this.numPart = numPart;
		}
		public int getNbLine() {
			return nbLine;
		}
		public String getNbLineLabel() {
			if(nbLine <= 1)
				return nbLine  + " " + Program.getLabel("Infos060");
			return nbLine  + " " + Program.getLabel("Infos061");
		}
		public void setNbLine(int nbLine) {
			this.nbLine = nbLine;
		}
		public int getNbWine() {
			return nbWine;
		}
		public String getNbWineLabel() {
			if(nbWine <= 1)
				return nbWine + " " + Program.getLabel("Infos063");
			return nbWine + " " + Program.getLabel("Infos064");
		}
		public void setNbWine(int nbWine) {
			this.nbWine = nbWine;
		}

	}

} // Fin de la classe
