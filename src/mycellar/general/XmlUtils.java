package mycellar.general;

import mycellar.MyCellarUtils;
import mycellar.Program;
import mycellar.core.IMyCellarObject;
import mycellar.core.LabelProperty;
import mycellar.placesmanagement.Part;
import mycellar.placesmanagement.Rangement;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import static mycellar.MyCellarUtils.isNullOrEmpty;
import static mycellar.ProgramConstants.SPACE;

/**
 * <p>Titre : </p>
 * <p>Description : </p>
 * <p>Copyright : Copyright (c) 2006</p>
 * <p>Soci&eacute;t&eacute; : SebInformatique</p>
 *
 * @author S&eacute;bastien Duch&eacute;
 * @version 3.3
 * @since 07/01/22
 */

public class XmlUtils {

  public static String getTextContent(NodeList nodeList) {
    return getTextContent(nodeList, "");
  }

  public static String getTextContent(NodeList nodeList, String defaultValue) {
    if (nodeList.getLength() > 0) {
      return nodeList.item(0).getTextContent();
    }
    return defaultValue;
  }

  /**
   * readMyCellarXml: Lit le fichier MyCellar.xml des rangements
   */
  public static boolean readMyCellarXml(String filename, final List<Rangement> rangementList) {
    Debug("readMyCellarXml: Reading file");
    rangementList.clear();
    if (isNullOrEmpty(filename)) {
      filename = Program.getXMLPlacesFileName();
    }

    File file = new File(filename);
    if (!file.exists()) {
      return false;
    }

    try {
      final var dbFactory = DocumentBuilderFactory.newInstance();
      final var dBuilder = dbFactory.newDocumentBuilder();
      final var doc = dBuilder.parse(file);
      doc.getDocumentElement().normalize();

      NodeList places = doc.getElementsByTagName("place");

      LinkedList<String> names = new LinkedList<>();
      for (int i = 0; i < places.getLength(); i++) {
        Node nNode = places.item(i);

        if (nNode.getNodeType() == Node.ELEMENT_NODE) {

          Element place = (Element) nNode;
          boolean bIsCaisse = Boolean.parseBoolean(place.getAttribute("IsCaisse"));
          final String aDefault = place.getAttribute("default");
          boolean isDefault = !aDefault.isBlank() && Boolean.parseBoolean(aDefault);
          int nPlace = Integer.parseInt(place.getAttribute("NbPlace"));
          String sName = place.getAttribute("name");
          if (sName.isEmpty()) {
            NodeList placeName = place.getElementsByTagName("name");
            sName = placeName.item(0).getTextContent();
          }
          if (bIsCaisse) {
            // C'est une caisse
            int nNumStart = Integer.parseInt(place.getAttribute("NumStart"));
            int nNbLimit = Integer.parseInt(place.getAttribute("NbLimit"));
            if (names.contains(sName)) {
              Debug("WARNING: Rangement name '" + sName + "' already used!");
            } else {
              boolean bLimit = (nNbLimit > 0);
              final Rangement caisse = new Rangement.SimplePlaceBuilder(sName)
                  .nbParts(nPlace)
                  .startSimplePlace(nNumStart)
                  .limited(bLimit)
                  .limit(nNbLimit)
                  .setDefaultPlace(isDefault).build();
              rangementList.add(caisse);
              names.add(sName);
            }
          } else {
            // C'est un rangement complexe
            // ___________________________

            final LinkedList<Part> listPart = new LinkedList<>();
            NodeList internalPlaces = place.getElementsByTagName("internal-place");
            for (int j = 0; j < internalPlaces.getLength(); j++) {
              Node nInternal = internalPlaces.item(j);
              if (nInternal.getNodeType() == Node.ELEMENT_NODE) {
                Part part = new Part(j);
                listPart.add(part);
                Element iPlace = (Element) nInternal;
                int nLine = Integer.parseInt(iPlace.getAttribute("NbLine"));
                part.setRows(nLine);
                NodeList Line = iPlace.getElementsByTagName("line");
                for (int k = 0; k < Line.getLength(); k++) {
                  Node nTempLine = Line.item(k);
                  if (nTempLine.getNodeType() == Node.ELEMENT_NODE) {
                    Element oLine = (Element) nTempLine;
                    int nColumn = Integer.parseInt(oLine.getAttribute("NbColumn"));
                    part.getRow(k).setCol(nColumn);
                  }
                }
              }
            }

            if (names.contains(sName)) {
              Debug("WARNING: Rangement name '" + sName + "' already used!");
            } else {
              names.add(sName);
              rangementList.add(new Rangement(sName, listPart));
            }
          }

        }
      }
    } catch (IOException e) {
      Debug("IOException");
      Program.showException(e, false);
      return false;
    } catch (ParserConfigurationException e) {
      Debug("ParserConfigurationException");
      Program.showException(e, false);
      return false;
    } catch (SAXException e) {
      Debug("SAXException");
      Program.showException(e, false);
      return false;
    }

    Debug("readMyCellarXml: Reading file OK");
    return true;
  }

  /**
   * writeMyCellarXml
   *
   * @param rangements LinkedList<Rangement>
   */
  public static void writeMyCellarXml(List<Rangement> rangements, String filename) {
    Debug("writeMyCellarXml: Writing file");
    if (isNullOrEmpty(filename)) {
      filename = Program.getXMLPlacesFileName();
    }
    try (var fileWriter = new FileWriter(filename)) {
      //Init XML File
      fileWriter.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<MyCellar>");
      // Ecriture des rangements
      for (Rangement r : rangements) {
        fileWriter.write(r.toXml());
      }
      fileWriter.write("</MyCellar>");
      fileWriter.flush();
    } catch (IOException ex) {
      Program.showException(ex);
    }
    Debug("writeMyCellarXml: Writing file OK");
  }

  /**
   * Writes content of places in HTML file
   */
  public static void writePlacesToHTML(String filename, List<Rangement> rangements, boolean preview) {
    Debug("writePlacesToHTML: Writing file");
    writePlacesToXML(Program.getPreviewXMLFileName(), rangements, preview);
    TransformerFactory tFactory = TransformerFactory.newInstance();

    StreamSource xslDoc = new StreamSource("resources/Rangement.xsl");
    StreamSource xmlDoc = new StreamSource(Program.getPreviewXMLFileName());

    if (isNullOrEmpty(filename)) {
      filename = Program.getPreviewHTMLFileName();
    }

    try (var htmlFile = new FileOutputStream(filename)) {
      var transformer = tFactory.newTransformer(xslDoc);
      transformer.transform(xmlDoc, new StreamResult(htmlFile));
    } catch (FileNotFoundException e1) {
      Debug("ERROR: File not found : " + e1.getMessage());
      Program.showException(e1);
    } catch (IOException | TransformerException e1) {
      Program.showException(e1);
    }
    Debug("writePlacesToHTML: Writing file Done");
  }

  /**
   * Writes content of places in XML file
   */
  public static void writePlacesToXML(String filename, List<Rangement> places, boolean preview) {
    Debug("writePlacesToXML: Writing file");
    if (isNullOrEmpty(filename)) {
      filename = Program.getPreviewXMLFileName();
    }
    try {
      final var dbFactory = DocumentBuilderFactory.newInstance();
      final var dBuilder = dbFactory.newDocumentBuilder();
      final var doc = dBuilder.newDocument();
      // root element
      Element root = doc.createElement("cave");
      doc.appendChild(root);

      Element rootDoc = doc.getDocumentElement();
      String dir = MyCellarUtils.convertToHTMLString(System.getProperty("user.dir"));

      Node pi = doc.createProcessingInstruction
          ("xml-stylesheet", "type=\"text/xsl\" href=\"" + dir + "/resources/Rangement.xsl\"");
      doc.insertBefore(pi, rootDoc);

      for (Rangement rangement : places) {
        Element r = doc.createElement("rangement");
        root.appendChild(r);
        Element name = doc.createElement("name");
        name.setTextContent(rangement.getName());
        r.appendChild(name);

        if (rangement.isSimplePlace()) {
          r.setAttribute("columns", "1");
          for (int i = 0; i < rangement.getNbParts(); i++) {
            Element partie = doc.createElement("partie");
            r.appendChild(partie);
            name = doc.createElement("nom-partie");
            name.setTextContent(Program.getLabel("Infos029") + SPACE + (i + rangement.getStartSimplePlace()));
            partie.appendChild(name);
            Element caisse = doc.createElement("caisse");
            partie.appendChild(caisse);
            for (int j = 0; j < rangement.getTotalCellUsed(i); j++) {
              Element vin = doc.createElement("vin");
              caisse.appendChild(vin);
              Element vin_name = doc.createElement("vin1");
              vin.appendChild(vin_name);
              if (preview) {
                vin_name.setTextContent(Program.getLabel("MyXmlDom.bottleHere", LabelProperty.A_SINGLE.withCapital()));
              } else {
                IMyCellarObject b = rangement.getObjectSimplePlaceAt(i, j);
                if (b != null)
                  vin_name.setTextContent(b.getNom());
                else
                  vin_name.setTextContent("-");
              }
            }
          }
        } else {
          r.setAttribute("columns", Integer.toString(rangement.getMaxColumCountAt()));
          for (int i = 0; i < rangement.getNbParts(); i++) {
            Element partie = doc.createElement("partie");
            r.appendChild(partie);
            name = doc.createElement("nom-partie");
            name.setTextContent(Program.getLabel("Infos029") + SPACE + (i + rangement.getStartSimplePlace() + 1));
            partie.appendChild(name);
            int lig = rangement.getLineCountAt(i);
            for (int j = 0; j < lig; j++) {
              int col = rangement.getColumnCountAt(i, j);
              Element ligne = doc.createElement("ligne");
              partie.appendChild(ligne);
              for (int k = 0; k < col; k++) {
                Element vin = doc.createElement("vin");
                ligne.appendChild(vin);
                Element vin_name = doc.createElement("vin1");
                vin.appendChild(vin_name);
                if (preview) {
                  vin_name.setTextContent(Program.getLabel("MyXmlDom.bottleHere", LabelProperty.A_SINGLE.withCapital()));
                } else {
                  rangement.getObject(i, j, k)
                      .ifPresentOrElse(myCellarObject -> vin_name.setTextContent(myCellarObject.getNom()), () -> vin_name.setTextContent("-"));
                }
              }
            }
          }
        }
      }

      var transformerFactory = TransformerFactory.newInstance();
      var transformer = transformerFactory.newTransformer();
      var source = new DOMSource(doc);
      var result = new StreamResult(new File(filename));
      transformer.transform(source, result);
    } catch (ParserConfigurationException e) {
      Debug("ParserConfigurationException");
      Program.showException(e, false);
    } catch (TransformerException e) {
      Debug("TransformerException");
      Program.showException(e, false);
    }
    Debug("writePlacesToXML: Writing file Done");
  }

  public static boolean writeXML(Object o, File f, Class<?> classe) {
    Debug("Writing JAXB File");
    try {
      JAXBContext jc = JAXBContext.newInstance(classe);
      Marshaller m = jc.createMarshaller();
      m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
      m.marshal(o, new StreamResult(f));
    } catch (JAXBException e) {
      Program.showException(e);
      return false;
    }
    Debug("Writing JAXB File Done");
    return true;
  }

  /**
   * Debug
   *
   * @param sText String
   */
  private static void Debug(String sText) {
    Program.Debug("MyXmlDom: " + sText);
  }

}
