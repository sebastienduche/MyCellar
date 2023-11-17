package mycellar;

import mycellar.core.MyCellarSettings;
import mycellar.core.exceptions.UnableToOpenFileException;
import mycellar.frame.MainFrame;
import mycellar.launcher.MyCellarServer;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import static mycellar.Filtre.EXTENSION_SINFO;
import static mycellar.MyCellarUtils.toCleanString;
import static mycellar.ProgramConstants.DOWNLOAD_COMMAND;
import static mycellar.ProgramConstants.ONE_DOT;
import static mycellar.ProgramConstants.OPTIONS_PARAM;
import static mycellar.ProgramConstants.RESTART_COMMAND;
import static mycellar.ProgramConstants.SPACE;

/**
 * Titre : Cave &agrave; vin
 * Description : Votre description
 * Copyright : Copyright (c) 2003
 * Soci&eacute;t&eacute; : Seb Informatique
 *
 * @author S&eacute;bastien Duch&eacute;
 * @version 32.8
 * @since 24/10/22
 */
public final class Start {


  private Start() {
  }

  public static void main(String[] args) {
    try {
      final SplashScreen splashscreen = new SplashScreen();
      Program.start();
      checkProgramParameters(args);

      Thread.setDefaultUncaughtExceptionHandler((t, e) -> Program.showException(e, true));

      while (splashscreen.isRunning()) {
      }

      SwingUtilities.invokeLater(() -> MainFrame.getInstance().initFrame());
    } catch (UnableToOpenFileException e) {
      Program.showException(e);
      System.exit(998);
    } catch (ExceptionInInitializerError a) {
      JOptionPane.showMessageDialog(null, "Error during program initialisation!!\nProgram files corrupted!!\nPlease reinstall the program.",
          "Error", JOptionPane.ERROR_MESSAGE);
      System.exit(999);
    }
  }
  
  private static void checkProgramParameters(String[] args) {
    String parameters = "";

    for (String arg : args) {
      parameters = parameters.concat(arg + SPACE);
    }
    if (!parameters.isBlank()) {
      int nIndex = parameters.indexOf(OPTIONS_PARAM);
      if (nIndex == -1) {
        // demarrage sans options
        Program.setNewFile(toCleanString(parameters));
      } else {
        // demarrage avec options
        // ______________________
        String tmp = parameters.substring(0, nIndex);
        // Recuperation du nom du fichier
        if (tmp.contains(ONE_DOT + EXTENSION_SINFO)) {
          Program.setNewFile(tmp.strip());
        } else {
          // On prend tout ce qu'il y a apres -opts
          tmp = parameters.substring(nIndex);
          if (tmp.contains(ONE_DOT + EXTENSION_SINFO)) {
            // Si l'on trouve l'extension du fichier
            // on cherche le caractere ' ' qui va separer les
            // options du nom du fichier
            String tmp2 = tmp.strip();
            tmp2 = tmp2.substring(tmp2.indexOf(SPACE));
            Program.setNewFile(tmp2.strip());
          }
        }
        // Recuperation des options
        tmp = parameters.substring(nIndex + OPTIONS_PARAM.length()).strip().toLowerCase();
        if (tmp.indexOf(' ') != -1) {
          tmp = tmp.substring(0, tmp.indexOf(' ')).strip();
        }
        // Options a gerer
        if (RESTART_COMMAND.equals(tmp)) {
          // Demarrage avec une nouvelle cave
          Program.putGlobalConfigBool(MyCellarSettings.GLOBAL_STARTUP, false);
          Program.putCaveConfigBool(MyCellarSettings.HAS_YEAR_CTRL, true);
          Program.putCaveConfigBool(MyCellarSettings.HAS_EXCEL_FILE, false);
        } else if (DOWNLOAD_COMMAND.equals(tmp)) {
          Program.Debug("Download a new version and exit");
          MyCellarServer.getInstance().downloadVersion();
          System.exit(3);
        }
      }
    }
  }

}
