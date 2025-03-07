package mycellar.actions;

import mycellar.AddVin;
import mycellar.MyCellarImage;
import mycellar.core.MyCellarObject;
import mycellar.core.text.LabelProperty;
import mycellar.general.ProgramPanels;

import javax.swing.AbstractAction;
import javax.swing.SwingUtilities;
import java.awt.event.ActionEvent;
import java.util.List;

import static mycellar.core.text.MyCellarLabelManagement.getLabelWithProperty;

public class OpenAddVinAction extends AbstractAction {

  private final List<MyCellarObject> listToModify;

  public OpenAddVinAction(List<MyCellarObject> listToModify) {
    this.listToModify = listToModify;
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    SwingUtilities.invokeLater(() -> {
      final AddVin addVin = ProgramPanels.createAddVin();
      addVin.setBottles(listToModify);

      int tabIndex = ProgramPanels.findTab(MyCellarImage.WINE, addVin);
      final String label = getLabelWithProperty("OpenVin.Modify1Item", LabelProperty.PLURAL);
      if (tabIndex != -1) {
        ProgramPanels.setTitleAt(tabIndex, label);
      } else {
        ProgramPanels.addTab(label, MyCellarImage.WINE, addVin);
      }
    });
  }
}
