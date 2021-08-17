package mycellar;

import mycellar.actions.OpenWorkSheetAction;
import mycellar.core.ICutCopyPastable;
import mycellar.core.IMyCellar;
import mycellar.core.IUpdatable;
import mycellar.core.LabelProperty;
import mycellar.core.LabelType;
import mycellar.core.MyCellarButton;
import mycellar.core.MyCellarCheckBox;
import mycellar.core.MyCellarComboBox;
import mycellar.core.MyCellarException;
import mycellar.core.MyCellarLabel;
import mycellar.core.MyCellarObject;
import mycellar.core.MyCellarSettings;
import mycellar.core.PopupListener;
import mycellar.core.TabEvent;
import mycellar.core.datas.history.HistoryState;
import mycellar.core.tablecomponents.ButtonCellEditor;
import mycellar.core.tablecomponents.ButtonCellRenderer;
import mycellar.core.tablecomponents.CheckboxCellEditor;
import mycellar.core.tablecomponents.CheckboxCellRenderer;
import mycellar.core.tablecomponents.ToolTipRenderer;
import mycellar.general.ProgramPanels;
import mycellar.placesmanagement.Rangement;
import mycellar.placesmanagement.RangementUtils;
import mycellar.requester.CollectionFilter;
import mycellar.requester.ui.PanelRequest;
import mycellar.vignobles.CountryVignobleController;
import net.miginfocom.swing.MigLayout;

import javax.swing.JDialog;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import java.awt.Color;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseListener;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>Titre : Cave &agrave; vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2003</p>
 * <p>Soci&eacute;t&eacute; : Seb Informatique</p>
 *
 * @author S&eacute;bastien Duch&eacute;
 * @version 22.3
 * @since 21/05/21
 */
public final class Search extends JPanel implements Runnable, ITabListener, ICutCopyPastable, IMyCellar, IUpdatable {

  private static final long serialVersionUID = 8497660112193602839L;
  private final TableValues model = new TableValues();
  private final MyCellarLabel txtNbresul = new MyCellarLabel(LabelType.INFO_OTHER, "Search.bottleFound", LabelProperty.PLURAL.withCapital()); //"Bouteille(s) trouvee(s): ");
  private final MyCellarLabel txtNb = new MyCellarLabel("-");
  private final MyCellarButton suppr = new MyCellarButton(MyCellarImage.DELETE);
  private final MyCellarButton export = new MyCellarButton(MyCellarImage.EXPORT);
  private final MyCellarButton modif = new MyCellarButton(MyCellarImage.WINE);
  private final MyCellarComboBox<Rangement> lieu = new MyCellarComboBox<>();
  private final MyCellarComboBox<String> num_lieu = new MyCellarComboBox<>();
  private final MyCellarComboBox<String> column = new MyCellarComboBox<>();
  private final MyCellarComboBox<String> line = new MyCellarComboBox<>();
  private final MyCellarComboBox<String> year = new MyCellarComboBox<>();
  private final MyCellarLabel label3 = new MyCellarLabel(LabelType.INFO, "081"); // Emplacement
  private final MyCellarLabel label4 = new MyCellarLabel(LabelType.INFO, "082"); // Numero lieu
  private final MyCellarLabel label5 = new MyCellarLabel(LabelType.INFO, "028"); // Ligne
  private final MyCellarLabel label6 = new MyCellarLabel(LabelType.INFO, "083"); // Colonne
  private final MyCellarButton cherche = new MyCellarButton(LabelType.INFO, "084", MyCellarImage.SEARCH); // Cherche
  private final MyCellarButton vider = new MyCellarButton(LabelType.INFO, "220"); // Effacer resultats
  private final char rechercheKey = Program.getLabel("RECHERCHE").charAt(0);
  private final char modificationKey = Program.getLabel("MODIF").charAt(0);
  private final char deleteKey = Program.getLabel("SUPPR").charAt(0);
  private final char exportKey = Program.getLabel("EXPORT").charAt(0);
  private final MyCellarLabel resul_txt = new MyCellarLabel();
  private final MyCellarCheckBox multi = new MyCellarCheckBox(LabelType.INFO_OTHER, "Search.AllBottlesInPlace", LabelProperty.PLURAL);
  private final String label_empl = Program.getLabel("Search.AllBottlesInPlace", LabelProperty.PLURAL); //"Tous les vins de l'emplacement");
  private final String label_num_empl = Program.getLabel("Search.AllBottlesInPart", LabelProperty.PLURAL); //"Tous les vins du lieu");
  private final String label_ligne = Program.getLabel("Search.AllBottlesInLine", LabelProperty.PLURAL); //"Tous les vins de la ligne");
  private final MyCellarCheckBox selectall = new MyCellarCheckBox(LabelType.INFO, "126"); // Tout selectionner
  private final MyCellarButton addToWorksheet = new MyCellarButton(MyCellarImage.WORK);
  private final MyCellarCheckBox empty_search = new MyCellarCheckBox(LabelType.INFO, "275"); //Vider automatiquement
  private final MouseListener popup_l = new PopupListener();
  private final JTabbedPane tabbedPane = new JTabbedPane();
  private final PanelYear panelYear = new PanelYear();
  private final PanelRequest panelRequest = new PanelRequest();
  private final JMenuItem moveLine = new JMenuItem(Program.getLabel("Infos365"));
  private JTable table;
  private TextFieldPopup name;
  private boolean updateView = false;
  private AllBottlesState allBottlesState = AllBottlesState.PLACE;

  /**
   * Search: Constructeur via Start.
   */
  public Search() {
    SwingUtilities.invokeLater(() -> {
      Debug("Constructor");

      if (Program.getCaveConfigBool(MyCellarSettings.EMPTY_SEARCH, false)) {
        empty_search.setSelected(true);
      }

      name = new TextFieldPopup(Program.getStorage().getDistinctNames(), 150) {
        @Override
        public void doAfterValidate() {
          new Thread(Search.this).start();
        }
      };

      //Ajout des lieux
      lieu.removeAllItems();
      lieu.addItem(Program.EMPTY_PLACE);
      Program.getCave().forEach(lieu::addItem);

      export.setText(Program.getLabel("Infos120")); //"Exporter le resultat");
      export.setMnemonic(exportKey);
      selectall.setHorizontalAlignment(SwingConstants.RIGHT);
      selectall.setHorizontalTextPosition(SwingConstants.LEFT);

      selectall.addActionListener(this::selectall_actionPerformed);
      addToWorksheet.setText(Program.getLabel("Search.AddWorksheet"));
      addToWorksheet.addActionListener(this::addToWorksheet_actionPerformed);
      empty_search.addActionListener(this::empty_search_actionPerformed);
      export.addActionListener(this::export_actionPerformed);
      suppr.setText(Program.getLabel("Main.Delete")); //"Supprimer");
      suppr.setMnemonic(deleteKey);
      MyCellarLabel infoLabel = new MyCellarLabel(LabelType.INFO, "080", LabelProperty.SINGLE); //"Selectionner un(des) vin(s) dans la liste. Cliquer sur \"Modifier\" ou \"Supprimer\"");
      modif.setText(Program.getLabel("Infos079")); //"Modifier");
      modif.setMnemonic(modificationKey);
      modif.setEnabled(false);
      suppr.setEnabled(false);
      export.setEnabled(false);

      suppr.addActionListener(this::suppr_actionPerformed);

      table = new JTable(model);
      table.setAutoCreateRowSorter(true);
      cherche.addActionListener(this::cherche_actionPerformed);
      cherche.setMnemonic(rechercheKey);
      vider.addActionListener(this::vider_actionPerformed);
      line.addItemListener(this::line_itemStateChanged);
      lieu.addItemListener(this::lieu_itemStateChanged);

      addKeyListener(new KeyAdapter() {
        @Override
        public void keyPressed(KeyEvent e) {
          keylistener_actionPerformed(e);
        }
      });

      num_lieu.addItemListener(this::num_lieu_itemStateChanged);
      TableColumnModel tcm = table.getColumnModel();
      TableColumn[] tc1 = new TableColumn[5];
      for (int w = 0; w < 5; w++) {
        tc1[w] = tcm.getColumn(w);
        tc1[w].setCellRenderer(new ToolTipRenderer());
        switch (w) {
          case 1:
            tc1[w].setMinWidth(150);
            break;
          case 2:
            tc1[w].setMinWidth(50);
            break;
          case 3:
            tc1[w].setMinWidth(100);
            break;
          default:
            tc1[w].setMinWidth(30);
            break;
        }
      }
      TableColumn tc = tcm.getColumn(TableValues.ETAT);
      tc.setCellRenderer(new CheckboxCellRenderer());
      tc.setCellEditor(new CheckboxCellEditor());
      tc.setMinWidth(25);
      tc.setMaxWidth(25);
      tc = tcm.getColumn(TableValues.SHOW);
      tc.setCellRenderer(new ButtonCellRenderer());
      tc.setCellEditor(new ButtonCellEditor());
      JScrollPane scrollpane = new JScrollPane(table);
      modif.addActionListener(this::modif_actionPerformed);
      resul_txt.setForeground(Color.red);
      resul_txt.setHorizontalAlignment(SwingConstants.CENTER);
      resul_txt.setFont(Program.FONT_DIALOG_SMALL);
      multi.addItemListener(this::multi_itemStateChanged);

      multi.setText(label_empl);
      allBottlesState = AllBottlesState.PLACE;
      txtNb.setForeground(Color.red);
      txtNb.setFont(Program.FONT_DIALOG_SMALL);
      txtNbresul.setHorizontalAlignment(SwingConstants.RIGHT);
      multi.setEnabled(false);

      tabbedPane.addChangeListener((e) -> {
        JTabbedPane pane = (JTabbedPane) e.getSource();
        if (pane.getSelectedComponent().equals(panelYear)) {
          panelYear.fillYear();
        }
      });

      moveLine.addActionListener((e) -> new MoveLine());

      num_lieu.setEnabled(false);
      column.setEnabled(false);
      line.setEnabled(false);

      setLayout(new MigLayout("", "[grow][]", "[]10px[][grow][]"));

      tabbedPane.add(Program.getLabel("Infos077"), new PanelName());
      tabbedPane.add(Program.getLabel("Infos078"), new PanelPlace());
      tabbedPane.add(Program.getLabel("Infos219"), panelYear);
      tabbedPane.add(Program.getLabel("Infos318"), panelRequest);

      add(tabbedPane, "growx");
      add(new PanelOption(), "wrap");
      add(scrollpane, "grow, wrap, span 2");
      add(addToWorksheet, "alignx left, aligny top");
      add(selectall, "wrap, alignx right, aligny top");
      add(infoLabel, "wrap, span 2, alignx center");
      add(resul_txt, "wrap, span 2, alignx center");
      add(modif, "split, span 2, align center");
      add(suppr, "wrap");

      setVisible(true);
      if (name.isVisible()) {
        name.requestFocusInWindow();
      }

      Start.getInstance().menuTools.add(moveLine);
    });
  }

  /**
   * Debug
   *
   * @param sText String
   */
  private static void Debug(String sText) {
    Program.Debug("Search: " + sText);
  }

  /**
   * export_actionPerformed: Fonction pour l'export du resultat de la recherche.
   *
   * @param e ActionEvent
   */
  private void export_actionPerformed(ActionEvent e) {
    try {
      Debug("Exporting...");
      List<MyCellarObject> v = model.getDatas();
      Export expor = new Export(v);
      JDialog dialog = new JDialog();
      dialog.add(expor);
      dialog.pack();
      dialog.setTitle(Program.getLabel("Infos151"));
      dialog.setLocationRelativeTo(Start.getInstance());
      dialog.setModal(true);
      dialog.setVisible(true);
      Debug("Export Done");
    } catch (RuntimeException exc) {
      Program.showException(exc);
    }
  }

  /**
   * suppr_actionPerformed: Fonction pour la suppression de bouteilles.
   *
   * @param e ActionEvent
   */
  private void suppr_actionPerformed(ActionEvent e) {
    try {
      Debug("Deleting...");
      final LinkedList<Bouteille> listToSupp = getSelectedBouteilles();

      if (listToSupp.isEmpty()) {
        //"Aucun vin a supprimer! / Veuillez selectionner les vins a supprimer.");
        Debug("ERROR: No bottle to delete!");
        Erreur.showSimpleErreur(Program.getError("Error064", LabelProperty.SINGLE), Program.getError("Error065", LabelProperty.THE_PLURAL), true);
        return;
      }
      String erreur_txt1;
      String erreur_txt2;
      if (listToSupp.size() == 1) {
        erreur_txt1 = Program.getError("Error067", LabelProperty.SINGLE); //"1 vin selectionne.");
        erreur_txt2 = Program.getError("Error068"); //"Voulez-vous le supprimer?");
      } else {
        erreur_txt1 = MessageFormat.format(Program.getError("Error130", LabelProperty.PLURAL), listToSupp.size()); //vins selectionnes.");
        erreur_txt2 = Program.getError("Error131"); //"Voulez-vous les supprimer?");
      }
      int resul = JOptionPane.showConfirmDialog(this, erreur_txt1 + " " + erreur_txt2, Program.getLabel("Infos049"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
      if (resul == JOptionPane.YES_OPTION) {
        SwingUtilities.invokeLater(() -> {
          for (Bouteille bottle : listToSupp) {
            model.removeBouteille(bottle);
            Program.getStorage().addHistory(HistoryState.DEL, bottle);
            try {
              Program.getStorage().deleteWine(bottle);
            } catch (MyCellarException myCellarException) {
              Program.showException(myCellarException);
            }
            Program.setToTrash(bottle);
            ProgramPanels.removeBottleTab(bottle);
          }

          RangementUtils.putTabStock();
          ProgramPanels.updateManagePlacePanel();

          if (listToSupp.size() == 1) {
            resul_txt.setText(Program.getLabel("Search.1ItemDeleted", LabelProperty.SINGLE));
          } else {
            resul_txt.setText(MessageFormat.format(Program.getLabel("Search.NItemDeleted", LabelProperty.PLURAL), listToSupp.size()));
          }
        });
      }
      Debug("Deleting Done");
    } catch (HeadlessException e1) {
      Debug("ERROR: Why this error? " + e1.getMessage());
      Program.showException(e1);
    } catch (RuntimeException exc) {
      Program.showException(exc);
    }
  }

  private void updateMultiCheckboxState() {
    if (num_lieu.getSelectedIndex() == 0) {
      multi.setText(label_empl);
      allBottlesState = AllBottlesState.PLACE;
    } else if (line.getSelectedIndex() == 0) {
      multi.setText(label_num_empl);
      allBottlesState = AllBottlesState.PART;
    } else {
      multi.setText(label_ligne);
      allBottlesState = AllBottlesState.LINE;
    }
  }

  /**
   * num_lieu_itemStateChanged: Fonction appellee lors d'un changement dans la
   * liste des numeros de lieu.
   *
   * @param e ItemEvent
   */
  private void num_lieu_itemStateChanged(ItemEvent e) {
    SwingUtilities.invokeLater(() -> {
      try {
        Debug("Num_lieu_itemStateChanging...");
        int num_select = num_lieu.getSelectedIndex();
        int lieu_select = lieu.getSelectedIndex();

        multi.setSelected(false);
        num_lieu.setEnabled(true);
        line.setEnabled(num_select > 0);
        column.setEnabled(false);

        resul_txt.setText("");
        int nb_ligne = 0;
        Rangement rangement = lieu.getItemAt(lieu_select);
        if (num_select > 0) {
          nb_ligne = rangement.getNbLignes(num_select - 1);
        }
        line.removeAllItems();
        column.removeAllItems();
        line.addItem("");
        for (int i = 1; i <= nb_ligne; i++) {
          line.addItem(Integer.toString(i));
        }
        updateMultiCheckboxState();
      } catch (RuntimeException exc) {
        Program.showException(exc);
      }
    });
  }

  /**
   * lieu_itemStateChanged: Fonction appellee lors d'un changement dans la liste
   * des emplacements.
   *
   * @param e ItemEvent
   */
  private void lieu_itemStateChanged(ItemEvent e) {
    SwingUtilities.invokeLater(() -> {
      try {
        Debug("Lieu_itemStateChanging...");
        int lieu_select = lieu.getSelectedIndex();
        Rangement rangement = lieu.getItemAt(lieu_select);

        multi.setEnabled(false);
        num_lieu.setEnabled(lieu_select > 0);

        line.setEnabled(false);
        column.setEnabled(false);

        multi.setSelected(false);
        resul_txt.setText("");
        if (lieu_select > 0) {
          multi.setEnabled(true);

          num_lieu.removeAllItems();
          final boolean caisse = rangement.isCaisse();
          if (caisse) {
            multi.setEnabled(false);
            num_lieu.addItem(Program.getLabel("Infos223")); //"Toutes");
            for (int i = 0; i < rangement.getNbEmplacements(); i++) {
              num_lieu.addItem(Integer.toString(i + rangement.getStartCaisse()));
            }
            label4.setText(Program.getLabel("Infos158")); //"Numero de caisse");
          } else {
            line.removeAllItems();
            column.removeAllItems();
            num_lieu.addItem("");
            for (int i = 1; i <= rangement.getNbEmplacements(); i++) {
              num_lieu.addItem(Integer.toString(i));
            }
            label4.setText(Program.getLabel("Infos082")); //"Numero du lieu");
          }
          line.setVisible(!caisse);
          column.setVisible(!caisse);
          label6.setVisible(!caisse);
          label5.setVisible(!caisse);
          updateMultiCheckboxState();
        }
      } catch (RuntimeException exc) {
        Program.showException(exc);
      }
    });
  }

  /**
   * line_itemStateChanged: Fonction appellee lors d'un changement dans la liste
   * des numeros de ligne.
   *
   * @param e ItemEvent
   */
  private void line_itemStateChanged(ItemEvent e) {
    SwingUtilities.invokeLater(() -> {
      try {
        Debug("Line_itemStateChanging...");
        int nb_col = 0;
        int num_select = line.getSelectedIndex();
        int emplacement = num_lieu.getSelectedIndex();
        int lieu_select = lieu.getSelectedIndex();
        Rangement rangement = lieu.getItemAt(lieu_select);

        multi.setSelected(false);
        column.setEnabled(num_select > 0);

        resul_txt.setText("");
        if (num_select > 0) {
          nb_col = rangement.getNbColonnes(emplacement - 1, num_select - 1);
        }
        column.removeAllItems();
        column.addItem("");
        for (int i = 1; i <= nb_col; i++) {
          column.addItem(Integer.toString(i));
        }
        updateMultiCheckboxState();
      } catch (RuntimeException exc) {
        Program.showException(exc);
      }
    });
  }


  /**
   * cherche_actionPerformed: Fonction de recherche.
   *
   * @param e ActionEvent
   */
  private void cherche_actionPerformed(ActionEvent e) {
    try {
      Debug("Cherche_actionPerforming...");
      name.removeMenu();
      txtNb.setText("-");
      txtNbresul.setText(Program.getLabel("Search.bottleFound", LabelProperty.SINGLE.withCapital()));
      resul_txt.setText(Program.getLabel("Infos087")); //"Recherche en cours...");
      new Thread(this).start();
    } catch (RuntimeException exc) {
      Program.showException(exc);
    }
  }

  /**
   * vider_actionPerformed: Fonction pour vider la liste de recherche.
   *
   * @param e ActionEvent
   */
  private void vider_actionPerformed(ActionEvent e) {
    try {
      //Efface la recherche
      Debug("vider_actionPerforming...");
      SwingUtilities.invokeLater(this::emptyRows);
    } catch (RuntimeException ex) {
      Program.showException(ex);
    }
  }

  private void emptyRows() {
    Debug("emptyRows...");
    txtNb.setText("-");
    txtNbresul.setText(Program.getLabel("Search.bottleFound", LabelProperty.SINGLE.withCapital()));
    modif.setEnabled(false);
    suppr.setEnabled(false);
    export.setEnabled(false);
    selectall.setSelected(false);
    addToWorksheet.setEnabled(false);
    resul_txt.setText("");
    model.removeAll();
    Debug("emptyRows End");
  }

  /**
   * searchByText: Fonction interne de recherche par nom.
   */
  private void searchByText() {
    Debug("Searching by text with pattern");
    //Recherche saisie
    String search = name.getText();
//		model.removeAll();
    Debug("Preparing statement...");

    StringBuilder regex = new StringBuilder(search);
    regex = replaceCharInSearch(regex, "*", ".{0,}");
    regex = replaceCharInSearch(regex, "?", ".{1}");
    // Replace $ in the regexp
    regex = replaceCharInSearch(regex, "$", "\\$");
    regex = replaceCharInSearch(regex, "^", "\\^");
    regex = replaceCharInSearch(regex, "(", "\\(");
    regex = replaceCharInSearch(regex, ")", "\\)");
    regex = replaceCharInSearch(regex, "\\", "\\\\");

    final String regexToSearch = regex.toString();
    Debug("Searching with regexp: " + regexToSearch);
    final Pattern p = Pattern.compile(regexToSearch, Pattern.CASE_INSENSITIVE);
    boolean already_found = false;
    List<MyCellarObject> bouteillesToAdd = new LinkedList<>();
    for (MyCellarObject bottle : Program.getStorage().getAllList()) {
      Matcher m = p.matcher(bottle.getNom());
      if (m.matches()) {
        if (model.hasNotBottle(bottle)) {
          bouteillesToAdd.add(bottle);
        } else {
          already_found = true;
        }
      }
    }
    boolean finalAlready_found = already_found;
    SwingUtilities.invokeLater(() -> {
      bouteillesToAdd.forEach(model::addBouteille);
      int nRows = model.getRowCount();
      updateLabelBottleNumber();
      if (nRows > 0) {
        modif.setEnabled(true);
        suppr.setEnabled(true);
      }
      resul_txt.setText(Program.getLabel("Infos088")); //"Recherche terminee.");
      doAfterSearch(finalAlready_found);
    });
  }

  private StringBuilder replaceCharInSearch(StringBuilder regex, String searchValue, String replaceValue) {
    int lastIndex = 0;
    String search = regex.toString();
    regex = new StringBuilder();
    int index = search.indexOf(searchValue);
    while (index != -1) {
      regex.append(search, lastIndex, index);
      regex.append(replaceValue);
      lastIndex = index + 1;
      index = search.indexOf(searchValue, index + 1);
    }
    regex.append(search.substring(lastIndex));
    return regex;
  }


  /**
   * modif_actionPerformed: Fonction appellee lors d'une modification de
   * bouteilles.
   *
   * @param e ActionEvent
   */
  private void modif_actionPerformed(ActionEvent e) {
    SwingUtilities.invokeLater(() -> {
      try {
        Debug("modif_actionPerforming...");
        int max_row = model.getRowCount();
        int row = 0;
        final LinkedList<MyCellarObject> listToModify = new LinkedList<>();
        do {
          if ((boolean) model.getValueAt(row, TableValues.ETAT)) {
            listToModify.add(model.getBouteille(row));
          }
          row++;
        } while (row < max_row);

        if (listToModify.isEmpty()) {
          //"Aucun vin a modifier! / Veuillez selectionner les vins a modifier.");
          Erreur.showSimpleErreur(Program.getError("Error071", LabelProperty.SINGLE), Program.getError("Error072", LabelProperty.THE_PLURAL), true);
        } else {
          Debug("Modifying " + listToModify.size() + " object(s)...");
          Program.modifyBottles(listToModify);
        }
      } catch (RuntimeException exc) {
        Program.showException(exc);
      }
    });
  }

  /**
   * multi_itemStateChanged: Fonction pour activer la recheche sur plusieurs
   * lieu / numero de lieu / ligne.
   *
   * @param e ItemEvent
   */
  private void multi_itemStateChanged(ItemEvent e) {
    if (multi.isSelected()) {
      if (line.getSelectedIndex() > 0) {
        column.setEnabled(false);
      } else if (num_lieu.getSelectedIndex() > 0) {
        column.setEnabled(false);
        line.setEnabled(false);
      } else if (lieu.getSelectedIndex() > 0) {
        column.setEnabled(false);
        line.setEnabled(false);
        num_lieu.setEnabled(false);
      }
    } else {
      if (lieu.getSelectedIndex() != 0) {
        num_lieu.setEnabled(true);
      }
      if (num_lieu.getSelectedIndex() != 0) {
        line.setEnabled(true);
      }
      if (line.getSelectedIndex() != 0) {
        column.setEnabled(true);
      }
    }
  }

  /**
   * run: Realise la recherche et l'affiche dans la JTable
   */
  @Override
  public void run() {
    try {
      Debug("Running...");
      cherche.setEnabled(false);
      vider.setEnabled(false);
      export.setEnabled(false);
      selectall.setSelected(false);
      selectall.setEnabled(false);
      addToWorksheet.setEnabled(false);
      if (empty_search.isSelected()) {
        emptyRows();
      }
      if (tabbedPane.getSelectedIndex() == 0) {
        searchByText();
      } else if (tabbedPane.getSelectedIndex() == 1) {
        searchByPlace();
      } else if (tabbedPane.getSelectedIndex() == 2) {
        searchByYear();
      } else if (tabbedPane.getSelectedIndex() == 3) {
        searchByRequest();
      }
    } catch (RuntimeException exc) {
      Program.showException(exc);
    }
  }

  private void doAfterSearch(boolean already_found) {
    if (already_found) {
      if (!Program.getCaveConfigBool(MyCellarSettings.DONT_SHOW_INFO, false)) {
        //"Lorsqu'une bouteille recherchee est deja presente dans la liste");
        //"des vins trouves, elle n'est pas ajoutee en double.");
        Erreur.showKeyErreur(Program.getError("Error133", LabelProperty.A_SINGLE), Program.getError("Error134"), MyCellarSettings.DONT_SHOW_INFO);
      }
    }
    resul_txt.setText(Program.getLabel("Infos088")); //"Recherche terminee.");
    if (model.getRowCount() > 0) {
      SwingUtilities.invokeLater(model::fireTableDataChanged);
      export.setEnabled(true);
      modif.setEnabled(true);
      suppr.setEnabled(true);
    }
    enableDefaultButtons();
  }

  private void enableDefaultButtons() {
    cherche.setEnabled(true);
    vider.setEnabled(true);
    selectall.setEnabled(true);
    addToWorksheet.setEnabled(true);
  }

  private void searchByRequest() {
    Debug("Search by request");
    if (Program.isWineType()) {
      CountryVignobleController.rebuild();
    }
    Collection<? extends MyCellarObject> bouteilles = CollectionFilter.select(Program.getStorage().getAllList(), panelRequest.getPredicates()).getResults();
    boolean already_found = false;
    List<MyCellarObject> bouteilleList = new LinkedList<>();
    if (bouteilles != null) {
      for (MyCellarObject b : bouteilles) {
        if (model.hasNotBottle(b)) {
          bouteilleList.add(b);
        } else {
          already_found = true;
        }
      }
    }

    StringBuilder sb = new StringBuilder();
    panelRequest.getPredicates().forEach(p -> sb.append(p.toString()));
    Debug(sb.toString());
    boolean finalAlready_found = already_found;
    SwingUtilities.invokeLater(() -> {
      bouteilleList.forEach(model::addBouteille);
      Debug(model.getRowCount() + " bottle(s) found");
      updateLabelBottleNumber();
      doAfterSearch(finalAlready_found);
      Debug("Search by request Done");
    });
  }

  private void updateLabelBottleNumber() {
    txtNb.setText(Integer.toString(model.getRowCount()));
    txtNbresul.setText(Program.getLabel("Search.bottleFound", new LabelProperty(model.getRowCount() > 1).withCapital()));
  }

  private void searchByPlace() {
    Debug("Searching by place");
    int lieu_select = lieu.getSelectedIndex();

    if (lieu_select == 0) {
      Debug("ERROR: No place selected");
      Erreur.showSimpleErreur(Program.getError("Error055")); //Select emplacement
      resul_txt.setText("");
      enableDefaultButtons();
      return;
    }

    Rangement rangement = lieu.getItemAt(lieu_select);
    boolean already_found = false;
    List<MyCellarObject> bouteilleList = new LinkedList<>();
    if (rangement.isCaisse()) {
      //Pour la caisse
      int lieu_num = num_lieu.getSelectedIndex();
      resul_txt.setText(Program.getLabel("Infos087")); //"Recherche en cours...");
      int nb_empl_cave = rangement.getNbEmplacements();
      int boucle_toutes;
      int start_boucle;
      if (lieu_num == 0) { //New
        start_boucle = 1;
        boucle_toutes = nb_empl_cave + 1;
      } else {
        start_boucle = lieu_num;
        boucle_toutes = lieu_num + 1;
      }
      for (int x = start_boucle; x < boucle_toutes; x++) {
        int nb_bottles = rangement.getNbCaseUse(x - 1);
        for (int l = 0; l < nb_bottles; l++) {
          MyCellarObject b = rangement.getBouteilleCaisseAt(x - 1, l); //lieu_num
          if (b != null) {
            if (model.hasNotBottle(b)) {
              bouteilleList.add(b);
            } else {
              already_found = true;
            }
          } else {
            Debug("No bottle found in lieuselect-1=" + (lieu_select - 1) + " x-1=" + (x - 1) + " l+1=" + (l + 1));
          }
        } //Fin for
      } //Fin for
    } else {
      //Type armoire
      if (!multi.isSelected()) {
        int lieu_num = num_lieu.getSelectedIndex();
        int ligne = line.getSelectedIndex();
        int colonne = column.getSelectedIndex();
        if (lieu_num == 0) {
          Debug("ERROR: No Num place selected");
          resul_txt.setText("");
          Erreur.showSimpleErreur(Program.getError("Error056")); //"Veuillez selectionner un numero d'emplacement!";
          enableDefaultButtons();
          return;
        }
        if (ligne == 0) {
          Debug("ERROR: No Line selected");
          resul_txt.setText("");
          Erreur.showSimpleErreur(Program.getError("Error057")); //"Veuillez selectionner un numero de ligne!";
          enableDefaultButtons();
          return;
        }
        if (colonne == 0) {
          Debug("ERROR: No column selected");
          resul_txt.setText("");
          Erreur.showSimpleErreur(Program.getError("Error058")); //"Veuillez selectionner un numero de colonne!";
          enableDefaultButtons();
          return;
        }
        Optional<MyCellarObject> b = rangement.getBouteille(lieu_num - 1, ligne - 1, colonne - 1);
        resul_txt.setText(Program.getLabel("Infos087")); //"Recherche en cours...");

        if (b.isEmpty()) {
          resul_txt.setText(Program.getLabel("Infos224")); //"Echec de la recherche.");
          Erreur.showSimpleErreur(Program.getError("Error066", LabelProperty.SINGLE)); //Aucune bouteille trouve
          txtNb.setText("0");
          txtNbresul.setText(Program.getLabel("Search.bottleFound", LabelProperty.SINGLE.withCapital()));
          modif.setEnabled(false);
          suppr.setEnabled(false);
        } else {
          final MyCellarObject bouteille = b.get();
          if (model.hasNotBottle(bouteille)) {
            bouteilleList.add(bouteille);
          } else {
            already_found = true;
          }
        }
      } else { //multi.getState == true
        //Cas recherche toutes bouteille (lieu, num_lieu, ligne)
        int lieu_num = num_lieu.getSelectedIndex();
        int ligne = line.getSelectedIndex();
        if (allBottlesState != AllBottlesState.PLACE) {
          if (lieu_num == 0) {
            Debug("ERROR: No Num place selected");
            Erreur.showSimpleErreur(Program.getError("Error056")); //"Veuillez selectionner un numero d'emplacement!";
            resul_txt.setText("");
            enableDefaultButtons();
            return;
          }

          if (allBottlesState != AllBottlesState.PART) {
            if (ligne == 0) {
              Debug("ERROR: No line selected");
              Erreur.showSimpleErreur(Program.getError("Error057")); //"Veuillez selectionner un numero de ligne!";
              resul_txt.setText("");
              enableDefaultButtons();
              return;
            }
          }
        }
        resul_txt.setText(Program.getLabel("Infos087")); //"Recherche en cours...");
        //Recherche toutes les bouteilles d'un emplacement
        int nb_empl = rangement.getNbEmplacements();
        int i_deb = 1;
        int j_deb = 1;
        int i_fin = nb_empl;
        int j_fin = 0;
        if (allBottlesState == AllBottlesState.PART) {
          i_deb = lieu_num;
          i_fin = lieu_num;
        }
        if (allBottlesState == AllBottlesState.LINE) {
          i_deb = lieu_num;
          i_fin = lieu_num;
          j_deb = ligne;
          j_fin = ligne;
        }
        for (int i = i_deb; i <= i_fin; i++) {
          int nb_lignes = rangement.getNbLignes(i - 1);
          if (allBottlesState != AllBottlesState.LINE) {
            j_fin = nb_lignes;
          }
          for (int j = j_deb; j <= j_fin; j++) {
            int nb_colonnes = rangement.getNbColonnes(i - 1, j - 1);
            for (int k = 1; k <= nb_colonnes; k++) {
              Optional<MyCellarObject> b = rangement.getBouteille(i - 1, j - 1, k - 1);
              if (b.isPresent()) {
                final MyCellarObject bouteille = b.get();
                //Ajout de la bouteille dans la liste si elle n'y ait pas deja
                if (model.hasNotBottle(bouteille)) {
                  bouteilleList.add(bouteille);
                } else {
                  already_found = true;
                }
              }
            }
          }
        }
      } //Fin else multi
    } //fin else

    boolean finalAlready_found = already_found;
    SwingUtilities.invokeLater(() -> {
      bouteilleList.forEach(model::addBouteille);
      final int rowCount = model.getRowCount();
      Debug(rowCount + " bottle(s) found");
      updateLabelBottleNumber();

      if (rowCount > 0) {
        modif.setEnabled(true);
        suppr.setEnabled(true);
      }
      resul_txt.setText(Program.getLabel("Infos088")); //"Recherche terminee.");
      doAfterSearch(finalAlready_found);
    });

  }

  private void searchByYear() {
    Debug("Searching by year");
    int item_select = year.getSelectedIndex();
    int nb_year = year.getItemCount();
    String sYear = "";
    if (year.getSelectedItem() != null) {
      sYear = year.getSelectedItem().toString();
    }
    int annee;
    if (Bouteille.isNonVintageYear(sYear)) {
      annee = Bouteille.NON_VINTAGE_INT;
    } else {
      annee = Program.safeParseInt(sYear, 0);
    }

    resul_txt.setText(Program.getLabel("Infos087")); //"Recherche en cours...");
    boolean already_found = false;
    List<MyCellarObject> bouteilleList = new ArrayList<>();
    for (MyCellarObject b : Program.getStorage().getAllList()) {
      if (b == null) {
        continue;
      }
      //Recuperation du numero du lieu
      Rangement rangement = b.getRangement();

      if (annee == b.getAnneeInt() && nb_year != item_select && rangement != null) {
        if (model.hasNotBottle(b)) {
          bouteilleList.add(b);
        } else {
          already_found = true;
        }
      } else {
        if (b.getAnneeInt() < 1000 && (nb_year - 1) == item_select) { // Cas Autre
          if (model.hasNotBottle(b)) {
            bouteilleList.add(b);
          } else {
            already_found = true;
          }
        }
      }
    }
    boolean finalAlready_found = already_found;
    SwingUtilities.invokeLater(() -> {
      bouteilleList.forEach(model::addBouteille);
      Debug(model.getRowCount() + " bottle(s) found");
      updateLabelBottleNumber();
      doAfterSearch(finalAlready_found);
    });
  }

  /**
   * keylistener_actionPerformed: Ecoute clavier.
   *
   * @param e KeyEvent
   */
  private void keylistener_actionPerformed(KeyEvent e) {
    if ((e.getKeyCode() == rechercheKey && e.isControlDown()) || e.getKeyCode() == KeyEvent.VK_ENTER) {
      cherche_actionPerformed(null);
    }
    if (e.getKeyCode() == modificationKey && modif.isEnabled() && e.isControlDown()) {
      modif_actionPerformed(null);
    }
    if (e.getKeyCode() == deleteKey && suppr.isEnabled() && e.isControlDown()) {
      suppr_actionPerformed(null);
    }
    if (e.getKeyCode() == exportKey && export.isEnabled() && e.isControlDown()) {
      export_actionPerformed(null);
    }
    if (e.getKeyCode() == KeyEvent.VK_F1) {
      Program.getAide();
    }
    if (e.getKeyCode() == KeyEvent.VK_C) {
      cut();
    }
    if (e.getKeyCode() == KeyEvent.VK_X) {
      copy();
    }
    if (e.getKeyCode() == KeyEvent.VK_V) {
      paste();
    }
  }

  /**
   * selectall_actionPerformed: Permet de selectionner toutes les lignes de la
   * JTable
   *
   * @param e ActionEvent
   */
  private void selectall_actionPerformed(ActionEvent e) {
    SwingUtilities.invokeLater(() -> {
      Debug("selectall_actionPerforming...");
      modif.setEnabled(false);
      suppr.setEnabled(false);
      for (int i = 0; i < model.getRowCount(); i++) {
        model.setValueAt(selectall.isSelected(), i, TableValues.ETAT);
      }
      if (model.getRowCount() > 0) {
        modif.setEnabled(true);
        suppr.setEnabled(true);
      }
      table.updateUI();
      Debug("selectall_actionPerforming... Done");
    });
  }

  /**
   * addToWorksheet_actionPerformed: Permet d'ajouter des bouteilles a la feuille de travail
   *
   * @param e ActionEvent
   */
  private void addToWorksheet_actionPerformed(ActionEvent e) {
    Debug("addToWorksheet_actionPerforming...");
    final LinkedList<Bouteille> list = getSelectedBouteilles();

    if (list.isEmpty()) {
      Erreur.showSimpleErreur(Program.getError("Error.NoWineSelected", LabelProperty.SINGLE), true);
      return;
    }
    new OpenWorkSheetAction(list).actionPerformed(null);
    Debug("addToWorksheet_actionPerforming... Done");
  }

  private LinkedList<Bouteille> getSelectedBouteilles() {
    int max_row = model.getRowCount();
    final LinkedList<Bouteille> list = new LinkedList<>();
    // Recuperation du nombre de lignes selectionnees
    for (int i = 0; i < max_row; i++) {
      if ((boolean) model.getValueAt(i, TableValues.ETAT)) {
        list.add(model.getBouteille(i));
      }
    }
    return list;
  }

  /**
   * empty_search_actionPerformed: Permet de vider automatiquement la recherche
   *
   * @param e ActionEvent
   */
  private void empty_search_actionPerformed(ActionEvent e) {
    Program.putCaveConfigBool(MyCellarSettings.EMPTY_SEARCH, empty_search.isSelected());
  }

  @Override
  public boolean tabWillClose(TabEvent event) {
    return true;
  }

  @Override
  public void tabClosed() {
    Start.getInstance().menuTools.remove(moveLine);
    Start.getInstance().updateMainPanel();
  }

  @Override
  public void setUpdateView() {
    updateView = true;
  }

  @Override
  public void updateView() {
    if (!updateView) {
      return;
    }
    updateView = false;
    lieu.removeAllItems();
    lieu.addItem(Program.EMPTY_PLACE);
    Program.getCave().forEach(lieu::addItem);
    panelYear.fillYear();
  }

  public void removeBottle(MyCellarObject bottleToDelete) {
    SwingUtilities.invokeLater(() -> {
      model.removeBouteille(bottleToDelete);
      updateLabelBottleNumber();
    });
  }

  public void updateTable() {
    SwingUtilities.invokeLater(model::fireTableDataChanged);
  }

  void clearResults() {
    SwingUtilities.invokeLater(model::removeAll);
  }

  @Override
  public void cut() {
    if (tabbedPane.getSelectedIndex() == 0) {
      String text = name.getSelectedText();
      if (text != null) {
        String fullText = name.getText();
        name.setText(fullText.substring(0, name.getSelectionStart()) + fullText.substring(name.getSelectionEnd()));
        Program.CLIPBOARD.copier(text);
      }
    }
  }

  @Override
  public void copy() {
    if (tabbedPane.getSelectedIndex() == 0) {
      String text = name.getSelectedText();
      if (text != null) {
        Program.CLIPBOARD.copier(text);
      }
    }
  }

  @Override
  public void paste() {
    if (tabbedPane.getSelectedIndex() == 0) {
      String fullText = name.getText();
      name.setText(fullText.substring(0, name.getSelectionStart()) + Program.CLIPBOARD.coller() + fullText.substring(name.getSelectionEnd()));
    }
  }

  private enum AllBottlesState {
    PLACE,
    PART,
    LINE
  }

  private final class PanelName extends JPanel {
    private static final long serialVersionUID = -2125241372841734287L;

    private PanelName() {
      name.setEditable(true);
      name.addMouseListener(popup_l);
      name.setFont(Program.FONT_PANEL);
      setLayout(new MigLayout("", "[grow]", "[]"));
      add(new MyCellarLabel(LabelType.INFO, "085"), "wrap");
      add(name, "grow");
    }
  }

  private final class PanelPlace extends JPanel {
    private static final long serialVersionUID = -2601861017578176513L;

    private PanelPlace() {
      setLayout(new MigLayout("", "[grow]", "[][][][]"));
      add(label3);
      add(label4);
      add(label5);
      add(label6, "wrap");
      add(lieu);
      add(num_lieu);
      add(line);
      add(column, "wrap");
      add(multi, "span 4");
    }
  }

  private final class PanelYear extends JPanel {
    private static final long serialVersionUID = 8579611890313378015L;

    private PanelYear() {
      setLayout(new MigLayout());
      MyCellarLabel labelYear = new MyCellarLabel(LabelType.INFO, "133");
      add(labelYear, "wrap");
      add(year);
    }

    private void fillYear() {
      year.removeAllItems();
      int[] an_array = Program.getAnnees();
//			String[] mes_string = new String[an_array.length];
//			for (int y = 0; y < an_array.length; y++) {
//				mes_string[y] = Integer.toString(an_array[y]);
//			}
//			Arrays.sort(mes_string, Collator.getInstance());
      for (int s : an_array) {
        if (s > 1000 && s < 9000) {
          year.addItem(Integer.toString(s));
        }
      }
      year.addItem(Program.getLabel("Infos390")); //NV
      year.addItem(Program.getLabel("Infos225")); //"Autre");
    }
  }

  private final class PanelOption extends JPanel {
    private static final long serialVersionUID = 6761656985728428915L;

    private PanelOption() {
      setLayout(new MigLayout("", "", "[][]"));
      add(cherche, "wrap");
      add(vider);
      add(export, "wrap");
      add(empty_search, "wrap, span 2");
      add(txtNbresul, "split");
      add(txtNb, "wrap");
    }
  }

}
