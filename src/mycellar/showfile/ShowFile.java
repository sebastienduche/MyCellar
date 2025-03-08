package mycellar.showfile;


import mycellar.ITabListener;
import mycellar.Program;
import mycellar.core.IMyCellar;
import mycellar.core.IUpdatable;
import mycellar.core.common.MyCellarFields;
import mycellar.core.tablecomponents.CheckboxCellEditor;
import mycellar.core.tablecomponents.CheckboxCellRenderer;
import mycellar.core.uicomponents.MyCellarButton;
import mycellar.frame.MainFrame;
import net.miginfocom.swing.MigLayout;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static mycellar.core.text.MyCellarLabelManagement.getLabel;
import static mycellar.general.ResourceKey.MAIN_DELETE;

/**
 * Titre : Cave &agrave; vin
 * Description : Votre description
 * Copyright : Copyright (c) 1998
 * Societe : Seb Informatique
 *
 * @author S&eacute;bastien Duch&eacute;
 * @version 12.7
 * @since 08/03/25
 */

public class ShowFile extends AbstractShowFilePanel implements ITabListener, IMyCellar, IUpdatable {

  private final MyCellarButton manageColumnsButton = new MyCellarButton("Main.Columns", new ManageColumnsAction());

  public ShowFile() {
    super(false);
    init();
  }

  private void init() {
    titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
    setLayout(new MigLayout("", "[][grow]", "[]10px[grow][]"));
    add(titleLabel, "align left");
    add(manageColumnsButton, "align right, split 3");
    add(modifyButton, "align right");

    deleteButton.setText(getLabel(MAIN_DELETE));
    deleteButton.addActionListener(e -> delete());
    add(deleteButton, "align right, wrap");

    model = new ShowFileModel();

    List<ShowFileColumn<?>> showFileColumns = filterColumns(false);
    ((ShowFileModel) model).setColumns(showFileColumns);
    table = new JTable(model);

    postInit();

    refresh();
    addTableSorter();
    updateModel(true, false);
  }


  protected void refresh() {
    SwingUtilities.invokeLater(() -> {
      model.setMyCellarObjects(Program.getStorage().getAllList());
      labelCount.setValue(Integer.toString(model.getRowCount()));
    });
  }

  public void Debug(String text) {
    Program.Debug("ShowFile: " + text);
  }

  private class ManageColumnsAction extends AbstractAction {
    @Override
    public void actionPerformed(ActionEvent e) {
      JPanel panel = new JPanel();
      List<MyCellarFields> list = MyCellarFields.getFieldsList();
      List<ShowFileColumn<?>> cols = ((ShowFileModel) model).getColumns();
      final List<ShowFileColumn<?>> showFileColumns = cols.stream().filter(ShowFileColumn::isDefault).collect(toList());
      ManageColumnModel modelColumn = new ManageColumnModel(list, showFileColumns);
      JTable jTable = new JTable(modelColumn);
      TableColumnModel tcm = jTable.getColumnModel();
      TableColumn tc = tcm.getColumn(0);
      tc.setCellRenderer(new CheckboxCellRenderer());
      tc.setCellEditor(new CheckboxCellEditor());
      tc.setMinWidth(25);
      tc.setMaxWidth(25);
      panel.add(new JScrollPane(jTable));
      JOptionPane.showMessageDialog(MainFrame.getInstance(), panel, getLabel("Main.Columns"), JOptionPane.PLAIN_MESSAGE);
      List<Integer> properties = modelColumn.getSelectedColumns();
      if (!properties.isEmpty()) {
        cols = new ArrayList<>();
        cols.add(checkBoxStartColumn);
        Program.setModified();
        for (ShowFileColumn<?> c : columns) {
          if (properties.contains(c.getField().getIndex())) {
            cols.add(c);
          }
        }
        cols.add(modifyButtonColumn);
      }
      int i = 0;
      StringBuilder buffer = new StringBuilder();
      for (ShowFileColumn<?> c : cols) {
        if (!c.isDefault()) {
          continue;
        }
        if (i > 0) {
          buffer.append(';');
        }
        i++;
        buffer.append(c.getField().name());
      }
      Program.saveShowColumns(buffer.toString());
      if (!cols.isEmpty()) {
        ((ShowFileModel) model).removeAllColumns();
        ((ShowFileModel) model).setColumns(cols);
        updateModel(true, false);
      }
    }
  }
}
