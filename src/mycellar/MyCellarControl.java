package mycellar;

/**
 * <p>Titre : Cave à vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2006</p>
 * <p>Société : Seb Informatique</p>
 * @author Sébastien Duché
 * @version 0.8
 * @since 01/03/18
 */

class MyCellarControl {

  /**
   * ctrl_Name Contrôle le nom saisie pour la création d'un rangement
   *
   * @param _sName String
   * @return boolean
   */
  public static boolean ctrl_Name(String _sName) {

    Debug("Controling name...");
    if (_sName.isEmpty()) {
      //Erreur le nom ne doit pas être vide
      Debug("ERROR: Name cannot be empty!");
      Erreur.showSimpleErreur(Program.getError("Error010"));
      return false;
    }

      //Erreur utilisation de caractères interdits
      if (_sName.contains("\"") || _sName.contains(";") || _sName.contains("<") || _sName.contains(">") || _sName.contains("?") || _sName.contains("\\") || _sName.contains("/") ||
          _sName.contains("|") || _sName.contains("*")) {
        Debug("ERROR: Forbidden Characters!");
        Erreur.showSimpleErreur(Program.getError("Error126"));
        return false;
      }
    return true;
  }

  /**
   * ctrl_existingName Contrôle si le nom renseigné est déjà utilisé
   *
   * @param _sName String
   * @return boolean
   */
  public static boolean ctrl_existingName(String _sName) {

    Debug("Controling existing name...");
    if (Program.getCave(_sName.trim()) != null) {
      Debug("ERROR: Name already use!");
      Erreur.showSimpleErreur(Program.getError("Error037"));//Le nom est déjà utilisé
      return false;
    }
    return true;
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
