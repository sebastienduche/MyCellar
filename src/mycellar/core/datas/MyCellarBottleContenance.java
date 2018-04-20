package mycellar.core.datas;

import mycellar.Bouteille;
import mycellar.Program;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;

/**
 * <p>Titre : Cave à vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2018</p>
 * <p>Société : Seb Informatique</p>
 * @author Sébastien Duché
 * @version 0.1
 * @since 20/04/18
 */
public final class MyCellarBottleContenance {

  private LinkedList<String> list;
  private String defaultValue;

  private MyCellarBottleContenance() {
  }

  public static void load() {
    getInstance().loadFile();
  }

  private void loadFile() {
    readTypesXml();
    if(list == null) {
      list = new LinkedList<>();
      if(Program.getStorage().getAllList() != null) {
        for(Bouteille b : Program.getStorage().getAllList()) {
          String type = b.getType();
          if(type != null && !type.isEmpty() && !list.contains(type))
            list.add(type);
        }
      }
      defaultValue = "75cl";
    }

    if(list.isEmpty()) {
      list.add("75cl");
      list.add("37.5cl");
      defaultValue = "75cl";
    }
  }

  public static void save() {
    getInstance().writeTypeXml();
  }

  private void writeTypeXml() {

    Debug("writeTypeXml: Writing file");
    String filename = Program.getXMLTypesFileName();
    try (FileWriter fw = new FileWriter(filename)){
      //Init XML File
      fw.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
      // Racine XML
      fw.write("<MyCellar>");
      // Ecriture des types
      for (String type: list){
        if(type.equals(defaultValue))
          fw.write("<type value=\""+type+"\" default=\"true\"/>");
        else
          fw.write("<type value=\""+type+"\"/>");
      }
      fw.write("</MyCellar>");
      fw.flush();
      fw.close();
    }
    catch (IOException ex) {
      Program.showException(ex);
    }
    Debug("writeTypeXml: Writing file OK");
  }

  public static LinkedList<String> getList() {
    return getInstance().list;
  }

  public static String getDefaultValue() {
    return getInstance().defaultValue;
  }

  public static void setDefaultValue(String defaultValue) {
    getInstance().defaultValue = defaultValue;
  }

  private void readTypesXml()  {

    Debug("readTypesXml: Reading file");
    File file = new File(Program.getXMLTypesFileName());
    if(!file.exists()) {
      Debug("WARNING: file '"+Program.getXMLTypesFileName()+"' not found!");
      return;
    }

    list = new LinkedList<>();
    try {
      DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
      DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
      Document doc = dBuilder.parse(new File(Program.getXMLTypesFileName()));
      doc.getDocumentElement().normalize();

      NodeList types = doc.getElementsByTagName("type");

      for (int i = 0; i < types.getLength(); i++) {
        Node nNode = types.item(i);

        if (nNode.getNodeType() == Node.ELEMENT_NODE) {
          Element type = (Element) nNode;
          // Récupération des noeuds des types
          String value = type.getAttribute("value");
          if (null != value && !value.isEmpty()) {
            if (type.hasAttribute("default"))
              defaultValue = value;
            if (!list.contains(value))
              list.add(value);
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

  /**
   * Debug
   *
   * @param sText String
   */
  public static void Debug(String sText) {
    Program.Debug("MyCellarBottleContenance: " + sText);
  }

  private static class LazyHolder {
    private static final MyCellarBottleContenance INSTANCE = new MyCellarBottleContenance();
  }

  public static MyCellarBottleContenance getInstance() {
    return LazyHolder.INSTANCE;
  }
}
