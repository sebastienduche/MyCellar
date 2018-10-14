package mycellar;

import java.io.File;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;
import java.util.List;

/**
 * <p>Titre : Cave à vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2006</p>
 * <p>Société : Seb Informatique</p>
 * @author Sébastien Duché
 * @version 1.1
 * @since 12/10/18
 */

public class MyCellarControl {

  /**
   * ctrl_Name Contrôle le nom saisie pour la création d'un rangement
   *
   * @param _sName String
   * @return boolean
   */
  static boolean ctrl_Name(String _sName) {

    Debug("Controling name...");
    if (_sName == null || _sName.isEmpty()) {
      //Erreur le nom ne doit pas être vide
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
   * controlPath Contrôle le chemin d'un fichier
   *
   * @param file File
   * @return boolean
   */
  static boolean controlPath(File file) {
    return controlPath(file.getAbsolutePath());
  }

  /**
   * controlPath Contrôle le chemin d'un fichier
   *
   * @param path String
   * @return boolean
   */
  static boolean controlPath(String path) {

    Debug("Controling path...");
    if (null == path || path.isEmpty()) {
      //Erreur le nom ne doit pas être vide
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
   * ctrl_existingName Contrôle si le nom renseigné est déjà utilisé
   *
   * @param _sName String
   * @return boolean
   */
  static boolean ctrl_existingName(String _sName) {

    Debug("Controling existing name...");
    if (Program.getCave(_sName.trim()) != null) {
      Debug("ERROR: Name already use!");
      Erreur.showSimpleErreur(Program.getError("Error037"));//Le nom est déjà utilisé
      return false;
    }
    return true;
  }

  /**
   * controlAndUpdateExtension Contrôle si le nom renseigné a la bonne extension et retourne le nom modifié
   *
   * @param name String
   * @param filtre Filtre
   * @return String
   */
  public static String controlAndUpdateExtension(final String name, final Filtre filtre) {
    return controlAndUpdateExtension(name, filtre.toString());

  }

  /**
 * controlAndUpdateExtension Contrôle si le nom renseigné a la bonne extension et retourne le nom modifié
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
   * controlExtension Contrôle si le nom renseigné a la bonne extension
   *
   * @param name String
   * @param extensions List
   * @return String
   */
  public static boolean controlExtension(final String name, final List<String> extensions) {

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
