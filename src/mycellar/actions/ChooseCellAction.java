package mycellar.actions;

import mycellar.Program;
import mycellar.Start;
import mycellar.core.IAddVin;

import javax.swing.AbstractAction;
import java.awt.event.ActionEvent;

public class ChooseCellAction extends AbstractAction {

	private static final long serialVersionUID = -6674616199012746620L;
	private final IAddVin addvin;
	public ChooseCellAction(IAddVin addvin) {
		super(Program.getLabel("AddVin.ChooseCell"));
		this.addvin = addvin;
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		Start.getInstance().openCellChooserPanel(addvin);
	}
}
