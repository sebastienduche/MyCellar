package mycellar.core.common.music;

import mycellar.Music;
import mycellar.Program;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>Titre : Cave à vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2018</p>
 * <p>Société : Seb Informatique</p>
 *
 * @author Sébastien Duché
 * @version 0.1
 * @since 22/08/21
 */
public final class MyCellarMusicSupport {

  private final LinkedList<String> list = new LinkedList<>();
  private String defaultValue;

  private MyCellarMusicSupport() {
  }

  public static void load() {
    getInstance().loadFile();
  }

  public static void save() {
    getInstance().writeTypeXml();
  }

  public static boolean isContenanceUsed(String value) {
    return Program.getStorage().getAllList()
        .stream()
        .map(myCellarObject -> (Music) myCellarObject)
        .map(Music::getKind)
        .anyMatch(value::equals);
  }

  public static void rename(String oldValue, String newValue) {
    Program.getStorage().getAllList()
        .stream()
        .filter(b -> oldValue.equals(b.getKind()))
        .map(myCellarObject -> (Music) myCellarObject)
        .forEach(bouteille -> bouteille.setKind(newValue));
    final int oldIndex = getList().indexOf(oldValue);
    final int index = getList().indexOf(newValue);
    if (index == -1) {
      getList().set(oldIndex, newValue);
    } else if (index != oldIndex) {
      getList().remove(oldIndex);
    }
  }

  public static List<String> getList() {
    return getInstance().list;
  }

  public static String getDefaultValue() {
    return getInstance().defaultValue;
  }

  public static void setDefaultValue(String defaultValue) {
    getInstance().defaultValue = defaultValue;
  }

  /**
   * Debug
   *
   * @param sText String
   */
  private static void Debug(String sText) {
    Program.Debug("MyCellarMusicSupport: " + sText);
  }

  public static MyCellarMusicSupport getInstance() {
    return LazyHolder.INSTANCE;
  }

  private void loadFile() {
    Arrays.asList(MusicSupport.values()).forEach(support -> list.add(support.toString()));
    readTypesXml();
    if (Program.getStorage().getAllList() != null) {
      final List<String> collect = Program.getStorage().getAllList()
          .stream()
          .map(myCellarObject -> (Music) myCellarObject)
          .map(Music::getKind)
          .map(String::trim)
          .distinct()
          .collect(Collectors.toList());
      for (String val : collect) {
        if (!list.contains(val)) {
          list.add(val);
        }
      }
      list.remove("");
    }

    defaultValue = MusicSupport.DIGITAL.toString();

  }

  private void readTypesXml() {
    Debug("readTypesXml: Reading file");
    File file = new File(Program.getXMLMusicTypesFileName());
    if (!file.exists()) {
      Debug("WARNING: file '" + Program.getXMLMusicTypesFileName() + "' not found!");
      return;
    }

    list.clear();
    try {
      var dbFactory = DocumentBuilderFactory.newInstance();
      var dBuilder = dbFactory.newDocumentBuilder();
      var doc = dBuilder.parse(new File(Program.getXMLMusicTypesFileName()));
      doc.getDocumentElement().normalize();

      NodeList types = doc.getElementsByTagName("type");

      for (int i = 0; i < types.getLength(); i++) {
        Node nNode = types.item(i);

        if (nNode.getNodeType() == Node.ELEMENT_NODE) {
          Element type = (Element) nNode;
          // Récupération des noeuds des types
          String value = type.getAttribute("value");
          if (null != value && !value.isEmpty()) {
            if (type.hasAttribute("default")) {
              defaultValue = value;
            }
            if (!list.contains(value)) {
              list.add(value);
            }
          }
        }
      }
    } catch (IOException e) {
      Debug("IOException");
      Program.showException(e, false);
    } catch (ParserConfigurationException e) {
      Debug("ParserConfigurationException");
      Program.showException(e, false);
    } catch (SAXException e) {
      Debug("SAXException");
      Program.showException(e, false);
    }
    Debug("readTypesXml: Reading file OK");
  }

  private void writeTypeXml() {
    Debug("writeTypeXml: Writing file");
    String filename = Program.getXMLMusicTypesFileName();
    try (var fileWriter = new FileWriter(filename)) {
      //Init XML File
      fileWriter.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<MyCellar>");
      // Ecriture des types
      for (String type : list) {
        if (type.equals(defaultValue)) {
          fileWriter.write("<type value=\"" + type + "\" default=\"true\"/>");
        } else {
          fileWriter.write("<type value=\"" + type + "\"/>");
        }
      }
      fileWriter.write("</MyCellar>");
      fileWriter.flush();
    } catch (IOException ex) {
      Debug("IOException");
      Program.showException(ex);
    }
    Debug("writeTypeXml: Writing file OK");
  }

  private static class LazyHolder {
    private static final MyCellarMusicSupport INSTANCE = new MyCellarMusicSupport();
  }
}
