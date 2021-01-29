package mycellar.vignobles;

import mycellar.Program;
import mycellar.Start;
import mycellar.core.datas.jaxb.AppelationJaxb;
import mycellar.core.datas.jaxb.CountryVignobleJaxb;

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
 * @version 1.3
 * @since 29/01/21
 */

class VineyardTableModel extends DefaultTableModel {

	private static final long serialVersionUID = -6356586420904968734L;
	static final int ACTION = 2;
	private List<AppelationJaxb> appelationJaxbs;
	private CountryVignobleJaxb vignoble;

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
		switch (column) {
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
		if (appelationJaxbs == null) {
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
		switch (column) {
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
		switch (column) {
		case 0:
			CountryVignobleController.setModified();
			CountryVignobleController.renameAOC(vignoble, appelationJaxb, (String)aValue);
			break;
		case 1:
			CountryVignobleController.setModified();
			CountryVignobleController.renameIGP(vignoble, appelationJaxb, (String)aValue);
			break;
		case 2:
			String name = appelationJaxb.getAOC() != null ? appelationJaxb.getAOC() : appelationJaxb.getIGP();
			CountryVignobleController.rebuild();
			if (CountryVignobleController.isAppellationUsed(appelationJaxb)) {
				JOptionPane.showMessageDialog(Start.getInstance(), Program.getLabel("VineyardPanel.unableDeleteAppellation"), Program.getLabel("Infos032"), JOptionPane.ERROR_MESSAGE);
				return;
			}

			if (JOptionPane.YES_OPTION != JOptionPane.showConfirmDialog(Start.getInstance(), MessageFormat.format(Program.getLabel("VineyardPanel.delAppellationQuestion"), name) , Program.getLabel("Infos049"), JOptionPane.YES_NO_OPTION)) {
				return;
			}
			CountryVignobleController.setModified();
			appelationJaxbs.remove(appelationJaxb);
			fireTableDataChanged();
			break;
		}
	}
	
	void setAppellations(CountryVignobleJaxb vignoble, List<AppelationJaxb> appelationJaxbs) {
		this.vignoble = vignoble;
		this.appelationJaxbs = appelationJaxbs;
		fireTableDataChanged();
	}

	void addAppellation(AppelationJaxb appellation) {
		appelationJaxbs.add(appellation);
		fireTableDataChanged();
		CountryVignobleController.setModified();
	}
}
