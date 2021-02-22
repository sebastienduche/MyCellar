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
 * @version 1.7
 * @since 22/02/21
 */

public final class MyCellarControl {

  static boolean hasInvalidBotteName(String name) {
    if (name == null || name.strip().isEmpty()) {
      Debug("ERROR: Wrong Name");
      Erreur.showSimpleErreur(Program.getError("Error054")); //"Veuillez saisir le nom du vin!"
      return true;
    }
    return false;
  }

  static boolean hasInvalidYear(String year) {
    if (!Bouteille.isValidYear(year)) {
      Debug("ERROR: Wrong date");
      Erreur.showSimpleErreur(Program.getError("Error053")); //"Veuillez saisir une ann&eacute;e valide!"
      return true;
    }
    return false;
  }
  static boolean hasInvalidPlaceNumber(int place) {
    if (place == 0) {
      Debug("ERROR: Wrong Place");
      Erreur.showSimpleErreur(Program.getError("Error055")); //"Veuillez s&eacute;lectionner un emplacement!"
      return true;
    }
    return false;
  }

  static boolean hasInvalidNumLieuNumber(int lieu_num, boolean isCaisse) {
    if (lieu_num == 0) {
      Debug("ERROR: Wrong Num Place");
      if (!isCaisse) {
        Erreur.showSimpleErreur(Program.getError("Error056"));
      }	else {
        Erreur.showSimpleErreur(Program.getError("Error174"));
      }
      return true;
    }
    return false;
  }

  static boolean hasInvalidLineNumber(int line) {
    if (line == 0) {
      Debug("ERROR: Wrong Line");
      Erreur.showSimpleErreur(Program.getError("Error057")); //"Veuillez s&eacute;lectionner un numero de line!"
      return true;
    }
    return false;
  }

  static boolean hasInvalidColumnNumber(int column) {
    if (column == 0) {
      Debug("ERROR: Wrong Column");
      Erreur.showSimpleErreur(Program.getError("Error058")); //"Veuillez s&eacute;lectionner un numero de colonne!"
      return true;
    }
    return false;
  }

  /**
   * ctrlName Controle le nom saisie pour la creation d'un rangement
   *
   * @param name String
   * @return boolean
   */
  public static boolean ctrlName(String name) {

    Debug("Controling name...");
    if (name == null || name.strip().isEmpty()) {
      //Erreur le nom ne doit pas &ecirc;tre vide
      Debug("ERROR: Name cannot be empty!");
      Erreur.showSimpleErreur(Program.getError("Error010"));
      return false;
    }

    try {
      Paths.get(name.strip());
    } catch (InvalidPathException e) {
      Debug("ERROR: Forbidden Characters!");
      Erreur.showSimpleErreur(Program.getError("Error126"));
      return false;
    }
    return true;
  }

  /**
   * controlPath Controle le chemin d'un fichier
   *
   * @param file File
   * @return boolean
   */
  static boolean controlPath(File file) {
    return controlPath(file.getAbsolutePath());
  }

  /**
   * controlPath Controle le chemin d'un fichier
   *
   * @param path String
   * @return boolean
   */
  static boolean controlPath(String path) {

    Debug("Controling path...");
    if (null == path || path.strip().isEmpty()) {
      //Erreur le nom ne doit pas &ecirc;tre vide
      Debug("ERROR: Name cannot be empty!");
      Erreur.showSimpleErreur(Program.getError("MyCellarControl.emptyPath"));
      return false;
    }

    try {
      Paths.get(path.strip());
    } catch (InvalidPathException e) {
      Debug("ERROR: Forbidden Characters!");
      Erreur.showSimpleErreur(Program.getError("MyCellarControl.invalidPath"));
      return false;
    }

    return true;
  }

  /**
   * ctrl_existingName Controle si le nom renseigne est deja utilise
   *
   * @param name String
   * @return boolean
   */
  public static boolean ctrl_existingName(String name) {

    Debug("Controlling existing name...");
    if (Program.isExistingPlace(name)) {
      Debug("ERROR: Name already use!");
      Erreur.showSimpleErreur(Program.getError("Error037"));//Le nom est d&eacute;j&agrave; utilis&eacute;
      return false;
    }
    return true;
  }

  /**
   * controlAndUpdateExtension Controle si le nom renseigne a la bonne extension et retourne le nom modifie
   *
   * @param name String
   * @param filtre Filtre
   * @return String
   */
  static String controlAndUpdateExtension(final String name, final Filtre filtre) {
    return controlAndUpdateExtension(name, filtre.toString());

  }

  /**
   * controlAndUpdateExtension Controle si le nom renseigne a la bonne extension et retourne le nom modifie
   *
   * @param name String
   * @param extension String
   * @return String
   */
  public static String controlAndUpdateExtension(final String name, final String extension) {

    Debug("Controlling extension...");
    if (name == null) {
      Debug("ERROR: name is null!");
      return "";
    }

    if (extension == null) {
      Debug("ERROR: extension is null!");
      return name;
    }
    if (!name.toLowerCase().strip().endsWith(extension.toLowerCase().strip())) {
      return name + extension.toLowerCase();
    }
    return name;
  }

  /**
   * controlExtension Controle si le nom renseigne a la bonne extension
   *
   * @param name String
   * @param extensions List
   * @return String
   */
  static boolean hasInvalidExtension(final String name, final List<String> extensions) {

    Debug("Controlling extension...");
    if (name == null) {
      Debug("ERROR: name is null!");
      return true;
    }

    if (extensions == null) {
      Debug("ERROR: extension is null!");
      return true;
    }

    String nameClean = name.toLowerCase().strip();
    return extensions.stream().noneMatch(nameClean::endsWith);
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
