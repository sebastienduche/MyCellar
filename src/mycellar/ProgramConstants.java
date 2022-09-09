package mycellar;

import java.awt.Font;
import java.awt.event.KeyEvent;
import java.time.format.DateTimeFormatter;

/**
 * Titre : Cave &agrave; vin
 * Description : Votre description
 * Copyright : Copyright (c) 2003
 * Soci&eacute;t&eacute; : Seb Informatique
 *
 * @author S&eacute;bastien Duch&eacute;
 * @version 1.1
 * @since 08/07/22
 */
public final class ProgramConstants {

  public static final String INTERNAL_VERSION = "4.9.3.9";
  public static final int VERSION = 75;
  public static final String MAIN_VERSION = VERSION + " BI";
  public static final String DEFAULT_STORAGE_EN = "Default storage";
  public static final String DEFAULT_STORAGE_FR = "Rangement par défaut";

  public static final Font FONT_PANEL = new Font("Arial", Font.PLAIN, 12);
  public static final Font FONT_DIALOG_BOLD = new Font("Dialog", Font.BOLD, 12);
  public static final Font FONT_LABEL_BOLD = new Font("Arial", Font.BOLD, 12);
  static final Font FONT_BOUTTON_SMALL = new Font("Arial", Font.PLAIN, 10);
  static final Font FONT_DIALOG_BIG_BOLD = new Font("Dialog", Font.BOLD, 16);

  public static final String TEMP_PLACE = "$$$@@@Temp_--$$$$||||";
  public static final String UNTITLED1_SINFO = "Untitled1.sinfo";
  public static final String UNTITLED = "Untitled";
  public static final String COUNTRIES_XML = "countries.xml";
  public static final String TEXT = ".txt";
  public static final String FRA = "FRA";
  public static final String FRANCE = "France";
  public static final String ITA = "ITA";
  public static final String FR = "fr";
  public static final String ONE = "1";
  public static final String ZERO = "0";
  public static final String ON = "ON";
  public static final String COLUMNS_SEPARATOR = ";";
  public static final DateTimeFormatter DATE_FORMATER_DDMMYYYY = DateTimeFormatter.ofPattern("dd/MM/yyyy");
  public static final DateTimeFormatter DATE_FORMATER_DD_MM_YYYY = DateTimeFormatter.ofPattern("dd-MM-yyyy");
  public static final DateTimeFormatter DATE_FORMATER_DD_MM_YYYY_HH_MM = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
  public static final String TIMESTAMP_PATTERN = "yyyyMMddHHmmss";
  public static final String STAR = "*";
  public static final String THREE_DOTS = "...";
  public static final String ONE_DOT = ".";
  public static final String SLASH = "/";
  public static final String COMMA = ",";
  public static final String DOUBLE_DOT = ":";
  public static final String SPACE = " ";
  public static final String EURO = "\u20ac";
  public static final String DASH = "-";
  public static final char CHAR_O = 'O';
  public static final char CHAR_DOT = '.';
  public static final char CHAR_COMMA = ',';
  public static final String NORMAL = "75cl";
  public static final String HALF = "37.5cl";
  public static final int IMPORT_COMBO_COUNT = 18;
  public static final String KEY_TYPE = "<KEY>";
  public static final String COUNTRY_LABEL_KEY = "Country.";
  public static final String ERRORS_LABEL_KEY = "Errors";
  static final String INFOS_VERSION = " 2022 v";
  static final String PREVIEW_XML = "preview.xml";
  static final String PREVIEW_HTML = "preview.html";
  static final String MY_CELLAR_XML = "MyCellar.xml";
  static final String TYPES_XML = "Types.xml";
  static final String TYPES_MUSIC_XML = "music_types.xml";
  static final String BOUTEILLES_XML = "Bouteilles.xml";
  public static final String HISTORY_XML = "history.xml";
  public static final String WORKSHEET_XML = "worksheet.xml";
  static final String CONFIG_INI = "config.ini";
  static final String RESTART_COMMAND = "restart";
  static final String DOWNLOAD_COMMAND = "download";
  static final String OPTIONS_PARAM = "-opts=";

  public static boolean isVK_O(KeyEvent event) {
    return event.getKeyCode() == KeyEvent.VK_O;
  }

  public static boolean isVK_ENTER(KeyEvent event) {
    return event.getKeyCode() == KeyEvent.VK_ENTER;
  }
}
