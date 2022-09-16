package mycellar.actions;

import mycellar.Start;
import mycellar.core.IPlacePosition;
import mycellar.core.text.MyCellarLabelManagement;

import javax.swing.AbstractAction;
import java.awt.event.ActionEvent;

public class ChooseCellAction extends AbstractAction {

  private static final long serialVersionUID = -6674616199012746620L;
  private final IPlacePosition iPlace;

  public ChooseCellAction(IPlacePosition iPlace) {
    super(MyCellarLabelManagement.getLabel("AddVin.ChooseCell"));
    this.iPlace = iPlace;
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    Start.getInstance().openCellChooserPanel(iPlace);
  }
}
