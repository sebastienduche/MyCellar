package Cave;

import java.awt.Color;
import java.awt.event.ItemEvent;

import javax.swing.JDialog;

import Cave.core.MyCellarButton;
import Cave.core.MyCellarComboBox;
import Cave.core.MyCellarLabel;
import net.miginfocom.swing.MigLayout;


/**
 * <p>Titre : Cave à Vins</p>
 * <p>Description : </p>
 * <p>Copyright : Copyright (c) 2005</p>
 * <p>Société : SebInformatique</p>
 * @author Sébastien Duché
 * @version 1.3
 * @since 13/11/16
 */
	
public class MoveLine extends JDialog {
	
	  private MyCellarLabel titre = new MyCellarLabel();
	  private MyCellarButton validate = new MyCellarButton();
	  private MyCellarButton cancel = new MyCellarButton();
	  private MyCellarLabel label_place = new MyCellarLabel();
	  private MyCellarLabel label_title = new MyCellarLabel();
	  private MyCellarLabel label_num_place = new MyCellarLabel();
	  private MyCellarLabel label_old_line = new MyCellarLabel();
	  private MyCellarLabel label_new_line = new MyCellarLabel();
	  private MyCellarLabel label_end = new MyCellarLabel();
	  private MyCellarComboBox<String> place_cbx = new MyCellarComboBox<String>();
	  private MyCellarComboBox<String> num_place_cbx = new MyCellarComboBox<String>();
	  private MyCellarComboBox<String> old_line_cbx = new MyCellarComboBox<String>();
	  private MyCellarComboBox<String> new_line_cbx = new MyCellarComboBox<String>();
	  static final long serialVersionUID = 40508;
	  
public MoveLine()
{
	setAlwaysOnTop(true);
	this.setTitle(Program.getLabel("Infos363"));
    setLayout(new MigLayout("","[]","[]20px[]10px[][]10px[][]20px[]10px[]"));
    titre.setForeground(Color.red);
    label_end.setForeground(Color.red);
    titre.setFont(Program.font_dialog);
    titre.setHorizontalAlignment(MyCellarLabel.CENTER);
    label_end.setHorizontalAlignment(MyCellarLabel.CENTER);
    
    titre.setText(Program.getLabel("Infos363"));
    label_title.setText(Program.getLabel("Infos364"));
    label_place.setText(Program.getLabel("Infos081"));
    label_num_place.setText(Program.getLabel("Infos082"));
    label_old_line.setText(Program.getLabel("Infos028"));
    label_new_line.setText(Program.getLabel("Infos362"));
    
    validate.setText(Program.getLabel("Infos315"));
    cancel.setText(Program.getLabel("Infos019"));
      
    place_cbx.addItem("");
    for (int i = 0; i < Program.GetCaveLength(); i++) {
      place_cbx.addItem(Program.getCave(i).getNom());
    }
    num_place_cbx.setEnabled(false);
    old_line_cbx.setEnabled(false);
    new_line_cbx.setEnabled(false);
    
    validate.addActionListener((e) -> {
      int nOldSelected = old_line_cbx.getSelectedIndex();
      int nNewSelected = new_line_cbx.getSelectedIndex();
      if ( nNewSelected == 0 || nOldSelected == nNewSelected )
      {
    	  new Erreur(Program.getError("Error192"), "");
        	  return;
          }
          int nNumLieu = num_place_cbx.getSelectedIndex();
          int nLieuSelected = place_cbx.getSelectedIndex();
          int nBottle = Program.getCave(nLieuSelected - 1).getNbCaseUseLigne(nNumLieu - 1, nNewSelected - 1);
          int nNbBottle = Program.getCave(nLieuSelected - 1).getNbCaseUseLigne(nNumLieu - 1, nOldSelected - 1);
 
          int nOldColumnCount = Program.getCave(nLieuSelected - 1).getNbColonnes(nNumLieu - 1, nOldSelected - 1);
          int nNewColumnCount = Program.getCave(nLieuSelected - 1).getNbColonnes(nNumLieu - 1, nNewSelected - 1);
          if( nOldColumnCount > nNewColumnCount && nNbBottle > nNewColumnCount )
          {
        	  new Erreur(Program.getError("Error194"), "");
    	  return;
      }
      if ( nNbBottle == 0 )
      {
    	  new Erreur(Program.getError("Error195"), "");
    	  return;
      }
      if ( nBottle > 0 )
      {
    	  new Erreur(Program.getError("Error193"), "");
    	  return;  
      }
      else
      {
    	  for( int i=1; i<=Program.getCave(nLieuSelected - 1).getNbColonnes(nNumLieu - 1, nOldSelected - 1); i++)
    	  {
        	  Bouteille bottle = Program.getCave(nLieuSelected - 1).getBouteille(nNumLieu - 1, nOldSelected - 1, i - 1);
        	  if( bottle != null )
        	  {
        		  Program.getStorage().addHistory(History.MODIFY, bottle);
        		  Program.getCave(nLieuSelected - 1).moveLineWine(bottle, nNewSelected);
        	  }
    	  }
    	  label_end.setText(Program.getLabel("Infos366"));
      }
      });
      cancel.addActionListener((e) -> close());
      
      place_cbx.addItemListener((e) -> lieu_itemStateChanged(e));
      num_place_cbx.addItemListener((e) -> num_lieu_itemStateChanged(e));
      old_line_cbx.addItemListener((e) -> old_line_itemStateChanged(e));

      try {
    	add(titre,"align center, span 3, wrap");
    	add(label_title,"span 3, wrap");
    	add(label_place,"");
    	add(label_num_place, "");
    	add(label_old_line,"wrap");
    	add(place_cbx,"");
    	add(num_place_cbx,"");
    	add(old_line_cbx,"wrap");
    	add(label_new_line,"wrap");
    	add(new_line_cbx,"wrap");
    	add(label_end,"span 3,wrap");
    	add(validate,"span 3, split, align center");
    	add(cancel,"");

        this.setSize(320,280);
        this.setResizable(true);
        this.setIconImage(MyCellarImage.ICON.getImage());
        this.setLocationRelativeTo(null);
        this.setVisible(true);
      }
      catch (Exception ex) {
      }

}

/**
 * close
 */
public void close() {
  this.dispose();
}

/**
 * lieu_itemStateChanged: Fonction pour la liste des lieux.
 *
 * @param e ItemEvent
 */
void lieu_itemStateChanged(ItemEvent e) {
  Debug("Lieu_itemStateChanging...");
  label_end.setText("");
  try {
    int nb_emplacement = 0;
    int lieu_select = place_cbx.getSelectedIndex();

    if (lieu_select == 0) {
      num_place_cbx.setEnabled(false);
      old_line_cbx.setEnabled(false);
      new_line_cbx.setEnabled(false);
    }
    else {
      num_place_cbx.setEnabled(true);
    }

    boolean bIsCaisse = false;
    if (lieu_select > 0) {
      nb_emplacement = Program.getCave(lieu_select - 1).getNbEmplacements();
      bIsCaisse = Program.getCave(lieu_select - 1).isCaisse();
    }
    if (bIsCaisse) { //Type caisse
    	num_place_cbx.setEnabled(false);
        old_line_cbx.setEnabled(false);
        new_line_cbx.setEnabled(false);
    }
 
    num_place_cbx.removeAllItems();
    old_line_cbx.removeAllItems();
    new_line_cbx.removeAllItems();
    num_place_cbx.addItem("");
    for (int i = 1; i <= nb_emplacement; i++) {
      num_place_cbx.addItem(Integer.toString(i));
    }
    num_place_cbx.setVisible(true);
    old_line_cbx.setVisible(true);
    new_line_cbx.setVisible(true);
    this.repaint();
    this.setVisible(true);
  }
  catch (Exception a) {
    Program.showException(a);
  }
}

/**
 * old_line_itemStateChanged: Fonction pour la liste des lignes.
 *
 * @param e ItemEvent
 */
void old_line_itemStateChanged(ItemEvent e) {
  Debug("old_line_itemStateChanging...");
  label_end.setText("");
  int num_select = old_line_cbx.getSelectedIndex();
  if (num_select == 0) {
      new_line_cbx.setEnabled(false);
    }
  else
	  new_line_cbx.setEnabled(true);
}

/**
 * num_lieu_itemStateChanged: Fonction pour la liste des numéros de lieu.
 *
 * @param e ItemEvent
 */
void num_lieu_itemStateChanged(ItemEvent e) {
  Debug("Num_lieu_itemStateChanging...");
  try {
	label_end.setText("");
    int nb_ligne = 0;
    int num_select = num_place_cbx.getSelectedIndex();
    int lieu_select = place_cbx.getSelectedIndex();
    boolean isCaisse = false;

    if (num_select == 0) {
      old_line_cbx.setEnabled(false);
      new_line_cbx.setEnabled(false);
    }
    else {
      old_line_cbx.setEnabled(true);
    }
    if (num_select > 0) { //!=0
      isCaisse = Program.getCave(lieu_select - 1).isCaisse();
      if (!isCaisse) {
        if (num_select != 0) {
          nb_ligne = Program.getCave(lieu_select - 1).getNbLignes(num_select - 1);
        }
        old_line_cbx.removeAllItems();
        new_line_cbx.removeAllItems();
        old_line_cbx.addItem("");
        new_line_cbx.addItem("");
        for (int i = 1; i <= nb_ligne; i++) {
          old_line_cbx.addItem(Integer.toString(i));
          new_line_cbx.addItem(Integer.toString(i));
        }
      }
      this.repaint();
      this.setVisible(true);
    }
  }
  catch (Exception a) {
    Program.showException(a);
  }
}

/**
 * Debug
 *
 * @param sText String
 */
public static void Debug(String sText) {
  Program.Debug("MoveLine: " + sText );
}
}