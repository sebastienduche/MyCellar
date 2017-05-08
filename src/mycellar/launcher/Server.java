package mycellar.launcher;

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

import mycellar.core.MyCellarVersion;

/**
 * 
 * Titre : Cave à vin
 * Description : Votre description
 * Copyright : Copyright (c) 2011
 * Société : Seb Informatique
 * @author Sébastien Duché
 * @version 1.5
 * @since 07/05/17
 */

public class Server implements Runnable {

	private String sServerVersion = "";
	private String sAvailableVersion = "";

	private final static String GETVERSION = "GETVERSION";
	private final static String DOWNLOAD = "DOWNLOAD";

	private String sAction = "";

	private static LinkedList<FileType> listFile = new LinkedList<FileType>();
	private static LinkedList<String> listFileToRemove = new LinkedList<String>();

	private boolean bDownloaded = false;
	private boolean bDownloadError = false;
	private boolean bExit = false;

	private static FileWriter oDebugFile = null;
	private static File debugFile = null;

	private static final Server INSTANCE = new Server();

	private static final String gitHubUrl = "https://github.com/sebastienduche/MyCellar/raw/master/Build/";

	private Server() {}

	public static Server getInstance() {
		return INSTANCE;
	}

	@Override
	public void run() {

		if (sAction == GETVERSION) {
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
		} else if (sAction == DOWNLOAD) {
			try {
				File f = new File("download");
				if (!f.exists())
					f.mkdir();

				bDownloadError = downloadFromGitHub("download");
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
					boolean lib = (sFile.indexOf("MyCellar") == -1 && sFile.endsWith(".jar"));
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

			bDownloadError = downloadFromGitHub("download");

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

		return (sServerVersion.compareTo(MyCellarVersion.version) > 0);
	}

	public boolean isDownloadCompleted() {
		return bDownloaded;
	}

	public boolean isDownloadError() {
		return bDownloadError;
	}

	public boolean download() {
		if (sAction == DOWNLOAD) {
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

	public boolean downloadAndExit() {
		bExit = true;
		return download();
	}

	/*public boolean downloadFromDropbox(String destination) {
		MyCellarLauncherLoading download = null;
		try{
			download = new MyCellarLauncherLoading("Downloading...");
			download.setText("Downloading in progress...", "Downloading...");
			download.setVisible(true);
		}catch(Exception e) {
			e.printStackTrace();
		}
		try{
			// Creation des fichiers pour lister les fichiers à supprimer
			for(String s : listFileToRemove) {
				Debug("Creating file to delete... "+s);
				File f = new File(destination + "/" + s + ".myCellar");
				f.createNewFile();
			}
			bDownloadError = false;
			Debug("Connecting to Dropbox...");

			int size = listFile.size();
			int percent = 80;
			if (size > 0)
				percent = 80 / size;
			for (int i = 0; i < size; i++) {
				FileType fType = listFile.get(i);
				String name = fType.getFile();
				String serverMd5 = fType.getMd5();
				Debug("Downloading... "+name);
				{
					File f = new File(destination + "/" + name);

					download.setValue(20 + i * percent);
					try {
						if(fType.isFolder())
							dropbox.downloadFolderFromDropbox(name, destination);
						else
							dropbox.downloadFromDropbox(name, f);
					} catch (DbxException | IOException e) {
						Debug("Error Downloading " + name);
						bDownloadError = true;
					}

					if(!f.isDirectory()) {
						InputStream stream = new FileInputStream(f);
						int fileSize = -1;
						try {
							fileSize = stream.available();
							String localMd5 = getMD5Checksum(f.getAbsolutePath());
							if(!serverMd5.isEmpty()) {
								if(localMd5.equals(serverMd5))
									Debug(name + " Md5 OK");
								else {
									Debug(name + " "+serverMd5 + " "+localMd5);
									bDownloadError = true;
								}
							}
						} finally {
							stream.close();
						}
						if (fileSize == 0)
							bDownloadError = true;

					}
					if(bDownloadError) {
						FileUtils.deleteQuietly(f);
					}
				}
			}
		} catch (IOException e) {
			Debug("Server IO Exception.");
			showException(e);
			if(download != null)
				download.dispose();
			bDownloadError = true;
		} catch (Exception e) {
			Debug("Exception.");
			showException(e);
			if(download != null)
				download.dispose();
			bDownloadError = true;
		} finally {
			if(download != null) {
				download.setValue(100);
				download.dispose();
			}
		}
		return bDownloadError;
	}*/

	public boolean downloadFromGitHub(String destination) {
		MyCellarLauncherLoading download = null;
		try{
			download = new MyCellarLauncherLoading("Downloading...");
			download.setText("Downloading in progress...", "Downloading...");
			download.setVisible(true);
		}catch(Exception e) {
			e.printStackTrace();
		}
		try{
			// Creation des fichiers pour lister les fichiers à supprimer
			for(String s : listFileToRemove) {
				Debug("Creating file to delete... "+s);
				File f = new File(destination + "/" + s + ".myCellar");
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
						int fileSize = -1;
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
			if(download != null)
				download.dispose();
			bDownloadError = true;
		} catch (Exception e) {
			Debug("Exception.");
			showException(e);
			if(download != null)
				download.dispose();
			bDownloadError = true;
		} finally {
			if(download != null) {
				download.setValue(100);
				download.dispose();
			}
		}
		return bDownloadError;
	}

	public void downloadFileFromGitHub(String name, String destination) throws IOException {
		String link;
		URL url = new URL( gitHubUrl + name );
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
		int n = -1;
		OutputStream output = new FileOutputStream( new File( destination ));
		while ((n = input.read(buffer)) != -1) {
			output.write( buffer, 0, n );
		}
		output.close();
	}

	/**
	 * Download a file from a FTP server. A FTP URL is generated with the
	 * following syntax: ftp://user:password@host:port/filePath;type=i.
	 * 
	 * @param ftpServer
	 *            , FTP server address (optional port ':portNumber').
	 * @param user
	 *            , Optional user name to login.
	 * @param password
	 *            , Optional password for user.
	 * @param filelist
	 *            , Name of files to download (with optional preceeding relative
	 *            path, e.g. one/two/three.txt).
	 * @param fromdirectory
	 *            , FTP Directory where files are stored
	 * @param destination
	 *            , Destination file to save.
	 */
	/*public boolean Download(String ftpServer, String user, String password, String fromdirectory, String destination) {
		FTPClient ftp = new FTPClient();
		// boolean storeFile = false;
		Loading download = new Loading("Downloading...");
		download.setText("Downloading in progress...", "Downloading...");

		try {
			download.setVisible(true);
			int reply;
			ftp.connect(ftpServer);
			download.setValue(10);
			Debug("Connected to " + ftpServer + ".");

			// After connection attempt, you should check the reply code to
			// verify
			// success.
			reply = ftp.getReplyCode();

			if (!FTPReply.isPositiveCompletion(reply)) {
				ftp.disconnect();
				Debug("FTP server refused connection.");
				download.dispose();
				return true;
			}
		} catch (IOException e) {
			if (ftp.isConnected()) {
				try {
					ftp.disconnect();
				} catch (IOException f) {
					// do nothing
				}
			}
			Debug("Could not connect to server.");
			showException(e);
			download.dispose();
			bDownloadError = true;
		}

		try {
			if (!ftp.login(user, password)) {
				ftp.logout();
				Debug("Unable to log on with user: " + user);
				download.dispose();
				return true;
			}

			download.setValue(20);

			ftp.setFileType(FTP.BINARY_FILE_TYPE);

			// Use passive mode as default because most of us are
			// behind firewalls these days.
			ftp.enterLocalPassiveMode();

			int size = listFile.size();
			int percent = 80;
			if (size > 0)
				percent = 80 / size;
			for (int i = 0; i < size; i++) {
				String name = listFile.get(i);
				String serverMd5 = listMd5.get(i);
				Debug("Downloading... "+name);
				{
					File f = new File(destination + "/" + name);
					FileOutputStream output;

					output = new FileOutputStream(f);

					download.setValue(20 + i * percent);
					if (!ftp.retrieveFile(fromdirectory + "/MyCellar/" + name, output)) {
						Debug("Error Downloading " + name);
						bDownloadError = true;
					}
					output.close();

					InputStream stream = new FileInputStream(f);
					int fileSize = -1;
					try {
						fileSize = stream.available();
						String localMd5 = getMD5Checksum(f.getAbsolutePath());
						if(!serverMd5.isEmpty()) {
							if(localMd5.equals(serverMd5))
								Debug(name + " Md5 OK");
							else {
								Debug(name + " "+serverMd5 + " "+localMd5);
								bDownloadError = true;
							}
						}
					} finally {
						stream.close();
					}
					if (fileSize == 0)
						bDownloadError = true;
					if(bDownloadError) {
						FileUtils.deleteQuietly(f);
					}
				}
			}

			ftp.logout();
			download.setValue(100);
		} catch (FTPConnectionClosedException e) {
			Debug("Server closed connection.");
			showException(e);
			download.dispose();
			bDownloadError = true;
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
			if (ftp.isConnected()) {
				try {
					ftp.disconnect();
				} catch (IOException f) {
					// do nothing
				}
			}
		}
		Debug("Download completed.");
		return bDownloadError;
	}*/

	public static byte[] createChecksum(String filename) throws Exception {
		InputStream fis = new FileInputStream(filename);

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
				debugFile = new File(sDir, "DebugFtp-"+sDate+".log");
				oDebugFile = new FileWriter(debugFile, true);
			}
			oDebugFile.write("[" + java.util.Calendar.getInstance().getTime().toString() + "]: " + sText + "\n");
			oDebugFile.flush();
		}
		catch (Exception e) {}
	}

	/**
	 * showException
	 * @param e Exception
	 */
	public static void showException(Exception e) {
		StackTraceElement st[] = new StackTraceElement[1];
		st = e.getStackTrace();
		String error = "";
		for (int z = 0; z < st.length; z++) {
			error = error.concat("\n" + st[z]);
		}
		FileWriter fw = null;
		try {
			String sDir = System.getProperty("user.home");
			if( !sDir.isEmpty() )
				sDir += "/MyCellarDebug";
			fw = new FileWriter(sDir+"/Errors.log");
			fw.write(e.toString());
			fw.write(error);
			fw.flush();
			fw.close();
		}
		catch (IOException ex) {}
		Debug("Server: ERROR:");
		Debug("Server: "+e.toString());
		Debug("Server: "+error);
		e.printStackTrace();
	}

	public boolean install() {
		Debug("Installing MyCellar...");
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
		boolean result = downloadFromGitHub(".");
		
		Debug("Installation of MyCellar Done.");
		return result;
	}

	private static boolean isRedirected( Map<String, List<String>> header ) {
		for( String hv : header.get( null )) {
			if(   hv.contains( " 301 " )
					|| hv.contains( " 302 " )) return true;
		}
		return false;
	}

	class FileType {

		private String file;
		private String md5;
		private boolean lib;

		public FileType(String file, String md5, boolean lib) {
			this.file = file;
			this.md5 = md5;
			this.lib = lib;
		}

		public FileType(String file, String md5) {
			this.file = file;
			this.md5 = md5;
			this.lib = false;
		}

		public String getFile() {
			return file;
		}

		public String getMd5() {
			return md5;
		}

		public boolean isForLibDirectory() {
			return lib;
		}
	}
}
