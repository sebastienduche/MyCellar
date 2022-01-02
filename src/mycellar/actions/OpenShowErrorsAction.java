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
      final ShowFile showErrors = ProgramPanels.createShowErrors();
      showErrors.updateView();
      int tabIndex = ProgramPanels.findTab(MyCellarImage.ERROR);
      final String label = Program.getLabel("ShowFile.ErrorTitle");
      if (tabIndex != -1) {
        ProgramPanels.TABBED_PANE.setTitleAt(tabIndex, label);
        ProgramPanels.TABBED_PANE.setSelectedIndex(tabIndex);
        ProgramPanels.updateTabLabel(tabIndex, label);
      } else {
        ProgramPanels.TABBED_PANE.addTab(label, MyCellarImage.ERROR, showErrors);
        final int index = ProgramPanels.TABBED_PANE.getTabCount() - 1;
        ProgramPanels.TABBED_PANE.setSelectedIndex(index);
        ProgramPanels.addTabLabel(index, label);
      }

      Utils.addCloseButtonToTab(showErrors);
      Start.getInstance().updateMainPanel();
    });
  }
}
