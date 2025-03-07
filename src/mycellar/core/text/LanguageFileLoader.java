package mycellar.core.text;

import mycellar.Program;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.ResourceBundle.Control;

/**
 * <p>Titre : Cave &agrave; vin
 * <p>Description : Votre description
 * <p>Copyright : Copyright (c) 2011
 * <p>Soci&eacute;t&eacute; : Seb Informatique
 *
 * @author S&eacute;bastien Duch&eacute;
 * @version 1.3
 * @since 07/03/25
 */
public final class LanguageFileLoader {

  private static final LanguageFileLoader INSTANCE = new LanguageFileLoader();
  private static final String LANGUAGE = "Language";
  private static final String CODE_LANG = "CodeLang";
  private Language language;
  private ResourceBundle bundleTitle;
  private ResourceBundle bundleMusicTitle;
  private ResourceBundle bundleError;
  private ResourceBundle bundleLanguage;

  private LanguageFileLoader() {
    loadLanguageFiles(Language.ENGLISH);
  }

  public ResourceBundle getBundleTitle() {
    return bundleTitle;
  }

  public ResourceBundle getBundleError() {
    return bundleError;
  }

  public static LanguageFileLoader getInstance() {
    return INSTANCE;
  }

  static String getLabel(String id) {
    if (INSTANCE.bundleTitle == null) {
      Debug("ERROR: Labels' map not intialized!");
      return "";
    }
    if (Program.isMusicType()) {
      try {
        return INSTANCE.bundleMusicTitle.getString(id);
      } catch (MissingResourceException ignored) {
      }
    }
    return INSTANCE.bundleTitle.getString(id);
  }

  static String getError(String id) {
    if (INSTANCE.bundleError == null) {
      Debug("ERROR: Errors' map not intialized!");
      return "";
    }
    return INSTANCE.bundleError.getString(id);
  }

  public static int getLanguageIndex(String language) {
    if (INSTANCE.bundleLanguage == null) {
      Debug("ERROR: Language' map not intialized!");
      return -1;
    }
    int i = 1;
    while (INSTANCE.bundleLanguage.containsKey(CODE_LANG + i)) {
      String string = INSTANCE.bundleLanguage.getString(CODE_LANG + i);
      if (string.equalsIgnoreCase(language)) {
        return i - 1;
      }
      i++;
    }
    return -1;
  }

  public static String getLanguageFromIndex(int index) {
    return INSTANCE.bundleLanguage.getString(CODE_LANG + (index + 1));
  }

  public static List<String> getLanguages() {
    ArrayList<String> list = new ArrayList<>();
    if (INSTANCE.bundleLanguage == null) {
      Debug("ERROR: Language' map not intialized!");
      return list;
    }
    int i = 1;
    while (INSTANCE.bundleLanguage.containsKey(LANGUAGE + i)) {
      list.add(INSTANCE.bundleLanguage.getString(LANGUAGE + i));
      i++;
    }
    return list;
  }

  public static boolean isFrench() {
    return getInstance().language == Language.FRENCH;
  }

  private static void Debug(String sText) {
    Program.Debug("LanguageFileLoader: " + sText);
  }

  public void loadLanguageFiles(Language language) {
    if (this.language == language) {
      return;
    }
    this.language = language;
    Debug("Loading labels' map in " + language);
    Locale locale = Locale.FRENCH;
    if (language == Language.ENGLISH) {
      locale = Locale.ENGLISH;
    }
    bundleTitle = ResourceBundle.getBundle("title", locale, new UTF8Control());
    bundleError = ResourceBundle.getBundle("error", locale, new UTF8Control());
    bundleMusicTitle = ResourceBundle.getBundle("music", locale, new UTF8Control());
    bundleLanguage = ResourceBundle.getBundle("language", Locale.FRENCH, new UTF8Control());
  }

  public boolean isLoaded() {
    return bundleError != null && bundleLanguage != null && bundleTitle != null;
  }
}

class UTF8Control extends Control {
  @Override
  public ResourceBundle newBundle(String baseName, Locale locale, String format, ClassLoader loader, boolean reload) throws IOException {
    // The below is a copy of the default implementation.
    String bundleName = toBundleName(baseName, locale);
    String resourceName = toResourceName(bundleName, "properties");
    ResourceBundle bundle = null;
    InputStream stream = null;
    if (reload) {
      URL url = loader.getResource(resourceName);
      if (url != null) {
        URLConnection connection = url.openConnection();
        if (connection != null) {
          connection.setUseCaches(false);
          stream = connection.getInputStream();
        }
      }
    } else {
      stream = loader.getResourceAsStream(resourceName);
    }
    if (stream != null) {
      try {
        // Only this line is changed to make it to read properties files as UTF-8.
        bundle = new PropertyResourceBundle(new InputStreamReader(stream, StandardCharsets.UTF_8));
      } finally {
        stream.close();
      }
    }
    return bundle;
  }
}
