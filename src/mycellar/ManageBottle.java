package mycellar;

import mycellar.actions.OpenShowErrorsAction;
import mycellar.core.BottlesStatus;
import mycellar.core.IMyCellarObject;
import mycellar.core.IUpdatable;
import mycellar.core.LabelProperty;
import mycellar.core.MyCellarButton;
import mycellar.core.MyCellarManageBottles;
import mycellar.core.PanelVignobles;
import mycellar.core.PopupListener;
import mycellar.core.common.bottle.BottleColor;
import mycellar.core.datas.history.HistoryState;
import mycellar.core.datas.jaxb.VignobleJaxb;
import mycellar.placesmanagement.Place;
import mycellar.placesmanagement.Rangement;
import mycellar.placesmanagement.RangementUtils;
import mycellar.vignobles.CountryVignobleController;
import net.miginfocom.swing.MigLayout;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.MessageFormat;
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
 * @version 8.1
 * @since 20/04/21
 */
public final class ManageBottle extends MyCellarManageBottles implements Runnable, ITabListener, IUpdatable {
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

		try {
			Debug("Constructor with Bottle");
			panelGeneral.initializeForEdition();

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
			m_add.setMnemonic(ajouterChar);

			PopupListener popup_l = new PopupListener();
			panelGeneral.setMouseListener(popup_l);
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

			setVisible(true);
			Debug("JbInit Done");

			setBottle(bottle);
		}	catch (RuntimeException e) {
			Program.showException(e);
		}
	}

	protected Bouteille getBottle() {
		return bottle;
	}

	/**
	 * saving: Fonction de sauvegarde
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
			panelGeneral.setMyCellarObject(bottle);
			initializeExtraProperties();
			panelVignobles.initializeVignobles(bottle);
			updateStatusAndTime();

			panelPlace.selectPlace(bottle);
			m_end.setText(Program.getLabel("Infos092")); //"Saisir les modifications");
			resetModified();
		}	catch (RuntimeException e) {
			Program.showException(e);
		}
		Debug("Set Bottle... Done");
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

		String nom = panelGeneral.getObjectName();
		String demie = panelGeneral.getType();

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

		if (!panelGeneral.performValidation()) {
			return false;
		}

		final Place place = panelPlace.getSelectedPlace();

		if (MyCellarControl.hasInvalidPlace(place)) {
			return false;
		}

		int lieu_num = place.getPlaceNum();
		Rangement cave = place.getRangement();
		boolean isCaisse = cave.isCaisse();

		if (MyCellarControl.hasInvalidNumLieuNumber(lieu_num, isCaisse)) {
			return false;
		}

		int line = 0;
		int column = 0;
		if (!isCaisse) {
			line = place.getLine();
			if (MyCellarControl.hasInvalidLineNumber(line)) {
				return false;
			}
			column = place.getColumn();
			if (MyCellarControl.hasInvalidColumnNumber(column)) {
				return false;
			}
		}

		Place oldPlace = bottle.getPlace();
		bottle.setAnnee(panelGeneral.getYear());
		bottle.setColor(color);
		bottle.setComment(comment1);
		bottle.setEmplacement(cave.getNom());
		bottle.setMaturity(dateOfC);
		bottle.setNom(nom);
		bottle.setParker(parker);
		bottle.setPrix(prix);
		bottle.setType(demie);
		bottle.setVignoble(new VignobleJaxb(country, vignoble, aoc, igp));
		bottle.setStatus(status);
		CountryVignobleController.addVignobleFromBottle(bottle);
		CountryVignobleController.setRebuildNeeded();
		if (isCaisse) {
			lieu_num = place.getPlaceNum();
			bottle.setNumLieu(lieu_num);
			bottle.setLigne(0);
			bottle.setColonne(0);
		}	else {
			Optional<IMyCellarObject> bottleInPlace = cave.getBouteille(new Bouteille.BouteilleBuilder("").numPlace(lieu_num).line(line).column(column).build());
			if (bottleInPlace.isPresent()) {
				if (!askToReplaceBottle(bottleInPlace.get())) {
					return false;
				}
			}
			bottle.setNumLieu(lieu_num);
			bottle.setLigne(line);
			bottle.setColonne(column);
		}

		bottle.setModified();
		Program.getStorage().addHistory(HistoryState.MODIFY, bottle);

		if (!oldPlace.isSimplePlace()) {
			oldPlace.getRangement().clearComplexStock(oldPlace);
		}

		if (!RangementUtils.putTabStock()) {
			new OpenShowErrorsAction().actionPerformed(null);
		}
		Program.getSearch().ifPresent(Search::updateTable);

		Rangement rangement = bottle.getRangement();
		if (!rangement.isCaisse()) {
			rangement.updateToStock(bottle);
		}

		m_end.setText(Program.getLabel("AddVin.1ItemModified", LabelProperty.SINGLE));
		Program.updatePanelsWithoutBottles();
		updateStatusAndTime();
		resetModified();
		Debug("Saving... Done");

		return true;
	}

	private boolean askToReplaceBottle(IMyCellarObject bouteille) {
		if (!bouteille.equals(bottle)) {
			Debug("ERROR: Not an empty place, Replace?");
			String erreur_txt1 = MessageFormat.format(Program.getError("Error059"),bouteille.getNom(), bouteille.getAnnee());
			String erreur_txt2 = Program.getError("Error060"); //"Voulez vous le remplacer?");
			if (JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(Start.getInstance(), erreur_txt1 + "\n" + erreur_txt2, Program.getLabel("Infos049"), JOptionPane.YES_NO_OPTION)) {
				replaceWine(bouteille);
				m_end.setText(Program.getLabel("AddVin.1ItemAdded", LabelProperty.SINGLE));
			} else {
				return false;
			}
		}
		return true;
	}

	private void resetModified() {
		panelGeneral.resetModified(false);
		m_comment.setModified(false);
		m_maturity.setModified(false);
		m_parker.setModified(false);
		m_colorList.setModified(false);
		statusList.setModified(false);
		m_price.setModified(false);
		panelVignobles.setModified(false);
		panelPlace.clearModified();
		Start.setPaneModified(false);
	}

	private void replaceWine(final IMyCellarObject bToDelete) {
		//Change wine in a place
		Program.getStorage().addHistory(HistoryState.MODIFY, bottle);
		Program.getStorage().deleteWine(bToDelete);

		bottle.getRangement().clearStock(bottle);

		Program.getSearch().ifPresent(search -> {
			search.removeBottle(bToDelete);
			search.updateTable();
		});

		final Rangement rangement = bottle.getRangement();
		if (!rangement.isCaisse()) {
			rangement.updateToStock(bottle);
		}
	}

	private boolean runExit() {
		Debug("Processing Quit...");
		m_add.setEnabled(false);

		boolean modified = panelGeneral.isModified(bottle);
		modified |= m_comment.isModified();
		modified |= m_maturity.isModified();
		modified |= m_parker.isModified();
		modified |= m_colorList.isModified();
		modified |= statusList.isModified();
		modified |= m_price.isModified();
		modified |= panelPlace.isModified();
		modified |= panelVignobles.isModified();

		if (modified && JOptionPane.NO_OPTION == JOptionPane.showConfirmDialog(Start.getInstance(), Program.getError("Error148", OF_THE_SINGLE) + " " + Program.getError("Error145"), Program.getLabel("Infos049"), JOptionPane.YES_NO_OPTION)) {
			Debug("Don't Quit.");
			m_add.setEnabled(true);
			return false;
		}

		Debug("Quitting...");
		if (!RangementUtils.putTabStock()) {
			new OpenShowErrorsAction().actionPerformed(null);
		}
		m_colorList.setSelectedItem(BottleColor.NONE);
		statusList.setSelectedItem(BottlesStatus.NONE);
		clearValues();
		Debug("Quitting... Done");
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

	private final class PanelMain extends JPanel {
		private static final long serialVersionUID = -4824541234206895953L;

		private PanelMain() {
			setLayout(new MigLayout("","grow","[][][]10px[][grow]10px[][]"));
			add(panelGeneral, "growx,wrap");
			add(panelPlace, "growx,wrap");
			add(new PanelAttribute(), "growx,split 2");
			add(panelVignobles = new PanelVignobles(true, true, true), "growx, wrap");
			add(m_labelComment, "growx, wrap");
			add(m_js_comment, "grow, wrap");
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
			panelPlace.setListenersEnabled(false);
			updateView = false;
			panelGeneral.updateView();
			panelVignobles.updateList();
			panelPlace.updateView();
			panelPlace.selectPlace(bottle);
			panelPlace.setListenersEnabled(true);
			Debug("updateView Done");
		});
	}

}
