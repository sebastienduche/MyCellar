//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2012.03.18 at 05:32:45 PM CET 
//


package mycellar;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * <p>Titre : Cave à vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2005</p>
 * <p>Société : Seb Informatique</p>
 * @author Sébastien Duché
 * @version 3.7
 * @since 13/03/18
 */

/**
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
 *         &lt;element name="other1" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="other2" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="other3" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="comment" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="maturity" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="parker" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="appellation" type="{http://www.w3.org/2001/XMLSchema}string"/>
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
		"other1",
		"other2",
		"other3",
		"comment",
		"maturity",
		"parker",
		"appellation",
		"vignoble",
		"color",
})
@XmlRootElement(name = "Bouteille")
public class Bouteille implements Serializable{

	private static final long serialVersionUID = 7443323147347096230L;

	private static int generatedValue = 0;
	private int id;

	@XmlElement(required = true)
	protected String nom;
	@XmlElement(required = true)
	protected String annee;
	@XmlElement(required = true)
	protected String type;
	@XmlElement(required = true)
	protected String emplacement;
	@XmlElement(name = "num_lieu")
	protected int numLieu;
	protected int ligne;
	protected int colonne;
	@XmlElement(required = true)
	protected String prix;
	@XmlElement(required = false)
	protected String other1;
	@XmlElement(required = false)
	protected String other2;
	@XmlElement(required = false)
	protected String other3;
	@XmlElement(required = true)
	protected String comment;
	@XmlElement(required = true)
	protected String maturity;
	@XmlElement(required = true)
	protected String parker;
	@XmlElement(required = true)
	protected String appellation;
	@XmlElement(required = false)
	protected Vignoble vignoble;
	@XmlElement(required = false)
	protected String color;

	public static int prix_max = 0;
	public static int NON_VINTAGE_INT = 9999;
	public static String NON_VINTAGE = "NV";

	/**
	 * Bouteille: Constructeur d'une bouteille vide.
	 */
	public Bouteille() {
		nom = type = emplacement = prix = comment = annee = maturity = parker = "";
		appellation = color = "";
		vignoble = null;
	}

	/**
	 * Bouteille: Constructeur par copie.
	 */
	public Bouteille(Bouteille b) {
		id = generatedValue++;
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
		appellation = b.getAppellation();
		color = b.getColor();
		vignoble = b.getVignoble();
	}

	public Bouteille(BouteilleBuilder builder){
		id = generatedValue++;
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
		appellation = builder.appellation;
		color = builder.color;
		vignoble = builder.vignoble;
	}
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * Gets the value of the nom property.
	 * 
	 * @return
	 *     possible object is
	 *     {@link String }
	 *     
	 */
	 public String getNom() {
		 return nom;
	 }

	 /**
	  * Sets the value of the nom property.
	  * 
	  * @param value
	  *     allowed object is
	  *     {@link String }
	  *     
	  */
	 public void setNom(String value) {
		 this.nom = value;
	 }

	 /**
	  * Gets the value of the annee property.
	  * 
	  * @return
	  *     possible object is
	  *     {@link String }
	  *     
	  */
	 public String getAnnee() {
		 return annee;
	 }

	 /**
	  * Sets the value of the annee property.
	  * 
	  * @param value
	  *     allowed object is
	  *     {@link String }
	  *     
	  */
	 public void setAnnee(String value) {
		 this.annee = value;
	 }

	 /**
	  * Gets the value of the type property.
	  * 
	  * @return
	  *     possible object is
	  *     {@link String }
	  *     
	  */
	 public String getType() {
		 return type;
	 }

	 /**
	  * Sets the value of the type property.
	  * 
	  * @param value
	  *     allowed object is
	  *     {@link String }
	  *     
	  */
	 public void setType(String value) {
		 this.type = value;
	 }

	 /**
	  * Gets the value of the emplacement property.
	  * 
	  * @return
	  *     possible object is
	  *     {@link String }
	  *     
	  */
	 public String getEmplacement() {
		 return emplacement;
	 }

	 /**
	  * Sets the value of the emplacement property.
	  * 
	  * @param value
	  *     allowed object is
	  *     {@link String }
	  *     
	  */
	 public void setEmplacement(String value) {
		 this.emplacement = value;
	 }

	 /**
	  * Gets the value of the numLieu property.
	  * 
	  */
	 public int getNumLieu() {
		 return numLieu;
	 }

	 /**
	  * Sets the value of the numLieu property.
	  * 
	  */
	 public void setNumLieu(int value) {
		 this.numLieu = value;
	 }

	 /**
	  * Gets the value of the ligne property.
	  * 
	  */
	 public int getLigne() {
		 return ligne;
	 }

	 /**
	  * Sets the value of the ligne property.
	  * 
	  */
	 public void setLigne(int value) {
		 this.ligne = value;
	 }

	 /**
	  * Gets the value of the colonne property.
	  * 
	  */
	 public int getColonne() {
		 return colonne;
	 }

	 /**
	  * Sets the value of the colonne property.
	  * 
	  */
	 public void setColonne(int value) {
		 this.colonne = value;
	 }

	 /**
	  * Gets the value of the prix property.
	  * 
	  * @return
	  *     possible object is
	  *     {@link String }
	  *     
	  */
	 public String getPrix() {
		 return prix;
	 }

	 /**
	  * Sets the value of the prix property.
	  * 
	  * @param value
	  *     allowed object is
	  *     {@link String }
	  *     
	  */
	 public void setPrix(String value) {
		 this.prix = value;
	 }

	 /**
	  * Gets the value of the other1 property.
	  * 
	  * @return
	  *     possible object is
	  *     {@link String }
	  *     
	  */
	 public String getOther1s() {
		 return other1;
	 }

	 /**
	  * Sets the value of the other1 property.
	  * 
	  * @param value
	  *     allowed object is
	  *     {@link String }
	  *     
	  */
	 public void setOther1s(String value) {
		 this.other1 = value;
	 }

	 /**
	  * Gets the value of the other2 property.
	  * 
	  * @return
	  *     possible object is
	  *     {@link String }
	  *     
	  */
	 public String getOther2() {
		 return other2;
	 }

	 /**
	  * Sets the value of the other2 property.
	  * 
	  * @param value
	  *     allowed object is
	  *     {@link String }
	  *     
	  */
	 public void setOther2(String value) {
		 this.other2 = value;
	 }

	 /**
	  * Gets the value of the other3 property.
	  * 
	  * @return
	  *     possible object is
	  *     {@link String }
	  *     
	  */
	 public String getOther3() {
		 return other3;
	 }

	 /**
	  * Sets the value of the other3 property.
	  * 
	  * @param value
	  *     allowed object is
	  *     {@link String }
	  *     
	  */
	 public void setOther3(String value) {
		 this.other3 = value;
	 }

	 /**
	  * Gets the value of the comment property.
	  * 
	  * @return
	  *     possible object is
	  *     {@link String }
	  *     
	  */
	 public String getComment() {
		 return comment;
	 }

	 /**
	  * Sets the value of the comment property.
	  * 
	  * @param value
	  *     allowed object is
	  *     {@link String }
	  *     
	  */
	 public void setComment(String value) {
		 this.comment = value;
	 }

	 /**
	  * Gets the value of the maturity property.
	  * 
	  * @return
	  *     possible object is
	  *     {@link String }
	  *     
	  */
	 public String getMaturity() {
		 return maturity;
	 }

	 /**
	  * Sets the value of the maturity property.
	  * 
	  * @param value
	  *     allowed object is
	  *     {@link String }
	  *     
	  */
	 public void setMaturity(String value) {
		 this.maturity = value;
	 }

	 /**
	  * Gets the value of the parker property.
	  * 
	  * @return
	  *     possible object is
	  *     {@link String }
	  *     
	  */
	 public String getParker() {
		 return parker;
	 }

	 /**
	  * Sets the value of the parker property.
	  * 
	  * @param value
	  *     allowed object is
	  *     {@link String }
	  *     
	  */
	 public void setParker(String value) {
		 this.parker = value;
	 }

	 /**
	  * Gets the value of the appellation property.
	  * 
	  * @return
	  *     possible object is
	  *     {@link String }
	  *     
	  */
	 public String getAppellation() {
		 return appellation;
	 }

	 /**
	  * Sets the value of the appellation property.
	  * 
	  * @param value
	  *     allowed object is
	  *     {@link String }
	  *     
	  */
	 public void setAppellation(String value) {
		 this.appellation = value;
	 }

	 /**
	  * Gets the value of the color property.
	  * 
	  * @return
	  *     possible object is
	  *     {@link String }
	  *     
	  */
	 public String getColor() {
		 return color;
	 }

	 /**
	  * Sets the value of the color property.
	  * 
	  * @param value
	  *     allowed object is
	  *     {@link String }
	  *     
	  */
	 public void setColor(String value) {
		 this.color = value;
	 }

	 /**
	  * Gets the value of the vignoble property.
	  * 
	  * @return
	  *     possible object is
	  *     {@link Vignoble }
	  *     
	  */
	 public Vignoble getVignoble() {
		 return vignoble;
	 }

	 /**
	  * Sets the value of the vignoble property.
	  * 
	  * @param value
	  *     allowed object is
	  *     {@link Vignoble }
	  *     
	  */
	 public void setVignoble(Vignoble value) {
		 this.vignoble = value;
	 }


	 public Rangement getRangement() {
		 return Program.getCave(emplacement);
	 }

	 public int getAnneeInt() {
		 if(annee.isEmpty())
			 return 0;
		 if(isNonVintageYear(annee))
			 return NON_VINTAGE_INT;
		 try{
			 int anneeInt = Integer.parseInt(annee);
			 return anneeInt;
		 }catch(NumberFormatException e){
			 return 0;
		 }
	 }

	 public static boolean isValidYear(String year) {
		 year = year.trim();
		 if( year.compareToIgnoreCase(NON_VINTAGE) == 0)
			 return true;
		 if(!Program.hasYearControl())
			 return true;
		 int n = 0;
		 try{
			 n = Integer.parseInt(year);
		 }catch( NumberFormatException pe)
		 {
			 Debug( "ERROR: Unable to parse '"+year+"'!!!!");
			 return false;
		 }

		 Calendar date = new GregorianCalendar();
		 int current_year = date.get(Calendar.YEAR);
		 if( year.length() == 4 && n > current_year )
			 return false;
		 return true;
	 }

	 public static boolean isNonVintageYear(String year) {
		 return ( year.compareToIgnoreCase(NON_VINTAGE) == 0);
	 }

	 public boolean isNonVintage() {
		 return ( annee.compareToIgnoreCase(NON_VINTAGE) == 0);
	 }

	 public double getPriceDouble() {
		 
		 String price = Program.convertStringFromHTMLString(prix);
		 if(price.isEmpty()) {
			 return 0;
		 }

		 try {
		 	return Program.stringToBigDecimal(price).doubleValue();
		 }
		 catch (NumberFormatException ignored) {
			 return 0;
		 }
	 }

	public boolean hasPrice() {

		String price = Program.convertStringFromHTMLString(prix);
		if (price.isEmpty())
			return false;
		try {
			Program.stringToBigDecimal(price);
		}
		catch (NumberFormatException ignored) {
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

	 public void update(Bouteille b) {
		 setNom(b.getNom());
		 setAnnee(b.getAnnee());
		 setAppellation(b.getAppellation());
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
	 }
	 
	 public void updateID() {
		 generatedValue++;
	 }

	 /**
	  * Debug
	  *
	  * @param sText String
	  */
	 public static void Debug(String sText) {
		 Program.Debug("Bouteille: " + sText);
	 }

	 @Override
	 public int hashCode() {
		 final int prime = 31;
		 int result = 1;
		 result = prime * result + ((annee == null) ? 0 : annee.hashCode());
		 result = prime * result
				 + ((appellation == null) ? 0 : appellation.hashCode());
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
		 result = prime * result
				 + ((vignoble == null) ? 0 : vignoble.hashCode());
		 return result;
	 }

	 @Override
	 public boolean equals(Object obj) {
		 if (this == obj)
			 return true;
		 if (obj == null)
			 return false;
		 if (getClass() != obj.getClass())
			 return false;
		 Bouteille other = (Bouteille) obj;
		 if(id != other.id)
			 return false;
		 if (annee == null) {
			 if (other.annee != null)
				 return false;
		 } else if (!annee.equals(other.annee))
			 return false;
		 if (appellation == null) {
			 if (other.appellation != null)
				 return false;
		 } else if (!appellation.equals(other.appellation))
			 return false;
		 if (colonne != other.colonne)
			 return false;
		 if (color == null) {
			 if (other.color != null)
				 return false;
		 } else if (!color.equals(other.color))
			 return false;
		 if (comment == null) {
			 if (other.comment != null)
				 return false;
		 } else if (!comment.equals(other.comment))
			 return false;
		 if (emplacement == null) {
			 if (other.emplacement != null)
				 return false;
		 } else if (!emplacement.equals(other.emplacement))
			 return false;
		 if (ligne != other.ligne)
			 return false;
		 if (maturity == null) {
			 if (other.maturity != null)
				 return false;
		 } else if (!maturity.equals(other.maturity))
			 return false;
		 if (nom == null) {
			 if (other.nom != null)
				 return false;
		 } else if (!nom.equals(other.nom))
			 return false;
		 if (numLieu != other.numLieu)
			 return false;
		 if (parker == null) {
			 if (other.parker != null)
				 return false;
		 } else if (!parker.equals(other.parker))
			 return false;
		 if (prix == null) {
			 if (other.prix != null)
				 return false;
		 } else if (!prix.equals(other.prix))
			 return false;
		 if (type == null) {
			 if (other.type != null)
				 return false;
		 } else if (!type.equals(other.type))
			 return false;
		 if (vignoble == null) {
			 if (other.vignoble != null)
				 return false;
		 } else if (!vignoble.equals(other.vignoble))
			 return false;
		 return true;
	 }



	 public static class BouteilleBuilder {
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
		 private String appellation;
		 private String color;
		 private Vignoble vignoble;

		 BouteilleBuilder(String nom){
			 this.nom = nom;
			 numLieu = ligne = colonne = 0;
			 type = emplacement = prix = comment = annee = maturity = parker = "";
			 appellation = color = "";
			 vignoble = null;
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

		 public BouteilleBuilder appelation(String app) {
			 this.appellation = app;
			 return this;
		 }

		 public BouteilleBuilder color(String color) {
			 this.color = color;
			 return this;
		 }

		 public BouteilleBuilder vignoble(String country, String name) {
			 vignoble = new Vignoble(country, name);
			 return this;
		 }

		 public BouteilleBuilder vignoble(String country, String name, String aoc) {
			 vignoble = new Vignoble(country, name, aoc);
			 return this;
		 }

		 public BouteilleBuilder vignoble(String country, String name, String aoc, String aop) {
			 vignoble = new Vignoble(country, name, aoc, null, aop);
			 return this;
		 }

		 public BouteilleBuilder vignoble(String country, String name, String aoc, String igp, String aop) {
			 vignoble = new Vignoble(country, name, aoc, igp, aop);
			 return this;
		 }

		 public Bouteille build() {
			 return new Bouteille(this);
		 }
	 }

}
