package mycellar;

import javax.swing.filechooser.FileFilter;
import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

import static mycellar.ProgramConstants.ONE_DOT;

/**
 * <p>Titre : Cave &agrave; vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2011</p>
 * <p>Soci&eacute;t&eacute; : Seb Informatique</p>
 *
 * @author S&eacute;bastien Duch&eacute;
 * @version 0.8
 * @since 14/12/21
 */

public class Filtre extends FileFilter {

  public static final String EXTENSION_SINFO = "sinfo";
  public static final String EXTENSION_XLS = "xls";
  public static final String EXTENSION_XLSX = "xslx";
  public static final String EXTENSION_ODS = "ods";
  public static final String EXTENSION_HTML = "html";
  public static final String EXTENSION_XML = "xml";
  public static final String EXTENSION_PDF = "pdf";
  public static final String EXTENSION_CSV = "csv";
  public static final String EXTENSION_TXT = "txt";
  public static final Filtre FILTRE_SINFO = new Filtre(EXTENSION_SINFO, Program.getLabel("Infos313"));
  public static final Filtre FILTRE_XLS = new Filtre(EXTENSION_XLS, Program.getLabel("Infos235"));
  public static final Filtre FILTRE_XLSX = new Filtre(EXTENSION_XLSX, Program.getLabel("Infos235"));
  public static final Filtre FILTRE_ODS = new Filtre(EXTENSION_ODS, Program.getLabel("Infos336"));
  public static final Filtre FILTRE_HTML = new Filtre(EXTENSION_HTML, Program.getLabel("Infos205"));
  public static final Filtre FILTRE_XML = new Filtre(EXTENSION_XML, Program.getLabel("Infos203"));
  public static final Filtre FILTRE_PDF = new Filtre(EXTENSION_PDF, Program.getLabel("Infos249"));
  public static final Filtre FILTRE_CSV = new Filtre(EXTENSION_CSV, Program.getLabel("Infos202"));
  public static final Filtre FILTRE_TXT = new Filtre(EXTENSION_TXT, Program.getLabel("Infos201"));

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
    int i = s.lastIndexOf('.');
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
