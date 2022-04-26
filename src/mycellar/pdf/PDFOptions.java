package mycellar.pdf;

import mycellar.Erreur;
import mycellar.Program;
import mycellar.Start;
import mycellar.core.MyCellarSettings;
import mycellar.core.common.MyCellarFields;
import mycellar.core.text.LabelType;
import mycellar.core.uicomponents.MyCellarButton;
import mycellar.core.uicomponents.MyCellarCheckBox;
import mycellar.core.uicomponents.MyCellarLabel;
import mycellar.core.uicomponents.MyCellarSpinner;
import net.miginfocom.swing.MigLayout;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.MessageFormat;
import java.util.List;

import static mycellar.ProgramConstants.FONT_PANEL;
import static mycellar.ProgramConstants.isVK_ENTER;
import static mycellar.ProgramConstants.isVK_O;
import static mycellar.core.text.MyCellarLabelManagement.getLabel;

/**
 * Titre : Cave &agrave; vin
 * Description : Votre description
 * Copyright : Copyright (c) 2004
 * Soci&eacute;t&eacute; : Seb Informatique
 *
 * @author S&eacute;bastien Duch&eacute;
 * @version 3.2
 * @since 26/04/22
 */
public final class PDFOptions extends JDialog {
  static final long serialVersionUID = 110805;
  private final MyCellarSpinner titleSize = new MyCellarSpinner(1, 99);
  private final MyCellarCheckBox boldCheck = new MyCellarCheckBox("Options.Bold");
  private final MyCellarCheckBox borderCheck = new MyCellarCheckBox("Options.Border");
  private final MyCellarCheckBox[] export;
  private final MyCellarSpinner[] col_size;
  private final JTextField pdf_title = new JTextField();
  private final MyCellarSpinner textSize = new MyCellarSpinner(1, 99);
  private final int nb_colonnes;

  public PDFOptions() {
    setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    setTitle(getLabel("Infos254"));
    setModal(true);
    addKeyListener(new KeyAdapter() {
      @Override
      public void keyPressed(KeyEvent e) {
        if (isVK_O(e) || isVK_ENTER(e)) {
          valider_actionPerformed(null);
        }
      }
    });

    setLayout(new MigLayout("", "grow", ""));

    JPanel jPanel1 = new JPanel();
    jPanel1.setBorder(BorderFactory.createEtchedBorder());
    jPanel1.setLayout(new MigLayout("", "grow", ""));
    jPanel1.setFont(FONT_PANEL);
    pdf_title.setText(Program.getCaveConfigString(MyCellarSettings.PDF_TITLE, ""));

    titleSize.setValue(Program.getCaveConfigInt(MyCellarSettings.TITLE_SIZE, 10));
    textSize.setValue(Program.getCaveConfigInt(MyCellarSettings.TEXT_SIZE, 10));

    if (Program.getCaveConfigBool(MyCellarSettings.BOLD, false)) {
      boldCheck.setSelected(true);
    }

    if (Program.getCaveConfigBool(MyCellarSettings.BORDER, false)) {
      borderCheck.setSelected(true);
    }
    List<MyCellarFields> listColumns = MyCellarFields.getFieldsList();
    nb_colonnes = listColumns.size();
    col_size = new MyCellarSpinner[nb_colonnes];
    export = new MyCellarCheckBox[nb_colonnes];
    MyCellarLabel[] colonnes = new MyCellarLabel[nb_colonnes];
    for (int i = 0; i < nb_colonnes; i++) {
      export[i] = new MyCellarCheckBox("Main.Exported");
      export[i].setSelected(1 == Program.getCaveConfigInt(MyCellarSettings.SIZE_COL + i + "EXPORT", 0));
      col_size[i] = new MyCellarSpinner(1, 99);
      colonnes[i] = new MyCellarLabel(listColumns.get(i).toString());

      col_size[i].setValue(Program.getCaveConfigInt(MyCellarSettings.SIZE_COL + i, 5));
    }
    JPanel jPanel2 = new JPanel();
    jPanel2.setLayout(new MigLayout("", "[grow][grow][grow]", ""));
    jPanel2.setFont(FONT_PANEL);
    MyCellarButton valider = new MyCellarButton(LabelType.INFO_OTHER, "Main.OK");
    valider.addActionListener(this::valider_actionPerformed);
    MyCellarButton annuler = new MyCellarButton(LabelType.INFO_OTHER, "Main.cancel");
    annuler.addActionListener((e) -> dispose());

    jPanel1.add(new MyCellarLabel(LabelType.INFO, "255"), "split 2"); //Titre du PDF
    jPanel1.add(pdf_title, "grow, wrap");
    jPanel1.add(new MyCellarLabel(LabelType.INFO, "256"), "split 4"); //Taille du texte
    jPanel1.add(titleSize);
    jPanel1.add(new MyCellarLabel("pt"));
    jPanel1.add(boldCheck, "grow, align right");
    add(jPanel1, "grow, wrap");
    jPanel2.add(new MyCellarLabel(LabelType.INFO, "256"), "split 4, span 3");
    jPanel2.add(textSize);
    jPanel2.add(new MyCellarLabel("pt"));
    jPanel2.add(borderCheck, "push, align right, gapbottom 15px");

    for (int i = 0; i < nb_colonnes; i++) {
      jPanel2.add(colonnes[i], "newline");
      jPanel2.add(col_size[i], "split 2");
      jPanel2.add(new MyCellarLabel("cm"));
      jPanel2.add(export[i], "push, align right");
    }

    JScrollPane jScrollPane = new JScrollPane(jPanel2);
    jScrollPane.setBorder(BorderFactory.createTitledBorder(getLabel("Infos258")));
    add(jScrollPane, "grow, wrap");
    add(valider, "gaptop 15px, split 2, center");
    add(annuler);
    setSize(400, 500);
    setLocationRelativeTo(Start.getInstance());
  }

  /**
   * valider_actionPerformed: Valider les modifications et quitter.
   *
   * @param e ActionEvent
   */
  private void valider_actionPerformed(ActionEvent e) {
    try {
      Program.putCaveConfigString(MyCellarSettings.PDF_TITLE, pdf_title.getText());
      Program.putCaveConfigString(MyCellarSettings.TITLE_SIZE, titleSize.getValue().toString());
      Program.putCaveConfigString(MyCellarSettings.TEXT_SIZE, textSize.getValue().toString());
      Program.putCaveConfigBool(MyCellarSettings.BOLD, boldCheck.isSelected());
      Program.putCaveConfigBool(MyCellarSettings.BORDER, borderCheck.isSelected());
      int col_size_max = 0;
      for (int i = 0; i < nb_colonnes; i++) {
        Program.putCaveConfigString(MyCellarSettings.SIZE_COL + i, col_size[i].getValue().toString());
        if (export[i].isSelected()) {
          col_size_max += Integer.parseInt(col_size[i].getValue().toString());
        }
        Program.putCaveConfigInt(MyCellarSettings.SIZE_COL + i + "EXPORT", export[i].isSelected() ? 1 : 0);
      }
      dispose();
      if (col_size_max > 19) {
        Erreur.showInformationMessage(MessageFormat.format(getLabel("Infos273"), col_size_max));
      }
    } catch (NumberFormatException e1) {
      Program.Debug("PDFOptions: ERROR: " + e1.getMessage());
      Program.showException(e1);
    } catch (RuntimeException exc) {
      Program.showException(exc);
    }
  }

}
