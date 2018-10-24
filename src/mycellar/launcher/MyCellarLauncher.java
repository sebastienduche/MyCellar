package mycellar.launcher;

/**
 * <p>Titre : Cave à vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2011</p>
 * <p>Société : Seb Informatique</p>
 * @author Sébastien Duché
 * @version 1.1
 * @since 08/06/18
 */

import mycellar.core.MyCellarVersion;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

import static javax.swing.JOptionPane.ERROR_MESSAGE;
import static javax.swing.JOptionPane.showMessageDialog;

class MyCellarLauncher {

	private MyCellarLauncher(final String[] args) {
		
		Thread updateThread = null;
		File fLib = new File("MyCellar.jar");
		
    	if (!fLib.exists()) {
    		boolean installError = install();
    		if (installError) {
        		System.exit(1);
    		} else {
        		System.out.println("Installation Done");
    		}
    	} else {
    		updateThread = new Thread(() -> {
            	Server.getInstance().checkVersion();
            	if(!Server.getInstance().hasAvailableUpdate())
            		return;
            	Server.getInstance().downloadVersion();
            	MyCellarVersion.setLocalVersion(Server.getInstance().getServerVersion());
                
            	File f = new File("download");
        		if(f.isDirectory()) {
        			try{
        				Server.Debug("Installing new version...");
        				File fList[] = f.listFiles();
        				for(File file : fList) {
        					String name = file.getName();
        					if(name.endsWith(".myCellar")) {
        						name = name.substring(0, name.indexOf(".myCellar"));
        						Server.Debug("Delete file "+name);
        						FileUtils.deleteQuietly(new File("lib", name));
        					} else if(file.getName().endsWith("ini")){
        						Server.Debug("Copy file "+file.getName()+" to config dir");
        						FileUtils.copyFileToDirectory(file, new File("config"));
        					}else if(file.getName().endsWith("jar") && !file.getName().equalsIgnoreCase("MyCellar.jar")){
        						Server.Debug("Copying file "+file.getName()+" to lib dir");
        						FileUtils.copyFileToDirectory(file, new File("lib"));
        					}else {
        						Server.Debug("Copying file "+file.getName()+" to current dir");
        						FileUtils.copyFileToDirectory(file, new File("."));
        					}	
        				}
        				FileUtils.deleteDirectory(f);
        				Server.Debug("Installing new version... Done");
        			}catch( IOException e) {
        				showException(e);
        			}
        		}
        		System.exit(0);
        });
    	}
    		
        try {
            ProcessBuilder pb = new ProcessBuilder("java","-Dfile.encoding=UTF8","-jar","MyCellar.jar");
            pb.redirectErrorStream(true);
            Process p = pb.start();
            p.waitFor();
            if(updateThread != null) {
            	Runtime.getRuntime().addShutdownHook(updateThread);
            	updateThread.start();
            }
        } catch (IOException | InterruptedException ex) {
            showException(ex);
        }
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new MyCellarLauncher(args);

	}

	private static void showException(Exception e ) {
		StackTraceElement st[] = e.getStackTrace();
		String error = "";
		for (StackTraceElement elem : st) {
			error = error.concat("\n" + elem);
		}
		showMessageDialog(null, e.toString(), "Error", ERROR_MESSAGE);
		System.exit(999);
	}
	
	private boolean install() {
		return Server.getInstance().install();
	}

}
