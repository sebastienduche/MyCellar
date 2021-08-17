package mycellar.actions;

import mycellar.Program;
import mycellar.Start;
import mycellar.core.IPlace;

import javax.swing.AbstractAction;
import java.awt.event.ActionEvent;

public class ChooseCellAction extends AbstractAction {

  private static final long serialVersionUID = -6674616199012746620L;
  private final IPlace iPlace;

  public ChooseCellAction(IPlace iPlace) {
    super(Program.getLabel("AddVin.ChooseCell"));
    this.iPlace = iPlace;
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    Start.getInstance().openCellChooserPanel(iPlace);
  }
}
