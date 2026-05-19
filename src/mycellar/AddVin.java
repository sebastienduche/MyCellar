package mycellar;

import mycellar.Bouteille.BouteilleBuilder;
import mycellar.actions.OpenShowErrorsAction;
import mycellar.core.BottlesStatus;
import mycellar.core.ICutCopyPastable;
import mycellar.core.IMyCellar;
import mycellar.core.IUpdatable;
import mycellar.core.MyCellarManageBottles;
import mycellar.core.MyCellarSettings;
import mycellar.core.MyCellarSwingWorker;
import mycellar.core.datas.history.HistoryState;
import mycellar.core.datas.jaxb.CountryJaxb;
import mycellar.core.datas.jaxb.VignobleJaxb;
import mycellar.core.exceptions.MyCellarException;
import mycellar.core.uicomponents.PopupListener;
import mycellar.core.uicomponents.TabEvent;
import mycellar.frame.MainFrame;
import mycellar.general.ProgramPanels;
import mycellar.placesmanagement.places.AbstractPlace;
import mycellar.placesmanagement.places.ComplexPlace;
import mycellar.placesmanagement.places.PlacePosition;
import mycellar.placesmanagement.places.PlaceUtils;
import mycellar.placesmanagement.places.SimplePlace;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import static mycellar.MyCellarUtils.nonNullValueOrDefault;
import static mycellar.core.text.MyCellarLabelManagement.getError;
import static mycellar.core.text.MyCellarLabelManagement.getLabel;
import static mycellar.general.ResourceErrorKey.ERROR_ALREADYINSTORAGE;
import static mycellar.general.ResourceErrorKey.ERROR_NOTENOUGHSPACESTORAGE;
import static mycellar.general.ResourceErrorKey.ERROR_QUESTIONADDNITEMIN;
import static mycellar.general.ResourceErrorKey.ERROR_QUESTIONADDNITEMSNEXT;
import static mycellar.general.ResourceErrorKey.ERROR_QUESTIONREPLACEIT;
import static mycellar.general.ResourceErrorKey.ERROR_SELECTANOTHERSTORAGE;
import static mycellar.general.ResourceErrorKey.ERROR_SELECTSIMPLESTORAGE;
import static mycellar.general.ResourceErrorKey.ERROR_STORAGEFULL;
import static mycellar.general.ResourceErrorKey.ERROR_UNABLETOMOVENITEMSIN;
import static mycellar.general.ResourceKey.ADDVIN_1ITEMADDED;
import static mycellar.general.ResourceKey.ADDVIN_1ITEMMODIFIED;
import static mycellar.general.ResourceKey.ADDVIN_ADDINGINPROGRESS;
import static mycellar.general.ResourceKey.ADDVIN_ENTERCHANGES;
import static mycellar.general.ResourceKey.ADDVIN_MODIFYINPROGRESS;
import static mycellar.general.ResourceKey.ADDVIN_MOVEERROR;
import static mycellar.general.ResourceKey.ADDVIN_NITEMADDED;
import static mycellar.general.ResourceKey.ADDVIN_NITEMMODIFIED;
import static mycellar.general.ResourceKey.ADDVIN_NOTSAVED;
import static mycellar.general.ResourceKey.MAIN_ADD;
import static mycellar.general.ResourceKey.MAIN_CANCEL;
import static mycellar.general.ResourceKey.MAIN_MODIFY;
import static mycellar.general.ResourceKey.MAIN_TABADD;


/**
 * Titre : Cave &agrave; vin
 * Description : Votre description
 * Copyright : Copyright (c) 2005
 * Soci&eacute;t&eacute; : Seb Informatique
 *
 * @author S&eacute;bastien Duch&eacute;
 * @version 33.4
 * @since 18/05/26
 */
public final class AddVin extends MyCellarManageBottles implements Runnable, ITabListener, ICutCopyPastable, IMyCellar, IUpdatable {

  private final AddVin instance;
  private boolean isModify = false; // Pour la Modification
  private AbstractPlace placeInModification;
  private ListVin listVin;
  private LinkedList<Bouteille> listBottleInModification; //Pour enlever dans ListVin

  public AddVin() {
    super();
    instance = this;
    Debug("Constructor");
    bottle = null;
    panelGeneral.setBottle(null);
    panelSave.initializeFirstButton(MAIN_ADD, new AddAction());
    panelSave.setFirstButtonMnemonic(ajouterChar);
    panelSave.initializeSecondButton(MAIN_CANCEL, new CancelAction());

    panelGeneral.setModificationDetectionActive(false);
    panelWineAttribute.setModificationDetectionActive(false);
    panelGeneral.initValues();
    panelWineAttribute.initValues();

    PopupListener popup_l = new PopupListener();
    panelGeneral.setMouseListener(popup_l);
    panelWineAttribute.setMouseListener(popup_l);
    commentTextArea.addMouseListener(popup_l);

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
   * @param bottles LinkedList<Bouteille>
   */
  public void setBottles(List<Bouteille> bottles) {
    Debug("Set Bottles...");
    if (listVin == null) {
      listVin = new ListVin(bottles, this);
      add(listVin, BorderLayout.WEST);
    } else {
      listVin.setObjects(bottles);
    }

    setBottle(bottles.getFirst());
  }

  /**
   * Fonction de chargement d'un vin pour la classe ListVin
   *
   * @param cellarObject Bouteille
   */
  private void setBottle(Bouteille cellarObject) {
    new MyCellarSwingWorker() {
      @Override
      protected void done() {
        Debug("Set Bottle...");
        bottle = cellarObject;
        panelGeneral.setSeveralItems(false);
        panelGeneral.setBottle(bottle);
        listBottleInModification = new LinkedList<>();
        listBottleInModification.add(bottle);
        isModify = true;
        initializeExtraProperties();
        panelWineAttribute.setStatus(bottle);
        panelVignobles.initializeVignobles(bottle);

        panelPlace.resetPanel();
        panelPlace.setBeforeObjectLabels(bottle);
        panelSave.setFirstButtonText(getLabel(MAIN_MODIFY));
        placeInModification = bottle.getAbstractPlace();
        panelSave.setEndText(getLabel(ADDVIN_ENTERCHANGES));
        Debug("Set Bottle... Done");
      }
    }.execute();
  }

  /**
   * Fonction pour le chargement de vins pour la classe ListVin.
   */
  void setObjectsInModification(LinkedList<Bouteille> bottles) {
    Debug("setBottlesInModification...");
    severalItems = bottles.size() > 1;
    panelGeneral.setSeveralItems(severalItems);
    listBottleInModification = bottles;

    resetValues();
    if (severalItems) {
      panelGeneral.setViewToSeveralItemsMode(listBottleInModification.size());
      panelWineAttribute.seNbItemsEnabled(false);
      panelSave.enableFirstButton(true);
      panelSave.setEndText(getLabel(ADDVIN_MOVEERROR));
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
    saveObject();
  }

  private Result modifyOrAddObjectsInComplexPlace(AbstractPlace rangement, PlacePosition place, String annee) throws MyCellarException {
    Result result = new Result();
    int countStillToAdd = panelWineAttribute.getNbItems();
    ComplexPlace complexPlace = (ComplexPlace) rangement;
    // Ajout dans une Armoire
    if (severalItems) { //On ne peut pas deplacer plusieurs bouteilles vers une armoire
      Debug("ERROR: Unable to move multiple objects to a Complex place");
      panelSave.resetText();
      String nomRangement = complexPlace.getName();
      Erreur.showSimpleErreur(getError(ERROR_UNABLETOMOVENITEMSIN, nomRangement), getError(ERROR_SELECTSIMPLESTORAGE));
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
    Bouteille myCellarObjectFound = null;
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
    Bouteille newMyCellarObject = createBouteille(annee, new PlacePosition.PlacePositionBuilderZeroBased(complexPlace)
        .withNumPlace(part)
        .withLine(line)
        .withColumn(column)
        .build(), complexPlace);
    if (myCellarObjectFound == null) {
      if (isModify) {
        Debug("Empty case: Modifying bottle");
        final PlacePosition oldPlace = bottle.getPlacePosition();
        bottle.update(newMyCellarObject);
        newMyCellarObject.getAbstractPlace().updateToStock(newMyCellarObject);
        Program.getStorage().addHistory(HistoryState.MODIFY, bottle);
        if (complexPlace.isComplexPlace()) {
          Debug("Deleting from previous complex place");
          oldPlace.getAbstractPlace().clearStorage(bottle, oldPlace);
        }
      } else {
        Debug("Empty case: Adding bottle");
        Program.getStorage().addHistory(HistoryState.ADD, newMyCellarObject);
        complexPlace.addObject(newMyCellarObject);
        if (countStillToAdd > 1 && nb_free_space > 1) { // Add bottles next to each others
          if (nb_free_space > countStillToAdd) {
            nb_free_space = countStillToAdd;
          }
          if (JOptionPane.YES_OPTION == Erreur.showAskConfirmationMessage(getError(ERROR_QUESTIONADDNITEMSNEXT, nb_free_space))) {
            Debug("Putting multiple bottle in chosen place");
            result.setNbItemsAdded(nb_free_space);
            countStillToAdd -= nb_free_space + 1;
            for (int i = 1; i < nb_free_space; i++) {
              newMyCellarObject = createBouteille(annee, new PlacePosition.PlacePositionBuilderZeroBased(complexPlace)
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
          panelSave.enableFirstButton(false);
          panelPlace.enablePlaceSelection(false);
        }
      }
      if (isModify) {
        panelPlace.enablePlace(true);
      }
      result.setAdded(true);
    } else { // La case n'est pas vide
      Debug("WARNING: Not an empty place, Replace?");
      String message = getError(ERROR_ALREADYINSTORAGE, myCellarObjectFound.getNom(), myCellarObjectFound.getAnnee()) + "\n" + getError(ERROR_QUESTIONREPLACEIT);
      if (JOptionPane.YES_OPTION == Erreur.showAskConfirmationMessage(message)) {
        replaceWine(newMyCellarObject, myCellarObjectFound);
        panelSave.setEndText(isModify ? getLabel(ADDVIN_1ITEMMODIFIED) : getLabel(ADDVIN_1ITEMADDED), true);
        result.setAdded(true);
        result.setRequireReset(true);
        resetValues();
      } else {
        panelSave.setEndText(getLabel(ADDVIN_NOTSAVED));
        enableAll(true);
        result.setHasError();
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
      Erreur.showSimpleErreur(ERROR_NOTENOUGHSPACESTORAGE, ERROR_SELECTANOTHERSTORAGE);
      panelSave.resetText();
      Debug("ERROR: No free spaces");
      return result;
    }

    if (severalItems) {
      return modifySeveralObjectsInSimplePlace(place, simplePlace);
    }

    Bouteille newMyCellarObject = createBouteille(annee, place, simplePlace);
    // Add multiple bottle with question
    if (countStillToAdd > 1) {
      if (!Program.hasOnlyOnePlace()) {
        Debug("Adding multiple objects in the same place?");
        String message = getError(ERROR_QUESTIONADDNITEMIN, countStillToAdd, simplePlace.getName());
        if (JOptionPane.YES_OPTION == Erreur.showAskConfirmationMessage(message)) {
          //Add several bottles in Caisse
          Debug("Adding multiple objects in the same place: YES");

          if (simplePlace.isLimited() && (simplePlace.getCountCellUsed(place) + countStillToAdd) > simplePlace.getMaxItemCount()) {
            Erreur.showSimpleErreur(ERROR_NOTENOUGHSPACESTORAGE, ERROR_SELECTANOTHERSTORAGE);
            panelSave.resetText();
          } else {
            result.setNbItemsAdded(countStillToAdd);
            for (int j = 0; j < countStillToAdd; j++) {
              Bouteille copy = new Bouteille(newMyCellarObject);
              Program.getStorage().addHistory(HistoryState.ADD, copy);
              simplePlace.addObject(copy);
            }
            panelSave.setEndText(getLabel(ADDVIN_NITEMADDED, countStillToAdd), true);
            result.setAdded(true);
            result.setRequireReset(true);
          }
        } else {
          Debug("Adding multiple objects in the same place: NO");
          //Add a single bottle in Caisse
          Program.getStorage().addHistory(HistoryState.ADD, newMyCellarObject);
          simplePlace.addObject(newMyCellarObject);
          panelSave.setEndText(getLabel(ADDVIN_1ITEMADDED), true);
          panelWineAttribute.setStillNbItems(countStillToAdd - 1);
        }
      } else { // One simplePlace
        if (simplePlace.isLimited() && (simplePlace.getCountCellUsed(place) + countStillToAdd) > simplePlace.getMaxItemCount()) {
          result.setHasError();
          Debug("ERROR: This caisse is full. Unable to add all bottles in the same place!");
          Erreur.showSimpleErreur(ERROR_NOTENOUGHSPACESTORAGE, ERROR_SELECTANOTHERSTORAGE);
          panelSave.resetText();
        } else {
          Debug("Adding n objects: " + (countStillToAdd - 1));
          for (int i = 0; i < countStillToAdd - 1; i++) {
            Bouteille copy = new Bouteille(newMyCellarObject);
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
        bottle.getAbstractPlace().clearStorage(bottle);
        bottle.update(newMyCellarObject);
        Program.getStorage().addHistory(HistoryState.MODIFY, bottle);
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
        Erreur.showSimpleErreur(getError(ERROR_STORAGEFULL, simplePlace.getName()), getError(ERROR_SELECTANOTHERSTORAGE));
        result.setHasError();
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
    if (simplePlace.isLimited() && (simplePlace.getCountCellUsed(place) + listBottleInModification.size()) > simplePlace.getMaxItemCount()) {
      Debug("ERROR: Not enough place!");
      Erreur.showSimpleErreur(ERROR_NOTENOUGHSPACESTORAGE, ERROR_SELECTANOTHERSTORAGE);
      panelPlace.enableSimplePlace(true);
      panelSave.enableFirstButton(true);
      panelSave.resetText();
      return result;
    }
    boolean bOneBottle = listBottleInModification.size() == 1;
    for (var tmp : listBottleInModification) {
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
      } else if (simplePlace.addObject(tmp)) {
        result.setAdded(true);
        result.setRequireReset(true);
        resetValues();
      }
    }
    return result;
  }

  private Result modifySeveralObjectsWithoutChangingSimplePlace() {
    Result result = new Result();
    Debug("Modifying without changing place");
    boolean oneBottle = listBottleInModification.size() == 1;
    for (var tmp : listBottleInModification) {
      updateMyCellarObject(oneBottle, tmp);
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

  private void updateMyCellarObject(boolean singleObject, Bouteille bouteille) {
    String price = panelWineAttribute.getPrice();
    String comment = commentTextArea.getText();
    String dateOfC = panelWineAttribute.getMaturity();
    String parker = panelWineAttribute.getParker();
    String color = panelWineAttribute.getColor();
    String status = nonNullValueOrDefault(panelWineAttribute.getStatusIfModified(), BottlesStatus.MODIFIED.name());
    CountryJaxb country = panelVignobles.getCountry();
    String vignoble = panelVignobles.getVignoble();
    String aoc = panelVignobles.getAOC();
    String igp = panelVignobles.getIGP();
    String type = panelGeneral.getType();
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
    if (singleObject || !country.getId().isEmpty() || !vignoble.isEmpty() || !aoc.isEmpty() || !igp.isEmpty()) {
      bouteille.setVignoble(new VignobleJaxb(country, vignoble, aoc, igp));
    }
    if (singleObject || panelWineAttribute.getStatusList().isModified()) {
      bouteille.setStatus(status);
    }
    if (singleObject || !type.isEmpty()) {
      bouteille.setKind(type);
    }
  }

  private Result modifyOneOrSeveralObjectsWithoutPlaceModification(String annee) throws MyCellarException {
    Debug("modifyOneOrSeveralObjectsWithoutPlaceModification...");
    if (!severalItems) {
      Result result = modifyOneInComplexPlace(annee);
      result.setRequireReset(true);
      resetValues();
      Debug("modifyOneOrSeveralObjectsWithoutPlaceModification... Done");
      return result;
    }

    Result result = new Result();
    Debug("Modifying multiple bottles in Armoire without changing place");
    final String comment = commentTextArea.isModified() ? commentTextArea.getText() : null;
    for (var bouteille : listBottleInModification) {
      bouteille.setPrix(nonNullValueOrDefault(panelWineAttribute.getPriceIfModified(), bouteille.getPrix()));
      bouteille.setComment(nonNullValueOrDefault(comment, bouteille.getComment()));
      bouteille.setMaturity(nonNullValueOrDefault(panelWineAttribute.getMaturityIfModified(), bouteille.getMaturity()));
      bouteille.setParker(nonNullValueOrDefault(panelWineAttribute.getParkerIfModified(), bouteille.getParker()));
      bouteille.setColor(nonNullValueOrDefault(panelWineAttribute.getColorIfModified(), bouteille.getColor()));
      if (panelVignobles.isModified()) {
        bouteille.setVignoble(new VignobleJaxb(panelVignobles.getCountry(), panelVignobles.getVignoble(), panelVignobles.getAOC(), panelVignobles.getIGP()));
      }

      bouteille.setKind(nonNullValueOrDefault(panelGeneral.getTypeIfModified(), bouteille.getKind()));
      bouteille.updateStatus();
      // Add multiple bottles
      Debug("Adding multiple bottles...");
      AbstractPlace rangement = bouteille.getAbstractPlace();
      if (isModify) {
        //Delete Bouteilles
        Debug("Deleting bottles when modifying");
        rangement.removeObject(bouteille);
      }
      //Ajout des bouteilles dans la caisse
      Debug("Adding bottle...");
      Program.getStorage().addHistory(isModify ? HistoryState.MODIFY : HistoryState.ADD, bouteille);
      //Ajout des bouteilles dans ALL
      if (rangement.addObject(bouteille)) {
        result.setAdded(true);
      }
    }
    result.setRequireReset(true);
    resetValues();
    Debug("modifyOneOrSeveralObjectsWithoutPlaceModification... Done");
    return result;
  }

  private Result modifyOneInComplexPlace(String annee) {
    Debug("Modifying one bottle in Armoire without changing place");
    Result result = new Result();
    Bouteille tmp = createBouteille(annee, bottle.getPlacePosition(), bottle.getAbstractPlace());
    Debug("Replacing bottle...");
    bottle.update(tmp);
    Program.getStorage().addHistory(HistoryState.MODIFY, tmp);
    result.setAdded(true);
    return result;
  }

  private void replaceWine(final Bouteille newMyCellarObject, final Bouteille objectToDelete) throws MyCellarException {
    Debug("ReplaceWine...");
    //Change wine in a place
    Program.getStorage().addHistory(isModify ? HistoryState.MODIFY : HistoryState.ADD, newMyCellarObject);
    PlaceUtils.replaceMyCellarObject(objectToDelete, newMyCellarObject, isModify ? bottle.getPlacePosition() : null);
    if (isModify) {
      bottle.update(newMyCellarObject);
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
    bottle = null;
    panelGeneral.setBottle(null);
    panelPlace.clearModified();
    ProgramPanels.updateCellOrganizerPanel(false);
    ProgramPanels.setPaneModified(selectedPaneIndex, false);
    if (!isModify) {
      Debug("Do After Run... Done");
      return;
    }

    if (listVin == null) {
      enableAll(true);
      isModify = false;
      panelPlace.setBeforeLabelsVisible(false);
      panelSave.setFirstButtonText(getLabel(MAIN_ADD));
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
      int selectedIndex = ProgramPanels.getSelectedTabIndex();
      ProgramPanels.setTitleAt(selectedIndex, getLabel(MAIN_TABADD));
      ProgramPanels.setPaneModified(selectedIndex, false);
      panelSave.resetText();
    }
    panelGeneral.setSeveralItems(false);
    panelPlace.resetValues();
    panelPlace.clearModified();
    enableAll(true);
    isModify = false;
    panelPlace.setBeforeLabelsVisible(false);
    panelSave.setFirstButtonText(getLabel(MAIN_ADD));
  }

  private boolean runExit() {
    Debug("runExit...");
    panelSave.enableFirstButton(false);
    //Verification qu'il n'y a pas de bouteilles en modif ou creation
    if (panelGeneral.askForQuit(isModify)) {
      panelSave.enableFirstButton(true);
      return false;
    }

    Debug("Quitting...");
    if (!PlaceUtils.putTabStock()) {
      OpenShowErrorsAction.open();
    }
    panelWineAttribute.runExit();
    panelPlace.resetValues();
    panelPlace.clearModified();
    Program.putCaveConfigBool(MyCellarSettings.KEEP_VINEYARD, panelVignobles.isKeepPreviousVineyardSelected());
    clearValues();
    reInitAddVin();
    Debug("runExit... Done");
    return true;
  }

  public void reInit() {
    Debug("ReInit...");
    bottle = null;
    panelGeneral.setBottle(null);
    listBottleInModification = null;
    reInitAddVin();
    Debug("ReInit... Done");
  }

  public boolean save() {
    if (panelGeneral.askForSave(isModify)) {
      return saveObject();
    }
    return true;
  }

  private boolean saveObject() {
    Debug("Running...");
    try {
      // Check Name / Year / Place / Part
      if (!performValidations()) {
        panelSave.resetText();
        enableAll(true);
        Debug("ERROR: Validations failed");
        return false;
      }

      Debug("Adding / Modifying...");
      if (isModify) {
        //On grise les champs en cours de modif
        Debug("Modifying in Progress...");
        panelSave.setEndText(getLabel(ADDVIN_MODIFYINPROGRESS));
        enableAll(false);
      }

      PlacePosition place = panelPlace.getSelectedPlacePosition();
      AbstractPlace abstractPlace = place.getAbstractPlace();
      Objects.requireNonNull(abstractPlace);
      if (!place.hasPlace() && isModify) {
        //Si aucun emplacement n'a ete selectionne (modif du nom)
        place = bottle.getPlacePosition();
        if (placeInModification != null) {
          abstractPlace = placeInModification;
        }
      }

      String annee = panelGeneral.updateYear(); // Keep it here before it become not editable
      panelSave.setEndText(getLabel(ADDVIN_ADDINGINPROGRESS));
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
            panelSave.setEndText(getLabel(ADDVIN_1ITEMMODIFIED), true);
          } else {
            panelSave.setEndText(getLabel(ADDVIN_NITEMMODIFIED, listBottleInModification.size()));
          }
        } else {
          if (result.getNbItemsAdded() == 0) {
            panelSave.setEndText(getLabel(ADDVIN_1ITEMADDED), true);
          } else {
            panelSave.setEndText(getLabel(ADDVIN_NITEMADDED, result.getNbItemsAdded()));
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
    return true;
  }

  private static class Result {
    private boolean added = false;
    private boolean requireReset = false;
    private boolean hasError = false;
    private int nbItemsAdded;

    private boolean isAdded() {
      return added;
    }

    private void setAdded(boolean added) {
      this.added = added;
    }

    private boolean isRequireReset() {
      return requireReset;
    }

    private void setRequireReset(boolean requireReset) {
      this.requireReset = requireReset;
    }

    private boolean isHasError() {
      return hasError;
    }

    private void setHasError() {
      hasError = true;
    }

    private void setNbItemsAdded(int nbItemsAdded) {
      this.nbItemsAdded = nbItemsAdded;
    }

    private int getNbItemsAdded() {
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
    return runExit();
  }

  class AddAction extends AbstractAction {

    private AddAction() {
      super("", MyCellarImage.ADD);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      new Thread(instance).start();
    }
  }

  class CancelAction extends AbstractAction {

    private CancelAction() {
      super("", MyCellarImage.DELETE);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      if (runExit()) {
        MainFrame.getInstance().removeCurrentTab();
      }
    }
  }
}
