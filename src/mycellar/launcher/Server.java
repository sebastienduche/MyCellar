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
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static mycellar.launcher.Server.Action.DOWNLOAD;
import static mycellar.launcher.Server.Action.GET_VERSION;
import static mycellar.launcher.Server.Action.NONE;

/**
 *
 * Titre : Cave à vin
 * Description : Votre description
 * Copyright : Copyright (c) 2011
 * Société : Seb Informatique
 * @author Sébastien Duché
 * @version 2.9
 * @since 27/01/21
 */

public class Server implements Runnable {

	enum Action {
		NONE,
		GET_VERSION,
		DOWNLOAD
	}

	private String serverVersion = "";
	private String availableVersion = "";

	private Action action = NONE;

	private static final LinkedList<FileType> FILE_TYPES = new LinkedList<>();

	private boolean downloadError = false;

	private static FileWriter debugFile = null;

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
		if (GET_VERSION.equals(action)) {
			getVersionFromServer();
		} else if (DOWNLOAD.equals(action)) {
			downloadFromServer();
		}
		if (downloadError) {
			new File(DOWNLOAD_DIRECTORY).deleteOnExit();
		}
	}

	private void downloadFromServer() {
		action = NONE;
		downloadError = false;
		try {
			File f = new File(DOWNLOAD_DIRECTORY);
			if (!f.exists()) {
				Files.createDirectory(f.toPath());
			}

			downloadError = downloadFromGitHub();
		} catch (Exception e) {
			showException(e);
			downloadError = true;
		}
	}

	private void getVersionFromServer() {
		serverVersion = "";
		action = NONE;
		try {
			final File myCellarVersion = downloadMyCellarVersionTxt();
			try (var in = new BufferedReader(new FileReader(myCellarVersion))) {
				serverVersion = in.readLine();
				availableVersion = in.readLine();
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
		serverVersion = "";
		try {
			final File myCellarVersion = downloadMyCellarVersionTxt();
			try (var bufferedReader = new BufferedReader(new FileReader(myCellarVersion))) {
				serverVersion = bufferedReader.readLine();
				availableVersion = bufferedReader.readLine();
				String file = bufferedReader.readLine();
				while (file != null && !file.isEmpty()) {
					int index = file.indexOf('@');
					String md5 = "";
					if (index != -1) {
						final String[] split = file.split("@");
						file = split[0];
						md5 = split[1];
					}
					Debug("sFile... " + file);
					boolean lib = (!file.contains(MY_CELLAR) && file.endsWith(".jar"));
					FILE_TYPES.add(new FileType(file, md5, lib));
					file = bufferedReader.readLine();
				}
			}
			Debug("GitHub version: " + serverVersion + "/" + availableVersion);
		} catch (Exception e) {
			showException(e);
		}
	}

	public void downloadVersion() {
		Debug("Downloading version from GitHub...");
		downloadFromServer();
		if (downloadError) {
			new File(DOWNLOAD_DIRECTORY).deleteOnExit();
		}
	}

	public String getAvailableVersion() {
		return availableVersion;
	}

	public String getServerVersion() {
		if (serverVersion.isEmpty()) {
			try {
				action = GET_VERSION;
				new Thread(this).start();
			} catch (Exception a) {
				showException(a);
			}
		}

		return serverVersion;
	}

	public boolean hasAvailableUpdate() {
		if (serverVersion.isEmpty()) {
			return false;
		}

		return (serverVersion.compareTo(MyCellarVersion.getLocalVersion()) > 0);
	}

	private List<String> getLibFiles() {
		try {
			return Files.walk(Path.of("./lib"), 1, FileVisitOption.FOLLOW_LINKS)
					.map(Path::toFile)
					.filter(File::isFile)
					.map(File::getName)
					.collect(Collectors.toList());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new ArrayList<>();
	}

	private boolean downloadFromGitHub() {
		MyCellarLauncherLoading download;
		try {
			download = new MyCellarLauncherLoading("Downloading...");
			download.setText("Downloading in progress...", "Downloading...");
			download.setVisible(true);
		} catch(Exception e) {
			e.printStackTrace();
			return false;
		}

		downloadError = false;
		try {
			final List<String> jarsOnServer = FILE_TYPES
					.stream()
					.map(FileType::getFile)
					.collect(Collectors.toList());
			final List<String> libFiles = getLibFiles();
			libFiles.removeAll(jarsOnServer);
			// Creation des fichiers pour lister les fichiers à supprimer
			File destination = new File(DOWNLOAD_DIRECTORY);
			for (String fileNameToRemove : libFiles) {
				Debug("Creating file to delete... " + fileNameToRemove);
				new File(destination, fileNameToRemove + ".myCellar").createNewFile();
			}
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
				final File file = new File(destination, name);
				if (file.exists()) {
					String localMd5 = getMD5Checksum(file.getAbsolutePath());
					if (localMd5.equals(serverMd5)) {
						Debug("Skipping downloading file: " + name + " Md5 OK");
						continue;
					}	else {
						Debug("Need to download file: " + name + " " + serverMd5 + " " + localMd5 + " KO");
					}
				}
				downloadError = false;
				Debug("Downloading... " + name);

				download.setValue(20 + i * percent);
				try {
					String serverDirectory = "";
					if (fType.isForLibDirectory()) {
						serverDirectory = LIB + File.separator;
					}
					downloadFileFromGitHub(serverDirectory + name, file);
				} catch (IOException e) {
					showException(e);
					Debug("Error Downloading " + name);
					downloadError = true;
				}

				if (!serverMd5.isEmpty() && !file.isDirectory()) {
					int fileSize;
					try (InputStream stream = new FileInputStream(file)) {
						fileSize = stream.available();
						String localMd5 = getMD5Checksum(file.getAbsolutePath());
						if (localMd5.equals(serverMd5)) {
							Debug(name + " Md5 OK");
						}	else {
							Debug(name + " " + serverMd5 + " " + localMd5 + " KO");
							downloadError = true;
						}
					}
					if (fileSize == 0) {
						downloadError = true;
					}
				}
				if (downloadError) {
					Debug("Error " + name);
					file.deleteOnExit();
				}
			}
		} catch (IOException e) {
			Debug("Server IO Exception.");
			showException(e);
			download.dispose();
			downloadError = true;
		} catch (Exception e) {
			Debug("Exception.");
			showException(e);
			download.dispose();
			downloadError = true;
		} finally {
			download.setValue(100);
			download.dispose();
		}
		return downloadError;
	}

	public void downloadFileFromGitHub(String name, File destination) throws IOException {
		URL url = new URL(GIT_HUB_URL + name);
		HttpURLConnection http = (HttpURLConnection)url.openConnection();
		Map< String, List< String >> header = http.getHeaderFields();
		while (isRedirected(header)) {
			String link = header.get("Location").get(0);
			url = new URL(link);
			http = (HttpURLConnection)url.openConnection();
			header = http.getHeaderFields();
		}

		try (InputStream input = http.getInputStream();
				 var output = new FileOutputStream(destination)) {
			int n;
			byte[] buffer = new byte[4096];
			while ((n = input.read(buffer)) != -1) {
				output.write(buffer, 0, n);
			}
		}
	}

	private static byte[] createChecksum(String filename) throws Exception {
		try (var fis = new FileInputStream(filename)) {
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
			if (debugFile == null) {
				String sDir = System.getProperty("user.home");
				if (!sDir.isEmpty()) {
					sDir += File.separator + MY_CELLAR_DEBUG;
				}
				File f_obj = new File(sDir);
				if (!f_obj.exists()) {
					Files.createDirectory(f_obj.toPath());
				}
				Calendar oCal = Calendar.getInstance();
				String sDate = oCal.get(Calendar.DATE) + "-" + (oCal.get(Calendar.MONTH) + 1) + "-" + oCal.get(Calendar.YEAR);
				debugFile = new FileWriter(new File(sDir, "DebugFtp-" + sDate + ".log"), true);
			}
			debugFile.write("[" + Calendar.getInstance().getTime().toString() + "]: " + sText + "\n");
			debugFile.flush();
		}
		catch (Exception ignored) {}
	}

//	public void testPopulateList() {
//		FILE_TYPES.add(new FileType("github-api-1.117.jar", "", true));
//		FILE_TYPES.add(new FileType("commons-lang3-3.9.jar", "", true));
//	}

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
		if (!sDir.isEmpty()) {
			sDir += File.separator + MY_CELLAR_DEBUG;
		}
		try (var fileWriter = new FileWriter(sDir + File.separator + "Errors.log")) {
			fileWriter.write(e.toString());
			fileWriter.write(error);
			fileWriter.flush();
		}
		catch (IOException ignored) {}
		Debug("Server: ERROR:");
		Debug("Server: " + e.toString());
		Debug("Server: " + error);
		e.printStackTrace();
	}

	private static boolean isRedirected(Map<String, List<String>> header) {
		if (header == null) {
			return false;
		}
		try {
			for (String hv : header.get(null)) {
				if (hv == null) {
					return false;
				}
				if (hv.contains(" 301 ") || hv.contains(" 302 "))
					return true;
			}
		} catch(Exception ignored) {}
		return false;
	}

	static class FileType {

		private final String file;
		private final String md5;
		private final boolean forLibDirectory;

		private FileType(String file, String md5, boolean forLibDirectory) {
			this.file = file;
			this.md5 = md5;
			this.forLibDirectory = forLibDirectory;
		}

		public String getFile() {
			return file;
		}

		private String getMd5() {
			return md5;
		}

		private boolean isForLibDirectory() {
			return forLibDirectory;
		}
	}
}
