package mycellar;

import mycellar.actions.OpenShowErrorsAction;
import mycellar.core.BottlesStatus;
import mycellar.core.IUpdatable;
import mycellar.core.MyCellarManageBottles;
import mycellar.core.MyCellarObject;
import mycellar.core.MyCellarSettings;
import mycellar.core.UpdateViewType;
import mycellar.core.datas.history.HistoryState;
import mycellar.core.datas.jaxb.VignobleJaxb;
import mycellar.core.exceptions.MyCellarException;
import mycellar.core.text.LabelProperty;
import mycellar.core.uicomponents.MyCellarButton;
import mycellar.core.uicomponents.PopupListener;
import mycellar.core.uicomponents.TabEvent;
import mycellar.frame.MainFrame;
import mycellar.general.ProgramPanels;
import mycellar.placesmanagement.places.AbstractPlace;
import mycellar.placesmanagement.places.ComplexPlace;
import mycellar.placesmanagement.places.PlacePosition;
import mycellar.placesmanagement.places.PlaceUtils;
import mycellar.vignobles.CountryVignobleController;

import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.Color;

import static mycellar.MyCellarUtils.nonNullValueOrDefault;
import static mycellar.ProgramConstants.SPACE;
import static mycellar.core.text.LabelProperty.OF_THE_SINGLE;
import static mycellar.core.text.MyCellarLabelManagement.getError;
import static mycellar.core.text.MyCellarLabelManagement.getErrorWithProperty;
import static mycellar.core.text.MyCellarLabelManagement.getLabel;
import static mycellar.core.text.MyCellarLabelManagement.getLabelWithProperty;
import static mycellar.general.ResourceErrorKey.ERROR_ALREADYINSTORAGE;
import static mycellar.general.ResourceErrorKey.ERROR_CONFIRMQUIT;
import static mycellar.general.ResourceErrorKey.ERROR_MODIFICATIONINCOMPLETED;
import static mycellar.general.ResourceErrorKey.ERROR_QUESTIONREPLACEIT;
import static mycellar.general.ResourceKey.ADDVIN_1ITEMADDED;
import static mycellar.general.ResourceKey.ADDVIN_1ITEMMODIFIED;
import static mycellar.general.ResourceKey.ADDVIN_ENTERCHANGES;
import static mycellar.general.ResourceKey.MAIN_ASKCONFIRMATION;
import static mycellar.general.ResourceKey.MANAGEBOTTLE_SAVEEXITMODIFICATIONS;
import static mycellar.general.ResourceKey.MANAGEBOTTLE_SAVEMODIFICATIONS;


/**
 * Titre : Cave &agrave; vin
 * Description : Votre description
 * Copyright : Copyright (c) 2005
 * Soci&eacute;t&eacute; : Seb Informatique
 *
 * @author S&eacute;bastien Duch&eacute;
 * @version 10.7
 * @since 14/03/25
 */
public final class ManageBottle extends MyCellarManageBottles implements Runnable, ITabListener, IUpdatable {
  private boolean saveAndExit;

  /**
   * Constructeur pour la modification de vins
   */
  public ManageBottle(MyCellarObject bottle) {
    super();
    isEditionMode = true;
    addButton = new MyCellarButton(MyCellarImage.SAVE);
    cancelButton = new MyCellarButton(MyCellarImage.SAVE);

    try {
      Debug("Constructor with Bottle");
      panelGeneral.initializeForEdition();
      panelWineAttribute.initValues();

      addButton.setText(getLabel(MANAGEBOTTLE_SAVEMODIFICATIONS));
      cancelButton.setText(getLabel(MANAGEBOTTLE_SAVEEXITMODIFICATIONS));
      addButton.setMnemonic(ajouterChar);

      PopupListener popupListener = new PopupListener();
      panelGeneral.setMouseListener(popupListener);
      panelWineAttribute.setMouseListener(popupListener);
      commentTextArea.addMouseListener(popupListener);

      end.setForeground(Color.red);
      end.setHorizontalAlignment(SwingConstants.CENTER);
      setLayout(new BorderLayout());
      add(new PanelMain(), BorderLayout.CENTER);

      addButton.addActionListener((e) -> saving());
      cancelButton.addActionListener((e) -> savingExit());

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

  public MyCellarObject getMyCellarObject() {
    return myCellarObject;
  }

  /**
   * Fonction de chargement d'un vin
   */
  private void setBottle(MyCellarObject cellarObject) {
    Debug("Set Bottle...");
    try {
      myCellarObject = cellarObject;
      panelGeneral.setMyCellarObject(cellarObject);
      initializeExtraProperties();
      if (Program.isWineType()) {
        panelVignobles.initializeVignobles((Bouteille) cellarObject);
      }
      initStatusAndTime();

      panelPlace.selectPlace(cellarObject.getPlacePosition());
      end.setText(getLabel(ADDVIN_ENTERCHANGES));
      resetModified();
    } catch (RuntimeException e) {
      Program.showException(e);
    }
    Debug("Set Bottle... Done");
  }

  private void updateStatusAndTime() {
    panelWineAttribute.updateStatusAndTime(myCellarObject);
  }

  private void initStatusAndTime() {
    panelWineAttribute.initStatusAndTime(myCellarObject);
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
        ProgramPanels.removeObjectTab(myCellarObject);
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

    PlacePosition oldPlace = myCellarObject.getPlacePosition();
    if (isCaisse) {
      lieu_num = place.getPart();
      myCellarObject.setNumLieu(lieu_num);
      myCellarObject.setLigne(0);
      myCellarObject.setColonne(0);
    } else {
      myCellarObject.setNumLieu(lieu_num);
      myCellarObject.setLigne(line);
      myCellarObject.setColonne(column);
      MyCellarObject bottleInPlace = ((ComplexPlace) cave).getObject(new PlacePosition.PlacePositionBuilder(cave)
          .withNumPlace(lieu_num)
          .withLine(line)
          .withColumn(column)
          .build()).orElse(null);
      if (bottleInPlace != null) {
        if (!askToReplaceBottle(bottleInPlace, oldPlace)) {
          myCellarObject.setNumLieu(oldPlace.getPart());
          myCellarObject.setLigne(oldPlace.getLine());
          myCellarObject.setColonne(oldPlace.getColumn());
          return false;
        }
      }
    }
    myCellarObject.setAnnee(panelGeneral.getYear());
    if (Program.isWineType()) {
      Bouteille bTemp = (Bouteille) myCellarObject;
      bTemp.setColor(color);
      bTemp.setComment(comment1);
      bTemp.setMaturity(dateOfC);
      bTemp.setParker(parker);
      bTemp.setPrix(prix);
      bTemp.setVignoble(new VignobleJaxb(country, vignoble, aoc, igp));
      CountryVignobleController.addVignobleFromBottle(bTemp);
      CountryVignobleController.setRebuildNeeded();
    }
    myCellarObject.setEmplacement(cave.getName());
    myCellarObject.setNom(nom);
    myCellarObject.setKind(demie);
    myCellarObject.setStatus(status);

    myCellarObject.setModified();
    Program.getStorage().addHistory(HistoryState.MODIFY, myCellarObject);

    if (oldPlace.isComplexPlace()) {
      oldPlace.getAbstractPlace().clearStorage(myCellarObject, oldPlace);
    }

    if (!PlaceUtils.putTabStock()) {
      new OpenShowErrorsAction().actionPerformed(null);
    }
    ProgramPanels.updateSearchTable();

    AbstractPlace rangement = myCellarObject.getAbstractPlace();
    if (!rangement.isSimplePlace()) {
      rangement.updateToStock(myCellarObject);
    }

    Program.putCaveConfigBool(MyCellarSettings.KEEP_VINEYARD, panelVignobles.isKeepPreviousVineyardSelected());
    end.setText(getLabelWithProperty(ADDVIN_1ITEMMODIFIED, LabelProperty.SINGLE), true);
    ProgramPanels.updatePanelsWithoutBottles();
    panelWineAttribute.setModificationDetectionActive(false);
    updateStatusAndTime();
    resetModified();
    panelWineAttribute.setModificationDetectionActive(true);
    ProgramPanels.setSelectedPaneModified(false);
    Debug("Saving... Done");

    return true;
  }

  private boolean askToReplaceBottle(MyCellarObject bouteille, PlacePosition oldPlace) throws MyCellarException {
    if (!bouteille.equals(myCellarObject)) {
      Debug("ERROR: Not an empty place, Replace?");
      String message = getError(ERROR_ALREADYINSTORAGE, bouteille.getNom(), bouteille.getAnnee()) + "\n" + getError(ERROR_QUESTIONREPLACEIT);
      if (JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(MainFrame.getInstance(), message , getLabel(MAIN_ASKCONFIRMATION), JOptionPane.YES_NO_OPTION)) {
        replaceWine(bouteille, oldPlace);
        panelPlace.clearLabelEnd();
        end.setText(getLabelWithProperty(ADDVIN_1ITEMADDED, LabelProperty.SINGLE));
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

  private void replaceWine(final MyCellarObject bToDelete, PlacePosition oldPlace) throws MyCellarException {
    //Change wine in a place
    Program.getStorage().addHistory(HistoryState.MODIFY, myCellarObject);
    PlaceUtils.replaceMyCellarObject(bToDelete, myCellarObject, oldPlace);
  }

  private boolean runExit() {
    Debug("Processing Quit...");
    addButton.setEnabled(false);
    cancelButton.setEnabled(false);

    boolean modified = panelGeneral.isModified(myCellarObject);
    modified |= commentTextArea.isModified();
    modified |= panelWineAttribute.isModified();
    modified |= panelPlace.isModified();
    modified |= panelVignobles.isModified();

    if (modified && JOptionPane.NO_OPTION == JOptionPane.showConfirmDialog(MainFrame.getInstance(), getErrorWithProperty(ERROR_MODIFICATIONINCOMPLETED, OF_THE_SINGLE) + SPACE + getError(ERROR_CONFIRMQUIT), getLabel(MAIN_ASKCONFIRMATION), JOptionPane.YES_NO_OPTION)) {
      Debug("Don't Quit.");
      addButton.setEnabled(true);
      cancelButton.setEnabled(true);
      return false;
    }

    Debug("Quitting...");
    if (!PlaceUtils.putTabStock()) {
      new OpenShowErrorsAction().actionPerformed(null);
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
      panelPlace.selectPlace(myCellarObject.getPlacePosition());
      panelPlace.setListenersEnabled(true);
      Debug("updateView Done");
    });
  }
}
