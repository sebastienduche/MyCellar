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
 * @version 2.5
 * @since 13/03/19
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
			new File("download").deleteOnExit();
		}
	}

	private void downloadFromServer() {
		sAction = "";
		bDownloadError = false;
		try {
			File f = new File("download");
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
		sServerVersion = "";
		try {
			File myCellarVersion = File.createTempFile("MyCellarVersion", "txt");
			myCellarVersion.deleteOnExit();
			downloadFileFromGitHub("MyCellarVersion.txt", myCellarVersion.getAbsolutePath());
			try (var in = new BufferedReader(new FileReader(myCellarVersion))){
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
					if(sFile.indexOf('-') == 0) {
						LIST_FILE_TO_REMOVE.add(sFile.substring(1));
					} else {
						FILE_TYPES.add(new FileType(sFile, md5));
					}
					sFile = in.readLine();
				}
			}
		} catch (Exception e) {
			showException(e);
		}
		sAction = "";
	}

	void checkVersion() {
		Debug("Checking Version from GitHub...");
		sServerVersion = "";
		try {
			File myCellarVersion = File.createTempFile("MyCellarVersion", "txt");
			downloadFileFromGitHub("MyCellarVersion.txt", myCellarVersion.getAbsolutePath());
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
					Debug("sFile... "+sFile+" "+ ((sFile.indexOf('-') == 0) ? "to delete" : ""));
					if(sFile.indexOf('-') == 0) {
						LIST_FILE_TO_REMOVE.add(sFile.substring(1));
					} else {
						boolean lib = (!sFile.contains("MyCellar") && sFile.endsWith(".jar"));
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
			new File("download").deleteOnExit();
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
			for(String s : LIST_FILE_TO_REMOVE) {
				Debug("Creating file to delete... "+s);
				File f = new File(destination, s + ".myCellar");
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
				System.out.println("Downloading... "+name);
				Debug("Downloading... "+name);
				File f = new File(destination, name);

				download.setValue(20 + i * percent);
				try {
					String dir = "";
					if(fType.isForLibDirectory()) {
						dir = "lib/";
					}
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
					if (fileSize == 0) {
						bDownloadError = true;
					}
				}
				if(bDownloadError) {
					Debug("Error "+name);
					f.deleteOnExit();
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
		URL url = new URL(GIT_HUB_URL + name);
		HttpURLConnection http = (HttpURLConnection)url.openConnection();
		Map< String, List< String >> header = http.getHeaderFields();
		while(isRedirected(header)) {
			link = header.get("Location").get(0);
			url = new URL(link);
			http = (HttpURLConnection)url.openConnection();
			header = http.getHeaderFields();
		}
		InputStream input = http.getInputStream();
		byte[] buffer = new byte[4096];
		int n;
		try(var output = new FileOutputStream(new File(destination))) {
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

	// see this How-to for a faster way to convert
	// a byte array to a HEX string
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
					sDir +=  File.separator + "MyCellarDebug";
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
			sDir += File.separator + "MyCellarDebug";
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
