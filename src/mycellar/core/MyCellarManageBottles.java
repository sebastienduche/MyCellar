package mycellar.core;

import mycellar.Program;
import mycellar.core.uicomponents.JModifyTextArea;
import mycellar.core.uicomponents.MyCellarButton;
import mycellar.core.uicomponents.MyCellarLabel;
import mycellar.general.PanelGeneral;
import mycellar.general.PanelWineAttribute;
import mycellar.placesmanagement.PanelPlace;
import mycellar.placesmanagement.Place;
import net.miginfocom.swing.MigLayout;

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
 * @version 5.0
 * @since 03/01/22
 */
public abstract class MyCellarManageBottles extends JPanel implements IPlace {

  private static final long serialVersionUID = 3056306291164598750L;

  protected final MyCellarLabel labelComment = new MyCellarLabel(LabelType.INFO, "137");
  protected final MyCellarLabel end = new MyCellarLabel(""); // Label pour les resultats
  protected final PanelPlace panelPlace = new PanelPlace();
  protected final PanelGeneral panelGeneral = new PanelGeneral();
  protected final PanelWineAttribute panelWineAttribute = new PanelWineAttribute();
  protected final JModifyTextArea commentTextArea = new JModifyTextArea();
  protected final JScrollPane scrollPaneComment = new JScrollPane(commentTextArea);
  protected final char ajouterChar = Program.getLabel("AJOUTER").charAt(0);
  protected MyCellarButton addButton;
  protected MyCellarButton cancelButton;
  protected PanelVignobles panelVignobles;
  protected MyCellarObject myCellarObject = null;
  protected boolean severalItems = false; //Pour ListVin
  protected boolean isEditionMode = false;

  protected boolean updateView = false;
  private UpdateViewType updateViewType;

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
    panelPlace.enableAll(enable);
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

  public void setUpdateView(UpdateViewType updateViewType) {
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
      panelGeneral.updateView();
      panelVignobles.updateList();
      if (updateViewType == UpdateViewType.PLACE) {
        panelPlace.updateView();
      }
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

  public final class PanelMain extends JPanel {
    private static final long serialVersionUID = -4824541234206895953L;

    public PanelMain(boolean withCancelButton) {
      panelVignobles = new PanelVignobles(false, true, true);
      setLayout(new MigLayout("", "grow", "[][][]10px[][grow]10px[][]"));
      add(panelGeneral, "growx, wrap");
      add(panelPlace, "growx, wrap");
      add(panelWineAttribute, "growx, split 2");
      if (Program.isWineType()) {
        add(panelVignobles, "growx, wrap");
      } else {
        add(new JPanel(), "growx, wrap");
      }
      add(labelComment, "growx, wrap");
      add(scrollPaneComment, "grow, wrap");
      add(end, "center, hidemode 3, wrap");
      if (withCancelButton) {
        add(addButton, "center, split 2");
        add(cancelButton);
      } else {
        add(addButton, "center");
      }
    }
  }

}
