package mycellar;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

/**
 * <p>Titre : Cave &agrave; vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 1998</p>
 * <p>Soci&eacute;t&eacute; : Seb Informatique</p>
 *
 * @author S&eacute;bastien Duch&eacute;
 * @version 0.1
 * @since 17/04/05
 */
public class MyClipBoard {

  /**
   * MyClipBoard: Constructeur par defaut.
   */
  public MyClipBoard() {
  }

  /**
   * copier: Copie un texte.
   *
   * @param text String
   */
  public void copier(String text) {
    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
    StringSelection contents = new StringSelection(text);
    clipboard.setContents(contents, null);
  }

  /**
   * coller: Colle un texte.
   *
   * @return String
   */
  public String coller() {

    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
    Transferable content = clipboard.getContents(this);
    try {
      return (String) content.getTransferData(DataFlavor.stringFlavor);
    } catch (UnsupportedFlavorException | IOException e) {
      Program.Debug("MyClipBoard: ERROR: " + e.getMessage());
    } catch (Throwable ignored) {
    }
    return "";
  }

}
