package mycellar.importer;

import mycellar.Music;
import mycellar.core.common.MyCellarFields;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ItunesLibraryImporter {

  private static final Map<String, MusicInfo> MUSIC_INFO_MAP = new HashMap<>();
  static {
    MUSIC_INFO_MAP.put("Track ID", new MusicInfo("integer", null));
    MUSIC_INFO_MAP.put("Track Number", new MusicInfo("integer", null));
    MUSIC_INFO_MAP.put("Year", new MusicInfo("integer", MyCellarFields.YEAR));
    MUSIC_INFO_MAP.put("Disc Number", new MusicInfo("integer", MyCellarFields.DISK_NUMBER));
    MUSIC_INFO_MAP.put("Disc Count", new MusicInfo("integer", MyCellarFields.DISK_COUNT));
    MUSIC_INFO_MAP.put("Size", new MusicInfo("integer", null));
    MUSIC_INFO_MAP.put("Rating", new MusicInfo("integer", MyCellarFields.RATING));
    MUSIC_INFO_MAP.put("Name", new MusicInfo("string", MyCellarFields.NAME));
    MUSIC_INFO_MAP.put("Kind", new MusicInfo("string", MyCellarFields.SUPPORT));
    MUSIC_INFO_MAP.put("Genre", new MusicInfo("string", MyCellarFields.STYLE));
    MUSIC_INFO_MAP.put("Total Time", new MusicInfo("integer", MyCellarFields.DURATION));
    MUSIC_INFO_MAP.put("Date Modified", new MusicInfo("date", null));
    MUSIC_INFO_MAP.put("Date Added", new MusicInfo("date", null));
    MUSIC_INFO_MAP.put("Location", new MusicInfo("string", MyCellarFields.FILE));
    MUSIC_INFO_MAP.put("Artist", new MusicInfo("string", MyCellarFields.ARTIST));
    MUSIC_INFO_MAP.put("Album", new MusicInfo("string", null));
    MUSIC_INFO_MAP.put("Composer", new MusicInfo("string", MyCellarFields.COMPOSER));
  }
  List<Music> list = new LinkedList<>();

  static class MusicInfo {
    private final String type;
    private final MyCellarFields field;

    public MusicInfo(String type, MyCellarFields field) {
      this.type = type;
      this.field = field;
    }

    public String getType() {
      return type;
    }

    public MyCellarFields getField() {
      return field;
    }
  }

  public List<Music> loadItunesLibrary(File file)  {
    if (!file.exists()) {
      return Collections.emptyList();
    }

    try {
      var dbFactory = DocumentBuilderFactory.newInstance();
      var dBuilder = dbFactory.newDocumentBuilder();
      var doc = dBuilder.parse(file);
      doc.getDocumentElement().normalize();

      final Node deepestDictElement = findDeepestDictElement(doc.getDocumentElement());
      final Node parentDictNode = deepestDictElement.getParentNode();
      NodeList dict = ((Element)parentDictNode).getElementsByTagName("dict");

      for (int i = 0; i < dict.getLength(); i++) {
        Node dictElement = dict.item(i);

        if (dictElement.getNodeType() == Node.ELEMENT_NODE) {
          Music music = new Music();
          Element musicElement = (Element) dictElement;
          // Récupération des noeuds des dict
          final NodeList key = musicElement.getElementsByTagName("key");
          for (int j = 0; j < key.getLength(); j++) {
            Node keyNode = key.item(j);
            setValueToMusic(keyNode, music);
          }
          list.add(music);
        }
      }
    } catch (IOException | SAXException | ParserConfigurationException ignored) {
    }
    return list;
  }

  private void setValueToMusic(Node keyNode, Music music) {
    final MusicInfo musicInfo = MUSIC_INFO_MAP.get(keyNode.getTextContent());
    if (musicInfo != null && musicInfo.getField() != null) {
      music.setValue(musicInfo.getField(), keyNode.getNextSibling().getTextContent());
    }
  }

  private Node findDeepestDictElement(Node element) {
    final NodeList dict = element.getChildNodes();
    for (int i = 0; i < dict.getLength(); i++) {
      if (dict.item(i).getNodeName().equals("dict")) {
        return findDeepestDictElement(dict.item(i));
      }
    }
    return element;
  }


}
