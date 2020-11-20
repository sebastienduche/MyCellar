package mycellar.actions;

import mycellar.Bouteille;
import mycellar.MyCellarImage;
import mycellar.Program;
import mycellar.Start;
import mycellar.Utils;
import mycellar.showfile.ShowFile;

import javax.swing.AbstractAction;
import javax.swing.SwingUtilities;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

public final class OpenWorkSheetAction extends AbstractAction {

  private static final long serialVersionUID = -2351197475699686315L;
  private final List<Bouteille> bouteilles;

	public OpenWorkSheetAction() {
		this(null);
	}
	public OpenWorkSheetAction(List<Bouteille> list) {
		super(Program.getLabel("ShowFile.Worksheet"), MyCellarImage.WORK);
		putValue(SHORT_DESCRIPTION, Program.getLabel("ShowFile.Worksheet"));
		bouteilles = list != null ? list : new ArrayList<>();
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		SwingUtilities.invokeLater(() -> {
			if(Program.getShowWorksheet() == null) {
				final ShowFile showWorksheet = Program.createShowWorksheet();
				Program.TABBED_PANE.addTab(Program.getLabel("ShowFile.Worksheet"), MyCellarImage.WORK, showWorksheet);
				Program.TABBED_PANE.setSelectedIndex(Program.TABBED_PANE.getTabCount()-1);
			}
			final ShowFile showWorksheet = Program.getShowWorksheet();
			showWorksheet.updateView();
			int tabIndex = Program.findTab(MyCellarImage.WORK);
			if(tabIndex != -1) {
				Program.TABBED_PANE.setTitleAt(tabIndex, Program.getLabel("ShowFile.Worksheet"));
				Program.TABBED_PANE.setSelectedIndex(tabIndex);
			}
			else {
				Program.TABBED_PANE.addTab(Program.getLabel("ShowFile.Worksheet"), MyCellarImage.WORK, showWorksheet);
				Program.TABBED_PANE.setSelectedIndex(Program.TABBED_PANE.getTabCount()-1);
			}
	
			Utils.addCloseButton(Program.TABBED_PANE, showWorksheet);
			Start.getInstance().updateMainPanel();
			showWorksheet.addWorkingBottles(bouteilles);
		});
	}
}
