package mycellar.actions;

import mycellar.Export;
import mycellar.Filtre;
import mycellar.MyCellarControl;
import mycellar.MyCellarImage;
import mycellar.Program;
import mycellar.core.MyCellarSettings;
import mycellar.core.text.MyCellarLabelManagement;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import java.awt.event.ActionEvent;
import java.io.File;

import static mycellar.general.ResourceKey.EXPORT_PDF;

public class ExportPDFAction extends AbstractAction {

  public ExportPDFAction() {
    super("", MyCellarImage.PDF);
    putValue(SHORT_DESCRIPTION, MyCellarLabelManagement.getLabel(EXPORT_PDF));
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    JFileChooser boiteFichier = new JFileChooser(Program.getCaveConfigString(MyCellarSettings.DIR, ""));
    boiteFichier.removeChoosableFileFilter(boiteFichier.getFileFilter());
    boiteFichier.addChoosableFileFilter(Filtre.FILTRE_PDF);

    int retour_jfc = boiteFichier.showSaveDialog(null);
    if (retour_jfc == JFileChooser.APPROVE_OPTION) {
      File nomFichier = boiteFichier.getSelectedFile();
      String name = nomFichier.getAbsolutePath();
      name = MyCellarControl.controlAndUpdateExtension(name, Filtre.FILTRE_PDF);
      Export.exportToPDF(Program.getStorage().getAllList(), new File(name));
    }
  }
}
