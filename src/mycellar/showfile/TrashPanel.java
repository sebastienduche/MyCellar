package mycellar.showfile;


import mycellar.ITabListener;
import mycellar.MyCellarImage;
import mycellar.Program;
import mycellar.core.IMyCellar;
import mycellar.core.IUpdatable;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;

import static mycellar.core.text.MyCellarLabelManagement.getLabel;
import static mycellar.general.ResourceKey.SHOWFILE_RESTORE;

/**
 * Titre : Cave &agrave; vin
 * Description : Votre description
 * Copyright : Copyright (c) 1998
 * Societe : Seb Informatique
 *
 * @author S&eacute;bastien Duch&eacute;
 * @version 0.3
 * @since 03/10/25
 */

public class TrashPanel extends AbstractShowFilePanel implements ITabListener, IMyCellar, IUpdatable {

  public TrashPanel() {
    super(false);
    init();
  }

  private void init() {
    titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
    setLayout(new MigLayout("", "[][grow]", "[]10px[grow][]"));
    add(titleLabel, "align left");

    deleteButton.setText(getLabel(SHOWFILE_RESTORE));
    deleteButton.setIcon(MyCellarImage.RESTORE);
    deleteButton.addActionListener(e -> restore());
    add(deleteButton, "align right, wrap");

    model = new TableShowValues();
    table = new JTable(model);

    postInit();

    refresh();
    addTableSorter();
    updateModel(false, false);
  }

  @Override
  protected void refresh() {
    SwingUtilities.invokeLater(() -> {
      model.setBottles(Program.getTrash());
      labelCount.setValue(Integer.toString(model.getRowCount()));
    });
  }

  public void Debug(String text) {
    Program.Debug("TrashPanel: " + text);
  }

}
