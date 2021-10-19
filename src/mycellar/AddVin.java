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
import mycellar.core.PopupListener;
import mycellar.core.TabEvent;
import mycellar.core.datas.history.HistoryState;
import mycellar.core.datas.jaxb.VignobleJaxb;
import mycellar.general.ProgramPanels;
import mycellar.placesmanagement.Place;
import mycellar.placesmanagement.Rangement;
import mycellar.placesmanagement.RangementUtils;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.text.MessageFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static mycellar.MyCellarUtils.nonNullValue;
import static mycellar.core.LabelProperty.A_SINGLE;
import static mycellar.core.LabelProperty.PLURAL;


/**
 * <p>Titre : Cave &agrave; vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2005</p>
 * <p>Soci&eacute;t&eacute; : Seb Informatique</p>
 *
 * @author S&eacute;bastien Duch&eacute;
 * @version 28.7
 * @since 19/10/21
 */
public final class AddVin extends MyCellarManageBottles implements Runnable, ITabListener, ICutCopyPastable, IMyCellar, IUpdatable {

  private static final long serialVersionUID = -8925831759212999905L;
  private final AddVin instance;
  private boolean isModify = false; // Pour la Modification
  private int nbNumForModif, nbLineForModif, nbColumnForModif; //Pour la Modification
  private Rangement rangementInModif;
  private ListVin listVin;
  private LinkedList<MyCellarObject> listBottleInModification; //Pour enlever dans ListVin
  private int nb_bottle_add_only_one_place = 0;

  /**
   * Constructeur pour l'ajout de vins
   */
  public AddVin() {
    super();
    instance = this;
    Debug("Constructor");
    bottle = null;
    panelGeneral.setMyCellarObject(null);
    addButton = new MyCellarButton(LabelType.INFO, "071", new AddAction());
    cancelButton = new MyCellarButton(LabelType.INFO, "055", new CancelAction());

    panelPlace.setModifyActive(false);
    panelGeneral.setModifyActive(false);
    panelWineAttribute.setModifyActive();
    commentTextArea.setModifyActive(false);
    addButton.setMnemonic(ajouterChar);
    panelGeneral.initValues();
    panelWineAttribute.initValues();

    // Init des valeurs pour modification
    nbNumForModif = nbLineForModif = nbColumnForModif = -1;

    addButton.setText(Program.getLabel("Infos071"));

    PopupListener popup_l = new PopupListener();
    panelGeneral.setMouseListener(popup_l);
    panelWineAttribute.setMouseListener(popup_l);
    commentTextArea.addMouseListener(popup_l);

    end.setForeground(Color.red);
    end.setHorizontalAlignment(SwingConstants.CENTER);
    setLayout(new BorderLayout());
    add(new PanelMain(true), BorderLayout.CENTER);

    setVisible(true);
    Debug("Constructor End");
  }

  protected static void Debug(String sText) {
    Program.Debug("AddVin: " + sText);
  }

  private void resetValues() {
    Debug("Reset Values...");
    panelGeneral.resetValues();
    panelWineAttribute.resetValues();

    commentTextArea.setText("");

    ProgramPanels.getSearch().ifPresent(Search::updateTable);
    panelVignobles.resetCombos();
    panelPlace.resetValues();
    rangementInModif = null;
    Debug("Reset Values... End");
  }

  /**
   * Fonction de chargement de plusieurs vins pour la classe ListVin
   *
   * @param myCellarObjects LinkedList<MyCellarObject>
   */
  public void setBottles(List<MyCellarObject> myCellarObjects) {
    Debug("Set Bottles...");
    if (listVin == null) {
      listVin = new ListVin(myCellarObjects, this);
      add(listVin, BorderLayout.WEST);
    } else {
      listVin.setBottles(myCellarObjects);
    }

    setBottle(myCellarObjects.get(0));
  }

  /**
   * Fonction de chargement d'un vin pour la classe ListVin
   *
   * @param cellarObject MyCellarObject
   */
  private void setBottle(MyCellarObject cellarObject) {
    Debug("Set Bottle...");
    try {
      bottle = cellarObject;
      panelGeneral.setSeveralItems(false);
      panelGeneral.setMyCellarObject(cellarObject);
      listBottleInModification = new LinkedList<>();
      listBottleInModification.add(bottle);
      isModify = true;
      initializeExtraProperties();
      panelWineAttribute.setStatus(cellarObject);
      if (Program.isWineType()) {
        panelVignobles.initializeVignobles((Bouteille) bottle);
      }

      panelPlace.resetValues();
      panelPlace.setBeforeBottle(bottle);
      addButton.setText(Program.getLabel("Infos079"));
      rangementInModif = cellarObject.getRangement();
      nbNumForModif = cellarObject.getNumLieu();
      nbLineForModif = cellarObject.getLigne();
      nbColumnForModif = cellarObject.getColonne();
      end.setText(Program.getLabel("Infos092")); //"Saisir les modifications");
    } catch (RuntimeException e) {
      Program.showException(e);
    }
    Debug("Set Bottle... End");
  }

  /**
   * Fonction pour le chargement de vins pour la classe ListVin.
   */
  void setBottlesInModification(LinkedList<MyCellarObject> myCellarObjects) {
    Debug("setBottlesInModification...");
    try {
      severalItems = myCellarObjects.size() > 1;
      panelGeneral.setSeveralItems(severalItems);
      listBottleInModification = myCellarObjects;

      resetValues();
      if (severalItems) {
        panelGeneral.setViewToSeveralItemsMode(listBottleInModification.size());
        panelWineAttribute.seNbItemsEnabled(false);
        addButton.setEnabled(true);
        end.setText(Program.getLabel("AddVin.moveError", LabelProperty.PLURAL)); //"Vous ne pouvez deplacer plusieurs bouteilles que dans une caisse");
      } else {
        setBottle(listBottleInModification.getFirst());
      }
    } catch (RuntimeException e) {
      Program.showException(e);
    }
    Debug("setBottlesInModification... End");
  }

  private boolean controlBottle() {
    Debug("Control Bottle...");
    boolean resul = panelGeneral.performValidation();
    resul &= panelPlace.performValidation(isModify);
    if (!resul) {
      end.setText("");
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
        end.setText("");
        enableAll(true);
        return;
      }
      // Ajout ou modification
      Debug("Adding / Modifying...");
      int nb_bottle_rest = panelWineAttribute.getNbItems() - 1;
      String prix = panelWineAttribute.getPrice();
      String comment = commentTextArea.getText();
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

      if (isModify) {
        //On grise les champs en cours de modif
        Debug("Modifying in Progress...");
        end.setText(Program.getLabel("Infos142")); //"Modification en cours..."
        enableAll(false);
      }

      Place place = panelPlace.getSelectedPlace();
      Rangement rangement = place.getRangement();
      Objects.requireNonNull(rangement);
      if (!place.hasPlace() && isModify) {
        //Si aucun emplacement n'a ete selectionne (modif du nom)
        place = bottle.getPlace();
        if (rangementInModif != null) {
          rangement = rangementInModif;
        }
      }
      boolean bIsCaisse = rangement.isCaisse();

      end.setText(Program.getLabel("Infos312"));
      boolean m_bbottle_add = false;
      boolean resul = true;
      if (!panelPlace.hasSelecedPlace() && isModify) {
        m_bbottle_add = modifyOneOrSeveralBottlesWithoutPlaceModification(prix, comment, status, country, vignoble, aoc, igp, annee, nom, demie);
      } else if (bIsCaisse) {
        //Caisse
        Debug("Is a Caisse");
        if (!rangement.hasFreeSpaceInCaisse(place)) {
          Erreur.showSimpleErreur(Program.getError("Error154"), Program.getError("Error153"));
          end.setText("");
          return;
        }

        if (!severalItems) {
          MyCellarObject newMyCellarObject = null;
          if (Program.isWineType()) {
            newMyCellarObject = createBouteille(nom, annee, demie, place, rangement, -1, -1, -1, prix, comment, dateOfC, parker, color, status, country, vignoble, aoc, igp);
          } else if (Program.isMusicType()) {
            newMyCellarObject = createMusic(nom, annee, demie, place, rangement, -1, -1, -1, prix, comment, status);
          } else {
            Program.throwNotImplemented();
          }
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
                  end.setText("");
                } else {
                  for (int j = 0; j <= nb_bottle_rest; j++) {
                    MyCellarObject copy = null;
                    if (Program.isWineType()) {
                      copy = new Bouteille().castCopy(newMyCellarObject);
                    } else if (Program.isMusicType()) {
                      copy = new Music().castCopy(newMyCellarObject);
                    } else {
                      Program.throwNotImplemented();
                    }
                    Program.getStorage().addHistory(HistoryState.ADD, copy);
                    rangement.addWine(copy);
                  }
                  end.setText(MessageFormat.format(Program.getLabel("AddVin.NItemAdded", LabelProperty.PLURAL), (nb_bottle_rest + 1)), true);
                  resetValues();
                }
              } else {
                Debug("Adding multiple bottles in the same place: NO");
                //Add a single bottle in Caisse
                Program.getStorage().addHistory(HistoryState.ADD, newMyCellarObject);
                rangement.addWine(newMyCellarObject);
                end.setText(Program.getLabel("AddVin.1ItemAdded", LabelProperty.SINGLE), true);
                panelWineAttribute.setStillNbItems(nb_bottle_rest);
              }
            } else { //Un seul rangement simple
              if (rangement.isLimited() && (rangement.getNbCaseUse(place) + nb_bottle_rest + 1) > rangement.getNbColonnesStock()) {
                resul = false;
                Debug("ERROR: This caisse is full. Unable to add all bottles in the same place!");
                Erreur.showSimpleErreur(Program.getError("Error154"), Program.getError("Error153"));
                end.setText("");
              } else {
                nb_bottle_add_only_one_place = nb_bottle_rest + 1;
                for (int z = 0; z < nb_bottle_rest; z++) {
                  MyCellarObject copy = null;
                  if (Program.isWineType()) {
                    copy = new Bouteille().castCopy(newMyCellarObject);
                  } else if (Program.isMusicType()) {
                    copy = new Music().castCopy(newMyCellarObject);
                  } else {
                    Program.throwNotImplemented();
                  }
                  Program.getStorage().addHistory(HistoryState.ADD, copy);
                  rangement.addWine(copy);
                }
                nb_bottle_rest = 0;
              }
            }
          } // Fin de l'ajout de plusieurs bouteilles restantes

          if (nb_bottle_rest == 0) {
            if (isModify) {
              //Suppression de la bouteille lors de la modification
              Debug("Updating bottle when modifying");
              bottle.getRangement().clearStock(bottle, bottle.getPlace());
              bottle.update(newMyCellarObject);
              Program.getStorage().addHistory(HistoryState.MODIFY, bottle);
              m_bbottle_add = true;
            } else {
              //Ajout de la bouteille
              Debug("Adding bottle...");
              Program.getStorage().addHistory(HistoryState.ADD, newMyCellarObject);
              m_bbottle_add = rangement.addWine(newMyCellarObject);
            }

            if (m_bbottle_add) {
              resetValues();
            } else {
              Debug("ERROR: Adding bottle: Storage full");
              Erreur.showSimpleErreur(MessageFormat.format(Program.getError("Error151"), rangement.getNom()), Program.getError("Error153"));
              resul = false;
            }
          }
        } else { //if(! m_bmulti) Modification de plusieurs vins vers une caisse
          //Recuperation des differentes bouteilles
          Debug("Modifying multiple bottles to a Simple place");
          if (!place.hasPlace()) {
            Debug("Modifying without changing place");
            boolean bOneBottle = listBottleInModification.size() == 1;
            // Modification sans changement de lieu 10/05/08
            for (MyCellarObject tmp : listBottleInModification) {
              updateMyCellarObject(prix, comment, dateOfC, parker, color, status, country, vignoble, aoc, igp, demie, bOneBottle, tmp);

              tmp.updateStatus();

              if (isModify) {
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
            if (isModify) {
              resetValues();
            }
          } else {
            Debug("Modifying with changing place");
            int nbbottle = listBottleInModification.size();
            if (rangement.isLimited() && (rangement.getNbCaseUse(place) + nbbottle) > rangement.getNbColonnesStock()) {
              Debug("ERROR: Not enough place!");
              Erreur.showSimpleErreur(Program.getError("Error154"), Program.getError("Error153"));
              panelPlace.enableSimplePlace(true);
              addButton.setEnabled(true);
              end.setText("");
            } else {
              boolean bOneBottle = listBottleInModification.size() == 1;
              for (MyCellarObject tmp : listBottleInModification) {
                updateMyCellarObject(prix, comment, dateOfC, parker, color, status, country, vignoble, aoc, igp, demie, bOneBottle, tmp);
                Debug("Adding multiple bottles in simple place...");
                if (isModify && tmp.isInExistingPlace()) {
                  Debug("Delete from stock");
                  tmp.getRangement().clearStock(tmp, tmp.getPlace());
                }
                //Ajout des bouteilles dans la caisse
                tmp.setEmplacement(rangement.getNom());
                tmp.setNumLieu(place.getPlaceNum());
                tmp.setLigne(0);
                tmp.setColonne(0);
                tmp.updateStatus();
                tmp.getRangement().updateToStock(tmp);
                Debug("Bottle updated.");
                Program.getStorage().addHistory(isModify ? HistoryState.MODIFY : HistoryState.ADD, tmp);
                if (isModify) {
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
      } else {
        // Ajout dans une Armoire
        if (severalItems) { //On ne peut pas deplacer plusieurs bouteilles vers une armoire
          Debug("ERROR: Unable to move multiple bottles to a Complex place");
          end.setText("");
          String nomRangement = rangement.getNom();
          Erreur.showSimpleErreur(MessageFormat.format(Program.getError("Error104", PLURAL), nomRangement), Program.getError("Error105")); //"Veuillez selectionner un rangement de type caisse.");//Impossible de deplacer plusieurs bouteilles dans
          enableAll(true);
        } else {
          // Ajout d'une bouteille dans l'armoire
          int lieu_num_selected = place.getPlaceNum();
          int ligne = place.getLine();
          int colonne = place.getColumn();

          int nb_free_space = 0;
          Optional<MyCellarObject> bouteilleOptional = Optional.empty();
          if (isModify && !panelPlace.isPlaceModified()) { //Si aucune modification du Lieu
            lieu_num_selected = nbNumForModif;
            ligne = nbLineForModif;
            colonne = nbColumnForModif;
          } else { //Si Ajout bouteille ou modification du lieu
            Debug("Adding bottle or modifying place");
            bouteilleOptional = rangement.getBouteille(lieu_num_selected - 1, ligne - 1, colonne - 1);
            if (bouteilleOptional.isEmpty()) {
              nb_free_space = rangement.getNbCaseFreeCoteLigne(lieu_num_selected - 1, ligne - 1, colonne - 1);
            }
          }
          //Creation de la nouvelle bouteille
          Debug("Creating new bottle...");
//          Bouteille newMyCellarObject = new BouteilleBuilder(nom)
//              .annee(annee)
//              .type(demie)
//              .place(rangement.getNom())
//              .numPlace(lieu_num_selected)
//              .line(ligne)
//              .column(colonne)
//              .price(prix)
//              .comment(comment1)
//              .maturity(dateOfC)
//              .parker(parker)
//              .color(color)
//              .status(status)
//              .vignoble(country, vignoble, aoc, igp).build();
          MyCellarObject newMyCellarObject = null;
          if (Program.isWineType()) {
            newMyCellarObject = createBouteille(nom, annee, demie, null, rangement, lieu_num_selected, ligne, colonne, prix, comment, dateOfC, parker, color, status, country, vignoble, aoc, igp);
          } else if (Program.isMusicType()) {
            newMyCellarObject = createMusic(nom, annee, demie, null, rangement, lieu_num_selected, ligne, colonne, prix, comment, status);
          } else {
            Program.throwNotImplemented();
          }
          if (bouteilleOptional.isEmpty()) {
            //Case vide donc ajout
            if (isModify) {
              Debug("Empty case: Modifying bottle");
              final Place oldPLace = bottle.getPlace();
              bottle.update(newMyCellarObject);
              newMyCellarObject.getRangement().updateToStock(newMyCellarObject);
              Program.getStorage().addHistory(HistoryState.MODIFY, bottle);
              if (!rangement.isCaisse()) {
                Debug("Deleting from older complex place");
                oldPLace.getRangement().clearComplexStock(oldPLace);
              }
            } else {
              Debug("Empty case: Adding bottle");
              Program.getStorage().addHistory(HistoryState.ADD, newMyCellarObject);
              rangement.addWine(newMyCellarObject);
              if (nb_bottle_rest > 0 && nb_free_space > 1) { //Ajout de bouteilles cote a cote
                if (nb_free_space > (nb_bottle_rest + 1)) {
                  nb_free_space = nb_bottle_rest + 1;
                }
                if (JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(Start.getInstance(), MessageFormat.format(Program.getError("Error175", PLURAL), nb_free_space), Program.getLabel("Infos049"), JOptionPane.YES_NO_OPTION)) {
                  Debug("Putting multiple bottle in chosen place");
                  nb_bottle_add_only_one_place = nb_free_space;
                  nb_bottle_rest = nb_bottle_rest - nb_free_space + 1;
                  for (int z = 1; z < nb_free_space; z++) {
//                    newMyCellarObject = new BouteilleBuilder(nom)
//                        .annee(annee)
//                        .type(demie)
//                        .place(rangement.getNom())
//                        .numPlace(lieu_num_selected)
//                        .line(ligne)
//                        .column(colonne + z)
//                        .price(prix)
//                        .comment(comment1)
//                        .maturity(dateOfC)
//                        .parker(parker)
//                        .color(color)
//                        .status(status)
//                        .vignoble(country, vignoble, aoc, igp).build();
                    if (Program.isWineType()) {
                      newMyCellarObject = createBouteille(nom, annee, demie, null, rangement, lieu_num_selected, ligne, colonne + z, prix, comment, dateOfC, parker, color, status, country, vignoble, aoc, igp);
                    } else if (Program.isMusicType()) {
                      newMyCellarObject = createMusic(nom, annee, demie, null, rangement, lieu_num_selected, ligne, colonne + z, prix, comment, status);
                    } else {
                      Program.throwNotImplemented();
                    }
                    Program.getStorage().addHistory(HistoryState.ADD, newMyCellarObject);
                    rangement.addWine(newMyCellarObject);
                  }
                }
              }
            }

            if (nb_bottle_rest > 0) {
              panelWineAttribute.setStillNbItems(nb_bottle_rest);
              panelPlace.resetValues();
            } else {
              resetValues();
              if (isModify) {
                panelGeneral.setEditable(false);
                panelWineAttribute.setEditable(false);
                commentTextArea.setEditable(false);
                addButton.setEnabled(false);
                panelPlace.enableAll(false);
              }
            }
            if (isModify) {
              panelPlace.enablePlace(true);
            }
            m_bbottle_add = true;
          } else { // La case n'est pas vide
            Debug("WARNING: Not an empty place, Replace?");
            final MyCellarObject myCellarObject = bouteilleOptional.get();
            String erreur_txt1 = MessageFormat.format(Program.getError("Error059"), myCellarObject.getNom(), myCellarObject.getAnnee()); // deja present a cette place
            String erreur_txt2 = Program.getError("Error060"); //"Voulez vous le remplacer?");
            if (JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(Start.getInstance(), erreur_txt1 + "\n" + erreur_txt2, Program.getLabel("Infos049"), JOptionPane.YES_NO_OPTION)) {
              replaceWine(newMyCellarObject, isModify, myCellarObject);
              if (isModify) {
                bottle.update(newMyCellarObject);
              }
              end.setText(isModify ? Program.getLabel("AddVin.1ItemModified", LabelProperty.SINGLE) : Program.getLabel("AddVin.1ItemAdded", LabelProperty.SINGLE), true);
              resetValues();
            } else {
              end.setText(Program.getLabel("AddVin.NotSaved", LabelProperty.THE_SINGLE));
              enableAll(true);
              resul = false;
            }
          }
        }
      }

      if (m_bbottle_add) {
        if (isModify) {
          if (listVin != null) {
            listVin.updateList(listBottleInModification);
          }
          if (listBottleInModification.size() == 1) {
            end.setText(Program.getLabel("AddVin.1ItemModified", LabelProperty.SINGLE), true); //"1 bouteille modifiee");
          } else {
            end.setText(MessageFormat.format(Program.getLabel("AddVin.NItemModified", LabelProperty.PLURAL), listBottleInModification.size())); //" bouteilles modifiees");
          }
        } else {
          if (nb_bottle_add_only_one_place == 0) {
            end.setText(Program.getLabel("AddVin.1ItemAdded", LabelProperty.SINGLE), true); //"1 bouteille ajoutee");
          } else {
            end.setText(MessageFormat.format(Program.getLabel("AddVin.NItemAdded", LabelProperty.PLURAL), nb_bottle_add_only_one_place)); //"x bouteilles ajoutees");
            nb_bottle_add_only_one_place = 0;
          }
          panelGeneral.setTypeDefault();
        }
      }
      if (resul) {
        doAfterRun();
      }
    } catch (MyCellarException e) {
      Program.showException(e);
    }
  }

  private Bouteille createBouteille(String nom, String annee, String demie, Place place, Rangement rangement, int numLieu, int line, int colonne, String prix, String comment, String dateOfC, String parker, String color, String status, String country, String vignoble, String aoc, String igp) {
    BouteilleBuilder bouteilleBuilder = new BouteilleBuilder(nom)
        .annee(annee)
        .type(demie)
        .place(rangement.getNom())
        .numPlace(place != null ? place.getPlaceNum() : numLieu)
        .price(prix)
        .comment(comment)
        .maturity(dateOfC)
        .parker(parker)
        .color(color)
        .status(status)
        .vignoble(country, vignoble, aoc, igp);
    if (line != -1) {
      bouteilleBuilder.line(line);
    }
    if (colonne != -1) {
      bouteilleBuilder.column(colonne);
    }
    return bouteilleBuilder.build();
  }

  private Music createMusic(String nom, String annee, String demie, Place place, Rangement rangement, int numLieu, int line, int colonne, String prix, String comment, String status) {
    Music.MusicBuilder musicBuilder = new Music.MusicBuilder(nom)
        .annee(annee)
//        .type(demie)
        .place(rangement.getNom())
        .numPlace(place != null ? place.getPlaceNum() : numLieu)
        .price(prix)
        .comment(comment)
//        .maturity(dateOfC)
//        .parker(parker)
//        .color(color)
        .status(status);
//        .vignoble(country, vignoble, aoc, igp);
    if (line != -1) {
      musicBuilder.line(line);
    }
    if (colonne != -1) {
      musicBuilder.column(colonne);
    }
    return musicBuilder.build();
  }

  private void updateMyCellarObject(String prix, String comment, String dateOfC, String parker, String color, String status, String country, String vignoble, String aoc, String igp, String demie, boolean singleObject, MyCellarObject cellarObject) {
    if (Program.isWineType()) {
      Bouteille bouteille = (Bouteille) cellarObject;
      if (singleObject || !comment.isEmpty()) {
        bouteille.setComment(comment);
      }
      if (singleObject || !dateOfC.isEmpty()) {
        bouteille.setMaturity(dateOfC);
      }
      if (singleObject || !parker.isEmpty()) {
        bouteille.setParker(parker);
      }
      if (singleObject || panelWineAttribute.getColorList().isModified()) {
        bouteille.setColor(color);
      }
      if (singleObject || !prix.isEmpty()) {
        bouteille.setPrix(prix);
      }
      if (singleObject || !country.isEmpty() || !vignoble.isEmpty() || !aoc.isEmpty() || !igp.isEmpty()) {
        bouteille.setVignoble(new VignobleJaxb(country, vignoble, aoc, igp));
      }
    } else if (Program.isMusicType()) {
      Music music = (Music) cellarObject;
      if (singleObject || !comment.isEmpty()) {
        music.setComment(comment);
      }
//      if (singleObject || !dateOfC.isEmpty()) {
//        music.setMaturity(dateOfC);
//      }
//      if (singleObject || !parker.isEmpty()) {
//        music.setParker(parker);
//      }
//      if (singleObject || panelWineAttribute.getColorList().isModified()) {
//        music.setColor(color);
//      }
      if (singleObject || !prix.isEmpty()) {
        music.setPrix(prix);
      }
//      if (singleObject || !country.isEmpty() || !vignoble.isEmpty() || !aoc.isEmpty() || !igp.isEmpty()) {
//        music.setVignoble(new VignobleJaxb(country, vignoble, aoc, igp));
//      }
    } else {
      Program.throwNotImplemented();
    }

    if (singleObject || panelWineAttribute.getStatusList().isModified()) {
      cellarObject.setStatus(status);
    }
    if (singleObject || !demie.isEmpty()) {
      cellarObject.setKind(demie);
    }
  }

  private boolean modifyOneOrSeveralBottlesWithoutPlaceModification(String prix,
                                                                    String comment,
                                                                    String status,
                                                                    String country,
                                                                    String vignoble,
                                                                    String aoc,
                                                                    String igp,
                                                                    String annee,
                                                                    String nom,
                                                                    String demie) throws MyCellarException {
    Debug("modifyOneOrSeveralBottlesWithoutPlaceModification...");
    String color = panelWineAttribute.getColorIfModified();
    String maturity = panelWineAttribute.getMaturityIfModified();
    String parker = panelWineAttribute.getParkerIfModified();
    boolean m_bbottle_add = false;
    if (!severalItems) {
      // Modification d'une bouteille dans Armoire sans changement de lieu
      Debug("Modifying one bottle in Armoire without changing place");
      MyCellarObject tmp = null;
      if (Program.isWineType()) {
        tmp = createBouteille(nom, annee, demie, null, bottle.getRangement(), bottle.getNumLieu(), bottle.getLigne(), bottle.getColonne(), prix, comment, maturity, parker, color, status, country, vignoble, aoc, igp);
      } else if (Program.isMusicType()) {
        tmp = createMusic(nom, annee, demie, null, bottle.getRangement(), bottle.getNumLieu(), bottle.getLigne(), bottle.getColonne(), prix, comment, status);
      } else {
        Program.throwNotImplemented();
      }
      Debug("Replacing bottle...");
      bottle.update(tmp);
      // Remplacement de la bouteille
      Program.getStorage().addHistory(HistoryState.MODIFY, tmp);
      m_bbottle_add = true;
      resetValues();
    } else {
      // Modification de bouteilles dans Armoire sans changement de lieu
      Debug("Modifying multiple bottles in Armoire without changing place");
      // Modification sans changement de lieu 11/05/08
      for (MyCellarObject tmp : listBottleInModification) {
        Rangement rangement = tmp.getRangement();
        if (Program.isWineType()) {
          Bouteille bTemp = new Bouteille().cast(tmp);
          bTemp.setPrix(prix);
          bTemp.setComment(comment);
          bTemp.setMaturity(nonNullValue(maturity, bTemp.getMaturity()));
          bTemp.setParker(nonNullValue(parker, bTemp.getParker()));
          bTemp.setColor(nonNullValue(color, bTemp.getColor()));
          if (!country.isEmpty() || !vignoble.isEmpty() || !aoc.isEmpty() || !igp.isEmpty()) {
            bTemp.setVignoble(new VignobleJaxb(country, vignoble, aoc, igp));
          }
        } else if (Program.isMusicType()) {
          Music bTemp = new Music().cast(tmp);
          bTemp.setPrix(prix);
          bTemp.setComment(comment);
          Program.throwNotImplementedIfNotFor(tmp, Music.class);
//          bTemp.setMaturity(dateOfC);
//          bTemp.setParker(parker);
//          bTemp.setColor(color);
//          if (!country.isEmpty() || !vignoble.isEmpty() || !aoc.isEmpty() || !igp.isEmpty()) {
//            bTemp.setVignoble(new VignobleJaxb(country, vignoble, aoc, igp));
//          }
        } else {
          Program.throwNotImplemented();
        }
        tmp.setKind(demie);
        tmp.updateStatus();
        // Add multiple bottles
        Debug("Adding multiple bottles...");
        if (isModify) {
          //Delete Bouteilles
          Debug("Deleting bottles when modifying");
          Program.getStorage().deleteWine(tmp);
          if (!rangement.isCaisse()) { //Si ce n'est pas une caisse on supprime de stockage
            Debug("is Not a Caisse. Delete from stock");
            rangement.clearComplexStock(tmp.getPlace());
          }
        }
        //Ajout des bouteilles dans la caisse
        Debug("Adding bottle...");
        Program.getStorage().addHistory(isModify ? HistoryState.MODIFY : HistoryState.ADD, tmp);
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

  private void replaceWine(final MyCellarObject newBottle, boolean modify, final MyCellarObject objectToDelete) throws MyCellarException {
    Debug("ReplaceWine...");
    //Change wine in a place
    Program.getStorage().addHistory(modify ? HistoryState.MODIFY : HistoryState.ADD, newBottle);
    Place oldPace = null;
    if (modify && bottle != null) {
      oldPace = bottle.getPlace();
    }
    RangementUtils.replaceMyCellarObject(objectToDelete, newBottle, oldPace);
    if (!modify) {
      Program.getStorage().addWine(newBottle);
    } else if (listVin != null) {
      listVin.updateList(listBottleInModification);
      listVin.updateList(List.of(objectToDelete));
    }
    Debug("ReplaceWine... End");
  }

  private void doAfterRun() {
    Debug("Do After Run...");
    bottle = null;
    panelGeneral.setMyCellarObject(null);
    ProgramPanels.updateManagePlacePanel();
    panelVignobles.updateList();
    if (!isModify) {
      return;
    }

    if (listVin == null) {
      enableAll(true);
      isModify = false;
      panelPlace.setBeforeLabelsVisible(false);
      addButton.setText(Program.getLabel("Infos071"));
    } else if (listVin.getListSize() == 0) {
      reInitAddVin();
    }

    ProgramPanels.TABBED_PANE.setTitleAt(ProgramPanels.TABBED_PANE.getSelectedIndex(), Program.getLabel("Main.tabAdd", A_SINGLE));
    Debug("Do After Run... End");
  }

  private void reInitAddVin() {
    severalItems = false;
    if (listVin != null) {
      remove(listVin);
      listVin = null;
    }
    panelGeneral.setSeveralItems(severalItems);
    panelPlace.managePlaceCombos();
    enableAll(true);
    isModify = false;
    panelPlace.setBeforeLabelsVisible(false);
    addButton.setText(Program.getLabel("Infos071"));
  }

  private boolean runExit() {
    Debug("runExit...");
    addButton.setEnabled(false);
    //Verification qu'il n'y a pas de bouteilles en modif ou creation
    if (!panelGeneral.runExit(isModify)) {
      addButton.setEnabled(true);
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

  @Override
  public boolean tabWillClose(TabEvent event) {
    return ProgramPanels.getAddVin().runExit();
  }

  @Override
  public void tabClosed() {
    Start.getInstance().updateMainPanel();
  }

//  private final class PanelMain extends JPanel {
//    private static final long serialVersionUID = -4824541234206895953L;
//
//    private PanelMain() {
//      panelVignobles = new PanelVignobles(false, true, true);
//      setLayout(new MigLayout("", "grow", "[][][]10px[][grow]10px[][]"));
//      add(panelGeneral, "growx, wrap");
//      add(panelPlace, "growx, wrap");
//      add(panelWineAttribute, "growx, split 2");
//      if (Program.isWineType()) {
//        add(panelVignobles, "growx, wrap");
//      } else {
//        add(new JPanel(), "growx, wrap");
//      }
//      add(labelComment, "growx, wrap");
//      add(scrollPaneComment, "grow, wrap");
//      add(end, "center, hidemode 3, wrap");
//      add(addButton, "center, split 2");
//      add(cancelButton);
//    }
//  }

  class AddAction extends AbstractAction {

    private static final long serialVersionUID = -2958181161054647775L;

    private AddAction() {
      super("", MyCellarImage.ADD);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      new Thread(instance).start();
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
