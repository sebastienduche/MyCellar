package mycellar;

import mycellar.core.IMyCellar;
import mycellar.core.LabelType;
import mycellar.core.MyCellarButton;
import mycellar.core.MyCellarComboBox;
import mycellar.core.MyCellarLabel;
import mycellar.countries.Country;
import mycellar.vignobles.Appelation;
import mycellar.vignobles.CountryVignoble;
import mycellar.vignobles.CountryVignobles;
import mycellar.vignobles.Vignobles;
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

/**
 * Titre : Cave &agrave; vin
 * Description : Votre description
 * Copyright : Copyright (c) 2015
 * Soci&eacute;t&eacute; : Seb Informatique
 *
 * @author S&eacute;bastien Duch&eacute;
 * @version 2.3
 * @since 06/11/20
 */

public class VineyardPanel extends JPanel implements ITabListener, IMyCellar {

	private static final long serialVersionUID = 2661586945830305901L;

	private final MyCellarComboBox<Country> comboCountry = new MyCellarComboBox<>();
	private final MyCellarComboBox<CountryVignoble> comboVignoble = new MyCellarComboBox<>();
	private final Country emptyCountry = new Country();
	private final MyCellarButton addVignoble = new MyCellarButton(LabelType.INFO_OTHER, "VineyardPanel.addVignoble", new AddVignobleAction());
	private final MyCellarButton delVignoble = new MyCellarButton(LabelType.INFO_OTHER, "VineyardPanel.delVignoble", new DelVignobleAction());
	private final MyCellarButton renameVignoble = new MyCellarButton(LabelType.INFO_OTHER, "VineyardPanel.renameVignoble", new RenameVignobleAction());
	private final MyCellarButton addAppellation = new MyCellarButton(LabelType.INFO_OTHER, "VineyardPanel.addAppellation", new AddAppellationAction());
	private Vignobles vignobles = null;
	private final VineyardTableModel model = new VineyardTableModel();

	public VineyardPanel() {
		MyCellarLabel labelCountries = new MyCellarLabel(Program.getLabel("Infos218")); // Selectionner un pays
		comboCountry.addItem(emptyCountry);
		Collections.sort(Program.getCountries());
		for(Country c : Program.getCountries()) {
			comboCountry.addItem(c);
		}
		comboCountry.addActionListener((e) -> {

			comboVignoble.removeAllItems();
			addVignoble.setEnabled(false);
			delVignoble.setEnabled(false);
			renameVignoble.setEnabled(false);
			addAppellation.setEnabled(false);
			if(comboCountry.getSelectedItem() != null && comboCountry.getSelectedItem().equals(emptyCountry)) {
				model.setAppellations(null, null, null);
				return;
			}
			addVignoble.setEnabled(true);
			Country country = (Country) comboCountry.getSelectedItem();
			CountryVignobles.getVignobles(country)
				.ifPresentOrElse(vignobles1 -> vignobles = vignobles1, () -> CountryVignobles.createVignoblesCountry(country)
					.ifPresentOrElse(vignobles1 -> vignobles = vignobles1,
							() -> Debug("ERROR: Unable to find country " + country.getName())));

			for (CountryVignoble v : vignobles.getVignoble()) {
				if (v.getName().isEmpty()) {
					continue;
				}
				comboVignoble.addItem(v);
			}
			if (comboVignoble.getItemCount() > 0) {
				CountryVignoble countryVignoble = (CountryVignoble)comboVignoble.getSelectedItem();
				if (countryVignoble != null) {
					model.setAppellations(country, countryVignoble, countryVignoble.getAppelation());
				}
			}	else {
				model.setAppellations(null, null, null);
				addAppellation.setEnabled(false);
			}
			comboVignoble.setEnabled(comboVignoble.getItemCount() > 0);
			delVignoble.setEnabled(comboVignoble.getItemCount() > 0);
			renameVignoble.setEnabled(comboVignoble.getItemCount() > 0);
		});

		comboVignoble.addActionListener((e) -> {

			if (comboVignoble.getSelectedItem() != null) {
				CountryVignoble countryVignoble = (CountryVignoble) comboVignoble.getSelectedItem();
				model.setAppellations((Country) comboCountry.getSelectedItem(), countryVignoble, countryVignoble.getAppelation());
				addAppellation.setEnabled(true);
			}
		});
		MyCellarLabel labelVineyard = new MyCellarLabel(LabelType.INFO, "166"); // Selectionner un vignoble
		MyCellarButton addCountry = new MyCellarButton(LabelType.INFO_OTHER, "VineyardPanel.addCountry", new AddCountryAction());
		MyCellarButton delCountry = new MyCellarButton(LabelType.INFO_OTHER, "VineyardPanel.delCountry", new DelCountryAction());
		setLayout(new MigLayout("", "grow",""));
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
		panelAppellations.setLayout(new MigLayout("", "grow", "grow"));
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
		tc.setCellRenderer(new StateButtonRenderer("", MyCellarImage.DELETE));
		tc.setCellEditor(new StateButtonEditor());
		tc.setMinWidth(25);
		tc.setMaxWidth(25);
	}

	private class AddVignobleAction extends AbstractAction {

		private static final long serialVersionUID = 2164410331118124652L;

		private AddVignobleAction() {
			super("", MyCellarImage.ADD);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			String val = JOptionPane.showInputDialog(Program.getLabel("VineyardPanel.addVignobleQuestion"));
			if (val != null && !val.isEmpty()) {
				CountryVignoble countryVignoble = new CountryVignoble();
				countryVignoble.setName(val);
				if(!vignobles.getVignoble().contains(countryVignoble)) {
					countryVignoble = vignobles.addVignoble(val);
					comboVignoble.setEnabled(true);
					comboVignoble.addItem(countryVignoble);
					comboVignoble.setSelectedItem(countryVignoble);
					delVignoble.setEnabled(true);
					renameVignoble.setEnabled(true);
					Program.setModified();
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
			CountryVignoble countryVignoble = (CountryVignoble) comboVignoble.getSelectedItem();
			Country country = (Country) comboCountry.getSelectedItem();
			if (countryVignoble != null) {
				if (JOptionPane.NO_OPTION == JOptionPane.showConfirmDialog(Start.getInstance(), MessageFormat.format(Program.getLabel("VineyardPanel.delVignobleQuestion"), countryVignoble.getName()), Program.getLabel("Infos049"), JOptionPane.YES_NO_OPTION)) {
					return;
				}

				CountryVignobles.rebuild();
				if (CountryVignobles.isVignobleUsed(country, countryVignoble)) {
					JOptionPane.showMessageDialog(Start.getInstance(), Program.getLabel("VineyardPanel.unableDeleteVignoble"), Program.getLabel("Infos032"), JOptionPane.ERROR_MESSAGE);
					return;
				}
				comboVignoble.removeItemAt(comboVignoble.getSelectedIndex());
				delVignoble.setEnabled(comboVignoble.getItemCount() > 0);
				renameVignoble.setEnabled(comboVignoble.getItemCount() > 0);
				vignobles.delVignoble(countryVignoble);
				Program.setModified();
			}
		}
	}

	class RenameVignobleAction extends AbstractAction {

		private static final long serialVersionUID = 2912399011575692147L;

		private RenameVignobleAction() {
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			CountryVignoble countryVignoble = (CountryVignoble) comboVignoble.getSelectedItem();
			Country country = (Country) comboCountry.getSelectedItem();
			if (countryVignoble != null) {
				String val = JOptionPane.showInputDialog(MessageFormat.format(Program.getLabel("VineyardPanel.renameVignobleQuestion"), countryVignoble.getName()));
				if (val != null && !val.isEmpty()) {
					CountryVignobles.renameVignoble(country, countryVignoble, val);
					comboVignoble.updateUI();
					Program.setModified();
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
			if (val != null && !val.isEmpty()) {
				Appelation v = new Appelation();
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
			if (val != null && !val.isEmpty()) {
				Country country = new Country(val);
				if(CountryVignobles.hasCountryByName(country)) {
					Erreur.showSimpleErreur(Program.getError("VineyardPanel.CountryExist"));
					return;
				}
				CountryVignobles.createVignoblesCountry(country);
				comboCountry.addItem(country);
				Program.setModified();
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
			Country country = (Country) comboCountry.getSelectedItem();
			if(country == null) {
				return;
			}
			CountryVignobles.getVignobles(country).ifPresent(vignoble -> {
				CountryVignobles.rebuild();
				for(CountryVignoble countryVignoble : vignoble.getVignoble()) {
					if(CountryVignobles.isVignobleUsed(country, countryVignoble)) {
						JOptionPane.showMessageDialog(Start.getInstance(), Program.getLabel("VineyardPanel.unableDeleteCountry"), Program.getLabel("Infos032"), JOptionPane.ERROR_MESSAGE);
						return;
					}
				}
				if(JOptionPane.NO_OPTION == JOptionPane.showConfirmDialog(Start.getInstance(), MessageFormat.format(Program.getLabel("VineyardPanel.delCountryQuestion"), country), Program.getLabel("Infos049"), JOptionPane.YES_NO_OPTION)) {
					return;
				}
				CountryVignobles.deleteCountry(country);
				comboCountry.removeItem(country);
				Program.setModified();
			});
		}
	}

	/**
	 * Debug
	 *
	 * @param sText String
	 */
	private static void Debug(String sText) {
		Program.Debug("VineyardPanel: " + sText );
	}

	@Override
	public boolean tabWillClose(TabEvent event) {
		if(model.isModified()) {
			Program.setModified();
			model.setModified(false);
		}
		return true;
	}

	@Override
	public void tabClosed() {
		comboCountry.setSelectedIndex(0);
		model.setAppellations(null, null, null);
		Program.updateAllPanels();
		Start.getInstance().updateMainPanel();
	}

}
