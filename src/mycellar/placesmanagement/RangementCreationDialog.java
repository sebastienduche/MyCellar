package mycellar.placesmanagement;

import mycellar.Program;
import mycellar.Start;
import mycellar.core.uicomponents.MyCellarButton;
import mycellar.core.uicomponents.MyCellarLabel;
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

import static mycellar.ProgramConstants.FONT_DIALOG_SMALL;
import static mycellar.core.text.MyCellarLabelManagement.getLabel;

/**
 * <p>Titre : Cave &agrave; vin
 * <p>Description : Votre description
 * <p>Copyright : Copyright (c) 20018
 * <p>Soci&eacute;t&eacute; : Seb Informatique
 *
 * @author S&eacute;bastien Duch&eacute;
 * @version 0.7
 * @since 20/01/22
 */
public final class RangementCreationDialog extends JDialog {

  private static final long serialVersionUID = 5075363436018889969L;
  private final Map<String, LinkedList<Part>> map;
  private final MyCellarLabel end = new MyCellarLabel();
  private final RangementToCreateTableModel model;

  public RangementCreationDialog(Map<String, LinkedList<Part>> map) {
    this.map = map;

    Debug("Constructor");
    setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    setModal(true);
    setTitle(getLabel("Infos267"));
    setLayout(new MigLayout("", "grow", ""));
    setResizable(false);
    model = new RangementToCreateTableModel(map);
    JTable table = new JTable(model);
    end.setHorizontalAlignment(SwingConstants.CENTER);
    end.setForeground(Color.red);
    end.setFont(FONT_DIALOG_SMALL);
    add(new JScrollPane(table), "grow, wrap");
    add(end, "grow, wrap");

    MyCellarButton valider = new MyCellarButton(getLabel("Infos018"));
    valider.addActionListener(this::valider_actionPerformed);
    MyCellarButton annuler = new MyCellarButton(getLabel("Infos019"));
    annuler.addActionListener((e) -> dispose());

    add(valider, "gaptop 15px, split 2, center");
    add(annuler);
    setSize(600, 500);
    setLocationRelativeTo(Start.getInstance());
    setVisible(true);
    Debug("Constructor Done");
  }

  public static void Debug(String text) {
    Program.Debug("RangementCreationDialog: " + text);
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
        rangement = new Rangement.SimplePlaceBuilder(name)
            .nbParts(parts.isEmpty() ? 1 : parts.size()).build();
      } else {
        Debug("Creating complex place: " + name + " parts: " + parts);
        rangement = new Rangement(name, parts);
      }
      Program.addPlace(rangement);
    });
    end.setText(MessageFormat.format(getLabel("RangementToCreateTableModel.end"), map.size()));
    model.clear();
  }
}
