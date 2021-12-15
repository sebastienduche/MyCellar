package mycellar.xls;

import mycellar.Program;
import mycellar.Start;
import mycellar.core.uicomponents.MyCellarButton;
import mycellar.core.uicomponents.MyCellarCheckBox;
import mycellar.core.uicomponents.MyCellarLabel;
import mycellar.core.uicomponents.MyCellarSpinner;
import mycellar.core.common.MyCellarFields;
import net.miginfocom.swing.MigLayout;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;

import static mycellar.ProgramConstants.FONT_PANEL;
import static mycellar.core.LabelType.INFO;
import static mycellar.core.LabelType.INFO_OTHER;
import static mycellar.core.MyCellarSettings.BOLD_XLS;
import static mycellar.core.MyCellarSettings.SIZE_COL;
import static mycellar.core.MyCellarSettings.TEXT_SIZE_XLS;
import static mycellar.core.MyCellarSettings.TITLE_SIZE_XLS;
import static mycellar.core.MyCellarSettings.XLS_TITLE;

/**
 * <p>Titre : Cave &agrave; vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2004</p>
 * <p>Soci&eacute;t&eacute; : Seb Informatique</p>
 *
 * @author S&eacute;bastien Duch&eacute;
 * @version 2.2
 * @since 16/04/21
 */
public final class XLSOptions extends JDialog {

  private static final long serialVersionUID = 5307297932934344545L;
  private final MyCellarSpinner titleSize = new MyCellarSpinner(1, 999);
  private final MyCellarCheckBox myCellarCheckBox = new MyCellarCheckBox(INFO, "257");
  private final MyCellarCheckBox[] export;
  private final JTextField pdf_title = new JTextField();
  private final MyCellarSpinner textSize = new MyCellarSpinner(1, 999);
  private final int nb_colonnes;

  public XLSOptions() {
    setModal(true);
    setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    setTitle(Program.getLabel("Infos268"));
    addKeyListener(new KeyAdapter() {
      @Override
      public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == 'o' || e.getKeyCode() == 'O' || e.getKeyCode() == KeyEvent.VK_ENTER) {
          valider_actionPerformed(null);
        }
      }
    });

    setLayout(new MigLayout("", "grow", ""));

    JPanel jPanel1 = new JPanel();
    jPanel1.setBorder(BorderFactory.createEtchedBorder());
    jPanel1.setLayout(new MigLayout("", "grow", ""));
    jPanel1.setFont(FONT_PANEL);
    pdf_title.setText(Program.getCaveConfigString(XLS_TITLE, ""));

    titleSize.setValue(Program.getCaveConfigInt(TITLE_SIZE_XLS, 10));
    textSize.setValue(Program.getCaveConfigInt(TEXT_SIZE_XLS, 10));

    myCellarCheckBox.setSelected(Program.getCaveConfigBool(BOLD_XLS, false));
    List<MyCellarFields> columns = MyCellarFields.getFieldsList();
    nb_colonnes = columns.size();
    export = new MyCellarCheckBox[nb_colonnes];
    MyCellarLabel[] colonnes = new MyCellarLabel[nb_colonnes];
    for (int i = 0; i < nb_colonnes; i++) {
      export[i] = new MyCellarCheckBox(INFO, "261");
      export[i].setSelected(1 == Program.getCaveConfigInt(SIZE_COL + i + "EXPORT_XLS", 0));
      colonnes[i] = new MyCellarLabel(columns.get(i).toString());

    }
    JPanel jPanel2 = new JPanel();
    jPanel2.setLayout(new MigLayout("", "[grow][grow]", ""));
    jPanel2.setFont(FONT_PANEL);
    MyCellarButton valider = new MyCellarButton(INFO_OTHER, "Main.OK");
    valider.addActionListener(this::valider_actionPerformed);
    MyCellarButton annuler = new MyCellarButton(INFO, "055");
    annuler.addActionListener((e) -> dispose());
    MyCellarLabel MyCellarLabel6 = new MyCellarLabel("pt");
    MyCellarLabel MyCellarLabel7 = new MyCellarLabel(INFO, "256"); //Taille du texte
    MyCellarLabel MyCellarLabel8 = new MyCellarLabel("pt");

    MyCellarLabel MyCellarLabel2 = new MyCellarLabel(INFO, "270"); //Titre du XLS
    MyCellarLabel MyCellarLabel3 = new MyCellarLabel(INFO, "256"); //Taille du texte
    jPanel1.add(MyCellarLabel2, "split 2");
    jPanel1.add(pdf_title, "grow, wrap");
    jPanel1.add(MyCellarLabel3, "split 6");
    jPanel1.add(titleSize);
    jPanel1.add(MyCellarLabel6);
    jPanel1.add(myCellarCheckBox);
    add(jPanel1, "grow, wrap");
    jPanel2.add(MyCellarLabel7, "split 3, span 2");
    jPanel2.add(textSize);
    jPanel2.add(MyCellarLabel8, "wrap");
    setSize(400, 500);
    for (int i = 0; i < nb_colonnes; i++) {
      jPanel2.add(colonnes[i], "newline, grow");
      jPanel2.add(export[i], "push, align right");
    }
    JScrollPane jScrollPane1 = new JScrollPane(jPanel2);
    jScrollPane1.setBorder(BorderFactory.createTitledBorder(Program.getLabel("Infos258")));
    add(jScrollPane1, "gaptop 15px, grow, wrap");
    add(valider, "split 2, center");
    add(annuler);
    setLocationRelativeTo(Start.getInstance());
  }

  /**
   * valider_actionPerformed: Valider les modifications et quitter.
   *
   * @param e ActionEvent
   */
  private void valider_actionPerformed(ActionEvent e) {
    try {
      Program.putCaveConfigString(XLS_TITLE, pdf_title.getText());
      Program.putCaveConfigString(TITLE_SIZE_XLS, titleSize.getValue().toString());
      Program.putCaveConfigString(TEXT_SIZE_XLS, textSize.getValue().toString());
      Program.putCaveConfigBool(BOLD_XLS, myCellarCheckBox.isSelected());
      for (int i = 0; i < nb_colonnes; i++) {
        Program.putCaveConfigInt(SIZE_COL + i + "EXPORT_XLS", export[i].isSelected() ? 1 : 0);
      }
      dispose();
    } catch (RuntimeException exc) {
      Program.showException(exc);
    }
  }
}
