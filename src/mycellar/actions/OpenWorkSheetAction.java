package mycellar.actions;

import mycellar.MyCellarImage;
import mycellar.core.IMyCellarObject;
import mycellar.core.uicomponents.MyCellarAction;
import mycellar.general.ProgramPanels;
import mycellar.showfile.WorksheetPanel;

import javax.swing.SwingUtilities;
import java.awt.event.ActionEvent;
import java.util.Collections;
import java.util.List;

import static mycellar.core.text.MyCellarLabelManagement.getLabel;
import static mycellar.general.ResourceKey.SHOWFILE_WORKSHEET;

public final class OpenWorkSheetAction extends MyCellarAction {

  private final List<IMyCellarObject> myCellarObjects;

  public OpenWorkSheetAction() {
    this(null);
  }

  public OpenWorkSheetAction(List<IMyCellarObject> list) {
    super(SHOWFILE_WORKSHEET, MyCellarImage.WORK);
    setDescriptionLabel(SHOWFILE_WORKSHEET);
    myCellarObjects = list != null ? list : Collections.emptyList();
  }

  @Override
  public void actionPerformed(ActionEvent e) {
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

      showWorksheet.addToWorksheet(myCellarObjects);
    });
  }
}
