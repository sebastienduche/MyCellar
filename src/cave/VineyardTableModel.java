package Cave;

import java.text.MessageFormat;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

import Cave.countries.Country;
import Cave.vignobles.Appelation;
import Cave.vignobles.CountryVignoble;
import Cave.vignobles.CountryVignobles;

/**
 * Titre : Cave à vin
 * Description : Votre description
 * Copyright : Copyright (c) 2015
 * Société : Seb Informatique
 * 
 * @author Sébastien Duché
 * @version 0.6
 * @since 21/01/17
 */

public class VineyardTableModel extends DefaultTableModel {

	private static final long serialVersionUID = -6356586420904968734L;
	public static final int ACTION = 2;
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
		if(appelations == null)
			return 0;
		return appelations.size();
	}
	
	@Override
	public Object getValueAt(int row, int column) {
		if(appelations == null)
			return "";
		
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
		if(appelations == null)
			return;
		
		Appelation appelation = appelations.get(row);
		switch(column) {
		case 0:
			setModified(true);
			CountryVignobles.renameAOC(vignoble, appelation, (String)aValue);
			break;
		case 1:
			setModified(true);
			CountryVignobles.renameIGP(vignoble, appelation, (String)aValue);
			break;
		case 2:
			if(JOptionPane.YES_OPTION != JOptionPane.showConfirmDialog(null, MessageFormat.format(Program.getLabel("VineyardPanel.delAppellationQuestion"), appelation.getAOC()) , Program.getLabel("Infos049"), JOptionPane.YES_NO_OPTION))
				return;
			
			if(CountryVignobles.isAppellationUsed(country, vignoble, appelation)) {
				JOptionPane.showMessageDialog(null, Program.getLabel("VineyardPanel.unableDeleteAppellation"), Program.getLabel("Infos032"), JOptionPane.ERROR_MESSAGE);
				return;
			}
			setModified(true);
			appelations.remove(appelation);
			this.fireTableDataChanged();
			break;
		}
	}
	
	public void setAppellations(Country country, CountryVignoble vignoble, List<Appelation> appelations) {
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

	public void addAppellation(Appelation appellation) {
		appelations.add(appellation);
		fireTableDataChanged();
		modified = true;
	}

}
