package mycellar;

import mycellar.core.MyCellarLabel;
import net.miginfocom.swing.MigLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
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
 * @version 3.6
 * @since 02/03/18
 */
class ListVin extends JPanel {
  private JTable table;
  private ListValues tv;

  private ListEditor le;
  static final long serialVersionUID = 10805;

  /**
   * ListVin: Constructeur avec liste des bouteilles
   *
   * @param bottle LinkedList<Bouteille>: Liste des bouteilles.
   */
  public ListVin(LinkedList<Bouteille> bottle) {

    try {
    	tv = new ListValues();     
        tv.setBouteilles(bottle);
        table = new JTable(tv);

        TableColumnModel tcm = table.getColumnModel();

        TableColumn tc1 = tcm.getColumn(0);
        tc1.setCellRenderer(new ToolTipRenderer());
        le = new ListEditor();
        tc1.setCellEditor(le);
        ListSelectionModel rowSM = table.getSelectionModel();
        rowSM.setSelectionInterval(0, 0);
        LinkedList<Bouteille> list = new LinkedList<>();
        list.add(bottle.getFirst());
        rowSM.addListSelectionListener( (e) -> {
            //Ignore extra messages.
            ListSelectionModel lsm = (ListSelectionModel) e.getSource();
            if (!lsm.isSelectionEmpty()) {
              int minSelectedRow = lsm.getMinSelectionIndex();
              int maxSelectedRow = lsm.getMaxSelectionIndex();
              LinkedList<Bouteille> list1 = new LinkedList<>();
              for (int x = minSelectedRow; x <= maxSelectedRow; x++) {
                if (lsm.isSelectedIndex(x)) {
                  list1.add(tv.getBouteille(x));
                }
              }
              le.putList(list1);
              le.getTableCellEditorComponent(table, le.getCellEditorValue(), true, maxSelectedRow, 0);
              //selectedRow is selected
            }
        });
        
        JScrollPane scrollpane = new JScrollPane(table);
        MyCellarLabel MyCellarLabel2 = new MyCellarLabel(Program.getLabel("Infos173"));

        scrollpane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        setLayout(new MigLayout("","grow","[grow][]"));
        MyCellarLabel textControl1 = new MyCellarLabel();
        textControl1.setForeground(Color.red);
        textControl1.setFont(new Font("Dialog", Font.BOLD, 13));
        textControl1.setText(Program.getLabel("Infos124"));
        textControl1.setHorizontalAlignment(SwingConstants.CENTER);

        add(scrollpane,"grow,wrap,width min(100,200)");
        add(MyCellarLabel2,"width min(100,200)");
        setVisible(true);
	} catch (Exception e) {
		Program.showException(e);
	}
  }

  /**
   * setAddVin: Mise à jour de la référence vers AddVin
   *
   * @param av AddVin
   */
  public void setAddVin(AddVin av) {
    le.setAddVin(av);
  }

  /**
   * updateList: Mise à jour de la liste des vins
   *
   * @param remove LinkedList<Bouteille>: Bouteilles à supprimer
   */
  public void updateList(List<Bouteille> remove) {
    for(Bouteille b: remove)
      tv.removeBouteille(b);
  }
  
  /**
   * setBottles: Mise à jour de la liste des vins
   */
  public void setBottles(List<Bouteille> bottles) {
	  tv.setBouteilles(bottles);
  }


  /**
   * getListSize: Retourne le nombre de lignes de la liste.
   *
   * @return int
   */
  public int getListSize() {
    return tv.getRowCount();
  }

}
