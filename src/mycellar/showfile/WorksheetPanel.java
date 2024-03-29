package mycellar.showfile;


import mycellar.ITabListener;
import mycellar.Program;
import mycellar.core.IMyCellar;
import mycellar.core.IUpdatable;
import mycellar.core.MyCellarObject;
import mycellar.core.datas.worksheet.WorkSheetData;
import mycellar.core.uicomponents.MyCellarButton;
import net.miginfocom.swing.MigLayout;

import javax.swing.AbstractAction;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import java.awt.event.ActionEvent;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static mycellar.core.text.MyCellarLabelManagement.getLabel;

/**
 * Titre : Cave &agrave; vin
 * Description : Votre description
 * Copyright : Copyright (c) 1998
 * Societe : Seb Informatique
 *
 * @author S&eacute;bastien Duch&eacute;
 * @version 12.7
 * @since 31/12/23
 */

public class WorksheetPanel extends AbstractShowFilePanel implements ITabListener, IMyCellar, IUpdatable {

  private final MyCellarButton manageColumnsButton = new MyCellarButton("Main.Columns", new ManageColumnsAction(true));
  private final MyCellarButton removeFromWorksheetButton = new MyCellarButton("ShowFile.RemoveFromWorksheet", new RemoveFromWorksheetAction());
  private final MyCellarButton clearWorksheetButton = new MyCellarButton("ShowFile.ClearWorksheet", new ClearWorksheetAction());

  public WorksheetPanel() {
    super(true);
    final List<Integer> bouteilles = Program.getWorksheetList().getWorsheet()
        .stream()
        .map(WorkSheetData::getBouteilleId)
        .collect(toList());
    workingBottles.addAll(Program.getExistingMyCellarObjects(bouteilles));
    init();
  }

  public void addToWorksheet(List<MyCellarObject> list) {
    final List<MyCellarObject> myCellarObjects = list
        .stream()
        .filter(bouteille -> !workingBottles.contains(bouteille))
        .toList();
    for (MyCellarObject myCellarObject : myCellarObjects) {
      Program.getStorage().addToWorksheet(myCellarObject);
    }
    workingBottles.addAll(myCellarObjects);
    model.setMyCellarObjects(workingBottles);
    labelCount.setValue(Integer.toString(model.getRowCount()));
  }

  private void init() {
    titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
    setLayout(new MigLayout("", "[][grow]", "[]10px[grow][]"));
    add(titleLabel, "align left");
    add(manageColumnsButton, "align right, split 5");
    add(clearWorksheetButton, "align right");
    add(removeFromWorksheetButton, "align right");
    add(modifyButton, "align right");

    deleteButton.setText(getLabel("Main.Delete"));
    deleteButton.addActionListener(e -> delete());
    add(deleteButton, "align right, wrap");

    // Remplissage de la table
    model = new ShowFileModel();

    List<ShowFileColumn<?>> showFileColumns = filterColumns(true);
    ((ShowFileModel) model).setColumns(showFileColumns);
    table = new JTable(model);

    postInit();

    refresh();
    addTableSorter();

    updateModel(true, true);
  }

  protected void refresh() {
    SwingUtilities.invokeLater(() -> {
      model.setMyCellarObjects(workingBottles);
      labelCount.setValue(Integer.toString(model.getRowCount()));
    });
  }

  public void Debug(String text) {
    Program.Debug("WorksheetPanel: " + text);
  }


  private class ClearWorksheetAction extends AbstractAction {
    @Override
    public void actionPerformed(ActionEvent e) {
      workingBottles.clear();
      labelCount.setValue("0");
      SwingUtilities.invokeLater(() -> {
        Program.getStorage().clearWorksheet();
        model.setMyCellarObjects(workingBottles);
      });
    }
  }

  private class RemoveFromWorksheetAction extends AbstractAction {
    @Override
    public void actionPerformed(ActionEvent e) {
      SwingUtilities.invokeLater(() -> {
        getSelectedMyCellarObjects().forEach(Program.getStorage()::removeFromWorksheet);
        workingBottles.removeAll(getSelectedMyCellarObjects());
        Program.setModified();
        model.fireTableDataChanged();
        labelCount.setValue(Integer.toString(model.getRowCount()));
      });
    }
  }
}
