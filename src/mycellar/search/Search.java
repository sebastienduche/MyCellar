package mycellar.search;

import mycellar.Bouteille;
import mycellar.Erreur;
import mycellar.Export;
import mycellar.ITabListener;
import mycellar.MyCellarUtils;
import mycellar.Program;
import mycellar.TextFieldPopup;
import mycellar.actions.OpenAddVinAction;
import mycellar.actions.OpenWorkSheetAction;
import mycellar.core.ICutCopyPastable;
import mycellar.core.IMyCellar;
import mycellar.core.IMyCellarObject;
import mycellar.core.IUpdatable;
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
import mycellar.core.uicomponents.MyCellarSimpleLabel;
import mycellar.core.uicomponents.PopupListener;
import mycellar.frame.MainFrame;
import mycellar.general.ProgramPanels;
import mycellar.general.ResourceKey;
import mycellar.placesmanagement.PanelPlacePosition;
import mycellar.placesmanagement.places.AbstractPlace;
import mycellar.placesmanagement.places.ComplexPlace;
import mycellar.placesmanagement.places.PlacePosition;
import mycellar.placesmanagement.places.SimplePlace;
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
import java.io.Serial;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static mycellar.MyCellarImage.DELETE;
import static mycellar.MyCellarImage.EXPORT;
import static mycellar.MyCellarImage.SEARCH;
import static mycellar.MyCellarImage.WINE;
import static mycellar.MyCellarImage.WORK;
import static mycellar.ProgramConstants.DASH;
import static mycellar.ProgramConstants.FONT_DIALOG_BOLD;
import static mycellar.ProgramConstants.FONT_PANEL;
import static mycellar.core.text.MyCellarLabelManagement.getError;
import static mycellar.core.text.MyCellarLabelManagement.getLabel;
import static mycellar.general.ResourceErrorKey.ERROR_1ITEMSELECTED;
import static mycellar.general.ResourceErrorKey.ERROR_CONFIRM1DELETE;
import static mycellar.general.ResourceErrorKey.ERROR_CONFIRMNDELETE;
import static mycellar.general.ResourceErrorKey.ERROR_DONTADDTWICE;
import static mycellar.general.ResourceErrorKey.ERROR_NITEMSSELECTED;
import static mycellar.general.ResourceErrorKey.ERROR_NOITEMFOUND;
import static mycellar.general.ResourceErrorKey.ERROR_NOITEMTODELETE;
import static mycellar.general.ResourceErrorKey.ERROR_NOITEMTOMODIFY;
import static mycellar.general.ResourceErrorKey.ERROR_NOWINESELECTED;
import static mycellar.general.ResourceErrorKey.ERROR_PLEASESELECT;
import static mycellar.general.ResourceErrorKey.ERROR_SELECTITEMTOMODIFY;
import static mycellar.general.ResourceKey.EXPORT_EXPORTFORMAT;
import static mycellar.general.ResourceKey.MAIN_DELETE;
import static mycellar.general.ResourceKey.MAIN_MODIFY;
import static mycellar.general.ResourceKey.MAIN_NV;
import static mycellar.general.ResourceKey.MAIN_OTHER;
import static mycellar.general.ResourceKey.MAIN_SEARCH;
import static mycellar.general.ResourceKey.MAIN_SELECTALL;
import static mycellar.general.ResourceKey.MODIF;
import static mycellar.general.ResourceKey.RECHERCHE;
import static mycellar.general.ResourceKey.SEARCH_1ITEMDELETED;
import static mycellar.general.ResourceKey.SEARCH_ADDWORKSHEET;
import static mycellar.general.ResourceKey.SEARCH_ADVANCEDSEARCH;
import static mycellar.general.ResourceKey.SEARCH_BOTTLEFOUND;
import static mycellar.general.ResourceKey.SEARCH_BYNAME;
import static mycellar.general.ResourceKey.SEARCH_BYSTORAGE;
import static mycellar.general.ResourceKey.SEARCH_BYYEAR;
import static mycellar.general.ResourceKey.SEARCH_CLEAR;
import static mycellar.general.ResourceKey.SEARCH_CLEARALL;
import static mycellar.general.ResourceKey.SEARCH_COMPLETED;
import static mycellar.general.ResourceKey.SEARCH_EXPORT;
import static mycellar.general.ResourceKey.SEARCH_FAILED;
import static mycellar.general.ResourceKey.SEARCH_INPROGRESS;
import static mycellar.general.ResourceKey.SEARCH_NAME;
import static mycellar.general.ResourceKey.SEARCH_NITEMDELETED;
import static mycellar.general.ResourceKey.SEARCH_SELECTROWS;
import static mycellar.general.ResourceKey.SEARCH_YEAR;
import static mycellar.general.ResourceKey.SUPPR;

/**
 * Titre : Cave &agrave; vin
 * Description : Votre description
 * Copyright : Copyright (c) 2003
 * Soci&eacute;t&eacute; : Seb Informatique
 *
 * @author S&eacute;bastien Duch&eacute;
 * @version 25.4
 * @since 03/04/25
 */
public final class Search extends JPanel implements Runnable, ITabListener, ICutCopyPastable, IMyCellar, IUpdatable {

  @Serial
  private static final long serialVersionUID = 8497660112193602839L;
  private final SearchTableModel searchTableModel = new SearchTableModel();
  private final MyCellarLabel objectFoundCountLabels = new MyCellarLabel(SEARCH_BOTTLEFOUND,"");
  private final MyCellarSimpleLabel countLabel = new MyCellarSimpleLabel(DASH);
  private final MyCellarButton deleteButton = new MyCellarButton(MAIN_DELETE, DELETE);
  private final MyCellarButton exportButton = new MyCellarButton(SEARCH_EXPORT, EXPORT);
  private final MyCellarButton modifyButton = new MyCellarButton(MAIN_MODIFY, WINE);
  private final MyCellarComboBox<String> year = new MyCellarComboBox<>();
  private final MyCellarButton searchButton = new MyCellarButton(MAIN_SEARCH, SEARCH);
  private final MyCellarButton emptyRowsButton = new MyCellarButton(SEARCH_CLEAR);
  private final char searchKey = getLabel(RECHERCHE).charAt(0);
  private final char modificationKey = getLabel(MODIF).charAt(0);
  private final char deleteKey = getLabel(SUPPR).charAt(0);
  private final char exportKey = getLabel(ResourceKey.EXPORT).charAt(0);
  private final MyCellarSimpleLabel resultInfoLabel = new MyCellarSimpleLabel();
  private final MyCellarCheckBox selectAllCheck = new MyCellarCheckBox(MAIN_SELECTALL);
  private final MyCellarButton addToWorksheetButton = new MyCellarButton(SEARCH_ADDWORKSHEET, WORK);
  private final MyCellarCheckBox emptySearchCheck = new MyCellarCheckBox(SEARCH_CLEARALL);
  private final MouseListener popupListener = new PopupListener();
  private final JTabbedPane tabbedPane = new JTabbedPane();
  private final PanelYear panelYear = new PanelYear();
  private final PanelRequest panelRequest = new PanelRequest();
  private final PanelPlacePosition panelPlace = new PanelPlacePosition(null, false, false, false, true, false, false, true);
  private JTable table;
  private TextFieldPopup searchByName;
  private boolean alreadyFoundItems = false;
  private boolean updateView = false;
  private UpdateViewType updateViewType;

  public Search() {
    SwingUtilities.invokeLater(() -> {
      Debug("Constructor");

      panelPlace.setModificationDetectionActive(false);
      if (Program.getCaveConfigBool(MyCellarSettings.EMPTY_SEARCH, false)) {
        emptySearchCheck.setSelected(true);
      }

      searchByName = new TextFieldPopup(Program.getStorage().getDistinctNames(), 150) {
        @Serial
        private static final long serialVersionUID = 3894902403893114601L;

        @Override
        public void doAfterValidate() {
          new Thread(Search.this).start();
        }
      };

      exportButton.setMnemonic(exportKey);
      selectAllCheck.setHorizontalAlignment(SwingConstants.RIGHT);
      selectAllCheck.setHorizontalTextPosition(SwingConstants.LEFT);

      selectAllCheck.addActionListener(this::selectall_actionPerformed);
      addToWorksheetButton.addActionListener(this::addToWorksheet_actionPerformed);
      emptySearchCheck.addActionListener(this::empty_search_actionPerformed);
      exportButton.addActionListener(this::export_actionPerformed);
      deleteButton.setMnemonic(deleteKey);
      modifyButton.setMnemonic(modificationKey);
      modifyButton.setEnabled(false);
      deleteButton.setEnabled(false);
      exportButton.setEnabled(false);

      deleteButton.addActionListener(this::deleteActionPerformed);

      table = new JTable(searchTableModel);
      table.setAutoCreateRowSorter(true);
      searchButton.addActionListener(this::cherche_actionPerformed);
      searchButton.setMnemonic(searchKey);
      emptyRowsButton.addActionListener(this::emptyRowsActionPerformed);

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
      JScrollPane scrollPane = new JScrollPane(table);
      modifyButton.addActionListener(this::modif_actionPerformed);
      resultInfoLabel.setForeground(Color.red);
      resultInfoLabel.setHorizontalAlignment(SwingConstants.CENTER);
      resultInfoLabel.setFont(FONT_DIALOG_BOLD);

      countLabel.setForeground(Color.red);
      countLabel.setFont(FONT_DIALOG_BOLD);
      objectFoundCountLabels.setHorizontalAlignment(SwingConstants.RIGHT);

      tabbedPane.addChangeListener((e) -> {
        JTabbedPane pane = (JTabbedPane) e.getSource();
        if (pane.getSelectedComponent().equals(panelYear)) {
          panelYear.fillYear();
        }
      });

      setLayout(new MigLayout("", "[grow][]", "[]10px[][grow][]"));

      tabbedPane.add(getLabel(SEARCH_BYNAME), new PanelName());
      tabbedPane.add(getLabel(SEARCH_BYSTORAGE), panelPlace);
      tabbedPane.add(getLabel(SEARCH_BYYEAR), panelYear);
      tabbedPane.add(getLabel(SEARCH_ADVANCEDSEARCH), panelRequest);

      add(tabbedPane, "growx");
      add(new PanelOption(), "wrap");
      add(scrollPane, "grow, wrap, span 2");
      add(addToWorksheetButton, "alignx left, aligny top");
      add(selectAllCheck, "wrap, alignx right, aligny top");
      add(new MyCellarLabel(SEARCH_SELECTROWS, ""), "wrap, span 2, alignx center");
      add(resultInfoLabel, "wrap, span 2, alignx center");
      add(modifyButton, "split, span 2, align center");
      add(deleteButton, "wrap");

      setVisible(true);
      if (searchByName.isVisible()) {
        searchByName.requestFocusInWindow();
      }
    });
  }

  private static void Debug(String sText) {
    Program.Debug("Search: " + sText);
  }

  /**
   * Export results
   *
   * @param e ActionEvent
   */
  private void export_actionPerformed(ActionEvent e) {
    Debug("Exporting...");
    JDialog dialog = new JDialog();
    dialog.add(new Export(searchTableModel.getDatas()));
    dialog.pack();
    dialog.setTitle(getLabel(EXPORT_EXPORTFORMAT));
    dialog.setLocationRelativeTo(MainFrame.getInstance());
    dialog.setModal(true);
    dialog.setVisible(true);
    Debug("Export Done");
  }

  private void deleteActionPerformed(ActionEvent event) {
    try {
      Debug("Deleting...");
      final List<IMyCellarObject> listToDelete = searchTableModel.getSelectedObjects();

      if (listToDelete.isEmpty()) {
        // No objet to delete / Select...
        Debug("ERROR: No bottle to delete!");
        Erreur.showInformationMessage(ERROR_NOITEMTODELETE, ERROR_PLEASESELECT);
        return;
      }
      String erreur_txt1;
      String erreur_txt2;
      if (listToDelete.size() == 1) {
        erreur_txt1 = getError(ERROR_1ITEMSELECTED);
        erreur_txt2 = getError(ERROR_CONFIRM1DELETE);
      } else {
        erreur_txt1 = getError(ERROR_NITEMSSELECTED, listToDelete.size());
        erreur_txt2 = getError(ERROR_CONFIRMNDELETE);
      }
      String message = String.format("%s %s", erreur_txt1, erreur_txt2);
      if (JOptionPane.YES_OPTION == Erreur.showAskConfirmationMessage(message)) {
        SwingUtilities.invokeLater(() -> {
          for (IMyCellarObject myCellarObject : listToDelete) {
            searchTableModel.removeObject(myCellarObject);
            Program.getStorage().addHistory(HistoryState.DEL, myCellarObject);
            try {
              final AbstractPlace abstractPlace = myCellarObject.getAbstractPlace();
              abstractPlace.removeObject(myCellarObject);
            } catch (MyCellarException myCellarException) {
              Program.showException(myCellarException);
            }
            Program.setToTrash(myCellarObject);
            ProgramPanels.removeObjectTab(myCellarObject);
          }

          ProgramPanels.updateCellOrganizerPanel(false);

          if (listToDelete.size() == 1) {
            resultInfoLabel.setText(getLabel(SEARCH_1ITEMDELETED));
          } else {
            resultInfoLabel.setText(getLabel(SEARCH_NITEMDELETED, listToDelete.size()));
          }
        });
      }
      Debug("Deleting Done");
    } catch (HeadlessException e) {
      Debug("ERROR: Why this error? " + e.getMessage());
      Program.showException(e);
    } catch (RuntimeException exc) {
      Program.showException(exc);
    }
  }

  private void cherche_actionPerformed(ActionEvent e) {
    Debug("Cherche_actionPerforming...");
    searchByName.removeMenu();
    updateLabelObjectNumber(false);
    resultInfoLabel.setText(getLabel(SEARCH_INPROGRESS));
    new Thread(this).start();
  }

  private void emptyRowsActionPerformed(ActionEvent e) {
    Debug("emptyRowsActionPerformed...");
    SwingUtilities.invokeLater(this::emptyRows);
  }

  private void emptyRows() {
    Debug("emptyRows...");
    modifyButton.setEnabled(false);
    deleteButton.setEnabled(false);
    exportButton.setEnabled(false);
    selectAllCheck.setSelected(false);
    addToWorksheetButton.setEnabled(false);
    resultInfoLabel.setText("");
    searchTableModel.removeAll();
    updateLabelObjectNumber(false);
    Debug("emptyRows Done");
  }

  private void searchByText() {
    Debug("Searching by text with pattern");
    String search = searchByName.getText();
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
      protected List<IMyCellarObject> doInBackground() {
        final Pattern p = Pattern.compile(regexToSearch, Pattern.CASE_INSENSITIVE);
        alreadyFoundItems = false;
        List<IMyCellarObject> list = new LinkedList<>();
        for (IMyCellarObject myCellarObject : Program.getStorage().getAllList()) {
          Matcher m = p.matcher(myCellarObject.getNom());
          if (m.matches()) {
            if (searchTableModel.doesNotContain(myCellarObject)) {
              list.add(myCellarObject);
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
          searchTableModel.addObjects(get());
          Debug(searchTableModel.getRowCount() + " object(s) found");
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
        final List<IMyCellarObject> listToModify = searchTableModel.getSelectedObjects();

        if (listToModify.isEmpty()) {
          Erreur.showInformationMessage(ERROR_NOITEMTOMODIFY, ERROR_SELECTITEMTOMODIFY);
        } else {
          Debug("Modifying " + listToModify.size() + " object(s)...");
          OpenAddVinAction.open(listToModify);
        }
      } catch (RuntimeException exc) {
        Program.showException(exc);
      }
    });
  }

  /**
   * Do the Search
   */
  @Override
  public void run() {
    try {
      Debug("Running...");
      searchButton.setEnabled(false);
      emptyRowsButton.setEnabled(false);
      exportButton.setEnabled(false);
      selectAllCheck.setSelected(false);
      selectAllCheck.setEnabled(false);
      addToWorksheetButton.setEnabled(false);
      alreadyFoundItems = false;
      if (emptySearchCheck.isSelected()) {
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
    updateLabelObjectNumber(true);
    if (alreadyFoundItems) {
      if (!Program.getCaveConfigBool(MyCellarSettings.DONT_SHOW_INFO, false)) {
        Erreur.showInformationMessageWithKey(getError(ERROR_DONTADDTWICE), MyCellarSettings.DONT_SHOW_INFO);
      }
    }
    alreadyFoundItems = false;
    resultInfoLabel.setText(getLabel(SEARCH_COMPLETED));
    if (searchTableModel.getRowCount() > 0) {
      exportButton.setEnabled(true);
      modifyButton.setEnabled(true);
      deleteButton.setEnabled(true);
    }
    enableDefaultButtons();
  }

  private void enableDefaultButtons() {
    searchButton.setEnabled(true);
    emptyRowsButton.setEnabled(true);
    selectAllCheck.setEnabled(true);
    addToWorksheetButton.setEnabled(true);
  }

  private void searchByRequest() {
    Debug("Search by request");
    StringBuilder sb = new StringBuilder();
    panelRequest.getPredicates().forEach(p -> sb.append(p.toString()));
    Debug(sb.toString());
    new MyCellarObjectSwingWorker() {
      @Override
      protected List<IMyCellarObject> doInBackground() {
        if (Program.isWineType()) {
          CountryVignobleController.rebuild();
        }
        Collection<? extends IMyCellarObject> objects = CollectionFilter.select(Program.getStorage().getAllList(), panelRequest.getPredicates()).getResults();
        List<IMyCellarObject> list = new LinkedList<>();
        if (objects != null) {
          for (IMyCellarObject b : objects) {
            if (searchTableModel.doesNotContain(b)) {
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
          final List<IMyCellarObject> myCellarObjects = get();
          searchTableModel.addObjects(myCellarObjects);
          Debug(searchTableModel.getRowCount() + " object(s) found");
          doAfterSearch();
          Debug("Search by request Done");
        } catch (InterruptedException | ExecutionException e) {
          Debug("ERROR: While searching by request");
          Debug(e.getMessage());
        }
      }
    }.execute();
  }

  private void updateLabelObjectNumber(boolean withCount) {
    if (withCount) {
      countLabel.setText(Integer.toString(searchTableModel.getRowCount()));
    } else {
      countLabel.setText(DASH);
    }
    objectFoundCountLabels.setText(getLabel(SEARCH_BOTTLEFOUND));
  }

  private void searchByPlace() {
    Debug("Searching by place");
    if (!panelPlace.performValidation(false, this)) {
      enableDefaultButtons();
      return;
    }

    final List<IMyCellarObject> myCellarObjectList;
    if (panelPlace.getSelectedAbstractPlace().isSimplePlace()) {
      myCellarObjectList = searchSimplePlace();
    } else {
      myCellarObjectList = searchComplexPlace();
    }

    SwingUtilities.invokeLater(() -> {
      searchTableModel.addObjects(myCellarObjectList);
      Debug(searchTableModel.getRowCount() + " object(s) found");
      doAfterSearch();
    });
  }

  private List<IMyCellarObject> searchComplexPlace() {
    final PlacePosition selectedPlace = panelPlace.getSelectedPlacePosition();
    ComplexPlace complexPlace = (ComplexPlace) selectedPlace.getAbstractPlace();
    List<IMyCellarObject> myCellarObjectList = new LinkedList<>();
    if (!panelPlace.isSeveralLocationChecked()) {
      final IMyCellarObject myCellarObject = complexPlace.getObject(selectedPlace).orElse(null);
      if (myCellarObject == null) {
        searchTableModel.removeAll();
        updateLabelObjectNumber(true);
        resultInfoLabel.setText(getLabel(SEARCH_FAILED));
        Erreur.showSimpleErreur(getError(ERROR_NOITEMFOUND)); //Aucun objet trouve
        modifyButton.setEnabled(false);
        deleteButton.setEnabled(false);
      } else {
        if (searchTableModel.doesNotContain(myCellarObject)) {
          myCellarObjectList.add(myCellarObject);
        } else {
          alreadyFoundItems = true;
        }
      }
    } else {
      // Search all objects
      int placeNumStart = 0;
      int placeNumEnd = complexPlace.getPartCount();
      if (panelPlace.isSeveralLocationStatePartChecked()) {
        placeNumStart = selectedPlace.getPlaceNumIndex();
        placeNumEnd = selectedPlace.getPart();
      }
      int lineStart = 0;
      int lineEnd = 0;
      if (panelPlace.isSeveralLocationStateLineChecked()) {
        placeNumStart = selectedPlace.getPlaceNumIndex();
        placeNumEnd = selectedPlace.getPart();
        lineStart = selectedPlace.getLineIndex();
        lineEnd = selectedPlace.getLine();
      }
      for (int i = placeNumStart; i < placeNumEnd; i++) {
        int nb_lignes = complexPlace.getLineCountAt(i);
        if (!panelPlace.isSeveralLocationStateLineChecked()) {
          lineEnd = nb_lignes;
        }
        for (int j = lineStart; j < lineEnd; j++) {
          int nb_colonnes = complexPlace.getColumnCountAt(i, j);
          for (int k = 0; k < nb_colonnes; k++) {
            IMyCellarObject myCellarObject = complexPlace.getObject(new PlacePosition.PlacePositionBuilderZeroBased(complexPlace)
                .withNumPlace(i)
                .withLine(j)
                .withColumn(k)
                .build()).orElse(null);
            if (myCellarObject != null) {
              if (searchTableModel.doesNotContain(myCellarObject)) {
                myCellarObjectList.add(myCellarObject);
              } else {
                alreadyFoundItems = true;
              }
            }
          }
        }
      }
    }
    return myCellarObjectList;
  }

  private List<IMyCellarObject> searchSimplePlace() {
    final PlacePosition selectedPlace = panelPlace.getSelectedPlacePosition();
    SimplePlace simplePlace = (SimplePlace) selectedPlace.getAbstractPlace();
    int lieu_num = selectedPlace.getPlaceNumIndex();
    int nb_empl_cave = simplePlace.getPartCount();
    int boucle_toutes;
    int start_boucle;
    if (lieu_num == -1) {
      start_boucle = 1;
      boucle_toutes = nb_empl_cave + 1;
    } else {
      start_boucle = lieu_num;
      boucle_toutes = lieu_num + 1;
    }

    List<IMyCellarObject> myCellarObjectList = new LinkedList<>();
    for (int part = start_boucle; part < boucle_toutes; part++) {
      int totalCellUsed = simplePlace.getCountCellUsed(part);
      for (int i = 0; i < totalCellUsed; i++) {
        IMyCellarObject b = simplePlace.getObjectAt(part, i);
        if (b != null) {
          if (searchTableModel.doesNotContain(b)) {
            myCellarObjectList.add(b);
          } else {
            alreadyFoundItems = true;
          }
        } else {
          Debug("No object found in " + simplePlace.getName() + ": part=" + part + " index=" + i);
        }
      }
    }
    return myCellarObjectList;
  }

  private void searchByYear() {
    Debug("Searching by year");
    new MyCellarObjectSwingWorker() {

      @Override
      protected List<IMyCellarObject> doInBackground() {
        String selectedYear = "";
        if (year.getSelectedItem() != null) {
          selectedYear = year.getSelectedItem().toString();
        }
        int annee;
        if (Bouteille.isNonVintageYear(selectedYear)) {
          annee = Bouteille.NON_VINTAGE_INT;
        } else {
          annee = MyCellarUtils.safeParseInt(selectedYear, 0); // It will be 0 for 'Others'
        }

        List<IMyCellarObject> list = new ArrayList<>();
        for (IMyCellarObject b : Program.getStorage().getAllList()) {
          if (annee == b.getAnneeInt()) {
            if (searchTableModel.doesNotContain(b)) {
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
          final List<IMyCellarObject> myCellarObjects = get();
          searchTableModel.addObjects(myCellarObjects);
          Debug(searchTableModel.getRowCount() + " object(s) found");
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
    if ((e.getKeyCode() == searchKey && e.isControlDown()) || e.getKeyCode() == KeyEvent.VK_ENTER) {
      cherche_actionPerformed(null);
    }
    if (e.getKeyCode() == modificationKey && modifyButton.isEnabled() && e.isControlDown()) {
      modif_actionPerformed(null);
    }
    if (e.getKeyCode() == deleteKey && deleteButton.isEnabled() && e.isControlDown()) {
      deleteActionPerformed(null);
    }
    if (e.getKeyCode() == exportKey && exportButton.isEnabled() && e.isControlDown()) {
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
      modifyButton.setEnabled(false);
      deleteButton.setEnabled(false);
      for (int i = 0; i < searchTableModel.getRowCount(); i++) {
        searchTableModel.setValueAt(selectAllCheck.isSelected(), i, SearchTableModel.ETAT);
      }
      if (searchTableModel.getRowCount() > 0) {
        modifyButton.setEnabled(true);
        deleteButton.setEnabled(true);
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
    final List<IMyCellarObject> list = searchTableModel.getSelectedObjects();

    if (list.isEmpty()) {
      Erreur.showInformationMessage(getError(ERROR_NOWINESELECTED));
      return;
    }
    OpenWorkSheetAction.open(list);
    Debug("addToWorksheet_actionPerforming... Done");
  }

  private void empty_search_actionPerformed(ActionEvent e) {
    Program.putCaveConfigBool(MyCellarSettings.EMPTY_SEARCH, emptySearchCheck.isSelected());
  }

  @Override
  public void setUpdateViewType(UpdateViewType updateViewType) {
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

  public void removeObject(IMyCellarObject myCellarObject) {
    new MyCellarObjectSwingWorker() {
      @Override
      protected void done() {
        searchTableModel.removeObject(myCellarObject);
        updateLabelObjectNumber(true);
      }
    }.execute();
  }

  public void updateTable() {
    SwingUtilities.invokeLater(searchTableModel::fireTableDataChanged);
    searchByName.setList(Program.getStorage().getDistinctNames());
  }

  @Override
  public void cut() {
    if (tabbedPane.getSelectedIndex() == 0) {
      String text = searchByName.getSelectedText();
      if (text != null) {
        String fullText = searchByName.getText();
        searchByName.setText(fullText.substring(0, searchByName.getSelectionStart()) + fullText.substring(searchByName.getSelectionEnd()));
        Program.CLIPBOARD.copy(text);
      }
    }
  }

  @Override
  public void copy() {
    if (tabbedPane.getSelectedIndex() == 0) {
      Program.CLIPBOARD.copy(searchByName.getSelectedText());
    }
  }

  @Override
  public void paste() {
    if (tabbedPane.getSelectedIndex() == 0) {
      String fullText = searchByName.getText();
      searchByName.setText(fullText.substring(0, searchByName.getSelectionStart()) + Program.CLIPBOARD.paste() + fullText.substring(searchByName.getSelectionEnd()));
    }
  }

  private final class PanelName extends JPanel {
    @Serial
    private static final long serialVersionUID = -2125241372841734287L;

    private PanelName() {
      searchByName.setEditable(true);
      searchByName.addMouseListener(popupListener);
      searchByName.setFont(FONT_PANEL);
      setLayout(new MigLayout("", "[grow]", "[]"));
      add(new MyCellarLabel(SEARCH_NAME), "wrap");
      add(searchByName, "grow");
    }
  }

  private final class PanelYear extends JPanel {
    @Serial
    private static final long serialVersionUID = 8579611890313378015L;

    private PanelYear() {
      setLayout(new MigLayout());
      MyCellarLabel labelYear = new MyCellarLabel(SEARCH_YEAR);
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
      year.addItem(getLabel(MAIN_NV));
      year.addItem(getLabel(MAIN_OTHER));
    }
  }

  private final class PanelOption extends JPanel {
    @Serial
    private static final long serialVersionUID = 6761656985728428915L;

    private PanelOption() {
      setLayout(new MigLayout("", "", "[][]"));
      add(searchButton, "wrap");
      add(emptyRowsButton);
      add(exportButton, "wrap");
      add(emptySearchCheck, "wrap, span 2");
      add(objectFoundCountLabels, "split");
      add(countLabel, "wrap");
    }
  }

}
