package Cave;

import java.awt.event.ActionEvent;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

import net.miginfocom.swing.MigLayout;
import Cave.vignobles.Appelation;
import Cave.vignobles.CountryVignoble;
import Cave.vignobles.Vignobles;
import Cave.core.MyCellarButton;
import Cave.core.MyCellarComboBox;
import Cave.core.MyCellarLabel;
import Cave.countries.Country;
import Cave.vignobles.CountryVignobles;

/**
 * Titre : Cave à vin
 * Description : Votre description
 * Copyright : Copyright (c) 2015
 * Société : Seb Informatique
 * 
 * @author Sébastien Duché
 * @version 1.5
 * @since 19/02/17
 */

public class VineyardPanel extends JPanel implements ITabListener {

	private static final long serialVersionUID = 2661586945830305901L;

	private MyCellarLabel labelCountries = new MyCellarLabel();
	private MyCellarComboBox<Country> comboCountry = new MyCellarComboBox<Country>();
	private MyCellarLabel labelVineyard = new MyCellarLabel();
	private MyCellarComboBox<CountryVignoble> comboVignoble = new MyCellarComboBox<CountryVignoble>();
	private Country emptyCountry = new Country();
	private MyCellarButton addVignoble = new MyCellarButton(new AddVignobleAction());
	private MyCellarButton delVignoble = new MyCellarButton(new DelVignobleAction());
	private MyCellarButton renameVignoble = new MyCellarButton(new RenameVignobleAction());
	private MyCellarButton addAppellation = new MyCellarButton(new AddAppellationAction());
	private MyCellarButton addCountry = new MyCellarButton(new AddCountryAction());
	private Vignobles vignobles = null;
	private VineyardTableModel model = new VineyardTableModel();
	private JTable tableAppellations = new JTable(model);

	public VineyardPanel() {
		labelCountries.setText(Program.getLabel("Infos218")); // Sélectionner un pays
		comboCountry.addItem(emptyCountry);
		Collections.sort(Program.getCountries());
		for(Country c : Program.getCountries())
			comboCountry.addItem(c);
		comboCountry.addActionListener((e) -> {

			comboVignoble.removeAllItems();
			addVignoble.setEnabled(false);
			delVignoble.setEnabled(false);
			renameVignoble.setEnabled(false);
			addAppellation.setEnabled(false);
			if(comboCountry.getSelectedItem().equals(emptyCountry)) {
				model.setAppellations(null, null, null);
				return;
			}
			addVignoble.setEnabled(true);
			Country country = (Country) comboCountry.getSelectedItem();
			vignobles = CountryVignobles.getVignobles(country);
			if(vignobles == null) {
				CountryVignobles.createCountry(country);
				vignobles = CountryVignobles.getVignobles(country);
				if(vignobles == null) {
					Debug("ERROR: Unable to find country "+country.getName());
				}
			}
			for(CountryVignoble v : vignobles.getVignoble()) {
				if(v.getName().isEmpty())
					continue;
				comboVignoble.addItem(v);
			}
			if(comboVignoble.getItemCount() > 0) {
				CountryVignoble countryVignoble = (CountryVignoble)comboVignoble.getSelectedItem();
				model.setAppellations(country, countryVignoble, countryVignoble.getAppelation());
			}
			else {
				model.setAppellations(null, null, null);
				addAppellation.setEnabled(false);
			}
			comboVignoble.setEnabled(comboVignoble.getItemCount() > 0);
			delVignoble.setEnabled(comboVignoble.getItemCount() > 0);
			renameVignoble.setEnabled(comboVignoble.getItemCount() > 0);
		});

		comboVignoble.addActionListener((e) -> {

			if(comboVignoble.getSelectedItem() != null) {
				CountryVignoble countryVignoble = (CountryVignoble)comboVignoble.getSelectedItem();
				model.setAppellations((Country) comboCountry.getSelectedItem(), countryVignoble, countryVignoble.getAppelation());
				addAppellation.setEnabled(true);
			}
		});
		labelVineyard.setText(Program.getLabel("Infos166")); // Sélectionner un vignoble
		setLayout(new MigLayout("", "grow",""));
		JPanel panelCombos = new JPanel();
		panelCombos.setLayout(new MigLayout("", "[][][][]", "[][]"));
		panelCombos.add(labelCountries);
		panelCombos.add(comboCountry);
		panelCombos.add(addCountry, "wrap");
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

	class AddVignobleAction extends AbstractAction {

		private static final long serialVersionUID = 2164410331118124652L;

		public AddVignobleAction() {
			super(Program.getLabel("VineyardPanel.addVignoble"), MyCellarImage.ADD);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			String val = JOptionPane.showInputDialog(Program.getLabel("VineyardPanel.addVignobleQuestion"));
			if(val != null && !val.isEmpty()) {
				CountryVignoble v = new CountryVignoble();
				v.setName(val);
				if(!vignobles.getVignoble().contains(v)) {
					v = vignobles.addVignoble(val);
					comboVignoble.setEnabled(true);
					comboVignoble.addItem(v);
					comboVignoble.setSelectedItem(v);
					delVignoble.setEnabled(true);
					renameVignoble.setEnabled(true);
					Program.setModified();
				}
			}
		}
	}

	class DelVignobleAction extends AbstractAction {

		private static final long serialVersionUID = 2839462637218767338L;

		public DelVignobleAction() {
			super(Program.getLabel("VineyardPanel.delVignoble"), MyCellarImage.DELETE);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			CountryVignoble v = (CountryVignoble) comboVignoble.getSelectedItem();
			if(JOptionPane.NO_OPTION == JOptionPane.showConfirmDialog(null, MessageFormat.format(Program.getLabel("VineyardPanel.delVignobleQuestion"), v.getName()) , Program.getLabel("Infos049"), JOptionPane.YES_NO_OPTION))
				return;

			if(CountryVignobles.isVignobleUsed(v)) {
				JOptionPane.showMessageDialog(null, Program.getLabel("VineyardPanel.unableDeleteVignoble"), Program.getLabel("Infos032"), JOptionPane.ERROR_MESSAGE);
				return;
			}
			comboVignoble.removeItemAt(comboVignoble.getSelectedIndex());
			vignobles.delVignoble(v);
			Program.setModified();
		}
	}

	class RenameVignobleAction extends AbstractAction {

		private static final long serialVersionUID = 2912399011575692147L;

		public RenameVignobleAction() {
			super(Program.getLabel("VineyardPanel.renameVignoble"));
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			CountryVignoble v = (CountryVignoble) comboVignoble.getSelectedItem();
			String val = JOptionPane.showInputDialog(MessageFormat.format(Program.getLabel("VineyardPanel.renameVignobleQuestion"), v.getName()));
			if(val != null && !val.isEmpty()) {
				CountryVignobles.renameVignoble(v, val);
				comboVignoble.updateUI();
				Program.setModified();
			}
		}
	}

	class AddAppellationAction extends AbstractAction {

		private static final long serialVersionUID = 2174872605239470622L;

		public AddAppellationAction() {
			super(Program.getLabel("VineyardPanel.addAppellation"), MyCellarImage.ADD);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			String val = JOptionPane.showInputDialog(Program.getLabel("VineyardPanel.addAppellationQuestion"));
			if(val != null && !val.isEmpty()) {
				Appelation v = new Appelation();
				v.setAOC(val);
				model.addAppellation(v);
			}
		}
	}

	class AddCountryAction extends AbstractAction {

		private static final long serialVersionUID = -6725950975161352023L;

		public AddCountryAction() {
			super(Program.getLabel("VineyardPanel.addCountry"), MyCellarImage.ADD);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			String val = JOptionPane.showInputDialog(Program.getLabel("VineyardPanel.addCountryQuestion"));
			if(val != null && !val.isEmpty()) {
				Country country = new Country(val);
				if(CountryVignobles.hasCountryByName(country)) {
					new Erreur(Program.getError("VineyardPanel.CountryExist"));
					return;
				}
				CountryVignobles.createCountry(country);
				comboCountry.addItem(country);
				Program.setModified();
			}
		}
	}

	/**
	 * Debug
	 *
	 * @param sText String
	 */
	public static void Debug(String sText) {
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
		Start.updateMainPanel();
	}

}
