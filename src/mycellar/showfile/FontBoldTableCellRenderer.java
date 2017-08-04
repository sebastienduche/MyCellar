package mycellar.showfile;

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import mycellar.Program;

public class FontBoldTableCellRenderer extends DefaultTableCellRenderer {

	private static final long serialVersionUID = -7366533325659261460L;

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {

		Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus,
				row, column);
		
		c.setFont(Program.font_label_bold);
		return c;
	}
}
