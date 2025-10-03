package mycellar;

import mycellar.actions.OpenShowErrorsAction;
import mycellar.core.BottlesStatus;
import mycellar.core.IUpdatable;
import mycellar.core.MyCellarManageBottles;
import mycellar.core.MyCellarSettings;
import mycellar.core.UpdateViewType;
import mycellar.core.datas.history.HistoryState;
import mycellar.core.datas.jaxb.VignobleJaxb;
import mycellar.core.exceptions.MyCellarException;
import mycellar.core.uicomponents.PopupListener;
import mycellar.core.uicomponents.TabEvent;
import mycellar.general.ProgramPanels;
import mycellar.placesmanagement.places.AbstractPlace;
import mycellar.placesmanagement.places.ComplexPlace;
import mycellar.placesmanagement.places.PlacePosition;
import mycellar.placesmanagement.places.PlaceUtils;
import mycellar.vignobles.CountryVignobleController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

import static mycellar.MyCellarUtils.nonNullValueOrDefault;
import static mycellar.core.text.MyCellarLabelManagement.getError;
import static mycellar.core.text.MyCellarLabelManagement.getLabel;
import static mycellar.general.ResourceErrorKey.ERROR_ALREADYINSTORAGE;
import static mycellar.general.ResourceErrorKey.ERROR_CONFIRMQUIT;
import static mycellar.general.ResourceErrorKey.ERROR_MODIFICATIONINCOMPLETED;
import static mycellar.general.ResourceErrorKey.ERROR_QUESTIONREPLACEIT;
import static mycellar.general.ResourceKey.ADDVIN_1ITEMADDED;
import static mycellar.general.ResourceKey.ADDVIN_1ITEMMODIFIED;
import static mycellar.general.ResourceKey.ADDVIN_ENTERCHANGES;
import static mycellar.general.ResourceKey.MANAGEBOTTLE_SAVEEXITMODIFICATIONS;
import static mycellar.general.ResourceKey.MANAGEBOTTLE_SAVEMODIFICATIONS;


/**
 * Titre : Cave &agrave; vin
 * Description : Votre description
 * Copyright : Copyright (c) 2005
 * Soci&eacute;t&eacute; : Seb Informatique
 *
 * @author S&eacute;bastien Duch&eacute;
 * @version 11.3
 * @since 03/10/25
 */
public final class ManageBottle extends MyCellarManageBottles implements Runnable, ITabListener, IUpdatable {
  private boolean saveAndExit;

  /**
   * Constructeur pour la modification de vins
   */
  public ManageBottle(Bouteille bottle) {
    super();
    isEditionMode = true;
    panelSave.initializeFirstButton(MANAGEBOTTLE_SAVEMODIFICATIONS, new SavingAction());
    panelSave.initializeSecondButton(MANAGEBOTTLE_SAVEEXITMODIFICATIONS, new SavingExitAction());

    try {
      Debug("Constructor with Bottle");
      panelGeneral.initializeForEdition();
      panelWineAttribute.initValues();

      panelSave.setFirstButtonMnemonic(ajouterChar);

      PopupListener popupListener = new PopupListener();
      panelGeneral.setMouseListener(popupListener);
      panelWineAttribute.setMouseListener(popupListener);
      commentTextArea.addMouseListener(popupListener);

      setLayout(new BorderLayout());
      add(new PanelMain(), BorderLayout.CENTER);

      setVisible(true);
      Debug("Constructor Done");

      setBottle(bottle);
    } catch (RuntimeException e) {
      Program.showException(e);
    }
  }

  protected static void Debug(String sText) {
    Program.Debug("ManageBottle: " + sText);
  }

  public Bouteille getBottle() {
    return bottle;
  }

  /**
   * Fonction de chargement d'un vin
   */
  private void setBottle(Bouteille bouteille) {
    Debug("Set Bottle...");
    try {
      bottle = bouteille;
      panelGeneral.setBottle(bottle);
      initializeExtraProperties();
      panelVignobles.initializeVignobles(bottle);
      initStatusAndTime();

      panelPlace.selectPlace(bottle.getPlacePosition());
      panelSave.setEndText(getLabel(ADDVIN_ENTERCHANGES));
      resetModified();
    } catch (RuntimeException e) {
      Program.showException(e);
    }
    Debug("Set Bottle... Done");
  }

  private void updateStatusAndTime() {
    panelWineAttribute.updateStatusAndTime(bottle);
  }

  private void initStatusAndTime() {
    panelWineAttribute.initStatusAndTime(bottle);
  }

  private void saving() {
    saveAndExit = false;
    new Thread(this).start();
  }

  private void savingExit() {
    saveAndExit = true;
    new Thread(this).start();
  }

  @Override
  public void run() {
    try {
      boolean result = save();
      if (result && saveAndExit) {
        ProgramPanels.removeObjectTab(bottle);
      }
    } catch (MyCellarException e) {
      Program.showException(e);
    }
  }

  public boolean save() throws MyCellarException {
    Debug("Saving...");

    String nom = panelGeneral.getObjectName();
    String demie = panelGeneral.getType();

    String prix = panelWineAttribute.getPrice();
    String comment1 = commentTextArea.getText();
    String dateOfC = panelWineAttribute.getMaturity();
    String parker = panelWineAttribute.getParker();
    String color = panelWineAttribute.getColor();
    String status = nonNullValueOrDefault(panelWineAttribute.getStatusIfModified(), BottlesStatus.MODIFIED.name());
    String country = panelVignobles.getCountry();
    String vignoble = panelVignobles.getVignoble();
    String aoc = panelVignobles.getAOC();
    String igp = panelVignobles.getIGP();

    if (!panelGeneral.performValidation()) {
      return false;
    }

    final PlacePosition place = panelPlace.getSelectedPlacePosition();

    if (MyCellarControl.hasInvalidPlace(place)) {
      return false;
    }

    int lieu_num = place.getPart();
    AbstractPlace cave = place.getAbstractPlace();
    boolean isCaisse = cave.isSimplePlace();

    if (MyCellarControl.hasInvalidNumLieuNumber(lieu_num, isCaisse)) {
      return false;
    }

    int line = 0;
    int column = 0;
    if (!isCaisse) {
      line = place.getLine();
      if (MyCellarControl.hasInvalidLineNumber(line)) {
        return false;
      }
      column = place.getColumn();
      if (MyCellarControl.hasInvalidColumnNumber(column)) {
        return false;
      }
    }

    PlacePosition oldPlace = bottle.getPlacePosition();
    if (isCaisse) {
      lieu_num = place.getPart();
      bottle.setNumLieu(lieu_num);
      bottle.setLigne(0);
      bottle.setColonne(0);
    } else {
      bottle.setNumLieu(lieu_num);
      bottle.setLigne(line);
      bottle.setColonne(column);
      Bouteille bottleInPlace = ((ComplexPlace) cave).getObject(new PlacePosition.PlacePositionBuilder(cave)
          .withNumPlace(lieu_num)
          .withLine(line)
          .withColumn(column)
          .build()).orElse(null);
      if (bottleInPlace != null) {
        if (!askToReplaceBottle(bottleInPlace, oldPlace)) {
          bottle.setNumLieu(oldPlace.getPart());
          bottle.setLigne(oldPlace.getLine());
          bottle.setColonne(oldPlace.getColumn());
          return false;
        }
      }
    }
    bottle.setAnnee(panelGeneral.getYear());
    bottle.setColor(color);
    bottle.setComment(comment1);
    bottle.setMaturity(dateOfC);
    bottle.setParker(parker);
    bottle.setPrix(prix);
    bottle.setVignoble(new VignobleJaxb(country, vignoble, aoc, igp));
    CountryVignobleController.addVignobleFromBottle(bottle);
    CountryVignobleController.setRebuildNeeded();
    bottle.setEmplacement(cave.getName());
    bottle.setNom(nom);
    bottle.setKind(demie);
    bottle.setStatus(status);

    bottle.setModified();
    Program.getStorage().addHistory(HistoryState.MODIFY, bottle);

    if (oldPlace.isComplexPlace()) {
      oldPlace.getAbstractPlace().clearStorage(bottle, oldPlace);
    }

    if (!PlaceUtils.putTabStock()) {
      OpenShowErrorsAction.open();
    }
    ProgramPanels.updateSearchTable();

    AbstractPlace rangement = bottle.getAbstractPlace();
    if (!rangement.isSimplePlace()) {
      rangement.updateToStock(bottle);
    }

    Program.putCaveConfigBool(MyCellarSettings.KEEP_VINEYARD, panelVignobles.isKeepPreviousVineyardSelected());
    panelSave.setEndText(getLabel(ADDVIN_1ITEMMODIFIED), true);
    ProgramPanels.updatePanelsWithoutBottles();
    panelWineAttribute.setModificationDetectionActive(false);
    updateStatusAndTime();
    resetModified();
    panelWineAttribute.setModificationDetectionActive(true);
    ProgramPanels.setSelectedPaneModified(false);
    Debug("Saving... Done");

    return true;
  }

  private boolean askToReplaceBottle(Bouteille bouteille, PlacePosition oldPlace) throws MyCellarException {
    if (!bouteille.equals(bottle)) {
      Debug("ERROR: Not an empty place, Replace?");
      String message = String.format("%s\n%s", getError(ERROR_ALREADYINSTORAGE, bouteille.getNom(), bouteille.getAnnee()), getError(ERROR_QUESTIONREPLACEIT));
      if (JOptionPane.YES_OPTION == Erreur.showAskConfirmationMessage(message)) {
        replaceWine(bouteille, oldPlace);
        panelPlace.clearLabelEnd();
        panelSave.setEndText(getLabel(ADDVIN_1ITEMADDED));
      } else {
        return false;
      }
    }
    return true;
  }

  private void resetModified() {
    panelGeneral.resetModified(false);
    panelWineAttribute.resetModified(false);
    commentTextArea.setModified(false);
    panelVignobles.setModified(false);
    panelPlace.clearModified();
  }

  private void replaceWine(final Bouteille bToDelete, PlacePosition oldPlace) throws MyCellarException {
    //Change wine in a place
    Program.getStorage().addHistory(HistoryState.MODIFY, bottle);
    PlaceUtils.replaceMyCellarObject(bToDelete, bottle, oldPlace);
  }

  private boolean runExit() {
    Debug("Processing Quit...");
    panelSave.enableFirstButton(false);
    panelSave.enableSecondButton(false);

    boolean modified = panelGeneral.isModified(bottle);
    modified |= commentTextArea.isModified();
    modified |= panelWineAttribute.isModified();
    modified |= panelPlace.isModified();
    modified |= panelVignobles.isModified();

    String message = String.format("%s %s", getError(ERROR_MODIFICATIONINCOMPLETED), getError(ERROR_CONFIRMQUIT));
    if (modified && JOptionPane.NO_OPTION == Erreur.showAskConfirmationMessage(message)) {
      Debug("Don't Quit.");
      panelSave.enableFirstButton(true);
      panelSave.enableSecondButton(true);
      return false;
    }

    Debug("Quitting...");
    if (!PlaceUtils.putTabStock()) {
      OpenShowErrorsAction.open();
    }
    panelWineAttribute.runExit();
    clearValues();
    Debug("Quitting... Done");
    return true;
  }

  @Override
  public boolean tabWillClose(TabEvent event) {
    return runExit();
  }

  @Override
  public void updateView() {
    if (!updateView) {
      return;
    }
    SwingUtilities.invokeLater(() -> {
      Debug("updateView...");
      panelPlace.setListenersEnabled(false);
      updateView = false;
      if (updateViewType == UpdateViewType.CAPACITY || updateViewType == UpdateViewType.ALL) {
        panelGeneral.updateView();
      }
      if (updateViewType == UpdateViewType.VINEYARD || updateViewType == UpdateViewType.ALL) {
        panelVignobles.updateList();
      }
      if (updateViewType == UpdateViewType.PLACE || updateViewType == UpdateViewType.ALL) {
        panelPlace.updateView();
      }
      panelPlace.selectPlace(bottle.getPlacePosition());
      panelPlace.setListenersEnabled(true);
      Debug("updateView Done");
    });
  }

  class SavingAction extends AbstractAction {
    private SavingAction() {
      super("", MyCellarImage.SAVE);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      saving();
    }
  }

  class SavingExitAction extends AbstractAction {
    private SavingExitAction() {
      super("", MyCellarImage.SAVE);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      savingExit();
    }
  }
}
