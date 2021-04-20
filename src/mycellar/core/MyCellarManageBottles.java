package mycellar.core;

import mycellar.Bouteille;
import mycellar.Program;
import mycellar.core.common.bottle.BottleColor;
import mycellar.general.PanelGeneral;
import mycellar.placesmanagement.PanelPlace;
import mycellar.placesmanagement.Place;
import net.miginfocom.swing.MigLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import java.text.NumberFormat;

/**
 * <p>Titre : Cave &agrave; vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2017</p>
 * <p>Soci&eacute;t&eacute; : Seb Informatique</p>
 * @author S&eacute;bastien Duch&eacute;
 * @version 4.4
 * @since 20/04/21
 */
public abstract class MyCellarManageBottles extends JPanel implements IPlace {

	private static final long serialVersionUID = 3056306291164598750L;

	private final MyCellarLabel m_labelPrice = new MyCellarLabel(LabelType.INFO, "135");
	private final MyCellarLabel m_labelNbBottle = new MyCellarLabel(LabelType.INFO, "405", LabelProperty.PLURAL);
	private final MyCellarLabel m_labelMaturity = new MyCellarLabel(LabelType.INFO, "391");
	private final MyCellarLabel m_labelParker = new MyCellarLabel(LabelType.INFO, "392");
	private final MyCellarLabel labelStatus = new MyCellarLabel(LabelType.INFO_OTHER, "MyCellarManageBottles.status");
	private final MyCellarLabel labelLastModified = new MyCellarLabel(LabelType.INFO_OTHER, "MyCellarManageBottles.lastModified");
	private final MyCellarLabel m_labelColor = new MyCellarLabel(LabelType.INFO_OTHER, "AddVin.Color");
	protected final MyCellarLabel lastModified = new MyCellarLabel("");
	protected final MyCellarLabel m_labelComment = new MyCellarLabel(LabelType.INFO, "137");
	protected final MyCellarLabel m_labelStillToAdd = new MyCellarLabel("");
	protected final MyCellarLabel m_end = new MyCellarLabel(""); // Label pour les résultats
	protected final PanelPlace panelPlace = new PanelPlace();
	protected final PanelGeneral panelGeneral = new PanelGeneral();
	protected MyCellarButton m_add;
	protected MyCellarButton m_cancel;
	protected final JModifyFormattedTextField m_price = new JModifyFormattedTextField(NumberFormat.getNumberInstance());
	protected final JModifyTextField m_maturity = new JModifyTextField();
	protected final JModifyTextField m_parker = new JModifyTextField();
	protected final JModifyComboBox<BottleColor> m_colorList = new JModifyComboBox<>();
	protected final JModifyComboBox<BottlesStatus> statusList = new JModifyComboBox<>();
	protected JModifyTextArea m_comment = new JModifyTextArea();
	protected final JScrollPane m_js_comment = new JScrollPane(m_comment);
	protected final MyCellarSpinner m_nb_bottle = new MyCellarSpinner(1, 999);
	protected boolean updateView = false;
	protected PanelVignobles panelVignobles;
	protected Bouteille bottle = null;
	protected char ajouterChar = Program.getLabel("AJOUTER").charAt(0);
	private final MyCellarLabel m_devise = new MyCellarLabel(Program.getCaveConfigString(MyCellarSettings.DEVISE, "€"));

	protected boolean m_bmulti = false; //Pour ListVin
	protected boolean isEditionMode = false;

	protected MyCellarManageBottles() {
		m_colorList.addItem(BottleColor.NONE);
		m_colorList.addItem(BottleColor.RED);
		m_colorList.addItem(BottleColor.PINK);
		m_colorList.addItem(BottleColor.WHITE);

		statusList.addItem(BottlesStatus.NONE);
		statusList.addItem(BottlesStatus.CREATED);
		statusList.addItem(BottlesStatus.MODIFIED);
		statusList.addItem(BottlesStatus.VERIFIED);
		statusList.addItem(BottlesStatus.TOCHECK);
	}

	protected void initializeExtraProperties() {
		enableAll(true);
		m_nb_bottle.setValue(1);
		m_nb_bottle.setEnabled(false);
		panelGeneral.initializeExtraProperties();

		m_price.setText(Program.convertStringFromHTMLString(bottle.getPrix()));
		m_comment.setText(bottle.getComment());
		m_maturity.setText(bottle.getMaturity());
		m_parker.setText(bottle.getParker());
		m_colorList.setSelectedItem(BottleColor.getColor(bottle.getColor()));
	}

	public void enableAll(boolean enable) {
		panelPlace.enableAll(enable);
		panelGeneral.enableAll(enable);
		m_add.setEnabled(enable);
		if (m_cancel != null) {
			m_cancel.setEnabled(enable);
		}
		m_price.setEditable(enable);
		m_maturity.setEditable(enable);
		m_parker.setEditable(enable);
		m_colorList.setEnabled(enable);
		statusList.setEnabled(enable);
		m_comment.setEditable(enable);
		m_nb_bottle.setEnabled(enable && !m_bmulti && !isEditionMode);
		panelVignobles.enableAll(enable);
		m_end.setVisible(enable);
	}

	public void setUpdateView(){
		updateView = true;
	}

	/**
	 * Mise a jour de la liste des rangements
	 */
	public void updateView() {
		if (!updateView) {
			return;
		}
		SwingUtilities.invokeLater(() -> {
			Debug("updateView...");
			updateView = false;
			panelGeneral.updateView();
			panelVignobles.updateList();
			panelPlace.updateView();
			Debug("updateView Done");
		});
	}

	/**
	 * Select a place in the lists (used from CellarOrganizerPanel)
	 * @param place
	 */
	@Override
	public void selectPlace(Place place) {
		panelPlace.selectPlace(place);
	}

	protected void clearValues() {
		m_parker.setText("");
		m_price.setText("");
		m_maturity.setText("");
		m_nb_bottle.setValue(1);
		panelGeneral.clear();
		panelPlace.clear();
		panelVignobles.resetCountrySelected();
	}

	protected static void Debug(String s) {
		Program.Debug("MyCellarManageBottles: " + s);
	}

	public final class PanelAttribute extends JPanel {
		private static final long serialVersionUID = 183053076444982489L;

		public PanelAttribute() {
			setLayout(new MigLayout("","[]30px[]30px[]",""));
			add(m_labelMaturity);
			add(m_labelParker);
			add(m_labelColor,"wrap");
			add(m_maturity,"width min(200,40%)");
			add(m_parker,"width min(150,30%)");
			add(m_colorList,"wrap, width min(150,30%)");
			add(m_labelPrice,"wrap");
			add(m_price,"width min(100,45%), split 2");
			add(m_devise,"gapleft 5px");
			add(m_labelNbBottle,"split, span 2");
			add(m_nb_bottle,"width min(50,10%)");
			add(m_labelStillToAdd,"wrap");
			add(labelStatus);
			add(labelLastModified, "wrap");
			add(statusList, "width min(150,30%)");
			add(lastModified);
		}
	}

}
