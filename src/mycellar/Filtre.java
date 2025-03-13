package mycellar;

import javax.swing.filechooser.FileFilter;
import java.io.File;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static mycellar.ProgramConstants.CHAR_DOT;
import static mycellar.ProgramConstants.ONE_DOT;
import static mycellar.core.text.MyCellarLabelManagement.getLabel;
import static mycellar.general.ResourceKey.FILTER_CSV;
import static mycellar.general.ResourceKey.FILTER_HTML;
import static mycellar.general.ResourceKey.FILTER_ODS;
import static mycellar.general.ResourceKey.FILTER_PDF;
import static mycellar.general.ResourceKey.FILTER_SINFO;
import static mycellar.general.ResourceKey.FILTER_TXT;
import static mycellar.general.ResourceKey.FILTER_XLS;
import static mycellar.general.ResourceKey.FILTER_XML;

/**
 * Titre : Cave &agrave; vin
 * Description : Votre description
 * Copyright : Copyright (c) 2011
 * Soci&eacute;t&eacute; : Seb Informatique
 *
 * @author S&eacute;bastien Duch&eacute;
 * @version 1.2
 * @since 13/03/25
 */

public class Filtre extends FileFilter {

  public static final String EXTENSION_SINFO = "sinfo";
  private static final String EXTENSION_XLS = "xls";
  private static final String EXTENSION_XLSX = "xlsx";
  private static final String EXTENSION_ODS = "ods";
  private static final String EXTENSION_HTML = "html";
  public static final String EXTENSION_XML = "xml";
  private static final String EXTENSION_PDF = "pdf";
  private static final String EXTENSION_CSV = "csv";
  private static final String EXTENSION_TXT = "txt";
  public static final Filtre FILTRE_SINFO = new Filtre(EXTENSION_SINFO, getLabel(FILTER_SINFO));
  public static final Filtre FILTRE_XLS = new Filtre(EXTENSION_XLS, getLabel(FILTER_XLS));
  public static final Filtre FILTRE_XLSX = new Filtre(EXTENSION_XLSX, getLabel(FILTER_XLS));
  public static final Filtre FILTRE_ODS = new Filtre(EXTENSION_ODS, getLabel(FILTER_ODS));
  public static final Filtre FILTRE_HTML = new Filtre(EXTENSION_HTML, getLabel(FILTER_HTML));
  public static final Filtre FILTRE_XML = new Filtre(EXTENSION_XML, getLabel(FILTER_XML));
  public static final Filtre FILTRE_PDF = new Filtre(EXTENSION_PDF, getLabel(FILTER_PDF));
  public static final Filtre FILTRE_CSV = new Filtre(EXTENSION_CSV, getLabel(FILTER_CSV));
  public static final Filtre FILTRE_TXT = new Filtre(EXTENSION_TXT, getLabel(FILTER_TXT));

  private final List<String> suffixes;
  private final String description;

  public Filtre(List<String> suffixes, String description) {
    this.suffixes = suffixes.stream()
        .map(String::toLowerCase)
        .collect(toList());
    this.description = description;
  }

  private Filtre(String suffixe, String description) {
    this(List.of(suffixe), description);
  }

  @Override
  public boolean accept(File f) {
    if (f.isDirectory()) {
      return true;
    }

    String suffixe = null;
    String s = f.getName();
    int i = s.lastIndexOf(CHAR_DOT);
    if (i > 0 && i < s.length() - 1) {
      suffixe = s.substring(i + 1).toLowerCase().strip();
    }
    return suffixe != null && suffixes.contains(suffixe);
  }

  @Override
  public String getDescription() {
    return description;
  }

  public String toString() {
    return ONE_DOT + suffixes.getFirst();
  }

}
