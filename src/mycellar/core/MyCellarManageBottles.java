package mycellar.core;

import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.LinkedList;

import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import mycellar.actions.ManageVineyardAction;
import mycellar.BottleColor;
import mycellar.Bouteille;
import mycellar.Erreur;
import mycellar.JCompletionComboBox;
import mycellar.ManageList;
import mycellar.MyClipBoard;
import mycellar.MyXmlDom;
import mycellar.Program;
import mycellar.Rangement;
import mycellar.RangementUtils;
import mycellar.Vignoble;
import mycellar.countries.Countries;
import mycellar.countries.Country;
import mycellar.vignobles.CountryVignoble;
import mycellar.vignobles.CountryVignobles;
import mycellar.vignobles.Vignobles;

/**
 * <p>Titre : Cave à vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2017</p>
 * <p>Société : Seb Informatique</p>
 * @author Sébastien Duché
 * @version 0.4
 * @since 18/07/17
 */
public class MyCellarManageBottles extends JPanel {

	private static final long serialVersionUID = 3056306291164598750L;
	
	protected final MyCellarLabel m_labelName = new MyCellarLabel();
	protected final MyCellarLabel m_labelYear = new MyCellarLabel();
	protected final MyCellarLabel m_labelPlace = new MyCellarLabel();
	protected final MyCellarLabel m_labelNumPlace = new MyCellarLabel();
	protected final MyCellarLabel m_contenance = new MyCellarLabel();
	protected final MyCellarLabel m_labelLine = new MyCellarLabel();
	protected final MyCellarLabel m_labelColumn = new MyCellarLabel();
	protected final MyCellarLabel m_labelPrice = new MyCellarLabel();
	protected final MyCellarLabel m_labelNbBottle = new MyCellarLabel();
	protected final MyCellarLabel m_labelMaturity = new MyCellarLabel();
	protected final MyCellarLabel m_labelParker = new MyCellarLabel();
	protected final MyCellarLabel m_labelColor = new MyCellarLabel();
	protected final MyCellarLabel m_labelComment = new MyCellarLabel();
	protected final MyCellarButton m_preview = new MyCellarButton();
	protected final MyCellarLabel m_labelStillToAdd = new MyCellarLabel();
	protected final MyCellarLabel m_end = new MyCellarLabel(); // Label pour les résultats
	protected final MyCellarCheckBox m_annee_auto = new MyCellarCheckBox();
	protected int SIECLE = Program.getCaveConfigInt("SIECLE", 20) - 1;
	protected Object m_objet1 = null;
	protected final MyClipBoard clipboard = new MyClipBoard();
	protected final JModifyComboBox<String> m_lieu = new JModifyComboBox<String>();
	protected final JModifyComboBox<String> m_num_lieu = new JModifyComboBox<String>();
	protected final JModifyComboBox<String> m_line = new JModifyComboBox<String>();
	protected final JModifyComboBox<String> m_column = new JModifyComboBox<String>();
	protected final MyCellarLabel m_labelExist = new MyCellarLabel();
	protected MyCellarButton m_add;
	protected MyCellarButton m_cancel;
	protected JCompletionComboBox name = new JCompletionComboBox();
	protected final JModifyTextField m_year = new JModifyTextField();
	protected final JModifyComboBox<String> m_half = new JModifyComboBox<String>();
	protected final MyCellarCheckBox m_noYear = new MyCellarCheckBox();
	protected final JModifyFormattedTextField m_price = new JModifyFormattedTextField(java.text.NumberFormat.getNumberInstance());
	protected final JModifyTextField m_maturity = new JModifyTextField();
	protected final JModifyTextField m_parker = new JModifyTextField();
	protected final JModifyComboBox<BottleColor> m_colorList = new JModifyComboBox<BottleColor>();
	protected JModifyTextArea m_comment = new JModifyTextArea();
	protected final JScrollPane m_js_comment = new JScrollPane(m_comment);
	protected JCompletionComboBox comboCountry;
	protected JCompletionComboBox comboVignoble;
	protected JCompletionComboBox comboAppelationAOC;
	protected JCompletionComboBox comboAppelationIGP;
	protected final MyCellarButton m_manageContenance = new MyCellarButton();
	protected final MyCellarSpinner m_nb_bottle = new MyCellarSpinner();
	protected boolean updateView = false;
	protected MyCellarButton m_chooseCell;
	protected PanelVignobles panelVignobles;
	protected final MyCellarLabel labelCountry = new MyCellarLabel();
	protected final MyCellarLabel labelVignoble = new MyCellarLabel();
	protected final MyCellarLabel labelAppelationAOC = new MyCellarLabel();
	protected final MyCellarLabel labelAppelationIGP = new MyCellarLabel();
	protected final MyCellarButton manageVineyardButton = new MyCellarButton(new ManageVineyardAction());
	protected Bouteille m_laBouteille = null;
	protected char AJOUTER = Program.getLabel("AJOUTER").charAt(0);
	protected char PREVIEW = Program.getLabel("PREVIEW").charAt(0);
	protected final MyCellarLabel m_devise = new MyCellarLabel(Program.getCaveConfigString("DEVISE", "€"));
	protected final JPopupMenu popup = new JPopupMenu();
	protected final JMenuItem couper = new JMenuItem(Program.getLabel("Infos241"), new ImageIcon("./resources/Cut16.gif"));
	protected final JMenuItem copier = new JMenuItem(Program.getLabel("Infos242"), new ImageIcon("./resources/Copy16.gif"));
	protected final JMenuItem coller = new JMenuItem(Program.getLabel("Infos243"), new ImageIcon("./resources/Paste16.gif"));
	protected final JMenuItem cut = new JMenuItem(Program.getLabel("Infos241"), new ImageIcon("./resources/Cut16.gif"));
	protected final JMenuItem copy = new JMenuItem(Program.getLabel("Infos242"), new ImageIcon("./resources/Copy16.gif"));
	protected final JMenuItem paste = new JMenuItem(Program.getLabel("Infos243"), new ImageIcon("./resources/Paste16.gif"));
	protected ItemListener lieuListener;
	protected ItemListener numLieuListener;
	protected ItemListener lineListener;
	protected ItemListener columnListener;
	private boolean listenersEnabled = true;
	
	protected boolean m_bmulti = false; //Pour ListVin
	
	public MyCellarManageBottles() {
		m_labelName.setText(Program.getLabel("Infos208")); //"Nom");
		m_labelYear.setText(Program.getLabel("Infos189")); //"Année");
		m_labelPlace.setText(Program.getLabel("Infos208")); //"Emplacement du vin");
		m_labelNumPlace.setText(Program.getLabel("Infos082")); //"Numéro du lieu");
		m_labelLine.setText(Program.getLabel("Infos028")); //"Ligne");
		m_labelColumn.setText(Program.getLabel("Infos083")); //"Colonne");
		m_labelPrice.setText(Program.getLabel("Infos135")); //"Prix");
		m_labelNbBottle.setText(Program.getLabel("Infos136") + ":"); //"Nombre de bouteilles");
		m_labelNbBottle.setHorizontalAlignment(MyCellarLabel.RIGHT);
		m_labelComment.setText(Program.getLabel("Infos137")); //"Commentaires");
		m_labelMaturity.setText(Program.getLabel("Infos391")); // Date de conso
		m_labelParker.setText(Program.getLabel("Infos392")); // Notation Parker
		m_labelColor.setText(Program.getLabel("AddVin.Color"));
		labelCountry.setText(Program.getLabel("Main.Country"));
		labelVignoble.setText(Program.getLabel("Main.Vignoble"));
		labelAppelationAOC.setText(Program.getLabel("Main.AppelationAOC"));
		labelAppelationIGP.setText(Program.getLabel("Main.AppelationIGP"));
		m_manageContenance.setText(Program.getLabel("Infos400"));
		m_preview.setMnemonic(PREVIEW);
		m_preview.setText(Program.getLabel("Infos138")); //"Visualiser le rangement");
		m_preview.setEnabled(false);
		m_preview.addActionListener((e) -> preview_actionPerformed(e));

		this.addKeyListener(new java.awt.event.KeyListener() {
			public void keyReleased(java.awt.event.KeyEvent e) {}

			public void keyPressed(java.awt.event.KeyEvent e) {
				keylistener_actionPerformed(e);
			}

			public void keyTyped(java.awt.event.KeyEvent e) {}
		});

		//Menu Contextuel
		couper.addActionListener((e) -> couper_actionPerformed(e));
		cut.addActionListener((e) -> couper_actionPerformed(e));

		copier.addActionListener((e) -> copier_actionPerformed(e));
		copy.addActionListener((e) -> copier_actionPerformed(e));

		coller.addActionListener((e) -> coller_actionPerformed(e));
		paste.addActionListener((e) -> coller_actionPerformed(e));
		
		m_colorList.addItem(BottleColor.NONE);
		m_colorList.addItem(BottleColor.RED);
		m_colorList.addItem(BottleColor.PINK);
		m_colorList.addItem(BottleColor.WHITE);
	}
	
	protected void keylistener_actionPerformed(KeyEvent e) {
		
	}
	
	protected void annee_auto_actionPerformed(ActionEvent e) {
		Debug("Annee_auto_actionPerformed...");
		if (!m_annee_auto.isSelected()) {
			Program.putCaveConfigInt("ANNEE_AUTO", 1);
			
			if (Program.getCaveConfigInt("ANNEE_AUTO_FALSE", 0) == 0) {
				String erreur_txt1 = Program.getError("Error084"); //"En d�cochant cette option, vous d�sactivez la transformation");
				String erreur_txt2 = Program.getError("Error085") + ( (SIECLE + 1) * 100) + ")."; //"automatique des ann�es sur 2 chiffres en 4 chiffres (00->2000).");
				new Erreur(erreur_txt1, erreur_txt2, true, "", true, "ANNEE_AUTO_FALSE");
			}
		}
		else {
			Program.putCaveConfigInt("ANNEE_AUTO", 0);

			if (Program.getCaveConfigInt("ANNEE_AUTO_TRUE", 0) == 0) {
				String erreur_txt1 = Program.getError("Error086"); //"En cochant cette option, vous activez la transformation");
				String erreur_txt2 = Program.getError("Error085") + ( (SIECLE + 1) * 100) + ")."; //"automatique des ann�es sur 2 chiffres en 4 chiffres (00->2000).");
				new Erreur(erreur_txt1, erreur_txt2, true, "", true, "ANNEE_AUTO_TRUE");
			}
		}
		Debug("Annee_auto_actionPerformed...End");
	}
	
	/**
	 * coller_actionPerformed: Couper
	 *
	 * @param e ActionEvent
	 */
	protected void coller_actionPerformed(ActionEvent e) {

		try {
			JTextField jtf = (JTextField) m_objet1;
			jtf.setText(jtf.getText().substring(0, jtf.getSelectionStart()) + clipboard.coller() + jtf.getText().substring(jtf.getSelectionEnd()));
		}
		catch (Exception e1) {}
		try {
			JTextArea jtf = (JTextArea) m_objet1;
			jtf.setText(jtf.getText().substring(0, jtf.getSelectionStart()) + clipboard.coller() + jtf.getText().substring(jtf.getSelectionEnd()));
		}
		catch (Exception e1) {}
	}
	
	/**
	 * couper_actionPerformed: Couper
	 *
	 * @param e ActionEvent
	 */
	protected void couper_actionPerformed(ActionEvent e) {
		String txt = "";
		try {
			JTextField jtf = (JTextField) m_objet1;
			txt = jtf.getSelectedText();
			jtf.setText(jtf.getText().substring(0, jtf.getSelectionStart()) + jtf.getText().substring(jtf.getSelectionEnd()));
		}
		catch (Exception e1) {}
		try {
			JTextArea jtf = (JTextArea) m_objet1;
			txt = jtf.getSelectedText();
			jtf.setText(jtf.getText().substring(0, jtf.getSelectionStart()) + jtf.getText().substring(jtf.getSelectionEnd()));
		}
		catch (Exception e1) {}

		clipboard.copier(txt);
	}

	/**
	 * copier_actionPerformed: Copier
	 *
	 * @param e ActionEvent
	 */
	protected void copier_actionPerformed(ActionEvent e) {
		String txt = "";
		try {
			JTextField jtf = (JTextField) m_objet1;
			txt = jtf.getSelectedText();
		}
		catch (Exception e1) {}
		try {
			JTextArea jtf = (JTextArea) m_objet1;
			txt = jtf.getSelectedText();
		}
		catch (Exception e1) {}

		clipboard.copier(txt);
	}
	
	/**
	 * column_itemStateChanged: Fonction pour la liste des colonnes.
	 *
	 * @param e ItemEvent
	 */
	protected void column_itemStateChanged(ItemEvent e) {
		SwingUtilities.invokeLater(() -> {
       		Debug("Column_itemStateChanging...");
       		int nPlace = m_lieu.getSelectedIndex();
       		int nNumLieu = m_num_lieu.getSelectedIndex();
       		int nLine = m_line.getSelectedIndex();
       		int nColumn = m_column.getSelectedIndex();
       
       		if ( nPlace < 1 || nNumLieu < 1 || nLine < 1 || nColumn < 1 )
       			return;
       
       		Bouteille b;
       		m_labelExist.setText("");
       		if ((b = Program.getCave(nPlace - 1).getBouteille(nNumLieu-1, nLine-1, nColumn-1)) != null){
       			m_labelExist.setText(Program.getLabel("Infos329")+" "+Program.convertStringFromHTMLString(b.getNom()));
       		}
       		Debug("Column_itemStateChanging... End");
		});
	}
	
	protected void enableAll(boolean enable) {
		m_lieu.setEnabled(enable);
		m_num_lieu.setEnabled(enable && m_lieu.getSelectedIndex() > 0);
		m_line.setEnabled(enable && m_num_lieu.getSelectedIndex() > 0);
		m_column.setEnabled(enable && m_line.getSelectedIndex() > 0);
		m_add.setEnabled(enable);
		if(m_cancel != null)
			m_cancel.setEnabled(enable);
		m_half.setEnabled(enable && !m_bmulti);
		name.setEditable(enable && !m_bmulti);
		m_year.setEditable(enable && !m_noYear.isSelected());
		m_price.setEditable(enable);
		m_maturity.setEditable(enable);
		m_parker.setEditable(enable);
		m_colorList.setEditable(enable);
		m_comment.setEditable(enable);
		m_annee_auto.setEnabled(enable);
		m_noYear.setEnabled(enable);
		m_nb_bottle.setEnabled(enable && !m_bmulti);
		m_manageContenance.setEnabled(enable);
		comboCountry.setEnabled(enable);
		comboVignoble.setEnabled(enable);
		comboAppelationAOC.setEnabled(enable);
		comboAppelationIGP.setEnabled(enable);
	}
	
	protected String getYear() {
		
		if(m_noYear.isSelected())
			return Bouteille.NON_VINTAGE;
		
		String annee = m_year.getText().trim();
		if( m_annee_auto.isSelected() && annee.length() == 2)
		{
			int n = Program.getCaveConfigInt("ANNEE", 50);
			int siecle = Program.getCaveConfigInt("SIECLE", 20);
			try
			{
				if( Integer.parseInt(annee) > n )
					annee = Integer.toString(siecle-1) + annee;
				else
					annee = Integer.toString(siecle) + annee;
			}
			catch(NumberFormatException e) {
				// On doit déjà avoir eu un message d'erreur avant
			}
		}
		return annee;
	}
	
	protected void initializeVignobles(Bouteille bottle) {
		if(bottle == null)
			return;
		Vignoble v = bottle.getVignoble();
		Vignobles vignobles = null;
		
		if(v == null)
			return;
		if(Program.france.getId().equals(v.country)) {
			comboCountry.setSelectedItem(Program.france);
			vignobles = CountryVignobles.getVignobles(Program.france);
		}
		else if("fr".equals(v.country)) {
			comboCountry.setSelectedItem(Program.france);
			vignobles = CountryVignobles.getVignobles(Program.france);
		}
		else if(v.country != null) {
			Country c = Countries.findByIdOrLabel(v.country);
			if(c != null) {
				comboCountry.setSelectedItem(c);
				vignobles = CountryVignobles.getVignobles(c);
			}
		}

		if(vignobles != null) {
			CountryVignoble v2 = vignobles.findVignoble(v);
		
    		if(v2 != null)
    			comboVignoble.setSelectedItem(v2);
		}

		if(v.aoc != null)
			comboAppelationAOC.setSelectedItem(v.aoc);

		if(v.igp != null)
			comboAppelationIGP.setSelectedItem(v.igp);
	}

	/**
	 * Gestion des listes
	 * @param e
	 */
	protected void manageContenance_actionPerformed(ActionEvent e) {
		new ManageList(Program.half, Program.getLabel("Infos400"));
		String selected = m_half.getSelectedItem().toString();
		m_half.removeAllItems();
		m_half.addItem("");
		for(String s:Program.half)
			m_half.addItem(s);
		m_half.setSelectedItem(selected);
		if(m_half.getSelectedIndex() == 0)
			m_half.setSelectedItem(Program.defaut_half);
	}
	
	/**
	 * preview_actionPerformed: Fonction pour visualiser un rangement.
	 *
	 * @param e ActionEvent
	 */
	protected void preview_actionPerformed(ActionEvent e) {
		Debug("Previewing...");
		try {
			int num_select = m_lieu.getSelectedIndex();
			//Program.getCave(num_select - 1).putTabStock();
			RangementUtils.putTabStock();
			LinkedList<Rangement> rangements = new LinkedList<Rangement>();
			rangements.add(Program.getCave(num_select - 1));
			MyXmlDom.writeRangements(Program.getPreviewXMLFileName(), rangements, false);
			Program.open( new File(Program.getPreviewXMLFileName()) );
		}
		catch (Exception a) {
			Program.showException(a);
		}
		Debug("Previewing... End");
	}
	
	protected void lieu_itemStateChanged(ItemEvent e) {
		
	}
	
	protected void line_itemStateChanged(ItemEvent e) {
		
	}
	
	/**
	 * num_lieu_itemStateChanged: Fonction pour la liste des numéros de lieu.
	 *
	 * @param e ItemEvent
	 */
	protected void num_lieu_itemStateChanged(ItemEvent e) {
		if(!isListenersEnabled())
			return;
		SwingUtilities.invokeLater(() -> {
			Debug("Num_lieu_itemStateChanging...");
			int nb_ligne = 0;
			int num_select = m_num_lieu.getSelectedIndex();
			int lieu_select = m_lieu.getSelectedIndex();
			boolean isCaisse = false;

			m_labelExist.setText("");

			if (num_select == 0) {
				m_line.setEnabled(false);
				m_column.setEnabled(false);
			}
			else {
				m_line.setEnabled(true);
			}
			if (num_select > 0) { //!=0
				isCaisse = Program.getCave(lieu_select - 1).isCaisse();
				if (!isCaisse) {
					if (num_select != 0) {
						nb_ligne = Program.getCave(lieu_select - 1).getNbLignes(num_select - 1);
					}
					m_line.removeAllItems();
					m_column.removeAllItems();
					m_line.addItem("");
					for (int i = 1; i <= nb_ligne; i++) {
						m_line.addItem(Integer.toString(i));
					}
				}
			}
			setVisible(true);
			Debug("Num_lieu_itemStateChanging... End");
		});
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
		SwingUtilities.invokeLater(() -> {
			Debug("updateView...");
			updateView = false;
			m_lieu.removeAllItems();
			m_lieu.addItem("");
			boolean complex = false;
			for (Rangement r : Program.getCave()) {
				m_lieu.addItem(r.getNom());
				if(!r.isCaisse())
					complex = true;
			}
			m_chooseCell.setEnabled(complex);
			m_half.removeAllItems();
			m_half.addItem("");
			for(String s : Program.half)
				m_half.addItem(s);
			m_half.setSelectedItem(Program.defaut_half);
			panelVignobles.updateList();
			Debug("updateView Done");
		});
	}
	
	protected void setListeners() {
		m_lieu.addItemListener(lieuListener = new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				lieu_itemStateChanged(e);
			}
		});
		m_num_lieu.addItemListener(numLieuListener = new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				num_lieu_itemStateChanged(e);
			}
		});

		m_line.addItemListener(lineListener = new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				line_itemStateChanged(e);
			}
		});
		m_column.addItemListener(columnListener = new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				column_itemStateChanged(e);
			}
		});
	}
	
	/**
	 * Select a place in the lists (used from CellarOrganizerPanel)
	 * @param rangement
	 * @param place
	 * @param row
	 * @param column
	 */
	public void selectPlace(Rangement rangement, int place, int row, int column) {
		setListenersEnabled(false);
		for(int i=0; i<m_lieu.getItemCount(); i++) {
			if(rangement.getNom().equals(m_lieu.getItemAt(i))){
				m_lieu.setSelectedIndex(i);
				break;
			}
		}
		m_labelExist.setText("");

		m_preview.setEnabled(true);
		int nbEmpl = rangement.getNbEmplacements();
		int nbLine = rangement.getNbLignes(place);
		int nbColumn = rangement.getNbColonnes(place, row);
		m_num_lieu.removeAllItems();
		m_column.removeAllItems();
		m_line.removeAllItems();
		m_num_lieu.addItem("");
		m_line.addItem("");
		m_column.addItem("");
		m_num_lieu.setEnabled(true);
		m_line.setEnabled(true);
		m_column.setEnabled(true);
		for(int i = 1; i<= nbEmpl; i++)
			m_num_lieu.addItem(Integer.toString(i));
		for(int i = 1; i<= nbLine; i++)
			m_line.addItem(Integer.toString(i));
		for(int i = 1; i<= nbColumn; i++)
			m_column.addItem(Integer.toString(i));
		m_num_lieu.setSelectedIndex(place+1);
		m_line.setSelectedIndex(row+1);
		m_column.setSelectedIndex(column+1);
		m_labelLine.setVisible(true);
		m_labelColumn.setVisible(true);
		m_line.setVisible(true);
		m_column.setVisible(true);
		setListenersEnabled(true);
	}
	
	public void selectPlace(Bouteille bottle) {
		Debug("selectPlaceWithBottle...");
		setListenersEnabled(false);
		Rangement rangement = bottle.getRangement();
		for(int i=0; i<m_lieu.getItemCount(); i++) {
			if(rangement.getNom().equals(m_lieu.getItemAt(i))){
				m_lieu.setSelectedIndex(i);
				break;
			}
		}
		int nbEmpl = rangement.getNbEmplacements();
		int nbLine = -1;
		int nbColumn = -1;
		m_num_lieu.removeAllItems();
		m_column.removeAllItems();
		m_line.removeAllItems();
		m_num_lieu.addItem("");
		m_line.addItem("");
		m_column.addItem("");
		
		
		boolean isCaisse = rangement.isCaisse();
		if(!isCaisse) {
			for(int i = 1; i<= nbEmpl; i++)
				m_num_lieu.addItem(Integer.toString(i));
			nbLine = rangement.getNbLignes(bottle.getNumLieu()-1);
			nbColumn = rangement.getNbColonnes(bottle.getNumLieu()-1, bottle.getLigne()-1);
			for(int i = 1; i<= nbLine; i++)
				m_line.addItem(Integer.toString(i));
			for(int i = 1; i<= nbColumn; i++)
				m_column.addItem(Integer.toString(i));
			m_line.setEnabled(true);
			m_column.setEnabled(true);
			m_num_lieu.setSelectedIndex(bottle.getNumLieu());
			m_line.setSelectedIndex(bottle.getLigne());
			m_column.setSelectedIndex(bottle.getColonne());
		}
		else {
			int start = rangement.getStartCaisse();
			for(int i = start; i< nbEmpl+start; i++)
				m_num_lieu.addItem(Integer.toString(i));
			m_num_lieu.setSelectedIndex(bottle.getNumLieu()-start+1);
		}
		m_num_lieu.setEnabled(true);
		
		m_labelLine.setVisible(!isCaisse);
		m_labelColumn.setVisible(!isCaisse);
		m_line.setVisible(!isCaisse);
		m_column.setVisible(!isCaisse);
		setListenersEnabled(true);
		Debug("selectPlaceWithBottle... Done");
	}
	
	protected boolean isListenersEnabled() {
		return listenersEnabled;
	}

	protected void setListenersEnabled(boolean listenersEnabled) {
		this.listenersEnabled = listenersEnabled;
	}

	protected static void Debug(String s) {
		
	}

}
