package mycellar.placesmanagement;

import mycellar.Bouteille;
import mycellar.Program;
import mycellar.core.JModifyComboBox;
import mycellar.core.LabelType;
import mycellar.core.MyCellarButton;
import mycellar.core.MyCellarLabel;
import net.miginfocom.swing.MigLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.EtchedBorder;
import java.awt.event.ItemEvent;
import java.text.MessageFormat;
import java.util.Objects;
import java.util.Optional;

/**
 * <p>Titre : Cave &agrave; vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2021</p>
 * <p>Societe : Seb Informatique</p>
 *
 * @author S&eacute;bastien Duch&eacute;
 * @version 0.2
 * @since 15/03/21
 */
public final class PanelPlace extends JPanel {
  private static final long serialVersionUID = -2601861017578176513L;
  private final MyCellarLabel m_labelPlace = new MyCellarLabel(LabelType.INFO, "208");
  private final MyCellarLabel m_labelNumPlace = new MyCellarLabel(LabelType.INFO, "082");
  private final MyCellarLabel m_labelLine = new MyCellarLabel(LabelType.INFO, "028");
  private final MyCellarLabel m_labelColumn = new MyCellarLabel(LabelType.INFO, "083");
  protected final JModifyComboBox<Rangement> place = new JModifyComboBox<>();
  protected final JModifyComboBox<String> numPlace = new JModifyComboBox<>();
  protected final JModifyComboBox<String> line = new JModifyComboBox<>();
  protected final JModifyComboBox<String> column = new JModifyComboBox<>();
  protected final MyCellarLabel m_labelExist = new MyCellarLabel();
  private final MyCellarLabel m_avant1 = new MyCellarLabel(); // Pour la Modification
  private final MyCellarLabel m_avant2 = new MyCellarLabel(); // Pour la Modification
  private final MyCellarLabel m_avant3 = new MyCellarLabel(); // Pour la Modification
  private final MyCellarLabel m_avant4 = new MyCellarLabel(); // Pour la Modification
  private final MyCellarLabel m_avant5 = new MyCellarLabel(); // Pour la Modification
  protected MyCellarButton m_chooseCell;
  protected final MyCellarButton m_preview = new MyCellarButton(LabelType.INFO, "138");
  private boolean listenersEnabled = true;

  public PanelPlace(Rangement rangement) {
    this(rangement, false);
  }

  public PanelPlace(Rangement rangement, boolean newLineForError) {
    setLayout(new MigLayout("","[]30px[]30px[]30px[]30px[grow]30px[]",""));
    setBorder(BorderFactory.createTitledBorder(new EtchedBorder(EtchedBorder.LOWERED), Program.getLabel("Infos217")));
    add(m_labelPlace);
    add(m_labelNumPlace);
    add(m_labelLine);
    add(m_labelColumn, "wrap");
    add(place);
    add(numPlace);
    add(line);
    add(column);
    if (!newLineForError) {
      add(m_labelExist, "hidemode 3");
    } else {
      add(new JLabel());
    }
//    add(m_chooseCell, "alignx right");
    add(m_preview, "alignx right, wrap");
    if (newLineForError) {
      add(m_labelExist, "hidemode 3, span 6, wrap");
    }
    add(m_avant1, "hidemode 3,split 2");
    add(m_avant2, "hidemode 3");
    add(m_avant3, "hidemode 3");
    add(m_avant4, "hidemode 3");
    add(m_avant5, "hidemode 3");
    initPlaceCombo();
    setListeners();
    place.setSelectedItem(rangement);
  }

  public Optional<Place> getSelectedPlace() {
    final Rangement rangement = (Rangement) place.getSelectedItem();
    Objects.requireNonNull(rangement);
    if (Program.EMPTY_PLACE.equals(rangement)) {
      return Optional.empty();
    }

    return new Place.PlaceBuilder(rangement)
        .withNumPlace(numPlace.getSelectedIndex())
        .withLine(line.getSelectedIndex())
        .withColumn(column.getSelectedIndex())
        .build();
  }

  protected void initPlaceCombo() {
    place.addItem(Program.EMPTY_PLACE);
    boolean complex = false;
    for (Rangement rangement : Program.getCave()) {
      place.addItem(rangement);
      if (!rangement.isCaisse()) {
        complex = true;
      }
    }
//    m_chooseCell.setEnabled(complex);
  }

  protected void setListeners() {
    place.addItemListener(this::lieu_itemStateChanged);
    numPlace.addItemListener(this::num_lieu_itemStateChanged);
    line.addItemListener(this::line_itemStateChanged);
    column.addItemListener(this::column_itemStateChanged);
  }

  protected boolean isListenersDisabled() {
    return !listenersEnabled;
  }

  protected void setListenersEnabled(boolean listenersEnabled) {
    this.listenersEnabled = listenersEnabled;
  }

  protected void lieu_itemStateChanged(ItemEvent e) {
    if (isListenersDisabled()) {
      return;
    }
    Debug("Lieu_itemStateChanging...");
    try {
      int lieu_select = place.getSelectedIndex();

      m_labelExist.setText("");

      if (lieu_select == 0) {
        m_preview.setEnabled(false);
        numPlace.setEnabled(false);
        line.setEnabled(false);
        column.setEnabled(false);
      }	else {
        m_preview.setEnabled(true);
        numPlace.setEnabled(true);
      }

      boolean bIsCaisse = false;
      int nb_emplacement = 0;
      int start_caisse = 0;
      if (lieu_select > 0) {
        Rangement cave = place.getItemAt(lieu_select);
        nb_emplacement = cave.getNbEmplacements();
        bIsCaisse = cave.isCaisse();
        start_caisse = cave.getStartCaisse();
      }
      numPlace.removeAllItems();
      numPlace.addItem("");
      if (bIsCaisse) { //Type caisse
        m_preview.setEnabled(false);
        for (int i = 0; i < nb_emplacement; i++) {
          numPlace.addItem(Integer.toString(i + start_caisse));
        }
        numPlace.setVisible(true);
        m_labelNumPlace.setText(Program.getLabel("Infos158")); //"Numero de caisse");
        if (nb_emplacement == 1) {
          numPlace.setSelectedIndex(1);
        }
      }	else {
        line.removeAllItems();
        column.removeAllItems();
        for (int i = 1; i <= nb_emplacement; i++) {
          numPlace.addItem(Integer.toString(i));
        }
        m_labelNumPlace.setText(Program.getLabel("Infos082")); //"Numero du lieu");
      }
      m_labelNumPlace.setVisible(true);
      numPlace.setVisible(true);
      line.setVisible(!bIsCaisse);
      column.setVisible(!bIsCaisse);
      m_labelLine.setVisible(!bIsCaisse);
      m_labelColumn.setVisible(!bIsCaisse);
      Debug("Lieu_itemStateChanging... Done");
    }	catch (RuntimeException a) {
      Program.showException(a);
    }
  }

  private void num_lieu_itemStateChanged(ItemEvent e) {
    if (isListenersDisabled()) {
      return;
    }
    SwingUtilities.invokeLater(() -> {
      Debug("Num_lieu_itemStateChanging...");
      int num_select = numPlace.getSelectedIndex();
      int lieu_select = place.getSelectedIndex();

      m_labelExist.setText("");

      if (num_select == 0) {
        line.setEnabled(false);
        column.setEnabled(false);
      }	else {
        line.setEnabled(true);
        Rangement rangement = place.getItemAt(lieu_select);
        if (!rangement.isCaisse()) {
          int nb_ligne = rangement.getNbLignes(num_select - 1);
          line.removeAllItems();
          column.removeAllItems();
          line.addItem("");
          for (int i = 1; i <= nb_ligne; i++) {
            line.addItem(Integer.toString(i));
          }
        }
      }
      setVisible(true);
      Debug("Num_lieu_itemStateChanging... End");
    });
  }

  protected void line_itemStateChanged(ItemEvent e) {
    int num_select = line.getSelectedIndex();
    int emplacement = numPlace.getSelectedIndex();
    int lieu_select = place.getSelectedIndex();
    column.setEnabled(num_select != 0);
    int nb_col = 0;
    if (num_select > 0) {
      Rangement cave = place.getItemAt(lieu_select);
      nb_col = cave.getNbColonnes(emplacement - 1, num_select - 1);
    }
    column.removeAllItems();
    column.addItem("");
    for (int i = 1; i <= nb_col; i++) {
      column.addItem(Integer.toString(i));
    }
  }

  private void column_itemStateChanged(ItemEvent e) {
    SwingUtilities.invokeLater(() -> {
      Debug("Column_itemStateChanging...");
      int nPlace = place.getSelectedIndex();
      int nNumLieu = numPlace.getSelectedIndex();
      int nLine = line.getSelectedIndex();
      int nColumn = column.getSelectedIndex();

      if (nPlace < 1 || nNumLieu < 1 || nLine < 1 || nColumn < 1) {
        return;
      }

      Rangement cave = place.getItemAt(nPlace);
      Optional<Bouteille> b = cave.getBouteille(nNumLieu - 1, nLine - 1, nColumn - 1);
      if (b.isPresent()) {
        m_labelExist.setText(MessageFormat.format(Program.getLabel("Infos329"), Program.convertStringFromHTMLString(b.get().getNom())));
      } else {
        m_labelExist.setText("");
      }
      Debug("Column_itemStateChanging... End");
    });
  }

  protected static void Debug(String sText) {
    Program.Debug("PanelPlace: " + sText);
  }
}
