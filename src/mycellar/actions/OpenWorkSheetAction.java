package mycellar.actions;

import mycellar.Bouteille;
import mycellar.MyCellarImage;
import mycellar.Program;
import mycellar.Start;
import mycellar.core.LabelProperty;
import mycellar.core.LabelType;
import mycellar.core.uicomponents.MyCellarAction;
import mycellar.general.ProgramPanels;
import mycellar.showfile.ShowFile;

import javax.swing.SwingUtilities;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

public final class OpenWorkSheetAction extends MyCellarAction {

  private static final long serialVersionUID = -2351197475699686315L;
  private static final String LABEL = "ShowFile.Worksheet";
  private final List<Bouteille> bouteilles;

  public OpenWorkSheetAction() {
    this(null);
  }

  public OpenWorkSheetAction(List<Bouteille> list) {
    super(LabelType.INFO_OTHER, LABEL, LabelProperty.SINGLE, MyCellarImage.WORK);
    setDescriptionLabelCode(LABEL);
    bouteilles = list != null ? list : new ArrayList<>();
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    SwingUtilities.invokeLater(() -> {
      final ShowFile showWorksheet = ProgramPanels.createShowWorksheet();
      showWorksheet.updateView();
      int tabIndex = ProgramPanels.findTab(MyCellarImage.WORK, null);
      final String label = Program.getLabel(LABEL);
      if (tabIndex != -1) {
        ProgramPanels.setTitleAt(tabIndex, label);
      } else {
        ProgramPanels.addTab(label, MyCellarImage.WORK, showWorksheet);
      }

      Start.getInstance().updateMainPanel();
      showWorksheet.addWorkingBottles(bouteilles);
    });
  }
}
