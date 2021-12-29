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
      final AddVin addVin = ProgramPanels.createAddVin();
      addVin.setBottles(listToModify);

      int tabIndex = ProgramPanels.findTab(MyCellarImage.WINE);
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
