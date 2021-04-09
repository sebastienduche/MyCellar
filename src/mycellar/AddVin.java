package mycellar;

import mycellar.Bouteille.BouteilleBuilder;
import mycellar.actions.OpenShowErrorsAction;
import mycellar.core.ICutCopyPastable;
import mycellar.core.IMyCellar;
import mycellar.core.IUpdatable;
import mycellar.core.LabelProperty;
import mycellar.core.LabelType;
import mycellar.core.MyCellarButton;
import mycellar.core.MyCellarManageBottles;
import mycellar.core.PanelVignobles;
import mycellar.core.PopupListener;
import mycellar.core.datas.MyCellarBottleContenance;
import mycellar.core.datas.history.HistoryState;
import mycellar.core.datas.jaxb.VignobleJaxb;
import mycellar.placesmanagement.Place;
import mycellar.placesmanagement.Rangement;
import mycellar.placesmanagement.RangementUtils;
import net.miginfocom.swing.MigLayout;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.MessageFormat;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;

import static mycellar.core.LabelProperty.A_SINGLE;
import static mycellar.core.LabelProperty.OF_THE_PLURAL;
import static mycellar.core.LabelProperty.OF_THE_SINGLE;
import static mycellar.core.LabelProperty.PLURAL;
import static mycellar.core.LabelProperty.SINGLE;


/**
 * <p>Titre : Cave &agrave; vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2005</p>
 * <p>Soci&eacute;t&eacute; : Seb Informatique</p>
 * @author S&eacute;bastien Duch&eacute;
 * @version 27.6
 * @since 23/03/21
 */
public final class AddVin extends MyCellarManageBottles implements Runnable, ITabListener, ICutCopyPastable, IMyCellar, IUpdatable {

	private static final long serialVersionUID = -8925831759212999905L;
	private boolean m_bmodify = false; // Pour la Modification
	private int m_nb_num, m_nb_lig, m_nb_col; //Pour la Modification
	private Rangement rangementInModif;
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
		bottle = null;
		m_add = new MyCellarButton(LabelType.INFO, "071", new AddAction());
		m_cancel = new MyCellarButton(LabelType.INFO, "055", new CancelAction());

		panelPlace.setModifyActive(false);
		m_year.setModifyActive(false);
		m_half.setModifyActive(false);
		m_price.setModifyActive(false);
		m_maturity.setModifyActive(false);
		m_parker.setModifyActive(false);
		m_colorList.setModifyActive(true);
		statusList.setModifyActive(true);
		m_comment.setModifyActive(false);
		m_add.setMnemonic(ajouterChar);
		try {
			LinkedList<String> list = new LinkedList<>();
			list.add("");
			list.addAll(Program.getStorage().getBottleNames());
			String[] bottlesNames = new String[0];
			bottlesNames = list.toArray(bottlesNames);
			name = new JCompletionComboBox<>(bottlesNames);
			name.setCaseSensitive(false);
			name.setEditable(true);

			m_half.addItem("");
			MyCellarBottleContenance.getList().forEach(m_half::addItem);
			m_half.setSelectedItem(MyCellarBottleContenance.getDefaultValue());

			// Init des valeurs pour modification
			m_nb_num = m_nb_lig = m_nb_col = -1;

			setYearAuto();

			m_nb_bottle.setToolTipText(Program.getLabel("AddVin.NbItemsToAdd", LabelProperty.PLURAL));
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
				if (m_noYear.isSelected()) {
					m_year.setText(Bouteille.NON_VINTAGE);
					m_year.setEditable(false);
				}	else {
					m_year.setText("");
					m_year.setEditable(true);
				}
			});

			setVisible(true);
			Debug("Constructor End");
		} catch (RuntimeException e) {
			Program.showException(e);
		}
	}

	/**
	 * Remise &agrave; z&eacute;ro des champs saisissable
	 */
	private void resetValues() {
		Debug("Reset Values...");
		name.removeAllItems();
		name.addItem("");
		Program.getStorage().getBottleNames().forEach(name::addItem);

		name.setEnabled(true);
		name.setEditable(true);
		if (m_noYear.isSelected()) {
			m_year.setText(Bouteille.NON_VINTAGE);
		} else {
			m_year.setText("");
		}
		m_price.setText("");
		m_comment.setText("");
		m_maturity.setText("");
		m_parker.setText("");
		m_colorList.setSelectedItem(BottleColor.NONE);
		statusList.setSelectedItem(BottlesStatus.NONE);
		lastModified.setText("");
		m_colorList.setModified(false);
		statusList.setModified(false);
		m_nb_bottle.setValue(1);
		m_labelStillToAdd.setText("");

		Program.getSearch().ifPresent(Search::updateTable);
		panelVignobles.resetCombos();
		panelPlace.clear();
		rangementInModif = null;
		Debug("Reset Values... End");
	}

	/**
	 * setBottles: Fonction de chargement de plusieurs vins pour la classe ListVin
	 *
	 * @param bottles LinkedList<Bouteille>
	 */
	public void setBottles(LinkedList<Bouteille> bottles) {
		Debug("Set Bottles...");
		if (m_lv == null) {
			m_lv = new ListVin(bottles, this);
			add(m_lv, BorderLayout.WEST);
		}	else {
			m_lv.setBottles(bottles);
		}

		setBottle(bottles.getFirst());
	}

	/**
	 * setBottle: Fonction de chargement d'un vin pour la classe ListVin
	 *
	 * @param bottle Bouteille
	 */
	private void setBottle(Bouteille bottle) {
		Debug("Set Bottle...");
		try {
			this.bottle = bottle;
			listBottleInModification = new LinkedList<>();
			listBottleInModification.add(bottle);
			m_bmodify = true;
			initializeExtraProperties();
			statusList.setSelectedItem(BottlesStatus.getStatus(bottle.getStatus()));
			lastModified.setText(bottle.getLastModified());
			panelVignobles.initializeVignobles(bottle);

			panelPlace.clear();
			panelPlace.setBeforeBottle(bottle);
			m_add.setText(Program.getLabel("Infos079"));
			rangementInModif = bottle.getRangement();
			m_nb_num = bottle.getNumLieu();
			m_nb_lig = bottle.getLigne();
			m_nb_col = bottle.getColonne();
			m_end.setText(Program.getLabel("Infos092")); //"Saisir les modifications");
		}	catch (RuntimeException e) {
			Program.showException(e);
		}
		Debug("Set Bottle... End");
	}

	/**
	 * setBottlesInModification: Fonction pour le chargement de vins pour la classe ListVin.
	 *
	 * @param bouteilles LinkedList<Bouteille>: Liste des bouteilles
	 */
	void setBottlesInModification(LinkedList<Bouteille> bouteilles) {
		Debug("setBottlesInModification...");
		try {
			m_bmulti = bouteilles.size() > 1;
			listBottleInModification = bouteilles;

			resetValues();
			if (m_bmulti) {
				name.setSelectedItem(MessageFormat.format(Program.getLabel("AddVin.NbItemsSelected", LabelProperty.PLURAL), listBottleInModification.size())); //" bouteilles selectionnees");
				name.setEnabled(false);
				m_annee_auto.setEnabled(false);
				m_noYear.setEnabled(false);
				m_nb_bottle.setEnabled(false);
				m_year.setEditable(false);
				if (m_half.getItemCount() > 0) {
					m_half.setSelectedIndex(0);
				}
				m_add.setEnabled(true);
				m_end.setText(Program.getLabel("AddVin.moveError", LabelProperty.PLURAL)); //"Vous ne pouvez deplacer plusieurs bouteilles que dans une caisse");
			}	else {
				setBottle(listBottleInModification.getFirst());
			}
		}	catch (RuntimeException e) {
			Program.showException(e);
		}
		Debug("setBottlesInModification... End");
	}

	private boolean controlBottle() {
		Debug("Control Bottle...");
		String nom = name.getEditor().getItem().toString();
		if (MyCellarControl.hasInvalidBotteName(nom)) {
			return false;
		}

		// Controle de la date
		if (!m_bmulti && (m_year.isEditable() || !m_noYear.isSelected())) {
			String annee = m_year.getText();

			// Erreur sur la date
			if (MyCellarControl.hasInvalidYear(annee)) {
				m_year.setEditable(true);
				return false;
			}
			annee = getYear();
			m_year.setText(annee);
		}

		final boolean resul = panelPlace.performValidation(m_bmodify);
		if (!resul) {
			m_end.setText("");
		}
		Debug("Control Bottle... End");
		return resul;
	}

	/**
	 * run: Ex&eacute;cution des t&acirc;ches.
	 */
	@Override
	public void run() {
		Debug("Running...");
		try {
			// Check Name / Year / Place / Num Place
			if (!controlBottle()) {
				m_end.setText("");
				enableAll(true);
				return;
			}
			// Ajout ou modification
			Debug("Adding / Modifying...");
			int nb_bottle_rest = Integer.parseInt(m_nb_bottle.getValue().toString()) - 1;
			String prix = m_price.getText();
			String comment1 = m_comment.getText();
			String dateOfC = m_maturity.getText();
			String parker = m_parker.getText();
			BottleColor bottleColor = (BottleColor) m_colorList.getSelectedItem();
			String color = bottleColor != null ? bottleColor.name() : "";
			BottlesStatus bottlesStatus = statusList.isModified() ? (BottlesStatus) statusList.getSelectedItem() : null;
			String status = bottlesStatus != null ? bottlesStatus.name() : "";
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

			Place place = panelPlace.getSelectedPlace();
			Rangement rangement = place.getRangement();
			Objects.requireNonNull(rangement);
			if (!place.hasPlace() && m_bmodify) {
				//Si aucun emplacement n'a ete selectionne (modif du nom)
				place = bottle.getPlace();
				if (rangementInModif != null) {
					rangement = rangementInModif;
				}
			}
			boolean bIsCaisse = rangement.isCaisse();

			m_end.setText(Program.getLabel("Infos312"));
			boolean m_bbottle_add = false;
			boolean resul = true;
			if (!panelPlace.hasSelecedPlace() && m_bmodify) {
				m_bbottle_add = modifyOneOrSeveralBottlesWithoutPlaceModification(prix, comment1, dateOfC, parker, color, status, country, vignoble, aoc, igp, annee, nom, demie);
			} else if (bIsCaisse) {
				//Caisse
				Debug("Is a Caisse");
				if (!rangement.hasFreeSpaceInCaisse(place)) {
					Erreur.showSimpleErreur(Program.getError("Error154"), Program.getError("Error153"));
					m_end.setText("");
					return;
				}

				if (!m_bmulti) {
					Bouteille bouteille = new BouteilleBuilder(nom)
							.annee(annee)
							.type(demie)
							.place(rangement.getNom())
							.numPlace(place.getPlaceNum())
							.price(prix)
							.comment(comment1)
							.maturity(dateOfC)
							.parker(parker)
							.color(color)
							.status(status)
							.vignoble(country, vignoble, aoc, igp).build();
					// Add multiple bottle with question
					if (nb_bottle_rest > 0) {
						if (Program.getCave().size() == 1) {
							Debug("Adding multiple bottles in the same place?");
							String erreur_txt1 = MessageFormat.format(Program.getError("Error061", LabelProperty.PLURAL), (nb_bottle_rest + 1), rangement.getNom()); //Voulez vous ajouter les xx bouteilles dans yy
							if (JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(this, erreur_txt1, Program.getLabel("Infos049"), JOptionPane.YES_NO_OPTION)) {
								//Add several bottles in Caisse
								Debug("Adding multiple bottles in the same place: YES");

								if (rangement.isLimited() && (rangement.getNbCaseUse(place) + nb_bottle_rest) >= rangement.getNbColonnesStock()) {
									Erreur.showSimpleErreur(Program.getError("Error154"), Program.getError("Error153"));
									m_end.setText("");
								} else {
									for (int j = 0; j <= nb_bottle_rest; j++) {
										Bouteille b = new Bouteille(bouteille);
										Program.getStorage().addHistory(HistoryState.ADD, b);
										rangement.addWine(b);
									}
									m_end.setText(MessageFormat.format(Program.getLabel("AddVin.NItemAdded", LabelProperty.PLURAL), (nb_bottle_rest + 1)));
									resetValues();
								}
							} else {
								Debug("Adding multiple bottles in the same place: NO");
								//Add a single bottle in Caisse
								Program.getStorage().addHistory(HistoryState.ADD, bouteille);
								rangement.addWine(bouteille);
								m_end.setText(Program.getLabel("AddVin.1ItemAdded", LabelProperty.SINGLE));
								setStillNbBottle(nb_bottle_rest);
							}
						} else { //Un seul rangement simple
							if (rangement.isLimited() && (rangement.getNbCaseUse(place) + nb_bottle_rest + 1) > rangement.getNbColonnesStock()) {
								resul = false;
								Debug("ERROR: This caisse is full. Unable to add all bottles in the same place!");
								m_bbottle_add = false;
								Erreur.showSimpleErreur(Program.getError("Error154"), Program.getError("Error153"));
								m_end.setText("");
							} else {
								m_nnb_bottle_add_only_one_place = nb_bottle_rest + 1;
								for (int z = 0; z < nb_bottle_rest; z++) {
									Bouteille b = new Bouteille(bouteille);
									Program.getStorage().addHistory(HistoryState.ADD, b);
									rangement.addWine(b);
								}
								nb_bottle_rest = 0;
							}
						}
					} // Fin de l'ajout de plusieurs bouteilles restantes

					if (nb_bottle_rest == 0) {
						boolean addReturn = true;
						if (m_bmodify) {
							//Suppression de la bouteille lors de la modification
							Debug("Updating bottle when modifying");
							bottle.getRangement().clearStock(bottle);
							bottle.update(bouteille);
							Program.getStorage().addHistory(HistoryState.MODIFY, bottle);
						} else {
							//Ajout de la bouteille
							Debug("Adding bottle...");
							Program.getStorage().addHistory(HistoryState.ADD, bouteille);
							addReturn = rangement.addWine(bouteille);
						}

						//Ajout dans ALL
						if (addReturn) {
							m_bbottle_add = true;
							resetValues();
							m_price.setText("");
							m_maturity.setText("");
							m_parker.setText("");
							m_colorList.setSelectedItem(BottleColor.NONE);
							statusList.setSelectedItem(BottlesStatus.NONE);
						} else {
							Debug("ERROR: Adding bottle: Storage full");
							m_bbottle_add = false;
							Erreur.showSimpleErreur(MessageFormat.format(Program.getError("Error151"), rangement.getNom()), Program.getError("Error153"));
							resul = false;
						}
					}
				}	else { //if(! m_bmulti) Multi == true => Modification de plusieurs vins vers une caisse
					//Recuperation des differentes bouteilles
					Debug("Modifying multiple bottles to a Simple place");
					resul = true;
					if (!place.hasPlace()) {
						Debug("Modifying without changing place");
						boolean bOneBottle = listBottleInModification.size() == 1;
						// Modification sans changement de lieu 10/05/08
						for (Bouteille tmp : listBottleInModification) {
							if (bOneBottle || !comment1.isEmpty()) {
								tmp.setComment(comment1);
							}
							if (bOneBottle || !dateOfC.isEmpty()) {
								tmp.setMaturity(dateOfC);
							}
							if (bOneBottle || !parker.isEmpty()) {
								tmp.setParker(parker);
							}
							if (bOneBottle || m_colorList.isModified()) {
								tmp.setColor(color);
							}
							if (bOneBottle || statusList.isModified()) {
								tmp.setStatus(status);
							}
							if (bOneBottle || !demie.isEmpty()) {
								tmp.setType(demie);
							}
							if (bOneBottle || !prix.isEmpty()) {
								tmp.setPrix(prix);
							}
							if (bOneBottle || !country.isEmpty() || !vignoble.isEmpty() || !aoc.isEmpty() || !igp.isEmpty()) {
								tmp.setVignoble(new VignobleJaxb(country, vignoble, aoc, igp));
							}
							tmp.updateStatus();

							if (m_bmodify) {
								Debug("Modifying bottle...");
								Program.getStorage().addHistory(HistoryState.MODIFY, tmp);
								m_bbottle_add = true;
							} else {
								Debug("Adding bottle...");
								Program.getStorage().addHistory(HistoryState.ADD, tmp);
								//Ajout des bouteilles
								if (tmp.getRangement().addWine(tmp)) {
									m_bbottle_add = true;
									resetValues();
									if (m_half.getItemCount() > 1) {
										m_half.setSelectedIndex(1);
									}
									m_labelStillToAdd.setText("");
								}
							}
						}
						if (m_bmodify) {
							resetValues();
							if (m_half.getItemCount() > 1) {
								m_half.setSelectedIndex(1);
							}
						}
					} else {
						Debug("Modifying with changing place");
						int nbbottle = listBottleInModification.size();
						if (rangement.isLimited() && (rangement.getNbCaseUse(place) + nbbottle) > rangement.getNbColonnesStock()) {
							Debug("ERROR: Not enough place!");
							Erreur.showSimpleErreur(Program.getError("Error154"), Program.getError("Error153"));
							panelPlace.enableSimplePlace(true);
							m_add.setEnabled(true);
							m_end.setText("");
						} else {
							boolean bOneBottle = listBottleInModification.size() == 1;
							for (Bouteille tmp : listBottleInModification) {
								if (bOneBottle || !comment1.isEmpty()) {
									tmp.setComment(comment1);
								}
								if (bOneBottle || !dateOfC.isEmpty()) {
									tmp.setMaturity(dateOfC);
								}
								if (bOneBottle || !parker.isEmpty()) {
									tmp.setParker(parker);
								}
								if (bOneBottle || m_colorList.isModified()) {
									tmp.setColor(color);
								}
								if (bOneBottle || statusList.isModified()) {
									tmp.setStatus(status);
								}
								if (bOneBottle || !demie.isEmpty()) {
									tmp.setType(demie);
								}
								if (bOneBottle || !prix.isEmpty()) {
									tmp.setPrix(prix);
								}
								if (bOneBottle || !country.isEmpty() || !vignoble.isEmpty() || !aoc.isEmpty() || !igp.isEmpty()) {
									tmp.setVignoble(new VignobleJaxb(country, vignoble, aoc, igp));
								}
								Debug("Adding multiple bottles in simple place...");
								if (m_bmodify && tmp.isInExistingPlace()) {
									Debug("Delete from stock");
									tmp.getRangement().clearStock(tmp);
								}
								//Ajout des bouteilles dans la caisse
								tmp.setEmplacement(rangement.getNom());
								tmp.setNumLieu(place.getPlaceNum());
								tmp.setLigne(0);
								tmp.setColonne(0);
								tmp.updateStatus();
								tmp.getRangement().updateToStock(tmp);
								Debug("Bottle updated.");
								Program.getStorage().addHistory(m_bmodify ? HistoryState.MODIFY : HistoryState.ADD, tmp);
								if (m_bmodify) {
									m_bbottle_add = true;
									resetValues();
									if (m_half.getItemCount() > 1) {
										m_half.setSelectedIndex(1);
									}
								} else {
									if (rangement.addWine(tmp)) {
										m_bbottle_add = true;
										resetValues();
										if (m_half.getItemCount() > 1) {
											m_half.setSelectedIndex(1);
										}
										m_labelStillToAdd.setText("");
									}
								}
							}
						}
					}
				}
			}	else {
				// Ajout dans une Armoire
				if (m_bmulti) { //On ne peut pas deplacer plusieurs bouteilles vers une armoire
					Debug("ERROR: Unable to move multiple bottles to a Complex place");
					m_end.setText("");
					String nomRangement = rangement.getNom();
					Erreur.showSimpleErreur(MessageFormat.format(Program.getError("Error104", PLURAL), nomRangement), Program.getError("Error105")); //"Veuillez selectionner un rangement de type caisse.");//Impossible de deplacer plusieurs bouteilles dans
					enableAll(true);
				}	else {
					// Ajout d'une bouteille dans l'armoire
					int lieu_num_selected = place.getPlaceNum();
					int ligne = place.getLine();
					int colonne = place.getColumn();

					int nb_free_space = 0;
					Optional<Bouteille> bouteille = Optional.empty();
					if (m_bmodify && !panelPlace.isPlaceModified()) { //Si aucune modification du Lieu
						lieu_num_selected = m_nb_num;
						ligne = m_nb_lig;
						colonne = m_nb_col;
					} else { //Si Ajout bouteille ou modification du lieu
						Debug("Adding bottle or modifying place");
						bouteille = rangement.getBouteille(lieu_num_selected - 1, ligne - 1, colonne - 1);
						if (bouteille.isEmpty()) {
							nb_free_space = rangement.getNbCaseFreeCoteLigne(lieu_num_selected - 1, ligne - 1, colonne - 1);
						}
					}
					//Creation de la nouvelle bouteille
					Debug("Creating new bottle...");
					Bouteille tmp = new BouteilleBuilder(nom)
							.annee(annee)
							.type(demie)
							.place(rangement.getNom())
							.numPlace(lieu_num_selected)
							.line(ligne)
							.column(colonne)
							.price(prix)
							.comment(comment1)
							.maturity(dateOfC)
							.parker(parker)
							.color(color)
							.status(status)
							.vignoble(country, vignoble, aoc, igp).build();
					if (bouteille.isEmpty()) {
						//Case vide donc ajout
						if (m_bmodify) {
							Debug("Empty case: Modifying bottle");
							final Place oldPLace = bottle.getPlace();
							bottle.update(tmp);
							tmp.getRangement().updateToStock(tmp);
							Program.getStorage().addHistory(HistoryState.MODIFY, bottle);
							if (!rangement.isCaisse()) {
								Debug("Deleting from older complex place");
								oldPLace.getRangement().clearComplexStock(oldPLace);
							}
						} else {
							Debug("Empty case: Adding bottle");
							Program.getStorage().addHistory(HistoryState.ADD, tmp);
							rangement.addWine(tmp);
							if (nb_bottle_rest > 0 && nb_free_space > 1) { //Ajout de bouteilles cote a cote
								if (nb_free_space > (nb_bottle_rest + 1)) {
									nb_free_space = nb_bottle_rest + 1;
								}
								if (JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(Start.getInstance(), MessageFormat.format(Program.getError("Error175", PLURAL), nb_free_space), Program.getLabel("Infos049"), JOptionPane.YES_NO_OPTION)) {
									Debug("Putting multiple bottle in chosen place");
									m_nnb_bottle_add_only_one_place = nb_free_space;
									nb_bottle_rest = nb_bottle_rest - nb_free_space + 1;
									for (int z = 1; z < nb_free_space; z++) {
										tmp = new BouteilleBuilder(nom)
												.annee(annee)
												.type(demie)
												.place(rangement.getNom())
												.numPlace(lieu_num_selected)
												.line(ligne)
												.column(colonne + z)
												.price(prix)
												.comment(comment1)
												.maturity(dateOfC)
												.parker(parker)
												.color(color)
												.status(status)
												.vignoble(country, vignoble, aoc, igp).build();
										Program.getStorage().addHistory(HistoryState.ADD, tmp);
										rangement.addWine(tmp);
									}
								}
							}
						}

						if (nb_bottle_rest > 0) {
							setStillNbBottle(nb_bottle_rest);
							panelPlace.clear();
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
								statusList.setEditable(false);
								m_comment.setEditable(false);
								m_add.setEnabled(false);
								panelPlace.enableAll(false);
							} else {
								m_labelStillToAdd.setText("");
								if (m_half.getItemCount() > 0) {
									m_half.setSelectedIndex(0);
								}
							}
						}
						if (m_bmodify) {
							panelPlace.enablePlace(true);
						}
						m_bbottle_add = true;
					}	else { // La case n'est pas vide
						Debug("WARNING: Not an empty place, Replace?");
						final Bouteille bouteille1 = bouteille.get();
						String erreur_txt1 = MessageFormat.format(Program.getError("Error059"), bouteille1.getNom(), bouteille1.getAnnee()); //" deja present a cette place!");
						String erreur_txt2 = Program.getError("Error060"); //"Voulez vous le remplacer?");
						if (JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(Start.getInstance(), erreur_txt1 + "\n" + erreur_txt2, Program.getLabel("Infos049"), JOptionPane.YES_NO_OPTION)) {
							replaceWine(tmp, m_bmodify, bouteille1);
							if (m_bmodify) {
								bottle.update(tmp);
							}
							m_end.setText(m_bmodify ? Program.getLabel("AddVin.1ItemModified", LabelProperty.SINGLE) : Program.getLabel("AddVin.1ItemAdded", LabelProperty.SINGLE));
							resetValues();
						} else {
							m_end.setText(Program.getLabel("AddVin.NotSaved", LabelProperty.THE_SINGLE));
							enableAll(true);
							resul = false;
						}
					}
				}
			}

			if (m_bbottle_add) {
				if (m_bmodify) {
					if (m_lv != null) {
						m_lv.updateList(listBottleInModification);
					}
					if (listBottleInModification.size() == 1) {
						m_end.setText(Program.getLabel("AddVin.1ItemModified", LabelProperty.SINGLE)); //"1 bouteille modifiee");
					} else {
						m_end.setText(MessageFormat.format(Program.getLabel("AddVin.NItemModified", LabelProperty.PLURAL), listBottleInModification.size())); //" bouteilles modifiees");
					}
				}	else {
					if (m_nnb_bottle_add_only_one_place == 0) {
						m_end.setText(Program.getLabel("AddVin.1ItemAdded", LabelProperty.SINGLE)); //"1 bouteille ajoutee");
					}	else {
						m_end.setText(MessageFormat.format(Program.getLabel("AddVin.NItemAdded", LabelProperty.PLURAL), m_nnb_bottle_add_only_one_place)); //"x bouteilles ajoutees");
						m_nnb_bottle_add_only_one_place = 0;
					}
					//Remise des valeurs par defaut
					m_half.setSelectedItem(MyCellarBottleContenance.getDefaultValue());
				}
			}
			if (resul) {
				doAfterRun();
			}
		} catch (RuntimeException e) {
			Program.showException(e);
		}
	}

	private boolean modifyOneOrSeveralBottlesWithoutPlaceModification(String prix,
																																		String comment,
																																		String dateOfC,
																																		String parker,
																																		String color,
																																		String status,
																																		String country,
																																		String vignoble,
																																		String aoc,
																																		String igp,
																																		String annee,
																																		String nom,
																																		String demie) {
		Debug("modifyOneOrSeveralBottlesWithoutPlaceModification...");
		boolean m_bbottle_add = false;
		if (!m_bmulti) {
			// Modification d'une bouteille dans Armoire sans changement de lieu
			Debug("Modifying one bottle in Armoire without changing place");
			Bouteille tmp = new BouteilleBuilder(nom)
					.annee(annee)
					.type(demie)
					.place(bottle.getEmplacement())
					.numPlace(bottle.getNumLieu())
					.line(bottle.getLigne())
					.column(bottle.getColonne())
					.price(prix)
					.comment(comment)
					.maturity(dateOfC)
					.parker(parker)
					.color(color)
					.status(status)
					.vignoble(country, vignoble, aoc, igp).build();
			Debug("Replacing bottle...");
			bottle.update(tmp);
			// Remplacement de la bouteille
			Program.getStorage().addHistory(HistoryState.MODIFY, tmp);
			m_bbottle_add = true;
			resetValues();
			if (m_half.getItemCount() > 0) {
				m_half.setSelectedIndex(0);
			}
			m_labelStillToAdd.setText("");
		}	else {
			// Modification de bouteilles dans Armoire sans changement de lieu
			Debug("Modifying multiple bottles in Armoire without changing place");
			// Modification sans changement de lieu 11/05/08
			for (Bouteille tmp : listBottleInModification) {
				Rangement rangement = tmp.getRangement();
				tmp.setPrix(prix);
				tmp.setComment(comment);
				tmp.setMaturity(dateOfC);
				tmp.setParker(parker);
				tmp.setColor(color);
				tmp.setType(demie);
				tmp.updateStatus();
				if (!country.isEmpty() || !vignoble.isEmpty() || !aoc.isEmpty() || !igp.isEmpty()) {
					tmp.setVignoble(new VignobleJaxb(country, vignoble, aoc, igp));
				}
				// Add multiple bottles
				Debug("Adding multiple bottles...");
				if (m_bmodify) {
					//Delete Bouteilles
					Debug("Deleting bottles when modifying");
					Program.getStorage().deleteWine(tmp);
					if (!rangement.isCaisse()) { //Si ce n'est pas une caisse on supprime de stockage
						Debug("is Not a Caisse. Delete from stock");
						rangement.clearStock(tmp);
					}
				}
				//Ajout des bouteilles dans la caisse
				Debug("Adding bottle...");
				Program.getStorage().addHistory( m_bmodify? HistoryState.MODIFY : HistoryState.ADD, tmp);
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
		Debug("modifyOneOrSeveralBottlesWithoutPlaceModification... End");
		return m_bbottle_add;
	}

	private void setStillNbBottle(int nb_bottle_rest) {
		m_nb_bottle.setValue(nb_bottle_rest);
		m_labelStillToAdd.setText(MessageFormat.format(Program.getLabel("AddVin.stillNtoAdd", new LabelProperty(nb_bottle_rest > 1)), nb_bottle_rest));
	}

	private void replaceWine(final Bouteille newBottle, boolean modify, final Bouteille bToDelete) {
		Debug("replaceWine...");
		//Change wine in a place
		Program.getStorage().addHistory(modify ? HistoryState.MODIFY : HistoryState.ADD, newBottle);
		Program.getStorage().addHistory(HistoryState.DEL, bToDelete);
		Program.getStorage().deleteWine(bToDelete);
		if (!modify) {
			Program.getStorage().addWine(newBottle);
		}	else {
			if (bottle != null) {
				Rangement r = bottle.getRangement();
				if (!r.isCaisse()) {
					r.clearStock(bottle);
				}
			}

			if (m_lv != null) {
				m_lv.updateList(listBottleInModification);
			}
			Program.getSearch().ifPresent(search -> {
				search.removeBottle(bToDelete);
				search.updateTable();
			});
		}
		Rangement r = newBottle.getRangement();
		if (!r.isCaisse()) {
			r.updateToStock(newBottle);
		}
		Debug("replaceWine... End");
	}

	private void doAfterRun() {
		Debug("Do After Run...");
		bottle = null;
		Program.updateManagePlacePanel();
		panelVignobles.updateList();
		new Timer().schedule(
				new TimerTask() {
					@Override
					public void run() {
						SwingUtilities.invokeLater(() -> m_end.setText(""));
					}
				},
				5000
		);
		if (!m_bmodify) {
			return;
		}

		if (m_lv == null) {
			enableAll(true);
			m_bmodify = false;
			panelPlace.setBeforeLabelsVisible(false);
			m_add.setText(Program.getLabel("Infos071"));
		}	else if (m_lv.getListSize() == 0) {
			reInitAddVin();
		}

		Program.TABBED_PANE.setTitleAt(Program.TABBED_PANE.getSelectedIndex(), Program.getLabel("Main.tabAdd", A_SINGLE));
		Debug("Do After Run... End");
	}

	private void reInitAddVin() {
		m_bmulti = false;
		if (m_lv != null) {
			remove(m_lv);
			m_lv = null;
		}
		panelPlace.managePlaceCombos();
		enableAll(true);
		m_bmodify = false;
		panelPlace.setBeforeLabelsVisible(false);
		m_add.setText(Program.getLabel("Infos071"));
	}

	private boolean runExit() {
		Debug("runExit...");
		m_add.setEnabled(false);
		//Verification qu'il n'y a pas de bouteilles en modif ou creation
		if (!name.getText().isEmpty()) {
			String erreur_txt1;
			if (!m_bmodify) {
				erreur_txt1 = Program.getError("Error144", SINGLE.withCapital());
			}	else {
				erreur_txt1 = Program.getError("Error148", name.isEnabled() ? OF_THE_SINGLE : OF_THE_PLURAL);
			}
			Debug("Message: Confirm to Quit?");
			if (JOptionPane.NO_OPTION == JOptionPane.showConfirmDialog(this, erreur_txt1 + " " + Program.getError("Error145"), Program.getLabel("Infos049"), JOptionPane.YES_NO_OPTION)) {
				Debug("Don't Quit.");
				m_add.setEnabled(true);
				return false;
			}
		}

		Debug("Quitting...");

		if (!RangementUtils.putTabStock()) {
			new OpenShowErrorsAction().actionPerformed(null);
		}
		m_colorList.setSelectedItem(BottleColor.NONE);
		statusList.setSelectedItem(BottlesStatus.NONE);
		panelPlace.clear();
		clearValues();
		reInitAddVin();
		Debug("runExit... End");
		return true;
	}

	void reInit() {
		Debug("ReInit...");
		bottle = null;
		listBottleInModification = null;
		reInitAddVin();
		Debug("ReInit... End");
	}

	@Override
	public void cut() {
		String text = name.getEditor().getItem().toString();
		if (text != null) {
			Program.CLIPBOARD.copier(text);
			name.getEditor().setItem("");
		}
	}

	@Override
	public void copy() {
		String text = name.getEditor().getItem().toString();
		if (text != null) {
			Program.CLIPBOARD.copier(text);
		}
	}

	@Override
	public void paste() {
		String text = Program.CLIPBOARD.coller();
		if (text != null && !text.isEmpty()) {
			name.getEditor().setItem(text);
		}
	}

	/**
	 * Debug
	 *
	 * @param sText String
	 */
	protected static void Debug(String sText) {
		Program.Debug("AddVin: " + sText);
	}

	private final class PanelMain extends JPanel{
		private static final long serialVersionUID = -4824541234206895953L;

		private PanelMain() {
			setLayout(new MigLayout("","grow","[][][]10px[][grow]10px[][]"));
			add(new PanelName(), "growx, wrap");
			add(panelPlace, "growx, wrap");
			add(new PanelAttribute(), "growx,split 2");
			add(panelVignobles = new PanelVignobles(false, true, true), "growx, wrap");
			add(m_labelComment, "growx, wrap");
			add(m_js_comment, "grow, wrap");
			add(m_end, "center, hidemode 3, wrap");
			add(m_add, "center, split 2");
			add(m_cancel);
		}
	}


	@Override
	public boolean tabWillClose(TabEvent event) {
		return Program.getAddVin().runExit();
	}

	@Override
	public void tabClosed() {
		Start.getInstance().updateMainPanel();
	}

	class AddAction extends AbstractAction {

		private static final long serialVersionUID = -2958181161054647775L;
		private AddAction() {
			super("", MyCellarImage.ADD);
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
			super("", MyCellarImage.DELETE);
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			runExit();
			Start.getInstance().removeCurrentTab();
		}
	}
}
