package mycellar.core;

import mycellar.Program;
import mycellar.core.uicomponents.JModifyTextArea;
import mycellar.core.uicomponents.MyCellarButton;
import mycellar.core.uicomponents.MyCellarLabel;
import mycellar.core.uicomponents.MyCellarSimpleLabel;
import mycellar.general.PanelGeneral;
import mycellar.general.PanelWineAttribute;
import mycellar.placesmanagement.PanelPlacePosition;
import mycellar.placesmanagement.places.PlacePosition;
import net.miginfocom.swing.MigLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import static mycellar.core.text.MyCellarLabelManagement.getLabel;

/**
 * Titre : Cave &agrave; vin
 * Description : Votre description
 * Copyright : Copyright (c) 2017
 * Soci&eacute;t&eacute; : Seb Informatique
 *
 * @author S&eacute;bastien Duch&eacute;
 * @version 5.5
 * @since 09/10/22
 */
public abstract class MyCellarManageBottles extends JPanel implements IPlacePosition, IPanelModifyable {

  protected final MyCellarLabel labelComment = new MyCellarLabel("Main.Comment");
  protected final MyCellarSimpleLabel end = new MyCellarSimpleLabel();
  protected final PanelPlacePosition panelPlace = new PanelPlacePosition();
  protected final PanelGeneral panelGeneral = new PanelGeneral();
  protected final PanelWineAttribute panelWineAttribute = new PanelWineAttribute();
  protected final JModifyTextArea commentTextArea = new JModifyTextArea();
  protected final JScrollPane scrollPaneComment = new JScrollPane(commentTextArea);
  protected final char ajouterChar = getLabel("AJOUTER").charAt(0);
  protected int selectedPaneIndex;
  protected MyCellarButton addButton;
  protected MyCellarButton cancelButton;
  protected PanelVignobles panelVignobles;
  protected MyCellarObject myCellarObject = null;
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
    panelWineAttribute.initializeExtraProperties(myCellarObject, severalItems, isEditionMode);

    commentTextArea.setText(myCellarObject.getComment());
  }

  public void enableAll(boolean enable) {
    panelPlace.setEditable(enable);
    panelGeneral.enableAll(enable);
    panelWineAttribute.enableAll(enable, severalItems, isEditionMode);
    addButton.setEnabled(enable);
    if (cancelButton != null) {
      cancelButton.setEnabled(enable);
    }
    commentTextArea.setEditable(enable);
    panelVignobles.enableAll(enable);
    end.setVisible(enable);
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
   *
   * @param place
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
      if (Program.isWineType()) {
        add(panelVignobles, "growx, wrap");
        panelVignobles.setKeepPreviousVineyardSelected(Program.getCaveConfigBool(MyCellarSettings.KEEP_VINEYARD, false));
      } else {
        add(new JPanel(), "growx, wrap");
      }
      add(labelComment, "growx, wrap");
      add(scrollPaneComment, "grow, wrap");
      add(end, "center, hidemode 3, wrap");
      add(addButton, "center, split 2");
      add(cancelButton);
    }
  }

}
