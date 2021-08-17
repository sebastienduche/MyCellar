package test;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>Titre : XYGridLayout</p>
 * <p>Description : Gestionnaire de placement</p>
 * <p>Copyright : Copyright (c) 2005</p>
 * <p>Soci�t� : Seb Informatique</p>
 *
 * @author S�bastien Duch�
 * @version 0.9
 * @since 20/12/05
 * <p>
 * Derni�res corrections et am�liorations:
 * v 0.9
 * Ajout de l'option de positionnement automatique au centre des JLabel qui peuvent occuper une ligne enti�re et
 * qui ont les propri�t�s HorizontalAlignment == CENTER et redimensionnable. Cette option est active par d�faut
 * et peux �tre modifi� avec enableAutoResizeCenteredColumn() et disableAutoResizeCenteredColumn(). Cette option
 * doit �tre positionn�e avant l'ajout des composants.
 * v 0.8
 * Correction sur le dimensionnement de la fen�tre lors de l'appel de setMinimumWidthForJLabelColumn(int).
 * Si le JLabel poss�de un alignement CENTER, on n'adapte pas sa largeur afin qu'il reste tel quel.
 * ajout de la variable space_bottom pour param�trer l'espace entre la derni�re ligne et la fin du composant.
 * v 0.7
 * D�veloppement des fonctions setMinimumWidthForJLabelColumn(int) et setMinimumWidthForAllJLabelColumn()
 * permetant le redimensionnement automatique des JLabel � leur taille minimale pour l'affichage de leur texte.
 * v 0.6
 * Correction d'une erreur sur le calcul de taille de la fen�tre principal dans certains cas
 * v 0.5
 * Prise en compte des espacements entre colonnes pour le calcul optimal des largeurs de ces derni�res.
 * Remplacement du +25 dans la variable size_width de UpdateLocation par +space_column
 * <p>
 * Description:
 * Cette classe a �t� d�velopp� afin de simplifier le positionnement des composants. J'ai voulu associer
 * la simplicit� du positionnement (x,y) et du dimensionnement des composants (comme utilis� avec le XYLayout
 * de Borland ou le setLayout(null) ) avec la possibilit� d'avoir un redimensionnement horizontal automatique
 * des composants lors de l'agrandissement/r�duction du panneau principal (comme dans le GridBagLayout ou
 * le GridLayout).
 * <p>
 * Dans cette version de la classe, seule le redimensionnement horizontal des composants est disponible.
 * Le positionnement des composants se r�alise comme si l'on mettait les composants dans un tableau (ligne i,
 * colonne j) avec la posibilit� d'avoir des cellules fusionn�s (par exemple un composant sera ajout� en ligne 1,
 * colonne 2 et occupera 1 ligne et 3 colonnes). Pour faire cela, on peut utiliser les 2 fonctions suivantes:
 * <p>
 * void add(JComponent c, int width, int row, int column, boolean isresizable) throws Exception;
 * Cette fonction ajoute un JComponent qui aura une largeur minimale (width) et sera positionn� sur la ligne
 * (row) et la colonne (column). isresizable doit �tre positionn� � true pour la taille du composant varie
 * suivant lorsque la largeur du JPanel principal varie. Le composant n'occupera qu'une seule cellule.
 * <p>
 * void add(JComponent c, int width, int row, int column, int nb_row, int nb_col, boolean isresizable) throws Exception;
 * Cette fonction ajoute un JComponent qui aura une largeur minimale (width) et sera positionn� sur la ligne
 * (row) et la colonne (column). De plus il occupera n lignes (nb_row) et m colonnes (nb_col).
 * isresizable fonctionne comme indiqu� pr�c�dement.
 * <p>
 * Lorsque des composants sont ajout�s, les fonctions updateSizeColumn et updateLocation sont appel�es.
 * updateSizeColumn:
 * Cette fonction calcule la largeur minimale que doit avoir chaque colonne afin que tous les composants soit
 * plac�s au plus pr�s les uns des autres en tenant compte des composants redimensionn�s...
 * <p>
 * updateLocation:
 * Cette fonction positionne chaque composant (coordonn�es x,y) en fonction de la ligne et la colonne o� il se
 * trouve avec un largeur fixe ou d�pendant de la largeur du composant principal. La largeur d'un composant ne sera
 * jamais inf�rieure � sa largeur minimale (width dans les fonctions add).
 * <p>
 * setMinimumWidthForJLabelColumn(int column):
 * Cette fonction permet de redimensionner la colonne (column) � sa taille minimale pour que tous le texte des JLabel
 * soit lisible. Si la colonne sp�cifi�e contient uniquement des JLabel, la colonne prendra pour largeur celle du JLabel
 * avec le texte le plus long. Si la colonne contient aussi d'autres composants plus grand que les JLabel, 2 r�gles sont
 * utilis�es:
 * 1) Si le composant (non JLabel) n'est pas redimensionable, alors la colonne aura la largeur du plus grand composant.
 * 2) Si le composant (non JLabel) est redimensionable, celui-ci s'adaptera � la taille de la colonne m�me si cette taille
 * est inf�rieure � la taille minimale sp�cifi�e lors de l'ajout.
 * <p>
 * setMinimumWidthForAllJLabelColumn:
 * Cette fonction fait appel � la fonction setMinimumWidthForJLabelColumn(int column) pour toute les colonnes de la fen�tre.
 * <p>
 * enableAutoResizeCenteredColumn et disableAutoResizeCenteredColumn:
 * Active et Desactive l'option de positionnement automatique au centre des JLabel occupant toute une ligne et poss�dant un
 * alignement Centr� et l'option redimensionnable � true.
 */

public class XYGridLayout implements ComponentListener {

  private final JPanel container;
  private Container the_window;
  private List<JComponent> list; //Liste des object
  private List<String> list_row; //Contient le num�ro de ligne pour chaque objet
  private List<String> list_column; //Contient le num�ro de colonne pour chaque objet
  private List<String> list_nb_row; //Contient le nombre de ligne pour chaque objet
  private List<String> list_nb_column; //Contient le nombre de colonne pour chaque objet
  private List<Boolean> resizable; //Indique si les objets peuvent �tre redimensionn�s
  private List<String> list_min_size; //Taille minimale n�cessaire pour chaque objet
  private int line_height = 25; //Hauteur de ligne
  private int space_line = 5; //Taille de l'interligne
  private int space_column = 5; //Taille de l'intercolonne
  private int space_top = 5; //Espace libre en haut
  private int space_left = 5; //Espace libre � gauche
  private int space_right = 5; //Espace libre � droite
  private int space_bottom = 15; //Espace libre en bas
  private List<String> sizecolumn; //Liste contenant la taille des colonnes
  private int size_width; //Largeur de la JFrame
  private int size_height; //Hauteur de la JFrame
  private int min_width; //Largeur minimale obligatoire
  private int min_height; //Hauteur minimale obligatoire
  private int nb_line; //indique le nombre de lignes
  private boolean auto_resize; //True via l'appel au redimensionnement automatique des colonnes
  private boolean auto_resize_centered_JLabel; //True si les JLabel peuvent �tre recentr�

  /**
   * XYGridLayout
   *
   * @param c JFrame
   */
  public XYGridLayout(JFrame c) {
    container = (JPanel) c.getContentPane();
    the_window = c;
    init();
  }

  /**
   * XYGridLayout
   *
   * @param c JDialog
   */
  public XYGridLayout(JDialog c) {
    container = (JPanel) c.getContentPane();
    the_window = c;
    init();
  }

  /**
   * XYGridLayout
   *
   * @param c JPanel
   */
  public XYGridLayout(JPanel c) {
    container = c;
    the_window = c;
    init();
  }

  /**
   * XYGridLayout
   *
   * @param c             JFrame
   * @param line_height1  int Hauteur des lignes (defaut 25)
   * @param space_line1   int Espace libre s�parant 2 lignes (defaut 5)
   * @param space_column1 int Espace libre s�parant 2 colonnes (defaut 5);
   * @param space_top1    int Espace libre entre le haut du composant principal et la premi�re ligne(defaut 5)
   * @param space_left1   int Espace libre entre la gauche du composant principal et la premi�re colonne (defaut 5)
   * @param space_right1  int Espace libre entre la droite du composant principal et la derni�re colonne (defaut 5)
   * @param space_bottom1 int Espace libre entre le bas du composant principal et la derni�re ligne (defaut 15)
   */
  public XYGridLayout(JFrame c, int line_height1, int space_line1, int space_column1, int space_top1, int space_left1, int space_right1,
                      int space_bottom1) {

    container = (JPanel) c.getContentPane();
    initSpace(line_height1, space_line1, space_column1, space_top1, space_left1, space_right1, space_bottom1);
    init();
  }

  /**
   * XYGridLayout
   *
   * @param c             JDialog
   * @param line_height1  int Hauteur des lignes (defaut 25)
   * @param space_line1   int Espace libre s�parant 2 lignes (defaut 5)
   * @param space_column1 int Espace libre s�parant 2 colonnes (defaut 5);
   * @param space_top1    int Espace libre entre le haut du composant principal et la premi�re ligne(defaut 5)
   * @param space_left1   int Espace libre entre la gauche du composant principal et la premi�re colonne (defaut 5)
   * @param space_right1  int Espace libre entre la droite du composant principal et la derni�re colonne (defaut 5)
   * @param space_bottom1 int Espace libre entre le bas du composant principal et la derni�re ligne (defaut 15)
   */
  public XYGridLayout(JDialog c, int line_height1, int space_line1, int space_column1, int space_top1, int space_left1, int space_right1,
                      int space_bottom1) {

    container = (JPanel) c.getContentPane();
    initSpace(line_height1, space_line1, space_column1, space_top1, space_left1, space_right1, space_bottom1);
    init();
  }

  /**
   * XYGridLayout
   *
   * @param c             JPanel
   * @param line_height1  int Hauteur des lignes (defaut 25)
   * @param space_line1   int Espace libre s�parant 2 lignes (defaut 5)
   * @param space_column1 int Espace libre s�parant 2 colonnes (defaut 5);
   * @param space_top1    int Espace libre entre le haut du composant principal et la premi�re ligne(defaut 5)
   * @param space_left1   int Espace libre entre la gauche du composant principal et la premi�re colonne (defaut 5)
   * @param space_right1  int Espace libre entre la droite du composant principal et la derni�re colonne (defaut 5)
   * @param space_bottom1 int Espace libre entre le bas du composant principal et la derni�re ligne (defaut 15)
   */
  public XYGridLayout(JPanel c, int line_height1, int space_line1, int space_column1, int space_top1, int space_left1, int space_right1,
                      int space_bottom1) {

    container = c;
    initSpace(line_height1, space_line1, space_column1, space_top1, space_left1, space_right1, space_bottom1);
    init();
  }

  /*
   * init Initialise les listes
   */
  private void init() {
    container.setLayout(null);
    container.addComponentListener(this);
    list = new ArrayList<>();
    list_row = new ArrayList<>();
    list_column = new ArrayList<>();
    list_nb_row = new ArrayList<>();
    list_nb_column = new ArrayList<>();
    sizecolumn = new ArrayList<>();
    resizable = new ArrayList<>();
    list_min_size = new ArrayList<>();
    auto_resize = false;
    auto_resize_centered_JLabel = true;
  }

  /*
   * initSpace Initialise les espacements
   *
   * @param line_height1 int Hauteur des lignes (defaut 25)
   * @param space_line1 int Espace libre s�parant 2 lignes (defaut 5)
   * @param space_column1 int Espace libre s�parant 2 colonnes (defaut 5);
   * @param space_top1 int Espace libre entre le haut du composant principal et la premi�re ligne(defaut 5)
   * @param space_left1 int Espace libre entre la gauche du composant principal et la premi�re colonne (defaut 5)
   * @param space_right1 int Espace libre entre la droite du composant principal et la derni�re colonne (defaut 5)
   * @param space_bottom1 int Espace libre entre le bas du composant principal et la derni�re ligne (defaut 15)
   */
  private void initSpace(int line_height1, int space_line1, int space_column1, int space_top1, int space_left1, int space_right1, int space_bottom1) {
    if (line_height1 <= 0) {
      line_height1 = 25;
    }
    if (space_line1 < 0) {
      space_line1 = 5;
    }
    if (space_column1 < 0) {
      space_column1 = 5;
    }
    if (space_top1 < 0) {
      space_top1 = 5;
    }
    if (space_left1 < 0) {
      space_left1 = 5;
    }
    if (space_right1 < 0) {
      space_right1 = 5;
    }
    if (space_bottom1 < 0) {
      space_bottom1 = 15;
    }
    line_height = line_height1;
    space_line = space_line1;
    space_column = space_column1;
    space_top = space_top1;
    space_left = space_left1;
    space_right = space_right1;
    space_bottom = space_bottom1;
  }

  /*
   * add Ajout de composant sur une ligne et une colonne sans possibilit� de redimensionnement
   *
   * @param c JComponent Composant swing � ajouter
   * @param width int Largeur minimale du composant
   * @param row int Num�ro de la ligne o� ajouter le composant (0...n)
   * @param column int Num�ro de la colonne o� ajouter le composant (0...n)
   */
  public void add(JComponent c, int width, int row, int column) throws Exception {
    add(c, width, row, column, false);
  }

  /*
   * add Ajout de composant sur une ligne et une colonne
   *
   * @param c JComponent Composant swing � ajouter
   * @param width int Largeur minimale du composant
   * @param row int Num�ro de la ligne o� ajouter le composant (0...n)
   * @param column int Num�ro de la colonne o� ajouter le composant (0...n)
   * @param isresizable boolean True si le composant peut �tre redimensionner lorsque le composant principal est redimensionn�
   */
  public void add(JComponent c, int width, int row, int column, boolean isresizable) throws Exception {
    if (row < 0 || column < 0) {
      throw new Exception("row or column cannot be smaller than 0!");
    }
    //On supprime le listener sur les composants afin de pouvoir ajouter et positionner les composants sans que la fonction de redimensionnement soit appel�
    container.removeComponentListener(this);
    //On ajoute des colonnes s'il n'y en a pas assez
    if (sizecolumn.size() < (column + 1)) {
      int size_tmp = sizecolumn.size();
      for (int i = 0; i < (column + 1 - size_tmp); i++) {
        sizecolumn.add("0");
      }
    }
    int size_column = 0;
    try {
      size_column = Integer.parseInt(sizecolumn.get(column));
    } catch (IndexOutOfBoundsException ioobe) {
      sizecolumn.add("0");
    }
    //On redimensionne la colonne si n�cessaire
    if (size_column == 0 || size_column < width) {
      sizecolumn.set(column, Integer.toString(width));
    }
    int size = 0;
    //On r�cup�re l'emplacement exacte pour la colonne
    for (int i = 0; i < column; i++) {
      size += Integer.parseInt(sizecolumn.get(i));
    }
    c.setLocation(space_left + size + (column * space_column), (row * (line_height + space_line)) + space_top);
    list.add(c);
    list_row.add(Integer.toString(row));
    list_column.add(Integer.toString(column));
    list_nb_column.add("1");
    list_nb_row.add("1");
    resizable.add(isresizable);
    list_min_size.add(Integer.toString(width));
    if (row > nb_line) {
      nb_line = row;
    }
    //Size du composant
    c.setMinimumSize(new Dimension(line_height, width));
    c.setSize(width, line_height);
    //Ajout du composant
    container.add(c);
    updateSizeColumn(true);
    //On repositionne le listener pour le redimensionnement
    container.addComponentListener(this);
  }

  /*
   * add Ajout de composant sur une ligne et une colonne
   *
   * @param c JComponent Composant swing � ajouter
   * @param width int Largeur minimale du composant
   * @param row int Num�ro de la ligne o� ajouter le composant (0...n)
   * @param column int Num�ro de la colonne o� ajouter le composant (0...n)
   * @param nb_row int Nombre de lignes utilis�es par le composant (1...n)
   * @param nb_col int Nombre de colonnes utilis�es par le composant (1...n)
   * @param isresizable boolean True si le composant peut �tre redimensionner lorsque le composant principal est redimensionn�
   */
  public void add(JComponent c, int width, int row, int column, int nb_row, int nb_col, boolean isresizable) throws Exception {
    if (row < 0 || column < 0) {
      throw new Exception("row or column cannot be smaller than 0!");
    }
    if (nb_row <= 0) {
      nb_row = 1;
    }
    if (nb_col <= 0) {
      nb_col = 1;
    }
    //On supprime la taille des espacement entre colonne pour calculer correctement les tailles des colonnes
    width -= (nb_col - 1) * space_column;
    //On supprime le listener sur les composants afin de pouvoir ajouter et positionner les composants sans que la fonction de redimensionnement soit appel�
    container.removeComponentListener(this);
    //On ajoute des colonnes s'il n'y en a pas assez
    if (sizecolumn.size() < (column + nb_col + 1)) {
      int size_tmp = sizecolumn.size();
      for (int i = 0; i < (column + nb_col - size_tmp + 1); i++) {
        sizecolumn.add("0");
      }
    }
    int size_column = 0;
    try {
      size_column = Integer.parseInt(sizecolumn.get(column));
    } catch (IndexOutOfBoundsException ioobe) {
      sizecolumn.add("0");
    }
    //On redimensionne la colonne si n�cessaire
    if ((size_column == 0 || size_column < width) && nb_col == 1) {
      sizecolumn.set(column, Integer.toString(width));
    }

    //On redimensionne la derni�re colonne utilisable si n�cessaire
    int size_max = 0;
    int size = 0;
    for (int i = column; i < column + nb_col; i++) {
      size_max += Integer.parseInt(sizecolumn.get(i));
    }
    if (size_max < width) {
      size = Integer.parseInt(sizecolumn.get((column + nb_col - 1)));
      size += (width - size_max);
      sizecolumn.set((column + nb_col - 1), Integer.toString(size));
    }

    size = 0;
    //On r�cup�re l'emplacement exacte pour la colonne
    for (int i = 0; i < column; i++) {
      size += Integer.parseInt(sizecolumn.get(i));
    }

    //On rajoute la taille des espacement entre colonne que l'on avait supprim� auparavant
    width += (nb_col - 1) * space_column;
    c.setLocation(space_left + size + (column * space_column), (row * (line_height + space_line)) + space_top);
    list.add(c);
    list_row.add(Integer.toString(row));
    list_column.add(Integer.toString(column));
    list_nb_column.add(Integer.toString(nb_col));
    list_nb_row.add(Integer.toString(nb_row));
    resizable.add(isresizable);
    list_min_size.add(Integer.toString(width));
    if (row > nb_line) {
      nb_line = row;
    }
    //Size du composant
    c.setMinimumSize(new Dimension(line_height, width));
    c.setSize(width, (line_height * nb_row) + (space_line * (nb_row - 1)));
    //Ajout du composant
    container.add(c);
    updateSizeColumn(true);
    //On remet le listener pour le redimensionnement
    container.addComponentListener(this);
  }

  /**
   * updateSizeColumn Donne la meilleure largeur aux colonnes
   *
   * @param setsize boolean indique si la taille minimale de la fen�tre doit �tre calcul�e
   */
  private void updateSizeColumn(boolean setsize) {
    //On met toute les tailles des colonnes dans un tableau
    int size_col[] = new int[sizecolumn.size()];
    for (int i = 0; i < size_col.length; i++) {
      size_col[i] = Integer.parseInt(sizecolumn.get(i));
    }
    //On r�cup�re les tailles maximales des colonnes demand�s par les composants
    int max_size_col[] = new int[sizecolumn.size()];
    //On traite d'abord les composants tenants sur une colonne
    for (int i = 0; i < list.size(); i++) {
      JComponent comp = list.get(i);
      int width = comp.getSize().width;
      int col = Integer.parseInt(list_column.get(i));
      int nb_col = Integer.parseInt(list_nb_column.get(i));
      if (nb_col == 1) {
        if (max_size_col[col] < width) {
          max_size_col[col] = width;
        }
      }
    }
    //On redimensionne les colonnes
    for (int i = 0; i < max_size_col.length; i++) {
      sizecolumn.set(i, Integer.toString(max_size_col[i]));
    }
    //Maintenant on s'occupe de redimensionner les colonnes si n�cessaire pour les composants tenant sur plusieurs colonnes
    for (int i = 0; i < list.size(); i++) {
      JComponent comp = list.get(i);
      int width = comp.getSize().width;
      int col = Integer.parseInt(list_column.get(i));
      int nb_col = Integer.parseInt(list_nb_column.get(i));
      //On supprime la taille des espacement entre colonne pour calculer correctement les tailles des colonnes
      width -= (nb_col - 1) * space_column;
      if (nb_col > 1) {
        int size_tot = 0;
        for (int j = col; j < col + nb_col; j++) {
          size_tot += max_size_col[j]; //correction v 0.7
        }
        if (size_tot < width) {
          //Am�lioration v 0.7
          Boolean b = resizable.get(i);
          //Si l'on est en train d'ex�cuter setMinimumWidthForJLabelColumn et que le composant peux �tre redimensionn� alors c'est la taille minimale du composant que l'on modifie
          //pour les JLabel size_tot contient d�j� la taille min obligatoire et pour les autres composants on s'autorise � r�duire leur largeur afin de s'adapter � la colonne
          if (auto_resize && b) {
            list_min_size.set(i, Integer.toString(size_tot));
          } else { //Sinon on utilise l'option par d�faut: Redimensionnement de la colonne pour contenir le composant
            max_size_col[col + nb_col - 1] += (width - size_tot);
          }
          //Fin Am�lioration v 0.7
        }
        //Am�lioration v 0.9
        //Si l'option de redimensionnement des JLabel centr� est active (true par d�faut)
        //Si le composant est un JLabel on va regarder si l'a les propri�t�s pour �tre redimensionn�e � la largeur de la fen�tre
        if (auto_resize_centered_JLabel && comp instanceof JLabel) {
          //On r�cup�re le nombre de colonne
          int max_nb_col = sizecolumn.size();
          //On ne compte pas la derni�re si elle est vide
          if (Integer.parseInt(sizecolumn.get(max_nb_col - 1)) == 0) {
            max_nb_col--;
          }
          JLabel aLabel = (JLabel) comp;
          //Si le JLabel d�bute sur la premi�re colonne est se termine sur la derni�re et qu'il peux �tre redimensionn� et qu'il est centr�
          if (col == 0 && nb_col == max_nb_col && resizable.get(i) && aLabel.getHorizontalAlignment() == JLabel.CENTER) {
            size_tot = 0;
            for (int j = 0; j < nb_col; j++) {
              size_tot += max_size_col[j];
            }
            //Alors on affecte au JLabel la taille de la fen�tre comme taille mini pour qu'il reste centr�
            list_min_size.set(i, Integer.toString(size_tot));
          }
        }
        //Fin Am�lioration v 0.9
      }
    }
    //On redimensionne les colonnes
    for (int i = 0; i < max_size_col.length; i++) {
      sizecolumn.set(i, Integer.toString(max_size_col[i]));
    }
    //Mise � jour des l'emplacement des composants
    updateLocation(setsize);
  }

  /**
   * updateLocation: Positionne correctement les composants
   *
   * @param setsize boolean True uniquement lors de l'ajout de composants
   */
  private void updateLocation(boolean setsize) {
    //On met toute les tailles des colonnes dans un tableau
    int size_col[] = new int[sizecolumn.size()];
    for (int i = 0; i < size_col.length; i++) {
      size_col[i] = Integer.parseInt(sizecolumn.get(i));
    }
    int max_size_width = 0;
    //Positionnement des composants
    for (int i = 0; i < list.size(); i++) {
      JComponent comp = list.get(i);
      int y_loc = comp.getLocation().y;
      int col = Integer.parseInt(list_column.get(i));
      int row = Integer.parseInt(list_row.get(i));
      int nb_row = Integer.parseInt(list_nb_row.get(i));
      int width = Integer.parseInt(sizecolumn.get(col));
      int size = 0;
      for (int j = 0; j < col; j++) {
        size += size_col[j];
      }
      //On positionne le composant
      int size_tot = space_left + size + (col * space_column);
      comp.setLocation(size_tot, (row * (line_height + space_line)) + space_top);

      size_width = size_tot + width + space_right + space_column; // correction v 0.5
      size_height = y_loc + ((nb_row + 1) * line_height) + space_bottom; // correction v 0.8
      //On affecte la taille maximale pour la largeur de la fen�tre (correction v 0.6)
      if (size_width > max_size_width) {
        max_size_width = size_width;
      }
    }
    //On ne met � jour le composant principal et la taille minimale uniquement lors de l'ajout de composant
    if (setsize) {
      size_width = max_size_width;
      //On d�finit la taille minimale du composant principal
      // Si la taille de la fen�tre est plus grande que la taille minimale calcul� et qu'on l'on est pas dans setMinimumWidthForJLabelColumn
      if (the_window.getSize().width > size_width && !auto_resize) { // Correction v 0.8
        size_width = the_window.getSize().width;
      }
      if (the_window.getSize().height > size_height) {
        size_height = the_window.getSize().height;
      }
      the_window.setSize(size_width, size_height);
      min_width = size_width;
      min_height = size_height;
    }
  }

  @Override
  public void componentResized(ComponentEvent e) {
    //Lorsque la fen�tre principale est redimensionn�e, on doit redimensionner en largeur les composants qui le peuvent
    Component c = e.getComponent();
    int jframe_width = c.getSize().width;
    int dif_width = jframe_width - size_width;
    //On ne redimensionne que s'il y a reellement eu un redimensionnement du composant principal
    if (dif_width != 0) {
      //Si la fen�tre est plus grande que la taille minimale autoris�e, on peux redimensionner
      if (min_width < jframe_width) {
        //Les colonnes contiennent-elles des composants devant �tre redimensionn�s?
        boolean can_resize[] = new boolean[sizecolumn.size()];
        //On s'occupe d'abord des composants tenant sur une colonne
        for (int i = 0; i < list.size(); i++) {
          Boolean b = resizable.get(i);
          //Si le composant peut �tre redimensionn�
          if (b) {
            //Si le composant tient sur une colonne, on met sa colonne ok pour redimensionner
            if (Integer.parseInt(list_nb_column.get(i)) <= 1) {
              can_resize[Integer.parseInt(list_column.get(i))] = true;
            }
          }
        }
        //On s'occupe des composants tenant sur plusieurs colonnes
        int[][] tab_resize = new int[list.size()][sizecolumn.size()];
        for (int i = 0; i < list.size(); i++) {
          Boolean b = resizable.get(i);
          //Si le composant peut �tre redimensionn�
          if (b) {
            //Si le composant tient sur plusieurs colonnes
            int tmp_nb_col = Integer.parseInt(list_nb_column.get(i));
            if (tmp_nb_col > 1) {
              //On met toutes ses colonnes comme pouvant �tre redimensionn�
              int tmp_num_col = Integer.parseInt(list_column.get(i));
              for (int j = tmp_num_col; j < (tmp_num_col + tmp_nb_col); j++) {
                tab_resize[i][j] = 1;
              }
            }
          }
        }
        //Pour les composants qui chevauchent une colonne � redimensionner, on met ses cases � 0
        for (int y = 0; y < can_resize.length; y++) {
          if (can_resize[y]) {
            for (int i = 0; i < tab_resize.length; i++) {
              if (tab_resize[i][y] == 1) {
                //Ce composant sera redimensionn� donc on peux mettre toutes les cases qu'il occupe � 0
                boolean same_comp = true;
                //On met � 0 les cases qui appartiennent au composant
                //1�re partie cases � partir de la case courante
                for (int z = y; z < tab_resize[i].length; z++) {
                  if (same_comp && tab_resize[i][z] == 1) {
                    tab_resize[i][z] = 0;
                  } else {
                    same_comp = false;
                  }
                }
                same_comp = true;
                //2�me partie cases se trouvant avant la case courante
                for (int z = y; z >= 0; z--) {
                  if (z != y) { //On ne retraite pas la case [i][y]
                    if (same_comp && tab_resize[i][z] == 1) {
                      tab_resize[i][z] = 0;
                    } else {
                      same_comp = false;
                    }
                  }
                }
              }
            }
          }
        }

        //Maintenant il faut affecter "true"  au moins de colonne possible pour que tous les composants � redimensionner le soit
        boolean verifyok;
        //On passe dans la boucle tant que tous les composants � redimensionner le sont pas
        do {
          verifyok = true;
          int tmp_num_col = -1;
          int max = 0;
          //Somme des colonnes
          for (int i = 0; i < tab_resize[0].length; i++) {
            int sum = 0;
            for (int j = 0; j < tab_resize.length; j++) {
              sum += tab_resize[j][i];
            }
            //Si la somme est sup�rieur � 0 ou qu'elle est �gale � celle d'une pr�c�dente colonne
            if (sum > 0 && sum >= max) {
              max = sum;
              tmp_num_col = i;
            }
          }
          if (tmp_num_col != -1) {
            //Il y a encore des colonnes � redimensionner
            verifyok = false;
            can_resize[tmp_num_col] = true;

            //M�me traitement, on enleve les 1 des composants qui croisent la colonne
            for (int i = 0; i < tab_resize.length; i++) {
              if (tab_resize[i][tmp_num_col] == 1) {
                //Ce composant sera redimensionn� donc on peux mettre toutes les cases qu'il occupe � 0
                boolean same_comp = true;
                //On met � 0 les cases qui appartiennent au composant
                //1�re partie cases � partir de la case courante
                for (int z = tmp_num_col; z < tab_resize[i].length; z++) {
                  if (same_comp && tab_resize[i][z] == 1) {
                    tab_resize[i][z] = 0;
                  } else {
                    same_comp = false;
                  }
                }
                same_comp = true;
                //2�me partie cases se trouvant avant la case courante
                for (int z = tmp_num_col; z >= 0; z--) {
                  if (z != tmp_num_col) { //On ne retraite pas la case [i][tmp_num_col]
                    if (same_comp && tab_resize[i][z] == 1) {
                      tab_resize[i][z] = 0;
                    } else {
                      same_comp = false;
                    }
                  }
                }
              }
            }
          }
        }
        while (!verifyok);

        //Calcul du nombre de colonnes pouvant �tre redimensionn�
        int nb_can_resize = 0;
        for (int i = 0; i < can_resize.length; i++) {
          if (can_resize[i]) {
            nb_can_resize++;
          }
        }
        //Taille minimale des colonnes: On g�re d'abord les composants tenant sur 1 colonne
        int min_col_size[] = new int[sizecolumn.size()];
        for (int i = 0; i < list.size(); i++) {
          //Si le composant tient sur une colonne et que sa taille minimale n�cessaire est sup�rieur � la taille min de sa col, on modifie la taille min de la colonne
          if (Integer.parseInt(list_nb_column.get(i)) <= 1 &&
              Integer.parseInt(list_min_size.get(i)) > min_col_size[Integer.parseInt(list_column.get(i))]) {
            min_col_size[Integer.parseInt(list_column.get(i))] = Integer.parseInt(list_min_size.get(i));
          }
        }
        //Taille minimale des colonnes: On g�re les composants tenant sur plusieurs colonnes
        for (int i = 0; i < list.size(); i++) {
          //Si le composant tient sur plus d'une colonne
          int tmp_nb_col = Integer.parseInt(list_nb_column.get(i));
          if (tmp_nb_col > 1) {
            // On calcul la taille minimale offerte par les colonnes qu'il utilise
            int tmp_num_col = Integer.parseInt(list_column.get(i));
            int tmp_size_available = 0;
            for (int j = tmp_num_col; j < (tmp_num_col + tmp_nb_col); j++) {
              tmp_size_available += Integer.parseInt(sizecolumn.get(j));
            }
            //On ajoute l'espace entre chaque colonne
            tmp_size_available += ((tmp_nb_col - 1) * space_column);
            //Si la taille minimale offerte n'est pas suffisante, on agrandit la derni�re colonne
            if (tmp_size_available < Integer.parseInt(list_min_size.get(i))) {
              min_col_size[(tmp_num_col + tmp_nb_col - 1)] += (Integer.parseInt(list_min_size.get(i)) - tmp_size_available);
            }
          }
        }
        //Taille disponible pour chaque colonne � redimensionner
        int space_for_resizing = 0;
        if (nb_can_resize > 0) {
          space_for_resizing = (jframe_width - min_width) / nb_can_resize;
        }
        //S'il y a de l'espace disponible
        if (space_for_resizing > 0) {
          //On redimensionne les composants qui le peuvent
          for (int i = 0; i < list.size(); i++) {
            Boolean b = resizable.get(i);
            //Si le composant peut �tre redimensionner
            if (b) {
              JComponent comp = list.get(i);
              int new_size = Integer.parseInt(list_min_size.get(i)) + space_for_resizing;
              comp.setSize(new_size, comp.getSize().height);
            }
          }
        }
        //Redimensionnement des colonnes
        for (int i = 0; i < can_resize.length; i++) {
          if (can_resize[i]) {
            min_col_size[i] += space_for_resizing;
          }
        }
        //Mise � jour de la liste sizecolumn
        for (int i = 0; i < min_col_size.length; i++) {
          sizecolumn.set(i, Integer.toString(min_col_size[i]));
        }
        //Appel de la mise � jour du positionnement
        updateLocation(false);
      } else {
        //La taille est plus petite que la taille minimale. On met la taille minimale
        for (int i = 0; i < list.size(); i++) {
          JComponent comp = list.get(i);
          comp.setSize(Integer.parseInt(list_min_size.get(i)), comp.getSize().height);
        }
        //Appel de la mise � jour du positionnement
        updateSizeColumn(false);
      }
    }
  }

  @Override
  public void componentHidden(ComponentEvent e) {
  }

  @Override
  public void componentMoved(ComponentEvent e) {
  }

  @Override
  public void componentShown(ComponentEvent e) {
  }

  /*
   * getLineHeight Renvoie la hauteur de chaque ligne
   *
   * @return int
   */
  public int getLineHeight() {
    return line_height;
  }

  /*
   * setLineHeight Sp�cifie la hauteur de chaque ligne
   *
   * @param line_height1 int
   */
  public void setLineHeight(int line_height1) {
    if (line_height1 <= 0) {
      line_height1 = 25;
    }
    line_height = line_height1;
  }

  /*
   * getSpaceLine Renvoie l'espacement entre chaque ligne
   *
   * @return int
   */
  public int getSpaceLine() {
    return space_line;
  }

  /*
   * setSpaceLine Sp�cifie l'espacement entre chaque ligne
   *
   * @param space_line1 int
   */
  public void setSpaceLine(int space_line1) {
    if (space_line1 < 0) {
      space_line1 = 5;
    }
    space_line = space_line1;
  }

  /*
   * getSpaceColumn Renvoie l'espacement entre chaque colonne
   *
   * @return int
   */
  public int getSpaceColumn() {
    return space_column;
  }

  /*
   * setSpaceColumn Sp�cifie l'espacement entre chaque colonne
   *
   * @param space_column1 int
   */
  public void setSpaceColumn(int space_column1) {
    if (space_column1 < 0) {
      space_column1 = 5;
    }
    space_column = space_column1;
  }

  /*
   * getSpaceTop Renvoie la taille de l'espace libre en haut du composant
   *
   * @return int
   */
  public int getSpaceTop() {
    return space_top;
  }

  /*
   * setSpaceTop Sp�cifie la taille de l'espace libre en haut du composant
   *
   * @param space_top1 int
   */
  public void setSpaceTop(int space_top1) {
    if (space_top1 < 0) {
      space_top1 = 5;
    }
    space_top = space_top1;
  }

  /*
   * getSpaceLeft Renvoie la taille de l'espace libre � gauche de la premi�re colonne
   *
   * @return int
   */
  public int getSpaceLeft() {
    return space_left;
  }

  /*
   * setSpaceLeft Sp�cifie la taille de l'espace libre � gauche de la premi�re colonne
   *
   * @param space_left1 int
   */
  public void setSpaceLeft(int space_left1) {
    if (space_left1 < 0) {
      space_left1 = 5;
    }
    space_left = space_left1;
  }

  /*
   * getSpaceRight Renvoie la taille de l'espace libre � droite de la derni�re colonne
   *
   * @return int
   */
  public int getSpaceRight() {
    return space_right;
  }

  /*
   * setSpaceRight Sp�cifie la taille de l'espace libre � droite de la derni�re colonne
   *
   * @param space_right1 int
   */
  public void setSpaceRight(int space_right1) {
    if (space_right1 < 0) {
      space_right1 = 5;
    }
    space_right = space_right1;
  }

  /*
   * getSpaceBottom Renvoie la taille de l'espace libre entre la derni�re ligne et le bas du composant
   *
   * @return int
   */
  public int getSpaceBottom() {
    return space_bottom;
  }

  /*
   * setSpaceBottom Sp�cifie la taille de l'espace libre entre la derni�re ligne et le bas du composant
   *
   * @param space_bottom1 int
   */
  public void setSpaceBottom(int space_bottom1) {
    if (space_bottom1 < 0) {
      space_bottom1 = 15;
    }
    space_bottom = space_bottom1;
  }

  /*
   * getMinimumSize Retourne la taille minimal que doit prendre le composant principal
   *
   * @return Dimension
   */
  public Dimension getMinimumSize() {
    return new Dimension(min_width, min_height);
  }

  /*
   * setMinimumWidthForJLabelColumn Affecte � chaque JLabel d'une colonne la largeur minimale qu'il doit avoir pour afficher son texte
   *
   * @param column int
   */
  public void setMinimumWidthForJLabelColumn(int column) {
    if (column >= 0) {
      auto_resize = true;
      boolean isJLabelmodified = false;
      JLabel aLabel = null;
      int column_size = 0;
      //On parcourt la liste des composants
      for (int i = 0; i < list.size(); i++) {
        JComponent comp = list.get(i);
        //Si le composant est un JLabel et qu'il est situ� dans la colonne sp�cifi�e
        if (comp instanceof JLabel && Integer.parseInt(list_column.get(i)) == column) {
          aLabel = (JLabel) comp;
          //On v�rifie que l'alignement n'est pas CENTER pour eviter que le JLabel ne soit pas centr�
          if (aLabel.getHorizontalAlignment() != JLabel.CENTER) { //correction v 0.8
            //On r�cup�re le FontMetrics en fonction de la Font du JLabel
            FontMetrics metrics = container.getFontMetrics(aLabel.getFont());
            //On r�cup�re la largeur du message du JLabel
            int messageWidth = metrics.stringWidth(aLabel.getText());
            //On met � jour la taille minimale pour la colonne
            if (messageWidth > column_size) {
              column_size = messageWidth;
            }
            //On met � jour la taille du JLabel et la liste contenant les tailles minimales
            comp.setSize(messageWidth, comp.getSize().height);
            list_min_size.set(i, Integer.toString(messageWidth));
            list.set(i, comp);
            isJLabelmodified = true;
          }
        }
      }
      //Si un JLabel a �t� modifi�
      if (isJLabelmodified) {
        int oldSize = Integer.parseInt(sizecolumn.get(column));
        sizecolumn.set(column, Integer.toString(column_size));

        //Mise � jour de la largeur des composants autorisant le redimensionnement automatique
        for (int i = 0; i < list.size(); i++) {
          JComponent comp = list.get(i);
          //Si le composant n'est pas un JLabel et qu'il est situ� dans la colonne sp�cifi�e ou chevauche la colonne sp�cifi�e
          if (!(comp instanceof JLabel) &&
              (Integer.parseInt(list_column.get(i)) == column ||
                  (Integer.parseInt(list_column.get(i)) <= column &&
                      Integer.parseInt(list_nb_column.get(i)) + Integer.parseInt(list_column.get(i)) > column))) {
            //On met � jour la taille du composant
            Boolean b = resizable.get(i);
            //Si le composant peux �tre redimensionn�
            if (b) {
              //On redimensionne le composant avec la diff�rence de taille
              list_min_size.set(i, Integer.toString(comp.getSize().width + (column_size - oldSize)));
            }
            list.set(i, comp);
          }
        }
        //On met a jour la taille de la fen�tre sans faire Appel � componentResized
        container.removeComponentListener(this);
        min_width = the_window.getSize().width + (column_size - oldSize);
        container.setSize(min_width, the_window.getSize().height);
        the_window.setSize(min_width, the_window.getSize().height);
        container.addComponentListener(this);
        //Mise � jour de la taille des colonnes avec autorisation pour le redimensionnement du composant principal
        updateSizeColumn(true);
      }
      auto_resize = false;
    }
  }

  /*
   * setMinimumWidthForAllJLabelColumn Affecte � chaque JLabel de chaque la largeur minimale qu'il doit avoir pour afficher son texte
   *
   * @param column int
   */
  public void setMinimumWidthForAllJLabelColumn() {
    for (int i = 0; i < sizecolumn.size(); i++) {
      setMinimumWidthForJLabelColumn(i);
    }
  }

  /*
   * enableAutoResizeCenteredColumn Active le redimensionnement automatique des JLabel avec les propri�t�s suivantes:
   *  - Le JLabel peux �tre redimensionn� (resizable == true)
   *  - Le JLabel occupe toute une ligne (col == 0 && nb_col == nombre_de_colonne_de_la_fen�tre)
   *  - Le JLabel poss�de un alignement � CENTER
   *
   */
  public void enableAutoResizeCenteredColumn() {
    auto_resize_centered_JLabel = true;
  }

  /*
   * disableAutoResizeCenteredColumn D�sactive le redimensionnement automatique des JLabel a positionner avant l'ajout des composants
   *
   */
  public void disableAutoResizeCenteredColumn() {
    auto_resize_centered_JLabel = false;
  }
}
