package mycellar.launcher;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.util.Calendar;
import java.util.LinkedList;

import mycellar.APropos;

import org.apache.commons.io.FileUtils;

import com.dropbox.core.DbxException;

/**
 * 
 * Titre : Cave à vin
 * Description : Votre description
 * Copyright : Copyright (c) 2011
 * Société : Seb Informatique
 * @author Sébastien Duché
 * @version 1.4
 * @since 17/04/17
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
	private static MyCellarDropbox dropbox;

	private static final Server INSTANCE = new Server();

	private Server() {
		try {
			dropbox = new MyCellarDropbox();
		} catch (Exception e) {
			showException(e);
		}
	}

	public static Server getInstance() {
		return INSTANCE;
	}

	@Override
	public void run() {

		if (sAction == GETVERSION) {
			sServerVersion = "";
			try {
				File myCellarVersion = File.createTempFile("MyCellarVersion", "txt");
				dropbox.downloadFromDropbox("MyCellarVersion.txt", myCellarVersion);
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
					Debug("sFile1... "+sFile+" "+sFile.indexOf('-'));
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

				bDownloadError = downloadFromDropbox("download");
			} catch (Exception e) {
				showException(e);
				sAction = "";
				bDownloadError = true;
				return;
			}
			bDownloaded = true;
			sAction = "";
		}
		if (bDownloadError)
			try {
				FileUtils.deleteDirectory(new File("download"));
			} catch (IOException e) {
			}
		if (bExit)
			System.exit(0);
	}

	public void checkVersion() {
		Debug("Checking Version from Dropbox...");
		sServerVersion = "";
		try {
			File myCellarVersion = File.createTempFile("MyCellarVersion", "txt");
			dropbox.downloadFromDropbox("MyCellarVersion.txt", myCellarVersion);
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
				else
					listFile.add(new FileType(sFile, md5));
				sFile = in.readLine();
			}

			in.close();
			Debug("Dropbox version: "+sServerVersion+"/"+sAvailableVersion);
		} catch (Exception e) {
			showException(e);
		}
	}

	public void downloadVersion() {
		Debug("Downloading version from Dropbox...");
		try {
			File f = new File("download");
			if (!f.exists())
				f.mkdir();

			bDownloadError = downloadFromDropbox("download");

		} catch (Exception e) {
			showException(e);
			sAction = "";
			bDownloadError = true;
			return;
		}
		bDownloaded = true;
		sAction = "";
		if (bDownloadError)
			try {
				FileUtils.deleteDirectory(new File("download"));
			} catch (IOException e) {
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

		return (sServerVersion.compareTo(APropos.sVersion) > 0);
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

	public boolean downloadFromDropbox(String destination) {
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

	public void install() {
		Debug("Installing MyCellar...");
		listFile.clear();
		listFile.add(new FileType("lib", "", true));
		listFile.add(new FileType("MyCellar.jar",""));
		listFile.add(new FileType("MyCellarLauncher.jar",""));
		listFile.add(new FileType("Finish.html",""));
		downloadFromDropbox(".");
		Debug("Installation of MyCellar Done.");
	}

	class FileType {

		private String file;
		private String md5;
		private boolean folder;

		public FileType(String file, String md5, boolean folder) {
			this.file = file;
			this.md5 = md5;
			this.folder = folder;
		}

		public FileType(String file, String md5) {
			this.file = file;
			this.md5 = md5;
			this.folder = false;
		}

		public String getFile() {
			return file;
		}

		public String getMd5() {
			return md5;
		}

		public boolean isFolder() {
			return folder;
		}
	}
}
