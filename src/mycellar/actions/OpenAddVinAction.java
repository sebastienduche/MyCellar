package mycellar.actions;

import java.awt.event.ActionEvent;
import java.util.LinkedList;

import javax.swing.AbstractAction;
import javax.swing.SwingUtilities;

import mycellar.AddVin;
import mycellar.Bouteille;
import mycellar.MyCellarImage;
import mycellar.Program;
import mycellar.Start;
import mycellar.Utils;

public class OpenAddVinAction extends AbstractAction {

	private static final long serialVersionUID = 6187152928186377148L;
	private LinkedList<Bouteille> listToModify;
	
	public OpenAddVinAction(LinkedList<Bouteille> listToModify) {
		this.listToModify = listToModify;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		SwingUtilities.invokeLater(() -> {
			if(Program.addWine == null) {
				Program.addWine = new AddVin();
				Program.tabbedPane.addTab(Program.getLabel("Infos131"), MyCellarImage.WINE, Program.addWine);
				Program.tabbedPane.setSelectedIndex(Program.tabbedPane.getTabCount()-1);
			}
			Program.addWine.setBottles(listToModify);
	
			int tabIndex = Program.findTab(MyCellarImage.WINE);
			if(tabIndex != -1) {
				Program.tabbedPane.setTitleAt(tabIndex, Program.getLabel("Infos131"));
				Program.tabbedPane.setSelectedIndex(tabIndex);
			}
			else {
				Program.tabbedPane.addTab(Program.getLabel("Infos131"), MyCellarImage.WINE, Program.addWine);
				Program.tabbedPane.setSelectedIndex(Program.tabbedPane.getTabCount()-1);
			}
	
			Utils.addCloseButton(Program.tabbedPane, Program.addWine);
			Start.updateMainPanel();
		});
	}
}
