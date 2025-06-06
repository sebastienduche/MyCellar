package mycellar;

import mycellar.actions.OpenAddVinAction;
import mycellar.core.IMyCellar;
import mycellar.core.IMyCellarObject;
import mycellar.core.datas.history.History;
import mycellar.core.datas.history.HistoryState;
import mycellar.core.tablecomponents.ButtonCellEditor;
import mycellar.core.tablecomponents.ButtonCellRenderer;
import mycellar.core.tablecomponents.CheckboxCellEditor;
import mycellar.core.tablecomponents.CheckboxCellRenderer;
import mycellar.core.tablecomponents.DateCellRenderer;
import mycellar.core.tablecomponents.ToolTipRenderer;
import mycellar.core.uicomponents.MyCellarButton;
import mycellar.core.uicomponents.MyCellarComboBox;
import mycellar.core.uicomponents.MyCellarLabel;
import mycellar.placesmanagement.places.AbstractPlace;
import mycellar.placesmanagement.places.PlaceUtils;
import net.miginfocom.swing.MigLayout;

import javax.swing.AbstractAction;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableRowSorter;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.io.Serial;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import static mycellar.core.text.MyCellarLabelManagement.getError;
import static mycellar.core.text.MyCellarLabelManagement.getLabel;
import static mycellar.general.ResourceErrorKey.ERROR_1ITEMSELECTED;
import static mycellar.general.ResourceErrorKey.ERROR_1LINESELECTED;
import static mycellar.general.ResourceErrorKey.ERROR_CONFIRMNDELETE;
import static mycellar.general.ResourceErrorKey.ERROR_DELETEIT;
import static mycellar.general.ResourceErrorKey.ERROR_NITEMSSELECTED;
import static mycellar.general.ResourceErrorKey.ERROR_NLINESELECTED;
import static mycellar.general.ResourceErrorKey.ERROR_NOLINESELECTED;
import static mycellar.general.ResourceErrorKey.ERROR_SELECTLINESTODELETE;
import static mycellar.general.ResourceKey.HISTORY_ENTERED;
import static mycellar.general.ResourceKey.HISTORY_EXITED;
import static mycellar.general.ResourceKey.HISTORY_FILTER;
import static mycellar.general.ResourceKey.HISTORY_MODIFIED;
import static mycellar.general.ResourceKey.HISTORY_NONE;
import static mycellar.general.ResourceKey.HISTORY_TOCHECK;
import static mycellar.general.ResourceKey.HISTORY_VALIDATED;
import static mycellar.general.ResourceKey.MAIN_DELETE;
import static mycellar.general.ResourceKey.SHOWFILE_NOBOTTLETORESTORE;
import static mycellar.general.ResourceKey.SHOWFILE_RESTORE;
import static mycellar.general.ResourceKey.SHOWFILE_RESTOREONE;
import static mycellar.general.ResourceKey.SHOWFILE_RESTORESEVERAL;
import static mycellar.general.ResourceKey.SHOWFILE_SELECTTORESTORE;
import static mycellar.general.ResourceKey.SHOWHISTORY_CANTRESTORENONDELETED;
import static mycellar.general.ResourceKey.SHOWHISTORY_CLEARHISTORY;

/**
 * Titre : Cave &agrave; vin
 * Description : Votre description
 * Copyright : Copyright (c) 1998
 * Soci&eacute;t&eacute; : Seb Informatique
 *
 * @author S&eacute;bastien Duch&eacute;
 * @version 6.2
 * @since 03/04/25
 */
public final class ShowHistory extends JPanel implements ITabListener, IMyCellar {

  @Serial
  private static final long serialVersionUID = 4778721795659106312L;
  private final MyCellarComboBox<FilterItem> filterCbx = new MyCellarComboBox<>();
  private final TableHistoryValues model;

  public ShowHistory() {
    Debug("Constructor");
    MyCellarLabel filterLabel = new MyCellarLabel(HISTORY_FILTER);
    filterCbx.addItem(new FilterItem(HistoryState.ALL, getLabel(HISTORY_NONE)));
    filterCbx.addItem(new FilterItem(HistoryState.ADD, getLabel(HISTORY_ENTERED)));
    filterCbx.addItem(new FilterItem(HistoryState.MODIFY, getLabel(HISTORY_MODIFIED)));
    filterCbx.addItem(new FilterItem(HistoryState.DEL, getLabel(HISTORY_EXITED)));
    filterCbx.addItem(new FilterItem(HistoryState.VALIDATED, getLabel(HISTORY_VALIDATED)));
    filterCbx.addItem(new FilterItem(HistoryState.TOCHECK, getLabel(HISTORY_TOCHECK)));
    filterCbx.addItemListener(this::filter_itemStateChanged);

    // Remplissage de la table
    model = new TableHistoryValues(true);
    model.setHistory(Program.getHistory());

    JTable table = new JTable(model);
    TableColumnModel tcm = table.getColumnModel();
    TableColumn[] tc1 = new TableColumn[4];
    for (int w = 0; w < 4; w++) {
      tc1[w] = tcm.getColumn(w);
      tc1[w].setCellRenderer(new ToolTipRenderer());
      switch (w) {
        case 0:
          tc1[w].setMinWidth(30);
          break;
        case 1:
        case 2:
          tc1[w].setMinWidth(100);
          break;
        case 3:
          tc1[w].setMinWidth(350);
          break;
      }
    }
    TableColumn tc = tcm.getColumn(TableHistoryValues.SELECT);
    tc.setCellRenderer(new CheckboxCellRenderer());
    tc.setCellEditor(new CheckboxCellEditor());
    tc.setMinWidth(30);
    tc.setMaxWidth(30);
    tc = tcm.getColumn(TableHistoryValues.ACTION);
    tc.setCellRenderer(new ButtonCellRenderer());
    tc.setCellEditor(new ButtonCellEditor());
    tc.setMinWidth(100);
    tc.setMaxWidth(100);
    tc = tcm.getColumn(TableHistoryValues.DATE);
    tc.setCellRenderer(new DateCellRenderer());

    TableRowSorter<TableHistoryValues> sorter = new TableRowSorter<>(model);
    sorter.setComparator(1, (Comparator<LocalDate>) (o1, o2) -> {
      if (o1 == null || o2 == null) {
        return 1;
      }
      return o1.compareTo(o2);
    });
    table.setRowSorter(sorter);
    sorter.setSortKeys(List.of(new RowSorter.SortKey(1, SortOrder.DESCENDING)));

    setLayout(new MigLayout("", "grow", "[][grow][]"));
    add(filterLabel, "split 5");
    add(filterCbx);
    add(new MyCellarButton(SHOWHISTORY_CLEARHISTORY, new ClearHistoryAction()), "gapleft 10px");
    add(new JLabel(), "growx");
    add(new MyCellarButton(SHOWFILE_RESTORE, new RestoreAction()), "align right, wrap");
    add(new JScrollPane(table), "grow, wrap");
    add(new MyCellarButton(MAIN_DELETE, new DeleteAction()), "center");
  }

  private static void Debug(String sText) {
    Program.Debug("ShowHistory: " + sText);
  }

  private void filter_itemStateChanged(ItemEvent e) {
    Debug("setFilter");
    model.setFilter(filterCbx.getSelectedIndex() - 1);
  }

  public void refresh() {
    model.setHistory(Program.getHistory());
  }

  static class FilterItem {
    private final HistoryState historyState;
    private final String label;

    public FilterItem(HistoryState historyState, String label) {
      this.historyState = historyState;
      this.label = label;
    }

    public HistoryState getHistoryState() {
      return historyState;
    }

    @Override
    public String toString() {
      return label;
    }
  }

  private final class RestoreAction extends AbstractAction {

    @Serial
    private static final long serialVersionUID = 4095399581910695568L;

    private RestoreAction() {
      super("", MyCellarImage.RESTORE);
      putValue(SHORT_DESCRIPTION, getLabel(SHOWFILE_RESTORE));
    }

    @Override
    public void actionPerformed(ActionEvent arg0) {
      LinkedList<IMyCellarObject> toRestoreList = new LinkedList<>();

      boolean nonExit = false;

      int max_row = model.getRowCount();
      if (max_row != 0) {
        int row = 0;
        do {
          if (Boolean.TRUE.equals(model.getValueAt(row, TableHistoryValues.SELECT))) {
            if (model.isDeleted(row))
              toRestoreList.add(model.getObject(row));
            else {
              nonExit = true;
            }
          }
          row++;
        } while (row < max_row);
      }

      if (nonExit) {
        Erreur.showInformationMessage(getLabel(SHOWHISTORY_CANTRESTORENONDELETED));
        return;
      }

      if (toRestoreList.isEmpty()) {
        Erreur.showInformationMessage(SHOWFILE_NOBOTTLETORESTORE, SHOWFILE_SELECTTORESTORE);
      } else {
        String message;
        if (toRestoreList.size() == 1) {
          message = String.format("%s %s", getError(ERROR_1ITEMSELECTED), getLabel(SHOWFILE_RESTOREONE));
        } else {
          message = String.format("%s %s", getError(ERROR_NITEMSSELECTED, toRestoreList.size()), getLabel(SHOWFILE_RESTORESEVERAL));
        }
        if (JOptionPane.YES_OPTION == Erreur.showAskConfirmationMessage(message)) {
          LinkedList<IMyCellarObject> cantRestoreList = new LinkedList<>();
          for (IMyCellarObject myCellarObject : toRestoreList) {
            if (myCellarObject.isInExistingPlace()) {
              AbstractPlace rangement = myCellarObject.getAbstractPlace();
              if (rangement.isSimplePlace()) {
                Program.getStorage().addHistory(HistoryState.ADD, myCellarObject);
                Program.getStorage().addWine(myCellarObject);
              } else {
                if (rangement.canAddObjectAt(myCellarObject.getPlacePosition())) {
                  Program.getStorage().addHistory(HistoryState.ADD, myCellarObject);
                  Program.getStorage().addWine(myCellarObject);
                } else {
                  cantRestoreList.add(myCellarObject);
                }
              }
            }
            if (!cantRestoreList.contains(myCellarObject)) {
              Program.getTrash().remove(myCellarObject);
            }
          }

          if (!cantRestoreList.isEmpty()) {
            OpenAddVinAction.open(cantRestoreList);
          }
        }
        PlaceUtils.putTabStock();
        model.setHistory(Program.getHistory());
      }
    }
  }

  final class DeleteAction extends AbstractAction {

    @Serial
    private static final long serialVersionUID = -1982193809982154836L;

    private DeleteAction() {
      super("", MyCellarImage.DELETE);
      putValue(SHORT_DESCRIPTION, getLabel(MAIN_DELETE));
    }

    @Override
    public void actionPerformed(ActionEvent arg0) {
      Debug("Deleting lines");

      try {
        int max_row = model.getRowCount();
        int row = 0;
        LinkedList<History> toDeleteList = new LinkedList<>();
        do {
          if (model.getValueAt(row, TableHistoryValues.SELECT).equals(Boolean.TRUE)) {
            toDeleteList.add(model.getHistoryAt(row));
          }
          row++;
        } while (row < max_row);

        if (toDeleteList.isEmpty()) {
          Erreur.showInformationMessage(ERROR_NOLINESELECTED, ERROR_SELECTLINESTODELETE);
          Debug("ERROR: No lines selected");
        } else {
          String message;
          if (toDeleteList.size() == 1) {
            message = String.format("%s %s", getError(ERROR_1LINESELECTED), getError(ERROR_DELETEIT));
          } else {
            message = String.format("%s %s", getError(ERROR_NLINESELECTED, toDeleteList.size()), getError(ERROR_CONFIRMNDELETE));
          }
          Debug(toDeleteList.size() + " line(s) selected");
          if (JOptionPane.YES_OPTION == Erreur.showAskConfirmationMessage(message)) {
            Debug("Deleting lines...");
            for (History b : toDeleteList) {
              Program.getStorage().removeHistory(b);
            }
            model.setHistory(Program.getHistory());
          }
        }
      } catch (HeadlessException e) {
        Debug("ERROR: Why this exception again? " + e.getMessage());
        Program.showException(e);
      } catch (RuntimeException e) {
        Program.showException(e);
      }
    }
  }

  class ClearHistoryAction extends AbstractAction {

    @Serial
    private static final long serialVersionUID = 3079501619032347868L;

    private ClearHistoryAction() {
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      Program.getStorage().clearHistory(((FilterItem) Objects.requireNonNull(filterCbx.getSelectedItem())).getHistoryState());
      filterCbx.setSelectedIndex(0);
      refresh();
    }
  }
}
