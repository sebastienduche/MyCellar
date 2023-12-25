package mycellar.actions;

import mycellar.MyCellarImage;
import mycellar.core.text.MyCellarLabelManagement;
import mycellar.general.ProgramPanels;
import mycellar.showfile.ShowFile;

import javax.swing.AbstractAction;
import javax.swing.SwingUtilities;
import java.awt.event.ActionEvent;

public class OpenShowErrorsAction extends AbstractAction {

  @Override
  public void actionPerformed(ActionEvent e) {
    SwingUtilities.invokeLater(() -> {
      final ShowFile showErrors = ProgramPanels.createShowErrors();
      showErrors.updateView();
      int tabIndex = ProgramPanels.findTab(MyCellarImage.ERROR, null);
      final String label = MyCellarLabelManagement.getLabel("ShowFile.ErrorTitle");
      if (tabIndex != -1) {
        ProgramPanels.setTitleAt(tabIndex, label);
      } else {
        ProgramPanels.addTab(label, MyCellarImage.ERROR, showErrors);
      }
    });
  }
}
