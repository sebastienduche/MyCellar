package mycellar;

import mycellar.core.IMyCellarObject;
import mycellar.core.MyCellarObject;
import mycellar.core.tablecomponents.ToolTipRenderer;
import mycellar.core.text.LabelProperty;
import mycellar.core.uicomponents.MyCellarLabel;
import net.miginfocom.swing.MigLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import java.awt.Color;
import java.awt.Font;
import java.util.LinkedList;
import java.util.List;

import static javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS;
import static javax.swing.SwingConstants.CENTER;
import static mycellar.general.ResourceKey.LISTVIN_LISTPROBLEMS;
import static mycellar.general.ResourceKey.LISTVIN_SELECTITEMS;

/**
 * Titre : Cave &agrave; vin
 * Description : Votre description
 * Copyright : Copyright (c) 2004
 * Soci&eacute;t&eacute; : Seb Informatique
 *
 * @author S&eacute;bastien Duch&eacute;
 * @version 4.7
 * @since 13/03/25
 */
final class ListVin extends JPanel {
  private final ListValues listValues;
  private final AddVin addVin;

  /**
   * Constructeur avec liste d'objets
   *
   * @param myCellarObjects LinkedList<IMyCellarObject>: Liste des objets.
   */
  ListVin(List<? extends IMyCellarObject> myCellarObjects, final AddVin addVin) {
    this.addVin = addVin;
    listValues = new ListValues();
    listValues.setObjects(myCellarObjects);
    JTable table = new JTable(listValues);

    TableColumnModel tcm = table.getColumnModel();

    TableColumn tc1 = tcm.getColumn(0);
    tc1.setCellRenderer(new ToolTipRenderer());
    ListSelectionModel rowSM = table.getSelectionModel();
    rowSM.setSelectionInterval(0, 0);
    rowSM.addListSelectionListener((e) -> {
      //Ignore extra messages.
      ListSelectionModel lsm = (ListSelectionModel) e.getSource();
      if (!lsm.isSelectionEmpty()) {
        int minSelectedRow = lsm.getMinSelectionIndex();
        int maxSelectedRow = lsm.getMaxSelectionIndex();
        LinkedList<MyCellarObject> list = new LinkedList<>();
        for (int x = minSelectedRow; x <= maxSelectedRow; x++) {
          if (lsm.isSelectedIndex(x)) {
            list.add(listValues.getObject(x));
          }
        }
        this.addVin.setObjectsInModification(list);
      }
    });

    MyCellarLabel selectItemsLabel = new MyCellarLabel(LISTVIN_SELECTITEMS, LabelProperty.THE_PLURAL, "");

    setLayout(new MigLayout("", "grow", "[grow][]"));
    MyCellarLabel listProblemsLabel = new MyCellarLabel(LISTVIN_LISTPROBLEMS, LabelProperty.PLURAL, "");
    listProblemsLabel.setForeground(Color.red);
    listProblemsLabel.setFont(new Font("Dialog", Font.BOLD, 13));
    listProblemsLabel.setHorizontalAlignment(CENTER);

    JScrollPane scrollpane = new JScrollPane(table);
    scrollpane.setVerticalScrollBarPolicy(VERTICAL_SCROLLBAR_ALWAYS);
    add(scrollpane, "grow,wrap,width min(100,200)");
    add(selectItemsLabel, "width min(100,200)");
    setVisible(true);
  }

  void updateList(List<MyCellarObject> remove) {
    for (MyCellarObject b : remove) {
      listValues.removeObject(b);
    }
  }

  public void setObjects(List<? extends IMyCellarObject> myCellarObjects) {
    listValues.setObjects(myCellarObjects);
  }

  boolean isEmpty() {
    return listValues.getRowCount() == 0;
  }

}
