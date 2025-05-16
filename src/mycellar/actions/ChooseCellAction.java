package mycellar.actions;

import mycellar.core.IPlacePosition;
import mycellar.core.text.MyCellarLabelManagement;
import mycellar.frame.MainFrame;

import javax.swing.AbstractAction;
import java.awt.event.ActionEvent;

import static mycellar.general.ResourceKey.ADDVIN_CHOOSECELL;

public class ChooseCellAction extends AbstractAction {

  private final IPlacePosition iPlace;

  public ChooseCellAction(IPlacePosition iPlace) {
    super(MyCellarLabelManagement.getLabel(ADDVIN_CHOOSECELL));
    this.iPlace = iPlace;
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    MainFrame.getInstance().openCellChooserPanel(iPlace);
  }
}
