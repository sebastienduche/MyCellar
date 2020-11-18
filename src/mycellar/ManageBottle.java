package mycellar;

import mycellar.actions.ChooseCellAction;
import mycellar.core.IAddVin;
import mycellar.core.IUpdatable;
import mycellar.core.LabelProperty;
import mycellar.core.LabelType;
import mycellar.core.MyCellarButton;
import mycellar.core.MyCellarManageBottles;
import mycellar.core.MyCellarSettings;
import mycellar.core.PanelVignobles;
import mycellar.core.PopupListener;
import mycellar.core.datas.MyCellarBottleContenance;
import mycellar.core.datas.jaxb.VignobleJaxb;
import mycellar.vignobles.CountryVignobleController;
import net.miginfocom.swing.MigLayout;

import javax.swing.BorderFactory;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ItemEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseListener;
import java.text.MessageFormat;
import java.util.LinkedList;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;

import static mycellar.core.LabelProperty.OF_THE_SINGLE;


/**
 * <p>Titre : Cave &agrave; vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2005</p>
 * <p>Soci&eacute;t&eacute; : Seb Informatique</p>
 * @author S&eacute;bastien Duch&eacute;
 * @version 6.8
 * @since 18/11/20
 */
public final class ManageBottle extends MyCellarManageBottles implements Runnable, ITabListener, IAddVin, IUpdatable {
	private static final long serialVersionUID = 5330256984954964913L;


	/**
	 * ManageBottle: Constructeur pour la modification de vins
	 *
	 * @param bottle
	 */
	public ManageBottle(Bouteille bottle) {
		super();
		isEditionMode = true;
		m_add = new MyCellarButton(MyCellarImage.SAVE);
		
		m_chooseCell = new MyCellarButton(LabelType.INFO_OTHER, "AddVin.ChooseCell", new ChooseCellAction(this));
		try {
			Debug("Constructor with Bottle");
			LinkedList<String> list = new LinkedList<>();
			list.add("");
			list.addAll(Program.getStorage().getBottleNames());
			String[] bottlesName = new String[0];
			bottlesName = list.toArray(bottlesName);
			name = new JCompletionComboBox<>(bottlesName) {
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
			MyCellarBottleContenance.getList().forEach(m_half::addItem);
			m_half.setSelectedItem(MyCellarBottleContenance.getDefaultValue());

			Debug("Starting JbInit");
			setYearAuto();

			m_price.addKeyListener(new KeyAdapter() {
				@Override
				public void keyTyped(KeyEvent e) {
					if (e.getKeyChar() == ',' || e.getKeyChar() == '.') {
						e.consume();
						char sep = Program.getDecimalSeparator();
						String text = m_price.getText();
						m_price.setText(text+sep);
					}
				}
			});

			m_nb_bottle.setToolTipText(Program.getLabel("AddVin.NbItemsToAdd", LabelProperty.PLURAL));
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
				if (m_noYear.isSelected()) {
					m_year.setText(Bouteille.NON_VINTAGE);
					m_year.setEditable(false);
				}	else {
					m_year.setText("");
					m_year.setEditable(true);
				}
			});


			initPlaceCombo();
			setListeners();

			m_num_lieu.setEnabled(false);
			m_line.setEnabled(false);
			m_column.setEnabled(false);

			setVisible(true);
			Debug("JbInit Done");

			setBottle(bottle);
		}	catch (RuntimeException e) {
			Program.showException(e);
		}
	}

	public Bouteille getBottle() {
		return bottle;
	}

	/**
	 * lieu_itemStateChanged: Fonction pour la liste des lieux.
	 *
	 * @param e ItemEvent
	 */
	@Override
	protected void lieu_itemStateChanged(ItemEvent e) {
		if (isListenersDisabled()) {
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
			}	else {
				m_preview.setEnabled(true);
				m_num_lieu.setEnabled(true);
			}

			boolean bIsCaisse = false;
			int nb_emplacement = 0;
			int start_caisse = 0;
			if (lieu_select > 0) {
				Rangement cave = m_lieu.getItemAt(lieu_select);
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
				m_labelNumPlace.setText(Program.getLabel("Infos158")); //"Numero de caisse");
				if (nb_emplacement == 1) {
					m_num_lieu.setSelectedIndex(1);
				}
			}	else {
				m_line.removeAllItems();
				m_column.removeAllItems();
				for (int i = 1; i <= nb_emplacement; i++) {
					m_num_lieu.addItem(Integer.toString(i));
				}
				m_labelNumPlace.setText(Program.getLabel("Infos082")); //"Numero du lieu");
			}
			m_labelNumPlace.setVisible(true);
			m_num_lieu.setVisible(true);
			m_line.setVisible(!bIsCaisse);
			m_column.setVisible(!bIsCaisse);
			m_labelLine.setVisible(!bIsCaisse);
			m_labelColumn.setVisible(!bIsCaisse);
			Debug("Lieu_itemStateChanging... Done");
		}	catch (RuntimeException a) {
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
		if (isListenersDisabled()) {
			return;
		}
		Debug("Line_itemStateChanging...");
		m_end.setText("");
		m_labelExist.setText("");

		initColumnCombo();
		Debug("Line_itemStateChanging... Done");
}

	/**
	 * saving: Fonction de sauvegarde
	 *
	 */
	private void saving() {
		Debug("Saving...");
		try {
			new Thread(this).start();
		}	catch (RuntimeException a) {
			Program.showException(a);
		}
	}

	/**
	 * setBottle: Fonction de chargement d'un vin
	 *
	 * @param bottle Bouteille
	 */
	private void setBottle(Bouteille bottle) {
		Debug("Set Bottle...");
		try {
			this.bottle = bottle;
			enableAll(true);
			m_nb_bottle.setValue(1);
			m_nb_bottle.setEnabled(false);
			name.setSelectedItem(bottle.getNom());
			m_year.setText(bottle.getAnnee());
			m_noYear.setSelected(bottle.isNonVintage());
			if (bottle.isNonVintage()) {
				m_year.setEditable(false);
			}
			m_half.removeAllItems();
			m_half.addItem("");
			MyCellarBottleContenance.getList().forEach(m_half::addItem);
			m_half.setSelectedItem(bottle.getType());
			String half_tmp = "";
			if (m_half.getSelectedItem() != null) {
				half_tmp = m_half.getSelectedItem().toString();
			}

			final boolean autoAdd = Program.getCaveConfigBool(MyCellarSettings.TYPE_AUTO, false);

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
			panelVignobles.initializeVignobles(bottle);
			updateStatusAndTime();

			selectPlace(bottle);
			m_end.setText(Program.getLabel("Infos092")); //"Saisir les modifications");
			resetModified();
		}	catch (RuntimeException e) {
			Program.showException(e);
		}
	}

	private void updateStatusAndTime() {
		statusList.setSelectedItem(BottlesStatus.getStatus(bottle.getStatus()));
		lastModified.setText(bottle.getLastModified());
	}

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
		Debug("Saving...");

		String nom = name.getEditor().getItem().toString();
		String demie = "";
		if (m_half.getSelectedItem() != null) {
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
		String status = BottlesStatus.MODIFIED.name();
		if (statusList.isModified() && statusList.getSelectedItem() != null) {
			status = ((BottlesStatus)statusList.getSelectedItem()).name();
		}
		String country = panelVignobles.getCountry();
		String vignoble = panelVignobles.getVignoble();
		String aoc = panelVignobles.getAOC();
		String igp = panelVignobles.getIGP();

		if (!MyCellarControl.checkBottleName(nom)) {
			return false;
		}

		// Controle de la date
		String annee = "";
		if (m_year.isEditable() || m_noYear.isSelected()) {
			annee = m_year.getText();

			// Erreur sur la date
			if (!MyCellarControl.checkYear(annee)) {
				m_year.setEditable(true);
				return false;
			}	else {
				annee = getYear();
				m_year.setText(annee);
			}
		}

		int lieu_select = m_lieu.getSelectedIndex();
		int lieu_num = m_num_lieu.getSelectedIndex();

		if (!MyCellarControl.checkPlaceNumberGreaterThan0(lieu_select)) {
			return false;
		}

		Rangement cave = m_lieu.getItemAt(lieu_select);
		boolean isCaisse = cave.isCaisse();

		if (!MyCellarControl.checkNumLieuNumberGreaterThan0(lieu_num, isCaisse)) {
			return false;
		}

		int line = 0;
		int column = 0;
		if (!isCaisse) {
			line = m_line.getSelectedIndex();
			if (!MyCellarControl.checkLineNumberGreaterThan0(line)) {
				return false;
			}
			column = m_column.getSelectedIndex();
			if (!MyCellarControl.checkColumnNumberGreaterThan0(column)) {
				return false;
			}
		}

		Rangement oldRangement = bottle.getRangement();
		int oldNum = bottle.getNumLieu();
		int oldLine = bottle.getLigne();
		int oldColumn = bottle.getColonne();
		bottle.setAnnee(annee);
		bottle.setColor(color);
		bottle.setComment(comment1);
		bottle.setEmplacement(cave.getNom());
		bottle.setMaturity(dateOfC);
		bottle.setNom(nom);
		bottle.setNumLieu(lieu_num);
		bottle.setParker(parker);
		bottle.setPrix(prix);
		bottle.setType(demie);
		bottle.setVignoble(new VignobleJaxb(country, vignoble, aoc, igp, null));
		bottle.setStatus(status);
		CountryVignobleController.addVignobleFromBottle(bottle);
		CountryVignobleController.setRebuildNeeded();
		if (isCaisse) {
			lieu_num = Integer.parseInt(m_num_lieu.getItemAt(lieu_num));
			bottle.setNumLieu(lieu_num);
			bottle.setLigne(0);
			bottle.setColonne(0);
		}	else {
			bottle.setLigne(line);
			bottle.setColonne(column);

			Optional<Bouteille> bottleInPlace = cave.getBouteille(bottle);
			bottleInPlace.ifPresent( bouteille -> {
				if (!bouteille.equals(bottle)) {
					Debug("ERROR: Not an empty place, Replace?");
					String erreur_txt1 = MessageFormat.format(Program.getError("Error059"),bouteille.getNom(), bouteille.getAnnee()); //" d&eacute;j&agrave; pr&eacute;sent &agrave; cette place!");
					String erreur_txt2 = Program.getError("Error060"); //"Voulez vous le remplacer?");
					if (JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(this, erreur_txt1 + "\n" + erreur_txt2, Program.getLabel("Infos049"), JOptionPane.YES_NO_OPTION)) {
						replaceWine(bouteille);
						m_end.setText(Program.getLabel("AddVin.1ItemAdded", LabelProperty.SINGLE));
					}
				}
			});
		}

		bottle.setModified();
		Program.getStorage().addHistory(History.MODIFY, bottle);

		Rangement rangement = bottle.getRangement();
		if (!oldRangement.isCaisse()) {
			oldRangement.clearStock(new Bouteille.BouteilleBuilder("").numPlace(oldNum).line(oldLine).column(oldColumn).build());
		}

		RangementUtils.putTabStock();
		Program.getSearch().ifPresent(Search::updateTable);

		if (!rangement.isCaisse()) {
			rangement.updateToStock(bottle);
		}

		m_end.setText(Program.getLabel("AddVin.1ItemModified", LabelProperty.SINGLE));
		Program.updateManagePlacePanel();
		updateStatusAndTime();
		resetModified();
		Debug("Saving... Done");
		
		return true;
	}

	private void resetModified() {
		name.setModified(false);
		m_year.setModified(false);
		m_comment.setModified(false);
		m_maturity.setModified(false);
		m_parker.setModified(false);
		m_colorList.setModified(false);
		statusList.setModified(false);
		m_price.setModified(false);
		m_lieu.setModified(false);
		m_num_lieu.setModified(false);
		m_line.setModified(false);
		m_column.setModified(false);
		m_half.setModified(false);
		panelVignobles.setModified(false);
		Start.setPaneModified(false);
	}

	private void replaceWine(final Bouteille bToDelete) {
		//Change wine in a place
		Program.getStorage().addHistory(History.MODIFY, bottle);
		Program.getStorage().deleteWine(bToDelete);

		bottle.getRangement().clearStock(bottle);

		Program.getSearch().ifPresent(search -> {
			search.removeBottle(bToDelete);
			search.updateTable();
		});

		final Rangement rangement = bottle.getRangement();
		if (rangement != null && !rangement.isCaisse()) {
			rangement.updateToStock(bottle);
		}
	}

	private boolean runExit() {
		Debug("Processing Quit...");
		m_add.setEnabled(false);
		
		boolean modified = name.isModified();
		modified |= m_year.isModified();
		modified |= (m_noYear.isSelected() != bottle.isNonVintage());
		modified |= m_comment.isModified();
		modified |= m_maturity.isModified();
		modified |= m_parker.isModified();
		modified |= m_colorList.isModified();
		modified |= statusList.isModified();
		modified |= m_price.isModified();
		modified |= m_lieu.isModified();
		modified |= m_num_lieu.isModified();
		modified |= m_line.isModified();
		modified |= m_column.isModified();
		modified |= m_half.isModified();
		modified |= panelVignobles.isModified();

		if (modified && JOptionPane.NO_OPTION == JOptionPane.showConfirmDialog(this, Program.getError("Error148", OF_THE_SINGLE) + " " + Program.getError("Error145"), Program.getLabel("Infos049"), JOptionPane.YES_NO_OPTION)) {
			Debug("Don't Quit.");
			m_add.setEnabled(true);
			return false;
		}

		Debug("Quitting...");
		RangementUtils.putTabStock();
		m_colorList.setSelectedItem(BottleColor.NONE);
		statusList.setSelectedItem(BottlesStatus.NONE);
    clearValues();
		Debug("Processing Quit... Done");
		return true;
	}

	/**
	 * Debug
	 *
	 * @param sText String
	 */
	protected static void Debug(String sText) {
		Program.Debug("ManageBottle: " + sText);
	}

	private final class PanelPlace extends JPanel {
		private static final long serialVersionUID = -2601861017578176513L;

		private PanelPlace(){
			setLayout(new MigLayout("","[]30px[]30px[]30px[]30px[grow]30px[]",""));
			setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), Program.getLabel("Infos105")));
			add(m_labelPlace);
			add(m_labelNumPlace);
			add(m_labelLine);
			add(m_labelColumn,"wrap");
			add(m_lieu);
			add(m_num_lieu);
			add(m_line);
			add(m_column);
			add(m_labelExist,"hidemode 3");
			add(m_chooseCell,"alignx right");
			add(m_preview,"alignx right, wrap");
		}
	}

	private final class PanelMain extends JPanel {
		private static final long serialVersionUID = -4824541234206895953L;

		private PanelMain(){
			setLayout(new MigLayout("","grow","[][][]10px[][grow]10px[][]"));
			add(new PanelName(),"growx,wrap");
			add(new PanelPlace(),"growx,wrap");
			add(new PanelAttribute(),"growx,split 2");
			add(panelVignobles = new PanelVignobles(true, true, true),"growx, wrap");
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
	
	@Override
	public void updateView() {
		if (!updateView) {
			return;
		}
		SwingUtilities.invokeLater(() -> {
			Debug("updateView...");
			setListenersEnabled(false);
			updateView = false;
			m_lieu.removeAllItems();
			initPlaceCombo();
			m_half.removeAllItems();
			m_half.addItem("");
			MyCellarBottleContenance.getList().forEach(m_half::addItem);
			m_half.setSelectedItem(MyCellarBottleContenance.getDefaultValue());
			panelVignobles.updateList();
			selectPlace(bottle);
			setListenersEnabled(true);
			Debug("updateView Done");
		});
	}

}
