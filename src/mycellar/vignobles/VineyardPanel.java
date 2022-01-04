package mycellar.vignobles;

import mycellar.Erreur;
import mycellar.ITabListener;
import mycellar.MyCellarImage;
import mycellar.Program;
import mycellar.Start;
import mycellar.core.IMyCellar;
import mycellar.core.IUpdatable;
import mycellar.core.LabelType;
import mycellar.core.UpdateViewType;
import mycellar.core.datas.jaxb.AppelationJaxb;
import mycellar.core.datas.jaxb.CountryJaxb;
import mycellar.core.datas.jaxb.CountryVignobleJaxb;
import mycellar.core.datas.jaxb.VignobleListJaxb;
import mycellar.core.tablecomponents.ButtonCellEditor;
import mycellar.core.tablecomponents.ButtonCellRenderer;
import mycellar.core.uicomponents.MyCellarButton;
import mycellar.core.uicomponents.MyCellarComboBox;
import mycellar.core.uicomponents.MyCellarLabel;
import mycellar.core.uicomponents.TabEvent;
import mycellar.general.ProgramPanels;
import net.miginfocom.swing.MigLayout;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.awt.event.ActionEvent;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static mycellar.MyCellarUtils.isDefined;

/**
 * Titre : Cave &agrave; vin
 * Description : Votre description
 * Copyright : Copyright (c) 2015
 * Soci&eacute;t&eacute; : Seb Informatique
 *
 * @author S&eacute;bastien Duch&eacute;
 * @version 3.3
 * @since 04/01/22
 */

public final class VineyardPanel extends JPanel implements ITabListener, IMyCellar, IUpdatable {

  private static final long serialVersionUID = 2661586945830305901L;

  private final MyCellarComboBox<CountryJaxb> comboCountry = new MyCellarComboBox<>();
  private final MyCellarComboBox<CountryVignobleJaxb> comboVignoble = new MyCellarComboBox<>();
  private final CountryJaxb emptyCountryJaxb = new CountryJaxb();
  private final MyCellarButton addVignoble = new MyCellarButton(LabelType.INFO_OTHER, "VineyardPanel.addVignoble", new AddVignobleAction());
  private final MyCellarButton delVignoble = new MyCellarButton(LabelType.INFO_OTHER, "VineyardPanel.delVignoble", new DelVignobleAction());
  private final MyCellarButton renameVignoble = new MyCellarButton(LabelType.INFO_OTHER, "VineyardPanel.renameVignoble", new RenameVignobleAction());
  private final MyCellarButton addAppellation = new MyCellarButton(LabelType.INFO_OTHER, "VineyardPanel.addAppellation", new AddAppellationAction());
  private final VineyardTableModel model = new VineyardTableModel();
  private VignobleListJaxb vignobleListJaxb = null;

  public VineyardPanel() {
    MyCellarLabel labelCountries = new MyCellarLabel(LabelType.INFO, "218"); // Selectionner un pays
    comboCountry.addItem(emptyCountryJaxb);
    Collections.sort(Program.getCountries());
    Program.getCountries().forEach(comboCountry::addItem);

    comboCountry.addActionListener((e) -> comboCountrySelected());
    comboVignoble.addActionListener((e) -> comboVignobleSelected());

    MyCellarLabel labelVineyard = new MyCellarLabel(LabelType.INFO, "166"); // Selectionner un vignoble
    MyCellarButton addCountry = new MyCellarButton(LabelType.INFO_OTHER, "VineyardPanel.addCountry", new AddCountryAction());
    MyCellarButton delCountry = new MyCellarButton(LabelType.INFO_OTHER, "VineyardPanel.delCountry", new DelCountryAction());
    setLayout(new MigLayout("", "grow", "[][grow]"));
    JPanel panelCombos = new JPanel();
    panelCombos.setLayout(new MigLayout("", "[][][][]", "[][]"));
    panelCombos.add(labelCountries);
    panelCombos.add(comboCountry);
    panelCombos.add(addCountry);
    panelCombos.add(delCountry, "wrap");
    panelCombos.add(labelVineyard);
    panelCombos.add(comboVignoble);
    panelCombos.add(addVignoble);
    panelCombos.add(delVignoble);
    panelCombos.add(renameVignoble);
    add(panelCombos, "wrap");
    JPanel panelAppellations = new JPanel();
    panelAppellations.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), Program.getLabel("Main.Appellations")));
    panelAppellations.setLayout(new MigLayout("", "grow", "[][grow]"));
    panelAppellations.add(addAppellation, "wrap");
    JTable tableAppellations = new JTable(model);
    panelAppellations.add(new JScrollPane(tableAppellations), "grow");
    add(panelAppellations, "grow");
    addVignoble.setEnabled(false);
    delVignoble.setEnabled(false);
    renameVignoble.setEnabled(false);
    addAppellation.setEnabled(false);
    tableAppellations.setAutoCreateRowSorter(true);
    TableRowSorter<TableModel> sorter = new TableRowSorter<>(tableAppellations.getModel());
    tableAppellations.setRowSorter(sorter);
    List<RowSorter.SortKey> sortKeys = new ArrayList<>();
    sortKeys.add(new RowSorter.SortKey(0, SortOrder.ASCENDING));
    sorter.setSortKeys(sortKeys);
    sorter.sort();
    TableColumnModel tcm = tableAppellations.getColumnModel();
    TableColumn tc = tcm.getColumn(VineyardTableModel.ACTION);
    tc.setCellRenderer(new ButtonCellRenderer("", MyCellarImage.DELETE));
    tc.setCellEditor(new ButtonCellEditor());
    tc.setMinWidth(25);
    tc.setMaxWidth(25);
  }

  private static void Debug(String sText) {
    Program.Debug("VineyardPanel: " + sText);
  }

  private void comboCountrySelected() {
    comboVignoble.removeAllItems();
    addVignoble.setEnabled(false);
    delVignoble.setEnabled(false);
    renameVignoble.setEnabled(false);
    addAppellation.setEnabled(false);
    if (comboCountry.getSelectedItem() != null && comboCountry.getSelectedItem().equals(emptyCountryJaxb)) {
      model.setAppellations(null, null);
      return;
    }
    addVignoble.setEnabled(true);
    CountryJaxb countryJaxb = (CountryJaxb) comboCountry.getSelectedItem();
    CountryVignobleController.getVignobles(countryJaxb)
        .ifPresentOrElse(vignobleListJaxb1 -> vignobleListJaxb = vignobleListJaxb1, () -> CountryVignobleController.createCountry(countryJaxb)
            .ifPresentOrElse(vignobleListJaxb1 -> vignobleListJaxb = vignobleListJaxb1,
                () -> Debug("ERROR: Unable to find country " + countryJaxb.getName())));

    vignobleListJaxb.getCountryVignobleJaxbList().stream()
        .filter(Objects::nonNull)
        .forEach(comboVignoble::addItem);

    if (comboVignoble.getItemCount() > 0) {
      CountryVignobleJaxb countryVignobleJaxb = (CountryVignobleJaxb) comboVignoble.getSelectedItem();
      if (countryVignobleJaxb != null) {
        model.setAppellations(countryVignobleJaxb, countryVignobleJaxb.getAppelation());
      }
    } else {
      model.setAppellations(null, null);
      addAppellation.setEnabled(false);
    }
    comboVignoble.setEnabled(comboVignoble.getItemCount() > 0);
    delVignoble.setEnabled(comboVignoble.getItemCount() > 0);
    renameVignoble.setEnabled(comboVignoble.getItemCount() > 0);
  }

  private void comboVignobleSelected() {
    if (comboVignoble.getSelectedItem() != null) {
      CountryVignobleJaxb countryVignobleJaxb = (CountryVignobleJaxb) comboVignoble.getSelectedItem();
      model.setAppellations(countryVignobleJaxb, countryVignobleJaxb.getAppelation());
      addAppellation.setEnabled(true);
    }
  }

  @Override
  public void setUpdateView(UpdateViewType updateViewType) {
  }

  @Override
  public void updateView() {
    if (CountryVignobleController.isRebuildNeeded()) {
      CountryVignobleController.rebuild();
    }
  }

  @Override
  public boolean tabWillClose(TabEvent event) {
    return true;
  }

  @Override
  public void tabClosed() {
    comboCountry.setSelectedIndex(0);
    model.setAppellations(null, null);
    ProgramPanels.updateAllPanelsForUpdatingVineyard();
    Start.getInstance().updateMainPanel();
  }

  private class AddVignobleAction extends AbstractAction {

    private static final long serialVersionUID = 2164410331118124652L;

    private AddVignobleAction() {
      super("", MyCellarImage.ADD);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      String val = JOptionPane.showInputDialog(Program.getLabel("VineyardPanel.addVignobleQuestion"));
      if (isDefined(val)) {
        CountryVignobleJaxb countryVignobleJaxb = new CountryVignobleJaxb();
        countryVignobleJaxb.setName(val);
        if (!vignobleListJaxb.getCountryVignobleJaxbList().contains(countryVignobleJaxb)) {
          countryVignobleJaxb = vignobleListJaxb.addVignoble(val);
          comboVignoble.setEnabled(true);
          comboVignoble.addItem(countryVignobleJaxb);
          comboVignoble.setSelectedItem(countryVignobleJaxb);
          delVignoble.setEnabled(true);
          renameVignoble.setEnabled(true);
          CountryVignobleController.setModified();
        }
      }
    }
  }

  class DelVignobleAction extends AbstractAction {

    private static final long serialVersionUID = 2839462637218767338L;

    private DelVignobleAction() {
      super("", MyCellarImage.DELETE);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      CountryVignobleJaxb countryVignobleJaxb = (CountryVignobleJaxb) comboVignoble.getSelectedItem();
      CountryJaxb countryJaxb = (CountryJaxb) comboCountry.getSelectedItem();
      if (countryVignobleJaxb != null) {
        CountryVignobleController.rebuild();
        if (CountryVignobleController.isVignobleUsed(countryJaxb, countryVignobleJaxb)) {
          JOptionPane.showMessageDialog(Start.getInstance(), Program.getLabel("VineyardPanel.unableDeleteVignoble"), Program.getLabel("Infos032"), JOptionPane.ERROR_MESSAGE);
          return;
        }
        if (JOptionPane.NO_OPTION == JOptionPane.showConfirmDialog(Start.getInstance(), MessageFormat.format(Program.getLabel("VineyardPanel.delVignobleQuestion"), countryVignobleJaxb.getName()), Program.getLabel("Infos049"), JOptionPane.YES_NO_OPTION)) {
          return;
        }
        comboVignoble.removeItemAt(comboVignoble.getSelectedIndex());
        delVignoble.setEnabled(comboVignoble.getItemCount() > 0);
        renameVignoble.setEnabled(comboVignoble.getItemCount() > 0);
        vignobleListJaxb.delVignoble(countryVignobleJaxb);
        CountryVignobleController.setModified();
      }
    }
  }

  class RenameVignobleAction extends AbstractAction {

    private static final long serialVersionUID = 2912399011575692147L;

    private RenameVignobleAction() {
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      CountryVignobleJaxb countryVignobleJaxb = (CountryVignobleJaxb) comboVignoble.getSelectedItem();
      if (countryVignobleJaxb != null) {
        String val = JOptionPane.showInputDialog(MessageFormat.format(Program.getLabel("VineyardPanel.renameVignobleQuestion"), countryVignobleJaxb.getName()));
        if (isDefined(val)) {
          CountryVignobleController.renameVignoble(countryVignobleJaxb, val);
          comboVignoble.updateUI();
          CountryVignobleController.setModified();
        }
      }
    }
  }

  class AddAppellationAction extends AbstractAction {

    private static final long serialVersionUID = 2174872605239470622L;

    private AddAppellationAction() {
      super("", MyCellarImage.ADD);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      String val = JOptionPane.showInputDialog(Program.getLabel("VineyardPanel.addAppellationQuestion"));
      if (isDefined(val)) {
        AppelationJaxb v = new AppelationJaxb();
        v.setAOC(val);
        model.addAppellation(v);
      }
    }
  }

  class AddCountryAction extends AbstractAction {

    private static final long serialVersionUID = -6725950975161352023L;

    private AddCountryAction() {
      super("", MyCellarImage.ADD);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      String val = JOptionPane.showInputDialog(Program.getLabel("VineyardPanel.addCountryQuestion"));
      if (isDefined(val)) {
        if (CountryVignobleController.hasCountryWithName(val)) {
          Erreur.showSimpleErreur(Program.getError("VineyardPanel.CountryExist"));
          return;
        }
        CountryJaxb countryJaxb = new CountryJaxb(val);
        CountryVignobleController.createCountry(countryJaxb);
        comboCountry.addItem(countryJaxb);
        CountryVignobleController.setModified();
      }
    }
  }

  class DelCountryAction extends AbstractAction {

    private static final long serialVersionUID = -2587952745857642464L;

    private DelCountryAction() {
      super("", MyCellarImage.DELETE);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      CountryJaxb countryJaxb = (CountryJaxb) comboCountry.getSelectedItem();
      if (countryJaxb == null) {
        return;
      }
      CountryVignobleController.getVignobles(countryJaxb).ifPresent(vignoble -> {
        CountryVignobleController.rebuild();
        for (CountryVignobleJaxb countryVignobleJaxb : vignoble.getCountryVignobleJaxbList()) {
          if (CountryVignobleController.isVignobleUsed(countryJaxb, countryVignobleJaxb)) {
            JOptionPane.showMessageDialog(Start.getInstance(), Program.getLabel("VineyardPanel.unableDeleteCountry"), Program.getLabel("Infos032"), JOptionPane.ERROR_MESSAGE);
            return;
          }
        }
        if (JOptionPane.NO_OPTION == JOptionPane.showConfirmDialog(Start.getInstance(), MessageFormat.format(Program.getLabel("VineyardPanel.delCountryQuestion"), countryJaxb), Program.getLabel("Infos049"), JOptionPane.YES_NO_OPTION)) {
          return;
        }
        CountryVignobleController.deleteCountry(countryJaxb);
        comboCountry.removeItem(countryJaxb);
        CountryVignobleController.setModified();
      });
    }
  }

}
