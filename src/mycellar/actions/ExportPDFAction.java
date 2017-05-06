package mycellar.actions;

import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;

import mycellar.Export;
import mycellar.Filtre;
import mycellar.MyCellarImage;
import mycellar.Program;

public class ExportPDFAction extends AbstractAction {

	private static final long serialVersionUID = -7521113567474056823L;
	public ExportPDFAction() {
		super(Program.getLabel("AddVin.ChooseCell"), MyCellarImage.PDF);
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		JFileChooser boiteFichier = new JFileChooser(Program.getCaveConfigString("DIR",""));
		  boiteFichier.removeChoosableFileFilter(boiteFichier.getFileFilter());
	      boiteFichier.addChoosableFileFilter(Filtre.FILTRE_PDF);
	   
	    int retour_jfc = boiteFichier.showSaveDialog(null);
	    if (retour_jfc == JFileChooser.APPROVE_OPTION) {
	      File nomFichier = boiteFichier.getSelectedFile();
	      String name = nomFichier.getName();
	      int index = -1;
	      if((index = name.indexOf('.')) == -1)
	    	  nomFichier = new File(nomFichier.getParentFile(), nomFichier.getName() + ".pdf");
	      else {
	    	  if(!name.substring(index).equalsIgnoreCase(".pdf"))
	    		  nomFichier = new File(nomFichier.getParentFile(), nomFichier.getName() + ".pdf");
	      }
	      Export.exportToPDF(Program.getStorage().getAllList(), nomFichier);
	    }
	}
}
