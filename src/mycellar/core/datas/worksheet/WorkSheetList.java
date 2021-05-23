//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2017.05.19 at 01:50:00 PM CEST 
//


package mycellar.core.datas.worksheet;

import mycellar.general.XmlUtils;
import mycellar.Program;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>Titre : Cave &agrave; vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 1998</p>
 * <p>Soci&eacute;t&eacute; : Seb Informatique</p>
 * @author S&eacute;bastien Duch&eacute;
 * @version 0.3
 * @since 28/01/21

 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "worksheet"
})
@XmlRootElement(name = "WorksheetList")
public class WorkSheetList {

  @XmlElement(name = "worksheet", required = true)
  private List<WorkSheetData> worksheet;


  public List<WorkSheetData> getWorsheet() {
    if (worksheet == null) {
      worksheet = new ArrayList<>();
    }
    return worksheet;
  }

  public void add(WorkSheetData workSheetData) {
    getWorsheet().add(workSheetData);
  }

  public void remove(WorkSheetData workSheetData) {
    getWorsheet().remove(workSheetData);
  }

  public void clear() {
    getWorsheet().clear();
  }

  public static boolean loadXML(File f) {
    Debug("Loading XML File " + f.getAbsolutePath());
    if (!f.exists()) {
      return false;
    }
    try {
      unMarshalXML(f);
      return true;
    } catch (FileNotFoundException | JAXBException e) {
      Debug("ERROR: Unable to Unmarshall JAXB File");
      Program.showException(e, false);
    } catch (RuntimeException e) {
      Debug("ERROR: Unexpected Error");
      Program.showException(e, false);
    }
    Debug("Manual loading of the XML file");
    try {
      manualLoadXML(f);
    } catch (ParserConfigurationException | IOException | SAXException e) {
      Debug("ERROR: Unable to load manually the File");
      Program.showException(e);
      return false;
    }
    return true;
  }

  private static void manualLoadXML(File f) throws ParserConfigurationException, IOException, SAXException {
    DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
    DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
    Document doc = dBuilder.parse(f);
    doc.getDocumentElement().normalize();

    WorkSheetList listeWorksheet = Program.getWorksheetList();
    NodeList worksheet = doc.getElementsByTagName("worksheet");

    for (int i = 0; i < worksheet.getLength(); i++) {
      Node node = worksheet.item(i);

      if (node.getNodeType() == Node.ELEMENT_NODE) {
        WorkSheetData workSheetData = new WorkSheetData();
        Element worksheetElem = (Element) node;
        final NodeList bouteilleElem = worksheetElem.getElementsByTagName("bouteilleId");
        String id = bouteilleElem.item(0).getTextContent();
        workSheetData.setBouteilleId(Integer.parseInt(id));
        listeWorksheet.getWorsheet().add(workSheetData);
      }
    }
    Debug("Loading Manually File Done");
  }

  private static void unMarshalXML(File f) throws JAXBException, FileNotFoundException {
    JAXBContext jc = JAXBContext.newInstance(WorkSheetFactory.class);
    Unmarshaller u = jc.createUnmarshaller();
    WorkSheetList lb =
        (WorkSheetList)u.unmarshal(new FileInputStream(f));
    Program.getStorage().getWorksheetList().getWorsheet().addAll(lb.getWorsheet());
    Debug("Loading JAXB File Done");
  }

  public static void writeXML(File f) {
    XmlUtils.writeXML(Program.getWorksheetList(), f, WorkSheetFactory.class);
  }

  /**
   * Debug
   *
   * @param sText String
   */
  private static void Debug(String sText) {
    Program.Debug("WorksheetList: " + sText);
  }
}
