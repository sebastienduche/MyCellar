package mycellar.actions;

import mycellar.Bouteille;
import mycellar.MyCellarImage;
import mycellar.Program;
import mycellar.Start;
import mycellar.Utils;
import mycellar.general.ProgramPanels;
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
      if (ProgramPanels.getShowWorksheet() == null) {
        final ShowFile showWorksheet = ProgramPanels.createShowWorksheet();
        ProgramPanels.TABBED_PANE.addTab(Program.getLabel("ShowFile.Worksheet"), MyCellarImage.WORK, showWorksheet);
        ProgramPanels.TABBED_PANE.setSelectedIndex(ProgramPanels.TABBED_PANE.getTabCount() - 1);
      }
      final ShowFile showWorksheet = ProgramPanels.getShowWorksheet();
      showWorksheet.updateView();
      int tabIndex = ProgramPanels.findTab(MyCellarImage.WORK);
      if (tabIndex != -1) {
        ProgramPanels.TABBED_PANE.setTitleAt(tabIndex, Program.getLabel("ShowFile.Worksheet"));
        ProgramPanels.TABBED_PANE.setSelectedIndex(tabIndex);
      } else {
        ProgramPanels.TABBED_PANE.addTab(Program.getLabel("ShowFile.Worksheet"), MyCellarImage.WORK, showWorksheet);
        ProgramPanels.TABBED_PANE.setSelectedIndex(ProgramPanels.TABBED_PANE.getTabCount() - 1);
      }

      Utils.addCloseButton(ProgramPanels.TABBED_PANE, showWorksheet);
      Start.getInstance().updateMainPanel();
      showWorksheet.addWorkingBottles(bouteilles);
    });
  }
}
