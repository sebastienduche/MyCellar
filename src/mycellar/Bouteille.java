//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2012.03.18 at 05:32:45 PM CET 
//


package mycellar;

import mycellar.core.BottlesStatus;
import mycellar.core.IMyCellarObject;
import mycellar.core.MyCellarObject;
import mycellar.core.common.MyCellarFields;
import mycellar.core.common.bottle.BottleColor;
import mycellar.core.datas.jaxb.VignobleJaxb;
import mycellar.placesmanagement.places.AbstractPlace;
import mycellar.placesmanagement.places.PlacePosition;
import mycellar.placesmanagement.places.PlaceUtils;
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
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static mycellar.MyCellarUtils.assertObjectType;
import static mycellar.ProgramConstants.DATE_FORMATER_DD_MM_YYYY_HH_MM;

/**
 * Titre : Cave &agrave; vin
 * Description : Votre description
 * Copyright : Copyright (c) 2005
 * Soci&eacute;t&eacute; : Seb Informatique
 *
 * @author S&eacute;bastien Duch&eacute;
 * @version 8.1
 * @since 30/06/22
 *
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
public class Bouteille extends MyCellarObject implements Serializable {

  public static final String NON_VINTAGE = "NV";
  public static final int NON_VINTAGE_INT = 9999;
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

  public Bouteille() {
    nom = type = emplacement = prix = comment = annee = maturity = parker = color = "";
    vignoble = null;
    status = "";
    lastModified = null;
  }

  public Bouteille(Bouteille b) {
    Objects.requireNonNull(b);
    id = Program.getNewID();
    nom = b.getNom();
    annee = b.getAnnee();
    type = b.getKind();
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

  public Bouteille(BouteilleBuilder builder) {
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

  public static boolean isInvalidYear(String year) {
    year = year.strip();
    if (year.compareToIgnoreCase(NON_VINTAGE) == 0) {
      return false;
    }
    if (!Program.hasYearControl()) {
      return false;
    }
    int n;
    try {
      n = Integer.parseInt(year);
    } catch (NumberFormatException e) {
      Debug("ERROR: Unable to parse year '" + year + "'!");
      return true;
    }

    int current_year = LocalDate.now().getYear();
    return year.length() == 4 && n > current_year;
  }

  public static boolean isNonVintageYear(String year) {
    return (year.compareToIgnoreCase(NON_VINTAGE) == 0);
  }

  public static Bouteille fromXml(Element element) {
    return new Bouteille().fromXmlElemnt(element);
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
  public int getId() {
    return id;
  }

  @Override
  public void setId(int id) {
    this.id = id;
  }

  @Override
  public String getNom() {
    return nom;
  }

  @Override
  public void setNom(String nom) {
    this.nom = nom;
  }

  @Override
  public String getAnnee() {
    return annee;
  }

  @Override
  public void setAnnee(String annee) {
    this.annee = annee;
  }

  @Override
  public String getKind() {
    return type;
  }

  @Override
  public void setKind(String kind) {
    type = kind;
  }

  @Override
  public String getEmplacement() {
    return emplacement;
  }

  @Override
  public void setEmplacement(String emplacement) {
    this.emplacement = emplacement;
  }

  @Override
  public int getNumLieu() {
    return numLieu;
  }

  @Override
  public void setNumLieu(int numLieu) {
    this.numLieu = numLieu;
  }

  @Override
  public int getLigne() {
    return ligne;
  }

  @Override
  public void setLigne(int ligne) {
    this.ligne = ligne;
  }

  @Override
  public int getColonne() {
    return colonne;
  }

  @Override
  public void setColonne(int colonne) {
    this.colonne = colonne;
  }

  @Override
  public String getPrix() {
    return prix;
  }

  public void setPrix(String prix) {
    this.prix = prix;
  }

  @Override
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

  @Override
  public String getStatus() {
    return status;
  }

  @Override
  public void setStatus(String status) {
    this.status = status;
  }

  @Override
  public String getLastModified() {
    return lastModified;
  }

  private void setLastModified(LocalDateTime lastModified) {
    this.lastModified = DATE_FORMATER_DD_MM_YYYY_HH_MM.format(lastModified);
  }

  @Override
  public AbstractPlace getAbstractPlace() {
    return PlaceUtils.getPlaceByName(emplacement);
  }

  @Override
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
    } catch (NumberFormatException e) {
      return 0;
    }
  }

  @Override
  public boolean isNonVintage() {
    return isNonVintageYear(annee);
  }

  @Override
  public double getPriceDouble() {
    return getPrice().doubleValue();
  }

  @Override
  public BigDecimal getPrice() {
    String price = MyCellarUtils.convertStringFromHTMLString(prix);
    if (price.isEmpty()) {
      return BigDecimal.ZERO;
    }

    return MyCellarUtils.safeStringToBigDecimal(price, BigDecimal.ZERO);
  }

  @Override
  public boolean hasPrice() {
    if (prix.isBlank()) {
      return false;
    }
    try {
      MyCellarUtils.stringToBigDecimal(MyCellarUtils.convertStringFromHTMLString(prix));
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
  public PlacePosition getPlacePosition() {
    return new PlacePosition.PlacePositionBuilder(getAbstractPlace())
        .withNumPlace(getNumLieu())
        .withLine(getLigne())
        .withColumn(getColonne())
        .build();
  }

  @Override
  public String toString() {
    return nom;
  }

  @Override
  public void update(final MyCellarObject myCellarObject) {
    Bouteille b = (Bouteille) myCellarObject;
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
    setKind(b.getKind());
    setColor(b.getColor());
    setVignoble(b.getVignoble());
    if (b.hasNoStatus()) {
      setStatus(BottlesStatus.MODIFIED.name());
    } else {
      setStatus(b.getStatus());
    }
    setLastModified(LocalDateTime.now());
  }

  @Override
  public Bouteille cast(MyCellarObject myCellarObject) {
    assertObjectType(myCellarObject, Bouteille.class);
    return (Bouteille) myCellarObject;
  }

  @Override
  public Bouteille castCopy(MyCellarObject myCellarObject) {
    assertObjectType(myCellarObject, Bouteille.class);
    return new Bouteille((Bouteille) myCellarObject);
  }

  @Override
  public void setModified() {
    setLastModified(LocalDateTime.now());
  }

  @Override
  public void setCreated() {
    setStatus(BottlesStatus.CREATED.name());
    setLastModified(LocalDateTime.now());
  }

  @Override
  public boolean hasNoStatus() {
    return status.isEmpty() || status.equals(BottlesStatus.NONE.name());
  }

  private boolean canChangeStatus() {
    return status.isEmpty() || status.equals(BottlesStatus.NONE.name()) || status.equals(BottlesStatus.CREATED.name());
  }

  @Override
  public void updateStatus() {
    if (canChangeStatus()) {
      status = BottlesStatus.MODIFIED.name();
    }
    setModified();
  }

  @Override
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
        setKind(value);
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
      case DISK_NUMBER:
      case DISK_COUNT:
      case RATING:
      case FILE:
      case COMPOSER:
      case ARTIST:
      case SUPPORT:
        Program.throwNotImplementedIfNotFor(this, Music.class);
        break;
      default:
        break;
    }
  }

  @Override
  public boolean updateID() {
    if (id != -1) {
      final List<IMyCellarObject> bouteilles = Program.getStorage().getAllList().stream().filter(bouteille -> bouteille.getId() == id).collect(Collectors.toList());
      if (bouteilles.size() == 1 && bouteilles.get(0).equals(this)) {
        return false;
      }
    }
    id = Program.getNewID();
    return true;
  }

  @Override
  public boolean isInTemporaryStock() {
    return PlaceUtils.isTemporaryPlace(emplacement);
  }

  @Override
  public Bouteille fromXmlElemnt(Element element) {
    NodeList nodeId = element.getElementsByTagName("id");
    final int elemId = Integer.parseInt(nodeId.item(0).getTextContent());
    NodeList nodeName = element.getElementsByTagName("nom");
    final String name = nodeName.item(0).getTextContent();
    NodeList nodeAnnee = element.getElementsByTagName("annee");
    final String year = nodeAnnee.item(0).getTextContent();
    NodeList nodeType = element.getElementsByTagName("type");
    final String elemType = nodeType.item(0).getTextContent();
    NodeList nodePlace = element.getElementsByTagName("emplacement");
    final String place = nodePlace.item(0).getTextContent();
    NodeList nodeNumLieu = element.getElementsByTagName("num_lieu");
    final int elemNumLieu = Integer.parseInt(nodeNumLieu.item(0).getTextContent());
    NodeList nodeLine = element.getElementsByTagName("ligne");
    final int line = Integer.parseInt(nodeLine.item(0).getTextContent());
    NodeList nodeColumn = element.getElementsByTagName("colonne");
    final int column = Integer.parseInt(nodeColumn.item(0).getTextContent());
    NodeList nodePrice = element.getElementsByTagName("prix");
    final String price = nodePrice.item(0).getTextContent();
    NodeList nodeComment = element.getElementsByTagName("comment");
    final String elemComment = nodeComment.item(0).getTextContent();
    NodeList nodeMaturity = element.getElementsByTagName("maturity");
    final String elemMaturity = nodeMaturity.item(0).getTextContent();
    NodeList nodeParker = element.getElementsByTagName("parker");
    final String elemParker = nodeParker.item(0).getTextContent();
    NodeList nodeColor = element.getElementsByTagName("color");
    final String elemColor = nodeColor.item(0).getTextContent();
    NodeList nodeStatus = element.getElementsByTagName("status");
    String elemStatus = "";
    if (nodeStatus.getLength() > 0) {
      elemStatus = nodeStatus.item(0).getTextContent();
    }
    NodeList nodeLastModified = element.getElementsByTagName("lastModified");
    String lastModifed = "";
    if (nodeLastModified.getLength() > 0) {
      lastModifed = nodeLastModified.item(0).getTextContent();
    }
    NodeList nodeVignoble = element.getElementsByTagName("vignoble");
    final Element elemVignoble = (Element) nodeVignoble.item(0);
    NodeList nodeCountry = elemVignoble.getElementsByTagName("country");
    final String country = nodeCountry.item(0).getTextContent();
    NodeList nodeVigobleName = elemVignoble.getElementsByTagName("name");
    String vignobleName, AOC, IGP;
    vignobleName = AOC = IGP = "";
    if (nodeVignoble.getLength() == 1) {
      vignobleName = nodeVigobleName.item(0).getTextContent();
      NodeList nodeAOC = elemVignoble.getElementsByTagName("AOC");
      if (nodeAOC.getLength() == 1) {
        AOC = nodeAOC.item(0).getTextContent();
      }
      NodeList nodeIGP = elemVignoble.getElementsByTagName("IGP");
      if (nodeIGP.getLength() == 1) {
        IGP = nodeIGP.item(0).getTextContent();
      }
    }
    return new BouteilleBuilder(name)
        .id(elemId)
        .annee(year)
        .type(elemType)
        .place(place)
        .numPlace(elemNumLieu)
        .line(line)
        .column(column)
        .price(price)
        .comment(elemComment)
        .maturity(elemMaturity)
        .parker(elemParker)
        .status(elemStatus)
        .lastModified(lastModifed)
        .color(elemColor)
        .vignoble(country, vignobleName, AOC, IGP)
        .build();
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
    if (!Objects.equals(getClass(), obj.getClass())) {
      return false;
    }
    Bouteille other = (Bouteille) obj;
    if (id != other.id) {
      return false;
    }
    if (equalsValue(annee, other.annee)) return false;
    if (equalsValue(color, other.color)) return false;
    if (equalsValue(comment, other.comment)) return false;
    if (equalsValue(emplacement, other.emplacement)) return false;
    if (equalsValue(maturity, other.maturity)) return false;
    if (equalsValue(nom, other.nom)) return false;
    if (equalsValue(parker, other.parker)) return false;
    if (equalsValue(prix, other.prix)) return false;
    if (equalsValue(type, other.type)) return false;
    if (equalsValue(status, other.status)) return false;
    if (equalsValue(lastModified, other.lastModified)) return false;
    if (colonne != other.colonne) {
      return false;
    }
    if (ligne != other.ligne) {
      return false;
    }
    if (numLieu != other.numLieu) {
      return false;
    }
    if (vignoble == null) {
      return other.vignoble == null;
    } else return vignoble.equals(other.vignoble);
  }

  @Override
  public boolean isInExistingPlace() {
    return PlaceUtils.isExistingPlace(emplacement);
  }


  public static class BouteilleBuilder {
    private final String nom;
    private int id;
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
