package Cave;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
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
 * @version 0.6
 * @since 27/04/14
 */
public class LanguageFileLoader {

	private static String language = null;
	private static LanguageFileLoader instance = new LanguageFileLoader();
	
	private static ResourceBundle bundleTitle;
	private static ResourceBundle bundleError;
	private static ResourceBundle bundleLanguage;

	private LanguageFileLoader(){}

	public static LanguageFileLoader getInstance(){
		return instance;
	}


	public static boolean loadLanguageFiles( String _language ) {
		Debug( "Loading labels' map in " + _language );
		language = _language;
		if(language == null)
			return false;
		Locale locale = Locale.FRENCH;
		if(language.equals("U"))
			locale = Locale.ENGLISH;
		bundleTitle = ResourceBundle.getBundle("title", locale, new UTF8Control());
		bundleError = ResourceBundle.getBundle("error", locale, new UTF8Control());
		bundleLanguage = ResourceBundle.getBundle("language", Locale.FRENCH, new UTF8Control());
		
		return true;
	}

	public static String getLabel( String _id ) {
		if(bundleTitle == null) {
			Debug( "ERROR: Labels' map not intialized " );
			return "";
		}
		return bundleTitle.getString(_id);
	}

	public static String getError( String _id ) {
		if(bundleError == null) {
			Debug( "ERROR: Errors' map not intialized " );
			return "";
		}
		return bundleError.getString(_id);
	}

	public static String getLanguage( String _id ) {
		if(bundleLanguage == null) {
			Debug( "ERROR: Language' map not intialized " );
			return "";
		}
		if(bundleLanguage.containsKey(_id))
			return bundleLanguage.getString(_id);
		return null;
	}

	/**
	 * Debug
	 *
	 * @param sText String
	 */
	public static void Debug(String sText) {
		Program.Debug("LanguageFileLoader: " + sText );
	}
}

class UTF8Control extends Control {
    public ResourceBundle newBundle
        (String baseName, Locale locale, String format, ClassLoader loader, boolean reload)
            throws IllegalAccessException, InstantiationException, IOException
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
                bundle = new PropertyResourceBundle(new InputStreamReader(stream, "UTF-8"));
            } finally {
                stream.close();
            }
        }
        return bundle;
    }
}
