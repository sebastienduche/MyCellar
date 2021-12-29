package mycellar;

import mycellar.Bouteille.BouteilleBuilder;
import mycellar.actions.OpenShowErrorsAction;
import mycellar.core.BottlesStatus;
import mycellar.core.ICutCopyPastable;
import mycellar.core.IMyCellar;
import mycellar.core.IUpdatable;
import mycellar.core.LabelProperty;
import mycellar.core.LabelType;
import mycellar.core.MyCellarManageBottles;
import mycellar.core.MyCellarObject;
import mycellar.core.datas.history.HistoryState;
import mycellar.core.datas.jaxb.VignobleJaxb;
import mycellar.core.exceptions.MyCellarException;
import mycellar.core.uicomponents.MyCellarButton;
import mycellar.core.uicomponents.PopupListener;
import mycellar.core.uicomponents.TabEvent;
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

import static mycellar.MyCellarUtils.nonNullValueOrDefault;
import static mycellar.core.LabelProperty.A_SINGLE;
import static mycellar.core.LabelProperty.PLURAL;


/**
 * <p>Titre : Cave &agrave; vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2005</p>
 * <p>Soci&eacute;t&eacute; : Seb Informatique</p>
 *
 * @author S&eacute;bastien Duch&eacute;
 * @version 29.1
 * @since 29/12/21
 */
public final class AddVin extends MyCellarManageBottles implements Runnable, ITabListener, ICutCopyPastable, IMyCellar, IUpdatable {

  private static final long serialVersionUID = -8925831759212999905L;
  private final AddVin instance;
  private boolean isModify = false; // Pour la Modification
  private Rangement rangementInModif;
  private ListVin listVin;
  private LinkedList<MyCellarObject> listBottleInModification; //Pour enlever dans ListVin

  public AddVin() {
    super();
    instance = this;
    Debug("Constructor");
    myCellarObject = null;
    panelGeneral.setMyCellarObject(null);
    addButton = new MyCellarButton(LabelType.INFO, "071", new AddAction());
    cancelButton = new MyCellarButton(LabelType.INFO, "055", new CancelAction());

    panelPlace.setModifyActive(true);
    panelGeneral.setModifyActive(true);
    panelWineAttribute.setModifyActive();
    commentTextArea.setActive(true);
    addButton.setMnemonic(ajouterChar);
    panelGeneral.initValues();
    panelWineAttribute.initValues();

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
    commentTextArea.setModified(false);

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
    myCellarObject = cellarObject;
    panelGeneral.setSeveralItems(false);
    panelGeneral.setMyCellarObject(myCellarObject);
    listBottleInModification = new LinkedList<>();
    listBottleInModification.add(myCellarObject);
    isModify = true;
    initializeExtraProperties();
    panelWineAttribute.setStatus(myCellarObject);
    if (Program.isWineType()) {
      panelVignobles.initializeVignobles((Bouteille) myCellarObject);
    }

    panelPlace.resetValues();
    panelPlace.setBeforeBottle(myCellarObject);
    addButton.setText(Program.getLabel("Infos079"));
    rangementInModif = myCellarObject.getRangement();
    end.setText(Program.getLabel("Infos092")); // Saisir les modifications
    Debug("Set Bottle... End");
  }

  /**
   * Fonction pour le chargement de vins pour la classe ListVin.
   */
  void setBottlesInModification(LinkedList<MyCellarObject> myCellarObjects) {
    Debug("setBottlesInModification...");
    severalItems = myCellarObjects.size() > 1;
    panelGeneral.setSeveralItems(severalItems);
    listBottleInModification = myCellarObjects;

    resetValues();
    if (severalItems) {
      panelGeneral.setViewToSeveralItemsMode(listBottleInModification.size());
      panelWineAttribute.seNbItemsEnabled(false);
      addButton.setEnabled(true);
      end.setText(Program.getLabel("AddVin.moveError", LabelProperty.PLURAL)); //"Vous ne pouvez deplacer plusieurs bouteilles que dans une caisse
    } else {
      setBottle(listBottleInModification.getFirst());
    }
    Debug("setBottlesInModification... End");
  }

  private boolean performValidations() {
    Debug("Perform validations...");
    boolean validations = panelGeneral.performValidation();
    validations &= panelPlace.performValidation(isModify);
    Debug("Perform validations... End");
    return validations;
  }

  @Override
  public void run() {
    Debug("Running...");
    try {
      // Check Name / Year / Place / Num Place
      if (!performValidations()) {
        end.setText("");
        enableAll(true);
        Debug("ERROR: Validations failed");
        return;
      }
      Debug("Adding / Modifying...");

      final String annee = panelGeneral.updateYear(); // Keep it here before it become not editable
      if (isModify) {
        //On grise les champs en cours de modif
        Debug("Modifying in Progress...");
        end.setText(Program.getLabel("Infos142")); //"Modification en cours..."
        enableAll(false);
      }

      int countStillToAdd = panelWineAttribute.getNbItems();
      Place place = panelPlace.getSelectedPlace();
      Rangement rangement = place.getRangement();
      Objects.requireNonNull(rangement);
      if (!place.hasPlace() && isModify) {
        //Si aucun emplacement n'a ete selectionne (modif du nom)
        place = myCellarObject.getPlace();
        if (rangementInModif != null) {
          rangement = rangementInModif;
        }
      }

      end.setText(Program.getLabel("Infos312"));
      boolean objectAdded = false;
      boolean hasNoError = true;
      int nb_bottle_add_only_one_place = 0;
      if (!panelPlace.isPlaceModified() && isModify) {
        objectAdded = modifyOneOrSeveralObjectsWithoutPlaceModification(annee);
      } else if (rangement.isSimplePlace()) {
        //Caisse
        Debug("Is a Caisse");
        if (!rangement.hasFreeSpaceInSimplePlace(place)) {
          Erreur.showSimpleErreur(Program.getError("Error154"), Program.getError("Error153"));
          end.setText("");
          Debug("ERROR: No free spaces");
          return;
        }

        if (!severalItems) {
          MyCellarObject newMyCellarObject = createMyCellarObject(annee, place, rangement, -1, -1, -1);
          // Add multiple bottle with question
          if (countStillToAdd > 1) {
            if (Program.getCave().size() == 1) {
              Debug("Adding multiple objects in the same place?");
              String message = MessageFormat.format(Program.getError("Error061", LabelProperty.PLURAL), countStillToAdd, rangement.getName()); //Voulez vous ajouter les xx bouteilles dans yy
              if (JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(Start.getInstance(), message, Program.getLabel("Infos049"), JOptionPane.YES_NO_OPTION)) {
                //Add several bottles in Caisse
                Debug("Adding multiple objects in the same place: YES");

                if (rangement.isSimplePlaceLimited() && (rangement.getTotalCellUsed(place) + countStillToAdd) > rangement.getNbColumnsStock()) {
                  Erreur.showSimpleErreur(Program.getError("Error154"), Program.getError("Error153"));
                  end.setText("");
                } else {
                  for (int j = 0; j < countStillToAdd; j++) {
                    MyCellarObject copy = createCopy(newMyCellarObject);
                    Program.getStorage().addHistory(HistoryState.ADD, copy);
                    rangement.addObject(copy);
                  }
                  end.setText(MessageFormat.format(Program.getLabel("AddVin.NItemAdded", LabelProperty.PLURAL), countStillToAdd), true);
                  resetValues();
                }
              } else {
                Debug("Adding multiple objects in the same place: NO");
                //Add a single bottle in Caisse
                Program.getStorage().addHistory(HistoryState.ADD, newMyCellarObject);
                rangement.addObject(newMyCellarObject);
                end.setText(Program.getLabel("AddVin.1ItemAdded", LabelProperty.SINGLE), true);
                panelWineAttribute.setStillNbItems(countStillToAdd - 1);
              }
            } else { //Un seul rangement simple
              if (rangement.isSimplePlaceLimited() && (rangement.getTotalCellUsed(place) + countStillToAdd) > rangement.getNbColumnsStock()) {
                hasNoError = false;
                Debug("ERROR: This caisse is full. Unable to add all bottles in the same place!");
                Erreur.showSimpleErreur(Program.getError("Error154"), Program.getError("Error153"));
                end.setText("");
              } else {
                nb_bottle_add_only_one_place = countStillToAdd;
                for (int z = 0; z < countStillToAdd - 1; z++) {
                  MyCellarObject copy = createCopy(newMyCellarObject);
                  Program.getStorage().addHistory(HistoryState.ADD, copy);
                  rangement.addObject(copy);
                }
                countStillToAdd = 1;
              }
            }
          } // Fin de l'ajout de plusieurs bouteilles restantes

          if (countStillToAdd == 1) {
            if (isModify) {
              //Suppression de la bouteille lors de la modification
              Debug("Updating bottle when modifying");
              myCellarObject.getRangement().clearStock(myCellarObject, myCellarObject.getPlace());
              myCellarObject.update(newMyCellarObject);
              Program.getStorage().addHistory(HistoryState.MODIFY, myCellarObject);
              objectAdded = true;
            } else {
              //Ajout de la bouteille
              Debug("Adding bottle...");
              Program.getStorage().addHistory(HistoryState.ADD, newMyCellarObject);
              objectAdded = rangement.addObject(newMyCellarObject);
            }

            if (objectAdded) {
              resetValues();
            } else {
              Debug("ERROR: Adding bottle: Storage full");
              Erreur.showSimpleErreur(MessageFormat.format(Program.getError("Error151"), rangement.getName()), Program.getError("Error153"));
              hasNoError = false;
            }
          }
        } else { // Modification de plusieurs vins vers une caisse
          objectAdded = modifySeveralObjectsInSimplePlace(place, rangement);
        }
      } else {
        // Ajout dans une Armoire
        if (severalItems) { //On ne peut pas deplacer plusieurs bouteilles vers une armoire
          Debug("ERROR: Unable to move multiple bottles to a Complex place");
          end.setText("");
          String nomRangement = rangement.getName();
          Erreur.showSimpleErreur(MessageFormat.format(Program.getError("Error104", PLURAL), nomRangement), Program.getError("Error105")); //"Veuillez selectionner un rangement de type caisse.");//Impossible de deplacer plusieurs bouteilles dans
          enableAll(true);
        } else {
          // Ajout d'une bouteille dans l'armoire
          int lieu_num_selected = place.getPlaceNum();
          int ligne = place.getLine();
          int colonne = place.getColumn();

          if (isModify && !panelPlace.isPlaceModified()) { //Si aucune modification du Lieu
            Debug("ERROR: Shouldn't come here");
            throw new RuntimeException("Shouldn't happen!");
          }
          int nb_free_space = 0;
          MyCellarObject myCellarObjectFound = null;
          if (!isModify || panelPlace.isPlaceModified()) { //Si Ajout bouteille ou modification du lieu
            Debug("Adding bottle or modifying place");
            myCellarObjectFound = rangement.getObject(lieu_num_selected - 1, ligne - 1, colonne - 1).orElse(null);
            if (myCellarObjectFound == null) {
              nb_free_space = rangement.getCountFreeCellFrom(lieu_num_selected - 1, ligne - 1, colonne - 1);
            }
          }
          Debug("Creating new bottle...");
          MyCellarObject newMyCellarObject = createMyCellarObject(annee, null, rangement, lieu_num_selected, ligne, colonne);
          if (myCellarObjectFound == null) {
            if (isModify) {
              Debug("Empty case: Modifying bottle");
              final Place oldPLace = myCellarObject.getPlace();
              myCellarObject.update(newMyCellarObject);
              newMyCellarObject.getRangement().updateToStock(newMyCellarObject);
              Program.getStorage().addHistory(HistoryState.MODIFY, myCellarObject);
              if (!rangement.isSimplePlace()) {
                Debug("Deleting from older complex place");
                oldPLace.getRangement().clearComplexStock(oldPLace);
              }
            } else {
              Debug("Empty case: Adding bottle");
              Program.getStorage().addHistory(HistoryState.ADD, newMyCellarObject);
              rangement.addObject(newMyCellarObject);
              if (countStillToAdd > 1 && nb_free_space > 1) { // Add bottles next to each others
                if (nb_free_space > countStillToAdd) {
                  nb_free_space = countStillToAdd;
                }
                if (JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(Start.getInstance(), MessageFormat.format(Program.getError("Error175", PLURAL), nb_free_space), Program.getLabel("Infos049"), JOptionPane.YES_NO_OPTION)) {
                  Debug("Putting multiple bottle in chosen place");
                  nb_bottle_add_only_one_place = nb_free_space;
                  countStillToAdd -= nb_free_space + 1;
                  for (int z = 1; z < nb_free_space; z++) {
                    newMyCellarObject = createMyCellarObject(annee, null, rangement, lieu_num_selected, ligne, colonne + z);
                    Program.getStorage().addHistory(HistoryState.ADD, newMyCellarObject);
                    rangement.addObject(newMyCellarObject);
                  }
                }
              }
            }

            if (countStillToAdd > 1) {
              panelWineAttribute.setStillNbItems(countStillToAdd - 1);
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
            objectAdded = true;
          } else { // La case n'est pas vide
            Debug("WARNING: Not an empty place, Replace?");
            String erreur_txt1 = MessageFormat.format(Program.getError("Error059"), myCellarObjectFound.getNom(), myCellarObjectFound.getAnnee()); // deja present a cette place
            String erreur_txt2 = Program.getError("Error060"); //"Voulez vous le remplacer?
            if (JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(Start.getInstance(), erreur_txt1 + "\n" + erreur_txt2, Program.getLabel("Infos049"), JOptionPane.YES_NO_OPTION)) {
              replaceWine(newMyCellarObject, myCellarObjectFound);
              end.setText(isModify ? Program.getLabel("AddVin.1ItemModified", LabelProperty.SINGLE) : Program.getLabel("AddVin.1ItemAdded", LabelProperty.SINGLE), true);
              resetValues();
            } else {
              end.setText(Program.getLabel("AddVin.NotSaved", LabelProperty.THE_SINGLE));
              enableAll(true);
              hasNoError = false;
            }
          }
        }
      }

      if (objectAdded) {
        if (isModify) {
          if (listVin != null) {
            listVin.updateList(listBottleInModification);
          }
          if (listBottleInModification.size() == 1) {
            end.setText(Program.getLabel("AddVin.1ItemModified", LabelProperty.SINGLE), true);
          } else {
            end.setText(MessageFormat.format(Program.getLabel("AddVin.NItemModified", LabelProperty.PLURAL), listBottleInModification.size()));
          }
        } else {
          if (nb_bottle_add_only_one_place == 0) {
            end.setText(Program.getLabel("AddVin.1ItemAdded", LabelProperty.SINGLE), true);
          } else {
            end.setText(MessageFormat.format(Program.getLabel("AddVin.NItemAdded", LabelProperty.PLURAL), nb_bottle_add_only_one_place));
          }
          panelGeneral.setTypeDefault();
        }
      }
      if (hasNoError) {
        doAfterRun();
      }
    } catch (MyCellarException e) {
      Program.showException(e);
    }
    Debug("Running End");
  }

  private boolean modifySeveralObjectsInSimplePlace(Place place, Rangement rangement) {
    Debug("modifySeveralObjectsInSimplePlace...");
    Debug("Modifying multiple bottles to a Simple place");
    boolean objectAdded = false;
    if (!place.hasPlace()) {
      Debug("Modifying without changing place");
      boolean bOneBottle = listBottleInModification.size() == 1;
      for (MyCellarObject tmp : listBottleInModification) {
        updateMyCellarObject(bOneBottle, tmp);
        tmp.updateStatus();

        if (isModify) {
          Debug("Modifying bottle...");
          Program.getStorage().addHistory(HistoryState.MODIFY, tmp);
          objectAdded = true;
        } else {
          Debug("Adding bottle...");
          Program.getStorage().addHistory(HistoryState.ADD, tmp);
          //Ajout des bouteilles
          if (tmp.getRangement().addObject(tmp)) {
            objectAdded = true;
          }
        }
      }
      resetValues();
    } else {
      Debug("Modifying with changing place");
      int nbbottle = listBottleInModification.size();
      if (rangement.isSimplePlaceLimited() && (rangement.getTotalCellUsed(place) + nbbottle) > rangement.getNbColumnsStock()) {
        Debug("ERROR: Not enough place!");
        Erreur.showSimpleErreur(Program.getError("Error154"), Program.getError("Error153"));
        panelPlace.enableSimplePlace(true);
        addButton.setEnabled(true);
        end.setText("");
        return false;
      }
      boolean bOneBottle = listBottleInModification.size() == 1;
      for (MyCellarObject tmp : listBottleInModification) {
        updateMyCellarObject(bOneBottle, tmp);
        Debug("Adding multiple bottles in simple place...");
        if (isModify && tmp.isInExistingPlace()) {
          Debug("Delete from stock");
          tmp.getRangement().clearStock(tmp, tmp.getPlace());
        }
        //Ajout des bouteilles dans la caisse
        tmp.setEmplacement(rangement.getName());
        tmp.setNumLieu(place.getPlaceNum());
        tmp.setLigne(0);
        tmp.setColonne(0);
        tmp.updateStatus();
        tmp.getRangement().updateToStock(tmp);
        Debug("Bottle updated.");
        Program.getStorage().addHistory(isModify ? HistoryState.MODIFY : HistoryState.ADD, tmp);
        if (isModify) {
          objectAdded = true;
          resetValues();
        } else {
          if (rangement.addObject(tmp)) {
            objectAdded = true;
            resetValues();
          }
        }
      }
    }
    Debug("modifySeveralObjectsInSimplePlace... End");
    return objectAdded;
  }

  private MyCellarObject createCopy(MyCellarObject newMyCellarObject) {
    if (Program.isWineType()) {
      return new Bouteille().castCopy(newMyCellarObject);
    }
    if (Program.isMusicType()) {
      return new Music().castCopy(newMyCellarObject);
    }
    Program.throwNotImplementedForNewType();
    return new Bouteille();
  }

  private MyCellarObject createMyCellarObject(String annee, Place place, Rangement rangement, int numLieu, int line, int column) {
    if (Program.isWineType()) {
      return createBouteille(annee, place, rangement, numLieu, line, column);
    }
    if (Program.isMusicType()) {
      return createMusic(annee, place, rangement, numLieu, line, column);
    }
    Program.throwNotImplementedForNewType();
    return new Bouteille();
  }

  private Bouteille createBouteille(String annee, Place place, Rangement rangement, int numLieu, int line, int colonne) {
    BouteilleBuilder bouteilleBuilder = new BouteilleBuilder(panelGeneral.getObjectName())
        .annee(annee)
        .type(panelGeneral.getType())
        .place(rangement.getName())
        .numPlace(place != null ? place.getPlaceNum() : numLieu)
        .price(panelWineAttribute.getPrice())
        .comment(commentTextArea.getText())
        .maturity(panelWineAttribute.getMaturity())
        .parker(panelWineAttribute.getParker())
        .color(panelWineAttribute.getColor())
        .status(nonNullValueOrDefault(panelWineAttribute.getStatusIfModified(), BottlesStatus.CREATED.name()))
        .vignoble(panelVignobles.getCountry(), panelVignobles.getVignoble(), panelVignobles.getAOC(), panelVignobles.getIGP());
    if (line != -1) {
      bouteilleBuilder.line(line);
    }
    if (colonne != -1) {
      bouteilleBuilder.column(colonne);
    }
    return bouteilleBuilder.build();
  }

  private Music createMusic(String annee, Place place, Rangement rangement, int numLieu, int line, int colonne) {
    Music.MusicBuilder musicBuilder = new Music.MusicBuilder(panelGeneral.getObjectName())
        .annee(annee)
//        .type(demie)
        .place(rangement.getName())
        .numPlace(place != null ? place.getPlaceNum() : numLieu)
        .price(panelWineAttribute.getPrice())
        .comment(commentTextArea.getText())
//        .maturity(dateOfC)
//        .parker(parker)
//        .color(color)
        .status(nonNullValueOrDefault(panelWineAttribute.getStatusIfModified(), BottlesStatus.CREATED.name()));
//        .vignoble(country, vignoble, aoc, igp);
    if (line != -1) {
      musicBuilder.line(line);
    }
    if (colonne != -1) {
      musicBuilder.column(colonne);
    }
    return musicBuilder.build();
  }

  private void updateMyCellarObject(boolean singleObject, MyCellarObject cellarObject) {
    String price = panelWineAttribute.getPrice();
    String comment = commentTextArea.getText();
    String dateOfC = panelWineAttribute.getMaturity();
    String parker = panelWineAttribute.getParker();
    String color = panelWineAttribute.getColor();
    String status = nonNullValueOrDefault(panelWineAttribute.getStatusIfModified(), BottlesStatus.MODIFIED.name());
    String country = panelVignobles.getCountry();
    String vignoble = panelVignobles.getVignoble();
    String aoc = panelVignobles.getAOC();
    String igp = panelVignobles.getIGP();
    String type = panelGeneral.getType();
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
      if (singleObject || !price.isEmpty()) {
        bouteille.setPrix(price);
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
      if (singleObject || !price.isEmpty()) {
        music.setPrix(price);
      }
//      if (singleObject || !country.isEmpty() || !vignoble.isEmpty() || !aoc.isEmpty() || !igp.isEmpty()) {
//        music.setVignoble(new VignobleJaxb(country, vignoble, aoc, igp));
//      }
    } else {
      Program.throwNotImplementedForNewType();
    }

    if (singleObject || panelWineAttribute.getStatusList().isModified()) {
      cellarObject.setStatus(status);
    }
    if (singleObject || !type.isEmpty()) {
      cellarObject.setKind(type);
    }
  }

  private boolean modifyOneOrSeveralObjectsWithoutPlaceModification(String annee) throws MyCellarException {
    Debug("modifyOneOrSeveralObjectsWithoutPlaceModification...");
    boolean m_bbottle_add = false;
    if (!severalItems) {
      Debug("Modifying one bottle in Armoire without changing place");
      MyCellarObject tmp = createMyCellarObject(annee, null, myCellarObject.getRangement(), myCellarObject.getNumLieu(), myCellarObject.getLigne(), myCellarObject.getColonne());
      Debug("Replacing bottle...");
      myCellarObject.update(tmp);
      Program.getStorage().addHistory(HistoryState.MODIFY, tmp);
      m_bbottle_add = true;
    } else {
      Debug("Modifying multiple bottles in Armoire without changing place");
      final String comment = commentTextArea.isModified() ? commentTextArea.getText() : null;
      final String prix = nonNullValueOrDefault(panelWineAttribute.getPriceIfModified(), null);
      for (MyCellarObject tmp : listBottleInModification) {
        if (Program.isWineType()) {
          Bouteille bouteille = new Bouteille().cast(tmp);
          bouteille.setPrix(nonNullValueOrDefault(prix, bouteille.getPrix()));
          bouteille.setComment(nonNullValueOrDefault(comment, bouteille.getComment()));
          bouteille.setMaturity(nonNullValueOrDefault(panelWineAttribute.getMaturityIfModified(), bouteille.getMaturity()));
          bouteille.setParker(nonNullValueOrDefault(panelWineAttribute.getParkerIfModified(), bouteille.getParker()));
          bouteille.setColor(nonNullValueOrDefault(panelWineAttribute.getColorIfModified(), bouteille.getColor()));
          if (panelVignobles.isModified()) {
            bouteille.setVignoble(new VignobleJaxb(panelVignobles.getCountry(), panelVignobles.getVignoble(), panelVignobles.getAOC(), panelVignobles.getIGP()));
          }
        } else if (Program.isMusicType()) {
          Music music = new Music().cast(tmp);
          music.setPrix(nonNullValueOrDefault(prix, music.getPrix()));
          music.setComment(nonNullValueOrDefault(comment, music.getComment()));
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
        tmp.setKind(nonNullValueOrDefault(panelGeneral.getTypeIfModified(), tmp.getKind()));
        tmp.updateStatus();
        // Add multiple bottles
        Debug("Adding multiple bottles...");
        Rangement rangement = tmp.getRangement();
        if (isModify) {
          //Delete Bouteilles
          Debug("Deleting bottles when modifying");
          Program.getStorage().deleteWine(tmp);
          if (!rangement.isSimplePlace()) { //Si ce n'est pas une caisse on supprime de stockage
            Debug("is Not a Caisse. Delete from stock");
            rangement.clearComplexStock(tmp.getPlace());
          }
        }
        //Ajout des bouteilles dans la caisse
        Debug("Adding bottle...");
        Program.getStorage().addHistory(isModify ? HistoryState.MODIFY : HistoryState.ADD, tmp);
        //Ajout des bouteilles dans ALL
        if (rangement.addObject(tmp)) {
          m_bbottle_add = true;
        }
      }
    }
    resetValues();
    Debug("modifyOneOrSeveralObjectsWithoutPlaceModification... End");
    return m_bbottle_add;
  }

  private void replaceWine(final MyCellarObject newMyCellarObject, final MyCellarObject objectToDelete) throws MyCellarException {
    Debug("ReplaceWine...");
    //Change wine in a place
    Program.getStorage().addHistory(isModify ? HistoryState.MODIFY : HistoryState.ADD, newMyCellarObject);
    RangementUtils.replaceMyCellarObject(objectToDelete, newMyCellarObject, isModify ? myCellarObject.getPlace() : null);
    if (isModify) {
      myCellarObject.update(newMyCellarObject);
      if (listVin != null) {
        listVin.updateList(listBottleInModification);
        listVin.updateList(List.of(objectToDelete));
      }
    } else {
      Program.getStorage().addWine(newMyCellarObject);
    }
    Debug("ReplaceWine... End");
  }

  private void doAfterRun() {
    Debug("Do After Run...");
    myCellarObject = null;
    panelGeneral.setMyCellarObject(null);
    ProgramPanels.updateManagePlacePanel();
    panelVignobles.updateList();
    if (!isModify) {
      Debug("Do After Run... End");
      return;
    }

    if (listVin == null) {
      enableAll(true);
      isModify = false;
      panelPlace.setBeforeLabelsVisible(false);
      addButton.setText(Program.getLabel("Infos071"));
    } else if (listVin.isEmpty()) {
      reInitAddVin();
    }

    Debug("Do After Run... End");
  }

  private void reInitAddVin() {
    severalItems = false;
    if (listVin != null) {
      remove(listVin);
      listVin = null;
      ProgramPanels.TABBED_PANE.setTitleAt(ProgramPanels.TABBED_PANE.getSelectedIndex(), Program.getLabel("Main.tabAdd", A_SINGLE));
    }
    panelGeneral.setSeveralItems(severalItems);
    panelPlace.resetValues();
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
    myCellarObject = null;
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
    return ProgramPanels.createAddVin().runExit();
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
