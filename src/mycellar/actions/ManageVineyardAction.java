package mycellar.actions;

import mycellar.frame.MainFrame;

import javax.swing.AbstractAction;
import java.awt.event.ActionEvent;

public class ManageVineyardAction extends AbstractAction {
  @Override
  public void actionPerformed(ActionEvent e) {
    MainFrame.getInstance().openVineyardPanel();
  }
}
