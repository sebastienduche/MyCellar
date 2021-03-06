//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2017.05.19 at 01:50:00 PM CEST 
//


package mycellar.core.datas.history;

import mycellar.Bouteille;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.time.LocalDate;
import java.util.Arrays;

import static mycellar.Program.DATE_FORMATER_DDMMYYYY;

/*
 * <p>Titre : Cave à vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 1998</p>
 * <p>Société : Seb Informatique</p>
 * @author Sébastien Duché
 * @version 1.5
 * @since 17/12/20
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
 *         &lt;element name="totalBottle" type="{http://www.w3.org/2001/XMLSchema}int"/>
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
    "totalBottle"
})
@XmlRootElement(name = "History")
public class History {

  @XmlElement(required = true)
  private String date;
  private int type;
  @XmlElement(name = "Bouteille", required = true)
  private Bouteille bouteille;
  @XmlElement
  private int totalBottle;

  /**
   * History: Contructeur avec une bouteille et un type d'action
   *
   * @param bouteille Bouteille
   * @param type int
   */
  public History(Bouteille bouteille, int type, int totalBottle) {
    this.bouteille = bouteille;
    this.type = type;
    this.totalBottle = totalBottle;
    date = LocalDate.now().format(DATE_FORMATER_DDMMYYYY);
  }

  public History() {}

  public String getDate() {
    return date;
  }

  public LocalDate getLocaleDate() {
    if (date == null) {
      return null;
    }
    return LocalDate.parse(date, DATE_FORMATER_DDMMYYYY);
  }

  public void setDate(String date) {
    this.date = date;
  }

  public int getType() {
    return type;
  }

  public void setType(int type) {
    this.type = type;
  }

  public HistoryState getState() {
    return Arrays.stream(HistoryState.values())
        .filter(historyState -> historyState.ordinal() == type)
        .findAny().orElse(HistoryState.ALL);
  }

  public boolean isDeleted() {
    return type == HistoryState.DEL.ordinal();
  }

  public boolean isAddedOrDeleted() {
    return type == HistoryState.ADD.ordinal() || type == HistoryState.DEL.ordinal();
  }

  public Bouteille getBouteille() {
    return bouteille;
  }

  public void setBouteille(Bouteille bouteille) {
    this.bouteille = bouteille;
  }

  public boolean hasTotalBottle() {
    return totalBottle > 0;
  }

  public int getTotalBottle() {
    return totalBottle;
  }

  public void setTotalBottle(int totalBottle) {
    this.totalBottle = totalBottle;
  }
}
