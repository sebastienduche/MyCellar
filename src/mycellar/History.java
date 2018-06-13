//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2017.05.19 at 01:50:00 PM CEST 
//


package mycellar;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * <p>Titre : Cave à vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 1998</p>
 * <p>Société : Seb Informatique</p>
 * @author Sébastien Duché
 * @version 0.8
 * @since 12/04/18
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
 *         &lt;element name="date" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="type" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element ref="{}Bouteille"/>
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
    "date",
    "type",
    "bouteille",
    "time"
})
@XmlRootElement(name = "History")
public class History {

    @XmlElement(required = true)
    private String date;
    private int type;
    private Date time;
    @XmlElement(name = "Bouteille", required = true)
    private Bouteille bouteille;
	
	  public static final int ADD = 0;
    public static final int MODIFY = 1;
    public static final int DEL = 2;
	
	/**
   * History: Contructeur avec une bouteille et un type d'action
   *
   * @param bouteille Bouteille
   * @param type int
   */
  public History(Bouteille bouteille, int type) {
    this.bouteille = bouteille;
    this.type = type;
    final LocalDate now = LocalDate.now();
    time = new Date(now.getYear(), now.getMonthValue()-1, now.getDayOfMonth());
    date = now.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
  }

    public History() {}

	/**
     * Gets the value of the date property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDate() {
        return date;
    }    

    /**
     * Sets the value of the date property.
     * 
     * @param date
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDate(String date) {
        this.date = date;
    }

    /**
     * Gets the value of the type property.
     * 
     */
    public int getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     * 
     */
    public void setType(int type) {
        this.type = type;
    }
    
    public boolean isDeleted() {
    	return type == DEL;
    }

    public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

	/**
     * Gets the value of the bouteille property.
     * 
     * @return
     *     possible object is
     *     {@link Bouteille }
     *     
     */
    public Bouteille getBouteille() {
        return bouteille;
    }

    /**
     * Sets the value of the bouteille property.
     * 
     * @param bouteille
     *     allowed object is
     *     {@link Bouteille }
     *     
     */
    public void setBouteille(Bouteille bouteille) {
        this.bouteille = bouteille;
    }

}
