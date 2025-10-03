package mycellar.core;

import mycellar.Bouteille;
import mycellar.Program;
import mycellar.core.panel.PanelSave;
import mycellar.core.uicomponents.JModifyTextArea;
import mycellar.core.uicomponents.MyCellarLabel;
import mycellar.general.PanelGeneral;
import mycellar.general.PanelWineAttribute;
import mycellar.placesmanagement.PanelPlacePosition;
import mycellar.placesmanagement.places.PlacePosition;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;

import static mycellar.core.text.MyCellarLabelManagement.getLabel;
import static mycellar.general.ResourceKey.AJOUTER;
import static mycellar.general.ResourceKey.MAIN_COMMENT;

/**
 * Titre : Cave &agrave; vin
 * Description : Votre description
 * Copyright : Copyright (c) 2017
 * Soci&eacute;t&eacute; : Seb Informatique
 *
 * @author S&eacute;bastien Duch&eacute;
 * @version 5.9
 * @since 03/10/25
 */
public abstract class MyCellarManageBottles extends JPanel implements IPlacePosition, IPanelModifyable {

  protected final MyCellarLabel labelComment = new MyCellarLabel(MAIN_COMMENT);
  protected final PanelPlacePosition panelPlace = new PanelPlacePosition();
  protected final PanelGeneral panelGeneral = new PanelGeneral();
  protected final PanelWineAttribute panelWineAttribute = new PanelWineAttribute();
  protected final JModifyTextArea commentTextArea = new JModifyTextArea();
  protected final JScrollPane scrollPaneComment = new JScrollPane(commentTextArea);
  protected final char ajouterChar = getLabel(AJOUTER).charAt(0);
  protected final PanelSave panelSave = new PanelSave();
  protected int selectedPaneIndex;
  protected PanelVignobles panelVignobles;
  protected Bouteille bottle = null;
  protected boolean severalItems = false; //Pour ListVin
  protected boolean isEditionMode = false;

  protected boolean updateView = false;
  protected UpdateViewType updateViewType;

  protected MyCellarManageBottles() {
  }

  protected static void Debug(String s) {
    Program.Debug("MyCellarManageBottles: " + s);
  }

  protected void initializeExtraProperties() {
    enableAll(true);
    panelGeneral.initializeExtraProperties();
    panelWineAttribute.initializeExtraProperties(bottle, severalItems, isEditionMode);

    commentTextArea.setText(bottle.getComment());
  }

  public void enableAll(boolean enable) {
    panelPlace.setEditable(enable);
    panelGeneral.enableAll(enable);
    panelWineAttribute.enableAll(enable, severalItems, isEditionMode);
    panelSave.enableAll(enable);
    commentTextArea.setEditable(enable);
    panelVignobles.enableAll(enable);
  }

  public void setUpdateViewType(UpdateViewType updateViewType) {
    updateView = true;
    this.updateViewType = updateViewType;
  }

  public void updateView() {
    if (!updateView) {
      return;
    }
    SwingUtilities.invokeLater(() -> {
      Debug("updateView...");
      updateView = false;
      if (updateViewType == UpdateViewType.PLACE || updateViewType == UpdateViewType.ALL) {
        panelPlace.updateView();
      }
      if (updateViewType == UpdateViewType.CAPACITY || updateViewType == UpdateViewType.ALL) {
        panelGeneral.updateView();
      }
      if (updateViewType == UpdateViewType.VINEYARD || updateViewType == UpdateViewType.ALL) {
        panelVignobles.updateList();
      }
      Debug("updateView Done");
    });
  }

  @Override
  public void setModified(boolean modified) {
    panelGeneral.setModified(modified);
  }

  @Override
  public void setPaneIndex(int index) {
    selectedPaneIndex = index;
    panelGeneral.setPaneIndex(index);
  }

  @Override
  public boolean isModified() {
    return panelGeneral.isModified();
  }

  /**
   * Select a place in the lists (used from CellarOrganizerPanel)
   */
  @Override
  public void selectPlace(PlacePosition place) {
    panelPlace.selectPlace(place);
  }

  protected void clearValues() {
    panelWineAttribute.clearValues();
    panelGeneral.clearValues();
    panelPlace.resetValues();
    panelVignobles.resetCountrySelected();
  }

  public final class PanelMain extends JPanel {

    public PanelMain() {
      panelVignobles = new PanelVignobles(false, true, true);
      setLayout(new MigLayout("", "grow", "[][][]10px[][grow]10px[][]"));
      add(panelGeneral, "growx, wrap");
      add(panelPlace, "growx, wrap");
      add(panelWineAttribute, "growx, split 2");
      add(panelVignobles, "growx, wrap");
      panelVignobles.setKeepPreviousVineyardSelected(Program.getCaveConfigBool(MyCellarSettings.KEEP_VINEYARD, false));
      add(labelComment, "growx, wrap");
      add(scrollPaneComment, "grow, wrap");
      add(panelSave, "growx");
    }
  }

}
