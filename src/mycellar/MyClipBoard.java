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
 * @version 0.2
 * @since 16/12/21
 */
public class MyClipBoard {

  public MyClipBoard() {
  }

  public void copy(String text) {
    if (text == null) {
      return;
    }
    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
    StringSelection contents = new StringSelection(text);
    clipboard.setContents(contents, null);
  }

  public String paste() {
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
