package mycellar;

import java.util.LinkedList;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import mycellar.core.MyCellarButton;
import mycellar.core.MyCellarComboBox;
import mycellar.core.MyCellarLabel;
import net.miginfocom.swing.MigLayout;

/**
 * <p>Titre : Cave à vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2003</p>
 * <p>Société : Seb Informatique</p>
 * @author Sébastien Duché
 * @version 0.5
 * @since 13/11/16
 */

public class ManageList extends JDialog {
	
	private static final long serialVersionUID = -116789055896509475L;
	private MyCellarLabel labelTitle = new MyCellarLabel(Program.getLabel("Infos403"));
	private MyCellarLabel labelDefault = new MyCellarLabel(Program.getLabel("Infos146"));
	private MyCellarButton add = new MyCellarButton(Program.getLabel("Infos071"));
	private MyCellarButton close = new MyCellarButton(Program.getLabel("Infos019"));
	private MyCellarButton remove = new MyCellarButton(Program.getLabel("Infos051"));
	private JTable table;
	private ManageListTableValue model = new ManageListTableValue();
	private MyCellarComboBox<String> defaultComboBox = new MyCellarComboBox<String>();

	public ManageList(LinkedList<String> list, String title)
	{
		this.setTitle(title);
		defaultComboBox.addItem("");
		for(String val : list) {
			defaultComboBox.addItem(val);
		}
		defaultComboBox.setSelectedItem(Program.defaut_half);
		table = new JTable(model);
		TableColumnModel tcm = table.getColumnModel();
	    TableColumn tc = tcm.getColumn(ManageListTableValue.ETAT);
	    tc.setCellRenderer(new StateRenderer());
	    tc.setCellEditor(new StateEditor());
	    tc.setMaxWidth(30);
	    tc.setMinWidth(30);
	    model.setValues(list);
		this.setLayout(new MigLayout("","grow","[]30px[grow]20px[]30px[]"));
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
				if(list.size() > 1)
					label = Program.getLabel("Infos130");
				int resul = JOptionPane.showConfirmDialog(null, label);
				if(resul == JOptionPane.YES_OPTION) {
					model.removeValueAt(list1);
					for(String val: values)
						defaultComboBox.removeItem(val);
				}
			}
		});
		this.setModal(true);
		java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
	    this.setLocation( (screenSize.width - 300) / 2, (screenSize.height - 400) / 2);
		this.setSize(300,400);
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		this.setVisible(true);
	}
	
	private void close(){
		if(defaultComboBox.getSelectedIndex() != 0)
			Program.defaut_half = (String)defaultComboBox.getSelectedItem();
		this.dispose();
	}
}
