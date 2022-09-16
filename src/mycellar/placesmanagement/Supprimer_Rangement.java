package mycellar.placesmanagement;

import mycellar.Erreur;
import mycellar.ITabListener;
import mycellar.MyCellarImage;
import mycellar.Program;
import mycellar.Start;
import mycellar.core.IMyCellar;
import mycellar.core.IUpdatable;
import mycellar.core.MyCellarObject;
import mycellar.core.MyCellarSwingWorker;
import mycellar.core.UpdateViewType;
import mycellar.core.datas.history.HistoryState;
import mycellar.core.exceptions.MyCellarException;
import mycellar.core.text.LabelProperty;
import mycellar.core.uicomponents.MyCellarButton;
import mycellar.core.uicomponents.MyCellarComboBox;
import mycellar.core.uicomponents.MyCellarLabel;
import mycellar.core.uicomponents.MyCellarSimpleLabel;
import mycellar.core.uicomponents.TabEvent;
import mycellar.general.ProgramPanels;
import mycellar.general.XmlUtils;
import mycellar.placesmanagement.places.AbstractPlace;
import mycellar.placesmanagement.places.ComplexPlace;
import mycellar.placesmanagement.places.SimplePlace;
import net.miginfocom.swing.MigLayout;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.MessageFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static mycellar.Program.EMPTY_PLACE;
import static mycellar.Program.getAbstractPlaces;
import static mycellar.Program.getAide;
import static mycellar.Program.getPreviewXMLFileName;
import static mycellar.Program.getStorage;
import static mycellar.Program.open;
import static mycellar.Program.removePlace;
import static mycellar.Program.setToTrash;
import static mycellar.ProgramConstants.FONT_DIALOG_BOLD;
import static mycellar.ProgramConstants.SPACE;
import static mycellar.core.text.MyCellarLabelManagement.getError;
import static mycellar.core.text.MyCellarLabelManagement.getLabel;
import static mycellar.general.ProgramPanels.deleteSupprimerRangement;


/**
 * Titre : Cave &agrave; vin
 * Description : Votre description
 * Copyright : Copyright (c) 2005
 * Soci&eacute;t&eacute; : Seb Informatique
 *
 * @author S&eacute;bastien Duch&eacute;
 * @version 10.4
 * @since 08/07/22
 */

public final class Supprimer_Rangement extends JPanel implements ITabListener, IMyCellar, IUpdatable {

  private static final long serialVersionUID = 6959053537854600207L;
  private final MyCellarComboBox<AbstractPlace> choix = new MyCellarComboBox<>();
  private final MyCellarSimpleLabel label_final = new MyCellarSimpleLabel();
  private final MyCellarButton preview = new MyCellarButton("Storage.Preview");
  private final char supprimerChar = getLabel("SUPPR").charAt(0);
  private final char previewChar = getLabel("VISUAL").charAt(0);
  private final JTable table;
  private final LinkedList<SupprimerLine> listSupprimer = new LinkedList<>();
  private final SupprimerModel model;
  private int nb_case_use_total = 0;

  private boolean updateView = false;
  private UpdateViewType updateViewType;

  public Supprimer_Rangement() {
    Debug("Initializing...");
    setLayout(new MigLayout("", "[grow]", "20px[]15px[grow]15px[]"));
    MyCellarButton supprimer = new MyCellarButton("Main.Delete", MyCellarImage.DELETE);
    supprimer.setMnemonic(supprimerChar);
    preview.setMnemonic(previewChar);

    supprimer.addActionListener(this::supprimer_actionPerformed);
    addKeyListener(new KeyAdapter() {
      @Override
      public void keyPressed(KeyEvent e) {
        keylistener_actionPerformed(e);
      }
    });

    model = new SupprimerModel(listSupprimer);
    table = new JTable(model);
    JScrollPane scroll = new JScrollPane(table);

    add(new MyCellarLabel("PlaceManagement.SelectToDelete"), "split 2, gap");
    add(choix, "wrap");
    add(scroll, "grow, wrap");
    add(label_final, "grow, center, wrap");
    add(preview, "split 2, center");
    add(supprimer, "");

    preview.addActionListener(this::preview_actionPerformed);
    choix.addItemListener(this::choix_itemStateChanged);

    choix.addItem(EMPTY_PLACE);
    getAbstractPlaces().forEach(choix::addItem);
    setVisible(true);
  }

  private static void Debug(String sText) {
    Program.Debug("Supprimer_Rangement: " + sText);
  }

  private void choix_itemStateChanged(ItemEvent e) {
    Debug("choix_itemStateChanging...");
    listSupprimer.clear();
    nb_case_use_total = 0;

    int num_select = choix.getSelectedIndex();
    if (num_select != 0) {
      preview.setEnabled(true);
      AbstractPlace abstractPlace = (AbstractPlace) choix.getSelectedItem();
      // Nombre d'emplacements
      if (abstractPlace != null) {
        int num_emplacement = abstractPlace.getPartCount();

        if (!abstractPlace.isSimplePlace()) {
          ComplexPlace complexPlace = (ComplexPlace) abstractPlace;
          model.setCaisse(false);
          Debug("Selecting standard place...");
          // Description du nombre de lignes par partie
          nb_case_use_total = 0;
          for (int i = 0; i < num_emplacement; i++) {
            SupprimerLine line = new SupprimerLine(i + 1, complexPlace.getLineCountAt(i), complexPlace.getTotalCellUsed(i));
            listSupprimer.add(line);
            nb_case_use_total += complexPlace.getTotalCellUsed(i);
          }
        } else { //Pour caisse
          int partNumberIncrement = ((SimplePlace) abstractPlace).getPartNumberIncrement();
          model.setCaisse(true);
          Debug("Selecting simple place...");
          nb_case_use_total = 0;
          for (int i = 0; i < num_emplacement; i++) {
            SupprimerLine line = new SupprimerLine(i + partNumberIncrement, 0, abstractPlace.getCountCellUsed(i));
            listSupprimer.add(line);
            nb_case_use_total += abstractPlace.getCountCellUsed(i);
          }
        }
      }

      label_final.setForeground(Color.red);
      label_final.setFont(FONT_DIALOG_BOLD);
      label_final.setHorizontalAlignment(SwingConstants.CENTER);
      Debug("There is (are) " + nb_case_use_total + " object(s) in this place!");
      if (nb_case_use_total == 0) {
        label_final.setText(getLabel("PlaceManagement.EmptyPlace"));
      } else {
        if (nb_case_use_total == 1) {
          label_final.setText(getLabel("DeletePlace.Still1Item", LabelProperty.SINGLE));
        } else {
          label_final.setText(MessageFormat.format(getLabel("DeletePlace.StillNItems", LabelProperty.PLURAL), nb_case_use_total));
        }
      }
      table.updateUI();
    } else {
      label_final.setText("");
      preview.setEnabled(false);
    }
  }

  private void supprimer_actionPerformed(ActionEvent e) {
    Debug("supprimer_actionPerforming...");
    final int num_select = choix.getSelectedIndex();

    // Verifier l'etat du rangement avant de le supprimer et demander confirmation
    if (num_select > 0) {
      if (Program.hasOnlyOnePlace()) {
        Erreur.showSimpleErreur(getError("SupprimerRangement.ForbiddenToDelete"));
        return;
      }
      final AbstractPlace abstractPlace = (AbstractPlace) choix.getSelectedItem();
      if (abstractPlace == null) {
        return;
      }
      if (nb_case_use_total == 0) {
        String name = abstractPlace.getName();
        Debug("MESSAGE: Delete this place: " + name + "?");
        if (JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(this, MessageFormat.format(getError("Error.questionDeleteStorage"), name), getLabel("Main.AskConfirmation"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE)) {
          removeSelectedPlace(abstractPlace, num_select);
        }
      } else {
        String nom = abstractPlace.getName();
        String error;
        if (nb_case_use_total == 1) {
          error = MessageFormat.format(getLabel("DeletePlace.Still1ItemIn", LabelProperty.SINGLE), nom);
        } else {
          error = MessageFormat.format(getLabel("DeletePlace.StillNItemsIn", LabelProperty.PLURAL), nb_case_use_total, nom);
        }
        // Delete place and objects in the place
        String errorPart2 = getError("Error.questionDeleteAllIncludedObjects", LabelProperty.THE_PLURAL);
        Debug("MESSAGE: Delete this place " + nom + " and all object(s) (" + nb_case_use_total + ")?");
        if (JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(this, error + SPACE + errorPart2, getLabel("Main.AskConfirmation"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE)) {
          new MyCellarSwingWorker() {
            @Override
            protected void done() {
              //Suppression des bouteilles presentes dans le rangement
              List<MyCellarObject> myCellarObjectList = getStorage().getAllList().stream().filter((bottle) -> bottle.getEmplacement().equals(abstractPlace.getName())).collect(Collectors.toList());
              for (MyCellarObject b : myCellarObjectList) {
                getStorage().addHistory(HistoryState.DEL, b);
                try {
                  abstractPlace.removeObject(b);
                } catch (MyCellarException myCellarException) {
                  Program.showException(myCellarException);
                }
                setToTrash(b);
              }
              removeSelectedPlace(abstractPlace, num_select);
            }
          }.execute();

        }
      }
    }
  }

  private void removeSelectedPlace(AbstractPlace abstractPlace, int num_select) {
    removePlace(abstractPlace);
    choix.removeItemAt(num_select);
    choix.setSelectedIndex(0);
    ProgramPanels.updateAllPanelsForUpdatingPlaces();
  }

  private void preview_actionPerformed(ActionEvent e) {
    Debug("preview_actionPerforming...");
    int num_select = choix.getSelectedIndex();
    if (num_select == 0) {
      preview.setEnabled(false);
      return;
    }
    XmlUtils.writePlacesToXML("", List.of((AbstractPlace) Objects.requireNonNull(choix.getSelectedItem())), false);
    open(getPreviewXMLFileName(), false);
  }

  private void keylistener_actionPerformed(KeyEvent e) {
    if (e.getKeyCode() == supprimerChar) {
      supprimer_actionPerformed(null);
    } else if (e.getKeyCode() == previewChar && preview.isEnabled()) {
      preview_actionPerformed(null);
    } else if (e.getKeyCode() == KeyEvent.VK_F1) {
      getAide();
    }
  }

  @Override
  public boolean tabWillClose(TabEvent event) {
    return true;
  }

  @Override
  public void tabClosed() {
    Start.getInstance().updateMainPanel();
    deleteSupprimerRangement();
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
    if (updateViewType == UpdateViewType.PLACE || updateViewType == UpdateViewType.ALL) {
      new MyCellarSwingWorker() {
        @Override
        protected void done() {
          choix.removeAllItems();
          choix.addItem(EMPTY_PLACE);
          getAbstractPlaces().forEach(choix::addItem);
        }
      }.execute();
    }
  }

  static class SupprimerModel extends DefaultTableModel {

    private static final long serialVersionUID = -3295046126691124148L;
    private final List<SupprimerLine> list;
    private final List<Column> columns;
    private final Column colLine = new Column(Column.LINE, getLabel("Storage.NumberLines"));
    private boolean isCaisse = false;

    private SupprimerModel(List<SupprimerLine> list) {
      this.list = list;
      columns = new LinkedList<>();
      columns.add(new Column(Column.PART, getLabel("Main.Storage")));
      columns.add(colLine);
      columns.add(new Column(Column.WINE, getLabel("Main.State")));

    }

    public void setCaisse(boolean caisse) {
      if (isCaisse != caisse) {
        isCaisse = caisse;
        if (isCaisse) {
          columns.remove(colLine);
        } else {
          columns.add(1, colLine);
        }
      }
      fireTableStructureChanged();
    }

    @Override
    public int getColumnCount() {
      return columns.size();
    }

    @Override
    public String getColumnName(int column) {
      return columns.get(column).getLabel();
    }

    @Override
    public int getRowCount() {
      if (list == null) {
        return 0;
      }
      return list.size();
    }

    @Override
    public Object getValueAt(int row, int column) {
      SupprimerLine line = list.get(row);
      Column col = columns.get(column);
      switch (col.getCol()) {
        case 0:
          return line.getNumPartLabel();
        case 1:
          return line.getNbLineLabel();
        case 2:
          return line.getNbWineLabel();
        default:
          return "";
      }
    }

    @Override
    public boolean isCellEditable(int row, int column) {
      return false;
    }

    static class Column {
      private static final int PART = 0;
      private static final int LINE = 1;
      private static final int WINE = 2;

      private final int col;
      private final String label;

      private Column(int col, String label) {
        this.col = col;
        this.label = label;
      }

      private int getCol() {
        return col;
      }

      public String getLabel() {
        return label;
      }
    }
  }

  static class SupprimerLine {
    private final int numPart;
    private final int nbLine;
    private final int nbWine;

    private SupprimerLine(int numPart, int nbLine, int nbWine) {
      this.numPart = numPart;
      this.nbLine = nbLine;
      this.nbWine = nbWine;
    }

    String getNumPartLabel() {
      return getLabel("Storage.Shelve") + SPACE + numPart;
    }

    String getNbLineLabel() {
      if (nbLine <= 1) {
        return MessageFormat.format(getLabel("Storage.NbLine"), nbLine);
      }
      return MessageFormat.format(getLabel("Storage.NbLines"), nbLine);
    }

    String getNbWineLabel() {
      return MessageFormat.format(getLabel("Main.SeveralItems", new LabelProperty(nbWine > 1)), nbWine);
    }
  }

}
