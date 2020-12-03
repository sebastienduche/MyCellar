package mycellar;

import mycellar.actions.OpenAddVinAction;
import mycellar.actions.OpenShowErrorsAction;
import mycellar.capacity.CapacityPanel;
import mycellar.core.Grammar;
import mycellar.core.IAddVin;
import mycellar.core.ICutCopyPastable;
import mycellar.core.IMyCellar;
import mycellar.core.IUpdatable;
import mycellar.core.LabelProperty;
import mycellar.core.MyCellarError;
import mycellar.core.MyCellarFields;
import mycellar.core.MyCellarFile;
import mycellar.core.MyCellarLabelManagement;
import mycellar.core.MyCellarSettings;
import mycellar.core.MyLinkedHashMap;
import mycellar.core.UnableToOpenFileException;
import mycellar.core.datas.MyCellarBottleContenance;
import mycellar.core.datas.jaxb.AppelationJaxb;
import mycellar.core.datas.jaxb.CountryJaxb;
import mycellar.core.datas.jaxb.CountryVignobleJaxb;
import mycellar.core.datas.worksheet.WorkSheetList;
import mycellar.core.datas.jaxb.CountryListJaxb;
import mycellar.pdf.PDFColumn;
import mycellar.pdf.PDFProperties;
import mycellar.pdf.PDFRow;
import mycellar.placesmanagement.Creer_Rangement;
import mycellar.placesmanagement.Supprimer_Rangement;
import mycellar.showfile.ShowFile;
import mycellar.vignobles.CountryVignobleController;
import mycellar.vignobles.VineyardPanel;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
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
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Font;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
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
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.Properties;
import java.util.Scanner;
import java.util.stream.Collectors;

import static mycellar.ScreenType.ADDVIN;
import static mycellar.ScreenType.CAPACITY;
import static mycellar.ScreenType.CELL_ORGANIZER;
import static mycellar.ScreenType.CHOOSE_CELL;
import static mycellar.ScreenType.CREATE_PLACE;
import static mycellar.ScreenType.CREER_TABLEAU;
import static mycellar.ScreenType.EXPORT;
import static mycellar.ScreenType.HISTORY;
import static mycellar.ScreenType.IMPORTER;
import static mycellar.ScreenType.MODIFY_PLACE;
import static mycellar.ScreenType.PARAMETRES;
import static mycellar.ScreenType.SEARCH;
import static mycellar.ScreenType.SHOW_ERRORS;
import static mycellar.ScreenType.SHOW_FILE;
import static mycellar.ScreenType.SHOW_TRASH;
import static mycellar.ScreenType.SHOW_WORKSHEET;
import static mycellar.ScreenType.STATS;
import static mycellar.ScreenType.SUPPRIMER_RANGEMENT;
import static mycellar.ScreenType.VIGNOBLES;
import static mycellar.core.MyCellarSettings.PROGRAM_TYPE;

/**
 * <p>Titre : Cave &agrave; vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2003</p>
 * <p>Soci&eacute;t&eacute; : Seb Informatique</p>
 * @author S&eacute;bastien Duch&eacute;
 * @version 23.9
 * @since 03/12/20
 */

public final class Program {

	public static final String INTERNAL_VERSION = "3.8.2.4";
	public static final int VERSION = 65;
	static final String INFOS_VERSION = " 2020 v";
	private static Type programType = Type.WINE;
	private static final String KEY_TYPE = "<KEY>";

	private static MyCellarFile myCellarFile = null;
	// Manage global config
	private static final MyLinkedHashMap CONFIG_GLOBAL = new MyLinkedHashMap();

	public static final Font FONT_PANEL = new Font("Arial", Font.PLAIN, 12);
	static final Font FONT_BOUTTON_SMALL = new Font("Arial", Font.PLAIN, 10);
	static final Font FONT_DIALOG = new Font("Dialog", Font.BOLD, 16);
	public static final Font FONT_DIALOG_SMALL = new Font("Dialog", Font.BOLD, 12);
	public static final Font FONT_LABEL_BOLD = new Font("Arial", Font.BOLD, 12);

	private static final Map<ScreenType, IMyCellar> OPENED_OBJECTS = new EnumMap<>(ScreenType.class);
	private static final Map<ScreenType, IUpdatable> UPDATABLE_OBJECTS = new EnumMap<>(ScreenType.class);
	private static final Map<Integer, IUpdatable> UPDATABLE_BOTTLES = new HashMap<>();

	static final PanelInfos PANEL_INFOS = new PanelInfos();
	public static final JTabbedPane TABBED_PANE = new JTabbedPane();

	private static FileWriter oDebugFile = null;
	private static File debugFile = null;

	private static final LinkedList<Rangement> PLACES = new LinkedList<>();
	private static final LinkedList<Bouteille> TRASH = new LinkedList<>();
	private static final LinkedList<MyCellarError> ERRORS = new LinkedList<>();

	static final Rangement DEFAULT_PLACE = new Rangement.CaisseBuilder("").build();
	public static final Rangement EMPTY_PLACE = new Rangement.CaisseBuilder("").build();
	static final String TEMP_PLACE = "$$$@@@Temp_--$$$$||||";

	private static String m_sWorkDir = null;
	private static String m_sGlobalDir = null;
	private static boolean m_bWorkDirCalculated = false;
	private static boolean m_bGlobalDirCalculated = false;

	public static final String UNTITLED1_SINFO = "Untitled1.sinfo";
	private static final String PREVIEW_XML = "preview.xml";
	private static final String PREVIEW_HTML = "preview.html";
	private static final String MY_CELLAR_XML = "MyCellar.xml";
	private static final String TYPES_XML = "Types.xml";
	private static final String BOUTEILLES_XML = "Bouteilles.xml";
	private static final String CONFIG_INI = "config.ini";
	public static final String COUNTRIES_XML = "countries.xml";
	static final String EXTENSION = ".sinfo";
	public static final String TEXT = ".txt";

	private static boolean bYearControlCalculated = false;
	private static boolean bYearControled = false;

	public static final String FRA = "FRA";
	public static final String ITA = "ITA";
	public static final String FR = "fr";
	public static final CountryJaxb FRANCE = new CountryJaxb(FRA, "France");
	public static final CountryJaxb NO_COUNTRY = new CountryJaxb("");
	public static final CountryVignobleJaxb NO_VIGNOBLE = new CountryVignobleJaxb();
	public static final AppelationJaxb NO_APPELATION = new AppelationJaxb();
	private static final List<File> DIR_TO_DELETE = new LinkedList<>();
	private static boolean modified = false;
	private static boolean listCaveModified = false;
	private static int nextID = -1;
	private static long localID = 0; // Used for all temp ids (jaxb)
	public static final MyClipBoard CLIPBOARD = new MyClipBoard();

	public static final DateTimeFormatter DATE_FORMATER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

	enum Type {
		WINE,
		BOOK,
		DISC
	}

	public static void start() throws UnableToOpenFileException {
		Debug("===================================================");
		Debug("Starting MyCellar version: " + VERSION + " Internal: " + INTERNAL_VERSION);
		// Initialisation du repertoire de travail
		getWorkDir(false);
		loadGlobalProperties();
		LanguageFileLoader.getInstance().loadLanguageFiles(LanguageFileLoader.Language.ENGLISH);
	}

	static void initConf() {
		try {
			Debug("Program: Initializing Configuration files...");
			loadProperties();
			LanguageFileLoader.getInstance().loadLanguageFiles(LanguageFileLoader.Language.ENGLISH);

			if (!hasConfigGlobalKey(MyCellarSettings.LANGUAGE) || getGlobalConfigString(MyCellarSettings.LANGUAGE, "").isEmpty()) {
				putGlobalConfigString(MyCellarSettings.LANGUAGE, "" + LanguageFileLoader.Language.FRENCH.getLanguage());
			}
			cleanAndUpgrade();
		} catch (UnableToOpenFileException | RuntimeException e) {
			showException(e);
		}
	}

	static void setProgramType(Type value) {
		programType = value;
	}

	private static String getLabelForType(boolean plural, boolean firstLetterUppercase, Grammar grammar) {
		return getLabelForType(programType, plural, firstLetterUppercase, grammar);
	}

	public static String getLabelForType(Type theType, boolean plural, boolean firstLetterUppercase, Grammar grammar) {
		String value;
		String prefix;
		String postfix = plural ? "s" : "";
		switch (grammar) {
			case SINGLE:
				prefix = plural ? "more" : "one";
				break;
			case THE:
				prefix = "the";
				break;
			case OF_THE:
				prefix = "ofthe";
				break;
			case NONE:
			default:
				prefix = "";
				break;
		}
		switch (theType) {
			case WINE:
				value = getLabel("Program." + prefix + "wine" + postfix);
				break;
			case BOOK:
				value = getLabel("Program." + prefix + "book" + postfix);
				break;
			case DISC:
				value = getLabel("Program." + prefix + "disc" + postfix);
				break;
			default:
				value = getLabel("Program." + prefix + "wine" + postfix);
		}
		if (firstLetterUppercase) {
			value = StringUtils.capitalize(value);
		}
		return value;
	}

	static void setNewFile(String file) {
		myCellarFile = new MyCellarFile(new File(file));
	}

	public static boolean hasFile() {
		return myCellarFile != null;
	}

	public static String getConfigFilePath() {
		return getWorkDir(true) + CONFIG_INI;
	}
	private static String getGlobalConfigFilePath() {
		return getGlobalDir() + CONFIG_INI;
	}

	private static void loadProperties() throws UnableToOpenFileException {
		try {
			String inputPropCave = getConfigFilePath();
			File f = new File(inputPropCave);
			if(!f.exists()) {
				if (!f.createNewFile()) {
					Debug("Program: ERROR: Unable to create file " + f.getAbsolutePath());
					throw new UnableToOpenFileException("Unable to create file " + f.getAbsolutePath());
				}
			} else {
				FileInputStream inputStream = new FileInputStream(inputPropCave);
				Properties properties = new Properties();
				properties.load(inputStream);
				inputStream.close();
				properties.forEach((key, value) -> putCaveConfigString(key.toString(), value.toString()));
				if (properties.isEmpty()) {
					// Initialisation de la devise pour les nouveaux fichiers
					putCaveConfigString(MyCellarSettings.DEVISE, "\u20ac");
				}
			}
		} catch (IOException e) {
			throw new UnableToOpenFileException("Load properties failed: " + e.getMessage());
		}

	}

	private static void loadGlobalProperties() throws UnableToOpenFileException {
		try {
			Debug("Program: Initializing Configuration files...");
			File fileIni = new File(getGlobalConfigFilePath());
			if(!fileIni.exists()) {
				if (!fileIni.createNewFile()) {
					Debug("Program: ERROR: Unable to create file " + fileIni.getAbsolutePath());
					throw new UnableToOpenFileException("Unable to create file " + fileIni.getAbsolutePath());
				}
			} else {
				FileInputStream inputStream = new FileInputStream(fileIni);
				Properties properties = new Properties();
				properties.load(inputStream);
				inputStream.close();
				//Initialisation de la Map contenant config
				properties.forEach((key, value) -> putGlobalConfigString(key.toString(), value.toString()));
			}
		} catch (IOException e) {
			throw new UnableToOpenFileException("Load properties failed: " + e.getMessage());
		}
	}

	/**
	 * cleanAndUpgrade
	 * 
	 * Pour nettoyer et mettre a jour le programme
	 */
	private static void cleanAndUpgrade() {
		if (hasFile()) {
			String sVersion = getCaveConfigString(MyCellarSettings.VERSION, "");
			if (sVersion.isEmpty() || sVersion.contains(".")) {
				putCaveConfigInt(MyCellarSettings.VERSION, VERSION);
			}
			final String type = getCaveConfigString(PROGRAM_TYPE, "");
			if (type.isBlank()) {
				putCaveConfigString(PROGRAM_TYPE, Program.Type.WINE.name());
			}
			File file = new File(getWorkDir(true) + "data.xml");
			if (file.exists()) {
				Debug("Deleting old file: data.xml");
				file.delete();
			}
			file = new File(getWorkDir(true) + "Options.txt");
			if (file.exists()) {
				Debug("Deleting old file: Options.txt");
				file.delete();
			}
			file = new File(getWorkDir(true) + "static_all.sinfo");
			if (file.exists()) {
				Debug("Deleting old file: static_all.sinfo");
				file.delete();
			}
			file = new File(getWorkDir(true) + "Errors.log");
			if (file.exists()) {
				Debug("Deleting old file: Errors.log");
				file.delete();
			}
			file = new File(getWorkDir(true) + "other1.ini");
			if (file.exists()) {
				Debug("Deleting old file: other1.ini");
				file.delete();
			}
			file = new File(getWorkDir(true) + "other2.ini");
			if (file.exists()) {
				Debug("Deleting old file: other2.ini");
				file.delete();
			}
			file = new File(getWorkDir(true) + "other3.ini");
			if (file.exists()) {
				Debug("Deleting old file: other3.ini");
				file.delete();
			}
		}
		CONFIG_GLOBAL.remove(MyCellarSettings.DEBUG);
		CONFIG_GLOBAL.remove(MyCellarSettings.TYPE_AUTO);

		//int version = Integer.parseInt(sVersion);
	}


	/**
	 * setLanguage
	 * @param lang Language
	 */
	static void setLanguage(LanguageFileLoader.Language lang) {
		Debug("Program: Set Language : " + lang);
		MyCellarLabelManagement.updateLabels();
		TABBED_PANE.removeAll();
		clearObjectsVariables();
		LanguageFileLoader.getInstance().loadLanguageFiles(lang);
		PANEL_INFOS.setLabels();
		Start.getInstance().updateLabels();
		Start.getInstance().updateMainPanel();
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
		e.printStackTrace();
		if (debugFile != null) {
			try {
				oDebugFile.flush();
				oDebugFile.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			sendMail(error, debugFile);
			oDebugFile = null;
		}

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

	public static String toCleanString(final Object o) {
		if (o == null) {
			return "";
		}
		String value = o.toString();
		return value == null ? "" : value.strip();
	}

	/**
	 * Chargement des donnees XML (Bouteilles et Rangement) ou des donnees serialisees en cas de pb
	 */
	static boolean loadObjects() {
		PLACES.clear();
		boolean load = MyXmlDom.readMyCellarXml("", PLACES);
		if(!load || PLACES.isEmpty()) {
			PLACES.clear();
			PLACES.add(DEFAULT_PLACE);
		}
		getStorage().loadHistory();
		getStorage().loadWorksheet();
		load = ListeBouteille.loadXML();
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
	 * getNbBouteilleAnnee: retourne le nombre de bouteilles d'une annee
	 *
	 * @param an int: annee souhaitee
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
	public static void getAide() {

		File f = new File("./Help/MyCellar.hs");
		if (f.exists()) {
			try {
				Runtime.getRuntime().exec("java -jar ./Help/hsviewer.jar -hsURL \"file:./Help/MyCellar.hs\"");
			} catch (IOException ignored) {}
		} else {
			Erreur.showSimpleErreur(getError("Error162"));
		}
	}

	static char getDecimalSeparator() {
		DecimalFormat format = (DecimalFormat) DecimalFormat.getInstance();
		DecimalFormatSymbols symbols = format.getDecimalFormatSymbols();
		return symbols.getDecimalSeparator();
	}

	/**
	 * Sauvegarde le fichier
	 */
	public static void save() {
		Debug("Program: Saving...");
		saveAs(myCellarFile.getFile());
	}

	/**
	 * saveAs
	 * @param file File
	 */
	static void saveAs(File file) {
		Debug("Program: Saving all files...");

		saveGlobalProperties();

		if(isListCaveModified()) {
			MyXmlDom.writeMyCellarXml(getCave(), "");
		}

		getStorage().saveHistory();
		getStorage().saveWorksheet();
		CountryVignobleController.save();
		CountryListJaxb.save();
		ListeBouteille.writeXML();

		myCellarFile.saveAs(file);

		modified = false;
		listCaveModified = false;
		Debug("Program: Saving all files OK");
	}

	public static void Debug(String sText) {
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
		if(oDebugFile == null) {
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
		return PLACES;
	}


	/**
	 * GetCave
	 *
	 * @param name String
	 * @return Rangement
	 */
	public static Rangement getCave(final String name) {
		if (name == null || name.strip().isEmpty()) {
			return null;
		}

		final String placeName = name.strip();
		final List<Rangement> list = PLACES.stream().filter(rangement -> rangement.getNom().equals(placeName))
				.collect(Collectors.toList());
		if (list.isEmpty()) {
			return null;
		}
		return list.get(0);
	}
	
	/**
	 * addCave
	 *
	 * @param rangement Rangement
	 */
	public static void addCave(Rangement rangement) {
		if(rangement == null) {
			return;
		}
		PLACES.add(rangement);
		setListCaveModified();
		setModified();
		Debug("Program: Sorting places...");
		Collections.sort(PLACES);
	}

	/**
	 * removeCave
	 *
	 * @param rangement Rangement
	 */
	 public static void removeCave(Rangement rangement) {
		if(rangement == null) {
			return;
		}
		PLACES.remove(rangement);
		setModified();
		setListCaveModified();
	}

	/**
	 * GetCaveLength
	 *
	 * @return int
	 */
	public static int getCaveLength() {
		return PLACES.size();
	}

	public static boolean hasComplexPlace() {
		return PLACES.stream().anyMatch(rangement -> !rangement.isCaisse());
	}

	/**
	 * newFile: Create a new file.
	 */
	static void newFile() {
		final File file = new File(getWorkDir(true) + UNTITLED1_SINFO);
		if(file.exists()) {
			file.delete();
		}
		try {
			file.createNewFile();
		} catch (IOException e) {
			showException(e);
		}
		try {
			openaFile(file, true);
		} catch (UnableToOpenFileException e) {
			showException(e);
		}
	}

	/**
	 * openaFile: Ouvre un fichier
	 *
	 * @param file File
	 */
	static void openaFile(File file) throws UnableToOpenFileException {
		openaFile(file, false);
	}

	private static void openaFile(File file, boolean isNewFile) throws UnableToOpenFileException {
		LinkedList<String> list = new LinkedList<>();
		list.addLast(getGlobalConfigString(MyCellarSettings.LAST_OPEN1,""));
		list.addLast(getGlobalConfigString(MyCellarSettings.LAST_OPEN2,""));
		list.addLast(getGlobalConfigString(MyCellarSettings.LAST_OPEN3,""));
		list.addLast(getGlobalConfigString(MyCellarSettings.LAST_OPEN4,""));
		if (isNewFile) {
			Debug("Program: openFile: Creating new file");
		} else {
			Debug("Program: openFile: Opening file: " + file.getAbsolutePath());
			list.remove(file.getAbsolutePath());
		}

		// Sauvegarde avant de charger le nouveau fichier
		closeFile();
		
		CountryVignobleController.init();

		if (isNewFile) {
			// Nouveau fichier de bouteilles
			ListeBouteille.writeXML();
		}

		if (!file.exists()) {
			Erreur.showSimpleErreur(MessageFormat.format(getError("Error020"), file.getAbsolutePath())); //Fichier non trouve);

			putGlobalConfigString(MyCellarSettings.LAST_OPEN1, list.pop());
			putGlobalConfigString(MyCellarSettings.LAST_OPEN2, list.pop());
			putGlobalConfigString(MyCellarSettings.LAST_OPEN3, list.pop());
			// On a deja enleve un element de la liste
			putGlobalConfigString(MyCellarSettings.LAST_OPEN4, "");
			saveGlobalProperties();
			throw new UnableToOpenFileException("File not found: " + file.getAbsolutePath());
		}

		myCellarFile = new MyCellarFile(file);
		myCellarFile.unzip();

		CountryListJaxb.init();

		//Chargement des objets Rangement, Bouteilles et History
		Debug("Program: Reading Places, Bottles & History");
		if (!loadObjects()) {
			Debug("Program: ERROR Reading Objects KO");
			throw new UnableToOpenFileException("Error while reading objects.");
		}

		Debug("Program: Checking place count");
		long i = getCave().stream().filter(Objects::nonNull).count();
		if (i != getCaveLength()) {
			Debug("Program: Place Count: Program=" + getCaveLength() + " cave=" + i);
			throw new UnableToOpenFileException("Place Count: Program=" + getCaveLength() + " cave=" + i);
		}

		loadProperties();

		MyCellarBottleContenance.load();

		RangementUtils.putTabStock();
		if(!getErrors().isEmpty()) {
			new OpenShowErrorsAction().actionPerformed(null);
		}
		CountryVignobleController.load();

		if(myCellarFile.isFileSavable()) {
			list.addFirst(file.getAbsolutePath());
		}

		putGlobalConfigString(MyCellarSettings.LAST_OPEN1, list.pop());
		putGlobalConfigString(MyCellarSettings.LAST_OPEN2, list.pop());
		putGlobalConfigString(MyCellarSettings.LAST_OPEN3, list.pop());
		putGlobalConfigString(MyCellarSettings.LAST_OPEN4, list.pop());
		setProgramType(Program.Type.valueOf(getCaveConfigString(PROGRAM_TYPE, getGlobalConfigString(PROGRAM_TYPE, Program.Type.WINE.name()))));

		putCaveConfigString(MyCellarSettings.DIR, file.getParent());

		saveGlobalProperties();
		modified = false;
		listCaveModified = false;
	}

	/**
	 * closeFile: Fermeture du fichier.
	 */
	static void closeFile() {
		if (myCellarFile == null) {
			Debug("Program: closeFile: File already closed!");
			return;
		}
		Debug("Program: closeFile: Closing file...");
		boolean bSave = false;
		File newFile = null;
		if (myCellarFile.exists() && isModified()) {
			if (JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(null, getError("Error199"), getLabel("Infos049"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE)) {
				bSave = true;
				if (!myCellarFile.isFileSavable()) {
					JFileChooser boiteFichier = new JFileChooser();
					boiteFichier.removeChoosableFileFilter(boiteFichier.getFileFilter());
					boiteFichier.addChoosableFileFilter(Filtre.FILTRE_SINFO);
					if (boiteFichier.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
						File nomFichier = boiteFichier.getSelectedFile();
						newFile = new File(MyCellarControl.controlAndUpdateExtension(nomFichier.getAbsolutePath(), Filtre.FILTRE_SINFO));
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

		//Tri du tableau et ecriture du fichier XML
		if (bSave) {
			if(!ListeBouteille.writeXML()) {
				return;
			}

			if(isListCaveModified()) {
				MyXmlDom.writeMyCellarXml(getCave(), "");
			}

			if (!getCave().isEmpty()) {
				getStorage().saveHistory();
				getStorage().saveWorksheet();
				CountryVignobleController.save();
				if (newFile != null) {
					myCellarFile.saveAs(newFile);
				} else {
					myCellarFile.save();
				}
			}
		}

		if (myCellarFile.exists()) {
			// Sauvegarde des proprietes globales
			saveGlobalProperties();

			if (getCaveConfigBool(MyCellarSettings.FIC_EXCEL, false)) {
				//Ecriture Excel
				final String file_excel = getCaveConfigString(MyCellarSettings.FILE_EXCEL, "");
				Debug("Program: Writing backup Excel file: " + file_excel);
				final List<Bouteille> bouteilles = Collections.unmodifiableList(getStorage().getAllList());
				Thread writingExcel = new Thread(() -> RangementUtils.write_XLS(new File(file_excel), bouteilles, true, null));
				Runtime.getRuntime().addShutdownHook(writingExcel);
			}
		}

		TABBED_PANE.removeAll();
		if (myCellarFile.exists()) {
			getStorage().close();
			CountryVignobleController.close();
			CountryListJaxb.close();
			getSearch().ifPresent(Search::clearResults);
		}
		clearObjectsVariables();
		m_bWorkDirCalculated = false;
		TRASH.clear();
		modified = false;
		listCaveModified = false;
		if (getCave() != null) {
			getCave().clear();
		}
		DEFAULT_PLACE.resetStock();
		EMPTY_PLACE.resetStock();
		myCellarFile = null;
		Debug("Program: closeFile: Closing file Ended");
	}

	private static void clearObjectsVariables() {
		UPDATABLE_OBJECTS.clear();
		UPDATABLE_BOTTLES.clear();
		OPENED_OBJECTS.clear();
	}

	private static void deleteTempFiles() {
		for (File f : DIR_TO_DELETE) {
			if (!f.exists() || f.getName().equalsIgnoreCase("Global")) {
				continue;
			}
			try {
				Debug("Program: closeFile: Deleting work directory: " + f.getAbsolutePath());
				FileUtils.deleteDirectory(f);
			} catch(Exception e) {
				Debug("Program: Error deleting " + f.getAbsolutePath());
				Debug("Program: " + e.getMessage());
			}
		}
	}

	/**
	 * Save global properties
	 */
	static void saveGlobalProperties() {
		Debug("Program: Saving Global Properties");
		saveProperties(CONFIG_GLOBAL, getGlobalConfigFilePath());
		Debug("Program: Saving Global Properties OK");
	}

	public static void saveProperties(final MyLinkedHashMap map, final String file) {
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
	 * getGlobalDir: Retourne le nom du repertoire des proprietes globales.
	 * @return
	 */
	private static String getGlobalDir() {
		if (m_bGlobalDirCalculated) {
			return m_sGlobalDir + File.separator;
		}
		m_bGlobalDirCalculated = true;
		String sDir = System.getProperty("user.home");
		if (sDir.isEmpty()) {
			m_sGlobalDir = "./Object/Global";
		} else {
			m_sGlobalDir = sDir + "/MyCellar/Global";
		}
		File f_obj = new File(m_sGlobalDir);
		if (!f_obj.exists()) {
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
		if (m_bWorkDirCalculated) {
			if (_bWithEndSlash) {
				return m_sWorkDir + File.separator;
			}
			return m_sWorkDir;
		}
		m_bWorkDirCalculated = true;
		Debug("Program: Calculating work directory.");
		String sDir = System.getProperty("user.home");
		if (sDir.isEmpty()) {
			m_sWorkDir = "." + File.separator + "Object";
		} else {
			m_sWorkDir = sDir + File.separator + "MyCellar";
		}
		File f_obj = new File(m_sWorkDir);
		if (!f_obj.exists()) {
			f_obj.mkdir();
		}

		String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));

		m_sWorkDir += File.separator + time;

		f_obj = new File(m_sWorkDir);
		if (!f_obj.exists()) {
			f_obj.mkdir();
		}

		Debug("Program: work directory: " + m_sWorkDir);
		DIR_TO_DELETE.add(new File(m_sWorkDir));

		if (_bWithEndSlash) {
			return m_sWorkDir + File.separator;
		}
		return m_sWorkDir;
	}

	static String getShortFilename() {
		if (myCellarFile == null) {
			return getShortFilename(UNTITLED1_SINFO);
		}
		return getShortFilename(myCellarFile.getFile().getAbsolutePath());
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

	static String getGlobalConfigString(String key, String defaultValue) {
		return CONFIG_GLOBAL.getString(key, defaultValue);
	}

	public static String getCaveConfigString(String key, String defaultValue) {
		if (null != getCaveConfig()) {
			return getCaveConfig().getString(key, defaultValue);
		}
		Debug("Program: ERROR: Calling null configCave for key '" + key + "' and default value '" + defaultValue + "'");
		return defaultValue;
	}

	static boolean getGlobalConfigBool(String key, boolean defaultValue) {
		return 1 == CONFIG_GLOBAL.getInt(key, defaultValue ? 1 : 0);
	}

	public static boolean getCaveConfigBool(String key, boolean defaultValue) {
		if (null != getCaveConfig()) {
			final String value = getCaveConfig().getString(key, defaultValue ? "1" : "0");
			return ("1".equals(value) || "ON".equalsIgnoreCase(value));
		}
		Debug("Program: ERROR: Calling null configCave for key '" + key + "' and default value '" + defaultValue + "'");
		return defaultValue;
	}

	public static int getCaveConfigInt(String key, int defaultValue) {
		if (null != getCaveConfig()) {
			return getCaveConfig().getInt(key, defaultValue);
		}
		Debug("Program: ERROR: Calling null configCave for key '" + key + "' and default value '" + defaultValue + "'");
		return defaultValue;
	}

	static void putGlobalConfigString(String key, String value) {
		CONFIG_GLOBAL.put(key, value);
	}

	public static void putCaveConfigString(String key, String value) {
		if (null != getCaveConfig()) {
			getCaveConfig().put(key, value);
		} else {
			Debug("Program: ERROR: Unable to put value in configCave: [" + key + " - " + value + "]");
		}
	}

	static void putGlobalConfigBool(String key, boolean value) {
		CONFIG_GLOBAL.put(key, value ? "1" : "0");
	}

	public static void putCaveConfigBool(String key, boolean value) {
		if (null != getCaveConfig()) {
			getCaveConfig().put(key, value ? "1" : "0");
		} else {
			Debug("Program: ERROR: Unable to put value in configCave: [" + key + " - " + value + "]");
		}
	}

	public static void putCaveConfigInt(String key, int value) {
		Objects.requireNonNull(getCaveConfig()).put(key, value);
	}

	static MyLinkedHashMap getCaveConfig() {
		return myCellarFile == null ? null : myCellarFile.getCaveConfig();
	}

	static boolean hasConfigCaveKey(String key) {
		return null != getCaveConfig() && getCaveConfig().containsKey(key);
	}

	static boolean hasConfigGlobalKey(String key) {
		return CONFIG_GLOBAL.containsKey(key);
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

	public static String getPreviewXMLFileName() {
		return getGlobalDir() + PREVIEW_XML;
	}

	private static String getPreviewHTMLFileName() {
		return getGlobalDir() + PREVIEW_HTML;
	}

	public static Storage getStorage() {
		return SerializedStorage.getInstance();
	}

	public static String getLabel(String id) {
		return getLabel(id, true);
	}

	public static String getLabel(String id, LabelProperty labelProperty) {
		if (labelProperty == null) {
			return getLabel(id, true);
		}
		String label = getLabel(id, true);
		label = label.replaceAll(KEY_TYPE, getLabelForType(labelProperty.isPlural(), labelProperty.isUppercaseFirst(), labelProperty.getGrammar()));
		if (labelProperty.isDoubleQuote()) {
			label += LanguageFileLoader.isFrench() ? " :" : ":";
		}
		return label;
	}

	public static String getLabel(String id, boolean displayError) {
		try {
			return LanguageFileLoader.getLabel(id);
		} catch(MissingResourceException e) {
			if (displayError) {
			  Debug("Program: ERROR: Missing Label " + id);
				JOptionPane.showMessageDialog(null, "Missing Label " + id, "Error", JOptionPane.ERROR_MESSAGE);
			}
			return id;
		}
	}

	public static String getError(String id, LabelProperty labelProperty) {
		if (labelProperty == null) {
			return getError(id);
		}
		String label = getError(id);
		return label.replaceAll(KEY_TYPE, getLabelForType(labelProperty.isPlural(), labelProperty.isUppercaseFirst(), labelProperty.getGrammar()));
	}

	public static String getError(String id) {
		try {
			return LanguageFileLoader.getError(id);
		} catch(MissingResourceException e) {
			JOptionPane.showMessageDialog(null, "Missing Error " + id, "Error", JOptionPane.ERROR_MESSAGE);
			return id;
		}
	}

	static List<String> getLanguages() {
		return LanguageFileLoader.getLanguages();
	}

	static int getLanguageIndex(String language) {
		return LanguageFileLoader.getLanguageIndex(language);
	}

	static String getLanguage(int val) {
		return LanguageFileLoader.getLanguageFromIndex(val);
	}

	public static void open(File file) {
		if (file != null) {
			try {
				if (System.getProperty("os.name").startsWith("Mac")) {
					Runtime.getRuntime().exec("/usr/bin/open " + file.getAbsolutePath());
				} else {
					Desktop.getDesktop().browse(file.toURI());
				}
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

	public static void updateAllPanels() {
		UPDATABLE_OBJECTS.forEach((screenType, iUpdatable) -> iUpdatable.setUpdateView());
		UPDATABLE_BOTTLES.forEach((s, iUpdatable) -> iUpdatable.setUpdateView());
	}

	static void updateManagePlacePanel() {
		final IUpdatable managePlace = UPDATABLE_OBJECTS.get(CELL_ORGANIZER);
		if (managePlace != null) {
			managePlace.setUpdateView();
		}
	}

	public static List<CountryJaxb> getCountries() {
		return CountryListJaxb.getInstance().getCountries();
	}

	public static int findTab(ImageIcon image) {
		for(int i = 0; i < TABBED_PANE.getTabCount(); i++) {
			try {
				if (TABBED_PANE.getTabComponentAt(i) != null && TABBED_PANE.getIconAt(i) != null && TABBED_PANE.getIconAt(i).equals(image)) {
					return i;
				}
			} catch(RuntimeException ignored) {}
		}
		return -1;
	}

	public static void setModified() {
		modified = true;
	}

	private static boolean isModified() {
		return modified;
	}

	public static void setListCaveModified() {
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
		for (int i=0; i<nbCol; i++) {
			int export = getCaveConfigInt(MyCellarSettings.SIZE_COL + i + "EXPORT", 0);
			if (export == 1) {
				countColumn++;
				int sizeCol = getCaveConfigInt(MyCellarSettings.SIZE_COL + i, 5);
				properties.addColumn(MyCellarFields.getFieldsList().get(i), i, sizeCol, MyCellarFields.getFieldsList().get(i).toString());
			}
		}
		if (countColumn == 0) {
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
		for (Bouteille b : list) {
			row = new PDFRow();
			for (PDFColumn column : columns) {
				if (column.getField().equals(MyCellarFields.NAME)) {
					row.addCell(b.getNom());
				}	else if (column.getField().equals(MyCellarFields.YEAR)) {
					row.addCell(b.getAnnee());
				} else if (column.getField().equals(MyCellarFields.TYPE)) {
					row.addCell(b.getType());
				} else if (column.getField().equals(MyCellarFields.PLACE)) {
					row.addCell(b.getEmplacement());
				} else if (column.getField().equals(MyCellarFields.NUM_PLACE)) {
					row.addCell(Integer.toString(b.getNumLieu()));
				} else if (column.getField().equals(MyCellarFields.LINE)) {
					row.addCell(Integer.toString(b.getLigne()));
				} else if (column.getField().equals(MyCellarFields.COLUMN)) {
					row.addCell(Integer.toString(b.getColonne()));
				} else if (column.getField().equals(MyCellarFields.PRICE)) {
					row.addCell(b.getPrix());
				} else if (column.getField().equals(MyCellarFields.COMMENT)) {
					row.addCell(b.getComment());
				} else if (column.getField().equals(MyCellarFields.MATURITY)) {
					row.addCell(b.getMaturity());
				} else if (column.getField().equals(MyCellarFields.PARKER)) {
					row.addCell(b.getParker());
				} else if (column.getField().equals(MyCellarFields.COUNTRY)) {
					if (b.getVignoble() != null) {
						row.addCell(b.getVignoble().getCountry());
					} else {
						row.addCell("");
					}
				}	else if (column.getField().equals(MyCellarFields.VINEYARD)) {
					if (b.getVignoble() != null) {
						row.addCell(b.getVignoble().getName());
					} else {
						row.addCell("");
					}
				}	else if (column.getField().equals(MyCellarFields.AOC)) {
					if (b.getVignoble() != null) {
						row.addCell(b.getVignoble().getAOC());
					} else {
						row.addCell("");
					}
				}	else if (column.getField().equals(MyCellarFields.IGP)) {
					if (b.getVignoble() != null) {
						row.addCell(b.getVignoble().getIGP());
					} else {
						row.addCell("");
					}
				} else if (column.getField().equals(MyCellarFields.COLOR)) {
					row.addCell(BottleColor.getColor(b.getColor()).toString());
				}	else if (column.getField().equals(MyCellarFields.STATUS)) {
					row.addCell(BottlesStatus.getStatus(b.getStatus()).toString());
				}
			}
			rows.add(row);
		}
		return rows;
	}

	static PDFRow getPDFHeader(PDFProperties properties) {
		LinkedList<PDFColumn> columns = properties.getColumns();
		PDFRow row = new PDFRow();
		for (PDFColumn column : columns) {
			row.addCell(column.getTitle());
		}
		return row;
	}

	private static void cleanDebugFiles() {
		String sDir = System.getProperty("user.home") + File.separator + "MyCellarDebug";
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
	
	private static void cleanTempDirs() {
	  String sDir = System.getProperty("user.home") + File.separator + "MyCellar";

    File file = new File(sDir);
    if (!file.exists()) {
      return;
    }

    long time = Long.parseLong(LocalDateTime.now().minusMonths(2).format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));

		final String[] list = file.list();
		if (list != null) {
			List<Long> oldTime = Arrays.stream(list)
					.filter(StringUtils::isNumeric)
					.map(Long::parseLong)
					.filter(value -> value < time).collect(Collectors.toList());

			oldTime.forEach(value -> DIR_TO_DELETE.add(new File(file + File.separator + value)));
		}
	}

	public static void saveShowColumns(String value) {
		putCaveConfigString(MyCellarSettings.SHOWFILE_COLUMN, value);
	}

	public static String getShowColumns() {
		return getCaveConfigString(MyCellarSettings.SHOWFILE_COLUMN, "");
	}

	public static void saveShowColumnsWork(String value) {
		putCaveConfigString(MyCellarSettings.SHOWFILE_COLUMN_WORK, value);
	}

	public static String getShowColumnsWork() {
		return getCaveConfigString(MyCellarSettings.SHOWFILE_COLUMN_WORK, "");
	}

	static void saveHTMLColumns(List<MyCellarFields> cols) {
		StringBuilder s = new StringBuilder();
		for (MyCellarFields f : cols) {
			if (s.length() != 0) {
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
		for (String field : fields) {
			for (MyCellarFields f : MyCellarFields.getFieldsList()) {
				if(f.name().equals(field)) {
					cols.add(f);
					break;
				}
			}
		}
		return cols;
	}

	public static AddVin getAddVin() {
		return (AddVin) OPENED_OBJECTS.get(ADDVIN);
	}

	public static AddVin createAddVin() {
		AddVin addVin = (AddVin) OPENED_OBJECTS.get(ADDVIN);
		if (addVin == null) {
			addVin = new AddVin();
			OPENED_OBJECTS.put(ADDVIN, addVin);
			UPDATABLE_OBJECTS.put(ADDVIN, addVin);
		}
		return addVin;
	}

	static Supprimer_Rangement getSupprimerRangement() {
		return (Supprimer_Rangement) OPENED_OBJECTS.get(SUPPRIMER_RANGEMENT);
	}

	static Supprimer_Rangement createSupprimerRangement() {
		final Supprimer_Rangement supprimerRangement = (Supprimer_Rangement) createOpenedObject(Supprimer_Rangement.class, SUPPRIMER_RANGEMENT);
		UPDATABLE_OBJECTS.put(SUPPRIMER_RANGEMENT, supprimerRangement);
		return supprimerRangement;
	}

	public static void deleteSupprimerRangement() {
		OPENED_OBJECTS.remove(SUPPRIMER_RANGEMENT);
		UPDATABLE_OBJECTS.remove(SUPPRIMER_RANGEMENT);
	}

	static Creer_Rangement getCreerRangement() {
		return (Creer_Rangement) OPENED_OBJECTS.get(CREATE_PLACE);
	}

	static Creer_Rangement createCreerRangement() {
		Creer_Rangement creerRangement = (Creer_Rangement) OPENED_OBJECTS.get(CREATE_PLACE);
		if (creerRangement == null) {
			creerRangement = new Creer_Rangement(false);
			OPENED_OBJECTS.put(CREATE_PLACE, creerRangement);
		}
		return creerRangement;
	}

	static Creer_Rangement getModifierRangement() {
		return (Creer_Rangement) OPENED_OBJECTS.get(MODIFY_PLACE);
	}

	static Creer_Rangement createModifierRangement() {
		Creer_Rangement creerRangement = (Creer_Rangement) OPENED_OBJECTS.get(MODIFY_PLACE);
		if (creerRangement == null) {
			creerRangement = new Creer_Rangement(true);
			OPENED_OBJECTS.put(MODIFY_PLACE, creerRangement);
		}
		return creerRangement;
	}

	static Optional<Search> getSearch() {
		return Optional.ofNullable((Search) OPENED_OBJECTS.get(SEARCH));
	}

	static Search createSearch() {
		final Search search = (Search) createOpenedObject(Search.class, SEARCH);
		UPDATABLE_OBJECTS.put(SEARCH, search);
		return search;
	}

	static Creer_Tableaux getCreerTableaux() {
		return (Creer_Tableaux) OPENED_OBJECTS.get(CREER_TABLEAU);
	}

	static Creer_Tableaux createCreerTableaux() {
		final Creer_Tableaux creerTableaux = (Creer_Tableaux) createOpenedObject(Creer_Tableaux.class, CREER_TABLEAU);
		UPDATABLE_OBJECTS.put(CREER_TABLEAU, creerTableaux);
		return creerTableaux;
	}

	static Importer getImporter() {
		return (Importer) OPENED_OBJECTS.get(IMPORTER);
	}

	static Importer createImporter() {
		final Importer importer = (Importer) createOpenedObject(Importer.class, IMPORTER);
		return importer;
	}

	static Export getExport() {
		return (Export) OPENED_OBJECTS.get(EXPORT);
	}

	static Export createExport() {
		final Export export = (Export) createOpenedObject(Export.class, EXPORT);
		return export;
	}

	static Stat getStat() {
		return (Stat) OPENED_OBJECTS.get(STATS);
	}

	static Stat createStat() {
		final Stat stat = (Stat) createOpenedObject(Stat.class, STATS);
		UPDATABLE_OBJECTS.put(STATS, stat);
		return stat;
	}

	static ShowHistory getShowHistory() {
		return (ShowHistory) OPENED_OBJECTS.get(HISTORY);
	}

	static ShowHistory createShowHistory() {
		final ShowHistory showHistory = (ShowHistory) createOpenedObject(ShowHistory.class, HISTORY);
		return showHistory;
	}

	static VineyardPanel getVineyardPanel() {
		return (VineyardPanel) OPENED_OBJECTS.get(VIGNOBLES);
	}

	public static CapacityPanel getCapacityPanel() {
		return (CapacityPanel) OPENED_OBJECTS.get(CAPACITY);
	}

	static VineyardPanel createVineyardPanel() {
		final VineyardPanel vineyardPanel = (VineyardPanel) createOpenedObject(VineyardPanel.class, VIGNOBLES);
		return vineyardPanel;
	}

	static CapacityPanel createCapacityPanel() {
		final CapacityPanel capacityPanel = (CapacityPanel) createOpenedObject(CapacityPanel.class, CAPACITY);
		return capacityPanel;
	}

	static ShowFile getShowFile() {
		return (ShowFile) OPENED_OBJECTS.get(SHOW_FILE);
	}

	static ShowFile createShowFile() {
		final ShowFile showFile = (ShowFile) createOpenedObject(ShowFile.class, SHOW_FILE);
		UPDATABLE_OBJECTS.put(SHOW_FILE, showFile);
		return showFile;
	}

	static ShowFile getShowTrash() {
		return (ShowFile) OPENED_OBJECTS.get(SHOW_TRASH);
	}

	static ShowFile createShowTrash() {
		ShowFile showFile = (ShowFile) OPENED_OBJECTS.get(SHOW_TRASH);
		if (showFile == null) {
			showFile = new ShowFile(ShowFile.ShowType.TRASH);
			OPENED_OBJECTS.put(SHOW_TRASH, showFile);
			UPDATABLE_OBJECTS.put(SHOW_TRASH, showFile);
		}
		return showFile;
	}

	public static ShowFile getShowWorksheet() {
		return (ShowFile) OPENED_OBJECTS.get(SHOW_WORKSHEET);
	}

	public static ShowFile createShowWorksheet() {
		ShowFile showFile = (ShowFile) OPENED_OBJECTS.get(SHOW_WORKSHEET);
		if (showFile == null) {
			showFile = new ShowFile(ShowFile.ShowType.WORK);
			OPENED_OBJECTS.put(SHOW_WORKSHEET, showFile);
			UPDATABLE_OBJECTS.put(SHOW_WORKSHEET, showFile);
		}
		return showFile;
	}

	public static ShowFile getShowErrors() {
		return (ShowFile) OPENED_OBJECTS.get(SHOW_ERRORS);
	}

	public static ShowFile createShowErrors() {
		ShowFile showFile = (ShowFile) OPENED_OBJECTS.get(SHOW_ERRORS);
		if (showFile == null) {
			showFile = new ShowFile(ShowFile.ShowType.ERROR);
			OPENED_OBJECTS.put(SHOW_ERRORS, showFile);
			UPDATABLE_OBJECTS.put(SHOW_ERRORS, showFile);
		}
		return showFile;
	}

	static CellarOrganizerPanel getCellarOrganizerPanel() {
		return (CellarOrganizerPanel) OPENED_OBJECTS.get(CELL_ORGANIZER);
	}

	static CellarOrganizerPanel createCellarOrganizerPanel() {
		final CellarOrganizerPanel cellarOrganizerPanel = (CellarOrganizerPanel) createOpenedObject(CellarOrganizerPanel.class, CELL_ORGANIZER);
		UPDATABLE_OBJECTS.put(CELL_ORGANIZER, cellarOrganizerPanel);
		return cellarOrganizerPanel;
	}

	static Parametres getParametres() {
		return (Parametres) OPENED_OBJECTS.get(PARAMETRES);
	}

	static Parametres createParametres() {
		final Parametres parametres = (Parametres) createOpenedObject(Parametres.class, PARAMETRES);
		return parametres;
	}

	static void deleteParametres() {
		OPENED_OBJECTS.remove(PARAMETRES);
	}

	static CellarOrganizerPanel getCellChoosePanel() {
		return (CellarOrganizerPanel) OPENED_OBJECTS.get(CHOOSE_CELL);
	}

	static CellarOrganizerPanel createChooseCellPanel(IAddVin addVin) {
		CellarOrganizerPanel cellarOrganizerPanel = (CellarOrganizerPanel) OPENED_OBJECTS.get(CHOOSE_CELL);
		if (cellarOrganizerPanel == null) {
			cellarOrganizerPanel = new CellarOrganizerPanel(addVin);
			OPENED_OBJECTS.put(CHOOSE_CELL, cellarOrganizerPanel);
			UPDATABLE_OBJECTS.put(CHOOSE_CELL, cellarOrganizerPanel);
		}
		return cellarOrganizerPanel;
	}

	static void deleteChooseCellPanel() {
		OPENED_OBJECTS.remove(CHOOSE_CELL);
		UPDATABLE_OBJECTS.remove(CHOOSE_CELL);
	}

	private static IMyCellar createOpenedObject(Class<?> className, ScreenType id) {
		IMyCellar object = OPENED_OBJECTS.get(id);
		if (object == null) {
			try {
				Constructor<?> ctor = className.getConstructor();
				object = (IMyCellar) ctor.newInstance();
			} catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
				showException(e);
			}
			OPENED_OBJECTS.put(id, object);
		}
		return object;
	}

	static void updateSelectedTab() {
		UPDATABLE_OBJECTS.forEach((s, iUpdatable) -> {
			if (iUpdatable.equals(TABBED_PANE.getSelectedComponent())) {
				iUpdatable.updateView();
			}
		});
		UPDATABLE_BOTTLES.forEach((s, iUpdatable) -> {
			if (iUpdatable.equals(TABBED_PANE.getSelectedComponent())) {
				iUpdatable.updateView();
			}
		});
	}

  static boolean isCutCopyPastTab() {
    return TABBED_PANE.getSelectedComponent() != null && TABBED_PANE.getSelectedComponent() instanceof ICutCopyPastable;
  }

  static <T> T getSelectedComponent(Class<T> className) {
	  return className.cast(TABBED_PANE.getSelectedComponent());
  }

	public static String readFirstLineText(final File f) {
		if (f == null || !f.exists()) {
			return "";
		}
		if (!f.getName().toLowerCase().endsWith(".txt")) {
			return "";
		}
		Debug("Program: Reading first line of file " + f.getName());
		try (var scanner = new Scanner(f)){
			if(scanner.hasNextLine()) {
				return scanner.nextLine().strip();
			}
		} catch (FileNotFoundException e) {
			showException(e, true);
		}
		return "";
	}
	
	static HistoryList getHistoryList() {
		return getStorage().getHistoryList();
	}

	public static WorkSheetList getWorksheetList() {
		return getStorage().getWorksheetList();
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
		return new DecimalFormat("###0.00", dfs);
	  }
	  
//	public static String bigDecimalToString(final BigDecimal value, final Locale locale) {
//    return getDecimalFormat(locale).format(value);
//  }

	public static BigDecimal safeStringToBigDecimal(final String value, BigDecimal defaultValue) {
		try {
			return stringToBigDecimal(value);
		} catch (NumberFormatException e) {
			return defaultValue;
		}
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
			nextID = getStorage().getBottlesCount();
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
			showBottle(listToModify.getFirst(), true);
		} else {
			new OpenAddVinAction(listToModify).actionPerformed(null);
		}
	}
	
	public static void showBottle(Bouteille bottle, boolean edit) {
		for (int i = 0; i < TABBED_PANE.getTabCount(); i++) {
			Component tab = TABBED_PANE.getComponentAt(i);
			if (tab instanceof ManageBottle && ((ManageBottle) tab).getBottle().equals(bottle)) {
				TABBED_PANE.setSelectedIndex(i);
				return;
			}
		}
		ManageBottle manage = new ManageBottle(bottle);
		manage.enableAll(edit);
		UPDATABLE_BOTTLES.put(bottle.getId(), manage);
		String bottleName = bottle.getNom();
		if (bottleName.length() > 30) {
			bottleName = bottleName.substring(0, 30) + " ...";
		}
		TABBED_PANE.addTab(bottleName, MyCellarImage.WINE, manage);
		TABBED_PANE.setSelectedIndex(TABBED_PANE.getTabCount() - 1);
		Utils.addCloseButton(TABBED_PANE, manage);
		Start.getInstance().updateMainPanel();
	}

	static void removeBottleTab(Bouteille bottle) {
		for (int i = 0; i < TABBED_PANE.getTabCount(); i++) {
			Component tab = TABBED_PANE.getComponentAt(i);
			if (tab instanceof ManageBottle && ((ManageBottle) tab).getBottle().equals(bottle)) {
				TABBED_PANE.removeTabAt(i);
				return;
			}
		}
	}

	public static List<Bouteille> getExistingBottles(List<Integer> bouteilles) {
		return getStorage().getAllList().stream().filter(bouteille -> bouteilles.contains(bouteille.getId())).collect(Collectors.toList());
	}

	public static boolean isExistingBottle(Bouteille bouteille) {
		return getStorage().getAllList().stream().anyMatch(bouteille1 -> bouteille1.getId() == bouteille.getId());
	}

	public static int safeParseInt(String value, int defaultValue) {
		try {
			return Integer.parseInt(value);
		} catch (NumberFormatException ignored) {
			return defaultValue;
		}
	}

	public static long generateID() {
		return localID++;
	}

	static void exit() {
	  cleanTempDirs();
		deleteTempFiles();
		cleanDebugFiles();
		Debug("Program: MyCellar End");
		closeDebug();
	}

	static boolean isFileSavable() {
		return myCellarFile != null && myCellarFile.isFileSavable();
	}
}
