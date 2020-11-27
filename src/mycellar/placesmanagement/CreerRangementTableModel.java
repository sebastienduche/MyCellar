package mycellar.placesmanagement;

import mycellar.Part;
import mycellar.Program;
import mycellar.Row;

import javax.swing.table.AbstractTableModel;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * <p>Titre : Cave à vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2012</p>
 * <p>Société : Seb Informatique</p>
 * @author Sébastien Duché
 * @version 0.9
 * @since 27/11/20
 */

class CreerRangementTableModel extends AbstractTableModel {

	private static final long serialVersionUID = -933488036527447807L;
	private final List<Column> columns = new LinkedList<>();
	private List<Part> rows = new LinkedList<>();
	private HashMap<Integer, Integer> mapPart = new HashMap<>();
	private final HashMap<Integer, Integer> mapLine = new HashMap<>();
	
	private static final int NAME = 0;
	private static final int ROW = 1;
	private static final int COLUMN = 2;
	
	private boolean sameColumnNumber = false;
	
	CreerRangementTableModel() {
		columns.add(new Column(NAME, Program.getLabel("Infos029")));
		columns.add(new Column(ROW, Program.getLabel("Infos027")));
		columns.add(new Column(COLUMN, Program.getLabel("Infos026")));
	}

	@Override
	public int getColumnCount() {
		return columns.size();
	}

	@Override
	public int getRowCount() {
		if(sameColumnNumber) {
			return rows.size();
		} else {
			int count = 0;
			for(Part p : rows) {
				if( p.getRowSize() == 0) {
					count += 1;
				} else {
					count += p.getRowSize();
				}
			}
			return count;
		}
	}

	@Override
	public Object getValueAt(int row, int col) {
		if(sameColumnNumber) {
			Part p = rows.get(row);
			if(p == null) {
				return "";
			}
			switch(col){
			case NAME:
				return  Program.getLabel("Infos029") + " " + p.getNum();
			case ROW:
				return p.getRows().size();
			case COLUMN:
				if(p.getRowSize() > 0) {
					return p.getRow(0).getCol();
				}
				return "0";
			}
		} else {
			int part = mapPart.get(row);
			int line = mapLine.get(row);
			Part p = rows.get(part);
			if(p == null) {
				return "";
			}
			switch(col){
			case NAME:
				return Program.getLabel("Infos029") + " " + p.getNum() + " " + Program.getLabel("Infos027");
			case ROW:
				return line;
			case COLUMN:
				if(p.getRow(line - 1) != null) {
					return p.getRow(line - 1).getCol();
				}
				return "0";
			}
		}
		return "";
	}

	@Override
	public String getColumnName(int col) {
		if (col >= columns.size()) {
			return "";
		}
		return columns.get(col).getLabel();
	}

	@Override
	public boolean isCellEditable(int row, int col) {
		return col == ROW || col == COLUMN;
	}

	@Override
	public void setValueAt(Object arg0, int row, int col) {
		Part p;
		if(sameColumnNumber) {
			p = rows.get(row);
		}	else {
			// Récupération du numéro de la partie
			// puis récupération de la partie correspondante
			int part = mapPart.get(row);
			p = rows.get(part);
		}
		if(p == null) {
			return;
		}
		switch(col){
		case ROW:
			int nRow;
			try{
				nRow = Integer.parseInt((String)arg0);
			}catch(NumberFormatException e) {
				return;
			}
			p.setRows(nRow);
			if(!sameColumnNumber) {
				updateValues();
				fireTableDataChanged();
				fireTableStructureChanged();
			} else if (p.getRowSize() > 0){
				final int nCol = p.getRow(0).getCol();
				for(Row r: p.getRows()) {
					r.setCol(nCol);
				}
			}
			return;
		case COLUMN:
			int nCol;
			try{
				nCol = Integer.parseInt((String)arg0);
			}catch(NumberFormatException e) {
				return;
			}
			if(sameColumnNumber) {
				for(Row r: p.getRows()) {
					r.setCol(nCol);
				}
			} else {
				int line = mapLine.get(row);
				p.getRow(line-1).setCol(nCol);
			}
		}
	}
	
	public void setValues(List<Part> parts){
		rows = parts;
		updateValues();
		fireTableDataChanged();
	}

	public boolean isSameColumnNumber() {
		return sameColumnNumber;
	}

	void setSameColumnNumber(boolean sameColumnNumber) {
		this.sameColumnNumber = sameColumnNumber;
		updateValues();
		fireTableDataChanged();
	}

	private void updateValues() {
		mapPart = new HashMap<>();
		int index = 0;
		int numPart = 0;
		for(Part p: rows) {
			if(sameColumnNumber) {
				// On positionne le nombre de colonne de la première ligne sur toute les lignes
				for( Row r: p.getRows()) {
					r.setCol(p.getRow(0).getCol());
				}
			}
			int line = 1;
			for(@SuppressWarnings("unused") Row r: p.getRows()) {
				mapPart.put(index, numPart);
				mapLine.put(index, line);
				line++;
				index++;
			}
			if(p.getRowSize() == 0) {
				mapPart.put(index, numPart);
				mapLine.put(index, line);
				p.setRows(1);
				index++;
			}
			numPart++;
		}
	}

}

class Column {
	private final int id;
	private final String label;
	
	Column(int id, String label) {
		this.id = id;
		this.label = label;
	}

	public int getId() {
		return id;
	}

	public String getLabel() {
		return label;
	}
	
	
}
