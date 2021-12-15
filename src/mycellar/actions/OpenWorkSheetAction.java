package mycellar.actions;

import mycellar.Bouteille;
import mycellar.MyCellarImage;
import mycellar.Program;
import mycellar.Start;
import mycellar.Utils;
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
  private final List<Bouteille> bouteilles;
  private static final String LABEL = "ShowFile.Worksheet";

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
      if (ProgramPanels.getShowWorksheet() == null) {
        final ShowFile showWorksheet = ProgramPanels.createShowWorksheet();
        ProgramPanels.TABBED_PANE.addTab(Program.getLabel(LABEL), MyCellarImage.WORK, showWorksheet);
        ProgramPanels.TABBED_PANE.setSelectedIndex(ProgramPanels.TABBED_PANE.getTabCount() - 1);
      }
      final ShowFile showWorksheet = ProgramPanels.getShowWorksheet();
      showWorksheet.updateView();
      int tabIndex = ProgramPanels.findTab(MyCellarImage.WORK);
      if (tabIndex != -1) {
        ProgramPanels.TABBED_PANE.setTitleAt(tabIndex, Program.getLabel(LABEL));
        ProgramPanels.TABBED_PANE.setSelectedIndex(tabIndex);
      } else {
        ProgramPanels.TABBED_PANE.addTab(Program.getLabel(LABEL), MyCellarImage.WORK, showWorksheet);
        ProgramPanels.TABBED_PANE.setSelectedIndex(ProgramPanels.TABBED_PANE.getTabCount() - 1);
      }

      Utils.addCloseButton(ProgramPanels.TABBED_PANE, showWorksheet);
      Start.getInstance().updateMainPanel();
      showWorksheet.addWorkingBottles(bouteilles);
    });
  }
}
