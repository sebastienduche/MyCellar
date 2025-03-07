package mycellar;

import mycellar.core.text.LabelProperty;
import mycellar.frame.MainFrame;
import mycellar.placesmanagement.places.PlacePosition;
import mycellar.placesmanagement.places.PlaceUtils;

import java.awt.Component;
import java.io.File;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;
import java.util.List;

import static mycellar.MyCellarUtils.isNullOrEmpty;
import static mycellar.core.text.MyCellarLabelManagement.getError;
import static mycellar.general.ResourceErrorKey.ERROR_SELECTSTORAGE;

/**
 * Titre : Cave &agrave; vin
 * Description : Votre description
 * Copyright : Copyright (c) 2006
 * Soci&eacute;t&eacute; : Seb Informatique
 *
 * @author S&eacute;bastien Duch&eacute;
 * @version 3.1
 * @since 07/03/25
 */

public final class MyCellarControl {

  public static boolean hasInvalidBotteName(String name) {
    if (isNullOrEmpty(name)) {
      Debug("ERROR: Wrong Name");
      Erreur.showSimpleErreur(getError("Error.enterName", LabelProperty.OF_THE_SINGLE));
      return true;
    }
    return false;
  }

  public static boolean hasInvalidYear(String year) {
    if (Bouteille.isInvalidYear(year)) {
      Debug("ERROR: Wrong date");
      Erreur.showSimpleErreur(getError("Error.enterValidYear"));
      return true;
    }
    return false;
  }

  public static boolean hasInvalidPlace(PlacePosition place) {
    return hasInvalidPlace(place, MainFrame.getInstance());
  }

  public static boolean hasInvalidPlace(PlacePosition place, Component component) {
    if (Program.EMPTY_PLACE.equals(place.getAbstractPlace())) {
      Debug("ERROR: Wrong PlacePosition");
      Erreur.showSimpleErreur(component, getError(ERROR_SELECTSTORAGE));
      return true;
    }
    return false;
  }

  public static boolean hasInvalidNumLieuNumber(int placeNum, boolean simplePlace) {
    return hasInvalidNumLieuNumber(placeNum, simplePlace, null);
  }

  public static boolean hasInvalidNumLieuNumber(int placeNum, boolean simplePlace, Component component) {
    if (simplePlace && placeNum <= -1) {
      Debug("ERROR: Wrong Part");
      Erreur.showSimpleErreur(component, getError("Error.selectStorageNumber"));
      return true;
    }
    if (!simplePlace && placeNum <= 0) {
      Debug("ERROR: Wrong Part");
      Erreur.showSimpleErreur(component, getError("Error.enterShelveNumber"));
      return true;
    }
    return false;
  }

  public static boolean hasInvalidLineNumber(int line) {
    return hasInvalidLineNumber(line, null);
  }

  public static boolean hasInvalidLineNumber(int line, Component component) {
    if (line <= 0) {
      Debug("ERROR: Wrong Line");
      Erreur.showSimpleErreur(component, getError("Error.enterLineNumber"));
      return true;
    }
    return false;
  }

  public static boolean hasInvalidColumnNumber(int column) {
    return hasInvalidColumnNumber(column, null);
  }

  public static boolean hasInvalidColumnNumber(int column, Component component) {
    if (column <= 0) {
      Debug("ERROR: Wrong Column");
      Erreur.showSimpleErreur(component, getError("Error.enterColumnNumber"));
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
      Erreur.showSimpleErreur(getError("Error.requireStorageName"));
      return false;
    }

    try {
      Paths.get(name.strip());
    } catch (InvalidPathException e) {
      Debug("ERROR: Forbidden Characters!");
      Erreur.showSimpleErreur(getError("Error.forbiddenCharacters"));
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
    Debug("Controlling path...");
    if (isNullOrEmpty(path)) {
      Debug("ERROR: Name cannot be empty!");
      Erreur.showSimpleErreur(getError("MyCellarControl.emptyPath"));
      return false;
    }

    try {
      Paths.get(path.strip());
    } catch (InvalidPathException e) {
      Debug("ERROR:Invalid Path!");
      Erreur.showSimpleErreur(getError("MyCellarControl.invalidPath"));
      return false;
    }
    return true;
  }

  /**
   * Check if the name of the storage is already used
   *
   * @param name String
   * @return boolean
   */
  public static boolean ctrl_existingName(String name) {
    Debug("Controlling existing name...");
    if (PlaceUtils.isExistingPlace(name)) {
      Debug("ERROR: Name already use!");
      Erreur.showSimpleErreur(getError("Error.storageNameAlreadyUsed"));
      return false;
    }
    return true;
  }

  /**
   * Check if the filename contains the extension and add it if needed
   *
   * @param name      String
   * @param extension Filtre
   * @return String
   */
  public static String controlAndUpdateExtension(final String name, final Filtre extension) {
    if (hasInvalidExtension(name, List.of(extension))) {
      return name + extension.toString().toLowerCase();
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
  public static boolean hasInvalidExtension(final String name, final List<Filtre> extensions) {
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
    return extensions.stream().noneMatch(filtre -> nameClean.endsWith(filtre.toString()));
  }

  private static void Debug(String sText) {
    Program.Debug("Control: " + sText);
  }
}
