package mycellar.actions;

import mycellar.Bouteille;
import mycellar.MyCellarImage;
import mycellar.Program;
import mycellar.Start;
import mycellar.Utils;
import mycellar.showfile.ShowFile;
import mycellar.showfile.ShowFile.ShowType;

import javax.swing.AbstractAction;
import javax.swing.SwingUtilities;
import java.awt.event.ActionEvent;
import java.util.List;

public class OpenWorkSheetAction extends AbstractAction {

	private final List<Bouteille> bouteilles;

	public OpenWorkSheetAction(List<Bouteille> list) {
		super(Program.getLabel("ShowFile.Worksheet"), MyCellarImage.WORK);
		putValue(SHORT_DESCRIPTION, Program.getLabel("ShowFile.Worksheet"));
		bouteilles = list;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		SwingUtilities.invokeLater(() -> {
			if(Program.showworksheet == null) {
				Program.showworksheet = new ShowFile(ShowType.WORK);
				Program.TABBED_PANE.addTab(Program.getLabel("ShowFile.Worksheet"), MyCellarImage.WORK, Program.showworksheet);
				Program.TABBED_PANE.setSelectedIndex(Program.TABBED_PANE.getTabCount()-1);
			} else {
				Program.showworksheet.refresh();
			}
			int tabIndex = Program.findTab(MyCellarImage.WORK);
			if(tabIndex != -1) {
				Program.TABBED_PANE.setTitleAt(tabIndex, Program.getLabel("ShowFile.Worksheet"));
				Program.TABBED_PANE.setSelectedIndex(tabIndex);
			}
			else {
				Program.TABBED_PANE.addTab(Program.getLabel("ShowFile.Worksheet"), MyCellarImage.WORK, Program.showworksheet);
				Program.TABBED_PANE.setSelectedIndex(Program.TABBED_PANE.getTabCount()-1);
			}
	
			Utils.addCloseButton(Program.TABBED_PANE, Program.showworksheet);
			Start.getInstance().updateMainPanel();
			Program.showworksheet.addWorkingBottles(bouteilles);
		});
	}
}
