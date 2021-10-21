package mycellar;

import mycellar.actions.OpenShowErrorsAction;
import mycellar.core.BottlesStatus;
import mycellar.core.IUpdatable;
import mycellar.core.LabelProperty;
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
import mycellar.vignobles.CountryVignobleController;

import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.Color;
import java.text.MessageFormat;

import static mycellar.MyCellarUtils.nonNullValueOrDefault;
import static mycellar.core.LabelProperty.OF_THE_SINGLE;


/**
 * <p>Titre : Cave &agrave; vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2005</p>
 * <p>Soci&eacute;t&eacute; : Seb Informatique</p>
 *
 * @author S&eacute;bastien Duch&eacute;
 * @version 8.9
 * @since 21/10/21
 */
public final class ManageBottle extends MyCellarManageBottles implements Runnable, ITabListener, IUpdatable {
  private static final long serialVersionUID = 5330256984954964913L;

  /**
   * Constructeur pour la modification de vins
   *
   * @param bottle
   */
  public ManageBottle(MyCellarObject bottle) {
    super();
    isEditionMode = true;
    addButton = new MyCellarButton(MyCellarImage.SAVE);

    try {
      Debug("Constructor with Bottle");
      panelGeneral.initializeForEdition();
      panelWineAttribute.initValues();

      addButton.setText(Program.getLabel("ManageBottle.SaveModifications"));
      addButton.setMnemonic(ajouterChar);

      PopupListener popupListener = new PopupListener();
      panelGeneral.setMouseListener(popupListener);
      panelWineAttribute.setMouseListener(popupListener);
      commentTextArea.addMouseListener(popupListener);

      end.setForeground(Color.red);
      end.setHorizontalAlignment(SwingConstants.CENTER);
      setLayout(new BorderLayout());
      add(new PanelMain(false), BorderLayout.CENTER);

      addButton.addActionListener((e) -> saving());

      setVisible(true);
      Debug("JbInit Done");

      setBottle(bottle);
    } catch (RuntimeException e) {
      Program.showException(e);
    }
  }

  protected static void Debug(String sText) {
    Program.Debug("ManageBottle: " + sText);
  }

  public MyCellarObject getBottle() {
    return myCellarObject;
  }

  /**
   * Fonction de chargement d'un vin
   *
   * @param cellarObject
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
      updateStatusAndTime();

      panelPlace.selectPlace(cellarObject);
      end.setText(Program.getLabel("Infos092")); //"Saisir les modifications
      resetModified();
    } catch (RuntimeException e) {
      Program.showException(e);
    }
    Debug("Set Bottle... Done");
  }

  private void updateStatusAndTime() {
    panelWineAttribute.updateStatusAndTime(myCellarObject);
  }

  /**
   * saving: Fonction de sauvegarde
   */
  private void saving() {
    new Thread(this).start();
  }

  @Override
  public void run() {
    try {
      save();
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
    String status = nonNullValueOrDefault(panelWineAttribute.getStatusIfModified(),  BottlesStatus.MODIFIED.name());
    String country = panelVignobles.getCountry();
    String vignoble = panelVignobles.getVignoble();
    String aoc = panelVignobles.getAOC();
    String igp = panelVignobles.getIGP();

    if (!panelGeneral.performValidation()) {
      return false;
    }

    final Place place = panelPlace.getSelectedPlace();

    if (MyCellarControl.hasInvalidPlace(place)) {
      return false;
    }

    int lieu_num = place.getPlaceNum();
    Rangement cave = place.getRangement();
    boolean isCaisse = cave.isCaisse();

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

    Place oldPlace = myCellarObject.getPlace();
    if (isCaisse) {
      lieu_num = place.getPlaceNum();
      myCellarObject.setNumLieu(lieu_num);
      myCellarObject.setLigne(0);
      myCellarObject.setColonne(0);
    } else {
      myCellarObject.setNumLieu(lieu_num);
      myCellarObject.setLigne(line);
      myCellarObject.setColonne(column);
      MyCellarObject bottleInPlace = cave.getBouteille(new Bouteille.BouteilleBuilder("").numPlace(lieu_num).line(line).column(column).build()).orElse(null);
      if (bottleInPlace != null) {
        if (!askToReplaceBottle(bottleInPlace, oldPlace)) {
          myCellarObject.setNumLieu(oldPlace.getPlaceNum());
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
    myCellarObject.setEmplacement(cave.getNom());
    myCellarObject.setNom(nom);
    myCellarObject.setKind(demie);
    myCellarObject.setStatus(status);

    myCellarObject.setModified();
    Program.getStorage().addHistory(HistoryState.MODIFY, myCellarObject);

    if (!oldPlace.isSimplePlace()) {
      oldPlace.getRangement().clearComplexStock(oldPlace);
    }

    if (!RangementUtils.putTabStock()) {
      new OpenShowErrorsAction().actionPerformed(null);
    }
    ProgramPanels.getSearch().ifPresent(Search::updateTable);

    Rangement rangement = myCellarObject.getRangement();
    if (!rangement.isCaisse()) {
      rangement.updateToStock(myCellarObject);
    }

    end.setText(Program.getLabel("AddVin.1ItemModified", LabelProperty.SINGLE), true);
    ProgramPanels.updatePanelsWithoutBottles();
    updateStatusAndTime();
    resetModified();
    Debug("Saving... Done");

    return true;
  }

  private boolean askToReplaceBottle(MyCellarObject bouteille, Place oldPlace) throws MyCellarException {
    if (!bouteille.equals(myCellarObject)) {
      Debug("ERROR: Not an empty place, Replace?");
      String erreur_txt1 = MessageFormat.format(Program.getError("Error059"), bouteille.getNom(), bouteille.getAnnee());
      String erreur_txt2 = Program.getError("Error060"); //"Voulez vous le remplacer?
      if (JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(Start.getInstance(), erreur_txt1 + "\n" + erreur_txt2, Program.getLabel("Infos049"), JOptionPane.YES_NO_OPTION)) {
        replaceWine(bouteille, oldPlace);
        end.setText(Program.getLabel("AddVin.1ItemAdded", LabelProperty.SINGLE));
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
    Start.setPaneModified(false);
  }

  private void replaceWine(final MyCellarObject bToDelete, Place oldPlace) throws MyCellarException {
    //Change wine in a place
    Program.getStorage().addHistory(HistoryState.MODIFY, myCellarObject);
    RangementUtils.replaceMyCellarObject(bToDelete, myCellarObject, oldPlace);
  }

  private boolean runExit() {
    Debug("Processing Quit...");
    addButton.setEnabled(false);

    boolean modified = panelGeneral.isModified(myCellarObject);
    modified |= commentTextArea.isModified();
    modified |= panelWineAttribute.isModified();
    modified |= panelPlace.isModified();
    modified |= panelVignobles.isModified();

    if (modified && JOptionPane.NO_OPTION == JOptionPane.showConfirmDialog(Start.getInstance(), Program.getError("Error148", OF_THE_SINGLE) + " " + Program.getError("Error145"), Program.getLabel("Infos049"), JOptionPane.YES_NO_OPTION)) {
      Debug("Don't Quit.");
      addButton.setEnabled(true);
      return false;
    }

    Debug("Quitting...");
    if (!RangementUtils.putTabStock()) {
      new OpenShowErrorsAction().actionPerformed(null);
    }
    panelWineAttribute.runExit();
    clearValues();
    Debug("Quitting... End");
    return true;
  }

  @Override
  public boolean tabWillClose(TabEvent event) {
    return runExit();
  }

  @Override
  public void tabClosed() {
    Start.getInstance().updateMainPanel();
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
      panelGeneral.updateView();
      panelVignobles.updateList();
      panelPlace.updateView();
      panelPlace.selectPlace(myCellarObject);
      panelPlace.setListenersEnabled(true);
      Debug("updateView End");
    });
  }
}
