package mycellar;

import mycellar.actions.ChooseCellAction;
import mycellar.core.IAddVin;
import mycellar.core.MyCellarButton;
import mycellar.core.MyCellarManageBottles;
import mycellar.core.PanelVignobles;
import mycellar.core.PopupListener;
import mycellar.core.datas.MyCellarBottleContenance;
import mycellar.countries.Country;
import mycellar.vignobles.Appelation;
import mycellar.vignobles.CountryVignoble;
import mycellar.vignobles.CountryVignobles;
import net.miginfocom.swing.MigLayout;

import javax.swing.BorderFactory;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseListener;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.MessageFormat;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;


/**
 * <p>Titre : Cave à vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2005</p>
 * <p>Société : Seb Informatique</p>
 * @author Sébastien Duché
 * @version 4.6
 * @since 07/12/18
 */
public class ManageBottle extends MyCellarManageBottles implements Runnable, ITabListener, IAddVin {
	private static final long serialVersionUID = 5330256984954964913L;
	private final char QUITTER = Program.getLabel("QUITTER").charAt(0);
	private final ManageBottle instance;
	

	/**
	 * ManageBottle: Constructeur pour la modification de vins
	 *
	 * @param bottle
	 */
	public ManageBottle(Bouteille bottle) {
		super();
		instance = this;
		isEditionMode = true;
		m_add = new MyCellarButton(MyCellarImage.SAVE);
		
		m_chooseCell = new MyCellarButton(new ChooseCellAction(this));
		try {
			Debug("Constructor with Bottle");
			LinkedList<String> list = new LinkedList<>();
			list.add("");
			list.addAll(Program.getStorage().getBottleNames());
			name = new JCompletionComboBox(list.toArray()) {
				private static final long serialVersionUID = 8137073557763181546L;
				@Override
				protected void doAfterModify() {
					super.doAfterModify();
					Start.setPaneModified(true);
				}
			};
			name.setCaseSensitive(false);
			name.setEditable(true);

			m_half.removeAllItems();
			m_half.addItem("");
			for(String s: MyCellarBottleContenance.getList()) {
					m_half.addItem(s);
			}
			m_half.setSelectedItem(MyCellarBottleContenance.getDefaultValue());
			jbInit();
			setBottle(bottle);
		}
		catch (Exception e) {
			Program.showException(e);
		}
	}
	
	public Bouteille getBottle() {
		return m_laBouteille;
	}

	/**
	 * jbInit: Fonction de démarrage.
	 *
	 */
	private void jbInit() {

		Debug("Starting JbInit");
		m_contenance.setText(Program.getLabel("Infos134")); //"Demie bouteille");
		m_annee_auto.setText(MessageFormat.format(Program.getLabel("Infos117"), ( (SIECLE + 1) * 100))); //"Annee 00 -> 2000");
		m_annee_auto.setSelected(Program.getCaveConfigInt("ANNEE_AUTO", 0) == 0);

		m_price.addKeyListener(new KeyAdapter() {
	        @Override
	        public void keyTyped(KeyEvent e) {
			if(e.getKeyChar() == ',' || e.getKeyChar() == '.') {
				e.consume();
				char sep = Program.getDecimalSeparator();
				String text = m_price.getText();
				m_price.setText(text+sep);
			}
		}
});
		
		m_noYear.setText(Program.getLabel("Infos399"));

		m_nb_bottle.setToolTipText(Program.getLabel("Infos263"));
		m_nb_bottle.setValue(1);
		m_nb_bottle.addChangeListener((e) -> {
				m_labelStillToAdd.setText("");
				if (Integer.parseInt(m_nb_bottle.getValue().toString()) <= 0) {
					m_nb_bottle.setValue(1);
				}
		});
	
		m_add.setText(Program.getLabel("ManageBottle.SaveModifications"));
		m_add.setMnemonic(AJOUTER);
		m_manageContenance.setText(Program.getLabel("Infos400"));


		MouseListener popup_l = new PopupListener();
		name.addMouseListener(popup_l);
		m_year.addMouseListener(popup_l);
		m_price.addMouseListener(popup_l);
		m_comment.addMouseListener(popup_l);
		m_maturity.addMouseListener(popup_l);
		m_parker.addMouseListener(popup_l);

		m_labelStillToAdd.setForeground(Color.red);
		m_end.setForeground(Color.red);
		m_end.setHorizontalAlignment(SwingConstants.CENTER);
		setLayout(new BorderLayout());
		add(new PanelMain(), BorderLayout.CENTER);

		m_add.addActionListener((e) -> saving());
		m_manageContenance.addActionListener(this::manageContenance_actionPerformed);
		m_annee_auto.addActionListener(this::annee_auto_actionPerformed);

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
			if(!rangement.isCaisse()) {
				complex = true;
			}
		}
		m_chooseCell.setEnabled(complex);

		// Listener sur les combobox
		// _________________________

		setListeners();

		m_num_lieu.setEnabled(false);
		m_line.setEnabled(false);
		m_column.setEnabled(false);

		setVisible(true);
		Debug("JbInit Done");
	}

	/**
	 * quit_actionPerformed: Fonction pour quitter.
	 *
	 * @param e ActionEvent
	 */
	private void quit_actionPerformed(ActionEvent e) {
		Debug("Quitting...");
		if (e == null) {
			name.setSelectedIndex(0);
		}
		try {
			new Thread(this::runExit).start();
		}
		catch (Exception a) {
			Program.showException(a);
		}
	}

	/**
	 * lieu_itemStateChanged: Fonction pour la liste des lieux.
	 *
	 * @param e ItemEvent
	 */
	@Override
	protected void lieu_itemStateChanged(ItemEvent e) {
		if(isListenersDisabled()) {
			return;
		}
		Debug("Lieu_itemStateChanging...");
		try {
			int lieu_select = m_lieu.getSelectedIndex();

			m_end.setText("");
			m_labelExist.setText("");

			if (lieu_select == 0) {
				m_preview.setEnabled(false);
				m_num_lieu.setEnabled(false);
				m_line.setEnabled(false);
				m_column.setEnabled(false);
			}
			else {
				m_preview.setEnabled(true);
				m_num_lieu.setEnabled(true);
			}

			boolean bIsCaisse = false;
			int nb_emplacement = 0;
			int start_caisse = 0;
			Rangement cave;
			if (lieu_select > 0 && (cave = Program.getCave(lieu_select - 1)) != null) {
				nb_emplacement = cave.getNbEmplacements();
				bIsCaisse = cave.isCaisse();
				start_caisse = cave.getStartCaisse();
			}
			m_num_lieu.removeAllItems();
			m_num_lieu.addItem("");
			if (bIsCaisse) { //Type caisse
				m_preview.setEnabled(false);
				for (int i = 0; i < nb_emplacement; i++) {
					m_num_lieu.addItem(Integer.toString(i + start_caisse));
				}
				m_num_lieu.setVisible(true);
				m_labelNumPlace.setText(Program.getLabel("Infos158")); //"Numéro de caisse");
				if (nb_emplacement == 1) {
					m_num_lieu.setSelectedIndex(1);
				}
			}	else {
				m_line.removeAllItems();
				m_column.removeAllItems();
				for (int i = 1; i <= nb_emplacement; i++) {
					m_num_lieu.addItem(Integer.toString(i));
				}
				m_labelNumPlace.setText(Program.getLabel("Infos082")); //"Numéro du lieu");
			}
			m_labelNumPlace.setVisible(true);
			m_num_lieu.setVisible(true);
			m_line.setVisible(!bIsCaisse);
			m_column.setVisible(!bIsCaisse);
			m_labelLine.setVisible(!bIsCaisse);
			m_labelColumn.setVisible(!bIsCaisse);
		}
		catch (Exception a) {
			Program.showException(a);
		}
	}

	/**
	 * line_itemStateChanged: Fonction pour la liste des lignes.
	 *
	 * @param e ItemEvent
	 */
	@Override
	protected void line_itemStateChanged(ItemEvent e) {
		if(isListenersDisabled()) {
			return;
		}
		Debug("Line_itemStateChanging...");
		try {
			int nb_col = 0;
			int num_select = m_line.getSelectedIndex();
			int emplacement = m_num_lieu.getSelectedIndex();
			int lieu_select = m_lieu.getSelectedIndex();

			m_end.setText("");
			m_labelExist.setText("");

			m_column.setEnabled(num_select != 0);
			Rangement cave;
			if (num_select > 0 && (cave = Program.getCave(lieu_select - 1)) != null) { //!=0
				nb_col = cave.getNbColonnes(emplacement - 1, num_select - 1);
			}
			m_column.removeAllItems();
			m_column.addItem("");
			for (int i = 1; i <= nb_col; i++) {
				m_column.addItem(Integer.toString(i));
			}
		}
		catch (Exception a) {
			Program.showException(a);
		}
	}

	/**
	 * saving: Fonction de sauvegarde
	 *
	 */
	private void saving() {
		Debug("Saving...");
		try {
			new Thread(this).start();
		}
		catch (Exception a) {
			Program.showException(a);
		}
	}

	/**
	 * setBottle: Fonction de chargement d'un vin dans la fenêtre pour la classe ListVin
	 *
	 * @param bottle Bouteille
	 */
	private void setBottle(Bouteille bottle) {

		Debug("Set Bottle...");
		try {
			enableAll(true);
			m_nb_bottle.setValue(1);
			m_nb_bottle.setEnabled(false);
			m_laBouteille = bottle;
			name.setSelectedItem(bottle.getNom());
			m_year.setText(bottle.getAnnee());
			m_noYear.setSelected(bottle.isNonVintage());
			if(bottle.isNonVintage()) {
				m_year.setEditable(false);
			}
			m_half.removeAllItems();
			m_half.addItem("");
			for(String s: MyCellarBottleContenance.getList()) {
					m_half.addItem(s);
			}
			m_half.setSelectedItem(bottle.getType());
			String half_tmp = "";
			if (m_half.getSelectedItem() != null) {
				half_tmp = m_half.getSelectedItem().toString();
			}

			final boolean autoAdd = Program.getCaveConfigBool("TYPE_AUTO", false);

			if (autoAdd && half_tmp.compareTo(bottle.getType()) != 0 && !bottle.getType().isEmpty()) {
				MyCellarBottleContenance.getList().add(bottle.getType());
				m_half.addItem(bottle.getType());
				m_half.setSelectedItem(bottle.getType());
			}

			m_price.setText(Program.convertStringFromHTMLString(bottle.getPrix()));
			m_comment.setText(bottle.getComment());
			m_maturity.setText(bottle.getMaturity());
			m_parker.setText(bottle.getParker());
			m_colorList.setSelectedItem(BottleColor.getColor(bottle.getColor()));
			initializeVignobles(bottle);

			selectPlace(bottle);
			m_end.setText(Program.getLabel("Infos092")); //"Saisir les modifications");
			resetModified();
		}
		catch (Exception e) {
			Program.showException(e);
		}
	}

	/**
	 * keylistener_actionPerformed: Fonction d'écoute du clavier.
	 *
	 * @param e KeyEvent
	 */
	protected void keylistener_actionPerformed(KeyEvent e) {
		if (e.getKeyCode() == QUITTER && e.isControlDown()) { //CTRL+Q
			quit_actionPerformed(null);
		}
		if ( (e.getKeyCode() == AJOUTER && e.isControlDown()) || e.getKeyCode() == KeyEvent.VK_ENTER) {
			saving();
		}
		if (e.getKeyCode() == PREVIEW && e.isControlDown() && m_preview.isEnabled()) {
			preview_actionPerformed(null);
		}
		if (e.getKeyCode() == KeyEvent.VK_F1) {
			aide_actionPerformed(null);
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
		if (e.getKeyCode() == KeyEvent.VK_Q) {
			quit_actionPerformed(null);
		}
	}

	/**
	 * run: Exécution des tâches.
	 */
	@Override
	public void run() {
		save();
		new Timer().schedule(
				new TimerTask() {
						@Override
						public void run() {
							SwingUtilities.invokeLater(() -> {
						Debug("Set Text ...");
						m_end.setText("");
						Debug("Set Text Done");
							});
						}
				},
				5000
		);
	}
	
	public boolean save() {
		Debug("Modifying...");

		String nom = name.getEditor().getItem().toString();
		String demie = "";
		if(m_half.getSelectedItem() != null) {
			demie = m_half.getSelectedItem().toString();
		}

		String prix = m_price.getText();
		String comment1 = m_comment.getText();
		String dateOfC = m_maturity.getText();
		String parker = m_parker.getText();
		String color = "";
		if (m_colorList.getSelectedItem() != null) {
			color = ((BottleColor)m_colorList.getSelectedItem()).name();
		}
		Object o = comboCountry.getEditor().getItem();
		String country;
		if(o instanceof Country) {
			country = ((Country) o).getId();
		}
		else {
			country = o.toString();
		}
		o = comboVignoble.getEditor().getItem();
		String vignoble;
		if(o instanceof CountryVignoble) {
			vignoble = ((CountryVignoble) o).getName();
		}
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
		String igp = o.toString();
		//Vérification du nom ...
		if (nom.isEmpty()) {
			Debug("ERROR: Wrong Name");
			Erreur.showSimpleErreur(Program.getError("Error054")); //"Veuillez saisir le nom du vin!"
			return false;
		}

		// Controle de la date
		String annee = "";
		if (m_year.isEditable() || m_noYear.isSelected()) {
			annee = m_year.getText().trim();

			// Erreur sur la date
			if (!Bouteille.isValidYear(annee)) {
				Debug("ERROR: Wrong date");
				Erreur.showSimpleErreur(Program.getError("Error053")); //"Veuillez saisir une année valide!"
				m_year.setEditable(true);
				return false;
			}
			else {
				annee = getYear();
				m_year.setText(annee);
			}
		}

		int lieu_select = m_lieu.getSelectedIndex();
		int lieu_num = m_num_lieu.getSelectedIndex();

		if (lieu_select == 0) {
			Debug("ERROR: Wrong Place");
			Erreur.showSimpleErreur(Program.getError("Error055")); //"Veuillez sélectionner un emplacement!"
			return false;
		}

		if(lieu_num == 0) {
			Debug("ERROR: Wrong Num Place");
			if (m_line.isVisible()) {
				Erreur.showSimpleErreur(Program.getError("Error056"));
			}
			else {
				Erreur.showSimpleErreur(Program.getError("Error174"));
			}
			return false;
		}

		Rangement cave = Program.getCave(lieu_select - 1);
		if (cave != null) {
			boolean isCaisse = cave.isCaisse();
			String sPlaceName = "";
			if (lieu_select > 0) {
				sPlaceName = cave.getNom();
			}

			int line = 0;
			int column = 0;
			if (!isCaisse) {
				line = m_line.getSelectedIndex();
				if (line == 0) {
					Debug("ERROR: Wrong Line");
					Erreur.showSimpleErreur(Program.getError("Error057")); //"Veuillez sélectionner un numero de line!"
					return false;
				}

				column = m_column.getSelectedIndex();
				if (column == 0) {
					Debug("ERROR: Wrong Column");
					Erreur.showSimpleErreur(Program.getError("Error058")); //"Veuillez sélectionner un numero de colonne!"
					return false;
				}
			}

			Rangement oldRangement = m_laBouteille.getRangement();
			int oldNum = m_laBouteille.getNumLieu();
			int oldLine = m_laBouteille.getLigne();
			int oldColumn = m_laBouteille.getColonne();
			m_laBouteille.setAnnee(annee);
			m_laBouteille.setColor(color);
			m_laBouteille.setComment(comment1);
			m_laBouteille.setEmplacement(sPlaceName);
			m_laBouteille.setMaturity(dateOfC);
			m_laBouteille.setNom(nom);
			m_laBouteille.setNumLieu(lieu_num);
			m_laBouteille.setParker(parker);
			m_laBouteille.setPrix(prix);
			m_laBouteille.setType(demie);
			m_laBouteille.setVignoble(new Vignoble(country, vignoble, aoc, igp, null));
			CountryVignobles.addVignobleFromBottle(m_laBouteille);
			if(isCaisse) {
				lieu_num=Integer.parseInt(m_num_lieu.getItemAt(lieu_num));
				m_laBouteille.setNumLieu(lieu_num);
				m_laBouteille.setLigne(0);
				m_laBouteille.setColonne(0);
			}
			else {
				m_laBouteille.setLigne(line);
				m_laBouteille.setColonne(column);
			}

			if(!isCaisse) {
				Bouteille bottleInPlace = cave.getBouteille(m_laBouteille.getNumLieu()-1, m_laBouteille.getLigne()-1, m_laBouteille.getColonne()-1);
				if(bottleInPlace != null && !bottleInPlace.equals(m_laBouteille)) {
					Debug("ERROR: Not an empty place, Replace?");
					String erreur_txt1 = MessageFormat.format(Program.getError("Error059"),bottleInPlace.getNom(), bottleInPlace.getAnnee()); //" déjà présent à cette place!");
					String erreur_txt2 = Program.getError("Error060"); //"Voulez vous le remplacer?");
					if( JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(this, erreur_txt1 + "\n" + erreur_txt2, Program.getLabel("Infos049"), JOptionPane.YES_NO_OPTION)) {
						replaceWine(m_laBouteille, bottleInPlace);
						m_end.setText(Program.getLabel("Infos075"));
					}
				}
			}


			Program.getStorage().addHistory(History.MODIFY, m_laBouteille);

			Rangement rangement = m_laBouteille.getRangement();
			if(!oldRangement.isCaisse()){
				Bouteille tmp = new Bouteille();
				tmp.setNumLieu(oldNum);
				tmp.setLigne(oldLine);
				tmp.setColonne(oldColumn);
				oldRangement.clearStock(tmp);
			}

			RangementUtils.putTabStock();
			Search.updateTable();

			if(!rangement.isCaisse()) {
				rangement.updateToStock(m_laBouteille);
			}

			m_end.setText(Program.getLabel("Infos144"));
		}
		Program.updateManagePlacePanel();
		resetModified();
		
		return true;
	}

	private void resetModified() {
		name.setModified(false);
		m_year.setModified(false);
		m_comment.setModified(false);
		m_maturity.setModified(false);
		m_parker.setModified(false);
		m_colorList.setModified(false);
		m_price.setModified(false);
		m_lieu.setModified(false);
		m_num_lieu.setModified(false);
		m_line.setModified(false);
		m_column.setModified(false);
		m_half.setModified(false);
		comboCountry.setModified(false);
		comboVignoble.setModified(false);
		comboAppelationAOC.setModified(false);
		comboAppelationIGP.setModified(false);
		Start.setPaneModified(false);
	}

	private void replaceWine(Bouteille bottle, Bouteille bToDelete) {
		//Change wine in a place
		int ligne = bottle.getLigne();
		int lieu_num = bottle.getNumLieu();
		int colonne = bottle.getColonne();
		String place = bottle.getEmplacement();

		Program.getStorage().addHistory(History.MODIFY, bottle);
		if(bToDelete != null) {
			Program.getStorage().deleteWine(bToDelete);
		} else {
			Program.getStorage().replaceWineAll(bottle, lieu_num, ligne, colonne);
		}

		if(m_laBouteille != null) {
			m_laBouteille.getRangement().clearStock(m_laBouteille);
		}

		Search.removeBottle(bToDelete);
		Search.updateTable();

		Rangement r = Program.getCave(place);
		if(r != null && !r.isCaisse()) {
			r.updateToStock(bottle);
		}
	}

	private boolean runExit() {
		Debug("Processing Quit");
		m_add.setEnabled(false);
		
		boolean modified = name.isModified();
		modified |= m_year.isModified();
		modified |= (m_noYear.isSelected() != m_laBouteille.isNonVintage());
		modified |= m_comment.isModified();
		modified |= m_maturity.isModified();
		modified |= m_parker.isModified();
		modified |= m_colorList.isModified();
		modified |= m_price.isModified();
		modified |= m_lieu.isModified();
		modified |= m_num_lieu.isModified();
		modified |= m_line.isModified();
		modified |= m_column.isModified();
		modified |= m_half.isModified();
		modified |= comboCountry.isModified();
		modified |= comboVignoble.isModified();
		modified |= comboAppelationAOC.isModified();
		modified |= comboAppelationIGP.isModified();

		if(modified && JOptionPane.NO_OPTION == JOptionPane.showConfirmDialog(this, Program.getError("Error148") + " " + Program.getError("Error145"), Program.getLabel("Infos049"), JOptionPane.YES_NO_OPTION)) {
			Debug("Don't Quit.");
			m_add.setEnabled(true);
			return false;
		}

		Debug("Quitting...");
		RangementUtils.putTabStock();
		m_colorList.setSelectedItem(BottleColor.NONE);
		name.setSelectedIndex(0);
		m_year.setText("");
		m_parker.setText("");
		m_price.setText("");
		m_maturity.setText("");
		m_lieu.setSelectedIndex(0);
		m_labelExist.setText("");
		m_nb_bottle.setValue(1);
		comboCountry.setSelectedIndex(0);
		return true;
	}

	/**
	 * aide_actionPerformed: Aide
	 *
	 * @param e ActionEvent
	 */
	private void aide_actionPerformed(ActionEvent e) {
		Program.getAide();
	}


	/**
	 * Debug
	 *
	 * @param sText String
	 */
	protected static void Debug(String sText) {
		Program.Debug("ManageBottle: " + sText );
	}

	private class PanelPlace extends JPanel{
		private static final long serialVersionUID = -2601861017578176513L;

		private PanelPlace(){
			setLayout(new MigLayout("","[]30px[]30px[]30px[]30px[grow]30px[]",""));
			setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), Program.getLabel("Infos105")));
			add(m_labelPlace,"");
			add(m_labelNumPlace,"");
			add(m_labelLine,"");
			add(m_labelColumn,"wrap");
			add(m_lieu,"");
			add(m_num_lieu,"");
			add(m_line,"");
			add(m_column,"");
			add(m_labelExist,"hidemode 3");
			add(m_chooseCell,"alignx right");
			add(m_preview,"alignx right, wrap");
		}
	}

	private class PanelMain extends JPanel{
		private static final long serialVersionUID = -4824541234206895953L;

		private PanelMain(){
			setLayout(new MigLayout("","grow","[][][]10px[][grow]10px[][]"));
			add(new PanelName(),"growx,wrap");
			add(new PanelPlace(),"growx,wrap");
			add(new PanelAttribute(),"growx,split 2");
			add(panelVignobles = new PanelVignobles(instance,true),"growx, wrap");
			add(m_labelComment,"growx, wrap");
			add(m_js_comment,"grow, wrap");
			add(m_end, "center, hidemode 3, wrap");
			add(m_add, "center");
		}
	}

	@Override
	public boolean tabWillClose(TabEvent event) {
		return runExit();
	}

	@Override
	public void tabClosed() {
		Start.getInstance().updateMainPanel();
	}

}
