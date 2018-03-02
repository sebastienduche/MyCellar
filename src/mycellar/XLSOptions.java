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
import java.util.ArrayList;

/**
 * <p>Titre : Cave à vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2004</p>
 * <p>Société : Seb Informatique</p>
 * @author Sébastien Duché
 * @version 1.2
 * @since 02/03/18
 */
public class XLSOptions extends JDialog {
  private final MyCellarSpinner MyCellarSpinner1 = new MyCellarSpinner();
  private final MyCellarCheckBox MyCellarCheckBox1 = new MyCellarCheckBox();
  private final MyCellarCheckBox export[];
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

    setLayout(new MigLayout("","grow",""));

    JPanel jPanel1 = new JPanel();
    jPanel1.setBorder(BorderFactory.createEtchedBorder());
    jPanel1.setLayout(new MigLayout("","grow",""));
    jPanel1.setFont(Program.font_panel);
    MyCellarLabel MyCellarLabel2 = new MyCellarLabel(Program.getLabel("Infos270")); //Titre du XLS
    String xls_title = Program.getCaveConfigString("XLS_TITLE", "");
    pdf_title.setText(xls_title);
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

    MyCellarSpinner1.setValue(Program.getCaveConfigInt("TITLE_SIZE_XLS",10));
    MyCellarSpinner3.setValue(Program.getCaveConfigInt("TEXT_SIZE_XLS",10));

    MyCellarCheckBox1.setText(Program.getLabel("Infos257")); //gras
    MyCellarCheckBox1.setSelected("bold".equals(Program.getCaveConfigString("BOLD_XLS","")));
    ArrayList<MyCellarFields> columns = MyCellarFields.getFieldsList();
    nb_colonnes = columns.size();
    MyCellarLabel[] colonnes = new MyCellarLabel[nb_colonnes];
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
    JPanel jPanel2 = new JPanel();
    jPanel2.setLayout(new MigLayout("","[grow][grow]",""));
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
    setLocationRelativeTo(null);
  }

  //Accepter et Fermer le message
  /**
   * valider_actionPerformed: Valider les modifications et quitter.
   *
   * @param e ActionEvent
   */
  private void valider_actionPerformed(ActionEvent e) {
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
      dispose();
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
