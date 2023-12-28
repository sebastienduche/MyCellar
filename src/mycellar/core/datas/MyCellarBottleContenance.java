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
import java.util.function.Predicate;

import static mycellar.MyCellarUtils.isDefined;
import static mycellar.ProgramConstants.HALF;
import static mycellar.ProgramConstants.NORMAL;

/**
 * <p>Titre : Cave &agrave; vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2018</p>
 * <p>Soci&eacute;t&eacute; : Seb Informatique</p>
 *
 * @author S&eacute;bastien Duch&eacute;
 * @version 0.9
 * @since 04/01/22
 */
public final class MyCellarBottleContenance {

  private final LinkedList<String> list = new LinkedList<>();
  private String defaultValue;

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
        .map(myCellarObject -> (Bouteille) myCellarObject)
        .map(Bouteille::getKind)
        .anyMatch(value::equals);
  }

  public static void rename(String oldValue, String newValue) {
    Program.getStorage().getAllList()
        .stream()
        .filter(b -> oldValue.equals(b.getKind()))
        .map(myCellarObject -> (Bouteille) myCellarObject)
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

  private static void Debug(String sText) {
    Program.Debug("MyCellarBottleContenance: " + sText);
  }

  public static MyCellarBottleContenance getInstance() {
    return LazyHolder.INSTANCE;
  }

  private void loadFile() {
    readTypesXml();
    if (Program.getStorage().getAllList() != null) {
      final List<String> collect = Program.getStorage().getAllList()
          .stream()
          .map(myCellarObject -> (Bouteille) myCellarObject)
          .map(Bouteille::getKind)
          .distinct()
          .filter(Predicate.not(String::isBlank))
          .toList();
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

  private void readTypesXml() {
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
          // Get node types
          String value = type.getAttribute("value");
          if (isDefined(value)) {
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
    String filename = Program.getXMLTypesFileName();
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
      Program.showException(ex);
    }
    Debug("writeTypeXml: Writing file OK");
  }

  private static class LazyHolder {
    private static final MyCellarBottleContenance INSTANCE = new MyCellarBottleContenance();
  }
}
