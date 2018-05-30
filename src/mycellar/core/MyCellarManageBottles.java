package mycellar.core;

import mycellar.BottleColor;
import mycellar.Bouteille;
import mycellar.Erreur;
import mycellar.JCompletionComboBox;
import mycellar.ManageList;
import mycellar.MyXmlDom;
import mycellar.Program;
import mycellar.Rangement;
import mycellar.RangementUtils;
import mycellar.Vignoble;
import mycellar.actions.ManageVineyardAction;
import mycellar.core.datas.MyCellarBottleContenance;
import mycellar.countries.Countries;
import mycellar.countries.Country;
import mycellar.vignobles.CountryVignoble;
import mycellar.vignobles.CountryVignobles;
import mycellar.vignobles.Vignobles;
import net.miginfocom.swing.MigLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.io.File;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.util.LinkedList;

/**
 * <p>Titre : Cave à vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2017</p>
 * <p>Société : Seb Informatique</p>
 * @author Sébastien Duché
 * @version 1.6
 * @since 30/05/18
 */
public abstract class MyCellarManageBottles extends JPanel {

	private static final long serialVersionUID = 3056306291164598750L;
	
	private final MyCellarLabel m_labelName = new MyCellarLabel();
	private final MyCellarLabel m_labelYear = new MyCellarLabel();
	protected final MyCellarLabel m_labelPlace = new MyCellarLabel();
	protected final MyCellarLabel m_labelNumPlace = new MyCellarLabel();
	protected final MyCellarLabel m_contenance = new MyCellarLabel();
	protected final MyCellarLabel m_labelLine = new MyCellarLabel();
	protected final MyCellarLabel m_labelColumn = new MyCellarLabel();
	private final MyCellarLabel m_labelPrice = new MyCellarLabel();
	private final MyCellarLabel m_labelNbBottle = new MyCellarLabel();
	private final MyCellarLabel m_labelMaturity = new MyCellarLabel();
	private final MyCellarLabel m_labelParker = new MyCellarLabel();
	private final MyCellarLabel m_labelColor = new MyCellarLabel();
	protected final MyCellarLabel m_labelComment = new MyCellarLabel();
	protected final MyCellarButton m_preview = new MyCellarButton();
	protected final MyCellarLabel m_labelStillToAdd = new MyCellarLabel();
	protected final MyCellarLabel m_end = new MyCellarLabel(); // Label pour les résultats
	protected final MyCellarCheckBox m_annee_auto = new MyCellarCheckBox();
	protected int SIECLE = Program.getCaveConfigInt("SIECLE", 20) - 1;
	private Object m_objet1 = null;
	protected final JModifyComboBox<String> m_lieu = new JModifyComboBox<>();
	protected final JModifyComboBox<String> m_num_lieu = new JModifyComboBox<>();
	protected final JModifyComboBox<String> m_line = new JModifyComboBox<>();
	protected final JModifyComboBox<String> m_column = new JModifyComboBox<>();
	protected final MyCellarLabel m_labelExist = new MyCellarLabel();
	protected MyCellarButton m_add;
	protected MyCellarButton m_cancel;
	protected JCompletionComboBox name = new JCompletionComboBox();
	protected final JModifyTextField m_year = new JModifyTextField();
	protected final JModifyComboBox<String> m_half = new JModifyComboBox<>();
	protected final MyCellarCheckBox m_noYear = new MyCellarCheckBox();
	protected final JModifyFormattedTextField m_price = new JModifyFormattedTextField(NumberFormat.getNumberInstance());
	protected final JModifyTextField m_maturity = new JModifyTextField();
	protected final JModifyTextField m_parker = new JModifyTextField();
	protected final JModifyComboBox<BottleColor> m_colorList = new JModifyComboBox<>();
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
	final MyCellarLabel labelCountry = new MyCellarLabel();
	final MyCellarLabel labelVignoble = new MyCellarLabel();
	final MyCellarLabel labelAppelationAOC = new MyCellarLabel();
	final MyCellarLabel labelAppelationIGP = new MyCellarLabel();
	final MyCellarButton manageVineyardButton = new MyCellarButton(new ManageVineyardAction());
	protected Bouteille m_laBouteille = null;
	protected char AJOUTER = Program.getLabel("AJOUTER").charAt(0);
	protected char PREVIEW = Program.getLabel("PREVIEW").charAt(0);
	private final MyCellarLabel m_devise = new MyCellarLabel(Program.getCaveConfigString("DEVISE", "€"));
	private boolean listenersEnabled = true;
	
	protected boolean m_bmulti = false; //Pour ListVin
	protected boolean isEditionMode = false;
	
	public MyCellarManageBottles() {
		m_labelName.setText(Program.getLabel("Infos208")); //"Nom");
		m_labelYear.setText(Program.getLabel("Infos189")); //"Année");
		m_labelPlace.setText(Program.getLabel("Infos208")); //"Nom");
		m_labelNumPlace.setText(Program.getLabel("Infos082")); //"Numéro du lieu");
		m_labelLine.setText(Program.getLabel("Infos028")); //"Ligne");
		m_labelColumn.setText(Program.getLabel("Infos083")); //"Colonne");
		m_labelPrice.setText(Program.getLabel("Infos135")); //"Prix");
		m_labelNbBottle.setText(Program.getLabel("Infos136") + ":"); //"Nombre de bouteilles");
		m_labelNbBottle.setHorizontalAlignment(SwingConstants.RIGHT);
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
		m_preview.addActionListener(this::preview_actionPerformed);

		m_colorList.addItem(BottleColor.NONE);
		m_colorList.addItem(BottleColor.RED);
		m_colorList.addItem(BottleColor.PINK);
		m_colorList.addItem(BottleColor.WHITE);
	}
	
	protected void annee_auto_actionPerformed(ActionEvent e) {
		Debug("Annee_auto_actionPerformed...");
		if (!m_annee_auto.isSelected()) {
			Program.putCaveConfigInt("ANNEE_AUTO", 1);
			
			if (Program.getCaveConfigInt("ANNEE_AUTO_FALSE", 0) == 0) {
				String erreur_txt1 = MessageFormat.format(Program.getError("Error084"), ( (SIECLE + 1) * 100)); //"En décochant cette option, vous désactivez la transformation");
				Erreur.showKeyErreur(erreur_txt1, "", "ANNEE_AUTO_FALSE");
			}
		}
		else {
			Program.putCaveConfigInt("ANNEE_AUTO", 0);

			if (Program.getCaveConfigInt("ANNEE_AUTO_TRUE", 0) == 0) {
				String erreur_txt1 = MessageFormat.format(Program.getError("Error086"), ( (SIECLE + 1) * 100));//"En cochant cette option, vous activez la transformation");
				Erreur.showKeyErreur(erreur_txt1, "", "ANNEE_AUTO_TRUE");
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
			jtf.setText(jtf.getText().substring(0, jtf.getSelectionStart()) + Program.clipboard.coller() + jtf.getText().substring(jtf.getSelectionEnd()));
		}
		catch (Exception e1) {}
		try {
			JTextArea jtf = (JTextArea) m_objet1;
			jtf.setText(jtf.getText().substring(0, jtf.getSelectionStart()) + Program.clipboard.coller() + jtf.getText().substring(jtf.getSelectionEnd()));
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

		Program.clipboard.copier(txt);
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

		Program.clipboard.copier(txt);
	}
	
	/**
	 * column_itemStateChanged: Fonction pour la liste des colonnes.
	 *
	 * @param e ItemEvent
	 */
	private void column_itemStateChanged(ItemEvent e) {
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
			Rangement cave = Program.getCave(nPlace - 1);
			if(cave != null) {
				if ((b = cave.getBouteille(nNumLieu - 1, nLine - 1, nColumn - 1)) != null) {
					m_labelExist.setText(MessageFormat.format(Program.getLabel("Infos329"), Program.convertStringFromHTMLString(b.getNom())));
				}
			}
       		Debug("Column_itemStateChanging... End");
		});
	}
	
	public void enableAll(boolean enable) {
		m_lieu.setEnabled(enable);
		m_num_lieu.setEnabled(enable && m_lieu.getSelectedIndex() > 0);
		m_line.setEnabled(enable && m_num_lieu.getSelectedIndex() > 0);
		m_column.setEnabled(enable && m_line.getSelectedIndex() > 0);
		m_add.setEnabled(enable);
		if(m_cancel != null) {
			m_cancel.setEnabled(enable);
		}
		m_half.setEnabled(enable && !m_bmulti);
		name.setEnabled(enable && !m_bmulti);
		m_year.setEditable(enable && !m_noYear.isSelected());
		m_price.setEditable(enable);
		m_maturity.setEditable(enable);
		m_parker.setEditable(enable);
		m_colorList.setEnabled(enable);
		m_comment.setEditable(enable);
		m_annee_auto.setEnabled(enable);
		m_noYear.setEnabled(enable);
		m_nb_bottle.setEnabled(enable && !m_bmulti && !isEditionMode);
		m_manageContenance.setEnabled(enable);
		comboCountry.setEnabled(enable);
		comboVignoble.setEnabled(enable);
		comboAppelationAOC.setEnabled(enable);
		comboAppelationIGP.setEnabled(enable);
		if(m_chooseCell != null)
			m_chooseCell.setEnabled(enable);
		m_end.setVisible(enable);
	}
	
	protected String getYear() {
		
		if(m_noYear.isSelected()) {
			return Bouteille.NON_VINTAGE;
		}
		
		String annee = m_year.getText().trim();
		if( m_annee_auto.isSelected() && annee.length() == 2) {
			int n = Program.getCaveConfigInt("ANNEE", 50);
			int siecle = Program.getCaveConfigInt("SIECLE", 20);
			try
			{
				if( Integer.parseInt(annee) > n ) {
					annee = Integer.toString(siecle - 1) + annee;
				} else {
					annee = Integer.toString(siecle) + annee;
				}
			}
			catch(NumberFormatException e) {
				// On doit déjà avoir eu un message d'erreur avant
			}
		}
		return annee;
	}
	
	protected void initializeVignobles(Bouteille bottle) {
		if(bottle == null) {
			return;
		}
		Vignoble vignoble = bottle.getVignoble();
		if(vignoble == null) {
			return;
		}

		Vignobles vignobles = null;
		if(Program.france.getId().equals(vignoble.country)) {
			comboCountry.setSelectedItem(Program.france);
			vignobles = CountryVignobles.getVignobles(Program.france);
		}	else if("fr".equals(vignoble.country)) {
			comboCountry.setSelectedItem(Program.france);
			vignobles = CountryVignobles.getVignobles(Program.france);
		}	else if(vignoble.country != null) {
			Country c = Countries.findByIdOrLabel(vignoble.country);
			if(c != null) {
				comboCountry.setSelectedItem(c);
				vignobles = CountryVignobles.getVignobles(c);
			}
		}

		if(vignobles != null) {
			CountryVignoble countryVignoble = vignobles.findVignoble(vignoble);
    		if(countryVignoble != null) {
					comboVignoble.setSelectedItem(countryVignoble);
				}
		}

		if(vignoble.aoc != null) {
			comboAppelationAOC.setSelectedItem(vignoble.aoc);
		}

		if(vignoble.igp != null) {
			comboAppelationIGP.setSelectedItem(vignoble.igp);
		}
	}

	/**
	 * Gestion des listes
	 * @param e
	 */
	protected void manageContenance_actionPerformed(ActionEvent e) {
		new ManageList();
		String selected = "";
		if (m_half.getSelectedItem() != null) {
			selected = m_half.getSelectedItem().toString();
		}
		m_half.removeAllItems();
		m_half.addItem("");
		for(String s: MyCellarBottleContenance.getList()) {
			m_half.addItem(s);
		}
		if (name.isModified()) {
			m_half.setSelectedItem(selected);
		}	else {
			m_half.setSelectedItem(MyCellarBottleContenance.getDefaultValue());
		}
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
			RangementUtils.putTabStock();
			LinkedList<Rangement> rangements = new LinkedList<>();
			rangements.add(Program.getCave(num_select - 1));
			MyXmlDom.writeRangements(Program.getPreviewXMLFileName(), rangements, false);
			Program.open( new File(Program.getPreviewXMLFileName()) );
		}
		catch (Exception a) {
			Program.showException(a);
		}
		Debug("Previewing... End");
	}

	
	/**
	 * num_lieu_itemStateChanged: Fonction pour la liste des numéros de lieu.
	 *
	 * @param e ItemEvent
	 */
	private void num_lieu_itemStateChanged(ItemEvent e) {
		if(isListenersDisabled())
			return;
		SwingUtilities.invokeLater(() -> {
			Debug("Num_lieu_itemStateChanging...");
			int num_select = m_num_lieu.getSelectedIndex();
			int lieu_select = m_lieu.getSelectedIndex();

			m_labelExist.setText("");

			if (num_select == 0) {
				m_line.setEnabled(false);
				m_column.setEnabled(false);
			}
			else {
				m_line.setEnabled(true);
			}
			Rangement r;
			if (num_select > 0 && null != (r = Program.getCave( lieu_select - 1))) { //!=0
				if (!r.isCaisse()) {
					int nb_ligne = r.getNbLignes(num_select - 1);
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
		if(!updateView) {
			return;
		}
		SwingUtilities.invokeLater(() -> {
			Debug("updateView...");
			updateView = false;
			m_lieu.removeAllItems();
			m_lieu.addItem("");
			boolean complex = false;
			for (Rangement r : Program.getCave()) {
				m_lieu.addItem(r.getNom());
				if(!r.isCaisse()) {
					complex = true;
				}
			}
			m_chooseCell.setEnabled(complex);
			m_half.removeAllItems();
			m_half.addItem("");
			for(String s : MyCellarBottleContenance.getList()) {
				m_half.addItem(s);
			}
			m_half.setSelectedItem(MyCellarBottleContenance.getDefaultValue());
			panelVignobles.updateList();
			Debug("updateView Done");
		});
	}
	
	protected void setListeners() {
		m_lieu.addItemListener(this::lieu_itemStateChanged);
		m_num_lieu.addItemListener(this::num_lieu_itemStateChanged);
		m_line.addItemListener(this::line_itemStateChanged);
		m_column.addItemListener(this::column_itemStateChanged);
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
		for(int i = 1; i<= nbEmpl; i++) {
			m_num_lieu.addItem(Integer.toString(i));
		}
		for(int i = 1; i<= nbLine; i++) {
			m_line.addItem(Integer.toString(i));
		}
		for(int i = 1; i<= nbColumn; i++) {
			m_column.addItem(Integer.toString(i));
		}
		m_num_lieu.setSelectedIndex(place+1);
		m_line.setSelectedIndex(row+1);
		m_column.setSelectedIndex(column+1);
		m_labelLine.setVisible(true);
		m_labelColumn.setVisible(true);
		m_line.setVisible(true);
		m_column.setVisible(true);
		setListenersEnabled(true);
	}
	
	protected void selectPlace(Bouteille bottle) {
		Debug("selectPlaceWithBottle...");
		setListenersEnabled(false);
		Rangement rangement = bottle.getRangement();
		for(int i=0; i<m_lieu.getItemCount(); i++) {
			if(rangement.getNom().equals(m_lieu.getItemAt(i))){
				m_lieu.setSelectedIndex(i);
				break;
			}
		}
		m_num_lieu.removeAllItems();
		m_column.removeAllItems();
		m_line.removeAllItems();
		m_num_lieu.addItem("");
		m_line.addItem("");
		m_column.addItem("");


		int nbEmpl = rangement.getNbEmplacements();
		boolean isCaisse = rangement.isCaisse();
		if(!isCaisse) {
			for(int i = 1; i<= nbEmpl; i++) {
				m_num_lieu.addItem(Integer.toString(i));
			}
			int nbLine = rangement.getNbLignes(bottle.getNumLieu()-1);
			int nbColumn = rangement.getNbColonnes(bottle.getNumLieu()-1, bottle.getLigne()-1);
			for(int i = 1; i<= nbLine; i++) {
				m_line.addItem(Integer.toString(i));
			}
			for(int i = 1; i<= nbColumn; i++) {
				m_column.addItem(Integer.toString(i));
			}
			m_line.setEnabled(true);
			m_column.setEnabled(true);
			m_num_lieu.setSelectedIndex(bottle.getNumLieu());
			m_line.setSelectedIndex(bottle.getLigne());
			m_column.setSelectedIndex(bottle.getColonne());
		}
		else {
			int start = rangement.getStartCaisse();
			for(int i = start; i< nbEmpl+start; i++) {
				m_num_lieu.addItem(Integer.toString(i));
			}
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
	
	protected boolean isListenersDisabled() {
		return !listenersEnabled;
	}

	private void setListenersEnabled(boolean listenersEnabled) {
		this.listenersEnabled = listenersEnabled;
	}

	protected static void Debug(String s) {}

	protected abstract void lieu_itemStateChanged(ItemEvent e);

	protected abstract void line_itemStateChanged(ItemEvent e);

	public class PanelAttribute extends JPanel{
		private static final long serialVersionUID = 183053076444982489L;

		public PanelAttribute(){
			setLayout(new MigLayout("","[]30px[]30px[]",""));
			add(m_labelMaturity,"");
			add(m_labelParker,"");
			add(m_labelColor,"wrap");
			add(m_maturity,"width min(200,40%)");
			add(m_parker,"width min(150,30%)");
			add(m_colorList,"wrap, width min(150,30%)");
			add(m_labelPrice,"wrap");
			add(m_price,"width min(100,45%), split 2");
			add(m_devise,"gapleft 5px");
			add(m_labelNbBottle,"split, span 2");
			add(m_nb_bottle,"width min(50,10%)");
			add(m_labelStillToAdd,"");
		}
	}
	
	public class PanelName extends JPanel{
		private static final long serialVersionUID = 8617685535706381964L;

		public PanelName(){
			setLayout(new MigLayout("","[grow]30px[]10px[]10px[]30px[]10px[]",""));
			add(m_labelName,"grow");
			add(m_labelYear);
			add(m_annee_auto);
			add(m_contenance,"wrap");
			add(name,"grow");
			add(m_year,"width min(100,10%)");
			add(m_noYear,"");
			add(m_half,"push");
			add(m_manageContenance,"");
		}
	}
}
