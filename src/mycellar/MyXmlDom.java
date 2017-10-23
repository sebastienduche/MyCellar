package mycellar;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Vector;

/**
 * <p>Titre : </p>
 * <p>Description : </p>
 * <p>Copyright : Copyright (c) 2006</p>
 * <p>Société : SebInformatique</p>
 * @author Sébastien Duché
 * @since 23/10/17
 * @version 1.9
 */

public class MyXmlDom {

	/**
	 * readMyCellarXml: Lit le fichier MyCellar.xml des rangements
	 *
	 * @return LinkedList<Rangement> Liste de rangements
	 */
	public static LinkedList<Rangement> readMyCellarXml(String _sFileName) {

		Debug("readMyCellarXml: Reading file");
		String filename = Program.getXMLPlacesFileName();
		if( !_sFileName.isEmpty() )
			filename = _sFileName;
		LinkedList<Rangement> oRangementVector = new LinkedList<Rangement>();
		LinkedList<String> names = new LinkedList<String>();

		File file = new File(filename);
		if(!file.exists())
			return null;

		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(file);
			doc.getDocumentElement().normalize();

			NodeList places = doc.getElementsByTagName("place");

			for (int i = 0; i < places.getLength(); i++) {
				Node nNode = places.item(i);

				if (nNode.getNodeType() == Node.ELEMENT_NODE) {

					Element place = (Element) nNode;
					Boolean bIsCaisse = new Boolean(place.getAttribute("IsCaisse"));
					int nPlace = Integer.parseInt(place.getAttribute("NbPlace"));
					String sName = place.getAttribute("name");
					if(sName.isEmpty())
					{
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
							oRangementVector.add(new Rangement( sName, nPlace, nNumStart, bLimit, nNbLimit));
							names.add(sName);
						}
					}
					else {
						// C'est un rangement complexe
						// ___________________________

						int nb_lignes[]; 
						int nb_colonnes[];
						LinkedList<Part> listPart = new LinkedList<Part>();
						Vector<Integer> oVector = new Vector<Integer>();
						NodeList internalPlaces = place.getElementsByTagName("internal-place");
						int nLieu = 0;
						nb_lignes = new int[nPlace];
						for (int j = 0; j < internalPlaces.getLength(); j++) {
							Node nInternal = internalPlaces.item(j);
							if (nInternal.getNodeType() == Node.ELEMENT_NODE) {
								Part part = new Part(i);
								listPart.add(part);
								Element iPlace = (Element)nInternal;
								int nLine = Integer.parseInt(iPlace.getAttribute("NbLine"));
								nb_lignes[nLieu] = nLine;
								part.setRows(nLine);
								NodeList Line = iPlace.getElementsByTagName("line");
								for (int k = 0; k < Line.getLength(); k++) {
									Node nTempLine = Line.item(k);
									if (nTempLine.getNodeType() == Node.ELEMENT_NODE) {
										Element oLine = (Element)nTempLine;
										int nColumn = Integer.parseInt(oLine.getAttribute("NbColumn"));
										part.getRow(k).setCol(nColumn);
										oVector.add(new Integer(nColumn));
									}
								}
								nLieu++;
							}
						}
						nb_colonnes = new int[oVector.size()];
						for (int j=0; j<nb_colonnes.length; j++)
							nb_colonnes[j] = oVector.get(j).intValue();
						if(names.contains(sName)) {
							Debug("WARNING: Rangement name '"+sName+"' already used!");
						}
						else {
							names.add(sName);
							//Rangement rangement = new Rangement(sName, nPlace, nb_lignes, nb_colonnes);
							oRangementVector.add(new Rangement(sName, listPart));
						}
					}

				}
			}
		}
		catch (IOException e) {
			Debug("IOException");
			Program.showException(e, false);
			return oRangementVector;
		} catch (ParserConfigurationException e) {
			Debug("ParserConfigurationException");
			Program.showException(e, false);
			return oRangementVector;
		} catch (SAXException e) {
			Debug("SAXException");
			Program.showException(e, false);
			return oRangementVector;
		}

		Debug("readMyCellarXml: Reading file OK");
		return oRangementVector;
	}

	/**
	 * writeMyCellarXml
	 *
	 * @param _oCave LinkedList<Rangement>
	 * @return boolean
	 */
	public static boolean writeMyCellarXml(LinkedList<Rangement> _oCave, String _sFilename) {

		Debug("writeMyCellarXml: Writing file");
		String filename = Program.getXMLPlacesFileName();
		if(!_sFilename.isEmpty())
			filename = _sFilename;
		try {
			FileWriter oFile = new FileWriter(filename);
			//Init XML File
			oFile.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
			// Racine XML
			oFile.write("<MyCellar>");
			// Ecriture des rangements
			for (int i=0; i<_oCave.size(); i++){
				if ( _oCave.get(i) != null )
					oFile.write(_oCave.get(i).toXml());
			}
			oFile.write("</MyCellar>");
			oFile.flush();
			oFile.close();
		}
		catch (IOException ex) {
			Program.showException(ex);
			return false;
		}
		Debug("writeMyCellarXml: Writing file OK");
		return true;
	}

	/**
	 * appendRangement
	 *
	 * @param _oCave Rangement
	 * @return boolean
	 */
	public static boolean appendRangement(Rangement _oCave) {
		Debug("appendRangement: Add place "+_oCave.getNom());
		LinkedList<Rangement> oCaveTmp = readMyCellarXml("");
		if( null == oCaveTmp )
			oCaveTmp = new LinkedList<Rangement>();
		if(!oCaveTmp.contains(_oCave))
			oCaveTmp.add(_oCave);

		return writeMyCellarXml(oCaveTmp,"");
	}

	/**
	 * writeTypeXml
	 *
	 * @param typeList LinkedList<String>
	 * @return boolean
	 */
	public static boolean writeTypeXml(LinkedList<String> typeList) {

		Debug("writeTypeXml: Writing file");
		String filename = Program.getXMLTypesFileName();
		try {
			FileWriter oFile = new FileWriter(filename);
			//Init XML File
			oFile.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
			// Racine XML
			oFile.write("<MyCellar>");
			// Ecriture des types
			for (String type: typeList){
				if(type.equals(Program.defaut_half))
					oFile.write("<type value=\""+type+"\" default=\"true\"/>");
				else
					oFile.write("<type value=\""+type+"\"/>");
			}
			oFile.write("</MyCellar>");
			oFile.flush();
			oFile.close();
		}
		catch (IOException ex) {
			Program.showException(ex);
			return false;
		}
		Debug("writeTypeXml: Writing file OK");
		return true;
	}

	/**
	 * readTypesXml: Lit le fichier Types.xml des types
	 *
	 * @return LinkedList<String> Liste de types 
	 */
	public static LinkedList<String> readTypesXml()  {

		Debug("readTypesXml: Reading file");
		File file = new File(Program.getXMLTypesFileName());
		if(!file.exists()) {
			Debug("WARNING: file '"+Program.getXMLTypesFileName()+"' not found!");
			return null;
		}

		LinkedList<String> typeList = new LinkedList<String>();
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
					if(type.hasAttribute("default"))
						Program.defaut_half = value;
					if(value != null && !value.isEmpty() && !typeList.contains(value))
						typeList.add(value);
				}
			}
		}
		catch (IOException e) {
			Debug("IOException");
			Program.showException(e, false);
			return typeList;
		} catch (ParserConfigurationException e) {
			Debug("ParserConfigurationException");
			Program.showException(e, false);
			return typeList;
		} catch (SAXException e) {
			Debug("SAXException");
			Program.showException(e, false);
			return typeList;
		}
		Debug("readTypesXml: Reading file OK");
		return typeList;
	}
	
	/**
	 * writeRangements: Ecriture des Rangements pour l'export XML/HTML
	 *
	 * @param String filename : Fichier à écrire
	 * @param LinkedList<Rangement> rangements : Liste des rangements à écrire
	 */
	public static boolean writeRangements(String filename, LinkedList<Rangement> rangements, boolean preview){

		Debug("writeRangement: Writing file");
		if (filename.isEmpty()) {
			filename = Program.getPreviewXMLFileName();
		}
		try {
			DocumentBuilderFactory dbFactory =
					DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = 
					dbFactory.newDocumentBuilder();
			Document doc = dBuilder.newDocument();
			// root element
			Element root = doc.createElement("cave");
			doc.appendChild(root);
			
			Element rootDoc = doc.getDocumentElement();
			String dir = Program.convertToHTMLString(System.getProperty("user.dir"));

			Node pi = doc.createProcessingInstruction
			         ("xml-stylesheet", "type=\"text/xsl\" href=\""+dir+"/resources/Rangement.xsl\"");
			doc.insertBefore(pi, rootDoc);

			for(Rangement rangement : rangements)
			{
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

			TransformerFactory transformerFactory =
					TransformerFactory.newInstance();
			Transformer transformer =
					transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result =
					new StreamResult(new File(filename));
			transformer.transform(source, result);
		} catch (ParserConfigurationException e) {
			Debug("ParserConfigurationException");
			Program.showException(e, false);
			return false;
		} catch (TransformerException e) {
			Debug("TransformerException");
			Program.showException(e, false);
			return false;
		}
		return true;
	}

	/**
	 * Debug
	 *
	 * @param sText String
	 */
	public static void Debug(String sText) {
		Program.Debug("MyXmlDom: " + sText);
	}

}
