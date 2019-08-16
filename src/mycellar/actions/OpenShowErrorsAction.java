package mycellar.actions;

import mycellar.MyCellarImage;
import mycellar.Program;
import mycellar.Start;
import mycellar.Utils;
import mycellar.showfile.ShowFile;

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
			if(Program.getShowErrors() == null) {
				final ShowFile showErrors = Program.createShowErrors();
				Program.TABBED_PANE.addTab(Program.getLabel("ShowFile.ErrorTitle"), MyCellarImage.ERROR, showErrors);
				Program.TABBED_PANE.setSelectedIndex(Program.TABBED_PANE.getTabCount()-1);
			}
			final ShowFile showErrors = Program.getShowErrors();
			showErrors.updateView();
			int tabIndex = Program.findTab(MyCellarImage.ERROR);
			if(tabIndex != -1) {
				Program.TABBED_PANE.setTitleAt(tabIndex, Program.getLabel("ShowFile.ErrorTitle"));
				Program.TABBED_PANE.setSelectedIndex(tabIndex);
			}
			else {
				Program.TABBED_PANE.addTab(Program.getLabel("ShowFile.ErrorTitle"), MyCellarImage.ERROR, showErrors);
				Program.TABBED_PANE.setSelectedIndex(Program.TABBED_PANE.getTabCount()-1);
			}
	
			Utils.addCloseButton(Program.TABBED_PANE, showErrors);
			Start.getInstance().updateMainPanel();
		});
	}
}
