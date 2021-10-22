package mycellar;

import javax.swing.filechooser.FileFilter;
import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>Titre : Cave à vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2011</p>
 * <p>Société : Seb Informatique</p>
 *
 * @author Sébastien Duché
 * @version 0.7
 * @since 24/12/20
 */

public class Filtre extends FileFilter {

  public static final Filtre FILTRE_SINFO = new Filtre("sinfo", Program.getLabel("Infos313"));
  public static final Filtre FILTRE_XLS = new Filtre("xls", Program.getLabel("Infos235"));
  public static final Filtre FILTRE_XLSX = new Filtre("xlsx", Program.getLabel("Infos235"));
  public static final Filtre FILTRE_ODS = new Filtre("ods", Program.getLabel("Infos336"));
  public static final Filtre FILTRE_HTML = new Filtre("html", Program.getLabel("Infos205"));
  public static final Filtre FILTRE_XML = new Filtre("xml", Program.getLabel("Infos203"));
  public static final Filtre FILTRE_PDF = new Filtre("pdf", Program.getLabel("Infos249"));
  public static final Filtre FILTRE_CSV = new Filtre("csv", Program.getLabel("Infos202"));
  public static final Filtre FILTRE_TXT = new Filtre("txt", Program.getLabel("Infos201"));

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
    return "." + suffixes.get(0);
  }

}
