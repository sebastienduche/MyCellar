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
 * @version 0.9
 * @since 13/11/16
 */
public class XLSOptions extends JDialog {
  private JPanel jPanel1 = new JPanel();
  private MyCellarLabel MyCellarLabel2 = new MyCellarLabel();
  private MyCellarLabel MyCellarLabel3 = new MyCellarLabel();
  private MyCellarSpinner MyCellarSpinner1 = new MyCellarSpinner();
  private MyCellarCheckBox MyCellarCheckBox1 = new MyCellarCheckBox();
  private MyCellarCheckBox export[] = new MyCellarCheckBox[1];
  private JScrollPane jScrollPane1;
  private JPanel jPanel2 = new JPanel();
  private MyCellarLabel colonnes[] = new MyCellarLabel[1];
  private MyCellarButton valider = new MyCellarButton();
  private MyCellarButton annuler = new MyCellarButton();
  private JTextField pdf_title = new JTextField();
  private MyCellarLabel MyCellarLabel6 = new MyCellarLabel();
  private MyCellarLabel MyCellarLabel7 = new MyCellarLabel();
  private MyCellarSpinner MyCellarSpinner3 = new MyCellarSpinner();
  private MyCellarLabel MyCellarLabel8 = new MyCellarLabel();
  private int nb_colonnes = 0;
  static final long serialVersionUID = 040705;

  /**
   * XLSOptions: Constructeur pour la fenêtre d'options.
   * @param nb_colonnes1 int
   */
  public XLSOptions() {
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
	  setModal(true);
	  setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    this.setTitle(Program.getLabel("Infos268"));
    this.addKeyListener(new java.awt.event.KeyListener() {
      public void keyReleased(java.awt.event.KeyEvent e) {}

      public void keyPressed(java.awt.event.KeyEvent e) {
        keylistener_actionPerformed(e);
      }

      public void keyTyped(java.awt.event.KeyEvent e) {}
    });

    this.setLayout(new MigLayout("","grow",""));
    
    jPanel1.setBorder(BorderFactory.createEtchedBorder());
    jPanel1.setLayout(new MigLayout("","grow",""));
    jPanel1.setFont(Program.font_panel);
    MyCellarLabel2.setText(Program.getLabel("Infos270")); //Titre du XLS
    String xls_title = Program.getCaveConfigString("XLS_TITLE", "");
    pdf_title.setText(xls_title);
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

    MyCellarSpinner1.setValue(new Integer(Program.getCaveConfigInt("TITLE_SIZE_XLS",10)));
    MyCellarSpinner3.setValue(new Integer(Program.getCaveConfigInt("TEXT_SIZE_XLS",10)));

    MyCellarCheckBox1.setText(Program.getLabel("Infos257")); //gras
    String bold = Program.getCaveConfigString("BOLD_XLS","");
    if (bold.equals("bold")) {
      MyCellarCheckBox1.setSelected(true);
    }
    LinkedList<MyCellarFields> columns = MyCellarFields.getFieldsList();
    nb_colonnes = columns.size();
    colonnes = new MyCellarLabel[nb_colonnes];
    export = new MyCellarCheckBox[nb_colonnes];
    for (int i = 0; i < nb_colonnes; i++) {
      export[i] = new MyCellarCheckBox(Program.getLabel("Infos261"));
      try {
        int I = Program.getCaveConfigInt("SIZE_COL" + i + "EXPORT_XLS",0);
        if (I == 1) {
          export[i].setSelected(true);
        }
        else {
          export[i].setSelected(false);
        }
      }
      catch (NumberFormatException nfe) {
        export[i].setSelected(false);
        Program.putCaveConfigInt("SIZE_COL" + i + "EXPORT_XLS", 0);
      }
      colonnes[i] = new MyCellarLabel(columns.get(i).toString());
     
    }
    jPanel2.setLayout(new MigLayout("","[grow][grow]",""));
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
    jScrollPane1 = new JScrollPane(jPanel2);
    jScrollPane1.setBorder(BorderFactory.createTitledBorder(Program.getLabel("Infos258")));
    this.add(jScrollPane1, "gaptop 15px, grow, wrap");
    this.add(valider, "split 2, center");
    this.add(annuler);
    this.setLocationRelativeTo(null);
  }

  //Accepter et Fermer le message
  /**
   * valider_actionPerformed: Valider les modifications et quitter.
   *
   * @param e ActionEvent
   */
  void valider_actionPerformed(ActionEvent e) {
    try {
      Program.putCaveConfigString("XLS_TITLE", pdf_title.getText());
      Program.putCaveConfigString("TITLE_SIZE_XLS", MyCellarSpinner1.getValue().toString());
      Program.putCaveConfigString("TEXT_SIZE_XLS", MyCellarSpinner3.getValue().toString());
      if (MyCellarCheckBox1.isSelected()) {
        Program.putCaveConfigString("BOLD_XLS", "bold");
      }
      else {
        Program.putCaveConfigString("BOLD_XLS", "");
      }
      for (int i = 0; i < nb_colonnes; i++) {
        if (export[i].isSelected()) {
          Program.putCaveConfigInt("SIZE_COL" + i + "EXPORT_XLS", 1);
        }
        else {
          Program.putCaveConfigInt("SIZE_COL" + i + "EXPORT_XLS", 0);
        }
      }
      this.dispose();
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
