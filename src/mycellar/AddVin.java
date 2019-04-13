package mycellar;

import mycellar.Bouteille.BouteilleBuilder;
import mycellar.actions.ChooseCellAction;
import mycellar.core.IAddVin;
import mycellar.core.ICutCopyPastable;
import mycellar.core.MyCellarButton;
import mycellar.core.MyCellarLabel;
import mycellar.core.MyCellarManageBottles;
import mycellar.core.MyCellarSettings;
import mycellar.core.PanelVignobles;
import mycellar.core.PopupListener;
import mycellar.core.datas.MyCellarBottleContenance;
import net.miginfocom.swing.MigLayout;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EtchedBorder;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.MessageFormat;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;


/**
 * <p>Titre : Cave à vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2005</p>
 * <p>Société : Seb Informatique</p>
 * @author Sébastien Duché
 * @version 24.4
 * @since 12/04/19
 */
public class AddVin extends MyCellarManageBottles implements Runnable, ITabListener, IAddVin, ICutCopyPastable {

	private static final long serialVersionUID = -8925831759212999905L;
	private boolean m_bmodify = false; // Pour la Modification
	private boolean m_bIsPlaceModify = true; // Pour la Modification
	private final MyCellarLabel m_avant1 = new MyCellarLabel(); // Pour la Modification 
	private final MyCellarLabel m_avant2 = new MyCellarLabel(); // Pour la Modification 
	private final MyCellarLabel m_avant3 = new MyCellarLabel(); // Pour la Modification 
	private final MyCellarLabel m_avant4 = new MyCellarLabel(); // Pour la Modification 
	private final MyCellarLabel m_avant5 = new MyCellarLabel(); // Pour la Modification
	private String m_sb_empl; //Pour la Modification
	private int m_nb_num, m_nb_lig, m_nb_col; //Pour la Modification
	private ListVin m_lv;
	private LinkedList<Bouteille> listBottleInModification; //Pour enlever dans ListVin
	private int m_nnb_bottle_add_only_one_place = 0;
	private final AddVin instance;

	/**
	 * AddVin: Constructeur pour l'ajout de vins
	 *
	 */
	public AddVin() {
		super();
		instance = this;
		Debug("Constructor");
		m_laBouteille = null;
		m_add = new MyCellarButton(new AddAction());
		m_cancel = new MyCellarButton(new CancelAction());
		
		m_lieu.setModifyActive(false);
		m_num_lieu.setModifyActive(false);
		m_line.setModifyActive(false);
		m_column.setModifyActive(false);
		m_year.setModifyActive(false);
		m_half.setModifyActive(false);
		m_price.setModifyActive(false);
		m_maturity.setModifyActive(false);
		m_parker.setModifyActive(false);
		m_colorList.setModifyActive(true);
		m_comment.setModifyActive(false);
		m_chooseCell = new MyCellarButton(new ChooseCellAction(instance));
		m_add.setMnemonic(AJOUTER);
		try {		
			LinkedList<String> list = new LinkedList<>();
			list.add("");
			list.addAll(Program.getStorage().getBottleNames());
			name = new JCompletionComboBox(list.toArray());
			name.setCaseSensitive(false);
			name.setEditable(true);

			m_half.addItem("");
			for(String s: MyCellarBottleContenance.getList()) {
				m_half.addItem(s);
			}
			m_half.setSelectedItem(MyCellarBottleContenance.getDefaultValue());

			// Init à vide des valeurs spécifiques modification
			m_nb_num = m_nb_lig = m_nb_col = -1;

			m_contenance.setText(Program.getLabel("Infos134")); //"Demie bouteille");
			m_annee_auto.setText(MessageFormat.format(Program.getLabel("Infos117"), ( (SIECLE + 1) * 100))); //"Année 00 -> 2000");
			m_annee_auto.setSelected(Program.getCaveConfigBool(MyCellarSettings.ANNEE_AUTO, false));
			m_noYear.setText(Program.getLabel("Infos399"));

			m_nb_bottle.setToolTipText(Program.getLabel("Infos263"));
			m_nb_bottle.setValue(1);
			m_nb_bottle.addChangeListener((e) -> {
					m_labelStillToAdd.setText("");
					if (Integer.parseInt(m_nb_bottle.getValue().toString()) <= 0) {
						m_nb_bottle.setValue(1);
				}
			});
			
			m_price.addKeyListener(new KeyAdapter() {
		        @Override
		        public void keyTyped(KeyEvent e) {
					if(e.getKeyChar() == ',' || e.getKeyChar() == '.') {
						e.consume();
						char sep = Program.getDecimalSeparator();
						String text = m_price.getText();
						m_price.setText(text+sep);
					}
				}
		});

			m_add.setText(Program.getLabel("Infos071"));

			PopupListener popup_l = new PopupListener();
			name.addMouseListener(popup_l);
			m_year.addMouseListener(popup_l);
			m_price.addMouseListener(popup_l);
			m_comment.addMouseListener(popup_l);
			m_maturity.addMouseListener(popup_l);
			m_parker.addMouseListener(popup_l);

			m_labelStillToAdd.setForeground(Color.red);
			m_end.setForeground(Color.red);
			m_end.setHorizontalAlignment(SwingConstants.CENTER);
			setLayout(new BorderLayout());
			add(new PanelMain(), BorderLayout.CENTER);

			m_manageContenance.addActionListener(this::manageContenance_actionPerformed);
			//Add name of place
			m_annee_auto.addActionListener(this::annee_auto_actionPerformed);

			m_noYear.addActionListener((e) -> {
				if(m_noYear.isSelected()) {
					m_year.setText(Bouteille.NON_VINTAGE);
					m_year.setEditable(false);
				}
				else {
					m_year.setText("");
					m_year.setEditable(true);
				}
			});


			m_lieu.addItem("");
			boolean complex = false;
			for (Rangement rangement : Program.getCave()) {
				m_lieu.addItem(rangement.getNom());
				if(!rangement.isCaisse()) {
					complex = true;
				}
			}
			m_chooseCell.setEnabled(complex);

			// Listener sur les combobox
			// _________________________

			setListeners();

			managePlaceCombos();

			setVisible(true);
			Debug("Constructor End");
		}
		catch (Exception e) {
			Program.showException(e);
		}
	}

	/**
	 * Remise à zéro des champs saisissable
	 */
	private void resetValues() {
		Debug("Reset Values...");
		name.removeAllItems();
		name.addItem("");
		for(String s:Program.getStorage().getBottleNames()) {
			name.addItem(s);
		}

		name.setEnabled(true);
		if(m_noYear.isSelected()) {
			m_year.setText(Bouteille.NON_VINTAGE);
		} else {
			m_year.setText("");
		}
		m_price.setText("");
		m_comment.setText("");
		m_maturity.setText("");
		m_parker.setText("");
		m_colorList.setSelectedItem(BottleColor.NONE);
		m_colorList.setModified(false);
		m_nb_bottle.setValue(1);
		m_labelStillToAdd.setText("");
		if(!m_bmodify) {
			if(m_lieu.getItemCount() > 0) {
				m_lieu.setSelectedIndex(0);
			}
			managePlaceCombos();
		}	else {
			m_lieu.setSelectedIndex(0);
		}
		m_labelExist.setText("");
		Search.updateTable();
		panelVignobles.resetCombos();
		Debug("Reset Values Done");
	}

	/**
	 * lieu_itemStateChanged: Fonction pour la liste des lieux.
	 *
	 * @param e ItemEvent
	 */
	@Override
	protected void lieu_itemStateChanged(ItemEvent e) {
		if(isListenersDisabled()) {
			return;
		}
		SwingUtilities.invokeLater(() -> {

			Debug("Lieu_itemStateChanging...");
			int lieu_select = m_lieu.getSelectedIndex();

			m_labelExist.setText("");

			m_preview.setEnabled(lieu_select > 0);

			if (lieu_select == 0 && m_bmodify) {
				m_bIsPlaceModify = false;
			}
			Rangement cave;
			int nb_emplacement = 0;
			int start_caisse = 0;
			boolean bIsCaisse = false;
			if (lieu_select > 0 && (cave = Program.getCave(lieu_select - 1)) != null) {
				nb_emplacement = cave.getNbEmplacements();
				bIsCaisse = cave.isCaisse();
				start_caisse = cave.getStartCaisse();
			}
			if (bIsCaisse) { //Type caisse
				m_preview.setEnabled(false);
				m_num_lieu.removeAllItems();
				m_num_lieu.addItem("");
				for (int i = 0; i < nb_emplacement; i++) {
					m_num_lieu.addItem(Integer.toString(i + start_caisse));
				}
				if (nb_emplacement == 1) {
					m_num_lieu.setSelectedIndex(1);
				}
			}	else {
				if (m_bmodify && lieu_select > 0) {
					m_bIsPlaceModify = true;
				}
				m_num_lieu.removeAllItems();
				m_line.removeAllItems();
				m_column.removeAllItems();
				m_num_lieu.addItem("");
				for (int i = 1; i <= nb_emplacement; i++) {
					m_num_lieu.addItem(Integer.toString(i));
				}
			}
			managePlaceCombos();
			Debug("Lieu_itemStateChanging...End");
		});
	}

	/**
	 * line_itemStateChanged: Fonction pour la liste des lignes.
	 *
	 * @param e ItemEvent
	 */
	@Override
	protected void line_itemStateChanged(ItemEvent e) {
		if(isListenersDisabled()) {
			return;
		}
		SwingUtilities.invokeLater(() -> {
			Debug("Line_itemStateChanging...");

			int num_select = m_line.getSelectedIndex();
			int emplacement = m_num_lieu.getSelectedIndex();
			int lieu_select = m_lieu.getSelectedIndex();

			m_labelExist.setText("");

			m_column.setEnabled(num_select != 0);

			Rangement cave;
			int nb_col = 0;
			if (num_select > 0 && (cave = Program.getCave(lieu_select - 1)) != null) {
				nb_col = cave.getNbColonnes(emplacement - 1, num_select - 1);
			}
			m_column.removeAllItems();
			m_column.addItem("");
			for (int i = 1; i <= nb_col; i++) {
				m_column.addItem(Integer.toString(i));
			}
			Debug("Line_itemStateChanging...End");
		});
	}

	/**
	 * setBottles: Fonction de chargement de plusieurs vins dans la fenêtre pour la classe ListVin
	 *
	 * @param bottles LinkedList<Bouteille>
	 */
	public void setBottles(LinkedList<Bouteille> bottles) {
		Debug("Set Bottles...");
		if(m_lv == null) {
				m_lv = new ListVin(bottles, this);
				add(m_lv, BorderLayout.WEST);
		}	else {
			m_lv.setBottles(bottles);
		}

		setBottle(bottles.getFirst());
	}

	/**
	 * setBottle: Fonction de chargement d'un vin dans la fenêtre pour la classe ListVin
	 *
	 * @param bottle Bouteille
	 */
	private void setBottle(Bouteille bottle) {

		Debug("Set Bottle...");
		try {
			m_laBouteille = bottle;
			listBottleInModification = new LinkedList<>();
			listBottleInModification.add(bottle);
			m_bmodify = true;
			enableAll(true);
			m_nb_bottle.setValue(1);
			m_nb_bottle.setEnabled(false);
			name.setSelectedItem(bottle.getNom());
			m_year.setText(bottle.getAnnee());
			m_noYear.setSelected(bottle.isNonVintage());
			if(bottle.isNonVintage()) {
				m_year.setEditable(false);
			}
			m_half.removeAllItems();
			m_half.addItem("");
			for(String s: MyCellarBottleContenance.getList()) {
					m_half.addItem(s);
			}
			m_half.setSelectedItem(bottle.getType());
			String half_tmp = "";
			Object oHalf = m_half.getSelectedItem();
			if(oHalf != null) {
				half_tmp = oHalf.toString();
			}
			final boolean auto = Program.getCaveConfigBool(MyCellarSettings.TYPE_AUTO, false);

			if (half_tmp.compareTo(bottle.getType()) != 0 && auto && !bottle.getType().isEmpty()) {
				MyCellarBottleContenance.getList().add(bottle.getType());
				m_half.addItem(bottle.getType());
				m_half.setSelectedItem(bottle.getType());
			}

			m_price.setText(Program.convertStringFromHTMLString(bottle.getPrix()));
			m_comment.setText(bottle.getComment());
			m_maturity.setText(bottle.getMaturity());
			m_parker.setText(bottle.getParker());
			m_colorList.setSelectedItem(BottleColor.getColor(bottle.getColor()));
			panelVignobles.initializeVignobles(bottle);

			m_avant1.setText(Program.getLabel("Infos091")); //"Avant");
			m_avant2.setText(bottle.getEmplacement());
			m_avant3.setText(Integer.toString(bottle.getNumLieu()));
			m_avant4.setText(Integer.toString(bottle.getLigne()));
			m_avant5.setText(Integer.toString(bottle.getColonne()));
			m_avant1.setVisible(true);
			m_avant2.setVisible(true);
			m_avant3.setVisible(true);
			m_avant4.setVisible(true);
			m_avant5.setVisible(true);
			m_add.setText(Program.getLabel("Infos079"));
			m_sb_empl = bottle.getEmplacement();
			m_nb_num = bottle.getNumLieu();
			m_nb_lig = bottle.getLigne();
			m_nb_col = bottle.getColonne();

			if (m_line.isVisible()) {
				m_line.setEnabled(false);
				m_num_lieu.setEnabled(false);
				m_lieu.setEnabled(true);
			}

			Rangement rangement = Program.getCave(m_sb_empl);
			if (rangement != null) {
				boolean bIsCaisse = rangement.isCaisse();
				m_line.setVisible(!bIsCaisse);
				m_column.setVisible(!bIsCaisse);
				m_avant4.setVisible(!bIsCaisse);
				m_avant5.setVisible(!bIsCaisse);
				m_labelLine.setVisible(!bIsCaisse);
				m_labelColumn.setVisible(!bIsCaisse);
			}
			m_end.setText(Program.getLabel("Infos092")); //"Saisir les modifications");
		}
		catch (Exception e) {
			Program.showException(e);
		}
		Debug("Set Bottle... End");
	}

	/**
	 * setBottlesInModification: Fonction pour le chargement de vins dans la fenêtre pour
	 * la classe ListVin.
	 *
	 * @param bouteilles LinkedList<Bouteille>: Liste des bouteilles
	 */
	void setBottlesInModification(LinkedList<Bouteille> bouteilles) {

		Debug("setBottlesInModification...");
		try {
			m_bmulti = bouteilles.size() > 1;
			listBottleInModification = bouteilles;

			resetValues();
			if(m_bmulti) {
				name.setSelectedItem(MessageFormat.format(Program.getLabel("Infos140"), listBottleInModification.size())); //" bouteilles sélectionn�es");
				name.setEnabled(false);
				m_annee_auto.setEnabled(false);
				m_noYear.setEnabled(false);
				m_nb_bottle.setEnabled(false);
				m_year.setEditable(false);
				if (m_half.getItemCount() > 0) {
					m_half.setSelectedIndex(0);
				}
				m_avant1.setText("");
				m_avant2.setText("");
				m_avant3.setText("");
				m_avant4.setText("");
				m_avant5.setText("");
				m_add.setEnabled(true);
				m_lieu.setEnabled(true);
				
				if(!m_bmodify) {
					if(m_lieu.getItemCount() > 0) {
						m_lieu.setSelectedIndex(0);
					}
					if(m_lieu.getItemCount() == 2) {
						m_lieu.setSelectedIndex(1);
						if(m_num_lieu.getItemCount() == 2) {
							m_num_lieu.setSelectedIndex(1);
						}
					}
				}

				if (m_bmodify && m_line.isVisible()) {
					m_line.setEnabled(false);
					m_num_lieu.setEnabled(false);
					m_lieu.setEnabled(true);
				}
				m_end.setText(Program.getLabel("Infos141")); //"Vous ne pouvez déplacer plusieurs bouteilles que dans une caisse");
			}
			else {
				setBottle(listBottleInModification.getFirst());
			}
		}
		catch (Exception e) {
			Program.showException(e);
		}
		Debug("setBottlesInModification...End");
	}


	/**
	 * keylistener_actionPerformed: Fonction d'écoute du clavier.
	 *
	 * @param e KeyEvent
	 */
	protected void keylistener_actionPerformed(KeyEvent e) {
		if ( (e.getKeyCode() == AJOUTER && e.isControlDown()) || e.getKeyCode() == KeyEvent.VK_ENTER) {
			new AddAction().actionPerformed(null);
		}
		if (e.getKeyCode() == PREVIEW && e.isControlDown() && m_preview.isEnabled()) {
			preview_actionPerformed(null);
		}
		if (e.getKeyCode() == KeyEvent.VK_C) {
			copy();
		}
		if (e.getKeyCode() == KeyEvent.VK_X) {
			cut();
		}
		if (e.getKeyCode() == KeyEvent.VK_V) {
			paste();
		}
	}
	
	private boolean controlBottle() {
		Debug("Control Bottle...");
		boolean resul = true;
		
		String nom = name.getEditor().getItem().toString();
		//Vérification du nom ...
		if (nom.isEmpty()) {
			Debug("ERROR: Wrong Name");
			resul = false;
			Erreur.showSimpleErreur(Program.getError("Error054")); //"Veuillez saisir le nom du vin!"
		}
		
		// Controle de la date
		if (resul && !m_bmulti && (m_year.isEditable() || m_noYear.isSelected())) {
			String annee = m_year.getText().trim();

			// Erreur sur la date
			if (!Bouteille.isValidYear(annee)) {
				Debug("ERROR: Wrong date");
				Erreur.showSimpleErreur(Program.getError("Error053")); //"Veuillez saisir une année valide!"
				resul = false;
				m_year.setEditable(true);
			}
			else {
				annee = getYear();
				m_year.setText(annee);
			}
		}
		
		int lieu_selected = m_lieu.getSelectedIndex();
		int lieu_num_selected = m_num_lieu.getSelectedIndex();
		if (lieu_selected == 0 && !m_bmodify && resul) {
			Debug("ERROR: Wrong Place");
			resul = false;
			Erreur.showSimpleErreur(Program.getError("Error055")); //"Veuillez sélectionner un emplacement!"
		}
		
		if (resul && ((!m_bmodify && lieu_num_selected == 0) || (m_bmodify && lieu_num_selected == 0 && lieu_selected != 0))) { //Nécessite la sélection du numéro de lieu
			Debug("ERROR: Wrong Num Place");
			Erreur.showSimpleErreur(m_line.isVisible() ? Program.getError("Error056") : Program.getError("Error174"));
			m_num_lieu.setEnabled(true);
			enableAll(true);
			resul = false;
		}
		
		Debug("Control Bottle... End");
		return resul;
	}

	/**
	 * run: Exécution des tâches.
	 */
	@Override
	public void run() {
		Debug("Running...");

		try {
			if(!controlBottle()) {
				m_end.setText("");
				enableAll(true);
				return;
			}
			// Ajout ou modification
			Debug("Adding / Modifying...");
			int nb_bottle_rest = Integer.parseInt(m_nb_bottle.getValue().toString()) - 1;
			int lieu_selected = m_lieu.getSelectedIndex();
			int lieu_num_selected = m_num_lieu.getSelectedIndex();
			String prix = m_price.getText();
			String comment1 = m_comment.getText();
			String dateOfC = m_maturity.getText();
			String parker = m_parker.getText();
			BottleColor bottleColor = (BottleColor) m_colorList.getSelectedItem();
			String color = bottleColor != null ? bottleColor.name() : "";
			String country = panelVignobles.getCountry();
			String vignoble = panelVignobles.getVignoble();
			String aoc = panelVignobles.getAOC();
			String igp = panelVignobles.getIGP();

			String annee = "";

			String nom = name.getEditor().getItem().toString();

			// Controle de la date
			if (!m_bmulti && (m_year.isEditable() || m_noYear.isSelected())) {
				annee = getYear();
				m_year.setText(annee);
			}

			String demie = m_half.getSelectedItem() != null ? m_half.getSelectedItem().toString() : "";

			if (m_bmodify) {
				//On grise les champs en cours de modif
				Debug("Modifying in Progress...");
				m_end.setText(Program.getLabel("Infos142")); //"Modification en cours..."
				enableAll(false);
			}
			
			String sPlaceName = "";
			boolean bModifyPlace = true;
			boolean bIsCaisse = false;
			if (lieu_selected > 0) {
				sPlaceName = Program.getCave(lieu_selected - 1).getNom();
				bIsCaisse = Program.getCave(lieu_selected - 1).isCaisse();
			}
			else if (m_bmodify) { //Si aucun emplacement n'a été sélectionné (modif du nom)
				bModifyPlace = false;
				sPlaceName = m_sb_empl;
				lieu_num_selected = 1;
				lieu_selected = Program.getCaveIndex(sPlaceName) + 1;
				bIsCaisse = Program.getCave(lieu_selected - 1).isCaisse();
			}

			m_end.setText(Program.getLabel("Infos312"));
			boolean m_bbottle_add = false;
			boolean resul = true;
			if (bIsCaisse) {
				//Caisse
				Debug("Is a Caisse");
				Rangement caisse = Program.getCave(lieu_selected - 1);
				if(!caisse.hasFreeSpaceInCaisse(lieu_num_selected - 1)) {
					Erreur.showSimpleErreur(Program.getError("Error154"), Program.getError("Error153"));
					m_end.setText("");
					return;
				}

				if (!m_bmulti) {
					Bouteille bouteille = new BouteilleBuilder(nom)
										.annee(annee)
										.type(demie)
										.place(sPlaceName)
										.numPlace(lieu_num_selected+caisse.getStartCaisse()-1)
										.price(prix)
										.comment(comment1)
										.maturity(dateOfC)
										.parker(parker)
										.color(color)
										.vignoble(country, vignoble, aoc, igp, aoc).build();
					// Add multiple bottle with question
					if (nb_bottle_rest > 0) {
						if (m_lieu.isEnabled() || m_num_lieu.isEnabled()) {
							Debug("Adding multiple bottles in the same place?");
							String erreur_txt1 = MessageFormat.format(Program.getError("Error061"), (nb_bottle_rest + 1), sPlaceName); //Voulez vous ajouter les xx bouteilles dans yy
							if( JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(this, erreur_txt1, Program.getLabel("Infos049"), JOptionPane.YES_NO_OPTION )) {
								//Add several bottles in Caisse
								Debug("Adding multiple bottles in the same place: YES");
								
								if (caisse.isLimited() && (caisse.getNbCaseUse(lieu_num_selected-1) + nb_bottle_rest) >= caisse.getNbColonnesStock()) {
									Erreur.showSimpleErreur(Program.getError("Error154"), Program.getError("Error153"));
								} else {
									for (int j = 0; j <= nb_bottle_rest; j++) {
										Bouteille b = new Bouteille(bouteille);
										Program.getStorage().addHistory(History.ADD, b);
										caisse.addWine(b);
									}
									m_end.setText(MessageFormat.format(Program.getLabel("Infos076"), (nb_bottle_rest + 1)));
									resetValues();
								}
							}	else {
								Debug("Adding multiple bottles in the same place: NO");
								//Add a single bottle in Caisse
								Rangement rangement = Program.getCave(lieu_selected - 1);
								if (rangement.hasFreeSpaceInCaisse(lieu_num_selected)) {
									Program.getStorage().addHistory(History.ADD, bouteille);
									rangement.addWine(bouteille);
									m_end.setText( Program.getLabel("Infos075"));
									setStillNbBottle(nb_bottle_rest);
								}	else {
									Erreur.showSimpleErreur(Program.getError("Error154"), Program.getError("Error153"));
								}
							}
						}	else { //Un seul rangement simple
							if (Program.getCave(lieu_selected - 1).isLimited() && (Program.getCave(lieu_selected - 1).getNbCaseUse(lieu_num_selected) + nb_bottle_rest + 1) > Program.getCave(lieu_selected - 1).getNbColonnesStock()) {
								resul = false;
								Debug("ERROR: This caisse is full. Unable to add all bottles in the same place!");
								m_bbottle_add = false;
								Erreur.showSimpleErreur(Program.getError("Error154"), Program.getError("Error153"));
							}	else {
								m_nnb_bottle_add_only_one_place = nb_bottle_rest + 1;
								for (int z = 0; z < nb_bottle_rest; z++) {
									Bouteille b = new Bouteille(bouteille);
									Program.getStorage().addHistory(History.ADD, b);
									Program.getCave(lieu_selected - 1).addWine(b);
								}
								nb_bottle_rest = 0;
							}
						}
					} // Fin de l'ajout de plusieurs bouteilles restantes

					if (nb_bottle_rest == 0 && resul) {
						if (lieu_num_selected == 0) {
							Erreur.showSimpleErreur(Program.getError("Error174"));
							resul = false;
						}
						if (resul) {
							boolean addReturn = true;
							if(m_bmodify) {
								//Suppression de la bouteille lors de la modification
								Debug("Updating bottle when modifying");
								m_laBouteille.update(bouteille);
								Program.getStorage().addHistory( History.MODIFY, m_laBouteille);

								Rangement r = Program.getCave(m_sb_empl);
								if (r != null) {
									r.clearStock(new BouteilleBuilder("").numPlace(m_nb_num).line(m_nb_lig).column(m_nb_col).build());
								}
							}	else {
								//Ajout de la bouteille
								Debug("Adding bottle...");
								Program.getStorage().addHistory( History.ADD, bouteille);
								addReturn = Program.getCave(lieu_selected - 1).addWine(bouteille);
							}

							//Ajout dans ALL
							if (addReturn) {
								m_bbottle_add = true;
								resetValues();

								if (m_bmodify && m_line.isVisible()) {
									m_line.setEnabled(false);
									m_num_lieu.setEnabled(false);
									m_lieu.setEnabled(true);
								}
								m_price.setText("");
								m_maturity.setText("");
								m_parker.setText("");
								m_colorList.setSelectedItem(BottleColor.NONE);
							}	else {
								Debug("ERROR: Adding bottle");
								m_bbottle_add = false;
								Erreur.showSimpleErreur(MessageFormat.format(Program.getError("Error151"), Program.getCave(lieu_selected - 1).getNom()),
										Program.getError("Error153"));
								resul = false;
							}
						}
					}
				}
				else { //if(! m_bmulti) Multi == true => Modification de plusieurs vins vers une caisse
					//Récupération des diffrentes bouteilles
					Debug("Modifying multiple bottle to a Caisse");
					resul = true;
					if(!bModifyPlace) {
						Debug("Modifying without changing place");
						boolean bOneBottle = listBottleInModification.size() == 1;
						// Modification sans changement de lieu 10/05/08
						for (Bouteille tmp: listBottleInModification) {
							if(bOneBottle || !comment1.isEmpty()) {
								tmp.setComment(comment1);
							}
							if(bOneBottle || !dateOfC.isEmpty()) {
								tmp.setMaturity(dateOfC);
							}
							if(bOneBottle || !parker.isEmpty()) {
								tmp.setParker(parker);
							}
							if(bOneBottle || m_colorList.isModified()) {
								tmp.setColor(color);
							}
							if(bOneBottle || !demie.isEmpty()) {
								tmp.setType(demie);
							}
							if(bOneBottle || !prix.isEmpty()) {
								tmp.setPrix(prix);
							}
							if(bOneBottle || !country.isEmpty() || !vignoble.isEmpty() || !aoc.isEmpty() || !igp.isEmpty()) {
								tmp.setVignoble(new Vignoble(country, vignoble, aoc, igp, aoc));
							}
							//Ajout des bouteilles dans la caisse
							
							if (m_bmodify) {
								Debug("Modifying bottle...");
								Program.getStorage().addHistory(History.MODIFY, tmp);
								m_bbottle_add = true;
							}	else {
								Debug("Adding bottle...");
								Program.getStorage().addHistory(History.ADD, tmp);
								//Ajout des bouteilles
								if (Program.getCave(tmp.getEmplacement()).addWine(tmp)) {
									m_bbottle_add = true;
									resetValues();
									if (m_half.getItemCount() > 1) {
										m_half.setSelectedIndex(1);
									}
									m_labelStillToAdd.setText("");
								}
							}
						}
						if(m_bmodify) {
							resetValues();
							if (m_half.getItemCount() > 1) {
								m_half.setSelectedIndex(1);
							}
						}
					}	else {
						Debug("Modifying with changing place");
						int nLieuNum = m_num_lieu.getSelectedIndex();
						if(nLieuNum == 0) {
							resul = false;
							Debug("ERROR: Wrong place number");
							Erreur.showSimpleErreur(Program.getError("Error056"));
							m_num_lieu.setEnabled(true);
							m_lieu.setEnabled(true);
							m_add.setEnabled(true);
							m_end.setText("");
						}	else {
							nLieuNum = Integer.parseInt(m_num_lieu.getItemAt(lieu_num_selected));
							int nbbottle = listBottleInModification.size();
							final Rangement cave = Program.getCave(lieu_selected - 1);
							if (cave.isLimited() && (cave.getNbCaseUse(nLieuNum) + nbbottle) > cave.getNbColonnesStock()) {
								Debug("ERROR: Not enough place!");
								Erreur.showSimpleErreur(Program.getError("Error154"), Program.getError("Error153"));
								m_lieu.setEnabled(true);
								m_add.setEnabled(true);
								m_end.setText("");
							}	else {
								boolean bOneBottle = listBottleInModification.size() == 1;
								for (Bouteille tmp:listBottleInModification) {
									if(bOneBottle || !comment1.isEmpty()) {
										tmp.setComment(comment1);
									}
									if(bOneBottle || !dateOfC.isEmpty()) {
										tmp.setMaturity(dateOfC);
									}
									if(bOneBottle || !parker.isEmpty()) {
										tmp.setParker(parker);
									}
									if(bOneBottle || m_colorList.isModified()) {
										tmp.setColor(color);
									}
									if(bOneBottle || !demie.isEmpty()) {
										tmp.setType(demie);
									}
									if(bOneBottle || !prix.isEmpty()) {
										tmp.setPrix(prix);
									}
									if(bOneBottle || !country.isEmpty() || !vignoble.isEmpty() || !aoc.isEmpty() || !igp.isEmpty()) {
										tmp.setVignoble(new Vignoble(country, vignoble, aoc, igp, aoc));
									}
									// Add multiple bottle with question
									Debug("Adding multiple bottles...");
									if (m_bmodify) {
										Rangement r = Program.getCave(tmp.getEmplacement());
										if(r != null) {
											Debug("Delete from simple place");
											r.clearStock(tmp);
										}
									}
									//Ajout des bouteilles dans la caisse
									tmp.setEmplacement(sPlaceName);
									tmp.setNumLieu(nLieuNum);
									tmp.setLigne(0);
									tmp.setColonne(0);
									Debug("Adding bottle.");
									Program.getStorage().addHistory(m_bmodify ? History.MODIFY : History.ADD, tmp);
									//Ajout des bouteilles dans ALL
									if(m_bmodify) {
										m_bbottle_add = true;
										resetValues();
										if (m_half.getItemCount() > 1) {
											m_half.setSelectedIndex(1);
										}
									}	else {
										if (Program.getCave(sPlaceName).addWine(tmp)) {
											m_bbottle_add = true;
											resetValues();
											if (m_half.getItemCount() > 1) {
												m_half.setSelectedIndex(1);
											}
											m_labelStillToAdd.setText("");
										}
									}
								}
								if(!m_bmodify){
									if(m_lieu.getItemCount() > 0) {
										m_lieu.setSelectedIndex(0);
									}
									if(m_lieu.getItemCount() == 2) {
										m_lieu.setSelectedIndex(1);
										if(m_num_lieu.getItemCount() == 2) {
											m_num_lieu.setSelectedIndex(1);
										}
									}
								} else {
									m_lieu.setSelectedIndex(0);
								}

								if (m_bmodify && m_line.isVisible()) {
									m_line.setEnabled(false);
									m_num_lieu.setEnabled(false);
									m_lieu.setEnabled(true);
								}
							}
						}
					}
				}
			}
			else if( m_lieu.getSelectedIndex() == 0) {
				if(!m_bmulti) {
					// Modification d'une bouteille dans Armoire sans changement de lieu
					Debug("Modifying one bottle in Armoire without changing place");
					Bouteille tmp = new BouteilleBuilder(nom)
						.annee(annee)
						.type(demie)
						.place(m_laBouteille.getEmplacement())
						.numPlace(m_laBouteille.getNumLieu())
						.line(m_laBouteille.getLigne())
						.column(m_laBouteille.getColonne())
						.price(prix)
						.comment(comment1)
						.maturity(dateOfC)
						.parker(parker)
						.color(color)
						.vignoble(country, vignoble, aoc, igp, aoc).build();
					Debug("Replacing bottle...");
					m_laBouteille.update(tmp);
					// Remplacement de la bouteille
					Program.getStorage().addHistory(History.MODIFY, tmp);
					m_bbottle_add = true;
					resetValues();
					if (m_half.getItemCount() > 0) {
						m_half.setSelectedIndex(0);
					}
					m_labelStillToAdd.setText("");
				}
				else {
					// Modification de bouteilles dans Armoire sans changement de lieu
					Debug("Modifying multiple bottles in Armoire without changing place");
					// Modification sans changement de lieu 11/05/08
					for (Bouteille tmp:listBottleInModification) {
						Rangement rangement = tmp.getRangement();
						boolean aCaisse = rangement.isCaisse();
						tmp.setPrix(prix);
						tmp.setComment(comment1);
						tmp.setMaturity(dateOfC);
						tmp.setParker(parker);
						tmp.setColor(color);
						tmp.setType(demie);
						if(!country.isEmpty() || !vignoble.isEmpty() || !aoc.isEmpty() || !igp.isEmpty()) {
							tmp.setVignoble(new Vignoble(country, vignoble, aoc, igp, aoc));
						}
						// Add multiple bottles
						Debug("Adding multiple bottles...");
						if (m_bmodify) {
							//Delete Bouteilles
							Debug("Deleting bottles when modifying");
							Program.getStorage().deleteWine(tmp);
							if (!aCaisse) { //Si ce n'est pas une caisse on supprime de stockage
								Debug("is Not a Caisse. Delete from stock");
								rangement.clearStock(tmp);
							}
						}
						//Ajout des bouteilles dans la caisse
						Debug("Adding bottle...");
						Program.getStorage().addHistory( m_bmodify? History.MODIFY : History.ADD, tmp);
						//Ajout des bouteilles dans ALL
						if (rangement.addWine(tmp)) {
							m_bbottle_add = true;
							resetValues();
							if (m_half.getItemCount() > 0) {
								m_half.setSelectedIndex(0);
							}
							m_labelStillToAdd.setText("");
						}
					}
				}
			}
			else {
				// Ajout dans une Armoire
				if (m_bmulti) { //On ne peut pas déplacer plusieurs bouteilles vers une armoire
					Debug("ERROR: Unable to move multiple bottles to an Armoire");
					m_end.setText("");
					Erreur.showSimpleErreur(MessageFormat.format(Program.getError("Error104"), m_lieu.getSelectedItem().toString().trim()), Program.getError("Error105")); //"Veuillez sélectionner un rangement de type caisse.");//Impossible de d�placer plusieurs bouteilles dans
					enableAll(true);
				}	else {
					// Modification d'une bouteille dans l'armoire
					int ligne = m_line.getSelectedIndex();
					lieu_num_selected = m_num_lieu.getSelectedIndex();
					int colonne = m_column.getSelectedIndex();
					
					if (lieu_num_selected == 0 && m_bIsPlaceModify && resul) {
						Debug("ERROR: Wrong place number");
						resul = false;
						m_end.setText("");
						Erreur.showSimpleErreur(Program.getError("Error056")); //"Veuillez sélectionner un numéro d'emplacement!"
						enableAll(true);
					}
					if (ligne == 0 && m_bIsPlaceModify && resul) {
						Debug("ERROR: Wrong line number");
						resul = false;
						m_end.setText("");
						Erreur.showSimpleErreur(Program.getError("Error057")); //"Veuillez sélectionner un numéro de ligne!"
						enableAll(true);
					}
					if (colonne == 0 && m_bIsPlaceModify && resul) {
						Debug("ERROR: Wrong column number");
						resul = false;
						m_end.setText("");
						Erreur.showSimpleErreur(Program.getError("Error058")); //"Veuillez sélectionner un numéro de colonne!"
						enableAll(true);
					}
					if (resul) {
						int nb_free_space = 0;
						Bouteille b = null;
						if (m_bmodify && !m_bIsPlaceModify) { //Si aucune modification du Lieu
							sPlaceName = m_sb_empl;
							lieu_num_selected = m_nb_num;
							ligne = m_nb_lig;
							colonne = m_nb_col;
							lieu_selected = Program.getCaveIndex(sPlaceName) + 1;
						}	else { //Si Ajout bouteille ou modification du lieu
							Debug("Adding bottle or modifying place");
							//Récupération de la bouteille présent à l'emplacement
							final Rangement cave = Program.getCave(lieu_selected - 1);
							b = cave.getBouteille(lieu_num_selected-1, ligne-1, colonne-1);
							if (b == null) {
								nb_free_space = cave.getNbCaseFreeCoteLigne(lieu_num_selected-1, ligne-1, colonne-1);
							}
						}
						//Création de la nouvelle bouteille
						Debug("Creating new bottle...");
						Bouteille tmp = new BouteilleBuilder(nom)
							.annee(annee)
							.type(demie)
							.place(sPlaceName)
							.numPlace(lieu_num_selected)
							.line(ligne)
							.column(colonne)
							.price(prix)
							.comment(comment1)
							.maturity(dateOfC)
							.parker(parker)
							.color(color)
							.vignoble(country, vignoble, aoc, igp, aoc).build();
						if (b == null) {
							//Case vide donc ajout
							if (m_bmodify) {
								Debug("Empty case: Modifying bottle");
								m_laBouteille.update(tmp);
								tmp.getRangement().updateToStock(tmp);
								Program.getStorage().addHistory(History.MODIFY, m_laBouteille);
								Rangement r = Program.getCave(m_sb_empl);
								if (!r.isCaisse()) {
									Debug("Deleting from older complex place");
									r.clearStock(new BouteilleBuilder("").numPlace(m_nb_num).line(m_nb_lig).column(m_nb_col).build());
								}
							}	else {
								Debug("Empty case: Adding bottle");
								Program.getStorage().addHistory(History.ADD, tmp);
								Program.getCave(lieu_selected - 1).addWine(tmp);
								if (nb_bottle_rest > 0 && nb_free_space > 1) { //Ajout de bouteilles côte à côte
									if(nb_free_space > (nb_bottle_rest + 1)) {
										nb_free_space = nb_bottle_rest + 1;
									}
									if(JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(this, MessageFormat.format(Program.getError("Error175"), nb_free_space), Program.getLabel("Infos049"), JOptionPane.YES_NO_OPTION)) {
										Debug("Putting multiple bottle in close place");
										m_nnb_bottle_add_only_one_place = nb_free_space;
										nb_bottle_rest = nb_bottle_rest - nb_free_space + 1;
										for (int z = 1; z < nb_free_space; z++) {
											tmp = new BouteilleBuilder(nom)
												.annee(annee)
												.type(demie)
												.place(sPlaceName)
												.numPlace(lieu_num_selected)
												.line(ligne)
												.column(colonne + z)
												.price(prix)
												.comment(comment1)
												.maturity(dateOfC)
												.parker(parker)
												.color(color)
												.vignoble(country, vignoble, aoc, igp, aoc).build();
											Program.getStorage().addHistory(History.ADD, tmp);
											Program.getCave(lieu_selected - 1).addWine(tmp);
										}
									}
								}
							}

							if (nb_bottle_rest > 0) {
								setStillNbBottle(nb_bottle_rest);
								m_lieu.setSelectedIndex(0);
							} else {
								resetValues();
								if (m_bmodify) {
									m_half.setEnabled(false);
									name.setEditable(false);
									m_year.setEditable(false);
									m_price.setEditable(false);
									m_maturity.setEditable(false);
									m_parker.setEditable(false);
									m_colorList.setEditable(false);
									m_comment.setEditable(false);
									m_add.setEnabled(false);
									m_lieu.setEnabled(false);
									m_num_lieu.setEnabled(false);
									m_line.setEnabled(false);
									m_column.setEnabled(false);
								} else {
									m_labelStillToAdd.setText("");
									if (m_half.getItemCount() > 0) {
										m_half.setSelectedIndex(0);
									}
									m_lieu.setSelectedIndex(0);
								}
							}
							m_lieu.setSelectedIndex(0);
							if (m_bmodify) {
								m_lieu.setEnabled(true);
							}
							if (m_bmodify && m_line.isVisible()) {
								m_line.setEnabled(false);
								m_num_lieu.setEnabled(false);
								m_lieu.setEnabled(true);
							}
							m_bbottle_add = true;
						}	else { // La case n'est pas vide
							Debug("WARNING: Not an empty place, Replace?");
							String erreur_txt1 = MessageFormat.format(Program.getError("Error059"), b.getNom(), b.getAnnee()); //" déjà présent à cette place!");
							String erreur_txt2 = Program.getError("Error060"); //"Voulez vous le remplacer?");
							if( JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(this, erreur_txt1 + "\n" + erreur_txt2, Program.getLabel("Infos049"), JOptionPane.YES_NO_OPTION)) {
								replaceWine(tmp, m_bmodify, b);
								if(m_bmodify) {
									m_laBouteille.update(tmp);
								}
								m_end.setText(m_bmodify ? Program.getLabel("Infos144") : Program.getLabel("Infos075"));
								resetValues();
							} else {
								m_end.setText(Program.getLabel("AddVin.NotSaved"));
								enableAll(true);
								resul = false;
							}
						}
					}
				}
			}

			if (m_bbottle_add) {
				if (m_bmodify) {
					if (m_lv != null) {
						m_lv.updateList(listBottleInModification);
					}
					if(listBottleInModification.size() == 1) {
						m_end.setText(Program.getLabel("Infos144")); //"1 bouteille modifiée");
					} else {
						m_end.setText(MessageFormat.format(Program.getLabel("Infos143"), listBottleInModification.size())); //" bouteilles modifiées");
					}
				}	else {
					if (m_nnb_bottle_add_only_one_place == 0) {
						m_end.setText(Program.getLabel("Infos075")); //"1 bouteille ajoutée");
					}	else {
						m_end.setText(MessageFormat.format(Program.getLabel("Infos076"), m_nnb_bottle_add_only_one_place)); //"x bouteilles ajoutées");
						m_nnb_bottle_add_only_one_place = 0;
					}
					//Remise des valeurs par défaut
					m_half.setSelectedItem(MyCellarBottleContenance.getDefaultValue());
				}
			}
			if(resul) {
				doAfterRun();
			}
		}
		catch (Exception e) {
			Program.showException(e);
		}
	}

	private void setStillNbBottle(int nb_bottle_rest) {
		m_nb_bottle.setValue(nb_bottle_rest);
		if(nb_bottle_rest == 1) {
			m_labelStillToAdd.setText(Program.getLabel("Infos067"));
		} else {
			m_labelStillToAdd.setText(MessageFormat.format(Program.getLabel("Infos062"), nb_bottle_rest));
		}
	}

	private void replaceWine(final Bouteille bottle, boolean modify, final Bouteille bToDelete) {
		Debug("replaceWine...");
		//Change wine in a place
		Program.getStorage().addHistory(modify ? History.MODIFY : History.ADD, bottle);
		Program.getStorage().deleteWine(bToDelete);
		if(!modify) {
			Program.getStorage().addWine(bottle);
		}	else {
			if(m_laBouteille != null) {
				final Rangement r = m_laBouteille.getRangement();
				if(!r.isCaisse()) {
					r.clearStock(m_laBouteille);
				}
			}

			if (m_lv != null) {
				m_lv.updateList(listBottleInModification);
			}
			Search.removeBottle(bToDelete);
			Search.updateTable();
		}
		final Rangement r = bottle.getRangement();
		if(!r.isCaisse()) {
			r.updateToStock(bottle);
		}
		Debug("replaceWine...End");
	}

	private void doAfterRun() {
		Debug("Do After Run...");
		m_laBouteille = null;
		Program.updateManagePlacePanel();
		panelVignobles.updateList();
		new Timer().schedule(
			new TimerTask() {
					@Override
					public void run() {
						SwingUtilities.invokeLater(() -> {
						m_end.setText("");
						});
					}
			},
			5000
		);
		if(!m_bmodify) {
			return;
		}

		if(m_lv == null) {
			enableAll(true);
			m_bmodify = false;
			m_avant1.setVisible(false);
			m_avant2.setVisible(false);
			m_avant3.setVisible(false);
			m_avant4.setVisible(false);
			m_avant5.setVisible(false);
			m_add.setText(Program.getLabel("Infos071"));
		}	else if(m_lv.getListSize() == 0) {
			reInitAddVin();
		}
		
		Program.TABBED_PANE.setTitleAt(Program.TABBED_PANE.getSelectedIndex(), Program.getLabel("Infos005"));
		Debug("Do After Run Done");
	}

	/**
	 * 
	 */
	private void reInitAddVin() {
		m_bmulti = false;
		if(m_lv != null) {
			remove(m_lv);
			m_lv = null;
		}
		enableAll(true);
		m_bmodify = false;
		m_avant1.setVisible(false);
		m_avant2.setVisible(false);
		m_avant3.setVisible(false);
		m_avant4.setVisible(false);
		m_avant5.setVisible(false);
		m_add.setText(Program.getLabel("Infos071"));
	}

	private boolean runExit() {
		Debug("runExit...");
		m_add.setEnabled(false);
		//Vérification qu'il n'y a pas de bouteilles en modif ou création
		if (!name.getEditor().getItem().toString().trim().isEmpty()) {
			String erreur_txt1;
			if (!m_bmodify) {
				erreur_txt1 = Program.getError("Error144");
			}
			else if (name.isEnabled()) {
				erreur_txt1 = Program.getError("Error148");
			}
			else {
				erreur_txt1 = Program.getError("Error149");
			}
			Debug("Message: Confirm to Quit?");
			if( JOptionPane.NO_OPTION == JOptionPane.showConfirmDialog(this, erreur_txt1 + " " + Program.getError("Error145"), Program.getLabel("Infos049"), JOptionPane.YES_NO_OPTION)) {
				Debug("Don't Quit.");
				m_add.setEnabled(true);
				return false;
			}
		}

		Debug("Quitting...");

		RangementUtils.putTabStock();
		m_colorList.setSelectedItem(BottleColor.NONE);
		m_avant1.setVisible(false);
		m_avant2.setVisible(false);
		m_avant3.setVisible(false);
		m_avant4.setVisible(false);
		m_avant5.setVisible(false);
		name.setSelectedIndex(0);
		m_year.setText("");
		m_parker.setText("");
		m_price.setText("");
		m_maturity.setText("");
		m_lieu.setSelectedIndex(0);
		m_labelExist.setText("");
		m_nb_bottle.setValue(1);
		panelVignobles.resetCountrySelected();
		reInitAddVin();
		Debug("runExit... Done");
		return true;
	}

	void reInit() {

		Debug("ReInit...");
		m_laBouteille = null;
		listBottleInModification = null;
		reInitAddVin();
		Debug("ReInit... Done");
	}

  @Override
  public void cut() {
		String text = name.getEditor().getItem().toString();
		if(text != null) {
			Program.CLIPBOARD.copier(text);
			name.getEditor().setItem("");
		}
  }

  @Override
  public void copy() {
		String text = name.getEditor().getItem().toString();
		if(text != null) {
			Program.CLIPBOARD.copier(text);
		}
  }

  @Override
  public void paste() {
		String text = Program.CLIPBOARD.coller();
		if(text != null && !text.isEmpty()) {
			name.getEditor().setItem(text);
		}
  }

	/**
	 * Debug
	 *
	 * @param sText String
	 */
	protected static void Debug(String sText) {
		Program.Debug("AddVin: " + sText );
	}

	private class PanelPlace extends JPanel{
		private static final long serialVersionUID = -2601861017578176513L;

		private PanelPlace(){
			setLayout(new MigLayout("","[]30px[]30px[]30px[]30px[grow]30px[]",""));
			setBorder(BorderFactory.createTitledBorder(new EtchedBorder(EtchedBorder.LOWERED), Program.getLabel("Infos217")));
			add(m_labelPlace,"");
			add(m_labelNumPlace,"");
			add(m_labelLine,"");
			add(m_labelColumn,"wrap");
			add(m_lieu,"");
			add(m_num_lieu,"");
			add(m_line,"");
			add(m_column,"");
			add(m_labelExist,"hidemode 3");
			add(m_chooseCell,"alignx right");
			add(m_preview,"alignx right, wrap");
			add(m_avant1,"hidemode 3,split 2");
			add(m_avant2,"hidemode 3");
			add(m_avant3,"hidemode 3");
			add(m_avant4,"hidemode 3");
			add(m_avant5,"hidemode 3");
		}
	}

	private class PanelMain extends JPanel{
		private static final long serialVersionUID = -4824541234206895953L;

		private PanelMain(){
			setLayout(new MigLayout("","grow","[][][]10px[][grow]10px[][]"));
			add(new PanelName(),"growx,wrap");
			add(new PanelPlace(),"growx,wrap");
			add(new PanelAttribute(),"growx,split 2");
			add(panelVignobles = new PanelVignobles(false, true),"growx, wrap");
			add(m_labelComment,"growx, wrap");
			add(m_js_comment,"grow, wrap");
			add(m_end, "center, hidemode 3, wrap");
			add(m_add, "center, split 2");
			add(m_cancel);
		}
	}
	

	@Override
	public boolean tabWillClose(TabEvent event) {
		return Program.addWine.runExit();
	}

	@Override
	public void tabClosed() {
		Start.getInstance().updateMainPanel();
	}
	
	private void managePlaceCombos() {
		m_lieu.setEnabled(true);
		if(m_lieu.getItemCount() == 2) {
			if(m_lieu.getSelectedIndex() == 0) {
				m_lieu.setSelectedIndex(1);
			}
			m_lieu.setEnabled(false);
			Rangement r = Program.getCave(0);
			if(m_num_lieu.getItemCount() == 2) {
				if(m_num_lieu.getSelectedIndex() == 0) {
					m_num_lieu.setSelectedIndex(1);
				}
				m_num_lieu.setEnabled(false);
			}
			setLineColumnVisible(r);
		}
		else {
			m_lieu.setEnabled(true);
			m_num_lieu.setEnabled(false);
			m_line.setVisible(false);
			m_column.setVisible(false);
			m_labelLine.setVisible(false);
			m_labelColumn.setVisible(false);
			if(m_lieu.getSelectedIndex() > 0) {
				m_num_lieu.setEnabled(true);
				Rangement r = Program.getCave(m_lieu.getSelectedIndex() - 1);
				if(m_num_lieu.getItemCount() == 2) {
					if(m_num_lieu.getSelectedIndex() == 0) {
						m_num_lieu.setSelectedIndex(1);
					}
					m_num_lieu.setEnabled(false);
				}
				setLineColumnVisible(r);
			}
		}
	}

	private void setLineColumnVisible(Rangement r) {
		if (r == null) {
			return;
		}
		boolean visible = !r.isCaisse();
		m_line.setVisible(visible);
		m_column.setVisible(visible);
		m_labelLine.setVisible(visible);
		m_labelColumn.setVisible(visible);
	}

	class AddAction extends AbstractAction {

		private static final long serialVersionUID = -2958181161054647775L;
		private AddAction() {
			super(Program.getLabel("Infos071"), MyCellarImage.ADD);
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				new Thread(instance).start();
			}
			catch (Exception a) {
				Program.showException(a);
			}
		}
	}
	
	class CancelAction extends AbstractAction {

		private static final long serialVersionUID = -8689301287853923641L;
		private CancelAction() {
			super(Program.getLabel("Infos055"), MyCellarImage.DELETE);
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			runExit();
			Start.getInstance().removeCurrentTab();
		}
	}
}
