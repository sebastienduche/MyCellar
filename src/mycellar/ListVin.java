package mycellar;

import mycellar.core.IMyCellarObject;
import mycellar.core.LabelProperty;
import mycellar.core.LabelType;
import mycellar.core.MyCellarLabel;
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
 * <p>Titre : Cave à vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2004</p>
 * <p>Société : Seb Informatique</p>
 * @author Sébastien Duché
 * @version 4.1
 * @since 27/05/21
 */
final class ListVin extends JPanel {
  private ListValues listValues;

  private AddVin addVin;
  static final long serialVersionUID = 10805;

  /**
   * ListVin: Constructeur avec liste des bouteilles
   *
   * @param bottle LinkedList<Bouteille>: Liste des bouteilles.
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
      rowSM.addListSelectionListener( (e) -> {
        //Ignore extra messages.
        ListSelectionModel lsm = (ListSelectionModel) e.getSource();
        if (!lsm.isSelectionEmpty()) {
          int minSelectedRow = lsm.getMinSelectionIndex();
          int maxSelectedRow = lsm.getMaxSelectionIndex();
          LinkedList<Bouteille> list = new LinkedList<>();
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
      setLayout(new MigLayout("","grow","[grow][]"));
      MyCellarLabel textControl1 = new MyCellarLabel(LabelType.INFO_OTHER, "ListVin.listProblems", LabelProperty.PLURAL);
      textControl1.setForeground(Color.red);
      textControl1.setFont(new Font("Dialog", Font.BOLD, 13));
      textControl1.setHorizontalAlignment(SwingConstants.CENTER);

      add(scrollpane,"grow,wrap,width min(100,200)");
      add(MyCellarLabel2,"width min(100,200)");
      setVisible(true);
    } catch (RuntimeException e) {
      Program.showException(e);
    }
  }

  /**
   * updateList: Mise à jour de la liste des vins
   *
   * @param remove LinkedList<Bouteille>: Bouteilles à supprimer
   */
  void updateList(List<Bouteille> remove) {
    for(Bouteille b: remove) {
      listValues.removeBouteille(b);
    }
  }

  /**
   * setBottles: Mise à jour de la liste des vins
   */
  public void setBottles(List<? extends IMyCellarObject> bottles) {
    listValues.setBouteilles(bottles);
  }


  /**
   * getListSize: Retourne le nombre de lignes de la liste.
   *
   * @return int
   */
  int getListSize() {
    return listValues.getRowCount();
  }

}
