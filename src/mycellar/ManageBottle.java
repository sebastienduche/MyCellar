package mycellar;

import mycellar.actions.OpenShowErrorsAction;
import mycellar.core.IUpdatable;
import mycellar.core.LabelProperty;
import mycellar.core.MyCellarButton;
import mycellar.core.MyCellarException;
import mycellar.core.MyCellarManageBottles;
import mycellar.core.MyCellarObject;
import mycellar.core.PanelVignobles;
import mycellar.core.PopupListener;
import mycellar.core.TabEvent;
import mycellar.core.datas.history.HistoryState;
import mycellar.core.datas.jaxb.VignobleJaxb;
import mycellar.general.ProgramPanels;
import mycellar.placesmanagement.Place;
import mycellar.placesmanagement.Rangement;
import mycellar.placesmanagement.RangementUtils;
import mycellar.vignobles.CountryVignobleController;
import net.miginfocom.swing.MigLayout;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.Color;
import java.text.MessageFormat;
import java.util.Optional;

import static mycellar.core.LabelProperty.OF_THE_SINGLE;


/**
 * <p>Titre : Cave &agrave; vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2005</p>
 * <p>Soci&eacute;t&eacute; : Seb Informatique</p>
 *
 * @author S&eacute;bastien Duch&eacute;
 * @version 8.5
 * @since 23/08/21
 */
public final class ManageBottle extends MyCellarManageBottles implements Runnable, ITabListener, IUpdatable {
  private static final long serialVersionUID = 5330256984954964913L;


  /**
   * ManageBottle: Constructeur pour la modification de vins
   *
   * @param bottle
   */
  public ManageBottle(Bouteille bottle) {
    super();
    isEditionMode = true;
    addButton = new MyCellarButton(MyCellarImage.SAVE);

    try {
      Debug("Constructor with Bottle");
      panelGeneral.initializeForEdition();
      panelWineAttribute.initValues();

      addButton.setText(Program.getLabel("ManageBottle.SaveModifications"));
      addButton.setMnemonic(ajouterChar);

      PopupListener popup_l = new PopupListener();
      panelGeneral.setMouseListener(popup_l);
      panelWineAttribute.setMouseListener(popup_l);
      comment.addMouseListener(popup_l);

      end.setForeground(Color.red);
      end.setHorizontalAlignment(SwingConstants.CENTER);
      setLayout(new BorderLayout());
      add(new PanelMain(), BorderLayout.CENTER);

      addButton.addActionListener((e) -> saving());

      setVisible(true);
      Debug("JbInit Done");

      setBottle(bottle);
    } catch (RuntimeException e) {
      Program.showException(e);
    }
  }

  /**
   * Debug
   *
   * @param sText String
   */
  protected static void Debug(String sText) {
    Program.Debug("ManageBottle: " + sText);
  }

  public MyCellarObject getBottle() {
    return bottle;
  }

  /**
   * setBottle: Fonction de chargement d'un vin
   *
   * @param bottle Bouteille
   */
  private void setBottle(MyCellarObject bottle) {
    Debug("Set Bottle...");
    try {
      this.bottle = bottle;
      panelGeneral.setMyCellarObject(bottle);
      initializeExtraProperties();
      if (Program.isWineType()) {
        panelVignobles.initializeVignobles((Bouteille) bottle);
      }
      updateStatusAndTime();

      panelPlace.selectPlace(bottle);
      end.setText(Program.getLabel("Infos092")); //"Saisir les modifications");
      resetModified();
    } catch (RuntimeException e) {
      Program.showException(e);
    }
    Debug("Set Bottle... Done");
  }

  /**
   * saving: Fonction de sauvegarde
   */
  private void saving() {
    Debug("Saving...");
    try {
      new Thread(this).start();
    } catch (RuntimeException a) {
      Program.showException(a);
    }
  }

  private void updateStatusAndTime() {
    panelWineAttribute.updateStatusAndTime(bottle);
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
    String comment1 = comment.getText();
    String dateOfC = panelWineAttribute.getMaturity();
    String parker = panelWineAttribute.getParker();
    String color = panelWineAttribute.getColor();
    String status = panelWineAttribute.getStatus();
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

    Place oldPlace = bottle.getPlace();
    if (isCaisse) {
      lieu_num = place.getPlaceNum();
      bottle.setNumLieu(lieu_num);
      bottle.setLigne(0);
      bottle.setColonne(0);
    } else {
      bottle.setNumLieu(lieu_num);
      bottle.setLigne(line);
      bottle.setColonne(column);
      Optional<MyCellarObject> bottleInPlace = cave.getBouteille(new Bouteille.BouteilleBuilder("").numPlace(lieu_num).line(line).column(column).build());
      if (bottleInPlace.isPresent()) {
        if (!askToReplaceBottle(bottleInPlace.get(), oldPlace)) {
          bottle.setNumLieu(oldPlace.getPlaceNum());
          bottle.setLigne(oldPlace.getLine());
          bottle.setColonne(oldPlace.getColumn());
          return false;
        }
      }
    }
    bottle.setAnnee(panelGeneral.getYear());
    if (Program.isWineType()) {
      Bouteille bTemp = (Bouteille) bottle;
      bTemp.setColor(color);
      bTemp.setComment(comment1);
      bTemp.setMaturity(dateOfC);
      bTemp.setParker(parker);
      bTemp.setPrix(prix);
      bTemp.setVignoble(new VignobleJaxb(country, vignoble, aoc, igp));
      CountryVignobleController.addVignobleFromBottle(bTemp);
      CountryVignobleController.setRebuildNeeded();
    }
    bottle.setEmplacement(cave.getNom());
    bottle.setNom(nom);
    bottle.setKind(demie);
    bottle.setStatus(status);

    bottle.setModified();
    Program.getStorage().addHistory(HistoryState.MODIFY, bottle);

    if (!oldPlace.isSimplePlace()) {
      oldPlace.getRangement().clearComplexStock(oldPlace);
    }

    if (!RangementUtils.putTabStock()) {
      new OpenShowErrorsAction().actionPerformed(null);
    }
    ProgramPanels.getSearch().ifPresent(Search::updateTable);

    Rangement rangement = bottle.getRangement();
    if (!rangement.isCaisse()) {
      rangement.updateToStock(bottle);
    }

    end.setText(Program.getLabel("AddVin.1ItemModified", LabelProperty.SINGLE), true);
    ProgramPanels.updatePanelsWithoutBottles();
    updateStatusAndTime();
    resetModified();
    Debug("Saving... Done");

    return true;
  }

  private boolean askToReplaceBottle(MyCellarObject bouteille, Place oldPlace) throws MyCellarException {
    if (!bouteille.equals(bottle)) {
      Debug("ERROR: Not an empty place, Replace?");
      String erreur_txt1 = MessageFormat.format(Program.getError("Error059"), bouteille.getNom(), bouteille.getAnnee());
      String erreur_txt2 = Program.getError("Error060"); //"Voulez vous le remplacer?");
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
    comment.setModified(false);
    panelVignobles.setModified(false);
    panelPlace.clearModified();
    Start.setPaneModified(false);
  }

  private void replaceWine(final MyCellarObject bToDelete, Place oldPlace) throws MyCellarException {
    //Change wine in a place
    Program.getStorage().addHistory(HistoryState.MODIFY, bottle);

    RangementUtils.replaceMyCellarObject(bToDelete, bottle, oldPlace);
  }

  private boolean runExit() {
    Debug("Processing Quit...");
    addButton.setEnabled(false);

    boolean modified = panelGeneral.isModified(bottle);
    modified |= comment.isModified();
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
    Debug("Quitting... Done");
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
      panelPlace.selectPlace(bottle);
      panelPlace.setListenersEnabled(true);
      Debug("updateView Done");
    });
  }

  private final class PanelMain extends JPanel {
    private static final long serialVersionUID = -4824541234206895953L;

    private PanelMain() {
      setLayout(new MigLayout("", "grow", "[][][]10px[][grow]10px[][]"));
      add(panelGeneral, "growx,wrap");
      add(panelPlace, "growx,wrap");
      add(panelWineAttribute, "growx,split 2");
      add(panelVignobles = new PanelVignobles(true, true, true), "growx, wrap");
      add(labelComment, "growx, wrap");
      add(scrollPaneComment, "grow, wrap");
      add(end, "center, hidemode 3, wrap");
      add(addButton, "center");
    }
  }

}
