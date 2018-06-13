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
				Program.TABBED_PANE.addTab(Program.getLabel("ShowFile.ErrorTitle"), MyCellarImage.ERROR, Program.showerrors);
				Program.TABBED_PANE.setSelectedIndex(Program.TABBED_PANE.getTabCount()-1);
			}
			int tabIndex = Program.findTab(MyCellarImage.ERROR);
			if(tabIndex != -1) {
				Program.TABBED_PANE.setTitleAt(tabIndex, Program.getLabel("ShowFile.ErrorTitle"));
				Program.TABBED_PANE.setSelectedIndex(tabIndex);
			}
			else {
				Program.TABBED_PANE.addTab(Program.getLabel("ShowFile.ErrorTitle"), MyCellarImage.ERROR, Program.showerrors);
				Program.TABBED_PANE.setSelectedIndex(Program.TABBED_PANE.getTabCount()-1);
			}
	
			Utils.addCloseButton(Program.TABBED_PANE, Program.showerrors);
			Start.getInstance().updateMainPanel();
		});
	}
}
