package mycellar;

import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.text.MessageFormat;
import java.util.LinkedList;

import javax.swing.AbstractAction;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import mycellar.actions.OpenAddVinAction;
import mycellar.core.MyCellarButton;
import mycellar.core.MyCellarComboBox;
import mycellar.core.MyCellarLabel;
import net.miginfocom.swing.MigLayout;

/**
 * Titre : Cave à vin
 * Description : Votre description
 * Copyright : Copyright (c) 1998
 * Société : Seb Informatique
 * 
 * @author Sébastien Duché
 * @version 2.8
 * @since 03/08/17
 */

public class ShowHistory extends JPanel implements ITabListener {
	private MyCellarButton m_oSuppr = new MyCellarButton(new DeleteAction());
	private MyCellarButton restoreButton = new MyCellarButton(new RestoreAction());
	private MyCellarLabel m_oFilterLabel = new MyCellarLabel();
	private MyCellarComboBox<String> m_oFilterCbx = new MyCellarComboBox<String>();
	public JScrollPane m_oScroll = new JScrollPane();
	public JTable m_oTable;
	private TableHistoryValues tv;
	private JMenuItem ClearHistory = new JMenuItem(Program.getLabel("Infos352"));
	private JMenuItem ClearSomeHistory = new JMenuItem(Program.getLabel("Infos353"));
	//private MyCellarCheckBox m_oSelectAll = new MyCellarCheckBox();
	static final long serialVersionUID = 0601071;

	public ShowHistory() {
		Debug("Constructor");
		try {
			jbInit();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	void jbInit() throws Exception {
		m_oFilterLabel.setText(Program.getLabel("Infos350")); // Filtre
		/*m_oSelectAll.setText(Program.getLabel("Infos126")); // "Tout sélectionner");
		m_oSelectAll.setHorizontalAlignment(4);
		m_oSelectAll.setHorizontalTextPosition(2);
		m_oSelectAll.addActionListener((e) -> selectall_actionPerformed(e));*/

		m_oFilterCbx.addItem(Program.getLabel("Infos351"));
		m_oFilterCbx.addItem(Program.getLabel("Infos345"));
		m_oFilterCbx.addItem(Program.getLabel("Infos346"));
		m_oFilterCbx.addItem(Program.getLabel("Infos347"));
		m_oFilterCbx.addItemListener((e) -> filter_itemStateChanged(e));

		// Remplissage de la table
		tv = new TableHistoryValues(true);
		tv.setHistory(Program.getStorage().getHistory().getHistory());

		m_oTable = new JTable(tv);
		TableColumnModel tcm = m_oTable.getColumnModel();
		TableColumn tc1[] = new TableColumn[4];
		for (int w = 0; w < 4; w++) {
			tc1[w] = tcm.getColumn(w);
			tc1[w].setCellRenderer(new ToolTipRenderer());
			switch (w) {
			case 0:
				tc1[w].setMinWidth(30);
				break;
			case 1:
				tc1[w].setMinWidth(100);
				break;
			case 2:
				tc1[w].setMinWidth(100);
				break;
			case 3:
				tc1[w].setMinWidth(350);
				break;
			case 4:
				tc1[w].setMinWidth(100);
				break;
			}
		}
		TableColumn tc = tcm.getColumn(TableHistoryValues.SELECT);
		tc.setCellRenderer(new StateRenderer());
		tc.setCellEditor(new StateEditor());
		tc.setMinWidth(30);
		tc.setMaxWidth(30);
		tc = tcm.getColumn(TableHistoryValues.ACTION);
		tc.setCellRenderer(new StateButtonRenderer());
		tc.setCellEditor(new StateButtonEditor());
		tc.setMinWidth(100);
		tc.setMaxWidth(100);

		m_oScroll = new JScrollPane(m_oTable);

		ClearHistory.addActionListener((e) -> clearHistory_actionPerformed(e));
		ClearSomeHistory.addActionListener((e) -> clearSomeHistory_actionPerformed(e));

		setLayout(new MigLayout("", "grow", "[][grow][]"));
		add(m_oFilterLabel, "split 4");
		add(m_oFilterCbx);
		add(new MyCellarLabel(), "growx");
		add(restoreButton, "align right, wrap");
		add(m_oScroll, "grow, wrap");
		add(m_oSuppr, "center");
		Start.outils.add(ClearSomeHistory);
		Start.outils.add(ClearHistory);
	}

	/**
	 * filter_itemStateChanged: Filtre la liste de l'historique
	 * 
	 * @param e
	 *            ItemEvent
	 */
	void filter_itemStateChanged(ItemEvent e) {
		Debug("SetFilter");
		tv.SetFilter(m_oFilterCbx.getSelectedIndex() - 1);
	}

	/**
	 * Debug
	 * 
	 * @param sText
	 *            String
	 */
	public static void Debug(String sText) {
		Program.Debug("ShowHistory: " + sText);
	}

	/**
	 * clearHistory_actionPerformed: Vidage de l'historique
	 * 
	 * @param e
	 *            ActionEvent
	 */
	void clearHistory_actionPerformed(ActionEvent e) {

		Debug("Clearing all history...");
		if (JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(null, Program.getError("Error182"),
				Program.getLabel("Infos049"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE)) {
			Program.getStorage().clearHistory();
			tv.removeAll();
			Debug("History cleared");
		}
	}

	/**
	 * clearSomeHistory_actionPerformed: Vidage d'une partie de l'historique
	 * 
	 * @param e
	 *            ActionEvent
	 */
	void clearSomeHistory_actionPerformed(ActionEvent e) {

		Debug("Clearing some history");
		String titre = Program.getLabel("Infos358");
		String message2 = Program.getLabel("Infos359");
		String titre_properties[] = new String[4];
		titre_properties[0] = Program.getLabel("Infos354");
		titre_properties[1] = Program.getLabel("Infos356");
		titre_properties[2] = Program.getLabel("Infos355");
		titre_properties[3] = Program.getLabel("Infos357");
		String default_value[] = new String[4];
		String key_properties[] = new String[4];
		key_properties[0] = "TEMP_PROP";
		key_properties[1] = "TEMP_PROP";
		key_properties[2] = "TEMP_PROP";
		key_properties[3] = "TEMP_PROP";

		default_value[0] = "true";
		default_value[1] = "false";
		default_value[2] = "false";
		default_value[3] = "false";
		Program.putCaveConfigString("TEMP_PROP", "-1");
		String type_objet[] = { "MyCellarRadioButton", "MyCellarRadioButton", "MyCellarRadioButton", "MyCellarRadioButton" };
		MyOptions myoptions = new MyOptions(titre, "", message2, titre_properties, default_value, key_properties, type_objet, Program.getCaveConfig(), true);
		myoptions.setVisible(true);
		int nValue = Program.getCaveConfigInt("TEMP_PROP", -1);
		boolean b = Program.getStorage().clearHistory(nValue);
		if (b) {
			Debug("History Cleared.");
			m_oFilterCbx.setSelectedIndex(0);
			tv.setHistory(Program.getStorage().getHistory().getHistory());
		}
	}

	/**
	 * selectall_actionPerformed: Permet de sélectionner toutes les lignes de la
	 * JTable
	 * 
	 * @param e
	 *            ActionEvent
	 */
	/*void selectall_actionPerformed(ActionEvent e) {
		Debug("selectall_actionPerforming...");
		if (m_oSelectAll.isSelected()) {
			m_oSuppr.setEnabled(false);
			for (int i = 0; i < tv.getRowCount(); i++) {
				tv.setValueAt(new Boolean(true), i, TableHistoryValues.SELECT);
			}
			m_oSuppr.setEnabled(true);
		} else {
			m_oSuppr.setEnabled(false);
			for (int i = 0; i < tv.getRowCount(); i++) {
				tv.setValueAt(new Boolean(false), i, TableHistoryValues.SELECT);
			}
			m_oSuppr.setEnabled(true);
		}
	}*/

	@Override
	public boolean tabWillClose(TabEvent event) {
		return true;
	}

	@Override
	public void tabClosed() {
		Start.outils.remove(ClearSomeHistory);
		Start.outils.remove(ClearHistory);
		Start.updateMainPanel();
	}

	class RestoreAction extends AbstractAction {

		private static final long serialVersionUID = 4095399581910695568L;

		public RestoreAction() {
			super(Program.getLabel("ShowFile.Restore"), MyCellarImage.RESTORE);
			putValue(SHORT_DESCRIPTION, Program.getLabel("ShowFile.Restore"));
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			String erreur_txt1, erreur_txt2;
			LinkedList<Bouteille> toRestoreList = new LinkedList<Bouteille>();

			boolean nonExit = false;

			int max_row = tv.getRowCount();
			if (max_row != 0) {
				int row = 0;
				do {
					if (((Boolean) tv.getValueAt(row, TableHistoryValues.SELECT)).equals(Boolean.TRUE)) {
						if (tv.isBottleDeleted(row))
							toRestoreList.add(tv.getBottle(row));
						else
							nonExit = true;
					}
					row++;
				} while (row < max_row);
			}

			if (nonExit) {
				new Erreur(Program.getLabel("ShowHistory.CantRestoreNonDeleted"), true);
			}

			if (toRestoreList.size() == 0) {
				new Erreur(Program.getLabel("ShowFile.NoBottleToRestore"), Program.getLabel("ShowFile.SelectToRestore"), true);
			} else {
				if (toRestoreList.size() == 1) {
					erreur_txt1 = Program.getError("Error067"); // "1 vin sélectionné.");
					erreur_txt2 = Program.getLabel("ShowFile.RestoreOne");
				} else {
					erreur_txt1 = MessageFormat.format(Program.getError("Error130"), toRestoreList.size()); // vins
					// sélectionnés.");
					erreur_txt2 = Program.getLabel("ShowFile.RestoreSeveral");
				}
				if (JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(null, erreur_txt1 + " " + erreur_txt2, Program.getLabel("Infos049"),
						JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE)) {
					LinkedList<Bouteille> cantRestoreList = new LinkedList<Bouteille>();
					for (int i = 0; i < toRestoreList.size(); i++) {
						Bouteille b = (Bouteille) toRestoreList.get(i);
						Rangement r = Program.getCave(b.getEmplacement());
						if(r != null) {
							if (r.isCaisse()) {
								Program.getStorage().addHistory(History.ADD, b);
								Program.getStorage().addWine(b);
							} else {
								if (r.getBouteille(b.getNumLieu() - 1, b.getLigne() - 1, b.getColonne() - 1) == null) {
									Program.getStorage().addHistory(History.ADD, b);
									Program.getStorage().addWine(b);
								} else
									cantRestoreList.add(b);
							}
						}
						if(!cantRestoreList.contains(b)) {
							if(Program.getTrash().contains(b))
								Program.getTrash().remove(b);
						}
					}

					if (!cantRestoreList.isEmpty())
						modifyBottles(cantRestoreList);
				}
				/*for (int j = 0; j < Program.GetCaveLength(); j++)
					Program.getCave(j).putTabStock();*/
				RangementUtils.putTabStock();
				tv.setHistory(Program.getStorage().getHistory().getHistory());
			}
		}
	}

	private void modifyBottles(LinkedList<Bouteille> listToModify) {
		new OpenAddVinAction(listToModify).actionPerformed(null);
	}

	class DeleteAction extends AbstractAction {

		private static final long serialVersionUID = -1982193809982154836L;

		public DeleteAction() {
			super(Program.getLabel("Infos051"), MyCellarImage.DELETE);
			putValue(SHORT_DESCRIPTION, Program.getLabel("Infos051"));
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			Debug("Deleting lines");
			String erreur_txt1, erreur_txt2;// erreur_txt3, erreur_txt4;
			java.util.LinkedList<History> toDeleteList = new java.util.LinkedList<History>();

			try {
				int count = 0;
				int max_row = tv.getRowCount();
				int row = 0;
				do {
					if (tv.getValueAt(row, TableHistoryValues.SELECT).toString() == "true") {
						toDeleteList.add(tv.getData().get(row));
					}
					row++;
				} while (row < max_row);

				if (toDeleteList.isEmpty()) {
					// Aucune ligne sélectionnée "Veuillez sélectionner des lignes à supprimer.");
					new Erreur(Program.getError("Error184"), Program.getError("Error185"), true);
					Debug("ERROR: No lines selected");
				} else {
					if (toDeleteList.size() == 1) {
						erreur_txt1 = Program.getError("Error186"); // "1 ligne sélectionnée.");
						erreur_txt2 = Program.getError("Error188"); // "Voulez-vous la supprimer?");
					} else {
						erreur_txt1 = MessageFormat.format(Program.getError("Error187"), count); // lignes
						erreur_txt2 = Program.getError("Error131"); // "Voulez-vous les supprimer?");
					}
					Debug(count + " line(s) selected");
					if (JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(null, erreur_txt1 + " " + erreur_txt2, Program.getLabel("Infos049"),
							JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE)) {
						Debug("Deleting lines...");
						for (int i = 0; i < toDeleteList.size(); i++) {
							History b = (History) toDeleteList.get(i);
							Program.getStorage().removeHistory(b);
						}
						tv.setHistory(Program.getStorage().getHistory().getHistory());
					}
				}
			} catch (Exception exc) {
				Program.showException(exc);
			}
		}
	}

	public void refresh() {
		tv.setHistory(Program.getStorage().getHistory().getHistory());
	}

}
