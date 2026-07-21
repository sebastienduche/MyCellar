package mycellar.showfile;


import mycellar.Erreur;
import mycellar.ITabListener;
import mycellar.MyCellarImage;
import mycellar.Program;
import mycellar.core.IMyCellar;
import mycellar.core.IUpdatable;
import mycellar.core.MyCellarError;
import mycellar.core.tablecomponents.ButtonCellEditor;
import mycellar.core.tablecomponents.ButtonCellRenderer;
import mycellar.core.tablecomponents.CheckboxCellEditor;
import mycellar.core.tablecomponents.CheckboxCellRenderer;
import mycellar.core.tablecomponents.ToolTipRenderer;
import mycellar.core.uicomponents.MyCellarButton;
import mycellar.core.uicomponents.MyCellarSimpleLabel;
import mycellar.core.uicomponents.TabEvent;
import mycellar.general.ResourceKey;
import mycellar.placesmanagement.places.PlaceUtils;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import java.awt.event.ActionEvent;

import static mycellar.core.text.MyCellarLabelManagement.getLabel;
import static mycellar.general.ResourceKey.MAIN_ADD;
import static mycellar.general.ResourceKey.MAIN_DELETE;
import static mycellar.general.ResourceKey.MAIN_STORAGETOCREATE;
import static mycellar.general.ResourceKey.SHOWFILE_QUITERRORS;
import static mycellar.general.ResourceKey.SHOWFILE_RELOADERRORS;

/**
 * Titre : Cave &agrave; vin
 * Description : Votre description
 * Copyright : Copyright (c) 1998
 * Societe : Seb Informatique
 *
 * @author S&eacute;bastien Duch&eacute;
 * @version 0.7
 * @since 03/10/25
 */

public final class ErrorShowPanel extends AbstractShowFilePanel implements ITabListener, IMyCellar, IUpdatable {

  public ErrorShowPanel() {
    super(false);
    MyCellarSimpleLabel title = new MyCellarSimpleLabel();
    title.setHorizontalAlignment(SwingConstants.CENTER);
    setLayout(new MigLayout("", "[][grow]", "[]10px[grow][]"));
    MyCellarButton delete = new MyCellarButton(MyCellarImage.DELETE);
    delete.setText(getLabel(MAIN_DELETE));
    delete.addActionListener((e) -> delete());

    add(title, "align left");
    MyCellarButton createPlacesButton = new MyCellarButton(MAIN_STORAGETOCREATE, new CreatePlacesAction());
    add(createPlacesButton, "align right, split 3");
    MyCellarButton reloadButton = new MyCellarButton(SHOWFILE_RELOADERRORS, new ReloadErrorsAction());
    add(reloadButton, "align right");
    add(delete, "align right, wrap");


    model = new ErrorShowValues();
    ((ErrorShowValues) model).setErrors(Program.getErrors());
    table = new JTable(model);
    title.setText(getLabel(ResourceKey.SHOWFILE_MANAGEERROR));

    postInit();
    refresh();
    addTableSorter();
    updateModel();
  }

  @Override
  protected void refresh() {
    SwingUtilities.invokeLater(() -> {
      ((ErrorShowValues) model).setErrors(Program.getErrors());
      labelCount.setValue(Integer.toString(model.getRowCount()));
    });
  }

  @Override
  public void updateView() {
    super.updateView();
    updateModel();
  }

  private void updateModel() {
    TableColumnModel tcm = table.getColumnModel();
    TableColumn[] tc1 = new TableColumn[5];
    for (int w = 0; w < 5; w++) {
      tc1[w] = tcm.getColumn(w);
      tc1[w].setCellRenderer(new ToolTipRenderer());
      switch (w) {
        case 1:
          tc1[w].setMinWidth(150);
          break;
        case 2:
          tc1[w].setMinWidth(50);
          break;
        case 4:
          tc1[w].setMinWidth(100);
          break;
        default:
          tc1[w].setMinWidth(30);
          break;
      }
    }
    TableColumn tc;
    tc = tcm.getColumn(ErrorShowValues.Column.STATE.getIndex());
    tc.setCellRenderer(new CheckboxCellRenderer());
    tc.setCellEditor(new CheckboxCellEditor());
    tc.setMinWidth(25);
    tc.setMaxWidth(25);
    tc = tcm.getColumn(ErrorShowValues.Column.PLACE.getIndex());
    tc.setCellEditor(new DefaultCellEditor(placeCbx));
    tc = tcm.getColumn(ErrorShowValues.Column.TYPE.getIndex());
    tc.setCellEditor(new DefaultCellEditor(typeCbx));
    tc = tcm.getColumn(ErrorShowValues.Column.STATUS.getIndex());
    tc.setCellRenderer(new FontBoldTableCellRenderer());
    tc = tcm.getColumn(ErrorShowValues.Column.BUTTON.getIndex());
    tc.setCellRenderer(new ButtonCellRenderer(getLabel(MAIN_ADD), MyCellarImage.ADD));
    tc.setCellEditor(new ButtonCellEditor());
  }

  @Override
  public boolean tabWillClose(TabEvent event) {
    if (Program.getErrors().stream().anyMatch(MyCellarError::isNotSolved)) {
      return JOptionPane.NO_OPTION != Erreur.showAskConfirmationMessage(getLabel(SHOWFILE_QUITERRORS));
    }
    PlaceUtils.putTabStock();
    return true;
  }

  public void Debug(String text) {
    Program.Debug("ErrorShowPanel : " + text);
  }

  private static class CreatePlacesAction extends AbstractAction {
    @Override
    public void actionPerformed(ActionEvent e) {
      PlaceUtils.findRangementToCreate();
    }
  }

  private class ReloadErrorsAction extends AbstractAction {
    @Override
    public void actionPerformed(ActionEvent e) {
      PlaceUtils.putTabStock();
      ((ErrorShowValues) model).setErrors(Program.getErrors());
    }
  }

}
