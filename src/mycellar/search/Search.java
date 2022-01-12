package mycellar.search;

import mycellar.Bouteille;
import mycellar.Erreur;
import mycellar.Export;
import mycellar.ITabListener;
import mycellar.MyCellarImage;
import mycellar.Program;
import mycellar.Start;
import mycellar.TextFieldPopup;
import mycellar.actions.OpenWorkSheetAction;
import mycellar.core.ICutCopyPastable;
import mycellar.core.IMyCellar;
import mycellar.core.IUpdatable;
import mycellar.core.LabelProperty;
import mycellar.core.LabelType;
import mycellar.core.MyCellarObject;
import mycellar.core.MyCellarObjectSwingWorker;
import mycellar.core.MyCellarSettings;
import mycellar.core.UpdateViewType;
import mycellar.core.datas.history.HistoryState;
import mycellar.core.exceptions.MyCellarException;
import mycellar.core.tablecomponents.ButtonCellEditor;
import mycellar.core.tablecomponents.ButtonCellRenderer;
import mycellar.core.tablecomponents.CheckboxCellEditor;
import mycellar.core.tablecomponents.CheckboxCellRenderer;
import mycellar.core.tablecomponents.ToolTipRenderer;
import mycellar.core.uicomponents.MyCellarButton;
import mycellar.core.uicomponents.MyCellarCheckBox;
import mycellar.core.uicomponents.MyCellarComboBox;
import mycellar.core.uicomponents.MyCellarLabel;
import mycellar.core.uicomponents.PopupListener;
import mycellar.core.uicomponents.TabEvent;
import mycellar.general.ProgramPanels;
import mycellar.placesmanagement.PanelPlace;
import mycellar.placesmanagement.Place;
import mycellar.placesmanagement.Rangement;
import mycellar.requester.CollectionFilter;
import mycellar.requester.ui.PanelRequest;
import mycellar.vignobles.CountryVignobleController;
import net.miginfocom.swing.MigLayout;

import javax.swing.JDialog;
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
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseListener;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static mycellar.ProgramConstants.FONT_DIALOG_SMALL;
import static mycellar.ProgramConstants.FONT_PANEL;
import static mycellar.ProgramConstants.SPACE;

/**
 * <p>Titre : Cave &agrave; vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2003</p>
 * <p>Soci&eacute;t&eacute; : Seb Informatique</p>
 *
 * @author S&eacute;bastien Duch&eacute;
 * @version 23.0
 * @since 12/01/22
 */
public final class Search extends JPanel implements Runnable, ITabListener, ICutCopyPastable, IMyCellar, IUpdatable {

  private static final long serialVersionUID = 8497660112193602839L;
  private final SearchTableModel model = new SearchTableModel();
  private final MyCellarLabel txtNbresul = new MyCellarLabel(LabelType.INFO_OTHER, "Search.bottleFound", LabelProperty.PLURAL.withCapital());
  private final MyCellarLabel txtNb = new MyCellarLabel("-");
  private final MyCellarButton suppr = new MyCellarButton(MyCellarImage.DELETE);
  private final MyCellarButton export = new MyCellarButton(MyCellarImage.EXPORT);
  private final MyCellarButton modif = new MyCellarButton(MyCellarImage.WINE);
  private final MyCellarComboBox<String> year = new MyCellarComboBox<>();
  private final MyCellarButton cherche = new MyCellarButton(LabelType.INFO, "084", MyCellarImage.SEARCH); // Cherche
  private final MyCellarButton vider = new MyCellarButton(LabelType.INFO, "220"); // Effacer resultats
  private final char rechercheKey = Program.getLabel("RECHERCHE").charAt(0);
  private final char modificationKey = Program.getLabel("MODIF").charAt(0);
  private final char deleteKey = Program.getLabel("SUPPR").charAt(0);
  private final char exportKey = Program.getLabel("EXPORT").charAt(0);
  private final MyCellarLabel resul_txt = new MyCellarLabel();
  private final MyCellarCheckBox selectall = new MyCellarCheckBox(LabelType.INFO, "126"); // Tout selectionner
  private final MyCellarButton addToWorksheet = new MyCellarButton(MyCellarImage.WORK);
  private final MyCellarCheckBox empty_search = new MyCellarCheckBox(LabelType.INFO, "275"); //Vider automatiquement
  private final MouseListener popup_l = new PopupListener();
  private final JTabbedPane tabbedPane = new JTabbedPane();
  private final PanelYear panelYear = new PanelYear();
  private final PanelRequest panelRequest = new PanelRequest();
  private final PanelPlace panelPlace = new PanelPlace(null, false, false, false, true, false, false, true);
  private JTable table;
  private TextFieldPopup name;
  private boolean alreadyFoundItems = false;
  private boolean updateView = false;
  private UpdateViewType updateViewType;

  public Search() {
    SwingUtilities.invokeLater(() -> {
      Debug("Constructor");

      panelPlace.setModificationDetectionActive(false);
      if (Program.getCaveConfigBool(MyCellarSettings.EMPTY_SEARCH, false)) {
        empty_search.setSelected(true);
      }

      name = new TextFieldPopup(Program.getStorage().getDistinctNames(), 150) {
        @Override
        public void doAfterValidate() {
          new Thread(Search.this).start();
        }
      };

      export.setText(Program.getLabel("Infos120")); // Export the results
      export.setMnemonic(exportKey);
      selectall.setHorizontalAlignment(SwingConstants.RIGHT);
      selectall.setHorizontalTextPosition(SwingConstants.LEFT);

      selectall.addActionListener(this::selectall_actionPerformed);
      addToWorksheet.setText(Program.getLabel("Search.AddWorksheet"));
      addToWorksheet.addActionListener(this::addToWorksheet_actionPerformed);
      empty_search.addActionListener(this::empty_search_actionPerformed);
      export.addActionListener(this::export_actionPerformed);
      suppr.setText(Program.getLabel("Main.Delete"));
      suppr.setMnemonic(deleteKey);
      modif.setText(Program.getLabel("Infos079")); // Modify
      modif.setMnemonic(modificationKey);
      modif.setEnabled(false);
      suppr.setEnabled(false);
      export.setEnabled(false);

      suppr.addActionListener(this::suppr_actionPerformed);

      table = new JTable(model);
      table.setAutoCreateRowSorter(true);
      cherche.addActionListener(this::cherche_actionPerformed);
      cherche.setMnemonic(rechercheKey);
      vider.addActionListener(this::emptyRowsActionPerformed);

      addKeyListener(new KeyAdapter() {
        @Override
        public void keyPressed(KeyEvent e) {
          keylistener_actionPerformed(e);
        }
      });

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
      TableColumn tc = tcm.getColumn(SearchTableModel.ETAT);
      tc.setCellRenderer(new CheckboxCellRenderer());
      tc.setCellEditor(new CheckboxCellEditor());
      tc.setMinWidth(25);
      tc.setMaxWidth(25);
      tc = tcm.getColumn(SearchTableModel.SHOW);
      tc.setCellRenderer(new ButtonCellRenderer());
      tc.setCellEditor(new ButtonCellEditor());
      JScrollPane scrollpane = new JScrollPane(table);
      modif.addActionListener(this::modif_actionPerformed);
      resul_txt.setForeground(Color.red);
      resul_txt.setHorizontalAlignment(SwingConstants.CENTER);
      resul_txt.setFont(FONT_DIALOG_SMALL);

      txtNb.setForeground(Color.red);
      txtNb.setFont(FONT_DIALOG_SMALL);
      txtNbresul.setHorizontalAlignment(SwingConstants.RIGHT);

      tabbedPane.addChangeListener((e) -> {
        JTabbedPane pane = (JTabbedPane) e.getSource();
        if (pane.getSelectedComponent().equals(panelYear)) {
          panelYear.fillYear();
        }
      });

      setLayout(new MigLayout("", "[grow][]", "[]10px[][grow][]"));

      tabbedPane.add(Program.getLabel("Infos077"), new PanelName());
      tabbedPane.add(Program.getLabel("Infos078"), panelPlace);
      tabbedPane.add(Program.getLabel("Infos219"), panelYear);
      tabbedPane.add(Program.getLabel("Infos318"), panelRequest);

      add(tabbedPane, "growx");
      add(new PanelOption(), "wrap");
      add(scrollpane, "grow, wrap, span 2");
      add(addToWorksheet, "alignx left, aligny top");
      add(selectall, "wrap, alignx right, aligny top");
      add(new MyCellarLabel(LabelType.INFO, "080", LabelProperty.SINGLE), "wrap, span 2, alignx center");
      add(resul_txt, "wrap, span 2, alignx center");
      add(modif, "split, span 2, align center");
      add(suppr, "wrap");

      setVisible(true);
      if (name.isVisible()) {
        name.requestFocusInWindow();
      }
    });
  }

  private static void Debug(String sText) {
    Program.Debug("Search: " + sText);
  }

  public void updateTable() {
    SwingUtilities.invokeLater(model::fireTableDataChanged);
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
        // No objet to delete / Select...
        Debug("ERROR: No bottle to delete!");
        Erreur.showInformationMessage(Program.getError("Error064", LabelProperty.SINGLE), Program.getError("Error065", LabelProperty.THE_PLURAL));
        return;
      }
      String erreur_txt1;
      String erreur_txt2;
      if (listToSupp.size() == 1) {
        erreur_txt1 = Program.getError("Error067", LabelProperty.SINGLE); //"1 vin selectionne.
        erreur_txt2 = Program.getError("Error068"); // Delete it ?
      } else {
        erreur_txt1 = MessageFormat.format(Program.getError("Error130", LabelProperty.PLURAL), listToSupp.size()); //vins selectionnes.
        erreur_txt2 = Program.getError("Error131"); //" Delete them ?
      }
      int resul = JOptionPane.showConfirmDialog(this, erreur_txt1 + SPACE + erreur_txt2, Program.getLabel("Infos049"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
      if (resul == JOptionPane.YES_OPTION) {
        SwingUtilities.invokeLater(() -> {
          for (Bouteille bottle : listToSupp) {
            model.removeObject(bottle);
            Program.getStorage().addHistory(HistoryState.DEL, bottle);
            try {
              final Rangement rangement = bottle.getRangement();
              if (rangement != null) {
                rangement.removeObject(bottle);
              } else {
                Program.getStorage().deleteWine(bottle);
              }
            } catch (MyCellarException myCellarException) {
              Program.showException(myCellarException);
            }
            Program.setToTrash(bottle);
            ProgramPanels.removeBottleTab(bottle);
          }

          ProgramPanels.updateCellOrganizerPanel(false);

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

  private void cherche_actionPerformed(ActionEvent e) {
    try {
      Debug("Cherche_actionPerforming...");
      name.removeMenu();
      txtNb.setText("-");
      txtNbresul.setText(Program.getLabel("Search.bottleFound", LabelProperty.SINGLE.withCapital()));
      resul_txt.setText(Program.getLabel("Infos087")); //"Recherche en cours...
      new Thread(this).start();
    } catch (RuntimeException exc) {
      Program.showException(exc);
    }
  }

  private void emptyRowsActionPerformed(ActionEvent e) {
    Debug("vider_actionPerforming...");
    SwingUtilities.invokeLater(this::emptyRows);
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
    Debug("emptyRows Done");
  }

  private void searchByText() {
    Debug("Searching by text with pattern");
    String search = name.getText();
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
    new MyCellarObjectSwingWorker() {
      @Override
      protected List<MyCellarObject> doInBackground() {
        final Pattern p = Pattern.compile(regexToSearch, Pattern.CASE_INSENSITIVE);
        alreadyFoundItems = false;
        List<MyCellarObject> list = new LinkedList<>();
        for (MyCellarObject bottle : Program.getStorage().getAllList()) {
          Matcher m = p.matcher(bottle.getNom());
          if (m.matches()) {
            if (model.hasNotObject(bottle)) {
              list.add(bottle);
            } else {
              alreadyFoundItems = true;
            }
          }
        }
        return list;
      }

      @Override
      protected void done() {
        try {
          final List<MyCellarObject> myCellarObjects = get();
          model.addObjects(myCellarObjects);
          Debug(model.getRowCount() + " object(s) found");
          updateLabelObjectNumber();
          doAfterSearch();
          Debug("Search by text Done");
        } catch (InterruptedException | ExecutionException e) {
          Debug("ERROR: While searching by name");
          Debug(e.getMessage());
        }
      }
    }.execute();
  }

  private StringBuilder replaceCharInSearch(StringBuilder regex, String searchValue, String replaceValue) {
    String search = regex.toString();
    regex = new StringBuilder();
    int index = search.indexOf(searchValue);
    int lastIndex = 0;
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
   * Modify an object
   */
  private void modif_actionPerformed(ActionEvent e) {
    SwingUtilities.invokeLater(() -> {
      try {
        Debug("modif_actionPerforming...");
        int max_row = model.getRowCount();
        int row = 0;
        final LinkedList<MyCellarObject> listToModify = new LinkedList<>();
        do {
          if ((boolean) model.getValueAt(row, SearchTableModel.ETAT)) {
            listToModify.add(model.getBouteille(row));
          }
          row++;
        } while (row < max_row);

        if (listToModify.isEmpty()) {
          //No object to modify / Select...
          Erreur.showInformationMessage(Program.getError("Error071", LabelProperty.SINGLE), Program.getError("Error072", LabelProperty.THE_PLURAL));
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
      alreadyFoundItems = false;
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

  private void doAfterSearch() {
    if (alreadyFoundItems) {
      if (!Program.getCaveConfigBool(MyCellarSettings.DONT_SHOW_INFO, false)) {
        // Don't add object if already in the list
        Erreur.showInformationMessageWithKey(Program.getError("Error133", LabelProperty.A_SINGLE), Program.getError("Error134"), MyCellarSettings.DONT_SHOW_INFO);
      }
    }
    alreadyFoundItems = false;
    resul_txt.setText(Program.getLabel("Infos088")); //"Recherche terminee.
    if (model.getRowCount() > 0) {
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
    StringBuilder sb = new StringBuilder();
    panelRequest.getPredicates().forEach(p -> sb.append(p.toString()));
    Debug(sb.toString());
    new MyCellarObjectSwingWorker() {
      @Override
      protected List<MyCellarObject> doInBackground() {
        if (Program.isWineType()) {
          CountryVignobleController.rebuild();
        }
        Collection<? extends MyCellarObject> objects = CollectionFilter.select(Program.getStorage().getAllList(), panelRequest.getPredicates()).getResults();
        List<MyCellarObject> list = new LinkedList<>();
        if (objects != null) {
          for (MyCellarObject b : objects) {
            if (model.hasNotObject(b)) {
              list.add(b);
            } else {
              alreadyFoundItems = true;
            }
          }
        }
        return list;
      }

      @Override
      protected void done() {
        try {
          final List<MyCellarObject> myCellarObjects = get();
          model.addObjects(myCellarObjects);
          Debug(model.getRowCount() + " object(s) found");
          updateLabelObjectNumber();
          doAfterSearch();
          Debug("Search by request Done");
        } catch (InterruptedException | ExecutionException e) {
          Debug("ERROR: While searching by request");
          Debug(e.getMessage());
        }
      }
    }.execute();
  }

  private void updateLabelObjectNumber() {
    txtNb.setText(Integer.toString(model.getRowCount()));
    txtNbresul.setText(Program.getLabel("Search.bottleFound", new LabelProperty(model.getRowCount() > 1).withCapital()));
  }

  private void searchByPlace() {
    Debug("Searching by place");
    if (!panelPlace.performValidation(false, this)) {
      enableDefaultButtons();
      return;
    }
    final Place selectedPlace = panelPlace.getSelectedPlace();

    Rangement rangement = selectedPlace.getRangement();
    List<MyCellarObject> myCellarObjectList = new LinkedList<>();
    if (rangement.isSimplePlace()) {
      //Pour la caisse
      int lieu_num = selectedPlace.getPlaceNumIndex();
      int nb_empl_cave = rangement.getNbParts();
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
        int totalCellUsed = rangement.getTotalCellUsed(x - 1);
        for (int l = 0; l < totalCellUsed; l++) {
          MyCellarObject b = rangement.getObjectSimplePlaceAt(x - 1, l); //lieu_num
          if (b != null) {
            if (model.hasNotObject(b)) {
              myCellarObjectList.add(b);
            } else {
              alreadyFoundItems = true;
            }
          } else {
            Debug("No object found in " + rangement.getName() + ": x-1=" + (x - 1) + " l+1=" + (l + 1));
          }
        } //Fin for
      } //Fin for
    } else {
      //Type armoire
      if (!panelPlace.isSeveralLocationChecked()) {
        final MyCellarObject myCellarObject = rangement.getObject(selectedPlace).orElse(null);
        if (myCellarObject == null) {
          resul_txt.setText(Program.getLabel("Infos224")); //"Echec de la recherche.
          Erreur.showSimpleErreur(Program.getError("Error066", LabelProperty.SINGLE)); //Aucun objet trouve
          txtNb.setText("0");
          txtNbresul.setText(Program.getLabel("Search.bottleFound", LabelProperty.SINGLE.withCapital()));
          modif.setEnabled(false);
          suppr.setEnabled(false);
        } else {
          if (model.hasNotObject(myCellarObject)) {
            myCellarObjectList.add(myCellarObject);
          } else {
            alreadyFoundItems = true;
          }
        }
      } else {
        // Search all objects
        int placeNumStart = 0;
        int placeNumEnd = rangement.getNbParts();
        if (panelPlace.isSeveralLocationStatePartChecked()) {
          placeNumStart = selectedPlace.getPlaceNumIndex();
          placeNumEnd = selectedPlace.getPlaceNum();
        }
        int lineStart = 0;
        int lineEnd = 0;
        if (panelPlace.isSeveralLocationStateLineChecked()) {
          placeNumStart = selectedPlace.getPlaceNumIndex();
          placeNumEnd = selectedPlace.getPlaceNum();
          lineStart = selectedPlace.getLineIndex();
          lineEnd = selectedPlace.getLine();
        }
        for (int i = placeNumStart; i < placeNumEnd; i++) {
          int nb_lignes = rangement.getLineCountAt(i);
          if (!panelPlace.isSeveralLocationStateLineChecked()) {
            lineEnd = nb_lignes;
          }
          for (int j = lineStart; j < lineEnd; j++) {
            int nb_colonnes = rangement.getColumnCountAt(i, j);
            for (int k = 0; k < nb_colonnes; k++) {
              MyCellarObject myCellarObject = rangement.getObject(i, j, k).orElse(null);
              if (myCellarObject != null) {
                if (model.hasNotObject(myCellarObject)) {
                  myCellarObjectList.add(myCellarObject);
                } else {
                  alreadyFoundItems = true;
                }
              }
            }
          }
        }
      }
    }

    SwingUtilities.invokeLater(() -> {
      model.addObjects(myCellarObjectList);
      final int rowCount = model.getRowCount();
      Debug(rowCount + " object(s) found");
      updateLabelObjectNumber();
      doAfterSearch();
    });
  }

  private void searchByYear() {
    Debug("Searching by year");
    new MyCellarObjectSwingWorker() {

      @Override
      protected List<MyCellarObject> doInBackground() {
        String selectedYear = "";
        if (year.getSelectedItem() != null) {
          selectedYear = year.getSelectedItem().toString();
        }
        int annee;
        if (Bouteille.isNonVintageYear(selectedYear)) {
          annee = Bouteille.NON_VINTAGE_INT;
        } else {
          annee = Program.safeParseInt(selectedYear, 0); // It will be 0 for 'Others'
        }

        List<MyCellarObject> list = new ArrayList<>();
        for (MyCellarObject b : Program.getStorage().getAllList()) {
          if (annee == b.getAnneeInt()) {
            if (model.hasNotObject(b)) {
              list.add(b);
            } else {
              alreadyFoundItems = true;
            }
          }
        }
        return list;
      }

      @Override
      protected void done() {
        try {
          final List<MyCellarObject> myCellarObjects = get();
          model.addObjects(myCellarObjects);
          Debug(model.getRowCount() + " object(s) found");
          updateLabelObjectNumber();
          doAfterSearch();
          Debug("Searching by year Done");
        } catch (InterruptedException | ExecutionException e) {
          Debug("ERROR: While searching by year");
          Debug(e.getMessage());
        }
      }
    }.execute();
  }

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
        model.setValueAt(selectall.isSelected(), i, SearchTableModel.ETAT);
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
      Erreur.showInformationMessage(Program.getError("Error.NoWineSelected", LabelProperty.SINGLE));
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
      if ((boolean) model.getValueAt(i, SearchTableModel.ETAT)) {
        list.add(model.getBouteille(i));
      }
    }
    return list;
  }

  private void empty_search_actionPerformed(ActionEvent e) {
    Program.putCaveConfigBool(MyCellarSettings.EMPTY_SEARCH, empty_search.isSelected());
  }

  @Override
  public boolean tabWillClose(TabEvent event) {
    return true;
  }

  @Override
  public void tabClosed() {
    Start.getInstance().updateMainPanel();
  }

  @Override
  public void setUpdateView(UpdateViewType updateViewType) {
    updateView = true;
    this.updateViewType = updateViewType;
  }

  @Override
  public void updateView() {
    if (!updateView) {
      return;
    }
    updateView = false;
    panelYear.fillYear();
    if (updateViewType == UpdateViewType.PLACE || updateViewType == UpdateViewType.ALL) {
      panelPlace.updateView();
    }
  }

  public void removeObject(MyCellarObject myCellarObject) {
    SwingUtilities.invokeLater(() -> {
      model.removeObject(myCellarObject);
      updateLabelObjectNumber();
    });
  }

  @Override
  public void cut() {
    if (tabbedPane.getSelectedIndex() == 0) {
      String text = name.getSelectedText();
      if (text != null) {
        String fullText = name.getText();
        name.setText(fullText.substring(0, name.getSelectionStart()) + fullText.substring(name.getSelectionEnd()));
        Program.CLIPBOARD.copy(text);
      }
    }
  }

  @Override
  public void copy() {
    if (tabbedPane.getSelectedIndex() == 0) {
      Program.CLIPBOARD.copy(name.getSelectedText());
    }
  }

  @Override
  public void paste() {
    if (tabbedPane.getSelectedIndex() == 0) {
      String fullText = name.getText();
      name.setText(fullText.substring(0, name.getSelectionStart()) + Program.CLIPBOARD.paste() + fullText.substring(name.getSelectionEnd()));
    }
  }

  private final class PanelName extends JPanel {
    private static final long serialVersionUID = -2125241372841734287L;

    private PanelName() {
      name.setEditable(true);
      name.addMouseListener(popup_l);
      name.setFont(FONT_PANEL);
      setLayout(new MigLayout("", "[grow]", "[]"));
      add(new MyCellarLabel(LabelType.INFO, "085"), "wrap");
      add(name, "grow");
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
      int[] years = Program.getYearsArray();
      for (int s : years) {
        if (s > 1000 && s < 9000) {
          year.addItem(Integer.toString(s));
        }
      }
      year.addItem(Program.getLabel("Infos390")); // NV
      year.addItem(Program.getLabel("Infos225")); // Autre
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
