package mycellar.launcher;

import mycellar.core.MyCellarVersion;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

import static javax.swing.JOptionPane.ERROR_MESSAGE;
import static javax.swing.JOptionPane.showMessageDialog;

/**
 *
 * <p>Titre : Cave à vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2011</p>
 * <p>Société : Seb Informatique</p>
 * @author Sébastien Duché
 * @version 1.4
 * @since 13/03/19
 */
class MyCellarLauncher {

	private MyCellarLauncher() {
		
		Thread updateThread = new Thread(() -> {
					Server.getInstance().checkVersion();
					if (!Server.getInstance().hasAvailableUpdate()) {
						return;
					}
					Server.getInstance().downloadVersion();
					MyCellarVersion.setLocalVersion(Server.getInstance().getServerVersion());

					File f = new File("download");
        		if (f.isDirectory()) {
        			try {
        				Server.Debug("Installing new version...");
        				final File[] fList = f.listFiles();
        				if (fList != null) {
									for (File file : fList) {
										String name = file.getName();
										if (name.endsWith(".myCellar")) {
											name = name.substring(0, name.indexOf(".myCellar"));
											Server.Debug("Delete file " + name);
											FileUtils.deleteQuietly(new File("lib", name));
										} else if (file.getName().endsWith("ini")) {
											Server.Debug("Copying file " + file.getName() + " to config dir");
											FileUtils.copyFileToDirectory(file, new File("config"));
										} else if (file.getName().endsWith("jar") && !file.getName().equalsIgnoreCase("MyCellar.jar")) {
											Server.Debug("Copying file " + file.getName() + " to lib dir");
											FileUtils.copyFileToDirectory(file, new File("lib"));
										} else {
											Server.Debug("Copying file " + file.getName() + " to current dir");
											FileUtils.copyFileToDirectory(file, new File("."));
										}
									}
								} else {
        					Server.Debug("ERROR: Unable to list files");
								}
        				FileUtils.deleteDirectory(f);
        				Server.Debug("Installing new version... Done");
        			} catch(IOException e) {
        				showException(e);
        			}
        		} else {
        			Server.Debug("ERROR: Missing download directory");
						}
        		System.exit(0);
        });

        try {
					var pb = new ProcessBuilder("java","-Dfile.encoding=UTF8","-jar","MyCellar.jar");
					pb.redirectErrorStream(true);
					Process p = pb.start();
					p.waitFor();
					Runtime.getRuntime().addShutdownHook(updateThread);
					updateThread.start();
        } catch (IOException | InterruptedException ex) {
            showException(ex);
        }
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new MyCellarLauncher();

	}

	private static void showException(Exception e) {
		StackTraceElement[] st = e.getStackTrace();
		String error = "";
		for (StackTraceElement elem : st) {
			error = error.concat("\n" + elem);
		}
		showMessageDialog(null, e.toString(), "Error", ERROR_MESSAGE);
		System.exit(999);
	}

}
