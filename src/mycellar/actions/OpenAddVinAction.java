package mycellar.actions;

import mycellar.AddVin;
import mycellar.MyCellarImage;
import mycellar.core.IMyCellarObject;
import mycellar.general.ProgramPanels;

import javax.swing.SwingUtilities;
import java.util.List;

import static mycellar.core.text.MyCellarLabelManagement.getLabel;
import static mycellar.general.ResourceKey.OPENVIN_MODIFY1ITEM;
import static mycellar.general.ResourceKey.OPENVIN_MODIFYNITEM;

public class OpenAddVinAction {

  public static void open(List<IMyCellarObject> listToModify) {
    if (listToModify == null || listToModify.isEmpty()) {
      return;
    }
    if (listToModify.size() == 1) {
      ProgramPanels.showBottle(listToModify.getFirst(), true);
    } else {
      SwingUtilities.invokeLater(() -> {
        final AddVin addVin = ProgramPanels.createAddVin();
        addVin.setBottles(listToModify);

        int tabIndex = ProgramPanels.findTab(MyCellarImage.WINE, addVin);
        final String label = getLabel(listToModify.size() > 1 ? OPENVIN_MODIFYNITEM : OPENVIN_MODIFY1ITEM);
        if (tabIndex != -1) {
          ProgramPanels.setTitleAt(tabIndex, label);
        } else {
          ProgramPanels.addTab(label, MyCellarImage.WINE, addVin);
        }
      });
    }
  }
}
