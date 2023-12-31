package mycellar.vignobles;

import mycellar.core.datas.jaxb.AppelationJaxb;
import mycellar.core.datas.jaxb.CountryVignobleJaxb;
import mycellar.frame.MainFrame;

import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import java.text.MessageFormat;
import java.util.List;

import static mycellar.core.text.MyCellarLabelManagement.getError;
import static mycellar.core.text.MyCellarLabelManagement.getLabel;

/**
 * Titre : Cave &agrave; vin
 * Description : Votre description
 * Copyright : Copyright (c) 2015
 * Soci&eacute;t&eacute; : Seb Informatique
 *
 * @author S&eacute;bastien Duch&eacute;
 * @version 1.6
 * @since 31/12/23
 */

class VineyardTableModel extends DefaultTableModel {

  static final int ACTION = 2;
  private List<AppelationJaxb> appellationJaxbs;
  private CountryVignobleJaxb vignoble;

  @Override
  public int getColumnCount() {
    return 3;
  }

  @Override
  public String getColumnName(int column) {
    return switch (column) {
      case 0 -> getLabel("Main.AppellationAOC");
      case 1 -> getLabel("Main.AppellationIGP");
      default -> "";
    };
  }

  @Override
  public int getRowCount() {
    return appellationJaxbs == null ? 0 : appellationJaxbs.size();
  }

  @Override
  public Object getValueAt(int row, int column) {
    if (appellationJaxbs == null) {
      return "";
    }

    AppelationJaxb appelationJaxb = appellationJaxbs.get(row);
    return switch (column) {
      case 0 -> appelationJaxb.getAOC();
      case 1 -> appelationJaxb.getIGP();
      case 2 -> Boolean.FALSE;
      default -> "";
    };
  }

  @Override
  public void setValueAt(Object aValue, int row, int column) {
    if (appellationJaxbs == null) {
      return;
    }

    AppelationJaxb appelationJaxb = appellationJaxbs.get(row);
    switch (column) {
      case 0:
        CountryVignobleController.setModified();
        CountryVignobleController.renameAOC(vignoble, appelationJaxb, (String) aValue);
        break;
      case 1:
        CountryVignobleController.setModified();
        CountryVignobleController.renameIGP(vignoble, appelationJaxb, (String) aValue);
        break;
      case 2:
        String name = appelationJaxb.getAOC() != null ? appelationJaxb.getAOC() : appelationJaxb.getIGP();
        CountryVignobleController.rebuild();
        if (CountryVignobleController.isAppellationUsed(appelationJaxb)) {
          JOptionPane.showMessageDialog(MainFrame.getInstance(), getLabel("VineyardPanel.UnableDeleteAppellation"), getError("Error.error"), JOptionPane.ERROR_MESSAGE);
          return;
        }

        if (JOptionPane.YES_OPTION != JOptionPane.showConfirmDialog(MainFrame.getInstance(), MessageFormat.format(getLabel("VineyardPanel.DelAppellationQuestion"), name), getLabel("Main.AskConfirmation"), JOptionPane.YES_NO_OPTION)) {
          return;
        }
        CountryVignobleController.setModified();
        appellationJaxbs.remove(appelationJaxb);
        fireTableDataChanged();
        break;
    }
  }

  void setAppellations(CountryVignobleJaxb vignoble, List<AppelationJaxb> appellationJaxbs) {
    this.vignoble = vignoble;
    this.appellationJaxbs = appellationJaxbs;
    fireTableDataChanged();
  }

  void addAppellation(AppelationJaxb appellation) {
    appellationJaxbs.add(appellation);
    fireTableDataChanged();
    CountryVignobleController.setModified();
  }
}
