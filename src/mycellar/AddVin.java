package mycellar;

import mycellar.Bouteille.BouteilleBuilder;
import mycellar.actions.OpenShowErrorsAction;
import mycellar.core.BottlesStatus;
import mycellar.core.ICutCopyPastable;
import mycellar.core.IMyCellar;
import mycellar.core.IUpdatable;
import mycellar.core.MyCellarManageBottles;
import mycellar.core.MyCellarObject;
import mycellar.core.MyCellarSwingWorker;
import mycellar.core.datas.history.HistoryState;
import mycellar.core.datas.jaxb.VignobleJaxb;
import mycellar.core.exceptions.MyCellarException;
import mycellar.core.text.LabelProperty;
import mycellar.core.uicomponents.MyCellarButton;
import mycellar.core.uicomponents.PopupListener;
import mycellar.core.uicomponents.TabEvent;
import mycellar.general.ProgramPanels;
import mycellar.placesmanagement.places.AbstractPlace;
import mycellar.placesmanagement.places.ComplexPlace;
import mycellar.placesmanagement.places.PlacePosition;
import mycellar.placesmanagement.places.PlaceUtils;
import mycellar.placesmanagement.places.SimplePlace;

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
import static mycellar.core.text.LabelProperty.A_SINGLE;
import static mycellar.core.text.LabelProperty.PLURAL;
import static mycellar.core.text.MyCellarLabelManagement.getError;
import static mycellar.core.text.MyCellarLabelManagement.getLabel;


/**
 * Titre : Cave &agrave; vin
 * Description : Votre description
 * Copyright : Copyright (c) 2005
 * Soci&eacute;t&eacute; : Seb Informatique
 *
 * @author S&eacute;bastien Duch&eacute;
 * @version 32.0
 * @since 16/09/22
 */
public final class AddVin extends MyCellarManageBottles implements Runnable, ITabListener, ICutCopyPastable, IMyCellar, IUpdatable {

  private static final long serialVersionUID = -8925831759212999905L;
  private final AddVin instance;
  private boolean isModify = false; // Pour la Modification
  private AbstractPlace placeInModification;
  private ListVin listVin;
  private LinkedList<MyCellarObject> listBottleInModification; //Pour enlever dans ListVin

  public AddVin() {
    super();
    instance = this;
    Debug("Constructor");
    myCellarObject = null;
    panelGeneral.setMyCellarObject(null);
    addButton = new MyCellarButton("Main.Add", new AddAction());
    addButton.setMnemonic(ajouterChar);
    cancelButton = new MyCellarButton("Main.Cancel", new CancelAction());

    panelGeneral.setModificationDetectionActive(false);
    panelWineAttribute.setModificationDetectionActive(false);
    panelGeneral.initValues();
    panelWineAttribute.initValues();

    PopupListener popup_l = new PopupListener();
    panelGeneral.setMouseListener(popup_l);
    panelWineAttribute.setMouseListener(popup_l);
    commentTextArea.addMouseListener(popup_l);

    end.setForeground(Color.red);
    end.setHorizontalAlignment(SwingConstants.CENTER);
    setLayout(new BorderLayout());
    add(new PanelMain(), BorderLayout.CENTER);

    panelPlace.setModificationDetectionActive(true);
    panelGeneral.setModificationDetectionActive(true);
    panelWineAttribute.setModificationDetectionActive(true);
    commentTextArea.setActive(true);
    setVisible(true);
    Debug("Constructor Done");
  }

  protected static void Debug(String sText) {
    Program.Debug("AddVin: " + sText);
  }

  private void resetValues() {
    Debug("Reset Values...");
    panelGeneral.resetValues();
    panelWineAttribute.resetValues();
    panelPlace.resetValues();

    commentTextArea.setText("");
    commentTextArea.setModified(false);

    ProgramPanels.updateSearchTable();
    panelVignobles.resetCombos();
    placeInModification = null;
    Debug("Reset Values... Done");
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
      listVin.setObjects(myCellarObjects);
    }

    setBottle(myCellarObjects.get(0));
  }

  /**
   * Fonction de chargement d'un vin pour la classe ListVin
   *
   * @param cellarObject MyCellarObject
   */
  private void setBottle(MyCellarObject cellarObject) {
    new MyCellarSwingWorker() {
      @Override
      protected void done() {
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

        panelPlace.resetPanel();
        panelPlace.setBeforeObjectLabels(myCellarObject);
        addButton.setText(getLabel("Main.Modify"));
        placeInModification = myCellarObject.getAbstractPlace();
        end.setText(getLabel("AddVin.EnterChanges"));
        Debug("Set Bottle... Done");
      }
    }.execute();
  }

  /**
   * Fonction pour le chargement de vins pour la classe ListVin.
   */
  void setObjectsInModification(LinkedList<MyCellarObject> myCellarObjects) {
    Debug("setBottlesInModification...");
    severalItems = myCellarObjects.size() > 1;
    panelGeneral.setSeveralItems(severalItems);
    listBottleInModification = myCellarObjects;

    resetValues();
    if (severalItems) {
      panelGeneral.setViewToSeveralItemsMode(listBottleInModification.size());
      panelWineAttribute.seNbItemsEnabled(false);
      addButton.setEnabled(true);
      end.setText(getLabel("AddVin.MoveError", LabelProperty.PLURAL));
    } else {
      setBottle(listBottleInModification.getFirst());
    }
    Debug("setBottlesInModification... Done");
  }

  private boolean performValidations() {
    Debug("Perform validations...");
    boolean validations = panelGeneral.performValidation();
    validations &= panelPlace.performValidation(isModify);
    Debug("Perform validations... Done");
    return validations;
  }

  @Override
  public void run() {
    Debug("Running...");
    try {
      // Check Name / Year / Place / Part
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
        end.setText(getLabel("AddVin.ModifyInProgreess"));
        enableAll(false);
      }

      PlacePosition place = panelPlace.getSelectedPlacePosition();
      AbstractPlace abstractPlace = place.getAbstractPlace();
      Objects.requireNonNull(abstractPlace);
      if (!place.hasPlace() && isModify) {
        //Si aucun emplacement n'a ete selectionne (modif du nom)
        place = myCellarObject.getPlacePosition();
        if (placeInModification != null) {
          abstractPlace = placeInModification;
        }
      }

      end.setText(getLabel("AddVin.AddingInProgress"));
      Result result;
      if (!panelPlace.isPlaceModified() && isModify) {
        result = modifyOneOrSeveralObjectsWithoutPlaceModification(annee);
      } else if (abstractPlace.isSimplePlace()) {
        result = modifyOrAddObjectsInSimplePlace(abstractPlace, place, annee);
      } else {
        result = modifyOrAddObjectsInComplexPlace(abstractPlace, place, annee);
      }

      if (result.isAdded()) {
        if (isModify) {
          if (listVin != null) {
            listVin.updateList(listBottleInModification);
          }
          if (listBottleInModification.size() == 1) {
            end.setText(getLabel("AddVin.1ItemModified", LabelProperty.SINGLE), true);
          } else {
            end.setText(MessageFormat.format(getLabel("AddVin.NItemModified", LabelProperty.PLURAL), listBottleInModification.size()));
          }
        } else {
          if (result.getNbItemsAdded() == 0) {
            end.setText(getLabel("AddVin.1ItemAdded", LabelProperty.SINGLE), true);
          } else {
            end.setText(MessageFormat.format(getLabel("AddVin.NItemAdded", LabelProperty.PLURAL), result.getNbItemsAdded()));
          }
          panelGeneral.setTypeDefault();
        }
      }
      if (result.isRequireReset()) {
        resetValues();
      }
      if (!result.isHasError()) {
        doAfterRun();
      }
    } catch (MyCellarException e) {
      Program.showException(e);
    }
    Debug("Running Done");
  }

  private Result modifyOrAddObjectsInComplexPlace(AbstractPlace rangement, PlacePosition place, String annee) throws MyCellarException {
    Result result = new Result();
    int countStillToAdd = panelWineAttribute.getNbItems();
    ComplexPlace complexPlace = (ComplexPlace) rangement;
    // Ajout dans une Armoire
    if (severalItems) { //On ne peut pas deplacer plusieurs bouteilles vers une armoire
      Debug("ERROR: Unable to move multiple objects to a Complex place");
      end.setText("");
      String nomRangement = complexPlace.getName();
      Erreur.showSimpleErreur(MessageFormat.format(getError("Error.unableToMoveNItemsIn", PLURAL), nomRangement), getError("Error.selectSimpleStorage"));
      enableAll(true);
      return result;
    }

    // Ajout d'une bouteille dans l'armoire
    int part = place.getPlaceNumIndex();
    int line = place.getLineIndex();
    int column = place.getColumnIndex();

    if (isModify && !panelPlace.isPlaceModified()) { //Si aucune modification du Lieu
      Debug("ERROR: Shouldn't come here");
      throw new RuntimeException("Shouldn't happen!");
    }
    int nb_free_space = 0;
    MyCellarObject myCellarObjectFound = null;
    if (!isModify || panelPlace.isPlaceModified()) { //Si Ajout bouteille ou modification du lieu
      Debug("Adding bottle or modifying place");
      myCellarObjectFound = complexPlace.getObject(
          new PlacePosition.PlacePositionBuilderZeroBased(complexPlace)
              .withNumPlace(part)
              .withLine(line)
              .withColumn(column)
              .build()).orElse(null);
      if (myCellarObjectFound == null) {
        nb_free_space = complexPlace.getCountFreeCellFrom(part, line, column);
      }
    }

    Debug("Creating new bottle...");
    MyCellarObject newMyCellarObject = createMyCellarObject(annee, new PlacePosition.PlacePositionBuilderZeroBased(complexPlace)
        .withNumPlace(part)
        .withLine(line)
        .withColumn(column)
        .build(), complexPlace);
    if (myCellarObjectFound == null) {
      if (isModify) {
        Debug("Empty case: Modifying bottle");
        final PlacePosition oldPLace = myCellarObject.getPlacePosition();
        myCellarObject.update(newMyCellarObject);
        newMyCellarObject.getAbstractPlace().updateToStock(newMyCellarObject);
        Program.getStorage().addHistory(HistoryState.MODIFY, myCellarObject);
        if (!complexPlace.isSimplePlace()) {
          Debug("Deleting from older complex place");
          ((ComplexPlace) oldPLace.getAbstractPlace()).clearStorage(oldPLace);
        }
      } else {
        Debug("Empty case: Adding bottle");
        Program.getStorage().addHistory(HistoryState.ADD, newMyCellarObject);
        complexPlace.addObject(newMyCellarObject);
        if (countStillToAdd > 1 && nb_free_space > 1) { // Add bottles next to each others
          if (nb_free_space > countStillToAdd) {
            nb_free_space = countStillToAdd;
          }
          if (JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(Start.getInstance(), MessageFormat.format(getError("Error.questionAddNItemsNext", PLURAL), nb_free_space), getLabel("Main.AskConfirmation"), JOptionPane.YES_NO_OPTION)) {
            Debug("Putting multiple bottle in chosen place");
            result.setNbItemsAdded(nb_free_space);
            countStillToAdd -= nb_free_space + 1;
            for (int i = 1; i < nb_free_space; i++) {
              newMyCellarObject = createMyCellarObject(annee, new PlacePosition.PlacePositionBuilderZeroBased(complexPlace)
                  .withNumPlace(part)
                  .withLine(line)
                  .withColumn(column + i)
                  .build(), complexPlace);
              Program.getStorage().addHistory(HistoryState.ADD, newMyCellarObject);
              complexPlace.addObject(newMyCellarObject);
            }
          }
        }
      }

      if (countStillToAdd > 1) {
        panelWineAttribute.setStillNbItems(countStillToAdd - 1);
        panelPlace.resetValues();
      } else {
        result.setRequireReset(true);
        resetValues();
        if (isModify) {
          panelGeneral.setEditable(false);
          panelWineAttribute.setEditable(false);
          commentTextArea.setEditable(false);
          addButton.setEnabled(false);
          panelPlace.enablePlaceSelection(false);
        }
      }
      if (isModify) {
        panelPlace.enablePlace(true);
      }
      result.setAdded(true);
    } else { // La case n'est pas vide
      Debug("WARNING: Not an empty place, Replace?");
      String erreur_txt1 = MessageFormat.format(getError("Error.alreadyInStorage"), myCellarObjectFound.getNom(), myCellarObjectFound.getAnnee());
      String erreur_txt2 = getError("Error.questionReplaceIt");
      if (JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(Start.getInstance(), erreur_txt1 + "\n" + erreur_txt2, getLabel("Main.AskConfirmation"), JOptionPane.YES_NO_OPTION)) {
        replaceWine(newMyCellarObject, myCellarObjectFound);
        end.setText(isModify ? getLabel("AddVin.1ItemModified", LabelProperty.SINGLE) : getLabel("AddVin.1ItemAdded", LabelProperty.SINGLE), true);
        result.setAdded(true);
        result.setRequireReset(true);
        resetValues();
      } else {
        end.setText(getLabel("AddVin.NotSaved", LabelProperty.THE_SINGLE));
        enableAll(true);
        result.setHasError(true);
      }
    }
    return result;
  }

  private Result modifyOrAddObjectsInSimplePlace(AbstractPlace abstractPlace, PlacePosition place, String annee) {
    Debug("modifyOrAddObjectsInSimplePlace...");
    Result result = new Result();
    int countStillToAdd = panelWineAttribute.getNbItems();
    SimplePlace simplePlace = (SimplePlace) abstractPlace;
    if (!simplePlace.hasFreeSpace(place)) {
      Erreur.showSimpleErreur(getError("Error.NotEnoughSpaceStorage"), getError("Error.selectAnotherStorage"));
      end.setText("");
      Debug("ERROR: No free spaces");
      return result;
    }

    if (severalItems) {
      return modifySeveralObjectsInSimplePlace(place, simplePlace);
    }

    MyCellarObject newMyCellarObject = createMyCellarObject(annee, place, simplePlace);
    // Add multiple bottle with question
    if (countStillToAdd > 1) {
      if (!Program.hasOnlyOnePlace()) {
        Debug("Adding multiple objects in the same place?");
        String message = MessageFormat.format(getError("Error.questionAddNItemIn", LabelProperty.PLURAL), countStillToAdd, simplePlace.getName());
        if (JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(Start.getInstance(), message, getLabel("Main.AskConfirmation"), JOptionPane.YES_NO_OPTION)) {
          //Add several bottles in Caisse
          Debug("Adding multiple objects in the same place: YES");

          if (simplePlace.isLimited() && (simplePlace.getCountCellUsed(place) + countStillToAdd) > simplePlace.getMaxItemCount()) {
            Erreur.showSimpleErreur(getError("Error.NotEnoughSpaceStorage"), getError("Error.selectAnotherStorage"));
            end.setText("");
          } else {
            result.setNbItemsAdded(countStillToAdd);
            for (int j = 0; j < countStillToAdd; j++) {
              MyCellarObject copy = createCopy(newMyCellarObject);
              Program.getStorage().addHistory(HistoryState.ADD, copy);
              simplePlace.addObject(copy);
            }
            end.setText(MessageFormat.format(getLabel("AddVin.NItemAdded", LabelProperty.PLURAL), countStillToAdd), true);
            result.setAdded(true);
            result.setRequireReset(true);
          }
        } else {
          Debug("Adding multiple objects in the same place: NO");
          //Add a single bottle in Caisse
          Program.getStorage().addHistory(HistoryState.ADD, newMyCellarObject);
          simplePlace.addObject(newMyCellarObject);
          end.setText(getLabel("AddVin.1ItemAdded", LabelProperty.SINGLE), true);
          panelWineAttribute.setStillNbItems(countStillToAdd - 1);
        }
      } else { // One simplePlace
        if (simplePlace.isLimited() && (simplePlace.getCountCellUsed(place) + countStillToAdd) > simplePlace.getMaxItemCount()) {
          result.setHasError(true);
          Debug("ERROR: This caisse is full. Unable to add all bottles in the same place!");
          Erreur.showSimpleErreur(getError("Error.NotEnoughSpaceStorage"), getError("Error.selectAnotherStorage"));
          end.setText("");
        } else {
          Debug("Adding n objects: " + (countStillToAdd - 1));
          for (int i = 0; i < countStillToAdd - 1; i++) {
            MyCellarObject copy = createCopy(newMyCellarObject);
            Program.getStorage().addHistory(HistoryState.ADD, copy);
            simplePlace.addObject(copy);
          }
          result.setAdded(true);
          result.setRequireReset(true);
          countStillToAdd = 1;
        }
      }
    } // Fin de l'ajout de plusieurs bouteilles restantes

    if (countStillToAdd == 1) {
      if (isModify) {
        //Suppression de la bouteille lors de la modification
        Debug("Updating bottle when modifying");
        myCellarObject.getAbstractPlace().clearStorage(myCellarObject);
        myCellarObject.update(newMyCellarObject);
        Program.getStorage().addHistory(HistoryState.MODIFY, myCellarObject);
      } else {
        //Ajout de la bouteille
        Debug("Adding bottle...");
        Program.getStorage().addHistory(HistoryState.ADD, newMyCellarObject);
        final boolean added = simplePlace.addObject(newMyCellarObject);
        result.setAdded(added);
        result.setRequireReset(added);
      }

      if (result.isAdded()) {
        resetValues();
      } else {
        Debug("ERROR: Adding bottle: Storage full");
        Erreur.showSimpleErreur(MessageFormat.format(getError("Error.storageFull"), simplePlace.getName()), getError("Error.selectAnotherStorage"));
        result.setHasError(true);
      }
    }
    return result;
  }

  private Result modifySeveralObjectsInSimplePlace(PlacePosition place, SimplePlace simplePlace) {
    Debug("modifySeveralObjectsInSimplePlace...");
    Debug("Modifying multiple bottles to a Simple place");
    if (!place.hasPlace()) {
      return modifySeveralObjectsWithoutChangingSimplePlace();
    } else {
      return modifySeveralObjectsWithChangingSimplePlace(place, simplePlace);
    }
  }

  private Result modifySeveralObjectsWithChangingSimplePlace(PlacePosition place, SimplePlace simplePlace) {
    Debug("Modifying with changing place");
    Result result = new Result();
    int nbbottle = listBottleInModification.size();
    if (simplePlace.isLimited() && (simplePlace.getCountCellUsed(place) + nbbottle) > simplePlace.getMaxItemCount()) {
      Debug("ERROR: Not enough place!");
      Erreur.showSimpleErreur(getError("Error.NotEnoughSpaceStorage"), getError("Error.selectAnotherStorage"));
      panelPlace.enableSimplePlace(true);
      addButton.setEnabled(true);
      end.setText("");
      return result;
    }
    boolean bOneBottle = listBottleInModification.size() == 1;
    for (MyCellarObject tmp : listBottleInModification) {
      updateMyCellarObject(bOneBottle, tmp);
      Debug("Adding multiple bottles in simple place...");
      if (isModify && tmp.isInExistingPlace()) {
        Debug("Delete from stock");
        tmp.getAbstractPlace().clearStorage(tmp, tmp.getPlacePosition());
      }
      //Ajout des bouteilles dans la caisse
      tmp.setEmplacement(simplePlace.getName());
      tmp.setNumLieu(place.getPart());
      tmp.setLigne(0);
      tmp.setColonne(0);
      tmp.updateStatus();
      tmp.getAbstractPlace().updateToStock(tmp);
      Debug("Bottle updated.");
      Program.getStorage().addHistory(isModify ? HistoryState.MODIFY : HistoryState.ADD, tmp);
      if (isModify) {
        result.setAdded(true);
        result.setRequireReset(true);
        resetValues();
      } else {
        if (simplePlace.addObject(tmp)) {
          result.setAdded(true);
          result.setRequireReset(true);
          resetValues();
        }
      }
    }
    return result;
  }

  private Result modifySeveralObjectsWithoutChangingSimplePlace() {
    Result result = new Result();
    Debug("Modifying without changing place");
    boolean bOneBottle = listBottleInModification.size() == 1;
    for (MyCellarObject tmp : listBottleInModification) {
      updateMyCellarObject(bOneBottle, tmp);
      tmp.updateStatus();

      if (isModify) {
        Debug("Modifying bottle...");
        Program.getStorage().addHistory(HistoryState.MODIFY, tmp);
        result.setAdded(true);
        result.setRequireReset(true);
      } else {
        Debug("Adding bottle...");
        Program.getStorage().addHistory(HistoryState.ADD, tmp);
        if (tmp.getAbstractPlace().addObject(tmp)) {
          result.setAdded(true);
          result.setRequireReset(true);
        }
      }
    }
    resetValues();
    return result;
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

  private MyCellarObject createMyCellarObject(String annee, PlacePosition place, AbstractPlace abstractPlace) {
    if (Program.isWineType()) {
      return createBouteille(annee, place, abstractPlace);
    }
    if (Program.isMusicType()) {
      return createMusic(annee, place, abstractPlace);
    }
    Program.throwNotImplementedForNewType();
    return new Bouteille();
  }

  private Bouteille createBouteille(String annee, PlacePosition place, AbstractPlace abstractPlace) {
    BouteilleBuilder bouteilleBuilder = new BouteilleBuilder(panelGeneral.getObjectName())
        .annee(annee)
        .type(panelGeneral.getType())
        .place(abstractPlace.getName())
        .numPlace(place.getPart())
        .price(panelWineAttribute.getPrice())
        .comment(commentTextArea.getText())
        .maturity(panelWineAttribute.getMaturity())
        .parker(panelWineAttribute.getParker())
        .color(panelWineAttribute.getColor())
        .status(nonNullValueOrDefault(panelWineAttribute.getStatusIfModified(), BottlesStatus.CREATED.name()))
        .vignoble(panelVignobles.getCountry(), panelVignobles.getVignoble(), panelVignobles.getAOC(), panelVignobles.getIGP());
    if (!abstractPlace.isSimplePlace()) {
      bouteilleBuilder.line(place.getLine());
      bouteilleBuilder.column(place.getColumn());
    }
    return bouteilleBuilder.build();
  }

  private Music createMusic(String annee, PlacePosition place, AbstractPlace abstractPlace) {
    Music.MusicBuilder musicBuilder = new Music.MusicBuilder(panelGeneral.getObjectName())
        .annee(annee)
//        .type(demie)
        .place(abstractPlace.getName())
        .numPlace(place.getPart())
        .price(panelWineAttribute.getPrice())
        .comment(commentTextArea.getText())
//        .maturity(dateOfC)
//        .parker(parker)
//        .color(color)
        .status(nonNullValueOrDefault(panelWineAttribute.getStatusIfModified(), BottlesStatus.CREATED.name()));
//        .vignoble(country, vignoble, aoc, igp);
    if (!abstractPlace.isSimplePlace()) {
      musicBuilder.line(place.getLine());
      musicBuilder.column(place.getColumn());
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

  private Result modifyOneOrSeveralObjectsWithoutPlaceModification(String annee) throws MyCellarException {
    Result result = new Result();
    Debug("modifyOneOrSeveralObjectsWithoutPlaceModification...");
    if (!severalItems) {
      Debug("Modifying one bottle in Armoire without changing place");
      MyCellarObject tmp = createMyCellarObject(annee, myCellarObject.getPlacePosition(), myCellarObject.getAbstractPlace());
      Debug("Replacing bottle...");
      myCellarObject.update(tmp);
      Program.getStorage().addHistory(HistoryState.MODIFY, tmp);
      result.setAdded(true);
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
        AbstractPlace rangement = tmp.getAbstractPlace();
        if (isModify) {
          //Delete Bouteilles
          Debug("Deleting bottles when modifying");
          rangement.removeObject(tmp);
//          Program.getStorage().deleteWine(tmp);
//          if (!rangement.isSimplePlace()) { //Si ce n'est pas une caisse on supprime de stockage
//            Debug("is Not a Caisse. Delete from stock");
//            rangement.clearComplexStock(tmp.getPlace());
//          }
        }
        //Ajout des bouteilles dans la caisse
        Debug("Adding bottle...");
        Program.getStorage().addHistory(isModify ? HistoryState.MODIFY : HistoryState.ADD, tmp);
        //Ajout des bouteilles dans ALL
        if (rangement.addObject(tmp)) {
          result.setAdded(true);
        }
      }
    }
    result.setRequireReset(true);
    resetValues();
    Debug("modifyOneOrSeveralObjectsWithoutPlaceModification... Done");
    return result;
  }

  private void replaceWine(final MyCellarObject newMyCellarObject, final MyCellarObject objectToDelete) throws MyCellarException {
    Debug("ReplaceWine...");
    //Change wine in a place
    Program.getStorage().addHistory(isModify ? HistoryState.MODIFY : HistoryState.ADD, newMyCellarObject);
    PlaceUtils.replaceMyCellarObject(objectToDelete, newMyCellarObject, isModify ? myCellarObject.getPlacePosition() : null);
    if (isModify) {
      myCellarObject.update(newMyCellarObject);
      if (listVin != null) {
        listVin.updateList(listBottleInModification);
        listVin.updateList(List.of(objectToDelete));
      }
    } else {
      Program.getStorage().addWine(newMyCellarObject);
    }
    Debug("ReplaceWine... Done");
  }

  private void doAfterRun() {
    Debug("Do After Run...");
    myCellarObject = null;
    panelGeneral.setMyCellarObject(null);
    panelPlace.clearModified();
    ProgramPanels.updateCellOrganizerPanel(false);
    ProgramPanels.setSelectedPaneModified(false);
    if (!isModify) {
      Debug("Do After Run... Done");
      return;
    }

    if (listVin == null) {
      enableAll(true);
      isModify = false;
      panelPlace.setBeforeLabelsVisible(false);
      addButton.setText(getLabel("Main.Add"));
    } else if (listVin.isEmpty()) {
      reInitAddVin();
    }

    Debug("Do After Run... Done");
  }

  private void reInitAddVin() {
    severalItems = false;
    if (listVin != null) {
      remove(listVin);
      listVin = null;
      final int selectedIndex = ProgramPanels.getSelectedTabIndex();
      final String label = getLabel("Main.TabAdd", A_SINGLE);
      ProgramPanels.setTitleAt(selectedIndex, label);
    }
    panelGeneral.setSeveralItems(severalItems);
    panelPlace.resetValues();
    panelPlace.clearModified();
    enableAll(true);
    isModify = false;
    panelPlace.setBeforeLabelsVisible(false);
    addButton.setText(getLabel("Main.Add"));
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
    if (!PlaceUtils.putTabStock()) {
      new OpenShowErrorsAction().actionPerformed(null);
    }
    panelWineAttribute.runExit();
    panelPlace.resetValues();
    panelPlace.clearModified();
    clearValues();
    reInitAddVin();
    Debug("runExit... Done");
    return true;
  }

  void reInit() {
    Debug("ReInit...");
    myCellarObject = null;
    panelGeneral.setMyCellarObject(null);
    listBottleInModification = null;
    reInitAddVin();
    Debug("ReInit... Done");
  }

  private static class Result {
    private boolean added = false;
    private boolean requireReset = false;
    private boolean hasError = false;
    private int nbItemsAdded;

    public boolean isAdded() {
      return added;
    }

    public void setAdded(boolean added) {
      this.added = added;
    }

    public boolean isRequireReset() {
      return requireReset;
    }

    public void setRequireReset(boolean requireReset) {
      this.requireReset = requireReset;
    }

    public void merge(Result result) {
      added = added || result.isAdded();
      requireReset = requireReset || result.isRequireReset();
    }

    public boolean isHasError() {
      return hasError;
    }

    public void setHasError(boolean hasError) {
      this.hasError = hasError;
    }

    public void setNbItemsAdded(int nbItemsAdded) {
      this.nbItemsAdded = nbItemsAdded;
    }

    public int getNbItemsAdded() {
      return nbItemsAdded;
    }
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
      if (runExit()) {
        Start.getInstance().removeCurrentTab();
      }
    }
  }
}
