package mycellar.actions;

import mycellar.Start;

import javax.swing.AbstractAction;
import java.awt.event.ActionEvent;

public class ManageVineyardAction extends AbstractAction {

  private static final long serialVersionUID = 8403205550584446018L;

  public ManageVineyardAction() {
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    Start.getInstance().openVineyardPanel();
  }
}
