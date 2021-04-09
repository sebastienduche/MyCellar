package mycellar.core;

import mycellar.Bouteille;
import mycellar.Erreur;
import mycellar.JCompletionComboBox;
import mycellar.Program;
import mycellar.actions.ManageCapacityAction;
import mycellar.core.bottle.BottleColor;
import mycellar.core.datas.MyCellarBottleContenance;
import mycellar.placesmanagement.PanelPlace;
import mycellar.placesmanagement.Place;
import net.miginfocom.swing.MigLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import java.awt.event.ActionEvent;
import java.text.MessageFormat;
import java.text.NumberFormat;

/**
 * <p>Titre : Cave &agrave; vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2017</p>
 * <p>Soci&eacute;t&eacute; : Seb Informatique</p>
 * @author S&eacute;bastien Duch&eacute;
 * @version 4.2
 * @since 17/03/21
 */
public abstract class MyCellarManageBottles extends JPanel implements IPlace {

	private static final long serialVersionUID = 3056306291164598750L;

	private final MyCellarLabel m_labelName = new MyCellarLabel(LabelType.INFO, "208");
	private final MyCellarLabel m_labelYear = new MyCellarLabel(LabelType.INFO, "189");
	protected final MyCellarLabel m_contenance = new MyCellarLabel(LabelType.INFO, "134");
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
	protected final MyCellarCheckBox m_annee_auto = new MyCellarCheckBox("");
	private final int siecle = Program.getCaveConfigInt(MyCellarSettings.SIECLE, 20) - 1;
	protected final PanelPlace panelPlace = new PanelPlace();
	protected MyCellarButton m_add;
	protected MyCellarButton m_cancel;
	protected JCompletionComboBox<String> name = new JCompletionComboBox<>();
	protected final JModifyTextField m_year = new JModifyTextField();
	protected final JModifyComboBox<String> m_half = new JModifyComboBox<>();
	protected final MyCellarCheckBox m_noYear = new MyCellarCheckBox(LabelType.INFO, "399");
	protected final JModifyFormattedTextField m_price = new JModifyFormattedTextField(NumberFormat.getNumberInstance());
	protected final JModifyTextField m_maturity = new JModifyTextField();
	protected final JModifyTextField m_parker = new JModifyTextField();
	protected final JModifyComboBox<BottleColor> m_colorList = new JModifyComboBox<>();
	protected final JModifyComboBox<BottlesStatus> statusList = new JModifyComboBox<>();
	protected JModifyTextArea m_comment = new JModifyTextArea();
	protected final JScrollPane m_js_comment = new JScrollPane(m_comment);
	protected final MyCellarButton m_manageContenance = new MyCellarButton(LabelType.INFO, "400");
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
		if (!half_tmp.equals(bottle.getType()) && !bottle.getType().isEmpty()) {
			MyCellarBottleContenance.getList().add(bottle.getType());
			m_half.addItem(bottle.getType());
			m_half.setSelectedItem(bottle.getType());
		}

		m_price.setText(Program.convertStringFromHTMLString(bottle.getPrix()));
		m_comment.setText(bottle.getComment());
		m_maturity.setText(bottle.getMaturity());
		m_parker.setText(bottle.getParker());
		m_colorList.setSelectedItem(BottleColor.getColor(bottle.getColor()));
	}

	protected void annee_auto_actionPerformed(ActionEvent e) {
		Debug("Annee_auto_actionPerformed...");
		if (!m_annee_auto.isSelected()) {
			Program.putCaveConfigBool(MyCellarSettings.ANNEE_AUTO, false);

			if (!Program.getCaveConfigBool(MyCellarSettings.ANNEE_AUTO_FALSE, false)) {
				String erreur_txt1 = MessageFormat.format(Program.getError("Error084"), ((siecle + 1) * 100)); //"En decochant cette option, vous dsactivez la transformation");
				Erreur.showKeyErreur(erreur_txt1, "", MyCellarSettings.ANNEE_AUTO_FALSE);
			}
		} else {
			Program.putCaveConfigBool(MyCellarSettings.ANNEE_AUTO, true);

			if (!Program.getCaveConfigBool(MyCellarSettings.ANNEE_AUTO_TRUE, false)) {
				String erreur_txt1 = MessageFormat.format(Program.getError("Error086"), ((siecle + 1) * 100));//"En cochant cette option, vous activez la transformation");
				Erreur.showKeyErreur(erreur_txt1, "", MyCellarSettings.ANNEE_AUTO_TRUE);
			}
		}
		Debug("Annee_auto_actionPerformed...End");
	}

	public void enableAll(boolean enable) {
		panelPlace.enableAll(enable);
		m_add.setEnabled(enable);
		if (m_cancel != null) {
			m_cancel.setEnabled(enable);
		}
		m_half.setEnabled(enable && !m_bmulti);
		name.setEnabled(enable && !m_bmulti);
		m_year.setEditable(enable && !m_noYear.isSelected());
		m_price.setEditable(enable);
		m_maturity.setEditable(enable);
		m_parker.setEditable(enable);
		m_colorList.setEnabled(enable);
		statusList.setEnabled(enable);
		m_comment.setEditable(enable);
		m_annee_auto.setEnabled(enable);
		m_noYear.setEnabled(enable);
		m_nb_bottle.setEnabled(enable && !m_bmulti && !isEditionMode);
		m_manageContenance.setEnabled(enable);
		panelVignobles.enableAll(enable);
		m_end.setVisible(enable);
	}

	protected String getYear() {

		if (m_noYear.isSelected()) {
			return Bouteille.NON_VINTAGE;
		}

		String annee = m_year.getText();
		if (m_annee_auto.isSelected() && annee.length() == 2) {
			int n = Program.getCaveConfigInt(MyCellarSettings.ANNEE, 50);
			if(Program.safeParseInt(annee, -1) > n) {
				annee = siecle + annee;
			} else {
				annee = siecle + 1 + annee;
			}
		}
		return annee;
	}

	protected final void setYearAuto() {
		m_annee_auto.setText(MessageFormat.format(Program.getLabel("Infos117"), ((siecle + 1) * 100))); //"Annee 00 -> 2000");
		m_annee_auto.setSelected(Program.getCaveConfigBool(MyCellarSettings.ANNEE_AUTO, false));
	}

	protected void manageContenance_actionPerformed(ActionEvent e) {
		Debug("Manage Capacity...");
		new ManageCapacityAction().actionPerformed(null);
		Debug("Manage Capacity... End");
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
			m_half.removeAllItems();
			m_half.addItem("");
			for (String s : MyCellarBottleContenance.getList()) {
				m_half.addItem(s);
			}
			m_half.setSelectedItem(MyCellarBottleContenance.getDefaultValue());
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
		name.setSelectedIndex(0);
		m_year.setText("");
		m_parker.setText("");
		m_price.setText("");
		m_maturity.setText("");
		m_nb_bottle.setValue(1);
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

	public final class PanelName extends JPanel {
		private static final long serialVersionUID = 8617685535706381964L;

		public PanelName() {
			setLayout(new MigLayout("","[grow]30px[]10px[]10px[]30px[]10px[]",""));
			add(m_labelName,"grow");
			add(m_labelYear);
			add(m_annee_auto);
			add(m_contenance,"wrap");
			add(name,"grow");
			add(m_year,"width min(100,10%)");
			add(m_noYear);
			add(m_half,"push");
			add(m_manageContenance);
		}
	}
}
