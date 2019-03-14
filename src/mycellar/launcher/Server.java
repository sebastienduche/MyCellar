package mycellar.launcher;

import mycellar.core.MyCellarVersion;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.util.Calendar;
import java.util.Deque;
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
 * @version 2.6
 * @since 14/03/19
 */

public class Server implements Runnable {

	private String sServerVersion = "";
	private String sAvailableVersion = "";

	private static final String GETVERSION = "GETVERSION";
	private static final String DOWNLOAD = "DOWNLOAD";

	private String sAction = "";

	private static final LinkedList<FileType> FILE_TYPES = new LinkedList<>();
	private static final Deque<String> LIST_FILE_TO_REMOVE = new LinkedList<>();

	private boolean bDownloadError = false;

	private static FileWriter oDebugFile = null;

	private static final Server INSTANCE = new Server();

	private static final String GIT_HUB_URL = "https://github.com/sebastienduche/MyCellar/raw/master/Build/";

	private static final String DOWNLOAD_DIRECTORY = "download";
	private static final String MY_CELLAR = "MyCellar";
	private static final String LIB = "lib";
	private static final String MY_CELLAR_DEBUG = "MyCellarDebug";

	private Server() {}

	public static Server getInstance() {
		return INSTANCE;
	}

	@Override
	public void run() {
		if (GETVERSION.equals(sAction)) {
			getVersionFromServer();
		} else if (DOWNLOAD.equals(sAction)) {
			downloadFromServer();
		}
		if (bDownloadError) {
			new File(DOWNLOAD_DIRECTORY).deleteOnExit();
		}
	}

	private void downloadFromServer() {
		sAction = "";
		bDownloadError = false;
		try {
			File f = new File(DOWNLOAD_DIRECTORY);
			if (!f.exists()) {
				Files.createDirectory(f.toPath());
			}

			bDownloadError = downloadFromGitHub(f);
		} catch (Exception e) {
			showException(e);
			bDownloadError = true;
		}
	}

	private void getVersionFromServer() {
		sServerVersion = sAction = "";
		try {
			final File myCellarVersion = downloadMyCellarVersionTxt();
			try (var in = new BufferedReader(new FileReader(myCellarVersion))){
				sServerVersion = in.readLine();
				sAvailableVersion = in.readLine();
			}
		} catch (Exception e) {
			showException(e);
		}
	}

	private File downloadMyCellarVersionTxt() throws IOException {
		final File myCellarVersion = File.createTempFile("MyCellarVersion", "txt");
		myCellarVersion.deleteOnExit();
		downloadFileFromGitHub("MyCellarVersion.txt", myCellarVersion);
		return myCellarVersion;
	}

	void checkVersion() {
		Debug("Checking Version from GitHub...");
		sServerVersion = "";
		try {
			final File myCellarVersion = downloadMyCellarVersionTxt();
			try(var bufferedReader = new BufferedReader(new FileReader(myCellarVersion))) {
				sServerVersion = bufferedReader.readLine();
				sAvailableVersion = bufferedReader.readLine();
				String sFile = bufferedReader.readLine();
				while (sFile != null && !sFile.isEmpty()) {
					int index = sFile.indexOf('@');
					String md5 = "";
					if(index != -1) {
						md5 = sFile.substring(index+1).trim();
						sFile = sFile.substring(0, index);
					}
					// Suppression de fichier commençant par -
					Debug("sFile... " + sFile + " " + ((sFile.indexOf('-') == 0) ? "to delete" : ""));
					if(sFile.indexOf('-') == 0) {
						LIST_FILE_TO_REMOVE.add(sFile.substring(1));
					} else {
						boolean lib = (!sFile.contains(MY_CELLAR) && sFile.endsWith(".jar"));
						FILE_TYPES.add(new FileType(sFile, md5, lib));
					}
					sFile = bufferedReader.readLine();
				}
			}
			Debug("GitHub version: "+sServerVersion+"/"+sAvailableVersion);
		} catch (Exception e) {
			showException(e);
		}
	}

	void downloadVersion() {
		Debug("Downloading version from GitHub...");
		downloadFromServer();
		if (bDownloadError) {
			new File(DOWNLOAD_DIRECTORY).deleteOnExit();
		}
	}

	public String getAvailableVersion() {
		return sAvailableVersion;
	}

	public String getServerVersion() {
		if (sServerVersion.isEmpty()) {
			try {
				sAction = GETVERSION;
				new Thread(this).start();
			} catch (Exception a) {
				showException(a);
			}
		}

		return sServerVersion;
	}

	public boolean hasAvailableUpdate() {
		if (sServerVersion.isEmpty()) {
			return false;
		}

		return (sServerVersion.compareTo(MyCellarVersion.getLocalVersion()) > 0);
	}

	public boolean isDownloadError() {
		return bDownloadError;
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
			for(String fileNameToRemove : LIST_FILE_TO_REMOVE) {
				Debug("Creating file to delete... "+fileNameToRemove);
				File f = new File(destination, fileNameToRemove + ".myCellar");
				f.createNewFile();
			}
			bDownloadError = false;
			Debug("Connecting to GitHub...");

			int size = FILE_TYPES.size();
			int percent = 80;
			if (size > 0) {
				percent = 80 / size;
			}
			for (int i = 0; i < size; i++) {
				FileType fType = FILE_TYPES.get(i);
				String name = fType.getFile();
				String serverMd5 = fType.getMd5();
				bDownloadError = false;
				Debug("Downloading... "+name);

				download.setValue(20 + i * percent);
				final File file = new File(destination, name);
				try {
					String dir = "";
					if(fType.isForLibDirectory()) {
						dir = LIB + File.separator;
					}
					downloadFileFromGitHub(dir + name, file);
				} catch (IOException e) {
					showException(e);
					Debug("Error Downloading " + name);
					bDownloadError = true;
				}

				if(!serverMd5.isEmpty() && !file.isDirectory()) {
					int fileSize;
					try(InputStream stream = new FileInputStream(file)) {
						fileSize = stream.available();
						String localMd5 = getMD5Checksum(file.getAbsolutePath());
						if(localMd5.equals(serverMd5)) {
							Debug(name + " Md5 OK");
						}	else {
							Debug(name + " " + serverMd5 + " " + localMd5 + " KO");
							bDownloadError = true;
						}
					}
					if (fileSize == 0) {
						bDownloadError = true;
					}
				}
				if(bDownloadError) {
					Debug("Error "+name);
					file.deleteOnExit();
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

	public void downloadFileFromGitHub(String name, File destination) throws IOException {
		URL url = new URL(GIT_HUB_URL + name);
		HttpURLConnection http = (HttpURLConnection)url.openConnection();
		Map< String, List< String >> header = http.getHeaderFields();
		while(isRedirected(header)) {
			String link = header.get("Location").get(0);
			url = new URL(link);
			http = (HttpURLConnection)url.openConnection();
			header = http.getHeaderFields();
		}

		try(InputStream input = http.getInputStream();
				var output = new FileOutputStream(destination)) {
			int n;
			byte[] buffer = new byte[4096];
			while ((n = input.read(buffer)) != -1) {
				output.write(buffer, 0, n);
			}
		}
	}

	private static byte[] createChecksum(String filename) throws Exception {
		try(var fis = new FileInputStream(filename)) {

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

	private static String getMD5Checksum(String filename) throws Exception {
		byte[] b = createChecksum(filename);
		StringBuilder result = new StringBuilder();
		for (byte v : b) {
			result.append(Integer.toString((v & 0xff) + 0x100, 16).substring(1));
		}
		return result.toString();
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
				if(!sDir.isEmpty()) {
					sDir +=  File.separator + MY_CELLAR_DEBUG;
				}
				File f_obj = new File(sDir);
				if(!f_obj.exists()) {
					Files.createDirectory(f_obj.toPath());
				}
				Calendar oCal = Calendar.getInstance();
				String sDate = oCal.get(Calendar.DATE) + "-" + (oCal.get(Calendar.MONTH)+1) + "-" + oCal.get(Calendar.YEAR);
				oDebugFile = new FileWriter(new File(sDir, "DebugFtp-"+sDate+".log"), true);
			}
			oDebugFile.write("[" + Calendar.getInstance().getTime().toString() + "]: " + sText + "\n");
			oDebugFile.flush();
		}
		catch (Exception ignored) {}
	}

	/**
	 * showException
	 * @param e Exception
	 */
	private static void showException(Exception e) {
		StackTraceElement[] st = e.getStackTrace();
		String error = "";
		for (StackTraceElement element : st) {
			error = error.concat("\n" + element);
		}
		String sDir = System.getProperty("user.home");
		if(!sDir.isEmpty()) {
			sDir += File.separator + MY_CELLAR_DEBUG;
		}
		try(var fileWriter = new FileWriter(sDir + File.separator + "Errors.log")) {
			fileWriter.write(e.toString());
			fileWriter.write(error);
			fileWriter.flush();
		}
		catch (IOException ignored) {}
		Debug("Server: ERROR:");
		Debug("Server: "+e.toString());
		Debug("Server: "+error);
		e.printStackTrace();
	}

	private static boolean isRedirected(Map<String, List<String>> header) {
		if(header == null) {
			return false;
		}
		try {
			for( String hv : header.get(null)) {
				if(hv == null) {
					return false;
				}
				if( hv.contains(" 301 ") || hv.contains(" 302 "))
					return true;
			}
		} catch(Exception ignored) {}
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
