package Cave.showfile;


import java.awt.Color;
import java.awt.Dimension;

import javax.swing.AbstractAction;
import javax.swing.DefaultCellEditor;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.SwingConstants;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import Cave.BottleColor;
import Cave.Bouteille;
import Cave.Erreur;
import Cave.History;
import Cave.ITabListener;
import Cave.MyCellarImage;
import Cave.Program;
import Cave.Rangement;
import Cave.Start;
import Cave.StateButtonEditor;
import Cave.StateButtonRenderer;
import Cave.StateEditor;
import Cave.StateRenderer;
import Cave.TabEvent;
import Cave.ToolTipRenderer;
import Cave.Vignoble;
import Cave.actions.OpenAddVinAction;
import Cave.core.MyCellarButton;
import Cave.core.MyCellarComboBox;
import Cave.core.MyCellarLabel;
import Cave.core.MyCellarFields;
import Cave.countries.Countries;
import Cave.countries.Country;

import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import net.miginfocom.swing.MigLayout;

/**
 * <p>Titre : Cave à vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 1998</p>
 * <p>Societe : Seb Informatique</p>
 * @author Sébastien Duché
 * @version 3.6
 * @since 19/03/17
 */

public class ShowFile extends JPanel implements ITabListener  {

	private MyCellarLabel m_oTitleLabel = new MyCellarLabel();
	private MyCellarLabel m_oResultLabel = new MyCellarLabel();
	private MyCellarButton m_oManageButton = new MyCellarButton(new ManageColumnAction());
	private MyCellarButton m_oDeleteButton = new MyCellarButton(MyCellarImage.DELETE);
	private MyCellarButton m_oModifyButton = new MyCellarButton(new ModifyBottlesAction());	
	private MyCellarComboBox<String> m_oPlaceCbx = new MyCellarComboBox<String>();
	private MyCellarComboBox<String> m_oTypeCbx = new MyCellarComboBox<String>();
	private MyCellarComboBox<BottleColor> m_oColorCbx = new MyCellarComboBox<BottleColor>();
	/*private MyCellarComboBox<String> m_oCountryCbx = new MyCellarComboBox<String>();
	private MyCellarComboBox<String> m_oVineyardCbx = new MyCellarComboBox<String>();
	private MyCellarComboBox<String> m_oAOCCbx = new MyCellarComboBox<String>();
	private MyCellarComboBox<String> m_oIGPCbx = new MyCellarComboBox<String>();*/
	public TableShowValues tv;
	public JTable m_oTable;
	public JScrollPane m_oScroll = new JScrollPane();
	private boolean updateView = false;
	private LinkedList<ShowFileColumn> columns = new LinkedList<ShowFileColumn>();
	
	private boolean trash = false;

	static final long serialVersionUID = 020107;

	public ShowFile() {
		try {
			columns.add(new ShowFileColumn(MyCellarFields.NAME) {

				@Override
				void setValue(Bouteille b, Object value) {
					b.setNom((String)value);
				}

				@Override
				Object getValue(Bouteille b) {
					return Program.convertStringFromHTMLString(b.getNom());
				}});
			columns.add(new ShowFileColumn(MyCellarFields.YEAR, 50) {

				@Override
				void setValue(Bouteille b, Object value) {
					if( Program.hasYearControl() && !Bouteille.isValidYear((String) value) )
				       	  javax.swing.JOptionPane.showMessageDialog(null, Program.getError("Error053"), Program.getError("Error015"), javax.swing.JOptionPane.ERROR_MESSAGE);
				        else{
				        	Program.getStorage().removeAnnee(b.getAnneeInt());
				        	b.setAnnee((String)value);	
				        	Program.getStorage().addAnnee(b.getAnneeInt());
				        }
				}

				@Override
				Object getValue(Bouteille b) {
					return b.getAnnee();
				}});
			columns.add(new ShowFileColumn(MyCellarFields.TYPE) {

				@Override
				void setValue(Bouteille b, Object value) {
					b.setType((String)value);
				}

				@Override
				Object getValue(Bouteille b) {
					return b.getType();
				}});
			columns.add(new ShowFileColumn(MyCellarFields.PLACE) {

				@Override
				void setValue(Bouteille b, Object value) {
					setRangementValue(b, MyCellarFields.PLACE, value);
				}

				@Override
				Object getValue(Bouteille b) {
					return Program.convertStringFromHTMLString(b.getEmplacement());
				}});
			columns.add(new ShowFileColumn(MyCellarFields.NUM_PLACE, 50) {

				@Override
				void setValue(Bouteille b, Object value) {
					setRangementValue(b, MyCellarFields.NUM_PLACE, value);
				}

				@Override
				Object getValue(Bouteille b) {
					return Integer.toString(b.getNumLieu());
				}});
			columns.add(new ShowFileColumn(MyCellarFields.LINE, 50) {

				@Override
				void setValue(Bouteille b, Object value) {
					setRangementValue(b, MyCellarFields.LINE, value);
				}

				@Override
				Object getValue(Bouteille b) {
					if(b.getRangement() == null || b.getRangement().isCaisse())
						return "";
					return Integer.toString(b.getLigne());
				}});
			columns.add(new ShowFileColumn(MyCellarFields.COLUMN, 50) {

				@Override
				void setValue(Bouteille b, Object value) {
					setRangementValue(b, MyCellarFields.COLUMN, value);
				}

				@Override
				Object getValue(Bouteille b) {
					if(b.getRangement() == null || b.getRangement().isCaisse())
						return "";
					return Integer.toString(b.getColonne());
				}});
			columns.add(new ShowFileColumn(MyCellarFields.PRICE, 50) {

				@Override
				void setValue(Bouteille b, Object value) {
					b.setPrix((String)value);
				}

				@Override
				Object getValue(Bouteille b) {
					return Program.convertStringFromHTMLString(b.getPrix());
				}});
			columns.add(new ShowFileColumn(MyCellarFields.COMMENT) {

				@Override
				void setValue(Bouteille b, Object value) {
					b.setComment(Program.convertStringFromHTMLString((String)value));
				}

				@Override
				Object getValue(Bouteille b) {
					return Program.convertStringFromHTMLString(b.getComment());
				}});
			columns.add(new ShowFileColumn(MyCellarFields.MATURITY) {

				@Override
				void setValue(Bouteille b, Object value) {
					b.setMaturity(Program.convertStringFromHTMLString((String)value));
				}

				@Override
				Object getValue(Bouteille b) {
					return Program.convertStringFromHTMLString(b.getMaturity());
				}});
			columns.add(new ShowFileColumn(MyCellarFields.PARKER) {

				@Override
				void setValue(Bouteille b, Object value) {
					b.setParker((String)value);
				}

				@Override
				Object getValue(Bouteille b) {
					return b.getParker();
				}});
			columns.add(new ShowFileColumn(MyCellarFields.COLOR) {

				@Override
				void setValue(Bouteille b, Object value) {
					b.setColor(((BottleColor)value).name());
				}

				@Override
				Object getValue(Bouteille b) {
					return BottleColor.getColor(b.getColor());
				}});
			columns.add(new ShowFileColumn(MyCellarFields.COUNTRY, 100, false) {

				@Override
				void setValue(Bouteille b, Object value) {
					/*Vignoble v = b.getVignoble();
					if(v == null) {
						v = new Vignoble();
						b.setVignoble(v);
					}
					Country country = Countries.findByLabel((String)value);
					if(country != null)
						v.setCountry(country.getId());
					m_oVineyardCbx.removeAllItems();
					m_oIGPCbx.removeAllItems();
					m_oAOCCbx.removeAllItems();
					m_oVineyardCbx.addItem("");
					Vignobles vignobles = CountryVignobles.getVignobles(country);
					if(vignobles != null) {
						ArrayList<CountryVignoble> list = (ArrayList<CountryVignoble>)vignobles.getVignoble();
						Collections.sort(list);
						for(CountryVignoble cv : list)
							m_oVineyardCbx.addItem(cv.toString());
					}*/
				}

				@Override
				Object getValue(Bouteille b) {
					if(b.getVignoble() == null)
						return "";
					Country country = Countries.find(b.getVignoble().getCountry());
					if(country != null)
						return country.getLabel();
					return b.getVignoble().getCountry();
				}});
			columns.add(new ShowFileColumn(MyCellarFields.VINEYARD, 100, false) {

				@Override
				void setValue(Bouteille b, Object value) {
					/*Vignoble v = b.getVignoble();
					if(v == null)
						return;
					v.setName((String)value);
					m_oIGPCbx.removeAllItems();
					m_oAOCCbx.removeAllItems();
					m_oIGPCbx.addItem("");
					m_oAOCCbx.addItem("");
					Vignobles vignobles = CountryVignobles.getVignobles(Countries.find(v.getCountry()));
					if(vignobles == null)
						return;
					CountryVignoble cv = vignobles.findVignoble(v);
					if(cv == null)
						return;
					for(Appelation app : cv.getAppelation()) {
						if(app.getAOC() != null && !app.getAOC().isEmpty())
							m_oAOCCbx.addItem(v.getAOC());
						if(app.getIGP() != null && !app.getIGP().isEmpty())
							m_oIGPCbx.addItem(v.getIGP());
					}*/
				}

				@Override
				Object getValue(Bouteille b) {
					if(b.getVignoble() == null)
						return "";
					return b.getVignoble().getName();
				}});
			columns.add(new ShowFileColumn(MyCellarFields.AOC, 100, false) {

				@Override
				void setValue(Bouteille b, Object value) {
					Vignoble v = b.getVignoble();
					if(v == null)
						return;
					v.setAOC((String)value);
				}

				@Override
				Object getValue(Bouteille b) {
					if(b.getVignoble() == null)
						return "";
					return b.getVignoble().getAOC();
				}});
			columns.add(new ShowFileColumn(MyCellarFields.IGP, 100, false) {

				@Override
				void setValue(Bouteille b, Object value) {
					Vignoble v = b.getVignoble();
					if(v == null)
						return;
					v.setIGP((String)value);
				}

				@Override
				Object getValue(Bouteille b) {
					if(b.getVignoble() == null)
						return "";
					return b.getVignoble().getIGP();
				}});
			jbInit();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
    public ShowFile(boolean trash) {
		this.trash = trash;
		try {
			jbInit();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void jbInit() throws Exception {

		m_oTitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
		m_oTitleLabel.setText(Program.getLabel("Infos325")); //"Contenu du fichier");
		m_oTitleLabel.setFont(Program.font_dialog);
		m_oTitleLabel.setForeground(Color.red);
		m_oResultLabel.setHorizontalAlignment(SwingConstants.CENTER);
		m_oResultLabel.setForeground(Color.red);
		this.setLayout(new MigLayout("","grow","[]10px[grow][]"));
		if(trash) {
			m_oDeleteButton.setText(Program.getLabel("ShowFile.Restore"));
			m_oDeleteButton.setIcon(MyCellarImage.RESTORE);
		} else
			m_oDeleteButton.setText(Program.getLabel("Infos051"));
		
		m_oDeleteButton.addActionListener((e) -> {
			if(trash)
				restore();
			else	
				delete_actionPerformed(e);
		});
		if(trash) {
    		add(m_oManageButton, "align right, split 2");
		}
		else {
			add(m_oManageButton, "align right, split 3");
			add(m_oModifyButton, "align right");
		}
		add(m_oDeleteButton, "align right, wrap");
		
		for( int i = 0; i < Program.GetCaveLength(); i++) {
			m_oPlaceCbx.addItem(Program.getCave(i).getNom());
		}
		
		m_oColorCbx.addItem(BottleColor.NONE);
		m_oColorCbx.addItem(BottleColor.RED);
		m_oColorCbx.addItem(BottleColor.PINK);
		m_oColorCbx.addItem(BottleColor.WHITE);
		
		/*m_oCountryCbx.addItem("");
		for(Country c : Program.getCountries())
			m_oCountryCbx.addItem(c.getLabel());*/
		
		m_oTypeCbx.addItem("");
		for(String type : Program.half)
			m_oTypeCbx.addItem(type);
		
		// Remplissage de la table
		tv = new TableShowValues();
		if(trash) {
			tv = new TableShowValues();
			tv.setBottles(Program.getTrash());
			m_oTable = new JTable(tv);
		}
		else {
			tv = new ShowFileModel();
			tv.setBottles(Program.getStorage().getAllList());
			String savedColumns = Program.getShowColumns();
			LinkedList<ShowFileColumn> cols;
			if(savedColumns.isEmpty()) {
				cols = columns;
				((ShowFileModel)tv).setColumns(columns);
			} else {
				cols = new LinkedList<ShowFileColumn>();
				String [] values = savedColumns.split(";");
				for(ShowFileColumn c : columns) {
					for(String s : values) {
    					if(s.equals(c.getField().name()))
    						cols.add(c);
					}
				}
				((ShowFileModel)tv).setColumns(cols);
			}
			m_oTable = new JTable(tv);
			TableColumnModel tcm = m_oTable.getColumnModel();
			int i=1;
			TableColumn tc;
			for(ShowFileColumn c : cols) {
				tc = tcm.getColumn(i++);
				tc.setMinWidth(c.getWidth());
				tc.setPreferredWidth(c.getWidth());
				if(c.getField() == MyCellarFields.PLACE)
					tc.setCellEditor(new DefaultCellEditor(m_oPlaceCbx));
				else if(c.getField() == MyCellarFields.TYPE)
					tc.setCellEditor(new DefaultCellEditor(m_oTypeCbx));
				else if(c.getField() == MyCellarFields.COLOR)
					tc.setCellEditor(new DefaultCellEditor(m_oColorCbx));
				/*else if(c.getField() == MyCellarFields.COUNTRY)
					tc.setCellEditor(new DefaultCellEditor(m_oCountryCbx));
				else if(c.getField() == MyCellarFields.VINEYARD)
					tc.setCellEditor(new DefaultCellEditor(m_oVineyardCbx));
				else if(c.getField() == MyCellarFields.AOC)
					tc.setCellEditor(new DefaultCellEditor(m_oAOCCbx));
				else if(c.getField() == MyCellarFields.IGP)
					tc.setCellEditor(new DefaultCellEditor(m_oIGPCbx));*/
			}
		}
		
		m_oTable.setAutoCreateRowSorter(true);
		TableRowSorter<TableModel> sorter = new TableRowSorter<>(m_oTable.getModel());
		m_oTable.setRowSorter(sorter);
		List<RowSorter.SortKey> sortKeys = new ArrayList<>();
		sortKeys.add(new RowSorter.SortKey(1, SortOrder.ASCENDING));
		sorter.setSortKeys(sortKeys);
		sorter.sort();
		TableColumnModel tcm = m_oTable.getColumnModel();
		TableColumn tc1[] = new TableColumn[5];
		for (int w = 0; w < 5; w++) {
			tc1[w] = tcm.getColumn(w);
			tc1[w].setCellRenderer(new ToolTipRenderer());
			switch (w) {
			case 1:
				tc1[w].setMinWidth(150);
				break;
			case 2:
				tc1[w].setMinWidth(50);
				break;
			case 4:
				tc1[w].setMinWidth(100);
				break;
			default:
				tc1[w].setMinWidth(30);
				break;
			}
		}
		TableColumn tc = tcm.getColumn(0);
		tc.setCellRenderer(new StateRenderer());
		tc.setCellEditor(new StateEditor());
		tc.setMinWidth(25);
		tc.setMaxWidth(25);
		tc = tcm.getColumn(TableShowValues.PLACE);
		tc.setCellEditor(new DefaultCellEditor(m_oPlaceCbx));
		tc = tcm.getColumn(TableShowValues.TYPE);
		tc.setCellEditor(new DefaultCellEditor(m_oTypeCbx));
		tc = tcm.getColumn(tcm.getColumnCount()-1);
		tc.setCellRenderer(new StateButtonRenderer());
		tc.setCellEditor(new StateButtonEditor());
		tc.setMinWidth(100);
		tc.setMaxWidth(100);

		m_oTable.setPreferredScrollableViewportSize(new Dimension(300, 200));
        m_oTable.setAutoResizeMode(JTable.AUTO_RESIZE_NEXT_COLUMN);
		m_oScroll = new JScrollPane(m_oTable);
			
		this.add(m_oScroll, "grow, wrap");
		this.add(m_oResultLabel, "alignx center, hidemode 3");
	}


	void delete_actionPerformed(ActionEvent e) {

		String erreur_txt1, erreur_txt2;
		LinkedList<Bouteille> toDeleteList = new LinkedList<Bouteille>();

		try {
			int max_row = tv.getRowCount();
			if(max_row != 0) {
				int row = 0;
				do {
					if (((Boolean)tv.getValueAt(row, TableShowValues.ETAT)).equals(Boolean.TRUE))
						toDeleteList.add(tv.getBottle(row));
					row++;
				}
				while (row < max_row);
			}

			if (toDeleteList.size() == 0) {
				erreur_txt1 = Program.getError("Error064"); //"Aucun vin à supprimer!");
				erreur_txt2 = Program.getError("Error065"); //"Veuillez sélectionner les vins à supprimer.");
				new Erreur(erreur_txt1, erreur_txt2,true);
			}
			else {

				if (toDeleteList.size() == 1) {
					erreur_txt1 = Program.getError("Error067"); //"1 vin sélectionné.");
					erreur_txt2 = Program.getError("Error068"); //"Voulez-vous le supprimer?");
				}
				else {
					erreur_txt1 = new String(toDeleteList.size() + " " + Program.getError("Error130")); //vins sélectionnés.");
					erreur_txt2 = Program.getError("Error131"); //"Voulez-vous les supprimer?");
				}
				if( JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(null, erreur_txt1 + " " + erreur_txt2, Program.getLabel("Infos049"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE))	
				{
					for (int i = 0; i < toDeleteList.size(); i++) {
						Bouteille b = (Bouteille) toDeleteList.get(i);
						Program.getStorage().addHistory(History.DEL, b);
						Program.getStorage().deleteWine(b);
						Program.setToTrash(b);
					}
				}
				refresh();
			}
			this.repaint();
		}
		catch (Exception exc) {
			Program.showException(exc);
		}

	}
	
	private void restore() {

		String erreur_txt1, erreur_txt2;
		LinkedList<Bouteille> toRestoreList = new LinkedList<Bouteille>();

		try {
			int max_row = tv.getRowCount();
			if(max_row != 0) {
				int row = 0;
				do {
					if (((Boolean)tv.getValueAt(row, TableShowValues.ETAT)).equals(Boolean.TRUE))
						toRestoreList.add(tv.getBottle(row));
					row++;
				}
				while (row < max_row);
			}

			if (toRestoreList.size() == 0) {
				erreur_txt1 = Program.getLabel("ShowFile.NoBottleToRestore");
				erreur_txt2 = Program.getLabel("ShowFile.SelectToRestore");
				new Erreur(erreur_txt1, erreur_txt2,true);
			}
			else {

				if (toRestoreList.size() == 1) {
					erreur_txt1 = Program.getError("Error067"); //"1 vin sélectionné.");
					erreur_txt2 = Program.getLabel("ShowFile.RestoreOne");
				}
				else {
					erreur_txt1 = new String(toRestoreList.size() + " " + Program.getError("Error130")); //vins sélectionnés.");
					erreur_txt2 = Program.getLabel("ShowFile.RestoreSeveral");
				}
				if( JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(null, erreur_txt1 + " " + erreur_txt2, Program.getLabel("Infos049"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE))	
				{
					LinkedList<Bouteille> cantRestoreList = new LinkedList<Bouteille>();
					for (int i = 0; i < toRestoreList.size(); i++) {
						Bouteille b = (Bouteille) toRestoreList.get(i);
						Program.getTrash().remove(b);
						int num_l = Rangement.convertNom_Int(b.getEmplacement());
						if(Program.getCave(num_l).isCaisse()){
							Program.getStorage().addHistory(History.ADD, b);
							Program.getStorage().addWine(b);
						}
						else {
							if(Program.getCave(num_l).getBouteille(b.getNumLieu()-1, b.getLigne()-1, b.getColonne()-1) == null) {
								Program.getStorage().addHistory(History.ADD, b);
								Program.getStorage().addWine(b);
							}
							else
								cantRestoreList.add(b);
						}
					}
					if(!cantRestoreList.isEmpty()) {
						modifyBottles(cantRestoreList);
					}
				}
				refresh();
			}
			this.repaint();
		}
		catch (Exception exc) {
			Program.showException(exc);
		}

	}
	
	private void modifyBottles(LinkedList<Bouteille> listToModify) {
		new OpenAddVinAction(listToModify).actionPerformed(null);
	}

	public void refresh() {
		if(trash)
			tv.setBottles(Program.getTrash());
		else
			tv.setBottles(Program.getStorage().getAllList());
		m_oResultLabel.setText("");
	}
	
	public void setRangementValue(Bouteille b, MyCellarFields column, Object value) {
		
		String empl_old = b.getEmplacement();
    	int num_empl_old = b.getNumLieu();
    	int line_old = b.getLigne();
    	int column_old = b.getColonne();
    	int n = Rangement.convertNom_Int(empl_old);
    	boolean bError = false;
    	int nValueToCheck = -1;
    	String empl = empl_old;
    	int num_empl = num_empl_old;
    	int line = line_old;
    	int column1 = column_old;
    	
    	Program.setModified();

    	if (column == MyCellarFields.PLACE) {
    		empl = (String)value; 
	    	n = Rangement.convertNom_Int((String)value);
    	}
    	else if (column == MyCellarFields.NUM_PLACE) {
    		try{
    			num_empl = Integer.parseInt((String)value);
    			nValueToCheck = num_empl;
    		}
            catch (Exception e) {
              new Erreur(Program.getError("Error196"));
              bError = true;
            }
    	}
    	else if (column == MyCellarFields.LINE) {
    		try{
    			line = Integer.parseInt((String)value);
    			nValueToCheck = line;
    		}
            catch (Exception e) {
              new Erreur(Program.getError("Error196"));
              bError = true;
            }
    	}
    	else if (column == MyCellarFields.COLUMN) {
    		try{
    			column1 = Integer.parseInt((String)value);
    			nValueToCheck = column1;
    		}
            catch (Exception e) {
              new Erreur(Program.getError("Error196"));
              bError = true;
            }
    	}
    	
    	if ( !bError && (column == MyCellarFields.NUM_PLACE || column == MyCellarFields.LINE || column == MyCellarFields.COLUMN) ) {
    		if (!Program.getCave(n).isCaisse() && nValueToCheck <= 0)
    		{
    			new Erreur(Program.getError("Error197"));
                bError = true;
    		}
    	}
    		
    	if ( !bError && (empl_old.compareTo(empl) != 0 || num_empl_old != num_empl || line_old != line || column_old != column1)) {
    		// Controle de l'emplacement de la bouteille
    		Rangement rangement = Program.getCave(n);
    		if(rangement.canAddBottle(num_empl, line, column1)) {
		    	Bouteille bTemp = null;
		    	if(!rangement.isCaisse())
		    		bTemp = rangement.getBouteille(num_empl-1, line-1, column1-1);
		    	if( bTemp != null) {
		    		String sText = Program.convertStringFromHTMLString(bTemp.getNom()) + " " + Program.getError("Error059");
		    		javax.swing.JOptionPane.showMessageDialog(null, sText, Program.getError("Error015"), javax.swing.JOptionPane.ERROR_MESSAGE);
		    	}
		    	else {
		    		String oldPlace = b.getEmplacement();
		    		if(column == MyCellarFields.PLACE)
		    			b.setEmplacement((String)value);
		    		else if(column == MyCellarFields.NUM_PLACE)
		    			b.setNumLieu(Integer.parseInt((String)value));
		    		else if(column == MyCellarFields.LINE)
		    			b.setLigne(Integer.parseInt((String)value));
		    		else if(column == MyCellarFields.COLUMN)
		    			b.setColonne(Integer.parseInt((String)value));
		    		if ( column == MyCellarFields.PLACE && rangement.isCaisse()) {
		    			int nNumEmpl = b.getNumLieu();//Integer.parseInt((String) values[row][NUM_PLACE]);
		    			if( nNumEmpl > rangement.getLastNumEmplacement())
		    				b.setNumLieu(rangement.getFreeNumPlaceInCaisse());
		    			b.setLigne(0);
		    			b.setColonne(0);
		    		}
		    		Program.getCave(Rangement.convertNom_Int(oldPlace)).putTabStock();
		    		Program.getCave(Rangement.convertNom_Int(b.getEmplacement())).putTabStock();
		    	}
    		}
    		else {
    			if (rangement.isCaisse()) {
    				new Erreur(Program.getError("Error154"));
    			} else {
    				if(JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(null, Program.getError("Error198"), Program.getLabel("Infos049"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE))
    					Start.showBottle(b);
    			}
    		}
    	}
	}

	void this_windowActivated(WindowEvent e) {
		refresh();
	}

	public void setUpdateView(){
		updateView = true;
	}

	/**
	 * Mise à jour de la liste des rangements
	 */
	public void updateView() {
		if(!updateView)
			return;
		updateView = false;
		m_oPlaceCbx.removeAllItems();
		for (int i = 0; i < Program.GetCaveLength(); i++) {
			m_oPlaceCbx.addItem(Program.getCave(i).getNom());
		}
		
		TableColumnModel tcm = m_oTable.getColumnModel();
		TableColumn tc = tcm.getColumn(TableShowValues.PLACE);
		tc.setCellEditor(new DefaultCellEditor(m_oPlaceCbx));
		
		m_oTypeCbx.removeAllItems();
		m_oTypeCbx.addItem("");
		for (String type : Program.half) {
			m_oTypeCbx.addItem(type);
		}
		
		tc = tcm.getColumn(TableShowValues.TYPE);
		tc.setCellEditor(new DefaultCellEditor(m_oTypeCbx));
	}
	

	@Override
	public boolean tabWillClose(TabEvent event) {
		return true;
	}

	@Override
	public void tabClosed() {
		Start.updateMainPanel();
	}

	class ManageColumnAction extends AbstractAction {

		private static final long serialVersionUID = 8165964725562440277L;

		public ManageColumnAction() {
			super(Program.getLabel("Main.Columns"));
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			JPanel panel = new JPanel();
			LinkedList<MyCellarFields> list = new LinkedList<MyCellarFields>();
			list.add(MyCellarFields.NAME);
			list.add(MyCellarFields.YEAR);
			list.add(MyCellarFields.TYPE);
			list.add(MyCellarFields.PLACE);
			list.add(MyCellarFields.NUM_PLACE);
			list.add(MyCellarFields.LINE);
			list.add(MyCellarFields.COLUMN);
			list.add(MyCellarFields.PRICE);
			list.add(MyCellarFields.COMMENT);
			list.add(MyCellarFields.MATURITY);
			list.add(MyCellarFields.PARKER);
			list.add(MyCellarFields.COLOR);
			list.add(MyCellarFields.COUNTRY);
			list.add(MyCellarFields.VINEYARD);
			list.add(MyCellarFields.AOC);
			list.add(MyCellarFields.IGP);
			LinkedList<ShowFileColumn> cols = ((ShowFileModel)tv).getColumns();
			ManageColumnModel modelColumn = new ManageColumnModel(list, cols);
			JTable table = new JTable(modelColumn);
			TableColumnModel tcm = table.getColumnModel();
			TableColumn tc = tcm.getColumn(0);
			tc.setCellRenderer(new StateRenderer());
			tc.setCellEditor(new StateEditor());
			tc.setMinWidth(25);
			tc.setMaxWidth(25);
			panel.add(new JScrollPane(table));
			JOptionPane.showMessageDialog(null, panel, Program.getLabel("Main.Columns"), JOptionPane.PLAIN_MESSAGE);
			((ShowFileModel)tv).removeAllColumns();
			cols = new LinkedList<ShowFileColumn>();
			Program.setModified();
			LinkedList<Integer> properties = modelColumn.getSelectedColumns();
			for(ShowFileColumn c : columns) {
				if(properties.contains(c.getField().ordinal()))
					cols.add(c);
			}
			((ShowFileModel)tv).setColumns(cols);
			tcm = m_oTable.getColumnModel();
			tc = tcm.getColumn(0);
			tc.setCellRenderer(new StateRenderer());
			tc.setCellEditor(new StateEditor());
			tc.setMinWidth(25);
			tc.setMaxWidth(25);
			tc = tcm.getColumn(tcm.getColumnCount()-1);
			tc.setCellRenderer(new StateButtonRenderer());
			tc.setCellEditor(new StateButtonEditor());
			tc.setMinWidth(100);
			tc.setMaxWidth(100);
			int i = 1;
			StringBuffer buffer = new StringBuffer();
			for(ShowFileColumn c : cols) {
				tc = tcm.getColumn(i++);
				tc.setMinWidth(c.getWidth());
				tc.setPreferredWidth(c.getWidth());
				if(c.getField() == MyCellarFields.PLACE)
					tc.setCellEditor(new DefaultCellEditor(m_oPlaceCbx));
				else if(c.getField() == MyCellarFields.TYPE)
					tc.setCellEditor(new DefaultCellEditor(m_oTypeCbx));
				else if(c.getField() == MyCellarFields.COLOR)
					tc.setCellEditor(new DefaultCellEditor(m_oColorCbx));
				/*else if(c.getField() == MyCellarFields.COUNTRY)
					tc.setCellEditor(new DefaultCellEditor(m_oCountryCbx));
				else if(c.getField() == MyCellarFields.VINEYARD)
					tc.setCellEditor(new DefaultCellEditor(m_oVineyardCbx));
				else if(c.getField() == MyCellarFields.AOC)
					tc.setCellEditor(new DefaultCellEditor(m_oAOCCbx));
				else if(c.getField() == MyCellarFields.IGP)
					tc.setCellEditor(new DefaultCellEditor(m_oIGPCbx));*/
				buffer.append(c.getField().name()).append(";");
			}
			buffer.setLength(buffer.length()-1);
			Program.saveShowColumns(buffer.toString());
		}	
	}
	
	class ModifyBottlesAction extends AbstractAction {

		private static final long serialVersionUID = -7590310564039085580L;
		
		public ModifyBottlesAction() {
			super(Program.getLabel("Infos079"), MyCellarImage.WINE);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			LinkedList<Bouteille> bottles = new LinkedList<Bouteille>();

			try {
				int max_row = tv.getRowCount();
				if(max_row != 0) {
					int row = 0;
					do {
						if (((Boolean)tv.getValueAt(row, TableShowValues.ETAT)).equals(Boolean.TRUE))
							bottles.add(tv.getBottle(row));
						row++;
					}
					while (row < max_row);
				}

				if(bottles.isEmpty()) {
					String erreur_txt1 = Program.getError("Error071"); //"Aucun vin à modifier!");
					String erreur_txt2 = Program.getError("Error072"); //"Veuillez sélectionner les vins à modifier.");
					new Erreur(erreur_txt1, erreur_txt2, true);
				}
				else {
					Debug("Modifying "+bottles.size()+" bottles...");
					modifyBottles(bottles);
				}
			}
			catch (Exception exc) {
				Program.showException(exc);
			}
		}
		
	}

	public void Debug(String text) {
		Program.Debug("ShowFile: "+text);
	}
}
