package mycellar.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import mycellar.Program;
import mycellar.Start;

public class ManageVineyardAction extends AbstractAction {

	private static final long serialVersionUID = 8403205550584446018L;
	public ManageVineyardAction() {
		super(Program.getLabel("Infos165"));
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		Start.openVineyardPanel();
	}
}
