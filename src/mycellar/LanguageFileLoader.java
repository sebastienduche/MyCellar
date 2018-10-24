package mycellar;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
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
 * @version 0.8
 * @since 22/08/18
 */
public class LanguageFileLoader {

	private static final LanguageFileLoader INSTANCE = new LanguageFileLoader();

	private char language;
	
	private ResourceBundle bundleTitle;
	private ResourceBundle bundleError;
	private ResourceBundle bundleLanguage;

	private LanguageFileLoader(){
		language = ' ';
		loadLanguageFiles('U');
	}

	public static LanguageFileLoader getInstance(){
		return INSTANCE;
	}


	boolean loadLanguageFiles(char language) {

		if (this.language == language) {
			return true;
		}
		this.language = language;
		Debug( "Loading labels' map in " + language);
		Locale locale = Locale.FRENCH;
		if(language == 'U') {
			locale = Locale.ENGLISH;
		}
		bundleTitle = ResourceBundle.getBundle("title", locale, new UTF8Control());
		bundleError = ResourceBundle.getBundle("error", locale, new UTF8Control());
		bundleLanguage = ResourceBundle.getBundle("language", Locale.FRENCH, new UTF8Control());
		
		return true;
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

	static String getLanguage( String _id ) {
		if(INSTANCE.bundleLanguage == null) {
			Debug("ERROR: Language' map not intialized!");
			return "";
		}
		if(INSTANCE.bundleLanguage.containsKey(_id)) {
			return INSTANCE.bundleLanguage.getString(_id);
		}
		return null;
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
