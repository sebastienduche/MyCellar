package mycellar.xls;

import mycellar.Program;
import mycellar.Start;
import mycellar.core.LabelType;
import mycellar.core.MyCellarButton;
import mycellar.core.MyCellarCheckBox;
import mycellar.core.MyCellarFields;
import mycellar.core.MyCellarLabel;
import mycellar.core.MyCellarSettings;
import mycellar.core.MyCellarSpinner;
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

/**
 * <p>Titre : Cave &agrave; vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2004</p>
 * <p>Soci&eacute;t&eacute; : Seb Informatique</p>
 * @author S&eacute;bastien Duch&eacute;
 * @version 1.9
 * @since 25/06/20
 */
final public class XLSOptions extends JDialog {

  private static final long serialVersionUID = 5307297932934344545L;
  private final MyCellarSpinner MyCellarSpinner1 = new MyCellarSpinner();
  private final MyCellarCheckBox MyCellarCheckBox1 = new MyCellarCheckBox(LabelType.INFO, "257");
  private final MyCellarCheckBox[] export;
  private final JTextField pdf_title = new JTextField();
  private final MyCellarSpinner MyCellarSpinner3 = new MyCellarSpinner();
  private final int nb_colonnes;

  /**
   * XLSOptions: Constructeur pour la fenêtre d'options.
   */
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

    setLayout(new MigLayout("","grow",""));

    JPanel jPanel1 = new JPanel();
    jPanel1.setBorder(BorderFactory.createEtchedBorder());
    jPanel1.setLayout(new MigLayout("","grow",""));
    jPanel1.setFont(Program.FONT_PANEL);
    pdf_title.setText(Program.getCaveConfigString(MyCellarSettings.XLS_TITLE, ""));
    MyCellarLabel MyCellarLabel2 = new MyCellarLabel(Program.getLabel("Infos270")); //Titre du XLS
    MyCellarLabel MyCellarLabel3 = new MyCellarLabel(Program.getLabel("Infos256")); //Taille du texte
    MyCellarSpinner3.addChangeListener((e) -> {
        if (Integer.parseInt(MyCellarSpinner3.getValue().toString()) <= 0) {
          MyCellarSpinner3.setValue(1);
        }
    });
    MyCellarSpinner1.addChangeListener((e) -> {
        if (Integer.parseInt(MyCellarSpinner1.getValue().toString()) <= 0) {
          MyCellarSpinner1.setValue(1);
        }
    });

    MyCellarSpinner1.setValue(Program.getCaveConfigInt(MyCellarSettings.TITLE_SIZE_XLS, 10));
    MyCellarSpinner3.setValue(Program.getCaveConfigInt(MyCellarSettings.TEXT_SIZE_XLS, 10));

    MyCellarCheckBox1.setSelected(Program.getCaveConfigBool(MyCellarSettings.BOLD_XLS, false));
    List<MyCellarFields> columns = MyCellarFields.getFieldsList();
    nb_colonnes = columns.size();
    MyCellarLabel[] colonnes = new MyCellarLabel[nb_colonnes];
    export = new MyCellarCheckBox[nb_colonnes];
    for (int i = 0; i < nb_colonnes; i++) {
      export[i] = new MyCellarCheckBox(Program.getLabel("Infos261"));
      export[i].setSelected(1 == Program.getCaveConfigInt(MyCellarSettings.SIZE_COL + i + "EXPORT_XLS", 0));
      colonnes[i] = new MyCellarLabel(columns.get(i).toString());
     
    }
    JPanel jPanel2 = new JPanel();
    jPanel2.setLayout(new MigLayout("","[grow][grow]",""));
    jPanel2.setFont(Program.FONT_PANEL);
    MyCellarButton valider = new MyCellarButton(Program.getLabel("Main.OK"));
    valider.addActionListener(this::valider_actionPerformed);
    MyCellarButton annuler = new MyCellarButton(Program.getLabel("Infos055"));
    annuler.addActionListener((e) -> dispose());
    MyCellarLabel MyCellarLabel6 = new MyCellarLabel("pt");
    MyCellarLabel MyCellarLabel7 = new MyCellarLabel(Program.getLabel("Infos256")); //Taille du texte
    MyCellarLabel MyCellarLabel8 = new MyCellarLabel("pt");
    
    jPanel1.add(MyCellarLabel2, "split 2");
    jPanel1.add(pdf_title, "grow, wrap");
    jPanel1.add(MyCellarLabel3, "split 6");
    jPanel1.add(MyCellarSpinner1);
    jPanel1.add(MyCellarLabel6);
    jPanel1.add(MyCellarCheckBox1);
    add(jPanel1, "grow, wrap");
    jPanel2.add(MyCellarLabel7, "split 3, span 2");
    jPanel2.add(MyCellarSpinner3);
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
      Program.putCaveConfigString(MyCellarSettings.XLS_TITLE, pdf_title.getText());
      Program.putCaveConfigString(MyCellarSettings.TITLE_SIZE_XLS, MyCellarSpinner1.getValue().toString());
      Program.putCaveConfigString(MyCellarSettings.TEXT_SIZE_XLS, MyCellarSpinner3.getValue().toString());
      Program.putCaveConfigBool(MyCellarSettings.BOLD_XLS, MyCellarCheckBox1.isSelected());
      for (int i = 0; i < nb_colonnes; i++) {
        Program.putCaveConfigInt(MyCellarSettings.SIZE_COL + i + "EXPORT_XLS", export[i].isSelected() ? 1 : 0);
      }
      dispose();
    }
    catch (Exception exc) {
      Program.showException(exc);
    }
  }
}
