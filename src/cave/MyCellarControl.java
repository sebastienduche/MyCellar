package Cave;

/**
 * <p>Titre : Cave à vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2006</p>
 * <p>Société : Seb Informatique</p>
 * @author Sébastien Duché
 * @since 29/12/12
 * @version 0.5
 */

public class MyCellarControl {

  /**
   * ctrl_Name Contrôle le nom saisie pour la création d'un rangement
   *
   * @param _sName String
   * @return boolean
   */
  public static boolean ctrl_Name(String _sName) {

    Debug("Controling name...");
    String sError1;
    boolean bResul = true;
    if (_sName.length() == 0) {
      //Erreur le nom ne doit pas être vide
      Debug("ERROR: Name cannot be empty!");
      sError1 = Program.getError("Error010"); //"Le nom de l'emplacement ne doit pas être vide");
      bResul = false;
      new Erreur(sError1, "");
    }

    if (bResul) {
      //Erreur utilisation de caractères interdits
      if (_sName.indexOf("\"") != -1 || _sName.indexOf(";") != -1 || _sName.indexOf("<") != -1 || _sName.indexOf(">") != -1 || _sName.indexOf("?") != -1 || _sName.indexOf("\\") != -1 || _sName.indexOf("/") != -1 ||
          _sName.indexOf("|") != -1 || _sName.indexOf("*") != -1) {
        sError1 = Program.getError("Error126");
        bResul = false;
        Debug("ERROR: Forbidden Characters!");
        new Erreur(sError1, "");
      }
    }
    return bResul;
  }

  /**
   * ctrl_existingName Contrôle si le nom renseigné est déjà utilisé
   *
   * @param _oCave LinkedList<Rangement>
   * @param _sName String
   * @return boolean
   */
  public static boolean ctrl_existingName(String _sName) {

    Debug("Controling existing name...");
    boolean bReturn = true;
    int nNum = Rangement.convertNom_Int(_sName.trim());
    if (nNum != -1) {
      String sErreur = Program.getError("Error037"); //Le nom est déjà utilisé
      bReturn = false;
      Debug("ERROR: Name already use!");
      new Erreur(sErreur, "");
    }
    return bReturn;
  }

  /**
   * Debug
   *
   * @param sText String
   */
  public static void Debug(String sText) {
    Program.Debug("Control: " + sText);
  }

}
