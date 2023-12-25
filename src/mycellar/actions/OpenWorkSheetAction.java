package mycellar.actions;

import mycellar.MyCellarImage;
import mycellar.core.MyCellarObject;
import mycellar.core.text.LabelProperty;
import mycellar.core.uicomponents.MyCellarAction;
import mycellar.general.ProgramPanels;
import mycellar.showfile.ShowFile;

import javax.swing.SwingUtilities;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import static mycellar.core.text.MyCellarLabelManagement.getLabel;

public final class OpenWorkSheetAction extends MyCellarAction {

  private static final String LABEL = "ShowFile.Worksheet";
  private final List<MyCellarObject> bouteilles;

  public OpenWorkSheetAction() {
    this(null);
  }

  public OpenWorkSheetAction(List<MyCellarObject> list) {
    super(LABEL, LabelProperty.SINGLE, MyCellarImage.WORK);
    setDescriptionLabel(LABEL, LabelProperty.SINGLE);
    bouteilles = list != null ? list : new ArrayList<>();
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    SwingUtilities.invokeLater(() -> {
      final ShowFile showWorksheet = ProgramPanels.createShowWorksheet();
      showWorksheet.updateView();
      int tabIndex = ProgramPanels.findTab(MyCellarImage.WORK, null);
      final String label = getLabel(LABEL);
      if (tabIndex != -1) {
        ProgramPanels.setTitleAt(tabIndex, label);
      } else {
        ProgramPanels.addTab(label, MyCellarImage.WORK, showWorksheet);
      }

      showWorksheet.addToWorsheet(bouteilles);
    });
  }
}
