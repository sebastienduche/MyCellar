package mycellar;

import java.io.File;
import java.io.FilenameFilter;

@Deprecated
class MyFilenameFilter implements FilenameFilter {

  @Override
  public boolean accept(File dir, String name) {
    return (name.endsWith(".ser"));
  }
}
