package mycellar.core.datas.worksheet;

import mycellar.Bouteille;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.UUID;

/**
 * <p>Titre : Cave &agrave; vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 1998</p>
 * <p>Soci&eacute;t&eacute; : Seb Informatique</p>
 *
 * @author S&eacute;bastien Duch&eacute;
 * @version 0.3
 * @since 06/04/26
 */

@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "worksheet")
public class WorkSheetData {

  @XmlElement
  private int bouteilleId;
  @XmlElement
  private UUID uuid;

  public WorkSheetData() {
  }

  public WorkSheetData(Bouteille bouteille) {
    bouteilleId = bouteille.getId();
    uuid = bouteille.getUuid();
  }

  @Deprecated
  public int getBouteilleId() {
    return bouteilleId;
  }

  @Deprecated
  public void setBouteilleId(int bouteilleId) {
    this.bouteilleId = bouteilleId;
  }

  public UUID getUuid() {
    return uuid;
  }

  public void setUuid(UUID uuid) {
    this.uuid = uuid;
  }
}
