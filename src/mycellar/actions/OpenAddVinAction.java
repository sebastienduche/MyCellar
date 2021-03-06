package mycellar.actions;

import mycellar.AddVin;
import mycellar.Bouteille;
import mycellar.MyCellarImage;
import mycellar.Program;
import mycellar.Start;
import mycellar.Utils;
import mycellar.core.LabelProperty;

import java.awt.event.ActionEvent;
import java.util.LinkedList;

import javax.swing.AbstractAction;
import javax.swing.SwingUtilities;

public class OpenAddVinAction extends AbstractAction {

	private static final long serialVersionUID = 6187152928186377148L;
	private final LinkedList<Bouteille> listToModify;
	
	public OpenAddVinAction(LinkedList<Bouteille> listToModify) {
		this.listToModify = listToModify;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		SwingUtilities.invokeLater(() -> {
			if (Program.getAddVin() == null) {
				final AddVin addVin = Program.createAddVin();
				Program.TABBED_PANE.addTab(Program.getLabel("OpenVin.modify1Item", LabelProperty.SINGLE), MyCellarImage.WINE, addVin);
				Program.TABBED_PANE.setSelectedIndex(Program.TABBED_PANE.getTabCount()-1);
			}
			final AddVin addVin = Program.getAddVin();
			addVin.setBottles(listToModify);
	
			int tabIndex = Program.findTab(MyCellarImage.WINE);
			// Seconde verification
			if (tabIndex != -1) {
				tabIndex = Program.TABBED_PANE.indexOfComponent(addVin);
			}
			if (tabIndex != -1) {
				Program.TABBED_PANE.setTitleAt(tabIndex, Program.getLabel("OpenVin.modify1Item", LabelProperty.PLURAL));
				Program.TABBED_PANE.setSelectedIndex(tabIndex);
			} else {
				Program.TABBED_PANE.addTab(Program.getLabel("OpenVin.modify1Item", LabelProperty.PLURAL), MyCellarImage.WINE, addVin);
				Program.TABBED_PANE.setSelectedIndex(Program.TABBED_PANE.getTabCount()-1);
			}
	
			Utils.addCloseButton(Program.TABBED_PANE, addVin);
			Start.getInstance().updateMainPanel();
		});
	}
}
