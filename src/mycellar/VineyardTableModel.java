package mycellar;

import mycellar.core.datas.jaxb.AppelationJaxb;
import mycellar.core.datas.jaxb.CountryVignobleJaxb;
import mycellar.vignobles.CountryVignobleController;

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
 * @version 1.1
 * @since 12/11/20
 */

class VineyardTableModel extends DefaultTableModel {

	private static final long serialVersionUID = -6356586420904968734L;
	static final int ACTION = 2;
	private List<AppelationJaxb> appelationJaxbs;
	private CountryVignobleJaxb vignoble;
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
		if(appelationJaxbs == null) {
			return 0;
		}
		return appelationJaxbs.size();
	}
	
	@Override
	public Object getValueAt(int row, int column) {
		if (appelationJaxbs == null) {
			return "";
		}
		
		AppelationJaxb appelationJaxb = appelationJaxbs.get(row);
		switch(column) {
		case 0:
			return appelationJaxb.getAOC();
		case 1:
			return appelationJaxb.getIGP();
		case 2:
			return Boolean.FALSE;
		default:
			return "";
		}
	}
	
	@Override
	public void setValueAt(Object aValue, int row, int column) {
		if (appelationJaxbs == null) {
			return;
		}
		
		AppelationJaxb appelationJaxb = appelationJaxbs.get(row);
		switch(column) {
		case 0:
			setModified(true);
			CountryVignobleController.renameAOC(vignoble, appelationJaxb, (String)aValue);
			break;
		case 1:
			setModified(true);
			CountryVignobleController.renameIGP(vignoble, appelationJaxb, (String)aValue);
			break;
		case 2:
			String name = appelationJaxb.getAOC() != null ? appelationJaxb.getAOC() : appelationJaxb.getIGP();
			if (JOptionPane.YES_OPTION != JOptionPane.showConfirmDialog(null, MessageFormat.format(Program.getLabel("VineyardPanel.delAppellationQuestion"), name) , Program.getLabel("Infos049"), JOptionPane.YES_NO_OPTION)) {
				return;
			}

			CountryVignobleController.rebuild();
			if (CountryVignobleController.isAppellationUsed(appelationJaxb)) {
				JOptionPane.showMessageDialog(null, Program.getLabel("VineyardPanel.unableDeleteAppellation"), Program.getLabel("Infos032"), JOptionPane.ERROR_MESSAGE);
				return;
			}
			setModified(true);
			appelationJaxbs.remove(appelationJaxb);
			fireTableDataChanged();
			break;
		}
	}
	
	void setAppellations(CountryVignobleJaxb vignoble, List<AppelationJaxb> appelationJaxbs) {
		this.appelationJaxbs = appelationJaxbs;
		this.vignoble = vignoble;
		fireTableDataChanged();
	}

	public boolean isModified() {
		return modified;
	}

	public void setModified(boolean modified) {
		this.modified = modified;
	}

	void addAppellation(AppelationJaxb appellation) {
		appelationJaxbs.add(appellation);
		fireTableDataChanged();
		modified = true;
	}

}
