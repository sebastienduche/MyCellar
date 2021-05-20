package mycellar;

import mycellar.Bouteille.BouteilleBuilder;
import mycellar.actions.OpenShowErrorsAction;
import mycellar.core.ICutCopyPastable;
import mycellar.core.IMyCellar;
import mycellar.core.IUpdatable;
import mycellar.core.LabelProperty;
import mycellar.core.LabelType;
import mycellar.core.MyCellarButton;
import mycellar.core.MyCellarException;
import mycellar.core.MyCellarManageBottles;
import mycellar.core.MyCellarObject;
import mycellar.core.PanelVignobles;
import mycellar.core.PopupListener;
import mycellar.core.datas.history.HistoryState;
import mycellar.core.datas.jaxb.VignobleJaxb;
import mycellar.general.ProgramPanels;
import mycellar.placesmanagement.Place;
import mycellar.placesmanagement.Rangement;
import mycellar.placesmanagement.RangementUtils;
import net.miginfocom.swing.MigLayout;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.text.MessageFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static mycellar.core.LabelProperty.A_SINGLE;
import static mycellar.core.LabelProperty.PLURAL;


/**
 * <p>Titre : Cave &agrave; vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2005</p>
 * <p>Soci&eacute;t&eacute; : Seb Informatique</p>
 * @author S&eacute;bastien Duch&eacute;
 * @version 28.2
 * @since 20/05/21
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
		panelGeneral.setMyCellarObject(null);
		m_add = new MyCellarButton(LabelType.INFO, "071", new AddAction());
		m_cancel = new MyCellarButton(LabelType.INFO, "055", new CancelAction());

		panelPlace.setModifyActive(false);
		panelGeneral.setModifyActive(false);
		panelWineAttribute.setModifyActive();
		m_comment.setModifyActive(false);
		m_add.setMnemonic(ajouterChar);
		panelGeneral.initValues();
		panelWineAttribute.initValues();

		// Init des valeurs pour modification
		m_nb_num = m_nb_lig = m_nb_col = -1;

		m_add.setText(Program.getLabel("Infos071"));

		PopupListener popup_l = new PopupListener();
		panelGeneral.setMouseListener(popup_l);
		panelWineAttribute.setMouseListener(popup_l);
		m_comment.addMouseListener(popup_l);

		m_end.setForeground(Color.red);
		m_end.setHorizontalAlignment(SwingConstants.CENTER);
		setLayout(new BorderLayout());
		add(new PanelMain(), BorderLayout.CENTER);

		setVisible(true);
		Debug("Constructor End");
	}

	/**
	 * Remise &agrave; z&eacute;ro des champs saisissable
	 */
	private void resetValues() {
		Debug("Reset Values...");
		panelGeneral.resetValues();
		panelWineAttribute.resetValues();

		m_comment.setText("");

		ProgramPanels.getSearch().ifPresent(Search::updateTable);
		panelVignobles.resetCombos();
		panelPlace.resetValues();
		rangementInModif = null;
		Debug("Reset Values... End");
	}

	/**
	 * setBottles: Fonction de chargement de plusieurs vins pour la classe ListVin
	 *
	 * @param myCellarObjects LinkedList<MyCellarObject>
	 */
	public void setBottles(List<MyCellarObject> myCellarObjects) {
		Debug("Set Bottles...");
		if (m_lv == null) {
			m_lv = new ListVin(myCellarObjects, this);
			add(m_lv, BorderLayout.WEST);
		}	else {
			m_lv.setBottles(myCellarObjects);
		}

		setBottle((Bouteille) myCellarObjects.get(0));
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
			panelGeneral.setMulti(false);
			panelGeneral.setMyCellarObject(bottle);
			listBottleInModification = new LinkedList<>();
			listBottleInModification.add(this.bottle);
			m_bmodify = true;
			initializeExtraProperties();
			panelWineAttribute.setStatus(bottle);
			panelVignobles.initializeVignobles(this.bottle);

			panelPlace.resetValues();
			panelPlace.setBeforeBottle(this.bottle);
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
			panelGeneral.setMulti(m_bmulti);
			listBottleInModification = bouteilles;

			resetValues();
			panelGeneral.resetMulti(listBottleInModification.size());
			if (m_bmulti) {
				panelWineAttribute.seNbItemsEnabled(false);
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
		boolean resul = panelGeneral.performValidation();
		resul &= panelPlace.performValidation(m_bmodify);
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
			int nb_bottle_rest = panelWineAttribute.getNbItems() - 1;
			String prix = panelWineAttribute.getPrice();
			String comment1 = m_comment.getText();
			String dateOfC = panelWineAttribute.getMaturity();
			String parker = panelWineAttribute.getParker();
			String color = panelWineAttribute.getColor();
			String status = panelWineAttribute.getStatus();
			String country = panelVignobles.getCountry();
			String vignoble = panelVignobles.getVignoble();
			String aoc = panelVignobles.getAOC();
			String igp = panelVignobles.getIGP();

			String nom = panelGeneral.getObjectName();
			String annee = panelGeneral.updateYear();
			String demie = panelGeneral.getType();

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
							if (JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(Start.getInstance(), erreur_txt1, Program.getLabel("Infos049"), JOptionPane.YES_NO_OPTION)) {
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
									m_end.setText(MessageFormat.format(Program.getLabel("AddVin.NItemAdded", LabelProperty.PLURAL), (nb_bottle_rest + 1)), true);
									resetValues();
								}
							} else {
								Debug("Adding multiple bottles in the same place: NO");
								//Add a single bottle in Caisse
								Program.getStorage().addHistory(HistoryState.ADD, bouteille);
								rangement.addWine(bouteille);
								m_end.setText(Program.getLabel("AddVin.1ItemAdded", LabelProperty.SINGLE), true);
								panelWineAttribute.setStillNbItems(nb_bottle_rest);
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
							if (bOneBottle || panelWineAttribute.getColorList().isModified()) {
								tmp.setColor(color);
							}
							if (bOneBottle || panelWineAttribute.getStatusList().isModified()) {
								tmp.setStatus(status);
							}
							if (bOneBottle || !demie.isEmpty()) {
								tmp.setKind(demie);
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
								}
							}
						}
						if (m_bmodify) {
							resetValues();
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
								if (bOneBottle || panelWineAttribute.getColorList().isModified()) {
									tmp.setColor(color);
								}
								if (bOneBottle || panelWineAttribute.getStatusList().isModified()) {
									tmp.setStatus(status);
								}
								if (bOneBottle || !demie.isEmpty()) {
									tmp.setKind(demie);
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
								} else {
									if (rangement.addWine(tmp)) {
										m_bbottle_add = true;
										resetValues();
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
					Optional<MyCellarObject> bouteille = Optional.empty();
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
							panelWineAttribute.setStillNbItems(nb_bottle_rest);
							panelPlace.resetValues();
						} else {
							resetValues();
							if (m_bmodify) {
								panelGeneral.setEditable(false);
								panelWineAttribute.setEditable(false);
								m_comment.setEditable(false);
								m_add.setEnabled(false);
								panelPlace.enableAll(false);
							}
						}
						if (m_bmodify) {
							panelPlace.enablePlace(true);
						}
						m_bbottle_add = true;
					}	else { // La case n'est pas vide
						Debug("WARNING: Not an empty place, Replace?");
						final MyCellarObject bouteille1 = bouteille.get();
						String erreur_txt1 = MessageFormat.format(Program.getError("Error059"), bouteille1.getNom(), bouteille1.getAnnee()); //" deja present a cette place!");
						String erreur_txt2 = Program.getError("Error060"); //"Voulez vous le remplacer?");
						if (JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(Start.getInstance(), erreur_txt1 + "\n" + erreur_txt2, Program.getLabel("Infos049"), JOptionPane.YES_NO_OPTION)) {
							replaceWine(tmp, m_bmodify, bouteille1);
							if (m_bmodify) {
								bottle.update(tmp);
							}
							m_end.setText(m_bmodify ? Program.getLabel("AddVin.1ItemModified", LabelProperty.SINGLE) : Program.getLabel("AddVin.1ItemAdded", LabelProperty.SINGLE), true);
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
						m_end.setText(Program.getLabel("AddVin.1ItemModified", LabelProperty.SINGLE), true); //"1 bouteille modifiee");
					} else {
						m_end.setText(MessageFormat.format(Program.getLabel("AddVin.NItemModified", LabelProperty.PLURAL), listBottleInModification.size())); //" bouteilles modifiees");
					}
				}	else {
					if (m_nnb_bottle_add_only_one_place == 0) {
						m_end.setText(Program.getLabel("AddVin.1ItemAdded", LabelProperty.SINGLE), true); //"1 bouteille ajoutee");
					}	else {
						m_end.setText(MessageFormat.format(Program.getLabel("AddVin.NItemAdded", LabelProperty.PLURAL), m_nnb_bottle_add_only_one_place)); //"x bouteilles ajoutees");
						m_nnb_bottle_add_only_one_place = 0;
					}
					panelGeneral.setTypeDefault();
				}
			}
			if (resul) {
				doAfterRun();
			}
		} catch (RuntimeException | MyCellarException e) {
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
																																		String demie) throws MyCellarException {
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
				tmp.setKind(demie);
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
				}
			}
		}
		Debug("modifyOneOrSeveralBottlesWithoutPlaceModification... End");
		return m_bbottle_add;
	}

	private void replaceWine(final MyCellarObject newBottle, boolean modify, final MyCellarObject bToDelete) throws MyCellarException {
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
			ProgramPanels.getSearch().ifPresent(search -> {
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
		panelGeneral.setMyCellarObject(null);
		ProgramPanels.updateManagePlacePanel();
		panelVignobles.updateList();
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

		ProgramPanels.TABBED_PANE.setTitleAt(ProgramPanels.TABBED_PANE.getSelectedIndex(), Program.getLabel("Main.tabAdd", A_SINGLE));
		Debug("Do After Run... End");
	}

	private void reInitAddVin() {
		m_bmulti = false;
		if (m_lv != null) {
			remove(m_lv);
			m_lv = null;
		}
		panelGeneral.setMulti(m_bmulti);
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
		if (!panelGeneral.runExit(m_bmodify)) {
			m_add.setEnabled(true);
			return false;
		}

		Debug("Quitting...");

		if (!RangementUtils.putTabStock()) {
			new OpenShowErrorsAction().actionPerformed(null);
		}
		panelWineAttribute.runExit();
		panelPlace.resetValues();
		clearValues();
		reInitAddVin();
		Debug("runExit... End");
		return true;
	}

	void reInit() {
		Debug("ReInit...");
		bottle = null;
		panelGeneral.setMyCellarObject(null);
		listBottleInModification = null;
		reInitAddVin();
		Debug("ReInit... End");
	}

	@Override
	public void cut() {
		panelGeneral.cut();
	}

	@Override
	public void copy() {
		panelGeneral.copy();
	}

	@Override
	public void paste() {
		panelGeneral.paste();
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
			add(panelGeneral, "growx, wrap");
			add(panelPlace, "growx, wrap");
			add(panelWineAttribute, "growx,split 2");
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
		return ProgramPanels.getAddVin().runExit();
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
