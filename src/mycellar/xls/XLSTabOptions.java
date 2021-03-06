package mycellar.xls;

import mycellar.Program;
import mycellar.Start;
import mycellar.StateEditor;
import mycellar.StateRenderer;
import mycellar.core.Grammar;
import mycellar.core.LabelType;
import mycellar.core.MyCellarButton;
import mycellar.core.MyCellarCheckBox;
import mycellar.core.MyCellarLabel;
import mycellar.core.MyCellarSettings;
import mycellar.core.MyCellarSpinner;
import net.miginfocom.swing.MigLayout;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;


/**
 * <p>Titre : Cave à vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2004</p>
 * <p>Société : Seb Informatique</p>
 * @author Sébastien Duché
 * @version 1.6
 * @since 25/06/20
 */
public class XLSTabOptions extends JDialog {
  private static final int LARGEUR = 480;
  private static final int HAUTEUR = 550;
  private final MyCellarSpinner title_size = new MyCellarSpinner(1, 99);
  private final MyCellarCheckBox boldTitleCheckBox = new MyCellarCheckBox(LabelType.INFO, "257");
  private final MyCellarCheckBox onePlacePerSheetCheckBox = new MyCellarCheckBox(LabelType.INFO_OTHER, "XLSOptions.onePlacePerSheet");
  private final JTextField pdf_title = new JTextField();
  private final MyCellarSpinner text_size = new MyCellarSpinner(1, 99);
  private final MyCellarSpinner column_size = new MyCellarSpinner(1, 99);
  private final MyCellarSpinner empty_line_part = new MyCellarSpinner(1, 99);
  private final MyCellarSpinner empty_line_place = new MyCellarSpinner(1, 99);
  private final MyCellarLabel empty_line_place_label;
  private final XLSOptionsValues tv;
  static final long serialVersionUID = 260706;

  /**
   * XLSOptions: Constructeur pour la fenêtre d'options.
   */
  public XLSTabOptions() {
	  setModal(true);
    tv = new XLSOptionsValues();
    setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    setTitle(Program.getLabel("Infos268"));
    addKeyListener(new KeyAdapter() {
      @Override
      public void keyPressed(KeyEvent e) {
        keylistener_actionPerformed(e);
      }
    });
    addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent e) {dispose();
      }
    });

    setSize(LARGEUR, HAUTEUR);

    final JPanel jPanel1 = new JPanel();
    jPanel1.setBorder(BorderFactory.createEtchedBorder());
    jPanel1.setFont(Program.FONT_PANEL);
    jPanel1.setBorder(BorderFactory.createTitledBorder(Program.getLabel("Infos331")));
    MyCellarLabel MyCellarLabel2 = new MyCellarLabel(Program.getLabel("Infos270")); //Titre du XLS
    String xls_title = Program.getCaveConfigString(MyCellarSettings.XLS_TAB_TITLE, "");
    pdf_title.setText(xls_title);
    MyCellarLabel MyCellarLabel3 = new MyCellarLabel(Program.getLabel("Infos256")); //Taille du texte
    
    onePlacePerSheetCheckBox.addActionListener(e -> updatePlaceSettings(onePlacePerSheetCheckBox.isSelected()));

    title_size.setValue(Program.getCaveConfigInt(MyCellarSettings.TITLE_TAB_SIZE_XLS, 10));
    text_size.setValue(Program.getCaveConfigInt(MyCellarSettings.TEXT_TAB_SIZE_XLS, 10));
    column_size.setValue(Program.getCaveConfigInt(MyCellarSettings.COLUMN_TAB_WIDTH_XLS, 10));
    empty_line_part.setValue(Program.getCaveConfigInt(MyCellarSettings.EMPTY_LINE_PART_XLS, 1));
    empty_line_place.setValue(Program.getCaveConfigInt(MyCellarSettings.EMPTY_LINE_PLACE_XLS, 3));

    if (Program.getCaveConfigBool(MyCellarSettings.BOLD_TAB_XLS, false)) {
      boldTitleCheckBox.setSelected(true);
    }

   
    JTable table = new JTable(tv);
    TableColumnModel tcm = table.getColumnModel();
    TableColumn tc = tcm.getColumn(XLSOptionsValues.ETAT);
    tc.setCellRenderer(new StateRenderer());
    tc.setCellEditor(new StateEditor());
    table.getColumnModel().getColumn(0).setMinWidth(20);
    table.getColumnModel().getColumn(0).setMaxWidth(20);
    table.getColumnModel().getColumn(1).setWidth(440);
    table.setSize(460, 100);
    JScrollPane oScrollPaneTab = new JScrollPane(table);

    tv.addString(Program.getLabel("Infos132"), Program.getCaveConfigBool(MyCellarSettings.XLSTAB_COL0, true));
    tv.addString(Program.getLabel("Infos189"), Program.getCaveConfigBool(MyCellarSettings.XLSTAB_COL1, false));
    tv.addString(Program.getLabel("Infos134"), Program.getCaveConfigBool(MyCellarSettings.XLSTAB_COL2, false));
    tv.addString(Program.getLabel("Infos135"), Program.getCaveConfigBool(MyCellarSettings.XLSTAB_COL3, false));

    final JPanel jPanel2 = new JPanel();
    jPanel2.setFont(Program.FONT_PANEL);
    jPanel2.setBorder(BorderFactory.createTitledBorder(Program.getLabel("Infos332")));
    MyCellarButton valider = new MyCellarButton(Program.getLabel("Main.OK"));
    valider.addActionListener(this::valider_actionPerformed);
    MyCellarButton annuler = new MyCellarButton(Program.getLabel("Infos055"));
    annuler.addActionListener((e) -> dispose());
    MyCellarLabel pt_label1 = new MyCellarLabel("pt");
    MyCellarLabel MyCellarLabel7 = new MyCellarLabel(Program.getLabel("Infos256")); //Taille du texte
    MyCellarLabel pt_label2 = new MyCellarLabel("pt");
    MyCellarLabel column_size_label = new MyCellarLabel(Program.getLabel("Infos333")); //Largeur des colonnes
    MyCellarLabel pt_label3 = new MyCellarLabel("px");
    MyCellarLabel empty_line_part_label = new MyCellarLabel(Program.getLabel("Infos334")); //nb ligne entre partie
    empty_line_place_label = new MyCellarLabel(Program.getLabel("Infos335")); //nb lignes entre rangement
    //Colonnes à utiliser
    MyCellarLabel column_label = new MyCellarLabel(Program.getLabel("Infos338"));

    if (Program.getCaveConfigBool(MyCellarSettings.ONE_PER_SHEET_XLS, false)) {
      updatePlaceSettings(true);
    }

    setLayout(new MigLayout("","grow",""));
    jPanel1.setLayout(new MigLayout("","[][grow]","[][]"));
    jPanel1.add(MyCellarLabel2);
    jPanel1.add(pdf_title, "grow, wrap");
    jPanel1.add(MyCellarLabel3);
    jPanel1.add(title_size, "split 3");
    jPanel1.add(pt_label1, "grow");
    jPanel1.add(boldTitleCheckBox, "align right");
    
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
    jPanel2.add(onePlacePerSheetCheckBox, "wrap");
    jPanel2.add(empty_line_place_label);
    jPanel2.add(empty_line_place);
    add(jPanel2, "grow, wrap");
    add(column_label, "gaptop 10px, wrap");
    add(oScrollPaneTab, "grow, wrap");
    add(valider, "split 2, center, gaptop 15px");
    add(annuler);
    setLocationRelativeTo(Start.getInstance());
  }

  private void updatePlaceSettings(boolean b) {
    onePlacePerSheetCheckBox.setSelected(b);
    empty_line_place_label.setVisible(!b);
    empty_line_place.setVisible(!b);
  }

  /**
   * valider_actionPerformed: Valider les modifications et quitter.
   *
   * @param e ActionEvent
   */
  private void valider_actionPerformed(ActionEvent e) {
    try {
      Program.putCaveConfigString(MyCellarSettings.XLS_TAB_TITLE, pdf_title.getText());
      Program.putCaveConfigString(MyCellarSettings.TITLE_TAB_SIZE_XLS, title_size.getValue().toString());
      Program.putCaveConfigString(MyCellarSettings.TEXT_TAB_SIZE_XLS, text_size.getValue().toString());
      Program.putCaveConfigString(MyCellarSettings.COLUMN_TAB_WIDTH_XLS, column_size.getValue().toString());
      Program.putCaveConfigString(MyCellarSettings.EMPTY_LINE_PART_XLS, empty_line_part.getValue().toString());
      Program.putCaveConfigString(MyCellarSettings.EMPTY_LINE_PLACE_XLS, empty_line_place.getValue().toString());
      Program.putCaveConfigBool(MyCellarSettings.BOLD_TAB_XLS, boldTitleCheckBox.isSelected());
      Program.putCaveConfigBool(MyCellarSettings.ONE_PER_SHEET_XLS, onePlacePerSheetCheckBox.isSelected());

      // Options des colonnes
      for ( int i=0; i<tv.getRowCount(); i++){
        Program.putCaveConfigBool(MyCellarSettings.XLSTAB_COL+i, ((Boolean)tv.getValueAt(i, XLSOptionsValues.ETAT)));
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
  private void keylistener_actionPerformed(KeyEvent e) {
    if (e.getKeyCode() == 'o' || e.getKeyCode() == 'O' || e.getKeyCode() == KeyEvent.VK_ENTER) {
      valider_actionPerformed(null);
    }
  }
}
