package mycellar.showfile;


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

import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import mycellar.actions.OpenAddVinAction;
import mycellar.BottleColor;
import mycellar.Bouteille;
import mycellar.Erreur;
import mycellar.History;
import mycellar.ITabListener;
import mycellar.MyCellarImage;
import mycellar.Program;
import mycellar.Rangement;
import mycellar.RangementUtils;
import mycellar.Start;
import mycellar.StateButtonEditor;
import mycellar.StateButtonRenderer;
import mycellar.StateEditor;
import mycellar.StateRenderer;
import mycellar.TabEvent;
import mycellar.ToolTipRenderer;
import mycellar.Vignoble;
import mycellar.core.MyCellarButton;
import mycellar.core.MyCellarComboBox;
import mycellar.core.MyCellarError;
import mycellar.core.MyCellarFields;
import mycellar.core.MyCellarLabel;
import mycellar.countries.Countries;
import mycellar.countries.Country;
import net.miginfocom.swing.MigLayout;

/**
 * <p>Titre : Cave à vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 1998</p>
 * <p>Societe : Seb Informatique</p>
 * @author Sébastien Duché
 * @version 4.5
 * @since 29/07/17
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
	private ArrayList<ShowFileColumn> columns = new ArrayList<ShowFileColumn>();
	private ShowType showType;
	
	public enum ShowType{
		NORMAL,
		TRASH,
		ERROR
	};

	static final long serialVersionUID = 020107;

	public ShowFile() {
		showType = ShowType.NORMAL;
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
				       	 new Erreur(Program.getError("Error053"), Program.getError("Error015"));
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
	
    public ShowFile(ShowType type) {
		this.showType = type;
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
		m_oResultLabel.setHorizontalAlignment(SwingConstants.CENTER);
		m_oResultLabel.setForeground(Color.red);
		this.setLayout(new MigLayout("","[][grow]","[]10px[grow][]"));
		if(showType == ShowType.TRASH) {
			m_oDeleteButton.setText(Program.getLabel("ShowFile.Restore"));
			m_oDeleteButton.setIcon(MyCellarImage.RESTORE);
		} else
			m_oDeleteButton.setText(Program.getLabel("Infos051"));
		
		m_oDeleteButton.addActionListener((e) -> {
			if(showType == ShowType.TRASH)
				restore();
			else	
				delete_actionPerformed(e);
		});
		if(showType == ShowType.NORMAL) {
			add(m_oTitleLabel, "align left");
			add(m_oManageButton, "align right, split 3");
			add(m_oModifyButton, "align right");
			add(m_oDeleteButton, "align right, wrap");
		} else {
			add(m_oTitleLabel, "align left");
			add(m_oDeleteButton, "align right, wrap");
		}
		
		
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
		if(showType == ShowType.TRASH) {
			tv = new TableShowValues();
			tv.setBottles(Program.getTrash());
			m_oTable = new JTable(tv);
		}
		else if(showType == ShowType.ERROR) {
			tv = new ErrorShowValues();
			((ErrorShowValues) tv).setErrors(Program.getErrors());
			m_oTable = new JTable(tv);
			m_oTitleLabel.setText(Program.getLabel("ShowFile.manageError"));
		}
		else {
			tv = new ShowFileModel();
			tv.setBottles(Program.getStorage().getAllList());
			String savedColumns = Program.getShowColumns();
			ArrayList<ShowFileColumn> cols;
			if(savedColumns.isEmpty()) {
				cols = (ArrayList<ShowFileColumn>) columns.stream().filter((field) -> {
					return !field.getField().equals(MyCellarFields.VINEYARD)
							&& !field.getField().equals(MyCellarFields.AOC)
							&& !field.getField().equals(MyCellarFields.IGP)
							&& !field.getField().equals(MyCellarFields.COUNTRY);}).collect(Collectors.toList());
				((ShowFileModel)tv).setColumns(cols);
			} else {
				cols = new ArrayList<ShowFileColumn>();
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
		TableColumn tc = tcm.getColumn(TableShowValues.ETAT);
		tc.setCellRenderer(new StateRenderer());
		tc.setCellEditor(new StateEditor());
		tc.setMinWidth(25);
		tc.setMaxWidth(25);
		if(showType == ShowType.ERROR) {
			tc = tcm.getColumn(ErrorShowValues.PLACE);
			tc.setCellEditor(new DefaultCellEditor(m_oPlaceCbx));
			tc = tcm.getColumn(ErrorShowValues.TYPE);
			tc.setCellEditor(new DefaultCellEditor(m_oTypeCbx));
			tc = tcm.getColumn(ErrorShowValues.STATUS);
			tc.setCellRenderer(new FontBoldTableCellRenderer());
			tc = tcm.getColumn(ErrorShowValues.BUTTON);
			tc.setCellRenderer(new StateButtonRenderer(Program.getLabel("Infos071"), MyCellarImage.ADD));
			tc.setCellEditor(new StateButtonEditor());
		}
		else if(showType == ShowType.NORMAL) {
			tc = tcm.getColumn(tcm.getColumnCount()-1);
			tc.setCellRenderer(new StateButtonRenderer());
			tc.setCellEditor(new StateButtonEditor());
			tc.setMinWidth(100);
			tc.setMaxWidth(100);
			tc = tcm.getColumn(TableShowValues.PLACE);
			tc.setCellEditor(new DefaultCellEditor(m_oPlaceCbx));
			tc = tcm.getColumn(TableShowValues.TYPE);
			tc.setCellEditor(new DefaultCellEditor(m_oTypeCbx));
		}

		m_oTable.setPreferredScrollableViewportSize(new Dimension(300, 200));
        m_oTable.setAutoResizeMode(JTable.AUTO_RESIZE_NEXT_COLUMN);
		m_oScroll = new JScrollPane(m_oTable);
			
		this.add(m_oScroll, "grow, span 2, wrap");
		this.add(m_oResultLabel, "span 2, alignx center, hidemode 3");
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
					erreur_txt1 = MessageFormat.format(Program.getError("Error130"), toDeleteList.size()); //vins sélectionnés.");
					erreur_txt2 = Program.getError("Error131"); //"Voulez-vous les supprimer?");
				}
				if( JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(null, erreur_txt1 + " " + erreur_txt2, Program.getLabel("Infos049"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE))	
				{
					if(showType == ShowType.ERROR) {
						for (int i = 0; i < toDeleteList.size(); i++) {
							Bouteille b = (Bouteille) toDeleteList.get(i);
							Program.getErrors().remove(new MyCellarError(0, b));
						}
					} else {
						for (int i = 0; i < toDeleteList.size(); i++) {
							Bouteille b = (Bouteille) toDeleteList.get(i);
							Program.getStorage().addHistory(History.DEL, b);
							Program.getStorage().deleteWine(b);
							Program.setToTrash(b);
						}
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
					erreur_txt1 = MessageFormat.format(Program.getError("Error130"), toRestoreList.size()); //vins sélectionnés.");
					erreur_txt2 = Program.getLabel("ShowFile.RestoreSeveral");
				}
				if( JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(null, erreur_txt1 + " " + erreur_txt2, Program.getLabel("Infos049"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE))	
				{
					LinkedList<Bouteille> cantRestoreList = new LinkedList<Bouteille>();
					for (int i = 0; i < toRestoreList.size(); i++) {
						Bouteille b = (Bouteille) toRestoreList.get(i);
						Program.getTrash().remove(b);
						Rangement r = Program.getCave(b.getEmplacement());
						if(r != null) {
							if(r.isCaisse()){
								Program.getStorage().addHistory(History.ADD, b);
								Program.getStorage().addWine(b);
							}
							else {
								if(r.getBouteille(b.getNumLieu()-1, b.getLigne()-1, b.getColonne()-1) == null) {
									Program.getStorage().addHistory(History.ADD, b);
									Program.getStorage().addWine(b);
								}
								else
									cantRestoreList.add(b);
							}
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
		if(showType == ShowType.TRASH)
			tv.setBottles(Program.getTrash());
		else if(showType == ShowType.ERROR)
			((ErrorShowValues)tv).setErrors(Program.getErrors());
		else
			tv.setBottles(Program.getStorage().getAllList());
		m_oResultLabel.setText("");
	}
	
	private void setRangementValue(Bouteille b, MyCellarFields field, Object value) {
		
		String empl_old = b.getEmplacement();
    	int num_empl_old = b.getNumLieu();
    	int line_old = b.getLigne();
    	int column_old = b.getColonne();
    	Rangement rangement = Program.getCave(empl_old);
    	boolean bError = false;
    	int nValueToCheck = -1;
    	String empl = empl_old;
    	int num_empl = num_empl_old;
    	int line = line_old;
    	int column = column_old;
    	
    	Program.setModified();

    	if (field == MyCellarFields.PLACE) {
    		empl = (String)value; 
    		rangement = Program.getCave(empl);
    	}
    	else if (field == MyCellarFields.NUM_PLACE) {
    		try{
    			num_empl = Integer.parseInt((String)value);
    			nValueToCheck = num_empl;
    		}
            catch (Exception e) {
              new Erreur(Program.getError("Error196"));
              bError = true;
            }
    	}
    	else if (field == MyCellarFields.LINE) {
    		try{
    			line = Integer.parseInt((String)value);
    			nValueToCheck = line;
    		}
            catch (Exception e) {
              new Erreur(Program.getError("Error196"));
              bError = true;
            }
    	}
    	else if (field == MyCellarFields.COLUMN) {
    		try{
    			column = Integer.parseInt((String)value);
    			nValueToCheck = column;
    		}
            catch (Exception e) {
              new Erreur(Program.getError("Error196"));
              bError = true;
            }
    	}
    	
    	if ( !bError && (field == MyCellarFields.NUM_PLACE || field == MyCellarFields.LINE || field == MyCellarFields.COLUMN) ) {
    		if (rangement != null && !rangement.isCaisse() && nValueToCheck <= 0)
    		{
    			new Erreur(Program.getError("Error197"));
                bError = true;
    		}
    	}
    		
    	if ( !bError && (empl_old.compareTo(empl) != 0 || num_empl_old != num_empl || line_old != line || column_old != column)) {
    		// Controle de l'emplacement de la bouteille
    		if(rangement.canAddBottle(num_empl, line, column)) {
		    	Bouteille bTemp = null;
		    	if(!rangement.isCaisse())
		    		bTemp = rangement.getBouteille(num_empl-1, line-1, column-1);
		    	if( bTemp != null) {
		    		new Erreur(MessageFormat.format(Program.getError("Error059"), Program.convertStringFromHTMLString(bTemp.getNom()), b.getAnnee()));
		    	}
		    	else {
		    		if(field == MyCellarFields.PLACE)
		    			b.setEmplacement((String)value);
		    		else if(field == MyCellarFields.NUM_PLACE)
		    			b.setNumLieu(Integer.parseInt((String)value));
		    		else if(field == MyCellarFields.LINE)
		    			b.setLigne(Integer.parseInt((String)value));
		    		else if(field == MyCellarFields.COLUMN)
		    			b.setColonne(Integer.parseInt((String)value));
		    		if ( field == MyCellarFields.PLACE && rangement.isCaisse()) {
		    			int nNumEmpl = b.getNumLieu();//Integer.parseInt((String) values[row][NUM_PLACE]);
		    			if( nNumEmpl > rangement.getLastNumEmplacement())
		    				b.setNumLieu(rangement.getFreeNumPlaceInCaisse());
		    			b.setLigne(0);
		    			b.setColonne(0);
		    		}
		    		RangementUtils.putTabStock();
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
		if(showType == ShowType.ERROR && Program.getErrors().stream().map((e) -> e.isStatus()).count() > 0) {
			if(JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(null, Program.getLabel("ShowFile.QuitErrors"))) {
				return true;
			}
			return false;
		}
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
			ArrayList<MyCellarFields> list = new ArrayList<MyCellarFields>();
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
			ArrayList<ShowFileColumn> cols = ((ShowFileModel)tv).getColumns();
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
			cols = new ArrayList<ShowFileColumn>();
			Program.setModified();
			LinkedList<Integer> properties = modelColumn.getSelectedColumns();
			for(ShowFileColumn c : columns) {
				if(properties.contains(c.getField().ordinal()))
					cols.add(c);
			}
			if(!cols.isEmpty()) {
				((ShowFileModel)tv).removeAllColumns();
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
			}
			int i = 1;
			StringBuffer buffer = new StringBuffer();
			for(ShowFileColumn c : cols) {
				if(i > 1)
					buffer.append(';');
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
				buffer.append(c.getField().name());
			}
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
