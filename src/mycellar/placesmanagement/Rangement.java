package mycellar.placesmanagement;

import mycellar.Program;
import mycellar.core.MyCellarObject;
import mycellar.core.exceptions.MyCellarException;
import mycellar.placesmanagement.places.IAbstractPlace;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Titre : Cave &agrave; vin
 * Description : Votre description
 * Copyright : Copyright (c) 2003
 * Soci&eacute;t&eacute; : Seb Informatique
 *
 * @author S&eacute;bastien Duch&eacute;
 * @version 29.0
 * @since 27/05/22
 */
public class Rangement implements Comparable<Rangement>, IAbstractPlace {

  private String name;
  private int nbParts; //Nombre d'emplacements
  private int nbColumnsStock; //Nombre max de colonnes pour tous les emplacements
  private int stock_nblign; //Nombre max de lignes pour tous les emplacements
  private boolean simplePlace; //Indique si le rangement est une caisse
  private int startSimplePlace; //Indique l'indice de d&eacute;marrage des caisses
  private MyCellarObject[][][] storage; //Stocke les objets du rangement: stockage[nb_emplacements][stock_nblign][stock_nbcol]
  private boolean simplePlaceLimited; //Indique si une limite de caisse est activ&eacute;e
  private List<Part> partList = null;
  private Map<Integer, ArrayList<MyCellarObject>> storageSimplePlace;
  private boolean defaultPlace = false;

  /**
   * Constructeur de cr&eacute;ation d'un rangement de type Armoire
   *
   * @param name     String: nom du rangement
   * @param listPart LinkedList<Part>: liste des parties
   */
  public Rangement(String name, List<Part> listPart) {
    this.name = name.strip();
    setPlace(listPart);
  }

  /**
   * Constructeur: rangement de type caisse
   *
   * @param name             String: nom du rangement
   * @param nbPart           int: nombre d'emplacement
   * @param startSimplePlace int: Num&eacute;ro de d&eacute;marrage de l'indice des caisses
   * @param isLimit          boolean: Limite de caisse activ&eacute;e?
   * @param limite_caisse    int: Capacit&eacute; pour la limite
   */
  private Rangement(String name, int nbPart, int startSimplePlace, boolean isLimit, int limite_caisse) {
    this.name = name.strip();
    nbParts = nbPart;
    this.startSimplePlace = startSimplePlace;

    simplePlaceLimited = isLimit;
    if (simplePlaceLimited) {
      nbColumnsStock = limite_caisse;
    } else {
      nbColumnsStock = -1;
    }

    stock_nblign = 1;
    simplePlace = true;

    storageSimplePlace = new HashMap<>(nbParts);
    for (int i = startSimplePlace; i < startSimplePlace + nbParts; i++) {
      storageSimplePlace.put(i, new ArrayList<>());
    }
  }

  private static void Debug(String sText) {
    Program.Debug("Rangement: " + sText);
  }

  @Override
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name.strip();
  }

  @Override
  public int getStartSimplePlace() {
    return startSimplePlace;
  }

  public void setStartSimplePlace(int startSimplePlace) {
    this.startSimplePlace = startSimplePlace;
  }

  @Deprecated
  public int getNbParts() {
    return nbParts;
  }

  public void setNbParts(int nbParts) {
    this.nbParts = nbParts;
  }

  /**
   * Retourne le nombre de colonnes autoris&eacute; par le stock
   *
   * @return int
   */
  public int getNbColumnsStock() {
    return nbColumnsStock;
  }

  public boolean isSimplePlaceLimited() {
    return simplePlaceLimited;
  }

  public void setSimplePlaceLimited(boolean simplePlaceLimited) {
    this.simplePlaceLimited = simplePlaceLimited;
  }

  /**
   * Positionne le nombre de bouteilles disponible dans une Caisse si elle est limit&eacute;
   *
   * @return boolean
   */
  public void setNbObjectInSimplePlace(int value) {
    if (simplePlaceLimited && value > 0) {
      nbColumnsStock = value;
    }
  }

  /**
   * Retourne le nombre de lignes d'un emplacement
   *
   * @param emplacement int: num&eacute;ro de l'emplacement (0...n)
   * @return int
   */
  public int getLineCountAt(int emplacement) {
    if (isSimplePlace()) {
      return -1;
    }
    return partList.get(emplacement).getRowSize();
  }

  /**
   * Indique si la cellule demand&eacute;e existe
   *
   * @param emplacement
   * @param ligne
   * @param col
   * @return
   */
  public boolean isExistingCell(int emplacement, int ligne, int col) {
    if (isSimplePlace()) {
      Debug("ERROR: Function isExistingCell can't be called on a simple place!");
      return false;
    }
    if (isInexistingNumPlace(emplacement)) {
      return false;
    }
    if (getLineCountAt(emplacement) <= ligne) {
      return false;
    }
    int nbCol = getColumnCountAt(emplacement, ligne);
    return (col < nbCol);
  }

  /**
   * Retourne le nombre de colonnes sur la ligne d'un emplacement
   *
   * @param emplacement int: num&eacute;ro d'emplacement (0...n)
   * @param ligne       int: num&eacute;ro de ligne (0...n)
   * @return int
   */
  public int getColumnCountAt(int emplacement, int ligne) {
    if (isSimplePlace()) {
      Debug("ERROR: Function getColumnCountAt(int, int) can't be called on a simple place!");
      return -1;
    }
    if (emplacement < 0 || ligne < 0) {
      return -1;
    }
    return partList.get(emplacement).getRow(ligne).getCol();
  }

  /**
   * Retourne le nombre maximal de colonnes d'un emplacement
   *
   * @param emplacement int: num&eacute;ro d'emplacement (0...n)
   * @return int
   */
  public int getMaxColumCountAt(int emplacement) {
    if (isSimplePlace()) {
      Debug("ERROR: Function getMaxColumCountAt can't be called on a simple place!");
      return -1;
    }
    return partList.get(emplacement).getRows().stream().mapToInt(Row::getCol).max().getAsInt();
  }

  /**
   * Retourne le nombre maximal de colonnes du rangement
   *
   * @return int
   */
  public int getMaxColumCount() {
    if (isSimplePlace()) {
      Debug("ERROR: Function getMaxColumCount can't be called on a simple place!");
      return -1;
    }
    int max = 0;
    for (int i = 0; i < getNbParts(); i++) {
      int val = getMaxColumCountAt(i);
      if (val > max) {
        max = val;
      }
    }
    return max;
  }

  /**
   * Retourne le nombre de cases utilis&eacute;es dans une ligne d'un
   * emplacement
   *
   * @param emplacement int: num&eacute;ro de l'emplacement (0...n)
   * @param ligne       int: num&eacute;ro de ligne (0...n)
   * @return int
   */
  public int getNbCaseUseInLine(int emplacement, int ligne) {
    if (isSimplePlace()) {
      Debug("ERROR: Function getNbCaseUseLigne can't be called on a simple place!");
      return -1;
    }
    int resul = 0;
    int nb_colonne = getColumnCountAt(emplacement, ligne);
    for (int i = 0; i < nb_colonne; i++) {
      if (storage[emplacement][ligne][i] != null) {
        resul += 1;
      }
    }
    return resul;
  }

  /**
   * Retourne le nombre de cases libre cote &agrave; cote dans une ligne d'un
   * emplacement &agrave; partir de la colonne indiqu&eacute;e
   *
   * @param emplacement int: num&eacute;ro de l'emplacement (0...n)
   * @param ligne       int: num&eacute;ro de ligne (0...n)
   * @param colonne     int: num&eacute;ro de colonne (0...n)
   * @return int
   */
  public int getCountFreeCellFrom(int emplacement, int ligne, int colonne) {
    if (isSimplePlace()) {
      Debug("ERROR: Function getNbCaseFreeCoteLigne can't be called on a simple place!");
      return -1;
    }
    int resul = 0;
    int nb_colonne = getColumnCountAt(emplacement, ligne);
    for (int i = colonne; i < nb_colonne; i++) {
      if (storage[emplacement][ligne][i] == null) {
        resul++;
      } else {
        return resul;
      }
    }
    return resul;
  }

  /**
   * Retourne le nombre de case utilis&eacute;e dans toutes les lignes
   * d'un emplacement
   *
   * @param emplacement int: num&eacute;ro d'emplacement (0...n)
   * @return int
   */
  public int getTotalCellUsed(int emplacement) {
    if (isSimplePlace()) {
      return getCountCellUsedInSimplePlace(emplacement + startSimplePlace);
    }

    int resul = 0;
    int nb_ligne = getLineCountAt(emplacement);
    for (int j = 0; j < nb_ligne; j++) {
      int nb_colonne = getColumnCountAt(emplacement, j);
      for (int i = 0; i < nb_colonne; i++) {
        if (storage[emplacement][j][i] != null) {
          resul++;
        }
      }
    }
    return resul;
  }

  public int getTotalCellUsed(Place place) {
    return getTotalCellUsed(place.getPlaceNumIndex());
  }

  /**
   * Retourne le nombre de case utilis&eacute;e dans toutes les lignes
   * d'une caisse
   *
   * @param emplacement int: num&eacute;ro d'emplacement (start_caisse...n)
   * @return int
   */
  private int getCountCellUsedInSimplePlace(int emplacement) {

    if (!isSimplePlace()) {
      Debug("ERROR: Function getCountCellUsedInSimplePlace(int) can't be called on a complex place!");
      return -1;
    }
    return storageSimplePlace.get(emplacement).size();
  }

  /**
   * Nombre de case utilis&eacute;e dans toutes les lignes
   *
   * @return int
   */
  public int getTotalCountCellUsed() {
    int resul = 0;
    for (int i = 0; i < nbParts; i++) {
      resul += getTotalCellUsed(i);
    }
    return resul;
  }

  @Override
  public boolean addObject(MyCellarObject myCellarObject) {
    if (myCellarObject.hasNoStatus()) {
      myCellarObject.setCreated();
    }
    if (isSimplePlace()) {
      return addObjectSimplePlace(myCellarObject);
    }
    addObjectComplexPlace(myCellarObject);
    return true;
  }

  @Override
  public void removeObject(MyCellarObject myCellarObject) throws MyCellarException {
    clearStock(myCellarObject);
    Program.getStorage().deleteWine(myCellarObject);
  }

  private boolean addObjectSimplePlace(MyCellarObject myCellarObject) {
    myCellarObject.setLigne(0);
    myCellarObject.setColonne(0);

    Debug("addObjectSimplePlace: " + myCellarObject.getNom() + " " + myCellarObject.getEmplacement() + " " + myCellarObject.getNumLieu());

    int num_empl = myCellarObject.getNumLieu();
    int count = getTotalCellUsed(num_empl - startSimplePlace);
    if (simplePlaceLimited && count == nbColumnsStock) {
      return false;
    }
    updateToStock(myCellarObject);
    Program.getStorage().addWine(myCellarObject);
    return true;
  }

  private void addObjectComplexPlace(MyCellarObject myCellarObject) {
    Debug("addObjectComplexPlace: " + myCellarObject.getNom() + " " + myCellarObject.getEmplacement() + " " + myCellarObject.getNumLieu() + " " + myCellarObject.getLigne() + " " + myCellarObject.getColonne());
    updateToStock(myCellarObject);
    Program.getStorage().addWine(myCellarObject);
  }

  /**
   * Change un objet dans le stock
   *
   * @param myCellarObject MyCellarObject: objet &agrave; changer
   */
  @Override
  public void updateToStock(MyCellarObject myCellarObject) {
    if (isSimplePlace()) {
      storageSimplePlace.get(myCellarObject.getNumLieu()).add(myCellarObject);
      return;
    }
    int line = myCellarObject.getLigne();
    int num_empl = myCellarObject.getNumLieu();
    int column = myCellarObject.getColonne();
    storage[num_empl - 1][line - 1][column - 1] = myCellarObject;
  }

  /**
   * D&eacute;placement d'un objet dans un rangement
   *
   * @param myCellarObject MyCellarObject: Objet &agrave; d&eacute;placer
   * @param newLine        int: nouveau num&eacute;ro de ligne
   */
  public void moveToLine(MyCellarObject myCellarObject, int newLine) throws MyCellarException {
    if (!isExistingCell(myCellarObject.getNumLieu() - 1, newLine - 1, myCellarObject.getColonne() - 1)) {
      throw new MyCellarException("Unable to move this object to a new line: " + myCellarObject);
    }
    clearStock(myCellarObject);
    myCellarObject.setLigne(newLine);
    updateToStock(myCellarObject);
  }

  /**
   * Retourne l'objet se trouvant &agrave; un emplacement pr&eacute;cis dans une armoire
   *
   * @param num_empl int: num&eacute;ro d'emplacement (0...n)
   * @param line     int: num&eacute;ro de ligne (0...n)
   * @param column   int: num&eacute;ro de colonne (0...n)
   * @return MyCellarObject
   */
  @Override
  public Optional<MyCellarObject> getObject(int num_empl, int line, int column) {
    if (isSimplePlace()) {
      Debug("ERROR: Function getObject(int, int, int) can't be called on a simple place!");
      return Optional.empty();
    }
    final MyCellarObject myCellarObject = storage[num_empl][line][column];
    return Optional.ofNullable(myCellarObject);
  }

  @Override
  public Optional<MyCellarObject> getObject(Place place) {
    return getObject(place.getPlaceNumIndex(), place.getLineIndex(), place.getColumnIndex());
  }

  /**
   * Vide la case
   *
   * @param myCellarObject MyCellarObject
   */
  public void clearStock(MyCellarObject myCellarObject) {
    clearStock(myCellarObject, myCellarObject.getPlace());
  }

  public void clearStock(MyCellarObject myCellarObject, Place place) {
    if (isSimplePlace()) {
      storageSimplePlace.get(place.getPlaceNum()).remove(myCellarObject);
    } else {
      clearComplexStock(place);
    }
  }

  public void clearComplexStock(Place place) {
    if (isSimplePlace()) {
      return;
    }
    storage[place.getPlaceNumIndex()][place.getLineIndex()][place.getColumnIndex()] = null;
  }

  /**
   * Retourne l'objet se trouvant &agrave; un emplacement pr&eacute;cis
   *
   * @param num_empl int: num&eacute;ro d'emplacement (0...n)
   * @param index    int: index de l'objet (0...n)
   * @return MyCellarObject
   */
  public MyCellarObject getObjectSimplePlaceAt(int num_empl, int index) {
    return storageSimplePlace.get(num_empl + startSimplePlace).get(index);
  }

  @Override
  public boolean isSimplePlace() {
    return simplePlace;
  }

  public boolean isSameColumnNumber() {
    for (int i = 0; i < nbParts; i++) {
      int nbCol = 0;
      for (int j = 0; j < getLineCountAt(i); j++) {
        if (nbCol == 0) {
          nbCol = getColumnCountAt(i, j);
          continue;
        }
        if (nbCol != getColumnCountAt(i, j)) {
          return false;
        }
      }
    }
    return true;
  }

  /**
   * toXml Converti un rangement en Xml
   *
   * @return String
   */
  @Override
  public String toXml() {
    StringBuilder sText = new StringBuilder();
    if (isSimplePlace()) {
      sText.append("<place name=\"\" IsCaisse=\"true\" NbPlace=\"")
          .append(getNbParts())
          .append("\" NumStart=\"")
          .append(getStartSimplePlace())
          .append("\"");
      if (isSimplePlaceLimited()) {
        sText.append(" NbLimit=\"").append(getNbColumnsStock()).append("\"");
      } else {
        sText.append(" NbLimit=\"0\"");
      }
      if (isDefaultPlace()) {
        sText.append(" default=\"true\">");
      } else {
        sText.append(" default=\"false\">");
      }
    } else {
      sText.append("<place name=\"\" IsCaisse=\"false\" NbPlace=\"")
          .append(getNbParts())
          .append("\">\n");
      for (int i = 0; i < getNbParts(); i++) {
        sText.append("<internal-place NbLine=\"").append(getLineCountAt(i)).append("\">\n");
        for (int j = 0; j < getLineCountAt(i); j++) {
          sText.append("<line NbColumn=\"").append(getColumnCountAt(i, j)).append("\"/>\n");
        }
        sText.append("</internal-place>\n");
      }
    }
    sText.append("<name><![CDATA[").append(getName()).append("]]></name></place>");
    return sText.toString();
  }

  @Override
  public boolean canAddObjectAt(MyCellarObject b) {
    if (isSimplePlace()) {
      return canAddObjectAt(b.getNumLieu() - startSimplePlace, 0, 0);
    }
    return canAddObjectAt(b.getNumLieu() - 1, b.getLigne() - 1, b.getColonne() - 1);
  }

  /**
   * Indique si l'on peut ajouter un objet dans un rangement
   *
   * @param _nEmpl Numero d'emplacement (0, n)
   * @param _nLine Numero de ligne (0, n)
   * @param _nCol  Numero de colonne (0, n)
   * @return
   */
  @Override
  public boolean canAddObjectAt(int _nEmpl, int _nLine, int _nCol) {
    if (_nEmpl < 0 || _nEmpl >= getNbParts()) {
      return false;
    }
    if (isSimplePlace()) {
      return !(isSimplePlaceLimited() && !hasFreeSpaceInSimplePlace(_nEmpl));
    }

    return _nLine >= 0 && _nLine < getLineCountAt(_nEmpl) && !(_nCol < 0 || _nCol >= getColumnCountAt(_nEmpl, _nLine));
  }

  @Override
  public boolean canAddObjectAt(Place place) {
    return canAddObjectAt(place.getPlaceNumIndex(), place.getLineIndex(), place.getColumnIndex());
  }

  /**
   * Indique si le numero du lieu existe
   *
   * @param numPlace Numero d'emplacement (startCaisse, n)
   * @return
   */
  boolean isInexistingNumPlace(int numPlace) {
    return numPlace < startSimplePlace || numPlace >= getNbParts() + startSimplePlace;
  }

  @Deprecated
  private boolean hasFreeSpaceInSimplePlace(int _nEmpl) {
    return isSimplePlace() && (!isSimplePlaceLimited() || getTotalCellUsed(_nEmpl) != getNbColumnsStock());
  }

  /**
   * HasFreeSpaceInCaisse Indique si l'on peut encore ajouter des
   * bouteilles dans une caisse
   */
  public boolean hasFreeSpaceInSimplePlace(Place place) {
    return isSimplePlace() && (!isSimplePlaceLimited() || getCountCellUsedInSimplePlace(place.getPlaceNum()) != getNbColumnsStock());
  }

  /**
   * Retourne le premier emplacement ou se trouve de la place ou -1
   */
  public int getFreeNumPlaceInSimplePlace() {
    if (!isSimplePlace()) {
      return -1;
    }

    for (int i = 0; i < getNbParts(); i++) {
      if (hasFreeSpaceInSimplePlace(i)) {
        return i + startSimplePlace;
      }
    }
    return -1;
  }

  /**
   * Retourne le dernier emplacement utilisable
   */
  public int getLastPartNumber() {
    if (isSimplePlace()) {
      return getStartSimplePlace() + getNbParts();
    }
    return getNbParts();
  }

  /**
   * Retourne le premier emplacement utilisable
   */
  public int getFirstPartNumber() {
    if (isSimplePlace()) {
      return getStartSimplePlace();
    }
    return 1;
  }

  /**
   * R&eacute;initialisation du stockage pour les caisses
   */
  public void updateSimplePlace(int nbEmplacements) {
    if (!isSimplePlace()) {
      return;
    }
    nbParts = nbEmplacements;
    storageSimplePlace = new HashMap<>(nbEmplacements);
    for (int i = startSimplePlace; i < startSimplePlace + nbEmplacements; i++) {
      storageSimplePlace.put(i, new ArrayList<>());
    }
  }

  @Override
  public void resetStockage() {
    resetStock();
  }

  /**
   * R&eacute;initialisation du stockage
   */
  @Deprecated
  public void resetStock() {
    if (isSimplePlace()) {
      updateSimplePlace(nbParts);
    } else {
      storage = new MyCellarObject[nbParts][stock_nblign][nbColumnsStock];
    }
  }

  public LinkedList<Part> getPlace() {
    LinkedList<Part> listPart = new LinkedList<>();
    for (Part p : partList) {
      Part part = new Part(p.getNum());
      listPart.add(part);
      for (int j = 0; j < p.getRowSize(); j++) {
        part.setRows(p.getRowSize());
        part.getRow(j).setCol(p.getRow(j).getCol());
      }
    }
    return listPart;
  }

  /**
   * Initialise les parties
   *
   * @param listPart LinkedList<Part>: liste des parties
   */
  private void setPlace(List<Part> listPart) {
    nbColumnsStock = 0;
    stock_nblign = 0;
    nbParts = listPart.size();
    partList = new LinkedList<>();
    for (int i = 0; i < nbParts; i++) {
      Part part = new Part(listPart.get(i).getNum());
      partList.add(part);
      int rowSize = listPart.get(i).getRowSize();
      part.setRows(rowSize);
      if (rowSize > stock_nblign) {
        stock_nblign = rowSize;
      }
      for (int j = 0; j < rowSize; j++) {
        int colSize = listPart.get(i).getRow(j).getCol();
        part.getRow(j).setCol(colSize);
        if (colSize > nbColumnsStock) {
          nbColumnsStock = colSize;
        }
      }
    }

    startSimplePlace = 0;
    simplePlaceLimited = false;
    simplePlace = false;

    storage = new MyCellarObject[nbParts][stock_nblign][nbColumnsStock];
  }

  public void updatePlace(List<Part> listPart) {
    Debug("Updating the list of places: ");
    listPart.forEach(part -> Debug(part.toString()));
    setPlace(listPart);
    Program.setListCaveModified();
    Program.setModified();
  }

  /**
   * Retourne le nombre de cases utilis&eacute;es par partie
   *
   * @return Map: le numero d'emplacement commence toujours &agrave; 0
   */
  public Map<Integer, Integer> getNumberOfObjectsPerPlace() {
    Map<Integer, Integer> numberOfObjectsPerPlace = new HashMap<>(nbParts);
    if (isSimplePlace()) {
      for (int i = 0; i < nbParts; i++) {
        numberOfObjectsPerPlace.put(i, getCountCellUsedInSimplePlace(i + startSimplePlace));
      }
    } else {
      for (int i = 0; i < nbParts; i++) {
        numberOfObjectsPerPlace.put(i, getTotalCellUsed(i));
      }
    }
    return numberOfObjectsPerPlace;
  }

  @Override
  public String toString() {
    return name;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (!Objects.equals(getClass(), obj.getClass())) {
      return false;
    }
    Rangement other = (Rangement) obj;
    if (name == null) {
      return other.name == null;
    } else return name.equals(other.name);
  }

  @Override
  public int compareTo(Rangement o) {
    return getName().compareTo(o.getName());
  }

  public boolean isSame(Rangement r) {
    if (!getName().equals(r.getName())) {
      return false;
    }
    if (getNbParts() != r.getNbParts()) {
      return false;
    }
    if (isSimplePlace() != r.isSimplePlace()) {
      return false;
    }
    if (!isSimplePlace()) {
      for (int i = 0; i < getNbParts(); i++) {
        int lignes = getLineCountAt(i);
        if (lignes != r.getLineCountAt(i)) {
          return false;
        }
        for (int j = 0; j < lignes; j++) {
          if (getColumnCountAt(i, j) != r.getColumnCountAt(i, j)) {
            return false;
          }
        }
      }
    }
    return true;
  }

  public boolean isDefaultPlace() {
    return defaultPlace;
  }

  public void setDefaultPlace(boolean defaultPlace) {
    this.defaultPlace = defaultPlace;
  }

  public static class RangementBuilder {
    private final String name;
    private final List<Part> partList;
    private int nbParts;

    private boolean sameColumns;
    private int[] columnsByPart;

    private int[] linesByPart;
    private int[][] columnsByLines;

    public RangementBuilder(String name) {
      this.name = name;
      partList = new LinkedList<>();
    }

    public RangementBuilder nbParts(int[] values) {
      nbParts = values.length;
      linesByPart = values;
      return this;
    }

    public RangementBuilder sameColumnsNumber(int[] values) {
      sameColumns = true;
      columnsByPart = values;
      return this;
    }

    public RangementBuilder differentColumnsNumber() {
      sameColumns = false;
      columnsByLines = new int[nbParts][1];
      return this;
    }

    public RangementBuilder columnsNumberForPart(int part, int[] columns) throws Exception {
      if (sameColumns) {
        throw new Exception("This place has the same column number option set!");
      }
      if (part >= nbParts) {
        throw new Exception("Incorrect part number! :" + part);
      }

      if (columns.length < linesByPart[part]) {
        throw new Exception("Incorrect columns length number! :" + part);
      }
      columnsByLines[part] = columns;
      return this;
    }

    public Rangement build() {
      for (int i = 0; i < nbParts; i++) {
        Part part = new Part(i);
        partList.add(part);
        part.setRows(linesByPart[i]);
        if (sameColumns) {
          for (Row row : part.getRows()) {
            row.setCol(columnsByPart[i]);
          }
        } else {
          for (Row row : part.getRows()) {
            row.setCol(columnsByLines[i][row.getNum() - 1]);
          }
        }
      }
      return new Rangement(name, partList);
    }
  }

  public static class SimplePlaceBuilder {
    private final String name;
    private int nbParts;
    private int startSimplePlace;
    private boolean limited;
    private int limit;
    private boolean defaultPlace;

    public SimplePlaceBuilder(String name) {
      this.name = name;
      nbParts = 1;
      startSimplePlace = 0;
      limited = false;
      limit = -1;
      defaultPlace = false;
    }

    public SimplePlaceBuilder nbParts(int value) {
      nbParts = value;
      return this;
    }

    public SimplePlaceBuilder startSimplePlace(int value) {
      startSimplePlace = value;
      return this;
    }

    public SimplePlaceBuilder limited(boolean value) {
      limited = value;
      return this;
    }

    public SimplePlaceBuilder limit(int value) {
      limit = value;
      return this;
    }

    public SimplePlaceBuilder setDefaultPlace(boolean defaultPlace) {
      this.defaultPlace = defaultPlace;
      return this;
    }

    public Rangement build() {
      final Rangement rangement = new Rangement(name, nbParts, startSimplePlace, limited, limit);
      rangement.setDefaultPlace(defaultPlace);
      return rangement;
    }
  }

  @Override
  public void clearStorage(MyCellarObject myCellarObject, Place place) {
    clearStock(myCellarObject, place);

  }

  @Override
  public int getCountCellUsed(int part) {
    return getTotalCellUsed(part);
  }

  @Override
  public void clearStorage(MyCellarObject myCellarObject) {
    clearStock(myCellarObject);
  }

  @Override
  public int getPartCount() {
    return nbParts;
  }
}

