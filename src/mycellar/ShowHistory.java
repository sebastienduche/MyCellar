package mycellar;

import mycellar.core.IMyCellar;
import mycellar.core.LabelType;
import mycellar.core.MyCellarButton;
import mycellar.core.MyCellarComboBox;
import mycellar.core.MyCellarLabel;
import net.miginfocom.swing.MigLayout;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableRowSorter;

import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;

/**
 * Titre : Cave &agrave; vin
 * Description : Votre description
 * Copyright : Copyright (c) 1998
 * Soci&eacute;t&eacute; : Seb Informatique
 *
 * @author S&eacute;bastien Duch&eacute;
 * @version 3.9
 * @since 29/08/20
 */

public class ShowHistory extends JPanel implements ITabListener, IMyCellar {

	private static final long serialVersionUID = 4778721795659106312L;
	private final MyCellarComboBox<String> filterCbx = new MyCellarComboBox<>();
	private final TableHistoryValues tv;

	@SuppressWarnings("deprecation")
	public ShowHistory() {
		Debug("Constructor");
		MyCellarLabel filterLabel = new MyCellarLabel(LabelType.INFO, "350"); // Filtre
		filterCbx.addItem(Program.getLabel("Infos351"));
		filterCbx.addItem(Program.getLabel("Infos345"));
		filterCbx.addItem(Program.getLabel("Infos346"));
		filterCbx.addItem(Program.getLabel("Infos347"));
		filterCbx.addItem(Program.getLabel("History.Validated"));
		filterCbx.addItem(Program.getLabel("History.ToCheck"));
		filterCbx.addItemListener(this::filter_itemStateChanged);

		// Remplissage de la table
		tv = new TableHistoryValues(true);
		tv.setHistory(Program.getHistory());

		JTable table = new JTable(tv);
		TableColumnModel tcm = table.getColumnModel();
		TableColumn[] tc1 = new TableColumn[4];
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

		TableRowSorter<TableHistoryValues> sorter = new TableRowSorter<>(tv);
		sorter.setComparator(1, new Comparator<String>() {

      @Override
      public int compare(String o1, String o2) {
        if (o1 == null || o2 == null) {
          return 1;
        }
        if (o1.isBlank() || o2.isBlank()
            || !o1.contains("/") || !o2.contains("/")) {
          return o1.compareTo(o2);
        }
        
        Date date1 = null;
        Date date2 = null;
        try {
          SimpleDateFormat parser = new SimpleDateFormat("dd/MM/yyyy");
          date1 = parser.parse(o1);
          date2 = parser.parse(o2);
        } catch (ParseException e) {}
        
        if (date1 == null || date2 == null) {
          return 1;
        }
        return date1.compareTo(date2);
      }
		  
		});
		table.setRowSorter(sorter);

		setLayout(new MigLayout("", "grow", "[][grow][]"));
		add(filterLabel, "split 5");
		add(filterCbx);
		add(new MyCellarButton(LabelType.INFO_OTHER, "ShowHistory.ClearHistory", new ClearHistoryAction()), "gapleft 10px");
		add(new MyCellarLabel(), "growx");
		add(new MyCellarButton(LabelType.INFO_OTHER, "ShowFile.Restore", new RestoreAction()), "align right, wrap");
		add(new JScrollPane(table), "grow, wrap");
		add(new MyCellarButton(LabelType.INFO, "051", new DeleteAction()), "center");
	}

	/**
	 * filter_itemStateChanged: Filtre la liste de l'historique
	 * 
	 * @param e: ItemEvent
	 */
	private void filter_itemStateChanged(ItemEvent e) {
		Debug("SetFilter");
		tv.SetFilter(filterCbx.getSelectedIndex() - 1);
	}

	private static void Debug(String sText) {
		Program.Debug("ShowHistory: " + sText);
	}

	@Override
	public boolean tabWillClose(TabEvent event) {
		return true;
	}

	@Override
	public void tabClosed() {
		Start.getInstance().updateMainPanel();
	}

	private class RestoreAction extends AbstractAction {

		private static final long serialVersionUID = 4095399581910695568L;

		private RestoreAction() {
			super("", MyCellarImage.RESTORE);
			putValue(SHORT_DESCRIPTION, Program.getLabel("ShowFile.Restore"));
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			LinkedList<Bouteille> toRestoreList = new LinkedList<>();

			boolean nonExit = false;

			int max_row = tv.getRowCount();
			if (max_row != 0) {
				int row = 0;
				do {
					if (Boolean.TRUE.equals(tv.getValueAt(row, TableHistoryValues.SELECT))) {
						if (tv.isBottleDeleted(row))
							toRestoreList.add(tv.getBottle(row));
						else {
							nonExit = true;
						}
					}
					row++;
				} while (row < max_row);
			}

			if (nonExit) {
				Erreur.showSimpleErreur(Program.getLabel("ShowHistory.CantRestoreNonDeleted"), true);
				return;
			}

			if (toRestoreList.isEmpty()) {
				Erreur.showSimpleErreur(Program.getLabel("ShowFile.NoBottleToRestore"), Program.getLabel("ShowFile.SelectToRestore"), true);
			} else {
				String erreur_txt1, erreur_txt2;
				if (toRestoreList.size() == 1) {
					erreur_txt1 = Program.getError("Error067"); // "1 vin selectionne.");
					erreur_txt2 = Program.getLabel("ShowFile.RestoreOne");
				} else {
					erreur_txt1 = MessageFormat.format(Program.getError("Error130"), toRestoreList.size()); // vins selectionnes.");
					erreur_txt2 = Program.getLabel("ShowFile.RestoreSeveral");
				}
				if (JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(Start.getInstance(), erreur_txt1 + " " + erreur_txt2, Program.getLabel("Infos049"),
						JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE)) {
					LinkedList<Bouteille> cantRestoreList = new LinkedList<>();
					for (Bouteille b : toRestoreList) {
						Rangement r = Program.getCave(b.getEmplacement());
						if (r != null) {
							if (r.isCaisse()) {
								Program.getStorage().addHistory(History.ADD, b);
								Program.getStorage().addWine(b);
							} else {
								if (r.canAddBottle(b.getNumLieu() - 1, b.getLigne() - 1, b.getColonne() - 1)) {
									Program.getStorage().addHistory(History.ADD, b);
									Program.getStorage().addWine(b);
								} else {
									cantRestoreList.add(b);
								}
							}
						}
						if (!cantRestoreList.contains(b)) {
							Program.getTrash().remove(b);
						}
					}

					if (!cantRestoreList.isEmpty()) {
						Program.modifyBottles(cantRestoreList);
					}
				}
				RangementUtils.putTabStock();
				tv.setHistory(Program.getHistory());
			}
		}
	}

	class DeleteAction extends AbstractAction {

		private static final long serialVersionUID = -1982193809982154836L;

		private DeleteAction() {
			super("", MyCellarImage.DELETE);
			putValue(SHORT_DESCRIPTION, Program.getLabel("Infos051"));
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			Debug("Deleting lines");

			try {
				int max_row = tv.getRowCount();
				int row = 0;
				LinkedList<History> toDeleteList = new LinkedList<>();
				do {
					if (tv.getValueAt(row, TableHistoryValues.SELECT).equals(Boolean.TRUE)) {
						toDeleteList.add(tv.getData().get(row));
					}
					row++;
				} while (row < max_row);

				if (toDeleteList.isEmpty()) {
					// Aucune ligne selectionnee "Veuillez selectionner des lignes a supprimer.");
					Erreur.showSimpleErreur(Program.getError("Error184"), Program.getError("Error185"), true);
					Debug("ERROR: No lines selected");
				} else {
					String erreur_txt1, erreur_txt2;// erreur_txt3, erreur_txt4;
					if (toDeleteList.size() == 1) {
						erreur_txt1 = Program.getError("Error186"); // "1 ligne selectionnee.");
						erreur_txt2 = Program.getError("Error188"); // "Voulez-vous la supprimer?");
					} else {
						erreur_txt1 = MessageFormat.format(Program.getError("Error187"), toDeleteList.size()); // lignes
						erreur_txt2 = Program.getError("Error131"); // "Voulez-vous les supprimer?");
					}
					Debug(toDeleteList.size() + " line(s) selected");
					if (JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(Start.getInstance(), erreur_txt1 + " " + erreur_txt2, Program.getLabel("Infos049"),
							JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE)) {
						Debug("Deleting lines...");
						for (History b : toDeleteList) {
							Program.getStorage().removeHistory(b);
						}
						tv.setHistory(Program.getHistory());
					}
				}
			} catch (Exception e) {
				Program.showException(e);
			}
		}
	}
	
	class ClearHistoryAction extends AbstractAction {

		private static final long serialVersionUID = 3079501619032347868L;

		private ClearHistoryAction() {
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			int filter = filterCbx.getSelectedIndex() - 1;
			Program.getStorage().clearHistory(filter);
			filterCbx.setSelectedIndex(0);
			refresh();
		}
	}

	void refresh() {
		tv.setHistory(Program.getHistory());
	}

}
