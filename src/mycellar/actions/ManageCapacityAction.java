package mycellar.actions;

import mycellar.Program;
import mycellar.Start;

import javax.swing.AbstractAction;
import java.awt.event.ActionEvent;

public class ManageCapacityAction extends AbstractAction {

  private static final long serialVersionUID = 8403205550584446018L;

  public ManageCapacityAction() {
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    Program.Debug("ManageCapacityAction: Manage Capacity...");
    Start.getInstance().openCapacityPanel();
    Program.Debug("ManageCapacityAction: Manage Capacity... Done");
  }
}
