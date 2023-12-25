package mycellar.actions;

import mycellar.Program;
import mycellar.frame.MainFrame;

import javax.swing.AbstractAction;
import java.awt.event.ActionEvent;

public class ManageCapacityAction extends AbstractAction {
  
  @Override
  public void actionPerformed(ActionEvent e) {
    Program.Debug("ManageCapacityAction: Manage Capacity...");
    MainFrame.openCapacityPanel();
    Program.Debug("ManageCapacityAction: Manage Capacity... Done");
  }
}
