package mycellar;

/**
 * <p>Titre : Cave � vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 1998</p>
 * <p>Soci�t� : Seb Informatique</p>
 * @author S�bastien Duch�
 * @version 0.1
 * @since 17/04/05
 */

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.DataFlavor;
import java.awt.Toolkit;

public class MyClipBoard {

  /**
   * MyClipBoard: Constructeur par d�faut.
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
    String text = null;
    try {
      text = (String) content.getTransferData(DataFlavor.stringFlavor);
    }
    catch (Throwable exc) {}
    if (text == null) {
      text = "";
    }
    return text;
  }

}
