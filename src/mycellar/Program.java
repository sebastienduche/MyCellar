package mycellar;

import mycellar.actions.OpenShowErrorsAction;
import mycellar.core.MyCellarError;
import mycellar.core.MyCellarFields;
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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
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
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.OptionalDouble;
import java.util.Properties;
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
 * @version 17.5
 * @since 16/03/18
 */

public class Program {

	private static final Properties propCave = new Properties();
	private static final Properties propGlobal = new Properties();
	private static String inputPropCave = null;
	private static String inputPropGlobal = null;
	public static Font font_panel = new Font("Arial", Font.PLAIN, 12);
	public static Font font_boutton_small = new Font("Arial", Font.PLAIN, 10);
	public static Font font_label_bold = new Font("Arial", Font.BOLD, 12);
	public static Font font_dialog = new Font("Dialog", Font.BOLD, 16);
	public static Font font_dialog_small = new Font("Dialog", Font.BOLD, 12);
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
	public static PanelInfos panelInfos = null;
	public static AddVin addWine = null;
	public static JTabbedPane tabbedPane = new JTabbedPane();
	public static String archive = null;
	public static LinkedList<String> half = new LinkedList<>();
	public static String defaut_half = null;
	private static final MyLinkedHashMap configGlobal = new MyLinkedHashMap();
	private static MyLinkedHashMap configCave = null;
	private static FileWriter oDebugFile = null;
	private static File debugFile = null;
	private static boolean bDebug = false;
	private static LinkedList<Rangement> m_oCave = new LinkedList<>();
	private static final LinkedList<Bouteille> TRASH = new LinkedList<>();
	private static final LinkedList<MyCellarError> ERRORS = new LinkedList<>();
	public static Rangement defaultPlace = new Rangement("");
	private static String m_sWorkDir = null;
	private static String m_sGlobalDir = null;
	private static boolean m_bIsTrueFile = false;
	private static final String DATA_XML = "data.xml";
	private static final String UNTITLED1_SINFO = "Untitled1.sinfo";
	private static final String PREVIEW_XML = "preview.xml";
	private static final String PREVIEW_HTML = "preview.html";
	private static final String MY_CELLAR_XML = "MyCellar.xml";
	private static final String TYPES_XML = "Types.xml";
	private static final String BOUTEILLES_XML = "Bouteilles.xml";
	private static final String INTERNAL_VRSION = "2.4";
	private static boolean m_bWorkDirCalculated = false;
	private static boolean m_bGlobalDirCalculated = false;
	private static boolean bYearControlCalculated = false;
	private static boolean bYearControled = false;
	public static Creer_Rangement createPlace;
	public static Creer_Rangement modifyPlace;
	public static CellarOrganizerPanel managePlace;
	public static CellarOrganizerPanel chooseCell;
	public static Supprimer_Rangement deletePlace;
	public static Stat stat;
	public static Country france = new Country("FRA", "France");
	private static final List<File> DIR_TO_DELETE = new LinkedList<>();
	private static boolean modified = false;
	private static boolean listCaveModified = false;
	private static int nextID = -1;

	/**
	 * init
	 */
	public static void init() {

		try {
			archive = "";
			bDebug = true;
			Debug("===================================================");
			// Initialisation du répertoire de travail
			getWorkDir(false);
			Debug("Program: Temp Dir: " + getWorkDir(false));
			Debug("Program: Initializing Configuration files...");
			inputPropGlobal = getGlobalDir() + "config.ini";
			File fileIni = new File(inputPropGlobal);
			if(!fileIni.exists()) {
				fileIni.createNewFile();
			}
			FileInputStream inputStream = new FileInputStream(fileIni); 
			propGlobal.load(inputStream);
			inputStream.close();
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
			File f = new File(getWorkDir(true) + DATA_XML);
			if(!f.exists())
				f.createNewFile();
			LanguageFileLoader.loadLanguageFiles( "U" );

			//Initialisation de la Map contenant config
			Debug("Program: Initialize ConfigGlobal");
			Enumeration<Object> keys = propGlobal.keys();
			while (keys.hasMoreElements()) {
				String key = keys.nextElement().toString();
				putGlobalConfigString(key, propGlobal.getProperty(key));
			}

			if(hasConfigCaveKey("PRICE_SEPARATOR")) {
				getCaveConfig().remove("PPRICE_SEPARATOR");
			}

			verifyConfigFile();
			cleanAndUpgrade();
		}
		catch (Exception e) {
			showException(e);
		}
	}

	/**
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	private static void loadProperties() throws IOException {

		inputPropCave = getWorkDir(true) + "config.ini";
		File f = new File(inputPropCave);
		if( !f.exists() )
			f.createNewFile();
		configCave = new MyLinkedHashMap();
		FileInputStream inputStream = new FileInputStream(inputPropCave);
		propCave.load(inputStream);
		inputStream.close();

		Enumeration<Object> keys = propCave.keys();
		while (keys.hasMoreElements()) {
			String key = keys.nextElement().toString();
			putCaveConfigString(key, propCave.getProperty(key));
		}

		f = new File(getWorkDir(true) + "search.ini");
		if(f.exists())
			FileUtils.deleteQuietly(f);

		half = MyXmlDom.readTypesXml();
		if(half == null) {
			half = new LinkedList<>();
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
	private static void cleanAndUpgrade() {
		String sVersion = getCaveConfigString("VERSION", "");
		if(sVersion.isEmpty()) {
			putCaveConfigString("VERSION", INTERNAL_VRSION);
			return;
		}
		int n1 = Integer.parseInt(sVersion.substring(0,1));
		int n2 = Integer.parseInt(sVersion.substring(2,3));
		int val = n1*10 + n2;
		// Affichage du nombre avec 2 décimales.
		if ( val < 24 ) {
			Debug("Program: WARNING: Destroying old files");
			File years = new File(getWorkDir(true) + "Years.xml");
			if(years.exists())
				FileUtils.deleteQuietly(years);
			File f1 = new File( getWorkDir(true) + "static_col.sinfo");
			FileUtils.deleteQuietly(f1);
			
			putCaveConfigString("VERSION", INTERNAL_VRSION);
		}
	}


	/**
	 * setLanguage
	 * @param lang String
	 * @return boolean
	 */
	public static boolean setLanguage(String lang) {
		Debug("Program: Set Language : "+lang);
		tabbedPane.removeAll();
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
		boolean load = LanguageFileLoader.loadLanguageFiles( lang );
		if(panelInfos != null) {
			panelInfos.setLabels();
			Start.updateMainPanel();
		}
		return load;
	}

	/**
	 * verifyConfigFile
	 */
	public static void verifyConfigFile() {

		Debug("Program: Verifying INI file...");
		if (!propGlobal.containsKey("LANGUAGE")) {
			propGlobal.setProperty("LANGUAGE", "F");
		}
		else if (propGlobal.get("LANGUAGE").equals("")) {
			propGlobal.setProperty("LANGUAGE", "F");
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
		StackTraceElement st[] =  e.getStackTrace();
		String error = "";
		for (StackTraceElement s : st) {
			error = error.concat("\n" + s);
		}
		if(error.contains("javax.swing.plaf.synth.SynthContext.getPainter(SynthContext.java:171)")
				|| error.contains("javax.swing.LayoutComparator.compare"))
			_bShowWindowErrorAndExit = false;
		if (_bShowWindowErrorAndExit)
			JOptionPane.showMessageDialog(null, e.toString(), "Error", JOptionPane.ERROR_MESSAGE);

		try (FileWriter fw = new FileWriter(getGlobalDir()+"Errors.log")){
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

	private static void sendMail(String error, File filename) {
		InputStreamReader stream = new InputStreamReader(Program.class.getClassLoader().getResourceAsStream("resources/MyCellar.dat"));


		try (BufferedReader reader = new BufferedReader(stream)) {
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

	public static void addError(MyCellarError error) {
		ERRORS.add(error);
	}

	/**
	 * deletePlaceFile: Suppression d'un objet sérialisé.
	 *
	 * @param num_rangement int: numéro du rangement à supprimer
	 */
	@Deprecated
	private static void deletePlaceFile(int num_rangement) {
		Debug("Program: Deleting serialized object...");
		try {
			File f = new File( getWorkDir(false) );
			String list[] = f.list(new MyFilenameFilter());
			if( list != null && list.length > num_rangement ) {
				f = new File( getWorkDir(true) + list[num_rangement]);
				f.delete();
			}
		}
		catch (Exception exc) {
			showException(exc);
		}
	}

	/**
	 * write_XSL: Ecriture du fichier XSL
	 */
	public static void write_XSL() {

		Debug("Program: Writing XSL...");
		String tmp;

		File f = new File("resources/vin.xsl");
		try (FileWriter ficout = new FileWriter(f)){
			ficout.flush();
			tmp = "<?xml version='1.0'?>\n<xsl:stylesheet xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\" version=\"1.0\"> <xsl:template match=\"/\">\n";
			ficout.write(tmp);
			ficout.flush();
			tmp = "<html>\n<body>\n<table border=\"1\" cellspacing=\"0\" cellpadding=\"3\">\n<tr bgcolor=\"#FFFF00\">\n";
			ficout.write(tmp);
			ficout.flush();
			tmp = "<td>" + Program.convertToHTMLString(Program.getLabel("Infos208")) + "</td>\n<td>" + Program.convertToHTMLString(Program.getLabel("Infos189")) + "</td>\n";
			ficout.write(tmp);
			ficout.flush();
			tmp = "<td>" + Program.convertToHTMLString(Program.getLabel("Infos134")) + "</td>\n<td>" + Program.convertToHTMLString(Program.getLabel("Infos105")) + "</td>\n";
			ficout.write(tmp);
			ficout.flush();
			tmp = "<td>" + Program.convertToHTMLString(Program.getLabel("Infos158")) + "</td>\n<td>" + Program.convertToHTMLString(Program.getLabel("Infos028")) + "</td>\n";
			ficout.write(tmp);
			ficout.flush();
			tmp = "<td>" + Program.convertToHTMLString(Program.getLabel("Infos083")) + "</td>\n<td>" + Program.convertToHTMLString(Program.getLabel("Infos135")) + "</td>\n";
			ficout.write(tmp);
			ficout.flush();
			tmp = "<td>" + Program.convertToHTMLString(Program.getLabel("Infos137")) + "</td>\n";
			ficout.write(tmp);
			ficout.flush();
			tmp = "</tr>\n<xsl:for-each select=\"cellar/name\">\n<xsl:sort select=\"name\"/>\n<tr>\n";
			ficout.write(tmp);
			ficout.flush();
			tmp = "<td><xsl:value-of select=\"name\"/></td>";
			ficout.write(tmp);
			ficout.flush();
			tmp = "<td><xsl:value-of select=\"year\"/></td>";
			ficout.write(tmp);
			ficout.flush();
			tmp = "<td><xsl:value-of select=\"half\"/></td>";
			ficout.write(tmp);
			ficout.flush();
			tmp = "<td><xsl:value-of select=\"place\"/></td>";
			ficout.write(tmp);
			ficout.flush();
			tmp = "<td><xsl:value-of select=\"num-place\"/></td>";
			ficout.write(tmp);
			ficout.flush();
			tmp = "<td><xsl:value-of select=\"line\"/></td>";
			ficout.write(tmp);
			ficout.flush();
			tmp = "<td><xsl:value-of select=\"column\"/></td>";
			ficout.write(tmp);
			ficout.flush();
			tmp = "<td><xsl:value-of select=\"price\"/></td>";
			ficout.write(tmp);
			ficout.flush();
			tmp = "<td><xsl:value-of select=\"comment\"/></td>";
			ficout.write(tmp);
			ficout.flush();
			tmp = "<td><xsl:value-of select=\"dateOfC\"/></td>";
			ficout.write(tmp);
			ficout.flush();
			tmp = "<td><xsl:value-of select=\"parker\"/></td>";
			ficout.write(tmp);
			ficout.flush();
			tmp = "<td><xsl:value-of select=\"appellation\"/></td>";
			ficout.write(tmp);
			ficout.flush();
			tmp = "</tr>\n</xsl:for-each>\n</table>\n</body>\n</html>\n</xsl:template>\n</xsl:stylesheet>";
			ficout.write(tmp);
			ficout.flush();
		}
		catch (IOException ex) {
			showException(ex, false);
		}

	}

	public static String convertToHTMLString(String s) {
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
	 * @return
	 */
	public static boolean loadObjects() {
		if(!ListeBouteille.loadXML()) {
			read_Object();
			getStorage().setListBouteilles(getStorage().getAllList());
		}
		else {
			m_oCave = MyXmlDom.readMyCellarXml("");
			getStorage().loadHistory();
		}

		if(m_oCave == null) {
			m_oCave = new LinkedList<>();
			m_oCave.add(defaultPlace);
			return false;
		}
		return true;
	}

	/**
	 * read_Object: Fonction de lecture des objets Rangement sérialiser.
	 */
	@Deprecated
	private static void read_Object() {
		Debug("Program: Loading places and history...");
		LinkedList<Rangement> cave = new LinkedList<>();
		boolean resul = getStorage().readRangement(cave);
		getStorage().loadHistory();
		if (!resul) {
			Debug("Program: WARNING: Loading Unsuccessful");
			getStorage().setAll(null);
		}
		else
			m_oCave = cave;
		Debug("Program: Loading places and history OK");
	}
	
	public static int getMaxPrice() {
		
		OptionalDouble i = getStorage().getAllList().stream().mapToDouble(Bouteille::getPriceDouble).max();
		if(i.isPresent())
			return (int) i.getAsDouble();
		return  0;
	}
	
	public static int getCellarValue() {	
		return (int) getStorage().getAllList().stream().mapToDouble(Bouteille::getPriceDouble).sum();
	}
	
	/**
	 * getNbBouteilleAnnee: retourne le nombre de bouteilles d'une année
	 *
	 * @param an int: année souhaitée
	 * @return int
	 */
	public static int getNbBouteilleAnnee(int an) {
		return (int)getStorage().getAllList().stream().filter(bouteille -> bouteille.getAnneeInt() == an).count();
	}
	
	public static int[] getAnnees() {
		return getStorage().getAllList().stream().mapToInt(Bouteille::getAnneeInt).distinct().toArray();
	}
	
	/**
	 * getNbAutreAnnee
	 * @return int
	 */
	public static int getNbAutreAnnee() {
		return (int)getStorage().getAllList().stream().filter(bouteille -> bouteille.getAnneeInt() < 1000).count();
	}

	/**
	 * getNbNonVintage
	 * @return int
	 */
	public static int getNbNonVintage() {
		return (int)getStorage().getAllList().stream().filter(bouteille -> Bouteille.isNonVintageYear(bouteille.getAnnee())).count();
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
			catch (IOException ignored) {
			}
		}
		else {
			Erreur.showSimpleErreur(getError("Error162"));
		}
	}

	/**
	 * zipDir: Compression de répertoire
	 *
	 * @param archive String
	 * @return boolean
	 */
	private static void zipDir(String archive) {

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
			LinkedList<String> zipEntryList = new LinkedList<>();
			// pour chacun des fichiers de la liste
			if (files != null) {
				for (String file : files) {
					f = new File(getWorkDir(true) + file);
					if (f.isDirectory() || file.compareTo(UNTITLED1_SINFO) == 0)
						continue;
					// création d'un flux de lecture
					FileInputStream fi = new FileInputStream(getWorkDir(true) + file);
					// création d'un tampon de lecture sur ce flux
					BufferedInputStream buffi = new BufferedInputStream(fi, BUFFER);
					// création d'en entrée Zip pour ce fichier
					String name = removeAccents(file);
					ZipEntry entry = new ZipEntry(name);
					if (zipEntryList.contains(name))
						continue;
					zipEntryList.add(name);
					// ajout de cette entrée dans le flux d'écriture de l'archive Zip
					out.putNextEntry(entry);
					// écriture du fichier par paquet de BUFFER octets dans le flux d'écriture
					int count;
					while ((count = buffi.read(data, 0, BUFFER)) != -1) {
						out.write(data, 0, count);
					}
					// Close the current entry
					out.closeEntry();
					// fermeture du flux de lecture
					buffi.close();
					fi.close();
				}
			}
			// fermeture du flux d'écriture
			out.close();
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
	 * @param archive String
	 * @return boolean
	 */
	private static boolean unzipDir(String dest_dir, String archive) {
		try {
			Debug("Program: Unzip: Archive "+archive );
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
				FileOutputStream fos = new FileOutputStream(dest_dir + File.separator + entry.getName());
				Debug( "Unzip: File "+dest_dir + File.separator + entry.getName() );
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
				fos.close();
			}
			// fermeture archive
			zis.close();
			buffi.close();
			fis.close();
		}
		catch (Exception e) {
			e.printStackTrace();
			Debug("Program: Unzip: Archive Error");
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
		saveAs("");
	}

	/**
	 * saveAs
	 * @param sFilename String
	 */
	public static void saveAs(String sFilename) {
		Debug("Program: Saving all files...");

		saveProperties();
		saveGlobalProperties();

		if(isListCaveModified())
			MyXmlDom.writeMyCellarXml(getCave(),"");

		getStorage().saveHistory();
		CountryVignobles.save();
		ListeBouteille.writeXML();

		if(!sFilename.isEmpty())
			zipDir(sFilename);
		else
			zipDir(archive);

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
			oDebugFile.write("[" + Calendar.getInstance().getTime().toString() + "]: " + sText + "\n");
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
	 * @param name String
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
	 * @param name String
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
	 * addCave
	 *
	 * @param rangement Rangement
	 */
	public static void addCave(Rangement rangement) {
		if(rangement == null)
			return;
		m_oCave.add(rangement);
		setListCaveModified();
		setModified();
		Debug("Program: Sorting places...");
		Collections.sort(m_oCave);
	}

	/**
	 * removeCave
	 *
	 * @param rangement Rangement
	 */
	public static void removeCave(Rangement rangement) {
		if(rangement == null)
			return;
		int num = m_oCave.indexOf(rangement);
		m_oCave.remove(rangement);
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
	 */
	static boolean newFile() {
		return openaFile(null);
	}

	/**
	 * openaFile: Ouvre un fichier
	 *
	 * @param f File
	 */
	public static boolean openaFile(File f) {
		if(f != null)
			Debug("Program: openFile: Opening file: "+f.getAbsolutePath());
		else
			Debug("Program: openFile: Creating new file");

		LinkedList<String> list = new LinkedList<String>();
		list.addLast(getGlobalConfigString("LAST_OPEN1",""));
		list.addLast(getGlobalConfigString("LAST_OPEN2",""));
		list.addLast(getGlobalConfigString("LAST_OPEN3",""));
		list.addLast(getGlobalConfigString("LAST_OPEN4",""));
		if(f != null && list.contains(f.getAbsolutePath()))
			list.remove(f.getAbsolutePath());

		// Sauvegarde avant de charger le nouveau fichier
		closeFile();
		
		CountryVignobles.init();
		Countries.init();

		if(f == null) {
			setFileSavable(false);
			// Nouveau fichier
			String fic = getWorkDir(true) + UNTITLED1_SINFO;
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
			Erreur.showSimpleErreur(MessageFormat.format(getError("Error020"), f.getAbsolutePath())); //Fichier non trouvé);

			putGlobalConfigString("LAST_OPEN1", list.pop());
			putGlobalConfigString("LAST_OPEN2", list.pop());
			putGlobalConfigString("LAST_OPEN3", list.pop());
			// On a déjà enlevé un élément de la liste
			putGlobalConfigString("LAST_OPEN4", "");
			saveGlobalProperties();
			return false;
		}

		archive = f.getAbsolutePath();

		try {
			// Dézippage
			boolean bUnzipSucceeded = unzipDir(getWorkDir(false), archive);
			if ( bUnzipSucceeded )
				Debug("Program: Unzipping "+archive+" to "+getWorkDir(false) +" OK");
			else {
				Debug("Program: Unzipping "+archive+" to "+getWorkDir(false) + " KO");
				Program.archive = "";
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
		if(!data.exists())
			Debug("Program: ERROR: Unable to find file data.xml!!");

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

			if (i != GetCaveLength())
				loaded = false;

			Debug("Program: Place Count: Program="+GetCaveLength()+" cave="+i);
		}
		// En cas d'erreur
		if (!loaded)
			Debug("Program: ERROR: Loading");

		//Chargement des rangement par le fichier d'options si la relecture des objets sérialisés a échouée
		if (GetCaveLength() == 0) {
			Debug("Program: Reading places from file");
			LinkedList<Rangement> cave = MyXmlDom.readMyCellarXml("");
			if(cave != null) {
				m_oCave = cave;
			}
		}

		try {
			loadProperties();
		} catch (IOException e) {
			showException(e,false);
			return false;
		}

		RangementUtils.putTabStock();
		if(!getErrors().isEmpty())
			new OpenShowErrorsAction().actionPerformed(null);
		CountryVignobles.load();
		CountryVignobles.addVignobleFromBottles();

		putGlobalConfigString("STARTUP", "1");
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
		return true;
	}

	/**
	 * Fonction pour sauvegarder les propriétés globales du programme
	 */
	public static void saveGlobalProperties() {
		Debug("Program: Saving Global Properties");
		Object[] val = configGlobal.keySet().toArray();
		for (Object o : val) {
			String key = o.toString();
			propGlobal.put(key, configGlobal.getString(key));
		}
		FileOutputStream outputStream = null;
		try {
			outputStream = new FileOutputStream(inputPropGlobal);
			propGlobal.store(outputStream, null);
		} catch (FileNotFoundException e) {
			showException(e);
		} catch (IOException e) {
			showException(e);
		} finally {
			if(outputStream != null)
				try {
					outputStream.close();
				} catch (IOException e) {}
		}
		Debug("Program: Saving Global Properties OK");
	}


	/**
	 * closeFile: Fermeture du fichier.
	 *
	 */
	static void closeFile() {

		Debug("Program: closeFile: Closing file...");
		try {
			boolean bSave = false;
			if(!archive.isEmpty() && isModified()) {
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

								archive = fic;
							}
						}
						catch (Exception e3) {
							showException(e3);
						}
					}
				}

				putCaveConfigString("ANNEE_AUTO", "0");
				putCaveConfigString("FILE_SRC", archive);
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
			if (bSave) {
				if(!ListeBouteille.writeXML())
					return;

				if(isListCaveModified())
					MyXmlDom.writeMyCellarXml(getCave(),"");

				saveProperties();

				if (!getCave().isEmpty()) {						
					getStorage().saveHistory();
					CountryVignobles.save();
					zipDir(archive);
				}
			}
			
			if(!archive.isEmpty()) {
				// Sauvegarde des propriétés globales
				saveGlobalProperties();
	
				if (getCaveConfigInt("FIC_EXCEL", 0) == 1) {
					//Ecriture Excel
					RangementUtils.write_XLS(getCaveConfigString("FILE_EXCEL",""), getStorage().getAllList(), true);
				}
			}
		}
		catch (Exception ex) {
			showException(ex);
			return;
		}
		tabbedPane.removeAll();
		if(!archive.isEmpty()){
			getStorage().close();
			CountryVignobles.close();
			Countries.close();
			Search.clearResults();
		}
		m_bWorkDirCalculated = false;
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
		TRASH.clear();
		setFileSavable(false);
		modified = false;
		listCaveModified = false;
		if(getCave() != null)
			getCave().clear();
		Debug("Program: closeFile: Closing file Ended");
	}

	public static void deleteTempFiles() {
		for(File f : DIR_TO_DELETE) {
			if(!f.exists() || f.getName().equalsIgnoreCase("Global"))
				continue;
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
	 * Save Properties
	 */
	private static void saveProperties() {

		MyXmlDom.writeTypeXml(half);

		if(inputPropCave != null)
		{
			Object[] val = configCave.keySet().toArray();
			for (Object o : val) {
				String key = o.toString();
				propCave.put(key, configCave.getString(key));
			}
			FileOutputStream outputStream = null;
			try {
				outputStream = new FileOutputStream(inputPropCave);
				propCave.store(outputStream, null);
			} catch (FileNotFoundException e) {
				showException(e);
			} catch (IOException e) {
				showException(e);
			} finally {
				if(outputStream != null)
					try {
						outputStream.close();
					} catch (IOException e) {}
			}
			inputPropCave = null;
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
		if( sDir.isEmpty() )
			m_sGlobalDir = "./Object/Global";
		else
			m_sGlobalDir = sDir + "/MyCellar/Global";
		File f_obj = new File( m_sGlobalDir );
		if(!f_obj.exists())
			f_obj.mkdir();

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
		if( m_bWorkDirCalculated ) {
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

		Calendar g = GregorianCalendar.getInstance();
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
		DIR_TO_DELETE.add(new File(m_sWorkDir));

		if (_bWithEndSlash)
			return m_sWorkDir + File.separator;
		return m_sWorkDir;
	}

	public static String getShortFilename() {
		return getShortFilename(archive);
	}

	public static String getShortFilename(String sFilename) {
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

	public static String getGlobalConfigString( String _sKey, String _sDefaultValue ) {
		return configGlobal.getString(_sKey, _sDefaultValue);
	}

	public static String getCaveConfigString( String _sKey, String _sDefaultValue ) {
		if( null != configCave )
			return configCave.getString(_sKey, _sDefaultValue);
		Debug("Program: ERROR: Calling null configCave for key '"+_sKey+"' and default value '"+_sDefaultValue+"'");
		return _sDefaultValue;
	}

	public static int getGlobalConfigInt( String _sKey, int _nDefaultValue ) {
		return configGlobal.getInt(_sKey, _nDefaultValue);
	}

	public static int getCaveConfigInt( String _sKey, int _nDefaultValue ) {
		if( null != configCave )
			return configCave.getInt(_sKey, _nDefaultValue);
		Debug("Program: ERROR: Calling null configCave for key '"+_sKey+"' and default value '"+_nDefaultValue+"'");
		return _nDefaultValue;
	}

	public static void putGlobalConfigString( String _sKey, String _sValue ) {
		configGlobal.put(_sKey, _sValue);
	}

	public static void putCaveConfigString( String _sKey, String _sValue ) {
		if( null != configCave )
			configCave.put(_sKey, _sValue);
		else
			Debug("Program: ERROR: Unable to put value in configCave: [" + _sKey + " - " + _sValue + "]");
	}

	public static void putGlobalConfigInt( String _sKey, Integer _sValue ) {
		configGlobal.put(_sKey, _sValue);
	}

	public static void putCaveConfigInt( String _sKey, Integer _sValue ) {
		configCave.put(_sKey, _sValue);
	}

	/*private static void removeGlobalConfigString( String _sKey )
	{
		if( configGlobal.containsKey(_sKey))
			configGlobal.remove(_sKey);
	}*/

	public static MyLinkedHashMap getCaveConfig() {
		return configCave;
	}

	public static boolean hasConfigCaveKey( String _sKey ) {
		if( null != configCave )
			return configCave.containsKey(_sKey);
		return false;
	}

	public static boolean hasConfigGlobalKey( String _sKey ) {
		return configGlobal.containsKey(_sKey);
	}

	public static boolean isFileSavable() {
		return m_bIsTrueFile;
	}

	public static void setFileSavable(boolean _bIsTrueFile) {
		m_bIsTrueFile = _bIsTrueFile;
	}

	private static String getDataFileName() {
		return getWorkDir(true) + DATA_XML;
	}

	public static String getXMLPlacesFileName() {
		return getWorkDir(true) + MY_CELLAR_XML;
	}

	public static String getXMLTypesFileName() {
		return getWorkDir(true) + TYPES_XML;
	}

	public static String getXMLBottlesFileName() {
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

	public static String getLanguage(String _id) {
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
		if (bYearControlCalculated)
			return bYearControled;
		bYearControled = (getCaveConfigInt("ANNEE_CTRL", 0) == 1);
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
		if (showfile != null)
			showfile.setUpdateView();
		if (showerrors != null)
			showerrors.setUpdateView();
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
				if(tabbedPane.getTabComponentAt(i) != null && tabbedPane.getIconAt(i) != null && tabbedPane.getIconAt(i).equals(image))
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

	private static boolean isListCaveModified() {
		return listCaveModified;
	}

	public static PDFProperties getPDFProperties() {
		String title = getCaveConfigString("PDF_TITLE", "");
		int titleSize = getCaveConfigInt("TITLE_SIZE", 10);
		int textSize = getCaveConfigInt("TEXT_SIZE", 10);
		String border = getCaveConfigString("BORDER", "ON");
		boolean boldTitle = "bold".equals(getCaveConfigString("BOLD", ""));

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

	public static List<PDFRow> getPDFRows(List<Bouteille> list, PDFProperties properties) {
		LinkedList<PDFRow> rows = new LinkedList<>();
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
		String files[] = f.list((dir, name) -> {
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
		});

		if (files != null) {
			for (String s : files) {
				f = new File(sDir, s);
				Debug("Program: Deleting file " + f.getAbsolutePath());
				f.deleteOnExit();
			}
		}
	}

	public static void saveShowColumns(String value) {
		putCaveConfigString("SHOWFILE_COLUMN", value);
	}

	public static String getShowColumns() {
		return getCaveConfigString("SHOWFILE_COLUMN", "");
	}

	public static void saveHTMLColumns(List<MyCellarFields> cols) {
		StringBuilder s = new StringBuilder();
		for(MyCellarFields f : cols) {
			if(s.length() != 0)
				s.append(";");
			s.append(f.name());
		}
		putCaveConfigString("HTMLEXPORT_COLUMN", s.toString());
	}

	public static ArrayList<MyCellarFields> getHTMLColumns() {
	ArrayList<MyCellarFields> cols = new ArrayList<>();
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
		try (BufferedReader buffer = new BufferedReader(new FileReader(f))){
			String line = buffer.readLine();
			buffer.close();
			return line.trim();
		} catch (IOException e) {
			showException(e, true);
		}
		return "";
	}
	
	public static HistoryList getHistoryList() {
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
		  if (c == ',') {
			  buf.append('.');
		  }
		  if (Character.isDigit(c)) {
			  buf.append(c);
		  }
	  }
		return new BigDecimal(buf.toString()).setScale(2, BigDecimal.ROUND_HALF_UP);
  }

	public static int getNewID() {
		if (nextID == -1) {
			nextID = getStorage().getAllNblign();
		}
		do {
			++nextID;
		} while (getStorage().getAllList().stream().anyMatch(bouteille -> bouteille.getId() == nextID));
		return nextID;
	}
}
