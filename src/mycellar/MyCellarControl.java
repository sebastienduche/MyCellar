package mycellar;

import java.io.File;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;
import java.util.List;

/**
 * <p>Titre : Cave &agrave; vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2006</p>
 * <p>Soci&eacute;t&eacute; : Seb Informatique</p>
 * @author S&eacute;bastien Duch&eacute;
 * @version 1.3
 * @since 12/07/19
 */

public class MyCellarControl {


  static boolean checkBottleName(String name) {
    if (name == null || name.isEmpty()) {
      Debug("ERROR: Wrong Name");
      Erreur.showSimpleErreur(Program.getError("Error054")); //"Veuillez saisir le nom du vin!"
      return false;
    }
    return true;
  }

  static boolean checkYear(String year) {
    if (!Bouteille.isValidYear(year)) {
      Debug("ERROR: Wrong date");
      Erreur.showSimpleErreur(Program.getError("Error053")); //"Veuillez saisir une ann&eacute;e valide!"
      return false;
    }
    return true;
  }
  static boolean checkPlaceNumberGreaterThan0(int place) {
    if (place == 0) {
      Debug("ERROR: Wrong Place");
      Erreur.showSimpleErreur(Program.getError("Error055")); //"Veuillez s&eacute;lectionner un emplacement!"
      return false;
    }
    return true;
  }

  static boolean checkNumLieuNumberGreaterThan0(int lieu_num, boolean isCaisse) {
    if(lieu_num == 0) {
      Debug("ERROR: Wrong Num Place");
      if (!isCaisse) {
        Erreur.showSimpleErreur(Program.getError("Error056"));
      }	else {
        Erreur.showSimpleErreur(Program.getError("Error174"));
      }
      return false;
    }
    return true;
  }

  static boolean checkLineNumberGreaterThan0(int line) {
    if (line == 0) {
      Debug("ERROR: Wrong Line");
      Erreur.showSimpleErreur(Program.getError("Error057")); //"Veuillez s&eacute;lectionner un numero de line!"
      return false;
    }
    return true;
  }

  static boolean checkColumnNumberGreaterThan0(int column) {
    if (column == 0) {
      Debug("ERROR: Wrong Column");
      Erreur.showSimpleErreur(Program.getError("Error058")); //"Veuillez s&eacute;lectionner un numero de colonne!"
      return false;
    }
    return true;
  }

  /**
   * ctrl_Name Contr&ocirc;le le nom saisie pour la cr&eacute;ation d'un rangement
   *
   * @param _sName String
   * @return boolean
   */
  static boolean ctrl_Name(String _sName) {

    Debug("Controling name...");
    if (_sName == null || _sName.isEmpty()) {
      //Erreur le nom ne doit pas &ecirc;tre vide
      Debug("ERROR: Name cannot be empty!");
      Erreur.showSimpleErreur(Program.getError("Error010"));
      return false;
    }

    try {
      Paths.get(_sName);
    } catch (InvalidPathException e) {
      Debug("ERROR: Forbidden Characters!");
      Erreur.showSimpleErreur(Program.getError("Error126"));
      return false;
    }
    return true;
  }

  /**
   * controlPath Contr&ocirc;le le chemin d'un fichier
   *
   * @param file File
   * @return boolean
   */
  static boolean controlPath(File file) {
    return controlPath(file.getAbsolutePath());
  }

  /**
   * controlPath Contr&ocirc;le le chemin d'un fichier
   *
   * @param path String
   * @return boolean
   */
  static boolean controlPath(String path) {

    Debug("Controling path...");
    if (null == path || path.isEmpty()) {
      //Erreur le nom ne doit pas &ecirc;tre vide
      Debug("ERROR: Name cannot be empty!");
      Erreur.showSimpleErreur(Program.getError("MyCellarControl.emptyPath"));
      return false;
    }

    try {
      Paths.get(path);
    } catch (InvalidPathException e) {
      Debug("ERROR: Forbidden Characters!");
      Erreur.showSimpleErreur(Program.getError("MyCellarControl.invalidPath"));
      return false;
    }

    return true;
  }

  /**
   * ctrl_existingName Contr&ocirc;le si le nom renseign&eacute; est d&eacute;j&agrave; utilis&eacute;
   *
   * @param _sName String
   * @return boolean
   */
  static boolean ctrl_existingName(String _sName) {

    Debug("Controling existing name...");
    if (Program.getCave(_sName.trim()) != null) {
      Debug("ERROR: Name already use!");
      Erreur.showSimpleErreur(Program.getError("Error037"));//Le nom est d&eacute;j&agrave; utilis&eacute;
      return false;
    }
    return true;
  }

  /**
   * controlAndUpdateExtension Contr&ocirc;le si le nom renseign&eacute; a la bonne extension et retourne le nom modifi&eacute;
   *
   * @param name String
   * @param filtre Filtre
   * @return String
   */
  static String controlAndUpdateExtension(final String name, final Filtre filtre) {
    return controlAndUpdateExtension(name, filtre.toString());

  }

  /**
   * controlAndUpdateExtension Contr&ocirc;le si le nom renseign&eacute; a la bonne extension et retourne le nom modifi&eacute;
   *
   * @param name String
   * @param extension String
   * @return String
   */
  public static String controlAndUpdateExtension(final String name, final String extension) {

    Debug("Controling extension...");
    if (name == null) {
      Debug("ERROR: name is null!");
      return "";
    }

    if (extension == null) {
      Debug("ERROR: extension is null!");
      return name;
    }
    if (!name.toLowerCase().trim().endsWith(extension.toLowerCase().trim())) {
      return name + extension.toLowerCase();
    }
    return name;
  }

  /**
   * controlExtension Contr&ocirc;le si le nom renseign&eacute; a la bonne extension
   *
   * @param name String
   * @param extensions List
   * @return String
   */
  static boolean controlExtension(final String name, final List<String> extensions) {

    Debug("Controling extension...");
    if (name == null) {
      Debug("ERROR: name is null!");
      return false;
    }

    if (extensions == null) {
      Debug("ERROR: extension is null!");
      return false;
    }

    String nameClean = name.toLowerCase().trim();
    return extensions.stream().anyMatch(nameClean::endsWith);
  }

  /**
   * Debug
   *
   * @param sText String
   */
  private static void Debug(String sText) {
    Program.Debug("Control: " + sText);
  }
}
