package mycellar;

import mycellar.core.MyCellarButton;
import mycellar.core.MyCellarCheckBox;
import mycellar.core.MyCellarFields;
import mycellar.core.MyCellarLabel;
import mycellar.core.MyCellarSpinner;
import net.miginfocom.swing.MigLayout;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.text.MessageFormat;
import java.util.ArrayList;

/**
 * <p>Titre : Cave à vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2004</p>
 * <p>Société : Seb Informatique</p>
 * @author Sébastien Duché
 * @version 2.3
 * @since 01/03/18
 */
class PDFOptions extends JDialog {
  private final MyCellarSpinner MyCellarSpinner1 = new MyCellarSpinner();
  private final MyCellarCheckBox MyCellarCheckBox1 = new MyCellarCheckBox();
  private final MyCellarCheckBox MyCellarCheckBox3 = new MyCellarCheckBox();
  private final MyCellarCheckBox[] export;
  private final MyCellarSpinner[] col_size;
  private final JTextField pdf_title = new JTextField();
  private final MyCellarSpinner MyCellarSpinner3 = new MyCellarSpinner();
  private final int nb_colonnes;
  static final long serialVersionUID = 110805;

  /**
   * PDFOptions: Constructeur pour la fenêtre d'options.
   */
  public PDFOptions() {
    setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    setTitle(Program.getLabel("Infos254"));
    setModal(true);
    addKeyListener(new KeyListener() {
      @Override
      public void keyReleased(KeyEvent e) {}
      @Override
      public void keyPressed(KeyEvent e) {
        keylistener_actionPerformed(e);
      }
      @Override
      public void keyTyped(KeyEvent e) {}
    });

    setLayout(new MigLayout("", "grow",""));

    JPanel jPanel1 = new JPanel();
    jPanel1.setBorder(BorderFactory.createEtchedBorder());
    jPanel1.setLayout(new MigLayout("","grow",""));
    jPanel1.setFont(Program.font_panel);
    MyCellarLabel MyCellarLabel2 = new MyCellarLabel(Program.getLabel("Infos255")); //Titre du PDF
    String pdf_title1 = Program.getCaveConfigString("PDF_TITLE", "");
    pdf_title.setText(pdf_title1);
    MyCellarLabel MyCellarLabel3 = new MyCellarLabel(Program.getLabel("Infos256")); //Taille du texte
    MyCellarSpinner1.addChangeListener((e) -> {
        if (Integer.parseInt(MyCellarSpinner1.getValue().toString()) <= 0) {
          MyCellarSpinner1.setValue(1);
        }
    });
    MyCellarSpinner3.addChangeListener((e) -> {
        if (Integer.parseInt(MyCellarSpinner3.getValue().toString()) <= 0) {
          MyCellarSpinner3.setValue(1);
        }
    });

    MyCellarSpinner1.setValue(Program.getCaveConfigInt("TITLE_SIZE",10));
    MyCellarSpinner3.setValue(Program.getCaveConfigInt("TEXT_SIZE",10));

    MyCellarCheckBox1.setText(Program.getLabel("Infos257")); //gras
    if ("bold".equals(Program.getCaveConfigString("BOLD", ""))) {
      MyCellarCheckBox1.setSelected(true);
    }
  
    MyCellarCheckBox3.setText(Program.getLabel("Infos264")); //bordure
    if ("ON".equals(Program.getCaveConfigString("BORDER", "OFF"))) {
      MyCellarCheckBox3.setSelected(true);
    }
    ArrayList<MyCellarFields> listColumns = MyCellarFields.getFieldsList();
    nb_colonnes = listColumns.size();
    MyCellarLabel[] colonnes = new MyCellarLabel[nb_colonnes];
    MyCellarLabel[] MyCellarLabel5 = new MyCellarLabel[nb_colonnes];
    col_size = new MyCellarSpinner[nb_colonnes];
    export = new MyCellarCheckBox[nb_colonnes];
    for (int i = 0; i < nb_colonnes; i++) {
      export[i] = new MyCellarCheckBox(Program.getLabel("Infos261"));
      try {
        export[i].setSelected(1 == Program.getCaveConfigInt("SIZE_COL" + i + "EXPORT", 0));
      }
      catch (NumberFormatException nfe) {
        export[i].setSelected(false);
        Program.putCaveConfigString("SIZE_COL" + i + "EXPORT", "0");
      }
      col_size[i] = new MyCellarSpinner();
      col_size[i].addChangeListener((e) -> {
          MyCellarSpinner js = (MyCellarSpinner) e.getSource();
          if (Integer.parseInt(js.getValue().toString()) <= 0) {
            js.setValue(1);
          }
      });
      colonnes[i] = new MyCellarLabel(listColumns.get(i).toString());
      MyCellarLabel5[i] = new MyCellarLabel("cm");
   
      col_size[i].setValue(Program.getCaveConfigInt("SIZE_COL" + i, 5));
    }
    JPanel jPanel2 = new JPanel();
    jPanel2.setLayout(new MigLayout("", "[grow][grow][grow]",""));
    jPanel2.setFont(Program.font_panel);
    MyCellarButton valider = new MyCellarButton(Program.getLabel("Main.OK"));
    valider.addActionListener(this::valider_actionPerformed);
    MyCellarButton annuler = new MyCellarButton(Program.getLabel("Infos055"));
    annuler.addActionListener((e) -> dispose());
    MyCellarLabel MyCellarLabel6 = new MyCellarLabel("pt");
    MyCellarLabel MyCellarLabel7 = new MyCellarLabel(Program.getLabel("Infos256")); //Taille du texte
    MyCellarLabel MyCellarLabel8 = new MyCellarLabel("pt");
 
    jPanel1.add(MyCellarLabel2, "split 2");
    jPanel1.add(pdf_title, "grow, wrap");
    jPanel1.add(MyCellarLabel3, "split 4");
    jPanel1.add(MyCellarSpinner1);
    jPanel1.add(MyCellarLabel6);
    jPanel1.add(MyCellarCheckBox1, "grow, align right");   
    add(jPanel1, "grow, wrap");
    jPanel2.add(MyCellarLabel7, "split 4, span 3");
    jPanel2.add(MyCellarSpinner3);
    jPanel2.add(MyCellarLabel8);
    jPanel2.add(MyCellarCheckBox3, "push, align right, gapbottom 15px");
    
    for (int i = 0; i < nb_colonnes; i++) {
        jPanel2.add(colonnes[i], "newline");
        jPanel2.add(col_size[i], "split 2");
        jPanel2.add(MyCellarLabel5[i]);
        jPanel2.add(export[i], "push, align right");
      }
    
    JScrollPane jScrollPane1 = new JScrollPane(jPanel2);
    jScrollPane1.setBorder(BorderFactory.createTitledBorder(Program.getLabel("Infos258")));
    add(jScrollPane1, "grow, wrap");
    add(valider, "gaptop 15px, split 2, center");
    add(annuler);
    setSize(400, 500);
    setLocationRelativeTo(null);
  }

  /**
   * valider_actionPerformed: Valider les modifications et quitter.
   *
   * @param e ActionEvent
   */
  private void valider_actionPerformed(ActionEvent e) {
    try {
      Program.putCaveConfigString("PDF_TITLE", pdf_title.getText());
      Program.putCaveConfigString("TITLE_SIZE", MyCellarSpinner1.getValue().toString());
      Program.putCaveConfigString("TEXT_SIZE", MyCellarSpinner3.getValue().toString());
      if (MyCellarCheckBox1.isSelected()) {
        Program.putCaveConfigString("BOLD", "bold");
      }
      else {
        Program.putCaveConfigString("BOLD", "");
      }
      if (MyCellarCheckBox3.isSelected()) {
        Program.putCaveConfigString("BORDER", "ON");
      }
      else {
        Program.putCaveConfigString("BORDER", "OFF");
      }
      int col_size_max = 0;
      for (int i = 0; i < nb_colonnes; i++) {

        Program.putCaveConfigString("SIZE_COL" + i, col_size[i].getValue().toString());
        if (export[i].isSelected()) {
          col_size_max += Integer.parseInt(col_size[i].getValue().toString());
          Program.putCaveConfigString("SIZE_COL" + i + "EXPORT", "1");
        }
        else {
          Program.putCaveConfigString("SIZE_COL" + i + "EXPORT", "0");
        }
      }
      dispose();
      if (col_size_max > 19) {
        Erreur.showSimpleErreur(MessageFormat.format(Program.getLabel("Infos273"), col_size_max), true);
      }
    }
    catch (Exception exc) {
      Program.showException(exc);
    }
  }

  /**
   * keylistener_actionPerformed: Fonction d'écoute clavier.
   *
   * @param e KeyEvent
   */
  void keylistener_actionPerformed(KeyEvent e) {
    if (e.getKeyCode() == 'o' || e.getKeyCode() == 'O' || e.getKeyCode() == KeyEvent.VK_ENTER) {
      valider_actionPerformed(null);
    }
  }
}
