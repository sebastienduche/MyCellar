package mycellar;

import mycellar.countries.Country;
import mycellar.vignobles.Appelation;
import mycellar.vignobles.CountryVignoble;
import mycellar.vignobles.CountryVignobles;

import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import java.text.MessageFormat;
import java.util.List;

/**
 * Titre : Cave à vin
 * Description : Votre description
 * Copyright : Copyright (c) 2015
 * Société : Seb Informatique
 * 
 * @author Sébastien Duché
 * @version 0.8
 * @since 10/04/19
 */

class VineyardTableModel extends DefaultTableModel {

	private static final long serialVersionUID = -6356586420904968734L;
	static final int ACTION = 2;
	private List<Appelation> appelations;
	private CountryVignoble vignoble;
	private Country country;
	private boolean modified = false;
	
	@Override
	public boolean isCellEditable(int row, int column) {
		return true;
	}
	
	@Override
	public int getColumnCount() {
		return 3;
	}
	
	@Override
	public String getColumnName(int column) {
		switch(column) {
		case 0:
			return Program.getLabel("Main.AppelationAOC");
		case 1:
			return Program.getLabel("Main.AppelationIGP");
		default:
			return "";
		}
	}
	
	@Override
	public int getRowCount() {
		if(appelations == null) {
			return 0;
		}
		return appelations.size();
	}
	
	@Override
	public Object getValueAt(int row, int column) {
		if(appelations == null) {
			return "";
		}
		
		Appelation appelation = appelations.get(row);
		switch(column) {
		case 0:
			return appelation.getAOC();
		case 1:
			return appelation.getIGP();
		case 2:
			return Boolean.FALSE;
		default:
			return "";
		}
	}
	
	@Override
	public void setValueAt(Object aValue, int row, int column) {
		if(appelations == null) {
			return;
		}
		
		Appelation appelation = appelations.get(row);
		switch(column) {
		case 0:
			setModified(true);
			CountryVignobles.renameAOC(country, vignoble, appelation, (String)aValue);
			break;
		case 1:
			setModified(true);
			CountryVignobles.renameIGP(country, vignoble, appelation, (String)aValue);
			break;
		case 2:
			String name = appelation.getAOC() != null ? appelation.getAOC() : appelation.getIGP();
			if(JOptionPane.YES_OPTION != JOptionPane.showConfirmDialog(null, MessageFormat.format(Program.getLabel("VineyardPanel.delAppellationQuestion"), name) , Program.getLabel("Infos049"), JOptionPane.YES_NO_OPTION)) {
				return;
			}

			CountryVignobles.addVignobleFromBottles();
			if(CountryVignobles.isAppellationUsed(country, vignoble, appelation)) {
				JOptionPane.showMessageDialog(null, Program.getLabel("VineyardPanel.unableDeleteAppellation"), Program.getLabel("Infos032"), JOptionPane.ERROR_MESSAGE);
				return;
			}
			setModified(true);
			appelations.remove(appelation);
			fireTableDataChanged();
			break;
		}
	}
	
	void setAppellations(Country country, CountryVignoble vignoble, List<Appelation> appelations) {
		this.appelations = appelations;
		this.vignoble = vignoble;
		this.country = country;
		fireTableDataChanged();
	}

	public boolean isModified() {
		return modified;
	}

	public void setModified(boolean modified) {
		this.modified = modified;
	}

	void addAppellation(Appelation appellation) {
		appelations.add(appellation);
		fireTableDataChanged();
		modified = true;
	}

}
