package mycellar.launcher;

import mycellar.core.MyCellarVersion;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * 
 * Titre : Cave à vin
 * Description : Votre description
 * Copyright : Copyright (c) 2011
 * Société : Seb Informatique
 * @author Sébastien Duché
 * @version 1.8
 * @since 14/03/18
 */

public class Server implements Runnable {

	private String sServerVersion = "";
	private String sAvailableVersion = "";

	private static final String GETVERSION = "GETVERSION";
	private static final String DOWNLOAD = "DOWNLOAD";

	private String sAction = "";

	private static final LinkedList<FileType> listFile = new LinkedList<>();
	private static final LinkedList<String> listFileToRemove = new LinkedList<>();

	private boolean bDownloaded = false;
	private boolean bDownloadError = false;
	private boolean bExit = false;

	private static FileWriter oDebugFile = null;

	private static final Server INSTANCE = new Server();

	private static final String GIT_HUB_URL = "https://github.com/sebastienduche/MyCellar/raw/master/Build/";

	private Server() {}

	public static Server getInstance() {
		return INSTANCE;
	}

	@Override
	public void run() {

		if (sAction.equals(GETVERSION)) {
			sServerVersion = "";
			try {
				File myCellarVersion = File.createTempFile("MyCellarVersion", "txt");
				downloadFileFromGitHub("MyCellarVersion.txt", myCellarVersion.getAbsolutePath());
				BufferedReader in = new BufferedReader(new FileReader(myCellarVersion));

				sServerVersion = in.readLine();
				sAvailableVersion = in.readLine();
				String sFile = in.readLine();
				while (sFile != null && !sFile.isEmpty()) {
					int index = sFile.indexOf('@');
					String md5 = "";
					if(index != -1) {
						md5 = sFile.substring(index+1).trim();
						sFile = sFile.substring(0, index);
					}
					// Suppression de fichier commençant par -
					if(sFile.indexOf('-') == 0)
						listFileToRemove.add(sFile.substring(1));
					else
						listFile.add(new FileType(sFile, md5));
					sFile = in.readLine();
				}

				in.close();
			} catch (Exception e) {
				showException(e);
			}
			sAction = "";
		} else if (sAction.equals(DOWNLOAD)) {
			try {
				File f = new File("download");
				if (!f.exists())
					f.mkdir();

				bDownloadError = downloadFromGitHub(f);
			} catch (Exception e) {
				showException(e);
				sAction = "";
				bDownloadError = true;
				return;
			}
			bDownloaded = true;
			sAction = "";
		}
		if (bDownloadError) {
			File f = new File("download");
			f.deleteOnExit();
		}
		if (bExit)
			System.exit(0);
	}

	public void checkVersion() {
		Debug("Checking Version from GitHub...");
		sServerVersion = "";
		try {
			File myCellarVersion = File.createTempFile("MyCellarVersion", "txt");
			downloadFileFromGitHub("MyCellarVersion.txt", myCellarVersion.getAbsolutePath());
			BufferedReader in = new BufferedReader(new FileReader(myCellarVersion));

			sServerVersion = in.readLine();
			sAvailableVersion = in.readLine();
			String sFile = in.readLine();
			while (sFile != null && !sFile.isEmpty()) {
				int index = sFile.indexOf('@');
				String md5 = "";
				if(index != -1) {
					md5 = sFile.substring(index+1).trim();
					sFile = sFile.substring(0, index);
				}
				// Suppression de fichier commençant par -
				Debug("sFile... "+sFile+" "+sFile.indexOf('-'));
				if(sFile.indexOf('-') == 0)
					listFileToRemove.add(sFile.substring(1));
				else {
					boolean lib = (!sFile.contains("MyCellar") && sFile.endsWith(".jar"));
					listFile.add(new FileType(sFile, md5, lib));
				}
				sFile = in.readLine();
			}

			in.close();
			Debug("GitHub version: "+sServerVersion+"/"+sAvailableVersion);
		} catch (Exception e) {
			showException(e);
		}
	}

	public void downloadVersion() {
		Debug("Downloading version from GitHub...");
		try {
			File f = new File("download");
			if (!f.exists())
				f.mkdir();

			bDownloadError = downloadFromGitHub(f);

		} catch (Exception e) {
			showException(e);
			sAction = "";
			bDownloadError = true;
			return;
		}
		bDownloaded = true;
		sAction = "";
		if (bDownloadError) {
			File f = new File("download");
			f.deleteOnExit();
		}
	}

	public String getAvailableVersion() {
		return sAvailableVersion;
	}

	public String getServerVersion() {

		if (sServerVersion.isEmpty()) {
			try {
				sAction = GETVERSION;
				Thread t = new Thread(this);
				t.start();
			} catch (Exception a) {
				showException(a);
			}
		}

		return sServerVersion;
	}

	public boolean hasAvailableUpdate() {
		if (sServerVersion.isEmpty())
			return false;

		return (sServerVersion.compareTo(MyCellarVersion.getLocalVersion()) > 0);
	}

	public boolean isDownloadCompleted() {
		return bDownloaded;
	}

	public boolean isDownloadError() {
		return bDownloadError;
	}

	private boolean download() {
		if (sAction.equals(DOWNLOAD)) {
			return false;
		}
		try {
			sAction = DOWNLOAD;
			Thread a = new Thread(this);
			a.start();
		} catch (Exception a) {
			showException(a);
			bDownloadError = true;
			return false;
		}
		return true;
	}

	private boolean downloadFromGitHub(File destination) {
		MyCellarLauncherLoading download;
		try{
			download = new MyCellarLauncherLoading("Downloading...");
			download.setText("Downloading in progress...", "Downloading...");
			download.setVisible(true);
		}catch(Exception e) {
			e.printStackTrace();
			return false;
		}
		try{
			// Creation des fichiers pour lister les fichiers à supprimer
			for(String s : listFileToRemove) {
				Debug("Creating file to delete... "+s);
				File f = new File(destination, s + ".myCellar");
				f.createNewFile();
			}
			bDownloadError = false;
			Debug("Connecting to GitHub...");

			int size = listFile.size();
			int percent = 80;
			if (size > 0)
				percent = 80 / size;
			for (int i = 0; i < size; i++) {
				FileType fType = listFile.get(i);
				String name = fType.getFile();
				String serverMd5 = fType.getMd5();
				bDownloadError = false;
				System.out.println("Downloading... "+name);
				Debug("Downloading... "+name);
				{
					File f = new File(destination + "/" + name);

					download.setValue(20 + i * percent);
					try {
						String dir = "";
						if(fType.isForLibDirectory())
							dir = "lib/";
						downloadFileFromGitHub(dir+name, f.getAbsolutePath());
					} catch (IOException e) {
						e.printStackTrace();
						Debug("Error Downloading " + name);
						System.out.println("Error Downloading "+name);
						bDownloadError = true;
					}

					if(!serverMd5.isEmpty() && !f.isDirectory()) {
						InputStream stream = new FileInputStream(f);
						int fileSize;
						try {
							fileSize = stream.available();
							String localMd5 = getMD5Checksum(f.getAbsolutePath());
							if(localMd5.equals(serverMd5)) {
								Debug(name + " Md5 OK");
								System.out.println("MD5 OK "+name);
							}
							else {
								Debug(name + " "+serverMd5 + " "+localMd5);
								System.out.println("Error MD5 "+name);
								bDownloadError = true;
							}
						} finally {
							stream.close();
						}
						if (fileSize == 0)
							bDownloadError = true;
					}
					if(bDownloadError) {
						System.out.println("Error "+name);
						f.deleteOnExit();
					}
				}
			}
		} catch (IOException e) {
			Debug("Server IO Exception.");
			showException(e);
			download.dispose();
			bDownloadError = true;
		} catch (Exception e) {
			Debug("Exception.");
			showException(e);
			download.dispose();
			bDownloadError = true;
		} finally {
			download.setValue(100);
			download.dispose();
		}
		return bDownloadError;
	}

	public void downloadFileFromGitHub(String name, String destination) throws IOException {
		String link;
		URL url = new URL( GIT_HUB_URL + name );
		HttpURLConnection http = (HttpURLConnection)url.openConnection();
		Map< String, List< String >> header = http.getHeaderFields();
		while( isRedirected( header )) {
			link = header.get( "Location" ).get( 0 );
			url = new URL( link );
			http = (HttpURLConnection)url.openConnection();
			header = http.getHeaderFields();
		}
		InputStream input = http.getInputStream();
		byte[] buffer = new byte[4096];
		int n;
		OutputStream output = new FileOutputStream( new File( destination ));
		while ((n = input.read(buffer)) != -1) {
			output.write( buffer, 0, n );
		}
		output.close();
	}

	private static byte[] createChecksum(String filename) throws Exception {
		try(InputStream fis = new FileInputStream(filename)) {

			byte[] buffer = new byte[1024];
			MessageDigest complete = MessageDigest.getInstance("MD5");
			int numRead;
			do {
				numRead = fis.read(buffer);
				if (numRead > 0) {
					complete.update(buffer, 0, numRead);
				}
			} while (numRead != -1);
			fis.close();
			return complete.digest();
		}
	}

	// see this How-to for a faster way to convert
	// a byte array to a HEX string
	public static String getMD5Checksum(String filename) throws Exception {
		byte[] b = createChecksum(filename);
		String result = "";
		for (int i = 0; i < b.length; i++) {
			result += Integer.toString((b[i] & 0xff) + 0x100, 16).substring(1);
		}
		return result;
	}

	/**
	 * Debug
	 * 
	 * @param sText String
	 */
	public static void Debug(String sText) {

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
				oDebugFile = new FileWriter(new File(sDir, "DebugFtp-"+sDate+".log"), true);
			}
			oDebugFile.write("[" + java.util.Calendar.getInstance().getTime().toString() + "]: " + sText + "\n");
			oDebugFile.flush();
		}
		catch (Exception ignored) {}
	}

	/**
	 * showException
	 * @param e Exception
	 */
	public static void showException(Exception e) {
		StackTraceElement st[] = e.getStackTrace();
		String error = "";
		for (int z = 0; z < st.length; z++) {
			error = error.concat("\n" + st[z]);
		}
		String sDir = System.getProperty("user.home");
		if(!sDir.isEmpty()) {
			sDir += "/MyCellarDebug";
		}
		try(FileWriter fw = new FileWriter(sDir+"/Errors.log")) {
			fw.write(e.toString());
			fw.write(error);
			fw.flush();
			fw.close();
		}
		catch (IOException ignored) {}
		Debug("Server: ERROR:");
		Debug("Server: "+e.toString());
		Debug("Server: "+error);
		e.printStackTrace();
	}

	public boolean install() {
		Debug("Installing MyCellar...");
		File directory = getDirectoryForInstall();
		if(directory != null ) {
			if(!directory.exists())
				directory.mkdirs();
		}
		else
			return false;
		File f = new File("lib");
		f.mkdir();
		f = new File("config");
		f.mkdir();
		listFile.clear();
		listFile.add(new FileType("lib/commons-io-2.1.jar", ""));
		listFile.add(new FileType("lib/commons-lang-2.1.jar", ""));
		listFile.add(new FileType("lib/commons-logging.jar", ""));
		listFile.add(new FileType("lib/commons-net-3.0.1.jar", ""));
		listFile.add(new FileType("lib/jcommon-1.0.18.jar", ""));
		listFile.add(new FileType("lib/jdom1.0.jar", ""));
		listFile.add(new FileType("lib/jfreechart-1.0.15.jar", ""));
		listFile.add(new FileType("lib/jxl.jar", ""));
		listFile.add(new FileType("lib/mailapi.jar", ""));
		listFile.add(new FileType("lib/miglayout-4.0-swing.jar", ""));
		listFile.add(new FileType("lib/pdfbox-app-2.0.5.jar", ""));
		listFile.add(new FileType("lib/smtp.jar", ""));
		listFile.add(new FileType("config/config.ini", ""));

		listFile.add(new FileType("MyCellar.jar",""));
		listFile.add(new FileType("MyCellarLauncher.jar",""));
		listFile.add(new FileType("Finish.html",""));
		boolean result = downloadFromGitHub(directory);
		
		Debug("Installation of MyCellar Done.");
		return result;
	}
	
	private File getDirectoryForInstall() {
		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout());
		panel.setPreferredSize(new Dimension(400, 75));
		panel.add(new JLabel("Choose the directory to install MyCellar."));
		JTextField text = new JTextField(30);
		text.setSize(100, 25);
		panel.add(text);
		JButton button = new JButton("...");
		button.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser =  new JFileChooser();
				fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				if( JFileChooser.APPROVE_OPTION == fileChooser.showSaveDialog(null) ) {
					File selectedFile = fileChooser.getSelectedFile();
					text.setText(selectedFile.getAbsolutePath());
				}
			}
		});
		panel.add(button);
		if( JOptionPane.OK_OPTION == JOptionPane.showOptionDialog(null, panel, "MyCellar", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, null, null)) {
			return new File(text.getText());
		}
		return null;
	}

	private static boolean isRedirected(Map<String, List<String>> header) {
		if(header == null)
			return false;
		try{
			for( String hv : header.get( null )) {
				if(hv == null)
					return false;
				if( hv.contains( " 301 " ) || hv.contains( " 302 " ))
					return true;
			}
		}catch(Exception e) {}
		return false;
	}

	class FileType {

		private final String file;
		private final String md5;
		private final boolean lib;

		private FileType(String file, String md5, boolean lib) {
			this.file = file;
			this.md5 = md5;
			this.lib = lib;
		}

		private FileType(String file, String md5) {
			this.file = file;
			this.md5 = md5;
			lib = false;
		}

		public String getFile() {
			return file;
		}

		private String getMd5() {
			return md5;
		}

		private boolean isForLibDirectory() {
			return lib;
		}
	}
}
