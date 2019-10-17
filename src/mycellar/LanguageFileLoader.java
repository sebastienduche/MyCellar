package mycellar;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.ResourceBundle.Control;

/**
 * <p>Titre : Cave à vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2011</p>
 * <p>Société : Seb Informatique</p>
 * @author Sébastien Duché
 * @version 0.9
 * @since 17/10/19
 */
public class LanguageFileLoader {

	private static final LanguageFileLoader INSTANCE = new LanguageFileLoader();
	private static final String LANGUAGE = "Language";
	private static final String CODE_LANG = "CodeLang";

	enum Language {
		FRENCH('F'),
		ENGLISH('U');

		private final char language;

		Language(char language) {
			this.language = language;
		}

		public char getLanguage() {
			return language;
		}
	}
	private Language language;
	
	private ResourceBundle bundleTitle;
	private ResourceBundle bundleError;
	private ResourceBundle bundleLanguage;

	private LanguageFileLoader() {
		loadLanguageFiles(Language.ENGLISH);
	}

	public static LanguageFileLoader getInstance(){
		return INSTANCE;
	}


	void loadLanguageFiles(Language language) {
		if (this.language == language) {
			return;
		}
		this.language = language;
		Debug( "Loading labels' map in " + language);
		Locale locale = Locale.FRENCH;
		if(language == Language.ENGLISH) {
			locale = Locale.ENGLISH;
		}
		bundleTitle = ResourceBundle.getBundle("title", locale, new UTF8Control());
		bundleError = ResourceBundle.getBundle("error", locale, new UTF8Control());
		bundleLanguage = ResourceBundle.getBundle("language", Locale.FRENCH, new UTF8Control());
	}

	boolean isLoaded() {
		return bundleError != null && bundleLanguage != null && bundleTitle != null;
	}

	public static String getLabel(String _id) {
		if(INSTANCE.bundleTitle == null) {
			Debug("ERROR: Labels' map not intialized!");
			return "";
		}
		return INSTANCE.bundleTitle.getString(_id);
	}

	public static String getError(String _id) {
		if(INSTANCE.bundleError == null) {
			Debug("ERROR: Errors' map not intialized!");
			return "";
		}
		return INSTANCE.bundleError.getString(_id);
	}

	static int getLanguageIndex(String language) {
		if(INSTANCE.bundleLanguage == null) {
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

	static String getLanguageFromIndex(int index) {
		return INSTANCE.bundleLanguage.getString(CODE_LANG + (index + 1));
	}

	static List<String> getLanguages() {
		ArrayList<String> list = new ArrayList<>();
		if(INSTANCE.bundleLanguage == null) {
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

	static Language getLanguage(char language) {
		if (language == Language.ENGLISH.getLanguage()) {
			return Language.ENGLISH;
		}
		if (language == Language.FRENCH.getLanguage()) {
			return Language.FRENCH;
		}
		return Language.ENGLISH;
	}

	/**
	 * Debug
	 *
	 * @param sText String
	 */
	private static void Debug(String sText) {
		Program.Debug("LanguageFileLoader: " + sText);
	}
}

class UTF8Control extends Control {
	@Override
    public ResourceBundle newBundle
        (String baseName, Locale locale, String format, ClassLoader loader, boolean reload)
            throws IOException
    {
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
