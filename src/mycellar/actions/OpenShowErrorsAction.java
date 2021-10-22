package mycellar.actions;

import mycellar.MyCellarImage;
import mycellar.Program;
import mycellar.Start;
import mycellar.Utils;
import mycellar.general.ProgramPanels;
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
      if (ProgramPanels.getShowErrors() == null) {
        final ShowFile showErrors = ProgramPanels.createShowErrors();
        ProgramPanels.TABBED_PANE.addTab(Program.getLabel("ShowFile.ErrorTitle"), MyCellarImage.ERROR, showErrors);
        ProgramPanels.TABBED_PANE.setSelectedIndex(ProgramPanels.TABBED_PANE.getTabCount() - 1);
      }
      final ShowFile showErrors = ProgramPanels.getShowErrors();
      showErrors.updateView();
      int tabIndex = ProgramPanels.findTab(MyCellarImage.ERROR);
      if (tabIndex != -1) {
        ProgramPanels.TABBED_PANE.setTitleAt(tabIndex, Program.getLabel("ShowFile.ErrorTitle"));
        ProgramPanels.TABBED_PANE.setSelectedIndex(tabIndex);
      } else {
        ProgramPanels.TABBED_PANE.addTab(Program.getLabel("ShowFile.ErrorTitle"), MyCellarImage.ERROR, showErrors);
        ProgramPanels.TABBED_PANE.setSelectedIndex(ProgramPanels.TABBED_PANE.getTabCount() - 1);
      }

      Utils.addCloseButton(ProgramPanels.TABBED_PANE, showErrors);
      Start.getInstance().updateMainPanel();
    });
  }
}
