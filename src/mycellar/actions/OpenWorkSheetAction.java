package mycellar.actions;

import mycellar.Bouteille;
import mycellar.MyCellarImage;
import mycellar.core.uicomponents.MyCellarAction;
import mycellar.general.ProgramPanels;
import mycellar.showfile.WorksheetPanel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.Collections;
import java.util.List;

import static mycellar.core.text.MyCellarLabelManagement.getLabel;
import static mycellar.general.ResourceKey.SHOWFILE_WORKSHEET;

public final class OpenWorkSheetAction extends MyCellarAction {

  public OpenWorkSheetAction() {
    super(SHOWFILE_WORKSHEET, MyCellarImage.WORK);
    setDescriptionLabel(SHOWFILE_WORKSHEET);
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    open(Collections.emptyList());
  }

  public static void open(List<Bouteille> bottles) {
    SwingUtilities.invokeLater(() -> {
      final WorksheetPanel showWorksheet = ProgramPanels.createWorksheetPanel();
      showWorksheet.updateView();
      int tabIndex = ProgramPanels.findTab(MyCellarImage.WORK, null);
      final String label = getLabel(SHOWFILE_WORKSHEET);
      if (tabIndex != -1) {
        ProgramPanels.setTitleAt(tabIndex, label);
      } else {
        ProgramPanels.addTab(label, MyCellarImage.WORK, showWorksheet);
      }

      showWorksheet.addToWorksheet(bottles);
    });
  }
}
