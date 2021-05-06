package test;

import mycellar.Music;
import mycellar.importer.ItunesLibraryImporter;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;

public class ItunesLibraryTest {

  @Test
  void testItunesLibrary() {
    File file = new File("/Users/sduche/Downloads/Bibliotheek.xml");
    List<Music> list = new ItunesLibraryImporter().loadItunesLibrary(file);
    list.forEach(System.out::println);
  }

}
