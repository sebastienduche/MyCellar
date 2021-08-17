package mycellar.core.datas.worksheet;

import mycellar.core.IMyCellarObject;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>Titre : Cave &agrave; vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 1998</p>
 * <p>Soci&eacute;t&eacute; : Seb Informatique</p>
 *
 * @author S&eacute;bastien Duch&eacute;
 * @version 0.2
 * @since 09/04/21
 */


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "bouteilleId"
})
@XmlRootElement(name = "worksheet")
public class WorkSheetData {

  private int bouteilleId;

  public WorkSheetData() {
  }

  public WorkSheetData(IMyCellarObject bouteille) {
    bouteilleId = bouteille.getId();
  }

  public int getBouteilleId() {
    return bouteilleId;
  }

  public void setBouteilleId(int bouteilleId) {
    this.bouteilleId = bouteilleId;
  }
}
