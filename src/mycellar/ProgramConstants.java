package mycellar;

import java.awt.Font;
import java.time.format.DateTimeFormatter;

/**
 * <p>Titre : Cave &agrave; vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2003</p>
 * <p>Soci&eacute;t&eacute; : Seb Informatique</p>
 *
 * @author S&eacute;bastien Duch&eacute;
 * @version 0.1
 * @since 14/10/21
 */
public final class ProgramConstants {

  public static final String INTERNAL_VERSION = "4.2.7.3";
  public static final int VERSION = 70;
  public static final String MAIN_VERSION = VERSION + " BI";
  public static final String DEFAULT_STORAGE_EN = "Default storage";
  public static final String DEFAULT_STORAGE_FR = "Rangement par défaut";
  public static final Font FONT_PANEL = new Font("Arial", Font.PLAIN, 12);
  public static final Font FONT_DIALOG_SMALL = new Font("Dialog", Font.BOLD, 12);
  public static final Font FONT_LABEL_BOLD = new Font("Arial", Font.BOLD, 12);
  public static final String TEMP_PLACE = "$$$@@@Temp_--$$$$||||";
  public static final String UNTITLED1_SINFO = "Untitled1.sinfo";
  public static final String COUNTRIES_XML = "countries.xml";
  public static final String TEXT = ".txt";
  public static final String FRA = "FRA";
  public static final String ITA = "ITA";
  public static final String FR = "fr";
  public static final DateTimeFormatter DATE_FORMATER_DDMMYYYY = DateTimeFormatter.ofPattern("dd/MM/yyyy");
  public static final DateTimeFormatter DATE_FORMATER_DD_MM_YYYY = DateTimeFormatter.ofPattern("dd-MM-yyyy");
  public static final DateTimeFormatter DATE_FORMATER_DD_MM_YYYY_HH_MM = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
  static final String INFOS_VERSION = " 2021 v";
  static final Font FONT_BOUTTON_SMALL = new Font("Arial", Font.PLAIN, 10);
  static final Font FONT_DIALOG = new Font("Dialog", Font.BOLD, 16);
  static final String EXTENSION = ".sinfo";
  static final String KEY_TYPE = "<KEY>";
  static final String PREVIEW_XML = "preview.xml";
  static final String PREVIEW_HTML = "preview.html";
  static final String MY_CELLAR_XML = "MyCellar.xml";
  static final String TYPES_XML = "Types.xml";
  static final String TYPES_MUSIC_XML = "music_types.xml";
  static final String BOUTEILLES_XML = "Bouteilles.xml";
  static final String CONFIG_INI = "config.ini";

  static final String RESTART_COMMAND = "restart";
  static final String DOWNLOAD_COMMAND = "download";
  static final String OPTIONS_PARAM = "-opts=";
}
