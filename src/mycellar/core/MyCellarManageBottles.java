package mycellar.core;

import mycellar.BottleColor;
import mycellar.BottlesStatus;
import mycellar.Bouteille;
import mycellar.Erreur;
import mycellar.JCompletionComboBox;
import mycellar.ManageList;
import mycellar.MyXmlDom;
import mycellar.Program;
import mycellar.Rangement;
import mycellar.RangementUtils;
import mycellar.core.datas.MyCellarBottleContenance;
import net.miginfocom.swing.MigLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.io.File;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.util.LinkedList;
import java.util.Optional;

/**
 * <p>Titre : Cave &agrave; vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2017</p>
 * <p>Soci&eacute;t&eacute; : Seb Informatique</p>
 * @author S&eacute;bastien Duch&eacute;
 * @version 3.6
 * @since 30/10/20
 */
public abstract class MyCellarManageBottles extends JPanel {

	private static final long serialVersionUID = 3056306291164598750L;
	
	private final MyCellarLabel m_labelName = new MyCellarLabel(LabelType.INFO, "208");
	private final MyCellarLabel m_labelYear = new MyCellarLabel(LabelType.INFO, "189");
	protected final MyCellarLabel m_labelPlace = new MyCellarLabel(LabelType.INFO, "208");
	protected final MyCellarLabel m_labelNumPlace = new MyCellarLabel(LabelType.INFO, "082");
	protected final MyCellarLabel m_contenance = new MyCellarLabel(LabelType.INFO, "134");
	protected final MyCellarLabel m_labelLine = new MyCellarLabel(LabelType.INFO, "028");
	protected final MyCellarLabel m_labelColumn = new MyCellarLabel(LabelType.INFO, "083");
	private final MyCellarLabel m_labelPrice = new MyCellarLabel(LabelType.INFO, "135");
	private final MyCellarLabel m_labelNbBottle = new MyCellarLabel(LabelType.INFO, "405", LabelProperty.PLURAL);
	private final MyCellarLabel m_labelMaturity = new MyCellarLabel(LabelType.INFO, "391");
	private final MyCellarLabel m_labelParker = new MyCellarLabel(LabelType.INFO, "392");
	private final MyCellarLabel labelStatus = new MyCellarLabel(LabelType.INFO_OTHER, "MyCellarManageBottles.status");
	private final MyCellarLabel labelLastModified = new MyCellarLabel(LabelType.INFO_OTHER, "MyCellarManageBottles.lastModified");
	private final MyCellarLabel m_labelColor = new MyCellarLabel(LabelType.INFO_OTHER, "AddVin.Color");
	protected final MyCellarLabel lastModified = new MyCellarLabel("");
	protected final MyCellarLabel m_labelComment = new MyCellarLabel(LabelType.INFO, "137");
	protected final MyCellarButton m_preview = new MyCellarButton(LabelType.INFO, "138");
	protected final MyCellarLabel m_labelStillToAdd = new MyCellarLabel("");
	protected final MyCellarLabel m_end = new MyCellarLabel(""); // Label pour les résultats
	protected final MyCellarCheckBox m_annee_auto = new MyCellarCheckBox("");
	private final int SIECLE = Program.getCaveConfigInt(MyCellarSettings.SIECLE, 20) - 1;
	protected final JModifyComboBox<Rangement> m_lieu = new JModifyComboBox<>();
	protected final JModifyComboBox<String> m_num_lieu = new JModifyComboBox<>();
	protected final JModifyComboBox<String> m_line = new JModifyComboBox<>();
	protected final JModifyComboBox<String> m_column = new JModifyComboBox<>();
	protected final MyCellarLabel m_labelExist = new MyCellarLabel();
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
	protected MyCellarButton m_chooseCell;
	protected PanelVignobles panelVignobles;
	protected Bouteille bottle = null;
	protected char AJOUTER = Program.getLabel("AJOUTER").charAt(0);
	private char PREVIEW = Program.getLabel("PREVIEW").charAt(0);
	private final MyCellarLabel m_devise = new MyCellarLabel(Program.getCaveConfigString(MyCellarSettings.DEVISE, "€"));
	private boolean listenersEnabled = true;
	
	protected boolean m_bmulti = false; //Pour ListVin
	protected boolean isEditionMode = false;
	
	protected MyCellarManageBottles() {
		m_preview.setMnemonic(PREVIEW);
		m_preview.setEnabled(false);
		m_preview.addActionListener(this::preview_actionPerformed);

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
	
	protected void annee_auto_actionPerformed(ActionEvent e) {
		Debug("Annee_auto_actionPerformed...");
		if (!m_annee_auto.isSelected()) {
			Program.putCaveConfigBool(MyCellarSettings.ANNEE_AUTO, false);
			
			if (!Program.getCaveConfigBool(MyCellarSettings.ANNEE_AUTO_FALSE, false)) {
				String erreur_txt1 = MessageFormat.format(Program.getError("Error084"), ((SIECLE + 1) * 100)); //"En decochant cette option, vous dsactivez la transformation");
				Erreur.showKeyErreur(erreur_txt1, "", MyCellarSettings.ANNEE_AUTO_FALSE);
			}
		}
		else {
			Program.putCaveConfigBool(MyCellarSettings.ANNEE_AUTO, true);

			if (!Program.getCaveConfigBool(MyCellarSettings.ANNEE_AUTO_TRUE, false)) {
				String erreur_txt1 = MessageFormat.format(Program.getError("Error086"), ((SIECLE + 1) * 100));//"En cochant cette option, vous activez la transformation");
				Erreur.showKeyErreur(erreur_txt1, "", MyCellarSettings.ANNEE_AUTO_TRUE);
			}
		}
		Debug("Annee_auto_actionPerformed...End");
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

			if (nPlace < 1 || nNumLieu < 1 || nLine < 1 || nColumn < 1) {
				return;
			}

			Rangement cave = m_lieu.getItemAt(nPlace);
			Optional<Bouteille> b = cave.getBouteille(nNumLieu - 1, nLine - 1, nColumn - 1);
			if (b.isPresent()) {
				m_labelExist.setText(MessageFormat.format(Program.getLabel("Infos329"), Program.convertStringFromHTMLString(b.get().getNom())));
			} else {
			  m_labelExist.setText("");
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
		if (m_chooseCell != null) {
			m_chooseCell.setEnabled(enable && Program.hasComplexPlace());
		}
		m_end.setVisible(enable);
	}
	
	protected String getYear() {
		
		if (m_noYear.isSelected()) {
			return Bouteille.NON_VINTAGE;
		}
		
		String annee = m_year.getText();
		if (m_annee_auto.isSelected() && annee.length() == 2) {
			int n = Program.getCaveConfigInt(MyCellarSettings.ANNEE, 50);
			int siecle = Program.getCaveConfigInt(MyCellarSettings.SIECLE, 20);
			if(Program.safeParseInt(annee, -1) > n) {
				annee = (siecle - 1) + annee;
			} else {
				annee = siecle + annee;
			}
		}
		return annee;
	}
	
	protected final void setYearAuto() {
	  m_annee_auto.setText(MessageFormat.format(Program.getLabel("Infos117"), ((SIECLE + 1) * 100))); //"Annee 00 -> 2000");
    m_annee_auto.setSelected(Program.getCaveConfigBool(MyCellarSettings.ANNEE_AUTO, false));
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
		} else {
			m_half.setSelectedItem(MyCellarBottleContenance.getDefaultValue());
		}
	}
	
	/**
	 * preview_actionPerformed: Fonction pour visualiser un rangement.
	 *
	 * @param e ActionEvent
	 */
	private void preview_actionPerformed(ActionEvent e) {
		Debug("Previewing...");
		RangementUtils.putTabStock();
		final LinkedList<Rangement> rangements = new LinkedList<>();
		rangements.add((Rangement) m_lieu.getSelectedItem());
		MyXmlDom.writeRangements(Program.getPreviewXMLFileName(), rangements, false);
		Program.open( new File(Program.getPreviewXMLFileName()) );
		Debug("Previewing... End");
	}

	
	/**
	 * num_lieu_itemStateChanged: Fonction pour la liste des numeros de lieu.
	 *
	 * @param e ItemEvent
	 */
	private void num_lieu_itemStateChanged(ItemEvent e) {
		if (isListenersDisabled()) {
			return;
		}
		SwingUtilities.invokeLater(() -> {
			Debug("Num_lieu_itemStateChanging...");
			int num_select = m_num_lieu.getSelectedIndex();
			int lieu_select = m_lieu.getSelectedIndex();

			m_labelExist.setText("");

			if (num_select == 0) {
				m_line.setEnabled(false);
				m_column.setEnabled(false);
			}	else {
				m_line.setEnabled(true);
				Rangement rangement = m_lieu.getItemAt(lieu_select);
				if (!rangement.isCaisse()) {
					int nb_ligne = rangement.getNbLignes(num_select - 1);
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
	 * Mise a jour de la liste des rangements
	 */
	public void updateView() {
		if (!updateView) {
			return;
		}
		SwingUtilities.invokeLater(() -> {
			Debug("updateView...");
			updateView = false;
			m_lieu.removeAllItems();
			initPlaceCombo();
			m_half.removeAllItems();
			m_half.addItem("");
			for (String s : MyCellarBottleContenance.getList()) {
				m_half.addItem(s);
			}
			m_half.setSelectedItem(MyCellarBottleContenance.getDefaultValue());
			panelVignobles.updateList();
			Debug("updateView Done");
		});
	}

	protected void initPlaceCombo() {
		m_lieu.addItem(Program.EMPTY_PLACE);
		boolean complex = false;
		for (Rangement rangement : Program.getCave()) {
			m_lieu.addItem(rangement);
			if (!rangement.isCaisse()) {
				complex = true;
			}
		}
		m_chooseCell.setEnabled(complex);
	}

	protected void initColumnCombo() {
		int num_select = m_line.getSelectedIndex();
		int emplacement = m_num_lieu.getSelectedIndex();
		int lieu_select = m_lieu.getSelectedIndex();
		m_column.setEnabled(num_select != 0);
		int nb_col = 0;
		if (num_select > 0) {
			Rangement cave = m_lieu.getItemAt(lieu_select);
			nb_col = cave.getNbColonnes(emplacement - 1, num_select - 1);
		}
		m_column.removeAllItems();
		m_column.addItem("");
		for (int i = 1; i <= nb_col; i++) {
			m_column.addItem(Integer.toString(i));
		}
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
		for (int i=0; i<m_lieu.getItemCount(); i++) {
			if (rangement.equals(m_lieu.getItemAt(i))){
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
		for (int i = 1; i<= nbEmpl; i++) {
			m_num_lieu.addItem(Integer.toString(i));
		}
		for (int i = 1; i<= nbLine; i++) {
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
	
	protected void selectPlace(final Bouteille bottle) {
		Debug("selectPlaceWithBottle...");
		setListenersEnabled(false);
		m_num_lieu.removeAllItems();
		m_column.removeAllItems();
		m_line.removeAllItems();
		m_num_lieu.addItem("");
		m_line.addItem("");
		m_column.addItem("");
		boolean isCaisse = false;
		
		Rangement rangement = bottle.getRangement();
		if(rangement != null) {
    		for (int i=0; i<m_lieu.getItemCount(); i++) {
    			if (rangement.equals(m_lieu.getItemAt(i))){
    				m_lieu.setSelectedIndex(i);
    				break;
    			}
    		}
    		
    		int nbEmpl = rangement.getNbEmplacements();
    		isCaisse = rangement.isCaisse();
    		if(!isCaisse) {
    			for (int i = 1; i<= nbEmpl; i++) {
    				m_num_lieu.addItem(Integer.toString(i));
    			}
    			int nbLine = rangement.getNbLignes(bottle.getNumLieu() - 1);
    			int nbColumn = rangement.getNbColonnes(bottle.getNumLieu() - 1, bottle.getLigne() - 1);
    			for (int i = 1; i<= nbLine; i++) {
    				m_line.addItem(Integer.toString(i));
    			}
    			for (int i = 1; i<= nbColumn; i++) {
    				m_column.addItem(Integer.toString(i));
    			}
    			m_line.setEnabled(true);
    			m_column.setEnabled(true);
    			m_num_lieu.setSelectedIndex(bottle.getNumLieu());
    			m_line.setSelectedIndex(bottle.getLigne());
    			m_column.setSelectedIndex(bottle.getColonne());
    		} else {
    			int start = rangement.getStartCaisse();
    			for (int i = start; i< nbEmpl+start; i++) {
    				m_num_lieu.addItem(Integer.toString(i));
    			}
    			m_num_lieu.setSelectedIndex(bottle.getNumLieu() - start + 1);
    		}
    		m_num_lieu.setEnabled(true);
		}
		
		m_labelLine.setVisible(!isCaisse);
		m_labelColumn.setVisible(!isCaisse);
		m_line.setVisible(!isCaisse);
		m_column.setVisible(!isCaisse);
		setListenersEnabled(true);
		Debug("selectPlaceWithBottle... Done");
	}

	protected void clearValues() {
		name.setSelectedIndex(0);
		m_year.setText("");
		m_parker.setText("");
		m_price.setText("");
		m_maturity.setText("");
		m_lieu.setSelectedIndex(0);
		m_labelExist.setText("");
		m_nb_bottle.setValue(1);
		panelVignobles.resetCountrySelected();
	}
	
	protected boolean isListenersDisabled() {
		return !listenersEnabled;
	}

	protected void setListenersEnabled(boolean listenersEnabled) {
		this.listenersEnabled = listenersEnabled;
	}

	protected static void Debug(String s) {}

	protected abstract void lieu_itemStateChanged(ItemEvent e);

	protected abstract void line_itemStateChanged(ItemEvent e);

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
