package mycellar.core;

import mycellar.Program;
import mycellar.general.PanelGeneral;
import mycellar.general.PanelWineAttribute;
import mycellar.placesmanagement.PanelPlace;
import mycellar.placesmanagement.Place;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

/**
 * <p>Titre : Cave &agrave; vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2017</p>
 * <p>Soci&eacute;t&eacute; : Seb Informatique</p>
 *
 * @author S&eacute;bastien Duch&eacute;
 * @version 4.6
 * @since 24/08/21
 */
public abstract class MyCellarManageBottles extends JPanel implements IPlace {

  private static final long serialVersionUID = 3056306291164598750L;

  protected final MyCellarLabel labelComment = new MyCellarLabel(LabelType.INFO, "137");
  protected final MyCellarLabel end = new MyCellarLabel(""); // Label pour les résultats
  protected final PanelPlace panelPlace = new PanelPlace();
  protected final PanelGeneral panelGeneral = new PanelGeneral();
  protected final PanelWineAttribute panelWineAttribute = new PanelWineAttribute();
  protected MyCellarButton addButton;
  protected MyCellarButton cancelButton;
  protected JModifyTextArea comment = new JModifyTextArea();
  protected final JScrollPane scrollPaneComment = new JScrollPane(comment);
  protected boolean updateView = false;
  protected PanelVignobles panelVignobles;
  protected MyCellarObject bottle = null;
  protected char ajouterChar = Program.getLabel("AJOUTER").charAt(0);

  protected boolean multi = false; //Pour ListVin
  protected boolean isEditionMode = false;

  protected MyCellarManageBottles() {
  }

  protected static void Debug(String s) {
    Program.Debug("MyCellarManageBottles: " + s);
  }

  protected void initializeExtraProperties() {
    enableAll(true);
    panelGeneral.initializeExtraProperties();
    panelWineAttribute.initializeExtraProperties(bottle, multi, isEditionMode);

    comment.setText(bottle.getComment());
  }

  public void enableAll(boolean enable) {
    panelPlace.enableAll(enable);
    panelGeneral.enableAll(enable);
    panelWineAttribute.enableAll(enable, multi, isEditionMode);
    addButton.setEnabled(enable);
    if (cancelButton != null) {
      cancelButton.setEnabled(enable);
    }
    comment.setEditable(enable);
    panelVignobles.enableAll(enable);
    end.setVisible(enable);
  }

  public void setUpdateView() {
    updateView = true;
  }

  /**
   * Mise a jour de la liste des rangements
   */
  public void updateView() {
    if (!updateView) {
      return;
    }
    SwingUtilities.invokeLater(() -> {
      Debug("updateView...");
      updateView = false;
      panelGeneral.updateView();
      panelVignobles.updateList();
      panelPlace.updateView();
      Debug("updateView Done");
    });
  }

  /**
   * Select a place in the lists (used from CellarOrganizerPanel)
   *
   * @param place
   */
  @Override
  public void selectPlace(Place place) {
    panelPlace.selectPlace(place);
  }

  protected void clearValues() {
    panelWineAttribute.clearValues();
    panelGeneral.clearValues();
    panelPlace.resetValues();
    panelVignobles.resetCountrySelected();
  }

}
