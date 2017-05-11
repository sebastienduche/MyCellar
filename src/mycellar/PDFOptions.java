package mycellar;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.LinkedList;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import mycellar.core.MyCellarButton;
import mycellar.core.MyCellarCheckBox;
import mycellar.core.MyCellarFields;
import mycellar.core.MyCellarLabel;
import mycellar.core.MyCellarSpinner;
import net.miginfocom.swing.MigLayout;

/**
 * <p>Titre : Cave à vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2004</p>
 * <p>Société : Seb Informatique</p>
 * @author Sébastien Duché
 * @version 1.9
 * @since 11/05/17
 */
public class PDFOptions extends JDialog {
  private JPanel jPanel1 = new JPanel();
  private MyCellarLabel MyCellarLabel2 = new MyCellarLabel();
  private MyCellarLabel MyCellarLabel3 = new MyCellarLabel();
  private MyCellarSpinner MyCellarSpinner1 = new MyCellarSpinner();
  private MyCellarCheckBox MyCellarCheckBox1 = new MyCellarCheckBox();
  private MyCellarCheckBox MyCellarCheckBox3 = new MyCellarCheckBox();
  private MyCellarCheckBox export[] = new MyCellarCheckBox[1];
  private JScrollPane jScrollPane1 = new JScrollPane();
  private JPanel jPanel2 = new JPanel();
  private MyCellarLabel colonnes[] = new MyCellarLabel[1];
  private MyCellarSpinner col_size[] = new MyCellarSpinner[1];
  private MyCellarLabel MyCellarLabel5[] = new MyCellarLabel[1];
  private MyCellarButton valider = new MyCellarButton();
  private MyCellarButton annuler = new MyCellarButton();
  private JTextField pdf_title = new JTextField();
  private MyCellarLabel MyCellarLabel6 = new MyCellarLabel();
  private MyCellarLabel MyCellarLabel7 = new MyCellarLabel();
  private MyCellarSpinner MyCellarSpinner3 = new MyCellarSpinner();
  private MyCellarLabel MyCellarLabel8 = new MyCellarLabel();
  private int nb_colonnes;
  static final long serialVersionUID = 110805;

  /**
   * PDFOptions: Constructeur pour la fenêtre d'options.
   */
  public PDFOptions() {
    try {
      jbInit();
    }
    catch (Exception e) {
      Program.showException(e);
    }
  }

  /**
   * jbInit: Fonction d'initialisation.
   *
   * @throws Exception
   */
  private void jbInit() throws Exception {
    this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    this.setTitle(Program.getLabel("Infos254"));
    setModal(true);
    this.addKeyListener(new java.awt.event.KeyListener() {
      public void keyReleased(java.awt.event.KeyEvent e) {}

      public void keyPressed(java.awt.event.KeyEvent e) {
        keylistener_actionPerformed(e);
      }

      public void keyTyped(java.awt.event.KeyEvent e) {}
    });

    this.setLayout(new MigLayout("", "grow",""));
    
    jPanel1.setBorder(BorderFactory.createEtchedBorder());
    jPanel1.setLayout(new MigLayout("","grow",""));
    jPanel1.setFont(Program.font_panel);
    MyCellarLabel2.setText(Program.getLabel("Infos255")); //Titre du PDF
    String pdf_title1 = Program.getCaveConfigString("PDF_TITLE", "");
    pdf_title.setText(pdf_title1);
    MyCellarLabel3.setText(Program.getLabel("Infos256")); //Taille du texte
    MyCellarSpinner1.addChangeListener(new javax.swing.event.ChangeListener() {
      public void stateChanged(javax.swing.event.ChangeEvent e) {
        if (Integer.parseInt(MyCellarSpinner1.getValue().toString()) <= 0) {
          MyCellarSpinner1.setValue(new Integer(1));
        }
      }
    });
    MyCellarSpinner3.addChangeListener(new javax.swing.event.ChangeListener() {
      public void stateChanged(javax.swing.event.ChangeEvent e) {
        if (Integer.parseInt(MyCellarSpinner3.getValue().toString()) <= 0) {
          MyCellarSpinner3.setValue(new Integer(1));
        }
      }
    });

    MyCellarSpinner1.setValue(new Integer(Program.getCaveConfigInt("TITLE_SIZE",10)));
    MyCellarSpinner3.setValue(new Integer(Program.getCaveConfigInt("TEXT_SIZE",10)));

    MyCellarCheckBox1.setText(Program.getLabel("Infos257")); //gras
    String bold = Program.getCaveConfigString("BOLD", "");
    if (bold.equals("bold")) {
      MyCellarCheckBox1.setSelected(true);
    }
  
    MyCellarCheckBox3.setText(Program.getLabel("Infos264")); //bordure
    String border = Program.getCaveConfigString("BORDER", "OFF");
    if (border.equals("ON")) {
      MyCellarCheckBox3.setSelected(true);
    }
    LinkedList<MyCellarFields> listColumns = MyCellarFields.getFieldsList();
    nb_colonnes = listColumns.size();
    colonnes = new MyCellarLabel[nb_colonnes];
    MyCellarLabel5 = new MyCellarLabel[nb_colonnes];
    col_size = new MyCellarSpinner[nb_colonnes];
    export = new MyCellarCheckBox[nb_colonnes];
    for (int i = 0; i < nb_colonnes; i++) {
      export[i] = new MyCellarCheckBox(Program.getLabel("Infos261"));
      try {
        int I = Program.getCaveConfigInt("SIZE_COL" + i + "EXPORT", 0);
        if (I == 1) {
          export[i].setSelected(true);
        }
        else {
          export[i].setSelected(false);
        }
      }
      catch (NumberFormatException nfe) {
        export[i].setSelected(false);
        Program.putCaveConfigString("SIZE_COL" + i + "EXPORT", "0");
      }
      col_size[i] = new MyCellarSpinner();
      col_size[i].addChangeListener(new javax.swing.event.ChangeListener() {
        public void stateChanged(javax.swing.event.ChangeEvent e) {
          MyCellarSpinner js = (MyCellarSpinner) e.getSource();
          if (Integer.parseInt(js.getValue().toString()) <= 0) {
            js.setValue(new Integer(1));
          }
        }
      });
        colonnes[i] = new MyCellarLabel(listColumns.get(i).toString());
      MyCellarLabel5[i] = new MyCellarLabel("cm");
   
      col_size[i].setValue(new Integer(Program.getCaveConfigInt("SIZE_COL" + i, 5)));
    }
    jPanel2.setLayout(new MigLayout("", "[grow][grow][grow]",""));
    jPanel2.setFont(Program.font_panel);
    valider.setText("OK");
    valider.addActionListener((e) -> valider_actionPerformed(e));
    annuler.setText(Program.getLabel("Infos055"));
    annuler.addActionListener((e) -> dispose());
    MyCellarLabel6.setText("pt");
    MyCellarLabel7.setText(Program.getLabel("Infos256")); //Taille du texte
    MyCellarLabel8.setText("pt");
 
    jPanel1.add(MyCellarLabel2, "split 2");
    jPanel1.add(pdf_title, "grow, wrap");
    jPanel1.add(MyCellarLabel3, "split 4");
    jPanel1.add(MyCellarSpinner1);
    jPanel1.add(MyCellarLabel6);
    jPanel1.add(MyCellarCheckBox1, "grow, align right");   
    this.add(jPanel1, "grow, wrap");
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
    
    jScrollPane1 = new JScrollPane(jPanel2);
    jScrollPane1.setBorder(BorderFactory.createTitledBorder(Program.getLabel("Infos258")));
    this.add(jScrollPane1, "grow, wrap");
    this.add(valider, "gaptop 15px, split 2, center");
    this.add(annuler);
    setSize(400, 500);
    setLocationRelativeTo(null);
  }

  /**
   * valider_actionPerformed: Valider les modifications et quitter.
   *
   * @param e ActionEvent
   */
  void valider_actionPerformed(ActionEvent e) {
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
      this.dispose();
      if (col_size_max > 19) {
        new Erreur(Program.getLabel("Infos273") + " " + col_size_max + "cms", "", true);
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
