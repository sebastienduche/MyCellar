package mycellar.capacity;

import mycellar.ITabListener;
import mycellar.MyCellarImage;
import mycellar.Program;
import mycellar.Start;
import mycellar.StateEditor;
import mycellar.StateRenderer;
import mycellar.TabEvent;
import mycellar.core.IMyCellar;
import mycellar.core.MyCellarButton;
import mycellar.core.MyCellarComboBox;
import mycellar.core.MyCellarLabel;
import mycellar.core.datas.MyCellarBottleContenance;
import net.miginfocom.swing.MigLayout;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import java.util.LinkedList;

import static mycellar.core.LabelType.INFO;

/**
 * <p>Titre : Cave à vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2003</p>
 * <p>Société : Seb Informatique</p>
 * @author Sébastien Duché
 * @version 1.2
 * @since 17/11/20
 */

public final class CapacityPanel extends JPanel implements ITabListener, IMyCellar {
	
	private static final long serialVersionUID = -116789055896509475L;
	private final CapacityTableModel model = new CapacityTableModel();
	private final MyCellarComboBox<String> defaultComboBox = new MyCellarComboBox<>();
	private boolean modified;

	public CapacityPanel() {
		defaultComboBox.addItem("");
		MyCellarBottleContenance.getList().forEach(defaultComboBox::addItem);
		defaultComboBox.setSelectedItem(MyCellarBottleContenance.getDefaultValue());
		JTable table = new JTable(model);
		TableColumnModel tcm = table.getColumnModel();
		TableColumn tc = tcm.getColumn(CapacityTableModel.ETAT);
		tc.setCellRenderer(new StateRenderer());
		tc.setCellEditor(new StateEditor());
		tc.setMaxWidth(30);
		tc.setMinWidth(30);
		final MyCellarLabel labelDefault = new MyCellarLabel(INFO, "146");
		final MyCellarButton add = new MyCellarButton(INFO, "071", MyCellarImage.ADD);
		final MyCellarButton remove = new MyCellarButton(INFO,"051", MyCellarImage.DELETE);
		setLayout(new MigLayout("","grow","30px[grow]20px[]30px[]"));
		add(new JScrollPane(table),"grow, wrap");
		add(labelDefault, "split 2");
		add(defaultComboBox, "gapleft 5, wrap");
		add(add,"split 2, center");
		add(remove,"wrap");

		add.addActionListener((e) -> add());
		remove.addActionListener((e) -> remove());
	}

	private void remove() {
		final LinkedList<Integer> list1 = model.getSelectedRows();
		if (!list1.isEmpty()) {
			LinkedList<String> values = model.getSelectedValues();
			String label = Program.getLabel("Infos129");
			if (values.size() > 1) {
				label = Program.getLabel("Infos130");
			}
			int resul = JOptionPane.showConfirmDialog(Start.getInstance(), label, Program.getLabel("Infos049"), JOptionPane.YES_NO_OPTION);
			if (resul == JOptionPane.YES_OPTION) {
				modified = true;
				model.removeValueAt(list1);
				for (String val: values) {
					defaultComboBox.removeItem(val);
				}
			}
		}
	}

	private void add() {
		String s = JOptionPane.showInputDialog(Start.getInstance(), Program.getLabel("Infos289"),Program.getLabel("Infos402"),JOptionPane.QUESTION_MESSAGE);
		if (null != s && !s.isEmpty()) {
			modified = true;
			model.addValue(s);
			defaultComboBox.addItem(s);
		}
	}

	@Override
	public boolean tabWillClose(TabEvent event) {
		if (defaultComboBox.getSelectedIndex() != 0) {
			MyCellarBottleContenance.setDefaultValue((String) defaultComboBox.getSelectedItem());
		}
		if (modified) {
			if (Program.getAddVin() != null) {
				Program.getAddVin().setUpdateView();
				Program.getAddVin().updateView();
			}
		}
		return true;
	}

	@Override
	public void tabClosed() {
		modified = false;
		Start.getInstance().updateMainPanel();
	}
}
