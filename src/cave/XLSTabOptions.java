package Cave;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import Cave.core.MyCellarButton;
import Cave.core.MyCellarCheckBox;
import Cave.core.MyCellarLabel;
import Cave.core.MyCellarSpinner;
import net.miginfocom.swing.MigLayout;


/**
 * <p>Titre : Cave à vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2004</p>
 * <p>Société : Seb Informatique</p>
 * @author Sébastien Duché
 * @version 0.8
 * @since 13/11/16
 */
public class XLSTabOptions extends JDialog {
  private int LARGEUR = 480;
  private int HAUTEUR = 550;
  private JPanel jPanel1 = new JPanel();
  private MyCellarLabel MyCellarLabel2 = new MyCellarLabel();
  private MyCellarLabel MyCellarLabel3 = new MyCellarLabel();
  private MyCellarSpinner title_size = new MyCellarSpinner();
  private MyCellarCheckBox MyCellarCheckBox1 = new MyCellarCheckBox();
  private JPanel jPanel2 = new JPanel();
  private MyCellarButton valider = new MyCellarButton();
  private MyCellarButton annuler = new MyCellarButton();
  private JTextField pdf_title = new JTextField();
  private MyCellarLabel pt_label1 = new MyCellarLabel();
  private MyCellarLabel MyCellarLabel7 = new MyCellarLabel();
  private MyCellarSpinner text_size = new MyCellarSpinner();
  private MyCellarLabel pt_label2 = new MyCellarLabel();
  private MyCellarLabel column_size_label = new MyCellarLabel();
  private MyCellarSpinner column_size = new MyCellarSpinner();
  private MyCellarLabel pt_label3 = new MyCellarLabel();
  private MyCellarLabel empty_line_part_label = new MyCellarLabel();
  private MyCellarSpinner empty_line_part = new MyCellarSpinner();
  private MyCellarLabel empty_line_place_label = new MyCellarLabel();
  private MyCellarSpinner empty_line_place = new MyCellarSpinner();
  private JScrollPane oScrollPaneTab = new JScrollPane();
  private MyCellarLabel column_label = new MyCellarLabel();
  private JTable table;
  public XLSOptionsValues tv;
  static final long serialVersionUID = 260706;

  /**
   * XLSOptions: Constructeur pour la fenêtre d'options.
   * @param nb_colonnes1 int
   */
  public XLSTabOptions() {
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
    tv = new XLSOptionsValues();
    this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    this.setTitle(Program.getLabel("Infos268"));
    this.addKeyListener(new java.awt.event.KeyListener() {
      public void keyReleased(java.awt.event.KeyEvent e) {}

      public void keyPressed(java.awt.event.KeyEvent e) {
        keylistener_actionPerformed(e);
      }

      public void keyTyped(java.awt.event.KeyEvent e) {}
    });
    this.addWindowListener(new java.awt.event.WindowAdapter() {
      public void windowClosing(WindowEvent e) {annuler_actionPerformed(null);
      }
    });

    setSize(LARGEUR, HAUTEUR);
    
    jPanel1.setBorder(BorderFactory.createEtchedBorder());
    jPanel1.setFont(Program.font_panel);
    jPanel1.setBorder(BorderFactory.createTitledBorder(Program.getLabel("Infos331")));
    MyCellarLabel2.setText(Program.getLabel("Infos270")); //Titre du XLS
    String xls_title = Program.getCaveConfigString("XLS_TAB_TITLE", "");
    pdf_title.setText(xls_title);
    MyCellarLabel3.setText(Program.getLabel("Infos256")); //Taille du texte
    title_size.addChangeListener(new javax.swing.event.ChangeListener() {
      public void stateChanged(javax.swing.event.ChangeEvent e) {
        if (Integer.parseInt(title_size.getValue().toString()) <= 0) {
          title_size.setValue(new Integer(1));
        }
      }
    });
    text_size.addChangeListener(new javax.swing.event.ChangeListener() {
      public void stateChanged(javax.swing.event.ChangeEvent e) {
        if (Integer.parseInt(text_size.getValue().toString()) <= 0) {
          text_size.setValue(new Integer(1));
        }
      }
    });
    column_size.addChangeListener(new javax.swing.event.ChangeListener() {
      public void stateChanged(javax.swing.event.ChangeEvent e) {
        if (Integer.parseInt(column_size.getValue().toString()) <= 0) {
          column_size.setValue(new Integer(1));
        }
      }
    });
    empty_line_part.addChangeListener(new javax.swing.event.ChangeListener() {
      public void stateChanged(javax.swing.event.ChangeEvent e) {
        if (Integer.parseInt(empty_line_part.getValue().toString()) <= 0) {
          empty_line_part.setValue(new Integer(1));
        }
      }
    });
    empty_line_place.addChangeListener(new javax.swing.event.ChangeListener() {
      public void stateChanged(javax.swing.event.ChangeEvent e) {
        if (Integer.parseInt(empty_line_place.getValue().toString()) <= 0) {
          empty_line_place.setValue(new Integer(1));
        }
      }
    });

    title_size.setValue(new Integer(Program.getCaveConfigInt("TITLE_TAB_SIZE_XLS", 10)));
    text_size.setValue(new Integer(Program.getCaveConfigInt("TEXT_TAB_SIZE_XLS", 10)));
    column_size.setValue(new Integer(Program.getCaveConfigInt("COLUMN_TAB_WIDTH_XLS", 10)));
    empty_line_part.setValue(new Integer(Program.getCaveConfigInt("EMPTY_LINE_PART_XLS", 1)));
    empty_line_place.setValue(new Integer(Program.getCaveConfigInt("EMPTY_LINE_PLACE_XLS", 3)));

    MyCellarCheckBox1.setText(Program.getLabel("Infos257")); //gras
    String bold = Program.getCaveConfigString("BOLD_TAB_XLS","");
    if (bold.equals("bold")) {
      MyCellarCheckBox1.setSelected(true);
    }
   
    table = new JTable(tv);
    TableColumnModel tcm = table.getColumnModel();
    TableColumn tc = tcm.getColumn(XLSOptionsValues.ETAT);
    tc.setCellRenderer(new StateRenderer());
    tc.setCellEditor(new StateEditor());
    table.getColumnModel().getColumn(0).setMinWidth(20);
    table.getColumnModel().getColumn(0).setMaxWidth(20);
    table.getColumnModel().getColumn(1).setWidth(440);
    table.setSize(460, 100);
    oScrollPaneTab = new JScrollPane(table);

    if( Program.getCaveConfigInt("XLSTAB_COL0", 1) == 1 ){
      tv.addString(Program.getLabel("Infos132"), true);
    }else{
      tv.addString(Program.getLabel("Infos132"), false);
    }
    if( Program.getCaveConfigInt("XLSTAB_COL1", 0) == 1 ){
      tv.addString(Program.getLabel("Infos133"), true);
    }else{
      tv.addString(Program.getLabel("Infos133"), false);
    }
    if( Program.getCaveConfigInt("XLSTAB_COL2", 0) == 1 ){
      tv.addString(Program.getLabel("Infos134"), true);
    }else{
      tv.addString(Program.getLabel("Infos134"), false);
    }
    if( Program.getCaveConfigInt("XLSTAB_COL3", 0) == 1 ){
      tv.addString(Program.getLabel("Infos135"), true);
    }else{
      tv.addString(Program.getLabel("Infos135"), false);
    }

    jPanel2.setFont(Program.font_panel);
    jPanel2.setBorder(BorderFactory.createTitledBorder(Program.getLabel("Infos332")));
    valider.setText("OK");
    valider.addActionListener((e) -> valider_actionPerformed(e));
    annuler.setText(Program.getLabel("Infos055"));
    annuler.addActionListener((e) -> annuler_actionPerformed(e));
    pt_label1.setText("pt");
    MyCellarLabel7.setText(Program.getLabel("Infos256")); //Taille du texte
    pt_label2.setText("pt");
    column_size_label.setText(Program.getLabel("Infos333")); //Largeur des colonnes
    pt_label3.setText("pt");
    empty_line_part_label.setText(Program.getLabel("Infos334")); //nb ligne entre partie
    empty_line_place_label.setText(Program.getLabel("Infos335")); //nb lignes entre rangement
    column_label.setText(Program.getLabel("Infos338")); //Colonnes à utiliser
    
    setLayout(new MigLayout("","grow",""));
    jPanel1.setLayout(new MigLayout("","[][grow]","[][]"));
    jPanel1.add(MyCellarLabel2);
    jPanel1.add(pdf_title, "grow, wrap");
    jPanel1.add(MyCellarLabel3);
    jPanel1.add(title_size, "split 3");
    jPanel1.add(pt_label1, "grow");
    jPanel1.add(MyCellarCheckBox1, "align right");
    
    add(jPanel1, "grow, wrap");
    
    jPanel2.setLayout(new MigLayout("","[][]",""));
    jPanel2.add(MyCellarLabel7);
    jPanel2.add(text_size, "split 2");
    jPanel2.add(pt_label2, "wrap");
    jPanel2.add(column_size_label);
    jPanel2.add(column_size,"split 2");
    jPanel2.add(pt_label3, "wrap");
    jPanel2.add(empty_line_part_label);
    jPanel2.add(empty_line_part, "wrap");
    jPanel2.add(empty_line_place_label);
    jPanel2.add(empty_line_place);
    add(jPanel2, "grow, wrap");
    add(column_label, "gaptop 10px, wrap");
    add(oScrollPaneTab, "grow, wrap");
    add(valider, "split 2, center, gaptop 15px");
    add(annuler);
    setLocationRelativeTo(null);
  }

  //Accepter et Fermer le message
  /**
   * valider_actionPerformed: Valider les modifications et quitter.
   *
   * @param e ActionEvent
   */
  void valider_actionPerformed(ActionEvent e) {
    try {
      Program.putCaveConfigString("XLS_TAB_TITLE", pdf_title.getText());
      Program.putCaveConfigString("TITLE_TAB_SIZE_XLS", title_size.getValue().toString());
      Program.putCaveConfigString("TEXT_TAB_SIZE_XLS", text_size.getValue().toString());
      Program.putCaveConfigString("COLUMN_TAB_WIDTH_XLS", column_size.getValue().toString());
      Program.putCaveConfigString("EMPTY_LINE_PART_XLS", empty_line_part.getValue().toString());
      Program.putCaveConfigString("EMPTY_LINE_PLACE_XLS", empty_line_place.getValue().toString());
      if (MyCellarCheckBox1.isSelected()) {
        Program.putCaveConfigString("BOLD_TAB_XLS", "bold");
      }
      else {
        Program.putCaveConfigString("BOLD_TAB_XLS", "");
      }

      // Options des colonnes
      for ( int i=0; i<tv.getRowCount(); i++){
        int nVal = 0;
        if ( ((Boolean)tv.getValueAt(i, XLSOptionsValues.ETAT)).booleanValue() )
          nVal = 1;
        Program.putCaveConfigInt("XLSTAB_COL"+i, new Integer(nVal));
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

  void annuler_actionPerformed(ActionEvent e) {
    this.dispose();
  }

}
