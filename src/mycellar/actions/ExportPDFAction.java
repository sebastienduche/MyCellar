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
    JFileChooser fileChooser = new JFileChooser(Program.getCaveConfigString(MyCellarSettings.DIR, ""));
    fileChooser.removeChoosableFileFilter(fileChooser.getFileFilter());
    fileChooser.addChoosableFileFilter(Filtre.FILTRE_PDF);

    if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
      String name = fileChooser.getSelectedFile().getAbsolutePath();
      name = MyCellarControl.controlAndUpdateExtension(name, Filtre.FILTRE_PDF);
      Export.exportToPDF(Program.getStorage().getAllList(), new File(name));
    }
  }
}
