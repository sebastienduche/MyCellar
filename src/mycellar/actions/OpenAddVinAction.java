package mycellar.actions;

import mycellar.AddVin;
import mycellar.MyCellarImage;
import mycellar.Program;
import mycellar.Start;
import mycellar.Utils;
import mycellar.core.LabelProperty;
import mycellar.core.MyCellarObject;
import mycellar.general.ProgramPanels;

import javax.swing.AbstractAction;
import javax.swing.SwingUtilities;
import java.awt.event.ActionEvent;
import java.util.List;

public class OpenAddVinAction extends AbstractAction {

  private static final long serialVersionUID = 6187152928186377148L;
  private final List<MyCellarObject> listToModify;

  public OpenAddVinAction(List<MyCellarObject> listToModify) {
    this.listToModify = listToModify;
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    SwingUtilities.invokeLater(() -> {
      if (ProgramPanels.getAddVin() == null) {
        final AddVin addVin = ProgramPanels.createAddVin();
        ProgramPanels.TABBED_PANE.addTab(Program.getLabel("OpenVin.modify1Item", LabelProperty.SINGLE), MyCellarImage.WINE, addVin);
        ProgramPanels.TABBED_PANE.setSelectedIndex(ProgramPanels.TABBED_PANE.getTabCount() - 1);
      }
      final AddVin addVin = ProgramPanels.getAddVin();
      addVin.setBottles(listToModify);

      int tabIndex = ProgramPanels.findTab(MyCellarImage.WINE);
      // Seconde verification
      if (tabIndex != -1) {
        tabIndex = ProgramPanels.TABBED_PANE.indexOfComponent(addVin);
      }
      if (tabIndex != -1) {
        ProgramPanels.TABBED_PANE.setTitleAt(tabIndex, Program.getLabel("OpenVin.modify1Item", LabelProperty.PLURAL));
        ProgramPanels.TABBED_PANE.setSelectedIndex(tabIndex);
      } else {
        ProgramPanels.TABBED_PANE.addTab(Program.getLabel("OpenVin.modify1Item", LabelProperty.PLURAL), MyCellarImage.WINE, addVin);
        ProgramPanels.TABBED_PANE.setSelectedIndex(ProgramPanels.TABBED_PANE.getTabCount() - 1);
      }

      Utils.addCloseButton(ProgramPanels.TABBED_PANE, addVin);
      Start.getInstance().updateMainPanel();
    });
  }
}
