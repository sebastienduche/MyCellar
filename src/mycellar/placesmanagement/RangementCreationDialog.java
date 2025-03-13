package mycellar.placesmanagement;

import mycellar.Program;
import mycellar.core.uicomponents.MyCellarButton;
import mycellar.core.uicomponents.MyCellarSimpleLabel;
import mycellar.frame.MainFrame;
import mycellar.placesmanagement.places.AbstractPlace;
import mycellar.placesmanagement.places.ComplexPlaceBuilder;
import mycellar.placesmanagement.places.Part;
import mycellar.placesmanagement.places.SimplePlaceBuilder;
import net.miginfocom.swing.MigLayout;

import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.io.Serial;
import java.util.LinkedList;
import java.util.Map;

import static mycellar.ProgramConstants.FONT_DIALOG_BOLD;
import static mycellar.core.text.MyCellarLabelManagement.getLabel;
import static mycellar.general.ResourceKey.MAIN_CLOSE;
import static mycellar.general.ResourceKey.MAIN_CREATE;
import static mycellar.general.ResourceKey.MAIN_STORAGETOCREATE;
import static mycellar.general.ResourceKey.RANGEMENTTOCREATETABLEMODEL_END;

/**
 * Titre : Cave &agrave; vin
 * Description : Votre description
 * Copyright : Copyright (c) 20018
 * Soci&eacute;t&eacute; : Seb Informatique
 *
 * @author S&eacute;bastien Duch&eacute;
 * @version 1.5
 * @since 13/03/25
 */
public final class RangementCreationDialog extends JDialog {

  @Serial
  private static final long serialVersionUID = 5075363436018889969L;
  private final Map<String, LinkedList<Part>> map;
  private final MyCellarSimpleLabel end = new MyCellarSimpleLabel();
  private final RangementToCreateTableModel model;

  public RangementCreationDialog(Map<String, LinkedList<Part>> map) {
    this.map = map;

    Debug("Constructor");
    setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    setModal(true);
    setTitle(getLabel(MAIN_STORAGETOCREATE));
    setLayout(new MigLayout("", "grow", ""));
    setResizable(false);
    model = new RangementToCreateTableModel(map);
    JTable table = new JTable(model);
    end.setHorizontalAlignment(SwingConstants.CENTER);
    end.setForeground(Color.red);
    end.setFont(FONT_DIALOG_BOLD);
    add(new JScrollPane(table), "grow, wrap");
    add(end, "grow, wrap");

    MyCellarButton valider = new MyCellarButton(MAIN_CREATE);
    valider.addActionListener(this::valider_actionPerformed);
    MyCellarButton annuler = new MyCellarButton(MAIN_CLOSE);
    annuler.addActionListener((e) -> dispose());

    add(valider, "gaptop 15px, split 2, center");
    add(annuler);
    setSize(600, 500);
    setLocationRelativeTo(MainFrame.getInstance());
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
        row += part.rows().size();
      }
      AbstractPlace abstractPlace;
      if (row == 0) {
        int part = parts.isEmpty() ? 1 : parts.size();
        Debug("Creating place: " + name + " parts: " + part);
        abstractPlace = new SimplePlaceBuilder(name)
            .nbParts(parts.isEmpty() ? 1 : parts.size()).build();
      } else {
        Debug("Creating complex place: " + name + " parts: " + parts);
        abstractPlace = new ComplexPlaceBuilder(name).withPartList(parts).build();
      }
      Program.addPlace(abstractPlace);
      MainFrame.updateManagePlaceButton();
    });
    end.setText(getLabel(RANGEMENTTOCREATETABLEMODEL_END, map.size()));
    model.clear();
  }
}
