package mycellar.capacity;

import mycellar.ITabListener;
import mycellar.MyCellarImage;
import mycellar.Program;
import mycellar.core.IMyCellar;
import mycellar.core.IUpdatable;
import mycellar.core.MyCellarSwingWorker;
import mycellar.core.UpdateViewType;
import mycellar.core.datas.MyCellarBottleContenance;
import mycellar.core.tablecomponents.ButtonCellEditor;
import mycellar.core.tablecomponents.ButtonCellRenderer;
import mycellar.core.uicomponents.MyCellarButton;
import mycellar.core.uicomponents.MyCellarComboBox;
import mycellar.core.uicomponents.MyCellarLabel;
import mycellar.core.uicomponents.TabEvent;
import mycellar.frame.MainFrame;
import mycellar.general.ProgramPanels;
import net.miginfocom.swing.MigLayout;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import static mycellar.MyCellarUtils.toCleanString;
import static mycellar.core.text.MyCellarLabelManagement.getLabel;
import static mycellar.general.ResourceKey.CAPACITYPANEL_DEFAULT;
import static mycellar.general.ResourceKey.CAPACITYPANEL_MODIFYINFO;
import static mycellar.general.ResourceKey.MAIN_ADD;
import static mycellar.general.ResourceKey.MAIN_ENTERVALUE;
import static mycellar.general.ResourceKey.MAIN_NEWVALUE;

/**
 * Titre : Cave &agrave; vin
 * Description : Votre description
 * Copyright : Copyright (c) 2003
 * Soci&eacute;t&eacute; : Seb Informatique
 *
 * @author S&eacute;bastien Duch&eacute;
 * @version 2.2
 * @since 13/03/25
 */

public final class CapacityPanel extends JPanel implements ITabListener, IMyCellar, IUpdatable {

  private final CapacityTableModel model = new CapacityTableModel();
  private final MyCellarComboBox<String> defaultComboBox = new MyCellarComboBox<>();
  private final JTable table;
  private UpdateViewType updateViewType;

  public CapacityPanel() {
    defaultComboBox.addItem("");
    MyCellarBottleContenance.getList().forEach(defaultComboBox::addItem);
    defaultComboBox.setSelectedItem(MyCellarBottleContenance.getDefaultValue());
    table = new JTable(model);
    TableColumnModel tcm = table.getColumnModel();
    TableColumn tc = tcm.getColumn(CapacityTableModel.STATE);
    tc.setCellRenderer(new ButtonCellRenderer("", MyCellarImage.DELETE));
    tc.setCellEditor(new ButtonCellEditor());
    tc.setMinWidth(25);
    tc.setMaxWidth(25);
    final MyCellarLabel labelDefault = new MyCellarLabel(CAPACITYPANEL_DEFAULT);
    final MyCellarButton add = new MyCellarButton(MAIN_ADD, MyCellarImage.ADD);
    final MyCellarLabel info = new MyCellarLabel(CAPACITYPANEL_MODIFYINFO);
    setLayout(new MigLayout("", "grow", "30px[][][grow]20px[]30px[]"));
    add(info, "wrap");
    add(add, "wrap");
    add(new JScrollPane(table), "grow, wrap");
    add(labelDefault, "split 2");
    add(defaultComboBox, "gapleft 5, wrap");

    add.addActionListener((e) -> add());
  }

  private void add() {
    String s = toCleanString(JOptionPane.showInputDialog(MainFrame.getInstance(), getLabel(MAIN_NEWVALUE), getLabel(MAIN_ENTERVALUE), JOptionPane.QUESTION_MESSAGE));
    if (!s.isEmpty()) {
      Program.setModified();
      model.addValue(s);
      defaultComboBox.addItem(s);
    }
    ProgramPanels.updateAllPanelsForUpdatingCapacity();
  }

  @Override
  public boolean tabWillClose(TabEvent event) {
    if (table.isEditing()) {
      table.getCellEditor().cancelCellEditing();
    }
    if (defaultComboBox.getSelectedIndex() != 0) {
      MyCellarBottleContenance.setDefaultValue((String) defaultComboBox.getSelectedItem());
    }
    if (model.isModify()) {
      Program.setModified();
    }
    return true;
  }

  @Override
  public void tabClosed() {
    model.setModify(false);
  }

  @Override
  public void setUpdateViewType(UpdateViewType updateViewType) {
    this.updateViewType = updateViewType;
  }

  @Override
  public void updateView() {
    if (updateViewType == UpdateViewType.CAPACITY || updateViewType == UpdateViewType.ALL) {
      new MyCellarSwingWorker() {
        @Override
        protected void done() {
          defaultComboBox.removeAllItems();
          defaultComboBox.addItem("");
          MyCellarBottleContenance.getList().forEach(defaultComboBox::addItem);
          defaultComboBox.setSelectedItem(MyCellarBottleContenance.getDefaultValue());
        }
      }.execute();
    }
  }
}
