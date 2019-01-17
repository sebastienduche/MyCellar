package mycellar;

import mycellar.core.MyCellarButton;
import mycellar.core.MyCellarLabel;
import net.miginfocom.swing.MigLayout;

import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.text.MessageFormat;
import java.util.LinkedList;
import java.util.Map;

/**
 * <p>Titre : Cave à vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2018</p>
 * <p>Société : Seb Informatique</p>
 * @author Sébastien Duché
 * @version 0.3
 * @since 11/01/19
 */
public class RangementCreationDialog extends JDialog {

  private final Map<String, LinkedList<Part>> map;
  private final MyCellarLabel end = new MyCellarLabel();
  private final RangementToCreateTableModel model;

  RangementCreationDialog(Map<String, LinkedList<Part>> map) {
    this.map = map;

    Debug("Constructor");
    setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    setModal(true);
    setTitle(Program.getLabel("Infos267"));
    setLayout(new MigLayout("","grow",""));
    setResizable(false);
    model = new RangementToCreateTableModel(map);
    JTable table = new JTable(model);
    end.setHorizontalAlignment(SwingConstants.CENTER);
    end.setForeground(Color.red);
    end.setFont(Program.FONT_DIALOG_SMALL);
    add(new JScrollPane(table), "grow, wrap");
    add(end, "grow, wrap");

    MyCellarButton valider = new MyCellarButton(Program.getLabel("Infos018"));
    valider.addActionListener(this::valider_actionPerformed);
    MyCellarButton annuler = new MyCellarButton(Program.getLabel("Infos019"));
    annuler.addActionListener((e) -> dispose());

    add(valider, "gaptop 15px, split 2, center");
    add(annuler);
    setSize(600, 500);
    setLocationRelativeTo(Start.getInstance());
    setVisible(true);
    Debug("Constructor OK");

  }

  private void valider_actionPerformed(ActionEvent actionEvent) {
    map.keySet().forEach(name -> {
      final LinkedList<Part> parts = map.get(name);
      int row = 0;
      for (Part part : parts) {
        row += part.getRowSize();
      }
      Rangement rangement;
      if (row == 0) {
        int part = parts.isEmpty() ? 1 : parts.size();
        Debug("Creating place: " + name + " parts: " + part);
        rangement = new Rangement(name, parts.isEmpty() ? 1 : parts.size(), 0, false, -1);
      } else {
        Debug("Creating complex place: " + name + " parts: " + parts);
        rangement = new Rangement(name, parts);
      }
      Program.addCave(rangement);
    });
    end.setText(MessageFormat.format(Program.getLabel("RangementToCreateTableModel.end"), map.size()));
    model.clear();
  }

  /**
   * Debug
   *
   * @param sText String
   */
  public static void Debug(String sText) {
    Program.Debug("RangementCreationDialog: " + sText);
  }
}
