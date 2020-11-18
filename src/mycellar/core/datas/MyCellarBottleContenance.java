package mycellar.core.datas;

import mycellar.Bouteille;
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
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>Titre : Cave à vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2018</p>
 * <p>Société : Seb Informatique</p>
 * @author Sébastien Duché
 * @version 0.5
 * @since 18/11/20
 */
public final class MyCellarBottleContenance {

  private final LinkedList<String> list = new LinkedList<>();
  private String defaultValue;
  private static final String NORMAL = "75cl";
  private static final String HALF = "37.5cl";

  private MyCellarBottleContenance() {
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
        .map(Bouteille::getType)
        .anyMatch(value::equals);
  }

  public static void rename(String oldValue, String newValue) {
    Program.getStorage().getAllList()
        .stream()
        .filter(b -> oldValue.equals(b.getType()))
        .forEach(bouteille -> bouteille.setType(newValue));
    getList().remove(oldValue);
    getList().add(newValue);
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

  private void loadFile() {
    readTypesXml();
    if(Program.getStorage().getAllList() != null) {
      final List<String> collect = Program.getStorage().getAllList()
          .stream()
          .map(Bouteille::getType)
          .distinct()
          .collect(Collectors.toList());
      for (String val : collect) {
        if (!list.contains(val)) {
          list.add(val);
        }
      }
    }

    defaultValue = NORMAL;

    if (list.isEmpty()) {
      list.add(NORMAL);
      list.add(HALF);
    }
  }


  private void readTypesXml()  {
    Debug("readTypesXml: Reading file");
    File file = new File(Program.getXMLTypesFileName());
    if (!file.exists()) {
      Debug("WARNING: file '" + Program.getXMLTypesFileName() + "' not found!");
      return;
    }

    list.clear();
    try {
      var dbFactory = DocumentBuilderFactory.newInstance();
      var dBuilder = dbFactory.newDocumentBuilder();
      var doc = dBuilder.parse(new File(Program.getXMLTypesFileName()));
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
    }
    catch (IOException e) {
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
    String filename = Program.getXMLTypesFileName();
    try (var fileWriter = new FileWriter(filename)) {
      //Init XML File
      fileWriter.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<MyCellar>");
      // Ecriture des types
      for (String type: list) {
        if (type.equals(defaultValue)) {
          fileWriter.write("<type value=\"" + type + "\" default=\"true\"/>");
        } else {
          fileWriter.write("<type value=\"" + type + "\"/>");
        }
      }
      fileWriter.write("</MyCellar>");
      fileWriter.flush();
    }
    catch (IOException ex) {
      Debug("IOException");
      Program.showException(ex);
    }
    Debug("writeTypeXml: Writing file OK");
  }

  /**
   * Debug
   *
   * @param sText String
   */
  private static void Debug(String sText) {
    Program.Debug("MyCellarBottleContenance: " + sText);
  }

  private static class LazyHolder {
    private static final MyCellarBottleContenance INSTANCE = new MyCellarBottleContenance();
  }

  public static MyCellarBottleContenance getInstance() {
    return LazyHolder.INSTANCE;
  }
}
