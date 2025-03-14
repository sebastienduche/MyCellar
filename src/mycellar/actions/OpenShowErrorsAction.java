package mycellar.actions;

import mycellar.MyCellarImage;
import mycellar.core.text.MyCellarLabelManagement;
import mycellar.general.ProgramPanels;
import mycellar.showfile.ErrorShowPanel;

import javax.swing.AbstractAction;
import javax.swing.SwingUtilities;
import java.awt.event.ActionEvent;

import static mycellar.general.ResourceKey.SHOWFILE_ERRORTITLE;

public class OpenShowErrorsAction extends AbstractAction {

  @Override
  public void actionPerformed(ActionEvent e) {
    SwingUtilities.invokeLater(() -> {
      final ErrorShowPanel showErrors = ProgramPanels.createShowErrors();
      showErrors.updateView();
      int tabIndex = ProgramPanels.findTab(MyCellarImage.ERROR, null);
      final String label = MyCellarLabelManagement.getLabel(SHOWFILE_ERRORTITLE);
      if (tabIndex != -1) {
        ProgramPanels.setTitleAt(tabIndex, label);
      } else {
        ProgramPanels.addTab(label, MyCellarImage.ERROR, showErrors);
      }
    });
  }
}
