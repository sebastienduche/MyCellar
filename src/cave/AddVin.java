package Cave;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.LinkedList;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.border.EtchedBorder;

import net.miginfocom.swing.MigLayout;
import Cave.Bouteille.BouteilleBuilder;
import Cave.actions.ChooseCellAction;
import Cave.core.IAddVin;
import Cave.core.IGPItem;
import Cave.core.MyCellarButton;
import Cave.core.MyCellarLabel;
import Cave.core.MyCellarManageBottles;
import Cave.core.PanelVignobles;
import Cave.countries.Country;
import Cave.vignobles.Appelation;
import Cave.vignobles.CountryVignoble;


/**
 * <p>Titre : Cave à vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2005</p>
 * <p>Société : Seb Informatique</p>
 * @author Sébastien Duché
 * @version 20.9
 * @since 01/05/17
 */
public class AddVin extends MyCellarManageBottles implements Runnable, ITabListener, IAddVin {

	private static final long serialVersionUID = -8925831759212999905L;
	private boolean m_bmodify = false; // Pour la Modification
	private boolean m_bIsPlaceModify = true; // Pour la Modification
	private final MyCellarLabel m_avant1 = new MyCellarLabel(); // Pour la Modification 
	private final MyCellarLabel m_avant2 = new MyCellarLabel(); // Pour la Modification 
	private final MyCellarLabel m_avant3 = new MyCellarLabel(); // Pour la Modification 
	private final MyCellarLabel m_avant4 = new MyCellarLabel(); // Pour la Modification 
	private final MyCellarLabel m_avant5 = new MyCellarLabel(); // Pour la Modification
	private String m_sb_empl; //Pour la Modification
	private int m_nb_num, m_nb_lig, m_nb_col; //Pour la Modification
	private boolean m_bbottle_add = false;
	private final String m_slabel_add = Program.getLabel("Infos071");
	private ListVin m_lv;
	private LinkedList<Bouteille> listBottleInModification; //Pour enlever dans ListVin
	private int m_nnb_bottle_add_only_one_place = 0;
	private final AddVin instance;

	/**
	 * AddVin: Constructeur pour l'ajout de vins
	 *
	 */
	public AddVin() {
		super();
		instance = this;
		Debug("Constructor");
		m_add = new MyCellarButton(new AddAction());
		m_cancel = new MyCellarButton(new CancelAction());
		
		m_lieu.setModifyActive(false);
		m_num_lieu.setModifyActive(false);
		m_line.setModifyActive(false);
		m_column.setModifyActive(false);
		m_year.setModifyActive(false);
		m_half.setModifyActive(false);
		m_price.setModifyActive(false);
		m_maturity.setModifyActive(false);
		m_parker.setModifyActive(false);
		m_colorList.setModifyActive(false);
		m_comment.setModifyActive(false);
		m_chooseCell = new MyCellarButton(new ChooseCellAction(instance));
		m_add.setMnemonic(AJOUTER);
		try {		
			LinkedList<String> list = new LinkedList<String>();
			list.add("");
			list.addAll(Program.getStorage().getBottleNames());
			name = new JCompletionComboBox(list.toArray());
			name.setCaseSensitive(false);
			name.setEditable(true);

			m_half.removeAllItems();
			try {
				m_half.addItem("");
				for(String s:Program.half) {
					if(!s.isEmpty())
						m_half.addItem(s);
				}
				if (Program.half.contains(Program.defaut_half)) {
					m_half.setSelectedItem(Program.defaut_half);
				}
			}
			catch (Exception e) {}

			// Init à vide des valeurs spécifiques modification
			m_nb_num = m_nb_lig = m_nb_col = -1;

			m_contenance.setText(Program.getLabel("Infos134")); //"Demie bouteille");
			m_annee_auto.setText(Program.getLabel("Infos117") + " " + ( (SIECLE + 1) * 100)); //"Année 00 -> 2000");
			try {
				m_annee_auto.setSelected(Program.getCaveConfigInt("ANNEE_AUTO", 0) == 0);
			}
			catch (NullPointerException npe) {
				m_annee_auto.setSelected(true);
				Program.putCaveConfigInt("ANNEE_AUTO", 0);
			}
			m_noYear.setText(Program.getLabel("Infos399"));

			m_nb_bottle.setToolTipText(Program.getLabel("Infos263"));
			m_nb_bottle.setValue(new Integer(1));
			m_nb_bottle.addChangeListener(new javax.swing.event.ChangeListener() {
				public void stateChanged(javax.swing.event.ChangeEvent e) {
					m_labelStillToAdd.setText("");
					if (Integer.parseInt(m_nb_bottle.getValue().toString()) <= 0) {
						m_nb_bottle.setValue(new Integer(1));
					}
				}
			});
			
			m_price.addKeyListener(new KeyAdapter() {
		        @Override
		        public void keyTyped(KeyEvent e) {
		        	if(e.getKeyChar() == ',' || e.getKeyChar() == '.') {
		        		e.consume();
    		        	DecimalFormat format = (DecimalFormat) DecimalFormat.getInstance();
    		        	DecimalFormatSymbols symbols = format.getDecimalFormatSymbols();
    		        	char sep = symbols.getDecimalSeparator();
    		        	String text = m_price.getText();
    		        	m_price.setText(text+sep);
		        	}
		        };
		    });

			m_add.setText(m_slabel_add);

			couper.setEnabled(false);
			copier.setEnabled(false);
			popup.add(couper);
			popup.add(copier);
			popup.add(coller);
			MouseListener popup_l = new PopupListener();
			name.addMouseListener(popup_l);
			m_year.addMouseListener(popup_l);
			m_price.addMouseListener(popup_l);
			m_comment.addMouseListener(popup_l);
			m_maturity.addMouseListener(popup_l);
			m_parker.addMouseListener(popup_l);
			cut.setEnabled(false);
			copy.setEnabled(false);

			cut.setAccelerator(KeyStroke.getKeyStroke('X', ActionEvent.CTRL_MASK));
			copy.setAccelerator(KeyStroke.getKeyStroke('C', ActionEvent.CTRL_MASK));
			paste.setAccelerator(KeyStroke.getKeyStroke('V', ActionEvent.CTRL_MASK));

			m_labelStillToAdd.setForeground(Color.red);
			m_end.setForeground(Color.red);
			m_end.setHorizontalAlignment(0);
			setLayout(new BorderLayout());
			add(new PanelMain(), BorderLayout.CENTER);

			m_manageContenance.addActionListener((e) -> manageContenance_actionPerformed(e));
			//Add name of place
			m_annee_auto.addActionListener((e) -> annee_auto_actionPerformed(e));

			m_noYear.addActionListener((e) -> {
				if(m_noYear.isSelected()) {
					m_year.setText(Bouteille.NON_VINTAGE);
					m_year.setEditable(false);
				}
				else {
					m_year.setText("");
					m_year.setEditable(true);
				}
			});


			m_lieu.addItem("");
			boolean complex = false;
			for (Rangement rangement : Program.getCave()) {
				m_lieu.addItem(rangement.getNom());
				if(!rangement.isCaisse())
					complex = true;
			}
			m_chooseCell.setEnabled(complex);

			// Listener sur les combobox
			// _________________________

			setListeners();

			managePlaceCombos();

			this.setVisible(true);
			Debug("Constructor End");
		}
		catch (Exception e) {
			Program.showException(e);
		}
	}

	/**
	 * Remise à zéro des champs saisissable
	 */
	private void resetValues() {
		Debug("Reset Values...");
		name.removeAllItems();
		name.addItem("");
		for(String s:Program.getStorage().getBottleNames())
			name.addItem(s);

		name.setEnabled(true);
		if(m_noYear.isSelected())
			m_year.setText(Bouteille.NON_VINTAGE);
		else
			m_year.setText("");
		m_price.setText("");
		m_comment.setText("");
		m_maturity.setText("");
		m_parker.setText("");
		m_colorList.setSelectedItem(BottleColor.NONE);
		m_nb_bottle.setValue(new Integer(1));
		m_labelStillToAdd.setText("");
		if(!m_bmodify) {
			if(m_lieu.getItemCount() > 0)
				m_lieu.setSelectedIndex(0);
			managePlaceCombos();
			/*if(m_lieu.getItemCount() > 0) {
				m_lieu.setSelectedIndex(0);
				m_lieu.setEnabled(false);
			}
			if(m_lieu.getItemCount() == 2) {
				m_lieu.setSelectedIndex(1);
				if(m_num_lieu.getItemCount() == 2) {
					m_num_lieu.setSelectedIndex(1);
					m_num_lieu.setEnabled(false);
				}
			}*/
		}
		else
			m_lieu.setSelectedIndex(0);
		m_labelExist.setText("");
		Search.updateTable();
		comboCountry.setSelectedIndex(0);
		if(comboVignoble.getItemCount() > 0)
			comboVignoble.setSelectedIndex(0);
		Debug("Reset Values Done");
	}

	/**
	 * lieu_itemStateChanged: Fonction pour la liste des lieux.
	 *
	 * @param e ItemEvent
	 */
	protected void lieu_itemStateChanged(ItemEvent e) {
		SwingUtilities.invokeLater(() -> {

			Debug("Lieu_itemStateChanging...");
			int nb_emplacement = 0;
			int lieu_select = m_lieu.getSelectedIndex();
			int start_caisse = 0;
			boolean bIsCaisse = false;

			m_labelExist.setText("");

			m_preview.setEnabled(lieu_select > 0);

			if (lieu_select == 0 && m_bmodify) {
				m_bIsPlaceModify = false;
			}
			if (lieu_select > 0) {
				nb_emplacement = Program.getCave(lieu_select - 1).getNbEmplacements();
				bIsCaisse = Program.getCave(lieu_select - 1).isCaisse();
				start_caisse = Program.getCave(lieu_select - 1).getStartCaisse();
			}
			if (bIsCaisse) { //Type caisse
				m_preview.setEnabled(false);
				m_num_lieu.removeAllItems();
				m_num_lieu.addItem("");
				for (int i = 0; i < nb_emplacement; i++) {
					m_num_lieu.addItem(Integer.toString(i + start_caisse));
				}
				if (nb_emplacement == 1)
					m_num_lieu.setSelectedIndex(1);
			}
			else {
				if (m_bmodify && lieu_select > 0) {
					m_bIsPlaceModify = true;
				}
				m_num_lieu.removeAllItems();
				m_line.removeAllItems();
				m_column.removeAllItems();
				m_num_lieu.addItem("");
				for (int i = 1; i <= nb_emplacement; i++) {
					m_num_lieu.addItem(Integer.toString(i));
				}
			}
			managePlaceCombos();
			Debug("Lieu_itemStateChanging...End");
		});
	}

	/**
	 * line_itemStateChanged: Fonction pour la liste des lignes.
	 *
	 * @param e ItemEvent
	 */
	protected void line_itemStateChanged(ItemEvent e) {
		SwingUtilities.invokeLater(() -> {
			Debug("Line_itemStateChanging...");
			int nb_col = 0;
			int num_select = m_line.getSelectedIndex();
			int emplacement = m_num_lieu.getSelectedIndex();
			int lieu_select = m_lieu.getSelectedIndex();

			//m_end.setText("");
			m_labelExist.setText("");

			m_column.setEnabled(num_select != 0);
			
			if (num_select > 0) { //!=0
				nb_col = Program.getCave(lieu_select - 1).getNbColonnes(emplacement - 1, num_select - 1);
			}
			m_column.removeAllItems();
			m_column.addItem("");
			for (int i = 1; i <= nb_col; i++) {
				m_column.addItem(Integer.toString(i));
			}
			Debug("Line_itemStateChanging...End");
		});
	}

	/**
	 * setBottles: Fonction de chargement de plusieurs vins dans la fenêtre pour la classe ListVin
	 *
	 * @param bottles LinkedList<Bouteille>
	 */
	public void setBottles(LinkedList<Bouteille> bottles) {
		Debug("Set Bottles...");
		if(bottles.size() > 1)
		{
			if(m_lv == null) {
    			m_lv = new ListVin(bottles);
    			add(m_lv, BorderLayout.WEST);
    			m_lv.setAddVin(this);
			}
			else
				m_lv.setBottles(bottles);
		}
		else
		{
			if(m_lv != null)
				remove(m_lv);
			setListVin(null);
		}
		setBottle(bottles.getFirst());
	}

	/**
	 * setBottle: Fonction de chargement d'un vin dans la fenêtre pour la classe ListVin
	 *
	 * @param bottle Bouteille
	 * @param num int
	 */
	public void setBottle(Bouteille bottle) {
		Debug("Set Bottle ...");
		try {
			listBottleInModification = new LinkedList<Bouteille>();
			listBottleInModification.add(bottle);
			m_bmodify = true;
			enableAll(true);
			m_nb_bottle.setValue(new Integer(1));
			m_nb_bottle.setEnabled(false);
			m_laBouteille = bottle;
			name.setSelectedItem(bottle.getNom());
			m_year.setText(bottle.getAnnee());
			m_noYear.setSelected(bottle.isNonVintage());
			if(bottle.isNonVintage())
				m_year.setEditable(false);
			m_half.removeAllItems();
			m_half.addItem("");
			for(String s: Program.half) {
				if(null != s && !s.isEmpty())
					m_half.addItem(s);
			}
			if (Program.half.contains(Program.defaut_half)) {
				m_half.setSelectedItem(Program.defaut_half);
			}
			m_half.setSelectedItem(bottle.getType());
			String half_tmp = null;
			try {
				half_tmp = m_half.getSelectedItem().toString();
			}
			catch (NullPointerException npe) {
				half_tmp = "";
			}
			String auto = Program.getCaveConfigString("TYPE_AUTO", "OFF");

			if (half_tmp.compareTo(bottle.getType()) != 0 && auto.equals("ON")) {
				if (!bottle.getType().isEmpty()) {
					Program.half.add(bottle.getType());
					m_half.addItem(bottle.getType());
					m_half.setSelectedItem(bottle.getType());
				}
			}

			m_price.setText(Program.convertStringFromHTMLString(bottle.getPrix()));
			m_comment.setText(bottle.getComment());
			m_maturity.setText(bottle.getMaturity());
			m_parker.setText(bottle.getParker());
			m_colorList.setSelectedItem(BottleColor.getColor(bottle.getColor()));
			initializeVignobles(bottle);

			m_avant1.setText(Program.getLabel("Infos091")); //"Avant");
			m_avant2.setText(bottle.getEmplacement());
			m_avant3.setText(Integer.toString(bottle.getNumLieu()));
			m_avant4.setText(Integer.toString(bottle.getLigne()));
			m_avant5.setText(Integer.toString(bottle.getColonne()));
			m_avant1.setVisible(true);
			m_avant2.setVisible(true);
			m_avant3.setVisible(true);
			m_avant4.setVisible(true);
			m_avant5.setVisible(true);
			m_add.setText(Program.getLabel("Infos079"));
			m_sb_empl = bottle.getEmplacement();
			m_nb_num = bottle.getNumLieu();
			m_nb_lig = bottle.getLigne();
			m_nb_col = bottle.getColonne();
			
			/*if(!m_bmodify){
				if(m_lieu.getItemCount() > 0)
					m_lieu.setSelectedIndex(0);
				if(m_lieu.getItemCount() == 2) {
					m_lieu.setSelectedIndex(1);
					if(m_num_lieu.getItemCount() == 2)
						m_num_lieu.setSelectedIndex(1);
				}
			}*/

			if (/*m_bmodify && */m_line.isVisible()) {
				m_line.setEnabled(false);
				m_num_lieu.setEnabled(false);
				m_lieu.setEnabled(true);
			}

			int num_rangement = Rangement.convertNom_Int(m_sb_empl);
			if (num_rangement >= 0) {
				if (Program.getCave(num_rangement).isCaisse()) {
					m_line.setVisible(false);
					m_column.setVisible(false);
					m_avant4.setVisible(false);
					m_avant5.setVisible(false);
					m_labelLine.setVisible(false);
					m_labelColumn.setVisible(false);
				}
				else {
					m_line.setVisible(true);
					m_column.setVisible(true);
					m_avant4.setVisible(true);
					m_avant5.setVisible(true);
					m_labelLine.setVisible(true);
					m_labelColumn.setVisible(true);
				}
			}
			m_end.setText(Program.getLabel("Infos092")); //"Saisir les modifications");
		}
		catch (Exception e) {
			Program.showException(e);
		}
		Debug("Set Bottle ...End");
	}

	/**
	 * setBottlesInModification: Fonction pour le chargement de vins dans la fenêtre pour
	 * la classe ListVin.
	 *
	 * @param bottle1 LinkedList<Bouteille>: Liste des bouteilles
	 */
	public void setBottlesInModification(LinkedList<Bouteille> bottle1) {
		Debug("setBottlesInModification...");
		try {
			m_bmulti = bottle1.size() > 1;
			listBottleInModification = bottle1;

			resetValues();
			if(m_bmulti)
			{
				name.setSelectedItem(listBottleInModification.size() + " " + Program.getLabel("Infos140")); //" bouteilles sélectionn�es");
				name.setEnabled(false);
				m_annee_auto.setEnabled(false);
				m_noYear.setEnabled(false);
				m_nb_bottle.setEnabled(false);
				m_year.setEditable(false);
				m_half.setEnabled(false);
				try {
					m_half.setSelectedIndex(0);
				}
				catch (Exception ex) {}
				m_avant1.setText("");
				m_avant2.setText("");
				m_avant3.setText("");
				m_avant4.setText("");
				m_avant5.setText("");
				m_add.setEnabled(true);
				m_lieu.setEnabled(true);
				
				if(!m_bmodify){
					if(m_lieu.getItemCount() > 0)
						m_lieu.setSelectedIndex(0);
					if(m_lieu.getItemCount() == 2) {
						m_lieu.setSelectedIndex(1);
						if(m_num_lieu.getItemCount() == 2)
							m_num_lieu.setSelectedIndex(1);
					}
				}

				if (m_bmodify && m_line.isVisible()) {
					m_line.setEnabled(false);
					m_num_lieu.setEnabled(false);
					m_lieu.setEnabled(true);
				}
				m_end.setText(Program.getLabel("Infos141")); //"Vous ne pouvez d�placer plusieurs bouteilles que dans une caisse");
			}
			else
				setBottle(listBottleInModification.getFirst());
		}
		catch (Exception e) {
			Program.showException(e);
		}
		Debug("setBottlesInModification...End");
	}

	/**
	 * setListVin: Mise à jour de la variable ListVin.
	 *
	 * @param lv1 ListVin
	 */
	public void setListVin(ListVin lv1) {
		if (lv1 != null) Debug("SetListVin...");
		m_lv = lv1;
	}


	/**
	 * keylistener_actionPerformed: Fonction d'écoute du clavier.
	 *
	 * @param e KeyEvent
	 */
	protected void keylistener_actionPerformed(KeyEvent e) {
		if ( (e.getKeyCode() == AJOUTER && e.isControlDown()) || e.getKeyCode() == KeyEvent.VK_ENTER) {
			new AddAction().actionPerformed(null);
		}
		if (e.getKeyCode() == PREVIEW && e.isControlDown() && m_preview.isEnabled()) {
			preview_actionPerformed(null);
		}
		if (e.getKeyCode() == KeyEvent.VK_C) {
			copier_actionPerformed(null);
		}
		if (e.getKeyCode() == KeyEvent.VK_X) {
			couper_actionPerformed(null);
		}
		if (e.getKeyCode() == KeyEvent.VK_V) {
			coller_actionPerformed(null);
		}
	}
	
	private boolean controlBottle() {
		Debug("Control Bottle...");
		boolean resul = true;
		
		String nom = name.getEditor().getItem().toString();
		//Vérification du nom ...
		if (nom.isEmpty()) {
			Debug("ERROR: Wrong Name");
			resul = false;
			new Erreur(Program.getError("Error054")); //"Veuillez saisir le nom du vin!"
		}
		
		String annee = "";
		// Controle de la date
		if (resul && !m_bmulti && (m_year.isEditable() || m_noYear.isSelected())) {
			annee = m_year.getText().trim();

			// Erreur sur la date
			if (!Bouteille.isValidYear(annee)) {
				Debug("ERROR: Wrong date");
				new Erreur(Program.getError("Error053")); //"Veuillez saisir une année valide!"
				resul = false;
				m_year.setEditable(true);
			}
			else {
				annee = getYear();
				m_year.setText(annee);
			}
		}
		
		int lieu_selected = m_lieu.getSelectedIndex();
		int lieu_num_selected = m_num_lieu.getSelectedIndex();
		if (lieu_selected == 0 && !m_bmodify && resul) {
			Debug("ERROR: Wrong Place");
			resul = false;
			new Erreur(Program.getError("Error055")); //"Veuillez sélectionner un emplacement!"
		}
		
		if (resul && ((!m_bmodify && lieu_num_selected == 0) || (m_bmodify && lieu_num_selected == 0 && lieu_selected != 0))) { //Nécessite la sélection du numéro de lieu
			Debug("ERROR: Wrong Num Place");
			if (m_line.isVisible()) {
				new Erreur(Program.getError("Error056"));
			}
			else {
				new Erreur(Program.getError("Error174"));
			}
			m_num_lieu.setEnabled(true);
			//m_end.setText("");
			enableAll(true);
			resul = false;
		}
		
		Debug("Control Bottle... End");
		return resul;
	}

	/**
	 * run: Exécution des tâches.
	 */
	public void run() {
		Debug("Running...");

		try {
			if(!controlBottle()) {
				m_end.setText("");
				enableAll(true);
				return;
			}
			// Ajout ou modification
			Debug("Adding / Modifying...");
			boolean resul = true;
			boolean bIsCaisse = false;
			int nb_bottle_rest = Integer.parseInt(m_nb_bottle.getValue().toString()) - 1;
			int lieu_selected = m_lieu.getSelectedIndex();
			int lieu_num_selected = m_num_lieu.getSelectedIndex();
			String prix = m_price.getText();
			String comment1 = m_comment.getText();
			String dateOfC = m_maturity.getText();
			String parker = m_parker.getText();
			BottleColor bottleColor = (BottleColor) m_colorList.getSelectedItem();
			String color = bottleColor.name();
			Object o = comboCountry.getEditor().getItem();
			String country;
			if(o instanceof Country)
				country = ((Country) o).getId();
			else {
				country = o.toString();
			}
			o = comboVignoble.getEditor().getItem();
			String vignoble;
			if(o instanceof CountryVignoble)
				vignoble = ((CountryVignoble) o).getName();
			else {
				vignoble = o.toString();
			}
			o = comboAppelationAOC.getEditor().getItem();
			String aoc;
			if(o instanceof Appelation) {
				aoc = ((Appelation) o).getAOC();
			}
			else {
				aoc = o.toString();
			}
			o = comboAppelationIGP.getEditor().getItem();
			String igp;
			if(o instanceof IGPItem)
				igp = ((IGPItem) o).toString();
			else {
				igp = o.toString();
			}
			String annee = "";

			m_bbottle_add = false;
			String nom = name.getEditor().getItem().toString();

			// Controle de la date
			if (!m_bmulti && (m_year.isEditable() || m_noYear.isSelected())) {
				annee = getYear();
				m_year.setText(annee);
			}

			String demie = "";
			try {
				demie = m_half.getSelectedItem().toString();
			}
			catch (Exception ex) {
				demie = "";
			}
			
			if (m_bmodify) {
				//On grise les champs en cours de modif
				Debug("Modifying in Progress...");
				m_end.setText(Program.getLabel("Infos142")); //"Modification en cours..."
				enableAll(false);
			}
			
			if ( (!m_bmodify && lieu_num_selected == 0) || (m_bmodify && lieu_num_selected == 0 && lieu_selected != 0)) { //Nécessite la sélection du numéro de lieu
				Debug("ERROR: Wrong Num Place");
				if (m_line.isVisible()) {
					new Erreur(Program.getError("Error056"));
				}
				else {
					new Erreur(Program.getError("Error174"));
				}
				m_num_lieu.setEnabled(true);
				m_end.setText("");
				enableAll(true);
				return;
			}
			
			String sPlaceName = "";
			if (lieu_selected > 0) {
				sPlaceName = Program.getCave(lieu_selected - 1).getNom();
				bIsCaisse = Program.getCave(lieu_selected - 1).isCaisse();
			}
			else if (m_bmodify) { //Si aucun emplacement n'a été sélectionné (modif du nom)
				sPlaceName = m_sb_empl;
				lieu_num_selected = 0;
				lieu_selected = Rangement.convertNom_Int(sPlaceName) + 1;
				bIsCaisse = Program.getCave(lieu_selected - 1).isCaisse();
			}

			m_end.setText(Program.getLabel("Infos312"));
			if (bIsCaisse) {
				//Caisse
				Debug("Is a Caisse");
				Rangement caisse = Program.getCave(lieu_selected - 1);
				if(!caisse.hasFreeSpaceInCaisse(lieu_num_selected - 1)) {
					new Erreur(Program.getError("Error154"), Program.getError("Error153"));
					m_end.setText("");
					return;
				}
				
				if (!m_bmulti) {
					lieu_num_selected -= 1;
					Bouteille bouteille = new BouteilleBuilder(nom)
										.annee(annee)
										.type(demie)
										.place(sPlaceName)
										.numPlace(lieu_num_selected)
										.price(prix)
										.comment(comment1)
										.maturity(dateOfC)
										.parker(parker)
										.color(color)
										.vignoble(country, vignoble, aoc, igp, null).build();
					// Add multiple bottle with question
					if (nb_bottle_rest > 0) {
						if (m_lieu.isEnabled() || m_num_lieu.isEnabled()) {
							Debug("Adding multiple bottles in the same place?");
							String erreur_txt1 = new String(Program.getError("Error061") + " " + (nb_bottle_rest + 1) + " " + Program.getError("Error062") + " " + sPlaceName + "?"); //Voulez vous ajouter les xx bouteilles dans yy
							if( JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(this, erreur_txt1, Program.getLabel("Infos049"), JOptionPane.YES_NO_OPTION ))
							{
								//Add several bottles in Caisse
								Debug("Adding multiple bottles in the same place: YES");
								
								if ( (caisse.getNbCaseUseCaisse(lieu_num_selected) + nb_bottle_rest) >= caisse.getStockNbcol() && caisse.isLimited()) {
									new Erreur(Program.getError("Error154"), Program.getError("Error153"));
								}
								else {
									for (int j = 0; j <= nb_bottle_rest; j++) {
										Bouteille b = new Bouteille(bouteille);
										Program.getStorage().addHistory(History.ADD, b);
										caisse.addWine(b);
									}
									m_end.setText( new String( (nb_bottle_rest + 1) + " " + Program.getLabel("Infos076")));
									resetValues();
								}
							}
							else {
								Debug("Adding multiple bottles in the same place: NO");
								//Add a single bottle in Caisse
								Rangement rangement = Program.getCave(lieu_selected - 1);
								if (rangement.hasFreeSpaceInCaisse(lieu_num_selected)) {
									Program.getStorage().addHistory(History.ADD, bouteille);
									rangement.addWine(bouteille);
									m_end.setText( Program.getLabel("Infos075"));
									m_nb_bottle.setValue(new Integer(nb_bottle_rest));
									m_labelStillToAdd.setText(Program.getLabel("Infos067") + " " + nb_bottle_rest + " " + Program.getLabel("Infos072")); //Il reste n bouteille � ajouter
								}
								else {
									new Erreur(Program.getError("Error154"), Program.getError("Error153"));
								}
							}
						}
						else { //Un seul rangement simple
							if (Program.getCave(lieu_selected - 1).isLimited() && (Program.getCave(lieu_selected - 1).getNbCaseUseCaisse(lieu_num_selected) + nb_bottle_rest + 1) > Program.getCave(lieu_selected - 1).getStockNbcol()) {
								resul = false;
								Debug("ERROR: This caisse is full. Unable to add all bottles in the same place!");
								m_bbottle_add = false;
								new Erreur(Program.getError("Error154"), Program.getError("Error153"));
							}
							else {
								m_nnb_bottle_add_only_one_place = nb_bottle_rest + 1;
								for (int z = 0; z < nb_bottle_rest; z++) {
									Bouteille b = new Bouteille(bouteille);
									Program.getStorage().addHistory(History.ADD, b);
									Program.getCave(lieu_selected - 1).addWine(b);
								}
								nb_bottle_rest = 0;
							}
						}
					} // Fin de l'ajout de plusieurs bouteilles restantes
					if (nb_bottle_rest == 0 && resul) {
						if (lieu_num_selected == -1) {
							new Erreur(Program.getError("Error174"));
							resul = false;
						}
						if (resul) {
							int addReturn = -1;
							if(m_bmodify)
							{
								//Suppression de la bouteille lors de la modification
								Debug("Updating bottle when modifying");
								m_laBouteille.update(bouteille);
								addReturn = 0;
								Program.getStorage().addHistory( History.MODIFY, m_laBouteille);

								int num_l = Rangement.convertNom_Int(m_sb_empl);
								if (num_l != -1) {
									Bouteille tmp = new Bouteille();
									tmp.setNumLieu(m_nb_num);
									tmp.setLigne(m_nb_lig);
									tmp.setColonne(m_nb_col);
									Program.getCave(num_l).clearStock(tmp);
								}
							}
							else {
								//Ajout de la bouteille
								Debug("Adding bottle...");
								Program.getStorage().addHistory( History.ADD, bouteille);
								addReturn = Program.getCave(lieu_selected - 1).addWine(bouteille);
							}

							//Ajout dans ALL
							if (addReturn != -1) {
								m_bbottle_add = true;
								resetValues();

								if (m_bmodify && m_line.isVisible()) {
									m_line.setEnabled(false);
									m_num_lieu.setEnabled(false);
									m_lieu.setEnabled(true);
								}
								m_price.setText("");
								m_maturity.setText("");
								m_parker.setText("");
								m_colorList.setSelectedItem(BottleColor.NONE);
							}
							else {
								Debug("ERROR: Adding bottle");
								m_bbottle_add = false;
								new Erreur(Program.getError("Error151") + " " + Program.getCave(lieu_selected - 1).getNom() + " " + Program.getError("Error152"),
										Program.getError("Error153"));
								resul = false;
							}
						}
					}
				}
				else { //if(! m_bmulti) Multi == true => Modification de plusieurs vins vers une caisse
					//Récupération des diffrentes bouteilles
					Debug("Modifying multiple bottle to a Caisse");
					int nbbottle = listBottleInModification.size();
					resul = true;
					if(lieu_num_selected == 0)
					{
						Debug("Modifying without changing place");
						boolean bOneBottle = listBottleInModification.size() == 1;
						// Modification sans changement de lieu 10/05/08
						for (Bouteille tmp: listBottleInModification) {
							if(bOneBottle || !comment1.isEmpty())
								tmp.setComment(comment1);
							if(bOneBottle || !dateOfC.isEmpty())
								tmp.setMaturity(dateOfC);
							if(bOneBottle || !parker.isEmpty())
								tmp.setParker(parker);
							if(bOneBottle || !color.isEmpty())
								tmp.setColor(color);
							if(bOneBottle || !country.isEmpty() || !vignoble.isEmpty() || !aoc.isEmpty() || !igp.isEmpty())
								tmp.setVignoble(new Vignoble(country, vignoble, aoc, igp));
							// Add multiple bottle with question
							int num_l = Rangement.convertNom_Int(tmp.getEmplacement());
							Debug("Adding multiple bottles...");
							if (m_bmodify) {
								//Delete Bouteilles
								Debug("Deleting bottles when modifying");
								Program.getStorage().deleteWine(tmp);
								if (!Program.getCave(num_l).isCaisse()) { //Si ce n'est pas une caisse on supprime de stockage
									Debug( Program.getCave(num_l).getNom() + " is Not a Caisse. Delete from stock");
									Program.getCave(num_l).clearStock(tmp);
								}
							}
							//Ajout des bouteilles dans la caisse
							Debug("Adding bottle...");
							if (m_bmodify)
								Program.getStorage().addHistory(History.MODIFY, tmp);
							else
								Program.getStorage().addHistory(History.ADD, tmp);
							//Ajout des bouteilles dans ALL
							if (Program.getCave(num_l).addWine(tmp) != -1) {
								m_bbottle_add = true;
								resetValues();
								try {
									m_half.setSelectedIndex(0);
								}
								catch (Exception ex) {}
								m_labelStillToAdd.setText("");
							}
						}
					}
					else
					{
						Debug("Modifying with changing place");
						int nLieuNum = -1;
						try {
							nLieuNum = Integer.parseInt(m_num_lieu.getItemAt(lieu_num_selected).toString());
						}
						catch (NumberFormatException nfe) {
							Debug("ERROR: Wrong place number");
							new Erreur(Program.getError("Error056"));
							m_num_lieu.setEnabled(true);
							resul = false;
							m_lieu.setEnabled(true);
							m_end.setText("");
							m_add.setEnabled(true);
						}
						if (resul) {
							if ( Program.getCave(lieu_selected - 1).isLimited() && (Program.getCave(lieu_selected - 1).getNbCaseUseCaisse(nLieuNum) + nbbottle) > Program.getCave(lieu_selected - 1).getStockNbcol() ) {
								Debug("ERROR: Not enough place!");
								new Erreur(Program.getError("Error154"), Program.getError("Error153"));
								m_lieu.setEnabled(true);
								m_end.setText("");
								m_add.setEnabled(true);
							}
							else {
								boolean bOneBottle = listBottleInModification.size() == 1;
								for (Bouteille tmp:listBottleInModification) {
									if(bOneBottle || !comment1.isEmpty())
										tmp.setComment(comment1);
									if(bOneBottle || !dateOfC.isEmpty())
										tmp.setMaturity(dateOfC);
									if(bOneBottle || !parker.isEmpty())
										tmp.setParker(parker);
									if(bOneBottle || !color.isEmpty())
										tmp.setColor(color);
									if(bOneBottle || !country.isEmpty() || !vignoble.isEmpty() || !aoc.isEmpty() || !igp.isEmpty())
										tmp.setVignoble(new Vignoble(country, vignoble, aoc, igp));
									// Add multiple bottle with question
									Debug("Adding multiple bottles...");
									if (m_bmodify) {
										//Delete Bouteilles
										Debug("Deleting bottles when modifying");
										int num_l = Rangement.convertNom_Int(tmp.getEmplacement());
										boolean aCaisse = false;
										Rangement r = Program.getCave(num_l);
										if(r != null)
											aCaisse = r.isCaisse();
										Program.getStorage().deleteWine(tmp);
										if (!aCaisse && r != null) { //Si ce n'est pas une caisse on supprime de stockage
											Debug("is Not a Caisse. Delete from stock");
											r.clearStock(tmp);
										}
									}
									//Ajout des bouteilles dans la caisse
									tmp.setEmplacement(sPlaceName);
									tmp.setNumLieu(nLieuNum);
									Debug("Adding bottle.");
									Program.getStorage().addHistory(m_bmodify ? History.MODIFY : History.ADD, tmp);
									int num_l = Rangement.convertNom_Int(tmp.getEmplacement());
									//Ajout des bouteilles dans ALL
									if (Program.getCave(num_l).addWine(tmp) != -1) {
										m_bbottle_add = true;
										resetValues();
										try {
											m_half.setSelectedIndex(0);
										}
										catch (Exception ex) {}
										m_labelStillToAdd.setText("");
									}

								}
								if(!m_bmodify){
									if(m_lieu.getItemCount() > 0)
										m_lieu.setSelectedIndex(0);
									if(m_lieu.getItemCount() == 2) {
										m_lieu.setSelectedIndex(1);
										if(m_num_lieu.getItemCount() == 2)
											m_num_lieu.setSelectedIndex(1);
									}
								}else
									m_lieu.setSelectedIndex(0);

								if (m_bmodify && m_line.isVisible()) {
									m_line.setEnabled(false);
									m_num_lieu.setEnabled(false);
									m_lieu.setEnabled(true);
								}
							}
						}
					}
				}
			}
			else if( m_lieu.getSelectedIndex() == 0) {
				if( !m_bmulti ) {
					// Modification d'une bouteille dans Armoire sans changement de lieu
					Debug("Modifying one bottle in Armoire without changing place");
					Bouteille tmp = new BouteilleBuilder(nom)
						.annee(annee)
						.type(demie)
						.place(m_laBouteille.getEmplacement())
						.numPlace(m_laBouteille.getNumLieu())
						.line(m_laBouteille.getLigne())
						.column(m_laBouteille.getColonne())
						.price(prix)
						.comment(comment1)
						.maturity(dateOfC)
						.parker(parker)
						.color(color)
						.vignoble(country, vignoble, aoc, igp, null).build();
					Debug("Replacing bottle...");
					if (m_bmodify) {
						// Remplacement de la bouteille
						Program.getStorage().addHistory(History.MODIFY, tmp);
						Program.getStorage().replaceWineAll(tmp, tmp.getNumLieu(), tmp.getLigne(), tmp.getColonne());
					}
					m_bbottle_add = true;
					resetValues();
					try {
						m_half.setSelectedIndex(0);
					}
					catch (Exception ex) {}
					m_labelStillToAdd.setText("");
				}
				else
				{
					// Modification de bouteilles dans Armoire sans changement de lieu
					Debug("Modifying multiple bottles in Armoire without changing place");
					// Modification sans changement de lieu 11/05/08
					for (Bouteille tmp:listBottleInModification) {
						int num_l = Rangement.convertNom_Int(tmp.getEmplacement());
						boolean aCaisse = Program.getCave(num_l).isCaisse();
						tmp.setPrix(prix);
						tmp.setComment(comment1);
						tmp.setMaturity(dateOfC);
						tmp.setParker(parker);
						tmp.setColor(color);
						if(!country.isEmpty() || !vignoble.isEmpty() || !aoc.isEmpty() || !igp.isEmpty())
							tmp.setVignoble(new Vignoble(country, vignoble, aoc, igp));
						// Add multiple bottles
						Debug("Adding multiple bottles...");
						if (m_bmodify) {
							//Delete Bouteilles
							Debug("Deleting bottles when modifying");
							Program.getStorage().deleteWine(tmp);
							if (!aCaisse) { //Si ce n'est pas une caisse on supprime de stockage
								Debug("is Not a Caisse. Delete from stock");
								Program.getCave(num_l).clearStock(tmp);
							}
						}
						//Ajout des bouteilles dans la caisse
						Debug("Adding bottle...");
						Program.getStorage().addHistory( m_bmodify? History.MODIFY : History.ADD, tmp);
						//Ajout des bouteilles dans ALL
						if (Program.getCave(num_l).addWine(tmp) != -1) {
							m_bbottle_add = true;
							resetValues();
							try {
								m_half.setSelectedIndex(0);
							}
							catch (Exception ex) {}
							m_labelStillToAdd.setText("");
						}
					}
				}
			}
			else {
				// Ajout dans une Armoire
				if (m_bmulti) { //On ne peut pas déplacer plusieurs bouteilles vers une armoire
					Debug("ERROR: Unable to move multiple bottles to an Armoire");
					m_end.setText("");
					new Erreur(Program.getError("Error104") + " " + m_lieu.getSelectedItem().toString().trim() + ".", Program.getError("Error105")); //"Veuillez s�lectionner un rangement de type caisse.");//Impossible de d�placer plusieurs bouteilles dans
					enableAll(true);
				}
				else {
					// Modification d'une bouteille dans l'armoire
					int ligne = m_line.getSelectedIndex();
					lieu_num_selected = m_num_lieu.getSelectedIndex();
					int colonne = m_column.getSelectedIndex();
					
					if (lieu_num_selected == 0 && m_bIsPlaceModify && resul) {
						Debug("ERROR: Wrong place number");
						resul = false;
						m_end.setText("");
						new Erreur(Program.getError("Error056")); //"Veuillez sélectionner un numéro d'emplacement!"
						enableAll(true);
					}
					if (ligne == 0 && m_bIsPlaceModify && resul) {
						Debug("ERROR: Wrong line number");
						resul = false;
						m_end.setText("");
						new Erreur(Program.getError("Error057")); //"Veuillez sélectionner un numéro de ligne!"
						enableAll(true);
					}
					if (colonne == 0 && m_bIsPlaceModify && resul) {
						Debug("ERROR: Wrong column number");
						resul = false;
						m_end.setText("");
						new Erreur(Program.getError("Error058")); //"Veuillez sélectionner un numéro de colonne!"
						enableAll(true);
					}
					int nb_free_space = 0;
					if (resul) {
						Bouteille b = null;
						if (m_bmodify && !m_bIsPlaceModify) { //Si aucune modification du Lieu
							sPlaceName = m_sb_empl;
							lieu_num_selected = m_nb_num;
							ligne = m_nb_lig;
							colonne = m_nb_col;
							lieu_selected = Rangement.convertNom_Int(sPlaceName) + 1;
						}
						else { //Si Ajout bouteille ou modification du lieu
							Debug("Adding bottle or modifying place");
							//Récupération de la bouteille présent à l'emplacement
							b = Program.getCave(lieu_selected - 1).getBouteille(lieu_num_selected-1, ligne-1, colonne-1);
							if (b == null) {
								nb_free_space = Program.getCave(lieu_selected-1).getNbCaseFreeCoteLigne(lieu_num_selected-1, ligne-1, colonne-1);
							}
						}
						//Création de la nouvelle bouteille
						Debug("Creating new bottle...");
						Bouteille tmp = new BouteilleBuilder(nom)
							.annee(annee)
							.type(demie)
							.place(sPlaceName)
							.numPlace(lieu_num_selected)
							.line(ligne)
							.column(colonne)
							.price(prix)
							.comment(comment1)
							.maturity(dateOfC)
							.parker(parker)
							.color(color)
							.vignoble(country, vignoble, aoc, igp, null).build();
						if (b == null) {
							//Case vide donc ajout
							if (m_bmodify)
							{
								Debug("Empty case: Modifying bottle");
								m_laBouteille.update(tmp);
								Program.getStorage().addHistory(History.MODIFY, m_laBouteille);
								Debug("Deleting bottle when modifying");
								int num_l = Rangement.convertNom_Int(m_sb_empl);
								if (!Program.getCave(num_l).isCaisse()) {
									Debug("Deleting from stock");
									Bouteille tmp1 = new Bouteille();
									tmp1.setNumLieu(m_nb_num);
									tmp1.setLigne(m_nb_lig);
									tmp1.setColonne(m_nb_col);
									Program.getCave(num_l).clearStock(tmp1);
								}
							}
							else
							{
								Debug("Empty case: Adding bottle");
								Program.getStorage().addHistory(History.ADD, tmp);
								Program.getCave(lieu_selected - 1).addWine(tmp);
								if (nb_bottle_rest > 0 && nb_free_space > 1) { //Ajout de bouteilles côte à côte
									if(nb_free_space > (nb_bottle_rest+1))
										nb_free_space = nb_bottle_rest + 1;
									if( JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(this, nb_free_space + " " + Program.getError("Error175") + " " + Program.getError("Error176"), Program.getLabel("Infos049"), JOptionPane.YES_NO_OPTION)) {
										Debug("Putting multiple bottle in close place");
										m_nnb_bottle_add_only_one_place = nb_free_space;
										nb_bottle_rest = nb_bottle_rest - nb_free_space + 1;
										for (int z = 1; z < nb_free_space; z++) {
											tmp = new BouteilleBuilder(nom)
												.annee(annee)
												.type(demie)
												.place(sPlaceName)
												.numPlace(lieu_num_selected)
												.line(ligne)
												.column(colonne + z)
												.price(prix)
												.comment(comment1)
												.maturity(dateOfC)
												.parker(parker)
												.color(color)
												.vignoble(country, vignoble, aoc, igp, null).build();
											Program.getStorage().addHistory(History.ADD, tmp);
											Program.getCave(lieu_selected - 1).addWine(tmp);
										}
									}
								}
							}

							if (nb_bottle_rest > 0) {
								m_nb_bottle.setValue(new Integer(nb_bottle_rest));
								m_labelStillToAdd.setText(Program.getLabel("Infos067") + " " + nb_bottle_rest + " " + Program.getLabel("Infos073")); //Il reste n bouteilles � ajouter
								m_lieu.setSelectedIndex(0);
							}
							else {
								resetValues();
								if (m_bmodify) {
									m_half.setEnabled(false);
									name.setEditable(false);
									m_year.setEditable(false);
									m_price.setEditable(false);
									m_maturity.setEditable(false);
									m_parker.setEditable(false);
									m_colorList.setEditable(false);
									m_comment.setEditable(false);
									m_add.setEnabled(false);
									m_lieu.setEnabled(false);
									m_num_lieu.setEnabled(false);
									m_line.setEnabled(false);
									m_column.setEnabled(false);
								}
								else {
									m_labelStillToAdd.setText("");
									try {
										m_half.setSelectedIndex(0);
									}
									catch (Exception ex) {}
									m_lieu.setSelectedIndex(0);
								}
							}
							m_lieu.setSelectedIndex(0);
							String key = Program.getCaveConfigString("JUST_ONE_PLACE", "0");
							if (key.equals("1")) {
								if (!m_bmodify) {
									m_lieu.setSelectedIndex(1);
								}
								m_lieu.setEnabled(false);
							}
							key = Program.getCaveConfigString("JUST_ONE_NUM_PLACE", "0");
							if (key.equals("1")) {
								m_num_lieu.setSelectedIndex(m_bmodify ? 0 : 1);
								m_num_lieu.setEnabled(false);
								m_line.setEnabled(true);
							}
							else {
								if (m_bmodify) {
									m_lieu.setEnabled(true);
								}
							}
							if (m_bmodify && m_line.isVisible()) {
								m_line.setEnabled(false);
								m_num_lieu.setEnabled(false);
								m_lieu.setEnabled(true);
							}
							m_bbottle_add = true;
						}
						else { // La case n'est pas vide
							Debug("ERROR: Not an empty place, Replace?");
							String erreur_txt1 = new String(b.getNom() + " " + b.getAnnee() + " " + Program.getError("Error059")); //" déjà présent à cette place!");
							String erreur_txt2 = Program.getError("Error060"); //"Voulez vous le remplacer?");
							if( JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(this, erreur_txt1 + "\n" + erreur_txt2, Program.getLabel("Infos049"), JOptionPane.YES_NO_OPTION))
							{
								replaceWine(tmp, m_bmodify, b);
								if(m_bmodify)
									m_laBouteille.update(tmp);
								m_end.setText(m_bmodify ? Program.getLabel("Infos075") : Program.getLabel("Infos141"));
								resetValues();
							}
							else
							{
								m_end.setText(Program.getLabel("Infos092"));
								enableAll(true);
								resul = false;
							}
						}
					}
				}
			}

			if (m_bbottle_add) {
				if (m_bmodify) {
					if (m_lv != null) {
						m_lv.updateList(listBottleInModification);
					}
					if(listBottleInModification.size() == 1)
						m_end.setText(Program.getLabel("Infos144")); //"1 bouteille modifiée");
					else {
						m_end.setText(listBottleInModification.size() + " " + Program.getLabel("Infos143")); //" bouteilles modifi�es");
					}
				}
				else {
					if (m_nnb_bottle_add_only_one_place == 0) {
						m_end.setText(Program.getLabel("Infos075")); //"1 bouteille ajoutée");
					}
					else {
						m_end.setText(m_nnb_bottle_add_only_one_place + " " + Program.getLabel("Infos076")); //"x bouteilles ajout�es");
						m_nnb_bottle_add_only_one_place = 0;
					}
					//Remise des valeurs par défaut
					m_half.setSelectedItem(Program.defaut_half);
				}
			}
			if(resul)
				doAfterRun();
		}
		catch (Exception e) {
			Program.showException(e);
		}
	}

	private void replaceWine(Bouteille bottle, boolean modify, Bouteille bToDelete) {
		Debug("replaceWine...");
		//Change wine in a place
		int ligne = bottle.getLigne();
		int lieu_num = bottle.getNumLieu();
		int colonne = bottle.getColonne();
		String place = bottle.getEmplacement();

		Program.getStorage().addHistory(modify ? History.MODIFY : History.ADD, bottle);
		if(bToDelete != null)
			Program.getStorage().deleteWine(bToDelete);
		else
			Program.getStorage().replaceWineAll(bottle, lieu_num, ligne, colonne);
		if(!modify) {
			Program.getStorage().addWine(bottle);
		}
		else {
			if(m_laBouteille != null) {
				int num_l = Rangement.convertNom_Int(m_laBouteille.getEmplacement());
				if(!Program.getCave(num_l).isCaisse())
					Program.getCave(num_l).clearStock(m_laBouteille);
			}

			if (m_lv != null) {
				m_lv.updateList(listBottleInModification);
			}
			Search.removeBottle(bToDelete);
			Search.updateTable();
		}
		int num_l = Rangement.convertNom_Int(place);
		if(!Program.getCave(num_l).isCaisse())
			Program.getCave(num_l).updateToStock(bottle);
		Debug("replaceWine...End");
	}

	private void doAfterRun() {
		Debug("Do After Run...");
		Program.updateManagePlacePanel();
		panelVignobles.updateList();
		new java.util.Timer().schedule( 
		        new java.util.TimerTask() {
		            @Override
		            public void run() {
		            	SwingUtilities.invokeLater(new Runnable() {
							@Override
							public void run() {
								Debug("Set Text ...");
								m_end.setText("");
								Debug("Set Text OK");
							}
		            	});
		            }
		        }, 
		        5000 
		);
		if(!m_bmodify) 
			return;

		if(m_lv == null) {
			enableAll(true);
			Program.tabbedPane.setTitleAt(Program.tabbedPane.getSelectedIndex(), Program.getLabel("Infos005"));
			m_bmodify = false;
			m_avant1.setVisible(false);
			m_avant2.setVisible(false);
			m_avant3.setVisible(false);
			m_avant4.setVisible(false);
			m_avant5.setVisible(false);
			m_add.setText(Program.getLabel("Infos071"));
		}
		else if(m_lv.getListSize() == 0)
			reInitAddVin();
		Debug("Do After Run Done");
	}

	/**
	 * 
	 */
	private void reInitAddVin() {
		m_bmulti = false;
		if(m_lv != null)
			remove(m_lv);
		m_lv = null;
		enableAll(true);
		int tabIndex = Program.findTab(MyCellarImage.WINE);
		if(tabIndex != -1)
			Program.tabbedPane.setTitleAt(tabIndex, Program.getLabel("Infos005"));
		m_bmodify = false;
		m_avant1.setVisible(false);
		m_avant2.setVisible(false);
		m_avant3.setVisible(false);
		m_avant4.setVisible(false);
		m_avant5.setVisible(false);
		//m_end.setText("");
		m_add.setText(Program.getLabel("Infos071"));
	}

	public boolean runExit() {
		Debug("runExit...");
		m_add.setEnabled(false);
		//Vérification qu'il n'y a pas de bouteilles en modif ou création
		if (name.getEditor().getItem().toString().trim().length() > 0) {
			String erreur_txt1 = "";
			if (!m_bmodify) {
				erreur_txt1 = Program.getError("Error144");
			}
			else {
				if (name.isEnabled()) {
					erreur_txt1 = Program.getError("Error148");
				}
				else {
					erreur_txt1 = Program.getError("Error149");
				}
			}
			Debug("Message: Confirm to Quit?");
			if( JOptionPane.NO_OPTION == JOptionPane.showConfirmDialog(this, erreur_txt1 + " " + Program.getError("Error145"), Program.getLabel("Infos049"), JOptionPane.YES_NO_OPTION)) {
				Debug("Don't Quit.");
				m_add.setEnabled(true);
				return false;
			}
		}

		//Quitter : Sauvegarde XML
		//Save XML file
		Debug("Quitting...");

		for (Rangement rangement : Program.getCave()) {
			rangement.putTabStock();
		}
		m_colorList.setSelectedItem(BottleColor.NONE);
		m_avant1.setVisible(false);
		m_avant2.setVisible(false);
		m_avant3.setVisible(false);
		m_avant4.setVisible(false);
		m_avant5.setVisible(false);
		name.setSelectedIndex(0);
		m_year.setText("");
		m_parker.setText("");
		m_price.setText("");
		m_maturity.setText("");
		m_lieu.setSelectedIndex(0);
		m_labelExist.setText("");
		m_nb_bottle.setValue(1);
		comboCountry.setSelectedIndex(0);
		reInitAddVin();
		Debug("runExit...End");
		
		return true;
	}

	/**
	 * <p>Titre : Cave � vin</p>
	 * <p>Description : Votre description</p>
	 * <p>Copyright : Copyright (c) 1998</p>
	 * <p>Soci�t� : Seb Informatique</p>
	 * @author S�bastien Duch�
	 * @version 0.1
	 * @since 17/04/05
	 */
	class PopupListener extends MouseAdapter {

		public void mousePressed(MouseEvent e) {
			maybeShowPopup(e);
		}

		public void mouseClicked(MouseEvent e) {
			maybeShowPopup(e);
		}

		public void mouseEntered(MouseEvent e) {
		}

		public void mouseExited(MouseEvent e) {
		}

		public void mouseReleased(MouseEvent e) {
		}

		private void maybeShowPopup(MouseEvent e) {
			JTextField jtf = null;
			try {
				jtf = (JTextField) e.getComponent();
				if (jtf.isEnabled() && jtf.isVisible()) {
					m_objet1 = e.getComponent();
				}
			}
			catch (Exception ee) {}
			try {
				jtf = (JTextField) m_objet1;
				if (e.getButton() == MouseEvent.BUTTON3) {
					if (jtf.isFocusable() && jtf.isEnabled()) {
						jtf.requestFocus();
						if (jtf.getSelectedText() == null) {
							couper.setEnabled(false);
							copier.setEnabled(false);
						}
						else {
							couper.setEnabled(true);
							copier.setEnabled(true);
						}
						if (jtf.isEnabled() && jtf.isVisible()) {
							popup.show(e.getComponent(), e.getX(), e.getY());
						}
					}
				}
				if (e.getButton() == MouseEvent.BUTTON1) {
					if (jtf.isFocusable() && jtf.isEnabled()) {
						jtf.requestFocus();
						if (jtf.getSelectedText() == null) {
							cut.setEnabled(false);
							copy.setEnabled(false);
						}
						else {
							cut.setEnabled(true);
							copy.setEnabled(true);
						}
					}
				}
			}
			catch (Exception ee) {}
			JTextArea jta = null;
			try {
				jta = (JTextArea) e.getComponent();
				if (jta.isEnabled() && jta.isVisible()) {
					m_objet1 = e.getComponent();
				}
			}
			catch (Exception e1) {}
			try {
				jta = (JTextArea) m_objet1;
				if (e.getButton() == MouseEvent.BUTTON3) {
					if (jta.isFocusable() && jta.isEnabled()) {
						jta.requestFocus();
						if (jta.getSelectedText() == null) {
							couper.setEnabled(false);
							copier.setEnabled(false);
						}
						else {
							couper.setEnabled(true);
							copier.setEnabled(true);
						}
						if (jta.isEnabled() && jta.isVisible()) {
							popup.show(e.getComponent(), e.getX(), e.getY());
						}
					}
				}
				if (e.getButton() == MouseEvent.BUTTON1) {
					if (jta.isFocusable() && jta.isEnabled()) {
						jta.requestFocus();
						if (jta.getSelectedText() == null) {
							cut.setEnabled(false);
							copy.setEnabled(false);
						}
						else {
							cut.setEnabled(true);
							copy.setEnabled(true);
						}
					}
				}
			}
			catch (Exception ee) {}
		}
	}

	/**
	 * Debug
	 *
	 * @param sText String
	 */
	public static void Debug(String sText) {
		Program.Debug("AddVin: " + sText );
	}

	class PanelName extends JPanel{
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

	class PanelAttribute extends JPanel{
		private static final long serialVersionUID = 183053076444982489L;

		public PanelAttribute(){
			setLayout(new MigLayout("","[]30px[]30px[]",""));
			add(m_labelMaturity,"");
			add(m_labelParker,"");
			add(m_labelColor,"wrap");
			add(m_maturity,"width min(200,45%)");
			add(m_parker,"width min(100,10%)");
			add(m_colorList,"wrap, width min(200,45%)");
			add(m_labelPrice,"wrap");
			add(m_price,"width min(100,45%), split 2");
			add(m_devise,"gapleft 5px");
			add(m_labelNbBottle,"split, span 2");
			add(m_nb_bottle,"width min(50,10%)");
			add(m_labelStillToAdd,"");

		}
	}

	class PanelPlace extends JPanel{
		private static final long serialVersionUID = -2601861017578176513L;

		public PanelPlace(){
			setLayout(new MigLayout("","[]30px[]30px[]30px[]30px[grow]30px[]",""));
			setBorder(BorderFactory.createTitledBorder(new EtchedBorder(EtchedBorder.LOWERED), Program.getLabel("Infos217")));
			this.add(m_labelPlace,"");
			this.add(m_labelNumPlace,"");
			this.add(m_labelLine,"");
			this.add(m_labelColumn,"wrap");
			this.add(m_lieu,"");
			this.add(m_num_lieu,"");
			this.add(m_line,"");
			this.add(m_column,"");
			this.add(m_labelExist,"hidemode 3");
			this.add(m_chooseCell,"alignx right");
			this.add(m_preview,"alignx right, wrap");
			this.add(m_avant1,"hidemode 3,split 2");
			this.add(m_avant2,"hidemode 3");
			this.add(m_avant3,"hidemode 3");
			this.add(m_avant4,"hidemode 3");
			this.add(m_avant5,"hidemode 3");
		}
	}

	class PanelMain extends JPanel{
		private static final long serialVersionUID = -4824541234206895953L;

		public PanelMain(){
			this.setLayout(new MigLayout("","grow","[][][]10px[][grow]10px[][]"));
			add(new PanelName(),"growx,wrap");
			add(new PanelPlace(),"growx,wrap");
			add(new PanelAttribute(),"growx,split 2");
			add(panelVignobles = new PanelVignobles(instance, false),"growx, wrap");
			add(m_labelComment,"growx, wrap");
			add(m_js_comment,"grow, wrap");
			add(m_end, "center, hidemode 3, wrap");
			add(m_add, "center, split 2");
			add(m_cancel);
		}
	}
	

	@Override
	public boolean tabWillClose(TabEvent event) {
		return Program.addWine.runExit();
	}

	

	@Override
	public void tabClosed() {
		Start.updateMainPanel();
	}
	
	private void managePlaceCombos() {
		m_lieu.setEnabled(true);
		if(m_lieu.getItemCount() == 2) {
			if(m_lieu.getSelectedIndex() == 0)
				m_lieu.setSelectedIndex(1);
			m_lieu.setEnabled(false);
			Rangement r = Program.getCave(0);
			if(m_num_lieu.getItemCount() == 2) {
				if(m_num_lieu.getSelectedIndex() == 0)
					m_num_lieu.setSelectedIndex(1);
				m_num_lieu.setEnabled(false);
			}
			boolean visible = !r.isCaisse();
			m_line.setVisible(visible);
			m_column.setVisible(visible);
			m_labelLine.setVisible(visible);
			m_labelColumn.setVisible(visible);
		}
		else {
			m_lieu.setEnabled(true);
			m_num_lieu.setEnabled(false);
			m_line.setVisible(false);
			m_column.setVisible(false);
			m_labelLine.setVisible(false);
			m_labelColumn.setVisible(false);
			if(m_lieu.getSelectedIndex() > 0) {
				m_num_lieu.setEnabled(true);
				Rangement r = Program.getCave(m_lieu.getSelectedIndex() - 1);
				if(m_num_lieu.getItemCount() == 2) {
					if(m_num_lieu.getSelectedIndex() == 0)
						m_num_lieu.setSelectedIndex(1);
					m_num_lieu.setEnabled(false);
				}
				boolean visible = !r.isCaisse();
				m_line.setVisible(visible);
				m_column.setVisible(visible);
				m_labelLine.setVisible(visible);
				m_labelColumn.setVisible(visible);
			}
		}
	}
	
	class AddAction extends AbstractAction {

		private static final long serialVersionUID = -2958181161054647775L;
		public AddAction() {
			super(Program.getLabel("Infos071"), MyCellarImage.ADD);
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				new Thread(instance).start();
			}
			catch (Exception a) {
				Program.showException(a);
			}
		}
	}
	
	class CancelAction extends AbstractAction {

		private static final long serialVersionUID = -8689301287853923641L;
		public CancelAction() {
			super(Program.getLabel("Infos055"), MyCellarImage.DELETE);
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			runExit();
			Start.removeCurrentTab();
		}
	}
}
