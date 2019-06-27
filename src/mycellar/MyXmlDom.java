package mycellar;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * <p>Titre : </p>
 * <p>Description : </p>
 * <p>Copyright : Copyright (c) 2006</p>
 * <p>Société : SebInformatique</p>
 * @author Sébastien Duché
 * @since 27/06/19
 * @version 2.5
 */

public class MyXmlDom {

	/**
	 * readMyCellarXml: Lit le fichier MyCellar.xml des rangements
	 *
	 */
	static boolean readMyCellarXml(String _sFileName, final List<Rangement> rangementList) {

		Debug("readMyCellarXml: Reading file");
		rangementList.clear();
		String filename = Program.getXMLPlacesFileName();
		if (!_sFileName.isEmpty()) {
			filename = _sFileName;
		}
		LinkedList<String> names = new LinkedList<>();

		File file = new File(filename);
		if(!file.exists()) {
			return false;
		}

		try {
			final var dbFactory = DocumentBuilderFactory.newInstance();
			final var dBuilder = dbFactory.newDocumentBuilder();
			final var doc = dBuilder.parse(file);
			doc.getDocumentElement().normalize();

			NodeList places = doc.getElementsByTagName("place");

			for (int i = 0; i < places.getLength(); i++) {
				Node nNode = places.item(i);

				if (nNode.getNodeType() == Node.ELEMENT_NODE) {

					Element place = (Element) nNode;
					Boolean bIsCaisse = Boolean.parseBoolean(place.getAttribute("IsCaisse"));
					int nPlace = Integer.parseInt(place.getAttribute("NbPlace"));
					String sName = place.getAttribute("name");
					if(sName.isEmpty()) {
						NodeList placeName = place.getElementsByTagName("name");
						sName = placeName.item(0).getTextContent();
					}
					if (bIsCaisse) {
						// C'est une caisse
						int nNumStart = Integer.parseInt(place.getAttribute("NumStart"));
						int nNbLimit = Integer.parseInt(place.getAttribute("NbLimit"));
						boolean bLimit = false;
						if ( nNbLimit > 0 )
							bLimit = true;
						if(names.contains(sName)) {
							Debug("WARNING: Rangement name '"+sName+"' already used!");
						}
						else {
							final Rangement caisse = new Rangement.CaisseBuilder(sName)
									.nb_emplacement(nPlace)
									.start_caisse(nNumStart)
									.limit(bLimit)
									.limite_caisse(nNbLimit).build();
							rangementList.add(caisse);
							names.add(sName);
						}
					}
					else {
						// C'est un rangement complexe
						// ___________________________

						final LinkedList<Part> listPart = new LinkedList<>();
						NodeList internalPlaces = place.getElementsByTagName("internal-place");
						for (int j = 0; j < internalPlaces.getLength(); j++) {
							Node nInternal = internalPlaces.item(j);
							if (nInternal.getNodeType() == Node.ELEMENT_NODE) {
								Part part = new Part(i);
								listPart.add(part);
								Element iPlace = (Element)nInternal;
								int nLine = Integer.parseInt(iPlace.getAttribute("NbLine"));
								part.setRows(nLine);
								NodeList Line = iPlace.getElementsByTagName("line");
								for (int k = 0; k < Line.getLength(); k++) {
									Node nTempLine = Line.item(k);
									if (nTempLine.getNodeType() == Node.ELEMENT_NODE) {
										Element oLine = (Element)nTempLine;
										int nColumn = Integer.parseInt(oLine.getAttribute("NbColumn"));
										part.getRow(k).setCol(nColumn);
									}
								}
							}
						}

						if(names.contains(sName)) {
							Debug("WARNING: Rangement name '"+sName+"' already used!");
						} else {
							names.add(sName);
							rangementList.add(new Rangement(sName, listPart));
						}
					}

				}
			}
		}
		catch (IOException e) {
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
	 * @param _oCave LinkedList<Rangement>
	 */
	static void writeMyCellarXml(List<Rangement> _oCave, String _sFilename) {

		Debug("writeMyCellarXml: Writing file");
		String filename = Program.getXMLPlacesFileName();
		if(!_sFilename.isEmpty()) {
			filename = _sFilename;
		}
		try (var oFile = new FileWriter(filename)){
			//Init XML File
			oFile.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<MyCellar>");
			// Ecriture des rangements
			for (Rangement r : _oCave){
				if (r != null) {
					oFile.write(r.toXml());
				}
			}
			oFile.write("</MyCellar>");
			oFile.flush();
		}
		catch (IOException ex) {
			Program.showException(ex);
		}
		Debug("writeMyCellarXml: Writing file OK");
	}

	/**
	 * writeRangements: Ecriture des Rangements pour l'export XML/HTML
	 *
	 * @param filename String : Fichier à écrire
	 * @param rangements LinkedList<Rangement>: Liste des rangements à écrire
	 */
	public static void writeRangements(String filename, List<Rangement> rangements, boolean preview){
		Debug("writeRangement: Writing file");
		if (filename.isEmpty()) {
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
			String dir = Program.convertToHTMLString(System.getProperty("user.dir"));

			Node pi = doc.createProcessingInstruction
			         ("xml-stylesheet", "type=\"text/xsl\" href=\""+dir+"/resources/Rangement.xsl\"");
			doc.insertBefore(pi, rootDoc);

			for(Rangement rangement : rangements) {
				Element r = doc.createElement("rangement");
				root.appendChild(r);
				Element name = doc.createElement("name");
				name.setTextContent(rangement.getNom());
				r.appendChild(name);
				
				if(rangement.isCaisse()) {
					r.setAttribute("columns", "1");
					for (int i = 0; i < rangement.getNbEmplacements(); i++) {
						Element partie = doc.createElement("partie");
						r.appendChild(partie);
						name = doc.createElement("nom-partie");
						name.setTextContent(Program.getLabel("Infos029") + " " + (i + rangement.getStartCaisse()));
						partie.appendChild(name);
						Element caisse = doc.createElement("caisse");
						partie.appendChild(caisse);
						for(int j=0; j<rangement.getNbCaseUse(i); j++) {
							Element vin = doc.createElement("vin");
							caisse.appendChild(vin);
							Element vin_name = doc.createElement("vin1");
							vin.appendChild(vin_name);
							if(preview) {
								vin_name.setTextContent(Program.getLabel("Infos229"));
							}else {
    							Bouteille b = rangement.getBouteilleCaisseAt(i, j);
    							if(b != null)
    								vin_name.setTextContent(b.getNom());
    							else
    								vin_name.setTextContent("-");
							}
						}
					}
				}
				else {
					r.setAttribute("columns", Integer.toString(rangement.getNbColonnesMax()));
				for (int i = 0; i < rangement.getNbEmplacements(); i++) {
					Element partie = doc.createElement("partie");
					r.appendChild(partie);
					name = doc.createElement("nom-partie");
					name.setTextContent(Program.getLabel("Infos029") + " " + (i + rangement.getStartCaisse() + 1));
					partie.appendChild(name);
					int lig = rangement.getNbLignes(i);
					for (int j = 0; j < lig; j++) {
						int col = rangement.getNbColonnes(i, j);
						Element ligne = doc.createElement("ligne");
						partie.appendChild(ligne);
						for (int k = 0; k < col; k++) {
							Element vin = doc.createElement("vin");
							ligne.appendChild(vin);
							Element vin_name = doc.createElement("vin1");
							vin.appendChild(vin_name);
							if(preview) {
								vin_name.setTextContent(Program.getLabel("Infos229"));
							}else {
    							Bouteille b = rangement.getBouteille(i, j, k);
    							if(b != null)
    								vin_name.setTextContent(b.getNom());
    							else
    								vin_name.setTextContent("-");
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
