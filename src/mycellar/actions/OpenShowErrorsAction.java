package mycellar.actions;

import mycellar.MyCellarImage;
import mycellar.Program;
import mycellar.Start;
import mycellar.Utils;
import mycellar.showfile.ShowFile;
import mycellar.showfile.ShowFile.ShowType;

import javax.swing.AbstractAction;
import javax.swing.SwingUtilities;
import java.awt.event.ActionEvent;

public class OpenShowErrorsAction extends AbstractAction {

	private static final long serialVersionUID = -2556341758211178986L;

	public OpenShowErrorsAction() {
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		SwingUtilities.invokeLater(() -> {
			if(Program.showerrors == null) {
				Program.showerrors = new ShowFile(ShowType.ERROR);
				Program.tabbedPane.addTab(Program.getLabel("ShowFile.ErrorTitle"), MyCellarImage.ERROR, Program.showerrors);
				Program.tabbedPane.setSelectedIndex(Program.tabbedPane.getTabCount()-1);
			}
			int tabIndex = Program.findTab(MyCellarImage.ERROR);
			if(tabIndex != -1) {
				Program.tabbedPane.setTitleAt(tabIndex, Program.getLabel("ShowFile.ErrorTitle"));
				Program.tabbedPane.setSelectedIndex(tabIndex);
			}
			else {
				Program.tabbedPane.addTab(Program.getLabel("ShowFile.ErrorTitle"), MyCellarImage.ERROR, Program.showerrors);
				Program.tabbedPane.setSelectedIndex(Program.tabbedPane.getTabCount()-1);
			}
	
			Utils.addCloseButton(Program.tabbedPane, Program.showerrors);
			Start.getInstance().updateMainPanel();
		});
	}
}
