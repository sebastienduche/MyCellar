//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2012.03.18 at 05:32:45 PM CET 
//


package mycellar;

import mycellar.core.MyCellarFields;
import mycellar.core.datas.jaxb.VignobleJaxb;
import mycellar.placesmanagement.Rangement;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * <p>Titre : Cave &agrave; vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2005</p>
 * <p>Soci&eacute;t&eacute; : Seb Informatique</p>
 * @author S&eacute;bastien Duch&eacute;
 * @version 5.6
 * @since 17/12/20

 * <p>Java class for anonymous complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="nom" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="annee" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="type" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="emplacement" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="num_lieu" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="ligne" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="colonne" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="prix" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="comment" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="maturity" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="parker" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element ref="{}vignoble"/>
 *         &lt;element name="color" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
		"id",
		"nom",
		"annee",
		"type",
		"emplacement",
		"numLieu",
		"ligne",
		"colonne",
		"prix",
		"comment",
		"maturity",
		"parker",
		"vignoble",
		"color",
		"status",
		"lastModified"
})
@XmlRootElement(name = "Bouteille")
public class Bouteille implements Serializable{

	private static final long serialVersionUID = 7443323147347096230L;

	private int id;

	@XmlElement(required = true)
	private String nom;
	@XmlElement(required = true)
	private String annee;
	@XmlElement(required = true)
	private String type;
	@XmlElement(required = true)
	private String emplacement;
	@XmlElement(name = "num_lieu")
	private int numLieu;
	private int ligne;
	private int colonne;
	@XmlElement(required = true)
	private String prix;
	@XmlElement(required = true)
	private String comment;
	@XmlElement(required = true)
	private String maturity;
	@XmlElement(required = true)
	private String parker;
	@XmlElement()
	private VignobleJaxb vignoble;
	@XmlElement()
	private String color;
	@XmlElement()
	private String status;
	@XmlElement()
	private String lastModified;

	static final int NON_VINTAGE_INT = 9999;
	public static final String NON_VINTAGE = "NV";

	/**
	 * Bouteille: Constructeur d'une bouteille vide.
	 */
	public Bouteille() {
		nom = type = emplacement = prix = comment = annee = maturity = parker = color = "";
		vignoble = null;
		status = "";
		lastModified = null;
	}

	/**
	 * Bouteille: Constructeur par copie.
	 */
	public Bouteille(Bouteille b) {
		Objects.requireNonNull(b);
		id = Program.getNewID();
		nom = b.getNom();
		annee = b.getAnnee();
		type = b.getType();
		emplacement = b.getEmplacement();
		numLieu = b.getNumLieu();
		ligne = b.getLigne();
		colonne = b.getColonne();
		prix = b.getPrix();
		comment = b.getComment();
		maturity = b.getMaturity();
		parker = b.getParker();
		color = b.getColor();
		vignoble = b.getVignoble();
		status = b.getStatus();
		lastModified = b.getLastModified();
	}

	public Bouteille(BouteilleBuilder builder){
		if (builder.id == 0) {
			id = Program.getNewID();
		} else {
			id = builder.id;
		}
		nom = builder.nom;
		annee = builder.annee;
		type = builder.type;
		emplacement = builder.emplacement;
		numLieu = builder.numLieu;
		ligne = builder.ligne;
		colonne = builder.colonne;
		prix = builder.prix;
		comment = builder.comment;
		maturity = builder.maturity;
		parker = builder.parker;
		color = builder.color;
		vignoble = builder.vignoble;
		status = builder.status;
		lastModified = builder.lastModified;
	}
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}

	 public String getNom() {
		 return nom;
	 }

	 public void setNom(String nom) {
		 this.nom = nom;
	 }

	 public String getAnnee() {
		 return annee;
	 }

	 public void setAnnee(String annee) {
		 this.annee = annee;
	 }

	 public String getType() {
		 return type;
	 }

	 public void setType(String type) {
		 this.type = type;
	 }

	 public String getEmplacement() {
		 return emplacement;
	 }

	 public void setEmplacement(String emplacement) {
		 this.emplacement = emplacement;
	 }

	 public int getNumLieu() {
		 return numLieu;
	 }

	 public void setNumLieu(int numLieu) {
		 this.numLieu = numLieu;
	 }

	 public int getLigne() {
		 return ligne;
	 }

	 public void setLigne(int ligne) {
		 this.ligne = ligne;
	 }

	 public int getColonne() {
		 return colonne;
	 }

	 public void setColonne(int colonne) {
		 this.colonne = colonne;
	 }

	 public String getPrix() {
		 return prix;
	 }

	 public void setPrix(String prix) {
		 this.prix = prix;
	 }

	 public String getComment() {
		 return comment;
	 }

	 public void setComment(String comment) {
		 this.comment = comment;
	 }

	 public String getMaturity() {
		 return maturity;
	 }

	 public void setMaturity(String maturity) {
		 this.maturity = maturity;
	 }

	 public String getParker() {
		 return parker;
	 }

	 public void setParker(String parker) {
		 this.parker = parker;
	 }

	 public String getColor() {
		 return color;
	 }

	 public void setColor(String color) {
		 this.color = color;
	 }

	 public VignobleJaxb getVignoble() {
		 return vignoble;
	 }

	 public void setVignoble(VignobleJaxb vignoble) {
		 this.vignoble = vignoble;
	 }

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	String getLastModified() {
		return lastModified;
	}

	private void setLastModified(LocalDateTime lastModified) {
		String ddMmYyyyHhMm = "dd-MM-yyyy HH:mm";
		DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern(ddMmYyyyHhMm);
		this.lastModified = dateFormat.format(lastModified);
	}

	 public Rangement getRangement() {
		 return Program.getCave(emplacement);
	 }

	 public int getAnneeInt() {
		 if (annee.isEmpty()) {
			 return 0;
		 }
		 if (isNonVintageYear(annee)) {
			 return NON_VINTAGE_INT;
		 }
		 try {
			 int anneeInt = Integer.parseInt(annee);
			 return anneeInt;
		 } catch(NumberFormatException e) {
			 return 0;
		 }
	 }

	 public static boolean isValidYear(String year) {
		 year = year.strip();
		 if (year.compareToIgnoreCase(NON_VINTAGE) == 0) {
			 return true;
		 }
		 if (!Program.hasYearControl()) {
			 return true;
		 }
		 int n;
		 try {
			 n = Integer.parseInt(year);
		 } catch(NumberFormatException e) {
			 Debug("ERROR: Unable to parse year '" + year + "'!");
			 return false;
		 }

		 int current_year = LocalDate.now().getYear();
		 return year.length() != 4 || n <= current_year;
	 }

	 static boolean isNonVintageYear(String year) {
		 return (year.compareToIgnoreCase(NON_VINTAGE) == 0);
	 }

	 public boolean isNonVintage() {
		 return isNonVintageYear(annee);
	 }

	 public double getPriceDouble() {
		 String price = Program.convertStringFromHTMLString(prix);
		 if (price.isEmpty()) {
			 return 0;
		 }

		 return Program.safeStringToBigDecimal(price, BigDecimal.ZERO).doubleValue();
	 }
	 
	 public BigDecimal getPrice() {
		 String price = Program.convertStringFromHTMLString(prix);
		 if (price.isEmpty()) {
			 return BigDecimal.ZERO;
		 }

		 return Program.safeStringToBigDecimal(price, BigDecimal.ZERO);
	 }

	public boolean hasPrice() {
		String price = Program.convertStringFromHTMLString(prix);
		if (price.isEmpty()) {
			return false;
		}
		try {
			Program.stringToBigDecimal(price);
		} catch (NumberFormatException ignored) {
			return false;
		}
		return true;
	}
	 
	 public boolean isRedWine() {
		 return BottleColor.getColor(color) == BottleColor.RED;
	 }
	 
	 public boolean isWhiteWine() {
		 return BottleColor.getColor(color) == BottleColor.WHITE;
	 }

	 public boolean isPinkWine() {
		 return BottleColor.getColor(color) == BottleColor.PINK;
	 }

	 @Override
	 public String toString() {
		 return nom;
	 }

	 public void update(final Bouteille b) {
		 setNom(b.getNom());
		 setAnnee(b.getAnnee());
		 setColonne(b.getColonne());
		 setComment(b.getComment());
		 setEmplacement(b.getEmplacement());
		 setLigne(b.getLigne());
		 setMaturity(b.getMaturity());
		 setNumLieu(b.getNumLieu());
		 setParker(b.getParker());
		 setPrix(b.getPrix());
		 setType(b.getType());
		 setColor(b.getColor());
		 setVignoble(b.getVignoble());
		 if (b.hasNoStatus()) {
			 setStatus(BottlesStatus.MODIFIED.name());
		 } else {
		 	setStatus(b.getStatus());
		 }
		 setLastModified(LocalDateTime.now());
	 }

	 public void setModified() {
	 	setLastModified(LocalDateTime.now());
	 }

	public void setCreated() {
		setStatus(BottlesStatus.CREATED.name());
		setLastModified(LocalDateTime.now());
	}

	public boolean hasNoStatus() {
		return status.isEmpty() || status.equals(BottlesStatus.NONE.name());
	}

	private boolean canChangeStatus() {
		return status.isEmpty() || status.equals(BottlesStatus.NONE.name()) || status.equals(BottlesStatus.CREATED.name());
	}

	public void updateStatus() {
	 	if (canChangeStatus()) {
	 		status = BottlesStatus.MODIFIED.name();
		}
	 	setModified();
	}

	 public void setValue(MyCellarFields field, String value) {
	 	setModified();
		 switch (field) {
			 case NAME:
				 setNom(value);
				 break;
			 case YEAR:
				 setAnnee(value);
				 break;
			 case TYPE:
				 setType(value);
				 break;
			 case PLACE:
				 setEmplacement(value);
				 break;
			 case NUM_PLACE:
				 setNumLieu(Double.valueOf(value).intValue());
				 break;
			 case LINE:
				 setLigne(Double.valueOf(value).intValue());
				 break;
			 case COLUMN:
				 setColonne(Double.valueOf(value).intValue());
				 break;
			 case PRICE:
				 setPrix(value);
				 break;
			 case COMMENT:
				 setComment(value);
				 break;
			 case MATURITY:
				 setMaturity(value);
				 break;
			 case PARKER:
				 setParker(value);
				 break;
			 case VINEYARD:
				 if (getVignoble() == null) {
					 setVignoble(new VignobleJaxb());
				 }
				 getVignoble().setName(value);
				 break;
			 case COLOR:
				 setColor(value);
				 break;
			 case COUNTRY:
				 if (getVignoble() == null) {
					 setVignoble(new VignobleJaxb());
				 }
				 getVignoble().setCountry(value);
				 break;
			 case AOC:
				 if (getVignoble() == null) {
					 setVignoble(new VignobleJaxb());
				 }
				 getVignoble().setAOC(value);
				 break;
			 case IGP:
				 if (getVignoble() == null) {
					 setVignoble(new VignobleJaxb());
				 }
				 getVignoble().setIGP(value);
				 break;
			 case STATUS:
				 setStatus(value);
				 break;
			 default:
				 break;
		 }
	 }

	 boolean updateID() {
	 	if (id != -1) {
			final List<Bouteille> bouteilles = Program.getStorage().getAllList().stream().filter(bouteille -> bouteille.getId() == id).collect(Collectors.toList());
			if(bouteilles.size() == 1 && bouteilles.get(0).equals(this)) {
				return false;
			}
		}
		 id = Program.getNewID();
	 	return true;
	 }

	 public boolean isInTemporaryStock() {
		return Program.TEMP_PLACE.equalsIgnoreCase(emplacement);
	}

  public static Bouteille getBouteilleFromXML(Element bouteilleElem) {
    NodeList nodeId = bouteilleElem.getElementsByTagName("id");
    final int id = Integer.parseInt(nodeId.item(0).getTextContent());
    NodeList nodeName = bouteilleElem.getElementsByTagName("nom");
    final String name = nodeName.item(0).getTextContent();
    NodeList nodeAnnee = bouteilleElem.getElementsByTagName("annee");
    final String year = nodeAnnee.item(0).getTextContent();
    NodeList nodeType = bouteilleElem.getElementsByTagName("type");
    final String type = nodeType.item(0).getTextContent();
    NodeList nodePlace = bouteilleElem.getElementsByTagName("emplacement");
    final String place = nodePlace.item(0).getTextContent();
    NodeList nodeNumLieu = bouteilleElem.getElementsByTagName("num_lieu");
    final int numLieu = Integer.parseInt(nodeNumLieu.item(0).getTextContent());
    NodeList nodeLine = bouteilleElem.getElementsByTagName("ligne");
    final int line = Integer.parseInt(nodeLine.item(0).getTextContent());
    NodeList nodeColumn = bouteilleElem.getElementsByTagName("colonne");
    final int column = Integer.parseInt(nodeColumn.item(0).getTextContent());
    NodeList nodePrice = bouteilleElem.getElementsByTagName("prix");
    final String price = nodePrice.item(0).getTextContent();
    NodeList nodeComment = bouteilleElem.getElementsByTagName("comment");
    final String comment = nodeComment.item(0).getTextContent();
    NodeList nodeMaturity = bouteilleElem.getElementsByTagName("maturity");
    final String maturity = nodeMaturity.item(0).getTextContent();
    NodeList nodeParker = bouteilleElem.getElementsByTagName("parker");
    final String parker = nodeParker.item(0).getTextContent();
    NodeList nodeColor = bouteilleElem.getElementsByTagName("color");
    final String color = nodeColor.item(0).getTextContent();
    NodeList nodeVignoble = bouteilleElem.getElementsByTagName("vignoble");
    final Element vignoble = (Element) nodeVignoble.item(0);
    NodeList nodeCountry = vignoble.getElementsByTagName("country");
    final String country = nodeCountry.item(0).getTextContent();
		NodeList nodeStatus = vignoble.getElementsByTagName("status");
		String status = "";
		if (nodeStatus.getLength() > 0) {
			status = nodeStatus.item(0).getTextContent();
		}
		NodeList nodeLAstModified = vignoble.getElementsByTagName("lastModified");
		String lastModifed = "";
		if (nodeLAstModified.getLength() > 0) {
			lastModifed = nodeLAstModified.item(0).getTextContent();
		}
    NodeList nodeVigobleName = vignoble.getElementsByTagName("name");
    String vignobleName, AOC, IGP, AOP;
    vignobleName = AOC = AOP = IGP = "";
    if (nodeVignoble.getLength() == 1) {
      vignobleName = nodeVigobleName.item(0).getTextContent();
      NodeList nodeAOC = vignoble.getElementsByTagName("AOC");
      if (nodeAOC.getLength() == 1) {
        AOC = nodeAOC.item(0).getTextContent();
      }
      NodeList nodeIGP = vignoble.getElementsByTagName("IGP");
      if (nodeIGP.getLength() == 1) {
        IGP = nodeIGP.item(0).getTextContent();
      }
    }
    return new Bouteille.BouteilleBuilder(name)
				.id(id)
				.annee(year)
				.type(type)
				.place(place)
				.numPlace(numLieu)
        .line(line)
				.column(column)
				.price(price)
				.comment(comment)
				.maturity(maturity)
				.parker(parker)
				.status(status)
				.lastModified(lastModifed)
        .color(color)
				.vignoble(country, vignobleName, AOC, IGP)
				.build();
  }

	 /**
	  * Debug
	  *
	  * @param sText String
	  */
	 private static void Debug(String sText) {
		 Program.Debug("Bouteille: " + sText);
	 }

	 @Override
	 public int hashCode() {
		 final int prime = 31;
		 int result = 1;
		 result = prime * result + ((annee == null) ? 0 : annee.hashCode());
		 result = prime * result + colonne;
		 result = prime * result + ((color == null) ? 0 : color.hashCode());
		 result = prime * result + ((comment == null) ? 0 : comment.hashCode());
		 result = prime * result
				 + ((emplacement == null) ? 0 : emplacement.hashCode());
		 result = prime * result + ligne;
		 result = prime * result
				 + ((maturity == null) ? 0 : maturity.hashCode());
		 result = prime * result + ((nom == null) ? 0 : nom.hashCode());
		 result = prime * result + numLieu;
		 result = prime * result + ((parker == null) ? 0 : parker.hashCode());
		 result = prime * result + ((prix == null) ? 0 : prix.hashCode());
		 result = prime * result + ((type == null) ? 0 : type.hashCode());
		 result = prime * result + ((vignoble == null) ? 0 : vignoble.hashCode());
		 result = prime * result + ((status == null) ? 0 : status.hashCode());
		 result = prime * result + ((lastModified == null) ? 0 : lastModified.hashCode());
		 return result;
	 }

	 @Override
	 public boolean equals(Object obj) {
		 if (this == obj) {
			 return true;
		 }
		 if (obj == null) {
			 return false;
		 }
		 if (getClass() != obj.getClass()) {
			 return false;
		 }
		 Bouteille other = (Bouteille) obj;
		 if(id != other.id) {
			 return false;
		 }
		 if (annee == null) {
			 if (other.annee != null) {
				 return false;
			 }
		 } else if (!annee.equals(other.annee)) {
			 return false;
		 }
		 if (colonne != other.colonne) {
			 return false;
		 }
		 if (color == null) {
			 if (other.color != null) {
				 return false;
			 }
		 } else if (!color.equals(other.color)) {
			 return false;
		 }
		 if (comment == null) {
			 if (other.comment != null) {
				 return false;
			 }
		 } else if (!comment.equals(other.comment)) {
			 return false;
		 }
		 if (emplacement == null) {
			 if (other.emplacement != null) {
				 return false;
			 }
		 } else if (!emplacement.equals(other.emplacement)) {
			 return false;
		 }
		 if (ligne != other.ligne) {
			 return false;
		 }
		 if (maturity == null) {
			 if (other.maturity != null) {
				 return false;
			 }
		 } else if (!maturity.equals(other.maturity)) {
			 return false;
		 }
		 if (nom == null) {
			 if (other.nom != null) {
				 return false;
			 }
		 } else if (!nom.equals(other.nom)) {
			 return false;
		 }
		 if (numLieu != other.numLieu) {
			 return false;
		 }
		 if (parker == null) {
			 if (other.parker != null) {
				 return false;
			 }
		 } else if (!parker.equals(other.parker)) {
			 return false;
		 }
		 if (prix == null) {
			 if (other.prix != null) {
				 return false;
			 }
		 } else if (!prix.equals(other.prix)) {
			 return false;
		 }
		 if (type == null) {
			 if (other.type != null) {
				 return false;
			 }
		 } else if (!type.equals(other.type)) {
			 return false;
		 }
		 if (vignoble == null) {
			 if (other.vignoble != null) {
				 return false;
			 }
		 } else if (!vignoble.equals(other.vignoble)) {
			 return false;
		 }
		 if (status == null) {
			 if (other.status != null) {
				 return false;
			 }
		 } else if (!status.equals(other.status)) {
			 return false;
		 }
		 if (lastModified == null) {
			 if (other.lastModified != null) {
				 return false;
			 }
		 } else if (!lastModified.equals(other.lastModified)) {
			 return false;
		 }
		 return true;
	 }


	public static class BouteilleBuilder {
		 private int id;
		 private final String nom;
		 private String annee;
		 private String type;
		 private String emplacement;
		 private int numLieu;
		 private int ligne;
		 private int colonne;
		 private String prix;
		 private String comment;
		 private String maturity;
		 private String parker;
		 private String color;
		 private VignobleJaxb vignoble;
		 private String status;
		 private String lastModified;

		 public BouteilleBuilder(String nom) {
			 this.nom = nom;
			 id = numLieu = ligne = colonne = 0;
			 type = emplacement = prix = comment = annee = maturity = parker = color = "";
			 vignoble = null;
			 status = "";
			 lastModified = null;
		 }

		 private BouteilleBuilder id(int id) {
			 this.id = id;
			 return this;
		 }

		 public BouteilleBuilder annee(String annee) {
			 this.annee = annee;
			 return this;
		 }

		 public BouteilleBuilder type(String type) {
			 this.type = type;
			 return this;
		 }

		 public BouteilleBuilder place(String place) {
			 emplacement = place;
			 return this;
		 }

		 public BouteilleBuilder numPlace(int num) {
			 numLieu = num;
			 return this;
		 }

		 public BouteilleBuilder line(int num) {
			 ligne = num;
			 return this;
		 }

		 public BouteilleBuilder column(int num) {
			 colonne = num;
			 return this;
		 }

		 public BouteilleBuilder price(String price) {
			 prix = price;
			 return this;
		 }

		 public BouteilleBuilder comment(String comment) {
			 this.comment = comment;
			 return this;
		 }

		 public BouteilleBuilder maturity(String maturity) {
			 this.maturity = maturity;
			 return this;
		 }

		 public BouteilleBuilder parker(String parker) {
			 this.parker = parker;
			 return this;
		 }

		 public BouteilleBuilder color(String color) {
			 this.color = color;
			 return this;
		 }

		 public BouteilleBuilder status(String status) {
			 this.status = status;
			 return this;
		 }

		 public BouteilleBuilder lastModified(String lastModified) {
			 this.lastModified = lastModified;
			 return this;
		 }

		 public BouteilleBuilder vignoble(String country, String name, String aoc, String igp) {
			 vignoble = new VignobleJaxb(country, name, aoc, igp);
			 return this;
		 }

		 public Bouteille build() {
			 return new Bouteille(this);
		 }
	 }

}
