package Cave;

import java.util.*;
import javax.swing.table.*;

/**
 * <p>Titre : Cave à vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2004</p>
 * <p>Société : Seb Informatique</p>
 * @author Sébastien Duché
 * @version 0.2
 * @since 06/08/07
 */
class Model extends AbstractTableModel {

  protected Vector<String> data;
  protected Vector<String> column;
  static final long serialVersionUID = 60807;

  /**
   * Model: Constructeur.
   */
  public Model() {
    data = new Vector<String>();
    column = new Vector<String>();

    //Ajout des colonnes
    column.addElement(new String("colonne1"));
    column.addElement(new String("colonne2"));
    column.addElement(new String("colonne3"));

    //Ajout des données
    data.addElement(new String("un"));
    data.addElement(new String("deux"));
    data.addElement(new String("trois"));
    data.addElement(new String("quatre"));
    data.addElement(new String("cinq"));
    data.addElement(new String("six"));

  }

  /**
   * addElements: Ajout d'un élément.
   *
   * @param txt1 String
   * @param txt2 String
   * @param txt3 String
   */
  public void addElements(String txt1, String txt2, String txt3) {

    data.addElement(txt1);
    data.addElement(txt2);
    data.addElement(txt3);
  }

  /**
   * removeElements: Supression d'un élément.
   *
   * @param i int
   */
  public void removeElements(int i) {

    if ( (i + 3) == data.size()) {
      data.removeElementAt(i);
    }
    else {
      data.removeElementAt(i);
      data.removeElementAt(i + 1);
      data.removeElementAt(i + 2);
    }
  }

  /**
   * getValueAt: Retourne l'objet sur une ligne / colonne.
   *
   * @param rowIndex int
   * @param columnIndex int
   * @return Object
   */
  public Object getValueAt(int rowIndex, int columnIndex) {
    return (String) data.elementAt(rowIndex * getColumnCount() + columnIndex);
  }

  /**
   * getColumnCount: Retourne le nombre de colonnes.
   *
   * @return int
   */
  public int getColumnCount() {
    return column.size();
  }

  /**
   * getRowCount: Retourne le nombre de lignes.
   *
   * @return int
   */
  public int getRowCount() {
    return data.size() / getColumnCount();
  }

}
