package mycellar.showfile;


import mycellar.Erreur;
import mycellar.ITabListener;
import mycellar.MyCellarImage;
import mycellar.Program;
import mycellar.core.BottlesStatus;
import mycellar.core.IMyCellar;
import mycellar.core.IUpdatable;
import mycellar.core.MyCellarEnum;
import mycellar.core.MyCellarError;
import mycellar.core.MyCellarObject;
import mycellar.core.UpdateViewType;
import mycellar.core.common.bottle.BottleColor;
import mycellar.core.common.music.MusicSupport;
import mycellar.core.datas.MyCellarBottleContenance;
import mycellar.core.tablecomponents.ButtonCellEditor;
import mycellar.core.tablecomponents.ButtonCellRenderer;
import mycellar.core.tablecomponents.CheckboxCellEditor;
import mycellar.core.tablecomponents.CheckboxCellRenderer;
import mycellar.core.tablecomponents.ToolTipRenderer;
import mycellar.core.text.LabelProperty;
import mycellar.core.uicomponents.MyCellarButton;
import mycellar.core.uicomponents.MyCellarComboBox;
import mycellar.core.uicomponents.MyCellarLabel;
import mycellar.core.uicomponents.MyCellarSimpleLabel;
import mycellar.core.uicomponents.TabEvent;
import mycellar.frame.MainFrame;
import mycellar.placesmanagement.places.AbstractPlace;
import mycellar.placesmanagement.places.PlaceUtils;
import net.miginfocom.swing.MigLayout;

import javax.swing.AbstractAction;
import javax.swing.DefaultCellEditor;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.RowSorter;
import javax.swing.ScrollPaneConstants;
import javax.swing.SortOrder;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static mycellar.MyCellarUtils.safeStringToBigDecimal;
import static mycellar.ProgramConstants.SPACE;
import static mycellar.core.text.MyCellarLabelManagement.getError;
import static mycellar.core.text.MyCellarLabelManagement.getLabel;

/**
 * Titre : Cave &agrave; vin
 * Description : Votre description
 * Copyright : Copyright (c) 1998
 * Societe : Seb Informatique
 *
 * @author S&eacute;bastien Duch&eacute;
 * @version 0.2
 * @since 30/12/23
 */

public class ErrorShowPanel extends JPanel implements ITabListener, IMyCellar, IUpdatable {

  private static final MyCellarEnum NONE = new MyCellarEnum(0, "");
  private static final MyCellarEnum VALIDATED = new MyCellarEnum(1, getLabel("History.Validated"));
  private static final MyCellarEnum TOCHECK = new MyCellarEnum(2, getLabel("History.ToCheck"));

  private final MyCellarLabel labelCount = new MyCellarLabel("Main.NumberOfItems", LabelProperty.PLURAL, "");
  private final MyCellarComboBox<AbstractPlace> placeCbx = new MyCellarComboBox<>();
  private final MyCellarComboBox<String> typeCbx = new MyCellarComboBox<>();
  private final ErrorShowValues model;
  private final JTable table;
  private boolean updateView = false;
  private UpdateViewType updateViewType;

  public ErrorShowPanel() {
    MyCellarSimpleLabel titleLabel = new MyCellarSimpleLabel();
    titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
    setLayout(new MigLayout("", "[][grow]", "[]10px[grow][]"));
    MyCellarButton deleteButton = new MyCellarButton(MyCellarImage.DELETE);
    deleteButton.setText(getLabel("Main.Delete"));
    deleteButton.addActionListener(this::delete);

    add(titleLabel, "align left");
    MyCellarButton createPlacesButton = new MyCellarButton("Main.StorageToCreate", new CreatePlacesAction());
    add(createPlacesButton, "align right, split 3");
    MyCellarButton reloadButton = new MyCellarButton("ShowFile.ReloadErrors", new ReloadErrorsAction());
    add(reloadButton, "align right");
    add(deleteButton, "align right, wrap");

    initPlacesCombo();

    if (Program.isMusicType()) {
      MyCellarComboBox<MusicSupport> musicSupportCbx = new MyCellarComboBox<>();
      Arrays.stream(MusicSupport.values()).forEach(musicSupportCbx::addItem);
    } else if (Program.isWineType()) {
      MyCellarComboBox<BottleColor> colorCbx = new MyCellarComboBox<>();
      Arrays.stream(BottleColor.values()).forEach(colorCbx::addItem);
    }
    MyCellarComboBox<BottlesStatus> statusCbx = new MyCellarComboBox<>();
    Arrays.stream(BottlesStatus.values()).forEach(statusCbx::addItem);

    typeCbx.addItem("");
    MyCellarBottleContenance.getList().forEach(typeCbx::addItem);

    MyCellarComboBox<MyCellarEnum> verifyStatusCbx = new MyCellarComboBox<>();
    verifyStatusCbx.addItem(NONE);
    verifyStatusCbx.addItem(VALIDATED);
    verifyStatusCbx.addItem(TOCHECK);

    model = new ErrorShowValues();
    model.setErrors(Program.getErrors());
    table = new JTable(model);
    titleLabel.setText(getLabel("ShowFile.ManageError"));

    refresh();
    table.setAutoCreateRowSorter(true);
    TableRowSorter<TableModel> sorter = new TableRowSorter<>(table.getModel());
    sorter.setComparator(TableShowValues.PRICE, (String o1, String o2) -> {
      BigDecimal price1;
      if (o1.isEmpty()) {
        price1 = BigDecimal.ZERO;
      } else {
        price1 = safeStringToBigDecimal(o1, BigDecimal.ZERO);
      }
      BigDecimal price2;
      if (o2.isEmpty()) {
        price2 = BigDecimal.ZERO;
      } else {
        price2 = safeStringToBigDecimal(o2, BigDecimal.ZERO);
      }
      return price1.compareTo(price2);
    });
    table.setRowSorter(sorter);
    List<RowSorter.SortKey> sortKeys = new ArrayList<>();
    sortKeys.add(new RowSorter.SortKey(1, SortOrder.ASCENDING));
    sorter.setSortKeys(sortKeys);
    sorter.sort();

    updateModel();

    table.setPreferredScrollableViewportSize(new Dimension(300, 200));
    table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

    add(new JScrollPane(table, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED), "grow, span 2, wrap");
    add(labelCount, "grow, span 2, align right, wrap");
  }

  private void delete(ActionEvent e) {
    LinkedList<MyCellarObject> toDeleteList = getSelectedMyCellarObjects();

    if (toDeleteList.isEmpty()) {
      Erreur.showInformationMessage(getError("Error.NoItemToDelete", LabelProperty.SINGLE), getError("Error.pleaseSelect", LabelProperty.THE_PLURAL));
    } else {
      String erreur_txt1, erreur_txt2;
      if (toDeleteList.size() == 1) {
        erreur_txt1 = getError("Error.1ItemSelected", LabelProperty.SINGLE);
        erreur_txt2 = getError("Error.Confirm1Delete");
      } else {
        erreur_txt1 = MessageFormat.format(getError("Error.NItemsSelected", LabelProperty.PLURAL), toDeleteList.size());
        erreur_txt2 = getError("Error.confirmNDelete");
      }
      if (JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(null, erreur_txt1 + SPACE + erreur_txt2, getLabel("Main.AskConfirmation"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE)) {
        for (MyCellarObject b : toDeleteList) {
          Program.getErrors().remove(new MyCellarError(MyCellarError.ID.INEXISTING_PLACE, b));
        }
      }
      refresh();
    }
  }

  private LinkedList<MyCellarObject> getSelectedMyCellarObjects() {
    final LinkedList<MyCellarObject> list = new LinkedList<>();
    int max_row = model.getRowCount();
    if (max_row == 0) {
      return list;
    }
    int row = 0;
    do {
      if (model.getValueAt(row, TableShowValues.ETAT).equals(Boolean.TRUE)) {
        list.add(model.getMyCellarObject(row));
      }
      row++;
    } while (row < max_row);

    return list;
  }


  private void refresh() {
    SwingUtilities.invokeLater(() -> {
      model.setErrors(Program.getErrors());
      labelCount.setValue(Integer.toString(model.getRowCount()));
    });
  }

  @Override
  public void setUpdateViewType(UpdateViewType updateViewType) {
    updateView = true;
    this.updateViewType = updateViewType;
  }

  @Override
  public void updateView() {
    refresh();
    if (!updateView) {
      return;
    }
    updateView = false;
    model.fireTableStructureChanged();
    if (updateViewType == UpdateViewType.PLACE || updateViewType == UpdateViewType.ALL) {
      initPlacesCombo();
    }

    if (updateViewType == UpdateViewType.CAPACITY || updateViewType == UpdateViewType.ALL) {
      typeCbx.removeAllItems();
      typeCbx.addItem("");
      MyCellarBottleContenance.getList().forEach(typeCbx::addItem);
    }

    updateModel();
  }

  private void initPlacesCombo() {
    placeCbx.removeAllItems();
    placeCbx.addItem(Program.EMPTY_PLACE);
    Program.getAbstractPlaces().forEach(placeCbx::addItem);
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
    tc.setCellRenderer(new ButtonCellRenderer(getLabel("Main.Add"), MyCellarImage.ADD));
    tc.setCellEditor(new ButtonCellEditor());
  }

  @Override
  public boolean tabWillClose(TabEvent event) {
    if (Program.getErrors().stream().anyMatch(MyCellarError::isNotSolved)) {
      return JOptionPane.NO_OPTION != JOptionPane.showConfirmDialog(MainFrame.getInstance(), getLabel("ShowFile.QuitErrors"), getLabel("Main.AskConfirmation"), JOptionPane.YES_NO_OPTION);
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
      model.setErrors(Program.getErrors());
    }
  }

}
