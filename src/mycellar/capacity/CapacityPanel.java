package mycellar.capacity;

import mycellar.ITabListener;
import mycellar.MyCellarImage;
import mycellar.Program;
import mycellar.ProgramPanels;
import mycellar.Start;
import mycellar.StateButtonEditor;
import mycellar.StateButtonRenderer;
import mycellar.TabEvent;
import mycellar.core.IMyCellar;
import mycellar.core.IUpdatable;
import mycellar.core.LabelProperty;
import mycellar.core.MyCellarButton;
import mycellar.core.MyCellarComboBox;
import mycellar.core.MyCellarLabel;
import mycellar.core.datas.MyCellarBottleContenance;
import net.miginfocom.swing.MigLayout;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import static mycellar.core.LabelType.INFO;

/**
 * <p>Titre : Cave à vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2003</p>
 * <p>Société : Seb Informatique</p>
 * @author Sébastien Duché
 * @version 1.4
 * @since 19/11/20
 */

public final class CapacityPanel extends JPanel implements ITabListener, IMyCellar, IUpdatable {
	
	private static final long serialVersionUID = -116789055896509475L;
	private final CapacityTableModel model = new CapacityTableModel();
	private final MyCellarComboBox<String> defaultComboBox = new MyCellarComboBox<>();
	private final JTable table;

	public CapacityPanel() {
		defaultComboBox.addItem("");
		MyCellarBottleContenance.getList().forEach(defaultComboBox::addItem);
		defaultComboBox.setSelectedItem(MyCellarBottleContenance.getDefaultValue());
		table = new JTable(model);
		TableColumnModel tcm = table.getColumnModel();
		TableColumn tc = tcm.getColumn(CapacityTableModel.ETAT);
		tc.setCellRenderer(new StateButtonRenderer("", MyCellarImage.DELETE));
		tc.setCellEditor(new StateButtonEditor());
		tc.setMinWidth(25);
		tc.setMaxWidth(25);
		final MyCellarLabel labelDefault = new MyCellarLabel(INFO, "146");
		final MyCellarButton add = new MyCellarButton(INFO, "071", MyCellarImage.ADD);
		final MyCellarLabel info = new MyCellarLabel(INFO,"129", LabelProperty.THE_PLURAL.withCapital());
		setLayout(new MigLayout("","grow","30px[][][grow]20px[]30px[]"));
		add(info,"wrap");
		add(add,"wrap");
		add(new JScrollPane(table),"grow, wrap");
		add(labelDefault, "split 2");
		add(defaultComboBox, "gapleft 5, wrap");

		add.addActionListener((e) -> add());
	}

	private void add() {
		String s = Program.toCleanString(JOptionPane.showInputDialog(Start.getInstance(), Program.getLabel("Infos289"), Program.getLabel("Infos402"), JOptionPane.QUESTION_MESSAGE));
		if (!s.isEmpty()) {
			Program.setModified();
			model.addValue(s);
			defaultComboBox.addItem(s);
		}
		ProgramPanels.updateAllPanels();
	}

	@Override
	public boolean tabWillClose(TabEvent event) {
		if (table.isEditing()) {
			table.getCellEditor().cancelCellEditing();
		}
		if (defaultComboBox.getSelectedIndex() != 0) {
			MyCellarBottleContenance.setDefaultValue((String) defaultComboBox.getSelectedItem());
		}
		if (model.isModify()) {
			Program.setModified();
		}
		return true;
	}

	@Override
	public void tabClosed() {
		model.setModify(false);
		Start.getInstance().updateMainPanel();
	}

	@Override
	public void setUpdateView() {

	}

	@Override
	public void updateView() {
		SwingUtilities.invokeLater(() -> {
			defaultComboBox.removeAllItems();
			defaultComboBox.addItem("");
			MyCellarBottleContenance.getList().forEach(defaultComboBox::addItem);
			defaultComboBox.setSelectedItem(MyCellarBottleContenance.getDefaultValue());
		});
	}
}
