package mycellar;

import mycellar.core.MyCellarButton;
import mycellar.core.MyCellarComboBox;
import mycellar.core.MyCellarLabel;
import mycellar.core.datas.MyCellarBottleContenance;
import net.miginfocom.swing.MigLayout;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.LinkedList;

/**
 * <p>Titre : Cave à vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2003</p>
 * <p>Société : Seb Informatique</p>
 * @author Sébastien Duché
 * @version 0.7
 * @since 20/04/18
 */

public class ManageList extends JDialog {
	
	private static final long serialVersionUID = -116789055896509475L;
	private final ManageListTableValue model = new ManageListTableValue();
	private final MyCellarComboBox<String> defaultComboBox = new MyCellarComboBox<>();

	public ManageList()
	{
		setTitle(Program.getLabel("Infos400"));
		defaultComboBox.addItem("");
		for(String val : MyCellarBottleContenance.getList()) {
			defaultComboBox.addItem(val);
		}
		defaultComboBox.setSelectedItem(MyCellarBottleContenance.getDefaultValue());
		JTable table = new JTable(model);
		TableColumnModel tcm = table.getColumnModel();
	    TableColumn tc = tcm.getColumn(ManageListTableValue.ETAT);
	    tc.setCellRenderer(new StateRenderer());
	    tc.setCellEditor(new StateEditor());
	    tc.setMaxWidth(30);
	    tc.setMinWidth(30);
	    model.setValues(MyCellarBottleContenance.getList());
		MyCellarLabel labelTitle = new MyCellarLabel(Program.getLabel("Infos403"));
		MyCellarLabel labelDefault = new MyCellarLabel(Program.getLabel("Infos146"));
		MyCellarButton add = new MyCellarButton(Program.getLabel("Infos071"));
		MyCellarButton close = new MyCellarButton(Program.getLabel("Infos019"));
		MyCellarButton remove = new MyCellarButton(Program.getLabel("Infos051"));
		setLayout(new MigLayout("","grow","[]30px[grow]20px[]30px[]"));
		add(labelTitle,"center, wrap");
		add(new JScrollPane(table),"grow,wrap");
		add(labelDefault, "split 2");
		add(defaultComboBox, "wrap");
		add(add,"split 2, center");
		add(remove,"wrap");
		add(close,"center");
		labelTitle.setFont(Program.font_label_bold);
		
		close.addActionListener((e) -> close());
		add.addActionListener((e) -> {
			String s = JOptionPane.showInputDialog(null,Program.getLabel("Infos289"),Program.getLabel("Infos402"),JOptionPane.QUESTION_MESSAGE);
			if(null != s && !s.isEmpty()) {
				model.addValue(s);
				defaultComboBox.addItem(s);
			}
		});
		remove.addActionListener((e) -> {
			final LinkedList<Integer> list1 = model.getSelectedRows();
			if(!list1.isEmpty())
			{
				LinkedList<String> values = model.getSelectedValues();
				String label = Program.getLabel("Infos129");
				if(MyCellarBottleContenance.getList().size() > 1)
					label = Program.getLabel("Infos130");
				int resul = JOptionPane.showConfirmDialog(null, label);
				if(resul == JOptionPane.YES_OPTION) {
					model.removeValueAt(list1);
					for(String val: values)
						defaultComboBox.removeItem(val);
				}
			}
		});
		setModal(true);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	    setLocation( (screenSize.width - 300) / 2, (screenSize.height - 400) / 2);
		setSize(300,400);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setVisible(true);
	}
	
	private void close(){
		if(defaultComboBox.getSelectedIndex() != 0)
			MyCellarBottleContenance.setDefaultValue((String)defaultComboBox.getSelectedItem());
		dispose();
	}
}
