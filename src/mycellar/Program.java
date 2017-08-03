package mycellar;

import java.awt.Desktop;
import java.awt.Font;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import mycellar.actions.OpenShowErrorsAction;
import mycellar.core.MyCellarError;
import mycellar.core.MyCellarFields;
import mycellar.countries.Countries;
import mycellar.countries.Country;
import mycellar.launcher.Server;
import mycellar.pdf.PDFColumn;
import mycellar.pdf.PDFProperties;
import mycellar.pdf.PDFRow;
import mycellar.showfile.ShowFile;
import mycellar.vignobles.CountryVignobles;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.net.util.Base64;

import java.text.MessageFormat;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;
import java.util.MissingResourceException;
import java.util.OptionalDouble;
import java.util.Properties;
import java.util.zip.Adler32;
import java.util.zip.CheckedOutputStream;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

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

/**
 * <p>Titre : Cave à vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2003</p>
 * <p>Société : Seb Informatique</p>
 * @author Sébastien Duché
 * @version 16.0
 * @since 03/08/17
 */

public class Program {

	private static Properties propCave = new Properties();
	private static Properties propGlobal = new Properties();
	private static String inputPropCave = null;
	private static String inputPropGlobal = null;
	public static Font font_panel = new Font("Arial", 0, 12);
	public static Font font_boutton_small = new Font("Arial", 0, 10);
	public static Font font_label_bold = new Font("Arial", 1, 12);
	public static Font font_dialog = new Font("Dialog", 1, 16);
	public static Font font_dialog_small = new Font("Dialog", 1, 12);
	public static Options options = null;
	public static Export export = null;
	public static Parametres parametres = null;
	public static Creer_Tableaux creer_tableau = null;
	public static Importer importer = null;
	public static ShowFile showfile = null;
	public static ShowFile showtrash = null;
	public static ShowFile showerrors = null;
	public static Search search = null;
	public static ShowHistory history = null;
	public static VineyardPanel vignobles = null;
	public static ShowMoreHistory Morehistory = null;
	public static AddVin addWine = null;
	public static JTabbedPane tabbedPane = new JTabbedPane();
	public static String archive = null;
	public static LinkedList<String> half = new LinkedList<String>();
	public static String defaut_half = null;
	private static MyLinkedHashMap configGlobal = new MyLinkedHashMap();
	private static MyLinkedHashMap configCave = null;
	private static FileWriter oDebugFile = null;
	private static File debugFile = null;
	private static boolean bDebug = false;
	protected static LinkedList<Rangement> m_oCave = new LinkedList<Rangement>();
	private static LinkedList<Bouteille> trash = new LinkedList<Bouteille>();
	private static LinkedList<MyCellarError> errors = new LinkedList<MyCellarError>();
	public static Rangement defaultPlace = new Rangement("");
	private static String m_sWorkDir = null;
	protected static String m_sTempDir = null;
	protected static String m_sGlobalDir = null;
	protected static boolean m_bIsTrueFile = false;
	protected static String m_sDataFile = "data.xml";
	protected static String m_sUntitledFile = "Untitled1.sinfo";
	protected static String m_sPreviewFile = "preview.xml";
	protected static String m_sPreviewHTMLFile = "preview.html";
	protected static String m_sXMLPlacesFile = "MyCellar.xml";
	protected static String m_sXMLTypesFile = "Types.xml";
	private static boolean isXMLTypesFileToDelete = false;
	protected static String m_sXMLYearsFile = "Years.xml";
	protected static String m_sXMLBottlesFile = "Bouteilles.xml";
	public static String m_sVersion = "2.3";
	protected static boolean m_bWorkDirCalculated = false;
	protected static boolean m_bTempDirCalculated = false;
	protected static boolean m_bGlobalDirCalculated = false;
	protected static boolean bYearControlCalculated = false;
	protected static boolean bYearControled = false;
	private static boolean MacOS = false;
	protected static LinkedList<String> listColumns = new LinkedList<String>();
	public static Creer_Rangement createPlace;
	public static Creer_Rangement modifyPlace;
	public static CellarOrganizerPanel managePlace;
	public static CellarOrganizerPanel chooseCell;
	public static Supprimer_Rangement deletePlace;
	public static Stat stat;
	public static Country france = new Country("FRA", "France");
	private static LinkedList<File> dirToDelete = new LinkedList<File>();
	private static boolean modified = false;
	private static boolean listCaveModified = false;
	public static char priceSeparator;

	/**
	 * init
	 */
	public static void init() {

		try {
			Program.archive = "";
			Program.bDebug = true;
			MacOS = System.getProperty("os.name").startsWith("Mac");
			Server.getInstance().getAvailableVersion();
			// Initailisation du répertoire de travail
			getWorkDir(false);
			Debug("Program: Temp Dir: " + getWorkDir(false));
			Debug("Program: Initializing Configuration files...");
			inputPropGlobal = getGlobalDir(true) + "config.ini";
			File fileIni = new File(inputPropGlobal);
			if(!fileIni.exists()) {
				fileIni.createNewFile();
			}
			propGlobal.load(new FileInputStream(fileIni));
			LanguageFileLoader.loadLanguageFiles( "U" );

			//Initialisation de la Map contenant config
			Enumeration<Object> keys = propGlobal.keys();
			while (keys.hasMoreElements()) {
				String key = keys.nextElement().toString();
				putGlobalConfigString(key, propGlobal.getProperty(key));
			}
		}
		catch (Exception e) {
			showException(e);
		}
	}

	/**
	 * initConf
	 */
	public static void initConf() {
		try {
			Debug("Program: Temp Dir: " + getWorkDir(false));
			Debug("Program: Initializing Configuration files...");
			loadProperties();
			File f = new File(getWorkDir(true) + m_sDataFile);
			if(!f.exists())
				f.createNewFile();
			LanguageFileLoader.loadLanguageFiles( "U" );

			//Initialisation de la Map contenant config
			Debug( "Program: Initialize ConfigGlobal");
			Enumeration<Object> keys = propGlobal.keys();
			while (keys.hasMoreElements()) {
				String key = keys.nextElement().toString();
				putGlobalConfigString(key, propGlobal.getProperty(key));
			}

			verifyConfigFile();
			initPriceSeparator();
			cleanAndUpgrade();
		}
		catch (Exception e) {
			showException(e);
		}
	}
	
	private static void initPriceSeparator() {
		 String sVirgule;
		 if(Program.hasConfigCaveKey("PRICE_SEPARATOR")) {
			 sVirgule = Program.getCaveConfigString("PRICE_SEPARATOR","");
			 priceSeparator = sVirgule.charAt(0);
		 }
		 else {
			 java.text.DecimalFormat df = new java.text.DecimalFormat();
			 priceSeparator = df.getDecimalFormatSymbols().getDecimalSeparator();
		 }
	}

	/**
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	private static void loadProperties() throws IOException, FileNotFoundException {

		inputPropCave = m_sWorkDir + "/config.ini";
		File f = new File(inputPropCave);
		if( !f.exists() )
			f.createNewFile();
		configCave = new MyLinkedHashMap();
		propCave.load(new FileInputStream(inputPropCave));

		Enumeration<Object> keys = propCave.keys();
		while (keys.hasMoreElements()) {
			String key = keys.nextElement().toString();
			putCaveConfigString(key, propCave.getProperty(key));
		}

		f = new File(m_sWorkDir + "/search.ini");
		if( f.exists() )
			FileUtils.deleteQuietly(f);

		half = MyXmlDom.readTypesXml();
		if(half == null) {
			half = new LinkedList<String>();
			if(getStorage().getAllList() != null) {
				for(Bouteille b : getStorage().getAllList()) {
					String type = b.getType();
					if(type != null && !type.isEmpty() && !half.contains(type))
						half.add(type);
				}
			}
			defaut_half = "75cl";
		}

		if(half.isEmpty()) {
			half.add("75cl");
			half.add("37.5cl");
			defaut_half = "75cl";
		}
	}

	/**
	 * cleanAndUpgrade
	 * 
	 * Pour nettoyer et mettre a jour le programme
	 */
	protected static void cleanAndUpgrade() {
		/*String sVersion = getCaveConfigString("VERSION", "");
		if(sVersion.isEmpty()) {
			putCaveConfigString("VERSION", m_sVersion);
			return;
		}
		int n1 = Integer.parseInt(sVersion.substring(0,1));
		int n2 = Integer.parseInt(sVersion.substring(2,3));
		int val = n1*10 + n2;
		// Affichage du nombre avec 2 décimales.
		if ( val < 23 )
		{
			int n = getCaveConfigInt("HAUTEUR_LV", 430);
			n += 50;
			putCaveConfigInt("HAUTEUR_LV", n);
			putCaveConfigString("VERSION", m_sVersion);
		}
		removeGlobalConfigString("SPLASHSCREEN");*/
	}


	/**
	 * setLanguage
	 * @param lang String
	 * @return boolean
	 */
	public static boolean setLanguage(String lang) {
		Program.Debug("set Language : "+lang);
		return LanguageFileLoader.loadLanguageFiles( lang );
	}

	/**
	 * verifyConfigFile
	 */
	public static void verifyConfigFile() {

		Debug("Program: Verifying INI file...");
		if (!propGlobal.containsKey("LANGUAGE")) {
			propGlobal.setProperty("LANGUAGE", "F");
		}
		else {
			if (propGlobal.get("LANGUAGE").equals("")) {
				propGlobal.setProperty("LANGUAGE", "F");
			}
		}
	}

	public static void showException(Exception e) {
		showException(e, true);
	}

	/**
	 * showException
	 * @param e Exception
	 */
	public static void showException(Throwable e, boolean _bShowWindowErrorAndExit) {
		StackTraceElement st[] = new StackTraceElement[1];
		st = e.getStackTrace();
		String error = "";
		for (int z = 0; z < st.length; z++) {
			error = error.concat("\n" + st[z]);
		}
		if(error.indexOf("javax.swing.plaf.synth.SynthContext.getPainter(SynthContext.java:171)") != -1
				|| error.indexOf("javax.swing.LayoutComparator.compare") != -1)
			_bShowWindowErrorAndExit = false;
		if (_bShowWindowErrorAndExit)
			javax.swing.JOptionPane.showMessageDialog(null, e.toString(), "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
		FileWriter fw = null;
		try {
			fw = new FileWriter(getGlobalDir(true)+"Errors.log");
			fw.write(e.toString());
			fw.write(error);
			fw.flush();
			fw.close();
		}
		catch (IOException ex) {}
		Debug("Program: ERROR:");
		Debug("Program: "+e.toString());
		Debug("Program: "+error);
		if (bDebug) e.printStackTrace();
		sendMail(error, debugFile);
		if (_bShowWindowErrorAndExit)
			System.exit(999);
	}

	public static void sendMail(String error, File filename) {
		InputStream stream = Program.class.getClassLoader().getResourceAsStream("resources/MyCellar.dat");
		BufferedReader reader = new BufferedReader(new InputStreamReader(stream));

		try {
			String line = reader.readLine();
			reader.close();
			String decoded = new String(Base64.decodeBase64(line.getBytes()));
			final String[] values = decoded.split("/");

			if(values == null)
				return;

			String to = values[0];
			String from = values[1];
			String msgText1 = error;
			String subject = "Problem";

			// create some properties and get the default Session
			Properties props = System.getProperties();

			props.put("mail.smtp.auth", "true");
			props.put("mail.smtp.starttls.enable", "true");
			props.put("mail.smtp.host", "smtp.gmail.com");
			props.put("mail.smtp.port", "587");

			Session session = Session.getInstance(props, new javax.mail.Authenticator() {
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
			msg.setSubject(subject);

			// create and fill the first message part
			MimeBodyPart mbp1 = new MimeBodyPart();
			mbp1.setText(msgText1);

			// create the second message part
			MimeBodyPart mbp2 = new MimeBodyPart();

			// attach the file to the message
			if(filename != null)
				mbp2.attachFile(filename);

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
			Exception ex = null;
			if ((ex = mex.getNextException()) != null) {
				ex.printStackTrace();
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static LinkedList<Bouteille> getTrash() {
		return trash;
	}

	public static void setToTrash(Bouteille b) {
		trash.add(b);
	}
	
	public static LinkedList<MyCellarError> getErrors() {
		return errors;
	}

	public static void addError(MyCellarError error) {
		errors.add(error);
	}

	/**
	 * deletePlaceFile: Suppression d'un objet sérialisé.
	 *
	 * @param num_rangement int: numéro du rangement à supprimer
	 * @return int
	 */
	private static int deletePlaceFile(int num_rangement) {
		int resul = 0;
		Debug("Program: Deleting serialized object...");
		try {
			File f = new File( getWorkDir(false) );
			String list[] = f.list(new MyFilenameFilter());
			if( list != null && list.length > num_rangement )
			{
				f = new File( getWorkDir(true) + list[num_rangement]);
				f.delete();
			}
		}
		catch (Exception exc) {
			Program.showException(exc);
		}
		return resul;
	}

	/**
	 * write_XSL: Ecriture du fichier XSL
	 */
	public static void write_XSL() {

		Debug("Program: Writing XSL...");
		String tmp;
		File f;
		FileWriter ficout;

		try {
			f = new File("resources/vin.xsl");
			ficout = new FileWriter(f);
			ficout.flush();
			tmp = new String("<?xml version='1.0'?>\n<xsl:stylesheet xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\" version=\"1.0\"> <xsl:template match=\"/\">\n");
			ficout.write(tmp);
			ficout.flush();
			tmp = new String("<html>\n<body>\n<table border=\"1\" cellspacing=\"0\" cellpadding=\"3\">\n<tr bgcolor=\"#FFFF00\">\n");
			ficout.write(tmp);
			ficout.flush();
			tmp = new String("<td>" + Program.convertToHTMLString(Program.getLabel("Infos208")) + "</td>\n<td>" + Program.convertToHTMLString(Program.getLabel("Infos189")) + "</td>\n");
			ficout.write(tmp);
			ficout.flush();
			tmp = new String("<td>" + Program.convertToHTMLString(Program.getLabel("Infos134")) + "</td>\n<td>" + Program.convertToHTMLString(Program.getLabel("Infos105")) + "</td>\n");
			ficout.write(tmp);
			ficout.flush();
			tmp = new String("<td>" + Program.convertToHTMLString(Program.getLabel("Infos158")) + "</td>\n<td>" + Program.convertToHTMLString(Program.getLabel("Infos028")) + "</td>\n");
			ficout.write(tmp);
			ficout.flush();
			tmp = new String("<td>" + Program.convertToHTMLString(Program.getLabel("Infos083")) + "</td>\n<td>" + Program.convertToHTMLString(Program.getLabel("Infos135")) + "</td>\n");
			ficout.write(tmp);
			ficout.flush();
			tmp = new String("<td>" + Program.convertToHTMLString(Program.getLabel("Infos137")) + "</td>\n");
			ficout.write(tmp);
			ficout.flush();
			tmp = new String("</tr>\n<xsl:for-each select=\"cellar/name\">\n<xsl:sort select=\"name\"/>\n<tr>\n");
			ficout.write(tmp);
			ficout.flush();
			tmp = new String("<td><xsl:value-of select=\"name\"/></td>");
			ficout.write(tmp);
			ficout.flush();
			tmp = new String("<td><xsl:value-of select=\"year\"/></td>");
			ficout.write(tmp);
			ficout.flush();
			tmp = new String("<td><xsl:value-of select=\"half\"/></td>");
			ficout.write(tmp);
			ficout.flush();
			tmp = new String("<td><xsl:value-of select=\"place\"/></td>");
			ficout.write(tmp);
			ficout.flush();
			tmp = new String("<td><xsl:value-of select=\"num-place\"/></td>");
			ficout.write(tmp);
			ficout.flush();
			tmp = new String("<td><xsl:value-of select=\"line\"/></td>");
			ficout.write(tmp);
			ficout.flush();
			tmp = new String("<td><xsl:value-of select=\"column\"/></td>");
			ficout.write(tmp);
			ficout.flush();
			tmp = new String("<td><xsl:value-of select=\"price\"/></td>");
			ficout.write(tmp);
			ficout.flush();
			tmp = new String("<td><xsl:value-of select=\"comment\"/></td>");
			ficout.write(tmp);
			ficout.flush();
			tmp = new String("<td><xsl:value-of select=\"dateOfC\"/></td>");
			ficout.write(tmp);
			ficout.flush();
			tmp = new String("<td><xsl:value-of select=\"parker\"/></td>");
			ficout.write(tmp);
			ficout.flush();
			tmp = new String("<td><xsl:value-of select=\"appellation\"/></td>");
			ficout.write(tmp);
			ficout.flush();
			tmp = new String("</tr>\n</xsl:for-each>\n</table>\n</body>\n</html>\n</xsl:template>\n</xsl:stylesheet>");
			ficout.write(tmp);
			ficout.flush();
			ficout.close();
		}
		catch (IOException ioe) {}
	}

	public static String convertToHTMLString(String s) {
		return StringEscapeUtils.escapeHtml(s);
	}

	public static String convertStringFromHTMLString(String s) {
		return StringEscapeUtils.unescapeHtml(s);
	}

	public static String convertToXMLString(String s) {
		return StringEscapeUtils.escapeXml(s);
	}

	public static String convertStringFromXMLString(String s) {
		return StringEscapeUtils.unescapeXml(s);
	}

	public static String removeAccents(String s) {
		s = Normalizer.normalize(s, Normalizer.Form.NFD);
		s = s.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
		return s;
	}


	/**
	 * writeOtherObject Ecrit tous les objets qui ne sont pas des rangements
	 */
	private static void writeOtherObject() {
		Debug("Program: Writing Other Objects...");

		MyXmlDom.writeYears(getStorage().getAnneeList());

		getStorage().saveHistory();
		Debug("Program: Writing Other Objects... Done");
	}

	/**
	 * Chargement des données XML (Bouteilles et Rangement) ou des données sérialisées en cas de pb
	 * @return
	 */
	public static boolean loadObjects() {
		if(!ListeBouteille.loadXML()) {
			read_Object();
			getStorage().setListBouteilles(getStorage().getAllList());
		}
		else {
			m_oCave = MyXmlDom.readMyCellarXml("");
			loadYears();
			loadMaxPrice();
			getStorage().loadHistory();
		}
		if(m_oCave == null) {
			m_oCave = new LinkedList<Rangement>();
			m_oCave.add(defaultPlace);
			return false;
		}
		return true;
	}

	/**
	 * read_Object: Fonction de lecture des objets Rangement sérialiser.
	 */
	private static void read_Object() {

		Debug("Program: Reading Objects...");
		File f1 = new File( m_sWorkDir + "/" );
		//Récupération de la liste des fichiers
		Debug("Program: Collecting file list");
		LinkedList<Rangement> cave = new LinkedList<Rangement>();
		boolean resul = true;
		//Lecture des fichiers et écriture de MyCellar.xml
		Debug("Program: Writing MyCellar.xml");
		boolean bresul = getStorage().readRangement(cave);
		if (bresul && !cave.isEmpty()) {
			loadYears();
		}
		else {
			Debug("Program: WARNING: Destroying internal files");
			f1 = new File( m_sWorkDir + "/static_col.sinfo");
			f1.delete();
			cave = null;
		}
		getStorage().loadHistory();
		if (!resul) {
			Debug("Program: WARNING: Loading Unsuccessful");
			getStorage().setAll(null);
		}
		if(cave != null)
			m_oCave = cave;
	}

	private static void loadYears() {
		File f1 = new File(getXMLYearsFileName());
		if(f1.exists()) {
			getStorage().setAnnee(MyXmlDom.readYears());
		}
		else
			rebuildStats();
	}

	/**
	 * 
	 */
	public static void rebuildStats() {
		Debug("Program: Rebuild Statistics");

		getStorage().setAnnee(null);
		for (Bouteille b:getStorage().getAllList()) {
			if (b != null) {
				getStorage().addAnnee(b.getAnneeInt());
			}
		}
	}
	
	public static void loadMaxPrice() {
		
		OptionalDouble i = getStorage().getAllList().stream().mapToDouble(bouteille -> bouteille.getPriceDouble()).max();
		if(i.isPresent())
			Bouteille.prix_max = (int) i.getAsDouble();
		else
			Bouteille.prix_max = 0;
	}
	
	public static int getCellarValue() {	
		return (int) getStorage().getAllList().stream().mapToDouble(bouteille -> bouteille.getPriceDouble()).sum();
	}


	/**
	 * getAide: Appel de l'aide
	 */
	public static void getAide() {

		String helpHS = "MyCellar.hs";
		File f = new File("./Help/" + helpHS);

		if (f.exists()) {
			try {
				Runtime.getRuntime().exec("java -jar ./Help/hsviewer.jar -hsURL \"file:./Help/MyCellar.hs\"");
			}
			catch (IOException ex) {
			}
		}
		else {
			new Erreur(Program.getError("Error162"));
		}
	}

	/**
	 * zipDir: Compression de répertoire
	 *
	 * @param archive String
	 * @return boolean
	 */
	private static boolean zipDir(String archive) {

		Debug("Program: zipDir: Zipping in "+m_sWorkDir+" with archive "+archive);
		int BUFFER = 2048;
		try {
			// création d'un flux d'écriture sur fichier
			FileOutputStream dest = new FileOutputStream(archive);
			// calcul du checksum : Adler32 (plus rapide) ou CRC32
			CheckedOutputStream checksum = new CheckedOutputStream(dest, new Adler32());
			// création d'un buffer d'écriture
			BufferedOutputStream buff = new BufferedOutputStream(checksum);
			// création d'un flux d'écriture Zip
			ZipOutputStream out = new ZipOutputStream(buff);
			// spécification de la méthode de compression
			out.setMethod(ZipOutputStream.DEFLATED);
			// spécifier la qualité de la compression 0..9
			out.setLevel(Deflater.BEST_COMPRESSION);
			// buffer temporaire des données à écrire dans le flux de sortie
			byte data[] = new byte[BUFFER];
			// extraction de la liste des fichiers du répertoire courant
			File f = new File(m_sWorkDir);
			String files[] = f.list();
			LinkedList<String> zipEntryList = new LinkedList<String>();
			// pour chacun des fichiers de la liste
			for (int i = 0; i < files.length; i++) {
				f = new File( m_sWorkDir + "/" +files[i]);
				if( f.isDirectory() || files[i].compareTo(m_sUntitledFile) == 0)
					continue;
				// création d'un flux de lecture
				FileInputStream fi = new FileInputStream(m_sWorkDir + "/" + files[i]);
				// création d'un tampon de lecture sur ce flux
				BufferedInputStream buffi = new BufferedInputStream(fi, BUFFER);
				// création d'en entrée Zip pour ce fichier
				String name = removeAccents(files[i]);
				ZipEntry entry = new ZipEntry(name);
				if(zipEntryList.contains(name))
					continue;
				zipEntryList.add(name);
				// ajout de cette entrée dans le flux d'écriture de l'archive Zip
				out.putNextEntry(entry);
				// écriture du fichier par paquet de BUFFER octets dans le flux d'écriture
				int count;
				while ( (count = buffi.read(data, 0, BUFFER)) != -1) {
					out.write(data, 0, count);
				}
				// Close the current entry
				out.closeEntry();
				// fermeture du flux de lecture
				buffi.close();
				fi.close();
			}
			// fermeture du flux d'écriture
			out.close();
			buff.close();
			checksum.close();
			dest.close();
		}
		catch (Exception e) {
			if (Program.bDebug) {
				Debug("Program: zipDir: Error while zipping");
				showException(e, false);
			}
			return false;
		}
		Debug("Program: zipDir Done");
		return true;
	}

	/**
	 * unzipDir: Dézippe une archive dans un répertoire
	 *
	 * @param dest_dir String
	 * @param archive String
	 * @return boolean
	 */
	private static boolean unzipDir(String dest_dir, String archive) {
		try {
			Debug( "Unzip: Archive "+archive );
			int BUFFER = 2048;
			// fichier destination
			BufferedOutputStream dest = null;
			// ouverture fichier entrée
			archive = archive.replaceAll("\\\\", "/");
			File f = new File(archive);
			if(!f.exists())
				return false;
			FileInputStream fis = new FileInputStream(archive);
			// ouverture fichier de buffer
			BufferedInputStream buffi = new BufferedInputStream(fis);
			// ouverture archive Zip d'entrée
			ZipInputStream zis = new ZipInputStream(buffi);
			// entrée Zip
			ZipEntry entry;
			// parcours des entrées de l'archive
			while ( (entry = zis.getNextEntry()) != null) {
				// affichage du nom de l'entrée
				int count;
				byte data[] = new byte[BUFFER];
				// création fichier
				f = new File(dest_dir);
				if( !f.exists() )
					f.mkdir();
				FileOutputStream fos = new FileOutputStream(dest_dir + "/" + entry.getName());
				if( bDebug )Debug( "Unzip: File "+dest_dir + "/" + entry.getName() );
				// affectation buffer de sortie
				dest = new BufferedOutputStream(fos, BUFFER);
				// écriture sur disque
				while ( (count = zis.read(data, 0, BUFFER)) != -1) {
					dest.write(data, 0, count);
				}
				// vidage du tampon
				dest.flush();
				// fermeture fichier
				dest.close();
			}
			// fermeture archive
			zis.close();
		}
		catch (Exception e) {e.printStackTrace();
		return false;
		}
		return true;
	}


	/**
	 * Sauvegarde le fichier
	 */
	public static void save() {
		Debug("Program: Saving...");
		saveAs("");
	}

	/**
	 * saveAs
	 * @param _sFilename String
	 */
	public static void saveAs(String _sFilename) {
		Debug("Program: Saving all files...");

		saveProperties();
		saveGlobalProperties();
		LinkedList<Rangement> cave = getCave();
		// Suppression de fichiers inutiles
		for (Rangement r : cave) {
			try {
				File fToDelete = new File(getWorkDir(true)+Program.convertToHTMLString(r.getNom()) + ".ser");
				if(fToDelete.exists())
					fToDelete.delete();
			}
			catch (NullPointerException npe) {}
		}

		if(isListCaveModified())
			MyXmlDom.writeMyCellarXml(cave,"");

		Program.writeOtherObject();
		CountryVignobles.save();
		ListeBouteille.writeXML();

		String sFileName = archive;
		if(!_sFilename.isEmpty())
			sFileName = _sFilename;
		Program.zipDir(sFileName);

		modified = false;
		listCaveModified = false;
		Debug("Program: Saving all files...Done");
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
		if(!bDebug)
			return;

		try {
			if (oDebugFile == null) {
				String sDir = System.getProperty("user.home");
				if( !sDir.isEmpty() )
					sDir += "/MyCellarDebug";
				File f_obj = new File( sDir );
				if(!f_obj.exists())
					f_obj.mkdir();
				Calendar oCal = Calendar.getInstance();
				String sDate = oCal.get(Calendar.DATE) + "-" + (oCal.get(Calendar.MONTH)+1) + "-" + oCal.get(Calendar.YEAR);
				debugFile = new File(sDir, "Debug-"+sDate+".log");
				oDebugFile = new FileWriter(debugFile, true);
			}
			oDebugFile.write("[" + java.util.Calendar.getInstance().getTime().toString() + "]: " + sText + "\n");
			oDebugFile.flush();
		}
		catch (Exception e) {}
	}

	/**
	 * GetCave
	 *
	 * @return LinkedList<Rangement>
	 */
	public static LinkedList<Rangement> getCave() {
		return m_oCave;
	}

	/**
	 * GetCave
	 *
	 * @param _nCave int
	 * @return Rangement
	 */
	public static Rangement getCave(int _nCave) {
		if ( _nCave >= m_oCave.size() || _nCave < 0 )
			return null;
		return m_oCave.get(_nCave);
	}

	/**
	 * GetCave
	 *
	 * @param _nCave String
	 * @return Rangement
	 */
	public static Rangement getCave(String name) {
		if (name == null || name.isEmpty())
			return null;
		for(Rangement r: m_oCave) {
			if(name.equals(r.getNom()))
				return r;
		}
		return null;
	}
	
	/**
	 * GetCaveIndex
	 *
	 * @param _nCave String
	 * @return int
	 */
	public static int getCaveIndex(String name) {
		if (name == null || name.isEmpty())
			return -1;
		for(int i = 0; i < m_oCave.size(); i++) {
			if(name.equals(m_oCave.get(i).getNom()))
				return i;
		}
		return -1;
	}


	/**
	 * SetCave
	 *
	 * @param _oCave LinkedList<Rangement>
	 */
	public static void setCave(LinkedList<Rangement> _oCave) {
		m_oCave = _oCave;
	}

	/**
	 * addCave
	 *
	 * @param _oCave Rangement
	 */
	public static void addCave(Rangement _oCave) {
		if(_oCave == null)
			return;
		m_oCave.add(_oCave);
		setListCaveModified();
		setModified();
		if (Program.bDebug) Debug("Program: Sorting places...");
		Collections.sort(m_oCave);
	}

	/**
	 * removeCave
	 *
	 * @param _oCave Rangement
	 */
	public static void removeCave(Rangement _oCave) {
		if(_oCave == null)
			return;
		int num = m_oCave.indexOf(_oCave);
		m_oCave.remove(_oCave);
		deletePlaceFile(num);
		setModified();
		setListCaveModified();
	}


	/**
	 * GetCaveLength
	 *
	 * @return int
	 */
	public static int GetCaveLength() {
		if (m_oCave == null)
			return 0;

		return m_oCave.size();
	}

	/**
	 * openFile: Choix d'un fichier à ouvrir.
	 *
	 * @param e ActionEvent
	 */
	static boolean newFile() {
		Debug("newFile: Start creating new file");
		return openaFile(null);
	}

	/**
	 * openaFile: Ouvre un fichier
	 *
	 * @param e ActionEvent
	 */
	public static boolean openaFile(File f) {
		if(f != null)
			Debug("openFile: Opening file: "+f.getAbsolutePath());
		else
			Debug("openFile: Creating new file");

		LinkedList<String> list = new LinkedList<String>();
		list.addLast(getGlobalConfigString("LAST_OPEN1",""));
		list.addLast(getGlobalConfigString("LAST_OPEN2",""));
		list.addLast(getGlobalConfigString("LAST_OPEN3",""));
		list.addLast(getGlobalConfigString("LAST_OPEN4",""));
		if(f != null && list.contains(f.getAbsolutePath()))
			list.remove(f.getAbsolutePath());

		// Sauvegarde avant de charger le nouveau fichier
		closeFile();

		if(f == null) {
			setFileSavable(false);
			// Nouveau fichier
			String fic = getWorkDir(true) + m_sUntitledFile;
			f = new File(fic);
			if(f.exists())
				f.delete();
			try {
				f.createNewFile();
			} catch (IOException e) {
				showException(e);
			}
		}
		else
			setFileSavable(f.exists());

		if(!f.exists()) {
			new Erreur(MessageFormat.format(Program.getError("Error020"), f.getAbsolutePath())); //Fichier non trouvé);

			putGlobalConfigString("LAST_OPEN1", list.pop());
			putGlobalConfigString("LAST_OPEN2", list.pop());
			putGlobalConfigString("LAST_OPEN3", list.pop());
			// On a déjà enlevé un élément de la liste
			putGlobalConfigString("LAST_OPEN4", "");
			saveGlobalProperties();
			return false;
		}

		Program.archive = f.getAbsolutePath();

		boolean bUnzipSucceeded = false;
		try {
			// Dézippage
			bUnzipSucceeded = Program.unzipDir(Program.getWorkDir(false), Program.archive);
			if ( bUnzipSucceeded )
				Debug("Unzipping "+Program.archive+" to "+Program.getWorkDir(false) +" OK");
			else {
				Debug("Unzipping "+Program.archive+" to "+Program.getWorkDir(false) + " KO");
				Program.archive = "";
			}
		}
		catch (Exception e) {
			Debug("ERROR: Unable to unzip file "+Program.archive);
			Program.showException(e,false);
			archive = "";
			return false;
		}

		// Chargement
		File data = new File(getDataFileName());
		if(!data.exists())
			Debug("ERROR: Unable to find file data.xml!!");

		//Chargement des objets Rangement, Bouteilles et History
		boolean loaded = true;
		Debug("Reading Places, Bottles & History");
		if (!Program.loadObjects()) {
			Debug("Reading Object KO");
			loaded = false;
		}
		// Contrôle du nombre de rangement
		Debug("Checking place count");
		if (loaded) {
			int i = 0;
			LinkedList<Rangement> cave = Program.getCave();
			while (i < Program.GetCaveLength() && cave.get(i) != null) {
				i++;
			}

			if (i != Program.GetCaveLength())
				loaded = false;

			Debug("Place Count: Program="+Program.GetCaveLength()+" cave="+i);
		}
		// En cas d'erreur
		if (!loaded)
			Debug("ERROR: Loading");

		//Chargement des rangement par le fichier d'options si la relecture des objets sérialisés a échouée
		if (Program.GetCaveLength() == 0) {
			Debug("Reading places from file");
			LinkedList<Rangement> cave = MyXmlDom.readMyCellarXml("");
			if(cave != null) {
				Program.setCave(cave);
			}
		}

		try {
			loadProperties();
		} catch (FileNotFoundException e) {
			Program.showException(e,false);
			return false;
		} catch (IOException e) {
			Program.showException(e,false);
			return false;
		}

		RangementUtils.putTabStock();
		if(!Program.getErrors().isEmpty())
			new OpenShowErrorsAction().actionPerformed(null);
		CountryVignobles.load();
		CountryVignobles.addVignobleFromBottles();

		Program.putGlobalConfigString("STARTUP", "1");
		// Fin chargement

		if(isFileSavable())
			list.addFirst(f.getAbsolutePath());

		putGlobalConfigString("LAST_OPEN1", list.pop());
		putGlobalConfigString("LAST_OPEN2", list.pop());
		putGlobalConfigString("LAST_OPEN3", list.pop());
		putGlobalConfigString("LAST_OPEN4", list.pop());

		putCaveConfigString("FILE_SRC", f.getAbsolutePath());
		putCaveConfigString("DIR", f.getParent());

		saveGlobalProperties();
		modified = false;
		listCaveModified = false;
		dirToDelete.add(new File(m_sWorkDir));
		return true;
	}

	/**
	 * Fonction pour sauvegarder les propriétés globales du programme
	 */
	public static void saveGlobalProperties() {
		Debug("Program: Saving Global Properties");
		Object[] val = Program.configGlobal.keySet().toArray();
		String key;
		for (int y = 0; y < val.length; y++) {
			key = val[y].toString();
			propGlobal.put(key, configGlobal.getString(key));
		}
		try {
			propGlobal.store(new FileOutputStream(inputPropGlobal), null);
		} catch (FileNotFoundException e) {
			showException(e);
		} catch (IOException e) {
			showException(e);
		}
	}


	/**
	 * closeFile: Fermeture du fichier.
	 *
	 */
	static boolean closeFile() {

		Debug("Program: closeFile: Start closing file");
		try {
			boolean bSave = false;
			if(!Program.archive.isEmpty() && isModified()) {
				if (JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(null, Program.getError("Error199"), Program.getLabel("Infos049"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE)) {
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
								int index = fic.indexOf(".");
								if (index == -1) {
									fic = fic.concat(".sinfo");
								}

								Program.archive = fic;
							}
						}
						catch (Exception e3) {
							Program.showException(e3);
						}
					}
				}

				putCaveConfigString("ANNEE_AUTO", "0");
				putCaveConfigString("FILE_SRC", Program.archive);
			}

			File f = new File(getPreviewXMLFileName());
			if (f.exists()) {
				f.delete();
			}
			f = new File(getPreviewHTMLFileName());
			if (f.exists()) {
				f.delete();
			}
			//Tri du tableau et écriture du fichier XML
			if ( bSave )
			{
				if(!ListeBouteille.writeXML())
					return false;

				if(isListCaveModified())
					MyXmlDom.writeMyCellarXml(Program.getCave(),"");

				if(!Program.configCave.containsKey("PRICE_SEPARATOR"))
				{
					java.text.DecimalFormat df = new java.text.DecimalFormat();
					char virgule = df.getDecimalFormatSymbols().getDecimalSeparator();
					String sVirgule = Character.toString(virgule);
					putCaveConfigString("PRICE_SEPARATOR", sVirgule);
				}

				// Suppression d'ancien fichier
				for(Rangement r : getCave()){
					File fToDelete = new File(getWorkDir(true)+Program.convertToHTMLString(r.getNom()) + ".ser");
					if(fToDelete.exists())
						fToDelete.delete();
				}

				saveProperties();

				if (!Program.getCave().isEmpty()) {						
					writeOtherObject();
					CountryVignobles.save();
					zipDir(Program.archive);
				}
			}
			// Sauvegarde des propriétés globales
			saveGlobalProperties();

			if (getCaveConfigInt("FIC_EXCEL", 0) == 1) {
				//Ecriture Excel
				RangementUtils.write_XLS(getCaveConfigString("FILE_EXCEL",""), getStorage().getAllList(), true);
			}

			dirToDelete.add(new File(m_sWorkDir));
			m_bWorkDirCalculated = false;
		}
		catch (Exception ex) {
			showException(ex);
			return false;
		}
		tabbedPane.removeAll();
		getStorage().close();
		CountryVignobles.init();
		Countries.init();
		archive = "";
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
		trash.clear();
		setFileSavable(false);
		modified = false;
		listCaveModified = false;
		Search.clearResults();
		if(getCave() != null)
			getCave().clear();
		Debug("Program: closeFile: Closing file Ended");

		return true;
	}

	public static void deleteTempFiles() {
		for(File f : dirToDelete) {
			if(!f.exists() || f.getName().equalsIgnoreCase("Global"))
				continue;
			try{
				Debug("Program: closeFile: Deleting work directory: "+f.getAbsolutePath());
				FileUtils.cleanDirectory(f);
				FileUtils.deleteDirectory(f);
			}catch(Exception e){
				Debug("Program: Error deleting "+f.getAbsolutePath());
				Debug("Program: "+e.getMessage());
			}
		}
	}

	/**
	 * Save Properties
	 */
	private static void saveProperties() {

		if(isXMLTypesFileToDelete) {
			File fToDelete = new File(getWorkDir(true) + m_sXMLTypesFile);
			if(fToDelete.exists())
				fToDelete.delete();
		}
		else
			MyXmlDom.writeTypeXml(half);

		if(inputPropCave != null)
		{
			Object[] val = Program.configCave.keySet().toArray();
			String key;
			for (int y = 0; y < val.length; y++) {
				key = val[y].toString();
				propCave.put(key, configCave.getString(key));
			}
			try {
				propCave.store(new FileOutputStream(inputPropCave), null);
			} catch (FileNotFoundException e) {
				Program.showException(e);
			} catch (IOException e) {
				Program.showException(e);
			}
			inputPropCave = null;
		}
	}

	/**
	 * getGlobalDir: Retourne le nom du repertoire des propriétés globales.
	 * @param _bWithEndSlash
	 * @return
	 */
	public static String getGlobalDir(boolean _bWithEndSlash)
	{
		if(m_bGlobalDirCalculated)
		{
			if (_bWithEndSlash)
				return m_sGlobalDir + File.separator;
			return m_sGlobalDir;
		}
		m_bGlobalDirCalculated = true;
		String sDir = System.getProperty("user.home");
		if( sDir.isEmpty() )
			m_sGlobalDir = "./Object/Global";
		else
			m_sGlobalDir = sDir + "/MyCellar/Global";
		File f_obj = new File( m_sGlobalDir );
		if(!f_obj.exists())
			f_obj.mkdir();

		if (_bWithEndSlash)
			return m_sGlobalDir + File.separator;
		return m_sGlobalDir;
	}

	/**
	 * getWorkDir: Retourne le nom du repertoire de travail.
	 * @param _bWithEndSlash
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
	public static String getWorkDir(boolean _bWithEndSlash)
	{
		if( m_bWorkDirCalculated )
		{
			if (_bWithEndSlash)
				return m_sWorkDir + File.separator;
			return m_sWorkDir;
		}
		m_bWorkDirCalculated = true;
		Debug("Program: Calculating work directory.");
		String sDir = System.getProperty("user.home");
		if( sDir.isEmpty() )
			m_sWorkDir = "./Object";
		else
			m_sWorkDir = sDir + "/MyCellar";
		File f_obj = new File( m_sWorkDir );
		if(!f_obj.exists())
			f_obj.mkdir();

		java.util.Calendar g = GregorianCalendar.getInstance();
		String sTime = Integer.toString( g.get(Calendar.YEAR ) );
		sTime += Integer.toString( g.get(Calendar.MONTH ) );
		sTime += Integer.toString( g.get(Calendar.DAY_OF_MONTH ) );
		sTime += Integer.toString( g.get(Calendar.HOUR ) );
		sTime += Integer.toString( g.get(Calendar.MINUTE ) );
		sTime += Integer.toString( g.get(Calendar.SECOND ) );

		m_sWorkDir += File.separator + sTime;

		f_obj = new File(m_sWorkDir);
		if(!f_obj.exists())
			f_obj.mkdir();

		Debug("Program: work directory: "+m_sWorkDir);

		if (_bWithEndSlash)
			return m_sWorkDir + File.separator;
		return m_sWorkDir;
	}

	/**
	 * getTempDir: Retourne le nom du repertoire Temporaire.
	 * @param _bWithEndSlash
	 * @return
	 */
	public static String getTempDir(boolean _bWithEndSlash)
	{
		if(m_bTempDirCalculated)
		{
			if (_bWithEndSlash)
				return m_sTempDir + File.separator;
			return m_sTempDir;
		}
		m_bTempDirCalculated = true;
		String sDir = System.getProperty("user.home");
		if(sDir.isEmpty())
			m_sTempDir = "./Temp";
		else
			m_sTempDir = sDir + "/MyCellar/Temp";
		File f_obj = new File( m_sTempDir );
		if(!f_obj.exists())
			f_obj.mkdir();

		if (_bWithEndSlash)
			return m_sTempDir + File.separator;
		return m_sTempDir;
	}

	public static String getFullFilename()
	{
		return archive;
	}

	public static String getShortFilename()
	{
		return getShortFilename(archive);
	}

	public static String getShortFilename( String sFilename )
	{
		String tmp = sFilename;
		tmp = tmp.replaceAll("\\\\", "/");
		int ind1 = tmp.lastIndexOf("/");
		int ind2 = tmp.indexOf(".sinfo");
		try {
			tmp = tmp.substring(ind1 + 1, ind2);
		}
		catch (Exception ex) {}
		return tmp;
	}

	public static String getGlobalConfigString( String _sKey, String _sDefaultValue )
	{
		return configGlobal.getString(_sKey, _sDefaultValue);
	}

	public static String getCaveConfigString( String _sKey, String _sDefaultValue )
	{
		if( null != configCave )
			return configCave.getString(_sKey, _sDefaultValue);
		Debug("ERROR: Calling null configCave for key '"+_sKey+"' and default value '"+_sDefaultValue+"'");
		return _sDefaultValue;
	}

	public static int getGlobalConfigInt( String _sKey, int _nDefaultValue )
	{
		return configGlobal.getInt(_sKey, _nDefaultValue);
	}

	public static int getCaveConfigInt( String _sKey, int _nDefaultValue )
	{
		if( null != configCave )
			return configCave.getInt(_sKey, _nDefaultValue);
		Debug("ERROR: Calling null configCave for key '"+_sKey+"' and default value '"+_nDefaultValue+"'");
		return _nDefaultValue;
	}

	public static void putGlobalConfigString( String _sKey, String _sValue )
	{
		configGlobal.put(_sKey, _sValue);
	}

	public static void putCaveConfigString( String _sKey, String _sValue )
	{
		if( null != configCave )
			configCave.put(_sKey, _sValue);
		else
			Debug("ERROR: Unable to put value in configCave: [" + _sKey + " - " + _sValue + "]");
	}

	public static void putGlobalConfigInt( String _sKey, Integer _sValue )
	{
		if( null != configGlobal )
			configGlobal.put(_sKey, _sValue);
		else
			Debug("ERROR: Unable to put value in configGlobal: [" + _sKey + " - " + _sValue + "]");
	}

	public static void putCaveConfigInt( String _sKey, Integer _sValue )
	{
		configCave.put(_sKey, _sValue);
	}

	/*private static void removeGlobalConfigString( String _sKey )
	{
		if( configGlobal.containsKey(_sKey))
			configGlobal.remove(_sKey);
	}*/

	public static MyLinkedHashMap getCaveConfig()
	{
		return configCave;
	}

	public static boolean hasConfigCaveKey( String _sKey )
	{
		if( null != configCave )
			return configCave.containsKey(_sKey);
		return false;
	}

	public static boolean hasConfigGlobalKey( String _sKey )
	{
		if( null != configGlobal )
			return configGlobal.containsKey(_sKey);
		return false;
	}

	public static boolean isFileSavable() {
		return m_bIsTrueFile;
	}

	public static void setFileSavable(boolean _bIsTrueFile) {
		m_bIsTrueFile = _bIsTrueFile;
	}

	public static String getDataFileName()
	{
		return getWorkDir(true) + m_sDataFile;
	}

	public static String getXMLPlacesFileName()
	{
		return getWorkDir(true) + m_sXMLPlacesFile;
	}

	public static String getXMLTypesFileName()
	{
		return getWorkDir(true) + m_sXMLTypesFile;
	}

	public static void setXMLTypesFileToDelete() {
		isXMLTypesFileToDelete = true;
	}

	public static String getXMLYearsFileName()
	{
		return getWorkDir(true) + m_sXMLYearsFile;
	}

	public static String getXMLBottlesFileName()
	{
		return getWorkDir(true) + m_sXMLBottlesFile;
	}

	public static String getUntitledFileName()
	{
		return m_sUntitledFile;
	}

	public static String getPreviewXMLFileName()
	{
		String sPreview = getGlobalDir(true) + m_sPreviewFile;
		return sPreview;
	}

	public static String getPreviewHTMLFileName()
	{
		String sPreview = getGlobalDir(true) + m_sPreviewHTMLFile;
		return sPreview;
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

	public static String getLanguage(String _id) {
		return LanguageFileLoader.getLanguage(_id);
	}

	public static void open(File file) {
		try {
			if( MacOS )
				Runtime.getRuntime().exec("/usr/bin/open " + file.getAbsolutePath());
			else
				Desktop.getDesktop().browse(file.toURI());
		} catch (IOException e) {
			showException(e, true);
		}
	}

	public static boolean hasYearControl() {
		if (bYearControlCalculated)
			return bYearControled;
		bYearControled = true;
		try {
			if (getCaveConfigInt("ANNEE_CTRL", 0) == 0) {
				bYearControled = false;
			}
		}
		catch (NullPointerException npe) {
			bYearControled = true;
			putCaveConfigInt("ANNEE_CTRL", 1);
		}
		bYearControlCalculated = true;
		return bYearControled;
	}

	public static void setYearControl(boolean b) {
		bYearControled = b;
		if(bYearControled)
			putCaveConfigInt("ANNEE_CTRL", 1);
		else
			putCaveConfigInt("ANNEE_CTRL", 0);

		bYearControlCalculated = true;
	}

	public static void updateAllPanels() {
		if(addWine != null)
			addWine.setUpdateView();
		if(search != null)
			search.setUpdateView();
		if(deletePlace != null)
			deletePlace.setUpdateView();
		if(showfile != null)
			showfile.setUpdateView();
		if(showtrash != null)
			showtrash.setUpdateView();
		if(chooseCell != null)
			chooseCell.setUpdateView();
		if(managePlace != null)
			managePlace.setUpdateView();
	}

	public static void updateManagePlacePanel() {
		if(managePlace != null)
			managePlace.setUpdateView();
	}

	public static List<Country> getCountries() {
		return Countries.getInstance().getCountries();
	}

	public static int findTab(ImageIcon image) {
		for(int i = 0; i<tabbedPane.getTabCount();i++){
			try{
				if(tabbedPane.getTabComponentAt(i) != null && tabbedPane.getIconAt(i) != null && tabbedPane.getIconAt(i).equals(MyCellarImage.WINE))
					return i;
			}catch(ArrayIndexOutOfBoundsException e){}
		}
		return -1;
	}

	public static void setModified() {
		modified = true;
	}

	public static boolean isModified() {
		return modified;
	}

	public static void setListCaveModified() {
		listCaveModified = true;
	}

	public static boolean isListCaveModified() {
		return listCaveModified;
	}

	public static PDFProperties getPDFProperties() {
		String title = getCaveConfigString("PDF_TITLE", "");
		int titleSize = getCaveConfigInt("TITLE_SIZE", 10);
		int textSize = getCaveConfigInt("TEXT_SIZE", 10);
		String border = getCaveConfigString("BORDER", "ON");
		boolean boldTitle = "bold".equals(Program.getCaveConfigString("BOLD", ""));

		PDFProperties properties = new PDFProperties(title, titleSize, textSize, "ON".equals(border), boldTitle);

		int nbCol = MyCellarFields.getFieldsList().size();
		int countColumn = 0;
		for(int i=0; i<nbCol; i++){
			int export = getCaveConfigInt("SIZE_COL" + i + "EXPORT", 0);
			if(export == 1) {
				countColumn++;
				int sizeCol = getCaveConfigInt("SIZE_COL" + i, 5);
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

	public static List<PDFRow> getPDFRows(LinkedList<Bouteille> list, PDFProperties properties) {
		LinkedList<PDFRow> rows = new LinkedList<PDFRow>();
		LinkedList<PDFColumn> columns = properties.getColumns();
		PDFRow row;
		for(Bouteille b : list) {
			row = new PDFRow();
			for(PDFColumn column : columns) {
				if(column.getField().equals(MyCellarFields.NAME))
					row.addCell(b.getNom());
				else if(column.getField().equals(MyCellarFields.YEAR))
					row.addCell(b.getAnnee());
				else if(column.getField().equals(MyCellarFields.TYPE))
					row.addCell(b.getType());
				else if(column.getField().equals(MyCellarFields.PLACE))
					row.addCell(b.getEmplacement());
				else if(column.getField().equals(MyCellarFields.NUM_PLACE))
					row.addCell(Integer.toString(b.getNumLieu()));
				else if(column.getField().equals(MyCellarFields.LINE))
					row.addCell(Integer.toString(b.getLigne()));
				else if(column.getField().equals(MyCellarFields.COLUMN))
					row.addCell(Integer.toString(b.getColonne()));
				else if(column.getField().equals(MyCellarFields.PRICE))
					row.addCell(b.getPrix());
				else if(column.getField().equals(MyCellarFields.COMMENT))
					row.addCell(b.getComment());
				else if(column.getField().equals(MyCellarFields.MATURITY))
					row.addCell(b.getMaturity());
				else if(column.getField().equals(MyCellarFields.PARKER))
					row.addCell(b.getParker());
				else if(column.getField().equals(MyCellarFields.COUNTRY)) {
					if(b.getVignoble() != null)
						row.addCell(b.getVignoble().getCountry());
					else
						row.addCell("");
				}
				else if(column.getField().equals(MyCellarFields.VINEYARD)) {
					if(b.getVignoble() != null)
						row.addCell(b.getVignoble().getName());
					else
						row.addCell("");
				}
				else if(column.getField().equals(MyCellarFields.AOC)) {
					if(b.getVignoble() != null)
						row.addCell(b.getVignoble().getAOC());
					else
						row.addCell("");
				}
				else if(column.getField().equals(MyCellarFields.IGP)) {
					if(b.getVignoble() != null)
						row.addCell(b.getVignoble().getIGP());
					else
						row.addCell("");
				}
				else if(column.getField().equals(MyCellarFields.COLOR))
					row.addCell(BottleColor.getColor(b.getColor()).toString());
			}
			rows.add(row);
		}
		return rows;
	}

	public static PDFRow getPDFHeader(PDFProperties properties) {
		LinkedList<PDFColumn> columns = properties.getColumns();
		PDFRow row = new PDFRow();
		for(PDFColumn column : columns) {
			row.addCell(column.getTitle());
		}
		return row;
	}

	protected static void cleanDebugFiles() {
		String sDir = System.getProperty("user.home");
		sDir += "/MyCellarDebug";
		File f = new File(sDir);
		Calendar oCal = Calendar.getInstance();
		oCal.add(Calendar.MONTH, -2);
		Calendar c = Calendar.getInstance();
		String files[] = f.list(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				if (name.startsWith("Debug-") && name.endsWith(".log")) {
					String date = name.substring(6, name.indexOf(".log"));
					String fields[] = date.split("-");
					c.set(Calendar.DAY_OF_MONTH, Integer.parseInt(fields[0]));
					c.set(Calendar.MONTH, Integer.parseInt(fields[1])-1);
					c.set(Calendar.YEAR, Integer.parseInt(fields[2]));
					return c.before(oCal);
				}
				if (name.startsWith("DebugFtp-") && name.endsWith(".log")) {
					String date = name.substring(9, name.indexOf(".log"));
					String fields[] = date.split("-");
					c.set(Calendar.DAY_OF_MONTH, Integer.parseInt(fields[0]));
					c.set(Calendar.MONTH, Integer.parseInt(fields[1])-1);
					c.set(Calendar.YEAR, Integer.parseInt(fields[2]));
					return c.before(oCal);
				}
				return false;
			}
		});

		for(String s : files) {
			f = new File(sDir, s);
			Debug("Program: Deleting file "+f.getAbsolutePath());
			f.deleteOnExit();
		}
	}

	public static void saveShowColumns(String value) {
		putCaveConfigString("SHOWFILE_COLUMN", value);
	}

	public static String getShowColumns() {
		return getCaveConfigString("SHOWFILE_COLUMN", "");
	}

	public static void saveHTMLColumns(ArrayList<MyCellarFields> cols) {
		String s = "";
		for(MyCellarFields f : cols) {
			if(!s.isEmpty())
				s += ";";
			s += f.name();
		}
		putCaveConfigString("HTMLEXPORT_COLUMN", s);
	}

	public static ArrayList<MyCellarFields> getHTMLColumns() {
	ArrayList<MyCellarFields> cols = new ArrayList<MyCellarFields>();
		String s = getCaveConfigString("HTMLEXPORT_COLUMN", "");
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

	public static boolean isSelectedTab(ITabListener tab) {
		if(tabbedPane.getSelectedComponent() == null)
			return false;
		return tabbedPane.getSelectedComponent().equals(tab);
	}

	public static String readFirstLineText(File f) {
		if(f == null)
			return "";
		if(!f.getName().toLowerCase().endsWith(".txt"))
			return "";
		try {
			FileReader reader = new FileReader(f);
			BufferedReader buffer = new BufferedReader(reader);
			String line = buffer.readLine();
			buffer.close();
			return line.trim();
		} catch (IOException e) {
			showException(e, true);
		}
		return "";
	}
}
