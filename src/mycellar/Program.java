package mycellar;

import mycellar.actions.OpenAddVinAction;
import mycellar.actions.OpenShowErrorsAction;
import mycellar.core.ICutCopyPastable;
import mycellar.core.MyCellarError;
import mycellar.core.MyCellarFields;
import mycellar.core.MyCellarSettings;
import mycellar.core.MyCellarVersion;
import mycellar.core.datas.MyCellarBottleContenance;
import mycellar.countries.Countries;
import mycellar.countries.Country;
import mycellar.pdf.PDFColumn;
import mycellar.pdf.PDFProperties;
import mycellar.pdf.PDFRow;
import mycellar.showfile.ShowFile;
import mycellar.vignobles.CountryVignobles;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.net.util.Base64;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import java.awt.Desktop;
import java.awt.Font;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.MessageFormat;
import java.text.Normalizer;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.OptionalDouble;
import java.util.Properties;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.zip.Adler32;
import java.util.zip.CheckedOutputStream;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * <p>Titre : Cave à vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2003</p>
 * <p>Société : Seb Informatique</p>
 * @author Sébastien Duché
 * @version 20.4
 * @since 10/04/19
 */

public class Program {

	// Manage cave config
	private static MyLinkedHashMap configCave = null;
	// Manage global config
	private static final MyLinkedHashMap CONFIG_GLOBAL = new MyLinkedHashMap();

	private static String archive = null;

	static final Font FONT_PANEL = new Font("Arial", Font.PLAIN, 12);
	static final Font FONT_BOUTTON_SMALL = new Font("Arial", Font.PLAIN, 10);
	static final Font FONT_DIALOG = new Font("Dialog", Font.BOLD, 16);
	static final Font FONT_DIALOG_SMALL = new Font("Dialog", Font.BOLD, 12);
	public static final Font FONT_LABEL_BOLD = new Font("Arial", Font.BOLD, 12);

	static Options options = null;
	static Export export = null;
	static Parametres parametres = null;
	static Creer_Tableaux creer_tableau = null;
	static Importer importer = null;
	static ShowFile showfile = null;
	static ShowFile showtrash = null;
	static Search search = null;
	static ShowHistory history = null;
	static VineyardPanel vignobles = null;
	static Creer_Rangement createPlace = null;
	static Creer_Rangement modifyPlace = null;
	static CellarOrganizerPanel managePlace = null;
	static CellarOrganizerPanel chooseCell = null;
	static Supprimer_Rangement deletePlace = null;
	static Stat stat = null;

	public static ShowFile showerrors = null;
	public static AddVin addWine = null;

	static final PanelInfos PANEL_INFOS = new PanelInfos();
	public static final JTabbedPane TABBED_PANE = new JTabbedPane();

	private static FileWriter oDebugFile = null;
	private static File debugFile = null;
	private static boolean bDebug = false;

	private static final LinkedList<Rangement> RANGEMENTS_LIST = new LinkedList<>();
	private static final LinkedList<Bouteille> TRASH = new LinkedList<>();
	private static final LinkedList<MyCellarError> ERRORS = new LinkedList<>();

	static final Rangement DEFAULT_PLACE = new Rangement("");
	static final Rangement EMPTY_PLACE = new Rangement("");
	static final String TEMP_PLACE = "$$$@@@Temp_--$$$$||||";

	private static String m_sWorkDir = null;
	private static String m_sGlobalDir = null;
	private static boolean m_bWorkDirCalculated = false;
	private static boolean m_bGlobalDirCalculated = false;

	private static boolean m_bIsTrueFile = false;

	private static final String DATA_XML = "data.xml";
	private static final String UNTITLED1_SINFO = "Untitled1.sinfo";
	private static final String PREVIEW_XML = "preview.xml";
	private static final String PREVIEW_HTML = "preview.html";
	private static final String MY_CELLAR_XML = "MyCellar.xml";
	private static final String TYPES_XML = "Types.xml";
	private static final String BOUTEILLES_XML = "Bouteilles.xml";
	private static final String INTERNAL_VERSION = "2.5";
	static final String EXTENSION = ".sinfo";

	private static boolean bYearControlCalculated = false;
	private static boolean bYearControled = false;

	public static Country france = new Country("FRA", "France");
	private static final List<File> DIR_TO_DELETE = new LinkedList<>();
	private static boolean modified = false;
	private static boolean listCaveModified = false;
	private static int nextID = -1;
	public static final MyClipBoard CLIPBOARD = new MyClipBoard();

	/**
	 * init
	 */
	public static void init() {

		try {
			archive = "";
			bDebug = true;
			Debug("===================================================");
			Debug("Starting MyCellar version: "+ MyCellarVersion.VERSION);
			// Initialisation du répertoire de travail
			getWorkDir(false);
			Debug("Program: Initializing Configuration files...");
			File fileIni = new File(getGlobalConfigFilePath());
			if(!fileIni.exists()) {
				fileIni.createNewFile();
			} else {
				FileInputStream inputStream = new FileInputStream(fileIni);
				Properties properties = new Properties();
				properties.load(inputStream);
				inputStream.close();
				//Initialisation de la Map contenant config
				Enumeration<Object> keys = properties.keys();
				while (keys.hasMoreElements()) {
					String key = keys.nextElement().toString();
					putGlobalConfigString(key, properties.getProperty(key));
				}
			}
			LanguageFileLoader.getInstance().loadLanguageFiles('U');
		}
		catch (Exception e) {
			showException(e);
		}
	}

	/**
	 * initConf
	 */
	static void initConf() {
		try {
			Debug("Program: Initializing Configuration files...");
			loadProperties();
			File f = new File(getWorkDir(true) + DATA_XML);
			if(!f.exists()) {
				f.createNewFile();
			}
			LanguageFileLoader.getInstance().loadLanguageFiles('U');

			if (!hasConfigGlobalKey(MyCellarSettings.LANGUAGE) || getGlobalConfigString(MyCellarSettings.LANGUAGE, "").isEmpty()) {
				putGlobalConfigString(MyCellarSettings.LANGUAGE, "F");
			}
			cleanAndUpgrade();
		}
		catch (Exception e) {
			showException(e);
		}
	}

	static void setArchive(String archive) {
		Program.archive = archive;
	}

	static String getArchive() {
		return archive;
	}

	private static String getConfigFilePath() {
		return getWorkDir(true) + "config.ini";
	}
	private static String getGlobalConfigFilePath() {
		return getGlobalDir() + "config.ini";
	}


	/**
	 * @throws IOException
	 */
	private static void loadProperties() throws IOException {

		String inputPropCave = getConfigFilePath();
		configCave = new MyLinkedHashMap();
		File f = new File(inputPropCave);
		if(!f.exists()) {
			f.createNewFile();
		} else {
			FileInputStream inputStream = new FileInputStream(inputPropCave);
			Properties properties = new Properties();
			properties.load(inputStream);
			inputStream.close();
			Enumeration<Object> keys = properties.keys();
			while (keys.hasMoreElements()) {
				String key = keys.nextElement().toString();
				putCaveConfigString(key, properties.getProperty(key));
			}
			if (properties.isEmpty()) {
				// Initialisation de la devise pour les nouveaux fichiers
				putCaveConfigString(MyCellarSettings.DEVISE, "€");
			}
		}

	}

	/**
	 * cleanAndUpgrade
	 * 
	 * Pour nettoyer et mettre a jour le programme
	 */
	private static void cleanAndUpgrade() {
		String sVersion = getCaveConfigString(MyCellarSettings.VERSION, "");
		if(sVersion.isEmpty()) {
			putCaveConfigString(MyCellarSettings.VERSION, INTERNAL_VERSION);
			return;
		}
		int n1 = Integer.parseInt(sVersion.substring(0,1));
		int n2 = Integer.parseInt(sVersion.substring(2,3));
		int val = n1*10 + n2;
		// Affichage du nombre avec 2 décimales.
		if (val < 24) {
			Debug("Program: Updating to internal version 2.4");
			Debug("Program: WARNING: Destroying old files");
			File years = new File(getWorkDir(true) + "Years.xml");
			if(years.exists()) {
				FileUtils.deleteQuietly(years);
			}
			File f1 = new File( getWorkDir(true) + "static_col.sinfo");
			FileUtils.deleteQuietly(f1);
			
			putCaveConfigString(MyCellarSettings.VERSION, INTERNAL_VERSION);
		}
		if (val < 25) {
			Debug("Program: Updating to internal version 2.5");
			if(hasConfigCaveKey("PRICE_SEPARATOR")) {
				getCaveConfig().remove("PRICE_SEPARATOR");
			}
			if(hasConfigCaveKey("JUST_ONE_PLACE")) {
				getCaveConfig().remove("JUST_ONE_PLACE");
			}
			if(hasConfigCaveKey("JUST_ONE_NUM_PLACE")) {
				getCaveConfig().remove("JUST_ONE_NUM_PLACE");
			}
			if(hasConfigCaveKey("SEARCH_DEFAULT")) {
				getCaveConfig().remove("SEARCH_DEFAULT");
			}
			if(hasConfigCaveKey("XML_TYPE")) {
				getCaveConfig().remove("XML_TYPE");
			}

			putCaveConfigBool(MyCellarSettings.ANNEE_AUTO, !getCaveConfigBool(MyCellarSettings.ANNEE_AUTO, false));
			putCaveConfigBool(MyCellarSettings.BOLD, "bold".equals(getCaveConfigString(MyCellarSettings.BOLD, "")));
			putCaveConfigBool(MyCellarSettings.BOLD_XLS, "bold".equals(getCaveConfigString(MyCellarSettings.BOLD_XLS, "")));
			putCaveConfigBool(MyCellarSettings.BOLD_TAB_XLS, "bold".equals(getCaveConfigString(MyCellarSettings.BOLD_TAB_XLS, "")));
			putCaveConfigString(MyCellarSettings.VERSION, INTERNAL_VERSION);
		}
		if(hasConfigCaveKey(MyCellarSettings.EXPORT_CSV + MyCellarFields.NAME.name())) {
			for (int i=0; i<9; i++) {
				getCaveConfig().remove("SIZE_COL"+i+"EXPORT_CSV");
			}
		}
	}


	/**
	 * setLanguage
	 * @param lang String
	 * @return boolean
	 */
	static boolean setLanguage(char lang) {
		Debug("Program: Set Language : "+lang);
		TABBED_PANE.removeAll();
		clearObjectsVariables();
		boolean load = LanguageFileLoader.getInstance().loadLanguageFiles(lang);
		PANEL_INFOS.setLabels();
		Start.getInstance().updateLabels();
		Start.getInstance().updateMainPanel();
		return load;
	}

	public static void showException(Exception e) {
		showException(e, true);
	}

	/**
	 * showException
	 * @param e Exception
	 */
	public static void showException(Throwable e, boolean _bShowWindowErrorAndExit) {
		StackTraceElement[] st =  e.getStackTrace();
		String error = "";
		for (StackTraceElement s : st) {
			error = error.concat("\n" + s);
		}
		if(error.contains("javax.swing.plaf.synth.SynthContext.getPainter(SynthContext.java:171)")
				|| error.contains("javax.swing.LayoutComparator.compare"))
			_bShowWindowErrorAndExit = false;
		if (_bShowWindowErrorAndExit) {
			JOptionPane.showMessageDialog(Start.getInstance(), e.toString(), "Error", JOptionPane.ERROR_MESSAGE);
		}
		try (var fileWriter = new FileWriter(getGlobalDir()+"Errors.log")){
			fileWriter.write(e.toString());
			fileWriter.write(error);
			fileWriter.flush();
		}
		catch (IOException ignored) {}
		Debug("Program: ERROR:");
		Debug("Program: "+e.toString());
		Debug("Program: "+error);
		if (bDebug) {
			e.printStackTrace();
		}
		try {
			oDebugFile.flush();
			oDebugFile.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		sendMail(error, debugFile);
		oDebugFile = null;

		if (_bShowWindowErrorAndExit) {
			System.exit(999);
		}
	}

	private static void sendMail(String error, File filename) {
		try (var stream = new InputStreamReader(Program.class.getClassLoader().getResourceAsStream("resources/MyCellar.dat"));
			 var reader = new BufferedReader(stream)) {
			String line = reader.readLine();
			reader.close();
			stream.close();
			String decoded = new String(Base64.decodeBase64(line.getBytes()));
			final String[] values = decoded.split("/");

			String to = values[0];
			String from = values[1];

			// create some properties and get the default Session
			Properties props = System.getProperties();

			props.put("mail.smtp.auth", "true");
			props.put("mail.smtp.starttls.enable", "true");
			props.put("mail.smtp.host", "smtp.gmail.com");
			props.put("mail.smtp.port", "587");

			Session session = Session.getInstance(props, new Authenticator() {
				@Override
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(from, values[2]);
				}
			});
			session.setDebug(false);

			// create a message
			MimeMessage msg = new MimeMessage(session);
			msg.setFrom(new InternetAddress(from));
			InternetAddress[] address = {new InternetAddress(to)};
			msg.setRecipients(Message.RecipientType.TO, address);
			msg.setSubject("Problem");

			// create and fill the first message part
			MimeBodyPart mbp1 = new MimeBodyPart();
			mbp1.setText(error);

			// create the second message part
			MimeBodyPart mbp2 = new MimeBodyPart();

			// attach the file to the message
			if(filename != null) {
				mbp2.attachFile(filename);
			}

			/*
			 * Use the following approach instead of the above line if
			 * you want to control the MIME type of the attached file.
			 * Normally you should never need to do this.
			 *
		    FileDataSource fds = new FileDataSource(filename) {
			public String getContentType() {
			    return "application/octet-stream";
			}
		    };
		    mbp2.setDataHandler(new DataHandler(fds));
		    mbp2.setFileName(fds.getName());
			 */

			// create the Multipart and add its parts to it
			Multipart mp = new MimeMultipart();
			mp.addBodyPart(mbp1);
			if(filename != null)
				mp.addBodyPart(mbp2);

			// add the Multipart to the message
			msg.setContent(mp);

			// set the Date: header
			msg.setSentDate(new Date());

			/*
			 * If you want to control the Content-Transfer-Encoding
			 * of the attached file, do the following.  Normally you
			 * should never need to do this.
			 *
		    msg.saveChanges();
		    mbp2.setHeader("Content-Transfer-Encoding", "base64");
			 */

			// send the message
			Transport.send(msg);

		} catch (MessagingException mex) {
			mex.printStackTrace();
			Exception ex;
			if ((ex = mex.getNextException()) != null) {
				ex.printStackTrace();
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static LinkedList<Bouteille> getTrash() {
		return TRASH;
	}

	public static void setToTrash(Bouteille b) {
		TRASH.add(b);
	}
	
	public static LinkedList<MyCellarError> getErrors() {
		return ERRORS;
	}

	static void addError(MyCellarError error) {
		ERRORS.add(error);
	}

	static String convertToHTMLString(String s) {
		return StringEscapeUtils.escapeHtml(s);
	}

	public static String convertStringFromHTMLString(String s) {
		return StringEscapeUtils.unescapeHtml(s);
	}

	public static String removeAccents(String s) {
		s = Normalizer.normalize(s, Normalizer.Form.NFD);
		s = s.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
		return s;
	}

	/**
	 * Chargement des données XML (Bouteilles et Rangement) ou des données sérialisées en cas de pb
	 */
	static boolean loadObjects() {
		RANGEMENTS_LIST.clear();
		boolean load = MyXmlDom.readMyCellarXml("", RANGEMENTS_LIST);
		getStorage().loadHistory();
		load |= ListeBouteille.loadXML();

		if(!load) {
			RANGEMENTS_LIST.clear();
			RANGEMENTS_LIST.add(DEFAULT_PLACE);
		}
		return load;
	}
	
	static int getMaxPrice() {
		OptionalDouble i = getStorage().getAllList().stream().mapToDouble(Bouteille::getPriceDouble).max();
		if(i.isPresent()) {
			return (int) i.getAsDouble();
		}
		return 0;
	}
	
	static int getCellarValue() {
		return (int) getStorage().getAllList().stream().mapToDouble(Bouteille::getPriceDouble).sum();
	}

	static int getNbBouteille() {
		return getStorage().getAllList().size();
	}
	
	/**
	 * getNbBouteilleAnnee: retourne le nombre de bouteilles d'une année
	 *
	 * @param an int: année souhaitée
	 * @return int
	 */
	static int getNbBouteilleAnnee(int an) {
		return (int) getStorage().getAllList().stream().filter(bouteille -> bouteille.getAnneeInt() == an).count();
	}
	
	static int[] getAnnees() {
		return getStorage().getAllList().stream().mapToInt(Bouteille::getAnneeInt).distinct().toArray();
	}
	
	/**
	 * getNbAutreAnnee
	 * @return int
	 */
	static int getNbAutreAnnee() {
		return (int) getStorage().getAllList().stream().filter(bouteille -> bouteille.getAnneeInt() < 1000).count();
	}

	/**
	 * getNbNonVintage
	 * @return int
	 */
	static int getNbNonVintage() {
		return (int) getStorage().getAllList().stream().filter(bouteille -> Bouteille.isNonVintageYear(bouteille.getAnnee())).count();
	}


	/**
	 * getAide: Appel de l'aide
	 */
	static void getAide() {

		File f = new File("./Help/MyCellar.hs");

		if (f.exists()) {
			try {
				Runtime.getRuntime().exec("java -jar ./Help/hsviewer.jar -hsURL \"file:./Help/MyCellar.hs\"");
			}
			catch (IOException ignored) {
			}
		}
		else {
			Erreur.showSimpleErreur(getError("Error162"));
		}
	}

	static char getDecimalSeparator() {
		DecimalFormat format = (DecimalFormat) DecimalFormat.getInstance();
		DecimalFormatSymbols symbols = format.getDecimalFormatSymbols();
		return symbols.getDecimalSeparator();
	}

	/**
	 * zipDir: Compression de répertoire
	 *
	 * @param fileName String
	 * @return boolean
	 */
	private static void zipDir(String fileName) {

		Debug("Program: zipDir: Zipping in "+m_sWorkDir+" with archive "+fileName);
		int BUFFER = 2048;
		try {
			// création d'un flux d'écriture sur fichier
			var dest = new FileOutputStream(fileName);
			// calcul du checksum : Adler32 (plus rapide) ou CRC32
			var checksum = new CheckedOutputStream(dest, new Adler32());
			// création d'un buffer d'écriture
			var buff = new BufferedOutputStream(checksum);
			// création d'un flux d'écriture Zip
			try(var out = new ZipOutputStream(buff)) {
				// spécification de la méthode de compression
				out.setMethod(ZipOutputStream.DEFLATED);
				// spécifier la qualité de la compression 0..9
				out.setLevel(Deflater.BEST_COMPRESSION);

				// extraction de la liste des fichiers du répertoire courant
				File f = new File(m_sWorkDir);
				String[] files = f.list();
				// pour chacun des fichiers de la liste
				if (files != null) {
					LinkedList<String> zipEntryList = new LinkedList<>();
					for (String file : files) {
						f = new File(getWorkDir(true) + file);
						if (f.isDirectory() || file.compareTo(UNTITLED1_SINFO) == 0)
							continue;
						// création d'un flux de lecture
						var inputStream = new FileInputStream(getWorkDir(true) + file);
						// création d'un tampon de lecture sur ce flux
						try (var bufferedInputStream = new BufferedInputStream(inputStream, BUFFER)) {
							// création d'en entrée Zip pour ce fichier
							String name = removeAccents(file);
							var entry = new ZipEntry(name);
							if (zipEntryList.contains(name)) {
								continue;
							}
							zipEntryList.add(name);
							// ajout de cette entrée dans le flux d'écriture de l'archive Zip
							out.putNextEntry(entry);
							// écriture du fichier par paquet de BUFFER octets dans le flux d'écriture
							int count;
							// buffer temporaire des données à écrire dans le flux de sortie
							byte[] data = new byte[BUFFER];
							while ((count = bufferedInputStream.read(data, 0, BUFFER)) != -1) {
								out.write(data, 0, count);
							}
							// Close the current entry
							out.closeEntry();
						}
						inputStream.close();
					}
				}
			}
			buff.close();
			checksum.close();
			dest.close();
		}
		catch (Exception e) {
			Debug("Program: zipDir: Error while zipping");
			showException(e, false);
		}
		Debug("Program: zipDir OK");
	}

	/**
	 * unzipDir: Dézippe une archive dans un répertoire
	 *
	 * @param dest_dir String
	 * @return boolean
	 */
	private static boolean unzipDir(String dest_dir) {
		try {
			Debug("Program: Unzip: Archive "+archive);
			int BUFFER = 2048;
			// ouverture fichier entrée
			archive = archive.replaceAll("\\\\", "/");
			File f = new File(archive);
			if(!f.exists())
				return false;
			var fileInputStream = new FileInputStream(archive);
			// ouverture fichier de buffer
			var bufferedInputStream = new BufferedInputStream(fileInputStream);
			// ouverture archive Zip d'entrée
			try(var zipInputStream = new ZipInputStream(bufferedInputStream)) {
				// entrée Zip
				ZipEntry entry;
				// parcours des entrées de l'archive
				while ((entry = zipInputStream.getNextEntry()) != null) {
					// affichage du nom de l'entrée
					// création fichier
					f = new File(dest_dir);
					boolean ok = true;
					if (!f.exists()) {
						ok = f.mkdir();
					}
					if (ok) {
						var fileOutputStream = new FileOutputStream(dest_dir + File.separator + entry.getName());
						Debug("Unzip: File " + dest_dir + File.separator + entry.getName());
						// affectation buffer de sortie
						try (var bufferOutputStream = new BufferedOutputStream(fileOutputStream, BUFFER)) {
							// écriture sur disque
							int count;
							byte[] data = new byte[BUFFER];
							while ((count = zipInputStream.read(data, 0, BUFFER)) != -1) {
								bufferOutputStream.write(data, 0, count);
							}
							// vidage du tampon
							bufferOutputStream.flush();
						}
						fileOutputStream.close();
					}
				}
			}
			bufferedInputStream.close();
			fileInputStream.close();
		}
		catch (Exception e) {
			Debug("Program: Unzip: Archive Error");
			Debug(e.getMessage());
			e.printStackTrace();
			return false;
		}
		Debug("Program: Unzip: Archive OK");
		return true;
	}


	/**
	 * Sauvegarde le fichier
	 */
	public static void save() {
		Debug("Program: Saving...");
		saveAs(archive);
	}

	/**
	 * saveAs
	 * @param sFilename String
	 */
	static void saveAs(String sFilename) {
		Debug("Program: Saving all files...");

		saveCaveProperties();
		saveGlobalProperties();

		if(isListCaveModified()) {
			MyXmlDom.writeMyCellarXml(getCave(), "");
		}

		getStorage().saveHistory();
		CountryVignobles.save();
		ListeBouteille.writeXML();

		if(!sFilename.isEmpty()) {
			zipDir(sFilename);
		} else {
			zipDir(archive);
		}

		archive = sFilename;
		modified = false;
		listCaveModified = false;
		Debug("Program: Saving all files OK");
	}

	public static void setDebug(boolean debug) {
		bDebug = debug;
	}

	public static boolean isDebug() {
		return bDebug;
	}

	/**
	 * Debug
	 *
	 * @param sText String
	 */
	public static void Debug(String sText) {
		if(!bDebug) {
			return;
		}

		try {
			if (oDebugFile == null) {
				String sDir = System.getProperty("user.home");
				if(!sDir.isEmpty())
					sDir += File.separator + "MyCellarDebug";
				File f_obj = new File( sDir );
				boolean ok = true;
				if(!f_obj.exists()) {
					ok = f_obj.mkdir();
				}
				if (ok) {
					String sDate = LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
					debugFile = new File(sDir, "Debug-" + sDate + ".log");
					oDebugFile = new FileWriter(debugFile, true);
				}
			}
			oDebugFile.write("[" + LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) + "]: " + sText + "\n");
			oDebugFile.flush();
		}
		catch (Exception ignored) {}
	}
	
	private static void closeDebug() {
		if(!bDebug || oDebugFile == null) {
			return;
		}
		
		try {
			oDebugFile.flush();
			oDebugFile.close();
		} catch (IOException ignored) {}
		
	}

	/**
	 * GetCave
	 *
	 * @return LinkedList<Rangement>
	 */
	public static LinkedList<Rangement> getCave() {
		return RANGEMENTS_LIST;
	}

	/**
	 * GetCave
	 *
	 * @param _nCave int
	 * @return Rangement
	 */
	public static Rangement getCave(int _nCave) {
		if (_nCave >= RANGEMENTS_LIST.size() || _nCave < 0) {
			return null;
		}
		return RANGEMENTS_LIST.get(_nCave);
	}

	/**
	 * GetCave
	 *
	 * @param name String
	 * @return Rangement
	 */
	public static Rangement getCave(final String name) {
		if (name == null || name.trim().isEmpty()) {
			return null;
		}

		final String placeName = name.trim();
		final List<Rangement> list = RANGEMENTS_LIST.stream().filter(rangement -> rangement.getNom().equals(placeName))
				.collect(Collectors.toList());
		if (list.isEmpty()) {
			return null;
		}
		return list.get(0);
	}
	
	/**
	 * GetCaveIndex
	 *
	 * @param name String
	 * @return int
	 */
	static int getCaveIndex(final String name) {
		if (name == null || name.trim().isEmpty()) {
			return -1;
		}
		final String placeName = name.trim();
		for(int i = 0; i < RANGEMENTS_LIST.size(); i++) {
			if(placeName.equals(RANGEMENTS_LIST.get(i).getNom())) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * addCave
	 *
	 * @param rangement Rangement
	 */
	static void addCave(Rangement rangement) {
		if(rangement == null) {
			return;
		}
		RANGEMENTS_LIST.add(rangement);
		setListCaveModified();
		setModified();
		Debug("Program: Sorting places...");
		Collections.sort(RANGEMENTS_LIST);
	}

	public static boolean hasComplexPlace() {
		return RANGEMENTS_LIST.stream().anyMatch(rangement -> !rangement.isCaisse());
	}

	/**
	 * removeCave
	 *
	 * @param rangement Rangement
	 */
	 static void removeCave(Rangement rangement) {
		if(rangement == null) {
			return;
		}
		RANGEMENTS_LIST.remove(rangement);
		setModified();
		setListCaveModified();
	}


	/**
	 * GetCaveLength
	 *
	 * @return int
	 */
	static int GetCaveLength() {
		return RANGEMENTS_LIST.size();
	}

	/**
	 * openFile: Choix d'un fichier à ouvrir.
	 */
	static boolean newFile() {
		return openaFile(null);
	}

	/**
	 * openaFile: Ouvre un fichier
	 *
	 * @param f File
	 */
	static boolean openaFile(File f) {
		LinkedList<String> list = new LinkedList<>();
		list.addLast(getGlobalConfigString(MyCellarSettings.LAST_OPEN1,""));
		list.addLast(getGlobalConfigString(MyCellarSettings.LAST_OPEN2,""));
		list.addLast(getGlobalConfigString(MyCellarSettings.LAST_OPEN3,""));
		list.addLast(getGlobalConfigString(MyCellarSettings.LAST_OPEN4,""));
		boolean newFile = false;
		if(f != null) {
			Debug("Program: openFile: Opening file: " + f.getAbsolutePath());
			list.remove(f.getAbsolutePath());
		} else {
			newFile = true;
			Debug("Program: openFile: Creating new file");
		}

		// Sauvegarde avant de charger le nouveau fichier
		closeFile();
		
		CountryVignobles.init();
		Countries.init();

		if(f == null) {
			// Nouveau fichier de bouteilles
			ListeBouteille.writeXML();
			setFileSavable(false);
			// Nouveau fichier
			String fic = getWorkDir(true) + UNTITLED1_SINFO;
			f = new File(fic);
			if(f.exists()) {
				f.delete();
			}
			try {
				f.createNewFile();
			} catch (IOException e) {
				showException(e);
			}
		}
		else {
			setFileSavable(f.exists());
		}

		if(!f.exists()) {
			Erreur.showSimpleErreur(MessageFormat.format(getError("Error020"), f.getAbsolutePath())); //Fichier non trouvé);

			putGlobalConfigString(MyCellarSettings.LAST_OPEN1, list.pop());
			putGlobalConfigString(MyCellarSettings.LAST_OPEN2, list.pop());
			putGlobalConfigString(MyCellarSettings.LAST_OPEN3, list.pop());
			// On a déjà enlevé un élément de la liste
			putGlobalConfigString(MyCellarSettings.LAST_OPEN4, "");
			saveGlobalProperties();
			return false;
		}

		archive = f.getAbsolutePath();

		try {
			// Dézippage
			boolean unzipOK = unzipDir(getWorkDir(false));
			Debug("Program: Unzipping " + archive + " to " + getWorkDir(false) + (unzipOK ? " OK" : " KO"));
			if (!unzipOK) {
				archive = "";
				return false;
			}
		}
		catch (Exception e) {
			Debug("Program: ERROR: Unable to unzip file "+archive);
			showException(e,false);
			archive = "";
			return false;
		}

		// Chargement
		File data = new File(getDataFileName());
		if(!newFile && !data.exists()) {
			Debug("Program: ERROR: Unable to find file data.xml!!");
		}

		//Chargement des objets Rangement, Bouteilles et History
		boolean loaded = true;
		Debug("Program: Reading Places, Bottles & History");
		if (!loadObjects()) {
			Debug("Program: Reading Object KO");
			loaded = false;
		}
		// Contrôle du nombre de rangement
		Debug("Program: Checking place count");
		if (loaded) {
			int i = 0;
			LinkedList<Rangement> cave = getCave();
			while (i < GetCaveLength() && cave.get(i) != null) {
				i++;
			}

			if (i != GetCaveLength()) {
				loaded = false;
			}

			Debug("Program: Place Count: Program="+GetCaveLength()+" cave="+i);
		}
		// En cas d'erreur
		if (!loaded) {
			Debug("Program: ERROR: Loading");
		}

		//Chargement des rangement par le fichier d'options si la relecture des objets sérialisés a échouée
		if (GetCaveLength() == 0) {
			Debug("Program: Reading places from file");
			MyXmlDom.readMyCellarXml("", RANGEMENTS_LIST);
		}

		try {
			loadProperties();
		} catch (IOException e) {
			showException(e,false);
			return false;
		}

		MyCellarBottleContenance.load();

		RangementUtils.putTabStock();
		if(!getErrors().isEmpty()) {
			new OpenShowErrorsAction().actionPerformed(null);
		}
		CountryVignobles.load();
		CountryVignobles.addVignobleFromBottles();

		if(isFileSavable()) {
			list.addFirst(f.getAbsolutePath());
		}

		putGlobalConfigString(MyCellarSettings.LAST_OPEN1, list.pop());
		putGlobalConfigString(MyCellarSettings.LAST_OPEN2, list.pop());
		putGlobalConfigString(MyCellarSettings.LAST_OPEN3, list.pop());
		putGlobalConfigString(MyCellarSettings.LAST_OPEN4, list.pop());

		putCaveConfigString(MyCellarSettings.DIR, f.getParent());

		saveGlobalProperties();
		modified = false;
		listCaveModified = false;
		return true;
	}

	/**
	 * closeFile: Fermeture du fichier.
	 */
	static void closeFile() {

		Debug("Program: closeFile: Closing file...");
		try {
			boolean bSave = false;
			if(!archive.isEmpty() && isModified()) {
				if (JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(null, getError("Error199"), getLabel("Infos049"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE)) {
					bSave = true;
					if(!isFileSavable()) {
						try {
							JFileChooser boiteFichier = new JFileChooser();
							boiteFichier.removeChoosableFileFilter(boiteFichier.getFileFilter());
							boiteFichier.addChoosableFileFilter(Filtre.FILTRE_SINFO);
							int retour_jfc = boiteFichier.showSaveDialog(null);
							if (retour_jfc == JFileChooser.APPROVE_OPTION) {
								setFileSavable(true);
								File nomFichier = boiteFichier.getSelectedFile();
								String fic = nomFichier.getAbsolutePath();
								fic = MyCellarControl.controlAndUpdateExtension(fic, Filtre.FILTRE_SINFO);
								archive = fic;
							}
						}
						catch (Exception e3) {
							showException(e3);
						}
					}
				}

				putCaveConfigBool(MyCellarSettings.ANNEE_AUTO, false);
			}

			File f = new File(getPreviewXMLFileName());
			if (f.exists()) {
				f.delete();
			}
			f = new File(getPreviewHTMLFileName());
			if (f.exists()) {
				f.delete();
			}

			getErrors().clear();

			//Tri du tableau et écriture du fichier XML
			if (bSave) {
				if(!ListeBouteille.writeXML()) {
					return;
				}

				if(isListCaveModified()) {
					MyXmlDom.writeMyCellarXml(getCave(), "");
				}

				saveCaveProperties();

				if (!getCave().isEmpty()) {
					getStorage().saveHistory();
					CountryVignobles.save();
					zipDir(archive);
				}
			}

			if(!archive.isEmpty()) {
				// Sauvegarde des propriétés globales
				saveGlobalProperties();

				if (getCaveConfigBool(MyCellarSettings.FIC_EXCEL, false)) {
					//Ecriture Excel
					final String file_excel = getCaveConfigString(MyCellarSettings.FILE_EXCEL, "");
					Debug("Program: Writing backup Excel file: " + file_excel);
					final List<Bouteille> bouteilles = Collections.unmodifiableList(getStorage().getAllList());
					Thread writingExcel = new Thread(() -> RangementUtils.write_XLS(file_excel, bouteilles, true, null));
					Runtime.getRuntime().addShutdownHook(writingExcel);
				}
			}
		}
		catch (Exception ex) {
			showException(ex);
			return;
		}
		TABBED_PANE.removeAll();
		if(!archive.isEmpty()){
			getStorage().close();
			CountryVignobles.close();
			Countries.close();
			Search.clearResults();
		}
		clearObjectsVariables();
		m_bWorkDirCalculated = false;
		archive = "";
		TRASH.clear();
		setFileSavable(false);
		modified = false;
		listCaveModified = false;
		if(getCave() != null) {
			getCave().clear();
		}
		DEFAULT_PLACE.resetStock();
		EMPTY_PLACE.resetStock();
		Debug("Program: closeFile: Closing file Ended");
	}

	private static void clearObjectsVariables() {
		addWine = null;
		createPlace = null;
		creer_tableau = null;
		export = null;
		history = null;
		modifyPlace = null;
		search = null;
		showfile = null;
		showtrash = null;
		vignobles = null;
		options = null;
		parametres = null;
		importer = null;
		history = null;
		managePlace = null;
		chooseCell = null;
		deletePlace = null;
		stat = null;
	}

	private static void deleteTempFiles() {
		for(File f : DIR_TO_DELETE) {
			if(!f.exists() || f.getName().equalsIgnoreCase("Global")) {
				continue;
			}
			try{
				Debug("Program: closeFile: Deleting work directory: "+f.getAbsolutePath());
				FileUtils.deleteDirectory(f);
			}catch(Exception e){
				Debug("Program: Error deleting "+f.getAbsolutePath());
				Debug("Program: "+e.getMessage());
			}
		}
	}

	/**
	 * Save Properties for current cave
	 */
	private static void saveCaveProperties() {
		MyCellarBottleContenance.save();
		saveProperties(configCave, getConfigFilePath());
	}

	/**
	 * Save global properties
	 */
	static void saveGlobalProperties() {
		Debug("Program: Saving Global Properties");
		saveProperties(CONFIG_GLOBAL, getGlobalConfigFilePath());
		Debug("Program: Saving Global Properties OK");
	}

	private static void saveProperties(final MyLinkedHashMap map, final String file) {
		Object[] val = map.keySet().toArray();
		final Properties properties = new Properties();
		for (Object o : val) {
			String key = o.toString();
			properties.put(key, map.getString(key));
		}
		try (var outputStream = new FileOutputStream(file)){
			properties.store(outputStream, null);
		} catch (IOException e) {
			showException(e);
		}
	}

	/**
	 * getGlobalDir: Retourne le nom du repertoire des propriétés globales.
	 * @return
	 */
	private static String getGlobalDir() {
		if(m_bGlobalDirCalculated) {
			return m_sGlobalDir + File.separator;
		}
		m_bGlobalDirCalculated = true;
		String sDir = System.getProperty("user.home");
		if(sDir.isEmpty()) {
			m_sGlobalDir = "./Object/Global";
		} else {
			m_sGlobalDir = sDir + "/MyCellar/Global";
		}
		File f_obj = new File(m_sGlobalDir);
		if(!f_obj.exists()) {
			f_obj.mkdir();
		}

		return m_sGlobalDir + File.separator;
	}

	/**
	 * hasWorkDir: Indique si le repertoire de travail existe.
	 * @return
	 */
	public static boolean hasWorkDir() {
		return m_bWorkDirCalculated;
	}

	/**
	 * getWorkDir: Retourne le nom du repertoire de travail.
	 * @param _bWithEndSlash
	 * @return
	 */
	public static String getWorkDir(boolean _bWithEndSlash) {
		if(m_bWorkDirCalculated) {
			if (_bWithEndSlash) {
				return m_sWorkDir + File.separator;
			}
			return m_sWorkDir;
		}
		m_bWorkDirCalculated = true;
		Debug("Program: Calculating work directory.");
		String sDir = System.getProperty("user.home");
		if(sDir.isEmpty()) {
			m_sWorkDir = "." + File.separator + "Object";
		} else {
			m_sWorkDir = sDir + File.separator + "MyCellar";
		}
		File f_obj = new File(m_sWorkDir);
		if(!f_obj.exists()) {
			f_obj.mkdir();
		}

		String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));

		m_sWorkDir += File.separator + time;

		f_obj = new File(m_sWorkDir);
		if(!f_obj.exists()) {
			f_obj.mkdir();
		}

		Debug("Program: work directory: "+m_sWorkDir);
		DIR_TO_DELETE.add(new File(m_sWorkDir));

		if (_bWithEndSlash) {
			return m_sWorkDir + File.separator;
		}
		return m_sWorkDir;
	}

	static String getShortFilename() {
		return getShortFilename(archive);
	}

	static String getShortFilename(String sFilename) {
		String tmp = sFilename;
		tmp = tmp.replaceAll("\\\\", "/");
		int ind1 = tmp.lastIndexOf("/");
		int ind2 = tmp.indexOf(EXTENSION);
		if (ind1 != -1 && ind2 != -1) {
			tmp = tmp.substring(ind1 + 1, ind2);
		}
		return tmp;
	}

	static String getGlobalConfigString(String _sKey, String _sDefaultValue) {
		return CONFIG_GLOBAL.getString(_sKey, _sDefaultValue);
	}

	public static String getCaveConfigString(String _sKey, String _sDefaultValue) {
		if(null != configCave) {
			return configCave.getString(_sKey, _sDefaultValue);
		}
		Debug("Program: ERROR: Calling null configCave for key '"+_sKey+"' and default value '"+_sDefaultValue+"'");
		return _sDefaultValue;
	}

	static boolean getGlobalConfigBool(String _sKey, boolean defaultValue) {
		return 1 == CONFIG_GLOBAL.getInt(_sKey, defaultValue ? 1 : 0);
	}

	public static boolean getCaveConfigBool(String _sKey, boolean defaultValue) {
		if(null != configCave) {
			final String value = configCave.getString(_sKey, defaultValue ? "1" : "0");
			return ("1".equals(value) || "ON".equalsIgnoreCase(value));
		}
		Debug("Program: ERROR: Calling null configCave for key '"+_sKey+"' and default value '"+defaultValue+"'");
		return defaultValue;
	}

	public static int getCaveConfigInt(String _sKey, int _nDefaultValue) {
		if(null != configCave) {
			return configCave.getInt(_sKey, _nDefaultValue);
		}
		Debug("Program: ERROR: Calling null configCave for key '"+_sKey+"' and default value '"+_nDefaultValue+"'");
		return _nDefaultValue;
	}

	static void putGlobalConfigString(String _sKey, String _sValue) {
		CONFIG_GLOBAL.put(_sKey, _sValue);
	}

	public static void putCaveConfigString(String _sKey, String _sValue) {
		if(null != configCave) {
			configCave.put(_sKey, _sValue);
		} else {
			Debug("Program: ERROR: Unable to put value in configCave: [" + _sKey + " - " + _sValue + "]");
		}
	}

	static void putGlobalConfigBool(String _sKey, boolean _sValue) {
		CONFIG_GLOBAL.put(_sKey, _sValue ? "1" : "0");
	}

	public static void putCaveConfigBool(String _sKey, boolean _sValue) {
		if(null != configCave) {
			configCave.put(_sKey, _sValue ? "1" : "0");
		} else {
			Debug("Program: ERROR: Unable to put value in configCave: [" + _sKey + " - " + _sValue + "]");
		}
	}

	static void putCaveConfigInt(String _sKey, Integer _sValue) {
		configCave.put(_sKey, _sValue);
	}

	static MyLinkedHashMap getCaveConfig() {
		return configCave;
	}

	static boolean hasConfigCaveKey(String _sKey) {
		return null != configCave && configCave.containsKey(_sKey);
	}

	static boolean hasConfigGlobalKey(String _sKey) {
		return CONFIG_GLOBAL.containsKey(_sKey);
	}

	static boolean isFileSavable() {
		return m_bIsTrueFile;
	}

	static void setFileSavable(boolean _bIsTrueFile) {
		m_bIsTrueFile = _bIsTrueFile;
	}

	private static String getDataFileName() {
		return getWorkDir(true) + DATA_XML;
	}

	static String getXMLPlacesFileName() {
		return getWorkDir(true) + MY_CELLAR_XML;
	}

	public static String getXMLTypesFileName() {
		return getWorkDir(true) + TYPES_XML;
	}

	static String getXMLBottlesFileName() {
		return getWorkDir(true) + BOUTEILLES_XML;
	}

	public static String getUntitledFileName() {
		return UNTITLED1_SINFO;
	}

	public static String getPreviewXMLFileName() {
		return getGlobalDir() + PREVIEW_XML;
	}

	private static String getPreviewHTMLFileName() {
		return getGlobalDir() + PREVIEW_HTML;
	}

	public static Storage getStorage() {
		return SerializedStorage.getInstance();
	}

	public static String getLabel(String _id) {
		return getLabel(_id, true);
	}

	public static String getLabel(String _id, boolean displayError) {
		try {
			return LanguageFileLoader.getLabel(_id);
		}catch(MissingResourceException e) {
			if(displayError) {
				JOptionPane.showMessageDialog(null, "Missing Label "+_id, "Error", JOptionPane.ERROR_MESSAGE);
			}
			return _id;
		}
	}

	public static String getError(String _id) {
		try {
			return LanguageFileLoader.getError(_id);
		}catch(MissingResourceException e) {
			JOptionPane.showMessageDialog(null, "Missing Error "+_id, "Error", JOptionPane.ERROR_MESSAGE);
			return _id;
		}
	}

	static String getLanguage(String _id) {
		return LanguageFileLoader.getLanguage(_id);
	}

	public static void open(File file) {
		if (file != null) {
			try {
				if (System.getProperty("os.name").startsWith("Mac"))
					Runtime.getRuntime().exec("/usr/bin/open " + file.getAbsolutePath());
				else
					Desktop.getDesktop().browse(file.toURI());
			} catch (IOException e) {
				showException(e, true);
			}
		}
	}

	public static boolean hasYearControl() {
		if (bYearControlCalculated) {
			return bYearControled;
		}
		bYearControled = getCaveConfigBool(MyCellarSettings.ANNEE_CTRL, false);
		bYearControlCalculated = true;
		return bYearControled;
	}

	static void setYearControl(boolean b) {
		bYearControled = b;
		putCaveConfigBool(MyCellarSettings.ANNEE_CTRL, bYearControled);
		bYearControlCalculated = true;
	}

	static void updateAllPanels() {
		if(addWine != null) {
			addWine.setUpdateView();
		}
		if(search != null) {
			search.setUpdateView();
		}
		if(deletePlace != null) {
			deletePlace.setUpdateView();
		}
		if(showfile != null) {
			showfile.setUpdateView();
		}
		if(showtrash != null) {
			showtrash.setUpdateView();
		}
		if(chooseCell != null) {
			chooseCell.setUpdateView();
		}
		if(managePlace != null) {
			managePlace.setUpdateView();
		}
		if (showfile != null) {
			showfile.setUpdateView();
		}
		if (showerrors != null) {
			showerrors.setUpdateView();
		}
	}

	static void updateManagePlacePanel() {
		if(managePlace != null) {
			managePlace.setUpdateView();
		}
	}

	public static List<Country> getCountries() {
		return Countries.getInstance().getCountries();
	}

	public static int findTab(ImageIcon image) {
		for(int i = 0; i< TABBED_PANE.getTabCount(); i++){
			try{
				if(TABBED_PANE.getTabComponentAt(i) != null && TABBED_PANE.getIconAt(i) != null && TABBED_PANE.getIconAt(i).equals(image)) {
					return i;
				}
			}catch(Exception e){}
		}
		return -1;
	}

	public static void setModified() {
		modified = true;
	}

	private static boolean isModified() {
		return modified;
	}

	static void setListCaveModified() {
		listCaveModified = true;
	}

	private static boolean isListCaveModified() {
		return listCaveModified;
	}

	public static PDFProperties getPDFProperties() {
		String title = getCaveConfigString(MyCellarSettings.PDF_TITLE, "");
		int titleSize = getCaveConfigInt(MyCellarSettings.TITLE_SIZE, 10);
		int textSize = getCaveConfigInt(MyCellarSettings.TEXT_SIZE, 10);
		final boolean border = getCaveConfigBool(MyCellarSettings.BORDER, true);
		boolean boldTitle = getCaveConfigBool(MyCellarSettings.BOLD, false);

		PDFProperties properties = new PDFProperties(title, titleSize, textSize, border, boldTitle);

		int nbCol = MyCellarFields.getFieldsList().size();
		int countColumn = 0;
		for(int i=0; i<nbCol; i++) {
			int export = getCaveConfigInt(MyCellarSettings.SIZE_COL + i + "EXPORT", 0);
			if(export == 1) {
				countColumn++;
				int sizeCol = getCaveConfigInt(MyCellarSettings.SIZE_COL + i, 5);
				properties.addColumn(MyCellarFields.getFieldsList().get(i), i, sizeCol, MyCellarFields.getFieldsList().get(i).toString());
			}
		}
		if(countColumn == 0) {
			properties.addColumn(MyCellarFields.getFieldsList().get(0), 0, 10, MyCellarFields.getFieldsList().get(0).toString());
			properties.addColumn(MyCellarFields.getFieldsList().get(1), 1, 2, MyCellarFields.getFieldsList().get(1).toString());
			properties.addColumn(MyCellarFields.getFieldsList().get(3), 3, 5, MyCellarFields.getFieldsList().get(3).toString());
			properties.addColumn(MyCellarFields.getFieldsList().get(7), 7, 2, MyCellarFields.getFieldsList().get(7).toString());
		}
		return properties;
	}

	static List<PDFRow> getPDFRows(List<Bouteille> list, PDFProperties properties) {
		LinkedList<PDFRow> rows = new LinkedList<>();
		LinkedList<PDFColumn> columns = properties.getColumns();
		PDFRow row;
		for(Bouteille b : list) {
			row = new PDFRow();
			for(PDFColumn column : columns) {
				if(column.getField().equals(MyCellarFields.NAME)) {
					row.addCell(b.getNom());
				}	else if(column.getField().equals(MyCellarFields.YEAR)) {
					row.addCell(b.getAnnee());
				} else if(column.getField().equals(MyCellarFields.TYPE)) {
					row.addCell(b.getType());
				} else if(column.getField().equals(MyCellarFields.PLACE)) {
					row.addCell(b.getEmplacement());
				} else if(column.getField().equals(MyCellarFields.NUM_PLACE)) {
					row.addCell(Integer.toString(b.getNumLieu()));
				} else if(column.getField().equals(MyCellarFields.LINE)) {
					row.addCell(Integer.toString(b.getLigne()));
				} else if(column.getField().equals(MyCellarFields.COLUMN)) {
					row.addCell(Integer.toString(b.getColonne()));
				} else if(column.getField().equals(MyCellarFields.PRICE)) {
					row.addCell(b.getPrix());
				} else if(column.getField().equals(MyCellarFields.COMMENT)) {
					row.addCell(b.getComment());
				} else if(column.getField().equals(MyCellarFields.MATURITY)) {
					row.addCell(b.getMaturity());
				} else if(column.getField().equals(MyCellarFields.PARKER)) {
					row.addCell(b.getParker());
				} else if(column.getField().equals(MyCellarFields.COUNTRY)) {
					if(b.getVignoble() != null) {
						row.addCell(b.getVignoble().getCountry());
					} else {
						row.addCell("");
					}
				}	else if(column.getField().equals(MyCellarFields.VINEYARD)) {
					if(b.getVignoble() != null) {
						row.addCell(b.getVignoble().getName());
					} else {
						row.addCell("");
					}
				}	else if(column.getField().equals(MyCellarFields.AOC)) {
					if(b.getVignoble() != null) {
						row.addCell(b.getVignoble().getAOC());
					} else {
						row.addCell("");
					}
				}	else if(column.getField().equals(MyCellarFields.IGP)) {
					if(b.getVignoble() != null) {
						row.addCell(b.getVignoble().getIGP());
					} else {
						row.addCell("");
					}
				}
				else if(column.getField().equals(MyCellarFields.COLOR)) {
					row.addCell(BottleColor.getColor(b.getColor()).toString());
				}
			}
			rows.add(row);
		}
		return rows;
	}

	static PDFRow getPDFHeader(PDFProperties properties) {
		LinkedList<PDFColumn> columns = properties.getColumns();
		PDFRow row = new PDFRow();
		for(PDFColumn column : columns) {
			row.addCell(column.getTitle());
		}
		return row;
	}

	private static void cleanDebugFiles() {
		String sDir = System.getProperty("user.home");
		sDir += File.separator + "MyCellarDebug";
		File f = new File(sDir);
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime monthsAgo = LocalDateTime.now().minusMonths(2);
		String[] files = f.list((dir, name) -> {
			String date = "";
				if (name.startsWith("Debug-") && name.endsWith(".log")) {
					date = name.substring(6, name.indexOf(".log"));
				}
				if (name.startsWith("DebugFtp-") && name.endsWith(".log")) {
					date = name.substring(9, name.indexOf(".log"));
				}
				if (!date.isEmpty()) {
					String[] fields = date.split("-");
					LocalDateTime dateTime = now.withMonth(Integer.parseInt(fields[1])).withDayOfMonth(Integer.parseInt(fields[0])).withYear(Integer.parseInt(fields[2]));
					return dateTime.isBefore(monthsAgo);
				}
				return false;
		});

		if (files != null) {
			for (String file : files) {
				f = new File(sDir, file);
				Debug("Program: Deleting file " + f.getAbsolutePath());
				f.deleteOnExit();
			}
		}
	}

	public static void saveShowColumns(String value) {
		putCaveConfigString(MyCellarSettings.SHOWFILE_COLUMN, value);
	}

	public static String getShowColumns() {
		return getCaveConfigString(MyCellarSettings.SHOWFILE_COLUMN, "");
	}

	static void saveHTMLColumns(List<MyCellarFields> cols) {
		StringBuilder s = new StringBuilder();
		for(MyCellarFields f : cols) {
			if(s.length() != 0) {
				s.append(";");
			}
			s.append(f.name());
		}
		putCaveConfigString(MyCellarSettings.HTMLEXPORT_COLUMN, s.toString());
	}

	static ArrayList<MyCellarFields> getHTMLColumns() {
	ArrayList<MyCellarFields> cols = new ArrayList<>();
		String s = getCaveConfigString(MyCellarSettings.HTMLEXPORT_COLUMN, "");
		String [] fields = s.split(";");
		for(String field : fields) {
			for(MyCellarFields f : MyCellarFields.getFieldsList()) {
				if(f.name().equals(field)) {
					cols.add(f);
					break;
				}
			}
		}
		return cols;
	}

	static boolean isSelectedTab(ITabListener tab) {
		if(TABBED_PANE.getSelectedComponent() == null) {
			return false;
		}
		return TABBED_PANE.getSelectedComponent().equals(tab);
	}

  static boolean isCutCopyPastTab() {
    return TABBED_PANE.getSelectedComponent() != null && TABBED_PANE.getSelectedComponent() instanceof ICutCopyPastable;
  }

  static <T> T getSelectedComponent(Class<T> className) {
	  return className.cast(TABBED_PANE.getSelectedComponent());
  }

	public static String readFirstLineText(final File f) {
		if(f == null) {
			return "";
		}
		if(!f.getName().toLowerCase().endsWith(".txt")) {
			return "";
		}
		try (var scanner = new Scanner(f)){
			String line = scanner.nextLine();
			return line.trim();
		} catch (IOException e) {
			showException(e, true);
		}
		return "";
	}
	
	static HistoryList getHistoryList() {
		return getStorage().getHistoryList();
	}
	
	public static List<History> getHistory() {
		return getStorage().getHistoryList().getHistory();
	}
	
	private static DecimalFormat getDecimalFormat(final Locale locale) {
		final DecimalFormatSymbols dfs = new DecimalFormatSymbols();

		if (Locale.UK.equals(locale) || Locale.US.equals(locale)) {
		  dfs.setGroupingSeparator(',');
		  dfs.setDecimalSeparator('.');
		} else {
		  dfs.setGroupingSeparator('.');
		  dfs.setDecimalSeparator(',');
		}

		// format with grouping separator and decimal separator.
		// always print first digit before comma, and two digits after comma.
		return new DecimalFormat("###0.00",dfs);
	  }
	  
	public static String bigDecimalToString(final BigDecimal value, final Locale locale) {
    return getDecimalFormat(locale).format(value);
  }
  
  public static BigDecimal stringToBigDecimal(final String value) throws NumberFormatException {
	  StringBuilder buf = new StringBuilder();
	  for(int i=0; i<value.length(); i++) {
		  char c = value.charAt(i);
		  if (c == ' ') {
			  continue;
		  }
		  if (c == ',' || c == '.') {
			  buf.append('.');
		  }
		  if (Character.isDigit(c)) {
			  buf.append(c);
		  }
	  }
		return new BigDecimal(buf.toString()).setScale(2, RoundingMode.HALF_UP);
  }

	static int getNewID() {
		if (nextID == -1) {
			nextID = getStorage().getAllNblign();
		}
		do {
			++nextID;
		} while (getStorage().getAllList().stream().anyMatch(bouteille -> bouteille.getId() == nextID));
		return nextID;
	}

	public static void modifyBottles(LinkedList<Bouteille> listToModify) {
		if (listToModify == null || listToModify.isEmpty()) {
			return;
		}
		if (listToModify.size() == 1) {
			Start.getInstance().showBottle(listToModify.getFirst(), true);
		} else {
			new OpenAddVinAction(listToModify).actionPerformed(null);
		}
	}

	static void exit() {
		deleteTempFiles();
		cleanDebugFiles();
		Debug("Program: MyCellar End");
		closeDebug();
	}
}
