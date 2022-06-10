package mycellar;

import javax.swing.filechooser.FileFilter;
import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

import static mycellar.ProgramConstants.CHAR_DOT;
import static mycellar.ProgramConstants.ONE_DOT;
import static mycellar.core.text.MyCellarLabelManagement.getLabel;

/**
 * Titre : Cave &agrave; vin
 * Description : Votre description
 * Copyright : Copyright (c) 2011
 * Soci&eacute;t&eacute; : Seb Informatique
 *
 * @author S&eacute;bastien Duch&eacute;
 * @version 1.1
 * @since 10/06/22
 */

public class Filtre extends FileFilter {

  public static final String EXTENSION_SINFO = "sinfo";
  public static final String EXTENSION_XLS = "xls";
  public static final String EXTENSION_XLSX = "xlsx";
  public static final String EXTENSION_ODS = "ods";
  public static final String EXTENSION_HTML = "html";
  public static final String EXTENSION_XML = "xml";
  public static final String EXTENSION_PDF = "pdf";
  public static final String EXTENSION_CSV = "csv";
  public static final String EXTENSION_TXT = "txt";
  public static final Filtre FILTRE_SINFO = new Filtre(EXTENSION_SINFO, getLabel("Filter.Sinfo"));
  public static final Filtre FILTRE_XLS = new Filtre(EXTENSION_XLS, getLabel("Filter.Xls"));
  public static final Filtre FILTRE_XLSX = new Filtre(EXTENSION_XLSX, getLabel("Filter.Xls"));
  public static final Filtre FILTRE_ODS = new Filtre(EXTENSION_ODS, getLabel("Filter.Ods"));
  public static final Filtre FILTRE_HTML = new Filtre(EXTENSION_HTML, getLabel("Filter.Html"));
  public static final Filtre FILTRE_XML = new Filtre(EXTENSION_XML, getLabel("Filter.Xml"));
  public static final Filtre FILTRE_PDF = new Filtre(EXTENSION_PDF, getLabel("Filter.Pdf"));
  public static final Filtre FILTRE_CSV = new Filtre(EXTENSION_CSV, getLabel("Filter.Csv"));
  public static final Filtre FILTRE_TXT = new Filtre(EXTENSION_TXT, getLabel("Filter.Txt"));

  private final List<String> suffixes;
  private final String description;

  public Filtre(List<String> suffixes, String description) {
    this.suffixes = suffixes.stream()
        .map(String::toLowerCase)
        .collect(Collectors.toList());
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
    return ONE_DOT + suffixes.get(0);
  }

}
