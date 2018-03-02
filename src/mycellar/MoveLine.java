package mycellar;

import mycellar.core.MyCellarButton;
import mycellar.core.MyCellarComboBox;
import mycellar.core.MyCellarLabel;
import net.miginfocom.swing.MigLayout;

import javax.swing.JDialog;
import java.awt.Color;
import java.awt.event.ItemEvent;


/**
 * <p>Titre : Cave à Vins</p>
 * <p>Description : </p>
 * <p>Copyright : Copyright (c) 2005</p>
 * <p>Société : SebInformatique</p>
 * @author Sébastien Duché
 * @version 1.6
 * @since 02/03/18
 */

public class MoveLine extends JDialog {

	private final MyCellarLabel label_end = new MyCellarLabel();
	private final MyCellarComboBox<String> place_cbx = new MyCellarComboBox<>();
	private final MyCellarComboBox<String> num_place_cbx = new MyCellarComboBox<>();
	private final MyCellarComboBox<String> old_line_cbx = new MyCellarComboBox<>();
	private final MyCellarComboBox<String> new_line_cbx = new MyCellarComboBox<>();
	static final long serialVersionUID = 40508;

	MoveLine() {
		setAlwaysOnTop(true);
		setTitle(Program.getLabel("Infos363"));
		setLayout(new MigLayout("","[]","[]20px[]10px[][]10px[][]20px[]10px[]"));
		MyCellarLabel titre = new MyCellarLabel(Program.getLabel("Infos363"));
		titre.setForeground(Color.red);
		label_end.setForeground(Color.red);
		titre.setFont(Program.font_dialog);
		titre.setHorizontalAlignment(MyCellarLabel.CENTER);
		label_end.setHorizontalAlignment(MyCellarLabel.CENTER);

		MyCellarLabel label_title = new MyCellarLabel(Program.getLabel("Infos364"));
		MyCellarLabel label_place = new MyCellarLabel(Program.getLabel("Infos081"));
		MyCellarLabel label_num_place = new MyCellarLabel(Program.getLabel("Infos082"));
		MyCellarLabel label_old_line = new MyCellarLabel(Program.getLabel("Infos028"));
		MyCellarLabel label_new_line = new MyCellarLabel(Program.getLabel("Infos362"));

		MyCellarButton validate = new MyCellarButton(Program.getLabel("Infos315"));
		MyCellarButton cancel = new MyCellarButton(Program.getLabel("Infos019"));

		place_cbx.addItem("");
		for (Rangement r : Program.getCave()) {
			place_cbx.addItem(r.getNom());
		}
		num_place_cbx.setEnabled(false);
		old_line_cbx.setEnabled(false);
		new_line_cbx.setEnabled(false);

		validate.addActionListener((e) -> {
			int nOldSelected = old_line_cbx.getSelectedIndex();
			int nNewSelected = new_line_cbx.getSelectedIndex();
			if ( nNewSelected == 0 || nOldSelected == nNewSelected ) {
				Erreur.showSimpleErreur(Program.getError("Error192"));
				return;
			}
			int nNumLieu = num_place_cbx.getSelectedIndex();
			int nLieuSelected = place_cbx.getSelectedIndex();
			Rangement r = Program.getCave(nLieuSelected - 1);
			if (r != null) {
				int nBottle = r.getNbCaseUseLigne(nNumLieu - 1, nNewSelected - 1);
				int nNbBottle = r.getNbCaseUseLigne(nNumLieu - 1, nOldSelected - 1);

				int nOldColumnCount = r.getNbColonnes(nNumLieu - 1, nOldSelected - 1);
				int nNewColumnCount = r.getNbColonnes(nNumLieu - 1, nNewSelected - 1);
				if( nOldColumnCount > nNewColumnCount && nNbBottle > nNewColumnCount ) {
					Erreur.showSimpleErreur(Program.getError("Error194"));
					return;
				}
				if ( nNbBottle == 0 ) {
					Erreur.showSimpleErreur(Program.getError("Error195"));
				} else if ( nBottle > 0 ) {
					Erreur.showSimpleErreur(Program.getError("Error193"));
				}
				else {
					for( int i=1; i<=r.getNbColonnes(nNumLieu - 1, nOldSelected - 1); i++) {
						Bouteille bottle = r.getBouteille(nNumLieu - 1, nOldSelected - 1, i - 1);
						if( bottle != null ) {
							Program.getStorage().addHistory(History.MODIFY, bottle);
							r.moveLineWine(bottle, nNewSelected);
						}
					}
					label_end.setText(Program.getLabel("Infos366"));
				}
			}
		});
		cancel.addActionListener((e) -> close());
		place_cbx.addItemListener(this::lieu_itemStateChanged);
		num_place_cbx.addItemListener(this::num_lieu_itemStateChanged);
		old_line_cbx.addItemListener(this::old_line_itemStateChanged);

		try {
			add(titre,"align center, span 3, wrap");
			add(label_title,"span 3, wrap");
			add(label_place,"");
			add(label_num_place, "");
			add(label_old_line,"wrap");
			add(place_cbx,"");
			add(num_place_cbx,"");
			add(old_line_cbx,"wrap");
			add(label_new_line,"wrap");
			add(new_line_cbx,"wrap");
			add(label_end,"span 3,wrap");
			add(validate,"span 3, split, align center");
			add(cancel,"");

			setSize(320,280);
			setResizable(true);
			setIconImage(MyCellarImage.ICON.getImage());
			setLocationRelativeTo(null);
			setVisible(true);
		}
		catch (Exception ex) {
		}

	}

	/**
	 * close
	 */
	public void close() {
		dispose();
	}

	/**
	 * lieu_itemStateChanged: Fonction pour la liste des lieux.
	 *
	 * @param e ItemEvent
	 */
	private void lieu_itemStateChanged(ItemEvent e) {
		Debug("Lieu_itemStateChanging...");
		label_end.setText("");
		try {
			int nb_emplacement = 0;
			int lieu_select = place_cbx.getSelectedIndex();

			if (lieu_select == 0) {
				num_place_cbx.setEnabled(false);
				old_line_cbx.setEnabled(false);
				new_line_cbx.setEnabled(false);
			}
			else {
				num_place_cbx.setEnabled(true);
			}

			boolean bIsCaisse = false;
			Rangement r;
			if (lieu_select > 0 && (r = Program.getCave(lieu_select - 1)) != null) {
				nb_emplacement = r.getNbEmplacements();
				bIsCaisse = r.isCaisse();
			}
			if (bIsCaisse) { //Type caisse
				num_place_cbx.setEnabled(false);
				old_line_cbx.setEnabled(false);
				new_line_cbx.setEnabled(false);
			}

			num_place_cbx.removeAllItems();
			old_line_cbx.removeAllItems();
			new_line_cbx.removeAllItems();
			num_place_cbx.addItem("");
			for (int i = 1; i <= nb_emplacement; i++) {
				num_place_cbx.addItem(Integer.toString(i));
			}
			num_place_cbx.setVisible(true);
			old_line_cbx.setVisible(true);
			new_line_cbx.setVisible(true);
			repaint();
			setVisible(true);
		}
		catch (Exception a) {
			Program.showException(a);
		}
	}

	/**
	 * old_line_itemStateChanged: Fonction pour la liste des lignes.
	 *
	 * @param e ItemEvent
	 */
	private void old_line_itemStateChanged(ItemEvent e) {
		Debug("old_line_itemStateChanging...");
		label_end.setText("");
		int num_select = old_line_cbx.getSelectedIndex();
		new_line_cbx.setEnabled(num_select != 0);
	}

	/**
	 * num_lieu_itemStateChanged: Fonction pour la liste des numéros de lieu.
	 *
	 * @param e ItemEvent
	 */
	private void num_lieu_itemStateChanged(ItemEvent e) {
		Debug("Num_lieu_itemStateChanging...");
		try {
			label_end.setText("");
			int nb_ligne = 0;
			int num_select = num_place_cbx.getSelectedIndex();
			int lieu_select = place_cbx.getSelectedIndex();

			if (num_select == 0) {
				old_line_cbx.setEnabled(false);
				new_line_cbx.setEnabled(false);
			}
			else {
				old_line_cbx.setEnabled(true);
			}
			Rangement r;
			if (num_select > 0 && (r = Program.getCave(lieu_select - 1)) != null) { //!=0
				if (!r.isCaisse()) {
					nb_ligne = r.getNbLignes(num_select - 1);
					old_line_cbx.removeAllItems();
					new_line_cbx.removeAllItems();
					old_line_cbx.addItem("");
					new_line_cbx.addItem("");
					for (int i = 1; i <= nb_ligne; i++) {
						old_line_cbx.addItem(Integer.toString(i));
						new_line_cbx.addItem(Integer.toString(i));
					}
				}
				repaint();
				setVisible(true);
			}
		}
		catch (Exception a) {
			Program.showException(a);
		}
	}

	/**
	 * Debug
	 *
	 * @param sText String
	 */
	public static void Debug(String sText) {
		Program.Debug("MoveLine: " + sText );
	}
}
