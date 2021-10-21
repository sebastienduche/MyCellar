package mycellar;

import mycellar.core.IMyCellarObject;
import mycellar.core.LabelProperty;
import mycellar.core.LabelType;
import mycellar.core.MyCellarLabel;
import mycellar.core.MyCellarObject;
import mycellar.core.tablecomponents.ToolTipRenderer;
import net.miginfocom.swing.MigLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import java.awt.Color;
import java.awt.Font;
import java.util.LinkedList;
import java.util.List;


/**
 * <p>Titre : Cave &agrave; vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2004</p>
 * <p>Soci&eacute;t&eacute; : Seb Informatique</p>
 *
 * @author S&eacute;bastien Duch&eacute;
 * @version 4.2
 * @since 23/0/21
 */
final class ListVin extends JPanel {
  static final long serialVersionUID = 10805;
  private ListValues listValues;
  private AddVin addVin;

  /**
   * Constructeur avec liste des bouteilles
   *
   * @param bottle LinkedList<IMyCellarObject>: Liste des objets.
   * @param addVin
   */
  ListVin(List<? extends IMyCellarObject> bottle, final AddVin addVin) {

    try {
      this.addVin = addVin;
      listValues = new ListValues();
      listValues.setBouteilles(bottle);
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
              list.add(listValues.getBouteille(x));
            }
          }
          this.addVin.setBottlesInModification(list);
        }
      });

      JScrollPane scrollpane = new JScrollPane(table);
      MyCellarLabel MyCellarLabel2 = new MyCellarLabel(Program.getLabel("ListVin.selectItems", LabelProperty.SINGLE));

      scrollpane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
      setLayout(new MigLayout("", "grow", "[grow][]"));
      MyCellarLabel textControl1 = new MyCellarLabel(LabelType.INFO_OTHER, "ListVin.listProblems", LabelProperty.PLURAL);
      textControl1.setForeground(Color.red);
      textControl1.setFont(new Font("Dialog", Font.BOLD, 13));
      textControl1.setHorizontalAlignment(SwingConstants.CENTER);

      add(scrollpane, "grow,wrap,width min(100,200)");
      add(MyCellarLabel2, "width min(100,200)");
      setVisible(true);
    } catch (RuntimeException e) {
      Program.showException(e);
    }
  }

  void updateList(List<MyCellarObject> remove) {
    for (MyCellarObject b : remove) {
      listValues.removeBouteille(b);
    }
  }

  public void setBottles(List<? extends IMyCellarObject> bottles) {
    listValues.setBouteilles(bottles);
  }

  int getListSize() {
    return listValues.getRowCount();
  }

}
