package mycellar;

import mycellar.core.LabelProperty;
import mycellar.placesmanagement.Place;
import mycellar.placesmanagement.RangementUtils;

import java.io.File;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;
import java.util.List;

import static mycellar.MyCellarUtils.isNullOrEmpty;

/**
 * <p>Titre : Cave &agrave; vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2006</p>
 * <p>Soci&eacute;t&eacute; : Seb Informatique</p>
 *
 * @author S&eacute;bastien Duch&eacute;
 * @version 2.3
 * @since 29/12/21
 */

public final class MyCellarControl {

  public static boolean hasInvalidBotteName(String name) {
    if (isNullOrEmpty(name)) {
      Debug("ERROR: Wrong Name");
      Erreur.showSimpleErreur(Program.getError("Error054", LabelProperty.OF_THE_SINGLE)); // Enter the name
      return true;
    }
    return false;
  }

  public static boolean hasInvalidYear(String year) {
    if (Bouteille.isInvalidYear(year)) {
      Debug("ERROR: Wrong date");
      Erreur.showSimpleErreur(Program.getError("Error053")); // Enter a valid year
      return true;
    }
    return false;
  }

  public static boolean hasInvalidPlace(Place place) {
    if (Program.EMPTY_PLACE.equals(place.getRangement())) {
      Debug("ERROR: Wrong Place");
      Erreur.showSimpleErreur(Program.getError("Error055")); // Select a place
      return true;
    }
    return false;
  }

  public static boolean hasInvalidNumLieuNumber(int lieu_num, boolean isCaisse) {
    if (isCaisse && lieu_num < 0) {
      Debug("ERROR: Wrong Num Place");
      Erreur.showSimpleErreur(Program.getError("Error174"));
      return true;
    }
    if (!isCaisse && lieu_num <= 0) {
      Debug("ERROR: Wrong Num Place");
      Erreur.showSimpleErreur(Program.getError("Error056"));
      return true;
    }
    return false;
  }

  public static boolean hasInvalidLineNumber(int line) {
    if (line <= 0) {
      Debug("ERROR: Wrong Line");
      Erreur.showSimpleErreur(Program.getError("Error057")); // Enter a line number
      return true;
    }
    return false;
  }

  public static boolean hasInvalidColumnNumber(int column) {
    if (column <= 0) {
      Debug("ERROR: Wrong Column");
      Erreur.showSimpleErreur(Program.getError("Error058")); // Enter a column number
      return true;
    }
    return false;
  }

  /**
   * ctrlName Controle le nom saisi pour la creation d'un rangement
   *
   * @param name String
   * @return boolean
   */
  public static boolean ctrlName(String name) {
    Debug("Controling name...");
    if (isNullOrEmpty(name)) {
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
    if (isNullOrEmpty(path)) {
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
    if (RangementUtils.isExistingPlace(name)) {
      Debug("ERROR: Name already use!");
      Erreur.showSimpleErreur(Program.getError("Error037")); // Name already used
      return false;
    }
    return true;
  }

  /**
   * Check if the filename contains the extension and add it if needed
   *
   * @param name   String
   * @param filtre Filtre
   * @return String
   */
  public static String controlAndUpdateExtension(final String name, final Filtre filtre) {
    return controlAndUpdateExtension(name, filtre.toString());
  }

  /**
   * Check if the filename contains the extension and add it if needed
   *
   * @param name      String
   * @param extension String
   * @return String
   */
  public static String controlAndUpdateExtension(final String name, final String extension) {
    if (hasInvalidExtension(name, List.of(extension))) {
      return name + extension.toLowerCase();
    }
    return name;
  }

  /**
   * Check if the filename contains one of the file extensions
   *
   * @param name       String
   * @param extensions List
   * @return String
   */
  public static boolean hasInvalidExtension(final String name, final List<String> extensions) {
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

  private static void Debug(String sText) {
    Program.Debug("Control: " + sText);
  }
}
