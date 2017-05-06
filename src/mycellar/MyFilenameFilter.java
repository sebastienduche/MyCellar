package mycellar;

import java.io.*;

class MyFilenameFilter implements FilenameFilter {

  public boolean accept(File dir, String name) {

    if (name.endsWith(".ser")) {
      return true;
    }
    return false;
  }
}
