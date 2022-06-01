package mycellar.general;

import mycellar.MyCellarUtils;
import mycellar.Program;
import mycellar.core.IMyCellarObject;
import mycellar.core.text.LabelProperty;
import mycellar.placesmanagement.Part;
import mycellar.placesmanagement.places.AbstractPlace;
import mycellar.placesmanagement.places.ComplexPlace;
import mycellar.placesmanagement.places.ComplexPlaceBuilder;
import mycellar.placesmanagement.places.SimplePlace;
import mycellar.placesmanagement.places.SimplePlaceBuilder;
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
import static mycellar.core.text.MyCellarLabelManagement.getLabel;

/**
 * Titre : Cave &agrave; vin
 * Description : Votre description
 * Copyright : Copyright (c) 2006
 * Soci&eacute;t&eacute; : Seb Informatique
 *
 * @author S&eacute;bastien Duch&eacute;
 * @version 3.8
 * @since 01/06/22
 */

public class XmlUtils {

  public static final String PLACE = "place";
  public static final String IS_CAISSE = "IsCaisse";
  public static final String DEFAULT = "default";
  public static final String NB_PLACE = "NbPlace";
  public static final String NAME = "name";
  public static final String NUM_START = "NumStart";
  public static final String NB_LIMIT = "NbLimit";
  public static final String INTERNAL_PLACE = "internal-place";
  public static final String NB_LINE = "NbLine";
  public static final String LINE = "line";
  public static final String NB_COLUMN = "NbColumn";
  public static final String CAVE = "cave";
  public static final String RANGEMENT = "rangement";
  public static final String COLUMNS = "columns";
  public static final String PARTIE = "partie";
  public static final String NOM_PARTIE = "nom-partie";
  public static final String CAISSE = "caisse";
  public static final String VIN = "vin";
  public static final String VIN_1 = "vin1";
  public static final String LIGNE = "ligne";

  public static String getTextContent(NodeList nodeList) {
    return getTextContent(nodeList, "");
  }

  public static String getTextContent(NodeList nodeList, String defaultValue) {
    if (nodeList.getLength() > 0) {
      return nodeList.item(0).getTextContent();
    }
    return defaultValue;
  }

  public static boolean readMyCellarXml(String filename, final List<AbstractPlace> rangementList) {
    Debug("readMyCellarXml1: Reading file");
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

      NodeList places = doc.getElementsByTagName(PLACE);

      LinkedList<String> names = new LinkedList<>();
      for (int i = 0; i < places.getLength(); i++) {
        Node nNode = places.item(i);

        if (nNode.getNodeType() == Node.ELEMENT_NODE) {

          Element place = (Element) nNode;
          boolean bIsCaisse = Boolean.parseBoolean(place.getAttribute(IS_CAISSE));
          final String aDefault = place.getAttribute(DEFAULT);
          boolean isDefault = !aDefault.isBlank() && Boolean.parseBoolean(aDefault);
          int nPlace = Integer.parseInt(place.getAttribute(NB_PLACE));
          String placeName = place.getAttribute(NAME);
          if (placeName.isEmpty()) {
            NodeList placeNameList = place.getElementsByTagName(NAME);
            placeName = placeNameList.item(0).getTextContent();
          }
          if (bIsCaisse) {
            // C'est une caisse
            int nNumStart = Integer.parseInt(place.getAttribute(NUM_START));
            int nNbLimit = Integer.parseInt(place.getAttribute(NB_LIMIT));
            if (names.contains(placeName)) {
              Debug("WARNING: Place name '" + placeName + "' already used!");
            } else {
              boolean bLimit = (nNbLimit > 0);
              final SimplePlace caisse = new SimplePlaceBuilder(placeName)
                  .nbParts(nPlace)
                  .startSimplePlace(nNumStart)
                  .limited(bLimit)
                  .limit(nNbLimit)
                  .setDefaultPlace(isDefault).build();
              rangementList.add(caisse);
              names.add(placeName);
            }
          } else {
            // C'est un rangement complexe
            // ___________________________

            final LinkedList<Part> listPart = new LinkedList<>();
            NodeList internalPlaces = place.getElementsByTagName(INTERNAL_PLACE);
            for (int j = 0; j < internalPlaces.getLength(); j++) {
              Node nInternal = internalPlaces.item(j);
              if (nInternal.getNodeType() == Node.ELEMENT_NODE) {
                Part part = new Part(j);
                listPart.add(part);
                Element iPlace = (Element) nInternal;
                int nLine = Integer.parseInt(iPlace.getAttribute(NB_LINE));
                part.setRows(nLine);
                NodeList Line = iPlace.getElementsByTagName(LINE);
                for (int k = 0; k < Line.getLength(); k++) {
                  Node nTempLine = Line.item(k);
                  if (nTempLine.getNodeType() == Node.ELEMENT_NODE) {
                    Element oLine = (Element) nTempLine;
                    int nColumn = Integer.parseInt(oLine.getAttribute(NB_COLUMN));
                    part.getRow(k).setCol(nColumn);
                  }
                }
              }
            }

            if (names.contains(placeName)) {
              Debug("WARNING: Place name '" + placeName + "' already used!");
            } else {
              names.add(placeName);
              ComplexPlace complexPlace = new ComplexPlaceBuilder(placeName)
                  .withPartList(listPart).build();
              rangementList.add(complexPlace);
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

    Debug("readMyCellarXml1: Reading file OK");
    return true;
  }

  /**
   * writeMyCellarXml
   *
   * @param rangements LinkedList<IBasicPlace>
   */
  public static void writeMyCellarXml(List<? extends AbstractPlace> rangements, String filename) {
    Debug("writeMyCellarXml: Writing file");
    if (isNullOrEmpty(filename)) {
      filename = Program.getXMLPlacesFileName();
    }
    try (var fileWriter = new FileWriter(filename)) {
      //Init XML File
      fileWriter.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<MyCellar>");
      // Ecriture des rangements
      for (AbstractPlace r : rangements) {
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
  public static void writePlacesToHTML(String filename, List<AbstractPlace> rangements, boolean preview) {
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
  public static void writePlacesToXML(String filename, List<AbstractPlace> places, boolean preview) {
    Debug("writePlacesToXML: Writing file");
    if (isNullOrEmpty(filename)) {
      filename = Program.getPreviewXMLFileName();
    }
    try {
      final var dbFactory = DocumentBuilderFactory.newInstance();
      final var dBuilder = dbFactory.newDocumentBuilder();
      final var doc = dBuilder.newDocument();
      // root element
      Element root = doc.createElement(CAVE);
      doc.appendChild(root);

      Element rootDoc = doc.getDocumentElement();
      String dir = MyCellarUtils.convertToHTMLString(System.getProperty("user.dir"));

      Node pi = doc.createProcessingInstruction
          ("xml-stylesheet", "type=\"text/xsl\" href=\"" + dir + "/resources/Rangement.xsl\"");
      doc.insertBefore(pi, rootDoc);

      for (AbstractPlace rangement : places) {
        Element r = doc.createElement(RANGEMENT);
        root.appendChild(r);
        Element name = doc.createElement(NAME);
        name.setTextContent(rangement.getName());
        r.appendChild(name);

        if (rangement.isSimplePlace()) {
          r.setAttribute(COLUMNS, "1");
          for (int i = 0; i < rangement.getPartCount(); i++) {
            Element partie = doc.createElement(PARTIE);
            r.appendChild(partie);
            name = doc.createElement(NOM_PARTIE);
            name.setTextContent(getLabel("Storage.Shelve") + SPACE + (i + ((SimplePlace) rangement).getPartNumberIncrement()));
            partie.appendChild(name);
            Element caisse = doc.createElement(CAISSE);
            partie.appendChild(caisse);
            for (int j = 0; j < rangement.getCountCellUsed(i); j++) {
              Element vin = doc.createElement(VIN);
              caisse.appendChild(vin);
              Element vin_name = doc.createElement(VIN_1);
              vin.appendChild(vin_name);
              if (preview) {
                vin_name.setTextContent(getLabel("MyXmlDom.ItemHere", LabelProperty.A_SINGLE.withCapital()));
              } else {
                IMyCellarObject b = ((SimplePlace) rangement).getObjectAt(i, j);
                if (b != null)
                  vin_name.setTextContent(b.getNom());
                else
                  vin_name.setTextContent("-");
              }
            }
          }
        } else {
          ComplexPlace complexPlace = (ComplexPlace) rangement;
          r.setAttribute(COLUMNS, Integer.toString(complexPlace.getMaxColumCount()));
          for (int i = 0; i < rangement.getPartCount(); i++) {
            Element partie = doc.createElement(PARTIE);
            r.appendChild(partie);
            name = doc.createElement(NOM_PARTIE);
            name.setTextContent(getLabel("Storage.Shelve") + SPACE + i + 1);
            partie.appendChild(name);
            int lig = complexPlace.getLineCountAt(i);
            for (int j = 0; j < lig; j++) {
              int col = complexPlace.getColumnCountAt(i, j);
              Element ligne = doc.createElement(LIGNE);
              partie.appendChild(ligne);
              for (int k = 0; k < col; k++) {
                Element vin = doc.createElement(VIN);
                ligne.appendChild(vin);
                Element vin_name = doc.createElement(VIN_1);
                vin.appendChild(vin_name);
                if (preview) {
                  vin_name.setTextContent(getLabel("MyXmlDom.ItemHere", LabelProperty.A_SINGLE.withCapital()));
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

  private static void Debug(String sText) {
    Program.Debug("MyXmlDom: " + sText);
  }

}
