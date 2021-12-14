package mycellar.launcher;

import mycellar.core.MyCellarVersion;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

import static javax.swing.JOptionPane.ERROR_MESSAGE;
import static javax.swing.JOptionPane.showMessageDialog;

/**
 * <p>Titre : Cave &agrave; vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2011</p>
 * <p>Soci&eacute;t&eacute; : Seb Informatique</p>
 *
 * @author S&eacute;bastien Duch&eacute;
 * @version 1.6
 * @since 08/07/21
 */
class MyCellarLauncher {

  private static final String MYCELLAR_EXTENSION = ".myCellar";
  private static final String LIB_DIRECTORY = "lib";
  private static final String MYCELLAR_JAR = "MyCellar.jar";
  private static final String CONFIG_DIR = "config";
  private static final String INI_EXTENSION = "ini";
  private static final String JAR_EXTENSION = "jar";

  private MyCellarLauncher() {

    Thread updateThread = new Thread(() -> {
      MyCellarServer.getInstance().checkVersion();
      if (!MyCellarServer.getInstance().hasAvailableUpdate(MyCellarVersion.getLocalVersion())) {
        return;
      }
      File downloadDirectory = MyCellarServer.getInstance().downloadVersion();

      if (downloadDirectory != null && downloadDirectory.isDirectory()) {
        try {
          MyCellarVersion.setLocalVersion(MyCellarServer.getInstance().getServerVersion());
          MyCellarServer.Debug("Installing new version...");
          final File[] fList = downloadDirectory.listFiles();
          if (fList != null) {
            for (File file : fList) {
              String fileName = file.getName();
              if (fileName.endsWith(MYCELLAR_EXTENSION)) {
                fileName = fileName.substring(0, fileName.indexOf(MYCELLAR_EXTENSION));
                MyCellarServer.Debug("Delete file " + fileName);
                FileUtils.deleteQuietly(new File(LIB_DIRECTORY, fileName));
              } else {
                if (fileName.endsWith(INI_EXTENSION)) {
                  MyCellarServer.Debug("Copying file " + fileName + " to config dir");
                  FileUtils.copyFileToDirectory(file, new File(CONFIG_DIR));
                } else {
                  if (fileName.endsWith(JAR_EXTENSION) && !fileName.equalsIgnoreCase(MYCELLAR_JAR)) {
                    MyCellarServer.Debug("Copying file " + fileName + " to lib dir");
                    FileUtils.copyFileToDirectory(file, new File(LIB_DIRECTORY));
                  } else {
                    MyCellarServer.Debug("Copying file " + fileName + " to current dir");
                    FileUtils.copyFileToDirectory(file, new File("."));
                  }
                }
              }
            }
          } else {
            MyCellarServer.Debug("ERROR: Unable to list files");
          }
          FileUtils.deleteDirectory(downloadDirectory);
          MyCellarServer.Debug("Installing new version... Done");
        } catch (IOException e) {
          showException(e);
        }
      } else {
        MyCellarServer.Debug("ERROR: Missing download directory");
      }
      System.exit(0);
    });

    try {
      var pb = new ProcessBuilder("java", "-Dfile.encoding=UTF8", "-jar", MYCELLAR_JAR);
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
