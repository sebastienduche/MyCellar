package mycellar.actions;

import mycellar.AddVin;
import mycellar.Bouteille;
import mycellar.MyCellarImage;
import mycellar.Program;
import mycellar.Start;
import mycellar.Utils;

import javax.swing.AbstractAction;
import javax.swing.SwingUtilities;
import java.awt.event.ActionEvent;
import java.util.LinkedList;

public class OpenAddVinAction extends AbstractAction {

	private static final long serialVersionUID = 6187152928186377148L;
	private final LinkedList<Bouteille> listToModify;
	
	public OpenAddVinAction(LinkedList<Bouteille> listToModify) {
		this.listToModify = listToModify;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		SwingUtilities.invokeLater(() -> {
			if(Program.addWine == null) {
				Program.addWine = new AddVin();
				Program.TABBED_PANE.addTab(Program.getLabel("Infos131"), MyCellarImage.WINE, Program.addWine);
				Program.TABBED_PANE.setSelectedIndex(Program.TABBED_PANE.getTabCount()-1);
			}
			Program.addWine.setBottles(listToModify);
	
			int tabIndex = Program.findTab(MyCellarImage.WINE);
			if(tabIndex != -1) {
				Program.TABBED_PANE.setTitleAt(tabIndex, Program.getLabel("Infos131"));
				Program.TABBED_PANE.setSelectedIndex(tabIndex);
			}
			else {
				Program.TABBED_PANE.addTab(Program.getLabel("Infos131"), MyCellarImage.WINE, Program.addWine);
				Program.TABBED_PANE.setSelectedIndex(Program.TABBED_PANE.getTabCount()-1);
			}
	
			Utils.addCloseButton(Program.TABBED_PANE, Program.addWine);
			Start.getInstance().updateMainPanel();
		});
	}
}
