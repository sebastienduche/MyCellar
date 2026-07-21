package mycellar.showfile;

import mycellar.Bouteille;
import mycellar.ITabListener;
import mycellar.Program;
import mycellar.core.IMyCellar;
import mycellar.core.IUpdatable;
import mycellar.core.datas.worksheet.WorkSheetData;
import mycellar.core.uicomponents.MyCellarButton;
import net.miginfocom.swing.MigLayout;

import javax.swing.AbstractAction;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static java.util.stream.Collectors.toList;
import static mycellar.core.MyCellarSettings.CONVERTED_TO_UUID;
import static mycellar.core.text.MyCellarLabelManagement.getLabel;
import static mycellar.general.ResourceKey.MAIN_COLUMNS;
import static mycellar.general.ResourceKey.MAIN_DELETE;
import static mycellar.general.ResourceKey.SHOWFILE_CLEARWORKSHEET;
import static mycellar.general.ResourceKey.SHOWFILE_REMOVEFROMWORKSHEET;

/**
 * Titre : Cave &agrave; vin
 * Description : Votre description
 * Copyright : Copyright (c) 1998
 * Societe : Seb Informatique
 *
 * @author S&eacute;bastien Duch&eacute;
 * @version 13.3
 * @since 27/06/26
 */

public class WorksheetPanel extends AbstractShowFilePanel implements ITabListener, IMyCellar, IUpdatable {

  private final MyCellarButton manageColumnsButton = new MyCellarButton(MAIN_COLUMNS, new ManageColumnsAction(true));
  private final MyCellarButton removeFromWorksheetButton = new MyCellarButton(SHOWFILE_REMOVEFROMWORKSHEET, new RemoveFromWorksheetAction());
  private final MyCellarButton clearWorksheetButton = new MyCellarButton(SHOWFILE_CLEARWORKSHEET, new ClearWorksheetAction());

  public WorksheetPanel() {
    super(true);
    if (!Program.getCaveConfigBool(CONVERTED_TO_UUID, false)) {
      final List<Integer> bouteilles = Program.getWorksheetList().getWorsheet()
          .stream()
          .filter(workSheetData -> workSheetData.getUuid() == null)
          .map(WorkSheetData::getBouteilleId)
          .collect(toList());
      workingBottles.addAll(Program.getExistingMyCellarObjectsWithID(bouteilles));
    } else {
      final List<UUID> bottles = Program.getWorksheetList().getWorsheet()
          .stream()
          .map(WorkSheetData::getUuid)
          .filter(Objects::nonNull)
          .collect(toList());
      workingBottles.addAll(Program.getExistingMyCellarObjects(bottles));
    }
    init();
  }

  public void addToWorksheet(List<Bouteille> list) {
    final List<Bouteille> bottles = list
        .stream()
        .filter(bouteille -> !workingBottles.contains(bouteille))
        .toList();
    for (Bouteille bottle : bottles) {
      Program.getStorage().addToWorksheet(bottle);
    }
    workingBottles.addAll(bottles);
    model.setBottles(workingBottles.stream().toList());
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

    deleteButton.setText(getLabel(MAIN_DELETE));
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

  @Override
  protected void refresh() {
    SwingUtilities.invokeLater(() -> {
      model.setBottles(workingBottles.stream().toList());
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
        model.setBottles(workingBottles.stream().toList());
      });
    }
  }

  private class RemoveFromWorksheetAction extends AbstractAction {
    @Override
    public void actionPerformed(ActionEvent e) {
      SwingUtilities.invokeLater(() -> {
        getSelectedBottles().forEach(Program.getStorage()::removeFromWorksheet);
        getSelectedBottles().forEach(workingBottles::remove);
        Program.setModified();
        model.fireTableDataChanged();
        labelCount.setValue(Integer.toString(model.getRowCount()));
      });
    }
  }
}
